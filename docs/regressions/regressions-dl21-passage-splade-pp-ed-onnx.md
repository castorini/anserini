# Anserini Regressions: TREC 2021 Deep Learning Track (Passage)

**Model**: SPLADE++ CoCondenser-EnsembleDistil (using ONNX for on-the-fly query encoding)

This page describes regression experiments, integrated into Anserini's regression testing framework, applying the [SPLADE++ CoCondenser-EnsembleDistil](https://huggingface.co/naver/splade-cocondenser-ensembledistil) model to the MS MARCO V2 passage corpus.
Here, we evaluate on the [TREC 2021 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2021.html), using ONNX to perform query encoding on the fly.

The model is described in the following paper:

> Thibault Formal, Carlos Lassance, Benjamin Piwowarski, and Stéphane Clinchant. [From Distillation to Hard Negative Sampling: Making Sparse Neural IR Models More Effective.](https://dl.acm.org/doi/10.1145/3477495.3531857) _Proceedings of the 45th International ACM SIGIR Conference on Research and Development in Information Retrieval_, pages 2353–2359.

For additional instructions on working with the MS MARCO V2 passage corpus, refer to [this page](../../docs/experiments-msmarco-v2.md).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl21-passage-splade-pp-ed-onnx.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl21-passage-splade-pp-ed-onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl21-passage-splade-pp-ed-onnx
```

We make available a version of the corpus that has already been encoded with SPLADE++ CoCondenser-EnsembleDistil.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression dl21-passage-splade-pp-ed-onnx
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco_v2_passage_splade_pp_ed.tar -P collections/
tar xvf collections/msmarco_v2_passage_splade_pp_ed.tar -C collections/
```

To confirm, `msmarco_v2_passage_splade_pp_ed.tar` is 66 GB and has MD5 checksum `2cdb2adc259b8fa6caf666b20ebdc0e8`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl21-passage-splade-pp-ed-onnx \
  --corpus-path collections/msmarco_v2_passage_splade_pp_ed
```

## Indexing

Sample indexing command:

```bash
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-v2-passage-splade-pp-ed \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.msmarco-v2-passage-splade-pp-ed/ \
  -threads 24 -impact -pretokenized -storeRaw \
  >& logs/log.msmarco-v2-passage-splade-pp-ed &
```

The path `/path/to/msmarco-v2-passage-splade-pp-ed/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the uniCOIL tokens.
Upon completion, we should have an index with 138,364,198 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 53 topics for which NIST has provided judgments as part of the [TREC 2021 Deep Learning Track](https://trec.nist.gov/data/deep2021.html).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-splade-pp-ed/ \
  -topics tools/topics-and-qrels/topics.dl21.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx.topics.dl21.txt \
  -parallelism 16 -impact -pretokenized -encoder SpladePlusPlusEnsembleDistil &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-splade-pp-ed/ \
  -topics tools/topics-and-qrels/topics.dl21.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx+rm3.topics.dl21.txt \
  -parallelism 16 -impact -pretokenized -encoder SpladePlusPlusEnsembleDistil -rm3 -collection JsonVectorCollection &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-splade-pp-ed/ \
  -topics tools/topics-and-qrels/topics.dl21.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx+rocchio.topics.dl21.txt \
  -parallelism 16 -impact -pretokenized -encoder SpladePlusPlusEnsembleDistil -rocchio -collection JsonVectorCollection &
```

Evaluation can be performed using `trec_eval`:

```bash
target/appassembler/bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx.topics.dl21.txt
target/appassembler/bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx.topics.dl21.txt
target/appassembler/bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx.topics.dl21.txt
target/appassembler/bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx.topics.dl21.txt
target/appassembler/bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx.topics.dl21.txt

target/appassembler/bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx+rm3.topics.dl21.txt
target/appassembler/bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx+rm3.topics.dl21.txt
target/appassembler/bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx+rm3.topics.dl21.txt
target/appassembler/bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx+rm3.topics.dl21.txt
target/appassembler/bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx+rm3.topics.dl21.txt

target/appassembler/bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx+rocchio.topics.dl21.txt
target/appassembler/bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx+rocchio.topics.dl21.txt
target/appassembler/bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx+rocchio.topics.dl21.txt
target/appassembler/bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx+rocchio.topics.dl21.txt
target/appassembler/bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-ed.splade-pp-ed-onnx+rocchio.topics.dl21.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **SPLADE++ CoCondenser-EnsembleDistil**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.3329    | 0.3340    | 0.3461    |
| **MRR@100**                                                                                                  | **SPLADE++ CoCondenser-EnsembleDistil**| **+RM3**  | **+Rocchio**|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.8289    | 0.7961    | 0.8471    |
| **nDCG@10**                                                                                                  | **SPLADE++ CoCondenser-EnsembleDistil**| **+RM3**  | **+Rocchio**|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.6851    | 0.6713    | 0.7006    |
| **R@100**                                                                                                    | **SPLADE++ CoCondenser-EnsembleDistil**| **+RM3**  | **+Rocchio**|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.5625    | 0.5693    | 0.5578    |
| **R@1000**                                                                                                   | **SPLADE++ CoCondenser-EnsembleDistil**| **+RM3**  | **+Rocchio**|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.8586    | 0.8705    | 0.8966    |
