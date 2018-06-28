import sys
import os
import argparse

from subprocess import call

index_cmd = """
nohup sh target/appassembler/bin/IndexCollection -collection CW12Collection \
 -input /tuna1/collections/web/ClueWeb12/ -generator JsoupGenerator \
 -index lucene-index.cw12.pos+docvectors -threads 88 \
 -storePositions -storeDocvectors"""

run_cmds = [ \
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.bm25.txt -bm25",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.web.251-300.bm25.txt -bm25",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.bm25+rm3.txt -bm25 -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.web.251-300.bm25+rm3.txt -bm25 -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.ql.txt -ql",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.web.251-300.ql.txt -ql",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.web.201-250.ql+rm3.txt -ql -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.web.251-300.ql+rm3.txt -ql -rm3"]

t1_qrels = "src/main/resources/topics-and-qrels/qrels.web.201-250.txt"
t2_qrels = "src/main/resources/topics-and-qrels/qrels.web.251-300.txt"

def extract_value_from_doc(key, row, col):
    return float(os.popen("grep '{}' docs/experiments-clueweb12.md | head -{} | tail -1".format(key, row)).read().split('|')[col].strip())

def trec_eval_metric(metric, qrels, run):
    return float(os.popen("eval/trec_eval.9.0/trec_eval -m {} {} {}".format(metric, qrels, run)).read().split("\t")[2].strip())

def ndcg_eval(qrels, run):
    return round(float(os.popen("eval/gdeval.pl {} {} | grep 'amean' | sed 's/.*amean.//'".format(qrels, run)).read().split(',')[0]), 4)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Run regression tests on ClueWeb12.')
    parser.add_argument('--index', dest='index', action='store_true', help='rebuild index from scratch')

    args = parser.parse_args()

    # Decide if we're going to index from scratch. If not, use pre-stored index at known location.
    if args.index == True:
        call(index_cmd, shell=True)
        index_path = 'lucene-index.cw12.pos+docvectors'
        print(args.index)
    else:
        index_path = '/tuna1/indexes/lucene-index.cw12.pos+docvectors'

    # Use the correct index path.
    for cmd in run_cmds:
        call(cmd.format(index_path), shell=True)

    expected_t1_map = extract_value_from_doc("TREC 2013 Web Track: Topics 201-250", 1, 1)
    expected_t2_map = extract_value_from_doc("TREC 2014 Web Track: Topics 251-300", 1, 1)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.web.201-250.bm25.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.web.251-300.bm25.txt")
    print("Topics 701-750: bm25     : map  : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 751-800: bm25     : map  : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    expected_t1_map = extract_value_from_doc("TREC 2013 Web Track: Topics 201-250", 1, 2)
    expected_t2_map = extract_value_from_doc("TREC 2014 Web Track: Topics 251-300", 1, 2)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.web.201-250.bm25+rm3.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.web.251-300.bm25+rm3.txt")
    print("Topics 701-750: bm25+rm3 : map  : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 751-800: bm25+rm3 : map  : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    expected_t1_map = extract_value_from_doc("TREC 2013 Web Track: Topics 201-250", 1, 3)
    expected_t2_map = extract_value_from_doc("TREC 2014 Web Track: Topics 251-300", 1, 3)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.web.201-250.ql.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.web.251-300.ql.txt")
    print("Topics 701-750: ql       : map  : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 751-800: ql       : map  : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    expected_t1_map = extract_value_from_doc("TREC 2013 Web Track: Topics 201-250", 1, 4)
    expected_t2_map = extract_value_from_doc("TREC 2014 Web Track: Topics 251-300", 1, 4)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.web.201-250.ql+rm3.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.web.251-300.ql+rm3.txt")
    print("Topics 701-750: ql+rm3   : map  : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 751-800: ql+rm3   : map  : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))

    expected_t1_p30 = extract_value_from_doc("TREC 2013 Web Track: Topics 201-250", 2, 1)
    expected_t2_p30 = extract_value_from_doc("TREC 2014 Web Track: Topics 251-300", 2, 1)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.web.201-250.bm25.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.web.251-300.bm25.txt")
    print("Topics 701-750: bm25     : p30  : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 751-800: bm25     : p30  : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC 2013 Web Track: Topics 201-250", 2, 2)
    expected_t2_p30 = extract_value_from_doc("TREC 2014 Web Track: Topics 251-300", 2, 2)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.web.201-250.bm25+rm3.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.web.251-300.bm25+rm3.txt")
    print("Topics 701-750: bm25+rm3 : p30  : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 751-800: bm25+rm3 : p30  : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC 2013 Web Track: Topics 201-250", 2, 3)
    expected_t2_p30 = extract_value_from_doc("TREC 2014 Web Track: Topics 251-300", 2, 3)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.web.201-250.ql.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.web.251-300.ql.txt")
    print("Topics 701-750: ql       : p30  : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 751-800: ql       : p30  : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC 2013 Web Track: Topics 201-250", 2, 4)
    expected_t2_p30 = extract_value_from_doc("TREC 2014 Web Track: Topics 251-300", 2, 4)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.web.201-250.ql+rm3.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.web.251-300.ql+rm3.txt")
    print("Topics 701-750: ql+rm3   : p30  : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 751-800: ql+rm3   : p30  : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))

    expected_t1_ndcg = extract_value_from_doc("TREC 2013 Web Track: Topics 201-250", 3, 1)
    expected_t2_ndcg = extract_value_from_doc("TREC 2014 Web Track: Topics 251-300", 3, 1)
    actual_t1_ndcg = ndcg_eval(t1_qrels, "run.web.201-250.bm25.txt")
    actual_t2_ndcg = ndcg_eval(t2_qrels, "run.web.251-300.bm25.txt")
    print("Topics 701-750: bm25     : ndcg : %.4f %.4f" % (expected_t1_ndcg, actual_t1_ndcg) + ('  !' if expected_t1_ndcg != actual_t1_ndcg else ''))
    print("Topics 751-800: bm25     : ndcg : %.4f %.4f" % (expected_t2_ndcg, actual_t2_ndcg) + ('  !' if expected_t2_ndcg != actual_t2_ndcg else ''))
    expected_t1_ndcg = extract_value_from_doc("TREC 2013 Web Track: Topics 201-250", 3, 2)
    expected_t2_ndcg = extract_value_from_doc("TREC 2014 Web Track: Topics 251-300", 3, 2)
    actual_t1_ndcg = ndcg_eval(t1_qrels, "run.web.201-250.bm25+rm3.txt")
    actual_t2_ndcg = ndcg_eval(t2_qrels, "run.web.251-300.bm25+rm3.txt")
    print("Topics 701-750: bm25+rm3 : ndcg : %.4f %.4f" % (expected_t1_ndcg, actual_t1_ndcg) + ('  !' if expected_t1_ndcg != actual_t1_ndcg else ''))
    print("Topics 751-800: bm25+rm3 : ndcg : %.4f %.4f" % (expected_t2_ndcg, actual_t2_ndcg) + ('  !' if expected_t2_ndcg != actual_t2_ndcg else ''))
    expected_t1_ndcg = extract_value_from_doc("TREC 2013 Web Track: Topics 201-250", 3, 3)
    expected_t2_ndcg = extract_value_from_doc("TREC 2014 Web Track: Topics 251-300", 3, 3)
    actual_t1_ndcg = ndcg_eval(t1_qrels, "run.web.201-250.ql.txt")
    actual_t2_ndcg = ndcg_eval(t2_qrels, "run.web.251-300.ql.txt")
    print("Topics 701-750: ql       : ndcg : %.4f %.4f" % (expected_t1_ndcg, actual_t1_ndcg) + ('  !' if expected_t1_ndcg != actual_t1_ndcg else ''))
    print("Topics 751-800: ql       : ndcg : %.4f %.4f" % (expected_t2_ndcg, actual_t2_ndcg) + ('  !' if expected_t2_ndcg != actual_t2_ndcg else ''))
    expected_t1_ndcg = extract_value_from_doc("TREC 2013 Web Track: Topics 201-250", 3, 4)
    expected_t2_ndcg = extract_value_from_doc("TREC 2014 Web Track: Topics 251-300", 3, 4)
    actual_t1_ndcg = ndcg_eval(t1_qrels, "run.web.201-250.ql+rm3.txt")
    actual_t2_ndcg = ndcg_eval(t2_qrels, "run.web.251-300.ql+rm3.txt")
    print("Topics 701-750: ql+rm3   : ndcg : %.4f %.4f" % (expected_t1_ndcg, actual_t1_ndcg) + ('  !' if expected_t1_ndcg != actual_t1_ndcg else ''))
    print("Topics 751-800: ql+rm3   : ndcg : %.4f %.4f" % (expected_t2_ndcg, actual_t2_ndcg) + ('  !' if expected_t2_ndcg != actual_t2_ndcg else ''))
