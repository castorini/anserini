'''scripts to generate the microblog dataset from the Anserini runs for reranking purpose

Run QL/QL+RM3 on TREC Microblog 2011, 2012, 2013, 2014 and store the output in the Castor style:
    a.toks
    b.toks
    sim.txt
    id.txt
    url.txt
Makes an external call to trec_eval for the actual computation of metrics.
'''


import json
import os
from tqdm import tqdm
import string
import argparse

from utils import *

parser = argparse.ArgumentParser()
parser.add_argument('--method', default='QL', help='[QL, QL+RM3]')
parser.add_argument('--output_dir', default='src/main/python/rerank/data', help='output directory')
parser.add_argument('--K', default=1000, type=int, help='number of document retrieved')
parser.add_argument('--indexmb11', default="/tuna1/indexes/lucene-index.mb11.pos+docvectors+rawdocs", help='index of MB 2011 corpus')
parser.add_argument('--indexmb13', default="/tuna1/indexes/lucene-index.mb13.pos+docvectors+rawdocs", help='index of MB 2013 corpus')
args = parser.parse_args()

searcher11 = JSearcher(JString(args.indexmb11))
searcher11.setSearchTweets(1)

searcher13 = JSearcher(JString(args.indexmb13))
searcher13.setSearchTweets(1)

for year in range(2011, 2015):
    if year in [2011, 2012]:
        searcher = searcher11
    else:
        searcher = searcher13 
    fqrel = "src/main/resources/topics-and-qrels/qrels.microblog{}.txt".format(year)
    qid2reldocids = get_qid2reldocids(fqrel)
    ftest = open("src/main/resources/topics-and-qrels/topics.microblog{}.txt".format(year))
    qid2text_time = get_qid2text_time_tweet(ftest)
    prediction_fn = "predict_{}_tweet_{}.txt".format(args.method, year)
    output_fn = os.path.join(args.output_dir, "MB-{}/trec-{}".format(args.method, year))
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
    search_tweet(searcher, prediction_fn, output_fn, qid2text_time, qid2reldocids, K=args.K) # 756
    fn_qrels = "src/main/resources/topics-and-qrels/qrels.microblog{}.txt".format(year)
    calculate_score(fn_qrels=fn_qrels, prediction=prediction_fn)


