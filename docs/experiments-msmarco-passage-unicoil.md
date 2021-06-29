# Anserini: uniCOIL for MS MARCO Passage Ranking

This page describes how to reproduce the uniCOIL experiments in the following paper:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

Here, we start with a version of the MS MARCO passage corpus that has already been processed with uniCOIL, i.e., gone through document expansion and term reweighting.
Thus, no neural inference is involved.


## Data Prep

We're going to use the repository's root directory as the working directory.
First, we need to download and extract the MS MARCO passage dataset with uniCOIL processing:

```bash
wget https://git.uwaterloo.ca/jimmylin/unicoil/-/raw/master/msmarco-passage-unicoil-b8.tar -P collections/
tar -xvf collections/msmarco-passage-unicoil-b8.tar -C collections/
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
First, fetch the MS MARCO passage ranking dev set queries: 

```
wget https://git.uwaterloo.ca/jimmylin/unicoil/-/raw/master/topics.msmarco-passage.dev-subset.unicoil.tsv.gz -P collections/
gzip -d collections/topics.msmarco-passage.dev-subset.unicoil.tsv.gz
```

We can now run retrieval:

```
target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage-unicoil-b8 \
 -topicreader TsvInt -topics collections/topics.msmarco-passage.dev-subset.unicoil.tsv \
 -output runs/run.msmarco-passage-unicoil-b8.trec \
 -impact -pretokenized
```

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

