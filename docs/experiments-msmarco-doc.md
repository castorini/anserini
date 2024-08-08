# Anserini: BM25 Baselines for MS MARCO Document Ranking

This page contains instructions for running BM25 baselines on the [MS MARCO *document* ranking task](https://microsoft.github.io/msmarco/).
Note that there is a separate [MS MARCO *passage* ranking task](experiments-msmarco-passage.md).

As of July 2023, this exercise has been removed from the Waterloo students [onboarding path](https://github.com/lintool/guide/blob/master/ura.md), which [starts here](start-here.md).

## Data Prep

We're going to use the repository's root directory as the working directory.
First, we need to download and extract the MS MARCO document dataset:

```bash
mkdir collections/msmarco-doc

wget https://msmarco.blob.core.windows.net/msmarcoranking/msmarco-docs.trec.gz -P collections/msmarco-doc

# Alternative mirror:
# wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-docs.trec.gz -P collections/msmarco-doc
```

To confirm, `msmarco-docs.trec.gz` should have MD5 checksum of `d4863e4f342982b51b9a8fc668b2d0c0`.


## Indexing

There's no need to uncompress the file, as Anserini can directly index gzipped files.
Build the index with the following command:

```bash
target/appassembler/bin/IndexCollection \
  -collection CleanTrecCollection \
  -input collections/msmarco-doc \
  -index indexes/msmarco-doc/lucene-index-msmarco \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 \
  -storePositions -storeDocvectors -storeRaw
```

On a modern desktop with an SSD, indexing takes around 40 minutes.
There should be a total of 3,213,835 documents indexed.


## Retrieval

After indexing finishes, we can do a retrieval run.
The dev queries are already stored in our repo:

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/msmarco-doc/lucene-index-msmarco \
  -topics tools/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc.dev.bm25.txt \
  -parallelism 4 \
  -bm25 -hits 1000
```

Retrieval speed will vary by machine:
On a reasonably modern desktop with an SSD, with four threads (as specified above), the run takes less than five minutes.
Adjust the parallelism by changing the `-parallelism` argument.

After the run completes, we can evaluate with `trec_eval`:

```bash
$ target/appassembler/bin/trec_eval -c -mmap -mrecall.1000 \
    tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.dev.bm25.txt
map                   	all	0.2309
recall_1000           	all	0.8856
```

Let's compare to the baselines provided by Microsoft.
First, download:

```bash
wget https://msmarco.blob.core.windows.net/msmarcoranking/msmarco-docdev-top100.gz -P runs
gunzip runs/msmarco-docdev-top100.gz
```

Then, run `trec_eval` to compare.
Note that to be fair, we restrict evaluation to top 100 hits per topic (which is what Microsoft provides):

```bash
$ target/appassembler/bin/trec_eval -c -mmap -M 100 \
    tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/msmarco-docdev-top100
map                   	all	0.2219

$ target/appassembler/bin/trec_eval -c -mmap -M 100 \
    tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.dev.bm25.txt
map                   	all	0.2302
```

We see that "out of the box" Anserini is already better!

This dataset is part of the [MS MARCO Document Ranking Leaderboard](https://microsoft.github.io/MSMARCO-Document-Ranking-Submissions/leaderboard/).
Let's try to reproduce runs on there!

A few minor details to pay attention to: the official metric is MRR@100, so we want to only return the top 100 hits, and the submission files to the leaderboard have a slightly different format.

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/msmarco-doc/lucene-index-msmarco \
  -topics tools/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc.leaderboard-dev.bm25base.txt -format msmarco \
  -parallelism 4 \
  -bm25 -bm25.k1 0.9 -bm25.b 0.4 -hits 100
```

The command above uses the default BM25 parameters (`k1=0.9`, `b=0.4`), and note we set `-hits 100`.
Command for evaluation:

```bash
$ python tools/scripts/msmarco/msmarco_doc_eval.py \
    --judgments tools/topics-and-qrels/qrels.msmarco-doc.dev.txt \
    --run runs/run.msmarco-doc.leaderboard-dev.bm25base.txt
#####################
MRR @100: 0.23005723505603573
QueriesRanked: 5193
#####################
```

The above run corresponds to "Anserini's BM25, default parameters (k1=0.9, b=0.4)" on the leaderboard.

Here's the invocation for BM25 with parameters optimized for recall@100 (`k1=4.46`, `b=0.82`):

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/msmarco-doc/lucene-index-msmarco \
  -topics tools/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc.leaderboard-dev.bm25tuned.txt -format msmarco \
  -parallelism 4 \
  -bm25 -bm25.k1 4.46 -bm25.b 0.82 -hits 100
```

Command for evaluation:

```bash
$ python tools/scripts/msmarco/msmarco_doc_eval.py \
    --judgments tools/topics-and-qrels/qrels.msmarco-doc.dev.txt \
    --run runs/run.msmarco-doc.leaderboard-dev.bm25tuned.txt
#####################
MRR @100: 0.2770296928568702
QueriesRanked: 5193
#####################
```

More details on tuning BM25 parameters below...

## BM25 Tuning

It is well known that BM25 parameter tuning is important.
The setting of `k1=0.9`, `b=0.4` is often used as a default.

Let's try to do better!
We tuned BM25 using the queries found [here](https://github.com/castorini/anserini-data/tree/master/MSMARCO): these are five different sets of 10k samples from the training queries (using the `shuf` command).
The basic approach is grid search of parameter values in tenth increments.
We tuned on each individual set and then averaged parameter values across all five sets (this has the effect of regularization).
In separate trials, we optimized for:

+ recall@1000, since Anserini output serves as input to downstream rerankers (e.g., based on BERT), and we want to maximize the number of relevant documents the rerankers have to work with;
+ MRR@10, for the case where Anserini output is directly presented to users (i.e., no downstream reranking).

It turns out that optimizing for MRR@10 and MAP yields the same settings.

Here's the comparison between different parameter settings:

| Setting                                        | MRR@100 |    MAP | Recall@1000 |
|:-----------------------------------------------|--------:|-------:|------------:|
| Default (`k1=0.9`, `b=0.4`)                    |  0.2301 | 0.2310 |      0.8856 |
| Optimized for MRR@100/MAP (`k1=3.8`, `b=0.87`) |  0.2784 | 0.2789 |      0.9326 |
| Optimized for recall@100 (`k1=4.46`, `b=0.82`) |  0.2770 | 0.2775 |      0.9357 |

As expected, BM25 tuning makes a big difference!

Note that MRR@100 is computed with the leaderboard eval script (with 100 hits per query), while the other two metrics are computed with `trec_eval` (with 1000 hits per query).
So, we need to use different search programs, for example:

```bash
$ target/appassembler/bin/SearchCollection \
    -index indexes/msmarco-doc/lucene-index-msmarco \
    -topics tools/topics-and-qrels/topics.msmarco-doc.dev.txt \
    -topicReader TsvInt \
    -output runs/run.msmarco-doc.dev.opt-mrr.txt \
    -parallelism 4 \
    -bm25 -bm25.k1 3.8 -bm25.b 0.87 -hits 1000

$ target/appassembler/bin/trec_eval -c -mmap -mrecall.1000 \
    tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc.dev.opt-mrr.txt
map                   	all	0.2789
recall_1000           	all	0.9326

$ target/appassembler/bin/SearchCollection \
    -index indexes/msmarco-doc/lucene-index-msmarco \
    -topics tools/topics-and-qrels/topics.msmarco-doc.dev.txt \
    -topicReader TsvInt \
    -output runs/run.msmarco-doc.leaderboard-dev.opt-mrr.txt -format msmarco \
    -parallelism 4 \
    -bm25 -bm25.k1 3.8 -bm25.b 0.87 -hits 100

$ python tools/scripts/msmarco/msmarco_doc_eval.py \
    --judgments tools/topics-and-qrels/qrels.msmarco-doc.dev.txt \
    --run runs/run.msmarco-doc.leaderboard-dev.opt-mrr.txt
#####################
MRR @100: 0.27836767424339787
QueriesRanked: 5193
#####################
```

That's it!

## Reproduction Log[*](reproducibility.md)

+ Results reproduced by [@edwinzhng](https://github.com/edwinzhng) on 2020-01-14 (commit [`3964169`](https://github.com/castorini/anserini/commit/3964169bf82a3783f9298907d9794f0bddf306f0))
+ Results reproduced by [@nikhilro](https://github.com/nikhilro) on 2020-01-21 (commit [`631589e`](https://github.com/castorini/anserini/commit/631589e9e08326373f46555e007e6c302c19126d))
+ Results reproduced by [@yuki617](https://github.com/yuki617) on 2020-03-29 (commit [`074723c`](https://github.com/castorini/anserini/commit/074723cbb10660fb9be2bfe6325739ab5fe0dd8d))
+ Results reproduced by [@HangCui0510](https://github.com/HangCui0510) on 2020-04-23 (commit [`0ae567d`](https://github.com/castorini/anserini/commit/0ae567df5c8a70ac211efd958c9ca1ff609ff782))
+ Results reproduced by [@x65han](https://github.com/x65han) on 2020-04-25 (commit [`f5496b9`](https://github.com/castorini/anserini/commit/f5496b905246084070f959e59626c6323210c3f2))
+ Results reproduced by [@y276lin](https://github.com/y276lin) on 2020-04-26 (commit [`8f48f8e`](https://github.com/castorini/anserini/commit/8f48f8e40a37e5f6b5910a3a3b5c050a0f9be914))
+ Results reproduced by [@stephaniewhoo](http://github.com/stephaniewhoo) on 2020-04-26 (commit [`8f48f8e`](https://github.com/castorini/anserini/commit/8f48f8e40a37e5f6b5910a3a3b5c050a0f9be914))
+ Results reproduced by [@YimingDou](https://github.com/YimingDou) on 2020-05-14 (commit [`3b0a642`](https://github.com/castorini/anserini/commit/3b0a6420e49863d9fe5908cf6e99582eb2d2882e))
+ Results reproduced by [@richard3983](https://github.com/richard3983) on 2020-05-14 (commit [`a65646f`](https://github.com/castorini/anserini/commit/a65646fe203bf5c9c32189a56082d6f4d3bc340d))
+ Results reproduced by [@MXueguang](https://github.com/MXueguang) on 2020-05-20 (commit [`3b2751e`](https://github.com/castorini/anserini/commit/3b2751e2d02a9d530e1c3d30b91083faeece8982))
+ Results reproduced by [@shaneding](https://github.com/shaneding) on 2020-05-23 (commit [`b6e0367`](https://github.com/castorini/anserini/commit/b6e0367ef4e2b4fce9d81c8397ef1188e35971e7))
+ Results reproduced by [@kelvin-jiang](https://github.com/kelvin-jiang) on 2020-05-24 (commit [`b6e0367`](https://github.com/castorini/anserini/commit/b6e0367ef4e2b4fce9d81c8397ef1188e35971e7))
+ Results reproduced by [@adamyy](https://github.com/adamyy) on 2020-05-28 (commit [`a1ecfa4`](https://github.com/castorini/anserini/commit/a1ecfa4aa38fb8a0cf41575d47629ba1c69228fb))
+ Results reproduced by [@TianchengY](https://github.com/TianchengY) on 2020-05-28 (commit [`2947a16`](https://github.com/castorini/anserini/commit/2947a1622efae35637b83e321aba8e6fccd43489))
+ Results reproduced by [@stariqmi](https://github.com/stariqmi) on 2020-05-28 (commit [`4914305`](https://github.com/castorini/anserini/commit/455169ea6a09f637817a6c4b4f6837dcc845f5f7))
+ Results reproduced by [@justinborromeo](https://github.com/justinborromeo) on 2020-06-11 (commit[`7954eab`](https://github.com/castorini/anserini/commit/7954eab43f17bb8d254987d5873933c0b9596bb4))
+ Results reproduced by [@yxzhu16](https://github.com/yxzhu16) on 2020-07-03 (commit [`68ace26`](https://github.com/castorini/anserini/commit/68ace26d0418a769df3d2b21e946495e54d462f6))
+ Results reproduced by [@LizzyZhang-tutu](https://github.com/LizzyZhang-tutu) on 2020-07-13 (commit [`8c98d5b`](https://github.com/castorini/anserini/commit/8c98d5ba0795bbea01bcef1e21abb153fe4c3da1))
+ Results reproduced by [@estella98](https://github.com/estella98) on 2020-08-05 (commit [`99092a8`](https://github.com/castorini/anserini/commit/99092a82179d7efd38fc0b8c7c967137a40cd96f))
+ Results reproduced by [@tangsaidi](https://github.com/tangsaidi) on 2020-08-19 (commit [`aba846`](https://github.com/castorini/anserini/commit/aba846aa07d6f319fb3dc9cb591c20b4ae69f9ef))
+ Results reproduced by [@qguo96](https://github.com/qguo96) on 2020-09-07 (commit [`e16b3c1`](https://github.com/castorini/anserini/commit/e16b3c160664057d4e00f2b4030cb6cb0d32fabd))
+ Results reproduced by [@yuxuan-ji](https://github.com/yuxuan-ji) on 2020-09-08 (commit [`0f9a8ec`](https://github.com/castorini/anserini/commit/0f9a8ec4f335fb49c9387351745d1c755afc0e84))
+ Results reproduced by [@wiltan-uw](https://github.com/wiltan-uw) on 2020-09-09 (commit [`93d913f`](https://github.com/castorini/anserini/commit/93d913f2619c44451be3d46816c7b9c44cbeb091))
+ Results reproduced by [@JeffreyCA](https://github.com/JeffreyCA) on 2020-09-13 (commit [`bc2628b`](https://github.com/castorini/anserini/commit/bc2628b9916ce42b8026497c695d4c4198547f04))
+ Results reproduced by [@jhuang265](https://github.com/jhuang265) on 2020-10-15 (commit [`66711b9`](https://github.com/castorini/anserini/commit/66711b9ff7722e2aea4ce2f59ca26ead5c091cac))
+ Results reproduced by [@rayyang29](https://github.com/rayyang29) on 2020-10-27 (commit [`ad8cc5a`](https://github.com/castorini/anserini/commit/ad8cc5a02a53f09a83a8a6bfd7d187c9c3f96bd5))
+ Results reproduced by [@Dahlia-Chehata](https://github.com/Dahlia-Chehata) on 2020-11-12 (commit [`22c0ad3`](https://github.com/castorini/anserini/commit/22c0ad3ebcff22c34a69ee3a7c122c4a9fb27a0e))
+ Results reproduced by [@rakeeb123](https://github.com/rakeeb123) on 2020-12-07 (commit [`f50dcce`](https://github.com/castorini/anserini/commit/f50dcceb6cd0ec3403c1e77066aa51bb3275d24e))
+ Results reproduced by [@jrzhang12](https://github.com/jrzhang12) on 2021-01-02 (commit [`be4e44d`](https://github.com/castorini/anserini/commit/be4e44d5ac1e898469fc179f8e6a336234b23d93))
+ Results reproduced by [@HEC2018](https://github.com/HEC2018) on 2021-01-04 (commit [`4de21ec`](https://github.com/castorini/anserini/commit/4de21ece5e53cf20b4fcc711b575606b83c0d1f1))
+ Results reproduced by [@KaiSun314](https://github.com/KaiSun314) on 2021-01-08 (commit [`113f1c7`](https://github.com/castorini/anserini/commit/113f1c78c3ffc8681a06c571901cf9ad8f5ee633))
+ Results reproduced by [@yemiliey](https://github.com/yemiliey) on 2021-01-18 (commit [`179c242`](https://github.com/castorini/anserini/commit/179c242562bbb990e421f315370f34d4d19bbb9f))
+ Results reproduced by [@larryli1999](https://github.com/larryli1999) on 2021-01-22 (commit [`179c242`](https://github.com/castorini/anserini/commit/179c242562bbb990e421f315370f34d4d19bbb9f))
+ Results reproduced by [@ArthurChen189](https://github.com/ArthurChen189) on 2021-04-08 (commit [`45a5a21`](https://github.com/castorini/anserini/commit/45a5a219af92c82dd429f4d96aee13bd87825147))
+ Results reproduced by [@printfCalvin](https://github.com/printfCalvin) on 2021-04-11 (commit [`d808d4a`](https://github.com/castorini/anserini/commit/d808d4a50ee69f44493e19a87dc36c7eb99402a9))
+ Results reproduced by [@saileshnankani](https://github.com/saileshnankani) on 2021-04-26 (commit [`5781c87`](https://github.com/castorini/anserini/commit/5781c871db12f0e36139982fbf1c805cfec189ee))
+ Results reproduced by [@andrewyguo](https://github.com/andrewyguo) on 2021-04-29 (commit [`71f3ca6`](https://github.com/castorini/anserini/commit/71f3ca671faf6ddd7b0dea0a1e7f4b590a0a02a5))
+ Results reproduced by [@mayankanand007](https://github.com/mayankanand007) on 2021-05-04 (commit [`906ca50`](https://github.com/castorini/anserini/commit/906ca5064cfe97266b92868e537e9372ac558e93))
+ Results reproduced by [@Albert-Ma](https://github.com/Albert-Ma) on 2021-05-07 (commit [`5bcbccd`](https://github.com/castorini/anserini/commit/5bcbccdb8e67a1c6a1a74da1219fd344c9e80b0b))
+ Results reproduced by [@rootofallevii](https://github.com/RootofalleviI) on 2021-05-14 (commit [`626da95`](https://github.com/castorini/anserini/commit/626da950249ecc1519c9b07710d1243e0653e1c5))
+ Results reproduced by [@jpark621](https://github.com/jpark621) on 2021-06-01 (commit [`2591e06`](https://github.com/castorini/anserini/commit/2591e063b4bee8881a641cf2167352ac212865a6))
+ Results reproduced by [@nimasadri11](https://github.com/nimasadri11) on 2021-06-27 (commit [`6f9352f`](https://github.com/castorini/anserini/commit/6f9352fc5d6a4938fadc2bda9d0c428056eec5f0))
+ Results reproduced by [@mzzchy](https://github.com/mzzchy) on 2021-07-05 (commit [`589928b`](https://github.com/castorini/anserini/commit/589928b1873b3ebe00234becd8b5da9d573dda6d))
+ Results reproduced by [@d1shs0ap](https://github.com/d1shs0ap) on 2021-07-16 (commit [`43ad899`](https://github.com/castorini/anserini/commit/43ad899337ac5e3b219d899bb218c4bcae18b1e6))
+ Results reproduced by [@apokali](https://github.com/apokali) on 2021-08-19 (commit[`ad4caeb`](https://github.com/castorini/anserini/commit/ad4caeb59ec512d0ce07412e6c4b873a8b841da4))
+ Results reproduced by [@leungjch](https://github.com/leungjch) on 2021-09-12 (commit [`f79fb67`](https://github.com/castorini/anserini/commit/f79fb67845b4b68b8c177eacb5832c209847dc29))
+ Results reproduced by [@AlexWang000](https://github.com/AlexWang000) on 2021-10-10 (commit [`fc2ddb0`](https://github.com/castorini/anserini/commit/fc2ddb026677d2695fa4a1e9cfdd5608cb0ac26b))
+ Results reproduced by [@ToluClassics](https://github.com/ToluClassics) on 2021-10-20 (commit [`fcc2aff`](https://github.com/castorini/anserini/commit/fcc2aff950edc8e81ad32776418288da6a4dbaf8))
+ Results reproduced by [@manveertamber](https://github.com/manveertamber) on 2021-12-05 (commit [`aee51ad`](https://github.com/castorini/anserini/commit/aee51adefe9d2b8f178df37abc5b236b185c5bab))
+ Results reproduced by [@lingwei-gu](https://github.com/lingwei-gu) on 2021-12-15 (commit [`30605f5`](https://github.com/castorini/anserini/commit/30605f535192befdf59c2f330decd3656315ffaa))
+ Results reproduced by [@tyao-t](https://github.com/tyao-t) on 2021-12-18 (commit [`6500560`](https://github.com/castorini/anserini/commit/65005606ec6ccd2d337c8dd150cc030d14b0aca9))
+ Results reproduced by [@kevin-wangg](https://github.com/kevin-wangg) on 2022-01-04 (commit [`c3e14dc`](https://github.com/castorini/anserini/commit/c3e14dcda516455e2daa4ffe10fb9900c4a8fc12))
+ Results reproduced by [@vivianliu0](https://github.com/vivianliu0) on 2022-01-06 (commit [`c3e14dc`](https://github.com/castorini/anserini/commit/c3e14dcda516455e2daa4ffe10fb9900c4a8fc12))
+ Results reproduced by [@mikhail-tsir](https://github.com/mikhail-tsir) on 2022-01-07 (commit [`806ac89`](https://github.com/castorini/anserini/commit/806ac896a4a5531f0a39dafb79d481e679c7dc19))
+ Results reproduced by [@AceZhan](https://github.com/AceZhan) on 2022-01-14 (commit [`7ff99e0`](https://github.com/castorini/anserini/commit/7ff99e0d3208dc8bfec6bb8ca254d0b016015a2d))
+ Results reproduced by [@jh8liang](https://github.com/jh8liang) on 2022-02-06 (commit [`5cdf9ec`](https://github.com/castorini/anserini/commit/5cdf9ec3600bf693cf8d80a9c869444b4e29ff75))
+ Results reproduced by [@mayankanand007](https://github.com/mayankanand007) on 2022-02-22 (commit [`6a70804`](https://github.com/castorini/anserini/commit/6a708047f71528f7d516c0dd45485204a36e6b1d))
+ Results reproduced by [@jasper-xian](https://github.com/jasper-xian) on 2022-03-27 (commit [`2e8e9fd`](https://github.com/castorini/anserini/commit/2e8e9fdfedc1b81522d9e22a805e08e6a7105762))
+ Results reproduced by [@jx3yang](https://github.com/jx3yang) on 2022-04-25 (commit [`b429218`](https://github.com/castorini/anserini/commit/b429218e52a385eabf3fd81979e221111fbc4a19))
+ Results reproduced by [@AreelKhan](https://github.com/AreelKhan) on 2022-04-27 (commit [`7adee1d`](https://github.com/castorini/anserini/commit/7adee1d9596f052a87b0427e654c4b95e2cd5e89))
+ Results reproduced by [@alvind1](https://github.com/alvind1) on 2022-05-05 (commit [`9b2dd5f5`](https://github.com/castorini/anserini/commit/9b2dd5f5e524ce56e5784cb73404d39926982733))
+ Results reproduced by [@Pie31415](https://github.com/Pie31415) on 2022-06-22 (commit [`6aef2eb`](https://github.com/castorini/anserini/commit/6aef2eb0681f34387bf04077465f04343c338cf4))
+ Results reproduced by [@aivan6842](https://github.com/aivan6842) on 2022-07-11 (commit [`8010d5c`](https://github.com/castorini/anserini/commit/8010d5c0b066f0316c6c506170274f8f7d558f73))
+ Results reproduced by [@Jasonwu-0803](https://github.com/Jasonwu-0803) on 2022-09-27 (commit [`b5ecc5a`](https://github.com/castorini/anserini/commit/b5ecc5aff79ddfc82b175f6bd3048f5039f0480f))
+ Results reproduced by [@limelody](https://github.com/limelody) on 2022-09-27 (commit [`252b5e2`](https://github.com/castorini/anserini/commit/252b5e2087dd7b3b994d41a444d4ae0044519819))
+ Results reproduced by [@minconszhang](https://github.com/minconszhang) on 2022-11-25 (commit [`6556550`](https://github.com/castorini/anserini/commit/6556550fe33f7804993aaf10201da9900d52c14b))
+ Results reproduced by [@jingliu](https://github.com/ljatca) on 2022-12-08 (commit [`6872c87`](https://github.com/castorini/anserini/commit/6872c878d6969ebdf1875e4436777b95746b35a5))
+ Results reproduced by [@farazkh80](https://github.com/farazkh80) on 2022-12-18 (commit [`4527a5d`](https://github.com/castorini/anserini/commit/4527a5d0555e5def53855a0f2fd3bb90f53ea7e9))
+ Results reproduced by [@Cath](https://github.com/Cathrineee) on 2023-01-14 (commit [`732cba4`](https://github.com/castorini/anserini/commit/732cba4775312f606888778d1e705e1ccea926df))
+ Results reproduced by [@dlrudwo1269](https://github.com/dlrudwo1269) on 2023-03-07 (commit [`4b7662c7`](https://github.com/castorini/anserini/commit/4b7662c7f154b9fbfe68aec9c371d8204d9ed31e))
+ Results reproduced by [@aryamancodes](https://github.com/aryamancodes) on 2023-04-11 (commit [`ed89401`](https://github.com/castorini/anserini/commit/ed894015977347e6e04363dea67d19e5474848ee))
+ Results reproduced by [@Jocn2020](https://github.com/Jocn2020) on 2023-04-30 (commit [`30269d6`](https://github.com/castorini/anserini/commit/30269d636a556174e3756fe59accab6c22a7c732))
+ Results reproduced by [@zoehahaha](https://github.com/zoehahaha) on 2023-05-12 (commit [`b429218`](https://github.com/castorini/anserini/commit/b429218e52a385eabf3fd81979e221111fbc4a19))
+ Results reproduced by [@billcui57](https://github.com/billcui57) on 2023-05-14 (commit [`d82b6f7`](https://github.com/billcui57/anserini/commit/d82b6f76cbef009bccb0e67fc2bedc6fa6ae56c6))
+ Results reproduced by [@Richard5678](https://github.com/richard5678) on 2023-06-13 (commit [`4aeb3ef`](https://github.com/castorini/anserini/commit/4aeb3ef64738e780f710eaf2d0ed957bec4f6ede))
+ Results reproduced by [@pratyushpal](https://github.com/pratyushpal) on 2023-07-14 (commit [`17d5fc7`](https://github.com/castorini/anserini/commit/17d5fc7f338b511c4dc49de88e9b2ab7ea27f8aa))
+ Results reproduced by [@Mofetoluwa](https://github.com/Mofetoluwa) on 2023-08-04 (commit [`7314128`](https://github.com/castorini/anserini/commit/73141282b62979e189ac3c87d9a902064f34a1c5))
+ Results reproduced by [@ErrenYeager](https://github.com/ErrenYeager) on 2023-09-02 (commit [`4ae518b`](https://github.com/castorini/anserini/commit/4ae518bb284ebcba0b273a473bc8774735cb7d19))
+ Results reproduced by [@Minhajul99](https://github.com/Minhajul99) on 2023-12-09 (commit [`f1d6320`](https://github.com/Minhajul99/anserini/commit/f1d6320a0002faeec28f3262cc5bf982c992503b))
+ Results reproduced by [@MariaPonomarenko38](https://github.com/MehrnazSadeghieh) on 2024-07-19 (commit [`1f750c6`](https://github.com/castorini/anserini/commit/1f750c67f42f02820b0a75237579e34a8c676248))
