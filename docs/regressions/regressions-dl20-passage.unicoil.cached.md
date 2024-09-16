# Anserini Regressions: TREC 2020 Deep Learning Track (Passage)

**Model**: uniCOIL with doc2query-T5 expansions (using cached queries)

This page describes regression experiments, integrated into Anserini's regression testing framework, using uniCOIL (with doc2query-T5 expansions) on the [TREC 2020 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2020.html).
The uniCOIL model is described in the following paper:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

The experiments on this page are not actually reported in the paper.
However, the model is the same.

In these experiments, we are using cached queries (i.e., cached results of query encoding).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](../../docs/experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl20-passage.unicoil.cached.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl20-passage.unicoil.cached.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.unicoil.cached
```

We make available a version of the MS MARCO Passage Corpus that has already been processed with uniCOIL, i.e., we have applied doc2query-T5 expansions, performed model inference on every document, and stored the output sparse vectors.
Thus, no neural inference is involved.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression dl20-passage.unicoil.cached
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-unicoil.tar -P collections/
tar xvf collections/msmarco-passage-unicoil.tar -C collections/
```

To confirm, `msmarco-passage-unicoil.tar` is 3.4 GB and has MD5 checksum `78eef752c78c8691f7d61600ceed306f`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.unicoil.cached \
  --corpus-path collections/msmarco-passage-unicoil
```

## Indexing

Sample indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-passage-unicoil \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-passage.unicoil/ \
  -impact -pretokenized -storeDocvectors \
  >& logs/log.msmarco-passage-unicoil &
```

The path `/path/to/msmarco-passage-unicoil/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the uniCOIL tokens.
Upon completion, we should have an index with 8,841,823 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 54 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage.unicoil/ \
  -topics tools/topics-and-qrels/topics.dl20.unicoil.0shot.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-unicoil.unicoil-cached.topics.dl20.unicoil.0shot.txt \
  -impact -pretokenized &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage.unicoil/ \
  -topics tools/topics-and-qrels/topics.dl20.unicoil.0shot.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-unicoil.unicoil-cached+rm3.topics.dl20.unicoil.0shot.txt \
  -impact -pretokenized -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage.unicoil/ \
  -topics tools/topics-and-qrels/topics.dl20.unicoil.0shot.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-unicoil.unicoil-cached+rocchio.topics.dl20.unicoil.0shot.txt \
  -impact -pretokenized -rocchio &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil-cached.topics.dl20.unicoil.0shot.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil-cached.topics.dl20.unicoil.0shot.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil-cached.topics.dl20.unicoil.0shot.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil-cached.topics.dl20.unicoil.0shot.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil-cached+rm3.topics.dl20.unicoil.0shot.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil-cached+rm3.topics.dl20.unicoil.0shot.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil-cached+rm3.topics.dl20.unicoil.0shot.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil-cached+rm3.topics.dl20.unicoil.0shot.txt

bin/trec_eval -m map -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil-cached+rocchio.topics.dl20.unicoil.0shot.txt
bin/trec_eval -m ndcg_cut.10 -c tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil-cached+rocchio.topics.dl20.unicoil.0shot.txt
bin/trec_eval -m recall.100 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil-cached+rocchio.topics.dl20.unicoil.0shot.txt
bin/trec_eval -m recall.1000 -c -l 2 tools/topics-and-qrels/qrels.dl20-passage.txt runs/run.msmarco-passage-unicoil.unicoil-cached+rocchio.topics.dl20.unicoil.0shot.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **uniCOIL (with doc2query-T5 expansions)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.4430    | 0.4478    | 0.4539    |
| **nDCG@10**                                                                                                  | **uniCOIL (with doc2query-T5 expansions)**| **+RM3**  | **+Rocchio**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.6745    | 0.6497    | 0.6599    |
| **R@100**                                                                                                    | **uniCOIL (with doc2query-T5 expansions)**| **+RM3**  | **+Rocchio**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.7006    | 0.6822    | 0.7096    |
| **R@1000**                                                                                                   | **uniCOIL (with doc2query-T5 expansions)**| **+RM3**  | **+Rocchio**|
| [DL20 (Passage)](https://trec.nist.gov/data/deep2020.html)                                                   | 0.8430    | 0.8417    | 0.8610    |

❗ Retrieval metrics here are computed to depth 1000 hits per query (as opposed to 100 hits per query for document ranking).
For computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., AP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).
The experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2102.07662).

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/dl20-passage.unicoil.cached.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@manveertamber](https://github.com/manveertamber) on 2022-02-25 (commit [`7472d86`](https://github.com/castorini/anserini/commit/7472d862c7311bc8bbd30655c940d6396e27c223))
+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-06-06 (commit [`236b386`](https://github.com/castorini/anserini/commit/236b386ddc11d292b4b736162b59488a02236d6c))
