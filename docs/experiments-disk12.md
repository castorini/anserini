# Anserini: Experiments on Disk12

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
 -input /path/to/disk12/ -generator JsoupGenerator \
 -index lucene-index.disk12.pos+docvectors+rawdocs -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -optimize >& log.disk12.pos+docvectors+rawdocs &
```

The directory `/path/to/disk12/` should be the root directory of the Disk12 collection, i.e., `ls /path/to/disk12/` should bring up subdirectories like `doe`, `wsj`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`.
After indexing has completed, you should be able to perform retrieval as follows:

```
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.51-100.txt  -output run.disk12.51-100.bm25.txt  -bm25 &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.101-150.bm25.txt -bm25 &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.151-200.bm25.txt -bm25 &

nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.51-100.txt  -output run.disk12.51-100.ql.txt  -ql &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.101-150.ql.txt -ql &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.151-200.ql.txt -ql &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.51-100.bm25.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.101-150.bm25.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.151-200.bm25.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.51-100.ql.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.101-150.ql.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.151-200.ql.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                   | BM25   | QL     
:-------------------------------------|--------|--------
TREC-1 Ad Hoc Track: Topics 51-100    | 0.2249 | 0.2178
TREC-2 Ad Hoc Track: Topics 101-150   | 0.2004 | 0.2014
TREC-3 Ad Hoc Track: Topics 151-200   | 0.2572 | 0.2531


P30                                   | BM25   | QL     
:-------------------------------------|--------|--------
TREC-1 Ad Hoc Track: Topics 51-100    | 0.4493 | 0.4453
TREC-2 Ad Hoc Track: Topics 101-150   | 0.4220 | 0.4160
TREC-3 Ad Hoc Track: Topics 151-200   | 0.4747 | 0.4653
