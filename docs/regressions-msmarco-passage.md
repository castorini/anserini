# Anserini: Regressions for [MS MARCO Passage Retrieval](https://github.com/microsoft/MSMARCO-Passage-Ranking)

This page documents regression experiments for the MS MARCO Passage Retrieval Task, which is integrated into Anserini's regression testing framework.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-passage.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection \
 -input /path/to/msmarco-passage \
 -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
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
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -output runs/run.msmarco-passage.bm25-default.topics.msmarco-passage.dev-subset.txt \
 -bm25 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -output runs/run.msmarco-passage.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt \
 -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -output runs/run.msmarco-passage.bm25-default+ax.topics.msmarco-passage.dev-subset.txt \
 -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -output runs/run.msmarco-passage.bm25-default+prf.topics.msmarco-passage.dev-subset.txt \
 -bm25 -bm25prf &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -output runs/run.msmarco-passage.bm25-tuned.topics.msmarco-passage.dev-subset.txt \
 -bm25 -bm25.k1 0.82 -bm25.b 0.68 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -output runs/run.msmarco-passage.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt \
 -bm25 -bm25.k1 0.82 -bm25.b 0.68 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -output runs/run.msmarco-passage.bm25-tuned+ax.topics.msmarco-passage.dev-subset.txt \
 -bm25 -bm25.k1 0.82 -bm25.b 0.68 -axiom -axiom.deterministic -rerankCutoff 20 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -output runs/run.msmarco-passage.bm25-tuned+prf.topics.msmarco-passage.dev-subset.txt \
 -bm25 -bm25.k1 0.82 -bm25.b 0.68 -bm25prf &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-default.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-default+ax.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-default+prf.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-tuned.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-tuned+ax.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.bm25-tuned+prf.topics.msmarco-passage.dev-subset.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Passage Ranking: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.1926    | 0.1661    | 0.1625    | 0.1520    | 0.1958    | 0.1762    | 0.1699    | 0.1582    |


R@1000                                  | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Passage Ranking: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.8526    | 0.8606    | 0.8747    | 0.8537    | 0.8573    | 0.8687    | 0.8809    | 0.8561    |

The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`, while "tuned" refers to the tuned setting of `k1=0.82`, `b=0.68`.
See [this page](experiments-msmarco-passage.md) for more details.
Note that these results are slightly different from the above referenced page because those experiments used `SearchMsmarco` to generate runs in the MS MARCO format, and then converted them into TREC format, which is slightly lossy (due to tie-breaking effects).

To generate runs corresponding to the submissions on the [MS MARCO Passage Ranking Leaderboard](https://microsoft.github.io/msmarco/), follow the instructions below:

The following command generates with `SearchMsmarco` the run denoted "BM25 (Tuned)" above (`k1=0.82`, `b=0.68`), which corresponds to the entry "BM25 (Lucene8, tuned)" dated 2019/06/26 on the leaderboard:

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

The following command generates with `SearchMsmarco` the run denoted "BM25 (Default)" above (`k1=0.9`, `b=0.4`), which corresponds to the entry "BM25 (Anserini)" dated 2019/04/10 on the leaderboard:

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
