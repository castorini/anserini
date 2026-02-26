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
+ Results reproduced by [@mindlesstruffle](https://github.com/mindlesstruffle) on 2025-07-09 (commit [`b3ab936`](https://github.com/castorini/anserini/commit/b3ab936b03e8af2e80be7bde861945c1920553f3))
+ Results reproduced by [@niruhan](https://github.com/niruhan) on 2025-07-17 (commit [`d6a8b36`](https://github.com/niruhan/anserini/commit/d6a8b36a6bc9a62b70d44412f6ebb2ca0bc709cd))
+ Results reproduced by [@br0mabs](https://github.com/br0mabs) on 2025-07-19 (commit [`b3ab936`](https://github.com/castorini/anserini/commit/b3ab936b03e8af2e80be7bde861945c1920553f3))
+ Results reproduced by [@bikram993298](https://github.com/bikram993298) on 2025-08-19 (commit [`c6ea078`](https://github.com/castorini/anserini/commit/c6ea078417e318e19fc868a5a911849067f80e10))
+ Results reproduced by [@JoshElkind](https://github.com/JoshElkind) on 2025-08-24 (commit [`7c3010f`](https://github.com/castorini/anserini/commit/7c3010fbda4618bea07ea372017e9e1e604f3d8b))
+ Results reproduced by [@Dinesh7K](https://github.com/Dinesh7K) on 2025-09-03 (commit [`e7cb101`](https://github.com/castorini/anserini/commit/e7cb101ed451b3595c74c4502632aa708605fb07))
+ Results reproduced by [@CereNova](https://github.com/CereNova) on 2025-09-05 (commit [`b01c121`](https://github.com/castorini/anserini/commit/b01c1218aa199b8465327bc0be39bc7912642efb))
+ Results reproduced by [@NathanNCN](https://github.com/NathanNCN) on 2025-09-06 (commit [`9e39f94`](https://github.com/castorini/anserini/commit/9e39f9463227b0100e0bbc4552895a394993754f))
+ Results reproduced by [@ShivamSingal](https://github.com/ShivamSingal) on 2025-09-07 (commit [`f59c8ee`](https://github.com/castorini/anserini/commit/f59c8ee05f5b61370874075c970f18c43ca41e37))
+ Results reproduced by [@FarmersWrap](https://github.com/FarmersWrap) on 2025-09-10 (commit [`676af03`](https://github.com/castorini/anserini/commit/676af034e8481d043880d22f8f2390ac56b8736d))
+ Results reproduced by [@shreyaadritabanik](https://github.com/shreyaadritabanik) on 2025-09-10 (commit [`3112afd`](https://github.com/castorini/anserini/commit/3112afd2eff0997b8fe6a3ad7ead20cd8f81ec09))
+ Results reproduced by [@k464wang](https://github.com/k464wang) on 2025-09-18 (commit [`df3b06c`](https://github.com/castorini/anserini/commit/df3b06c375f42b7ac85ef5d9edb74273428956e0))
+ Results reproduced by [@mahdi-behnam](https://github.com/mahdi-behnam) on 2025-09-19 (commit [`6799a7d`](https://github.com/castorini/anserini/commit/6799a7d5ebca4c332d38bcafbc3a9a938a38d7e2))
+ Results reproduced by [@InanSyed](https://github.com/InanSyed) on 2025-09-23 (commit [`52265a6`](https://github.com/castorini/anserini/commit/52265a6a10b9aea45baddc28b560ea705407d2c1))
+ Results reproduced by [@rashadjn](https://github.com/rashadjn) on 2025-09-19 (commit [`a92e25c`](https://github.com/castorini/anserini/commit/a92e25c0775cec601776f15154f85d69dac62108))
+ Results reproduced by [@samin-mehdizadeh](https://github.com/samin-mehdizadeh) on 2025-09-27 (commit [`a92e25c`](https://github.com/castorini/anserini/commit/a92e25c0775cec601776f15154f85d69dac62108))
+ Results reproduced by [@AniruddhThakur](https://github.com/AniruddhThakur) on 2025-09-27 (commit [`eeb7756`](https://github.com/castorini/anserini/commit/eeb775657e7fd4a6d70ad303b9f45b1b48f48a49))
+ Results reproduced by [@prav0761](https://github.com/prav0761) on 2025-10-13 (commit [`4a2f9a0`](https://github.com/castorini/anserini/commit/4a2f9a0022d50c743b3bb7f983c9c605d77fcb29))
+ Results reproduced by [@henry4516](https://github.com/henry4516) on 2025-10-14 (commit [`338ac0e`](https://github.com/castorini/anserini/commit/338ac0e333204a7cb2bb625be11ce6846ff8f170))
+ Results reproduced by [@yazdanzv](https://github.com/yazdanzv) on 2025-10-15 (commit [`e011b38`](https://github.com/castorini/anserini/commit/e011b386423df6e089efae6210e19bc1abbca317))
+ Results reproduced by [@royary](https://github.com/royary) on 2025-10-23 (commit [`7302ce9`](https://github.com/castorini/anserini/commit/7302ce9d9e832d2d7158e3000973e1a99bf23a24))
+ Results reproduced by [@Raptors65](https://github.com/Raptors65) on 2025-10-23 (commit [`7302ce9`](https://github.com/castorini/anserini/commit/7302ce9d9e832d2d7158e3000973e1a99bf23a24))
+ Results reproduced by [@LiHuua258](https://github.com/LiHuua258) on 2025-10-24 (commit [`7302ce9`](https://github.com/castorini/anserini/commit/7302ce9d9e832d2d7158e3000973e1a99bf23a24))
+ Results reproduced by [@RichHene](https://github.com/RichHene) on 2025-10-24 (commit [`7302ce9`](https://github.com/castorini/anserini/commit/7302ce9d9e832d2d7158e3000973e1a99bf23a24))
+ Results reproduced by [@ivan-0862](https://github.com/ivan-0862) on 2025-10-25 (commit [`7fc1b57`](https://github.com/castorini/anserini/commit/7fc1b57c2a11ad605ae26e9609bd5da451a6430d))
+ Results reproduced by [@brandonzhou2002](https://github.com/brandonzhou2002) on 2025-10-26 (commit [`5c7d986`](https://github.com/castorini/anserini/commit/5c7d9866918f7ec45bf9ba4ccc60d589b6ea244c))
+ Results reproduced by [@MahdiNoori2003](https://github.com/MahdiNoori2003) on 2025-10-28 (commit [`5c7d986`](https://github.com/castorini/anserini/commit/5c7d9866918f7ec45bf9ba4ccc60d589b6ea244c))
+ Results reproduced by [@minj22](https://github.com/minj22) on 2025-11-04 (commit [`c274dd8`](https://github.com/castorini/anserini/commit/c274dd8674ca2289c840e2a7844fd33afd2a7bbb))
+ Results reproduced by [@ipouyall](https://github.com/ipouyall) on 2025-11-05 (commit [`82dfba0`](https://github.com/castorini/anserini/commit/82dfba06fa8e96d8380aa4fcd3f5782c321c70b7))
+ Results reproduced by [@AdrianGri](https://github.com/adriangri) on 2025-11-12 (commit [`c19077b`](https://github.com/castorini/anserini/commit/c19077b36a471263742cf63ac4d9b9ce57b7118d))
+ Results reproduced by [@jianxyou](https://github.com/jianxyou) on 2025-11-17 (commit [`9406dd8`](https://github.com/castorini/anserini/commit/9406dd893e02922cf2b690a8fabf181a14d36bf4))
+ Results reproduced by [@xincanfeng](https://github.com/xincanfeng) on 2025-11-18 (commit [`9406dd8`](https://github.com/castorini/anserini/commit/9406dd893e02922cf2b690a8fabf181a14d36bf4))
+ Results reproduced by [@Blank9999](https://github.com/Blank9999) on 2025-11-18 (commit [`9406dd8`](https://github.com/castorini/anserini/commit/9406dd893e02922cf2b690a8fabf181a14d36bf4))
+ Results reproduced by [@ball2004244](https://github.com/ball2004244) on 2025-11-23 (commit [`9aea5f3`](https://github.com/castorini/anserini/commit/9aea5f357c4d1ff50bd8cdf4594c035631ca73a5))
+ Results reproduced by [@Hasebul21](https://github.com/Hasebul21) on 2025-11-27 (commit [`9aea5f3`](https://github.com/castorini/anserini/commit/9aea5f357c4d1ff50bd8cdf4594c035631ca73a5))
+ Results reproduced by [@RudraMantri123](https://github.com/RudraMantri123) on 2025-11-28 (commit [`9aea5f3`](https://github.com/castorini/anserini/commit/9aea5f357c4d1ff50bd8cdf4594c035631ca73a5))
+ Results reproduced by [@imishrr](https://www.github.com/imishrr) on 2025-12-01 (commit [`79e7777`](https://www.github.com/castorini/anserini/commit/79e77779dcf02c7e69b6e9869fc3c92005e5f2a4))
+ Results reproduced by [@MehdiJmlkh](https://github.com/MehdiJmlkh) on 2025-12-08 (commit [`259d483`](https://github.com/castorini/anserini/commit/259d483628a7ae97995be05ee23d168b308238b5))
+ Results reproduced by [@Kushion32](https://github.com/Kushion32) on 2025-12-09 (commit [`3e65fbd`](https://github.com/castorini/anserini/commit/3e65fbd9227d32bbb343ec1ff0ccaba43915dd4f))
+ Results reproduced by [@anjanpa](https://github.com/anjanpa) on 2025-12-17 (commit [`1d062ef`](https://github.com/castorini/anserini/commit/1d062ef7461b0da9dd861b5385aa4bbcf61bb272))
+ Results reproduced by [@MuhammadAli13562](https://github.com/MuhammadAli13562) on 2025-12-18 (commit [`68311a1`](https://github.com/castorini/anserini/commit/68311a10242c77bccc0fa97200b0627f0e223767))
+ Results reproduced by [@Hossein-Molaeian](https://github.com/Hossein-Molaeian) on 2025-12-19 (commit [`b64fba2`](https://github.com/castorini/anserini/commit/b64fba2b603051abc1b2530432c29a367f7e769f))
+ Results reproduced by [@FayizMohideen](https://github.com/FayizMohideen) on 2025-12-21 (commit [`1c5cd32`](https://github.com/castorini/anserini/commit/1c5cd32b48f03f63eb5752834600ad7c17e5fe7d))
+ Results reproduced by [@nli33](https://github.com/nli33) on 2025-12-22 (commit [`1c5cd32`](https://github.com/castorini/anserini/commit/1c5cd32b48f03f63eb5752834600ad7c17e5fe7d))
+ Results reproduced by [@VarnitOS](https://github.com/VarnitOS) on 2025-12-26 (commit [⁠ 1c5cd32 ⁠](https://github.com/castorini/anserini/commit/1c5cd32b48f03f63eb5752834600ad7c17e5fe7d))
+ Results reproduced by [@zizimind](https://github.com/zizimind) on 2026-01-06 (commit [`d276b57`](https://github.com/castorini/anserini/commit/d276b57e1a5b1d1ba63558588ae88d90190258c3))
+ Results reproduced by [@izzat5233](https://github.com/izzat5233) on 2026-01-17 (commit [`5bda670`](https://github.com/castorini/anserini/commit/5bda6701ebe8cc217ffc66a600d3583671fe299d))
+ Results reproduced by [@HusamIsied](https://github.com/HusamIsied) on 2026-01-25 (commit [`952ac5e4`](https://github.com/castorini/anserini/commit/952ac5e4573486a255778828b8f26fd892cda854))
+ Results reproduced by [@aaryanshroff](https://github.com/aaryanshroff) on 2026-01-26 (commit [`952ac5e`](https://github.com/castorini/anserini/commit/952ac5e4573486a255778828b8f26fd892cda854))
+ Results reproduced by [@maherapp](https://github.com/maherapp) on 2026-02-01 (commit [`f0ecf565`](https://github.com/castorini/anserini/commit/f0ecf5655430b3fdccb802cde31e7f8ef821d0de))
+ Results reproduced by [@Karrrthik](https://github.com/Karrrthik) on 2026-02-20 (commit [`792d9cb`](https://github.com/castorini/anserini/commit/792d9cbccf23507d164d63bc1a2a39b065777122))
