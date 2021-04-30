# Anserini: Regressions for [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html) w/ per-doc docTTTTTquery

This page describes document expansion experiments, integrated into Anserini's regression testing framework, for the TREC 2020 Deep Learning Track (Document Ranking Task) on the MS MARCO document collection using relevance judgments from NIST.
These experimental runs take advantage of [docTTTTTquery](http://doc2query.ai/) expansions on a _per-document_ basis.

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO document collection, refer to [this page](experiments-msmarco-doc.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl20-doc-docTTTTTquery-per-doc.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl20-doc-docTTTTTquery-per-doc.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

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

The directory `/path/to/msmarco-doc/` should be a directory containing the official document collection (a single file), in TREC format.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 45 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-docTTTTTquery-per-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.msmarco-doc-docTTTTTquery-per-doc.bm25-default.topics.dl20.txt \
 -bm25 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-docTTTTTquery-per-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.msmarco-doc-docTTTTTquery-per-doc.bm25-default+rm3.topics.dl20.txt \
 -bm25 -rm3 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-docTTTTTquery-per-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.msmarco-doc-docTTTTTquery-per-doc.bm25-tuned.topics.dl20.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 -hits 100 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-doc-docTTTTTquery-per-doc.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.dl20.txt \
 -output runs/run.msmarco-doc-docTTTTTquery-per-doc.bm25-tuned+rm3.topics.dl20.txt \
 -bm25 -bm25.k1 3.44 -bm25.b 0.87 -rm3 -hits 100 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery-per-doc.bm25-default.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery-per-doc.bm25-default+rm3.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery-per-doc.bm25-tuned.topics.dl20.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m ndcg_cut.10 -c -m recip_rank -c -m recall.100 -c src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-docTTTTTquery-per-doc.bm25-tuned+rm3.topics.dl20.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Document Ranking: Dev Queries](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.4230    | 0.4228    | 0.4150    | 0.4214    |


NDCG@10                                 | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Document Ranking: Dev Queries](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.5885    | 0.5407    | 0.5877    | 0.5705    |


RR                                      | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Document Ranking: Dev Queries](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.9369    | 0.8147    | 0.9328    | 0.8547    |


R@100                                   | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Document Ranking: Dev Queries](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.6412    | 0.6555    | 0.6231    | 0.6326    |

Note that retrieval metrics are computed to depth 100 hits per query (as opposed to 1000 hits per query for DL20 passage ranking).
Also, remember that we keep qrels of _all_ relevance grades, unlike the case for DL20 passage ranking, where relevance grade 1 needs to be discarded when computing certain metrics.

The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`, while "tuned" refers to the tuned setting of `k1=3.44`, `b=0.87` (see [this page](experiments-msmarco-doc.md) for more details about tuning).
