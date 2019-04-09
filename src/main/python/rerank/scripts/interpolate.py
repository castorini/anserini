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


def get_map_inter(docno2sim, docno2sim_ql, l, debug=False, mode="train"):
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
    # always remember to close the file !
    f_inter.close()
    
    cmd = "./eval/trec_eval.9.0/trec_eval {} {} -m ndcg_cut.20 -m map -m recip_rank -m P.20,30".format(fn_qrels, fn_inter)
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

def get_inter_tune(l, fn_baseline_train, fn_rerank_valid, debug=False):
    docno2sim = {}
    docno2sim_ql = {}
    docno2sim_ql = get_docsim(fn_baseline_train, docno2sim_ql)
    docno2sim = get_docsim(fn_rerank_valid, docno2sim)
    return get_map_inter(docno2sim, docno2sim_ql, l, debug=debug)

def select_l(model, fn_baseline_train, fn_rerank_valid, debug=False):
    maxL = 0
    maxMap = 0
    ll = []
    for l in range(0, 100, 5):
        l = l / 100.0
        # print("trying lambda: {:.2f}".format(l))
        Map, P30, Mrr, P20, NDCG20 = get_inter_tune(l, fn_baseline_train, fn_rerank_valid, debug=debug)
        ll.append(Map)
        if Map > maxMap:
            maxMap = Map
            maxL = l
    print("best lambda: {} with MAP: {}".format(maxL, maxMap))
    return maxL, ll

def get_inter_test(model, l, fn_baseline, fn_rerank):
    docno2sim = {}
    docno2sim_ql = {}
    docno2sim_ql = get_docsim(fn_baseline, docno2sim_ql)
    docno2sim = get_docsim(fn_rerank, docno2sim)
    return get_map_inter(docno2sim, docno2sim_ql, l, debug=False, mode="test")


if __name__ == '__main__':

	parser = argparse.ArgumentParser()
	parser.add_argument('--fn_baseline', default='BM25_0.9_0.5', help='[QL, QL+RM3, BM25, BM25+RM3] + parameters, which is in the ')
	parser.add_argument('--fn_baseline_dev', default='drmm', help='[drmm, knrm, ...]')
	aarser.add_argument('--fn_rerank', default=1000, type=int, help='number of document retrieved')
	parser.add_argument('--fn_rerank_valid', default="/tuna1/indexes/lucene-index.robust04.pos+docvectors+rawdocs", help='index of Robust04 corpus')
	parser.add_argument('--fn_qrels', default="../../../resources/topics-and-qrels/qrels.robust2004.txt", help='qrels file of Robust04')
	args = parser.parse_args()
		
	maxL, ll = select_l(model, fn_baseline_dev, fn_rerank_valid, debug=False)
	testMap, P30, MRR, P20, NDCG20 = get_inter_test(model=model, l=maxL, fn_baseline=fn_baseline, fn_rerank=fn_rerank)
	print("Model: {}, Map={:.4f}, MRR={:.4f}, P30={:.4f}, P20={:.4f}, NDCG20={:.4f}, with lambda = {}"
          .format(model, testMap, MRR, P30, P20, NDCG20, maxL))
