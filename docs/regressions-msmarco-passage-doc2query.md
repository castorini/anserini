# Anserini: Regressions for [MS MARCO Passage Retrieval](https://github.com/microsoft/MSMARCO-Passage-Ranking)

This page documents regression experiments for the MS MARCO Passage Retrieval Task with doc2query expansions (also called doc2query-base) , as proposed in the following paper:

> Rodrigo Nogueira, Wei Yang, Jimmy Lin, and Kyunghyun Cho. [Document Expansion by Query Prediction.](https://arxiv.org/abs/1904.08375) arXiv:1904.08375, 2019.

These experiments are integrated into Anserini's regression testing framework.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-doc2query.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-passage-doc2query.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-passage-doc2query.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/msmarco-passage-doc2query \
  -index indexes/lucene-index.msmarco-passage-doc2query/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 9 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-passage-doc2query &
```

The directory `/path/to/msmarco-passage-doc2query` should be a directory containing `jsonl` files containing the expanded passage collection.
[This page](experiments-doc2query.md) explains how to perform this data preparation.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-doc2query/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage-doc2query.bm25-default.topics.msmarco-passage.dev-subset.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-doc2query/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage-doc2query.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-doc2query/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage-doc2query.bm25-tuned.topics.msmarco-passage.dev-subset.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-doc2query/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage-doc2query.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -rm3 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-doc2query.bm25-default.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-doc2query.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-doc2query.bm25-tuned.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-doc2query.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (default)| +RM3      | BM25 (tuned)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.2270    | 0.2028    | 0.2293    | 0.2077    |


R@1000                                  | BM25 (default)| +RM3      | BM25 (tuned)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.8900    | 0.8916    | 0.8911    | 0.8957    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=0.82`, `b=0.68`, tuned on _on the original passages_, as described in [this page](experiments-msmarco-passage.md).

## Additional Implementation Details

Note that prior to December 2021, runs generated with `SearchCollection` in the TREC format and then converted into the MS MARCO format give slightly different results from runs generated by `SearchMsmarco` directly in the MS MARCO format, due to tie-breaking effects.
This was fixed with [#1458](https://github.com/castorini/anserini/issues/1458), which also introduced (intra-configuration) multi-threading.
As a result, `SearchMsmarco` has been deprecated and replaced by `SearchCollection`; both have been verified to generate _identical_ output.

The commands below have been retained for historical reasons only.

The following command generates with `SearchMsmarco` the run denoted "BM25 (tuned)" above (`k1=0.82`, `b=0.68`):

```bash
$ sh target/appassembler/bin/SearchMsmarco -hits 1000 -threads 8 \
    -index indexes/lucene-index.msmarco-passage-doc2query.pos+docvectors+raw \
    -queries collections/msmarco-passage/queries.dev.small.tsv \
    -k1 0.82 -b 0.68 \
    -output runs/run.msmarco-passage-doc2query

$ python tools/scripts/msmarco/msmarco_passage_eval.py \
   collections/msmarco-passage/qrels.dev.small.tsv runs/run.msmarco-passage-doc2query

#####################
MRR @10: 0.2213412471005586
QueriesRanked: 6980
#####################
```

Note that this run does _not_ correspond to the scores reported in the paper that introduced doc2query:

> Rodrigo Nogueira, Wei Yang, Jimmy Lin, and Kyunghyun Cho. [Document Expansion by Query Prediction.](https://arxiv.org/abs/1904.08375) arXiv:1904.08375, 2019.

The scores reported in the above paper refer to entry "BM25 (Anserini) + doc2query" dated 2019/04/10 on the [MS MARCO Passage Ranking Leaderboard](https://microsoft.github.io/msmarco/).
The paper/leaderboard run reports 0.215 MRR@10, which is slightly lower than the "BM25 (Tuned)" regression run above, due to an earlier version of Lucene (7.6) and use of default BM25 parameters.
