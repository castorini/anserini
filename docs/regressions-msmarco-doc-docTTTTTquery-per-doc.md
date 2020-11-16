# Anserini: Regressions for MS MARCO Document Ranking

This page documents regression experiments for the [MS MARCO document ranking task](https://github.com/microsoft/MSMARCO-Document-Ranking) with per-document docTTTTTquery document expansion, which is integrated into Anserini's regression testing framework.
For more complete instructions on how to run end-to-end experiments, refer to [this page](https://github.com/castorini/docTTTTTquery#Replicating-MS-MARCO-Document-Ranking-Results-with-Anserini).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-doc-docTTTTTquery-per-doc.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-doc-docTTTTTquery-per-doc.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection \
 -input /path/to/msmarco-doc-docTTTTTquery-per-doc \
 -index indexes/lucene-index.msmarco-doc-docTTTTTquery-per-doc.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 1 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-doc-docTTTTTquery-per-doc &
```

The directory `/path/to/msmarco-doc-docTTTTTquery-per-doc/` should be a directory containing the expanded document collection; see [this link](https://github.com/castorini/docTTTTTquery#replicating-ms-marco-document-ranking-results-with-anserini) for how to prepare this collection.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 5193 dev set questions.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-docTTTTTquery-per-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -output runs/run.msmarco-doc-docTTTTTquery-per-doc.bm25-default.topics.msmarco-doc.dev.txt \
 -bm25 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-docTTTTTquery-per-doc.bm25-default.topics.msmarco-doc.dev.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25 (Default)|
:---------------------------------------|-----------|
[MS MARCO Document Ranking: Dev Queries](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.2886    |


R@1000                                  | BM25 (Default)|
:---------------------------------------|-----------|
[MS MARCO Document Ranking: Dev Queries](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.9259    |

See [this page](https://github.com/castorini/docTTTTTquery#Replicating-MS-MARCO-Document-Ranking-Results-with-Anserini) for more details.
Note that here we are using `trec_eval` to evaluate the top 1000 hits for each query; beware, the runs provided by MS MARCO organizers for reranking have only 100 hits per query.
