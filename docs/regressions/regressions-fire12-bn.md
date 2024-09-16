# Anserini Regressions: FIRE 2012 Monolingual Bengali

This page documents BM25 regression experiments for [FIRE 2012 ad hoc retrieval (Monolingual Bengali)](https://www.isical.ac.in/~fire/2012/adhoc.html).
The document collection can be found in [FIRE data page](http://fire.irsi.res.in/fire/static/data).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/fire12-bn.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/fire12-bn.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression fire12-bn
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection CleanTrecCollection \
  -input /path/to/fire12-bn \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.fire12-bn/ \
  -storePositions -storeDocvectors -storeRaw -language bn \
  >& logs/log.fire12-bn &
```

The directory `/path/to/fire12-bn/` should be a directory containing the collection, containing `bn_ABP` and `bn_BDNews24` directories.
There should be 500,122 documents in total.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
They are downloaded from the [FIRE data page](http://fire.irsi.res.in/fire/static/data):

+ [`topics.fire12bn.176-225.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.fire12bn.176-225.txt): topics for FIRE 2012 Monolingual Bengali (176 to 225)
+ [`qrels.fire12bn.176-225.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.fire12bn.176-225.txt): qrels (version II) for FIRE 2012 Monolingual Bengali (176 to 225)

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.fire12-bn/ \
  -topics tools/topics-and-qrels/topics.fire12bn.176-225.txt \
  -topicReader Trec \
  -output runs/run.fire12-bn.bm25.topics.fire12bn.176-225.txt \
  -bm25 -language bn &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -m map -m P.20 -m ndcg_cut.20 tools/topics-and-qrels/qrels.fire12bn.176-225.txt runs/run.fire12-bn.bm25.topics.fire12bn.176-225.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [FIRE 2012 (Monolingual Bengali)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.fire12bn.176-225.txt)| 0.2881    |
| **P20**                                                                                                      | **BM25**  |
| [FIRE 2012 (Monolingual Bengali)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.fire12bn.176-225.txt)| 0.3740    |
| **nDCG@20**                                                                                                  | **BM25**  |
| [FIRE 2012 (Monolingual Bengali)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.fire12bn.176-225.txt)| 0.4261    |
