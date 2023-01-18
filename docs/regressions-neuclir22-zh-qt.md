# Anserini Regressions: NeuCLIR22 &mdash; Chinese (Query Translation)

This page documents BM25 regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Chinese, using query translation (i.e., human translations provided by the organizers).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/neuclir22-zh-qt.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/neuclir22-zh-qt.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-qt
```

## Corpus Download

The NeuCLIR 2022 corpus can be downloaded following the instructions [here](https://neuclir.github.io/).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-qt \
  --corpus-path collections/neuclir22-zh
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection NeuClirCollection \
  -input /path/to/neuclir22-zh \
  -index indexes/lucene-index.neuclir22-zh \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -storePositions -storeDocvectors -storeRaw -language zh \
  >& logs/log.neuclir22-zh &
```

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-zh.orig-title.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-title.txt \
  -bm25 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-zh.orig-desc.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-desc.txt \
  -bm25 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-zh.orig-desc_title.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-desc_title.txt \
  -bm25 -language zh &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-zh.orig-title.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-title.txt \
  -bm25 -rm3 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-zh.orig-desc.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-desc.txt \
  -bm25 -rm3 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-zh.orig-desc_title.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-desc_title.txt \
  -bm25 -rm3 -language zh &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-zh.orig-title.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-title.txt \
  -bm25 -rocchio -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-zh.orig-desc.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-desc.txt \
  -bm25 -rocchio -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-zh.orig-desc_title.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-desc_title.txt \
  -bm25 -rocchio -language zh &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22-zh.orig-desc_title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22-zh.orig-desc_title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22-zh.orig-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Chinese): title](https://neuclir.github.io/)                                                  | 0.1505    | 0.1501    | 0.1692    |
| [NeuCLIR 2022 (Chinese): desc](https://neuclir.github.io/)                                                   | 0.1306    | 0.0925    | 0.1251    |
| [NeuCLIR 2022 (Chinese): desc+title](https://neuclir.github.io/)                                             | 0.1734    | 0.1367    | 0.1888    |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title](https://neuclir.github.io/)                                                  | 0.2478    | 0.2127    | 0.2544    |
| [NeuCLIR 2022 (Chinese): desc](https://neuclir.github.io/)                                                   | 0.2068    | 0.1519    | 0.1985    |
| [NeuCLIR 2022 (Chinese): desc+title](https://neuclir.github.io/)                                             | 0.2572    | 0.1891    | 0.2734    |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title](https://neuclir.github.io/)                                                  | 0.3895    | 0.3132    | 0.4004    |
| [NeuCLIR 2022 (Chinese): desc](https://neuclir.github.io/)                                                   | 0.3588    | 0.2461    | 0.3689    |
| [NeuCLIR 2022 (Chinese): desc+title](https://neuclir.github.io/)                                             | 0.4298    | 0.2925    | 0.4298    |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title](https://neuclir.github.io/)                                                  | 0.4759    | 0.4651    | 0.5230    |
| [NeuCLIR 2022 (Chinese): desc](https://neuclir.github.io/)                                                   | 0.4577    | 0.3703    | 0.5113    |
| [NeuCLIR 2022 (Chinese): desc+title](https://neuclir.github.io/)                                             | 0.4940    | 0.4373    | 0.5327    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/neuclir22-zh-qt.template) and run `bin/build.sh` to rebuild the documentation.

