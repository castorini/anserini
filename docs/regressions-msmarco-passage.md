# Anserini: Regressions for [MS MARCO Passage Retrieval](https://github.com/microsoft/MSMARCO-Passage-Ranking)

This page documents regression experiments for the MS MARCO Passage Retrieval Task, which is integrated into Anserini's regression testing framework.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-passage.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/msmarco-passage \
  -index indexes/lucene-index.msmarco-passage/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 9 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-passage &
```

The directory `/path/to/msmarco-passage/` should be a directory containing `jsonl` files converted from the official passage collection, which is in `tsv` format.
[This page](experiments-msmarco-passage.md) explains how to perform this conversion.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-default.topics.msmarco-passage.dev-subset.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-default+ax.topics.msmarco-passage.dev-subset.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-default+prf.topics.msmarco-passage.dev-subset.txt \
  -bm25 -bm25prf &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned.topics.msmarco-passage.dev-subset.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned+ax.topics.msmarco-passage.dev-subset.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -axiom -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -topicreader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned+prf.topics.msmarco-passage.dev-subset.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-default.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-default+ax.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-default+prf.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-tuned.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-tuned+ax.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-tuned+prf.topics.msmarco-passage.dev-subset.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (default)| +RM3      | +Ax       | +PRF      | BM25 (tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.1926    | 0.1661    | 0.1625    | 0.1520    | 0.1958    | 0.1762    | 0.1699    | 0.1582    |


R@1000                                  | BM25 (default)| +RM3      | +Ax       | +PRF      | BM25 (tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.8526    | 0.8606    | 0.8747    | 0.8537    | 0.8573    | 0.8687    | 0.8809    | 0.8561    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=0.82`, `b=0.68`, as described in [this page](experiments-msmarco-passage.md). Note that results here are slightly different from the above referenced page because those experiments used `SearchMsmarco` to generate runs in the MS MARCO format, and then converted them into the TREC format, which is slightly lossy (due to tie-breaking effects).

To generate runs corresponding to the submissions on the [MS MARCO Passage Ranking Leaderboard](https://microsoft.github.io/msmarco/), follow the instructions below:

## Additional Implementation Details

Note that prior to December 2021, runs generated with `SearchCollection` in the TREC format and then converted into the MS MARCO format give slightly different results from runs generated by `SearchMsmarco` directly in the MS MARCO format, due to tie-breaking effects.
This was fixed with [#1458](https://github.com/castorini/anserini/issues/1458), which also introduced (intra-configuration) multi-threading.
As a result, `SearchMsmarco` has been deprecated and replaced by `SearchCollection`; both have been verified to generate _identical_ output.

The commands below have been retained for historical reasons only, since in some cases they correspond to official MS MARCO leaderboard submissions.

The following command generates with `SearchMsmarco` the run denoted "BM25 (default)" above (`k1=0.9`, `b=0.4`), which roughly corresponds to the entry "BM25 (Anserini)" dated 2019/04/10 on the leaderboard (but Anserini was using Lucene 7.6 at the time):

```bash
$ sh target/appassembler/bin/SearchMsmarco -hits 1000 -threads 8 \
   -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
   -queries collections/msmarco-passage/queries.dev.small.tsv \
   -k1 0.9 -b 0.4 \
   -output runs/run.msmarco-passage.bm25.default.tsv

$ python tools/scripts/msmarco/msmarco_passage_eval.py \
   collections/msmarco-passage/qrels.dev.small.tsv runs/run.msmarco-passage.bm25.default.tsv

#####################
MRR @10: 0.18398616227770961
QueriesRanked: 6980
#####################
```

The following command generates with `SearchMsmarco` the run denoted "BM25 (tuned)" above (`k1=0.82`, `b=0.68`), which corresponds to the entry "BM25 (Lucene8, tuned)" dated 2019/06/26 on the leaderboard:

```bash
$ sh target/appassembler/bin/SearchMsmarco -hits 1000 -threads 8 \
   -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
   -queries collections/msmarco-passage/queries.dev.small.tsv \
   -k1 0.82 -b 0.68 \
   -output runs/run.msmarco-passage.bm25.tuned.tsv

$ python tools/scripts/msmarco/msmarco_passage_eval.py \
   collections/msmarco-passage/qrels.dev.small.tsv runs/run.msmarco-passage.bm25.tuned.tsv

#####################
MRR @10: 0.18741227770955546
QueriesRanked: 6980
#####################
```
