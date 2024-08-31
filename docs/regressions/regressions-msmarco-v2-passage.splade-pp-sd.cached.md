# Anserini Regressions: MS MARCO (V2) Passage Ranking

**Model**: SPLADE++ CoCondenser-SelfDistil (using cached queries)

This page describes regression experiments, integrated into Anserini's regression testing framework, applying the [SPLADE++ CoCondenser-SelfDistil](https://huggingface.co/naver/splade-cocondenser-selfdistil) model to the MS MARCO V2 passage corpus.
Here, we evaluate on the dev queries, using cached queries (i.e., cached results of query encoding).

The model is described in the following paper:

> Thibault Formal, Carlos Lassance, Benjamin Piwowarski, and Stéphane Clinchant. [From Distillation to Hard Negative Sampling: Making Sparse Neural IR Models More Effective.](https://dl.acm.org/doi/10.1145/3477495.3531857) _Proceedings of the 45th International ACM SIGIR Conference on Research and Development in Information Retrieval_, pages 2353–2359.

For additional instructions on working with the MS MARCO V2 passage corpus, refer to [this page](../../docs/experiments-msmarco-v2.md).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-v2-passage.splade-pp-sd.cached.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-v2-passage.splade-pp-sd.cached.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage.splade-pp-sd.cached
```

We make available a version of the corpus that has already been encoded with SPLADE++ CoCondenser-SelfDistil.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression msmarco-v2-passage.splade-pp-sd.cached
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
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage.splade-pp-sd.cached \
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

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doc lengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the pre-encoded tokens.
Upon completion, we should have an index with 8,841,823 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage.splade-pp-sd/ \
  -topics tools/topics-and-qrels/topics.msmarco-v2-passage.dev.splade-pp-sd.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-cached.topics.msmarco-v2-passage.dev.splade-pp-sd.txt \
  -parallelism 16 -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage.splade-pp-sd/ \
  -topics tools/topics-and-qrels/topics.msmarco-v2-passage.dev2.splade-pp-sd.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-cached.topics.msmarco-v2-passage.dev2.splade-pp-sd.txt \
  -parallelism 16 -impact -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-cached.topics.msmarco-v2-passage.dev.splade-pp-sd.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-cached.topics.msmarco-v2-passage.dev.splade-pp-sd.txt
bin/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank tools/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-cached.topics.msmarco-v2-passage.dev.splade-pp-sd.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-cached.topics.msmarco-v2-passage.dev2.splade-pp-sd.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-cached.topics.msmarco-v2-passage.dev2.splade-pp-sd.txt
bin/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank tools/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-splade-pp-sd.splade-pp-sd-cached.topics.msmarco-v2-passage.dev2.splade-pp-sd.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **SPLADE++ CoCondenser-SelfDistil**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.1522    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.1615    |
| **MRR@100**                                                                                                  | **SPLADE++ CoCondenser-SelfDistil**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.1537    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.1629    |
| **R@100**                                                                                                    | **SPLADE++ CoCondenser-SelfDistil**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.6075    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.6183    |
| **R@1000**                                                                                                   | **SPLADE++ CoCondenser-SelfDistil**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.8270    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.8234    |
