# TREC-COVID doc2query Baselines

This document describes various doc2query baselines for the [TREC-COVID Challenge](https://ir.nist.gov/covidSubmit/), which uses the [COVID-19 Open Research Dataset (CORD-19)](https://pages.semanticscholar.org/coronavirus-research) from the [Allen Institute for AI](https://allenai.org/).
Here, we focus on running retrieval experiments; for basic instructions on building Anserini indexes, see [this page](experiments-cord19.md) and for instructions specific to building doc2query expanded Anserini indexes, see [this page](https://github.com/castorini/docTTTTTquery/).

doc2query describes a family of document expansion techniques:

+ Rodrigo Nogueira, Wei Yang, Jimmy Lin, Kyunghyun Cho. [Document Expansion by Query Prediction](https://arxiv.org/abs/1904.08375). _arXiv:1904.08375_.
+ Rodrigo Nogueira and Jimmy Lin. [From doc2query to docTTTTTquery](https://cs.uwaterloo.ca/~jimmylin/publications/Nogueira_Lin_2019_docTTTTTquery-v2.pdf). December 2019.

The idea is conceptually simple: prior to indexing, for each document, we use a model to predict queries for which that document will be relevant.
These predicted queries are then appended to the original document and indexed as usual.

For CORD-19, these predictions were made using only article title and abstracts with T5 trained on MS MARCO passage date.
These expansions were then appended to the abstract, full-text, and paragraph index conditions, as described on [this page](experiments-cord19.md).

All the runs referenced on this page are stored in [this repo](https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs).
As an alternative to downloading each run separately, clone the repo and you'll have everything.

## Round 5

These are runs that can be easily replicated with Anserini, from pre-built doc2query expanded CORD-19 indexes we have provided (version from 2020/07/16, the official corpus used in round 5).
They were prepared _for_ round 5 (for participants who wish to have a baseline run to rerank); to provide a sense of effectiveness, we present evaluation results with the cumulative qrels from rounds 1, 2, 3, and 4 ([`qrels_covid_d4_j0.5-4.txt`](https://ir.nist.gov/covidSubmit/data/qrels-covid_d4_j0.5-4.txt) provided by NIST, stored in our repo as [`qrels.covid-round4-cumulative.txt`](../src/main/resources/topics-and-qrels/qrels.covid-round4-cumulative.txt)).

|    | index     | field(s)                        | nDCG@10 | J@10 | R@1k | run file | checksum |
|---:|:----------|:--------------------------------|--------:|-----:|-----:|:---------|----------|
|  1 | abstract  | query+question                  | 0.4635 | 0.5300 | 0.4462 | [[download](https://www.dropbox.com/s/sa6abjrk1esxn38/expanded.anserini.covid-r5.abstract.qq.bm25.txt?dl=1)]    | `9923233a31ac004f84b7d563baf6543c` |
|  2 | abstract  | UDel qgen                       | 0.4548 | 0.5000 | 0.4527 | [[download](https://www.dropbox.com/s/t3s3oj9g0b1nphk/expanded.anserini.covid-r5.abstract.qdel.bm25.txt?dl=1)]  | `e0c7a1879e5b1742045bba0f5293d558` |
|  3 | full-text | query+question                  | 0.4450 | 0.6020 | 0.4473 | [[download](https://www.dropbox.com/s/utvw91nluzwm3ex/expanded.anserini.covid-r5.full-text.qq.bm25.txt?dl=1)]   | `78aa7f481de91d22192163ed934d02ee` |
|  4 | full-text | UDel qgen                       | 0.4817 | 0.6040 | 0.4711 | [[download](https://www.dropbox.com/s/xk2jyiwh5fjdwst/expanded.anserini.covid-r5.full-text.qdel.bm25.txt?dl=1)] | `51cbae025bf90dadf8f26c5c31af9f66` |
|  5 | paragraph | query+question                  | 0.4904 | 0.5820 | 0.5004 | [[download](https://www.dropbox.com/s/rjbyljcpziv31xx/expanded.anserini.covid-r5.paragraph.qq.bm25.txt?dl=1)]   | `0b80444c8a737748ba9199ddf0795421` |
|  6 | paragraph | UDel qgen                       | 0.4940 | 0.5700 | 0.5070 | [[download](https://www.dropbox.com/s/f4h2jhhla4o26wr/expanded.anserini.covid-r5.paragraph.qdel.bm25.txt?dl=1)] | `2040b9a4759af722d50610f26989c328` |
|  7 | -         | reciprocal rank fusion(1, 3, 5) | 0.4908 | 0.5880 | 0.5119 | [[download](https://www.dropbox.com/s/bj00pfwngi2j2g1/expanded.anserini.covid-r5.fusion1.txt?dl=1)]             | `c0ffc7b1719f64d2f37ce99a9ef0413c` |
|  8 | -         | reciprocal rank fusion(2, 4, 6) | 0.4846 | 0.5740 | 0.5218 | [[download](https://www.dropbox.com/s/f5ro0ex38gkvnqc/expanded.anserini.covid-r5.fusion2.txt?dl=1)]             | `329f13267abf3f3d429a1593c1bd862f` |
|  9 | abstract  | UDel qgen + RF                  | 0.6095 | 0.6320 | 0.5280 | [[download](https://www.dropbox.com/s/j6op32bcaszd1up/expanded.anserini.covid-r5.abstract.qdel.bm25%2Brm3Rf.txt?dl=1)] | `a5e016c84d5547519ffbcf74c9a24fc8` |

**IMPORTANT NOTES!!!**

+ These runs are performed at [`539f7d`](https://github.com/castorini/anserini/commit/539f7d43a0183454a633f34aa20b46d2eeec1a19), 2020/07/24.
+ J@10 refers to Judged@10 and R@1k refers to Recall@1000.
+ The evaluation numbers are produced with the NIST-prepared cumulative qrels from rounds 1, 2, 3, and 4 ([`qrels_covid_d4_j0.5-4.txt`](https://ir.nist.gov/covidSubmit/data/qrels-covid_d4_j0.5-4.txt) provided by NIST, stored in our repo as [`qrels.covid-round4-cumulative.txt`](../src/main/resources/topics-and-qrels/qrels.covid-round4-cumulative.txt)) on the round 5 collection (release of 7/16).
+ For the abstract and full-text indexes, we request up to 10k hits for each topic; the number of actual hits retrieved is fairly close to this (a bit less because of deduping). For the paragraph index, we request up to 50k hits for each topic; because multiple paragraphs are retrieved from the same document, the number of unique documents in each list of hits is much smaller. A cautionary note: our experience is that choosing the top _k_ documents to rerank has a large impact on end-to-end effectiveness. Reranking the top 100 seems to provide higher precision than top 1000, but the likely tradeoff is lower recall. It is very likely the case that you _don't_ want to rerank all available hits.
+ Row 9 represents the feedback baseline condition introduced in round 3: abstract index, UDel query generator, BM25+RM3 relevance feedback (100 feedback terms).

The final runs after removing judgments from 1, 2, 3, and 4 (cumulatively), are as follows:

| runtag | run file | checksum |
|:-------|:---------|:---------|
| `r5.fusion1` = Row 7 | [[download](https://www.dropbox.com/s/5ke2c4x2z8de31h/expanded.anserini.final-r5.fusion1.txt?dl=1)] | `2295216ed623d2621f00c294f7c389e1` |
| `r5.fusion2` = Row 8 | [[download](https://www.dropbox.com/s/j1qdqr88cbsybae/expanded.anserini.final-r5.fusion2.txt?dl=1)] | `a65fabe7b5b7bc4216be632296269ce6` |
| `r5.rf` = Row 9      | [[download](https://www.dropbox.com/s/5bm4pdngh5bx3px/expanded.anserini.final-r5.rf.txt?dl=1)]      | `24f0b75a25273b7b00d3e65065e98147` |

We have written scripts that automate the replication of these baselines:

```
$ python src/main/python/trec-covid/download_doc2query_indexes.py --date 2020-07-16
$ python src/main/python/trec-covid/generate_round5_doc2query_baselines.py
```

### Evaluation with Round 5 Qrels

Since the above runs were prepared _for_ round 5, we do not know how well they actually performed until the round 5 judgments from NIST were released.
Here, we provide these evaluation results.

Note that the runs posted on the [TREC-COVID archive](https://ir.nist.gov/covidSubmit/archive.html) are _not_ exactly the same the runs we submitted.
According to NIST (from email to participants), they removed "documents that were previously judged but had id changes from the Round 5 submissions for scoring, even though the change in `cord_uid` was unknown at submission time."
The actual evaluated runs are (mirrored from URL above):

| group | runtag | run file | checksum |
|:------|:-------|:---------|:---------|
| `anserini` | `r5.d2q.fusion1` (NIST post-processed) | [[download](https://www.dropbox.com/s/ojphpgilqs8xexc/expanded.anserini.final-r5.fusion1.post-processed.txt?dl=1)] | `03ad001d94c772649e17f4d164d4b2e2` |
| `anserini` | `r5.d2q.fusion2` (NIST post-processed) | [[download](https://www.dropbox.com/s/q7vx0l8n2u81s7z/expanded.anserini.final-r5.fusion2.post-processed.txt?dl=1)] | `4137c93e76970616e0eff2803501cd08` |
| `anserini` | `r5.d2q.rf` (NIST post-processed)      | [[download](https://www.dropbox.com/s/l4l1bbbi8msmrfh/expanded.anserini.final-r5.rf.post-processed.txt?dl=1)]      | `3dfba85c0630865a7b581c4358cf4587` |

Effectiveness results (note that starting in Round 4, NIST changed from nDCG@10 to nDCG@20):

| group | runtag | nDCG@20 | J@20 | AP   | R@1k |
|:------|:-------|--------:|-----:|-----:|-----:|
| `anserini` | `r5.d2q.fusion1`                       | 0.5374 | 0.8530 | 0.2236 | 0.5798
| `anserini` | `r5.d2q.fusion1` (NIST post-processed) | 0.5414 | 0.8610 | 0.2246 | 0.5798
| `anserini` | `r5.d2q.fusion2`                       | 0.5393 | 0.8650 | 0.2310 | 0.5861
| `anserini` | `r5.d2q.fusion2` (NIST post-processed) | 0.5436 | 0.8700 | 0.2319 | 0.5861
| `anserini` | `r5.d2q.rf`                            | 0.6040 | 0.8370 | 0.2410 | 0.6039
| `anserini` | `r5.d2q.rf` (NIST post-processed)      | 0.6124 | 0.8470 | 0.2433 | 0.6039

The scores of the post-processed runs match those reported by NIST.
We see that that NIST post-processing improves scores slightly.

Below, we report the effectiveness of the runs using the "complete" cumulative qrels file (covering rounds 1 through 5).
This qrels file, provided by NIST as [`qrels-covid_d5_j0.5-5.txt`](https://ir.nist.gov/covidSubmit/data/qrels-covid_d5_j0.5-5.txt), is stored in our repo as [`qrels.covid-complete.txt`](../src/main/resources/topics-and-qrels/qrels.covid-complete.txt)).

|    | index     | field(s)                 | nDCG@10 | J@10 | nDCG@20 | J@20 | AP | R@1k | J@1k |
|---:|:----------|:-------------------------|--------:|-----:|--------:|-----:|---:|-----:|-----:|
|  1 | abstract  | query+question                  | 0.6808 | 0.9980 | 0.6375 | 0.9600 | 0.2718 | 0.4550 | 0.3845
|  2 | abstract  | UDel qgen                       | 0.6939 | 0.9920 | 0.6524 | 0.9610 | 0.2752 | 0.4595 | 0.3825
|  3 | full-text | query+question                  | 0.6300 | 0.9680 | 0.5843 | 0.9260 | 0.2475 | 0.4201 | 0.3921
|  4 | full-text | UDel qgen                       | 0.6611 | 0.9800 | 0.6360 | 0.9610 | 0.2746 | 0.4496 | 0.4073
|  5 | paragraph | query+question                  | 0.6827 | 0.9800 | 0.6477 | 0.9670 | 0.3080 | 0.4936 | 0.4360
|  6 | paragraph | UDel qgen                       | 0.7067 | 0.9960 | 0.6614 | 0.9760 | 0.3127 | 0.4985 | 0.4328
|  7 | -         | reciprocal rank fusion(1, 3, 5) | 0.7072 | 1.0000 | 0.6731 | 0.9920 | 0.2964 | 0.5063 | 0.4528
|  8 | -         | reciprocal rank fusion(2, 4, 6) | 0.7131 | 1.0000 | 0.6755 | 0.9910 | 0.3036 | 0.5166 | 0.4518
|  9 | abstract  | UDel qgen + RF                  | 0.8160 | 1.0000 | 0.7787 | 0.9960 | 0.3421 | 0.5249 | 0.4107

Note that all of the results above can be replicated with the following script:

```bash
$ python src/main/python/trec-covid/download_doc2query_indexes.py --date 2020-07-16
$ python src/main/python/trec-covid/generate_round5_doc2query_baselines.py
```


## Round 4

Document expansion with doc2query was introduced in our round 4 submissions.
The runs below represent correspond to our [TREC-COVID baselines](experiments-covid.md), except on pre-built CORD-19 indexes that have been expanded using doc2query (version from 2020/06/19, the official corpus used in round 4).

|    | index     | field(s)                        | run file | checksum |
|---:|:----------|:--------------------------------|:---------|----------|
|  1 | abstract  | query+question                  | [[download](https://www.dropbox.com/s/yxapvqec9o2ucon/expanded.anserini.covid-r4.abstract.qq.bm25.txt?dl=1)]           | `d1d32cd6962c4e355a47e7f1fdfb0c74` |
|  2 | abstract  | UDel qgen                       | [[download](https://www.dropbox.com/s/vnk3swwwfcncolk/expanded.anserini.covid-r4.abstract.qdel.bm25.txt?dl=1)]         | `55ae93b92bae20ed64fc9f191c6ea667` |
|  3 | full-text | query+question                  | [[download](https://www.dropbox.com/s/pkk3m90bv0rpxru/expanded.anserini.covid-r4.full-text.qq.bm25.txt?dl=1)]          | `512e14c6d15eb36f7fc9c537281badd3` |
|  4 | full-text | UDel qgen                       | [[download](https://www.dropbox.com/s/44hoa9xkf6tv0hq/expanded.anserini.covid-r4.full-text.qdel.bm25.txt?dl=1)]        | `0901d7b083aa28afd431cf330fe7293c` |
|  5 | paragraph | query+question                  | [[download](https://www.dropbox.com/s/z90xag7eh5pi53e/expanded.anserini.covid-r4.paragraph.qq.bm25.txt?dl=1)]          | `f8512ba33d5cc79176d71424d05f81cb` |
|  6 | paragraph | UDel qgen                       | [[download](https://www.dropbox.com/s/eno3z8pi7bnfy2p/expanded.anserini.covid-r4.paragraph.qdel.bm25.txt?dl=1)]        | `123896c0af4cdbae471c21d2da7de1f7` |
|  7 | -         | reciprocal rank fusion(1, 3, 5) | [[download](https://www.dropbox.com/s/zfbt15ivm37tolt/expanded.anserini.covid-r4.fusion1.txt?dl=1)]                    | `77b619a2e6e87852b85d31637ceb6219` |
|  8 | -         | reciprocal rank fusion(2, 4, 6) | [[download](https://www.dropbox.com/s/e7ki5e8jqi718bp/expanded.anserini.covid-r4.fusion2.txt?dl=1)]                    | `1e7bb2a6e483d3629378c3107457b216` |
|  9 | abstract  | UDel qgen + RF                  | [[download](https://www.dropbox.com/s/1uzy5ni33kvxq2o/expanded.anserini.covid-r4.abstract.qdel.bm25%2Brm3Rf.txt?dl=1)] | `b6b1d949fff00e54b13e533e27455731` |

These runs are performed at [`539f7d`](https://github.com/castorini/anserini/commit/539f7d43a0183454a633f34aa20b46d2eeec1a19), 2020/07/24. Note that these runs were created _after_ the round 4 qrels became available, so this is a post-hoc simulation of "what would have happened".

The final runs, after removing judgments from 1, 2, and 3 (cumulatively), are as follows:

| runtag | run file | checksum |
|:-------|:---------|:---------|
| `r4.fusion1` = Row 7 | [[download](https://www.dropbox.com/s/mjgb5lz9ftty1w2/expanded.anserini.final-r4.fusion1.txt?dl=1)] | `ae7513f68e2ca82d8b0efdd244082046` |
| `r4.fusion2` = Row 8 | [[download](https://www.dropbox.com/s/5epunmkexqtupe6/expanded.anserini.final-r4.fusion2.txt?dl=1)] | `590400c12b72ce8ed3b5af2f4c45f039` |
| `r4.rf` = Row 9      | [[download](https://www.dropbox.com/s/kqbu3cui214ijyh/expanded.anserini.final-r4.rf.txt?dl=1)]      | `b9e7bb80fd8dc97f93908d895fb07f7f` |

We have written scripts that automate the replication of these baselines:

```
$ python src/main/python/trec-covid/download_doc2query_indexes.py --date 2020-06-19
$ python src/main/python/trec-covid/generate_round4_doc2query_baselines.py
```

Effectiveness results, based on round 4 qrels:

| group | runtag | nDCG@20 | J@20 | AP   | R@1k |
|:------|:-------|--------:|-----:|-----:|-----:|
| `anserini` | `r4.fusion1` | 0.5115 | 0.6944 | 0.2498 | 0.6717
| `anserini` | `r4.fusion2` | 0.5175 | 0.6911 | 0.2550 | 0.6800
| `anserini` | `r4.rf`      | 0.5606 | 0.6833 | 0.2658 | 0.6759

Below, we report the effectiveness of the runs using the cumulative qrels file from round 4.
This qrels file, provided by NIST as [`qrels_covid_d4_j0.5-4.txt`](https://ir.nist.gov/covidSubmit/data/qrels-covid_d4_j0.5-4.txt), is stored in our repo as [`qrels.covid-round4-cumulative.txt`](../src/main/resources/topics-and-qrels/qrels.covid-round4-cumulative.txt)).

|    | index     | field(s)                 | nDCG@10 | J@10 | nDCG@20 | J@20 | AP | R@1k | J@1k |
|---:|:----------|:-------------------------|--------:|-----:|--------:|-----:|---:|-----:|-----:|
|  1 | abstract  | query+question                  | 0.6115 | 0.8022 | 0.5823 | 0.7900 | 0.2499 | 0.5038 | 0.2676
|  2 | abstract  | UDel qgen                       | 0.6321 | 0.8022 | 0.5922 | 0.7678 | 0.2528 | 0.5098 | 0.2672
|  3 | full-text | query+question                  | 0.6045 | 0.9044 | 0.5640 | 0.8522 | 0.2420 | 0.4996 | 0.3037
|  4 | full-text | UDel qgen                       | 0.6514 | 0.9289 | 0.5991 | 0.8711 | 0.2665 | 0.5240 | 0.3114
|  5 | paragraph | query+question                  | 0.6429 | 0.8622 | 0.6080 | 0.8333 | 0.2932 | 0.5635 | 0.3256
|  6 | paragraph | UDel qgen                       | 0.6694 | 0.8622 | 0.6229 | 0.8411 | 0.2953 | 0.5677 | 0.3232
|  7 | -         | reciprocal rank fusion(1, 3, 5) | 0.6739 | 0.8778 | 0.6188 | 0.8533 | 0.2914 | 0.5750 | 0.3362
|  8 | -         | reciprocal rank fusion(2, 4, 6) | 0.6618 | 0.8622 | 0.6331 | 0.8444 | 0.2974 | 0.5847 | 0.3344
|  9 | abstract  | UDel qgen + RF                  | 0.7447 | 0.8933 | 0.7067 | 0.8589 | 0.3182 | 0.5812 | 0.2904
