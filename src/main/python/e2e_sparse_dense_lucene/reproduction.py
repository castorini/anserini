# Anserini: A toolkit for reproducible information retrieval research built on Lucene
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import yaml
from typing import Union, Dict, List, Optional, Any
import os
import subprocess
TOPIC_NAMES = ['msmarco-passage-dev-subset', 'dl19-passage', 'dl20-passage']
EVAL_CMD_MAP = {
    'map': '-c -m map',                     # AP
    'ndcg_cut_10': '-c -m ndcg_cut.10',     # nDCG@10
    'recall_1000': '-c -m  recall.1000',    # R@1000
    'recip_rank': '-c -M 10 -m recip_rank'  # RR@10
}
TOPIC_EVAL_MAP = {
    'msmarco-passage-dev-subset': ['recip_rank', 'recall_1000'],
    'dl19-passage': ['map', 'ndcg_cut_10', 'recall_1000'],
    'dl20-passage': ['map', 'ndcg_cut_10', 'recall_1000']
}


def get_output_run_file_name(topic: str, name: str):
    return f'runs/{topic}_{name}.txt'


def get_search_command(model_name: str, cmd_template: str, topics: List[str]):
    outputs = [get_output_run_file_name(
        topic_name, model_name) for topic_name in TOPIC_NAMES]

    for topic, output in zip(topics, outputs):
        cmd = cmd_template.format(topic=topic, output=output)
        yield cmd


def get_eval_command(param: str, qrel: str, run_file: str, cmd_template: str):
    cmd = cmd_template.format(
        param=param, qrel=qrel, output=run_file)
    yield cmd


def main(config):
    # print all search commands
    for model_name, model_config in config['collections'].items():
        print("running model: ", model_name)
        # search
        for cmd in get_search_command(model_name, model_config['search_command'], model_config['topics']):
            p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            stdout, stderr = p.communicate()
            if stderr:
                print(stderr.decode('utf-8'))

        # eval
        expected_results = model_config['results']
        run_files = [get_output_run_file_name(topic_name, model_name) for topic_name in TOPIC_NAMES]
        eval_cmd = model_config['eval_command']
        metric_precision = model_config['metric_precision']

        for run_file, topic_name, qrel in zip(run_files, TOPIC_NAMES, model_config['qrels']):
            for metric in TOPIC_EVAL_MAP[topic_name]:
                for cmd in get_eval_command(EVAL_CMD_MAP[metric], qrel, run_file, eval_cmd):
                    p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                    stdout, stderr = p.communicate()
                    stdout = [out.strip() for out in stdout.decode('utf-8').split('\t')]
                    actual_result = round(float(stdout[-1]), metric_precision)
                    expected_result = expected_results[topic_name][metric]
                    assert actual_result == expected_result, f'{model_name} {topic_name} {metric} {actual_result} != {expected_result}, expected: {expected_results[topic_name]}'
                    print(f"{topic_name} {metric} {actual_result} == {expected_result}")
        print(f"{model_name} passed!")
        print("="*50)
        
                    





if __name__ == '__main__':
    with open('src/main/resources/e2e_sparse_dense_lucene/pre-encoded.yaml') as f:
        config = yaml.load(f, Loader=yaml.FullLoader)
    main(config)
