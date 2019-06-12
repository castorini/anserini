# Anserini: Regressions for [MS MARCO (Document)](https://github.com/microsoft/TREC-2019-Deep-Learning)

This page documents regression experiments for the MS MARCO Document Ranking Task, which is integrated into Anserini's regression testing framework.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-msmarco-doc.md).

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
-generator LuceneDocumentGenerator -threads 1 -input /path/to/msmarco-doc -index \
lucene-index.msmarco-doc.pos+docvectors+rawdocs -storePositions -storeDocvectors \
-storeRawDocs >& log.msmarco-doc.pos+docvectors+rawdocs &
```

The directory `/path/to/msmarco-doc/` should be a directory containing the official document collection (a single file), in TREC format.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`.
The regression experiments here evaluate on the 5193 dev set questions; see [this page](experiments-msmarco-doc.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Tsv -index lucene-index.msmarco-doc.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt -output run.msmarco-doc.bm25.topics.msmarco-doc.dev.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Tsv -index lucene-index.msmarco-doc.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt -output run.msmarco-doc.bm25+rm3.topics.msmarco-doc.dev.txt -bm25 -rm3 &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt run.msmarco-doc.bm25.topics.msmarco-doc.dev.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt run.msmarco-doc.bm25+rm3.topics.msmarco-doc.dev.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | +RM3      |
:---------------------------------------|-----------|-----------|
[MS MARCO Document Ranking: Dev Queries](https://github.com/microsoft/TREC-2019-Deep-Learning)| 0.2310    | 0.1632    |


R@1000                                  | BM25      | +RM3      |
:---------------------------------------|-----------|-----------|
[MS MARCO Document Ranking: Dev Queries](https://github.com/microsoft/TREC-2019-Deep-Learning)| 0.8856    | 0.8785    |


