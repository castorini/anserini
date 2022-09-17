# Anserini Regressions: Gov2

**Models**: various bag-of-words approaches

This page describes regressions for the Terabyte Tracks from TREC 2004 to 2006, which uses the [Gov2 collection](http://ir.dcs.gla.ac.uk/test_collections/gov2-summary.htm).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/gov2.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/gov2.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression gov2
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection TrecwebCollection \
  -input /path/to/gov2 \
  -index indexes/lucene-index.gov2/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 44 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.gov2 &
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
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt \
  -topicreader Trec \
  -output runs/run.gov2.bm25.topics.terabyte04.701-750.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt \
  -topicreader Trec \
  -output runs/run.gov2.bm25.topics.terabyte05.751-800.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt \
  -topicreader Trec \
  -output runs/run.gov2.bm25.topics.terabyte06.801-850.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt \
  -topicreader Trec \
  -output runs/run.gov2.bm25+rm3.topics.terabyte04.701-750.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt \
  -topicreader Trec \
  -output runs/run.gov2.bm25+rm3.topics.terabyte05.751-800.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt \
  -topicreader Trec \
  -output runs/run.gov2.bm25+rm3.topics.terabyte06.801-850.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt \
  -topicreader Trec \
  -output runs/run.gov2.bm25+ax.topics.terabyte04.701-750.txt \
  -bm25 -axiom -axiom.beta 0.1 -axiom.deterministic -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt \
  -topicreader Trec \
  -output runs/run.gov2.bm25+ax.topics.terabyte05.751-800.txt \
  -bm25 -axiom -axiom.beta 0.1 -axiom.deterministic -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt \
  -topicreader Trec \
  -output runs/run.gov2.bm25+ax.topics.terabyte06.801-850.txt \
  -bm25 -axiom -axiom.beta 0.1 -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt \
  -topicreader Trec \
  -output runs/run.gov2.ql.topics.terabyte04.701-750.txt \
  -qld &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt \
  -topicreader Trec \
  -output runs/run.gov2.ql.topics.terabyte05.751-800.txt \
  -qld &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt \
  -topicreader Trec \
  -output runs/run.gov2.ql.topics.terabyte06.801-850.txt \
  -qld &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt \
  -topicreader Trec \
  -output runs/run.gov2.ql+rm3.topics.terabyte04.701-750.txt \
  -qld -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt \
  -topicreader Trec \
  -output runs/run.gov2.ql+rm3.topics.terabyte05.751-800.txt \
  -qld -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt \
  -topicreader Trec \
  -output runs/run.gov2.ql+rm3.topics.terabyte06.801-850.txt \
  -qld -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt \
  -topicreader Trec \
  -output runs/run.gov2.ql+ax.topics.terabyte04.701-750.txt \
  -qld -axiom -axiom.beta 0.1 -axiom.deterministic -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt \
  -topicreader Trec \
  -output runs/run.gov2.ql+ax.topics.terabyte05.751-800.txt \
  -qld -axiom -axiom.beta 0.1 -axiom.deterministic -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.gov2/ \
  -topics src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt \
  -topicreader Trec \
  -output runs/run.gov2.ql+ax.topics.terabyte06.801-850.txt \
  -qld -axiom -axiom.beta 0.1 -axiom.deterministic -rerankCutoff 20 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte04.701-750.txt runs/run.gov2.bm25.topics.terabyte04.701-750.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte05.751-800.txt runs/run.gov2.bm25.topics.terabyte05.751-800.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte06.801-850.txt runs/run.gov2.bm25.topics.terabyte06.801-850.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte04.701-750.txt runs/run.gov2.bm25+rm3.topics.terabyte04.701-750.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte05.751-800.txt runs/run.gov2.bm25+rm3.topics.terabyte05.751-800.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte06.801-850.txt runs/run.gov2.bm25+rm3.topics.terabyte06.801-850.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte04.701-750.txt runs/run.gov2.bm25+ax.topics.terabyte04.701-750.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte05.751-800.txt runs/run.gov2.bm25+ax.topics.terabyte05.751-800.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte06.801-850.txt runs/run.gov2.bm25+ax.topics.terabyte06.801-850.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte04.701-750.txt runs/run.gov2.ql.topics.terabyte04.701-750.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte05.751-800.txt runs/run.gov2.ql.topics.terabyte05.751-800.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte06.801-850.txt runs/run.gov2.ql.topics.terabyte06.801-850.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte04.701-750.txt runs/run.gov2.ql+rm3.topics.terabyte04.701-750.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte05.751-800.txt runs/run.gov2.ql+rm3.topics.terabyte05.751-800.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte06.801-850.txt runs/run.gov2.ql+rm3.topics.terabyte06.801-850.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte04.701-750.txt runs/run.gov2.ql+ax.topics.terabyte04.701-750.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte05.751-800.txt runs/run.gov2.ql+ax.topics.terabyte05.751-800.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.terabyte06.801-850.txt runs/run.gov2.ql+ax.topics.terabyte06.801-850.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25**  | **+RM3**  | **+Ax**   | **QL**    | **+RM3**  | **+Ax**   |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
| [TREC 2004 Terabyte Track (Topics 701-750)](../src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt)| 0.2689    | 0.2840    | 0.2716    | 0.2681    | 0.2699    | 0.2714    |
| [TREC 2005 Terabyte Track (Topics 751-800)](../src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt)| 0.3391    | 0.3814    | 0.3664    | 0.3304    | 0.3555    | 0.3620    |
| [TREC 2006 Terabyte Track (Topics 801-850)](../src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt)| 0.3081    | 0.3372    | 0.3088    | 0.2998    | 0.3159    | 0.3075    |
| **P30**                                                                                                      | **BM25**  | **+RM3**  | **+Ax**   | **QL**    | **+RM3**  | **+Ax**   |
| [TREC 2004 Terabyte Track (Topics 701-750)](../src/main/resources/topics-and-qrels/topics.terabyte04.701-750.txt)| 0.4864    | 0.5156    | 0.5109    | 0.4755    | 0.4939    | 0.4973    |
| [TREC 2005 Terabyte Track (Topics 751-800)](../src/main/resources/topics-and-qrels/topics.terabyte05.751-800.txt)| 0.5540    | 0.5933    | 0.5967    | 0.5347    | 0.5580    | 0.5887    |
| [TREC 2006 Terabyte Track (Topics 801-850)](../src/main/resources/topics-and-qrels/topics.terabyte06.801-850.txt)| 0.4907    | 0.5167    | 0.5107    | 0.4727    | 0.4867    | 0.5027    |
