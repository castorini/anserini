# Anserini Regressions: TREC 2020 Deep Learning Track (Passage)

**Model**: cosDPR-distil with inverted indexes using the "fake-words" technique (q=40) using cached queries

This page describes regression experiments, integrated into Anserini's regression testing framework, using the cosDPR-distil model on the [TREC 2020 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2019.html).
In these experiments, we are using cached queries (i.e., cached results of query encoding).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl20-passage.cos-dpr-distil.fw.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl20-passage.cos-dpr-distil.fw.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cos-dpr-distil.fw
```

We make available a version of the MS MARCO Passage Corpus that has already been encoded with cosDPR-distil.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression dl20-passage.cos-dpr-distil.fw
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-cos-dpr-distil.tar -P collections/
tar xvf collections/msmarco-passage-cos-dpr-distil.tar -C collections/
```

To confirm, `msmarco-passage-cos-dpr-distil.tar` is 57 GB and has MD5 checksum `e20ffbc8b5e7f760af31298aefeaebbd`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cos-dpr-distil.fw \
  --corpus-path collections/msmarco-passage-cos-dpr-distil
```

## Indexing

Sample indexing command, applying inverted indexes to dense vectors using the "fake-words" technique:

```bash
bin/run.sh io.anserini.index.IndexInvertedDenseVectors \
  -threads 16 \
  -collection JsonDenseVectorCollection \
  -input /path/to/msmarco-passage-cos-dpr-distil \
  -generator InvertedDenseVectorDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-passage.cos-dpr-distil.fw-40/ \
  -encoding fw -fw.q 40 \
  >& logs/log.msmarco-passage-cos-dpr-distil &
```

The path `/path/to/msmarco-passage-cos-dpr-distil/` should point to the corpus downloaded above.

Upon completion, we should have an index with 8,841,823 documents.

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 54 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchInvertedDenseVectors \
  -index indexes/lucene-inverted.msmarco-v1-passage.cos-dpr-distil.fw-40/ \
  -topics tools/topics-and-qrels/topics.dl20.cos-dpr-distil.jsonl.gz \
  -topicReader JsonIntVector \
  -output runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-fw-40-cached_q.topics.dl20.cos-dpr-distil.jsonl.txt \
  -topicField vector -threads 16 -encoding fw -fw.q 40 -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-fw-40-cached_q.topics.dl20.cos-dpr-distil.jsonl.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-fw-40-cached_q.topics.dl20.cos-dpr-distil.jsonl.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-fw-40-cached_q.topics.dl20.cos-dpr-distil.jsonl.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-cos-dpr-distil.cos-dpr-distil-fw-40-cached_q.topics.dl20.cos-dpr-distil.jsonl.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **cosDPR-distill**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.4597    |
| **nDCG@10**                                                                                                  | **cosDPR-distill**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.6666    |
| **R@100**                                                                                                    | **cosDPR-distill**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.6909    |
| **R@1000**                                                                                                   | **cosDPR-distill**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.8194    |

❗ Retrieval metrics here are computed to depth 1000 hits per query (as opposed to 100 hits per query for document ranking).
For computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., AP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).
The experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2102.07662).

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/dl20-passage.cos-dpr-distil.fw.template) and run `bin/build.sh` to rebuild the documentation.
