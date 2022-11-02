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
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.zh.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.title.txt \
  -bm25 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.zh.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.desc.txt \
  -bm25 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.zh.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.desc.title.txt \
  -bm25 -language zh &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.zh.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.title.txt \
  -bm25 -rm3 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.zh.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.desc.txt \
  -bm25 -rm3 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.zh.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.desc.title.txt \
  -bm25 -rm3 -language zh &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.zh.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.title.txt \
  -bm25 -rocchio -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.zh.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.desc.txt \
  -bm25 -rocchio -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.zh.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.desc.title.txt \
  -bm25 -rocchio -language zh &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default.topics.neuclir22.zh.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rm3.topics.neuclir22.zh.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh.bm25-default+rocchio.topics.neuclir22.zh.desc.title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Chinese): title](https://neuclir.github.io/)                                                  | 0.1796    | 0.1758    | 0.2090    |
| [NeuCLIR 2022 (Chinese): desc](https://neuclir.github.io/)                                                   | 0.1515    | 0.1216    | 0.1763    |
| [NeuCLIR 2022 (Chinese): desc+title](https://neuclir.github.io/)                                             | 0.1952    | 0.1707    | 0.2311    |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title](https://neuclir.github.io/)                                                  | 0.3203    | 0.2718    | 0.3279    |
| [NeuCLIR 2022 (Chinese): desc](https://neuclir.github.io/)                                                   | 0.2803    | 0.2112    | 0.2769    |
| [NeuCLIR 2022 (Chinese): desc+title](https://neuclir.github.io/)                                             | 0.3343    | 0.2671    | 0.3501    |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title](https://neuclir.github.io/)                                                  | 0.3895    | 0.3132    | 0.4004    |
| [NeuCLIR 2022 (Chinese): desc](https://neuclir.github.io/)                                                   | 0.3588    | 0.2461    | 0.3689    |
| [NeuCLIR 2022 (Chinese): desc+title](https://neuclir.github.io/)                                             | 0.4298    | 0.2925    | 0.4298    |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title](https://neuclir.github.io/)                                                  | 0.4527    | 0.4334    | 0.5121    |
| [NeuCLIR 2022 (Chinese): desc](https://neuclir.github.io/)                                                   | 0.4377    | 0.3755    | 0.4989    |
| [NeuCLIR 2022 (Chinese): desc+title](https://neuclir.github.io/)                                             | 0.4743    | 0.4174    | 0.5201    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/neuclir22-zh-qt.template) and run `bin/build.sh` to rebuild the documentation.

