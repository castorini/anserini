# Anserini Regressions: TREC 2022 Deep Learning Track (Passage)

**Model**: SPLADE++ CoCondenser-SelfDistil

This page describes baseline experiments, integrated into Anserini's regression testing framework, on the [TREC 2022 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2022.html) using the MS MARCO V2 Passage Corpus.
Here, we cover experiments with the [SPLADE++ CoCondenser-SelfDistil](https://huggingface.co/naver/splade-cocondenser-selfdistil) model, described in the following paper:

> Thibault Formal, Carlos Lassance, Benjamin Piwowarski, and Stéphane Clinchant. [From Distillation to Hard Negative Sampling: Making Sparse Neural IR Models More Effective.](https://dl.acm.org/doi/10.1145/3477495.3531857) _Proceedings of the 45th International ACM SIGIR Conference on Research and Development in Information Retrieval_, pages 2353–2359.

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with the MS MARCO V2 Passage Corpus, refer to [this page](../../docs/experiments-msmarco-v2.md).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl22-passage-splade-pp-sd.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl22-passage-splade-pp-sd.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl22-passage-splade-pp-sd
```

We make available a version of the corpus that has already been encoded with SPLADE++ CoCondenser-SelfDistil.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression dl22-passage-splade-pp-sd
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco_v2_passage_splade_pp_sd.tar -P collections/
tar xvf collections/msmarco_v2_passage_splade_pp_sd.tar -C collections/
```

To confirm, `msmarco_v2_passage_splade_pp_sd.tar` is 76 GB and has MD5 checksum `061930dd615c7c807323ea7fc7957877`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl22-passage-splade-pp-sd \
  --corpus-path collections/msmarco_v2_passage_splade_pp_sd
```

## Indexing

Sample indexing command:

```bash
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-v2-passage-splade-pp-sd \
  -index indexes/lucene-index.msmarco-v2-passage-splade-pp-sd/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 24 -impact -pretokenized -storeRaw \
  >& logs/log.msmarco-v2-passage-splade-pp-sd &
```

The path `/path/to/msmarco-v2-passage-splade-pp-sd/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the uniCOIL tokens.
Upon completion, we should have an index with 138,364,198 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 76 topics for which NIST has provided judgments as part of the [TREC 2022 Deep Learning Track](https://trec.nist.gov/data/deep2022.html).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-splade-pp-sd/ \
  -topics tools/topics-and-qrels/topics.dl22.splade-pp-sd.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd.topics.dl22.splade-pp-sd.txt \
  -parallelism 16 -impact -pretokenized &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-splade-pp-sd/ \
  -topics tools/topics-and-qrels/topics.dl22.splade-pp-sd.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd+rm3.topics.dl22.splade-pp-sd.txt \
  -parallelism 16 -impact -pretokenized -rm3 -collection JsonVectorCollection &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-splade-pp-sd/ \
  -topics tools/topics-and-qrels/topics.dl22.splade-pp-sd.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd+rocchio.topics.dl22.splade-pp-sd.txt \
  -parallelism 16 -impact -pretokenized -rocchio -collection JsonVectorCollection &
```

Evaluation can be performed using `trec_eval`:

```bash
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd.topics.dl22.splade-pp-sd.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd.topics.dl22.splade-pp-sd.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd.topics.dl22.splade-pp-sd.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd.topics.dl22.splade-pp-sd.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd.topics.dl22.splade-pp-sd.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd+rm3.topics.dl22.splade-pp-sd.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd+rm3.topics.dl22.splade-pp-sd.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd+rm3.topics.dl22.splade-pp-sd.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd+rm3.topics.dl22.splade-pp-sd.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd+rm3.topics.dl22.splade-pp-sd.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd+rocchio.topics.dl22.splade-pp-sd.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd+rocchio.topics.dl22.splade-pp-sd.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd+rocchio.topics.dl22.splade-pp-sd.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd+rocchio.topics.dl22.splade-pp-sd.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd+rocchio.topics.dl22.splade-pp-sd.txt
```

Note that the TREC 2022 passage qrels are not publicly available (yet).
However, if you are a participant, you can download them from the NIST "active participants" site.
Place the qrels file in `tools/topics-and-qrels/qrels.dl22-passage.txt` for the above evaluation commands to work.

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **SPLADE++ CoCondenser-SelfDistil**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL22 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.1801    | 0.1879    | 0.1923    |
| **MRR@100**                                                                                                  | **SPLADE++ CoCondenser-SelfDistil**| **+RM3**  | **+Rocchio**|
| [DL22 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.7588    | 0.7287    | 0.7230    |
| **nDCG@10**                                                                                                  | **SPLADE++ CoCondenser-SelfDistil**| **+RM3**  | **+Rocchio**|
| [DL22 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.5786    | 0.5668    | 0.5917    |
| **R@100**                                                                                                    | **SPLADE++ CoCondenser-SelfDistil**| **+RM3**  | **+Rocchio**|
| [DL22 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.3681    | 0.3693    | 0.3761    |
| **R@1000**                                                                                                   | **SPLADE++ CoCondenser-SelfDistil**| **+RM3**  | **+Rocchio**|
| [DL22 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.6551    | 0.6350    | 0.6696    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/dl22-passage-splade-pp-sd.template) and run `bin/build.sh` to rebuild the documentation.
