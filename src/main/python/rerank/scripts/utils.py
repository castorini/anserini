import shlex
import subprocess
import sys
import string
printable = set(string.printable)
printable.remove("\n")
printable.remove("\t")
printable.remove("\r")
import os

# Pyjnius setup
import sys
sys.path += ['src/main/python']
from pyserini.setup import configure_classpath
configure_classpath()

from jnius import autoclass
JString = autoclass('java.lang.String')
JSearcher = autoclass('io.anserini.search.SimpleSearcher')
from nltk.tokenize import TweetTokenizer
tknzr = TweetTokenizer()

def calculate_score(fn_qrels="src/main/resources/topics-and-qrels/qrels.microblog2014.txt", prediction="score.txt"):
    cmd = "eval/trec_eval.9.0/trec_eval {judgement} {output} -m map -m recip_rank".format(output=prediction, judgement=fn_qrels)
    pargs = shlex.split(cmd)
    p = subprocess.Popen(pargs, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    pout, perr = p.communicate()
    print("running {}".format(cmd))
    if sys.version_info[0] < 3:
        lines = pout.split('\n')
    else:
        lines = pout.split(b'\n')
    MAP = float(lines[0].strip().split()[-1])
    MRR = float(lines[1].strip().split()[-1])
    print("MAP: {}".format(MAP))
    print("MRR: {}".format(MRR))
    return MAP, MRR

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

def search_tweet(searcher, prediction_fn, output_fn, qid2text_time, qid2reldocids, K=1000):
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
            b, url = parse_doc_from_index_tweet(hits[i].content)
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

def search_robust04(searcher, prediction_fn, qid2text, output_fn, qid2reldocids, K=1000):
    f = open(prediction_fn, "w")
    out = open(output_fn, "w")
    for qid in qid2text:
        a = qid2text[qid]
        hits = searcher.search(JString(a), K)
        for i in range(len(hits)):
            sim = hits[i].score
            docno = hits[i].docid
            label = 1 if qid in qid2reldocids and docno in qid2reldocids[qid] else 0
            b = parse_doc_from_index_robust04(hits[i].content)
            f.write("{} 0 {} 0 {} SimpleSearcher\n".format(qid, docno, sim))
            out.write("{}\t{}\t{}\t{}\t{}\n".format(label, a, b, qid, docno))
            out.flush()
    f.close()
    out.close()
    
def get_qid2text_time_tweet(data):
    qid2text_time = {}
    import re
    num_pattern = "<num> Number: MB(\\d+) </num>"
    query_pattern = "<query>\\s*(.*?)\\s*</query>"
    query_pattern2 = "<title>\\s*(.*?)\\s*</title>"
    querytime_pattern = "<querytweettime>\\s*(\\d+)\\s*</querytweettime>"
    for l in data:
        qid_match = re.search(num_pattern, l)
        if qid_match:
            qid = qid_match.group(1)
        else:
            query_match = re.search(query_pattern, l)
            if query_match:
                query = query_match.group(1)
            else:
                query_match2= re.search(query_pattern2, l)
                if query_match2:
                    query = query_match2.group(1)
                else:
                    querytime_match = re.search(querytime_pattern, l)
                    if querytime_match:
                        querytime = querytime_match.group(1)
                        qid2text_time[int(qid)] = (query, int(querytime))
        
    return qid2text_time

def get_qid2text_robust04(data):
    qid2text = {}
    for d in data['questions']:
        qid2text[d["id"]] = d["body"]
    return qid2text

def parse_doc_from_index_tweet(content):
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


def parse_doc_from_index_robust04(content):
    ls = content.split("\n")
    see_text = False
    doc = ""
    for l in ls:
        l = l.replace("\n", "").strip()
        if "<TEXT>" in l:
            see_text = True
        elif "</TEXT>" in l:
            break
        elif see_text:
            if l == "<P>" or l == "</P>":
                continue
            doc += l + " "
    return doc.strip()
