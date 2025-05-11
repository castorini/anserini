# Anserini: Dense Retrieval for MS MARCO Passage Ranking

If you're a Waterloo student traversing the [onboarding path](https://github.com/lintool/guide/blob/master/ura.md), [start here](start-here.md).
In general, don't try to rush through this guide by just blindly copying and pasting commands into a shell;
that's what I call [cargo culting](https://en.wikipedia.org/wiki/Cargo_cult_programming).
Instead, really try to understand what's going on.

**Learning outcomes** for this guide, building on previous lessons in the onboarding path:

+ Be able to use Anserini prebuilt indexes to skip indexing, for both BM25 and dense retrieval.
+ Be able to use Anserini to perform a batch retrieval run using a dense retrieval model.

## Repeating Retrieval with Prebuilt Indexes

In the [previous lesson](experiments-msmarco-passage.md), you learned that indexing and retrieval are two distinct phases.
Indexing only needs to be done once, and once it's done we can perform retrieval on as many queries as we'd like.
Of course, if the document collection changes, we'll need to modify the index, but nearly all collections used for research are static.

This is where prebuilt indexes come in:
Instead of making everyone build their own indexes, we can directly share indexes that have already been built.
Take the time to read [this guide about prebuilt documents](prebuilt-indexes.md).

Here's the same retrieval run that you've done before, on the MS MARCO passage collection with the dev queries, but now using a prebuilt index:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index msmarco-v1-passage \
  -topics collections/msmarco-passage/queries.dev.small.tsv \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.dev.bm25.txt \
  -parallelism 4 \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -hits 1000
```

Instead of passing the path to an index in the `-index` parameter, we specify the name of an index that Anserini already "knows about".
Anserini downloads the index from a known location on UWaterloo servers, and stores a copy in `~/.cache/pyserini/indexes`.
Go ahead and confirm it's there.

The complete list of prebuilt indexes (and where to find them) is in the class [`IndexInfo`](https://github.com/castorini/anserini/blob/master/src/main/java/io/anserini/index/IndexInfo.java).

We can then evaluate the run with the `trec_eval` tool.
Let's compute the MRR@10 score, which is the official metric:

```bash
bin/trec_eval -c -M 10 -m recip_rank \
  collections/msmarco-passage/qrels.dev.small.trec \
  runs/run.msmarco-passage.dev.bm25.txt
```

The MRR@10 should be 0.1875.

There's a _tiny_ bit of difference between this result and the one from the previous lesson.
Previously, we used `-format msmarco` to generate the output in the MS MARCO format, which we then converted into the TREC format before evaluating.
This conversion is lossy and causes slight score differences due to tie-breaking effects (i.e., what happens when two documents are tied in terms of score).

## Retrieval with Dense Indexes

Next, we're going to look at retrieval using dense vector representations (or just dense vectors).
This is also called dense retrieval or vector search.
We'll learn more about how they work later in the onboarding path in Pyserini, but for now, let's perform a retrieval run, using the same queries on the same collection.

Here, we are using the BGE-base model.
Retrieval using dense vectors requires different indexes.
In this case, we're using what is known as an HNSW index (in contrast to the inverted index that you used for BM25).

Putting everything together, the complete retrieval command is:

```
bin/run.sh io.anserini.search.SearchHnswDenseVectors \
  -index msmarco-v1-passage.bge-base-en-v1.5.hnsw \
  -topics collections/msmarco-passage/queries.dev.small.tsv \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.dev.bge.txt \
  -encoder BgeBaseEn15 -hits 1000 -threads 4
```

Note: If you encounter encoder loading errors, clear the cache at `~/.cache/pyserini/encoders`.

Instead of `SearchCollection`, we use `SearchHnswDenseVectors` since it's a different type of index.
We are using a prebuilt index, specified as `-index msmarco-v1-passage.bge-base-en-v1.5.hnsw`.
The above retrieval command automatically downloads the HNSW index for the MS MARCO passage collection.
Beware, it's 26 GB.

For reference, on a circa 2022 MacBook Air with an Apple M2 processor and 24 GB RAM, the retrieval run takes around 4 minutes.

Let's compute the MRR@10 score:

```
bin/trec_eval -c -M 10 -m recip_rank \
  collections/msmarco-passage/qrels.dev.small.trec \
  runs/run.msmarco-passage.dev.bge.txt
```

You should get a score of 0.3521, which is much higher than the 0.1874 score from BM25.
Yes, dense retrieval is better.


## Wrapping Up

As a next step in the onboarding path, you basically [do the same thing again in Python with Pyserini](https://github.com/castorini/pyserini/blob/master/docs/experiments-msmarco-passage.md) (as opposed to Java with Anserini here).

Before you move on, however, add an entry in the "Reproduction Log" at the bottom of this page, following the same format: use `yyyy-mm-dd`, make sure you're using a commit id that's on the main trunk of Anserini, and use its 7-hexadecimal prefix for the link anchor text.
In the description of your pull request, please provide some details on your setup (e.g., operating system, environment and configuration, etc.).
In addition, also provide some indication of success (e.g., everything worked) or document issues you encountered.
If you think this guide can be improved in any way (e.g., you caught a typo or think a clarification is warranted), feel free to include it in the pull request.

## Reproduction Log[*](reproducibility.md)

+ Results reproduced by [@b8zhong](https://github.com/b8zhong) on 2025-02-23 (commit [`daceb40`](https://github.com/castorini/anserini/commit/daceb4084c8e8103e3e86c81a8e0d597d409220e))
+ Results reproduced by [@lilyjge](https://github.com/lilyjge) on 2025-02-23 (commit [`9b13fe4`](https://github.com/castorini/anserini/commit/9b13fe488d3227ba3a271366210eadfed521d0f5))
+ Results reproduced by [@JJGreen0](https://github.com/JJGreen0) on 2025-04-19 (commit [`2d8674c`](https://github.com/castorini/anserini/commit/2d8674c0cd741e1c407e0ac7cce8ea38fdd0bb97))
+ Results reproduced by [@ricky42613](https://github.com/ricky42613) on 2025-04-25 (commit [`adce4e3`](https://github.com/castorini/anserini/commit/adce4e30cc9abce3dc2afdf2f6d7694a447a071a))
+ Results reproduced by [@lzguan](https://github.com/lzguan) on 2025-04-30 (commit [`4d94f55`](https://github.com/castorini/anserini/commit/4d94f5533d05f882a1677f84c5af5de078739be6))
+ Results reproduced by [@Yaohui2019](https://github.com/Yaohui2019) on 2025-05-02 (commit [`4d94f55`](https://github.com/castorini/anserini/commit/4d94f5533d05f882a1677f84c5af5de078739be6))
+ Results reproduced by [@karush17](https://github.com/karush17) on 2025-05-09 (commit [`b21b7da`](https://github.com/castorini/anserini/commit/b21b7da1141148df7f479f0c23ee4532d5c53838))
+ Results reproduced by [@YousefNafea](https://github.com/YousefNafea) on 2025-05-10 (commit [`b21b7da`](https://github.com/castorini/anserini/commit/b21b7da1141148df7f479f0c23ee4532d5c53838))
