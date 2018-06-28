import sys
import os
import argparse

from subprocess import call

index_cmd = """
nohup sh target/appassembler/bin/IndexCollection -collection TweetCollection \
 -input /tuna1/collections/twitter/Tweets2011-corpus/json.gold/ -generator TweetGenerator \
 -index lucene-index.tweets2011.pos+docvectors+rawdocs -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -optimize -uniqueDocid -tweet.keepUrls -tweet.stemming"""

run_cmds = [ \
    "sh target/appassembler/bin/SearchCollection -searchtweets -topicreader Microblog -index {} -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt -output run.mb11.bm25.txt -bm25 -hits 1000",
    "sh target/appassembler/bin/SearchCollection -searchtweets -topicreader Microblog -index {} -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt -output run.mb12.bm25.txt -bm25 -hits 1000",
    "sh target/appassembler/bin/SearchCollection -searchtweets -topicreader Microblog -index {} -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt -output run.mb11.bm25+rm3.txt -bm25 -rm3 -hits 1000",
    "sh target/appassembler/bin/SearchCollection -searchtweets -topicreader Microblog -index {} -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt -output run.mb12.bm25+rm3.txt -bm25 -rm3 -hits 1000",
    "sh target/appassembler/bin/SearchCollection -searchtweets -topicreader Microblog -index {} -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt -output run.mb11.ql.txt -ql -hits 1000",
    "sh target/appassembler/bin/SearchCollection -searchtweets -topicreader Microblog -index {} -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt -output run.mb12.ql.txt -ql -hits 1000",
    "sh target/appassembler/bin/SearchCollection -searchtweets -topicreader Microblog -index {} -topics src/main/resources/topics-and-qrels/topics.microblog2011.txt -output run.mb11.ql+rm3.txt -ql -rm3 -hits 1000",
    "sh target/appassembler/bin/SearchCollection -searchtweets -topicreader Microblog -index {} -topics src/main/resources/topics-and-qrels/topics.microblog2012.txt -output run.mb12.ql+rm3.txt -ql -rm3 -hits 1000"
]

t1_qrels = "src/main/resources/topics-and-qrels/qrels.microblog2011.txt"
t2_qrels = "src/main/resources/topics-and-qrels/qrels.microblog2012.txt"

def extract_value_from_doc(key, row, col):
    return float(os.popen("grep '{}' docs/experiments-microblog.md | head -{} | tail -1".format(key, row)).read().split('|')[col].strip())

def trec_eval_metric(metric, qrels, run):
    return float(os.popen("eval/trec_eval.9.0/trec_eval -m {} {} {}".format(metric, qrels, run)).read().split("\t")[2].strip())

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Run regression tests on Tweets2011.')
    parser.add_argument('--index', dest='index', action='store_true', help='rebuild index from scratch')

    args = parser.parse_args()

    # Decide if we're going to index from scratch. If not, use pre-stored index at known location.
    if args.index == True:
        call(index_cmd, shell=True)
        index_path = 'lucene-index.tweets2011.pos+docvectors+rawdocs'
        print(args.index)
    else:
        index_path = '/tuna1/indexes/lucene-index.tweets2011.pos+docvectors+rawdocs'

    # Use the correct index path.
    for cmd in run_cmds:
        call(cmd.format(index_path), shell=True)

    expected_t1_map = extract_value_from_doc("TREC 2011 Microblog Track", 1, 1)
    expected_t2_map = extract_value_from_doc("TREC 2012 Microblog Track", 1, 1)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.mb11.bm25.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.mb12.bm25.txt")
    print("TREC 2011 Microblog Track: bm25     : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("TREC 2012 Microblog Track: bm25     : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    expected_t1_map = extract_value_from_doc("TREC 2011 Microblog Track", 1, 2)
    expected_t2_map = extract_value_from_doc("TREC 2012 Microblog Track", 1, 2)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.mb11.bm25+rm3.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.mb12.bm25+rm3.txt")
    print("TREC 2011 Microblog Track: bm25+rm3 : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("TREC 2012 Microblog Track: bm25+rm3 : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    expected_t1_map = extract_value_from_doc("TREC 2011 Microblog Track", 1, 3)
    expected_t2_map = extract_value_from_doc("TREC 2012 Microblog Track", 1, 3)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.mb11.ql.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.mb12.ql.txt")
    print("TREC 2011 Microblog Track: ql       : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("TREC 2012 Microblog Track: ql       : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    expected_t1_map = extract_value_from_doc("TREC 2011 Microblog Track", 1, 4)
    expected_t2_map = extract_value_from_doc("TREC 2012 Microblog Track", 1, 4)
    actual_t1_map = trec_eval_metric("map", t1_qrels, "run.mb11.ql+rm3.txt")
    actual_t2_map = trec_eval_metric("map", t2_qrels, "run.mb12.ql+rm3.txt")
    print("TREC 2011 Microblog Track: ql+rm3   : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("TREC 2012 Microblog Track: ql+rm3   : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))

    expected_t1_p30 = extract_value_from_doc("TREC 2011 Microblog Track", 2, 1)
    expected_t2_p30 = extract_value_from_doc("TREC 2012 Microblog Track", 2, 1)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.mb11.bm25.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.mb12.bm25.txt")
    print("TREC 2011 Microblog Track: bm25     : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("TREC 2012 Microblog Track: bm25     : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC 2011 Microblog Track", 2, 2)
    expected_t2_p30 = extract_value_from_doc("TREC 2012 Microblog Track", 2, 2)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.mb11.bm25+rm3.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.mb12.bm25+rm3.txt")
    print("TREC 2011 Microblog Track: bm25+rm3 : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("TREC 2012 Microblog Track: bm25+rm3 : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC 2011 Microblog Track", 2, 3)
    expected_t2_p30 = extract_value_from_doc("TREC 2012 Microblog Track", 2, 3)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.mb11.ql.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.mb12.ql.txt")
    print("TREC 2011 Microblog Track: ql       : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("TREC 2012 Microblog Track: ql       : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    expected_t1_p30 = extract_value_from_doc("TREC 2011 Microblog Track", 2, 4)
    expected_t2_p30 = extract_value_from_doc("TREC 2012 Microblog Track", 2, 4)
    actual_t1_p30 = trec_eval_metric("P.30", t1_qrels, "run.mb11.ql+rm3.txt")
    actual_t2_p30 = trec_eval_metric("P.30", t2_qrels, "run.mb12.ql+rm3.txt")
    print("TREC 2011 Microblog Track: ql+rm3   : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("TREC 2012 Microblog Track: ql+rm3   : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
