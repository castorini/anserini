import string
printable = set(string.printable)
printable.remove("\n")
printable.remove("\t")
printable.remove("\r")
import os

import jnius_config
jnius_config.set_classpath("target/anserini-0.4.1-SNAPSHOT-fatjar.jar")
from jnius import autoclass
JString = autoclass('java.lang.String')
JSearcher = autoclass('io.anserini.search.SimpleSearcher')

def cal_score(fn_qrels="src/main/resources/topics-and-qrels/qrels.microblog2014.txt", prediction="score.txt"):
    cmd = "/bin/sh run_eval_new.sh {} {}".format(prediction, fn_qrels)
    pargs = shlex.split(cmd)
    p = subprocess.Popen(pargs, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    pout, perr = p.communicate()
    print("running {}".format(cmd))
    if sys.version_info[0] < 3:
        lines = pout.split('\n')
    else:
        lines = pout.split(b'\n')
    Map = float(lines[0].strip().split()[-1])
    Mrr = float(lines[1].strip().split()[-1])
    P20 = float(lines[2].strip().split()[-1])
    P30 = float(lines[3].strip().split()[-1])
    NDCG20 = float(lines[4].strip().split()[-1])
    print(Map)
    print(Mrr)
    print(P30)
    print(P20)
    print(NDCG20)
    return Map, Mrr, P30, P20, NDCG20

def get_qid2reldocids(fqrel):
    f = open(fqrel)
    qid2reldocids = {}
    for l in f:
        qid, _, docid, score = l.replace("\n", "").strip().split()
        qid = int(qid)
        if int(score) > 0:
            if qid not in qid2reldocids:
                qid2reldocids[qid] = set()
            qid2reldocids[qid].add(docid)
    return qid2reldocids

def search(searcher, prediction_fn, output_fn, qid2text_time, qid2reldocids, K=1000):
    f = open(prediction_fn, "w")
    outa = open(os.path.join(output_fn, "a.toks"), "w") # , encoding="utf-8"
    outb = open(os.path.join(output_fn, "b.toks"), "w") # , encoding="utf-8"
    outid = open(os.path.join(output_fn, "id.txt"), "w") # , encoding="utf-8"
    outsim = open(os.path.join(output_fn, "sim.txt"), "w") # , encoding="utf-8"
    outurl = open(os.path.join(output_fn, "url.txt"), "w", encoding="utf-8") # , encoding="utf-8"
    for qid in qid2text_time:
        a, t = qid2text_time[qid]
        hits = searcher.search(JString(a), K, t)
        for i in range(len(hits)):
            sim = hits[i].score
            docno = hits[i].docid
            label = 1 if qid in qid2reldocids and docno in qid2reldocids[qid] else 0
            b, url = parse_doc_from_index(hits[i].content)
            b = "".join(filter(lambda x: x in printable, b))
            f.write("{} Q0 {} {} {:.6f} Anserini\n".format(qid, docno, i+1, sim))
            outa.write("{}\n".format(a))
            outb.write("{}\n".format(b))
            outid.write("{} Q0 {} {} {:.6f} Anserini\n".format(qid, docno, i+1, sim))
            outsim.write("{}\n".format(label))
            outurl.write("{}\n".format(url))
    outa.close()
    outb.close()
    outid.close()
    outsim.close()
    outurl.close()
    f.close()
    
def get_qid2text_time_new(data):
    qid2text_time = {}
    import re
    num_pattern = "<num> Number: MB(\\d+) </num>"
    query_pattern = "<query>\\s*(.*?)\\s*</query>"
    query_pattern2 = "<title>\\s*(.*?)\\s*</title>"
    querytime_pattern = "<querytweettime>\\s*(\\d+)\\s*</querytweettime>"
    for l in data:
        tmp = re.search(num_pattern, l)
        if tmp:
            qid = tmp.group(1)
        else:
            tmp = re.search(query_pattern, l)
            if tmp:
                query = tmp.group(1)
            else:
                tmp = re.search(query_pattern2, l)
                if tmp:
                    query = tmp.group(1)
                else:
                    tmp = re.search(querytime_pattern, l)
                    if tmp:
                        querytime = tmp.group(1)
                        qid2text_time[int(qid)] = (query, int(querytime))
        
    return qid2text_time

from nltk.tokenize import TweetTokenizer
tknzr = TweetTokenizer()
def parse_doc_from_index(content):
    import json
    text = json.loads(content)["text"].replace("\n", " ").replace("\t", " ").replace("\r", " ")
    bs = []
    urls = []
    for w in text.split():
        if not w.startswith("http"):
            bs.append(w)
        else:
            urls.append(w)
    b = " ".join(bs)
    url = " ".join(urls)
    b = " ".join(tknzr.tokenize(b))
    return b, url
