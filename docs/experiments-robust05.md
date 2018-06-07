# Anserini: Experiments on Robust05

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
 -input /path/to/AQUAINT/ -generator JsoupGenerator \
 -index lucene-index.aquaint.pos+docvectors+rawdocs -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -optimize >& log.aquaint.pos+docvectors+rawdocs &
```

The directory `/path/to/aquaint/` should be the root directory of AQUAINT collection; under subdirectory `disk1/` there should be `NYT/` and under subdirectory `disk2/` there should be `APW/` and `XIE/`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`.
After indexing has completed, you should be able to perform retrieval as follows:

```
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.aquaint.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust05.txt -output run.aquaint.robust05.bm25.txt -bm25 &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.aquaint.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust05.txt -output run.aquaint.robust05.bm25+rm3.txt -bm25 -rm3 &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.aquaint.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust05.txt -output run.aquaint.robust05.ql.txt -ql &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.aquaint.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust05.txt -output run.aquaint.robust05.ql+rm3.txt -ql -rm3 &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt run.aquaint.robust05.bm25.txt     | egrep "^(map|P_30)"
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt run.aquaint.robust05.bm25+rm3.txt | egrep "^(map|P_30)"
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt run.aquaint.robust05.ql.txt       | egrep "^(map|P_30)"
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt run.aquaint.robust05.ql+rm3.txt   | egrep "^(map|P_30)"
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                                                        | BM25   | BM25+RM3 | QL     | QL+RM3
:--------------------------------------------------------------------------|--------|----------|--------|--------
[TREC 2005 Robust Track Topics](http://trec.nist.gov/data/t14_robust.html) | 0.2003 | 0.2445   | 0.2022 | 0.2418


P30                                                                        | BM25   | BM25+RM3 | QL     | QL+RM3
:--------------------------------------------------------------------------|--------|----------|--------|--------
[TREC 2005 Robust Track Topics](http://trec.nist.gov/data/t14_robust.html) | 0.3667 | 0.3913   | 0.3700 | 0.3913

