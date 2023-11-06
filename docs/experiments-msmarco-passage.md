# Anserini: BM25 Baselines for MS MARCO Passage Ranking

This page contains instructions for running BM25 baselines on the [MS MARCO *passage* ranking task](https://microsoft.github.io/msmarco/).
Note that there is a separate [MS MARCO *document* ranking task](experiments-msmarco-doc.md).
This exercise will require a machine with >8 GB RAM and >15 GB free disk space .

If you're a Waterloo student traversing the [onboarding path](https://github.com/lintool/guide/blob/master/ura.md), [start here](start-here.md
).
In general, don't try to rush through this guide by just blindly copying and pasting commands into a shell;
that's what I call [cargo culting](https://en.wikipedia.org/wiki/Cargo_cult_programming).
Instead, really try to understand what's going on.


**Learning outcomes** for this guide, building on previous steps in the onboarding path:

+ Be able to use Anserini to build a Lucene inverted index on the MS MARCO passage collection.
+ Be able to use Anserini to perform a batch retrieval run on the MS MARCO passage collection with the dev queries.
+ Be able to evaluate the retrieved results above.
+ Understand the MRR metric.

What's Anserini?
Well, it's the repo that you're in right now.
Anserini is a toolkit (in Java) for reproducible information retrieval research built on the [Luence search library](https://lucene.apache.org/).
The Lucene search library provides components of the popular [Elasticsearch](https://www.elastic.co/) platform.

Think of it this way: Lucene provides a "kit of parts".
Elasticsearch provides "assembly of parts" targeted to production search applications, with a REST-centric API.
Anserini provides an alternative way of composing the same core components together, targeted at information retrieval researchers.
By building on Lucene, Anserini aims to bridge the gap between academic information retrieval research and the practice of building real-world search applications.
That is, most things done with Anserini can be "translated" into Elasticsearch quite easily.

## Data Prep

In this guide, we're just going through the mechanical steps of data prep.
To better understand what you're actually doing, go through the [start here](start-here.md
) guide.
The guide contains the same exact instructions, but provide more detailed explanations.

We're going to use the repository's root directory as the working directory.
First, we need to download and extract the MS MARCO passage dataset:

```bash
mkdir collections/msmarco-passage

wget https://msmarco.blob.core.windows.net/msmarcoranking/collectionandqueries.tar.gz -P collections/msmarco-passage

# Alternative mirror:
# wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/collectionandqueries.tar.gz -P collections/msmarco-passage

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

We need to do a bit of data munging on the queries as well.
There are queries in the dev set that don't have relevance judgments.
Let's discard them:

```bash
python tools/scripts/msmarco/filter_queries.py \
  --qrels collections/msmarco-passage/qrels.dev.small.tsv \
  --queries collections/msmarco-passage/queries.dev.tsv \
  --output collections/msmarco-passage/queries.dev.small.tsv
```

The output queries file `collections/msmarco-passage/queries.dev.small.tsv` should contain 6980 lines.

## Indexing

In building a retrieval system, there are generally two phases:

+ In the **indexing** phase, an indexer takes the document collection (i.e., corpus) and builds an index, which is a data structure that supports efficient retrieval.
+ In the **retrieval** (or **search**) phase, the retrieval system returns a ranked list given a query _q_, with the aid of the index constructed in the previous phase.

(There's also a training phase when we start to discuss models that _learn_ from data, but we're not there yet.)

Given a (static) document collection, indexing only needs to be performed once, and hence there are fewer constraints on latency, throughput, and other aspects of performance (just needs to be "reasonable").
On the other hand, retrieval needs to be fast, i.e., low latency, high throughput, etc.

With the data prep above, we can now index the MS MARCO passage collection in `collections/msmarco-passage/collection_jsonl`.

If you haven't built Anserini already, build it now using the instructions in [anserini#-getting-started](https://github.com/castorini/anserini#-getting-started).

We index these docs as a `JsonCollection` (a specification of how documents are encoded) using Anserini:

```bash
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input collections/msmarco-passage/collection_jsonl \
  -index indexes/msmarco-passage/lucene-index-msmarco \
  -generator DefaultLuceneDocumentGenerator \
  -threads 9 -storePositions -storeDocvectors -storeRaw 
```

In this case, Lucene creates what is known as an **inverted index**.

Upon completion, we should have an index with 8,841,823 documents.
The indexing speed may vary; on a modern desktop with an SSD, indexing takes a couple of minutes.


## Retrieval

In the above step, we've built the inverted index.
Now we can now perform a retrieval run using queries we've prepared:

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/msmarco-passage/lucene-index-msmarco \
  -topics collections/msmarco-passage/queries.dev.small.tsv \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage.dev.small.tsv -format msmarco \
  -parallelism 4 \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -hits 1000
```

This is the **retrieval** (or **search**) phase.
We're performing retrieval _in batch_, on a set of queries.

Retrieval here uses a "bag-of-words" model known as **BM25**.
A "bag of words" model just means that documents are scored based on the matching of query terms (i.e., words) that appear in the documents, without regard to the structure of the document, the order of the words, etc.
BM25 is perhaps the most popular bag-of-words retrieval model; it's the default in the popular [Elasticsearch](https://www.elastic.co/) platform.
We'll discuss retrieval models in much more detail later.

The above command uses BM25 with tuned parameters `k1=0.82`, `b=0.68`.
The option `-hits` specifies the number of documents per query to be retrieved.
Thus, the output file should have approximately 6980 × 1000 = 6.9M lines.

Retrieval speed will vary by machine:
On a reasonably modern desktop with an SSD, with four threads (as specified above), the run takes a couple of minutes.
Adjust the parallelism by changing the `-parallelism` argument.

Congratulations, you've performed your first **retrieval run**!

Recap of what you've done:
You've fed the retrieval system a bunch of queries and the retrieval run is the output.
For each query, the retrieval system produced a ranked list of results (i.e., a list of hits).
The retrieval run contains the ranked lists for all queries you fed to it.

Let's take a look:

```bash
$ head runs/run.msmarco-passage.dev.small.tsv
1048585	7187158	1
1048585	7187157	2
1048585	7187163	3
1048585	7546327	4
1048585	7187160	5
1048585	8227279	6
1048585	7617404	7
1048585	7187156	8
1048585	2298838	9
1048585	7187155	10
```

The first column is the `qid` (corresponding to the query).
From above, we can see that `qid` 1048585 is the query "what is paula deen's brother".
The second column is the `docid` of the retrieved result (i.e., the hit), and the third column is the rank position.
That is, in a search interface, `docid` 7187158 would be shown in the top position, `docid` 7187157 would be shown in the second position, etc.

You can grep through the collection to see what that actual passage is:

```bash
$ grep 7187158 collections/msmarco-passage/collection.tsv
7187158	Paula Deen and her brother Earl W. Bubba Hiers are being sued by a former general manager at Uncle Bubba'sâ¦ Paula Deen and her brother Earl W. Bubba Hiers are being sued by a former general manager at Uncle Bubba'sâ
```

In this case, the document (hit) seems relevant.
That is, it contains information that addresses the information need.
So here, the retrieval system "did well".
Remember that this document was indeed marked relevant in the qrels, as we saw in the [start here](start-here.md
) guide.

As an additional sanity check, run the following:

```bash
$ cut -f 1 runs/run.msmarco-passage.dev.small.tsv | uniq | wc
    6980    6980   51039
```

This tells us that there are 6980 unique tokens in the first column of the run file.
Since the first column indicates the `qid`, it means that the file contains ranked lists for 6980 queries, which checks out.

## Evaluation

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

(Yea, the number of digits of precision is a bit... excessive)

Remember from the [start here](start-here.md
) guide that with relevance judgments (qrels), we can automatically evaluate the retrieval system output (i.e., the run).

The final ingredient is a metric, i.e., how to quantify the "quality" of a ranked list.
Here, we're using a metric called MRR, or mean reciprocal rank.
The idea is quite simple:
We look at where the relevant `docid` appears.
If it appears at rank 1, the system gets a score of one.
If it appears at rank 2, the system gets a score of 1/2.
If it appears at rank 3, the system gets a score of 1/3.
And so on.
MRR@10 means that we only go down to rank 10.
If the relevant `docid` doesn't appear in the top 10, then the system gets a score of zero.

That's the score of a query.
We take the average of the scores across all queries (6980 in this case), and we arrive at the score for the entire run.

You can find this run on the [MS MARCO Passage Ranking Leaderboard](https://microsoft.github.io/MSMARCO-Passage-Ranking-Submissions/leaderboard/) as the entry named "BM25 (Lucene8, tuned)", dated 2019/06/26.
So you've just reproduced (part of) a leaderboard submission!

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
  collections/msmarco-passage/qrels.dev.small.trec \
  runs/run.msmarco-passage.dev.small.trec
```

The output should be:

```
map                   	all	0.1957
recall_1000           	all	0.8573
```

In many retrieval applications, average precision and recall@1000 are the two metrics we care about the most.

You can use `trec_eval` to compute the MRR@10 also, which gives results identical to above (just fewer digits of precision):

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 10 -m recip_rank \
  collections/msmarco-passage/qrels.dev.small.trec \
  runs/run.msmarco-passage.dev.small.trec
```

It's a different command-line incantation of `trec_eval` to compute MRR@10.
And if you add `-q`, the tool will spit out the MRR@10 _per query_ (for all 6908 queries, in addition to the final average).

```
tools/eval/trec_eval.9.0.4/trec_eval -q -c -M 10 -m recip_rank \
  collections/msmarco-passage/qrels.dev.small.trec \
  runs/run.msmarco-passage.dev.small.trec
```

We can find the MRR@10 for `qid` 1048585 above:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -q -c -M 10 -m recip_rank \
    collections/msmarco-passage/qrels.dev.small.trec \
    runs/run.msmarco-passage.dev.small.trec | grep 1048585

recip_rank            	1048585	1.0000
```

This is consistent with the example we worked through above.
At this point, make sure that the connections between a query, the relevance judgments for a query, the ranked list, and the metric (MRR@10) are clear in your mind.
Work through a few more examples (take another query, look at its qrels and ranked list, and compute its MRR@10 by hand) to convince yourself that you understand what's going on.

The tl;dr is that there are different formats for run files and lots of different metrics you can compute.
`trec_eval` is a standard tool used by information retrieval researchers (which has many command-line options that you'll slowly learn over time).
Researchers have been trying to answer the question "how do we know if a search result is good and how do we measure it" for over half a century... and the question still has not been fully resolved.
In short, it's complicated.

At this time, look back through the learning outcomes again and make sure you're good.
As a next step in the onboarding path, you basically [do the same thing again in Python with Pyserini](https://github.com/castorini/pyserini/blob/master/docs/experiments-msmarco-passage.md) (as opposed to Java with Anserini here).

Before you move on, however, add an entry in the "Reproduction Log" at the bottom of this page, following the same format: use `yyyy-mm-dd`, make sure you're using a commit id that's on the main trunk of Anserini, and use its 7-hexadecimal prefix for the link anchor text.
In the description of your pull request, please provide some details on your setup (e.g., operating system, environment and configuration, etc.).
In addition, also provide some indication of success (e.g., everything worked) or document issues you encountered.
If you think this guide can be improved in any way (e.g., you caught a typo or think a clarification is warranted), feel free to include it in the pull request.

## BM25 Tuning

This section is **not** part of the onboarding path, so feel free to skip.

Note that this figure differs slightly from the value reported in [Document Expansion by Query Prediction](https://arxiv.org/abs/1904.08375), which uses the Anserini (system-wide) default of `k1=0.9`, `b=0.4`.

Tuning was accomplished with `tools/scripts/msmarco/tune_bm25.py`, using the queries found [here](https://github.com/castorini/Anserini-data/tree/master/MSMARCO); the basic approach is grid search of parameter values in tenth increments.
There are five different sets of 10k samples (using the `shuf` command).
We tuned on each individual set and then averaged parameter values across all five sets (this has the effect of regularization).
In separate trials, we optimized for:

+ recall@1000, since Anserini output serves as input to downstream rerankers (e.g., based on BERT), and we want to maximize the number of relevant documents the rerankers have to work with;
+ MRR@10, for the case where Anserini output is directly presented to users (i.e., no downstream reranking).

It turns out that optimizing for MRR@10 and MAP yields the same settings.

Here's the comparison between the Anserini default and optimized parameters:

| Setting                                         | MRR@10 |    MAP | Recall@1000 |
|:------------------------------------------------|-------:|-------:|------------:|
| Default (`k1=0.9`, `b=0.4`)                     | 0.1840 | 0.1926 |      0.8526 |
| Optimized for recall@1000 (`k1=0.82`, `b=0.68`) | 0.1874 | 0.1957 |      0.8573 |
| Optimized for MRR@10/MAP (`k1=0.60`, `b=0.62`)  | 0.1892 | 0.1972 |      0.8555 |

As mentioned above, the BM25 run with `k1=0.82`, `b=0.68` corresponds to the entry "BM25 (Lucene8, tuned)" dated 2019/06/26 on the [MS MARCO Passage Ranking Leaderboard](https://microsoft.github.io/MSMARCO-Passage-Ranking-Submissions/leaderboard/).
The BM25 run with default parameters `k1=0.9`, `b=0.4` roughly corresponds to the entry "BM25 (Anserini)" dated 2019/04/10 (but Anserini was using Lucene 7.6 at the time).

## Reproduction Log[*](reproducibility.md)

+ Results reproduced by [@ronakice](https://github.com/ronakice) on 2019-08-12 (commit [`5b29d16`](https://github.com/castorini/anserini/commit/5b29d1654abc5e8a014c2230da990ab2f91fb340))
+ Results reproduced by [@MathBunny](https://github.com/MathBunny) on 2019-08-12 (commit [`5b29d16`](https://github.com/castorini/anserini/commit/5b29d1654abc5e8a014c2230da990ab2f91fb340))
+ Results reproduced by [@JMMackenzie](https://github.com/JMMackenzie) on 2020-01-08 (commit [`f63cd22`](https://github.com/castorini/anserini/commit/f63cd2275fa5a9d4da2d17e5f983a3308e8b50ce ))
+ Results reproduced by [@edwinzhng](https://github.com/edwinzhng) on 2020-01-08 (commit [`5cc923d`](https://github.com/castorini/anserini/commit/5cc923d5c02777d8b25df32ff2e2a59be5badfdd))
+ Results reproduced by [@LuKuuu](https://github.com/LuKuuu) on 2020-01-15 (commit [`f21137b`](https://github.com/castorini/anserini/commit/f21137b44f1115d25d1ff8ecaf7780c36498c5de))
+ Results reproduced by [@kevinxyc1](https://github.com/kevinxyc1) on 2020-01-18 (commit [`f21137b`](https://github.com/castorini/anserini/commit/f21137b44f1115d25d1ff8ecaf7780c36498c5de))
+ Results reproduced by [@nikhilro](https://github.com/nikhilro) on 2020-01-21 (commit [`631589e`](https://github.com/castorini/anserini/commit/631589e9e08326373f46555e007e6c302c19126d))
+ Results reproduced by [@yuki617](https://github.com/yuki617) on 2020-03-29 (commit [`074723c`](https://github.com/castorini/anserini/commit/074723cbb10660fb9be2bfe6325739ab5fe0dd8d))
+ Results reproduced by [@weipang142857](https://github.com/weipang142857) on 2020-04-20 (commit [`074723c`](https://github.com/castorini/anserini/commit/074723cbb10660fb9be2bfe6325739ab5fe0dd8d))
+ Results reproduced by [@HangCui0510](https://github.com/HangCui0510) on 2020-04-23 (commit [`0ae567d`](https://github.com/castorini/anserini/commit/0ae567df5c8a70ac211efd958c9ca1ff609ff782))
+ Results reproduced by [@x65han](https://github.com/x65han) on 2020-04-25 (commit [`f5496b9`](https://github.com/castorini/anserini/commit/f5496b905246084070f959e59626c6323210c3f2))
+ Results reproduced by [@y276lin](https://github.com/y276lin) on 2020-04-26 (commit [`8f48f8e`](https://github.com/castorini/anserini/commit/8f48f8e40a37e5f6b5910a3a3b5c050a0f9be914))
+ Results reproduced by [@stephaniewhoo](http://github.com/stephaniewhoo) on 2020-04-26 (commit [`8f48f8e`](https://github.com/castorini/anserini/commit/8f48f8e40a37e5f6b5910a3a3b5c050a0f9be914))
+ Results reproduced by [@eiston](http://github.com/eiston) on 2020-05-04 (commit [`dd84a5a`](https://github.com/castorini/anserini/commit/dd84a5a514700365d9aa4a1ea988107372515f33))
+ Results reproduced by [@rohilg](http://github.com/rohilg) on 2020-05-09 (commit [`20ee950`](https://github.com/castorini/anserini/commit/20ee950fbdc5cc9ce1c993911cbca4fcbfa86d02))
+ Results reproduced by [@wongalvis14](https://github.com/wongalvis14) on 2020-05-09 (commit [`ebac5d6`](https://github.com/castorini/anserini/commit/ebac5d62f2e626e0a48c83dad79bddba60cadcf5))
+ Results reproduced by [@YimingDou](https://github.com/YimingDou) on 2020-05-14 (commit [`3b0a642`](https://github.com/castorini/anserini/commit/3b0a6420e49863d9fe5908cf6e99582eb2d2882e))
+ Results reproduced by [@richard3983](https://github.com/richard3983) on 2020-05-14 (commit [`a65646f`](https://github.com/castorini/anserini/commit/a65646fe203bf5c9c32189a56082d6f4d3bc340d))
+ Results reproduced by [@MXueguang](https://github.com/MXueguang) on 2020-05-20 (commit [`3b2751e`](https://github.com/castorini/anserini/commit/3b2751e2d02a9d530e1c3d30b91083faeece8982))
+ Results reproduced by [@shaneding](https://github.com/shaneding) on 2020-05-23 (commit [`b6e0367`](https://github.com/castorini/anserini/commit/b6e0367ef4e2b4fce9d81c8397ef1188e35971e7))
+ Results reproduced by [@adamyy](https://github.com/adamyy) on 2020-05-28 (commit [`94893f1`](https://github.com/castorini/anserini/commit/94893f170e047d77c3ef5b8b995d7fbdd13f4298))
+ Results reproduced by [@kelvin-jiang](https://github.com/kelvin-jiang) on 2020-05-28 (commit [`d55531a`](https://github.com/castorini/anserini/commit/d55531a738d2cf9e14c376d798d2de4bd3020b6b))
+ Results reproduced by [@TianchengY](https://github.com/TianchengY) on 2020-05-28 (commit [`2947a16`](https://github.com/castorini/anserini/commit/2947a1622efae35637b83e321aba8e6fccd43489))
+ Results reproduced by [@stariqmi](https://github.com/stariqmi) on 2020-05-28 (commit [`4914305`](https://github.com/castorini/anserini/commit/455169ea6a09f637817a6c4b4f6837dcc845f5f7))
+ Results reproduced by [@justinborromeo](https://github.com/justinborromeo) on 2020-06-10 (commit [`7954eab`](https://github.com/castorini/anserini/commit/7954eab43f17bb8d254987d5873933c0b9596bb4))
+ Results reproduced by [@yxzhu16](https://github.com/yxzhu16) on 2020-07-03 (commit [`68ace26`](https://github.com/castorini/anserini/commit/68ace26d0418a769df3d2b21e946495e54d462f6))
+ Results reproduced by [@LizzyZhang-tutu](https://github.com/LizzyZhang-tutu) on 2020-07-13 (commit [`8c98d5b`](https://github.com/castorini/anserini/commit/8c98d5ba0795bbea01bcef1e21abb153fe4c3da1))
+ Results reproduced by [@estella98](https://github.com/estella98) on 2020-07-29 (commit [`99092a8`](https://github.com/castorini/anserini/commit/99092a82179d7efd38fc0b8c7c967137a40cd96f))
+ Results reproduced by [@tangsaidi](https://github.com/tangsaidi) on 2020-08-19 (commit [`aba846`](https://github.com/castorini/anserini/commit/aba846aa07d6f319fb3dc9cb591c20b4ae69f9ef))
+ Results reproduced by [@qguo96](https://github.com/qguo96) on 2020-09-07 (commit [`e16b3c1`](https://github.com/castorini/anserini/commit/e16b3c160664057d4e00f2b4030cb6cb0d32fabd))
+ Results reproduced by [@yuxuan-ji](https://github.com/yuxuan-ji) on 2020-09-08 (commit [`0f9a8ec`](https://github.com/castorini/anserini/commit/0f9a8ec4f335fb49c9387351745d1c755afc0e84))
+ Results reproduced by [@wiltan-uw](https://github.com/wiltan-uw) on 2020-09-09 (commit [`93d913f`](https://github.com/castorini/anserini/commit/93d913f2619c44451be3d46816c7b9c44cbeb091))
+ Results reproduced by [@JeffreyCA](https://github.com/JeffreyCA) on 2020-09-13 (commit [`bc2628b`](https://github.com/castorini/anserini/commit/bc2628b9916ce42b8026497c695d4c4198547f04))
+ Results reproduced by [@jhuang265](https://github.com/jhuang265) on 2020-10-15 (commit [`66711b9`](https://github.com/castorini/anserini/commit/66711b9ff7722e2aea4ce2f59ca26ead5c091cac))
+ Results reproduced by [@rayyang29](https://github.com/rayyang29) on 2020-10-27 (commit [`ad8cc5a`](https://github.com/castorini/anserini/commit/ad8cc5a02a53f09a83a8a6bfd7d187c9c3f96bd5))
+ Results reproduced by [@Dahlia-Chehata](https://github.com/Dahlia-Chehata) on 2020-11-11 (commit [`22c0ad3`](https://github.com/castorini/anserini/commit/22c0ad3ebcff22c34a69ee3a7c122c4a9fb27a0e))
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
+ Results reproduced by [@AceZhan](https://github.com/AceZhan) on 2022-01-13 (commit [`7ff99e0`](https://github.com/castorini/anserini/commit/7ff99e0d3208dc8bfec6bb8ca254d0b016015a2d))
+ Results reproduced by [@jh8liang](https://github.com/jh8liang) on 2022-02-06 (commit [`5cdf9ec`](https://github.com/castorini/anserini/commit/5cdf9ec3600bf693cf8d80a9c869444b4e29ff75))
+ Results reproduced by [@mayankanand007](https://github.com/mayankanand007) on 2022-02-22 (commit [`6a70804`](https://github.com/castorini/anserini/commit/6a708047f71528f7d516c0dd45485204a36e6b1d))
+ Results reproduced by [@jasper-xian](https://github.com/jasper-xian) on 2022-03-27 (commit [`2e8e9fd`](https://github.com/castorini/anserini/commit/2e8e9fdfedc1b81522d9e22a805e08e6a7105762))
+ Results reproduced by [@jx3yang](https://github.com/jx3yang) on 2022-04-25 (commit [`b429218`](https://github.com/castorini/anserini/commit/b429218e52a385eabf3fd81979e221111fbc4a19))
+ Results reproduced by [@AreelKhan](https://github.com/AreelKhan) on 2022-04-27 (commit [`7adee1d`](https://github.com/castorini/anserini/commit/7adee1d9596f052a87b0427e654c4b95e2cd5e89))
+ Results reproduced by [@alvind1](https://github.com/alvind1) on 2022-05-05 (commit [`9b2dd5f5`](https://github.com/castorini/anserini/commit/9b2dd5f5e524ce56e5784cb73404d39926982733))
+ Results reproduced by [@Pie31415](https://github.com/Pie31415) on 2022-06-22 (commit [`6aef2eb`](https://github.com/castorini/anserini/commit/6aef2eb0681f34387bf04077465f04343c338cf4))
+ Results reproduced by [@aivan6842](https://github.com/aivan6842) on 2022-07-11 (commit [`8010d5c`](https://github.com/castorini/anserini/commit/8010d5c0b066f0316c6c506170274f8f7d558f73))
+ Results reproduced by [@AileenLin](https://github.com/AileenLin) on 2022-09-11 (commit [`760dab0`](https://github.com/castorini/anserini/commit/760dab07745383f0488f8d91b563d2b23c19e0ce))
+ Results reproduced by [@Jasonwu-0803](https://github.com/Jasonwu-0803) on 2022-09-27 (commit [`b5ecc5a`](https://github.com/castorini/anserini/commit/b5ecc5aff79ddfc82b175f6bd3048f5039f0480f))
+ Results reproduced by [@limelody](https://github.com/limelody) on 2022-09-27 (commit [`252b5e2`](https://github.com/castorini/anserini/commit/252b5e2087dd7b3b994d41a444d4ae0044519819))
+ Results reproduced by [@minconszhang](https://github.com/minconszhang) on 2022-11-25 (commit [`6556550`](https://github.com/castorini/anserini/commit/6556550fe33f7804993aaf10201da9900d52c14b))
+ Results reproduced by [@jingliu](https://github.com/ljatca) on 2022-12-08 (commit [`6872c87`](https://github.com/castorini/anserini/commit/6872c878d6969ebdf1875e4436777b95746b35a5))
+ Results reproduced by [@farazkh80](https://github.com/farazkh80) on 2022-12-18 (commit [`4527a5d`](https://github.com/castorini/anserini/commit/4527a5d0555e5def53855a0f2fd3bb90f53ea7e9))
+ Results reproduced by [@Cath](https://github.com/Cathrineee) on 2023-01-14 (commit [`732cba4`](https://github.com/castorini/anserini/commit/732cba4775312f606888778d1e705e1ccea926df))
+ Results reproduced by [@dlrudwo1269](https://github.com/dlrudwo1269) on 2023-03-06 (commit [`4b7662c7`](https://github.com/castorini/anserini/commit/4b7662c7f154b9fbfe68aec9c371d8204d9ed31e))
+ Results reproduced by [@aryamancodes](https://github.com/aryamancodes) on 2023-04-11 (commit [`ed89401`](https://github.com/castorini/anserini/commit/ed894015977347e6e04363dea67d19e5474848ee))
+ Results reproduced by [@tailaiwang](https://github.com/tailaiwang) on 2023-04-29 (commit [`44dc9db`](https://github.com/castorini/anserini/commit/44dc9db16266107e28d70c4a14e3ba50191c81a0))
+ Results reproduced by [@Jocn2020](https://github.com/Jocn2020) on 2023-04-30 (commit [`30269d6`](https://github.com/castorini/anserini/commit/30269d636a556174e3756fe59accab6c22a7c732))
+ Results reproduced by [@billcui57](https://github.com/billcui57) on 2023-05-11 (commit [`a913b52`](https://github.com/castorini/anserini/commit/a913b52478ae63e4427215e16f58081686f8e8f2))
+ Results reproduced by [@zoehahaha](https://github.com/zoehahaha) on 2023-05-12 (commit [`b429218`](https://github.com/castorini/anserini/commit/b429218e52a385eabf3fd81979e221111fbc4a19))
+ Results reproduced by [@Singularity-tian](https://github.com/Singularity-tian) on 2023-05-12 (commit [`d82b6f7`](https://github.com/castorini/anserini/commit/d82b6f76cbef009bccb0e67fc2bedc6fa6ae56c6))
+ Results reproduced by [@Richard5678](https://github.com/Richard5678) on 2023-06-11 (commit [`2d484d3`](https://github.com/castorini/anserini/commit/2d484d330b6218852552901fa4dc62c441e7ff17))
+ Results reproduced by [@pratyushpal](https://github.com/pratyushpal) on 2023-07-14 (commit [`17d5fc7`](https://github.com/castorini/anserini/commit/17d5fc7f338b511c4dc49de88e9b2ab7ea27f8aa))
+ Results reproduced by [@sahel-sh](https://github.com/sahel-sh) on 2023-07-22 (commit [`4b8f051`](https://github.com/castorini/anserini/commit/4b8f051c25992a5d87ecf8d30d45a93aff17abc4))
+ Results reproduced by [@Mofetoluwa](https://github.com/Mofetoluwa) on 2023-08-03 (commit [`7314128`](https://github.com/castorini/anserini/commit/73141282b62979e189ac3c87d9a902064f34a1c5))
+ Results reproduced by [@yilinjz](https://github.com/yilinjz) on 2023-08-24 (commit [`d4cb6fd`](https://github.com/castorini/anserini/commit/d4cb6fd1c0b5ed0a7eac4747af919823acc939fa))
+ Results reproduced by [@Andrwyl](https://github.com/Andrwyl) on 2023-08-26 (commit [`b64a412`](https://github.com/castorini/anserini/commit/b64a412453d0fee1b89179d3b665984651a8b8f8))
+ Results reproduced by [@UShivani3](https://github.com/UShivani3) on 2023-08-29 (commit [`24ab292`](https://github.com/castorini/anserini/commit/24ab292c5eaaccd40bbfa13fa7122eeb58261aaa))
+ Results reproduced by [@ErrenYeager](https://github.com/ErrenYeager) on 2023-09-02 (commit [`4ae518b`](https://github.com/castorini/anserini/commit/4ae518bb284ebcba0b273a473bc8774735cb7d19))
+ Results reproduced by [@lucedes27](https://github.com/lucedes27) on 2023-09-03 (commit [`211e74f`](https://github.com/castorini/anserini/commit/211e74f1453b2b100c03ac78d2a130b07b19b780))
+ Results reproduced by [@mchlp](https://github.com/mchlp) on 2023-09-03 (commit [`211e74f`](https://github.com/castorini/anserini/commit/211e74f1453b2b100c03ac78d2a130b07b19b780))
+ Results reproduced by [@Edward-J-Xu](https://github.com/Edward-J-Xu) on 2023-09-04 (commit [`6030b1`](https://github.com/castorini/anserini/commit/6030b1886d3b501c69f258f9f716508bf7fb80ff))
+ Results reproduced by [@Sumon-Kanti-Dey](https://github.com/SumonKantiDey) on 2023-09-04 (commit [`a9de13a`](https://github.com/castorini/anserini/commit/a9de13a81194189bbbffa39d294acd59372229a6))
+ Results reproduced by [@MojTabaa4](https://github.com/MojTabaa4) on 2023-09-13 (commit [`adf480b`](https://github.com/castorini/anserini/commit/ae6ff62f1830f8e3f38199a7d8f723940a62fb28))
+ Results reproduced by [@Kshama](https://github.com/Kshama33) on 2023-09-17 (commit [`f27984b`](https://github.com/castorini/anserini/commit/f27984be65c3ff34f273241d7bf82b23d3708ccb))
+ Results reproduced by [@MelvinMo](https://github.com/MelvinMo) on 2023-09-23 (commit [`21efc3f`](https://github.com/castorini/anserini/commit/21efc3fed8b65c2cfc0512fa4772aafffbcefe76))
+ Results reproduced by [@dourian](https://github.com/dourian) on 2023-09-25 (commit [`24ab292`](https://github.com/castorini/anserini/commit/24ab292c5eaaccd40bbfa13fa7122eeb58261aaa))
+ Results reproduced by [@ksunisth](https://github.com/ksunisth) on 2023-09-27 (commit [`7c95d91`](https://github.com/castorini/anserini/commit/7c95d9155e59cfc55c573139b3538cc76d0f5371))
+ Results reproduced by [@maizerrr](https://github.com/maizerrr) on 2023-10-01 (commit [`2abdd37`](https://github.com/castorini/anserini/commit/2abdd373d70ae64ff6638c3440d7f0063514d313))
+ Results reproduced by [@shayanbali](https://github.com/shayanbali) on 2023-10-12 (commit [`8194b8e`](https://github.com/castorini/anserini/commit/8194b8e91fe799fcb0d3b4348e9d710a0aa1ad26))
+ Results reproduced by [@gituserbs](https://github.com/gituserbs) on 2023-10-14 (commit [`8194b8e`](https://github.com/castorini/anserini/commit/8194b8e91fe799fcb0d3b4348e9d710a0aa1ad26))
+ Results reproduced by [@oscarbelda86](https://github.com/oscarbelda86) on 2023-10-31 (commit [`4c06d8a`](https://github.com/castorini/anserini/commit/4c06d8a6de655caf9c3dc4d6d1977b58f68e3992))
+ Results reproduced by [@shakibaam](https://github.com/shakibaam) on 2023-11-03 (commit [`2f3e7d5`](https://github.com/castorini/anserini/commit/2f3e7d50ef87a687f519ec0c4874807845a71078))
+ Results reproduced by [@gitHubAndyLee2020](https://github.com/gitHubAndyLee2020) on 2023-11-03 (commit [`2f3e7d5`](https://github.com/castorini/anserini/commit/2f3e7d50ef87a687f519ec0c4874807845a71078))
+ Results reproduced by [@Melissa1412](https://github.com/Melissa1412) on 2023-11-04 (commit [`cf459b3`](https://github.com/castorini/anserini/commit/cf459b39ecc2c92a400210a9ba84736bdb5d4422))
+ Results reproduced by [@aliranjbari](https://github.com/aliranjbari) on 2023-11-06 (commit [`d2fb8a5`](https://github.com/castorini/anserini/commit/d2fb8a51f130c40aed99eee10ce34fc3f66869c7))
