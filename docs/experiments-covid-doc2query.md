# TREC-COVID doc2query Baselines

This document describes various doc2query baselines for the [TREC-COVID Challenge](https://ir.nist.gov/covidSubmit/), which uses the [COVID-19 Open Research Dataset (CORD-19)](https://pages.semanticscholar.org/coronavirus-research) from the [Allen Institute for AI](https://allenai.org/).
Here, we focus on running retrieval experiments; for basic instructions on building Anserini indexes, see [this page](experiments-cord19.md) and for instructions specific to building doc2query expanded Anserini indexes, see [this page](https://github.com/castorini/docTTTTTquery/).

## Round 5

These are runs that can be easily replicated with Anserini, from pre-built doc2query expanded CORD-19 indexes we have provided (version from 2020/07/16, which is the official corpus used in round 5).
They were prepared _for_ round 5 (for participants who wish to have a baseline run to rerank); to provide a sense of effectiveness, we present evaluation results with the cumulative qrels from rounds 1, 2, 3, and 4 ([`qrels_covid_d4_j0.5-4.txt`](https://ir.nist.gov/covidSubmit/data/qrels-covid_d4_j0.5-4.txt) provided by NIST, stored in our repo as [`qrels.covid-round4-cumulative.txt`](../src/main/resources/topics-and-qrels/qrels.covid-round4-cumulative.txt)).

|    | index     | field(s)                        | nDCG@10 | J@10 | R@1k | run file | checksum |
|---:|:----------|:--------------------------------|--------:|-----:|-----:|:---------|----------|
|  1 | abstract  | query+question                  | 0.4635 | 0.5300 | 0.4462 | [[download](https://www.dropbox.com/s/sa6abjrk1esxn38/expanded.anserini.covid-r5.abstract.qq.bm25.txt)]    | `9923233a31ac004f84b7d563baf6543c` |
|  2 | abstract  | UDel qgen                       | 0.4548 | 0.5000 | 0.4527 | [[download](https://www.dropbox.com/s/t3s3oj9g0b1nphk/expanded.anserini.covid-r5.abstract.qdel.bm25.txt)]  | `e0c7a1879e5b1742045bba0f5293d558` |
|  3 | full-text | query+question                  | 0.4450 | 0.6020 | 0.4473 | [[download](https://www.dropbox.com/s/utvw91nluzwm3ex/expanded.anserini.covid-r5.full-text.qq.bm25.txt)]   | `78aa7f481de91d22192163ed934d02ee` |
|  4 | full-text | UDel qgen                       | 0.4817 | 0.6040 | 0.4711 | [[download](https://www.dropbox.com/s/xk2jyiwh5fjdwst/expanded.anserini.covid-r5.full-text.qdel.bm25.txt)] | `51cbae025bf90dadf8f26c5c31af9f66` |
|  5 | paragraph | query+question                  | 0.4904 | 0.5820 | 0.5004 | [[download](https://www.dropbox.com/s/rjbyljcpziv31xx/expanded.anserini.covid-r5.paragraph.qq.bm25.txt)]   | `0b80444c8a737748ba9199ddf0795421` |
|  6 | paragraph | UDel qgen                       | 0.4940 | 0.5700 | 0.5070 | [[download](https://www.dropbox.com/s/f4h2jhhla4o26wr/expanded.anserini.covid-r5.paragraph.qdel.bm25.txt)] | `2040b9a4759af722d50610f26989c328` |
|  7 | -         | reciprocal rank fusion(1, 3, 5) | 0.4908 | 0.5880 | 0.5119 | [[download](https://www.dropbox.com/s/bj00pfwngi2j2g1/expanded.anserini.covid-r5.fusion1.txt)]             | `c0ffc7b1719f64d2f37ce99a9ef0413c` |
|  8 | -         | reciprocal rank fusion(2, 4, 6) | 0.4846 | 0.5740 | 0.5218 | [[download](https://www.dropbox.com/s/f5ro0ex38gkvnqc/expanded.anserini.covid-r5.fusion2.txt)]             | `329f13267abf3f3d429a1593c1bd862f` |
|  9 | abstract  | UDel qgen + RF                  | 0.6095 | 0.6320 | 0.5280 | [[download](https://www.dropbox.com/s/j6op32bcaszd1up/expanded.anserini.covid-r5.abstract.qdel.bm25%2Brm3Rf.txt)] | `a5e016c84d5547519ffbcf74c9a24fc8` |

**IMPORTANT NOTES!!!**

+ These runs are performed at [`539f7d4`](https://github.com/castorini/anserini/commit/539f7d43a0183454a633f34aa20b46d2eeec1a19).
+ J@10 refers to Judged@10 and R@1k refers to Recall@1000.
+ The evaluation numbers are produced with the NIST-prepared cumulative qrels from rounds 1, 2, 3, and 4 ([`qrels_covid_d4_j0.5-4.txt`](https://ir.nist.gov/covidSubmit/data/qrels-covid_d4_j0.5-4.txt) provided by NIST, stored in our repo as [`qrels.covid-round4-cumulative.txt`](../src/main/resources/topics-and-qrels/qrels.covid-round4-cumulative.txt)) on the round 5 collection (release of 7/16).
+ For the abstract and full-text indexes, we request up to 10k hits for each topic; the number of actual hits retrieved is fairly close to this (a bit less because of deduping). For the paragraph index, we request up to 50k hits for each topic; because multiple paragraphs are retrieved from the same document, the number of unique documents in each list of hits is much smaller. A cautionary note: our experience is that choosing the top _k_ documents to rerank has a large impact on end-to-end effectiveness. Reranking the top 100 seems to provide higher precision than top 1000, but the likely tradeoff is lower recall. It is very likely the case that you _don't_ want to rerank all available hits.
+ Row 9 represents the feedback baseline condition introduced in round 3: abstract index, UDel query generator, BM25+RM3 relevance feedback (100 feedback terms).

The final runs after removing judgments from 1, 2, 3, and 4 (cumulatively), are as follows:

| runtag | run file | checksum |
|:-------|:---------|:---------|
| `r5.fusion1` = Row 7 | [[download](https://www.dropbox.com/s/5ke2c4x2z8de31h/expanded.anserini.final-r5.fusion1.txt)] | `2295216ed623d2621f00c294f7c389e1` |
| `r5.fusion2` = Row 8 | [[download](https://www.dropbox.com/s/j1qdqr88cbsybae/expanded.anserini.final-r5.fusion2.txt)] | `a65fabe7b5b7bc4216be632296269ce6` |
| `r5.rf` = Row 9      | [[download](https://www.dropbox.com/s/5bm4pdngh5bx3px/expanded.anserini.final-r5.rf.txt)]      | `24f0b75a25273b7b00d3e65065e98147` |

We have written scripts that automate the replication of these baselines:

```
$ python src/main/python/trec-covid/download_doc2query_indexes.py --date 2020-07-16
$ python src/main/python/trec-covid/generate_round5_doc2query_baselines.py
```


## Round 4

These are runs that can be easily replicated with Anserini, from pre-built doc2query expanded CORD-19 indexes we have provided (version from 2020/06/19, which is the official corpus used in round 4).
To provide a sense of effectiveness, we present evaluation results with the cumulative qrels from rounds 1, 2, and 3 ([`qrels_covid_d3_j0.5-3.txt`](https://ir.nist.gov/covidSubmit/data/qrels-covid_d3_j0.5-3.txt) provided by NIST, stored in our repo as [`qrels.covid-round3-cumulative.txt`](../src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt)).

|    | index     | field(s)                        | nDCG@10 | J@10 | R@1k | run file | checksum |
|---:|:----------|:--------------------------------|--------:|-----:|-----:|:---------|----------|
|  1 | abstract  | query+question                  | 0.3249 | 0.3933 | 0.4264 | [[download](https://www.dropbox.com/s/yxapvqec9o2ucon/expanded.anserini.covid-r4.abstract.qq.bm25.txt)]    | `d1d32cd6962c4e355a47e7f1fdfb0c74` |
|  2 | abstract  | UDel qgen                       | 0.3301 | 0.3733 | 0.4288 | [[download](https://www.dropbox.com/s/vnk3swwwfcncolk/expanded.anserini.covid-r4.abstract.qdel.bm25.txt)]  | `55ae93b92bae20ed64fc9f191c6ea667` |
|  3 | full-text | query+question                  | 0.3302 | 0.4511 | 0.4556 | [[download](https://www.dropbox.com/s/pkk3m90bv0rpxru/expanded.anserini.covid-r4.full-text.qq.bm25.txt)]   | `512e14c6d15eb36f7fc9c537281badd3` |
|  4 | full-text | UDel qgen                       | 0.3565 | 0.4356 | 0.4789 | [[download](https://www.dropbox.com/s/44hoa9xkf6tv0hq/expanded.anserini.covid-r4.full-text.qdel.bm25.txt)] | `0901d7b083aa28afd431cf330fe7293c` |
|  5 | paragraph | query+question                  | 0.3638 | 0.4533 | 0.5062 | [[download](https://www.dropbox.com/s/z90xag7eh5pi53e/expanded.anserini.covid-r4.paragraph.qq.bm25.txt)]   | `f8512ba33d5cc79176d71424d05f81cb` |
|  6 | paragraph | UDel qgen                       | 0.3665 | 0.4311 | 0.5062 | [[download](https://www.dropbox.com/s/eno3z8pi7bnfy2p/expanded.anserini.covid-r4.paragraph.qdel.bm25.txt)] | `123896c0af4cdbae471c21d2da7de1f7` |
|  7 | -         | reciprocal rank fusion(1, 3, 5) | 0.3781 | 0.4600 | 0.5112 | [[download](https://www.dropbox.com/s/zfbt15ivm37tolt/expanded.anserini.covid-r4.fusion1.txt)]             | `77b619a2e6e87852b85d31637ceb6219` |
|  8 | -         | reciprocal rank fusion(2, 4, 6) | 0.3692 | 0.4378 | 0.5182 | [[download](https://www.dropbox.com/s/e7ki5e8jqi718bp/expanded.anserini.covid-r4.fusion2.txt)]             | `1e7bb2a6e483d3629378c3107457b216` |
|  9 | abstract  | UDel qgen + RF                  | 0.4469 | 0.4444 | 0.5172 | [[download](https://www.dropbox.com/s/1uzy5ni33kvxq2o/expanded.anserini.covid-r4.abstract.qdel.bm25%2Brm3Rf.txt)] | `b6b1d949fff00e54b13e533e27455731` |

**IMPORTANT NOTES!!!**

+ These runs are performed at [`539f7d4`](https://github.com/castorini/anserini/commit/539f7d43a0183454a633f34aa20b46d2eeec1a19).
+ J@10 refers to Judged@10 and R@1k refers to Recall@1000.
+ The evaluation numbers are produced with the NIST-prepared cumulative qrels from rounds 1, 2, and 3 ([`qrels_covid_d3_j0.5-3.txt`](https://ir.nist.gov/covidSubmit/data/qrels-covid_d3_j0.5-3.txt) provided by NIST, stored in our repo as [`qrels.covid-round3-cumulative.txt`](../src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt)) on the round 4 collection (release of 6/19).
+ For the abstract and full-text indexes, we request up to 10k hits for each topic; the number of actual hits retrieved is fairly close to this (a bit less because of deduping). For the paragraph index, we request up to 50k hits for each topic; because multiple paragraphs are retrieved from the same document, the number of unique documents in each list of hits is much smaller. A cautionary note: our experience is that choosing the top _k_ documents to rerank has a large impact on end-to-end effectiveness. Reranking the top 100 seems to provide higher precision than top 1000, but the likely tradeoff is lower recall. It is very likely the case that you _don't_ want to rerank all available hits.
+ Row 9 represents the feedback baseline condition introduced in round 3: abstract index, UDel query generator, BM25+RM3 relevance feedback (100 feedback terms).

The final runs, after removing judgments from 1, 2, and 3 (cumulatively), are as follows:

| runtag | run file | checksum |
|:-------|:---------|:---------|
| `r4.fusion1` = Row 7 | [[download](https://www.dropbox.com/s/mjgb5lz9ftty1w2/expanded.anserini.final-r4.fusion1.txt)] | `ae7513f68e2ca82d8b0efdd244082046` |
| `r4.fusion2` = Row 8 | [[download](https://www.dropbox.com/s/5epunmkexqtupe6/expanded.anserini.final-r4.fusion2.txt)] | `590400c12b72ce8ed3b5af2f4c45f039` |
| `r4.rf` = Row 9      | [[download](https://www.dropbox.com/s/kqbu3cui214ijyh/expanded.anserini.final-r4.rf.txt)]      | `b9e7bb80fd8dc97f93908d895fb07f7f` |

We have written scripts that automate the replication of these baselines:

```
$ python src/main/python/trec-covid/download_doc2query_indexes.py --date 2020-06-19
$ python src/main/python/trec-covid/generate_round4_doc2query_baselines.py
```


