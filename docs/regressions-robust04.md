# Anserini: Regressions for [Disks 4 &amp; 5](https://trec.nist.gov/data_disks.html) (Robust04)

This page describes regressions for the TREC 2004 Robust Track, which uses [TREC Disks 4 &amp; 5](https://trec.nist.gov/data_disks.html).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/robust04.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/robust04.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection -input /path/to/robust04 \
 -index lucene-index.robust04.pos+docvectors+rawdocs -generator JsoupGenerator -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs >& log.robust04.pos+docvectors+rawdocs &
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
nohup target/appassembler/bin/SearchCollection -index lucene-index.robust04.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
 -bm25 -output run.robust04.bm25.topics.robust04.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.robust04.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
 -bm25 -rm3 -output run.robust04.bm25+rm3.topics.robust04.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.robust04.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -output run.robust04.bm25+ax.topics.robust04.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.robust04.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
 -ql -output run.robust04.ql.topics.robust04.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.robust04.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
 -ql -rm3 -output run.robust04.ql+rm3.topics.robust04.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.robust04.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
 -ql -axiom -rerankCutoff 20 -axiom.deterministic -output run.robust04.ql+ax.topics.robust04.txt &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt run.robust04.bm25.topics.robust04.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt run.robust04.bm25+rm3.topics.robust04.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt run.robust04.bm25+ax.topics.robust04.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt run.robust04.ql.topics.robust04.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt run.robust04.ql+rm3.topics.robust04.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt run.robust04.ql+ax.topics.robust04.txt
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
+ Results replicated by [@infinitecold](https://github.com/infinitecold) on 2019-09-08 (commit [`a1892ae`](https://github.com/castorini/anserini/commit/a1892aec726efe55111a7bc501ab0914afab3a30))
