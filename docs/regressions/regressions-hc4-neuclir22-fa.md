# Anserini Regressions: HC4 (v1.0) on NeuCLIR22 &mdash; Persian

This page documents BM25 regression experiments for [HC4 (v1.0) Persian topics](https://github.com/hltcoe/HC4) on the [NeuCLIR22 Persian corpus](https://neuclir.github.io/).
The HC4 qrels have been filtered down to include only those in the intersection of the HC4 and NeuCLIR22 corpora.
To be clear, the queries are in Persian (human translations) and the corpus is in Persian.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/hc4-neuclir22-fa.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/hc4-neuclir22-fa.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-fa
```

## Corpus Download

The HC4 corpus can be downloaded following the instructions [here](https://github.com/hltcoe/HC4).

After download, verify that all and only specified documents have been downloaded by running the code [provided here](https://github.com/hltcoe/HC4#postprocessing-of-the-downloaded-documents).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-fa \
  --corpus-path collections/neuclir22-fa
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection NeuClirCollection \
  -input /path/to/neuclir22-fa \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.neuclir22-fa \
  -storePositions -storeDocvectors -storeRaw -language fa \
  >& logs/log.neuclir22-fa &
```

See [this page](https://github.com/hltcoe/HC4) for more details about the HC4 corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-fa.test.title.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.title.txt \
  -bm25 -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-fa.test.desc.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.txt \
  -bm25 -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-fa.test.desc.title.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.title.txt \
  -bm25 -language fa &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-fa.test.title.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.title.txt \
  -bm25 -rm3 -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-fa.test.desc.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.txt \
  -bm25 -rm3 -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-fa.test.desc.title.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.title.txt \
  -bm25 -rm3 -language fa &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-fa.test.title.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.title.txt \
  -bm25 -rocchio -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-fa.test.desc.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.txt \
  -bm25 -rocchio -language fa &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-fa.test.desc.title.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.title.txt \
  -bm25 -rocchio -language fa &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-------------------|-----------|-------------|
| [HC4 (Persian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.1331            | 0.1045    | 0.1318      |
| [HC4 (Persian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.1420            | 0.0998    | 0.1250      |
| [HC4 (Persian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.1536            | 0.1255    | 0.1382      |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Persian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.1999            | 0.1532    | 0.1847      |
| [HC4 (Persian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.2197            | 0.1526    | 0.1926      |
| [HC4 (Persian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.2371            | 0.1925    | 0.2057      |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Persian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.1510            | 0.1270    | 0.1650      |
| [HC4 (Persian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.1490            | 0.1120    | 0.1580      |
| [HC4 (Persian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.1670            | 0.1270    | 0.1690      |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Persian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.7483            | 0.6933    | 0.7991      |
| [HC4 (Persian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.7475            | 0.6365    | 0.8233      |
| [HC4 (Persian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.7964            | 0.6835    | 0.8532      |

The above results reproduce the BM25 title queries run in Table 2 of [this paper](https://arxiv.org/pdf/2201.08471.pdf).

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/hc4-neuclir22-fa.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-07-13 (commit [`500e87`](https://github.com/castorini/anserini/commit/500e872d594a86cbf01adae644479f74a4b4af2d))
