# Anserini Regressions: HC4 (v1.0) on NeuCLIR22 &mdash; Persian

This page documents BM25 regression experiments for [HC4 (v1.0) Persian topics](https://github.com/hltcoe/HC4) on the [NeuCLIR22 Persian corpus](https://neuclir.github.io/).
The HC4 qrels have been filtered down to include only those in the intersection of the HC4 and NeuCLIR22 corpora.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/hc4-neuclir22-fa.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/hc4-neuclir22-fa.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

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
  --corpus-path collections/hc4-filtered-fa
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection NeuClirCollection \
  -input /path/to/hc4-filtered-fa \
  -index indexes/lucene-index.neuclir22-fa \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -storePositions -storeDocvectors -storeRaw -language fa \
  >& logs/log.hc4-filtered-fa &
```

See [this page](https://github.com/hltcoe/HC4) for more details about the HC4 corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-fa.bm25-default.topics.hc4-v1.0-fa.test.title.txt \
  -bm25 -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.txt \
  -bm25 -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.title.txt \
  -bm25 -language fa &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.title.txt \
  -bm25 -rm3 -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.txt \
  -bm25 -rm3 -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.title.txt \
  -bm25 -rm3 -language fa &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.title.txt \
  -bm25 -rocchio -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.txt \
  -bm25 -rocchio -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.hc4-filtered-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.title.txt \
  -bm25 -rocchio -language fa &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default.topics.hc4-v1.0-fa.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default.topics.hc4-v1.0-fa.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default.topics.hc4-v1.0-fa.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default.topics.hc4-v1.0-fa.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rm3.topics.hc4-v1.0-fa.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.hc4-filtered-fa.bm25-default+rocchio.topics.hc4-v1.0-fa.test.desc.title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [HC4 (Persian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.1198    | 0.1050    | 0.1221    |
| [HC4 (Persian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.1435    | 0.0845    | 0.1254    |
| [HC4 (Persian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.1438    | 0.1079    | 0.1351    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Persian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.1514    | 0.1286    | 0.1404    |
| [HC4 (Persian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.1971    | 0.1053    | 0.1703    |
| [HC4 (Persian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.1913    | 0.1491    | 0.1683    |
| **J@10**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Persian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.1760    | 0.1260    | 0.1620    |
| [HC4 (Persian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.1720    | 0.1260    | 0.1800    |
| [HC4 (Persian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.1800    | 0.1460    | 0.1660    |

The above results reproduce the BM25 title queries run in Table 2 of [this paper](https://arxiv.org/pdf/2201.08471.pdf).

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/hc4-neuclir22-fa.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-07-13 (commit [`500e87`](https://github.com/castorini/anserini/commit/500e872d594a86cbf01adae644479f74a4b4af2d))
