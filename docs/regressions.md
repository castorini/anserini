# Anserini Regression Experiments

Regression experiments in Anserini are hooked into a rigorous end-to-end regression framework driven by the [`run_regression.py`](../src/main/python/run_regression.py) script.
This script automatically runs experiments based on configuration files stored in [`src/main/resources/regression/`](../src/main/resources/regression/), performing the following actions:

+ Building the index from scratch.
+ Verifying index statistics (sanity check that the index has been built properly).
+ Performing retrieval runs with standard settings.
+ Evaluating the runs and verifying effectiveness results.

Furthermore, the regression documentation pages are auto-generated based on [raw templates](../src/main/resources/docgen/templates).

Internally at Waterloo, we are continuously running these regression tests to ensure that new commits do not break any existing experimental runs (see below).
We keep a [change log](regressions-log.md) to document substantive changes.

## The Anserini Reproducibility Promise

It is the highest priority of the project to ensure that all regression experiments are reproducible _all the time_.
This means that anyone with the document collection should be able to reproduce _exactly_ the effectiveness scores we report in our regression documentation pages.

We hold this ideal in such high esteem and are so dedicated to reproducibility that if you discover a broken regression before we do, Jimmy Lin will buy you a beverage of choice (coffee, beer, etc.) at the next event you see him (e.g., SIGIR, TREC, etc.).

Here's how you can help:
In the course of reproducing one of our results, please let us know you've been successful by sending a pull request with a simple note, like what appears at the bottom of [the regressions for Disks 4 &amp; 5 page](regressions/regressions-disk45.md).
Since the regression documentation is auto-generated, pull requests should be sent against the [raw templates](../src/main/resources/docgen/templates).
In turn, you'll be recognized as a [contributor](https://github.com/castorini/anserini/graphs/contributors).

## Invocations

Internally at Waterloo, we have two machines (`tuna.cs.uwaterloo.ca` and `orca.cs.uwaterloo.ca`) for the development of Anserini and is set up to run the regression experiments.

Copy and paste the following lines into console to run the regressions from the raw collection:

<details>
<summary>MS MARCO V1 + DL19/DL20 regressions</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage >& logs/log.msmarco-passage &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-bm25-b8 >& logs/log.msmarco-passage-bm25-b8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-wp >& logs/log.msmarco-passage-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-hgf-wp >& logs/log.msmarco-passage-hgf-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-ca >& logs/log.msmarco-passage-ca &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-doc2query >& logs/log.msmarco-passage-doc2query &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-docTTTTTquery >& logs/log.msmarco-passage-docTTTTTquery &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-deepimpact >& logs/log.msmarco-passage-deepimpact &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-unicoil >& logs/log.msmarco-passage-unicoil &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-unicoil-noexp >& logs/log.msmarco-passage-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-unicoil-tilde-expansion >& logs/log.msmarco-passage-unicoil-tilde-expansion &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-distill-splade-max >& logs/log.msmarco-passage-distill-splade-max &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-splade-pp-ed >& logs/log.msmarco-passage-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-splade-pp-sd >& logs/log.msmarco-passage-splade-pp-sd &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-cos-dpr-distil-hnsw >& logs/log.msmarco-passage-cos-dpr-distil-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-cos-dpr-distil-hnsw-int8 >& logs/log.msmarco-passage-cos-dpr-distil-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-cos-dpr-distil-fw >& logs/log.msmarco-passage-cos-dpr-distil-fw &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-cos-dpr-distil-lexlsh >& logs/log.msmarco-passage-cos-dpr-distil-lexlsh &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-bge-base-en-v1.5-hnsw >& logs/log.msmarco-passage-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-bge-base-en-v1.5-hnsw-int8 >& logs/log.msmarco-passage-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-cohere-embed-english-v3.0-hnsw >& logs/log.msmarco-passage-cohere-embed-english-v3.0-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-cohere-embed-english-v3.0-hnsw-int8 >& logs/log.msmarco-passage-cohere-embed-english-v3.0-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-openai-ada2 >& logs/log.msmarco-passage-openai-ada2 &

nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-splade-pp-ed-onnx >& logs/log.msmarco-passage-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-splade-pp-sd-onnx >& logs/log.msmarco-passage-splade-pp-sd-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-cos-dpr-distil-hnsw-onnx >& logs/log.msmarco-passage-cos-dpr-distil-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-cos-dpr-distil-hnsw-int8-onnx >& logs/log.msmarco-passage-cos-dpr-distil-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-bge-base-en-v1.5-hnsw-onnx >& logs/log.msmarco-passage-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.msmarco-passage-bge-base-en-v1.5-hnsw-int8-onnx &

nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc >& logs/log.msmarco-doc &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-wp >& logs/log.msmarco-doc-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-hgf-wp >& logs/log.msmarco-doc-hgf-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-ca >& logs/log.msmarco-doc-ca &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-docTTTTTquery >& logs/log.msmarco-doc-docTTTTTquery &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-segmented >& logs/log.msmarco-doc-segmented &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-segmented-wp >& logs/log.msmarco-doc-segmented-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-segmented-ca >& logs/log.msmarco-doc-segmented-ca &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-segmented-docTTTTTquery >& logs/log.msmarco-doc-segmented-docTTTTTquery &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-segmented-unicoil >& logs/log.msmarco-doc-segmented-unicoil &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-segmented-unicoil-noexp >& logs/log.msmarco-doc-segmented-unicoil-noexp &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage >& logs/log.dl19-passage &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-bm25-b8 >& logs/log.dl19-passage-bm25-b8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-wp >& logs/log.dl19-passage-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-hgf-wp >& logs/log.dl19-passage-hgf-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-ca >& logs/log.dl19-passage-ca &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-docTTTTTquery >& logs/log.dl19-passage-docTTTTTquery &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-unicoil >& logs/log.dl19-passage-unicoil &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-unicoil-noexp >& logs/log.dl19-passage-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-splade-pp-ed >& logs/log.dl19-passage-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-splade-pp-sd >& logs/log.dl19-passage-splade-pp-sd &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-cos-dpr-distil-hnsw >& logs/log.dl19-passage-cos-dpr-distil-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-cos-dpr-distil-hnsw-int8 >& logs/log.dl19-passage-cos-dpr-distil-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-cos-dpr-distil-fw >& logs/log.dl19-passage-cos-dpr-distil-fw &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-cos-dpr-distil-lexlsh >& logs/log.dl19-passage-cos-dpr-distil-lexlsh &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-bge-base-en-v1.5-hnsw >& logs/log.dl19-passage-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-bge-base-en-v1.5-hnsw-int8 >& logs/log.dl19-passage-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-openai-ada2 >& logs/log.dl19-passage-openai-ada2 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-cohere-embed-english-v3.0-hnsw >& logs/log.dl19-passage-cohere-embed-english-v3.0-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-cohere-embed-english-v3.0-hnsw-int8 >& logs/log.dl19-passage-cohere-embed-english-v3.0-hnsw-int8 &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-splade-pp-ed-onnx >& logs/log.dl19-passage-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-splade-pp-sd-onnx >& logs/log.dl19-passage-splade-pp-sd-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-cos-dpr-distil-hnsw-onnx >& logs/log.dl19-passage-cos-dpr-distil-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-cos-dpr-distil-hnsw-int8-onnx >& logs/log.dl19-passage-cos-dpr-distil-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-bge-base-en-v1.5-hnsw-onnx >& logs/log.dl19-passage-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.dl19-passage-bge-base-en-v1.5-hnsw-int8-onnx &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc >& logs/log.dl19-doc &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-wp >& logs/log.dl19-doc-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-hgf-wp >& logs/log.dl19-doc-hgf-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-ca >& logs/log.dl19-doc-ca &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-docTTTTTquery >& logs/log.dl19-doc-docTTTTTquery &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-segmented >& logs/log.dl19-doc-segmented &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-segmented-wp >& logs/log.dl19-doc-segmented-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-segmented-ca >& logs/log.dl19-doc-segmented-ca &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-segmented-docTTTTTquery >& logs/log.dl19-doc-segmented-docTTTTTquery &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-segmented-unicoil >& logs/log.dl19-doc-segmented-unicoil &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-segmented-unicoil-noexp >& logs/log.dl19-doc-segmented-unicoil-noexp &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage >& logs/log.dl20-passage &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-bm25-b8 >& logs/log.dl20-passage-bm25-b8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-wp >& logs/log.dl20-passage-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-ca >& logs/log.dl20-passage-ca &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-hgf-wp >& logs/log.dl20-passage-hgf-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-docTTTTTquery >& logs/log.dl20-passage-docTTTTTquery &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-unicoil >& logs/log.dl20-passage-unicoil &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-unicoil-noexp >& logs/log.dl20-passage-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-splade-pp-ed >& logs/log.dl20-passage-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-splade-pp-sd >& logs/log.dl20-passage-splade-pp-sd &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-cos-dpr-distil-hnsw >& logs/log.dl20-passage-cos-dpr-distil-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-cos-dpr-distil-hnsw-int8 >& logs/log.dl20-passage-cos-dpr-distil-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-cos-dpr-distil-fw >& logs/log.dl20-passage-cos-dpr-distil-fw &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-cos-dpr-distil-lexlsh >& logs/log.dl20-passage-cos-dpr-distil-lexlsh &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-bge-base-en-v1.5-hnsw >& logs/log.dl20-passage-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-bge-base-en-v1.5-hnsw-int8 >& logs/log.dl20-passage-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-openai-ada2 >& logs/log.dl20-passage-openai-ada2 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-cohere-embed-english-v3.0-hnsw >& logs/log.dl20-passage-cohere-embed-english-v3.0-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-cohere-embed-english-v3.0-hnsw-int8 >& logs/log.dl20-passage-cohere-embed-english-v3.0-hnsw-int8 &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-splade-pp-ed-onnx >& logs/log.dl20-passage-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-splade-pp-sd-onnx >& logs/log.dl20-passage-splade-pp-sd-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-cos-dpr-distil-hnsw-onnx >& logs/log.dl20-passage-cos-dpr-distil-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-cos-dpr-distil-hnsw-int8-onnx >& logs/log.dl20-passage-cos-dpr-distil-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-bge-base-en-v1.5-hnsw-onnx >& logs/log.dl20-passage-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.dl20-passage-bge-base-en-v1.5-hnsw-int8-onnx &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc >& logs/log.dl20-doc &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-wp >& logs/log.dl20-doc-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-hgf-wp >& logs/log.dl20-doc-hgf-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-ca >& logs/log.dl20-doc-ca &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-docTTTTTquery >& logs/log.dl20-doc-docTTTTTquery &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-segmented >& logs/log.dl20-doc-segmented &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-segmented-wp >& logs/log.dl20-doc-segmented-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-segmented-ca >& logs/log.dl20-doc-segmented-ca &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-segmented-docTTTTTquery >& logs/log.dl20-doc-segmented-docTTTTTquery &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-segmented-unicoil >& logs/log.dl20-doc-segmented-unicoil &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-segmented-unicoil-noexp >& logs/log.dl20-doc-segmented-unicoil-noexp &
```

</details>
<details>
<summary>MS MARCO V2 + DL21 regressions</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage >& logs/log.msmarco-v2-passage &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-d2q-t5 >& logs/log.msmarco-v2-passage-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-augmented >& logs/log.msmarco-v2-passage-augmented &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-augmented-d2q-t5 >& logs/log.msmarco-v2-passage-augmented-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-unicoil-noexp-0shot >& logs/log.msmarco-v2-passage-unicoil-noexp-0shot &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-unicoil-0shot >& logs/log.msmarco-v2-passage-unicoil-0shot &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-splade-pp-ed >& logs/log.msmarco-v2-passage-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-splade-pp-ed-onnx >& logs/log.msmarco-v2-passage-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-splade-pp-sd >& logs/log.msmarco-v2-passage-splade-pp-sd &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-splade-pp-sd-onnx >& logs/log.msmarco-v2-passage-splade-pp-sd-onnx &

nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc >& logs/log.msmarco-v2-doc &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc-d2q-t5 >& logs/log.msmarco-v2-doc-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc-segmented >& logs/log.msmarco-v2-doc-segmented &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc-segmented-d2q-t5 >& logs/log.msmarco-v2-doc-segmented-d2q-t5 &

nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc-segmented-unicoil-noexp-0shot >& logs/log.msmarco-v2-doc-segmented-unicoil-noexp-0shot &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc-segmented-unicoil-noexp-0shot-v2 >& logs/log.msmarco-v2-doc-segmented-unicoil-noexp-0shot-v2 &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc-segmented-unicoil-0shot >& logs/log.msmarco-v2-doc-segmented-unicoil-0shot &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc-segmented-unicoil-0shot-v2 >& logs/log.msmarco-v2-doc-segmented-unicoil-0shot-v2 &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage >& logs/log.dl21-passage &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage-d2q-t5 >& logs/log.dl21-passage-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage-augmented >& logs/log.dl21-passage-augmented &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage-augmented-d2q-t5 >& logs/log.dl21-passage-augmented-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage-unicoil-noexp-0shot >& logs/log.dl21-passage-unicoil-noexp-0shot &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage-unicoil-0shot >& logs/log.dl21-passage-unicoil-0shot &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage-splade-pp-ed >& logs/log.dl21-passage-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage-splade-pp-ed-onnx >& logs/log.dl21-passage-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage-splade-pp-sd >& logs/log.dl21-passage-splade-pp-sd &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage-splade-pp-sd-onnx >& logs/log.dl21-passage-splade-pp-sd-onnx &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc >& logs/log.dl21-doc &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-d2q-t5 >& logs/log.dl21-doc-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented >& logs/log.dl21-doc-segmented &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented-d2q-t5 >& logs/log.dl21-doc-segmented-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented-unicoil-noexp-0shot >& logs/log.dl21-doc-segmented-unicoil-noexp-0shot &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented-unicoil-noexp-0shot-v2 >& logs/log.dl21-doc-segmented-unicoil-noexp-0shot-v2 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented-unicoil-0shot >& logs/log.dl21-doc-segmented-unicoil-0shot &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented-unicoil-0shot-v2 >& logs/log.dl21-doc-segmented-unicoil-0shot-v2 &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage >& logs/log.dl22-passage &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage-d2q-t5 >& logs/log.dl22-passage-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage-augmented >& logs/log.dl22-passage-augmented &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage-augmented-d2q-t5 >& logs/log.dl22-passage-augmented-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage-unicoil-noexp-0shot >& logs/log.dl22-passage-unicoil-noexp-0shot &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage-unicoil-0shot >& logs/log.dl22-passage-unicoil-0shot &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage-splade-pp-ed >& logs/log.dl22-passage-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage-splade-pp-ed-onnx >& logs/log.dl22-passage-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage-splade-pp-sd >& logs/log.dl22-passage-splade-pp-sd &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage-splade-pp-sd-onnx >& logs/log.dl22-passage-splade-pp-sd-onnx &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-doc >& logs/log.dl22-doc &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-doc-d2q-t5 >& logs/log.dl22-doc-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-doc-segmented >& logs/log.dl22-doc-segmented &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-doc-segmented-d2q-t5 >& logs/log.dl22-doc-segmented-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-doc-segmented-unicoil-noexp-0shot-v2 >& logs/log.dl22-doc-segmented-unicoil-noexp-0shot-v2 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-doc-segmented-unicoil-0shot-v2 >& logs/log.dl22-doc-segmented-unicoil-0shot-v2 &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage >& logs/log.dl23-passage &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage-d2q-t5 >& logs/log.dl23-passage-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage-augmented >& logs/log.dl23-passage-augmented &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage-augmented-d2q-t5 >& logs/log.dl23-passage-augmented-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage-unicoil-noexp-0shot >& logs/log.dl23-passage-unicoil-noexp-0shot &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage-unicoil-0shot >& logs/log.dl23-passage-unicoil-0shot &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage-splade-pp-ed >& logs/log.dl23-passage-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage-splade-pp-ed-onnx >& logs/log.dl23-passage-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage-splade-pp-sd >& logs/log.dl23-passage-splade-pp-sd &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage-splade-pp-sd-onnx >& logs/log.dl23-passage-splade-pp-sd-onnx &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-doc >& logs/log.dl23-doc &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-doc-d2q-t5 >& logs/log.dl23-doc-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-doc-segmented >& logs/log.dl23-doc-segmented &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-doc-segmented-d2q-t5 >& logs/log.dl23-doc-segmented-d2q-t5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-doc-segmented-unicoil-noexp-0shot-v2 >& logs/log.dl23-doc-segmented-unicoil-noexp-0shot-v2 &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-doc-segmented-unicoil-0shot-v2 >& logs/log.dl23-doc-segmented-unicoil-0shot-v2 &
```

</details>
<details>
<summary>BEIR (v1.0.0): BGE-base-en-v1.5</summary>

```bash
# Original HNSW indexes, pre-encoded queries
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-trec-covid-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-bioasq-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-nfcorpus-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-nq-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-hotpotqa-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-fiqa-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-signal1m-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-trec-news-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-robust04-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-arguana-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-webis-touche2020-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-quora-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-scidocs-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-fever-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-climate-fever-bge-base-en-v1.5-hnsw &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact-bge-base-en-v1.5-hnsw >& logs/log.beir-v1.0.0-scifact-bge-base-en-v1.5-hnsw &

# Original HNSW indexes, ONNX
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-trec-covid-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-bioasq-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-nfcorpus-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-nq-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-hotpotqa-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-fiqa-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-signal1m-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-trec-news-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-robust04-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-arguana-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-webis-touche2020-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-quora-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-scidocs-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-fever-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-climate-fever-bge-base-en-v1.5-hnsw-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact-bge-base-en-v1.5-hnsw-onnx >& logs/log.beir-v1.0.0-scifact-bge-base-en-v1.5-hnsw-onnx &

# Quantized HNSW indexes, pre-encoded queries
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-trec-covid-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-bioasq-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-nfcorpus-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-nq-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-hotpotqa-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-fiqa-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-signal1m-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-trec-news-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-robust04-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-arguana-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-webis-touche2020-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-quora-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-scidocs-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-fever-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-climate-fever-bge-base-en-v1.5-hnsw-int8 &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact-bge-base-en-v1.5-hnsw-int8 >& logs/log.beir-v1.0.0-scifact-bge-base-en-v1.5-hnsw-int8 &

# Quantized HNSW indexes, ONNX
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-trec-covid-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-bioasq-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-nfcorpus-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-nq-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-hotpotqa-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-fiqa-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-signal1m-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-trec-news-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-robust04-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-arguana-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-webis-touche2020-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-quora-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-scidocs-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-fever-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-climate-fever-bge-base-en-v1.5-hnsw-int8-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact-bge-base-en-v1.5-hnsw-int8-onnx >& logs/log.beir-v1.0.0-scifact-bge-base-en-v1.5-hnsw-int8-onnx &
```

</details>
<details>
<summary>BEIR (v1.0.0): SPLADE++ CoCondenser-EnsembleDistil</summary>

```bash
# Pre-encoded queries
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid-splade-pp-ed >& logs/log.beir-v1.0.0-trec-covid-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq-splade-pp-ed >& logs/log.beir-v1.0.0-bioasq-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus-splade-pp-ed >& logs/log.beir-v1.0.0-nfcorpus-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq-splade-pp-ed >& logs/log.beir-v1.0.0-nq-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa-splade-pp-ed >& logs/log.beir-v1.0.0-hotpotqa-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa-splade-pp-ed >& logs/log.beir-v1.0.0-fiqa-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m-splade-pp-ed >& logs/log.beir-v1.0.0-signal1m-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news-splade-pp-ed >& logs/log.beir-v1.0.0-trec-news-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04-splade-pp-ed >& logs/log.beir-v1.0.0-robust04-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana-splade-pp-ed >& logs/log.beir-v1.0.0-arguana-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020-splade-pp-ed >& logs/log.beir-v1.0.0-webis-touche2020-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android-splade-pp-ed >& logs/log.beir-v1.0.0-cqadupstack-android-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english-splade-pp-ed >& logs/log.beir-v1.0.0-cqadupstack-english-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming-splade-pp-ed >& logs/log.beir-v1.0.0-cqadupstack-gaming-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis-splade-pp-ed >& logs/log.beir-v1.0.0-cqadupstack-gis-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica-splade-pp-ed >& logs/log.beir-v1.0.0-cqadupstack-mathematica-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics-splade-pp-ed >& logs/log.beir-v1.0.0-cqadupstack-physics-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers-splade-pp-ed >& logs/log.beir-v1.0.0-cqadupstack-programmers-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats-splade-pp-ed >& logs/log.beir-v1.0.0-cqadupstack-stats-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex-splade-pp-ed >& logs/log.beir-v1.0.0-cqadupstack-tex-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix-splade-pp-ed >& logs/log.beir-v1.0.0-cqadupstack-unix-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters-splade-pp-ed >& logs/log.beir-v1.0.0-cqadupstack-webmasters-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress-splade-pp-ed >& logs/log.beir-v1.0.0-cqadupstack-wordpress-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora-splade-pp-ed >& logs/log.beir-v1.0.0-quora-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity-splade-pp-ed >& logs/log.beir-v1.0.0-dbpedia-entity-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs-splade-pp-ed >& logs/log.beir-v1.0.0-scidocs-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever-splade-pp-ed >& logs/log.beir-v1.0.0-fever-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever-splade-pp-ed >& logs/log.beir-v1.0.0-climate-fever-splade-pp-ed &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact-splade-pp-ed >& logs/log.beir-v1.0.0-scifact-splade-pp-ed &

# ONNX
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-trec-covid-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-bioasq-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-nfcorpus-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-nq-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-hotpotqa-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-fiqa-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-signal1m-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-trec-news-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-robust04-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-arguana-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-webis-touche2020-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-cqadupstack-android-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-cqadupstack-english-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-cqadupstack-gaming-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-cqadupstack-gis-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-cqadupstack-mathematica-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-cqadupstack-physics-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-cqadupstack-programmers-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-cqadupstack-stats-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-cqadupstack-tex-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-cqadupstack-unix-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-cqadupstack-webmasters-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-cqadupstack-wordpress-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-quora-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-dbpedia-entity-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-scidocs-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-fever-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-climate-fever-splade-pp-ed-onnx &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact-splade-pp-ed-onnx >& logs/log.beir-v1.0.0-scifact-splade-pp-ed-onnx &
```

</details>
<details>
<summary>BEIR (v1.0.0): uniCOIL (noexp)</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid-unicoil-noexp >& logs/log.beir-v1.0.0-trec-covid-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq-unicoil-noexp >& logs/log.beir-v1.0.0-bioasq-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus-unicoil-noexp >& logs/log.beir-v1.0.0-nfcorpus-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq-unicoil-noexp >& logs/log.beir-v1.0.0-nq-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa-unicoil-noexp >& logs/log.beir-v1.0.0-hotpotqa-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa-unicoil-noexp >& logs/log.beir-v1.0.0-fiqa-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m-unicoil-noexp >& logs/log.beir-v1.0.0-signal1m-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news-unicoil-noexp >& logs/log.beir-v1.0.0-trec-news-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04-unicoil-noexp >& logs/log.beir-v1.0.0-robust04-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana-unicoil-noexp >& logs/log.beir-v1.0.0-arguana-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020-unicoil-noexp >& logs/log.beir-v1.0.0-webis-touche2020-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android-unicoil-noexp >& logs/log.beir-v1.0.0-cqadupstack-android-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english-unicoil-noexp >& logs/log.beir-v1.0.0-cqadupstack-english-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming-unicoil-noexp >& logs/log.beir-v1.0.0-cqadupstack-gaming-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis-unicoil-noexp >& logs/log.beir-v1.0.0-cqadupstack-gis-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica-unicoil-noexp >& logs/log.beir-v1.0.0-cqadupstack-mathematica-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics-unicoil-noexp >& logs/log.beir-v1.0.0-cqadupstack-physics-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers-unicoil-noexp >& logs/log.beir-v1.0.0-cqadupstack-programmers-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats-unicoil-noexp >& logs/log.beir-v1.0.0-cqadupstack-stats-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex-unicoil-noexp >& logs/log.beir-v1.0.0-cqadupstack-tex-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix-unicoil-noexp >& logs/log.beir-v1.0.0-cqadupstack-unix-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters-unicoil-noexp >& logs/log.beir-v1.0.0-cqadupstack-webmasters-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress-unicoil-noexp >& logs/log.beir-v1.0.0-cqadupstack-wordpress-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora-unicoil-noexp >& logs/log.beir-v1.0.0-quora-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity-unicoil-noexp >& logs/log.beir-v1.0.0-dbpedia-entity-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs-unicoil-noexp >& logs/log.beir-v1.0.0-scidocs-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever-unicoil-noexp >& logs/log.beir-v1.0.0-fever-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever-unicoil-noexp >& logs/log.beir-v1.0.0-climate-fever-unicoil-noexp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact-unicoil-noexp >& logs/log.beir-v1.0.0-scifact-unicoil-noexp &
```

</details>
<details>
<summary>BEIR (v1.0.0): "flat" baseline</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid-flat >& logs/log.beir-v1.0.0-trec-covid-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq-flat >& logs/log.beir-v1.0.0-bioasq-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus-flat >& logs/log.beir-v1.0.0-nfcorpus-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq-flat >& logs/log.beir-v1.0.0-nq-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa-flat >& logs/log.beir-v1.0.0-hotpotqa-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa-flat >& logs/log.beir-v1.0.0-fiqa-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m-flat >& logs/log.beir-v1.0.0-signal1m-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news-flat >& logs/log.beir-v1.0.0-trec-news-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04-flat >& logs/log.beir-v1.0.0-robust04-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana-flat >& logs/log.beir-v1.0.0-arguana-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020-flat >& logs/log.beir-v1.0.0-webis-touche2020-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android-flat >& logs/log.beir-v1.0.0-cqadupstack-android-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english-flat >& logs/log.beir-v1.0.0-cqadupstack-english-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming-flat >& logs/log.beir-v1.0.0-cqadupstack-gaming-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis-flat >& logs/log.beir-v1.0.0-cqadupstack-gis-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica-flat >& logs/log.beir-v1.0.0-cqadupstack-mathematica-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics-flat >& logs/log.beir-v1.0.0-cqadupstack-physics-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers-flat >& logs/log.beir-v1.0.0-cqadupstack-programmers-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats-flat >& logs/log.beir-v1.0.0-cqadupstack-stats-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex-flat >& logs/log.beir-v1.0.0-cqadupstack-tex-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix-flat >& logs/log.beir-v1.0.0-cqadupstack-unix-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters-flat >& logs/log.beir-v1.0.0-cqadupstack-webmasters-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress-flat >& logs/log.beir-v1.0.0-cqadupstack-wordpress-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora-flat >& logs/log.beir-v1.0.0-quora-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity-flat >& logs/log.beir-v1.0.0-dbpedia-entity-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs-flat >& logs/log.beir-v1.0.0-scidocs-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever-flat >& logs/log.beir-v1.0.0-fever-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever-flat >& logs/log.beir-v1.0.0-climate-fever-flat &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact-flat >& logs/log.beir-v1.0.0-scifact-flat &
```

</details>
<details>
<summary>BEIR (v1.0.0): "flat" baseline with WordPiece tokenization</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid-flat-wp >& logs/log.beir-v1.0.0-trec-covid-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq-flat-wp >& logs/log.beir-v1.0.0-bioasq-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus-flat-wp >& logs/log.beir-v1.0.0-nfcorpus-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq-flat-wp >& logs/log.beir-v1.0.0-nq-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa-flat-wp >& logs/log.beir-v1.0.0-hotpotqa-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa-flat-wp >& logs/log.beir-v1.0.0-fiqa-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m-flat-wp >& logs/log.beir-v1.0.0-signal1m-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news-flat-wp >& logs/log.beir-v1.0.0-trec-news-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04-flat-wp >& logs/log.beir-v1.0.0-robust04-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana-flat-wp >& logs/log.beir-v1.0.0-arguana-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020-flat-wp >& logs/log.beir-v1.0.0-webis-touche2020-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android-flat-wp >& logs/log.beir-v1.0.0-cqadupstack-android-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english-flat-wp >& logs/log.beir-v1.0.0-cqadupstack-english-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming-flat-wp >& logs/log.beir-v1.0.0-cqadupstack-gaming-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis-flat-wp >& logs/log.beir-v1.0.0-cqadupstack-gis-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica-flat-wp >& logs/log.beir-v1.0.0-cqadupstack-mathematica-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics-flat-wp >& logs/log.beir-v1.0.0-cqadupstack-physics-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers-flat-wp >& logs/log.beir-v1.0.0-cqadupstack-programmers-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats-flat-wp >& logs/log.beir-v1.0.0-cqadupstack-stats-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex-flat-wp >& logs/log.beir-v1.0.0-cqadupstack-tex-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix-flat-wp >& logs/log.beir-v1.0.0-cqadupstack-unix-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters-flat-wp >& logs/log.beir-v1.0.0-cqadupstack-webmasters-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress-flat-wp >& logs/log.beir-v1.0.0-cqadupstack-wordpress-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora-flat-wp >& logs/log.beir-v1.0.0-quora-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity-flat-wp >& logs/log.beir-v1.0.0-dbpedia-entity-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs-flat-wp >& logs/log.beir-v1.0.0-scidocs-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever-flat-wp >& logs/log.beir-v1.0.0-fever-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever-flat-wp >& logs/log.beir-v1.0.0-climate-fever-flat-wp &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact-flat-wp >& logs/log.beir-v1.0.0-scifact-flat-wp &
```

</details>
<details>
<summary>BEIR (v1.0.0): "multifield" baseline</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid-multifield >& logs/log.beir-v1.0.0-trec-covid-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq-multifield >& logs/log.beir-v1.0.0-bioasq-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus-multifield >& logs/log.beir-v1.0.0-nfcorpus-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq-multifield >& logs/log.beir-v1.0.0-nq-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa-multifield >& logs/log.beir-v1.0.0-hotpotqa-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa-multifield >& logs/log.beir-v1.0.0-fiqa-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m-multifield >& logs/log.beir-v1.0.0-signal1m-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news-multifield >& logs/log.beir-v1.0.0-trec-news-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04-multifield >& logs/log.beir-v1.0.0-robust04-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana-multifield >& logs/log.beir-v1.0.0-arguana-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020-multifield >& logs/log.beir-v1.0.0-webis-touche2020-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android-multifield >& logs/log.beir-v1.0.0-cqadupstack-android-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english-multifield >& logs/log.beir-v1.0.0-cqadupstack-english-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming-multifield >& logs/log.beir-v1.0.0-cqadupstack-gaming-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis-multifield >& logs/log.beir-v1.0.0-cqadupstack-gis-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica-multifield >& logs/log.beir-v1.0.0-cqadupstack-mathematica-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics-multifield >& logs/log.beir-v1.0.0-cqadupstack-physics-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers-multifield >& logs/log.beir-v1.0.0-cqadupstack-programmers-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats-multifield >& logs/log.beir-v1.0.0-cqadupstack-stats-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex-multifield >& logs/log.beir-v1.0.0-cqadupstack-tex-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix-multifield >& logs/log.beir-v1.0.0-cqadupstack-unix-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters-multifield >& logs/log.beir-v1.0.0-cqadupstack-webmasters-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress-multifield >& logs/log.beir-v1.0.0-cqadupstack-wordpress-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora-multifield >& logs/log.beir-v1.0.0-quora-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity-multifield >& logs/log.beir-v1.0.0-dbpedia-entity-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs-multifield >& logs/log.beir-v1.0.0-scidocs-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever-multifield >& logs/log.beir-v1.0.0-fever-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever-multifield >& logs/log.beir-v1.0.0-climate-fever-multifield &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact-multifield >& logs/log.beir-v1.0.0-scifact-multifield &
```

</details>
<details>
<summary>Mr.TyDi (v1.1): BM25 regressions</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ar >& logs/log.mrtydi-v1.1-ar &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-bn >& logs/log.mrtydi-v1.1-bn &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-en >& logs/log.mrtydi-v1.1-en &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-fi >& logs/log.mrtydi-v1.1-fi &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-id >& logs/log.mrtydi-v1.1-id &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ja >& logs/log.mrtydi-v1.1-ja &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ko >& logs/log.mrtydi-v1.1-ko &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ru >& logs/log.mrtydi-v1.1-ru &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-sw >& logs/log.mrtydi-v1.1-sw &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-te >& logs/log.mrtydi-v1.1-te &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-th >& logs/log.mrtydi-v1.1-th &

nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ar-aca >& logs/log.mrtydi-v1.1-ar-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-bn-aca >& logs/log.mrtydi-v1.1-bn-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-en-aca >& logs/log.mrtydi-v1.1-en-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-fi-aca >& logs/log.mrtydi-v1.1-fi-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-id-aca >& logs/log.mrtydi-v1.1-id-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ja-aca >& logs/log.mrtydi-v1.1-ja-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ko-aca >& logs/log.mrtydi-v1.1-ko-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ru-aca >& logs/log.mrtydi-v1.1-ru-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-sw-aca >& logs/log.mrtydi-v1.1-sw-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-te-aca >& logs/log.mrtydi-v1.1-te-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-th-aca >& logs/log.mrtydi-v1.1-th-aca &
```

</details>
<details>
<summary>MIRACL (v1.0): BM25 regressions</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ar >& logs/log.miracl-v1.0-ar &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-bn >& logs/log.miracl-v1.0-bn &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-en >& logs/log.miracl-v1.0-en &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-es >& logs/log.miracl-v1.0-es &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-fa >& logs/log.miracl-v1.0-fa &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-fi >& logs/log.miracl-v1.0-fi &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-fr >& logs/log.miracl-v1.0-fr &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-hi >& logs/log.miracl-v1.0-hi &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-id >& logs/log.miracl-v1.0-id &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ja >& logs/log.miracl-v1.0-ja &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ko >& logs/log.miracl-v1.0-ko &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ru >& logs/log.miracl-v1.0-ru &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-sw >& logs/log.miracl-v1.0-sw &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-te >& logs/log.miracl-v1.0-te &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-th >& logs/log.miracl-v1.0-th &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-zh >& logs/log.miracl-v1.0-zh &

nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ar-aca >& logs/log.miracl-v1.0-ar-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-bn-aca >& logs/log.miracl-v1.0-bn-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-en-aca >& logs/log.miracl-v1.0-en-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-es-aca >& logs/log.miracl-v1.0-es-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-fa-aca >& logs/log.miracl-v1.0-fa-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-fi-aca >& logs/log.miracl-v1.0-fi-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-fr-aca >& logs/log.miracl-v1.0-fr-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-hi-aca >& logs/log.miracl-v1.0-hi-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-id-aca >& logs/log.miracl-v1.0-id-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ja-aca >& logs/log.miracl-v1.0-ja-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ko-aca >& logs/log.miracl-v1.0-ko-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ru-aca >& logs/log.miracl-v1.0-ru-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-sw-aca >& logs/log.miracl-v1.0-sw-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-te-aca >& logs/log.miracl-v1.0-te-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-th-aca >& logs/log.miracl-v1.0-th-aca &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-zh-aca >& logs/log.miracl-v1.0-zh-aca &
```

</details>
<details>
<summary>Other cross-lingual and multi-lingual regressions</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression ntcir8-zh >& logs/log.ntcir8-zh &
nohup python src/main/python/run_regression.py --index --verify --search --regression clef06-fr >& logs/log.clef06-fr &
nohup python src/main/python/run_regression.py --index --verify --search --regression trec02-ar >& logs/log.trec02-ar &
nohup python src/main/python/run_regression.py --index --verify --search --regression fire12-bn >& logs/log.fire12-bn &
nohup python src/main/python/run_regression.py --index --verify --search --regression fire12-hi >& logs/log.fire12-hi &
nohup python src/main/python/run_regression.py --index --verify --search --regression fire12-en >& logs/log.fire12-en &

nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-v1.0-fa >& logs/log.hc4-v1.0-fa &
nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-v1.0-ru >& logs/log.hc4-v1.0-ru &
nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-v1.0-zh >& logs/log.hc4-v1.0-zh &

nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-fa >& logs/log.hc4-neuclir22-fa &
nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-ru >& logs/log.hc4-neuclir22-ru &
nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-zh >& logs/log.hc4-neuclir22-zh &

nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-fa-en >& logs/log.hc4-neuclir22-fa-en &
nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-ru-en >& logs/log.hc4-neuclir22-ru-en &
nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-zh-en >& logs/log.hc4-neuclir22-zh-en &

nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-qt >& logs/log.neuclir22-fa-qt &
nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-dt >& logs/log.neuclir22-fa-dt &

nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-qt >& logs/log.neuclir22-ru-qt &
nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-dt >& logs/log.neuclir22-ru-dt &

nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-qt >& logs/log.neuclir22-zh-qt &
nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-dt >& logs/log.neuclir22-zh-dt &

nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-qt-splade >& logs/log.neuclir22-fa-qt-splade &
nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-dt-splade >& logs/log.neuclir22-fa-dt-splade &

nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-qt-splade >& logs/log.neuclir22-ru-qt-splade &
nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-dt-splade >& logs/log.neuclir22-ru-dt-splade &

nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-qt-splade >& logs/log.neuclir22-zh-qt-splade &
nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-dt-splade >& logs/log.neuclir22-zh-dt-splade &

nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-ha >& logs/log.ciral-v1.0-ha &
nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-so >& logs/log.ciral-v1.0-so &
nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-sw >& logs/log.ciral-v1.0-sw &
nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-yo >& logs/log.ciral-v1.0-yo &

nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-ha-en >& logs/log.ciral-v1.0-ha-en &
nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-so-en >& logs/log.ciral-v1.0-so-en &
nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-sw-en >& logs/log.ciral-v1.0-sw-en &
nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-yo-en >& logs/log.ciral-v1.0-yo-en &
```

</details>
<details>
<summary>Other regressions</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression backgroundlinking18 >& logs/log.backgroundlinking18 &
nohup python src/main/python/run_regression.py --index --verify --search --regression backgroundlinking19 >& logs/log.backgroundlinking19 &
nohup python src/main/python/run_regression.py --index --verify --search --regression backgroundlinking20 >& logs/log.backgroundlinking20 &

nohup python src/main/python/run_regression.py --index --verify --search --regression disk12 >& logs/log.disk12 &
nohup python src/main/python/run_regression.py --index --verify --search --regression disk45 >& logs/log.disk45 &
nohup python src/main/python/run_regression.py --index --verify --search --regression robust05 >& logs/log.robust05 &
nohup python src/main/python/run_regression.py --index --verify --search --regression core17 >& logs/log.core17 &
nohup python src/main/python/run_regression.py --index --verify --search --regression core18 >& logs/log.core18 &

nohup python src/main/python/run_regression.py --index --verify --search --regression mb11 >& logs/log.mb11 &
nohup python src/main/python/run_regression.py --index --verify --search --regression mb13 >& logs/log.mb13 &

nohup python src/main/python/run_regression.py --index --verify --search --regression car17v1.5 >& logs/log.car17v1.5 &
nohup python src/main/python/run_regression.py --index --verify --search --regression car17v2.0 >& logs/log.car17v2.0 &
nohup python src/main/python/run_regression.py --index --verify --search --regression car17v2.0-doc2query >& logs/log.car17v2.0-doc2query &

nohup python src/main/python/run_regression.py --index --verify --search --regression wt10g >& logs/log.wt10g &
nohup python src/main/python/run_regression.py --index --verify --search --regression gov2 >& logs/log.gov2 &
nohup python src/main/python/run_regression.py --index --verify --search --regression cw09b >& logs/log.cw09b &
nohup python src/main/python/run_regression.py --index --verify --search --regression cw12b13 >& logs/log.cw12b13 &
nohup python src/main/python/run_regression.py --index --verify --search --regression cw12 >& logs/log.cw12 &

nohup python src/main/python/run_regression.py --index --verify --search --regression fever >& logs/log.fever &

nohup python src/main/python/run_regression.py --index --verify --search --regression wikipedia-dpr-100w-bm25 >& logs/log.wikipedia-dpr-100w-bm25 &
nohup python src/main/python/run_regression.py --index --verify --search --regression wiki-all-6-3-tamber-bm25 >& logs/log.wiki-all-6-3-tamber-bm25 &
```

</details>

The `--regression` option specifies the regression to run, corresponding to the YAML configuration file in [`src/main/resources/regression/`](../src/main/resources/regression/).
The three main options are:

+ `--index`: Build the index.
+ `--verify`: Verify index statistics.
+ `--search`: Perform retrieval runs and verify effectiveness.

**Watch out!** The full `cw12` regression can take a couple days to run and generates a 12TB index!

Although the regression script is hard-coded to run on Waterloo machines (paths to corpoa are hard-coded), the corpus path can be manually specified from the command line with the `--corpus-path` option, for example:

```bash
python src/main/python/run_regression.py --index --verify --search --regression disk45 --corpus-path /path/to/corpus
```
