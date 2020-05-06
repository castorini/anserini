# TREC-COVID Baselines

This document describes various baselines for the [TREC-COVID Challenge](https://ir.nist.gov/covidSubmit/), which uses the [COVID-19 Open Research Dataset (CORD-19)](https://pages.semanticscholar.org/coronavirus-research) from the [Allen Institute for AI](https://allenai.org/).
Here, we focus on running retrieval experiments; for basic instructions on building Anserini indexes, see [this page](experiments-cord19.md).

## Round 2

tl;dr - here are the runs that can be easily replicated with Anserini, from pre-built indexes available [here](experiments-cord19.md#pre-built-indexes-all-versions):

|    | index     | field(s)                 | nDCG@10 | Recall@1000 | run file |
|---:|:----------|:-------------------------|--------:|------------:|:---------|
|  1 | abstract  | query+question           |  0.3522 | 0.6601 | [[download]](https://www.dropbox.com/s/duimcackueph2co/anserini.covid-r2.abstract.qq.bm25.txt.gz)
|  2 | abstract  | query (UDel)             |  0.3781 | 0.6485 | [[download]](https://www.dropbox.com/s/n9yfssge5asez74/anserini.covid-r2.abstract.qdel.bm25.txt.gz)
|  3 | full-text | query+question           |  0.2070 | 0.5953 | [[download]](https://www.dropbox.com/s/iswpuj9tf5pj5ei/anserini.covid-r2.full-text.qq.bm25.txt.gz)
|  4 | full-text | query (UDel)             |  0.3123 | 0.6517 | [[download]](https://www.dropbox.com/s/bj93a4iddpfvp09/anserini.covid-r2.full-text.qdel.bm25.txt.gz)
|  5 | paragraph | query+question           |  0.2772 | 0.7248 | [[download]](https://www.dropbox.com/s/da7jg1ho5ubl8jt/anserini.covid-r2.paragraph.qq.bm25.txt.gz)
|  6 | paragraph | query (UDel)             |  0.3353 | 0.7196 | [[download]](https://www.dropbox.com/s/7hplgsdq7ndn2ql/anserini.covid-r2.paragraph.qdel.bm25.txt.gz)
|  7 | -         | reciprocal rank fusion(1, 3, 5) | 0.3297 | 0.7561 | [[download]](https://www.dropbox.com/s/wqb0vhxp98g7dxh/anserini.covid-r2.fusion1.txt.gz)
|  8 | -         | reciprocal rank fusion(2, 4, 6) | 0.3679 | 0.7511 | [[download]](https://www.dropbox.com/s/cd1ps4au79wvb8j/anserini.covid-r2.fusion2.txt.gz)

**IMPORTANT NOTES!!!**

+ These runs are performed at [`39c9a92`](https://github.com/castorini/anserini/commit/39c9a92a957b9c444fe0dc89f4560f3f5a3b612f), at the release of Anserini 0.9.1.
+ The evaluation numbers are produced with round 1 qrels on the round 2 collection (release of 5/1).
+ The above runs **do not** conform to NIST's residual collection guidelines. That is, those runs **include** documents from the round 1 qrels. If you use these runs as the basis for reranking, you **must** make sure you conform to the [official round 2 guidelines](https://ir.nist.gov/covidSubmit/round2.html) from NIST. The reason for keeping documents from round 1 is so that it is possible to know the score distribution of relevant and non-relevant documents with respect to the new corpus.
+ The above runs provide up to 10k hits for each topic (sometimes less because of deduping). A cautionary note: our experience is that choosing the top _k_ documents to rerank has a large impact on end-to-end effectiveness. Reranking the top 100 seems to provide higher precision than top 1000, but the likely tradeoff is lower recall (although with such shallow pools currently, it's hard to tell). It is very likely the case that you _don't_ want to rerank all 10k hits.

Exact commands for replicating these runs are found [further down on this page](experiments-covid.md#round-2-replication-commands).

## Round 1

tl;dr - here are the runs that can be easily replicated with Anserini, from pre-built indexes available [here](experiments-cord19.md#pre-built-indexes-all-versions):

|    | index     | field(s)                 | nDCG@10 | Recall@1000 |
|---:|:----------|:-------------------------|--------:|------------:|
|  1 | abstract  | query                    |  0.4100 | 0.5279 |
|  2 | abstract  | question                 |  0.5179 | 0.6313 |
|  3 | abstract  | query+question           |  0.5514 | 0.6989 |
|  4 | abstract  | query+question+narrative |  0.5294 | 0.6929 |
|  5 | abstract  | query (UDel)             |  0.5824 | 0.6927 |
|  6 | full-text | query                    |  0.3900 | 0.6277 |
|  7 | full-text | question                 |  0.3439 | 0.6389 |
|  8 | full-text | query+question           |  0.4064 | 0.6714 |
|  9 | full-text | query+question+narrative |  0.3280 | 0.6591 |
| 10 | full-text | query (UDel)             |  0.5407 | 0.7214 |
| 11 | paragraph | query                    |  0.4302 | 0.4327 |
| 12 | paragraph | question                 |  0.4410 | 0.5111 |
| 13 | paragraph | query+question           |  0.5450 | 0.5743 |
| 14 | paragraph | query+question+narrative |  0.4899 | 0.5918 |
| 15 | paragraph | query (UDel)             |  0.5544 | 0.5640 |
| 16 | -         | reciprocal rank fusion(3, 8, 13)  | 0.5716 | 0.8117 |
| 17 | -         | reciprocal rank fusion(5, 10, 15) | 0.6019 | 0.8121 |

The "query (UDel)" condition represents the query generator from run [`udel_fang_run3`](https://ir.nist.gov/covidSubmit/archive/round1/udel_fang_run3.pdf), contributed to the repo as part of commit [`0d4bcd5`](https://github.com/castorini/anserini/commit/0d4bcd55370295ff72605d718dbab5be40d246d9).
Ablation analyses by [lukuang](https://github.com/lukuang) revealed that the query generator provides the greatest contribution, and results above exceed `udel_fang_run3` (thus making exact replication unnecessary).

For reference, the best automatic run is run [`sab20.1.meta.docs`](https://ir.nist.gov/covidSubmit/archive/round1/sab20.1.meta.docs.pdf) with nDCG@10 0.6080.

Why report nDCG@10 and Recall@1000?
The first is one of the metrics used by the organizers.
Given the pool depth of seven, nDCG@10 should be okay-ish, from the perspective of missing judgments, and nDCG is better than P@k since it captures relevance grades.
Average precision is _not_ included intentionally because of the shallow judgment pool, and hence likely to be very noisy.
Recall@1000 captures the upper bound potential of downstream rerankers.
Note that recall under the paragraph index isn't very good because of duplicates.
Multiple paragraphs from the same article are retrieved, and duplicates are discarded; we start with top 1k hits, but end up with far fewer results per topic.

Caveats:

+ These runs represent, essentially, testing on training data. Beware of generalization or lack thereof.
+ Beware of unjudged documents.

TODO:

+ Run query expansion.
+ Run different fusion techniques.

Exact commands for replicating these runs are found [further down on this page](experiments-covid.md#round-1-replication-commands).

## Round 2: Replication Commands

Here are the replication commands for the individual runs:

```bash
wget https://www.dropbox.com/s/wxjoe4g71zt5za2/lucene-index-cord19-abstract-2020-05-01.tar.gz
tar xvfz lucene-index-cord19-abstract-2020-05-01.tar.gz

target/appassembler/bin/SearchCollection -index lucene-index-cord19-abstract-2020-05-01 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round2.xml -topicfield query+question -removedups \
 -bm25 -hits 10000 -output runs/anserini.covid-r2.abstract.qq.bm25.txt -runtag anserini.covid-r2.abstract.qq.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-cord19-abstract-2020-05-01 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round2-udel.xml -topicfield query -removedups \
 -bm25 -hits 10000 -output runs/anserini.covid-r2.abstract.qdel.bm25.txt -runtag anserini.covid-r2.abstract.qdel.bm25.txt

eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/anserini.covid-r2.abstract.qq.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/anserini.covid-r2.abstract.qdel.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'

wget https://www.dropbox.com/s/di27r5o2g5kat5k/lucene-index-cord19-full-text-2020-05-01.tar.gz
tar xvfz lucene-index-cord19-full-text-2020-05-01.tar.gz

target/appassembler/bin/SearchCollection -index lucene-index-cord19-full-text-2020-05-01 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round2.xml -topicfield query+question -removedups \
 -bm25 -hits 10000 -output runs/anserini.covid-r2.full-text.qq.bm25.txt -runtag anserini.covid-r2.full-text.qq.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-cord19-full-text-2020-05-01 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round2-udel.xml -topicfield query -removedups \
 -bm25 -hits 10000 -output runs/anserini.covid-r2.full-text.qdel.bm25.txt -runtag anserini.covid-r2.full-text.qdel.bm25.txt

eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/anserini.covid-r2.full-text.qq.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/anserini.covid-r2.full-text.qdel.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'

wget https://www.dropbox.com/s/6ib71scm925mclk/lucene-index-cord19-paragraph-2020-05-01.tar.gz
tar xvfz lucene-index-cord19-paragraph-2020-05-01.tar.gz

target/appassembler/bin/SearchCollection -index lucene-index-cord19-paragraph-2020-05-01 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round2.xml -topicfield query+question -removedups -strip_segment_id \
 -bm25 -hits 10000 -output runs/anserini.covid-r2.paragraph.qq.bm25.txt -runtag anserini.covid-r2.paragraph.qq.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-cord19-paragraph-2020-05-01 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round2-udel.xml -topicfield query -removedups -strip_segment_id \
 -bm25 -hits 10000 -output runs/anserini.covid-r2.paragraph.qdel.bm25.txt -runtag anserini.covid-r2.paragraph.qdel.bm25.txt

eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/anserini.covid-r2.paragraph.qq.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/anserini.covid-r2.paragraph.qdel.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
```

To generate the fusion run:

```python
from trectools import TrecRun, TrecEval, fusion

r1 = TrecRun("runs/anserini.covid-r2.abstract.qq.bm25.txt")
r2 = TrecRun("runs/anserini.covid-r2.full-text.qq.bm25.txt")
r3 = TrecRun("runs/anserini.covid-r2.paragraph.qq.bm25.txt")

fused_run = fusion.reciprocal_rank_fusion([r1,r2,r3])
fused_run.print_subset("runs/anserini.covid-r2.fusion1.txt", topics=fused_run.topics())

r4 = TrecRun("runs/anserini.covid-r2.abstract.qdel.bm25.txt")
r5 = TrecRun("runs/anserini.covid-r2.full-text.qdel.bm25.txt")
r6 = TrecRun("runs/anserini.covid-r2.paragraph.qdel.bm25.txt")

fused_run = fusion.reciprocal_rank_fusion([r4,r5,r6])
fused_run.print_subset("runs/anserini.covid-r2.fusion2.txt", topics=fused_run.topics())
```

And to evalute the fusion runs:

```bash
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/anserini.covid-r2.fusion1.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/anserini.covid-r2.fusion2.txt | egrep '(ndcg_cut_10 |recall_1000 )'
```


## Round 1: Replication Commands

Here are the commands to generate the runs on the abstract index:

```bash
wget https://www.dropbox.com/s/j55t617yhvmegy8/lucene-index-covid-2020-04-10.tar.gz

tar xvfz lucene-index-covid-2020-04-10.tar.gz

target/appassembler/bin/SearchCollection -index lucene-index-covid-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield query -removedups \
 -bm25 -output runs/run.covid-r1.abstract.query.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-covid-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield question -removedups \
 -bm25 -output runs/run.covid-r1.abstract.question.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-covid-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield query+question -removedups \
 -bm25 -output runs/run.covid-r1.abstract.query+question.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-covid-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield query+question+narrative -removedups \
 -bm25 -output runs/run.covid-r1.abstract.query+question+narrative.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-covid-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1-udel.xml -topicfield query -removedups \
 -bm25 -output runs/run.covid-r1.abstract.query-udel.bm25.txt
```

Here are the commands to evaluate results on the abstract index:

```bash
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.abstract.query.bm25.txt | grep 'ndcg_cut_10 '
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.abstract.question.bm25.txt | grep 'ndcg_cut_10 '
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.abstract.query+question.bm25.txt | grep 'ndcg_cut_10 '
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.abstract.query+question+narrative.bm25.txt | grep 'ndcg_cut_10 '
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.abstract.query-udel.bm25.txt | grep 'ndcg_cut_10 '
```

Here are the commands to generate the runs on the full-text index:

```bash
wget https://www.dropbox.com/s/gtq2c3xq81mjowk/lucene-index-covid-full-text-2020-04-10.tar.gz

tar xvfz lucene-index-covid-full-text-2020-04-10.tar.gz

target/appassembler/bin/SearchCollection -index lucene-index-covid-full-text-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield query -removedups \
 -bm25 -output runs/run.covid-r1.full-text.query.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-covid-full-text-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield question -removedups \
 -bm25 -output runs/run.covid-r1.full-text.question.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-covid-full-text-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield query+question -removedups \
 -bm25 -output runs/run.covid-r1.full-text.query+question.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-covid-full-text-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield query+question+narrative -removedups \
 -bm25 -output runs/run.covid-r1.full-text.query+question+narrative.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-covid-full-text-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1-udel.xml -topicfield query -removedups \
 -bm25 -output runs/run.covid-r1.full-text.query-udel.bm25.txt
```

Here are the commands to evaluate results on the full-text index:

```bash
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.full-text.query.bm25.txt | grep 'ndcg_cut_10 '
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.full-text.question.bm25.txt | grep 'ndcg_cut_10 '
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.full-text.query+question.bm25.txt | grep 'ndcg_cut_10 '
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.full-text.query+question+narrative.bm25.txt | grep 'ndcg_cut_10 '
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.full-text.query-udel.bm25.txt | grep 'ndcg_cut_10 '
```

Here are the commands to generate the runs on the paragraph index:

```bash
wget https://www.dropbox.com/s/ivk87journyajw3/lucene-index-covid-paragraph-2020-04-10.tar.gz

tar xvfz lucene-index-covid-paragraph-2020-04-10.tar.gz

target/appassembler/bin/SearchCollection -index lucene-index-covid-paragraph-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield query -removedups -strip_segment_id \
 -bm25 -output runs/run.covid-r1.paragraph.query.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-covid-paragraph-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield question -removedups -strip_segment_id \
 -bm25 -output runs/run.covid-r1.paragraph.question.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-covid-paragraph-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield query+question -removedups -strip_segment_id \
 -bm25 -output runs/run.covid-r1.paragraph.query+question.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-covid-paragraph-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield query+question+narrative -removedups -strip_segment_id \
 -bm25 -output runs/run.covid-r1.paragraph.query+question+narrative.bm25.txt

target/appassembler/bin/SearchCollection -index lucene-index-covid-paragraph-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1-udel.xml -topicfield query -removedups -strip_segment_id \
 -bm25 -output runs/run.covid-r1.paragraph.query-udel.bm25.txt
```

Here are the commands to evaluate results on the paragraph index:

```bash
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.paragraph.query.bm25.txt | grep 'ndcg_cut_10 '
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.paragraph.question.bm25.txt | grep 'ndcg_cut_10 '
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.paragraph.query+question.bm25.txt | grep 'ndcg_cut_10 '
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.paragraph.query+question+narrative.bm25.txt | grep 'ndcg_cut_10 '
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.paragraph.query-udel.bm25.txt | grep 'ndcg_cut_10 '
```

Here are the Python commands to generate the fusion runs, using [`trectools`](https://github.com/joaopalotti/trectools):

```python
from trectools import TrecRun, TrecEval, fusion

r1 = TrecRun("runs/run.covid-r1.abstract.query+question.bm25.txt")
r2 = TrecRun("runs/run.covid-r1.full-text.query+question.bm25.txt")
r3 = TrecRun("runs/run.covid-r1.paragraph.query+question.bm25.txt")

fused_run = fusion.reciprocal_rank_fusion([r1,r2,r3])
fused_run.print_subset("runs/run.covid-r1.fusion1.txt", topics=fused_run.topics())

r4 = TrecRun("runs/run.covid-r1.abstract.query-udel.bm25.txt")
r5 = TrecRun("runs/run.covid-r1.full-text.query-udel.bm25.txt")
r6 = TrecRun("runs/run.covid-r1.paragraph.query-udel.bm25.txt")

fused_run = fusion.reciprocal_rank_fusion([r4,r5,r6])
fused_run.print_subset("runs/run.covid-r1.fusion2.txt", topics=fused_run.topics())
```

And to evalute the fusion runs:

```bash
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.fusion1.txt | grep 'ndcg_cut_10 '
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.fusion2.txt | grep 'ndcg_cut_10 '
```
