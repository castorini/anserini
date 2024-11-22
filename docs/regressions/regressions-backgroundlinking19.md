# Anserini Regressions: TREC 2019 News Background Linking

**Models**: various bag-of-words approaches

This page describes regressions for the background linking task in the [TREC 2019 News Track](http://trec-news.org/).
The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/backgroundlinking19.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/backgroundlinking19.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression backgroundlinking19
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection WashingtonPostCollection \
  -input /path/to/wapo.v2 \
  -generator WashingtonPostGenerator \
  -index indexes/lucene-index.wapo.v2/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.wapo.v2 &
```

The directory `/path/to/core18/` should be the root directory of the [TREC Washington Post Corpus](https://trec.nist.gov/data/wapost/), i.e., `ls /path/to/core18/`
should bring up a single JSON file.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
They are downloaded from NIST:

+ [`topics.backgroundlinking19.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.backgroundlinking19.txt): topics for the background linking task of the TREC 2019 News Track
+ [`qrels.backgroundlinking19.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.backgroundlinking19.txt): qrels for the background linking task of the TREC 2019 News Track

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wapo.v2/ \
  -topics tools/topics-and-qrels/topics.backgroundlinking19.txt \
  -topicReader BackgroundLinking \
  -output runs/run.wapo.v2.bm25.topics.backgroundlinking19.txt \
  -backgroundLinking -backgroundLinking.k 100 -bm25 -hits 100 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wapo.v2/ \
  -topics tools/topics-and-qrels/topics.backgroundlinking19.txt \
  -topicReader BackgroundLinking \
  -output runs/run.wapo.v2.bm25+rm3.topics.backgroundlinking19.txt \
  -backgroundLinking -backgroundLinking.k 100 -bm25 -rm3 -hits 100 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wapo.v2/ \
  -topics tools/topics-and-qrels/topics.backgroundlinking19.txt \
  -topicReader BackgroundLinking \
  -output runs/run.wapo.v2.bm25+rm3+df.topics.backgroundlinking19.txt \
  -backgroundLinking -backgroundLinking.dateFilter -backgroundLinking.k 100 -bm25 -rm3 -hits 100 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M1000 -m map -c -M1000 -m ndcg_cut.5 tools/topics-and-qrels/qrels.backgroundlinking19.txt runs/run.wapo.v2.bm25.topics.backgroundlinking19.txt

bin/trec_eval -c -M1000 -m map -c -M1000 -m ndcg_cut.5 tools/topics-and-qrels/qrels.backgroundlinking19.txt runs/run.wapo.v2.bm25+rm3.topics.backgroundlinking19.txt

bin/trec_eval -c -M1000 -m map -c -M1000 -m ndcg_cut.5 tools/topics-and-qrels/qrels.backgroundlinking19.txt runs/run.wapo.v2.bm25+rm3+df.topics.backgroundlinking19.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25**  | **+RM3**  | **+RM3+DF**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [TREC 2019 Topics](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.backgroundlinking19.txt)| 0.3029    | 0.3787    | 0.3160    |
| **nDCG@5**                                                                                                   | **BM25**  | **+RM3**  | **+RM3+DF**|
| [TREC 2019 Topics](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.backgroundlinking19.txt)| 0.4785    | 0.5200    | 0.5018    |

