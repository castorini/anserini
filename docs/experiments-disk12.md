# Anserini: Experiments on Disks 1 &amp; 2

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
-generator JsoupGenerator -threads 16 -input /path/to/disk12 -index \
lucene-index.disk12.pos+docvectors -storePositions -storeDocvectors \
-storeRawDocs >& log.disk12.pos+docvectors+rawdocs &
```

The directory `/path/to/disk12/` should be the root directory of the Disk12 collection, i.e., `ls /path/to/disk12/` should bring up subdirectories like `doe`, `wsj`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.51-100.txt`: [Topics 51-100 (TREC-1 Ad Hoc Track)](http://trec.nist.gov/data/topics_eng/topics.51-100.gz)
+ `topics.101-150.txt`: [Topics 101-150 (TREC-2 Ad Hoc Track)](http://trec.nist.gov/data/topics_eng/topics.101-150.gz)
+ `topics.151-200.txt`: [Topics 151-200 (TREC-3 Ad Hoc Track)](http://trec.nist.gov/data/topics_eng/topics.151-200.gz)
+ `qrels.51-100.txt`: [qrels for Topics 51-100 (TREC-1 Ad Hoc Track)](http://trec.nist.gov/data/qrels_eng/qrels.51-100.disk1.disk2.parts1-5.tar.gz)
+ `qrels.101-150.txt`: [qrels for Topics 101-150 (TREC-2 Ad Hoc Track)](http://trec.nist.gov/data/qrels_eng/qrels.101-150.disk1.disk2.parts1-5.tar.gz)
+ `qrels.151-200.txt`: [qrels for Topics 151-200 (TREC-3 Ad Hoc Track)](http://trec.nist.gov/data/qrels_eng/qrels.151-200.201-250.disks1-3.all.tar.gz)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.bm25.topics.51-100.txt -bm25 &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.bm25.topics.101-150.txt -bm25 &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.bm25.topics.151-200.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.bm25+rm3.topics.51-100.txt -bm25 -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.bm25+rm3.topics.101-150.txt -bm25 -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.bm25+rm3.topics.151-200.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.bm25+ax.topics.51-100.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.bm25+ax.topics.101-150.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.bm25+ax.topics.151-200.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.ql.topics.51-100.txt -ql &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.ql.topics.101-150.txt -ql &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.ql.topics.151-200.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.ql+rm3.topics.51-100.txt -ql -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.ql+rm3.topics.101-150.txt -ql -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.ql+rm3.topics.151-200.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.ql+ax.topics.51-100.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.101-150.txt -output run.disk12.ql+ax.topics.101-150.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.disk12.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.151-200.txt -output run.disk12.ql+ax.topics.151-200.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.bm25.topics.51-100.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.bm25.topics.101-150.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.bm25.topics.151-200.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.bm25+rm3.topics.51-100.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.bm25+rm3.topics.101-150.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.bm25+rm3.topics.151-200.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.bm25+ax.topics.51-100.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.bm25+ax.topics.101-150.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.bm25+ax.topics.151-200.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.ql.topics.51-100.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.ql.topics.101-150.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.ql.topics.151-200.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.ql+rm3.topics.51-100.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.ql+rm3.topics.101-150.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.ql+rm3.topics.151-200.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.ql+ax.topics.51-100.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.101-150.txt run.disk12.ql+ax.topics.101-150.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.151-200.txt run.disk12.ql+ax.topics.151-200.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
TREC-1 Ad Hoc Track: Topics 51-100      | 0.2254    | 0.2612    | 0.2675    | 0.2188    | 0.2502    | 0.2519    |
TREC-2 Ad Hoc Track: Topics 101-150     | 0.2003    | 0.2582    | 0.2708    | 0.2013    | 0.2477    | 0.2606    |
TREC-3 Ad Hoc Track: Topics 151-200     | 0.2571    | 0.3214    | 0.3349    | 0.2530    | 0.3015    | 0.3113    |


P30                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
TREC-1 Ad Hoc Track: Topics 51-100      | 0.4493    | 0.4853    | 0.5167    | 0.4453    | 0.4700    | 0.4967    |
TREC-2 Ad Hoc Track: Topics 101-150     | 0.4213    | 0.4573    | 0.4787    | 0.4153    | 0.4407    | 0.4660    |
TREC-3 Ad Hoc Track: Topics 151-200     | 0.4740    | 0.5087    | 0.5160    | 0.4647    | 0.4993    | 0.5160    |


