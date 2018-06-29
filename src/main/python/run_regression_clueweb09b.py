import sys
import os
import argparse

from subprocess import call

index_cmd = """
nohup sh target/appassembler/bin/IndexCollection -collection CW09Collection \
 -input /tuna1/collections/web/ClueWeb09b/ClueWeb09_English_1/ -generator JsoupGenerator \
 -index lucene-index.cw09b.pos+docvectors -threads 32 \
 -storePositions -storeDocvectors"""

run_cmds = [ \
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.web.51-100.bm25.txt -bm25",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.web.101-150.bm25.txt -bm25",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.web.151-200.bm25.txt -bm25",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.web.51-100.bm25+rm3.txt -bm25 -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.web.101-150.bm25+rm3.txt -bm25 -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.web.151-200.bm25+rm3.txt -bm25 -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.web.51-100.ql.txt -ql",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.web.101-150.ql.txt -ql",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.web.151-200.ql.txt -ql",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.51-100.txt -output run.web.51-100.ql+rm3.txt -ql -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.101-150.txt -output run.web.101-150.ql+rm3.txt -ql -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Webxml -index {} -topics src/main/resources/topics-and-qrels/topics.web.151-200.txt -output run.web.151-200.ql+rm3.txt -ql -rm3"]


t1_qrels = "src/main/resources/topics-and-qrels/qrels.web.51-100.txt"
t2_qrels = "src/main/resources/topics-and-qrels/qrels.web.101-150.txt"
t3_qrels = "src/main/resources/topics-and-qrels/qrels.web.151-200.txt"

def extract_value_from_doc(key, row, col):
    return float(os.popen("grep '{}' docs/experiments-clueweb09b.md | head -{} | tail -1".format(key, row)).read().split('|')[col].strip())

def trec_eval_metric(metric, qrels, run):
    return float(os.popen("eval/trec_eval.9.0/trec_eval -m {} {} {}".format(metric, qrels, run)).read().split("\t")[2].strip())

def ndcg_eval(qrels, run):
    return round(float(os.popen("eval/gdeval.pl {} {} | grep 'amean' | sed 's/.*amean.//'".format(qrels, run)).read().split(',')[0]), 4)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Run regression tests on ClueWeb09b.')
    parser.add_argument('--index', dest='index', action='store_true', help='rebuild index from scratch')

    args = parser.parse_args()

    # Decide if we're going to index from scratch. If not, use pre-stored index at known location.
    if args.index == True:
        call(index_cmd, shell=True)
        index_path = 'lucene-index.cw09b.pos+docvectors'
        print(args.index)
    else:
        index_path = '/tuna1/indexes/lucene-index.cw09b.pos+docvectors'

    # Use the correct index path.
    for cmd in run_cmds:
        call(cmd.format(index_path), shell=True)

    expected_t1_map = extract_value_from_doc("TREC 2010 Web Track: Topics 51-100", 1, 1)
    expected_t2_map = extract_value_from_doc("TREC 2011 Web Track: Topics 101-150", 1, 1)
    expected_t3_map = extract_value_from_doc("TREC 2012 Web Track: Topics 151-200", 1, 1)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.web.51-100.bm25.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.web.101-150.bm25.txt")
    actual_t3_map = trec_eval_metric("map", t3_qrels, "run.web.151-200.bm25.txt")
    print("Topics  51-100: bm25     : map  : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 101-150: bm25     : map  : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 151-200: bm25     : map  : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))
    expected_t1_map = extract_value_from_doc("TREC 2010 Web Track: Topics 51-100", 1, 2)
    expected_t2_map = extract_value_from_doc("TREC 2011 Web Track: Topics 101-150", 1, 2)
    expected_t3_map = extract_value_from_doc("TREC 2012 Web Track: Topics 151-200", 1, 2)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.web.51-100.bm25+rm3.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.web.101-150.bm25+rm3.txt")
    actual_t3_map = trec_eval_metric("map", t3_qrels, "run.web.151-200.bm25+rm3.txt")
    print("Topics  51-100: bm25+rm3 : map  : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 101-150: bm25+rm3 : map  : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 151-200: bm25+rm3 : map  : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))
    expected_t1_map = extract_value_from_doc("TREC 2010 Web Track: Topics 51-100", 1, 3)
    expected_t2_map = extract_value_from_doc("TREC 2011 Web Track: Topics 101-150", 1, 3)
    expected_t3_map = extract_value_from_doc("TREC 2012 Web Track: Topics 151-200", 1, 3)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.web.51-100.ql.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.web.101-150.ql.txt")
    actual_t3_map = trec_eval_metric("map", t3_qrels, "run.web.151-200.ql.txt")
    print("Topics  51-100: ql       : map  : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 101-150: ql       : map  : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 151-200: ql       : map  : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))
    expected_t1_map = extract_value_from_doc("TREC 2010 Web Track: Topics 51-100", 1, 4)
    expected_t2_map = extract_value_from_doc("TREC 2011 Web Track: Topics 101-150", 1, 4)
    expected_t3_map = extract_value_from_doc("TREC 2012 Web Track: Topics 151-200", 1, 4)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.web.51-100.ql+rm3.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.web.101-150.ql+rm3.txt")
    actual_t3_map = trec_eval_metric("map", t3_qrels, "run.web.151-200.ql+rm3.txt")
    print("Topics  51-100: ql+rm3   : map  : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 101-150: ql+rm3   : map  : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 151-200: ql+rm3   : map  : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))

    expected_t1_p30 = extract_value_from_doc("TREC 2010 Web Track: Topics 51-100", 2, 1)
    expected_t2_p30 = extract_value_from_doc("TREC 2011 Web Track: Topics 101-150", 2, 1)
    expected_t3_p30 = extract_value_from_doc("TREC 2012 Web Track: Topics 151-200", 2, 1)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.web.51-100.bm25.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.web.101-150.bm25.txt")
    actual_t3_p30 = trec_eval_metric("P.30", t3_qrels, "run.web.151-200.bm25.txt")
    print("Topics  51-100: bm25     : p30  : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 101-150: bm25     : p30  : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 151-200: bm25     : p30  : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC 2010 Web Track: Topics 51-100", 2, 2)
    expected_t2_p30 = extract_value_from_doc("TREC 2011 Web Track: Topics 101-150", 2, 2)
    expected_t3_p30 = extract_value_from_doc("TREC 2012 Web Track: Topics 151-200", 2, 2)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.web.51-100.bm25+rm3.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.web.101-150.bm25+rm3.txt")
    actual_t3_p30 = trec_eval_metric("P.30", t3_qrels, "run.web.151-200.bm25+rm3.txt")
    print("Topics  51-100: bm25+rm3 : p30  : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 101-150: bm25+rm3 : p30  : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 151-200: bm25+rm3 : p30  : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC 2010 Web Track: Topics 51-100", 2, 3)
    expected_t2_p30 = extract_value_from_doc("TREC 2011 Web Track: Topics 101-150", 2, 3)
    expected_t3_p30 = extract_value_from_doc("TREC 2012 Web Track: Topics 151-200", 2, 3)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.web.51-100.ql.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.web.101-150.ql.txt")
    actual_t3_p30 = trec_eval_metric("P.30", t3_qrels, "run.web.151-200.ql.txt")
    print("Topics  51-100: ql       : p30  : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 101-150: ql       : p30  : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 151-200: ql       : p30  : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC 2010 Web Track: Topics 51-100", 2, 4)
    expected_t2_p30 = extract_value_from_doc("TREC 2011 Web Track: Topics 101-150", 2, 4)
    expected_t3_p30 = extract_value_from_doc("TREC 2012 Web Track: Topics 151-200", 2, 4)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.web.51-100.ql+rm3.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.web.101-150.ql+rm3.txt")
    actual_t3_p30 = trec_eval_metric("P.30", t3_qrels, "run.web.151-200.ql+rm3.txt")
    print("Topics  51-100: ql+rm3   : p30  : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 101-150: ql+rm3   : p30  : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 151-200: ql+rm3   : p30  : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))

    expected_t1_ndcg = extract_value_from_doc("TREC 2010 Web Track: Topics 51-100", 3, 1)
    expected_t2_ndcg = extract_value_from_doc("TREC 2011 Web Track: Topics 101-150", 3, 1)
    expected_t3_ndcg = extract_value_from_doc("TREC 2012 Web Track: Topics 151-200", 3, 1)
    actual_t1_ndcg = ndcg_eval(t1_qrels, "run.web.51-100.bm25.txt")
    actual_t2_ndcg = ndcg_eval(t2_qrels, "run.web.101-150.bm25.txt")
    actual_t3_ndcg = ndcg_eval(t3_qrels, "run.web.151-200.bm25.txt")
    print("Topics  51-100: bm25     : ndcg : %.4f %.4f" % (expected_t1_ndcg, actual_t1_ndcg) + ('  !' if expected_t1_ndcg != actual_t1_ndcg else ''))
    print("Topics 101-150: bm25     : ndcg : %.4f %.4f" % (expected_t2_ndcg, actual_t2_ndcg) + ('  !' if expected_t2_ndcg != actual_t2_ndcg else ''))
    print("Topics 151-200: bm25     : ndcg : %.4f %.4f" % (expected_t3_ndcg, actual_t3_ndcg) + ('  !' if expected_t3_ndcg != actual_t3_ndcg else ''))
    expected_t1_ndcg = extract_value_from_doc("TREC 2010 Web Track: Topics 51-100", 3, 2)
    expected_t2_ndcg = extract_value_from_doc("TREC 2011 Web Track: Topics 101-150", 3, 2)
    expected_t3_ndcg = extract_value_from_doc("TREC 2012 Web Track: Topics 151-200", 3, 2)
    actual_t1_ndcg = ndcg_eval(t1_qrels, "run.web.51-100.bm25+rm3.txt")
    actual_t2_ndcg = ndcg_eval(t2_qrels, "run.web.101-150.bm25+rm3.txt")
    actual_t3_ndcg = ndcg_eval(t3_qrels, "run.web.151-200.bm25+rm3.txt")
    print("Topics  51-100: bm25+rm3 : ndcg : %.4f %.4f" % (expected_t1_ndcg, actual_t1_ndcg) + ('  !' if expected_t1_ndcg != actual_t1_ndcg else ''))
    print("Topics 101-150: bm25+rm3 : ndcg : %.4f %.4f" % (expected_t2_ndcg, actual_t2_ndcg) + ('  !' if expected_t2_ndcg != actual_t2_ndcg else ''))
    print("Topics 151-200: bm25+rm3 : ndcg : %.4f %.4f" % (expected_t3_ndcg, actual_t3_ndcg) + ('  !' if expected_t3_ndcg != actual_t3_ndcg else ''))
    expected_t1_ndcg = extract_value_from_doc("TREC 2010 Web Track: Topics 51-100", 3, 3)
    expected_t2_ndcg = extract_value_from_doc("TREC 2011 Web Track: Topics 101-150", 3, 3)
    expected_t3_ndcg = extract_value_from_doc("TREC 2012 Web Track: Topics 151-200", 3, 3)
    actual_t1_ndcg = ndcg_eval(t1_qrels, "run.web.51-100.ql.txt")
    actual_t2_ndcg = ndcg_eval(t2_qrels, "run.web.101-150.ql.txt")
    actual_t3_ndcg = ndcg_eval(t3_qrels, "run.web.151-200.ql.txt")
    print("Topics  51-100: ql       : ndcg : %.4f %.4f" % (expected_t1_ndcg, actual_t1_ndcg) + ('  !' if expected_t1_ndcg != actual_t1_ndcg else ''))
    print("Topics 101-150: ql       : ndcg : %.4f %.4f" % (expected_t2_ndcg, actual_t2_ndcg) + ('  !' if expected_t2_ndcg != actual_t2_ndcg else ''))
    print("Topics 151-200: ql       : ndcg : %.4f %.4f" % (expected_t3_ndcg, actual_t3_ndcg) + ('  !' if expected_t3_ndcg != actual_t3_ndcg else ''))
    expected_t1_ndcg = extract_value_from_doc("TREC 2010 Web Track: Topics 51-100", 3, 4)
    expected_t2_ndcg = extract_value_from_doc("TREC 2011 Web Track: Topics 101-150", 3, 4)
    expected_t3_ndcg = extract_value_from_doc("TREC 2012 Web Track: Topics 151-200", 3, 4)
    actual_t1_ndcg = ndcg_eval(t1_qrels, "run.web.51-100.ql+rm3.txt")
    actual_t2_ndcg = ndcg_eval(t2_qrels, "run.web.101-150.ql+rm3.txt")
    actual_t3_ndcg = ndcg_eval(t3_qrels, "run.web.151-200.ql+rm3.txt")
    print("Topics  51-100: ql+rm3   : ndcg : %.4f %.4f" % (expected_t1_ndcg, actual_t1_ndcg) + ('  !' if expected_t1_ndcg != actual_t1_ndcg else ''))
    print("Topics 101-150: ql+rm3   : ndcg : %.4f %.4f" % (expected_t2_ndcg, actual_t2_ndcg) + ('  !' if expected_t2_ndcg != actual_t2_ndcg else ''))
    print("Topics 151-200: ql+rm3   : ndcg : %.4f %.4f" % (expected_t3_ndcg, actual_t3_ndcg) + ('  !' if expected_t3_ndcg != actual_t3_ndcg else ''))
