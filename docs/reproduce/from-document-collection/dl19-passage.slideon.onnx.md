# Anserini Regressions: TREC 2019 Deep Learning Track (Passage)

**Model**: SlideOn (using ONNX for on-the-fly query encoding)

This page describes regression experiments, integrated into Anserini's regression testing framework, using the [SlideOn](https://huggingface.co/lightonai/SlideOn) model on the [TREC 2019 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2019.html).

In these experiments, we are using ONNX to perform query encoding on the fly.
See [SlideOn ONNX inference](../../onnx-slideon.md) for model requirements and query settings.

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).
For additional instructions on working with MS MARCO passage collection, refer to [this page](../../../docs/experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../../../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.slideon.onnx.yaml).
Note that this page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/dl19-passage.slideon.onnx.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

First convert the pre-encoded Parquet vectors to JSONL as described below.
From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config dl19-passage.slideon.onnx
```

## Preparing the Collection

On our Waterloo servers, the pre-encoded Parquet files are located at `/store/collections/msmarco/msmarco-passage-slideon`.
These files contain `id`, `indices`, and FP16 `weights`, rather than the JSONL records required by `JsonVectorCollection`.
Convert them using [to_anserini.py](../../../src/main/python/slideon/to_anserini.py), copied from the [upstream script](https://huggingface.co/datasets/lightonai/SlideOn-sparse-vectors/blob/main/to_anserini.py):

```bash
python -m pip install 'pyarrow>=21' numpy
python src/main/python/slideon/to_anserini.py \
  /store/collections/msmarco/msmarco-passage-slideon \
  collections/msmarco/msmarco-passage-slideon-jsonl/corpus.jsonl.gz \
  --k 512 --scale 100
```

## Indexing

Sample indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-v1-passage.slideon \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-passage.slideon/ \
  -impact -pretokenized \
  >& logs/log.msmarco-v1-passage.slideon &
```

The path `/path/to/msmarco-v1-passage.slideon/` should point to the corpus converted above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the pre-encoded tokens.
Upon completion, we should have an index with 8,841,823 documents.

For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored in a [centralized repo containing evaluation data](https://github.com/castorini/eval).
The regression experiments here evaluate on the 43 topics for which NIST has provided judgments as part of the TREC 2019 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2019.html).

This experiment uses the same index as [MS MARCO V1 Passage](msmarco-v1-passage.slideon.onnx.md).
If that index has already been built, use `--verify --search` without `--index`; there is no need to convert the collection or build the index again.
Use the default SlideOn query settings (q64, scale 100); overriding these settings changes the experiment.
The ONNX model is downloaded automatically on first use.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage.slideon/ \
  -topics dl19-passage \
  -topicReader TsvInt \
  -output runs/run.lucene-inverted.msmarco-v1-passage.slideon.model-slideon-onnx.topics-dl19-passage.txt \
  -impact -pretokenized -hits 1000 -encoder SlideOn &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -m map -c -l 2 dl19-passage runs/run.lucene-inverted.msmarco-v1-passage.slideon.model-slideon-onnx.topics-dl19-passage.txt
bin/trec_eval -c -m ndcg_cut.10 dl19-passage runs/run.lucene-inverted.msmarco-v1-passage.slideon.model-slideon-onnx.topics-dl19-passage.txt
bin/trec_eval -c -l 2 -m recall.100 dl19-passage runs/run.lucene-inverted.msmarco-v1-passage.slideon.model-slideon-onnx.topics-dl19-passage.txt
bin/trec_eval -c -l 2 -m recall.1000 dl19-passage runs/run.lucene-inverted.msmarco-v1-passage.slideon.model-slideon-onnx.topics-dl19-passage.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                | **SlideOn (ONNX), d512/q64, scale 100** |
|:-----------------------------------------------------------|:---------------------------------------:|
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html) | 0.4881                                  |
| **nDCG@10**                                                | **SlideOn (ONNX), d512/q64, scale 100** |
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html) | 0.7252                                  |
| **R@100**                                                  | **SlideOn (ONNX), d512/q64, scale 100** |
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html) | 0.6506                                  |
| **R@1000**                                                 | **SlideOn (ONNX), d512/q64, scale 100** |
| [DL19 (Passage)](https://trec.nist.gov/data/deep2019.html) | 0.8742                                  |

❗ Retrieval metrics here are computed to depth 1000 hits per query (as opposed to 100 hits per query for document ranking).
For computing nDCG, remember that we keep qrels of _all_ relevance grades, whereas for other metrics (e.g., AP), relevance grade 1 is considered not relevant (i.e., use the `-l 2` option in `trec_eval`).
The experimental results reported here are directly comparable to the results reported in the [track overview paper](https://arxiv.org/abs/2003.07820).
