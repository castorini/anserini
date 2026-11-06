# Anserini Regressions: RETECO v1.1 Track 1a &mdash; IOTA

This page documents BM25 regression experiments for [RETECO v1.1 Track 1a &mdash; IOTA](https://datascienceuibk.github.io/RETECO/).
One full domain index is shared by train and dev.

The exact configurations for these regressions are stored in [this YAML file](../../../src/main/resources/reproduce/from-document-collection/configs/reteco-v1.1-track1-iota.yaml).
Note that this page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/reteco-v1.1-track1-iota.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and build Anserini to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), after [preparing the local corpus](../from-document-collection-summaries/reteco.md#available-corpora-for-download), the following command will perform the complete regression, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection \
  --index --verify --search --config reteco-v1.1-track1-iota \
  --corpus-path collections/reteco/track1_tempo/iota --search-pool 1
```

All the RETECO Track 1 corpora are available from the [official dataset](https://huggingface.co/datasets/DataScience-UIBK/RETECO-SemEval2027/tree/c21e10b), pinned to the deduplicated v1.1 revision `c21e10b`.
Outside Waterloo, download and convert this domain as follows:

```bash
mkdir -p collections/reteco-v1.1-raw/track1_tempo/iota
wget -c https://huggingface.co/datasets/DataScience-UIBK/RETECO-SemEval2027/resolve/c21e10b/track1_tempo/iota/documents.jsonl \
  -O collections/reteco-v1.1-raw/track1_tempo/iota/documents.jsonl
python src/main/python/reteco/convert_collection.py \
  --input collections/reteco-v1.1-raw --output collections/reteco --domains iota
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 2 \
  -collection JsonCollection \
  -input /path/to/reteco-v1.1-track1-iota \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.reteco-v1.1-track1-iota/ \
  -storePositions -storeDocvectors -storeRaw -optimize \
  >& logs/log.reteco-v1.1-track1-iota &
wait
```

The path `/path/to/reteco-v1.1-track1-iota/` should point to the prepared domain directory containing only `docs.jsonl`.
For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored in a [centralized repo containing evaluation data](https://github.com/castorini/eval/tree/5a26fd8d1a9e8408ae9a2606e95852312f1e0062).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.reteco-v1.1-track1-iota/ \
  -topics reteco-v1.1-track1a-iota.train \
  -topicReader TsvString \
  -output runs/run.lucene-inverted.reteco-v1.1-track1-iota.model-bm25.topics-reteco-v1.1-track1a-iota.train.txt \
  -bm25 -bm25.k1 0.9 -bm25.b 0.4 -hits 1000 -threads 2 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.reteco-v1.1-track1-iota/ \
  -topics reteco-v1.1-track1a-iota.dev \
  -topicReader TsvString \
  -output runs/run.lucene-inverted.reteco-v1.1-track1-iota.model-bm25.topics-reteco-v1.1-track1a-iota.dev.txt \
  -bm25 -bm25.k1 0.9 -bm25.b 0.4 -hits 1000 -threads 2 &
wait
```

These commands use standard Anserini BM25 (`k1=0.9`, `b=0.4`, top 1000).

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-iota.train runs/run.lucene-inverted.reteco-v1.1-track1-iota.model-bm25.topics-reteco-v1.1-track1a-iota.train.txt
bin/trec_eval -c -m recall.100 reteco-v1.1-track1a-iota.train runs/run.lucene-inverted.reteco-v1.1-track1-iota.model-bm25.topics-reteco-v1.1-track1a-iota.train.txt
bin/trec_eval -c -m recall.1000 reteco-v1.1-track1a-iota.train runs/run.lucene-inverted.reteco-v1.1-track1-iota.model-bm25.topics-reteco-v1.1-track1a-iota.train.txt
bin/trec_eval -c -m ndcg_cut.10 reteco-v1.1-track1a-iota.dev runs/run.lucene-inverted.reteco-v1.1-track1-iota.model-bm25.topics-reteco-v1.1-track1a-iota.dev.txt
bin/trec_eval -c -m recall.100 reteco-v1.1-track1a-iota.dev runs/run.lucene-inverted.reteco-v1.1-track1-iota.model-bm25.topics-reteco-v1.1-track1a-iota.dev.txt
bin/trec_eval -c -m recall.1000 reteco-v1.1-track1a-iota.dev runs/run.lucene-inverted.reteco-v1.1-track1-iota.model-bm25.topics-reteco-v1.1-track1a-iota.dev.txt
```

nDCG@10 is the primary benchmark metric; R@100 and R@1000 are additional diagnostics. `-c` includes all qrels queries.
The full domain corpus is searched for every split, with unmodified retrieval scores and version-matched qrels.

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                   | **BM25**   |
|:------------------------------|:----------:|
| RETECO: IOTA, Track 1a, train | 0.0352     |
| RETECO: IOTA, Track 1a, dev   | 0.3852     |
| **R@100**                     | **BM25**   |
| RETECO: IOTA, Track 1a, train | 0.2714     |
| RETECO: IOTA, Track 1a, dev   | 0.5000     |
| **R@1000**                    | **BM25**   |
| RETECO: IOTA, Track 1a, train | 0.7000     |
| RETECO: IOTA, Track 1a, dev   | 1.0000     |
