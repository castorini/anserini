# TREC-COVID Baselines

This document describes various baselines for the [TREC-COVID Challenge](https://ir.nist.gov/covidSubmit/), which uses the [COVID-19 Open Research Dataset (CORD-19)](https://pages.semanticscholar.org/coronavirus-research) from the [Allen Institute for AI](https://allenai.org/).
Here, we focus on running retrieval experiments; for basic instructions on building Anserini indexes, see [this page](experiments-cord19.md).

## Round 3

These are runs that can be easily replicated with Anserini, from pre-built indexes available [here](experiments-cord19.md#pre-built-indexes-all-versions) (version from 2020/05/19, which is the official corpus used in the evaluation).
They were prepared _for_ round 3 (for participants who wish to have a baseline run to rerank), and so effectiveness is computed with the union of round 1 and round 2 qrels.

|    | index     | field(s)                 | nDCG@10 | Judged@10 | Recall@1000 | run file | checksum |
|---:|:----------|:-------------------------|--------:|----------:|------------:|:---------|----------|
|  1 | abstract  | query+question           | 0.2118 | 0.3300 | 0.4398 | [[download](https://www.dropbox.com/s/g80cqdxud1l06wq/anserini.covid-r3.abstract.qq.bm25.txt)]    | `d08d85c87e30d6c4abf54799806d282f` |
|  2 | abstract  | UDel query generator     | 0.2470 | 0.3375 | 0.4537 | [[download](https://www.dropbox.com/s/sjcnxq7h0a3j3xz/anserini.covid-r3.abstract.qdel.bm25.txt)]  | `d552dff90995cd860a5727637f0be4d1` |
|  3 | full-text | query+question           | 0.2337 | 0.4650 | 0.4817 | [[download](https://www.dropbox.com/s/4bjx35sgosu0jz0/anserini.covid-r3.full-text.qq.bm25.txt)]   | `6c9f4c09d842b887262ca84d61c61a1f` |
|  4 | full-text | UDel query generator     | 0.3430 | 0.5025 | 0.5267 | [[download](https://www.dropbox.com/s/mjt7y1ywae784d0/anserini.covid-r3.full-text.qdel.bm25.txt)] | `c5f9db7733c72eea78ece2ade44d3d35` |
|  5 | paragraph | query+question           | 0.2848 | 0.5175 | 0.5527 | [[download](https://www.dropbox.com/s/qwn7jd8vg2chjik/anserini.covid-r3.paragraph.qq.bm25.txt)]   | `872673b3e12c661748d8899f24d3ba48` |
|  6 | paragraph | UDel query generator     | 0.3604 | 0.5050 | 0.5676 | [[download](https://www.dropbox.com/s/2928i60fj2i09bt/anserini.covid-r3.paragraph.qdel.bm25.txt)] | `c1b966e4c3f387b6810211f339b35852` |
|  7 | -         | reciprocal rank fusion(1, 3, 5) | 0.3093 | 0.4975 | 0.5566 | [[download](https://www.dropbox.com/s/6vk5iohqf81iy8b/anserini.covid-r3.fusion1.txt)]      | `61cbd73c6e60ba44f18ce967b5b0e5b3` |
|  8 | -         | reciprocal rank fusion(2, 4, 6) | 0.3568 | 0.5250 | 0.5769 | [[download](https://www.dropbox.com/s/n09595t1eqymkks/anserini.covid-r3.fusion2.txt)]      | `d7eabf3dab840104c88de925e918fdab` |

**IMPORTANT NOTES!!!**

+ These runs are performed at [`2b4dcc2`](https://github.com/castorini/anserini/commit/2b4dcc2662f91ff50d3dbcd6fb4e2959a9de58c6), at the release of Anserini 0.9.3.
+ The evaluation numbers are produced with the union of _both_ round 1 qrels and round 2 qrels on the round 3 collection (release of 5/19).
+ For the abstract and full-text indexes, we request up to 10k hits for each topic; the number of actual hits retrieved is fairly close to this (a bit less because of deduping). For the paragraph index, we request up to 50k hits for each topic; because multiple paragraphs are retrieved from the same document, the number of unique documents in each list of hits is much smaller. A cautionary note: our experience is that choosing the top _k_ documents to rerank has a large impact on end-to-end effectiveness. Reranking the top 100 seems to provide higher precision than top 1000, but the likely tradeoff is lower recall. It is very likely the case that you _don't_ want to rerank all available hits.
+ For reciprocal rank fusion, the underlying fusion library returns only up to 1000 hits per topic. This was a known issue for round 2, since the Anserini fusion script did not specify a larger value. However, this does appear to be a limitation in the underlying library, see [this issue](https://github.com/joaopalotti/trectools/issues/22).

Exact commands for replicating these runs are found [further down on this page](experiments-covid.md#round-3-replication-commands).

The final runs submitted to NIST, after removing judgments from round 1 and round 2, are as follows:

| group | runtag | run file | checksum |
|:------|:-------|:---------|:---------|
| `anserini` | `r3.fusion1` | [[download](https://www.dropbox.com/s/ypoe9tgwef17rak/anserini.final-r3.fusion1.txt)] | `c1caf63a9c3b02f0b12e233112fc79a6` |
| `anserini` | `r3.fusion2` | [[download](https://www.dropbox.com/s/uvfrssp6nw2v2jl/anserini.final-r3.fusion2.txt)] | `12679197846ed77306ecb2ca7895b011` |
| `anserini` | `r3.rf`      | [[download](https://www.dropbox.com/s/2wrg7ceaca3n7ac/anserini.final-r3.rf.txt)]      | `7192a08c5275b59d5ef18395917ff694` |

We resolved the issue from round 2 where the final submitted runs have less than 1000 hits per topic.
The final run `r3.rf` represents code in [PR #1236](https://github.com/castorini/anserini/pull/1236): abstract index, UDel query generator, BM25+RM3 relevance feedback (100 feedback terms).
Merge of the PR into master is pending confirmation that all previous regression tests pass.

Exact commands for replicating these runs are found [further down on this page](experiments-covid.md#round-3-replication-commands).

**(Updates 2020/06/23)** We report the effectiveness of the Anserini baselines according to official round 3 judgments from NIST below.
NIST provides the following caveat [here](https://ir.nist.gov/covidSubmit/archive.html):

> Since there were previously judged documents whose doc-ids changed between the Round 1 and Round 2 judgment sets and the Round 3 data sets, these documents were removed from submissions by NIST. Almost all runs had some documents removed.

Thus, the runs submitted above were _not_ the actual runs evaluated by NIST.
They are, instead:

| group | runtag | run file | checksum |
|:------|:-------|:---------|:---------|
| `anserini` | `r3.fusion1` (NIST post-processed) | [[download](https://www.dropbox.com/s/ilqgky1tti0zvez/anserini.final-r3.fusion1.post-processed.txt)] | `f7c69c9bff381a847af86e5a8daf7526` |
| `anserini` | `r3.fusion2` (NIST post-processed) | [[download](https://www.dropbox.com/s/ue3z6xxxca9krkb/anserini.final-r3.fusion2.post-processed.txt)] | `84c5fd2c7de0a0282266033ac4f27c22` |
| `anserini` | `r3.rf` (NIST post-processed)      | [[download](https://www.dropbox.com/s/95vk831wp1ldnpm/anserini.final-r3.rf.post-processed.txt)]      | `3e79099639a9426cb53afe7066239011` |

And the effectiveness of the above runs:

| group | runtag | nDCG@10 | Judged@10 | Recall@1000
|:------|:-------|:---------|:---------|:---------|
| `anserini` | `r3.fusion1` | 0.5359 | 0.8475 | 0.2293
| `anserini` | `r3.fusion2` | 0.6100 | 0.9100 | 0.2641
| `anserini` | `r3.rf`      | 0.6883 | 0.9750 | 0.2817

**(Updates 2020/06/23)** We report the effectiveness of all the runs that were prepared _for_ round 3 using the cumulative qrels file (containing qrels from round 1, 2 and 3) mapped to the Round 3 (May 19) release of CORD-19. 
This qrels file, called `qrels_covid_d3_j0.5-3.txt`, provided by NIST [here](https://ir.nist.gov/covidSubmit/data/qrels-covid_d3_j0.5-3.txt), is stored in our repo as [`qrels.covid-round3-cumulative.txt`](https://github.com/castorini/anserini/blob/master/src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt).

|    | index     | field(s)                 | nDCG@10 | Judged@10 | Recall@1000 |
|---:|:----------|:-------------------------|--------:|----------:|------------:|
|  1 | abstract  | query+question           | 0.5781 | 0.8875 | 0.5040 |
|  2 | abstract  | UDel query generator     | 0.6291 | 0.9300 | 0.5215 |
|  3 | full-text | query+question           | 0.3977 | 0.7500 | 0.4708 |
|  4 | full-text | UDel query generator     | 0.5790 | 0.9050 | 0.5313 |
|  5 | paragraph | query+question           | 0.5396 | 0.9425 | 0.5766 |
|  6 | paragraph | UDel query generator     | 0.6327 | 0.9600 | 0.5923 |
|  7 | -         | reciprocal rank fusion(1, 3, 5) | 0.5924 | 0.9625 | 0.5956 |
|  8 | -         | reciprocal rank fusion(2, 4, 6) | 0.6515 | 0.9875 | 0.6194 |

## Round 2

These are runs that can be easily replicated with Anserini, from pre-built indexes available [here](experiments-cord19.md#pre-built-indexes-all-versions) (version from 2020/05/01, which is the official corpus used in the evaluation).
They were prepared _for_ round 2 (for participants who wish to have a baseline run to rerank), and so effectiveness is computed with round 1 qrels.

|    | index     | field(s)       | nDCG@10 | Judged@10 | Recall@1000 | run file | checksum |
|---:|:----------|:---------------|--------:|----------:|------------:|:---------|----------|
|  1 | abstract  | query+question |  0.3522 | 0.5371 | 0.6601 | [[download](https://www.dropbox.com/s/duimcackueph2co/anserini.covid-r2.abstract.qq.bm25.txt.gz)]    | `9cdea30a3881f9e60d3c61a890b094bd` |
|  2 | abstract  | UDel qgen      |  0.3781 | 0.5371 | 0.6485 | [[download](https://www.dropbox.com/s/n9yfssge5asez74/anserini.covid-r2.abstract.qdel.bm25.txt.gz)]  | `1e1bcdf623f69799a2b1b2982f53c23d` |
|  3 | full-text | query+question |  0.2070 | 0.4286 | 0.5953 | [[download](https://www.dropbox.com/s/iswpuj9tf5pj5ei/anserini.covid-r2.full-text.qq.bm25.txt.gz)]   | `6d704c60cc2cf134430c36ec2a0a3faa` |
|  4 | full-text | UDel qgen      |  0.3123 | 0.4229 | 0.6517 | [[download](https://www.dropbox.com/s/bj93a4iddpfvp09/anserini.covid-r2.full-text.qdel.bm25.txt.gz)] | `352a8b35a0626da21cab284bddb2e4e5` |
|  5 | paragraph | query+question |  0.2772 | 0.4400 | 0.7248 | [[download](https://www.dropbox.com/s/da7jg1ho5ubl8jt/anserini.covid-r2.paragraph.qq.bm25.txt.gz)]   | `b48c9ffb3cf9b35269ca9321ac39e758` |
|  6 | paragraph | UDel qgen      |  0.3353 | 0.4343 | 0.7196 | [[download](https://www.dropbox.com/s/7hplgsdq7ndn2ql/anserini.covid-r2.paragraph.qdel.bm25.txt.gz)] | `580fd34fbbda855dd09e1cb94467cb19` |
|  7 | -         | reciprocal rank fusion(1, 3, 5) | 0.3297 | 0.4657 | 0.7561 | [[download](https://www.dropbox.com/s/wqb0vhxp98g7dxh/anserini.covid-r2.fusion1.txt.gz)]       | `2a131517308d088c3f55afa0b8d5bb04` |
|  8 | -         | reciprocal rank fusion(2, 4, 6) | 0.3679 | 0.4829 | 0.7511 | [[download](https://www.dropbox.com/s/cd1ps4au79wvb8j/anserini.covid-r2.fusion2.txt.gz)]       | `9760124d8cfa03a0e3aae3a4c6e32550` |

**IMPORTANT NOTES!!!**

+ These runs are performed at [`39c9a92`](https://github.com/castorini/anserini/commit/39c9a92a957b9c444fe0dc89f4560f3f5a3b612f), at the release of Anserini 0.9.1.
+ "UDel qgen" refers to query generator contributed by the University of Delaware (see below).
+ The evaluation numbers are produced with round 1 qrels on the round 2 collection (release of 5/1).
+ The above runs **do not** conform to NIST's residual collection guidelines. That is, those runs **include** documents from the round 1 qrels. If you use these runs as the basis for reranking, you **must** make sure you conform to the [official round 2 guidelines](https://ir.nist.gov/covidSubmit/round2.html) from NIST. The reason for keeping documents from round 1 is so that it is possible to know the score distribution of relevant and non-relevant documents with respect to the new corpus.
+ The above runs provide up to 10k hits for each topic (sometimes less because of deduping). A cautionary note: our experience is that choosing the top _k_ documents to rerank has a large impact on end-to-end effectiveness. Reranking the top 100 seems to provide higher precision than top 1000, but the likely tradeoff is lower recall (although with such shallow pools currently, it's hard to tell). It is very likely the case that you _don't_ want to rerank all 10k hits.

The final runs submitted to NIST, after removing round 1 judgments, are as follows:

| group | runtag | run file | checksum |
|:------|:-------|:---------|:---------|
| `anserini` | `r2.fusion1` | [[download](https://www.dropbox.com/s/s5r6ufa95xeait4/anserini.r2.fusion1.txt)] | `89544da0409435c74dd4f3dd5fc9dc62` |
| `anserini` | `r2.fusion2` | [[download](https://www.dropbox.com/s/6kb14aggemtz6hq/anserini.r2.fusion2.txt)] | `774359c157c65bb7142d4f43b614e38f` |

We discovered at the last minute that the package we used to perform reciprocal rank fusion trimmed runs to 1000 hits per topic.
Thus the final submitted runs have less than 1000 hits per topic after removal of round 1 judgments.

Exact commands for replicating these runs are found [further down on this page](experiments-covid.md#round-2-replication-commands).

**(Updates 2020/05/26)** The effectiveness of the Anserini baselines according to official round 2 judgments from NIST:

| group | runtag | nDCG@10 | Judged@10 | Recall@1000
|:------|:-------|:---------|:---------|:---------|
| `anserini` | `r2.fusion1` | 0.4827 | 0.9543 | 0.6273
| `anserini` | `r2.fusion2` | 0.5553 | 0.9743 | 0.6630


## Round 1

These are runs that can be easily replicated with Anserini, from pre-built indexes available [here](experiments-cord19.md#pre-built-indexes-all-versions) (version from 2020/04/10, which is the official corpus used in the evaluation).
They were prepared _after_ round 1, and so we can report effectiveness results.

|    | index     | field(s)                          | nDCG@10 | Judged@10 | Recall@1000 |
|---:|:----------|:----------------------------------|--------:|----------:|------------:|
|  1 | abstract  | query                             |  0.4100 | 0.8267 | 0.5279 |
|  2 | abstract  | question                          |  0.5179 | 0.9833 | 0.6313 |
|  3 | abstract  | query+question                    |  0.5514 | 0.9833 | 0.6989 |
|  4 | abstract  | query+question+narrative          |  0.5294 | 0.9333 | 0.6929 |
|  5 | abstract  | UDel query generator              |  0.5824 | 0.9567 | 0.6927 |
|  6 | abstract  | `Covid19QueryGenerator`           |  0.4520 | 0.6500 | 0.5061 |
|  7 | full-text | query                             |  0.3900 | 0.7433 | 0.6277 |
|  8 | full-text | question                          |  0.3439 | 0.9267 | 0.6389 |
|  9 | full-text | query+question                    |  0.4064 | 0.9367 | 0.6714 |
| 10 | full-text | query+question+narrative          |  0.3280 | 0.7567 | 0.6591 |
| 11 | full-text | UDel query generator              |  0.5407 | 0.9067 | 0.7214 |
| 12 | full-text | `Covid19QueryGenerator`           |  0.2434 | 0.5233 | 0.5692 |
| 13 | paragraph | query                             |  0.4302 | 0.8400 | 0.4327 |
| 14 | paragraph | question                          |  0.4410 | 0.9167 | 0.5111 |
| 15 | paragraph | query+question                    |  0.5450 | 0.9733 | 0.5743 |
| 16 | paragraph | query+question+narrative          |  0.4899 | 0.8967 | 0.5918 |
| 17 | paragraph | UDel query generator              |  0.5544 | 0.9200 | 0.5640 |
| 18 | paragraph | `Covid19QueryGenerator`           |  0.3180 | 0.5333 | 0.3552 |
| 19 | -         | reciprocal rank fusion(3, 9, 15)  |  0.5716 | 0.9867 | 0.8117 |
| 20 | -         | reciprocal rank fusion(5, 11, 17) |  0.6019 | 0.9733 | 0.8121 |

**IMPORTANT NOTE:** These results **cannot** be replicated using the indexer at `HEAD` because the indexing code has changed since the time the above indexes were generated.
The results are only replicable with the state of the indexer at the time of submission of TREC-COVID round 1 (which were conducted with the above indexes).
Since it is not feasible to rerun and reevaluate with every indexer change, we have decided to perform all round 1 experiments only against the above indexes.
For more discussion, see [issue #1154](https://github.com/castorini/anserini/issues/1153); another major indexer change was [#1101](https://github.com/castorini/anserini/pull/1101), which substantively changes the full-text and paragraph indexes.

The "UDel query generator" condition represents the query generator from run [`udel_fang_run3`](https://ir.nist.gov/covidSubmit/archive/round1/udel_fang_run3.pdf), contributed to the repo as part of commit [`0d4bcd5`](https://github.com/castorini/anserini/commit/0d4bcd55370295ff72605d718dbab5be40d246d9) via [#1142](https://github.com/castorini/anserini/pull/1142).
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

## Round 3: Replication Commands

Here are the replication commands for the individual runs:

```bash
# abstract index

cat src/main/resources/topics-and-qrels/qrels.covid-round1.txt src/main/resources/topics-and-qrels/qrels.covid-round2.txt > src/main/resources/topics-and-qrels/qrels.covid-round12.txt

wget https://www.dropbox.com/s/3ld34ms35zfb4m9/lucene-index-cord19-abstract-2020-05-19.tar.gz
tar xvfz lucene-index-cord19-abstract-2020-05-19.tar.gz -C indexes

target/appassembler/bin/SearchCollection -index indexes/lucene-index-cord19-abstract-2020-05-19 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round3.xml -topicfield query+question -removedups \
 -bm25 -hits 10000 -output runs/anserini.covid-r3.abstract.qq.bm25.txt -runtag anserini.covid-r3.abstract.qq.bm25.txt

target/appassembler/bin/SearchCollection -index indexes/lucene-index-cord19-abstract-2020-05-19 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round3-udel.xml -topicfield query -removedups \
 -bm25 -hits 10000 -output runs/anserini.covid-r3.abstract.qdel.bm25.txt -runtag anserini.covid-r3.abstract.qdel.bm25.txt

eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round12.txt runs/anserini.covid-r3.abstract.qq.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round12.txt runs/anserini.covid-r3.abstract.qdel.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'

python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round12.txt --cutoffs 10 --run runs/anserini.covid-r3.abstract.qq.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round12.txt --cutoffs 10 --run runs/anserini.covid-r3.abstract.qdel.bm25.txt

cut -d ' ' -f 1 runs/anserini.covid-r3.abstract.qq.bm25.txt | sort | uniq | wc
cut -d ' ' -f 1 runs/anserini.covid-r3.abstract.qdel.bm25.txt | sort | uniq | wc

# full-text index

wget https://www.dropbox.com/s/qih3tjsir3xulrn/lucene-index-cord19-full-text-2020-05-19.tar.gz
tar xvfz lucene-index-cord19-full-text-2020-05-19.tar.gz -C indexes

target/appassembler/bin/SearchCollection -index indexes/lucene-index-cord19-full-text-2020-05-19 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round3.xml -topicfield query+question -removedups \
 -bm25 -hits 10000 -output runs/anserini.covid-r3.full-text.qq.bm25.txt -runtag anserini.covid-r3.full-text.qq.bm25.txt

target/appassembler/bin/SearchCollection -index indexes/lucene-index-cord19-full-text-2020-05-19 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round3-udel.xml -topicfield query -removedups \
 -bm25 -hits 10000 -output runs/anserini.covid-r3.full-text.qdel.bm25.txt -runtag anserini.covid-r3.full-text.qdel.bm25.txt

eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round12.txt runs/anserini.covid-r3.full-text.qq.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round12.txt runs/anserini.covid-r3.full-text.qdel.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'

python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round12.txt --cutoffs 10 --run runs/anserini.covid-r3.full-text.qq.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round12.txt --cutoffs 10 --run runs/anserini.covid-r3.full-text.qdel.bm25.txt

cut -d ' ' -f 1 runs/anserini.covid-r3.full-text.qq.bm25.txt | sort | uniq | wc
cut -d ' ' -f 1 runs/anserini.covid-r3.full-text.qdel.bm25.txt | sort | uniq | wc

# paragraph index

wget https://www.dropbox.com/s/7z8szogu5neuhqe/lucene-index-cord19-paragraph-2020-05-19.tar.gz
tar xvfz lucene-index-cord19-paragraph-2020-05-19.tar.gz -C indexes

target/appassembler/bin/SearchCollection -index indexes/lucene-index-cord19-paragraph-2020-05-19 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round3.xml -topicfield query+question -removedups -strip_segment_id \
 -bm25 -hits 50000 -output runs/anserini.covid-r3.paragraph.qq.bm25.txt -runtag anserini.covid-r3.paragraph.qq.bm25.txt

target/appassembler/bin/SearchCollection -index indexes/lucene-index-cord19-paragraph-2020-05-19 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round3-udel.xml -topicfield query -removedups -strip_segment_id \
 -bm25 -hits 50000 -output runs/anserini.covid-r3.paragraph.qdel.bm25.txt -runtag anserini.covid-r3.paragraph.qdel.bm25.txt

eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round12.txt runs/anserini.covid-r3.paragraph.qq.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round12.txt runs/anserini.covid-r3.paragraph.qdel.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'

python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round12.txt --cutoffs 10 --run runs/anserini.covid-r3.paragraph.qq.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round12.txt --cutoffs 10 --run runs/anserini.covid-r3.paragraph.qdel.bm25.txt

cut -d ' ' -f 1 runs/anserini.covid-r3.paragraph.qq.bm25.txt | sort | uniq | wc
cut -d ' ' -f 1 runs/anserini.covid-r3.paragraph.qdel.bm25.txt | sort | uniq | wc
```

To replicate the fusion runs:

```bash
python src/main/python/fusion.py --method RRF --max_docs 10000 --out runs/anserini.covid-r3.fusion1.txt \
 --runs runs/anserini.covid-r3.abstract.qq.bm25.txt runs/anserini.covid-r3.full-text.qq.bm25.txt runs/anserini.covid-r3.paragraph.qq.bm25.txt

python src/main/python/fusion.py --method RRF --max_docs 10000 --out runs/anserini.covid-r3.fusion2.txt \
 --runs runs/anserini.covid-r3.abstract.qdel.bm25.txt runs/anserini.covid-r3.full-text.qdel.bm25.txt runs/anserini.covid-r3.paragraph.qdel.bm25.txt

eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round12.txt runs/anserini.covid-r3.fusion1.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round12.txt runs/anserini.covid-r3.fusion2.txt | egrep '(ndcg_cut_10 |recall_1000 )'

python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round12.txt --cutoffs 10 --run runs/anserini.covid-r3.fusion1.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round12.txt --cutoffs 10 --run runs/anserini.covid-r3.fusion2.txt

cut -d ' ' -f 1 runs/anserini.covid-r3.paragraph.qq.bm25.txt | sort | uniq | wc
cut -d ' ' -f 1 runs/anserini.covid-r3.paragraph.qdel.bm25.txt | sort | uniq | wc
```

To prepare the final runs for submission (removing judgments from round 1 and round 2):

```bash
python src/main/python/trec-covid/remove_judged_docids.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round12.txt \
 --input runs/anserini.covid-r3.fusion1.txt --output runs/anserini.final-r3.fusion1.txt --runtag r3.fusion1

python src/main/python/trec-covid/remove_judged_docids.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round12.txt \
 --input runs/anserini.covid-r3.fusion2.txt --output runs/anserini.final-r3.fusion2.txt --runtag r3.fusion2
```

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

python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/anserini.covid-r2.abstract.qq.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/anserini.covid-r2.abstract.qdel.bm25.txt

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

python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/anserini.covid-r2.full-text.qq.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/anserini.covid-r2.full-text.qdel.bm25.txt

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

python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/anserini.covid-r2.paragraph.qq.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/anserini.covid-r2.paragraph.qdel.bm25.txt
```

We've written a convenience script to generate fusion runs that wraps [`trectools`](https://github.com/joaopalotti/trectools) (v0.0.43):

```bash
python src/main/python/fusion.py --method RRF --out runs/anserini.covid-r2.fusion1.txt \
 --runs runs/anserini.covid-r2.abstract.qq.bm25.txt runs/anserini.covid-r2.full-text.qq.bm25.txt runs/anserini.covid-r2.paragraph.qq.bm25.txt

python src/main/python/fusion.py --method RRF --out runs/anserini.covid-r2.fusion2.txt \
 --runs runs/anserini.covid-r2.abstract.qdel.bm25.txt runs/anserini.covid-r2.full-text.qdel.bm25.txt runs/anserini.covid-r2.paragraph.qdel.bm25.txt
```

And to evalute the fusion runs:

```bash
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/anserini.covid-r2.fusion1.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/anserini.covid-r2.fusion2.txt | egrep '(ndcg_cut_10 |recall_1000 )'

python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/anserini.covid-r2.fusion1.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/anserini.covid-r2.fusion2.txt
```

To prepare the final runs for submission (removing round 1 judgments):

```bash
python src/main/python/trec-covid/remove_judged_docids.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt \
 --input runs/anserini.covid-r2.fusion1.txt --output anserini.r2.fusion1.txt --runtag r2.fusion1

python src/main/python/trec-covid/remove_judged_docids.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt \
 --input runs/anserini.covid-r2.fusion2.txt --output anserini.r2.fusion2.txt --runtag r2.fusion2
```

Evaluating runs with round 2 judgments:

```bash
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round2.txt runs/anserini.r2.fusion1.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round2.txt runs/anserini.r2.fusion2.txt | egrep '(ndcg_cut_10 |recall_1000 )'

python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round2.txt --cutoffs 10 --run runs/anserini.r2.fusion1.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round2.txt --cutoffs 10 --run runs/anserini.r2.fusion2.txt
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

target/appassembler/bin/SearchCollection -index lucene-index-covid-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield query -querygenerator Covid19QueryGenerator -removedups \
 -bm25 -output runs/run.covid-r1.abstract.query-covid19.bm25.txt
```

Here are the commands to evaluate results on the abstract index:

```bash
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.abstract.query.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.abstract.question.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.abstract.query+question.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.abstract.query+question+narrative.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.abstract.query-udel.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.abstract.query-covid19.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'

python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.abstract.query.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.abstract.question.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.abstract.query+question.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.abstract.query+question+narrative.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.abstract.query-udel.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.abstract.query-covid19.bm25.txt
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

target/appassembler/bin/SearchCollection -index lucene-index-covid-full-text-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield query -querygenerator Covid19QueryGenerator -removedups \
 -bm25 -output runs/run.covid-r1.full-text.query-covid19.bm25.txt
```

Here are the commands to evaluate results on the full-text index:

```bash
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.full-text.query.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.full-text.question.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.full-text.query+question.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.full-text.query+question+narrative.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.full-text.query-udel.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.full-text.query-covid19.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'

python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.full-text.query.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.full-text.question.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.full-text.query+question.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.full-text.query+question+narrative.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.full-text.query-udel.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.full-text.query-covid19.bm25.txt
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

target/appassembler/bin/SearchCollection -index lucene-index-covid-paragraph-2020-04-10 \
 -topicreader Covid -topics src/main/resources/topics-and-qrels/topics.covid-round1.xml -topicfield query -querygenerator Covid19QueryGenerator -removedups -strip_segment_id \
 -bm25 -output runs/run.covid-r1.paragraph.query-covid19.bm25.txt
```

Here are the commands to evaluate results on the paragraph index:

```bash
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.paragraph.query.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.paragraph.question.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.paragraph.query+question.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.paragraph.query+question+narrative.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.paragraph.query-udel.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.paragraph.query-covid19.bm25.txt | egrep '(ndcg_cut_10 |recall_1000 )'

python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.paragraph.query.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.paragraph.question.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.paragraph.query+question.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.paragraph.query+question+narrative.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.paragraph.query-udel.bm25.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.paragraph.query-covid19.bm25.txt
```

We've written a convenience script to generate fusion runs that wraps [`trectools`](https://github.com/joaopalotti/trectools) (v0.0.43):

```bash
python src/main/python/fusion.py --method RRF --out runs/run.covid-r1.fusion1.txt \
 --runs runs/run.covid-r1.abstract.query+question.bm25.txt runs/run.covid-r1.full-text.query+question.bm25.txt runs/run.covid-r1.paragraph.query+question.bm25.txt

python src/main/python/fusion.py --method RRF --out runs/run.covid-r1.fusion2.txt \
 --runs runs/run.covid-r1.abstract.query-udel.bm25.txt runs/run.covid-r1.full-text.query-udel.bm25.txt runs/run.covid-r1.paragraph.query-udel.bm25.txt
```

And to evalute the fusion runs:

```bash
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.fusion1.txt | egrep '(ndcg_cut_10 |recall_1000 )'
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m all_trec src/main/resources/topics-and-qrels/qrels.covid-round1.txt runs/run.covid-r1.fusion2.txt | egrep '(ndcg_cut_10 |recall_1000 )'

python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.fusion1.txt
python eval/measure_judged.py --qrels src/main/resources/topics-and-qrels/qrels.covid-round1.txt --cutoffs 10 --run runs/run.covid-r1.fusion2.txt
```
