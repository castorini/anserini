# Anserini: Regressions for [Wt10g](http://ir.dcs.gla.ac.uk/test_collections/wt10g.html)

This page describes regressions for the TREC-9 Web Track and the TREC 2001 Web Track, which uses the [Wt10g collection](http://ir.dcs.gla.ac.uk/test_collections/wt10g.html).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/wt10g.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/wt10g.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection TrecwebCollection \
  -input /path/to/wt10g \
  -index indexes/lucene-index.wt10g/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.wt10g &
```

The directory `/path/to/wt10g/` should be the root directory of the [Wt10g collection](http://ir.dcs.gla.ac.uk/test_collections/wt10g.html), containing a bunch of subdirectories, `WTX001` to `WTX104`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.adhoc.451-550.txt`](../src/main/resources/topics-and-qrels/topics.adhoc.451-550.txt): topics for the [TREC-9 Web Track](http://trec.nist.gov/data/topics_eng/topics.451-500.gz) and the [TREC 2001 Web Track](http://trec.nist.gov/data/topics_eng/topics.501-550.txt)
+ [`qrels.adhoc.451-550.txt`](../src/main/resources/topics-and-qrels/qrels.adhoc.451-550.txt): qrels for the [TREC-9 Web Track](http://trec.nist.gov/data/qrels_eng/qrels.trec9.main_web.gz) and the [TREC 2001 Web Track](http://trec.nist.gov/data/qrels_eng/adhoc_qrels.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wt10g/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.451-550.txt -topicreader Trec \
  -output runs/run.wt10g.bm25.topics.adhoc.451-550.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wt10g/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.451-550.txt -topicreader Trec \
  -output runs/run.wt10g.bm25+rm3.topics.adhoc.451-550.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wt10g/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.451-550.txt -topicreader Trec \
  -output runs/run.wt10g.bm25+ax.topics.adhoc.451-550.txt \
  -bm25 -axiom -axiom.beta 0.1 -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wt10g/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.451-550.txt -topicreader Trec \
  -output runs/run.wt10g.ql.topics.adhoc.451-550.txt \
  -qld &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wt10g/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.451-550.txt -topicreader Trec \
  -output runs/run.wt10g.ql+rm3.topics.adhoc.451-550.txt \
  -qld -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wt10g/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.451-550.txt -topicreader Trec \
  -output runs/run.wt10g.ql+ax.topics.adhoc.451-550.txt \
  -qld -axiom -axiom.beta 0.1 -axiom.deterministic -rerankCutoff 20 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.451-550.txt runs/run.wt10g.bm25.topics.adhoc.451-550.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.451-550.txt runs/run.wt10g.bm25+rm3.topics.adhoc.451-550.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.451-550.txt runs/run.wt10g.bm25+ax.topics.adhoc.451-550.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.451-550.txt runs/run.wt10g.ql.topics.adhoc.451-550.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.451-550.txt runs/run.wt10g.ql+rm3.topics.adhoc.451-550.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.451-550.txt runs/run.wt10g.ql+ax.topics.adhoc.451-550.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[Wt10g (Topics 451-550)](../src/main/resources/topics-and-qrels/topics.adhoc.451-550.txt)| 0.1991    | 0.2270    | 0.2196    | 0.2021    | 0.2188    | 0.2268    |


P30                                     | BM25      | +RM3      | +Ax       | QL        | +RM3      | +Ax       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[Wt10g (Topics 451-550)](../src/main/resources/topics-and-qrels/topics.adhoc.451-550.txt)| 0.2211    | 0.2401    | 0.2466    | 0.2180    | 0.2306    | 0.2469    |
