# Anserini: Regressions for [Disks 4 &amp; 5](https://trec.nist.gov/data_disks.html) (Robust04)

This page describes regressions for the TREC 2004 Robust Track, which uses [TREC Disks 4 &amp; 5](https://trec.nist.gov/data_disks.html).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/robust04.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/robust04.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
 -input /path/to/robust04 \
 -index indexes/lucene-index.robust04.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 16 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.robust04 &
```

The directory `/path/to/disk45/` should be the root directory of [TREC Disks 4 &amp; 5](https://trec.nist.gov/data_disks.html); inside each there should be subdirectories like `ft`, `fr94`.
Note that Anserini ignores the `cr` folder when indexing, which is the standard configuration.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.robust04.txt`](../src/main/resources/topics-and-qrels/topics.robust04.txt): [topics for the TREC 2004 Robust Track (Topics 301-450 &amp; 601-700)](http://trec.nist.gov/data/robust/04.testset.gz)
+ [`qrels.robust04.txt`](../src/main/resources/topics-and-qrels/qrels.robust04.txt): [qrels for the TREC 2004 Robust Track (Topics 301-450 &amp; 601-700)](http://trec.nist.gov/data/robust/qrels.robust2004.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.robust04.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
 -output runs/run.robust04.bm25.topics.robust04.txt \
 -bm25 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.robust04.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
 -output runs/run.robust04.bm25+rm3.topics.robust04.txt \
 -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.robust04.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
 -output runs/run.robust04.bm25+ax.topics.robust04.txt \
 -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.robust04.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
 -output runs/run.robust04.ql.topics.robust04.txt \
 -qld &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.robust04.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
 -output runs/run.robust04.ql+rm3.topics.robust04.txt \
 -qld -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.robust04.pos+docvectors+raw \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
 -output runs/run.robust04.ql+ax.topics.robust04.txt \
 -qld -axiom -axiom.deterministic -rerankCutoff 20 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.robust04.bm25.topics.robust04.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.robust04.bm25+rm3.topics.robust04.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.robust04.bm25+ax.topics.robust04.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.robust04.ql.topics.robust04.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.robust04.ql+rm3.topics.robust04.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.robust04.ql+ax.topics.robust04.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2004 Robust Track Topics](../src/main/resources/topics-and-qrels/topics.robust04.txt)| 0.2531    | 0.2903    | 0.2896    | 0.2467    | 0.2747    | 0.2774    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2004 Robust Track Topics](../src/main/resources/topics-and-qrels/topics.robust04.txt)| 0.3102    | 0.3365    | 0.3333    | 0.3079    | 0.3232    | 0.3229    |

## Replication Log

+ Results replicated by [@chriskamphuis](https://github.com/chriskamphuis) on 2018-12-18 (commit [`a15235`](https://github.com/castorini/Anserini/commit/a152359435ac6ae694b39f561343bba5eed8fdc9))
+ Results replicated by [@kelvin-jiang](https://github.com/kelvin-jiang) on 2019-09-08 (commit [`a1892ae`](https://github.com/castorini/anserini/commit/a1892aec726efe55111a7bc501ab0914afab3a30))
+ Results replicated by [@JMMackenzie](https://github.com/JMMackenzie) on 2020-01-21 (commit [`f63cd22`](https://github.com/castorini/anserini/commit/f63cd2275fa5a9d4da2d17e5f983a3308e8b50ce))
+ Results replicated by [@nikhilro](https://github.com/nikhilro) on 2020-01-26 (commit [`d5ee069`](https://github.com/castorini/anserini/commit/d5ee069399e6a306d7685bda756c1f19db721156))
+ Results replicated by [@edwinzhng](https://github.com/edwinzhng) on 2020-01-26 (commit [`7b76dfb`](https://github.com/castorini/anserini/commit/7b76dfbea7e0c01a3a5dc13e74f54852c780ec9b))
+ Results replicated by [@yuki617](https://github.com/yuki617) on 2020-05-17 (commit [`cee4463`](https://github.com/castorini/anserini/commit/cee446338137415899436f0b2f2d738769745cde))
+ Results replicated by [@x65han](https://github.com/x65han) on 2020-05-19 (commit [`33b0684`](https://github.com/castorini/anserini/commit/33b068437c4582067486e5fe79dfbecb8d4a145c))
+ Results replicated by [@yxzhu16](https://github.com/yxzhu16) on 2020-07-17 (commit [`fad12be`](https://github.com/castorini/anserini/commit/fad12be2e37a075100707c3a674eb67bc0aa57ef))
