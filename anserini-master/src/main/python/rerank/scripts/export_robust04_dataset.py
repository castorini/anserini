'''scripts to generate the Robust04 dataset from the Anserini runs for reranking purpose

Run BM25/BM25+RM3 on TREC Robust04 and store the output for reranking.
Makes an external call to trec_eval for the actual computation of metrics.
'''


import json
import os
from tqdm import tqdm
import string
import argparse

from utils import *

parser = argparse.ArgumentParser()
parser.add_argument('--method', default='BM25', help='[BM25, BM25+RM3]')
parser.add_argument('--output_dir', default='src/main/python/rerank/data', help='output directory')
parser.add_argument('--K', default=1000, type=int, help='number of document retrieved')
parser.add_argument('--index', default="/tuna1/indexes/lucene-index.robust04.pos+docvectors+rawdocs", help='index of Robust04 corpus')
args = parser.parse_args()

searcher = JSearcher(JString(args.index))

fqrel = "src/main/resources/topics-and-qrels/qrels.robust2004.txt"
qid2reldocids = get_qid2reldocids(fqrel)
# parameters is from Lin, Jimmy. "The Neural Hype and Comparisons Against Weak Baselines." ACM SIGIR Forum. Vol. 52. No. 1. ACM, 2019.
best_rm3_parameters = [[47, 9, 0.3], [47, 9, 0.3], [47, 9, 0.3], [47, 9, 0.3], [26, 8, 0.3]]

for split in range(1, 6):
    ftrain = json.load(open("src/main/resources/fine_tuning/drr_folds/rob04.train.s{}.json".format(split)))
    fdev = json.load(open("src/main/resources/fine_tuning/drr_folds/rob04.dev.s{}.json".format(split)))
    ftest = json.load(open("src/main/resources/fine_tuning/drr_folds/rob04.test.s{}.json".format(split)))
    for mode, data in [("train", ftrain), ("dev", fdev), ("test", ftest)]: #  
        qid2text = get_qid2text_robust04(data)
        if args.method == "BM25+RM3":
            method = "BM25_0.9_0.5_RM3_{}_{}_{}".format(*best_rm3_parameters[split-1])
        elif args.method == "BM25":
            method = "BM25_0.9_0.4"
        else:
            print("Unsupported ranking method")
            break 
        prediction_fn = "predict_{}_robust04_split{}_{}.txt".format(method, split, mode)
        output_dir = os.path.join(args.output_dir, "Robust04Corpus")
        output_fn = os.path.join(output_dir, "split{}_{}_{}.txt".format(split, mode, method))
        if not os.path.exists(output_dir):
            os.makedirs(output_dir)
            searcher.setBM25Similarity(0.9, 0.4)
        if args.method == "BM25+RM3":
                searcher.setRM3Reranker(*best_rm3_parameters[split-1])
        elif args.method == "BM25":
            searcher.setDefaultReranker()
        search_robust04(searcher, prediction_fn, qid2text, output_fn, qid2reldocids, K=args.K)
        calculate_score(fn_qrels=fqrel, prediction=prediction_fn)
