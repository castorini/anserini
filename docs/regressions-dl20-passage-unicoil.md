# Anserini: Regressions for [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html) w/ uniCOIL

This page describes document expansion experiments, integrated into Anserini's regression testing framework, for the TREC 2020 Deep Learning Track (Passage Ranking Task) on the MS MARCO passage collection using relevance judgments from NIST.
These runs use the uniCOIL model described in the following paper:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

The paper does not actually report on the experiments here; these were created after the paper was posted on arXiv.
However, the model is the same.

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl20-passage-unicoil.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl20-passage-unicoil.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-passage-unicoil \
  -index indexes/lucene-index.msmarco-passage-unicoil/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -impact -pretokenized \
  >& logs/log.msmarco-passage-unicoil &
```

The directory `/path/to/msmarco-passage-unicoil/` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](experiments-msmarco-unicoil.md) for additional details.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 54 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-unicoil/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.unicoil.0shot.tsv.gz -topicreader TsvInt \
  -output runs/run.msmarco-passage-unicoil.unicoil.topics.dl20.unicoil.0shot.tsv.gz \
  -impact -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil.topics.dl20.unicoil.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil.topics.dl20.unicoil.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -m recip_rank -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil.topics.dl20.unicoil.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil.topics.dl20.unicoil.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 src/main/resources/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil.topics.dl20.unicoil.0shot.tsv.gz
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | uniCOIL w/ doc2query-T5 expansion|
:---------------------------------------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.4430    |


nDCG@10                                 | uniCOIL w/ doc2query-T5 expansion|
:---------------------------------------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.6745    |


MRR                                     | uniCOIL w/ doc2query-T5 expansion|
:---------------------------------------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.8235    |


R@100                                   | uniCOIL w/ doc2query-T5 expansion|
:---------------------------------------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.7006    |


R@1000                                  | uniCOIL w/ doc2query-T5 expansion|
:---------------------------------------|-----------|
[DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)| 0.8430    |
