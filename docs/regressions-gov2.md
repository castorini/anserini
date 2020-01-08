# Anserini: Regressions for [Gov2](http://ir.dcs.gla.ac.uk/test_collections/gov2-summary.htm)

This page describes regressions for the Terabyte Tracks from TREC 2004 to 2006, which uses the [Gov2 collection](http://ir.dcs.gla.ac.uk/test_collections/gov2-summary.htm).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/gov2.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/gov2.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecwebCollection -input /path/to/gov2 \
 -index lucene-index.gov2.pos+docvectors+rawdocs -generator JsoupGenerator -threads 44 \
 -storePositions -storeDocvectors -storeRawDocs >& log.gov2.pos+docvectors+rawdocs &
```

The directory `/path/to/gov2/` should be the root directory of the [Gov2 collection](http://ir.dcs.gla.ac.uk/test_collections/gov2-summary.htm), i.e., `ls /path/to/gov2/` should bring up a bunch of subdirectories, `GX000` to `GX272`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.terabyte04.701-750.txt`](../src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt): [topics for the TREC 2004 Terabyte Track (Topics 701-750)](http://trec.nist.gov/data/terabyte/04/04topics.701-750.txt)
+ [`topics.terabyte05.751-800.txt`](../src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt): [topics for the TREC 2005 Terabyte Track (Topics 751-800)](http://trec.nist.gov/data/terabyte/05/05.topics.751-800.txt)
+ [`topics.terabyte06.801-850.txt`](../src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt): [topics for the TREC 2006 Terabyte Track (Topics 801-850)](http://trec.nist.gov/data/terabyte/06/06.topics.801-850.txt)
+ [`qrels.terabyte04.701-750.txt`](../src/main/resources/topics-and-qrels/qrels.terabyte04.701-750.txt): [qrels for the TREC 2004 Terabyte Track (Topics 701-750)](http://trec.nist.gov/data/terabyte/04/04.qrels.12-Nov-04)
+ [`qrels.terabyte05.751-800.txt`](../src/main/resources/topics-and-qrels/qrels.terabyte05.751-800.txt): [qrels for the TREC 2005 Terabyte Track (Topics 751-800)](http://trec.nist.gov/data/terabyte/05/05.adhoc_qrels)
+ [`qrels.terabyte06.801-850.txt`](../src/main/resources/topics-and-qrels/qrels.terabyte06.801-850.txt): [qrels for the TREC 2006 Terabyte Track (Topics 801-850)](http://trec.nist.gov/data/terabyte/06/qrels.tb06.top50)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt \
 -bm25 -output run.gov2.bm25.topics.terabyte04.701-750.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt \
 -bm25 -output run.gov2.bm25.topics.terabyte05.751-800.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt \
 -bm25 -output run.gov2.bm25.topics.terabyte06.801-850.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt \
 -bm25 -rm3 -output run.gov2.bm25+rm3.topics.terabyte04.701-750.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt \
 -bm25 -rm3 -output run.gov2.bm25+rm3.topics.terabyte05.751-800.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt \
 -bm25 -rm3 -output run.gov2.bm25+rm3.topics.terabyte06.801-850.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt \
 -bm25 -axiom -axiom.beta 0.1 -rerankCutoff 20 -axiom.deterministic -output run.gov2.bm25+ax.topics.terabyte04.701-750.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt \
 -bm25 -axiom -axiom.beta 0.1 -rerankCutoff 20 -axiom.deterministic -output run.gov2.bm25+ax.topics.terabyte05.751-800.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt \
 -bm25 -axiom -axiom.beta 0.1 -rerankCutoff 20 -axiom.deterministic -output run.gov2.bm25+ax.topics.terabyte06.801-850.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt \
 -ql -output run.gov2.ql.topics.terabyte04.701-750.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt \
 -ql -output run.gov2.ql.topics.terabyte05.751-800.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt \
 -ql -output run.gov2.ql.topics.terabyte06.801-850.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt \
 -ql -rm3 -output run.gov2.ql+rm3.topics.terabyte04.701-750.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt \
 -ql -rm3 -output run.gov2.ql+rm3.topics.terabyte05.751-800.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt \
 -ql -rm3 -output run.gov2.ql+rm3.topics.terabyte06.801-850.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt \
 -ql -axiom -rerankCutoff 20 -axiom.beta 0.1 -axiom.deterministic -output run.gov2.ql+ax.topics.terabyte04.701-750.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt \
 -ql -axiom -rerankCutoff 20 -axiom.beta 0.1 -axiom.deterministic -output run.gov2.ql+ax.topics.terabyte05.751-800.txt &
nohup target/appassembler/bin/SearchCollection -index lucene-index.gov2.pos+docvectors+rawdocs \
 -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt \
 -ql -axiom -rerankCutoff 20 -axiom.beta 0.1 -axiom.deterministic -output run.gov2.ql+ax.topics.terabyte06.801-850.txt &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte04.701-750.txt run.gov2.bm25.topics.terabyte04.701-750.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte05.751-800.txt run.gov2.bm25.topics.terabyte05.751-800.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte06.801-850.txt run.gov2.bm25.topics.terabyte06.801-850.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte04.701-750.txt run.gov2.bm25+rm3.topics.terabyte04.701-750.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte05.751-800.txt run.gov2.bm25+rm3.topics.terabyte05.751-800.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte06.801-850.txt run.gov2.bm25+rm3.topics.terabyte06.801-850.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte04.701-750.txt run.gov2.bm25+ax.topics.terabyte04.701-750.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte05.751-800.txt run.gov2.bm25+ax.topics.terabyte05.751-800.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte06.801-850.txt run.gov2.bm25+ax.topics.terabyte06.801-850.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte04.701-750.txt run.gov2.ql.topics.terabyte04.701-750.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte05.751-800.txt run.gov2.ql.topics.terabyte05.751-800.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte06.801-850.txt run.gov2.ql.topics.terabyte06.801-850.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte04.701-750.txt run.gov2.ql+rm3.topics.terabyte04.701-750.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte05.751-800.txt run.gov2.ql+rm3.topics.terabyte05.751-800.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte06.801-850.txt run.gov2.ql+rm3.topics.terabyte06.801-850.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte04.701-750.txt run.gov2.ql+ax.topics.terabyte04.701-750.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte05.751-800.txt run.gov2.ql+ax.topics.terabyte05.751-800.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte06.801-850.txt run.gov2.ql+ax.topics.terabyte06.801-850.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2004 Terabyte Track (Topics 701-750)](../src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt)| 0.2689    | 0.2844    | 0.2669    | 0.2681    | 0.2708    | 0.2666    |
[TREC 2005 Terabyte Track (Topics 751-800)](../src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt)| 0.3390    | 0.3820    | 0.3666    | 0.3303    | 0.3559    | 0.3646    |
[TREC 2006 Terabyte Track (Topics 801-850)](../src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt)| 0.3080    | 0.3377    | 0.3069    | 0.2997    | 0.3154    | 0.3084    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2004 Terabyte Track (Topics 701-750)](../src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt)| 0.4864    | 0.5190    | 0.4993    | 0.4755    | 0.4925    | 0.4932    |
[TREC 2005 Terabyte Track (Topics 751-800)](../src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt)| 0.5540    | 0.5920    | 0.5933    | 0.5347    | 0.5620    | 0.5840    |
[TREC 2006 Terabyte Track (Topics 801-850)](../src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt)| 0.4907    | 0.5160    | 0.5033    | 0.4720    | 0.4847    | 0.4920    |
