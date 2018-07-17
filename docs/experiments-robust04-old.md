# Anserini: Robust04 Experiments on Disks 4 &amp; 5

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
 -input /path/to/disk45/ -generator JsoupGenerator \
 -index lucene-index.robust04.pos+docvectors+rawdocs -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -optimize \
 >& log.robust04.pos+docvectors+rawdocs &
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
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust04.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt -output run.disk45.301-450.601-700.bm25.txt -bm25 &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust04.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt -output run.disk45.301-450.601-700.bm25+rm3.txt -bm25 -rm3 &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust04.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt -output run.disk45.301-450.601-700.ql.txt -ql &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust04.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt -output run.disk45.301-450.601-700.ql+rm3.txt -ql -rm3 &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2004.txt run.disk45.301-450.601-700.bm25.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2004.txt run.disk45.301-450.601-700.bm25+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2004.txt run.disk45.301-450.601-700.ql.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2004.txt run.disk45.301-450.601-700.ql+rm3.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP        | BM25   | BM25+RM3 | QL     | QL+RM3 |
:----------|--------|----------|--------|--------|
All Topics | 0.2501 | 0.2757   | 0.2468 | 0.2645 |


P30        | BM25   | BM25+RM3 | QL     | QL+RM3 |
:----------|--------|----------|--------|--------|
All Topics | 0.3123 | 0.3256   | 0.3083 | 0.3153 |
