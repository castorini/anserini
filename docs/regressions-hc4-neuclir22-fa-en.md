# Anserini Regressions: HC4 (v1.0) on translated NeuCLIR22 &mdash; Persian

This page documents BM25 regression experiments for [HC4 (v1.0) Persian topics](https://github.com/hltcoe/HC4) on the [NeuCLIR22 translated Persian corpus](https://neuclir.github.io/).
The HC4 qrels have been filtered down to include only those in the intersection of the HC4 and NeuCLIR22 corpora.
To be clear, the queries are in English and the corpus is in English (automatically translated by the organizers using Sockeye).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/hc4-neuclir22-fa-en.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/hc4-neuclir22-fa-en.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-fa-en
```

## Corpus Download

The HC4 corpus can be downloaded following the instructions [here](https://github.com/hltcoe/HC4).

After download, verify that all and only specified documents have been downloaded by running the code [provided here](https://github.com/hltcoe/HC4#postprocessing-of-the-downloaded-documents).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-fa-en \
  --corpus-path collections/neuclir22-fa-en
```

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection NeuClirCollection \
  -input /path/to/neuclir22-fa-en \
  -index indexes/lucene-index.neuclir22-fa-en \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.neuclir22-fa-en &
```

See [this page](https://github.com/hltcoe/HC4) for more details about the HC4 corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.en.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default.topics.hc4-v1.0-fa.en.test.title.txt \
  -bm25 -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.en.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default.topics.hc4-v1.0-fa.en.test.desc.txt \
  -bm25 -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.en.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default.topics.hc4-v1.0-fa.en.test.desc.title.txt \
  -bm25 -language fa &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.en.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default+rm3.topics.hc4-v1.0-fa.en.test.title.txt \
  -bm25 -rm3 -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.en.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default+rm3.topics.hc4-v1.0-fa.en.test.desc.txt \
  -bm25 -rm3 -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.en.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default+rm3.topics.hc4-v1.0-fa.en.test.desc.title.txt \
  -bm25 -rm3 -language fa &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.en.test.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.hc4-v1.0-fa.en.test.title.txt \
  -bm25 -rocchio -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.en.test.desc.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.hc4-v1.0-fa.en.test.desc.txt \
  -bm25 -rocchio -language fa &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.en.test.desc.title.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.hc4-v1.0-fa.en.test.desc.title.txt \
  -bm25 -rocchio -language fa &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default.topics.hc4-v1.0-fa.en.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default.topics.hc4-v1.0-fa.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default.topics.hc4-v1.0-fa.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default.topics.hc4-v1.0-fa.en.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default.topics.hc4-v1.0-fa.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default.topics.hc4-v1.0-fa.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default.topics.hc4-v1.0-fa.en.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default.topics.hc4-v1.0-fa.en.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default.topics.hc4-v1.0-fa.en.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.hc4-v1.0-fa.en.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.hc4-v1.0-fa.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.hc4-v1.0-fa.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.hc4-v1.0-fa.en.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.hc4-v1.0-fa.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.hc4-v1.0-fa.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.hc4-v1.0-fa.en.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.hc4-v1.0-fa.en.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.hc4-v1.0-fa.en.test.desc.title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.hc4-v1.0-fa.en.test.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.hc4-v1.0-fa.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.hc4-v1.0-fa.en.test.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.hc4-v1.0-fa.en.test.desc.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.hc4-v1.0-fa.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.hc4-v1.0-fa.en.test.desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.hc4-v1.0-fa.en.test.desc.title.txt
python -m pyserini.eval.trec_eval -c -m judged.10 src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.hc4-v1.0-fa.en.test.desc.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.hc4-neuclir22-fa.test.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.hc4-v1.0-fa.en.test.desc.title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [HC4 (Persian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.0519    | 0.0489    | 0.0503    |
| [HC4 (Persian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.0526    | 0.0485    | 0.0562    |
| [HC4 (Persian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.0571    | 0.0668    | 0.0661    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Persian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.0569    | 0.0348    | 0.0423    |
| [HC4 (Persian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.0529    | 0.0442    | 0.0522    |
| [HC4 (Persian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.0643    | 0.0689    | 0.0681    |
| **J@10**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [HC4 (Persian): test-topic title](https://github.com/hltcoe/HC4)                                             | 0.0796    | 0.0680    | 0.0720    |
| [HC4 (Persian): test-topic description](https://github.com/hltcoe/HC4)                                       | 0.0800    | 0.0700    | 0.0700    |
| [HC4 (Persian): test-topic description+title](https://github.com/hltcoe/HC4)                                 | 0.0920    | 0.0880    | 0.0880    |

The above results reproduce the BM25 title queries run in Table 2 of [this paper](https://arxiv.org/pdf/2201.08471.pdf).

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/hc4-neuclir22-fa-en.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-07-13 (commit [`500e87`](https://github.com/castorini/anserini/commit/500e872d594a86cbf01adae644479f74a4b4af2d))
