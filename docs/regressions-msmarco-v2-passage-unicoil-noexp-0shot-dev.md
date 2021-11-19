# Anserini: Regressions for [MS MARCO (V2) Passage Ranking](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)

This page documents regression experiments for passage ranking on the MS MARCO (V2) passage corpus using the dev queries, which is integrated into Anserini's regression testing framework.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-msmarco-v2.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-v2-passage-dev.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-v2-passage-dev.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonVectorCollection \
 -input /path/to/msmarco-v2-passage-unicoil-noexp-0shot \
 -index indexes/lucene-index.msmarco-v2-passage-unicoil-noexp-0shot \
 -generator DefaultLuceneDocumentGenerator \
 -threads 18 -impact -pretokenized \
  >& logs/log.msmarco-v2-passage-unicoil-noexp-0shot &
```

The directory `/path/to/msmarco-v2-passage/` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
These regression experiments use the [dev queries](../src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt).

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-passage-unicoil-noexp-0shot \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.unicoil-noexp.0shot.tsv.gz \
 -output runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.msmarco-v2-doc.dev.unicoil-noexp.0shot.tsv.gz \
 -impact -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m recall.100 -c src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.msmarco-v2-doc.dev.unicoil-noexp.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.msmarco-v2-doc.dev.unicoil-noexp.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -M 100 -m map -c -M 100 -m recip_rank -c src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.msmarco-v2-doc.dev.unicoil-noexp.0shot.tsv.gz
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | uniCOIL (no expansion, zero-shot)|
:---------------------------------------|-----------|
[MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.1333    |


MRR@100                                 | uniCOIL (no expansion, zero-shot)|
:---------------------------------------|-----------|
[MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.1342    |


R@100                                   | uniCOIL (no expansion, zero-shot)|
:---------------------------------------|-----------|
[MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.4976    |


R@1000                                  | uniCOIL (no expansion, zero-shot)|
:---------------------------------------|-----------|
[MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)| 0.7010    |
