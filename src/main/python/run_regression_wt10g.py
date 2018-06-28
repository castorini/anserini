import sys
import os
import argparse

from subprocess import call

index_cmd = """
nohup sh target/appassembler/bin/IndexCollection -collection WtCollection \
 -input /tuna1/collections/web/wt10g/ -generator JsoupGenerator \
 -index lucene-index.wt10g.pos+docvectors -threads 16 \
 -storePositions -storeDocvectors"""

run_cmds = [ \
    "nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.451-550.bm25.txt -bm25",
    "nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.451-550.bm25+rm3.txt -bm25 -rm3",
    "nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.451-550.ql.txt -ql",
    "nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index {} -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.451-550.ql+rm3.txt -ql -rm3"]

qrels = "src/main/resources/topics-and-qrels/qrels.451-550.txt"

def extract_value_from_doc(key, row, col):
    return float(os.popen("grep '{}' docs/experiments-wt10g.md | head -{} | tail -1".format(key, row)).read().split('|')[col].strip())

def trec_eval_metric(metric, qrels, run):
    return float(os.popen("eval/trec_eval.9.0/trec_eval -m {} {} {}".format(metric, qrels, run)).read().split("\t")[2].strip())

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Run regression tests on Wt10g.')
    parser.add_argument('--index', dest='index', action='store_true', help='rebuild index from scratch')

    args = parser.parse_args()

    # Decide if we're going to index from scratch. If not, use pre-stored index at known location.
    if args.index == True:
        call(index_cmd, shell=True)
        index_path = 'lucene-index.wt10g.pos+docvectors'
        print(args.index)
    else:
        index_path = '/tuna1/indexes/lucene-index.wt10g.pos+docvectors'

    # Use the correct index path.
    for cmd in run_cmds:
        call(cmd.format(index_path), shell=True)

    expected_map = extract_value_from_doc("Wt10g: Topics 451-550", 1, 1)
    actual_map = trec_eval_metric("map", qrels, "run.wt10g.451-550.bm25.txt")
    print("Wt10g: Topics 451-550: bm25     : map : %.4f %.4f" % (expected_map, actual_map) + ('  !' if expected_map != actual_map else ''))
    expected_map = extract_value_from_doc("Wt10g: Topics 451-550", 1, 2)
    actual_map = trec_eval_metric("map", qrels, "run.wt10g.451-550.bm25+rm3.txt")
    print("Wt10g: Topics 451-550: bm25+rm3 : map : %.4f %.4f" % (expected_map, actual_map) + ('  !' if expected_map != actual_map else ''))
    expected_map = extract_value_from_doc("Wt10g: Topics 451-550", 1, 3)
    actual_map = trec_eval_metric("map", qrels, "run.wt10g.451-550.ql.txt")
    print("Wt10g: Topics 451-550: ql       : map : %.4f %.4f" % (expected_map, actual_map) + ('  !' if expected_map != actual_map else ''))
    expected_map = extract_value_from_doc("Wt10g: Topics 451-550", 1, 4)
    actual_map = trec_eval_metric("map", qrels, "run.wt10g.451-550.ql+rm3.txt")
    print("Wt10g: Topics 451-550: ql+rm3   : map : %.4f %.4f" % (expected_map, actual_map) + ('  !' if expected_map != actual_map else ''))

    expected_p30 = extract_value_from_doc("Wt10g: Topics 451-550", 2, 1)
    actual_p30 = trec_eval_metric("P.30", qrels, "run.wt10g.451-550.bm25.txt")
    print("Wt10g: Topics 451-550: bm25     : p30 : %.4f %.4f" % (expected_p30, actual_p30) + ('  !' if expected_p30 != actual_p30 else ''))
    expected_p30 = extract_value_from_doc("Wt10g: Topics 451-550", 2, 2)
    actual_p30 = trec_eval_metric("P.30", qrels, "run.wt10g.451-550.bm25+rm3.txt")
    print("Wt10g: Topics 451-550: bm25+rm3 : p30 : %.4f %.4f" % (expected_p30, actual_p30) + ('  !' if expected_p30 != actual_p30 else ''))
    expected_p30 = extract_value_from_doc("Wt10g: Topics 451-550", 2, 3)
    actual_p30 = trec_eval_metric("P.30", qrels, "run.wt10g.451-550.ql.txt")
    print("Wt10g: Topics 451-550: ql       : p30 : %.4f %.4f" % (expected_p30, actual_p30) + ('  !' if expected_p30 != actual_p30 else ''))
    expected_p30 = extract_value_from_doc("Wt10g: Topics 451-550", 2, 4)
    actual_p30 = trec_eval_metric("P.30", qrels, "run.wt10g.451-550.ql+rm3.txt")
    print("Wt10g: Topics 451-550: ql+rm3   : p30 : %.4f %.4f" % (expected_p30, actual_p30) + ('  !' if expected_p30 != actual_p30 else ''))
