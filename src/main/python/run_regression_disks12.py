import sys
import os
from subprocess import call

index_cmd = """
target/appassembler/bin/IndexCollection -collection TrecCollection \
 -input /tuna1/collections/newswire/disk12/ -generator JsoupGenerator \
 -index lucene-index.disk12.pos+docvectors+rawdocs -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -optimize"""

run_cmds = [ \
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.51-100.bm25.txt -bm25",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.101-150.bm25.txt -bm25",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.151-200.bm25.txt -bm25",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.51-100.bm25+rm3.txt -bm25 -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.101-150.bm25+rm3.txt -bm25 -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.151-200.bm25+rm3.txt -bm25 -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.51-100.ql.txt -ql",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.101-150.ql.txt -ql",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.151-200.ql.txt -ql",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.51-100.ql+rm3.txt -ql -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.101-150.ql+rm3.txt -ql -rm3",
    "sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.151-200.ql+rm3.txt -ql -rm3"]

if __name__ == "__main__":
    call(index_cmd, shell=True)
    for cmd in run_cmds:
        call(cmd, shell=True)

    expected_t1_map = float(os.popen("grep 'TREC-1 Ad Hoc Track: Topics 51-100' docs/experiments-disk12.md | head -1").read().split('|')[1].strip())
    expected_t2_map = float(os.popen("grep 'TREC-2 Ad Hoc Track: Topics 101-150' docs/experiments-disk12.md | head -1").read().split('|')[1].strip())
    expected_t3_map = float(os.popen("grep 'TREC-3 Ad Hoc Track: Topics 151-200' docs/experiments-disk12.md | head -1").read().split('|')[1].strip())    
    actual_t1_map = float(os.popen("eval/trec_eval.9.0/trec_eval -m map src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.51-100.bm25.txt").read().split("\t")[2].strip())
    actual_t2_map = float(os.popen("eval/trec_eval.9.0/trec_eval -m map src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.101-150.bm25.txt").read().split("\t")[2].strip())
    actual_t3_map = float(os.popen("eval/trec_eval.9.0/trec_eval -m map src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.151-200.bm25.txt").read().split("\t")[2].strip())
    print("Topics  51-100: bm25     : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 101-150: bm25     : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 151-200: bm25     : map : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))
    expected_t1_map = float(os.popen("grep 'TREC-1 Ad Hoc Track: Topics 51-100' docs/experiments-disk12.md | head -1").read().split('|')[2].strip())
    expected_t2_map = float(os.popen("grep 'TREC-2 Ad Hoc Track: Topics 101-150' docs/experiments-disk12.md | head -1").read().split('|')[2].strip())
    expected_t3_map = float(os.popen("grep 'TREC-3 Ad Hoc Track: Topics 151-200' docs/experiments-disk12.md | head -1").read().split('|')[2].strip())    
    actual_t1_map = float(os.popen("eval/trec_eval.9.0/trec_eval -m map src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.51-100.bm25+rm3.txt").read().split("\t")[2].strip())
    actual_t2_map = float(os.popen("eval/trec_eval.9.0/trec_eval -m map src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.101-150.bm25+rm3.txt").read().split("\t")[2].strip())
    actual_t3_map = float(os.popen("eval/trec_eval.9.0/trec_eval -m map src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.151-200.bm25+rm3.txt").read().split("\t")[2].strip())
    print("Topics  51-100: bm25+rm3 : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 101-150: bm25+rm3 : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 151-200: bm25+rm3 : map : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))
    expected_t1_map = float(os.popen("grep 'TREC-1 Ad Hoc Track: Topics 51-100' docs/experiments-disk12.md | head -1").read().split('|')[3].strip())
    expected_t2_map = float(os.popen("grep 'TREC-2 Ad Hoc Track: Topics 101-150' docs/experiments-disk12.md | head -1").read().split('|')[3].strip())
    expected_t3_map = float(os.popen("grep 'TREC-3 Ad Hoc Track: Topics 151-200' docs/experiments-disk12.md | head -1").read().split('|')[3].strip())    
    actual_t1_map = float(os.popen("eval/trec_eval.9.0/trec_eval -m map src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.51-100.ql.txt").read().split("\t")[2].strip())
    actual_t2_map = float(os.popen("eval/trec_eval.9.0/trec_eval -m map src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.101-150.ql.txt").read().split("\t")[2].strip())
    actual_t3_map = float(os.popen("eval/trec_eval.9.0/trec_eval -m map src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.151-200.ql.txt").read().split("\t")[2].strip())
    print("Topics  51-100: ql       : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 101-150: ql       : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 151-200: ql       : map : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))
    expected_t1_map = float(os.popen("grep 'TREC-1 Ad Hoc Track: Topics 51-100' docs/experiments-disk12.md | head -1").read().split('|')[4].strip())
    expected_t2_map = float(os.popen("grep 'TREC-2 Ad Hoc Track: Topics 101-150' docs/experiments-disk12.md | head -1").read().split('|')[4].strip())
    expected_t3_map = float(os.popen("grep 'TREC-3 Ad Hoc Track: Topics 151-200' docs/experiments-disk12.md | head -1").read().split('|')[4].strip())    
    actual_t1_map = float(os.popen("eval/trec_eval.9.0/trec_eval -m map src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.51-100.ql+rm3.txt").read().split("\t")[2].strip())
    actual_t2_map = float(os.popen("eval/trec_eval.9.0/trec_eval -m map src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.101-150.ql+rm3.txt").read().split("\t")[2].strip())
    actual_t3_map = float(os.popen("eval/trec_eval.9.0/trec_eval -m map src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.151-200.ql+rm3.txt").read().split("\t")[2].strip())
    print("Topics  51-100: ql+rm3   : map : %.4f %.4f" % (expected_t1_map, actual_t1_map) + ('  !' if expected_t1_map != actual_t1_map else ''))
    print("Topics 101-150: ql+rm3   : map : %.4f %.4f" % (expected_t2_map, actual_t2_map) + ('  !' if expected_t2_map != actual_t2_map else ''))
    print("Topics 151-200: ql+rm3   : map : %.4f %.4f" % (expected_t3_map, actual_t3_map) + ('  !' if expected_t3_map != actual_t3_map else ''))

    expected_t1_p30 = float(os.popen("grep 'TREC-1 Ad Hoc Track: Topics 51-100' docs/experiments-disk12.md | tail -1").read().split('|')[1].strip())
    expected_t2_p30 = float(os.popen("grep 'TREC-2 Ad Hoc Track: Topics 101-150' docs/experiments-disk12.md | tail -1").read().split('|')[1].strip())
    expected_t3_p30 = float(os.popen("grep 'TREC-3 Ad Hoc Track: Topics 151-200' docs/experiments-disk12.md | tail -1").read().split('|')[1].strip())    
    actual_t1_p30 = float(os.popen("eval/trec_eval.9.0/trec_eval -m P.30 src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.51-100.bm25.txt").read().split("\t")[2].strip())
    actual_t2_p30 = float(os.popen("eval/trec_eval.9.0/trec_eval -m P.30 src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.101-150.bm25.txt").read().split("\t")[2].strip())
    actual_t3_p30 = float(os.popen("eval/trec_eval.9.0/trec_eval -m P.30 src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.151-200.bm25.txt").read().split("\t")[2].strip())
    print("Topics  51-100: bm25     : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 101-150: bm25     : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 151-200: bm25     : p30 : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
    expected_t1_p30 = float(os.popen("grep 'TREC-1 Ad Hoc Track: Topics 51-100' docs/experiments-disk12.md | tail -1").read().split('|')[2].strip())
    expected_t2_p30 = float(os.popen("grep 'TREC-2 Ad Hoc Track: Topics 101-150' docs/experiments-disk12.md | tail -1").read().split('|')[2].strip())
    expected_t3_p30 = float(os.popen("grep 'TREC-3 Ad Hoc Track: Topics 151-200' docs/experiments-disk12.md | tail -1").read().split('|')[2].strip())    
    actual_t1_p30 = float(os.popen("eval/trec_eval.9.0/trec_eval -m P.30 src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.51-100.bm25+rm3.txt").read().split("\t")[2].strip())
    actual_t2_p30 = float(os.popen("eval/trec_eval.9.0/trec_eval -m P.30 src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.101-150.bm25+rm3.txt").read().split("\t")[2].strip())
    actual_t3_p30 = float(os.popen("eval/trec_eval.9.0/trec_eval -m P.30 src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.151-200.bm25+rm3.txt").read().split("\t")[2].strip())
    print("Topics  51-100: bm25+rm3 : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 101-150: bm25+rm3 : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 151-200: bm25+rm3 : p30 : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
    expected_t1_p30 = float(os.popen("grep 'TREC-1 Ad Hoc Track: Topics 51-100' docs/experiments-disk12.md | tail -1").read().split('|')[3].strip())
    expected_t2_p30 = float(os.popen("grep 'TREC-2 Ad Hoc Track: Topics 101-150' docs/experiments-disk12.md | tail -1").read().split('|')[3].strip())
    expected_t3_p30 = float(os.popen("grep 'TREC-3 Ad Hoc Track: Topics 151-200' docs/experiments-disk12.md | tail -1").read().split('|')[3].strip())    
    actual_t1_p30 = float(os.popen("eval/trec_eval.9.0/trec_eval -m P.30 src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.51-100.ql.txt").read().split("\t")[2].strip())
    actual_t2_p30 = float(os.popen("eval/trec_eval.9.0/trec_eval -m P.30 src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.101-150.ql.txt").read().split("\t")[2].strip())
    actual_t3_p30 = float(os.popen("eval/trec_eval.9.0/trec_eval -m P.30 src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.151-200.ql.txt").read().split("\t")[2].strip())
    print("Topics  51-100: ql       : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 101-150: ql       : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 151-200: ql       : p30 : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
    expected_t1_p30 = float(os.popen("grep 'TREC-1 Ad Hoc Track: Topics 51-100' docs/experiments-disk12.md | tail -1").read().split('|')[4].strip())
    expected_t2_p30 = float(os.popen("grep 'TREC-2 Ad Hoc Track: Topics 101-150' docs/experiments-disk12.md | tail -1").read().split('|')[4].strip())
    expected_t3_p30 = float(os.popen("grep 'TREC-3 Ad Hoc Track: Topics 151-200' docs/experiments-disk12.md | tail -1").read().split('|')[4].strip())    
    actual_t1_p30 = float(os.popen("eval/trec_eval.9.0/trec_eval -m P.30 src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.51-100.ql+rm3.txt").read().split("\t")[2].strip())
    actual_t2_p30 = float(os.popen("eval/trec_eval.9.0/trec_eval -m P.30 src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.101-150.ql+rm3.txt").read().split("\t")[2].strip())
    actual_t3_p30 = float(os.popen("eval/trec_eval.9.0/trec_eval -m P.30 src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.151-200.ql+rm3.txt").read().split("\t")[2].strip())
    print("Topics  51-100: ql+rm3   : p30 : %.4f %.4f" % (expected_t1_p30, actual_t1_p30) + ('  !' if expected_t1_p30 != actual_t1_p30 else ''))
    print("Topics 101-150: ql+rm3   : p30 : %.4f %.4f" % (expected_t2_p30, actual_t2_p30) + ('  !' if expected_t2_p30 != actual_t2_p30 else ''))
    print("Topics 151-200: ql+rm3   : p30 : %.4f %.4f" % (expected_t3_p30, actual_t3_p30) + ('  !' if expected_t3_p30 != actual_t3_p30 else ''))
