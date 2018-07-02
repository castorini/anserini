import sys
import os
import argparse

from subprocess import call

index_cmd = """
target/appassembler/bin/IndexCollection -collection TrecCollection \
 -input /tuna1/collections/newswire/disk12/ -generator JsoupGenerator \
 -index lucene-index.disk12.pos+docvectors+rawdocs -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -optimize"""

run_cmds = [ \
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.51-100.bm25.txt -bm25",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.101-150.bm25.txt -bm25",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.151-200.bm25.txt -bm25",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.51-100.bm25+rm3.txt -bm25 -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.101-150.bm25+rm3.txt -bm25 -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.151-200.bm25+rm3.txt -bm25 -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.51-100.ql.txt -ql",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.101-150.ql.txt -ql",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.151-200.ql.txt -ql",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.51-100.ql+rm3.txt -ql -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.101-150.ql+rm3.txt -ql -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.151-200.ql+rm3.txt -ql -rm3"]

t1_qrels = "src/main/resources/topics-and-qrels/qrels.51-100.txt"
t2_qrels = "src/main/resources/topics-and-qrels/qrels.101-150.txt"
t3_qrels = "src/main/resources/topics-and-qrels/qrels.151-200.txt"

def extract_value_from_doc(key, row, col):
    return float(os.popen("grep '{}' docs/experiments-disk12.md | head -{} | tail -1".format(key, row)).read().split('|')[col].strip())

def trec_eval_metric(metric, qrels, run):
    return float(os.popen("eval/trec_eval.9.0/trec_eval -m {} {} {}".format(metric, qrels, run)).read().split("\t")[2].strip())

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Run regression tests on Disks 1 and 2.')
    parser.add_argument('--index', dest='index', action='store_true', help='rebuild index from scratch')

    args = parser.parse_args()

    # Decide if we're going to index from scratch. If not, use pre-stored index at known location.
    if args.index == True:
        call(index_cmd, shell=True)
        index_path = 'lucene-index.disk12.pos+docvectors+rawdocs'
        print(args.index)
    else:
        index_path = '/tuna1/indexes/lucene-index.disk12.pos+docvectors+rawdocs'

    # Use the correct index path.
    for cmd in run_cmds:
        call(cmd.format(index_path), shell=True)

    expected_t1_map = extract_value_from_doc("TREC-1 Ad Hoc Track: Topics 51-100", 1, 1)
    expected_t2_map = extract_value_from_doc("TREC-2 Ad Hoc Track: Topics 101-150", 1, 1)
    expected_t3_map = extract_value_from_doc("TREC-3 Ad Hoc Track: Topics 151-200", 1, 1)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.disk12.51-100.bm25.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.disk12.101-150.bm25.txt")
    actual_t3_map = trec_eval_metric("map", t3_qrels, "run.disk12.151-200.bm25.txt")
    print("Topics  51-100: bm25     : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 101-150: bm25     : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 151-200: bm25     : map : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))
    expected_t1_map = extract_value_from_doc("TREC-1 Ad Hoc Track: Topics 51-100", 1, 2)
    expected_t2_map = extract_value_from_doc("TREC-2 Ad Hoc Track: Topics 101-150", 1, 2)
    expected_t3_map = extract_value_from_doc("TREC-3 Ad Hoc Track: Topics 151-200", 1, 2)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.disk12.51-100.bm25+rm3.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.disk12.101-150.bm25+rm3.txt")
    actual_t3_map = trec_eval_metric("map", t3_qrels, "run.disk12.151-200.bm25+rm3.txt")
    print("Topics  51-100: bm25+rm3 : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 101-150: bm25+rm3 : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 151-200: bm25+rm3 : map : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))
    expected_t1_map = extract_value_from_doc("TREC-1 Ad Hoc Track: Topics 51-100", 1, 3)
    expected_t2_map = extract_value_from_doc("TREC-2 Ad Hoc Track: Topics 101-150", 1, 3)
    expected_t3_map = extract_value_from_doc("TREC-3 Ad Hoc Track: Topics 151-200", 1, 3)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.disk12.51-100.ql.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.disk12.101-150.ql.txt")
    actual_t3_map = trec_eval_metric("map", t3_qrels, "run.disk12.151-200.ql.txt")
    print("Topics  51-100: ql       : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 101-150: ql       : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 151-200: ql       : map : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))
    expected_t1_map = extract_value_from_doc("TREC-1 Ad Hoc Track: Topics 51-100", 1, 4)
    expected_t2_map = extract_value_from_doc("TREC-2 Ad Hoc Track: Topics 101-150", 1, 4)
    expected_t3_map = extract_value_from_doc("TREC-3 Ad Hoc Track: Topics 151-200", 1, 4)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.disk12.51-100.ql+rm3.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.disk12.101-150.ql+rm3.txt")
    actual_t3_map = trec_eval_metric("map", t3_qrels, "run.disk12.151-200.ql+rm3.txt")
    print("Topics  51-100: ql+rm3   : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 101-150: ql+rm3   : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 151-200: ql+rm3   : map : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))

    expected_t1_p30 = extract_value_from_doc("TREC-1 Ad Hoc Track: Topics 51-100", 2, 1)
    expected_t2_p30 = extract_value_from_doc("TREC-2 Ad Hoc Track: Topics 101-150", 2, 1)
    expected_t3_p30 = extract_value_from_doc("TREC-3 Ad Hoc Track: Topics 151-200", 2, 1)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.disk12.51-100.bm25.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.disk12.101-150.bm25.txt")
    actual_t3_p30 = trec_eval_metric("P.30", t3_qrels, "run.disk12.151-200.bm25.txt")
    print("Topics  51-100: bm25     : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 101-150: bm25     : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 151-200: bm25     : p30 : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC-1 Ad Hoc Track: Topics 51-100", 2, 2)
    expected_t2_p30 = extract_value_from_doc("TREC-2 Ad Hoc Track: Topics 101-150", 2, 2)
    expected_t3_p30 = extract_value_from_doc("TREC-3 Ad Hoc Track: Topics 151-200", 2, 2)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.disk12.51-100.bm25+rm3.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.disk12.101-150.bm25+rm3.txt")
    actual_t3_p30 = trec_eval_metric("P.30", t3_qrels, "run.disk12.151-200.bm25+rm3.txt")
    print("Topics  51-100: bm25+rm3 : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 101-150: bm25+rm3 : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 151-200: bm25+rm3 : p30 : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC-1 Ad Hoc Track: Topics 51-100", 2, 3)
    expected_t2_p30 = extract_value_from_doc("TREC-2 Ad Hoc Track: Topics 101-150", 2, 3)
    expected_t3_p30 = extract_value_from_doc("TREC-3 Ad Hoc Track: Topics 151-200", 2, 3)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.disk12.51-100.ql.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.disk12.101-150.ql.txt")
    actual_t3_p30 = trec_eval_metric("P.30", t3_qrels, "run.disk12.151-200.ql.txt")
    print("Topics  51-100: ql       : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 101-150: ql       : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 151-200: ql       : p30 : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC-1 Ad Hoc Track: Topics 51-100", 2, 4)
    expected_t2_p30 = extract_value_from_doc("TREC-2 Ad Hoc Track: Topics 101-150", 2, 4)
    expected_t3_p30 = extract_value_from_doc("TREC-3 Ad Hoc Track: Topics 151-200", 2, 4)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.disk12.51-100.ql+rm3.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.disk12.101-150.ql+rm3.txt")
    actual_t3_p30 = trec_eval_metric("P.30", t3_qrels, "run.disk12.151-200.ql+rm3.txt")
    print("Topics  51-100: ql+rm3   : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 101-150: ql+rm3   : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 151-200: ql+rm3   : p30 : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
