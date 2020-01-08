# Anserini: Regressions for [MS MARCO (Document)](https://github.com/microsoft/TREC-2019-Deep-Learning)

This page documents regression experiments for the MS MARCO Document Ranking Task, which is integrated into Anserini's regression testing framework.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-msmarco-doc.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-doc.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-doc.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection -input /path/to/msmarco-doc \
 -index lucene-index.msmarco-doc.pos+docvectors+rawdocs -generator LuceneDocumentGenerator -threads 1 \
 -storePositions -storeDocvectors -storeRawDocs >& log.msmarco-doc.pos+docvectors+rawdocs &
```

The directory `/path/to/msmarco-doc/` should be a directory containing the official document collection (a single file), in TREC format.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 5193 dev set questions; see [this page](experiments-msmarco-doc.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-doc.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -bm25 -output run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-doc.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -bm25 -rm3 -output run.msmarco-doc.bm25-default+rm3.topics.msmarco-doc.dev.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-doc.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -output run.msmarco-doc.bm25-default+ax.topics.msmarco-doc.dev.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-doc.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -bm25 -bm25prf -output run.msmarco-doc.bm25-default+prf.topics.msmarco-doc.dev.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-doc.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -bm25 -k1 3.44 -b 0.87 -output run.msmarco-doc.bm25-tuned.topics.msmarco-doc.dev.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-doc.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -bm25 -k1 3.44 -b 0.87 -rm3 -output run.msmarco-doc.bm25-tuned+rm3.topics.msmarco-doc.dev.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-doc.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -bm25 -k1 3.44 -b 0.87 -axiom -rerankCutoff 20 -axiom.deterministic -output run.msmarco-doc.bm25-tuned+ax.topics.msmarco-doc.dev.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-doc.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 -bm25 -k1 3.44 -b 0.87 -bm25prf -output run.msmarco-doc.bm25-tuned+prf.topics.msmarco-doc.dev.txt &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt run.msmarco-doc.bm25-default.topics.msmarco-doc.dev.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt run.msmarco-doc.bm25-default+rm3.topics.msmarco-doc.dev.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt run.msmarco-doc.bm25-default+ax.topics.msmarco-doc.dev.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt run.msmarco-doc.bm25-default+prf.topics.msmarco-doc.dev.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt run.msmarco-doc.bm25-tuned.topics.msmarco-doc.dev.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt run.msmarco-doc.bm25-tuned+rm3.topics.msmarco-doc.dev.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt run.msmarco-doc.bm25-tuned+ax.topics.msmarco-doc.dev.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt run.msmarco-doc.bm25-tuned+prf.topics.msmarco-doc.dev.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Document Ranking: Dev Queries](https://github.com/microsoft/TREC-2019-Deep-Learning)| 0.2310    | 0.1632    | 0.1147    | 0.1357    | 0.2788    | 0.2289    | 0.1895    | 0.1559    |


R@1000                                  | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Document Ranking: Dev Queries](https://github.com/microsoft/TREC-2019-Deep-Learning)| 0.8856    | 0.8785    | 0.8369    | 0.8471    | 0.9326    | 0.9320    | 0.9264    | 0.8758    |

The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`, while "tuned" refers to the tuned setting of `k1=3.44`, `b=0.87`.
See [this page](experiments-msmarco-doc.md) for more details.
Note that here we are using `trec_eval` to evaluate the top 1000 hits for each query; beware, the runs provided by MS MARCO organizers for reranking have only 100 hits per query.
