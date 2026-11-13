# Anserini Regressions: BEIR (v1.0.0) &mdash; CQADupStack-android

**Model**: [SlideOn](https://huggingface.co/lightonai/SlideOn) (using ONNX for on-the-fly query encoding)

This page describes regression experiments, integrated into Anserini's regression testing framework, using [SlideOn](https://huggingface.co/lightonai/SlideOn) on [BEIR (v1.0.0) &mdash; CQADupStack-android](http://beir.ai/).
The model itself can be downloaded [here](https://huggingface.co/lightonai/SlideOn).

In these experiments, we are using ONNX to perform query encoding on the fly.
See [SlideOn ONNX inference](../../onnx-slideon.md) for model requirements and query settings.

The exact configurations for these regressions are stored in [this YAML file](../../../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.slideon.onnx.yaml).
Note that this page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/beir-v1.0.0-cqadupstack-android.slideon.onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

First convert the pre-encoded Parquet vectors to JSONL as described below.
From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config beir-v1.0.0-cqadupstack-android.slideon.onnx
```

## Preparing the Collection

On our Waterloo servers, the pre-encoded Parquet files are located at `/store/collections/beir-v1.0.0/slideon/cqadupstack-android`.
These files contain `id`, `indices`, and FP16 `weights`, rather than the JSONL records required by `JsonVectorCollection`.
Convert them using [to_anserini.py](../../../src/main/python/slideon/to_anserini.py), copied from the [upstream script](https://huggingface.co/datasets/lightonai/SlideOn-sparse-vectors/blob/main/to_anserini.py):

```bash
python -m pip install 'pyarrow>=21' numpy
python src/main/python/slideon/to_anserini.py \
  /store/collections/beir-v1.0.0/slideon/cqadupstack-android \
  collections/beir-v1.0.0/slideon-jsonl/cqadupstack-android/corpus.jsonl.gz \
  --k 512 --scale 100
```

## Indexing

Sample indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection JsonVectorCollection \
  -input /path/to/beir-v1.0.0-cqadupstack-android.slideon \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.beir-v1.0.0-cqadupstack-android.slideon/ \
  -impact -pretokenized \
  >& logs/log.beir-v1.0.0-cqadupstack-android.slideon &
```

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the pre-encoded tokens.
For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored in a [centralized repo containing evaluation data](https://github.com/castorini/eval).

Use the default SlideOn query settings (q64, scale 100); overriding these settings changes the experiment.
The ONNX model is downloaded automatically on first use.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.beir-v1.0.0-cqadupstack-android.slideon/ \
  -topics beir-cqadupstack-android \
  -topicReader TsvString \
  -output runs/run.lucene-inverted.beir-v1.0.0-cqadupstack-android.slideon.model-slideon-onnx.topics-beir-cqadupstack-android.txt \
  -impact -pretokenized -hits 1000 -encoder SlideOn -removeQuery &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m ndcg_cut.10 beir-v1.0.0-cqadupstack-android.test runs/run.lucene-inverted.beir-v1.0.0-cqadupstack-android.slideon.model-slideon-onnx.topics-beir-cqadupstack-android.txt
bin/trec_eval -c -m recall.100 beir-v1.0.0-cqadupstack-android.test runs/run.lucene-inverted.beir-v1.0.0-cqadupstack-android.slideon.model-slideon-onnx.topics-beir-cqadupstack-android.txt
bin/trec_eval -c -m recall.1000 beir-v1.0.0-cqadupstack-android.test runs/run.lucene-inverted.beir-v1.0.0-cqadupstack-android.slideon.model-slideon-onnx.topics-beir-cqadupstack-android.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **nDCG@10**                        | **SlideOn (ONNX), d512/q64, scale 100** |
|:-----------------------------------|:---------------------------------------:|
| BEIR (v1.0.0): CQADupStack-android | 0.4772                                  |
| **R@100**                          | **SlideOn (ONNX), d512/q64, scale 100** |
| BEIR (v1.0.0): CQADupStack-android | 0.8299                                  |
| **R@1000**                         | **SlideOn (ONNX), d512/q64, scale 100** |
| BEIR (v1.0.0): CQADupStack-android | 0.9552                                  |
