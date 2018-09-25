# Anserini: Experiments on [Gov2](http://ir.dcs.gla.ac.uk/test_collections/gov2-summary.htm)

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecwebCollection \
-generator JsoupGenerator -threads 44 -input /path/to/gov2 -index \
lucene-index.gov2.pos+docvectors -storePositions -storeDocvectors -storeRawDocs \
>& log.gov2.pos+docvectors+rawdocs &
```

The directory `/path/to/gov2/` should be the root directory of Gov2 collection, i.e., `ls /path/to/gov2/` should bring up a bunch of subdirectories, `GX000` to `GX272`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.701-750.txt`: [Topics 701-750 (TREC 2004 Terabyte Track)](http://trec.nist.gov/data/terabyte/04/04topics.701-750.txt)
+ `topics.751-800.txt`: [Topics 751-800 (TREC 2005 Terabyte Track)](http://trec.nist.gov/data/terabyte/05/05.topics.751-800.txt)
+ `topics.801-850.txt`: [Topics 801-850 (TREC 2006 Terabyte Track)](http://trec.nist.gov/data/terabyte/06/06.topics.801-850.txt)
+ `qrels.701-750.txt`: [qrels for Topics 701-750 (TREC 2004 Terabyte Track)](http://trec.nist.gov/data/terabyte/04/04.qrels.12-Nov-04)
+ `qrels.751-800.txt`: [qrels for Topics 751-800 (TREC 2005 Terabyte Track)](http://trec.nist.gov/data/terabyte/05/05.adhoc_qrels)
+ `qrels.801-850.txt`: [qrels for Topics 801-850 (TREC 2006 Terabyte Track)](http://trec.nist.gov/data/terabyte/06/qrels.tb06.top50)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.bm25.topics.701-750.txt -bm25 &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.751-800.txt -output run.gov2.bm25.topics.751-800.txt -bm25 &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.801-850.txt -output run.gov2.bm25.topics.801-850.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.bm25+rm3.topics.701-750.txt -bm25 -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.751-800.txt -output run.gov2.bm25+rm3.topics.751-800.txt -bm25 -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.801-850.txt -output run.gov2.bm25+rm3.topics.801-850.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.bm25+ax.topics.701-750.txt -bm25 -axiom -axiom.beta 0.1 -rerankCutoff 20 -axiom.deterministic &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.751-800.txt -output run.gov2.bm25+ax.topics.751-800.txt -bm25 -axiom -axiom.beta 0.1 -rerankCutoff 20 -axiom.deterministic &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.801-850.txt -output run.gov2.bm25+ax.topics.801-850.txt -bm25 -axiom -axiom.beta 0.1 -rerankCutoff 20 -axiom.deterministic &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.ql.topics.701-750.txt -ql &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.751-800.txt -output run.gov2.ql.topics.751-800.txt -ql &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.801-850.txt -output run.gov2.ql.topics.801-850.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.ql+rm3.topics.701-750.txt -ql -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.751-800.txt -output run.gov2.ql+rm3.topics.751-800.txt -ql -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.801-850.txt -output run.gov2.ql+rm3.topics.801-850.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.ql+ax.topics.701-750.txt -ql -axiom -rerankCutoff 20 -axiom.beta 0.1 -axiom.deterministic &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.751-800.txt -output run.gov2.ql+ax.topics.751-800.txt -ql -axiom -rerankCutoff 20 -axiom.beta 0.1 -axiom.deterministic &
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.gov2.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.801-850.txt -output run.gov2.ql+ax.topics.801-850.txt -ql -axiom -rerankCutoff 20 -axiom.beta 0.1 -axiom.deterministic &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.701-750.txt run.gov2.bm25.topics.701-750.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.751-800.txt run.gov2.bm25.topics.751-800.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.801-850.txt run.gov2.bm25.topics.801-850.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.701-750.txt run.gov2.bm25+rm3.topics.701-750.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.751-800.txt run.gov2.bm25+rm3.topics.751-800.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.801-850.txt run.gov2.bm25+rm3.topics.801-850.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.701-750.txt run.gov2.bm25+ax.topics.701-750.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.751-800.txt run.gov2.bm25+ax.topics.751-800.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.801-850.txt run.gov2.bm25+ax.topics.801-850.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.701-750.txt run.gov2.ql.topics.701-750.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.751-800.txt run.gov2.ql.topics.751-800.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.801-850.txt run.gov2.ql.topics.801-850.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.701-750.txt run.gov2.ql+rm3.topics.701-750.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.751-800.txt run.gov2.ql+rm3.topics.751-800.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.801-850.txt run.gov2.ql+rm3.topics.801-850.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.701-750.txt run.gov2.ql+ax.topics.701-750.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.751-800.txt run.gov2.ql+ax.topics.751-800.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.801-850.txt run.gov2.ql+ax.topics.801-850.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2004 Terabyte Track: Topics 701-750](http://trec.nist.gov/data/terabyte04.html)| 0.2689    | 0.2951    | 0.2665    | 0.2681    | 0.2809    | 0.2666    |
[TREC 2005 Terabyte Track: Topics 751-800](http://trec.nist.gov/data/terabyte05.html)| 0.3390    | 0.3810    | 0.3664    | 0.3303    | 0.3634    | 0.3646    |
[TREC 2006 Terabyte Track: Topics 801-850](http://trec.nist.gov/data/terabyte06.html)| 0.3080    | 0.3356    | 0.3069    | 0.2996    | 0.3166    | 0.3084    |


P30                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2004 Terabyte Track: Topics 701-750](http://trec.nist.gov/data/terabyte04.html)| 0.4864    | 0.5279    | 0.4986    | 0.4755    | 0.4932    | 0.4932    |
[TREC 2005 Terabyte Track: Topics 751-800](http://trec.nist.gov/data/terabyte05.html)| 0.5540    | 0.5920    | 0.5933    | 0.5347    | 0.5693    | 0.5840    |
[TREC 2006 Terabyte Track: Topics 801-850](http://trec.nist.gov/data/terabyte06.html)| 0.4907    | 0.5167    | 0.5033    | 0.4720    | 0.4753    | 0.4920    |


