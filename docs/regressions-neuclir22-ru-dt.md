# Anserini Regressions: NeuCLIR22 &mdash; Russian (Document Translation)

This page documents BM25 regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Russian, using document translation (i.e., corpus translation provided by the organizers).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/neuclir22-ru-dt.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/neuclir22-ru-dt.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-dt
```

## Corpus Download

The NeuCLIR 2022 corpus can be downloaded following the instructions [here](https://neuclir.github.io/).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-dt \
  --corpus-path collections/neuclir22-ru-en
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection NeuClirCollection \
  -input /path/to/neuclir22-ru-en \
  -index indexes/lucene-index.neuclir22-ru-en \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.neuclir22-ru-en &
```

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.orig-title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-title.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.orig-desc.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-desc.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.orig-desc_title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-desc_title.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.orig-title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-title.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.orig-desc.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-desc.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.orig-desc_title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-desc_title.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.orig-title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-title.txt \
  -bm25 -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.orig-desc.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-desc.txt \
  -bm25 -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.orig-desc_title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-desc_title.txt \
  -bm25 -rocchio &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default.topics.neuclir22-en.orig-desc_title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rm3.topics.neuclir22-en.orig-desc_title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en.bm25-default+rocchio.topics.neuclir22-en.orig-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Russian): title](https://neuclir.github.io/)                                                  | 0.2685    | 0.2500    | 0.2567    |
| [NeuCLIR 2022 (Russian): desc](https://neuclir.github.io/)                                                   | 0.1190    | 0.1769    | 0.1731    |
| [NeuCLIR 2022 (Russian): desc+title](https://neuclir.github.io/)                                             | 0.2066    | 0.2220    | 0.2253    |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title](https://neuclir.github.io/)                                                  | 0.3693    | 0.3545    | 0.3589    |
| [NeuCLIR 2022 (Russian): desc](https://neuclir.github.io/)                                                   | 0.2060    | 0.2539    | 0.2627    |
| [NeuCLIR 2022 (Russian): desc+title](https://neuclir.github.io/)                                             | 0.3080    | 0.3024    | 0.3188    |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title](https://neuclir.github.io/)                                                  | 0.3702    | 0.3882    | 0.3868    |
| [NeuCLIR 2022 (Russian): desc](https://neuclir.github.io/)                                                   | 0.2772    | 0.3417    | 0.3382    |
| [NeuCLIR 2022 (Russian): desc+title](https://neuclir.github.io/)                                             | 0.3618    | 0.3737    | 0.3811    |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title](https://neuclir.github.io/)                                                  | 0.7409    | 0.7808    | 0.7908    |
| [NeuCLIR 2022 (Russian): desc](https://neuclir.github.io/)                                                   | 0.5780    | 0.6772    | 0.6780    |
| [NeuCLIR 2022 (Russian): desc+title](https://neuclir.github.io/)                                             | 0.7255    | 0.7658    | 0.7798    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/neuclir22-ru-dt.template) and run `bin/build.sh` to rebuild the documentation.

