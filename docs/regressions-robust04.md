# Anserini: Regressions for [Disks 4 &amp; 5](https://trec.nist.gov/data_disks.html) (Robust04)

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
-generator JsoupGenerator -threads 16 -input /path/to/robust04 -index \
lucene-index.robust04.pos+docvectors+rawdocs -storePositions -storeDocvectors \
-storeRawDocs >& log.robust04.pos+docvectors+rawdocs &
```

The directory `/path/to/disk45/` should be the root directory of Disk4 and Disk5 collection; inside each there should be subdirectories like `ft`, `fr94`.
Note that Anserini ignores the `cr` folder when indexing, which is the standard configuration.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.robust04.301-450.601-700.txt`: [Topics 301-450 &amp; 601-700 (TREC 2004 Robust Track)](http://trec.nist.gov/data/robust/04.testset.gz)
+ `qrels.robust2004.txt`: [qrels for Topics 301-450 &amp; 601-700 (TREC 2004 Robust Track)](http://trec.nist.gov/data/robust/qrels.robust2004.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust04.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt -output run.robust04.bm25.topics.robust04.301-450.601-700.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust04.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt -output run.robust04.bm25+rm3.topics.robust04.301-450.601-700.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust04.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt -output run.robust04.bm25+ax.topics.robust04.301-450.601-700.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust04.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt -output run.robust04.ql.topics.robust04.301-450.601-700.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust04.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt -output run.robust04.ql+rm3.topics.robust04.301-450.601-700.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust04.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt -output run.robust04.ql+ax.topics.robust04.301-450.601-700.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2004.txt run.robust04.bm25.topics.robust04.301-450.601-700.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2004.txt run.robust04.bm25+rm3.topics.robust04.301-450.601-700.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2004.txt run.robust04.bm25+ax.topics.robust04.301-450.601-700.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2004.txt run.robust04.ql.topics.robust04.301-450.601-700.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2004.txt run.robust04.ql+rm3.topics.robust04.301-450.601-700.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2004.txt run.robust04.ql+ax.topics.robust04.301-450.601-700.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2004 Robust Track Topics](http://trec.nist.gov/data/robust/04.testset.gz)| 0.2531    | 0.2903    | 0.2896    | 0.2467    | 0.2747    | 0.2774    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2004 Robust Track Topics](http://trec.nist.gov/data/robust/04.testset.gz)| 0.3102    | 0.3365    | 0.3333    | 0.3079    | 0.3232    | 0.3229    |



## Replication Log

+ Results replicated by [@chriskamphuis](https://github.com/chriskamphuis) on 2018-12-18 (commit [`a15235`](https://github.com/castorini/Anserini/commit/a152359435ac6ae694b39f561343bba5eed8fdc9))
