# Anserini Regressions: NeuCLIR22 &mdash; Chinese (Document Translation)

This page documents BM25 regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Chinese, using document translation (i.e., corpus translation provided by the organizers).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/neuclir22-zh-dt.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/neuclir22-zh-dt.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-dt
```

## Corpus Download

The NeuCLIR 2022 corpus can be downloaded following the instructions [here](https://neuclir.github.io/).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-dt \
  --corpus-path collections/neuclir22-zh-en
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection NeuClirCollection \
  -input /path/to/neuclir22-zh-en \
  -index indexes/lucene-index.neuclir22-zh-en \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.neuclir22-zh-en &
```

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.en.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.title.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.en.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.desc.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.en.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.desc.title.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.en.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.title.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.en.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.desc.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.en.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.desc.title.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.en.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.title.txt \
  -bm25 -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.en.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.desc.txt \
  -bm25 -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22.en.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.desc.title.txt \
  -bm25 -rocchio &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22.en.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22.en.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22.en.desc.title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Chinese): title](https://neuclir.github.io/)                                                  | 0.3472    | 0.3801    | 0.3818    |
| [NeuCLIR 2022 (Chinese): desc](https://neuclir.github.io/)                                                   | 0.2499    | 0.3052    | 0.3021    |
| [NeuCLIR 2022 (Chinese): desc+title](https://neuclir.github.io/)                                             | 0.3480    | 0.3610    | 0.3702    |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title](https://neuclir.github.io/)                                                  | 0.4774    | 0.4828    | 0.4879    |
| [NeuCLIR 2022 (Chinese): desc](https://neuclir.github.io/)                                                   | 0.3665    | 0.3974    | 0.3997    |
| [NeuCLIR 2022 (Chinese): desc+title](https://neuclir.github.io/)                                             | 0.4725    | 0.4588    | 0.4743    |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title](https://neuclir.github.io/)                                                  | 0.3908    | 0.4211    | 0.4197    |
| [NeuCLIR 2022 (Chinese): desc](https://neuclir.github.io/)                                                   | 0.3412    | 0.3978    | 0.3974    |
| [NeuCLIR 2022 (Chinese): desc+title](https://neuclir.github.io/)                                             | 0.3899    | 0.4154    | 0.4184    |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title](https://neuclir.github.io/)                                                  | 0.7423    | 0.8143    | 0.8158    |
| [NeuCLIR 2022 (Chinese): desc](https://neuclir.github.io/)                                                   | 0.6509    | 0.7556    | 0.7477    |
| [NeuCLIR 2022 (Chinese): desc+title](https://neuclir.github.io/)                                             | 0.7607    | 0.8113    | 0.8149    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/neuclir22-zh-dt.template) and run `bin/build.sh` to rebuild the documentation.

