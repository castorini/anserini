# Anserini: Regressions for [AQUAINT](https://tac.nist.gov//data/data_desc.html#AQUAINT) (Robust05)

This page describes regressions for the TREC 2005 Robust Track, which uses the [AQUAINT collection](https://tac.nist.gov//data/data_desc.html#AQUAINT).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/robust05.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/robust05.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection -input /path/to/robust05 \
 -index lucene-index.robust05.pos+docvectors+rawdocs -generator JsoupGenerator -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs >& log.robust05.pos+docvectors+rawdocs &
```

The directory `/path/to/aquaint/` should be the root directory of the [AQUAINT collection](https://tac.nist.gov//data/data_desc.html#AQUAINT); under subdirectory `disk1/` there should be `NYT/` and under subdirectory `disk2/` there should be `APW/` and `XIE/`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.robust05.txt`](../src/main/resources/topics-and-qrels/topics.robust05.txt): [topics for the TREC 2005 Robust Track (Hard Topics of Robust04)](http://trec.nist.gov/data/robust/05/05.50.topics.txt)
+ [`qrels.robust05.txt`](../src/main/resources/topics-and-qrels/qrels.robust05.txt): [qrels for the TREC 2005 Robust Track (Hard Topics of Robust04)](http://trec.nist.gov/data/robust/05/TREC2005.qrels.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.robust05.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust05.txt \
 -bm25 -output run.robust05.bm25.topics.robust05.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.robust05.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust05.txt \
 -bm25 -rm3 -output run.robust05.bm25+rm3.topics.robust05.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.robust05.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust05.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -output run.robust05.bm25+ax.topics.robust05.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.robust05.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust05.txt \
 -ql -output run.robust05.ql.topics.robust05.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.robust05.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust05.txt \
 -ql -rm3 -output run.robust05.ql+rm3.topics.robust05.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.robust05.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.robust05.txt \
 -ql -axiom -rerankCutoff 20 -axiom.deterministic -output run.robust05.ql+ax.topics.robust05.txt &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust05.txt run.robust05.bm25.topics.robust05.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust05.txt run.robust05.bm25+rm3.topics.robust05.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust05.txt run.robust05.bm25+ax.topics.robust05.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust05.txt run.robust05.ql.topics.robust05.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust05.txt run.robust05.ql+rm3.topics.robust05.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust05.txt run.robust05.ql+ax.topics.robust05.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2005 Robust Track Topics](../src/main/resources/topics-and-qrels/topics.robust05.txt)| 0.2032    | 0.2602    | 0.2587    | 0.2028    | 0.2491    | 0.2476    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2005 Robust Track Topics](../src/main/resources/topics-and-qrels/topics.robust05.txt)| 0.3693    | 0.4187    | 0.4120    | 0.3653    | 0.4067    | 0.4113    |
