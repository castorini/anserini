# Anserini: SPLADEv2 for MS MARCO V1 Passage Ranking

This page describes how to reproduce with Pyserini the DistilSPLADE-max experiments in the following paper:

> Thibault Formal, Carlos Lassance, Benjamin Piwowarski, Stéphane Clinchant. [SPLADE v2: Sparse Lexical and Expansion Model for Information Retrieval.](https://arxiv.org/abs/2109.10086) _arXiv:2109.10086_.

Here, we start with a version of the MS MARCO passage corpus that has already been processed with SPLADE, i.e., gone through document expansion and term reweighting.
Thus, no neural inference is involved. As SPLADE weights are given in fp16, they have been converted to integer by taking the round of weight*100.

Note that Pyserini provides [a comparable reproduction guide](https://github.com/castorini/pyserini/blob/master/docs/experiments-spladev2.md), so if you don't like Java, you can get _exactly_ the same results from Python.

## Data Prep

We're going to use the repository's root directory as the working directory.
First, we need to download and extract the MS MARCO passage dataset with SPLADE processing:

```bash
# Alternate mirrors of the same data, pick one:
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-distill-splade-max.tar -P collections/
wget https://vault.cs.uwaterloo.ca/s/poCLbJDMm7JxwPk/download -O collections/msmarco-passage-distill-splade-max.tar

tar xvf collections/msmarco-passage-distill-splade-max.tar -C collections/
```

To confirm, `msmarco-passage-distill-splade-max.tar` is ~9.8 GB and has MD5 checksum `95b89a7dfd88f3685edcc2d1ffb120d1`.

## Indexing

We can now index these docs as a `JsonVectorCollection` using Anserini:

```bash
sh target/appassembler/bin/IndexCollection -collection JsonVectorCollection \
 -input collections/msmarco-passage-distill-splade-max \
 -index indexes/lucene-index.msmarco-passage.distill-splade-max \
 -generator DefaultLuceneDocumentGenerator -impact -pretokenized \
 -threads 12
```

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doc lengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the SPLADEv2 tokens.

Upon completion, we should have an index with 8,841,823 documents.
The indexing speed may vary; on a modern desktop with an SSD (using 12 threads, per above), indexing takes around 30 minutes.

## Retrieval

To ensure that the tokenization in the index aligns exactly with the queries, we use pre-tokenized queries.
The queries are already stored in the repo, so we can run retrieval directly:

```bash
target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.distill-splade-max \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.distill-splade-max.tsv.gz \
 -output runs/run.msmarco-passage.distill-splade-max.tsv -format msmarco \
 -impact -pretokenized
```

Note that, mirroring the indexing options, we also specify `-impact -pretokenized` here.
Query evaluation is much slower than with bag-of-words BM25; a complete run takes around 4 hours (on a single thread).
No, this isn't a mistake!
This model suffers from very slow queries with Lucene due to some yet unknown issue.
We're looking into it.

With `-format msmarco`, runs are already in the MS MARCO output format, so we can evaluate directly:

```bash
python tools/scripts/msmarco/msmarco_passage_eval.py \
   tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.distill-splade-max.tsv
```

The results should be as follows:

```
#####################
MRR @10: 0.36852691363078205
QueriesRanked: 6980
#####################
```

This corresponds to the effectiveness reported in the paper.

## Reproduction Log[*](reproducibility.md)
+ Results reproduced by [@jmmackenzie](https://github.com/jmmackenzie) on 2021-10-15 (commit [`52b76f6`](https://github.com/castorini/anserini/commit/52b76f63b163036e8fad1a6e1b10b431b4ddd06c))
