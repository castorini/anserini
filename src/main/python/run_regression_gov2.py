import sys
import os
import argparse

from subprocess import call

index_cmd = """
nohup sh target/appassembler/bin/IndexCollection -collection Gov2Collection \
 -input /tuna1/collections/web/gov2/gov2-corpus/ -generator JsoupGenerator \
 -index lucene-index.gov2.pos+docvectors -threads 16 \
 -storePositions -storeDocvectors"""

run_cmds = [ \
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.701-750.bm25.txt -bm25",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.751-800.txt -output run.gov2.751-800.bm25.txt -bm25",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.801-850.txt -output run.gov2.801-850.bm25.txt -bm25",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.701-750.bm25+rm3.txt -bm25 -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.751-800.txt -output run.gov2.751-800.bm25+rm3.txt -bm25 -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.801-850.txt -output run.gov2.801-850.bm25+rm3.txt -bm25 -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.701-750.ql.txt -ql",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.751-800.txt -output run.gov2.751-800.ql.txt -ql",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.801-850.txt -output run.gov2.801-850.ql.txt -ql",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.701-750.ql+rm3.txt -ql -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.751-800.txt -output run.gov2.751-800.ql+rm3.txt -ql -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.801-850.txt -output run.gov2.801-850.ql+rm3.txt -ql -rm3"]

t1_qrels = "src/main/resources/topics-and-qrels/qrels.701-750.txt"
t2_qrels = "src/main/resources/topics-and-qrels/qrels.751-800.txt"
t3_qrels = "src/main/resources/topics-and-qrels/qrels.801-850.txt"

def extract_value_from_doc(key, row, col):
    return float(os.popen("grep '{}' docs/experiments-gov2.md | head -{} | tail -1".format(key, row)).read().split('|')[col].strip())

def trec_eval_metric(metric, qrels, run):
    return float(os.popen("eval/trec_eval.9.0/trec_eval -m {} {} {}".format(metric, qrels, run)).read().split("\t")[2].strip())

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Run regression tests on Gov2.')
    parser.add_argument('--index', dest='index', action='store_true', help='rebuild index from scratch')

    args = parser.parse_args()

    # Decide if we're going to index from scratch. If not, use pre-stored index at known location.
    if args.index == True:
        call(index_cmd, shell=True)
        index_path = 'lucene-index.gov2.pos+docvectors'
        print(args.index)
    else:
        index_path = '/tuna1/indexes/lucene-index.gov2.pos+docvectors'

    # Use the correct index path.
    for cmd in run_cmds:
        call(cmd.format(index_path), shell=True)

    expected_t1_map = extract_value_from_doc("TREC 2004 Terabyte Track: Topics 701-750", 1, 1)
    expected_t2_map = extract_value_from_doc("TREC 2005 Terabyte Track: Topics 751-800", 1, 1)
    expected_t3_map = extract_value_from_doc("TREC 2006 Terabyte Track: Topics 801-850", 1, 1)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.gov2.701-750.bm25.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.gov2.751-800.bm25.txt")
    actual_t3_map = trec_eval_metric("map", t3_qrels, "run.gov2.801-850.bm25.txt")
    print("Topics 701-750: bm25     : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 751-800: bm25     : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 801-850: bm25     : map : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))
    expected_t1_map = extract_value_from_doc("TREC 2004 Terabyte Track: Topics 701-750", 1, 2)
    expected_t2_map = extract_value_from_doc("TREC 2005 Terabyte Track: Topics 751-800", 1, 2)
    expected_t3_map = extract_value_from_doc("TREC 2006 Terabyte Track: Topics 801-850", 1, 2)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.gov2.701-750.bm25+rm3.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.gov2.751-800.bm25+rm3.txt")
    actual_t3_map = trec_eval_metric("map", t3_qrels, "run.gov2.801-850.bm25+rm3.txt")
    print("Topics 701-750: bm25+rm3 : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 751-800: bm25+rm3 : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 801-850: bm25+rm3 : map : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))
    expected_t1_map = extract_value_from_doc("TREC 2004 Terabyte Track: Topics 701-750", 1, 3)
    expected_t2_map = extract_value_from_doc("TREC 2005 Terabyte Track: Topics 751-800", 1, 3)
    expected_t3_map = extract_value_from_doc("TREC 2006 Terabyte Track: Topics 801-850", 1, 3)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.gov2.701-750.ql.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.gov2.751-800.ql.txt")
    actual_t3_map = trec_eval_metric("map", t3_qrels, "run.gov2.801-850.ql.txt")
    print("Topics 701-750: ql       : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 751-800: ql       : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 801-850: ql       : map : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))
    expected_t1_map = extract_value_from_doc("TREC 2004 Terabyte Track: Topics 701-750", 1, 4)
    expected_t2_map = extract_value_from_doc("TREC 2005 Terabyte Track: Topics 751-800", 1, 4)
    expected_t3_map = extract_value_from_doc("TREC 2006 Terabyte Track: Topics 801-850", 1, 4)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.gov2.701-750.ql+rm3.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.gov2.751-800.ql+rm3.txt")
    actual_t3_map = trec_eval_metric("map", t3_qrels, "run.gov2.801-850.ql+rm3.txt")
    print("Topics 701-750: ql+rm3   : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 751-800: ql+rm3   : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 801-850: ql+rm3   : map : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))

    expected_t1_p30 = extract_value_from_doc("TREC 2004 Terabyte Track: Topics 701-750", 2, 1)
    expected_t2_p30 = extract_value_from_doc("TREC 2005 Terabyte Track: Topics 751-800", 2, 1)
    expected_t3_p30 = extract_value_from_doc("TREC 2006 Terabyte Track: Topics 801-850", 2, 1)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.gov2.701-750.bm25.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.gov2.751-800.bm25.txt")
    actual_t3_p30 = trec_eval_metric("P.30", t3_qrels, "run.gov2.801-850.bm25.txt")
    print("Topics 701-750: bm25     : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 751-800: bm25     : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 801-850: bm25     : p30 : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC 2004 Terabyte Track: Topics 701-750", 2, 2)
    expected_t2_p30 = extract_value_from_doc("TREC 2005 Terabyte Track: Topics 751-800", 2, 2)
    expected_t3_p30 = extract_value_from_doc("TREC 2006 Terabyte Track: Topics 801-850", 2, 2)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.gov2.701-750.bm25+rm3.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.gov2.751-800.bm25+rm3.txt")
    actual_t3_p30 = trec_eval_metric("P.30", t3_qrels, "run.gov2.801-850.bm25+rm3.txt")
    print("Topics 701-750: bm25+rm3 : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 751-800: bm25+rm3 : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 801-850: bm25+rm3 : p30 : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC 2004 Terabyte Track: Topics 701-750", 2, 3)
    expected_t2_p30 = extract_value_from_doc("TREC 2005 Terabyte Track: Topics 751-800", 2, 3)
    expected_t3_p30 = extract_value_from_doc("TREC 2006 Terabyte Track: Topics 801-850", 2, 3)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.gov2.701-750.ql.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.gov2.751-800.ql.txt")
    actual_t3_p30 = trec_eval_metric("P.30", t3_qrels, "run.gov2.801-850.ql.txt")
    print("Topics 701-750: ql       : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 751-800: ql       : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 801-850: ql       : p30 : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC 2004 Terabyte Track: Topics 701-750", 2, 4)
    expected_t2_p30 = extract_value_from_doc("TREC 2005 Terabyte Track: Topics 751-800", 2, 4)
    expected_t3_p30 = extract_value_from_doc("TREC 2006 Terabyte Track: Topics 801-850", 2, 4)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.gov2.701-750.ql+rm3.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.gov2.751-800.ql+rm3.txt")
    actual_t3_p30 = trec_eval_metric("P.30", t3_qrels, "run.gov2.801-850.ql+rm3.txt")
    print("Topics 701-750: ql+rm3   : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 751-800: ql+rm3   : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 801-850: ql+rm3   : p30 : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
