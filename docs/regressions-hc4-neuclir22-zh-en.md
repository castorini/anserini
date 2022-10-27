# Anserini Regressions: HC4 (v1.0) on translated NeuCLIR22 &mdash; Chinese

This page documents BM25 regression experiments for [HC4 (v1.0) Chinese topics](https://github.com/hltcoe/HC4) on the [NeuCLIR22 translated Chinese corpus](https://neuclir.github.io/).
The HC4 qrels have been filtered down to include only those in the intersection of the HC4 and NeuCLIR22 corpora.
To be clear, the queries are in English and the corpus is in English (automatically translated by the organizers using Sockeye).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/hc4-neuclir22-zh-en.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/hc4-neuclir22-zh-en.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-zh-en
```

## Corpus Download

The HC4 corpus can be downloaded following the instructions [here](https://github.com/hltcoe/HC4).

After download, verify that all and only specified documents have been downloaded by running the code [provided here](https://github.com/hltcoe/HC4#postprocessing-of-the-downloaded-documents).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-zh-en \
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

See [this page](https://github.com/hltcoe/HC4) for more details about the HC4 corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.en.test.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.title.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.en.test.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.desc.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.en.test.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.desc.title.txt \
  -bm25 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.en.test.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.title.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.en.test.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.desc.txt \
  -bm25 -rm3 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.en.test.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.desc.title.txt \
  -bm25 -rm3 &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.en.test.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.title.txt \
  -bm25 -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.en.test.desc.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.desc.txt \
  -bm25 -rocchio &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.en.test.desc.title.tsv \
  -topicreader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.desc.title.txt \
  -bm25 -rocchio &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default.topics.hc4-v1.0-zh.en.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.hc4-v1.0-zh.en.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.hc4-v1.0-zh.en.test.desc.title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [HC4 (Chinese): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.0839    | 0.0730    | 0.0701    |
| [HC4 (Chinese): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.0773    | 0.0845    | 0.0842    |
| [HC4 (Chinese): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.0861    | 0.0822    | 0.0865    |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Chinese): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.1271    | 0.1142    | 0.1106    |
| [HC4 (Chinese): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.1217    | 0.1250    | 0.1238    |
| [HC4 (Chinese): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.1369    | 0.1292    | 0.1327    |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Chinese): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.0860    | 0.0810    | 0.0790    |
| [HC4 (Chinese): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.0800    | 0.0770    | 0.0820    |
| [HC4 (Chinese): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.0860    | 0.0940    | 0.0870    |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Chinese): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.5982    | 0.6357    | 0.6364    |
| [HC4 (Chinese): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.5573    | 0.6077    | 0.6142    |
| [HC4 (Chinese): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.6182    | 0.6482    | 0.6516    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/hc4-neuclir22-zh-en.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-07-13 (commit [`500e87`](https://github.com/castorini/anserini/commit/500e872d594a86cbf01adae644479f74a4b4af2d))
