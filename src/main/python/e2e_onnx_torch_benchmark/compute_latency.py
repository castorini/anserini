import os
import numpy as np
import scipy.stats as st
import argparse
import torch
import numpy as np
import onnxruntime as rt
import time
from transformers import AutoTokenizer, AutoModel

from pyserini.search import get_topics

LOCAL_CACHE_PATH_FOR_ONNX = os.path.join(os.path.expanduser('~'), "anserini/encoders")
MODEL_MAP = {'cosdprdistil': 
                {'onnx': 'cos-dpr-distil-optimized.onnx', 'pytorch': 'castorini/cosdpr-distil'}, 
            'splade': 
                {'onnx': 'splade-pp-ed-optimized.onnx', 'pytorch': 'naver/splade-cocondenser-ensembledistil'}
            }
BATCH_SIZE = 1 # this is defaulted to 1 since this would likely be the case in production
RUNS = 3
WARMUP_RUNS = 3

MSMARCO_TOPICS = [val['title']
                  for val in get_topics('msmarco-passage-dev-subset').values()]


def calculate_95percent_ci(data):
    n = len(data)
    m, se = np.mean(data), st.sem(data)
    h = se * 1.96
    return m, h


def main(args):
    throughput_map = {}
    models = args.models
    if args.models == ["all"]:
        models = ["splade-onnx", "splade-pytorch", "cosdprdistil-onnx", "cosdprdistil-pytorch"]
    
    for model in models:
        torch_model = MODEL_MAP[model.split("-")[0]]['pytorch']
        onnx_model = MODEL_MAP[model.split("-")[0]]['onnx']
        use_torch_model = True if model.split("-")[1] == "pytorch" else False

        tokenizer = AutoTokenizer.from_pretrained(torch_model)
        batch_tokenized_topics_pt = tokenizer(MSMARCO_TOPICS, padding=True, truncation=True, return_tensors="pt")
        batch_tokenized_topics_np = tokenizer(MSMARCO_TOPICS, padding=True, truncation=True, return_tensors="np")

        print(f"Running {model} with {args.threads} threads")

        if use_torch_model:
            throughput_map[f'{torch_model}-{args.threads}'] = [] if f'{torch_model}-{args.threads}' not in throughput_map else throughput_map[f'{torch_model}-{args.threads}']
            torch.set_num_threads(args.threads)

            if model == "splade-pytorch":
                from splade.models.transformer_rep import Splade
                model = Splade(torch_model, agg="max")
                model.eval()
                for i in range(args.actual_runs+args.warmup_runs):
                    start = time.time()
                    for i in range(0, len(MSMARCO_TOPICS), args.batch_size):
                        outputs = model({'input_ids': batch_tokenized_topics_pt['input_ids'][i:i+args.batch_size], 
                                'attention_mask': batch_tokenized_topics_pt['attention_mask'][i:i+args.batch_size], 
                                'token_type_ids': batch_tokenized_topics_pt['token_type_ids'][i:i+args.batch_size]})
                    end = time.time()
                    throughput_map[f'{torch_model}-{args.threads}'].append(len(MSMARCO_TOPICS)/(end - start))
            else:
                model = AutoModel.from_pretrained(torch_model).eval()
                for i in range(args.actual_runs+args.warmup_runs):
                    start = time.time()
                    for i in range(0, len(MSMARCO_TOPICS), args.batch_size):
                        outputs = model(input_ids=batch_tokenized_topics_pt['input_ids'][i:i+args.batch_size], 
                                attention_mask=batch_tokenized_topics_pt['attention_mask'][i:i+args.batch_size], 
                                token_type_ids=batch_tokenized_topics_pt['token_type_ids'][i:i+args.batch_size])
                    end = time.time()
                    throughput_map[f'{torch_model}-{args.threads}'].append(len(MSMARCO_TOPICS)/(end - start))
            
        else:
            throughput_map[f'{onnx_model}-{args.threads}'] = [] if f'{onnx_model}-{args.threads}' not in throughput_map else throughput_map[f'{onnx_model}-{args.threads}']
            sess_opt = rt.SessionOptions()
            sess_opt.intra_op_num_threads = args.threads  
            model = rt.InferenceSession(os.path.join(LOCAL_CACHE_PATH_FOR_ONNX, onnx_model), sess_opt)
            for i in range(args.actual_runs+args.warmup_runs):
                if onnx_model == "splade-pp-ed-optimized.onnx":
                    start = time.time()
                    for i in range(0, len(MSMARCO_TOPICS), args.batch_size):
                        outputs = model.run(None, {'input_ids': batch_tokenized_topics_np['input_ids'][i:i+args.batch_size], 
                            'attention_mask': batch_tokenized_topics_np['attention_mask'][i:i+args.batch_size], 
                            'token_type_ids': batch_tokenized_topics_np['token_type_ids'][i:i+args.batch_size]})
                    end = time.time()
                    throughput_map[f'{onnx_model}-{args.threads}'].append(len(MSMARCO_TOPICS)/(end - start))
                else:
                    start = time.time()
                    for i in range(0, len(MSMARCO_TOPICS), args.batch_size):
                        outputs = model.run(None, {'input_ids': batch_tokenized_topics_np['input_ids'][i:i+args.batch_size]})
                    end = time.time()
                    throughput_map[f'{onnx_model}-{args.threads}'].append(len(MSMARCO_TOPICS)/(end - start))
    for key, value in throughput_map.items():
        mean, ci = calculate_95percent_ci(value[args.warmup_runs:])
        print(f"{key} {mean:.2f} \pm {ci:.2f} q/s")

    if args.write_to_file:
        with open(f"throughput-{'-'.join(models)}-threads={args.threads}.txt", "w") as f:
            f.write(f"Throughput of {args.models} with batch size {args.batch_size} and {args.threads} threads\n")
            for key, value in throughput_map.items():
                mean, ci = calculate_95percent_ci(value[args.warmup_runs:])
                f.write(f"{key} {mean:.2f} \pm {ci:.2f} q/s\n")      


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--models", choices=["all", "splade-onnx", "splade-pytorch", "cosdprdistil-onnx", "cosdprdistil-pytorch"], default="all", nargs="+")
    parser.add_argument("--batch_size", type=int, default=BATCH_SIZE)
    parser.add_argument("--actual_runs", type=int, default=RUNS)
    parser.add_argument("--warmup_runs", type=int, default=WARMUP_RUNS)
    parser.add_argument("--threads", type=int, default=1)
    parser.add_argument("--write-to-file", action="store_true", default=False)
    args = parser.parse_args()
    main(args)
