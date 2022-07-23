# Anserini Regressions: HC4 (v1.0) on NeuCLIR22 &mdash; Chinese

This page documents BM25 regression experiments for [HC4 (v1.0) Chinese topics](https://github.com/hltcoe/HC4) on the [NeuCLIR22 Chinese corpus](https://neuclir.github.io/).
The HC4 qrels have been filtered down to include only those in the intersection of the HC4 and NeuCLIR22 corpora.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/hc4-neuclir22-zh.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/hc4-neuclir22-zh.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-zh
```

## Corpus Download

The HC4 corpus can be downloaded following the instructions [here](https://github.com/hltcoe/HC4).

After download, verify that all and only specified documents have been downloaded by running the code [provided here](https://github.com/hltcoe/HC4#postprocessing-of-the-downloaded-documents).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-zh \
  --corpus-path collections/hc4-filtered-zh
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection NeuClirCollection \
  -input /path/to/hc4-filtered-zh \
  -index indexes/lucene-index.neuclir22-zh \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -storePositions -storeDocvectors -storeRaw -language zh \
  >& logs/log.hc4-filtered-zh &
```

See [this page](https://github.com/hltcoe/HC4) for more details about the HC4 corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-zh.bm25-default.topics.hc4-v1.0-zh.test.title.txt \
  -bm25 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.txt \
  -bm25 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.title.txt \
  -bm25 -language zh &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.title.txt \
  -bm25 -rm3 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.txt \
  -bm25 -rm3 -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.title.txt \
  -bm25 -rm3 -language zh &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.title.txt \
  -bm25 -rocchio -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.txt \
  -bm25 -rocchio -language zh &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-zh \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.title.txt \
  -bm25 -rocchio -language zh &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default.topics.hc4-v1.0-zh.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default.topics.hc4-v1.0-zh.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rm3.topics.hc4-v1.0-zh.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-zh.test.txt runs/run.hc4-filtered-zh.bm25-default+rocchio.topics.hc4-v1.0-zh.test.desc.title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [HC4 (Chinese): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.0561    | 0.0449    | 0.0488    |
| [HC4 (Chinese): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.0428    | 0.0262    | 0.0277    |
| [HC4 (Chinese): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.0597    | 0.0435    | 0.0462    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Chinese): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.0737    | 0.0550    | 0.0626    |
| [HC4 (Chinese): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.0612    | 0.0327    | 0.0397    |
| [HC4 (Chinese): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.0727    | 0.0556    | 0.0634    |
| **J@10**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Chinese): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.0820    | 0.0660    | 0.0820    |
| [HC4 (Chinese): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.0740    | 0.0480    | 0.0720    |
| [HC4 (Chinese): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.0720    | 0.0600    | 0.0800    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/hc4-neuclir22-zh.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-07-13 (commit [`500e87`](https://github.com/castorini/anserini/commit/500e872d594a86cbf01adae644479f74a4b4af2d))
