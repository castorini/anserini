# Anserini Regressions: TREC Disks 4 &amp; 5

**Models**: various bag-of-words approaches

This page describes regressions for ad hoc topics from TREC 7-8, which use [TREC Disks 4 &amp; 5](https://trec.nist.gov/data/cd45/index.html).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/disk45.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/disk45.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression disk45
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection TrecCollection \
  -input /path/to/disk45 \
  -index indexes/lucene-index.disk45/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.disk45 &
```

The directory `/path/to/disk45/` should be the root directory of [TREC Disks 4 &amp; 5](https://trec.nist.gov/data/cd45/index.html); inside each there should be subdirectories like `ft`, `fr94`.
Note that Anserini ignores the `cr` folder when indexing, which is the standard configuration.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.adhoc.351-400.txt`](../src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt): [TREC-7 Ad Hoc Topics 351-400](http://trec.nist.gov/data/topics_eng/)
+ [`topics.adhoc.401-450.txt`](../src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt): [TREC-8 Ad Hoc Topics 401-450](http://trec.nist.gov/data/topics_eng/)
+ [`qrels.adhoc.351-400.txt`](../src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt): [qrels for TREC-7 Ad Hoc Topics 351-400](http://trec.nist.gov/data/qrels_eng/)
+ [`qrels.adhoc.401-450.txt`](../src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt): [qrels for TREC-8 Ad Hoc Topics 401-450](http://trec.nist.gov/data/qrels_eng/)

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25.topics.adhoc.351-400.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25.topics.adhoc.401-450.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25.topics.robust04.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25+rm3.topics.adhoc.351-400.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25+rm3.topics.adhoc.401-450.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25+rm3.topics.robust04.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25+rocchio.topics.adhoc.351-400.txt \
  -bm25 -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25+rocchio.topics.adhoc.401-450.txt \
  -bm25 -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25+rocchio.topics.robust04.txt \
  -bm25 -rocchio &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25+rocchio-neg.topics.adhoc.351-400.txt \
  -bm25 -rocchio -rocchio.useNegative -rerankCutoff 1000 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25+rocchio-neg.topics.adhoc.401-450.txt \
  -bm25 -rocchio -rocchio.useNegative -rerankCutoff 1000 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25+rocchio-neg.topics.robust04.txt \
  -bm25 -rocchio -rocchio.useNegative -rerankCutoff 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25+ax.topics.adhoc.351-400.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25+ax.topics.adhoc.401-450.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -topicreader Trec \
  -output runs/run.disk45.bm25+ax.topics.robust04.txt \
  -bm25 -axiom -axiom.deterministic -rerankCutoff 20 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
  -topicreader Trec \
  -output runs/run.disk45.ql.topics.adhoc.351-400.txt \
  -qld &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
  -topicreader Trec \
  -output runs/run.disk45.ql.topics.adhoc.401-450.txt \
  -qld &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -topicreader Trec \
  -output runs/run.disk45.ql.topics.robust04.txt \
  -qld &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
  -topicreader Trec \
  -output runs/run.disk45.ql+rm3.topics.adhoc.351-400.txt \
  -qld -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
  -topicreader Trec \
  -output runs/run.disk45.ql+rm3.topics.adhoc.401-450.txt \
  -qld -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -topicreader Trec \
  -output runs/run.disk45.ql+rm3.topics.robust04.txt \
  -qld -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
  -topicreader Trec \
  -output runs/run.disk45.qld+rocchio.topics.adhoc.351-400.txt \
  -qld -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
  -topicreader Trec \
  -output runs/run.disk45.qld+rocchio.topics.adhoc.401-450.txt \
  -qld -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -topicreader Trec \
  -output runs/run.disk45.qld+rocchio.topics.robust04.txt \
  -qld -rocchio &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
  -topicreader Trec \
  -output runs/run.disk45.qld+rocchio-neg.topics.adhoc.351-400.txt \
  -qld -rocchio -rocchio.useNegative -rerankCutoff 1000 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
  -topicreader Trec \
  -output runs/run.disk45.qld+rocchio-neg.topics.adhoc.401-450.txt \
  -qld -rocchio -rocchio.useNegative -rerankCutoff 1000 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -topicreader Trec \
  -output runs/run.disk45.qld+rocchio-neg.topics.robust04.txt \
  -qld -rocchio -rocchio.useNegative -rerankCutoff 1000 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt \
  -topicreader Trec \
  -output runs/run.disk45.ql+ax.topics.adhoc.351-400.txt \
  -qld -axiom -axiom.deterministic -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt \
  -topicreader Trec \
  -output runs/run.disk45.ql+ax.topics.adhoc.401-450.txt \
  -qld -axiom -axiom.deterministic -rerankCutoff 20 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.disk45/ \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -topicreader Trec \
  -output runs/run.disk45.ql+ax.topics.robust04.txt \
  -qld -axiom -axiom.deterministic -rerankCutoff 20 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.bm25.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.bm25.topics.adhoc.401-450.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.disk45.bm25.topics.robust04.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.bm25+rm3.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.bm25+rm3.topics.adhoc.401-450.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.disk45.bm25+rm3.topics.robust04.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.bm25+rocchio.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.bm25+rocchio.topics.adhoc.401-450.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.disk45.bm25+rocchio.topics.robust04.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.bm25+rocchio-neg.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.bm25+rocchio-neg.topics.adhoc.401-450.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.disk45.bm25+rocchio-neg.topics.robust04.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.bm25+ax.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.bm25+ax.topics.adhoc.401-450.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.disk45.bm25+ax.topics.robust04.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.ql.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.ql.topics.adhoc.401-450.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.disk45.ql.topics.robust04.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.ql+rm3.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.ql+rm3.topics.adhoc.401-450.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.disk45.ql+rm3.topics.robust04.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.qld+rocchio.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.qld+rocchio.topics.adhoc.401-450.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.disk45.qld+rocchio.topics.robust04.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.qld+rocchio-neg.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.qld+rocchio-neg.topics.adhoc.401-450.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.disk45.qld+rocchio-neg.topics.robust04.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.351-400.txt runs/run.disk45.ql+ax.topics.adhoc.351-400.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.adhoc.401-450.txt runs/run.disk45.ql+ax.topics.adhoc.401-450.txt
tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.disk45.ql+ax.topics.robust04.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25**  | **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **QL**    | **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
| [TREC-7 Ad Hoc Topics](../src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt)                      | 0.1862    | 0.2354    | 0.2397    | 0.2391    | 0.2431    | 0.1843    | 0.2146    | 0.2189    | 0.2202    | 0.2298    |
| [TREC-8 Ad Hoc Topics](../src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt)                      | 0.2515    | 0.2746    | 0.2743    | 0.2751    | 0.2812    | 0.2460    | 0.2686    | 0.2709    | 0.2708    | 0.2647    |
| [TREC 2004 Robust Track Topics](../src/main/resources/topics-and-qrels/topics.robust04.txt)                  | 0.2531    | 0.2908    | 0.2924    | 0.2932    | 0.2896    | 0.2467    | 0.2744    | 0.2756    | 0.2773    | 0.2774    |
| **P30**                                                                                                      | **BM25**  | **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   | **QL**    | **+RM3**  | **+Rocchio**| **+Rocchio***| **+Ax**   |
| [TREC-7 Ad Hoc Topics](../src/main/resources/topics-and-qrels/topics.adhoc.351-400.txt)                      | 0.3093    | 0.3407    | 0.3527    | 0.3500    | 0.3287    | 0.3073    | 0.3307    | 0.3293    | 0.3300    | 0.3193    |
| [TREC-8 Ad Hoc Topics](../src/main/resources/topics-and-qrels/topics.adhoc.401-450.txt)                      | 0.3560    | 0.3753    | 0.3707    | 0.3713    | 0.3753    | 0.3480    | 0.3713    | 0.3640    | 0.3660    | 0.3500    |
| [TREC 2004 Robust Track Topics](../src/main/resources/topics-and-qrels/topics.robust04.txt)                  | 0.3102    | 0.3349    | 0.3364    | 0.3378    | 0.3333    | 0.3079    | 0.3244    | 0.3237    | 0.3246    | 0.3229    |

## Reproduction Log[*](reproducibility.md)

(Prior to the addition of TREC 7/8 topics)

+ Results reproduced by [@chriskamphuis](https://github.com/chriskamphuis) on 2018-12-18 (commit [`a15235`](https://github.com/castorini/Anserini/commit/a152359435ac6ae694b39f561343bba5eed8fdc9))
+ Results reproduced by [@kelvin-jiang](https://github.com/kelvin-jiang) on 2019-09-08 (commit [`a1892ae`](https://github.com/castorini/anserini/commit/a1892aec726efe55111a7bc501ab0914afab3a30))
+ Results reproduced by [@JMMackenzie](https://github.com/JMMackenzie) on 2020-01-21 (commit [`f63cd22`](https://github.com/castorini/anserini/commit/f63cd2275fa5a9d4da2d17e5f983a3308e8b50ce))
+ Results reproduced by [@nikhilro](https://github.com/nikhilro) on 2020-01-26 (commit [`d5ee069`](https://github.com/castorini/anserini/commit/d5ee069399e6a306d7685bda756c1f19db721156))
+ Results reproduced by [@edwinzhng](https://github.com/edwinzhng) on 2020-01-26 (commit [`7b76dfb`](https://github.com/castorini/anserini/commit/7b76dfbea7e0c01a3a5dc13e74f54852c780ec9b))
+ Results reproduced by [@yuki617](https://github.com/yuki617) on 2020-05-17 (commit [`cee4463`](https://github.com/castorini/anserini/commit/cee446338137415899436f0b2f2d738769745cde))
+ Results reproduced by [@x65han](https://github.com/x65han) on 2020-05-19 (commit [`33b0684`](https://github.com/castorini/anserini/commit/33b068437c4582067486e5fe79dfbecb8d4a145c))
+ Results reproduced by [@yxzhu16](https://github.com/yxzhu16) on 2020-07-17 (commit [`fad12be`](https://github.com/castorini/anserini/commit/fad12be2e37a075100707c3a674eb67bc0aa57ef))
