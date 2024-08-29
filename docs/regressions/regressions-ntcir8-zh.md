# Anserini Regressions: NTCIR-8 Monolingual Chinese

This page documents BM25 regression experiments for [NTCIR-8 ACLIA (IR4QA subtask), monolingual Chinese topics](http://research.nii.ac.jp/ntcir/ntcir-ws8/ws-en.html).
The description of the document collection can be found in the [NTCIR-8 data page](http://research.nii.ac.jp/ntcir/permission/ntcir-8/perm-en-ACLIA.html).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/ntcir8-zh.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/ntcir8-zh.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression ntcir8-zh
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection CleanTrecCollection \
  -input /path/to/ntcir8-zh \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.ntcir8-zh/ \
  -storePositions -storeDocvectors -storeRaw -language zh -uniqueDocid -optimize \
  >& logs/log.ntcir8-zh &
```

The collection comprises Xinhua articles from 2002-2005, totaling 308,845 documents, from [LDC2007T38: Chinese Gigaword Third Edition](https://catalog.ldc.upenn.edu/LDC2007T38).
We build the index directly from the raw LDC data:
the directory `/path/to/ntcir8-zh/` should point to the directory `data/xin_cmn/` from LDC2007T38.
In that directory, there should be 48 gzipped files matching the pattern `xin_cmn_200[2-5]*`.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
They are downloaded from the [NTCIR Test Collection page](https://www.nii.ac.jp/dsc/idr/en/ntcir/ntcir.html):

+ [`topics.ntcir8zh.eval.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.ntcir8zh.eval.txt): NTCIR-8 ACLIA (IR4QA subtask), monolingual Chinese topics
+ [`qrels.ntcir8.eval.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.ntcir8.eval.txt): NTCIR-8 ACLIA (IR4QA subtask) relevance judgments

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.ntcir8-zh/ \
  -topics tools/topics-and-qrels/topics.ntcir8zh.eval.txt \
  -topicReader TsvString \
  -output runs/run.ntcir8-zh.bm25.topics.ntcir8zh.eval.txt \
  -bm25 -language zh &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -m map -m P.20 -m ndcg_cut.20 tools/topics-and-qrels/qrels.ntcir8.eval.txt runs/run.ntcir8-zh.bm25.topics.ntcir8zh.eval.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [NTCIR-8 ACLIA (IR4QA subtask, Monolingual Chinese)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.ntcir8zh.eval.txt)| 0.4014    |
| **P20**                                                                                                      | **BM25**  |
| [NTCIR-8 ACLIA (IR4QA subtask, Monolingual Chinese)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.ntcir8zh.eval.txt)| 0.3849    |
| **nDCG@20**                                                                                                  | **BM25**  |
| [NTCIR-8 ACLIA (IR4QA subtask, Monolingual Chinese)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.ntcir8zh.eval.txt)| 0.4757    |
