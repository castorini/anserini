# Anserini Regressions: BRIGHT &mdash; LeetCode

This page documents BM25 regression experiments for [BRIGHT &mdash; LeetCode](https://brightbenchmark.github.io/) with query-side BM25, as described in the following paper:

> Yijun Ge, Sahel Sharifymoghaddam, and Jimmy Lin. [Lighting the Way for BRIGHT: Reproducible Baselines with Anserini, Pyserini, and RankLLM.](https://arxiv.org/abs/2509.02558) _arXiv:2509.02558_, 2025.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/bright-leetcode.bm25qs.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/bright-leetcode.bm25qs.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and build Anserini to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression bright-leetcode.bm25qs
```

All the BRIGHT corpora are available for download:

```bash
wget https://huggingface.co/datasets/castorini/collections-bright/resolve/main/bright-corpus.tar -P collections/
tar xvf collections/bright-corpus.tar -C collections/
```

The tarball is 284 MB and has MD5 checksum `568b594709a9977369033117bfb6889c`.
After download and unpacking the corpora, the `run_regression.py` command above should work without any issue.

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection JsonCollection \
  -input /path/to/bright-leetcode \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.bright-leetcode/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.bright-leetcode &
```

The path `/path/to/bright-leetcode/` should point to the corpus downloaded above.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.bright-leetcode/ \
  -topics tools/topics-and-qrels/topics.bright-leetcode.tsv.gz \
  -topicReader TsvString \
  -output runs/run.bright-leetcode.bm25qs.topics.bright-leetcode.txt \
  -bm25.querySide -removeQuery -hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.bright-leetcode.txt runs/run.bright-leetcode.bm25qs.topics.bright-leetcode.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.bright-leetcode.txt runs/run.bright-leetcode.bm25qs.topics.bright-leetcode.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.bright-leetcode.txt runs/run.bright-leetcode.bm25qs.topics.bright-leetcode.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                                                                                                  | **query-side-BM25**|
|:-------------------------------------------------------------------------------------------------------------|--------------------|
| BRIGHT: LeetCode                                                                                             | 0.2497             |
| **R@100**                                                                                                    | **query-side-BM25**|
| BRIGHT: LeetCode                                                                                             | 0.5376             |
| **R@1000**                                                                                                   | **query-side-BM25**|
| BRIGHT: LeetCode                                                                                             | 0.8673             |
