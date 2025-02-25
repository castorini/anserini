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
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage >& logs/log.msmarco-v1-passage.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.bm25-b8 >& logs/log.msmarco-v1-passage.bm25-b8.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.wp-tok >& logs/log.msmarco-v1-passage.wp-tok.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.wp-hgf >& logs/log.msmarco-v1-passage.wp-hgf.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.wp-ca >& logs/log.msmarco-v1-passage.wp-ca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.doc2query >& logs/log.msmarco-v1-passage.doc2query.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.docTTTTTquery >& logs/log.msmarco-v1-passage.docTTTTTquery.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.deepimpact.cached >& logs/log.msmarco-v1-passage.deepimpact.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.unicoil.cached >& logs/log.msmarco-v1-passage.unicoil.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.unicoil-noexp.cached >& logs/log.msmarco-v1-passage.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.unicoil-tilde-expansion.cached >& logs/log.msmarco-v1-passage.unicoil-tilde-expansion.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.distill-splade-max.cached >& logs/log.msmarco-v1-passage.distill-splade-max.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.splade-pp-ed.cached >& logs/log.msmarco-v1-passage.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.splade-pp-ed.onnx >& logs/log.msmarco-v1-passage.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.splade-pp-sd.cached >& logs/log.msmarco-v1-passage.splade-pp-sd.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.splade-pp-sd.onnx >& logs/log.msmarco-v1-passage.splade-pp-sd.onnx.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cos-dpr-distil.parquet.fw >& logs/log.msmarco-v1-passage.cos-dpr-distil.parquet.fw.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cos-dpr-distil.parquet.lexlsh >& logs/log.msmarco-v1-passage.cos-dpr-distil.parquet.lexlsh.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cos-dpr-distil.parquet.flat.cached >& logs/log.msmarco-v1-passage.cos-dpr-distil.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cos-dpr-distil.parquet.flat.onnx >& logs/log.msmarco-v1-passage.cos-dpr-distil.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cos-dpr-distil.parquet.flat-int8.cached >& logs/log.msmarco-v1-passage.cos-dpr-distil.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cos-dpr-distil.parquet.flat-int8.onnx >& logs/log.msmarco-v1-passage.cos-dpr-distil.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cos-dpr-distil.parquet.hnsw.cached >& logs/log.msmarco-v1-passage.cos-dpr-distil.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cos-dpr-distil.parquet.hnsw.onnx >& logs/log.msmarco-v1-passage.cos-dpr-distil.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cos-dpr-distil.parquet.hnsw-int8.cached >& logs/log.msmarco-v1-passage.cos-dpr-distil.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cos-dpr-distil.parquet.hnsw-int8.onnx >& logs/log.msmarco-v1-passage.cos-dpr-distil.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.bge-base-en-v1.5.parquet.flat.cached >& logs/log.msmarco-v1-passage.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.msmarco-v1-passage.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.msmarco-v1-passage.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.msmarco-v1-passage.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.openai-ada2.parquet.flat.cached >& logs/log.msmarco-v1-passage.openai-ada2.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.openai-ada2.parquet.flat-int8.cached >& logs/log.msmarco-v1-passage.openai-ada2.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.openai-ada2.parquet.hnsw.cached >& logs/log.msmarco-v1-passage.openai-ada2.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.openai-ada2.parquet.hnsw-int8.cached >& logs/log.msmarco-v1-passage.openai-ada2.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat.cached >& logs/log.msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat-int8.cached >& logs/log.msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cohere-embed-english-v3.0.parquet.hnsw.cached >& logs/log.msmarco-v1-passage.cohere-embed-english-v3.0.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.cohere-embed-english-v3.0.parquet.hnsw-int8.cached >& logs/log.msmarco-v1-passage.cohere-embed-english-v3.0.parquet.hnsw-int8.cached.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-doc >& logs/log.msmarco-v1-doc.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-doc.wp-tok >& logs/log.msmarco-v1-doc.wp-tok.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-doc.wp-hgf >& logs/log.msmarco-v1-doc.wp-hgf.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-doc.wp-ca >& logs/log.msmarco-v1-doc.wp-ca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-doc.docTTTTTquery >& logs/log.msmarco-v1-doc.docTTTTTquery.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-doc-segmented >& logs/log.msmarco-v1-doc-segmented.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-doc-segmented.wp-tok >& logs/log.msmarco-v1-doc-segmented.wp-tok.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-doc-segmented.wp-ca >& logs/log.msmarco-v1-doc-segmented.wp-ca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-doc-segmented.docTTTTTquery >& logs/log.msmarco-v1-doc-segmented.docTTTTTquery.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-doc-segmented.unicoil.cached >& logs/log.msmarco-v1-doc-segmented.unicoil.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-doc-segmented.unicoil-noexp.cached >& logs/log.msmarco-v1-doc-segmented.unicoil-noexp.cached.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage >& logs/log.dl19-passage.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.bm25-b8 >& logs/log.dl19-passage.bm25-b8.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.wp-tok >& logs/log.dl19-passage.wp-tok.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.wp-hgf >& logs/log.dl19-passage.wp-hgf.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.wp-ca >& logs/log.dl19-passage.wp-ca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.docTTTTTquery >& logs/log.dl19-passage.docTTTTTquery.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.unicoil.cached >& logs/log.dl19-passage.unicoil.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.unicoil-noexp.cached >& logs/log.dl19-passage.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.splade-pp-ed.cached >& logs/log.dl19-passage.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.splade-pp-ed.onnx >& logs/log.dl19-passage.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.splade-pp-sd.cached >& logs/log.dl19-passage.splade-pp-sd.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.splade-pp-sd.onnx >& logs/log.dl19-passage.splade-pp-sd.onnx.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.cos-dpr-distil.parquet.fw >& logs/log.dl19-passage.cos-dpr-distil.parquet.fw.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.cos-dpr-distil.parquet.lexlsh >& logs/log.dl19-passage.cos-dpr-distil.parquet.lexlsh.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.cos-dpr-distil.parquet.flat.cached >& logs/log.dl19-passage.cos-dpr-distil.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.cos-dpr-distil.parquet.flat.onnx >& logs/log.dl19-passage.cos-dpr-distil.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.cos-dpr-distil.parquet.flat-int8.cached >& logs/log.dl19-passage.cos-dpr-distil.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.cos-dpr-distil.parquet.flat-int8.onnx >& logs/log.dl19-passage.cos-dpr-distil.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.cos-dpr-distil.parquet.hnsw.cached >& logs/log.dl19-passage.cos-dpr-distil.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.cos-dpr-distil.parquet.hnsw.onnx >& logs/log.dl19-passage.cos-dpr-distil.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.cos-dpr-distil.parquet.hnsw-int8.cached >& logs/log.dl19-passage.cos-dpr-distil.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.cos-dpr-distil.parquet.hnsw-int8.onnx >& logs/log.dl19-passage.cos-dpr-distil.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.bge-base-en-v1.5.parquet.flat.cached >& logs/log.dl19-passage.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.dl19-passage.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.dl19-passage.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.dl19-passage.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.dl19-passage.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.dl19-passage.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.dl19-passage.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.dl19-passage.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.openai-ada2.parquet.flat.cached >& logs/log.dl19-passage.openai-ada2.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.openai-ada2.parquet.flat-int8.cached >& logs/log.dl19-passage.openai-ada2.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.openai-ada2.parquet.hnsw.cached >& logs/log.dl19-passage.openai-ada2.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.openai-ada2.parquet.hnsw-int8.cached >& logs/log.dl19-passage.openai-ada2.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.cohere-embed-english-v3.0.parquet.flat.cached >& logs/log.dl19-passage.cohere-embed-english-v3.0.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.cohere-embed-english-v3.0.parquet.flat-int8.cached >& logs/log.dl19-passage.cohere-embed-english-v3.0.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.cohere-embed-english-v3.0.parquet.hnsw.cached >& logs/log.dl19-passage.cohere-embed-english-v3.0.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-passage.cohere-embed-english-v3.0.parquet.hnsw-int8.cached >& logs/log.dl19-passage.cohere-embed-english-v3.0.parquet.hnsw-int8.cached.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc >& logs/log.dl19-doc.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc.wp-tok >& logs/log.dl19-doc.wp-tok.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc.wp-hgf >& logs/log.dl19-doc.wp-hgf.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc.wp-ca >& logs/log.dl19-doc.wp-ca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc.docTTTTTquery >& logs/log.dl19-doc.docTTTTTquery.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-segmented >& logs/log.dl19-doc-segmented.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-segmented.wp-tok >& logs/log.dl19-doc-segmented.wp-tok.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-segmented.wp-ca >& logs/log.dl19-doc-segmented.wp-ca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-segmented.docTTTTTquery >& logs/log.dl19-doc-segmented.docTTTTTquery.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-segmented.unicoil.cached >& logs/log.dl19-doc-segmented.unicoil.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl19-doc-segmented.unicoil-noexp.cached >& logs/log.dl19-doc-segmented.unicoil-noexp.cached.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage >& logs/log.dl20-passage.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.bm25-b8 >& logs/log.dl20-passage.bm25-b8.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.wp-tok >& logs/log.dl20-passage.wp-tok.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.wp-hgf >& logs/log.dl20-passage.wp-hgf.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.wp-ca >& logs/log.dl20-passage.wp-ca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.docTTTTTquery >& logs/log.dl20-passage.docTTTTTquery.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.unicoil.cached >& logs/log.dl20-passage.unicoil.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.unicoil-noexp.cached >& logs/log.dl20-passage.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.splade-pp-ed.cached >& logs/log.dl20-passage.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.splade-pp-ed.onnx >& logs/log.dl20-passage.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.splade-pp-sd.cached >& logs/log.dl20-passage.splade-pp-sd.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.splade-pp-sd.onnx >& logs/log.dl20-passage.splade-pp-sd.onnx.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cos-dpr-distil.parquet.fw >& logs/log.dl20-passage.cos-dpr-distil.parquet.fw.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cos-dpr-distil.parquet.lexlsh >& logs/log.dl20-passage.cos-dpr-distil.parquet.lexlsh.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cos-dpr-distil.parquet.flat.cached >& logs/log.dl20-passage.cos-dpr-distil.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cos-dpr-distil.parquet.flat.onnx >& logs/log.dl20-passage.cos-dpr-distil.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cos-dpr-distil.parquet.flat-int8.cached >& logs/log.dl20-passage.cos-dpr-distil.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cos-dpr-distil.parquet.flat-int8.onnx >& logs/log.dl20-passage.cos-dpr-distil.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cos-dpr-distil.parquet.hnsw.cached >& logs/log.dl20-passage.cos-dpr-distil.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cos-dpr-distil.parquet.hnsw.onnx >& logs/log.dl20-passage.cos-dpr-distil.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cos-dpr-distil.parquet.hnsw-int8.cached >& logs/log.dl20-passage.cos-dpr-distil.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cos-dpr-distil.parquet.hnsw-int8.onnx >& logs/log.dl20-passage.cos-dpr-distil.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.bge-base-en-v1.5.parquet.flat.cached >& logs/log.dl20-passage.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.dl20-passage.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.dl20-passage.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.dl20-passage.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.dl20-passage.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.dl20-passage.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.dl20-passage.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.dl20-passage.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.openai-ada2.parquet.flat.cached >& logs/log.dl20-passage.openai-ada2.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.openai-ada2.parquet.flat-int8.cached >& logs/log.dl20-passage.openai-ada2.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.openai-ada2.parquet.hnsw.cached >& logs/log.dl20-passage.openai-ada2.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.openai-ada2.parquet.hnsw-int8.cached >& logs/log.dl20-passage.openai-ada2.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cohere-embed-english-v3.0.parquet.flat.cached >& logs/log.dl20-passage.cohere-embed-english-v3.0.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cohere-embed-english-v3.0.parquet.flat-int8.cached >& logs/log.dl20-passage.cohere-embed-english-v3.0.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cohere-embed-english-v3.0.parquet.hnsw.cached >& logs/log.dl20-passage.cohere-embed-english-v3.0.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-passage.cohere-embed-english-v3.0.parquet.hnsw-int8.cached >& logs/log.dl20-passage.cohere-embed-english-v3.0.parquet.hnsw-int8.cached.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc >& logs/log.dl20-doc.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc.wp-tok >& logs/log.dl20-doc.wp-tok.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc.wp-hgf >& logs/log.dl20-doc.wp-hgf.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc.wp-ca >& logs/log.dl20-doc.wp-ca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc.docTTTTTquery >& logs/log.dl20-doc.docTTTTTquery.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-segmented >& logs/log.dl20-doc-segmented.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-segmented.wp-tok >& logs/log.dl20-doc-segmented.wp-tok.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-segmented.wp-ca >& logs/log.dl20-doc-segmented.wp-ca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-segmented.docTTTTTquery >& logs/log.dl20-doc-segmented.docTTTTTquery.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-segmented.unicoil.cached >& logs/log.dl20-doc-segmented.unicoil.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl20-doc-segmented.unicoil-noexp.cached >& logs/log.dl20-doc-segmented.unicoil-noexp.cached.txt &
```

</details>
<details>
<summary>MS MARCO V2 + DL21 regressions</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage >& logs/log.msmarco-v2-passage.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage.d2q-t5 >& logs/log.msmarco-v2-passage.d2q-t5.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage.unicoil-noexp-0shot.cached >& logs/log.msmarco-v2-passage.unicoil-noexp-0shot.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage.unicoil-0shot.cached >& logs/log.msmarco-v2-passage.unicoil-0shot.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage.splade-pp-ed.cached >& logs/log.msmarco-v2-passage.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage.splade-pp-ed.onnx >& logs/log.msmarco-v2-passage.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage.splade-pp-sd.cached >& logs/log.msmarco-v2-passage.splade-pp-sd.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage.splade-pp-sd.onnx >& logs/log.msmarco-v2-passage.splade-pp-sd.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-augmented >& logs/log.msmarco-v2-passage-augmented.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-augmented.d2q-t5 >& logs/log.msmarco-v2-passage-augmented.d2q-t5.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc >& logs/log.msmarco-v2-doc.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc.d2q-t5 >& logs/log.msmarco-v2-doc.d2q-t5.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc-segmented >& logs/log.msmarco-v2-doc-segmented.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc-segmented.d2q-t5 >& logs/log.msmarco-v2-doc-segmented.d2q-t5.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc-segmented.unicoil-noexp-0shot.cached >& logs/log.msmarco-v2-doc-segmented.unicoil-noexp-0shot.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc-segmented.unicoil-noexp-0shot-v2.cached >& logs/log.msmarco-v2-doc-segmented.unicoil-noexp-0shot-v2.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc-segmented.unicoil-0shot.cached >& logs/log.msmarco-v2-doc-segmented.unicoil-0shot.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-doc-segmented.unicoil-0shot-v2.cached >& logs/log.msmarco-v2-doc-segmented.unicoil-0shot-v2.cached.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage >& logs/log.dl21-passage.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage.d2q-t5 >& logs/log.dl21-passage.d2q-t5.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage.unicoil-noexp-0shot.cached >& logs/log.dl21-passage.unicoil-noexp-0shot.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage.unicoil-0shot.cached >& logs/log.dl21-passage.unicoil-0shot.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage.splade-pp-ed.cached >& logs/log.dl21-passage.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage.splade-pp-ed.onnx >& logs/log.dl21-passage.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage.splade-pp-sd.cached >& logs/log.dl21-passage.splade-pp-sd.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage.splade-pp-sd.onnx >& logs/log.dl21-passage.splade-pp-sd.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage-augmented >& logs/log.dl21-passage-augmented.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-passage-augmented.d2q-t5 >& logs/log.dl21-passage-augmented.d2q-t5.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc >& logs/log.dl21-doc.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc.d2q-t5 >& logs/log.dl21-doc.d2q-t5.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented >& logs/log.dl21-doc-segmented.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented.d2q-t5 >& logs/log.dl21-doc-segmented.d2q-t5.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented.unicoil-noexp-0shot.cached >& logs/log.dl21-doc-segmented.unicoil-noexp-0shot.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented.unicoil-noexp-0shot-v2.cached >& logs/log.dl21-doc-segmented.unicoil-noexp-0shot-v2.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented.unicoil-0shot.cached >& logs/log.dl21-doc-segmented.unicoil-0shot.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented.unicoil-0shot-v2.cached >& logs/log.dl21-doc-segmented.unicoil-0shot-v2.cached.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage >& logs/log.dl22-passage.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage.d2q-t5 >& logs/log.dl22-passage.d2q-t5.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage.unicoil-noexp-0shot.cached >& logs/log.dl22-passage.unicoil-noexp-0shot.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage.unicoil-0shot.cached >& logs/log.dl22-passage.unicoil-0shot.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage.splade-pp-ed.cached >& logs/log.dl22-passage.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage.splade-pp-ed.onnx >& logs/log.dl22-passage.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage.splade-pp-sd.cached >& logs/log.dl22-passage.splade-pp-sd.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage.splade-pp-sd.onnx >& logs/log.dl22-passage.splade-pp-sd.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage-augmented >& logs/log.dl22-passage-augmented.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-passage-augmented.d2q-t5 >& logs/log.dl22-passage-augmented.d2q-t5.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-doc >& logs/log.dl22-doc.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-doc.d2q-t5 >& logs/log.dl22-doc.d2q-t5.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-doc-segmented >& logs/log.dl22-doc-segmented.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-doc-segmented.d2q-t5 >& logs/log.dl22-doc-segmented.d2q-t5.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-doc-segmented.unicoil-noexp-0shot-v2.cached >& logs/log.dl22-doc-segmented.unicoil-noexp-0shot-v2.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-doc-segmented.unicoil-0shot-v2.cached >& logs/log.dl22-doc-segmented.unicoil-0shot-v2.cached.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage >& logs/log.dl23-passage.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage.d2q-t5 >& logs/log.dl23-passage.d2q-t5.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage.unicoil-noexp-0shot.cached >& logs/log.dl23-passage.unicoil-noexp-0shot.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage.unicoil-0shot.cached >& logs/log.dl23-passage.unicoil-0shot.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage.splade-pp-ed.cached >& logs/log.dl23-passage.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage.splade-pp-ed.onnx >& logs/log.dl23-passage.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage.splade-pp-sd.cached >& logs/log.dl23-passage.splade-pp-sd.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage.splade-pp-sd.onnx >& logs/log.dl23-passage.splade-pp-sd.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage-augmented >& logs/log.dl23-passage-augmented.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-passage-augmented.d2q-t5 >& logs/log.dl23-passage-augmented.d2q-t5.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-doc >& logs/log.dl23-doc.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-doc.d2q-t5 >& logs/log.dl23-doc.d2q-t5.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-doc-segmented >& logs/log.dl23-doc-segmented.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-doc-segmented.d2q-t5 >& logs/log.dl23-doc-segmented.d2q-t5.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-doc-segmented.unicoil-noexp-0shot-v2.cached >& logs/log.dl23-doc-segmented.unicoil-noexp-0shot-v2.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-doc-segmented.unicoil-0shot-v2.cached >& logs/log.dl23-doc-segmented.unicoil-0shot-v2.cached.txt &
```

</details>
<details>
<summary>MS MARCO V2.1 + RAG24 regressions</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression rag24-doc-segmented-test >& logs/log.rag24-doc-segmented-test.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2.1-doc >& logs/log.msmarco-v2.1-doc.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2.1-doc-segmented >& logs/log.msmarco-v2.1-doc-segmented.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-msmarco-v2.1 >& logs/log.dl21-doc-msmarco-v2.1.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented-msmarco-v2.1 >& logs/log.dl21-doc-segmented-msmarco-v2.1.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-doc-msmarco-v2.1 >& logs/log.dl22-doc-msmarco-v2.1.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl22-doc-segmented-msmarco-v2.1 >& logs/log.dl22-doc-segmented-msmarco-v2.1.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-doc-msmarco-v2.1 >& logs/log.dl23-doc-msmarco-v2.1.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression dl23-doc-segmented-msmarco-v2.1 >& logs/log.dl23-doc-segmented-msmarco-v2.1.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression rag24-doc-raggy-dev >& logs/log.rag24-doc-raggy-dev.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression rag24-doc-segmented-raggy-dev >& logs/log.rag24-doc-segmented-raggy-dev.txt &
```

</details>
<details>
<summary>BEIR (v1.0.0): BGE-base-en-v1.5</summary>

Flat indexes:

```bash
# Original flat indexes, cached queries
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat.cached >& logs/log.beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat.cached.txt &

# Original flat indexes, ONNX
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat.onnx >& logs/log.beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat.onnx.txt &

# Quantized flat indexes, cached queries
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat-int8.cached >& logs/log.beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat-int8.cached.txt &

# Quantized flat indexes, ONNX
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat-int8.onnx >& logs/log.beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat-int8.onnx.txt &
```

HNSW indexes:

```bash
# Original HNSW indexes, cached queries
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw.cached >& logs/log.beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw.cached.txt &

# Original HNSW indexes, ONNX
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw.onnx >& logs/log.beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw.onnx.txt &

# Quantized HNSW indexes, cached queries
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw-int8.cached >& logs/log.beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw-int8.cached.txt &

# Quantized HNSW indexes, ONNX
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw-int8.onnx >& logs/log.beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw-int8.onnx.txt &
```

</details>
<details>
<summary>BEIR (v1.0.0): SPLADE++ CoCondenser-EnsembleDistil</summary>

```bash
# Cached queries
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid.splade-pp-ed.cached >& logs/log.beir-v1.0.0-trec-covid.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.splade-pp-ed.cached >& logs/log.beir-v1.0.0-bioasq.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus.splade-pp-ed.cached >& logs/log.beir-v1.0.0-nfcorpus.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.splade-pp-ed.cached >& logs/log.beir-v1.0.0-nq.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa.splade-pp-ed.cached >& logs/log.beir-v1.0.0-hotpotqa.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa.splade-pp-ed.cached >& logs/log.beir-v1.0.0-fiqa.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.splade-pp-ed.cached >& logs/log.beir-v1.0.0-signal1m.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news.splade-pp-ed.cached >& logs/log.beir-v1.0.0-trec-news.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04.splade-pp-ed.cached >& logs/log.beir-v1.0.0-robust04.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.splade-pp-ed.cached >& logs/log.beir-v1.0.0-arguana.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.splade-pp-ed.cached >& logs/log.beir-v1.0.0-webis-touche2020.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android.splade-pp-ed.cached >& logs/log.beir-v1.0.0-cqadupstack-android.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english.splade-pp-ed.cached >& logs/log.beir-v1.0.0-cqadupstack-english.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.cached >& logs/log.beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis.splade-pp-ed.cached >& logs/log.beir-v1.0.0-cqadupstack-gis.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.cached >& logs/log.beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics.splade-pp-ed.cached >& logs/log.beir-v1.0.0-cqadupstack-physics.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.cached >& logs/log.beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats.splade-pp-ed.cached >& logs/log.beir-v1.0.0-cqadupstack-stats.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex.splade-pp-ed.cached >& logs/log.beir-v1.0.0-cqadupstack-tex.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix.splade-pp-ed.cached >& logs/log.beir-v1.0.0-cqadupstack-unix.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.cached >& logs/log.beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.cached >& logs/log.beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora.splade-pp-ed.cached >& logs/log.beir-v1.0.0-quora.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity.splade-pp-ed.cached >& logs/log.beir-v1.0.0-dbpedia-entity.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs.splade-pp-ed.cached >& logs/log.beir-v1.0.0-scidocs.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever.splade-pp-ed.cached >& logs/log.beir-v1.0.0-fever.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.splade-pp-ed.cached >& logs/log.beir-v1.0.0-climate-fever.splade-pp-ed.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact.splade-pp-ed.cached >& logs/log.beir-v1.0.0-scifact.splade-pp-ed.cached.txt &

# ONNX
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-trec-covid.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-bioasq.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-nfcorpus.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-nq.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-hotpotqa.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-fiqa.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-signal1m.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-trec-news.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-robust04.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-arguana.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-webis-touche2020.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-cqadupstack-android.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-cqadupstack-english.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-cqadupstack-gis.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-cqadupstack-physics.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-cqadupstack-stats.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-cqadupstack-tex.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-cqadupstack-unix.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-quora.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-dbpedia-entity.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-scidocs.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-fever.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-climate-fever.splade-pp-ed.onnx.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact.splade-pp-ed.onnx >& logs/log.beir-v1.0.0-scifact.splade-pp-ed.onnx.txt &
```

</details>
<details>
<summary>BEIR (v1.0.0): uniCOIL (noexp)</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid.unicoil-noexp.cached >& logs/log.beir-v1.0.0-trec-covid.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.unicoil-noexp.cached >& logs/log.beir-v1.0.0-bioasq.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus.unicoil-noexp.cached >& logs/log.beir-v1.0.0-nfcorpus.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.unicoil-noexp.cached >& logs/log.beir-v1.0.0-nq.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa.unicoil-noexp.cached >& logs/log.beir-v1.0.0-hotpotqa.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa.unicoil-noexp.cached >& logs/log.beir-v1.0.0-fiqa.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.unicoil-noexp.cached >& logs/log.beir-v1.0.0-signal1m.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news.unicoil-noexp.cached >& logs/log.beir-v1.0.0-trec-news.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04.unicoil-noexp.cached >& logs/log.beir-v1.0.0-robust04.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.unicoil-noexp.cached >& logs/log.beir-v1.0.0-arguana.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.unicoil-noexp.cached >& logs/log.beir-v1.0.0-webis-touche2020.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android.unicoil-noexp.cached >& logs/log.beir-v1.0.0-cqadupstack-android.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english.unicoil-noexp.cached >& logs/log.beir-v1.0.0-cqadupstack-english.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming.unicoil-noexp.cached >& logs/log.beir-v1.0.0-cqadupstack-gaming.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis.unicoil-noexp.cached >& logs/log.beir-v1.0.0-cqadupstack-gis.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica.unicoil-noexp.cached >& logs/log.beir-v1.0.0-cqadupstack-mathematica.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics.unicoil-noexp.cached >& logs/log.beir-v1.0.0-cqadupstack-physics.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers.unicoil-noexp.cached >& logs/log.beir-v1.0.0-cqadupstack-programmers.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats.unicoil-noexp.cached >& logs/log.beir-v1.0.0-cqadupstack-stats.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex.unicoil-noexp.cached >& logs/log.beir-v1.0.0-cqadupstack-tex.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix.unicoil-noexp.cached >& logs/log.beir-v1.0.0-cqadupstack-unix.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters.unicoil-noexp.cached >& logs/log.beir-v1.0.0-cqadupstack-webmasters.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress.unicoil-noexp.cached >& logs/log.beir-v1.0.0-cqadupstack-wordpress.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora.unicoil-noexp.cached >& logs/log.beir-v1.0.0-quora.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity.unicoil-noexp.cached >& logs/log.beir-v1.0.0-dbpedia-entity.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs.unicoil-noexp.cached >& logs/log.beir-v1.0.0-scidocs.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever.unicoil-noexp.cached >& logs/log.beir-v1.0.0-fever.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.unicoil-noexp.cached >& logs/log.beir-v1.0.0-climate-fever.unicoil-noexp.cached.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact.unicoil-noexp.cached >& logs/log.beir-v1.0.0-scifact.unicoil-noexp.cached.txt &
```

</details>
<details>
<summary>BEIR (v1.0.0): "flat" baseline</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid.flat >& logs/log.beir-v1.0.0-trec-covid.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.flat >& logs/log.beir-v1.0.0-bioasq.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus.flat >& logs/log.beir-v1.0.0-nfcorpus.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.flat >& logs/log.beir-v1.0.0-nq.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa.flat >& logs/log.beir-v1.0.0-hotpotqa.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa.flat >& logs/log.beir-v1.0.0-fiqa.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.flat >& logs/log.beir-v1.0.0-signal1m.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news.flat >& logs/log.beir-v1.0.0-trec-news.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04.flat >& logs/log.beir-v1.0.0-robust04.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.flat >& logs/log.beir-v1.0.0-arguana.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.flat >& logs/log.beir-v1.0.0-webis-touche2020.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android.flat >& logs/log.beir-v1.0.0-cqadupstack-android.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english.flat >& logs/log.beir-v1.0.0-cqadupstack-english.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming.flat >& logs/log.beir-v1.0.0-cqadupstack-gaming.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis.flat >& logs/log.beir-v1.0.0-cqadupstack-gis.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica.flat >& logs/log.beir-v1.0.0-cqadupstack-mathematica.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics.flat >& logs/log.beir-v1.0.0-cqadupstack-physics.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers.flat >& logs/log.beir-v1.0.0-cqadupstack-programmers.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats.flat >& logs/log.beir-v1.0.0-cqadupstack-stats.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex.flat >& logs/log.beir-v1.0.0-cqadupstack-tex.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix.flat >& logs/log.beir-v1.0.0-cqadupstack-unix.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters.flat >& logs/log.beir-v1.0.0-cqadupstack-webmasters.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress.flat >& logs/log.beir-v1.0.0-cqadupstack-wordpress.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora.flat >& logs/log.beir-v1.0.0-quora.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity.flat >& logs/log.beir-v1.0.0-dbpedia-entity.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs.flat >& logs/log.beir-v1.0.0-scidocs.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever.flat >& logs/log.beir-v1.0.0-fever.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.flat >& logs/log.beir-v1.0.0-climate-fever.flat.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact.flat >& logs/log.beir-v1.0.0-scifact.flat.txt &
```

</details>
<details>
<summary>BEIR (v1.0.0): "flat" baseline with WordPiece tokenization</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid.flat-wp >& logs/log.beir-v1.0.0-trec-covid.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.flat-wp >& logs/log.beir-v1.0.0-bioasq.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus.flat-wp >& logs/log.beir-v1.0.0-nfcorpus.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.flat-wp >& logs/log.beir-v1.0.0-nq.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa.flat-wp >& logs/log.beir-v1.0.0-hotpotqa.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa.flat-wp >& logs/log.beir-v1.0.0-fiqa.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.flat-wp >& logs/log.beir-v1.0.0-signal1m.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news.flat-wp >& logs/log.beir-v1.0.0-trec-news.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04.flat-wp >& logs/log.beir-v1.0.0-robust04.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.flat-wp >& logs/log.beir-v1.0.0-arguana.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.flat-wp >& logs/log.beir-v1.0.0-webis-touche2020.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android.flat-wp >& logs/log.beir-v1.0.0-cqadupstack-android.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english.flat-wp >& logs/log.beir-v1.0.0-cqadupstack-english.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming.flat-wp >& logs/log.beir-v1.0.0-cqadupstack-gaming.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis.flat-wp >& logs/log.beir-v1.0.0-cqadupstack-gis.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica.flat-wp >& logs/log.beir-v1.0.0-cqadupstack-mathematica.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics.flat-wp >& logs/log.beir-v1.0.0-cqadupstack-physics.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers.flat-wp >& logs/log.beir-v1.0.0-cqadupstack-programmers.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats.flat-wp >& logs/log.beir-v1.0.0-cqadupstack-stats.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex.flat-wp >& logs/log.beir-v1.0.0-cqadupstack-tex.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix.flat-wp >& logs/log.beir-v1.0.0-cqadupstack-unix.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters.flat-wp >& logs/log.beir-v1.0.0-cqadupstack-webmasters.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress.flat-wp >& logs/log.beir-v1.0.0-cqadupstack-wordpress.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora.flat-wp >& logs/log.beir-v1.0.0-quora.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity.flat-wp >& logs/log.beir-v1.0.0-dbpedia-entity.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs.flat-wp >& logs/log.beir-v1.0.0-scidocs.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever.flat-wp >& logs/log.beir-v1.0.0-fever.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.flat-wp >& logs/log.beir-v1.0.0-climate-fever.flat-wp.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact.flat-wp >& logs/log.beir-v1.0.0-scifact.flat-wp.txt &
```

</details>
<details>
<summary>BEIR (v1.0.0): "multifield" baseline</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-covid.multifield >& logs/log.beir-v1.0.0-trec-covid.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-bioasq.multifield >& logs/log.beir-v1.0.0-bioasq.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nfcorpus.multifield >& logs/log.beir-v1.0.0-nfcorpus.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-nq.multifield >& logs/log.beir-v1.0.0-nq.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-hotpotqa.multifield >& logs/log.beir-v1.0.0-hotpotqa.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fiqa.multifield >& logs/log.beir-v1.0.0-fiqa.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-signal1m.multifield >& logs/log.beir-v1.0.0-signal1m.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-trec-news.multifield >& logs/log.beir-v1.0.0-trec-news.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-robust04.multifield >& logs/log.beir-v1.0.0-robust04.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-arguana.multifield >& logs/log.beir-v1.0.0-arguana.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-webis-touche2020.multifield >& logs/log.beir-v1.0.0-webis-touche2020.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-android.multifield >& logs/log.beir-v1.0.0-cqadupstack-android.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-english.multifield >& logs/log.beir-v1.0.0-cqadupstack-english.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gaming.multifield >& logs/log.beir-v1.0.0-cqadupstack-gaming.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-gis.multifield >& logs/log.beir-v1.0.0-cqadupstack-gis.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-mathematica.multifield >& logs/log.beir-v1.0.0-cqadupstack-mathematica.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-physics.multifield >& logs/log.beir-v1.0.0-cqadupstack-physics.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-programmers.multifield >& logs/log.beir-v1.0.0-cqadupstack-programmers.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-stats.multifield >& logs/log.beir-v1.0.0-cqadupstack-stats.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-tex.multifield >& logs/log.beir-v1.0.0-cqadupstack-tex.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-unix.multifield >& logs/log.beir-v1.0.0-cqadupstack-unix.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-webmasters.multifield >& logs/log.beir-v1.0.0-cqadupstack-webmasters.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-cqadupstack-wordpress.multifield >& logs/log.beir-v1.0.0-cqadupstack-wordpress.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-quora.multifield >& logs/log.beir-v1.0.0-quora.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-dbpedia-entity.multifield >& logs/log.beir-v1.0.0-dbpedia-entity.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scidocs.multifield >& logs/log.beir-v1.0.0-scidocs.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-fever.multifield >& logs/log.beir-v1.0.0-fever.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-climate-fever.multifield >& logs/log.beir-v1.0.0-climate-fever.multifield.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression beir-v1.0.0-scifact.multifield >& logs/log.beir-v1.0.0-scifact.multifield.txt &
```

</details>
<details>
<summary>Mr.TyDi (v1.1): BM25 regressions</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ar >& logs/log.mrtydi-v1.1-ar.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-bn >& logs/log.mrtydi-v1.1-bn.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-en >& logs/log.mrtydi-v1.1-en.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-fi >& logs/log.mrtydi-v1.1-fi.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-id >& logs/log.mrtydi-v1.1-id.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ja >& logs/log.mrtydi-v1.1-ja.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ko >& logs/log.mrtydi-v1.1-ko.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ru >& logs/log.mrtydi-v1.1-ru.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-sw >& logs/log.mrtydi-v1.1-sw.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-te >& logs/log.mrtydi-v1.1-te.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-th >& logs/log.mrtydi-v1.1-th.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ar-aca >& logs/log.mrtydi-v1.1-ar-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-bn-aca >& logs/log.mrtydi-v1.1-bn-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-en-aca >& logs/log.mrtydi-v1.1-en-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-fi-aca >& logs/log.mrtydi-v1.1-fi-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-id-aca >& logs/log.mrtydi-v1.1-id-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ja-aca >& logs/log.mrtydi-v1.1-ja-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ko-aca >& logs/log.mrtydi-v1.1-ko-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-ru-aca >& logs/log.mrtydi-v1.1-ru-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-sw-aca >& logs/log.mrtydi-v1.1-sw-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-te-aca >& logs/log.mrtydi-v1.1-te-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-th-aca >& logs/log.mrtydi-v1.1-th-aca.txt &
```

</details>
<details>
<summary>MIRACL (v1.0): BM25 regressions</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ar >& logs/log.miracl-v1.0-ar.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-bn >& logs/log.miracl-v1.0-bn.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-en >& logs/log.miracl-v1.0-en.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-es >& logs/log.miracl-v1.0-es.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-fa >& logs/log.miracl-v1.0-fa.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-fi >& logs/log.miracl-v1.0-fi.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-fr >& logs/log.miracl-v1.0-fr.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-hi >& logs/log.miracl-v1.0-hi.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-id >& logs/log.miracl-v1.0-id.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ja >& logs/log.miracl-v1.0-ja.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ko >& logs/log.miracl-v1.0-ko.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ru >& logs/log.miracl-v1.0-ru.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-sw >& logs/log.miracl-v1.0-sw.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-te >& logs/log.miracl-v1.0-te.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-th >& logs/log.miracl-v1.0-th.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-zh >& logs/log.miracl-v1.0-zh.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ar-aca >& logs/log.miracl-v1.0-ar-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-bn-aca >& logs/log.miracl-v1.0-bn-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-en-aca >& logs/log.miracl-v1.0-en-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-es-aca >& logs/log.miracl-v1.0-es-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-fa-aca >& logs/log.miracl-v1.0-fa-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-fi-aca >& logs/log.miracl-v1.0-fi-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-fr-aca >& logs/log.miracl-v1.0-fr-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-hi-aca >& logs/log.miracl-v1.0-hi-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-id-aca >& logs/log.miracl-v1.0-id-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ja-aca >& logs/log.miracl-v1.0-ja-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ko-aca >& logs/log.miracl-v1.0-ko-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-ru-aca >& logs/log.miracl-v1.0-ru-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-sw-aca >& logs/log.miracl-v1.0-sw-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-te-aca >& logs/log.miracl-v1.0-te-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-th-aca >& logs/log.miracl-v1.0-th-aca.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression miracl-v1.0-zh-aca >& logs/log.miracl-v1.0-zh-aca.txt &
```

</details>
<details>
<summary>Other cross-lingual and multi-lingual regressions</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression ntcir8-zh >& logs/log.ntcir8-zh.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression clef06-fr >& logs/log.clef06-fr.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression trec02-ar >& logs/log.trec02-ar.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression fire12-bn >& logs/log.fire12-bn.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression fire12-hi >& logs/log.fire12-hi.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression fire12-en >& logs/log.fire12-en.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-v1.0-fa >& logs/log.hc4-v1.0-fa.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-v1.0-ru >& logs/log.hc4-v1.0-ru.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-v1.0-zh >& logs/log.hc4-v1.0-zh.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-fa >& logs/log.hc4-neuclir22-fa.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-ru >& logs/log.hc4-neuclir22-ru.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-zh >& logs/log.hc4-neuclir22-zh.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-fa-en >& logs/log.hc4-neuclir22-fa-en.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-ru-en >& logs/log.hc4-neuclir22-ru-en.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression hc4-neuclir22-zh-en >& logs/log.hc4-neuclir22-zh-en.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-qt >& logs/log.neuclir22-fa-qt.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-dt >& logs/log.neuclir22-fa-dt.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-qt >& logs/log.neuclir22-ru-qt.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-dt >& logs/log.neuclir22-ru-dt.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-qt >& logs/log.neuclir22-zh-qt.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-dt >& logs/log.neuclir22-zh-dt.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-qt-splade >& logs/log.neuclir22-fa-qt-splade.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-dt-splade >& logs/log.neuclir22-fa-dt-splade.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-qt-splade >& logs/log.neuclir22-ru-qt-splade.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-dt-splade >& logs/log.neuclir22-ru-dt-splade.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-qt-splade >& logs/log.neuclir22-zh-qt-splade.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-dt-splade >& logs/log.neuclir22-zh-dt-splade.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-ha >& logs/log.ciral-v1.0-ha.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-so >& logs/log.ciral-v1.0-so.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-sw >& logs/log.ciral-v1.0-sw.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-yo >& logs/log.ciral-v1.0-yo.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-ha-en >& logs/log.ciral-v1.0-ha-en.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-so-en >& logs/log.ciral-v1.0-so-en.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-sw-en >& logs/log.ciral-v1.0-sw-en.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression ciral-v1.0-yo-en >& logs/log.ciral-v1.0-yo-en.txt &
```

</details>
<details>
<summary>Other regressions</summary>

```bash
nohup python src/main/python/run_regression.py --index --verify --search --regression backgroundlinking18 >& logs/log.backgroundlinking18.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression backgroundlinking19 >& logs/log.backgroundlinking19.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression backgroundlinking20 >& logs/log.backgroundlinking20.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression disk12 >& logs/log.disk12.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression disk45 >& logs/log.disk45.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression robust05 >& logs/log.robust05.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression core17 >& logs/log.core17.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression core18 >& logs/log.core18.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression mb11 >& logs/log.mb11.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression mb13 >& logs/log.mb13.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression car17v1.5 >& logs/log.car17v1.5.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression car17v2.0 >& logs/log.car17v2.0.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression car17v2.0-doc2query >& logs/log.car17v2.0-doc2query.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression wt10g >& logs/log.wt10g.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression gov2 >& logs/log.gov2.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression cw09b >& logs/log.cw09b.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression cw12b13 >& logs/log.cw12b13.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression cw12 >& logs/log.cw12.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression fever >& logs/log.fever.txt &

nohup python src/main/python/run_regression.py --index --verify --search --regression wikipedia-dpr-100w-bm25 >& logs/log.wikipedia-dpr-100w-bm25.txt &
nohup python src/main/python/run_regression.py --index --verify --search --regression wiki-all-6-3-tamber-bm25 >& logs/log.wiki-all-6-3-tamber-bm25.txt &
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
