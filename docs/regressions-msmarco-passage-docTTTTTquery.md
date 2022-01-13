# Anserini: Regressions for [MS MARCO Passage Retrieval](https://github.com/microsoft/MSMARCO-Passage-Ranking)

This page documents regression experiments for the MS MARCO Passage Retrieval Task with [docTTTTTquery](https://github.com/castorini/docTTTTTquery) (also called doc2query-T5) expansions, as proposed in the following paper:

> Rodrigo Nogueira and Jimmy Lin. [From doc2query to docTTTTTquery.](https://cs.uwaterloo.ca/~jimmylin/publications/Nogueira_Lin_2019_docTTTTTquery-latest.pdf) December 2019.

These experiments are integrated into Anserini's regression testing framework.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-passage-docTTTTTquery.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-passage-docTTTTTquery.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/msmarco-passage-docTTTTTquery \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 18 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-passage-docTTTTTquery &
```

The directory `/path/to/msmarco-passage-docTTTTTquery` should be a directory containing `jsonl` files containing the expanded passage collection.
[Instructions in the docTTTTTquery repo](http://doc2query.ai/) explain how to perform this data preparation.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.msmarco-passage.dev-subset.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.msmarco-passage.dev-subset.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2.topics.msmarco-passage.dev-subset.txt \
  -bm25 -bm25.k1 2.18 -bm25.b 0.86 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-docTTTTTquery/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rm3.topics.msmarco-passage.dev-subset.txt \
  -bm25 -bm25.k1 2.18 -bm25.b 0.86 -rm3 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned2+rm3.topics.msmarco-passage.dev-subset.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (default)| +RM3      | BM25 (tuned)| +RM3      | BM25 (tuned2)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.2805    | 0.2243    | 0.2850    | 0.2266    | 0.2893    | 0.2464    |


R@1000                                  | BM25 (default)| +RM3      | BM25 (tuned)| +RM3      | BM25 (tuned2)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.9470    | 0.9463    | 0.9471    | 0.9479    | 0.9506    | 0.9528    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=0.82`, `b=0.68`, tuned on _on the original passages_, as described in [this page](experiments-msmarco-passage.md).
+ The setting "tuned2" refers to `k1=2.18`, `b=0.86`, tuned to optimize for recall@1000 directly _on the expanded passages_ (in 2020/12); this is the configuration reported in the Lin et al. (SIGIR 2021) Pyserini paper.

## Additional Implementation Details

Note that prior to December 2021, runs generated with `SearchCollection` in the TREC format and then converted into the MS MARCO format give slightly different results from runs generated by `SearchMsmarco` directly in the MS MARCO format, due to tie-breaking effects.
This was fixed with [#1458](https://github.com/castorini/anserini/issues/1458), which also introduced (intra-configuration) multi-threading.
As a result, `SearchMsmarco` has been deprecated and replaced by `SearchCollection`; both have been verified to generate _identical_ output.

The commands below have been retained for historical reasons only, since they correspond to previously published results.

The following command generates with `SearchMsmarco` the run denoted "BM25 (tuned)" above (`k1=0.82`, `b=0.68`), which corresponds to the entry "docTTTTTquery" dated 2019/11/27 on the [MS MARCO Passage Ranking Leaderboard](https://microsoft.github.io/msmarco/):

```bash
$ sh target/appassembler/bin/SearchMsmarco -hits 1000 -threads 8 \
    -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
    -queries collections/msmarco-passage/queries.dev.small.tsv \
    -k1 0.82 -b 0.68 \
    -output runs/run.msmarco-passage-docTTTTTquery.1

$ python tools/scripts/msmarco/msmarco_passage_eval.py \
   collections/msmarco-passage/qrels.dev.small.tsv runs/run.msmarco-passage-docTTTTTquery.1

#####################
MRR @10: 0.27680089370991834
QueriesRanked: 6980
#####################
```

This corresponds to the scores reported in the following paper:

> Rodrigo Nogueira and Jimmy Lin. [From doc2query to docTTTTTquery.](https://cs.uwaterloo.ca/~jimmylin/publications/Nogueira_Lin_2019_docTTTTTquery-latest.pdf) December 2019.

And are identical to the scores reported in [the docTTTTTquery repo](https://github.com/castorini/docTTTTTquery).

The following command generates with `SearchMsmarco` the run denoted "BM25 (tuned2)" above (`k1=2.18`, `b=0.86`).
This does _not_ correspond to an official leaderboard submission.

```bash
$ sh target/appassembler/bin/SearchMsmarco -hits 1000 -threads 8 \
    -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
    -queries collections/msmarco-passage/queries.dev.small.tsv \
    -k1 2.18 -b 0.86 \
    -output runs/run.msmarco-passage-docTTTTTquery.2

$ python tools/scripts/msmarco/msmarco_passage_eval.py \
   collections/msmarco-passage/qrels.dev.small.tsv runs/run.msmarco-passage-docTTTTTquery.2

#####################
MRR @10: 0.281560751807885
QueriesRanked: 6980
#####################
```

This corresponds to the scores reported in the Lin et al. (SIGIR 2021) Pyserini paper.
