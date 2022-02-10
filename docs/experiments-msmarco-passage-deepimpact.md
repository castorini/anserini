# Anserini: DeepImpact for MS MARCO V1 Passage Ranking

This page previously hosted a guide on how to reproduce the DeepImpact experiments in the following paper:

> Antonio Mallia, Omar Khattab, Nicola Tonellotto, and Torsten Suel. [Learning Passage Impacts for Inverted Indexes.](https://dl.acm.org/doi/10.1145/3404835.3463030) _SIGIR 2021_.

The guide has been integrated in [Anserini's regression framework](regressions-msmarco-passage-deepimpact.md), and this page has been reduced to a redirect stub.

---

Here, we start with a version of the MS MARCO passage corpus that has already been processed with DeepImpact, i.e., gone through document expansion and term reweighting.
Thus, no neural inference is involved.

Note that Pyserini provides [a comparable reproduction guide](https://github.com/castorini/pyserini/blob/master/docs/experiments-deepimpact.md), so if you don't like Java, you can get _exactly_ the same results from Python.

## Data Prep

```bash
# Alternate mirrors of the same data, pick one:
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-deepimpact-b8.tar -P collections/
wget https://vault.cs.uwaterloo.ca/s/57AE5aAjzw2ox2n/download -O collections/msmarco-passage-deepimpact-b8.tar

tar xvf collections/msmarco-passage-deepimpact-b8.tar -C collections/
```

To confirm, `msmarco-passage-deepimpact-b8.tar` is ~3.6 GB and has MD5 checksum `3c317cb4f9f9bcd3bbec60f05047561a`.

## Indexing

We can now index these docs as a `JsonVectorCollection` using Anserini:

```bash
sh target/appassembler/bin/IndexCollection -collection JsonVectorCollection \
 -input collections/msmarco-passage-deepimpact-b8/ \
 -index indexes/lucene-index.msmarco-passage.deepimpact-b8 \
 -generator DefaultLuceneDocumentGenerator -impact -pretokenized \
 -threads 18 -storeRaw
```

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the DeepImpact tokens.

Upon completion, we should have an index with 8,841,823 documents.
The indexing speed may vary; on a modern desktop with an SSD (using 12 threads, per above), indexing takes around 15 minutes.

## Retrieval

To ensure that the tokenization in the index aligns exactly with the queries, we use pre-tokenized queries.
The queries are already stored in the repo, so we can run retrieval directly:

```bash
target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.deepimpact-b8 \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.deepimpact.tsv.gz \
 -output runs/run.msmarco-passage.deepimpact-b8.tsv -format msmarco \
 -impact -pretokenized
```

Note that, mirroring the indexing options, we also specify `-impact -pretokenized` here.
Query evaluation is much slower than with bag-of-words BM25; a complete run takes around 30 minutes (on a single thread).

With `-format msmarco`, runs are already in the MS MARCO output format, so we can evaluate directly:

```bash
python tools/scripts/msmarco/msmarco_passage_eval.py \
   collections/msmarco-passage/qrels.dev.small.tsv runs/run.msmarco-passage.deepimpact-b8.tsv
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
