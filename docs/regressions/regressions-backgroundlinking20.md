# Anserini Regressions: TREC 2020 News Background Linking

**Models**: various bag-of-words approaches

This page describes regressions for the background linking task in the [TREC 2020 News Track](http://trec-news.org/).
The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/backgroundlinking20.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/backgroundlinking20.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression backgroundlinking20
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection WashingtonPostCollection \
  -input /path/to/wapo.v3 \
  -generator WashingtonPostGenerator \
  -index indexes/lucene-index.wapo.v3/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.wapo.v3 &
```

The directory `/path/to/core18/` should be the root directory of the [TREC Washington Post Corpus *v3*](https://trec.nist.gov/data/wapost/), i.e., `ls /path/to/core18/`
should bring up a single JSON file.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
They are downloaded from NIST:

+ [`topics.backgroundlinking20.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.backgroundlinking20.txt): topics for the background linking task of the TREC 2020 News Track
+ [`qrels.backgroundlinking20.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.backgroundlinking20.txt): qrels for the background linking task of the TREC 2020 News Track

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wapo.v3/ \
  -topics tools/topics-and-qrels/topics.backgroundlinking20.txt \
  -topicReader BackgroundLinking \
  -output runs/run.wapo.v3.bm25.topics.backgroundlinking20.txt \
  -backgroundLinking -backgroundLinking.k 100 -bm25 -hits 100 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wapo.v3/ \
  -topics tools/topics-and-qrels/topics.backgroundlinking20.txt \
  -topicReader BackgroundLinking \
  -output runs/run.wapo.v3.bm25+rm3.topics.backgroundlinking20.txt \
  -backgroundLinking -backgroundLinking.k 100 -bm25 -rm3 -hits 100 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wapo.v3/ \
  -topics tools/topics-and-qrels/topics.backgroundlinking20.txt \
  -topicReader BackgroundLinking \
  -output runs/run.wapo.v3.bm25+rm3+df.topics.backgroundlinking20.txt \
  -backgroundLinking -backgroundLinking.dateFilter -backgroundLinking.k 100 -bm25 -rm3 -hits 100 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M1000 -m map -c -M1000 -m ndcg_cut.5 tools/topics-and-qrels/qrels.backgroundlinking20.txt runs/run.wapo.v3.bm25.topics.backgroundlinking20.txt

bin/trec_eval -c -M1000 -m map -c -M1000 -m ndcg_cut.5 tools/topics-and-qrels/qrels.backgroundlinking20.txt runs/run.wapo.v3.bm25+rm3.topics.backgroundlinking20.txt

bin/trec_eval -c -M1000 -m map -c -M1000 -m ndcg_cut.5 tools/topics-and-qrels/qrels.backgroundlinking20.txt runs/run.wapo.v3.bm25+rm3+df.topics.backgroundlinking20.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25**  | **+RM3**  | **+RM3+DF**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [TREC 2020 Topics](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.backgroundlinking20.txt)| 0.3286    | 0.4528    | 0.3438    |
| **nDCG@5**                                                                                                   | **BM25**  | **+RM3**  | **+RM3+DF**|
| [TREC 2020 Topics](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.backgroundlinking20.txt)| 0.5231    | 0.5696    | 0.5304    |

