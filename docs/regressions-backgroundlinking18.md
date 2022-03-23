# Anserini Regressions: TREC 2018 News Track (Background Linking)

**Models**: various bag-of-words approaches

This page describes regressions for the background linking task in the [TREC 2018 News Track](http://trec-news.org/).
The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/backgroundlinking18.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/backgroundlinking18.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression backgroundlinking18
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection WashingtonPostCollection \
  -input /path/to/wapo.v2 \
  -index indexes/lucene-index.wapo.v2/ \
  -generator WashingtonPostGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.wapo.v2 &
```

The directory `/path/to/core18/` should be the root directory of the [TREC Washington Post Corpus](https://trec.nist.gov/data/wapost/), i.e., `ls /path/to/core18/`
should bring up a single JSON file.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ [`topics.backgroundlinking18.txt`](../src/main/resources/topics-and-qrels/topics.backgroundlinking18.txt): [topics for the background linking task of the TREC 2018 News Track](https://trec.nist.gov/data/news/2018/newsir18-topics.txt)
+ [`qrels.backgroundlinking18.txt`](../src/main/resources/topics-and-qrels/qrels.backgroundlinking18.txt): [qrels for the background linking task of the TREC 2018 News Track](https://trec.nist.gov/data/news/2018/bqrels.exp-gains.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wapo.v2/ \
  -topics src/main/resources/topics-and-qrels/topics.backgroundlinking18.txt \
  -topicreader BackgroundLinking \
  -output runs/run.wapo.v2.bm25.topics.backgroundlinking18.txt \
  -backgroundlinking -backgroundlinking.k 100 -bm25 -hits 100 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wapo.v2/ \
  -topics src/main/resources/topics-and-qrels/topics.backgroundlinking18.txt \
  -topicreader BackgroundLinking \
  -output runs/run.wapo.v2.bm25+rm3.topics.backgroundlinking18.txt \
  -backgroundlinking -backgroundlinking.k 100 -bm25 -rm3 -hits 100 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wapo.v2/ \
  -topics src/main/resources/topics-and-qrels/topics.backgroundlinking18.txt \
  -topicreader BackgroundLinking \
  -output runs/run.wapo.v2.bm25+rm3+df.topics.backgroundlinking18.txt \
  -backgroundlinking -backgroundlinking.datefilter -backgroundlinking.k 100 -bm25 -rm3 -hits 100 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -c -M1000 -m ndcg_cut.5 src/main/resources/topics-and-qrels/qrels.backgroundlinking18.txt runs/run.wapo.v2.bm25.topics.backgroundlinking18.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -c -M1000 -m ndcg_cut.5 src/main/resources/topics-and-qrels/qrels.backgroundlinking18.txt runs/run.wapo.v2.bm25+rm3.topics.backgroundlinking18.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -c -M1000 -m ndcg_cut.5 src/main/resources/topics-and-qrels/qrels.backgroundlinking18.txt runs/run.wapo.v2.bm25+rm3+df.topics.backgroundlinking18.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| MAP                                                                                                          | BM25      | +RM3      | +RM3+DF   |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [TREC 2018 Topics](../src/main/resources/topics-and-qrels/topics.backgroundlinking18.txt)                    | 0.2490    | 0.2642    | 0.2692    |


| nDCG@5                                                                                                       | BM25      | +RM3      | +RM3+DF   |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [TREC 2018 Topics](../src/main/resources/topics-and-qrels/topics.backgroundlinking18.txt)                    | 0.3293    | 0.3526    | 0.4171    |

