# Anserini Regressions: NeuCLIR22 &mdash; Russian (Query Translation)

This page documents BM25 regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Russian, using query translation (i.e., human translations provided by the organizers).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/neuclir22-ru-qt.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/neuclir22-ru-qt.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-qt
```

## Corpus Download

The NeuCLIR 2022 corpus can be downloaded following the instructions [here](https://neuclir.github.io/).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-qt \
  --corpus-path collections/neuclir22-ru
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection NeuClirCollection \
  -input /path/to/neuclir22-ru \
  -index indexes/lucene-index.neuclir22-ru \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -storePositions -storeDocvectors -storeRaw -language ru \
  >& logs/log.neuclir22-ru &
```

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.orig-title.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-title.txt \
  -bm25 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.orig-desc.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-desc.txt \
  -bm25 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.orig-desc_title.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-desc_title.txt \
  -bm25 -language ru &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.orig-title.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-title.txt \
  -bm25 -rm3 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.orig-desc.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-desc.txt \
  -bm25 -rm3 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.orig-desc_title.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-desc_title.txt \
  -bm25 -rm3 -language ru &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.orig-title.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-title.txt \
  -bm25 -rocchio -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.orig-desc.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-desc.txt \
  -bm25 -rocchio -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-ru.orig-desc_title.txt \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-desc_title.txt \
  -bm25 -rocchio -language ru &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default.topics.neuclir22-ru.orig-desc_title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.neuclir22-ru.orig-desc_title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.neuclir22-ru.orig-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Russian): title](https://neuclir.github.io/)                                                  | 0.2821    | 0.2049    | 0.2649    |
| [NeuCLIR 2022 (Russian): desc](https://neuclir.github.io/)                                                   | 0.2107    | 0.1287    | 0.2458    |
| [NeuCLIR 2022 (Russian): desc+title](https://neuclir.github.io/)                                             | 0.2662    | 0.1653    | 0.2734    |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title](https://neuclir.github.io/)                                                  | 0.3668    | 0.2984    | 0.3572    |
| [NeuCLIR 2022 (Russian): desc](https://neuclir.github.io/)                                                   | 0.3138    | 0.2155    | 0.3366    |
| [NeuCLIR 2022 (Russian): desc+title](https://neuclir.github.io/)                                             | 0.3665    | 0.2638    | 0.3630    |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title](https://neuclir.github.io/)                                                  | 0.3732    | 0.3246    | 0.3873    |
| [NeuCLIR 2022 (Russian): desc](https://neuclir.github.io/)                                                   | 0.3522    | 0.2592    | 0.3803    |
| [NeuCLIR 2022 (Russian): desc+title](https://neuclir.github.io/)                                             | 0.3943    | 0.2917    | 0.3947    |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title](https://neuclir.github.io/)                                                  | 0.7125    | 0.6471    | 0.7381    |
| [NeuCLIR 2022 (Russian): desc](https://neuclir.github.io/)                                                   | 0.6655    | 0.5406    | 0.7276    |
| [NeuCLIR 2022 (Russian): desc+title](https://neuclir.github.io/)                                             | 0.7421    | 0.6320    | 0.7770    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/neuclir22-ru-qt.template) and run `bin/build.sh` to rebuild the documentation.

