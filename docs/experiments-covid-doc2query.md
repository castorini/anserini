# TREC-COVID Doc2Query Baselines

This document describes various doc2query baselines for the [TREC-COVID Challenge](https://ir.nist.gov/covidSubmit/), which uses the [COVID-19 Open Research Dataset (CORD-19)](https://pages.semanticscholar.org/coronavirus-research) from the [Allen Institute for AI](https://allenai.org/).
Here, we focus on running retrieval experiments; for basic instructions on building Anserini indexes, see [this page](experiments-cord19.md) and for instructions specific to building doc2query expanded Anserini indexes, see [this page](https://github.com/castorini/docTTTTTquery/).

## Round 4

These are runs that can be easily replicated with Anserini, from pre-built doc2query expanded CORD-19 indexes we have provided (version from 2020/06/19, which is the official corpus used in round 4).
To provide a sense of effectiveness, we present evaluation results with the cumulative qrels from rounds 1, 2, and 3 ([`qrels_covid_d3_j0.5-3.txt`](https://ir.nist.gov/covidSubmit/data/qrels-covid_d3_j0.5-3.txt) provided by NIST, stored in our repo as [`qrels.covid-round3-cumulative.txt`](../src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt)).

|    | index     | field(s)                        | nDCG@10 | J@10 | R@1k | run file | checksum |
|---:|:----------|:--------------------------------|--------:|-----:|-----:|:---------|----------|
|  1 | abstract  | query+question                  | 0.3249 | 0.3933 | 0.4264 | [[download](https://www.dropbox.com/s/mf79huhxfy96g6i/anserini.covid-r4.abstract.qq.bm25.txt)]    | `d1d32cd6962c4e355a47e7f1fdfb0c74` |
|  2 | abstract  | UDel qgen                       | 0.3301 | 0.3733 | 0.4288 | [[download](https://www.dropbox.com/s/4zau6ejrkvgn9m7/anserini.covid-r4.abstract.qdel.bm25.txt)]  | `55ae93b92bae20ed64fc9f191c6ea667` |
|  3 | full-text | query+question                  | 0.3302 | 0.4511 | 0.4556 | [[download](https://www.dropbox.com/s/bpdopie6gqffv0w/anserini.covid-r4.full-text.qq.bm25.txt)]   | `512e14c6d15eb36f7fc9c537281badd3` |
|  4 | full-text | UDel qgen                       | 0.3565 | 0.4356 | 0.4789 | [[download](https://www.dropbox.com/s/rh0uy71ogbpas0v/anserini.covid-r4.full-text.qdel.bm25.txt)] | `0901d7b083aa28afd431cf330fe7293c` |
|  5 | paragraph | query+question                  | 0.3638 | 0.4533 | 0.5062 | [[download](https://www.dropbox.com/s/ifkjm8ff8g2aoh1/anserini.covid-r4.paragraph.qq.bm25.txt)]   | `f8512ba33d5cc79176d71424d05f81cb` |
|  6 | paragraph | UDel qgen                       | 0.3665 | 0.4311 | 0.5062 | [[download](https://www.dropbox.com/s/keuogpx1dzinsgy/anserini.covid-r4.paragraph.qdel.bm25.txt)] | `123896c0af4cdbae471c21d2da7de1f7` |
|  7 | -         | reciprocal rank fusion(1, 3, 5) | 0.3781 | 0.4600 | 0.5112 | [[download](https://www.dropbox.com/s/zjc0069do0a4gu3/anserini.covid-r4.fusion1.txt)]             | `77b619a2e6e87852b85d31637ceb6219` |
|  8 | -         | reciprocal rank fusion(2, 4, 6) | 0.3692 | 0.4378 | 0.5182 | [[download](https://www.dropbox.com/s/qekc9vr3oom777n/anserini.covid-r4.fusion2.txt)]             | `1e7bb2a6e483d3629378c3107457b216` |
|  9 | abstract  | UDel qgen + RF                  | 0.4469 | 0.4444 | 0.5172 | [[download](https://www.dropbox.com/s/2jx27rh3lknps9q/anserini.covid-r4.abstract.qdel.bm25%2Brm3Rf.txt)] | `b6b1d949fff00e54b13e533e27455731` |

**IMPORTANT NOTES!!!**

+ These runs are performed at [`539f7d4`](https://github.com/castorini/anserini/commit/539f7d43a0183454a633f34aa20b46d2eeec1a19).
+ J@10 refers to Judged@10 and R@1k refers to Recall@1000.
+ The evaluation numbers are produced with the NIST-prepared cumulative qrels from rounds 1, 2, and 3 ([`qrels_covid_d3_j0.5-3.txt`](https://ir.nist.gov/covidSubmit/data/qrels-covid_d3_j0.5-3.txt) provided by NIST, stored in our repo as [`qrels.covid-round3-cumulative.txt`](../src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt)) on the round 4 collection (release of 6/19).
+ For the abstract and full-text indexes, we request up to 10k hits for each topic; the number of actual hits retrieved is fairly close to this (a bit less because of deduping). For the paragraph index, we request up to 50k hits for each topic; because multiple paragraphs are retrieved from the same document, the number of unique documents in each list of hits is much smaller. A cautionary note: our experience is that choosing the top _k_ documents to rerank has a large impact on end-to-end effectiveness. Reranking the top 100 seems to provide higher precision than top 1000, but the likely tradeoff is lower recall. It is very likely the case that you _don't_ want to rerank all available hits.
+ Row 9 represents the feedback baseline condition introduced in round 3: abstract index, UDel query generator, BM25+RM3 relevance feedback (100 feedback terms).

The final runs, after removing judgments from 1, 2, and 3 (cumulatively), are as follows:

| runtag | run file | checksum |
|:-------|:---------|:---------|
| `expanded.r4.fusion1` = Row 7 | [[download](https://www.dropbox.com/s/g3giixyusk4tzro/anserini.final-r4.fusion1.txt)] | `ae7513f68e2ca82d8b0efdd244082046` |
| `expanded.r4.fusion2` = Row 8 | [[download](https://www.dropbox.com/s/z4wbqj9gfos8wln/anserini.final-r4.fusion2.txt)] | `590400c12b72ce8ed3b5af2f4c45f039` |
| `expanded.r4.rf` = Row 9      | [[download](https://www.dropbox.com/s/28w83b07yzndlbg/anserini.final-r4.rf.txt)]      | `b9e7bb80fd8dc97f93908d895fb07f7f` |

We have written scripts that automate the replication of these baselines:

```
$ python src/main/python/trec-covid/download_doc2query_indexes.py --date 2020-06-19
$ python src/main/python/trec-covid/generate_round4_doc2query_baselines.py
```


