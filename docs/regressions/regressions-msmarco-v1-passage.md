# Anserini Regressions: MS MARCO Passage Ranking

**Models**: various bag-of-words approaches

This page documents regression experiments on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking), which is integrated into Anserini's regression testing framework.
For more complete instructions on how to run end-to-end experiments, refer to [this page](../../docs/experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-v1-passage.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-v1-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 9 \
  -collection JsonCollection \
  -input /path/to/msmarco-passage \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-passage &
```

The directory `/path/to/msmarco-passage/` should be a directory containing `jsonl` files converted from the official passage collection, which is in `tsv` format.
[This page](../../docs/experiments-msmarco-passage.md) explains how to perform this conversion.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 6980 dev set questions; see [this page](../../docs/experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -topics tools/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-default.topics.msmarco-passage.dev-subset.txt \
  -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage/ \
  -topics tools/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.bm25-tuned.topics.msmarco-passage.dev-subset.txt \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-default.topics.msmarco-passage.dev-subset.txt
bin/trec_eval -c -M 10 -m recip_rank tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-default.topics.msmarco-passage.dev-subset.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-default.topics.msmarco-passage.dev-subset.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-default.topics.msmarco-passage.dev-subset.txt

bin/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-tuned.topics.msmarco-passage.dev-subset.txt
bin/trec_eval -c -M 10 -m recip_rank tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-tuned.topics.msmarco-passage.dev-subset.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-tuned.topics.msmarco-passage.dev-subset.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-tuned.topics.msmarco-passage.dev-subset.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **BM25 (default)**| **BM25 (tuned)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.1926    | 0.1958    |
| **RR@10**                                                                                                    | **BM25 (default)**| **BM25 (tuned)**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.1840    | 0.1875    |
| **R@100**                                                                                                    | **BM25 (default)**| **BM25 (tuned)**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.6578    | 0.6701    |
| **R@1000**                                                                                                   | **BM25 (default)**| **BM25 (tuned)**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.8526    | 0.8573    |

Explanation of settings:

+ The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`.
+ The setting "tuned" refers to `k1=0.82`, `b=0.68`, as described in [this page](../../docs/experiments-msmarco-passage.md).

To generate runs corresponding to the submissions on the [MS MARCO Passage Ranking Leaderboard](https://microsoft.github.io/msmarco/), follow the instructions below:

## Additional Implementation Details

Note that prior to December 2021, runs generated with `SearchCollection` in the TREC format and then converted into the MS MARCO format give slightly different results from runs generated by `SearchMsmarco` directly in the MS MARCO format, due to tie-breaking effects.
This was fixed with [#1458](https://github.com/castorini/anserini/issues/1458), which also introduced (intra-configuration) multi-threading.
As a result, `SearchMsmarco` has been deprecated and replaced by `SearchCollection`; both have been verified to generate _identical_ output.

The commands below have been retained for historical reasons only, since in some cases they correspond to official MS MARCO leaderboard submissions.

The following command generates with `SearchMsmarco` the run denoted "BM25 (default)" above (`k1=0.9`, `b=0.4`), which roughly corresponds to the entry "BM25 (Anserini)" dated 2019/04/10 on the leaderboard (but Anserini was using Lucene 7.6 at the time):

```bash
$ sh target/appassembler/bin/SearchMsmarco -hits 1000 -threads 8 \
    -index indexes/lucene-index.msmarco-passage/ \
    -queries tools/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
    -k1 0.9 -b 0.4 \
    -output runs/run.msmarco-passage.bm25.default.tsv

$ python tools/scripts/msmarco/msmarco_passage_eval.py \
    tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25.default.tsv

#####################
MRR @10: 0.18398616227770961
QueriesRanked: 6980
#####################
```

The following command generates with `SearchMsmarco` the run denoted "BM25 (tuned)" above (`k1=0.82`, `b=0.68`), which corresponds to the entry "BM25 (Lucene8, tuned)" dated 2019/06/26 on the leaderboard:

```bash
$ sh target/appassembler/bin/SearchMsmarco -hits 1000 -threads 8 \
    -index indexes/lucene-index.msmarco-passage/ \
    -queries tools/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
    -k1 0.82 -b 0.68 \
    -output runs/run.msmarco-passage.bm25.tuned.tsv

$ python tools/scripts/msmarco/msmarco_passage_eval.py \
    tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25.tuned.tsv

#####################
MRR @10: 0.18741227770955546
QueriesRanked: 6980
#####################
```

As of February 2022, following resolution of [#1730](https://github.com/castorini/anserini/issues/1730), BM25 runs for the MS MARCO leaderboard can be generated with the commands below.
For default parameters (`k1=0.9`, `b=0.4`):

```
$ sh target/appassembler/bin/SearchCollection \
    -index indexes/lucene-index.msmarco-passage/ \
    -topics tools/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
    -topicreader TsvInt \
    -output runs/run.msmarco-passage.bm25.default.tsv \
    -format msmarco \
    -bm25

$ python tools/scripts/msmarco/msmarco_passage_eval.py \
    tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25.default.tsv

#####################
MRR @10: 0.18398616227770961
QueriesRanked: 6980
#####################
```

For tuned parameters (`k1=0.82`, `b=0.68`):

```
$ sh target/appassembler/bin/SearchCollection \
    -index indexes/lucene-index.msmarco-passage/ \
    -topics tools/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
    -topicreader TsvInt \
    -output runs/run.msmarco-passage.bm25.tuned.tsv \
    -format msmarco \
    -bm25 -bm25.k1 0.82 -bm25.b 0.68

$ python tools/scripts/msmarco/msmarco_passage_eval.py \
    tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25.tuned.tsv

#####################
MRR @10: 0.18741227770955546
QueriesRanked: 6980
#####################
```

Note that the resolution of [#1730](https://github.com/castorini/anserini/issues/1730) did not change the results.
