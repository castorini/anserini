# Anserini: BM25 Baselines for MS MARCO Passage Ranking

This page contains instructions for running BM25 baselines on the [MS MARCO *passage* ranking task](https://microsoft.github.io/msmarco/).
Note that there is a separate [MS MARCO *document* ranking task](experiments-msmarco-doc.md).
We also have a [separate page](experiments-doc2query.md) describing document expansion experiments (Doc2query) for this task.

## Data Prep

We're going to use the repository's root directory as the working directory.
First, we need to download and extract the MS MARCO passage dataset:

```bash
mkdir collections/msmarco-passage

wget https://msmarco.blob.core.windows.net/msmarcoranking/collectionandqueries.tar.gz -P collections/msmarco-passage

# Alternative mirror:
# wget https://www.dropbox.com/s/9f54jg2f71ray3b/collectionandqueries.tar.gz -P collections/msmarco-passage

tar xvfz collections/msmarco-passage/collectionandqueries.tar.gz -C collections/msmarco-passage
```

To confirm, `collectionandqueries.tar.gz` should have MD5 checksum of `31644046b18952c1386cd4564ba2ae69`.

Next, we need to convert the MS MARCO tsv collection into Anserini's jsonl files (which have one json object per line):

```bash
python tools/scripts/msmarco/convert_collection_to_jsonl.py \
 --collection-path collections/msmarco-passage/collection.tsv \
 --output-folder collections/msmarco-passage/collection_jsonl
```

The above script should generate 9 jsonl files in `collections/msmarco-passage/collection_jsonl`, each with 1M lines (except for the last one, which should have 841,823 lines).

We can now index these docs as a `JsonCollection` using Anserini:

```bash
sh target/appassembler/bin/IndexCollection -threads 9 -collection JsonCollection \
 -generator DefaultLuceneDocumentGenerator -input collections/msmarco-passage/collection_jsonl \
 -index indexes/msmarco-passage/lucene-index-msmarco -storePositions -storeDocvectors -storeRaw 
```

Upon completion, we should have an index with 8,841,823 documents.
The indexing speed may vary; on a modern desktop with an SSD, indexing takes a couple of minutes.

## Performing Retrieval on the Dev Queries

Since queries of the set are too many (+100k), it would take a long time to retrieve all of them. To speed this up, we use only the queries that are in the qrels file: 

```bash
python tools/scripts/msmarco/filter_queries.py \
 --qrels collections/msmarco-passage/qrels.dev.small.tsv \
 --queries collections/msmarco-passage/queries.dev.tsv \
 --output collections/msmarco-passage/queries.dev.small.tsv
```

The output queries file should contain 6980 lines.
We can now perform a retrieval run using this smaller set of queries:

```bash
sh target/appassembler/bin/SearchMsmarco -hits 1000 -threads 1 \
 -index indexes/msmarco-passage/lucene-index-msmarco \
 -queries collections/msmarco-passage/queries.dev.small.tsv \
 -output runs/run.msmarco-passage.dev.small.tsv
```

Note that by default, the above script uses BM25 with tuned parameters `k1=0.82`, `b=0.68`.
The option `-hits` specifies the number of documents per query to be retrieved.
Thus, the output file should have approximately 6980 × 1000 = 6.9M lines.

Retrieval speed will vary by machine:
On a modern desktop with an SSD, we can get ~0.07 s/query, so the run should finish in under ten minutes.
We can perform multi-threaded retrieval by changing the `-threads` argument.

Finally, we can evaluate the retrieved documents using this the official MS MARCO evaluation script: 

```bash
python tools/scripts/msmarco/msmarco_passage_eval.py \
 collections/msmarco-passage/qrels.dev.small.tsv runs/run.msmarco-passage.dev.small.tsv
```

And the output should be like this:

```
#####################
MRR @10: 0.18741227770955546
QueriesRanked: 6980
#####################
```

You can find this entry on the [MS MARCO Passage Ranking Leaderboard](https://microsoft.github.io/msmarco/) as entry "BM25 (Lucene8, tuned)", so you've just replicated (part of) a leaderboard submission!

We can also use the official TREC evaluation tool, `trec_eval`, to compute other metrics than MRR@10. 
For that we first need to convert runs and qrels files to the TREC format:

```bash
python tools/scripts/msmarco/convert_msmarco_to_trec_run.py \
 --input runs/run.msmarco-passage.dev.small.tsv \
 --output runs/run.msmarco-passage.dev.small.trec

python tools/scripts/msmarco/convert_msmarco_to_trec_qrels.py \
 --input collections/msmarco-passage/qrels.dev.small.tsv \
 --output collections/msmarco-passage/qrels.dev.small.trec
```

And run the `trec_eval` tool:

```bash
tools/eval/trec_eval.9.0.4/trec_eval -c -mrecall.1000 -mmap \
 collections/msmarco-passage/qrels.dev.small.trec runs/run.msmarco-passage.dev.small.trec
```

The output should be:

```
map                   	all	0.1957
recall_1000           	all	0.8573
```

Average precision and recall@1000 are the two metrics we care about the most.

## BM25 Tuning

Note that this figure differs slightly from the value reported in [Document Expansion by Query Prediction](https://arxiv.org/abs/1904.08375), which uses the Anserini (system-wide) default of `k1=0.9`, `b=0.4`.

Tuning was accomplished with `tools/scripts/msmarco/tune_bm25.py`, using the queries found [here](https://github.com/castorini/Anserini-data/tree/master/MSMARCO); the basic approach is grid search of parameter values in tenth increments.
There are five different sets of 10k samples (using the `shuf` command).
We tuned on each individual set and then averaged parameter values across all five sets (this has the effect of regularization).
In separate trials, we optimized for:

+ recall@1000, since Anserini output serves as input to downstream rerankers (e.g., based on BERT), and we want to maximize the number of relevant documents the rerankers have to work with;
+ MRR@10, for the case where Anserini output is directly presented to users (i.e., no downstream reranking).

It turns out that optimizing for MRR@10 and MAP yields the same settings.

Here's the comparison between the Anserini default and optimized parameters:

Setting                     | MRR@10 | MAP    | Recall@1000 |
:---------------------------|-------:|-------:|------------:|
Default (`k1=0.9`, `b=0.4`) | 0.1840 | 0.1926 | 0.8526
Optimized for recall@1000 (`k1=0.82`, `b=0.68`) | 0.1874 | 0.1957 | 0.8573
Optimized for MRR@10/MAP (`k1=0.60`, `b=0.62`)  | 0.1892 | 0.1972 | 0.8555

To replicate these results, the `SearchMsmarco` class above takes `k1` and `b` parameters as command-line arguments, e.g., `-k1 0.60 -b 0.62` (note that the default setting is `k1=0.82` and `b=0.68`).

## Replication Log

+ Results replicated by [@ronakice](https://github.com/ronakice) on 2019-08-12 (commit [`5b29d16`](https://github.com/castorini/anserini/commit/5b29d1654abc5e8a014c2230da990ab2f91fb340))
+ Results replicated by [@MathBunny](https://github.com/MathBunny) on 2019-08-12 (commit [`5b29d16`](https://github.com/castorini/anserini/commit/5b29d1654abc5e8a014c2230da990ab2f91fb340))
+ Results replicated by [@JMMackenzie](https://github.com/JMMackenzie) on 2020-01-08 (commit [`f63cd22`](https://github.com/castorini/anserini/commit/f63cd2275fa5a9d4da2d17e5f983a3308e8b50ce ))
+ Results replicated by [@edwinzhng](https://github.com/edwinzhng) on 2020-01-08 (commit [`5cc923d`](https://github.com/castorini/anserini/commit/5cc923d5c02777d8b25df32ff2e2a59be5badfdd))
+ Results replicated by [@LuKuuu](https://github.com/LuKuuu) on 2020-01-15 (commit [`f21137b`](https://github.com/castorini/anserini/commit/f21137b44f1115d25d1ff8ecaf7780c36498c5de))
+ Results replicated by [@kevinxyc1](https://github.com/kevinxyc1) on 2020-01-18 (commit [`798cb3a`](https://github.com/castorini/anserini/commit/798cb3a1a86f39317a5e5badb209795826d2033d))
+ Results replicated by [@nikhilro](https://github.com/nikhilro) on 2020-01-21 (commit [`631589e`](https://github.com/castorini/anserini/commit/631589e9e08326373f46555e007e6c302c19126d))
+ Results replicated by [@yuki617](https://github.com/yuki617) on 2020-03-29 (commit [`074723c`](https://github.com/castorini/anserini/commit/074723cbb10660fb9be2bfe6325739ab5fe0dd8d))
+ Results replicated by [@weipang142857](https://github.com/weipang142857) on 2020-04-20 (commit [`074723c`](https://github.com/castorini/anserini/commit/074723cbb10660fb9be2bfe6325739ab5fe0dd8d))
+ Results replicated by [@HangCui0510](https://github.com/HangCui0510) on 2020-04-23 (commit [`0ae567d`](https://github.com/castorini/anserini/commit/0ae567df5c8a70ac211efd958c9ca1ff609ff782))
+ Results replicated by [@x65han](https://github.com/x65han) on 2020-04-25 (commit [`f5496b9`](https://github.com/castorini/anserini/commit/f5496b905246084070f959e59626c6323210c3f2))
+ Results replicated by [@y276lin](https://github.com/y276lin) on 2020-04-26 (commit [`8f48f8e`](https://github.com/castorini/anserini/commit/8f48f8e40a37e5f6b5910a3a3b5c050a0f9be914))
+ Results replicated by [@stephaniewhoo](http://github.com/stephaniewhoo) on 2020-04-26 (commit [`8f48f8e`](https://github.com/castorini/anserini/commit/8f48f8e40a37e5f6b5910a3a3b5c050a0f9be914))
+ Results replicated by [@eiston](http://github.com/eiston) on 2020-05-04 (commit [`dd84a5a`](https://github.com/castorini/anserini/commit/dd84a5a514700365d9aa4a1ea988107372515f33))
+ Results replicated by [@rohilg](http://github.com/rohilg) on 2020-05-09 (commit [`20ee950`](https://github.com/castorini/anserini/commit/20ee950fbdc5cc9ce1c993911cbca4fcbfa86d02))
+ Results replicated by [@wongalvis14](https://github.com/wongalvis14) on 2020-05-09 (commit [`ebac5d6`](https://github.com/castorini/anserini/commit/ebac5d62f2e626e0a48c83dad79bddba60cadcf5))
+ Results replicated by [@YimingDou](https://github.com/YimingDou) on 2020-05-14 (commit [`3b0a642`](https://github.com/castorini/anserini/commit/3b0a6420e49863d9fe5908cf6e99582eb2d2882e))
+ Results replicated by [@richard3983](https://github.com/richard3983) on 2020-05-14 (commit [`a65646f`](https://github.com/castorini/anserini/commit/a65646fe203bf5c9c32189a56082d6f4d3bc340d))
+ Results replicated by [@MXueguang](https://github.com/MXueguang) on 2020-05-20 (commit [`3b2751e`](https://github.com/castorini/anserini/commit/3b2751e2d02a9d530e1c3d30b91083faeece8982))
+ Results replicated by [@shaneding](https://github.com/shaneding) on 2020-05-23 (commit [`b6e0367`](https://github.com/castorini/anserini/commit/b6e0367ef4e2b4fce9d81c8397ef1188e35971e7))
+ Results replicated by [@adamyy](https://github.com/adamyy) on 2020-05-28 (commit [`94893f1`](https://github.com/castorini/anserini/commit/94893f170e047d77c3ef5b8b995d7fbdd13f4298))
+ Results replicated by [@kelvin-jiang](https://github.com/kelvin-jiang) on 2020-05-28 (commit [`d55531a`](https://github.com/castorini/anserini/commit/d55531a738d2cf9e14c376d798d2de4bd3020b6b))
+ Results replicated by [@TianchengY](https://github.com/TianchengY) on 2020-05-28 (commit [`2947a16`](https://github.com/castorini/anserini/commit/2947a1622efae35637b83e321aba8e6fccd43489))
+ Results replicated by [@stariqmi](https://github.com/stariqmi) on 2020-05-28 (commit [`4914305`](https://github.com/castorini/anserini/commit/455169ea6a09f637817a6c4b4f6837dcc845f5f7))
+ Results replicated by [@justinborromeo](https://github.com/justinborromeo) on 2020-06-10 (commit [`7954eab`](https://github.com/castorini/anserini/commit/7954eab43f17bb8d254987d5873933c0b9596bb4))
+ Results replicated by [@yxzhu16](https://github.com/yxzhu16) on 2020-07-03 (commit [`68ace26`](https://github.com/castorini/anserini/commit/68ace26d0418a769df3d2b21e946495e54d462f6))
+ Results replicated by [@LizzyZhang-tutu](https://github.com/LizzyZhang-tutu) on 2020-07-13 (commit [`8c98d5b`](https://github.com/castorini/anserini/commit/8c98d5ba0795bbea01bcef1e21abb153fe4c3da1))
+ Results replicated by [@estella98](https://github.com/estella98) on 2020-07-29 
(commit [`99092a8`](https://github.com/castorini/anserini/commit/99092a82179d7efd38fc0b8c7c967137a40cd96f))
+ Results replicated by [@tangsaidi](https://github.com/tangsaidi) on 2020-08-19 
(commit [`aba846`](https://github.com/castorini/anserini/commit/aba846aa07d6f319fb3dc9cb591c20b4ae69f9ef))
+ Results replicated by [@qguo96](https://github.com/qguo96) on 2020-09-07 (commit [`e16b3c1`](https://github.com/castorini/anserini/commit/e16b3c160664057d4e00f2b4030cb6cb0d32fabd))
+ Results replicated by [@yuxuan-ji](https://github.com/yuxuan-ji) on 2020-09-08 (commit [`0f9a8ec`](https://github.com/castorini/anserini/commit/0f9a8ec4f335fb49c9387351745d1c755afc0e84))
+ Results replicated by [@wiltan-uw](https://github.com/wiltan-uw) on 2020-09-09 (commit [`93d913f`](https://github.com/castorini/anserini/commit/93d913f2619c44451be3d46816c7b9c44cbeb091))
+ Results replicated by [@JeffreyCA](https://github.com/JeffreyCA) on 2020-09-13 (commit [`bc2628b`](https://github.com/castorini/anserini/commit/bc2628b9916ce42b8026497c695d4c4198547f04))
+ Results replicated by [@jhuang265](https://github.com/jhuang265) on 2020-10-15 (commit [`66711b9`](https://github.com/castorini/anserini/commit/66711b9ff7722e2aea4ce2f59ca26ead5c091cac))
+ Results replicated by [@rayyang29](https://github.com/rayyang29) on 2020-10-27 (commit [`ad8cc5a`](https://github.com/castorini/anserini/commit/ad8cc5a02a53f09a83a8a6bfd7d187c9c3f96bd5))
+ Results replicated by [@Dahlia-Chehata](https://github.com/Dahlia-Chehata) on 2020-11-11 (commit [`22c0ad3`](https://github.com/castorini/anserini/commit/22c0ad3ebcff22c34a69ee3a7c122c4a9fb27a0e))
+ Results replicated by [@rakeeb123](https://github.com/rakeeb123) on 2020-12-07 (commit [`f50dcce`](https://github.com/castorini/anserini/commit/f50dcceb6cd0ec3403c1e77066aa51bb3275d24e))
+ Results replicated by [@jrzhang12](https://github.com/jrzhang12) on 2021-01-02 (commit [`be4e44d`](https://github.com/castorini/anserini/commit/be4e44d5ac1e898469fc179f8e6a336234b23d93))
+ Results replicated by [@HEC2018](https://github.com/HEC2018) on 2021-01-04 (commit [`4de21ec`](https://github.com/castorini/anserini/commit/4de21ece5e53cf20b4fcc711b575606b83c0d1f1))
+ Results replicated by [@KaiSun314](https://github.com/KaiSun314) on 2021-01-08 (commit [`113f1c7`](https://github.com/castorini/anserini/commit/113f1c78c3ffc8681a06c571901cf9ad8f5ee633))
+ Results replicated by [@yemiliey](https://github.com/yemiliey) on 2021-01-18 (commit [`179c242`](https://github.com/castorini/anserini/commit/179c242562bbb990e421f315370f34d4d19bbb9f))
+ Results replicated by [@larryli1999](https://github.com/larryli1999) on 2021-01-22 (commit [`3f9af5`](https://github.com/castorini/anserini/commit/3f9af54d6215eacbade7fc99ff8890920fdddee0))
