'''scripts to aggregate the retrieval scores and the reranking scores by linear interpolation

This scipt requires five input files:

1. Four run files following the TREC format (query-id Q0 document-id rank score STANDARD)
of both the baseline run and the rerank run on both the validation set and the test set
2. One qrel file following the TREC format (query-id 0 document-id relevance) that contains
relevance info on the whole dataset (including dev and test set)

This script will print out the tuned scores on the test set (Map, P30, Mrr, P20, NDCG20).
You can modify the trec_eval arguments to try other evaluation metrics.

The hyper-parameter \lamda is tuned on the dev set
'''
import numpy as np
import shlex
import subprocess
import sys
import pprint
import argparse

def get_docsim(fn, docno2sim={}):
    p = open(fn)
    docno_list = []
    sim_list = []
    count = 0
    for l in p:
        ls = l[:-1].split()
        if len(ls) == 7:
            qid, iternum, docno, rank, sim, run_id, label = ls
        else:
            qid, iternum, docno, rank, sim, run_id = ls
        if docno+"_"+qid in docno2sim:
            # print("docno {} already in docno2sim".format(docno))
            count += 1
        else:
            docno2sim[docno+"_"+qid] = float(sim)
        sim_list.append(float(sim))
        docno_list.append(docno+"_"+qid)
    maxSim = max(sim_list)
    minSim = min(sim_list)
    # print("count: {}".format(count))
    for docnoqid in docno_list:
        docno2sim[docnoqid] = (docno2sim[docnoqid] - minSim) / (maxSim - minSim)
    return docno2sim


def get_map_inter(docno2sim, docno2sim_ql, l, model, fn_qrels, debug=False, mode="train"):
    docno2sim_inter = {}
    for docnoqid in docno2sim:
        if docnoqid in docno2sim_ql:
            docno2sim_inter[docnoqid] = docno2sim[docnoqid] * l + docno2sim_ql[docnoqid] * (1 -l)
    if mode == "train":
        fn_inter = "predict.inter.{}.{}.l{:.2f}".format(mode, model, l)
    else:
        fn_inter = "predict.inter.{}.{}".format(mode, model)
        
    f_inter = open(fn_inter, "w")
    if debug:
        print("total pairs: {}, written to file: {}".format(len(docno2sim_inter), fn_inter))
    for docnoqid in docno2sim_inter:
        temp = docnoqid.split("_")
        docno = temp[0]
        qid = temp[1]
        score = docno2sim_inter[docno+"_"+qid]
        f_inter.write('{} 0 {} 0 {} {}\n'.format(qid, docno, score, model))
    f_inter.close()
    
    cmd = "./eval/trec_eval.9.0.4/trec_eval {} {} -m ndcg_cut.20 -m map -m recip_rank -m P.20,30".format(fn_qrels, fn_inter)
    pargs = shlex.split(cmd)
    p = subprocess.Popen(pargs, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    pout, perr = p.communicate()
    if debug:
        print("running {}".format(cmd))
        if len(pout) != 0:
            print(pout.decode('utf-8'))
        else:
            print(perr.decode('utf-8'))
    if sys.version_info[0] < 3:
        lines = pout.split('\n')
    else:
        lines = pout.split(b'\n')
    Map = float(lines[0].strip().split()[-1])
    Mrr = float(lines[1].strip().split()[-1])
    P20 = float(lines[2].strip().split()[-1])
    P30 = float(lines[3].strip().split()[-1])
    NDCG20 = float(lines[4].strip().split()[-1])
    return Map, P30, Mrr, P20, NDCG20

def get_inter_tune(l, fn_baseline_dev, fn_rerank_dev, model, fn_qrels, debug=False):
    docno2sim = {}
    docno2sim_ql = {}
    docno2sim_ql = get_docsim(fn_baseline_dev, docno2sim_ql)
    docno2sim = get_docsim(fn_rerank_dev, docno2sim)
    return get_map_inter(docno2sim, docno2sim_ql, l, model, fn_qrels, debug=debug, mode="dev")

def tune_lambda(fn_baseline_train, fn_rerank_dev, model, fn_qrels, debug=False):
    maxL = 0
    maxMap = 0
    ll = []
    for l in range(0, 100, 5):
        l = l / 100.0
        Map, P30, Mrr, P20, NDCG20 = get_inter_tune(l, fn_baseline_train, fn_rerank_dev, model=model, debug=debug, fn_qrels=fn_qrels)
        ll.append(Map)
        if Map > maxMap:
            maxMap = Map
            maxL = l
    print("best lambda: {} with MAP: {}".format(maxL, maxMap))
    return maxL, ll

def get_inter_test(model, l, fn_baseline, fn_rerank, fn_qrels, debug=False):
    docno2sim = {}
    docno2sim_ql = {}
    docno2sim_ql = get_docsim(fn_baseline, docno2sim_ql)
    docno2sim = get_docsim(fn_rerank, docno2sim)
    return get_map_inter(docno2sim, docno2sim_ql, l, fn_qrels=fn_qrels, debug=debug, mode="test", model=model)


if __name__ == '__main__':
	parser = argparse.ArgumentParser()
	parser.add_argument('--fn_baseline', default='predict_BM25_0.9_0.5_RM3_47_9_0.3_robust04_split1_test.txt', help='file name of the baseline run on the test set')
	parser.add_argument('--fn_baseline_dev', default='predict_BM25_0.9_0.5_RM3_47_9_0.3_robust04_split1_dev.txt', help='file name of the baseline run on the dev set')
	parser.add_argument('--fn_rerank', default='src/main/python/rerank/MatchZoo/data/robust04/predict.test.drmm.txt', help='file name of the rerank run on the test set')
	parser.add_argument('--fn_rerank_dev', default='src/main/python/rerank/MatchZoo/data/robust04/predict.valid.drmm.txt', help='file name of the rerank run on the dev set')
	parser.add_argument('--fn_qrels', default="src/main/resources/topics-and-qrels/qrels.robust2004.txt", help='qrels file of Robust04')
	parser.add_argument('--model_rerank', default="drmm", help='rerank model name')
	parser.add_argument('--debug', action='store_true', help='qrels file of Robust04')
	args = parser.parse_args()
		
	maxL, ll = tune_lambda(args.fn_baseline_dev, args.fn_rerank_dev, model=args.model_rerank, fn_qrels=args.fn_qrels, debug=args.debug)
	testMap, P30, MRR, P20, NDCG20 = get_inter_test(model=args.model_rerank, l=maxL, fn_qrels=args.fn_qrels, fn_baseline=args.fn_baseline, fn_rerank=args.fn_rerank, debug=args.debug)
	print("Model: {}, Map={:.4f}, MRR={:.4f}, P30={:.4f}, P20={:.4f}, NDCG20={:.4f}, with lambda = {}"
          .format(args.model_rerank, testMap, MRR, P30, P20, NDCG20, maxL))
