# Anserini: Regressions for [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)

This page describes baseline experiments, integrated into Anserini's regression testing framework, for the TREC 2021 Deep Learning Track (Passage Ranking Task) on the MS MARCO V2 passage collection using relevance judgments from NIST.
Here, we cover experiments with the uniCOIL model trained on the MS MARCO V1 passage ranking test collection, applied in a zero-shot manner, with doc2query-T5 expansion.

The uniCOIL model is described in the following paper:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

At the time this regression was created (November 2021), the qrels are only available to TREC participants.
You must download the qrels from NIST's "active participants" password-protected site and place at `src/main/resources/topics-and-qrels/qrels.dl21-passage.txt`.
The qrels will be added to Anserini when they are publicly released in Spring 2022.

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO V2 passage collection, refer to [this page](experiments-msmarco-v2.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl21-passage-unicoil-0shot.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl21-passage-unicoil-0shot.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Corpus

Download, unpack, and prepare the corpus:

```
# Download
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_passage_unicoil_0shot.tar -P collections/

# Unpack
tar -xvf collections/msmarco_v2_passage_unicoil_0shot.tar -C collections/

# Rename (indexer is expecting corpus under a slightly different name)
mv collections/msmarco_v2_passage_unicoil_0shot collections/msmarco-v2-passage-unicoil-0shot
```

To confirm, `msmarco_v2_passage_unicoil_0shot.tar` is 41 GB and has an MD5 checksum of `1949a00bfd5e1f1a230a04bbc1f01539`.

## Indexing

Sample indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-v2-passage-unicoil-0shot \
  -index indexes/lucene-index.msmarco-v2-passage-unicoil-0shot/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 18 -impact -pretokenized \
  >& logs/log.msmarco-v2-passage-unicoil-0shot &
```

The path `/path/to/msmarco-v2-passage-unicoil-0shot/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the uniCOIL tokens.
Upon completion, we should have an index with 138,364,198 documents.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 53 topics for which NIST has provided judgments as part of the TREC 2021 Deep Learning Track.
<!-- The original data can be found [here](https://trec.nist.gov/data/deep2021.html). -->

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-unicoil-0shot/ \
  -topics src/main/resources/topics-and-qrels/topics.dl21.unicoil.0shot.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-unicoil-0shot.unicoil-0shot.topics.dl21.unicoil.0shot.txt \
  -impact -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-unicoil-0shot.unicoil-0shot.topics.dl21.unicoil.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-unicoil-0shot.unicoil-0shot.topics.dl21.unicoil.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-unicoil-0shot.unicoil-0shot.topics.dl21.unicoil.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-unicoil-0shot.unicoil-0shot.topics.dl21.unicoil.0shot.tsv.gz
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 src/main/resources/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-unicoil-0shot.unicoil-0shot.topics.dl21.unicoil.0shot.tsv.gz
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP@100                                 | uniCOIL (zero-shot)|
:---------------------------------------|-----------|
[DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.2538    |


MRR@100                                 | uniCOIL (zero-shot)|
:---------------------------------------|-----------|
[DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.7311    |


nDCG@10                                 | uniCOIL (zero-shot)|
:---------------------------------------|-----------|
[DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.6159    |


R@100                                   | uniCOIL (zero-shot)|
:---------------------------------------|-----------|
[DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.4731    |


R@1000                                  | uniCOIL (zero-shot)|
:---------------------------------------|-----------|
[DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)| 0.7551    |

This run roughly corresponds to run `d_unicoil0` submitted to the TREC 2021 Deep Learning Track under the "baseline" group.
The difference is that here we are using pre-encoded queries, whereas the official submission performed query encoding on the fly.

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/dl21-passage-unicoil-0shot.template) and run `bin/build.sh` to rebuild the documentation.
