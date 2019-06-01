# Anserini: Lucene 7 vs. Lucene 8

Experiments performed in late April 2019 on an Intel E5-2699 v4 @ 2.20GHz processor, single thread.
Query evaluation latency on the ClueWeb12-B13 collection, running the first 10k queries from the [TREC 2005 Terabyte Track efficiency queries](https://trec.nist.gov/data/terabyte05.html):

Hits       | Lucene 7.6 | Lucene 8.0 | speedup |
----------:|-----------:|-----------:|--------:|
10 hits    |      2885s |       654s |    ~3.5 |
100 hits   |      3209s |      1161s |    ~2.8 |
1000 hits  |      5691s |      4050s |    ~1.4 |

Results are averaged over three trials, after discarding a warmup run.

