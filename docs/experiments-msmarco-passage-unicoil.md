# Anserini: uniCOIL for MS MARCO Passage Ranking

This page describes how to reproduce the uniCOIL experiments in the following paper:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

In this guide, we start with a version of the MS MARCO passage corpus that has already been processed with uniCOIL, i.e., gone through document expansion and term reweighting.
Thus, no neural inference is involved.
For details on how to train uniCOIL and perform inference, please see [this guide](https://github.com/luyug/COIL/tree/main/uniCOIL).

Note that Pyserini provides [a comparable reproduction guide](https://github.com/castorini/pyserini/blob/master/docs/experiments-unicoil.md), so if you don't like Java, you can get _exactly_ the same results from Python.

## Data Prep

We're going to use the repository's root directory as the working directory.
First, we need to download and extract the MS MARCO passage dataset with uniCOIL processing:

```bash
wget https://git.uwaterloo.ca/jimmylin/unicoil/-/raw/master/msmarco-passage-unicoil-b8.tar -P collections/

# Alternate mirror
wget https://vault.cs.uwaterloo.ca/s/Rm6fknT432YdBts/download -O collections/msmarco-passage-unicoil-b8.tar

tar xvf collections/msmarco-passage-unicoil-b8.tar -C collections/
```

To confirm, `msmarco-passage-unicoil-b8.tar` should have MD5 checksum of `eb28c059fad906da2840ce77949bffd7`.


## Indexing

We can now index these docs as a `JsonVectorCollection` using Anserini:

```bash
sh target/appassembler/bin/IndexCollection -collection JsonVectorCollection \
 -input collections/msmarco-passage-unicoil-b8/ \
 -index indexes/lucene-index.msmarco-passage-unicoil-b8 \
 -generator DefaultLuceneDocumentGenerator -impact -pretokenized \
 -threads 12 -storeRaw -optimize
```

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the uniCOIL tokens.

Upon completion, we should have an index with 8,841,823 documents.
The indexing speed may vary; on a modern desktop with an SSD (using 12 threads, per above), indexing takes around ten minutes.


## Retrieval

To ensure that the tokenization in the index aligns exactly with the queries, we use pre-tokenized queries.
The queries are already stored in the repo, so we can run retrieval directly:

```
target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage-unicoil-b8 \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.unicoil.tsv.gz \
 -output runs/run.msmarco-passage-unicoil-b8.trec \
 -impact -pretokenized
```

The queries are also available to download at the following locations:

```bash
wget https://git.uwaterloo.ca/jimmylin/unicoil/-/raw/master/topics.msmarco-passage.dev-subset.unicoil.tsv.gz -P collections/
wget https://vault.cs.uwaterloo.ca/s/QGoHeBm4YsAgt6H/download -O collections/topics.msmarco-passage.dev-subset.unicoil.tsv.gz

# MD5 checksum: 1af1da05ae5fe0b9d8ddf2d143b6e7f8
```

We can now run retrieval:


Query evaluation is much slower than with bag-of-words BM25; a complete run can take around 15 min.
Note that, mirroring the indexing options, we specify `-impact -pretokenized` here also.

The output is in TREC output format.
Let's convert to MS MARCO output format and then evaluate:

```
python tools/scripts/msmarco/convert_trec_to_msmarco_run.py \
   --input runs/run.msmarco-passage-unicoil-b8.trec \
   --output runs/run.msmarco-passage-unicoil-b8.txt --quiet

python tools/scripts/msmarco/msmarco_passage_eval.py \
   tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-unicoil-b8.txt
```

The results should be as follows:

```
#####################
MRR @10: 0.35155222404147896
QueriesRanked: 6980
#####################
```


## Reproduction Log[*](reproducibility.md)

+ Results reproduced by [@lintool](https://github.com/lintool) on 2021-06-28 (commit [`1550683`](https://github.com/castorini/anserini/commit/1550683e41cefe89b7e67c0a5f0e147bc70dfcda))
+ Results reproduced by [@JMMackenzie](https://github.com/JMMackenzie) on 2021-07-02 (commit [`e4c5127`](https://github.com/castorini/anserini/commit/e4c51278d375ebad9aa2bf9bde66cab32260d6b4))
+ Results reproduced by [@amallia](https://github.com/amallia) on 2021-07-14 (commit [`dad4b82`](https://github.com/castorini/anserini/commit/dad4b82cba2d879ae20147b2abdd04564331ea6f))
+ Results reproduced by [@ArvinZhuang](https://github.com/ArvinZhuang) on 2021-07-16 (commit [`43ad899`](https://github.com/castorini/anserini/commit/43ad899337ac5e3b219d899bb218c4bcae18b1e6))
