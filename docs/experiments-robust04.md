# Anserini: Experiments on Robust04

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
 -input /path/to/disk45/ -generator JsoupGenerator \
 -index lucene-index.robust04.pos+docvectors+rawdocs -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -optimize >& log.robust04.pos+docvectors+rawdocs &
```

The directory `/path/to/disk45/` should be the root directory of Disk4 and Disk5 collection; inside each there should be subdirectories like `ft`, `fr94`.
Note that Anserini ignores the `cr` folder when indexing, which is the standard configuration.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`.
After indexing has completed, you should be able to perform retrieval as follows:

```
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust04.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt -output run.disk45.301-450.601-700.bm25.txt -bm25 &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust04.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt -output run.disk45.301-450.601-700.ql.txt -ql &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt run.disk45.301-450.601-700.bm25.txt     | egrep "^(map|P_30)"
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt run.disk45.301-450.601-700.ql.txt       | egrep "^(map|P_30)"
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP        | BM25   | QL     
:----------|--------|--------
All Topics | 0.2500 | 0.2465 


P30        | BM25   | QL     
:----------|--------|--------
All Topics | 0.3120 | 0.3078 
