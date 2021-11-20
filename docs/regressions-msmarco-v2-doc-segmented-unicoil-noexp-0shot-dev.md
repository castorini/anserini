# Anserini: Regressions for [MS MARCO (V2) Document Ranking](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)

This page describes regression experiments for document ranking _on the segmented version_ of the MS MARCO (V2) document corpus using the dev queries, which is integrated into Anserini's regression testing framework.
Here, we cover experiments with the uniCOIL model trained on the MS MARCO V1 passage ranking test collection, applied in a zero-shot manner, with no document expansion.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-v2-doc-segmented-unicoil-noexp-0shot-dev.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-v2-doc-segmented-unicoil-noexp-0shot-dev.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonVectorCollection \
 -input /path/to/msmarco-v2-doc-segmented-unicoil-noexp-0shot-dev \
 -index indexes/lucene-index.msmarco-v2-doc-segmented-unicoil-noexp-0shot \
 -generator DefaultLuceneDocumentGenerator \
 -threads 18 -impact -pretokenized \
  >& logs/log.msmarco-v2-doc-segmented-unicoil-noexp-0shot-dev &
```

The value of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
These regression experiments use the [dev queries](../src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.txt).

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-doc-segmented-unicoil-noexp-0shot \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.unicoil-noexp.0shot.tsv.gz \
 -output runs/run.msmarco-v2-doc-segmented-unicoil-noexp-0shot-dev.unicoil-noexp-0shot.topics.msmarco-v2-doc.dev.unicoil-noexp.0shot.tsv.gz \
 -impact -pretokenized -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented-unicoil-noexp-0shot-dev.unicoil-noexp-0shot.topics.msmarco-v2-doc.dev.unicoil-noexp.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented-unicoil-noexp-0shot-dev.unicoil-noexp-0shot.topics.msmarco-v2-doc.dev.unicoil-noexp.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c -M 100 -m recip_rank -c src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented-unicoil-noexp-0shot-dev.unicoil-noexp-0shot.topics.msmarco-v2-doc.dev.unicoil-noexp.0shot.tsv.gz
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | uniCOIL (no expansion, zero-shot)|
:---------------------------------------|-----------|
[MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.2050    |


MRR@100                                 | uniCOIL (no expansion, zero-shot)|
:---------------------------------------|-----------|
[MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.2069    |


R@100                                   | uniCOIL (no expansion, zero-shot)|
:---------------------------------------|-----------|
[MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.7198    |


R@1000                                  | uniCOIL (no expansion, zero-shot)|
:---------------------------------------|-----------|
[MS MARCO V2 Doc: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.8854    |
