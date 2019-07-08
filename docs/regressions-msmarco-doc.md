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
nohup target/appassembler/bin/SearchCollection -topicreader Tsv -index lucene-index.msmarco-doc.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt -output run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Tsv -index lucene-index.msmarco-doc.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt -output run.msmarco-doc.bm25-default+rm3.topics.msmarco-doc.dev.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Tsv -index lucene-index.msmarco-doc.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt -output run.msmarco-doc.bm25-tuned.topics.msmarco-doc.dev.txt -bm25 -k1 3.44 -b 0.87 &

nohup target/appassembler/bin/SearchCollection -topicreader Tsv -index lucene-index.msmarco-doc.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt -output run.msmarco-doc.bm25-tuned+rm3.topics.msmarco-doc.dev.txt -bm25 -k1 3.44 -b 0.87 -rm3 &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt run.msmarco-doc.bm25-default+rm3.topics.msmarco-doc.dev.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt run.msmarco-doc.bm25-tuned.topics.msmarco-doc.dev.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt run.msmarco-doc.bm25-tuned+rm3.topics.msmarco-doc.dev.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Document Ranking: Dev Queries](https://github.com/microsoft/TREC-2019-Deep-Learning)| 0.2310    | 0.1632    | 0.2788    | 0.2289    |


R@1000                                  | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Document Ranking: Dev Queries](https://github.com/microsoft/TREC-2019-Deep-Learning)| 0.8856    | 0.8785    | 0.9326    | 0.9320    |



The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`, while "tuned" refers to the tuned setting of `k1=3.44`, `b=0.87`.
See [this page](experiments-msmarco-doc.md) for more details.
Note that here we are using `trec_eval` to evaluate the top 1000 hits for each query; beware, the runs provided by MS MARCO organizers for reranking have only 100 hits per query.
