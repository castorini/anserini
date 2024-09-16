# Anserini Regressions: HC4 (v1.0) on NeuCLIR22 &mdash; Russian

This page documents BM25 regression experiments for [HC4 (v1.0) Russian topics](https://github.com/hltcoe/HC4) on the [NeuCLIR22 Russian corpus](https://neuclir.github.io/).
The HC4 qrels have been filtered down to include only those in the intersection of the HC4 and NeuCLIR22 corpora.
To be clear, the queries are in Russian (human translations) and the corpus is in Russian.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/hc4-neuclir22-ru.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/hc4-neuclir22-ru.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-ru
```

## Corpus Download

The HC4 corpus can be downloaded following the instructions [here](https://github.com/hltcoe/HC4).

After download, verify that all and only specified documents have been downloaded by running the code [provided here](https://github.com/hltcoe/HC4#postprocessing-of-the-downloaded-documents).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-ru \
  --corpus-path collections/neuclir22-ru
```


## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection NeuClirCollection \
  -input /path/to/neuclir22-ru \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.neuclir22-ru \
  -storePositions -storeDocvectors -storeRaw -language ru \
  >& logs/log.neuclir22-ru &
```

See [this page](https://github.com/hltcoe/HC4) for more details about the HC4 corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-ru.test.title.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt \
  -bm25 -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt \
  -bm25 -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.title.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt \
  -bm25 -language ru &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-ru.test.title.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt \
  -bm25 -rm3 -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt \
  -bm25 -rm3 -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.title.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt \
  -bm25 -rm3 -language ru &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-ru.test.title.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt \
  -bm25 -rocchio -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt \
  -bm25 -rocchio -language ru &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics tools/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.title.tsv \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt \
  -bm25 -rocchio -language ru &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [HC4 (Russian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.1040    | 0.0854    | 0.1231    |
| [HC4 (Russian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.0963    | 0.0654    | 0.0964    |
| [HC4 (Russian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.1264    | 0.0868    | 0.1314    |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Russian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.1445    | 0.1335    | 0.1655    |
| [HC4 (Russian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.1495    | 0.1061    | 0.1569    |
| [HC4 (Russian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.1762    | 0.1425    | 0.1875    |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Russian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.0860    | 0.0760    | 0.0930    |
| [HC4 (Russian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.0790    | 0.0620    | 0.0890    |
| [HC4 (Russian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.0900    | 0.0750    | 0.0980    |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Russian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.6319    | 0.6125    | 0.6982    |
| [HC4 (Russian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.6640    | 0.5408    | 0.6407    |
| [HC4 (Russian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.6667    | 0.6254    | 0.6810    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/hc4-neuclir22-ru.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-07-13 (commit [`500e87`](https://github.com/castorini/anserini/commit/500e872d594a86cbf01adae644479f74a4b4af2d))
