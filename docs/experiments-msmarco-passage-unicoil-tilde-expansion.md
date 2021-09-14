# Anserini: uniCOIL (w/ TILDE) for MS MARCO Passage Ranking

This page describes how to reproduce experiments using uniCOIL with TILDE document expansion, as described in the following paper:

> Shengyao Zhuang and Guido Zuccon. [Fast Passage Re-ranking with Contextualized Exact Term
Matching and Efficient Passage Expansion.](https://arxiv.org/pdf/2108.08513) _arXiv:2108.08513_.

The original uniCOIL model is described here:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

In the original uniCOIL paper, doc2query-T5 is used to perform document expansion, which is slow and expensive.
As an alternative, Zhuang and Zuccon proposed to use the TILDE model to expand the corpus, resulting in a faster and cheaper document expansion process.
For details of how to use TILDE to expand documents, please see [this guide](https://github.com/ielab/TILDE).

In this guide, we start with a version of the MS MARCO passage corpus that has already been processed with uniCOIL, i.e., gone through document expansion and term reweighting.
Thus, no neural inference is involved.
For details on how to train uniCOIL and perform inference, please see [this guide](https://github.com/luyug/COIL/tree/main/uniCOIL).

Note that Pyserini provides [a comparable reproduction guide](https://github.com/castorini/pyserini/blob/master/docs/experiments-unicoil-tilde-expansion.md), so if you don't like Java, you can get _exactly_ the same results from Python.

## Data Prep

We're going to use the repository's root directory as the working directory.
First, we need to download and extract the MS MARCO passage dataset with uniCOIL processing:

```bash
wget https://git.uwaterloo.ca/jimmylin/unicoil/-/raw/master/msmarco-passage-unicoil-tilde-expansion-b8.tar -P collections/

# Alternate mirror
wget https://vault.cs.uwaterloo.ca/s/6LECmLdiaBoPwrL/download -O collections/msmarco-passage-unicoil-tilde-expansion-b8.tar

tar -xvf collections/msmarco-passage-unicoil-tilde-expansion-b8.tar -C collections/
```

To confirm, `msmarco-passage-unicoil-tilde-expansion-b8.tar` should have MD5 checksum of `be0a786033140ebb7a984a3e155c19ae`.


## Indexing

We can now index these docs as a `JsonVectorCollection` using Anserini:

```bash
sh target/appassembler/bin/IndexCollection -collection JsonVectorCollection \
 -input collections/msmarco-passage-unicoil-tilde-expansion-b8/ \
 -index indexes/lucene-index.msmarco-passage-unicoil-tilde-expansion-b8 \
 -generator DefaultLuceneDocumentGenerator -impact -pretokenized \
 -threads 12 -storeRaw -optimize
```

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the uniCOIL tokens.

Upon completion, we should have an index with 8,841,823 documents.
The indexing speed may vary; on a modern desktop with an SSD (using 12 threads, per above), indexing takes around ten minutes.


## Retrieval

To ensure that the tokenization in the index aligns exactly with the queries, we use pre-tokenized queries.
The queries are already stored in the repo, so we can run retrieval directly:

```bash
target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage-unicoil-tilde-expansion-b8 \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.unicoil.tilde.expansion.tsv.gz \
 -output runs/run.msmarco-passage-unicoil-tilde-expansion-b8.trec \
 -impact -pretokenized
```

The queries are also available to download at the following locations:

```bash
wget https://git.uwaterloo.ca/jimmylin/unicoil/-/raw/master/topics.msmarco-passage.dev-subset.unicoil.tilde.expansion.tsv.gz -P collections/
wget https://vault.cs.uwaterloo.ca/s/QGoHeBm4YsAgt6H/download -O collections/topics.msmarco-passage.dev-subset.unicoil.tilde.expansion.tsv.gz

# MD5 checksum:
```

Query evaluation is much slower than with bag-of-words BM25; a complete run can take around 15 min.
Note that, mirroring the indexing options, we specify `-impact -pretokenized` here also.

The output is in TREC output format.
Let's convert to MS MARCO output format and then evaluate:

```bash
python tools/scripts/msmarco/convert_trec_to_msmarco_run.py \
   --input runs/run.msmarco-passage-unicoil-tilde-expansion-b8.trec \
   --output runs/run.msmarco-passage-unicoil-tilde-expansion-b8.txt --quiet

python tools/scripts/msmarco/msmarco_passage_eval.py \
   tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-unicoil-tilde-expansion-b8.txt
```

The results should be as follows:

```
#####################
MRR @10: 0.34965502342293175
QueriesRanked: 6980
#####################
```

This corresponds to the effectiveness reported in the paper.


## Reproduction Log[*](reproducibility.md)

Results reproduced by [@MXueguang](https://github.com/MXueguang) on 2021-09-14 (commit [`a05fc52`](https://github.com/castorini/anserini/commit/a05fc5215a6d9de77bd5f4b8f874f608442024a3))
