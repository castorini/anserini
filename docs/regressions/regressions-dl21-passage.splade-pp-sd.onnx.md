# Anserini Regressions: TREC 2021 Deep Learning Track (Passage)

**Model**: SPLADE++ CoCondenser-SelfDistil (using ONNX for on-the-fly query encoding)

This page describes regression experiments, integrated into Anserini's regression testing framework, applying the [SPLADE++ CoCondenser-SelfDistil](https://huggingface.co/naver/splade-cocondenser-selfdistil) model to the MS MARCO V2 passage corpus.
Here, we evaluate on the [TREC 2021 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2021.html), using ONNX to perform query encoding on the fly.

The model is described in the following paper:

> Thibault Formal, Carlos Lassance, Benjamin Piwowarski, and Stéphane Clinchant. [From Distillation to Hard Negative Sampling: Making Sparse Neural IR Models More Effective.](https://dl.acm.org/doi/10.1145/3477495.3531857) _Proceedings of the 45th International ACM SIGIR Conference on Research and Development in Information Retrieval_, pages 2353–2359.

For additional instructions on working with the MS MARCO V2 passage corpus, refer to [this page](../../docs/experiments-msmarco-v2.md).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl21-passage.splade-pp-sd.onnx.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl21-passage.splade-pp-sd.onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl21-passage.splade-pp-sd.onnx
```

We make available a version of the corpus that has already been encoded with SPLADE++ CoCondenser-SelfDistil.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression dl21-passage.splade-pp-sd.onnx
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
python src/main/python/run_regression.py --index --verify --search --regression dl21-passage.splade-pp-sd.onnx \
  --corpus-path collections/msmarco_v2_passage_splade_pp_sd
```

## Indexing

Sample indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-v2-passage-splade-pp-sd \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2-passage.splade-pp-sd/ \
  -impact -pretokenized -storeRaw \
  >& logs/log.msmarco-v2-passage-splade-pp-sd &
```

The path `/path/to/msmarco-v2-passage-splade-pp-sd/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the uniCOIL tokens.
Upon completion, we should have an index with 138,364,198 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 53 topics for which NIST has provided judgments as part of the [TREC 2021 Deep Learning Track](https://trec.nist.gov/data/deep2021.html).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage.splade-pp-sd/ \
  -topics tools/topics-and-qrels/topics.dl21.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx.topics.dl21.txt \
  -parallelism 16 -impact -pretokenized -encoder SpladePlusPlusSelfDistil &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage.splade-pp-sd/ \
  -topics tools/topics-and-qrels/topics.dl21.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx+rm3.topics.dl21.txt \
  -parallelism 16 -impact -pretokenized -encoder SpladePlusPlusSelfDistil -rm3 -collection JsonVectorCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage.splade-pp-sd/ \
  -topics tools/topics-and-qrels/topics.dl21.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx+rocchio.topics.dl21.txt \
  -parallelism 16 -impact -pretokenized -encoder SpladePlusPlusSelfDistil -rocchio -collection JsonVectorCollection &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx.topics.dl21.txt
bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx.topics.dl21.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx.topics.dl21.txt
bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx.topics.dl21.txt
bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx.topics.dl21.txt

bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx+rm3.topics.dl21.txt
bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx+rm3.topics.dl21.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx+rm3.topics.dl21.txt
bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx+rm3.topics.dl21.txt
bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx+rm3.topics.dl21.txt

bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx+rocchio.topics.dl21.txt
bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx+rocchio.topics.dl21.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx+rocchio.topics.dl21.txt
bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx+rocchio.topics.dl21.txt
bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl21-passage.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-onnx+rocchio.topics.dl21.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **SPLADE++ CoCondenser-SelfDistil**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.3253    | 0.3430    | 0.3452    |
| **MRR@100**                                                                                                  | **SPLADE++ CoCondenser-SelfDistil**| **+RM3**  | **+Rocchio**|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.8080    | 0.8138    | 0.8231    |
| **nDCG@10**                                                                                                  | **SPLADE++ CoCondenser-SelfDistil**| **+RM3**  | **+Rocchio**|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.6806    | 0.6919    | 0.7033    |
| **R@100**                                                                                                    | **SPLADE++ CoCondenser-SelfDistil**| **+RM3**  | **+Rocchio**|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.5524    | 0.5603    | 0.5574    |
| **R@1000**                                                                                                   | **SPLADE++ CoCondenser-SelfDistil**| **+RM3**  | **+Rocchio**|
| [DL21 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.8522    | 0.8663    | 0.8825    |
