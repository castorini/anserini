# Anserini Regressions: HC4 (v1.0) on NeuCLIR22 &mdash; Russian

This page documents BM25 regression experiments for [HC4 (v1.0) Russian topics](https://github.com/hltcoe/HC4) on the [NeuCLIR22 Russian corpus](https://neuclir.github.io/).
The HC4 qrels have been filtered down to include only those in the intersection of the HC4 and NeuCLIR22 corpora.
To be clear, the queries are in Russian (human translations) and the corpus is in Russian.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/hc4-neuclir22-ru.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/hc4-neuclir22-ru.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

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
target/appassembler/bin/IndexCollection \
  -collection NeuClirCollection \
  -input /path/to/neuclir22-ru \
  -index indexes/lucene-index.neuclir22-ru \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -storePositions -storeDocvectors -storeRaw -language ru \
  >& logs/log.neuclir22-ru &
```

See [this page](https://github.com/hltcoe/HC4) for more details about the HC4 corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt \
  -bm25 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt \
  -bm25 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt \
  -bm25 -language ru &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt \
  -bm25 -rm3 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt \
  -bm25 -rm3 -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt \
  -bm25 -rm3 -language ru &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt \
  -bm25 -rocchio -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt \
  -bm25 -rocchio -language ru &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-ru \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt \
  -bm25 -rocchio -language ru &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default.topics.hc4-v1.0-ru.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rm3.topics.hc4-v1.0-ru.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-ru.test.txt runs/run.neuclir22-ru.bm25-default+rocchio.topics.hc4-v1.0-ru.test.desc.title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [HC4 (Russian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.0964    | 0.0811    | 0.1245    |
| [HC4 (Russian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.0926    | 0.0605    | 0.1064    |
| [HC4 (Russian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.1113    | 0.0771    | 0.1341    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Russian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.1225    | 0.1093    | 0.1455    |
| [HC4 (Russian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.1078    | 0.0758    | 0.1382    |
| [HC4 (Russian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.1423    | 0.1099    | 0.1682    |
| **J@10**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Russian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.1060    | 0.1000    | 0.1200    |
| [HC4 (Russian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.1000    | 0.0840    | 0.1120    |
| [HC4 (Russian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.1140    | 0.1040    | 0.1120    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/hc4-neuclir22-ru.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-07-13 (commit [`500e87`](https://github.com/castorini/anserini/commit/500e872d594a86cbf01adae644479f74a4b4af2d))
