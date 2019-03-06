import shlex
import subprocess
import sys
import json
from tqdm import tqdm
import string
import argparse

from utils import *

parser = argparse.ArgumentParser()
parser.add_argument('--method', default='QL', help='[QL, QL+RM3]')
args = parser.parse_args()


searcher11 = JSearcher(JString('/tuna1/indexes/lucene-index.mb11.pos+docvectors+rawdocs'))
searcher11.setSearchTweets(1)

searcher13 = JSearcher(JString('/tuna1/indexes/lucene-index.mb13.pos+docvectors+rawdocs'))
searcher13.setSearchTweets(1)


for year in range(2011, 2015):
    if year in [2011, 2012]:
        searcher = searcher11
    else:
        searcher = searcher13 
    fqrel = "src/main/resources/topics-and-qrels/qrels.microblog{}.txt".format(year)
    qid2reldocids = get_qid2reldocids(fqrel)
    ftest = open("src/main/resources/topics-and-qrels/topics.microblog{}.txt".format(year))
    qid2text_time = get_qid2text_time_new(ftest)
    prediction_fn = "predictions_tuned/predict_{}_tweet_{}.txt".format(args.method, year)
    output_fn = "src/main/python/rerank/data/MB-{}/trec-{}".format(args.method, year)
    if not os.path.exists(output_fn):
        os.makedirs(output_fn)
    searcher.setLMDirichletSimilarity(1000.0)
    if args.method == "QL+RM3":
        searcher.setRM3Reranker()
    elif args.method == "QL":
        searcher.setDefaultReranker()
    else:
        print("Unsupported ranking method")
        break 
    search(searcher, prediction_fn, output_fn, qid2text_time, qid2reldocids, K=1000) # 756
    fn_qrels = "src/main/resources/topics-and-qrels/qrels.microblog{}.txt".format(year)
    cal_score(fn_qrels=fn_qrels, prediction=prediction_fn)


