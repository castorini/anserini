# Anserini: DeepImpact for MS MARCO Passage Ranking

This page describes how to reproduce the DeepImpact experiments in the following paper:

> Antonio Mallia, Omar Khattab, Nicola Tonellotto, and Torsten Suel. [Learning Passage Impacts for Inverted Indexes.](https://arxiv.org/abs/2104.12016) _arXiv:2104.12016_.

Here, we start with a version of the MS MARCO passage corpus that has already been processed with DeepImpact, i.e., gone through document expansion and term reweighting.
Thus, no neural inference is involved.


## Data Prep

We're going to use the repository's root directory as the working directory.
First, we need to download and extract the MS MARCO passage dataset with DeepImpact processing:

```bash
wget https://git.uwaterloo.ca/jimmylin/deep-impact/raw/master/msmarco-passage-deepimpact-b8.tar.gz -P collections/
tar -xzvf collections/msmarco-passage-deepimpact-b8.tar.gz -C collections/
```

To confirm, `msmarco-passage-deepimpact-b8.tar.gz` should have MD5 checksum of `8ea0ebdd707d5853a87940e5bdfd9b00`.


## Indexing

We can now index these docs as a `JsonVectorCollection` using Anserini:

```bash
sh target/appassembler/bin/IndexCollection -collection JsonVectorCollection \
 -input collections/msmarco-passage-deepimpact-b8/ \
 -index indexes/lucene-index.msmarco-passage-deepimpact-b8 \
 -generator DefaultLuceneDocumentGenerator -impact -pretokenized \
 -threads 18 -storeRaw
```

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the DeepImpact tokens.

Upon completion, we should have an index with 8,841,823 documents.
The indexing speed may vary; on a modern desktop with an SSD (using 18 threads, per above), indexing takes around ten minutes.


## Retrieval

To ensure that the tokenization in the index aligns exactly with the queries, we use pre-tokenized queries.
First, fetch the MS MARCO passage ranking dev set queries: 

```
wget https://git.uwaterloo.ca/jimmylin/deep-impact/raw/master/topics.msmarco-passage.dev-subset.deep-impact.tsv -P collections/
```

We can now run retrieval:

```
target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage-deepimpact-b8 \
 -topicreader TsvInt -topics collections/topics.msmarco-passage.dev-subset.deep-impact.tsv \
 -output runs/run.msmarco-passage-deepimpact-b8.trec \
 -impact -pretokenized
```

Query evaluation is much slower than with bag-of-words BM25; a complete run can take around half an hour.
Note that, mirroring the indexing options, we specify `-impact -pretokenized` here also.

The output is in TREC output format.
Let's convert to MS MARCO output format and then evaluate:

```
python tools/scripts/msmarco/convert_trec_to_msmarco_run.py \
   --input runs/run.msmarco-passage-deepimpact-b8.trec \
   --output runs/run.msmarco-passage-deepimpact-b8.txt --quiet

python tools/scripts/msmarco/msmarco_passage_eval.py \
   collections/msmarco-passage/qrels.dev.small.tsv runs/run.msmarco-passage-deepimpact-b8.txt
```

The results should be as follows:

```
#####################
MRR @10: 0.3252764133351524
QueriesRanked: 6980
#####################
```

The final evaluation metric is very close to the one reported in the paper (0.326).


## Reproduction Log[*](reproducibility.md)

+ Results reproduced by [@MXueguang](https://github.com/MXueguang) on 2021-06-17 (commit [`ff618db`](https://github.com/castorini/anserini/commit/ff618dbf87feee0ad75dc42c72a361c05984097d))
+ Results reproduced by [@JMMackenzie](https://github.com/jmmackenzie) on 2021-06-22 (commit [`490434`](https://github.com/castorini/anserini/commit/490434172a035b6eade8c17771aed83cc7f5d996))
+ Results reproduced by [@amyxie361](https://github.com/amyxie361) on 2021-06-22 (commit [`6f9352`](https://github.com/castorini/anserini/commit/6f9352fc5d6a4938fadc2bda9d0c428056eec5f0))
