# Anserini: Regressions for the [New York Times](https://catalog.ldc.upenn.edu/LDC2008T19) ([Core17](https://trec-core.github.io/2017/))

This page describes regressions for the TREC 2017 Common Core Track, which uses the [New York Times Annotated Corpus](https://catalog.ldc.upenn.edu/LDC2008T19).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/core17.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/core17.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection NewYorkTimesCollection \
 -input /path/to/core17 \
 -index indexes/lucene-index.core17.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 16 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.core17 &
```

The directory `/path/to/nyt_corpus/` should be the root directory of the [New York Times Annotated Corpus](https://catalog.ldc.upenn.edu/LDC2008T19), i.e., `ls /path/to/nyt_corpus/`
should bring up a bunch of subdirectories, `1987` to `2007`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.core17.txt`](../src/main/resources/topics-and-qrels/topics.core17.txt): [topics for the TREC 2017 Common Core Track](https://trec.nist.gov/data/core/core_nist.txt)
+ [`qrels.core17.txt`](../src/main/resources/topics-and-qrels/qrels.core17.txt): [qrels for the TREC 2017 Common Core Track](https://trec.nist.gov/data/core/qrels.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.core17.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.core17.txt \
 -output runs/run.core17.bm25.topics.core17.txt \
 -bm25 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.core17.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.core17.txt \
 -output runs/run.core17.bm25+rm3.topics.core17.txt \
 -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.core17.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.core17.txt \
 -output runs/run.core17.bm25+ax.topics.core17.txt \
 -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.core17.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.core17.txt \
 -output runs/run.core17.ql.topics.core17.txt \
 -qld &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.core17.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.core17.txt \
 -output runs/run.core17.ql+rm3.topics.core17.txt \
 -qld -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.core17.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.core17.txt \
 -output runs/run.core17.ql+ax.topics.core17.txt \
 -qld -axiom -axiom.deterministic -rerankCutoff 20 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt runs/run.core17.bm25.topics.core17.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt runs/run.core17.bm25+rm3.topics.core17.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt runs/run.core17.bm25+ax.topics.core17.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt runs/run.core17.ql.topics.core17.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt runs/run.core17.ql+rm3.topics.core17.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt runs/run.core17.ql+ax.topics.core17.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2017 Common Core Track Topics](../src/main/resources/topics-and-qrels/topics.core17.txt)| 0.2087    | 0.2823    | 0.2739    | 0.2032    | 0.2606    | 0.2579    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2017 Common Core Track Topics](../src/main/resources/topics-and-qrels/topics.core17.txt)| 0.4293    | 0.5093    | 0.4940    | 0.4467    | 0.4827    | 0.4893    |

## Replication Log

+ Results replicated by [@tteofili](https://github.com/tteofili) on 2019-01-27 (commit [`951090`](https://github.com/castorini/Anserini/commit/951090b66230040f037dde46534d896416467337))
+ Results replicated by [@chriskamphuis](https://github.com/chriskamphuis) on 2019-09-07 (commit [`61f6f20`](https://github.com/castorini/anserini/commit/61f6f20ff6872484966ea1badcdcdcebf1eea852))
+ Results replicated by [@yuki617](https://github.com/yuki617) on 2020-05-17 (commit [`cee4463`](https://github.com/castorini/anserini/commit/cee446338137415899436f0b2f2d738769745cde))
+ Results replicated by [@x65han](https://github.com/x65han) on 2020-05-19 (commit [`33b0684`](https://github.com/castorini/anserini/commit/33b068437c4582067486e5fe79dfbecb8d4a145c))
