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
+ Results reproduced by [@AnthonyZ0425](https://github.com/AnthonyZ0425) on 2025-05-13 (commit [`83b7b45`](https://github.com/castorini/anserini/commit/83b7b45d36ffb114abe72a2db42800212bcec190))
+ Results reproduced by [@luisvenezian](https://github.com/luisvenezian) on 2025-05-15 (commit [`bd4c3c7`](https://github.com/castorini/pyserini/commit/74dce4f0fde6b82f22d3ba6a2a798ac4d8033f66))
+ Results reproduced by [@MINGYISU](https://github.com/MINGYISU) on 2025-05-14 (commit [`bd4c3c7`](https://github.com/castorini/anserini/commit/bd4c3c78823e26bf5ea2ae81a89ab69e1b630575))
+ Results reproduced by [@Armd04](https://github.com/Armd04) on 2025-05-16 (commit [`bd4c3c7`](https://github.com/castorini/anserini/commit/bd4c3c78823e26bf5ea2ae81a89ab69e1b630575))
+ Results reproduced by [@Cassidy-Li](https://github.com/Cassidy-Li) on 2025-05-20 (commit [`a6fe05c`](https://github.com/castorini/anserini/commit/a6fe05ccd6921c5241ea717146ac37ce1eabc8b2))
+ Results reproduced by [@James-Begin](https://github.com/James-Begin) on 2025-05-21 (commit [`a6fe05c`](https://github.com/castorini/anserini/commit/a6fe05ccd6921c5241ea717146ac37ce1eabc8b2))
+ Results reproduced by [@Roselynzzz](https://github.com/Roselynzzz) on 2025-05-26 (commit [`ef25129`](https://github.com/castorini/anserini/commit/ef2512948a40550f9ff1121ecb785fd74b3ebad4))
+ Results reproduced by [@AnnieZhang2](https://github.com/AnnieZhang2) on 2025-05-28 (commit [`bd4c3c7`](https://github.com/castorini/anserini/commit/bd4c3c78823e26bf5ea2ae81a89ab69e1b630575))
+ Results reproduced by [@Vik7am10](https://github.com/Vik7am10) on 2025-06-03 (commit [`b216a5f`](https://github.com/castorini/anserini/commit/b216a5f715f3a6e947389459fef3c2711b85b46e))
+ Results reproduced by [@kevin-zkc](https://github.com/kevin-zkc) on 2025-06-05 (commit [`173312d`](https://github.com/castorini/anserini/commit/173312d7798c343b3cc1d7a3988b213b044eda82))
+ Results reproduced by [@YuvaanshKapila](https://github.com/YuvaanshKapila) on 2025-06-08 (commit [`17bd9ac`](https://github.com/castorini/anserini/commit/17bd9acff9109589e6f9d3bfd0a7e867577930cd))
+ Results reproduced by [@erfan-yazdanparast](https://github.com/erfan-yazdanparast) on 2025-06-08 (commit [`2201471`](https://github.com/castorini/anserini/commit/22014714d2aa12229cfecdb87005f28db66f9caa))
+ Results reproduced by [@nahalhz](https://github.com/nahalhz) on 2025-06-09 (commit [`c07454b`](https://github.com/castorini/anserini/commit/c07454b7c64422789834314d97348907c8c66842))
+ Results reproduced by [@sadlulu](https://github.com/sadlulu) on 2025-06-17 (commit [`2e6a58b`](https://github.com/castorini/anserini/commit/2e6a58b0bae319e4eaaa026bdf81ab74f1ee8360))
+ Results reproduced by [@goodzcyabc](https://github.com/goodzcyabc) on 2025-06-20 (commit [`c07454b`](https://github.com/castorini/anserini/commit/c07454b7c64422789834314d97348907c8c66842))
+ Results reproduced by [@adefioye](https://github.com/adefioye) on 2025-06-26 (commit [`0299e2a`](https://github.com/castorini/anserini/commit/0299e2af610087ceb87331e6df0b9e0962a778df))
+ Results reproduced by [@hari495](https://github.com/hari495) on 2025-06-31 (commit [`0299e2a`](https://github.com/castorini/anserini/commit/0299e2af610087ceb87331e6df0b9e0962a778df))
+ Results reproduced by [@suraj-subrahmanyan](https://github.com/suraj-subrahmanyan) on 2025-07-02 (commit [`017841f`](https://github.com/castorini/anserini/commit/017841f33794508f760d26f58dedb5c770c1cbfc))
+ Results reproduced by [@ed-ward-huang](https://github.com/ed-ward-huang) on 2025-07-06 (commit [`4039c30`](https://github.com/castorini/anserini/commit/4039c3054c961e80dc1562899609396142bc869b))
+ Results reproduced by [@OmarKhaled0K](https://github.com/OmarKhaled0K) on 2025-07-08 (commit [`dafa81e`](https://github.com/castorini/anserini/commit/dafa81e63ff4f21479cf65357c157d9e9763b3d9))
+ Results reproduced by [@mindlesstruffle](https://github.com/mindlesstruffle) on 2025-07-09 (commit [`b5d4838`](https://github.com/castorini/anserini/commit/b5d48381c171e0ac36cd0c2523fe77b7bfe45435))