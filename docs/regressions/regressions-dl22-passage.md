# Anserini Regressions: TREC 2022 Deep Learning Track (Passage)

**Models**: various bag-of-words approaches on original passages

This page describes baseline experiments, integrated into Anserini's regression testing framework, on the [TREC 2022 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2022.html) using the MS MARCO V2 passage corpus.
For additional instructions on working with the MS MARCO V2 passage corpus, refer to [this page](../../docs/experiments-msmarco-v2.md).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl22-passage.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl22-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression dl22-passage
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection MsMarcoV2PassageCollection \
  -input /path/to/msmarco-v2-passage \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2-passage/ \
  -storeRaw \
  >& logs/log.msmarco-v2-passage &
```

The value of `-input` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](../../docs/experiments-msmarco-v2.md) for additional details.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 76 topics for which NIST has provided judgments as part of the [TREC 2022 Deep Learning Track](https://trec.nist.gov/data/deep2022.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage/ \
  -topics tools/topics-and-qrels/topics.dl22.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default.topics.dl22.txt \
  -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage/ \
  -topics tools/topics-and-qrels/topics.dl22.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl22.txt \
  -bm25 -rm3 -collection MsMarcoV2PassageCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage/ \
  -topics tools/topics-and-qrels/topics.dl22.txt \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.dl22.txt \
  -bm25 -rocchio -collection MsMarcoV2PassageCollection &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default.topics.dl22.txt
bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default.topics.dl22.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default.topics.dl22.txt
bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default.topics.dl22.txt
bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default.topics.dl22.txt

bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl22.txt
bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl22.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl22.txt
bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl22.txt
bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default+rm3.topics.dl22.txt

bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.dl22.txt
bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.dl22.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.dl22.txt
bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.dl22.txt
bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl22-passage.txt runs/run.msmarco-v2-passage.bm25-default+rocchio.topics.dl22.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL22 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.0325    | 0.0310    | 0.0340    |
| **MRR@100**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL22 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.3256    | 0.2564    | 0.2733    |
| **nDCG@10**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL22 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.2692    | 0.2686    | 0.2742    |
| **R@100**                                                                                                    | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL22 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.1382    | 0.1263    | 0.1395    |
| **R@1000**                                                                                                   | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [DL22 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.3321    | 0.3559    | 0.3639    |

The "BM25 (default)" condition corresponds to the `p_bm25` run submitted to the TREC 2022 Deep Learning Track as a "baseline".
As of [`91ec67`](https://github.com/castorini/anserini/commit/91ec6749bfef206e210bcc1df8cd4060e7d7aaff), this correspondence was _exact_.
That is, modulo the runtag and the number of hits, the output runfile should be identical.
This can be confirmed as follows:

```bash
# Trim out the runtag:
cut -d ' ' -f 1-5 runs/p_bm25 > runs/p_bm25.submitted.cut

# Trim out the runtag and retain only top 100 hits per query:
python tools/scripts/trim_run_to_top_k.pl --k 100 --input runs/run.msmarco-v2-passage.dl22.bm25-default --output runs/run.msmarco-v2-passage.dl22.bm25-default.hits100
cut -d ' ' -f 1-5 runs/run.msmarco-v2-passage.dl22.bm25-default.hits100 > runs/p_bm25.new.cut

# Verify the two runfiles are identical:
diff runs/p_bm25.submitted.cut runs/p_bm25.new.cut
```

The "BM25 + RM3" and "BM25 + Rocchio" conditions above correspond to the `p_bm25rm3` run and the `p_bm25rocchio` run submitted to the TREC 2022 Deep Learning Track as "baselines".
However, due to [`a60e84`](https://github.com/castorini/anserini/commit/a60e842e9b47eca0ad5266659081fe1180c96b7f), the results are slightly different (because the underlying implementation changed).
