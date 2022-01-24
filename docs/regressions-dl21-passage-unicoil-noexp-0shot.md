# Anserini: Regressions for [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)

This page describes baseline experiments, integrated into Anserini's regression testing framework, for the TREC 2021 Deep Learning Track (Passage Ranking Task) on the MS MARCO V2 passage collection using relevance judgments from NIST.
Here, we cover experiments with the uniCOIL model trained on the MS MARCO V1 passage ranking test collection, applied in a zero-shot manner, with no document expansion.

At the time this regression was created (November 2021), the qrels are only available to TREC participants.
You must download the qrels from NIST's "active participants" password-protected site and place at `src/main/resources/topics-and-qrels/qrels.dl21-passage.txt`.
The qrels will be added to Anserini when they are publicly released in Spring 2022.

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO V2 passage collection, refer to [this page](experiments-msmarco-v2.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl21-passage-unicoil-noexp-0shot.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl21-passage-unicoil-noexp-0shot.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-v2-passage-unicoil-noexp-0shot \
  -index indexes/lucene-index.msmarco-v2-passage-unicoil-noexp-0shot/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 18 -impact -pretokenized \
  >& logs/log.msmarco-v2-passage-unicoil-noexp-0shot &
```

The value of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 53 topics for which NIST has provided judgments as part of the TREC 2021 Deep Learning Track.
<!-- The original data can be found [here](https://trec.nist.gov/data/deep2021.html). -->

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-unicoil-noexp-0shot/ \
  -topics src/main/resources/topics-and-qrels/topics.dl21.unicoil-noexp.0shot.tsv.gz -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.dl21.unicoil-noexp.0shot.tsv.gz \
  -impact -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.dl21.unicoil-noexp.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.dl21.unicoil-noexp.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.dl21.unicoil-noexp.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.dl21.unicoil-noexp.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.dl21.unicoil-noexp.0shot.tsv.gz
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP@100                                 | uniCOIL (no expansion, zero-shot)|
:---------------------------------------|-----------|
[DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.2193    |


MRR@100                                 | uniCOIL (no expansion, zero-shot)|
:---------------------------------------|-----------|
[DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.6991    |


nDCG@10                                 | uniCOIL (no expansion, zero-shot)|
:---------------------------------------|-----------|
[DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.5756    |


R@100                                   | uniCOIL (no expansion, zero-shot)|
:---------------------------------------|-----------|
[DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.4246    |


R@1000                                  | uniCOIL (no expansion, zero-shot)|
:---------------------------------------|-----------|
[DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.6897    |

This run roughly corresponds to run `d_unicoil0` submitted to the TREC 2021 Deep Learning Track under the "baseline" group.
The difference is that here we are using pre-encoded queries, whereas the official submission performed query encoding on the fly.