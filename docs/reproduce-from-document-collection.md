# Anserini: Reproduce Experimental Results from Document Collections

Reproduction experiments in Anserini are coordinated by a rigorous end-to-end framework implemented in [`io.anserini.reproduce.ReproduceFromDocumentCollection`](../src/main/java/io/anserini/reproduce/ReproduceFromDocumentCollection.java).
The driver automatically runs experiments based on configuration files stored in [`src/main/resources/reproduce/from-document-collection/configs`](../src/main/resources/reproduce/from-document-collection/configs), performing the following actions:

+ Building the index from scratch (i.e., from the raw document collection).
+ Verifying index statistics (sanity check that the index has been built properly).
+ Performing retrieval runs with standard settings.
+ Evaluating the runs and verifying effectiveness results.

Furthermore, documentation pages are auto-generated based on [raw templates](../src/main/resources/reproduce/from-document-collection/docgen).

Internally at Waterloo, we are continuously running these regression tests to ensure that new commits do not break any existing experimental runs (see below).
We keep a [change log](regressions-log.md) to document substantive changes.

## The Anserini Reproducibility Promise

It is the highest priority of the project to ensure that all results are reproducible _all the time_.
This means that anyone with the document collection should be able to reproduce _exactly_ the effectiveness scores we report in our documentation pages.

We hold this ideal in such high esteem and are so dedicated to reproducibility that if you discover a broken regression before we do, Jimmy Lin will buy you a beverage of choice (coffee, beer, etc.) at the next event you see him (e.g., SIGIR, TREC, etc.).

## Invocations

Internally at Waterloo, we have two machines (`tuna.cs.uwaterloo.ca` and `orca.cs.uwaterloo.ca`) for the development of Anserini and is set up to run the regression experiments.

Sample invocation:

```
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config cacm
```

The following configurations (i.e., `--config` settings) are available:

<details>
<summary>CACM tests</summary>

+ [`cacm`](../src/main/resources/reproduce/from-document-collection/configs/cacm.yaml)
+ [`cacm-download`](../src/main/resources/reproduce/from-document-collection/configs/cacm-download.yaml)

</details>
<details>
<summary>MS MARCO V1 + DL19/DL20 regressions</summary>

+ [`msmarco-v1-passage`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.yaml)
+ [`msmarco-v1-passage.bm25-b8`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bm25-b8.yaml)
+ [`msmarco-v1-passage.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.wp-tok.yaml)
+ [`msmarco-v1-passage.wp-hgf`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.wp-hgf.yaml)
+ [`msmarco-v1-passage.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.wp-ca.yaml)
+ [`msmarco-v1-passage.doc2query`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.doc2query.yaml)
+ [`msmarco-v1-passage.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.docTTTTTquery.yaml)


<div></div>

+ [`msmarco-v1-passage.deepimpact.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.deepimpact.cached.yaml)
+ [`msmarco-v1-passage.unicoil.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.unicoil.cached.yaml)
+ [`msmarco-v1-passage.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.unicoil-noexp.cached.yaml)
+ [`msmarco-v1-passage.unicoil-tilde-expansion.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.unicoil-tilde-expansion.cached.yaml)
+ [`msmarco-v1-passage.distill-splade-max.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.distill-splade-max.cached.yaml)
+ [`msmarco-v1-passage.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.splade-pp-ed.cached.yaml)
+ [`msmarco-v1-passage.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.splade-pp-ed.onnx.yaml)
+ [`msmarco-v1-passage.splade-pp-sd.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.splade-pp-sd.cached.yaml)
+ [`msmarco-v1-passage.splade-pp-sd.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.splade-pp-sd.onnx.yaml)
+ [`msmarco-v1-passage.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.splade-v3.onnx.yaml)
+ [`msmarco-v1-passage.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.splade-v3.cached.yaml)


<div></div>

+ [`msmarco-v1-passage.cos-dpr-distil.parquet.fw`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.fw.yaml)
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.lexlsh`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.lexlsh.yaml)
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.flat.cached.yaml)
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.flat.onnx.yaml)
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.flat-int8.cached.yaml)
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.flat-int8.onnx.yaml)
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.hnsw.cached.yaml)
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.hnsw.onnx.yaml)
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.hnsw-int8.cached.yaml)
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.hnsw-int8.onnx.yaml)
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`msmarco-v1-passage.openai-ada2.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.openai-ada2.parquet.flat.cached.yaml)
+ [`msmarco-v1-passage.openai-ada2.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.openai-ada2.parquet.flat-int8.cached.yaml)
+ [`msmarco-v1-passage.openai-ada2.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.openai-ada2.parquet.hnsw.cached.yaml)
+ [`msmarco-v1-passage.openai-ada2.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.openai-ada2.parquet.hnsw-int8.cached.yaml)
+ [`msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat.cached.yaml)
+ [`msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat-int8.cached.yaml)
+ [`msmarco-v1-passage.cohere-embed-english-v3.0.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cohere-embed-english-v3.0.parquet.hnsw.cached.yaml)
+ [`msmarco-v1-passage.cohere-embed-english-v3.0.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cohere-embed-english-v3.0.parquet.hnsw-int8.cached.yaml)


<div></div>

+ [`msmarco-v1-doc`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc.yaml)
+ [`msmarco-v1-doc.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc.wp-tok.yaml)
+ [`msmarco-v1-doc.wp-hgf`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc.wp-hgf.yaml)
+ [`msmarco-v1-doc.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc.wp-ca.yaml)
+ [`msmarco-v1-doc.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc.docTTTTTquery.yaml)


<div></div>

+ [`msmarco-v1-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc-segmented.yaml)
+ [`msmarco-v1-doc-segmented.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc-segmented.wp-tok.yaml)
+ [`msmarco-v1-doc-segmented.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc-segmented.wp-ca.yaml)
+ [`msmarco-v1-doc-segmented.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc-segmented.docTTTTTquery.yaml)
+ [`msmarco-v1-doc-segmented.unicoil.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc-segmented.unicoil.cached.yaml)
+ [`msmarco-v1-doc-segmented.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc-segmented.unicoil-noexp.cached.yaml)


<div></div>

+ [`dl19-passage`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.yaml)
+ [`dl19-passage.bm25-b8`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bm25-b8.yaml)
+ [`dl19-passage.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.wp-tok.yaml)
+ [`dl19-passage.wp-hgf`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.wp-hgf.yaml)
+ [`dl19-passage.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.wp-ca.yaml)
+ [`dl19-passage.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.docTTTTTquery.yaml)


<div></div>

+ [`dl19-passage.unicoil.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.unicoil.cached.yaml)
+ [`dl19-passage.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.unicoil-noexp.cached.yaml)
+ [`dl19-passage.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.splade-pp-ed.cached.yaml)
+ [`dl19-passage.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.splade-pp-ed.onnx.yaml)
+ [`dl19-passage.splade-pp-sd.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.splade-pp-sd.cached.yaml)
+ [`dl19-passage.splade-pp-sd.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.splade-pp-sd.onnx.yaml)
+ [`dl19-passage.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.splade-v3.onnx.yaml)
+ [`dl19-passage.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.splade-v3.cached.yaml)


<div></div>

+ [`dl19-passage.cos-dpr-distil.parquet.fw`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.fw.yaml)
+ [`dl19-passage.cos-dpr-distil.parquet.lexlsh`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.lexlsh.yaml)
+ [`dl19-passage.cos-dpr-distil.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.flat.cached.yaml)
+ [`dl19-passage.cos-dpr-distil.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.flat.onnx.yaml)
+ [`dl19-passage.cos-dpr-distil.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.flat-int8.cached.yaml)
+ [`dl19-passage.cos-dpr-distil.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.flat-int8.onnx.yaml)
+ [`dl19-passage.cos-dpr-distil.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.hnsw.cached.yaml)
+ [`dl19-passage.cos-dpr-distil.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.hnsw.onnx.yaml)
+ [`dl19-passage.cos-dpr-distil.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.hnsw-int8.cached.yaml)
+ [`dl19-passage.cos-dpr-distil.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.hnsw-int8.onnx.yaml)
+ [`dl19-passage.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`dl19-passage.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`dl19-passage.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`dl19-passage.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`dl19-passage.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`dl19-passage.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`dl19-passage.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`dl19-passage.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`dl19-passage.openai-ada2.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.openai-ada2.parquet.flat.cached.yaml)
+ [`dl19-passage.openai-ada2.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.openai-ada2.parquet.flat-int8.cached.yaml)
+ [`dl19-passage.openai-ada2.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.openai-ada2.parquet.hnsw.cached.yaml)
+ [`dl19-passage.openai-ada2.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.openai-ada2.parquet.hnsw-int8.cached.yaml)
+ [`dl19-passage.cohere-embed-english-v3.0.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cohere-embed-english-v3.0.parquet.flat.cached.yaml)
+ [`dl19-passage.cohere-embed-english-v3.0.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cohere-embed-english-v3.0.parquet.flat-int8.cached.yaml)
+ [`dl19-passage.cohere-embed-english-v3.0.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cohere-embed-english-v3.0.parquet.hnsw.cached.yaml)
+ [`dl19-passage.cohere-embed-english-v3.0.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cohere-embed-english-v3.0.parquet.hnsw-int8.cached.yaml)


<div></div>

+ [`dl19-doc`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc.yaml)
+ [`dl19-doc.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc.wp-tok.yaml)
+ [`dl19-doc.wp-hgf`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc.wp-hgf.yaml)
+ [`dl19-doc.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc.wp-ca.yaml)
+ [`dl19-doc.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc.docTTTTTquery.yaml)


<div></div>

+ [`dl19-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc-segmented.yaml)
+ [`dl19-doc-segmented.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc-segmented.wp-tok.yaml)
+ [`dl19-doc-segmented.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc-segmented.wp-ca.yaml)
+ [`dl19-doc-segmented.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc-segmented.docTTTTTquery.yaml)
+ [`dl19-doc-segmented.unicoil.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc-segmented.unicoil.cached.yaml)
+ [`dl19-doc-segmented.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc-segmented.unicoil-noexp.cached.yaml)


<div></div>

+ [`dl20-passage`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.yaml)
+ [`dl20-passage.bm25-b8`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bm25-b8.yaml)
+ [`dl20-passage.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.wp-tok.yaml)
+ [`dl20-passage.wp-hgf`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.wp-hgf.yaml)
+ [`dl20-passage.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.wp-ca.yaml)
+ [`dl20-passage.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.docTTTTTquery.yaml)


<div></div>

+ [`dl20-passage.unicoil.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.unicoil.cached.yaml)
+ [`dl20-passage.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.unicoil-noexp.cached.yaml)
+ [`dl20-passage.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.splade-pp-ed.cached.yaml)
+ [`dl20-passage.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.splade-pp-ed.onnx.yaml)
+ [`dl20-passage.splade-pp-sd.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.splade-pp-sd.cached.yaml)
+ [`dl20-passage.splade-pp-sd.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.splade-pp-sd.onnx.yaml)
+ [`dl20-passage.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.splade-v3.onnx.yaml)
+ [`dl20-passage.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.splade-v3.cached.yaml)


<div></div>

+ [`dl20-passage.cos-dpr-distil.parquet.fw`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.fw.yaml)
+ [`dl20-passage.cos-dpr-distil.parquet.lexlsh`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.lexlsh.yaml)
+ [`dl20-passage.cos-dpr-distil.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.flat.cached.yaml)
+ [`dl20-passage.cos-dpr-distil.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.flat.onnx.yaml)
+ [`dl20-passage.cos-dpr-distil.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.flat-int8.cached.yaml)
+ [`dl20-passage.cos-dpr-distil.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.flat-int8.onnx.yaml)
+ [`dl20-passage.cos-dpr-distil.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.hnsw.cached.yaml)
+ [`dl20-passage.cos-dpr-distil.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.hnsw.onnx.yaml)
+ [`dl20-passage.cos-dpr-distil.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.hnsw-int8.cached.yaml)
+ [`dl20-passage.cos-dpr-distil.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.hnsw-int8.onnx.yaml)
+ [`dl20-passage.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`dl20-passage.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`dl20-passage.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`dl20-passage.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`dl20-passage.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`dl20-passage.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`dl20-passage.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`dl20-passage.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`dl20-passage.openai-ada2.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.openai-ada2.parquet.flat.cached.yaml)
+ [`dl20-passage.openai-ada2.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.openai-ada2.parquet.flat-int8.cached.yaml)
+ [`dl20-passage.openai-ada2.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.openai-ada2.parquet.hnsw.cached.yaml)
+ [`dl20-passage.openai-ada2.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.openai-ada2.parquet.hnsw-int8.cached.yaml)
+ [`dl20-passage.cohere-embed-english-v3.0.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cohere-embed-english-v3.0.parquet.flat.cached.yaml)
+ [`dl20-passage.cohere-embed-english-v3.0.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cohere-embed-english-v3.0.parquet.flat-int8.cached.yaml)
+ [`dl20-passage.cohere-embed-english-v3.0.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cohere-embed-english-v3.0.parquet.hnsw.cached.yaml)
+ [`dl20-passage.cohere-embed-english-v3.0.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cohere-embed-english-v3.0.parquet.hnsw-int8.cached.yaml)


<div></div>

+ [`dl20-doc`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc.yaml)
+ [`dl20-doc.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc.wp-tok.yaml)
+ [`dl20-doc.wp-hgf`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc.wp-hgf.yaml)
+ [`dl20-doc.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc.wp-ca.yaml)
+ [`dl20-doc.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc.docTTTTTquery.yaml)


<div></div>

+ [`dl20-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc-segmented.yaml)
+ [`dl20-doc-segmented.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc-segmented.wp-tok.yaml)
+ [`dl20-doc-segmented.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc-segmented.wp-ca.yaml)
+ [`dl20-doc-segmented.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc-segmented.docTTTTTquery.yaml)
+ [`dl20-doc-segmented.unicoil.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc-segmented.unicoil.cached.yaml)
+ [`dl20-doc-segmented.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc-segmented.unicoil-noexp.cached.yaml)

</details>
<details>
<summary>MS MARCO V2 + DL21 regressions</summary>

+ [`msmarco-v2-passage`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.yaml)
+ [`msmarco-v2-passage.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.d2q-t5.yaml)
+ [`msmarco-v2-passage.unicoil-noexp-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.unicoil-noexp-0shot.cached.yaml)
+ [`msmarco-v2-passage.unicoil-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.unicoil-0shot.cached.yaml)
+ [`msmarco-v2-passage.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.splade-pp-ed.cached.yaml)
+ [`msmarco-v2-passage.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.splade-pp-ed.onnx.yaml)
+ [`msmarco-v2-passage.splade-pp-sd.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.splade-pp-sd.cached.yaml)
+ [`msmarco-v2-passage.splade-pp-sd.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.splade-pp-sd.onnx.yaml)
+ [`msmarco-v2-passage-augmented`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage-augmented.yaml)
+ [`msmarco-v2-passage-augmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage-augmented.d2q-t5.yaml)
+ [`msmarco-v2-doc`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-doc.yaml)
+ [`msmarco-v2-doc.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-doc.d2q-t5.yaml)
+ [`msmarco-v2-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-doc-segmented.yaml)
+ [`msmarco-v2-doc-segmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-doc-segmented.d2q-t5.yaml)
+ [`msmarco-v2-doc-segmented.unicoil-noexp-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-doc-segmented.unicoil-noexp-0shot-v2.cached.yaml)
+ [`msmarco-v2-doc-segmented.unicoil-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-doc-segmented.unicoil-0shot-v2.cached.yaml)


<div></div>

+ [`dl21-passage`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.yaml)
+ [`dl21-passage.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.d2q-t5.yaml)
+ [`dl21-passage.unicoil-noexp-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.unicoil-noexp-0shot.cached.yaml)
+ [`dl21-passage.unicoil-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.unicoil-0shot.cached.yaml)
+ [`dl21-passage.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.splade-pp-ed.cached.yaml)
+ [`dl21-passage.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.splade-pp-ed.onnx.yaml)
+ [`dl21-passage.splade-pp-sd.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.splade-pp-sd.cached.yaml)
+ [`dl21-passage.splade-pp-sd.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.splade-pp-sd.onnx.yaml)
+ [`dl21-passage-augmented`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage-augmented.yaml)
+ [`dl21-passage-augmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage-augmented.d2q-t5.yaml)
+ [`dl21-doc`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc.yaml)
+ [`dl21-doc.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc.d2q-t5.yaml)
+ [`dl21-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented.yaml)
+ [`dl21-doc-segmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented.d2q-t5.yaml)
+ [`dl21-doc-segmented.unicoil-noexp-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented.unicoil-noexp-0shot-v2.cached.yaml)
+ [`dl21-doc-segmented.unicoil-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented.unicoil-0shot-v2.cached.yaml)


<div></div>

+ [`dl22-passage`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.yaml)
+ [`dl22-passage.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.d2q-t5.yaml)
+ [`dl22-passage.unicoil-noexp-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.unicoil-noexp-0shot.cached.yaml)
+ [`dl22-passage.unicoil-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.unicoil-0shot.cached.yaml)
+ [`dl22-passage.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.splade-pp-ed.cached.yaml)
+ [`dl22-passage.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.splade-pp-ed.onnx.yaml)
+ [`dl22-passage.splade-pp-sd.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.splade-pp-sd.cached.yaml)
+ [`dl22-passage.splade-pp-sd.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.splade-pp-sd.onnx.yaml)
+ [`dl22-passage-augmented`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage-augmented.yaml)
+ [`dl22-passage-augmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage-augmented.d2q-t5.yaml)
+ [`dl22-doc`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc.yaml)
+ [`dl22-doc.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc.d2q-t5.yaml)
+ [`dl22-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-segmented.yaml)
+ [`dl22-doc-segmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-segmented.d2q-t5.yaml)
+ [`dl22-doc-segmented.unicoil-noexp-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-segmented.unicoil-noexp-0shot-v2.cached.yaml)
+ [`dl22-doc-segmented.unicoil-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-segmented.unicoil-0shot-v2.cached.yaml)


<div></div>

+ [`dl23-passage`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.yaml)
+ [`dl23-passage.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.d2q-t5.yaml)
+ [`dl23-passage.unicoil-noexp-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.unicoil-noexp-0shot.cached.yaml)
+ [`dl23-passage.unicoil-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.unicoil-0shot.cached.yaml)
+ [`dl23-passage.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.splade-pp-ed.cached.yaml)
+ [`dl23-passage.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.splade-pp-ed.onnx.yaml)
+ [`dl23-passage.splade-pp-sd.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.splade-pp-sd.cached.yaml)
+ [`dl23-passage.splade-pp-sd.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.splade-pp-sd.onnx.yaml)
+ [`dl23-passage-augmented`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage-augmented.yaml)
+ [`dl23-passage-augmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage-augmented.d2q-t5.yaml)
+ [`dl23-doc`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc.yaml)
+ [`dl23-doc.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc.d2q-t5.yaml)
+ [`dl23-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-segmented.yaml)
+ [`dl23-doc-segmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-segmented.d2q-t5.yaml)
+ [`dl23-doc-segmented.unicoil-noexp-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-segmented.unicoil-noexp-0shot-v2.cached.yaml)
+ [`dl23-doc-segmented.unicoil-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-segmented.unicoil-0shot-v2.cached.yaml)

</details>
<details>
<summary>MS MARCO V2.1 + RAG24 regressions</summary>

+ [`rag24-doc-segmented-test-umbrela`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.yaml)
+ [`rag24-doc-segmented-test-umbrela.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.splade-v3.onnx.yaml)
+ [`rag24-doc-segmented-test-umbrela.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.splade-v3.cached.yaml)
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard00.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard00.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard01.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard01.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard02.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard02.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard03.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard03.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard04.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard04.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard05.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard05.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard06.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard06.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard07.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard07.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard08.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard08.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard09.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard09.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-nist`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.yaml)
+ [`rag24-doc-segmented-test-nist.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.splade-v3.onnx.yaml)
+ [`rag24-doc-segmented-test-nist.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.splade-v3.cached.yaml)
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard00.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard00.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard01.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard01.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard02.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard02.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard03.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard03.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard04.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard04.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard05.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard05.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard06.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard06.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard07.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard07.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard08.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard08.flat.onnx.yaml)
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard09.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard09.flat.onnx.yaml)


<div></div>

+ [`rag25-doc-segmented-test-nist`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-nist.yaml)
+ [`rag25-doc-segmented-test-umbrela2`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.yaml)


<div></div>

+ [`msmarco-v2.1-doc`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2.1-doc.yaml)
+ [`msmarco-v2.1-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2.1-doc-segmented.yaml)
+ [`msmarco-v2.1-doc-segmented.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2.1-doc-segmented.splade-v3.onnx.yaml)
+ [`msmarco-v2.1-doc-segmented.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2.1-doc-segmented.splade-v3.cached.yaml)


<div></div>

+ [`dl21-doc-msmarco-v2.1`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-msmarco-v2.1.yaml)
+ [`dl21-doc-segmented-msmarco-v2.1`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented-msmarco-v2.1.yaml)
+ [`dl21-doc-segmented-msmarco-v2.1.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented-msmarco-v2.1.splade-v3.onnx.yaml)
+ [`dl21-doc-segmented-msmarco-v2.1.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented-msmarco-v2.1.splade-v3.cached.yaml)
+ [`dl22-doc-msmarco-v2.1`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-msmarco-v2.1.yaml)
+ [`dl22-doc-segmented-msmarco-v2.1`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-segmented-msmarco-v2.1.yaml)
+ [`dl22-doc-segmented-msmarco-v2.1.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-segmented-msmarco-v2.1.splade-v3.onnx.yaml)
+ [`dl22-doc-segmented-msmarco-v2.1.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-segmented-msmarco-v2.1.splade-v3.cached.yaml)
+ [`dl23-doc-msmarco-v2.1`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-msmarco-v2.1.yaml)
+ [`dl23-doc-segmented-msmarco-v2.1`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-segmented-msmarco-v2.1.yaml)
+ [`dl23-doc-segmented-msmarco-v2.1.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-segmented-msmarco-v2.1.splade-v3.onnx.yaml)
+ [`dl23-doc-segmented-msmarco-v2.1.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-segmented-msmarco-v2.1.splade-v3.cached.yaml)


<div></div>

+ [`rag24-doc-raggy-dev`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-raggy-dev.yaml)
+ [`rag24-doc-segmented-raggy-dev`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-raggy-dev.yaml)
+ [`rag24-doc-segmented-raggy-dev.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-raggy-dev.splade-v3.onnx.yaml)
+ [`rag24-doc-segmented-raggy-dev.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-raggy-dev.splade-v3.cached.yaml)

</details>
<details>
<summary>BEIR (v1.0.0): BGE-base-en-v1.5</summary>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat.cached.yaml)
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat.cached.yaml)


<div></div>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat.onnx.yaml)
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat.onnx.yaml)


<div></div>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat-int8.cached.yaml)


<div></div>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat-int8.onnx.yaml)


<div></div>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw.cached.yaml)
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw.cached.yaml)


<div></div>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw.onnx.yaml)


<div></div>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw-int8.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw-int8.cached.yaml)


<div></div>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw-int8.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw-int8.onnx.yaml)

</details>
<details>
<summary>BEIR (v1.0.0): SPLADE-v3</summary>

+ [`beir-v1.0.0-trec-covid.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.splade-v3.cached.yaml)
+ [`beir-v1.0.0-bioasq.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.splade-v3.cached.yaml)
+ [`beir-v1.0.0-nfcorpus.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.splade-v3.cached.yaml)
+ [`beir-v1.0.0-nq.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.splade-v3.cached.yaml)
+ [`beir-v1.0.0-hotpotqa.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.splade-v3.cached.yaml)
+ [`beir-v1.0.0-fiqa.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.splade-v3.cached.yaml)
+ [`beir-v1.0.0-signal1m.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.splade-v3.cached.yaml)
+ [`beir-v1.0.0-trec-news.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.splade-v3.cached.yaml)
+ [`beir-v1.0.0-robust04.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.splade-v3.cached.yaml)
+ [`beir-v1.0.0-arguana.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.splade-v3.cached.yaml)
+ [`beir-v1.0.0-webis-touche2020.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.splade-v3.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-android.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.splade-v3.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-english.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.splade-v3.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.splade-v3.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.splade-v3.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.splade-v3.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.splade-v3.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.splade-v3.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.splade-v3.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.splade-v3.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.splade-v3.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.splade-v3.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.splade-v3.cached.yaml)
+ [`beir-v1.0.0-quora.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.splade-v3.cached.yaml)
+ [`beir-v1.0.0-dbpedia-entity.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.splade-v3.cached.yaml)
+ [`beir-v1.0.0-scidocs.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.splade-v3.cached.yaml)
+ [`beir-v1.0.0-fever.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.splade-v3.cached.yaml)
+ [`beir-v1.0.0-climate-fever.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.splade-v3.cached.yaml)
+ [`beir-v1.0.0-scifact.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.splade-v3.cached.yaml)


<div></div>

+ [`beir-v1.0.0-trec-covid.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-bioasq.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-nfcorpus.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-nq.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-hotpotqa.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-fiqa.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-signal1m.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-trec-news.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-robust04.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-arguana.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-webis-touche2020.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-android.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-english.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-quora.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-dbpedia-entity.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-scidocs.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-fever.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-climate-fever.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.splade-v3.onnx.yaml)
+ [`beir-v1.0.0-scifact.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.splade-v3.onnx.yaml)

</details>
<details>
<summary>BEIR (v1.0.0): SPLADE++ CoCondenser-EnsembleDistil</summary>

+ [`beir-v1.0.0-trec-covid.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-bioasq.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-nfcorpus.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-nq.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-hotpotqa.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-fiqa.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-signal1m.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-trec-news.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-robust04.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-arguana.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-webis-touche2020.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-android.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-english.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-quora.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-dbpedia-entity.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-scidocs.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-fever.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-climate-fever.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.splade-pp-ed.cached.yaml)
+ [`beir-v1.0.0-scifact.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.splade-pp-ed.cached.yaml)


<div></div>

+ [`beir-v1.0.0-trec-covid.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-bioasq.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-nfcorpus.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-nq.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-hotpotqa.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-fiqa.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-signal1m.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-trec-news.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-robust04.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-arguana.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-webis-touche2020.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-android.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-english.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-quora.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-dbpedia-entity.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-scidocs.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-fever.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-climate-fever.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.splade-pp-ed.onnx.yaml)
+ [`beir-v1.0.0-scifact.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.splade-pp-ed.onnx.yaml)

</details>
<details>
<summary>BEIR (v1.0.0): uniCOIL (noexp)</summary>

+ [`beir-v1.0.0-trec-covid.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-bioasq.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-nfcorpus.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-nq.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-hotpotqa.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-fiqa.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-signal1m.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-trec-news.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-robust04.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-arguana.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-webis-touche2020.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-android.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-english.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-quora.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-dbpedia-entity.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-scidocs.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-fever.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-climate-fever.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.unicoil-noexp.cached.yaml)
+ [`beir-v1.0.0-scifact.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.unicoil-noexp.cached.yaml)

</details>
<details>
<summary>BEIR (v1.0.0): "flat" baseline</summary>

+ [`beir-v1.0.0-trec-covid.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.flat.yaml)
+ [`beir-v1.0.0-bioasq.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.flat.yaml)
+ [`beir-v1.0.0-nfcorpus.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.flat.yaml)
+ [`beir-v1.0.0-nq.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.flat.yaml)
+ [`beir-v1.0.0-hotpotqa.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.flat.yaml)
+ [`beir-v1.0.0-fiqa.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.flat.yaml)
+ [`beir-v1.0.0-signal1m.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.flat.yaml)
+ [`beir-v1.0.0-trec-news.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.flat.yaml)
+ [`beir-v1.0.0-robust04.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.flat.yaml)
+ [`beir-v1.0.0-arguana.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.flat.yaml)
+ [`beir-v1.0.0-webis-touche2020.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.flat.yaml)
+ [`beir-v1.0.0-cqadupstack-android.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.flat.yaml)
+ [`beir-v1.0.0-cqadupstack-english.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.flat.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.flat.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.flat.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.flat.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.flat.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.flat.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.flat.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.flat.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.flat.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.flat.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.flat.yaml)
+ [`beir-v1.0.0-quora.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.flat.yaml)
+ [`beir-v1.0.0-dbpedia-entity.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.flat.yaml)
+ [`beir-v1.0.0-scidocs.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.flat.yaml)
+ [`beir-v1.0.0-fever.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.flat.yaml)
+ [`beir-v1.0.0-climate-fever.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.flat.yaml)
+ [`beir-v1.0.0-scifact.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.flat.yaml)

</details>
<details>
<summary>BEIR (v1.0.0): "flat" baseline with WordPiece tokenization</summary>

+ [`beir-v1.0.0-trec-covid.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.flat-wp.yaml)
+ [`beir-v1.0.0-bioasq.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.flat-wp.yaml)
+ [`beir-v1.0.0-nfcorpus.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.flat-wp.yaml)
+ [`beir-v1.0.0-nq.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.flat-wp.yaml)
+ [`beir-v1.0.0-hotpotqa.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.flat-wp.yaml)
+ [`beir-v1.0.0-fiqa.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.flat-wp.yaml)
+ [`beir-v1.0.0-signal1m.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.flat-wp.yaml)
+ [`beir-v1.0.0-trec-news.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.flat-wp.yaml)
+ [`beir-v1.0.0-robust04.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.flat-wp.yaml)
+ [`beir-v1.0.0-arguana.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.flat-wp.yaml)
+ [`beir-v1.0.0-webis-touche2020.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.flat-wp.yaml)
+ [`beir-v1.0.0-cqadupstack-android.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.flat-wp.yaml)
+ [`beir-v1.0.0-cqadupstack-english.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.flat-wp.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.flat-wp.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.flat-wp.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.flat-wp.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.flat-wp.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.flat-wp.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.flat-wp.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.flat-wp.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.flat-wp.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.flat-wp.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.flat-wp.yaml)
+ [`beir-v1.0.0-quora.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.flat-wp.yaml)
+ [`beir-v1.0.0-dbpedia-entity.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.flat-wp.yaml)
+ [`beir-v1.0.0-scidocs.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.flat-wp.yaml)
+ [`beir-v1.0.0-fever.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.flat-wp.yaml)
+ [`beir-v1.0.0-climate-fever.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.flat-wp.yaml)
+ [`beir-v1.0.0-scifact.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.flat-wp.yaml)

</details>
<details>
<summary>BEIR (v1.0.0): "multifield" baseline</summary>

+ [`beir-v1.0.0-trec-covid.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.multifield.yaml)
+ [`beir-v1.0.0-bioasq.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.multifield.yaml)
+ [`beir-v1.0.0-nfcorpus.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.multifield.yaml)
+ [`beir-v1.0.0-nq.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.multifield.yaml)
+ [`beir-v1.0.0-hotpotqa.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.multifield.yaml)
+ [`beir-v1.0.0-fiqa.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.multifield.yaml)
+ [`beir-v1.0.0-signal1m.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.multifield.yaml)
+ [`beir-v1.0.0-trec-news.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.multifield.yaml)
+ [`beir-v1.0.0-robust04.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.multifield.yaml)
+ [`beir-v1.0.0-arguana.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.multifield.yaml)
+ [`beir-v1.0.0-webis-touche2020.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.multifield.yaml)
+ [`beir-v1.0.0-cqadupstack-android.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.multifield.yaml)
+ [`beir-v1.0.0-cqadupstack-english.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.multifield.yaml)
+ [`beir-v1.0.0-cqadupstack-gaming.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.multifield.yaml)
+ [`beir-v1.0.0-cqadupstack-gis.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.multifield.yaml)
+ [`beir-v1.0.0-cqadupstack-mathematica.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.multifield.yaml)
+ [`beir-v1.0.0-cqadupstack-physics.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.multifield.yaml)
+ [`beir-v1.0.0-cqadupstack-programmers.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.multifield.yaml)
+ [`beir-v1.0.0-cqadupstack-stats.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.multifield.yaml)
+ [`beir-v1.0.0-cqadupstack-tex.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.multifield.yaml)
+ [`beir-v1.0.0-cqadupstack-unix.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.multifield.yaml)
+ [`beir-v1.0.0-cqadupstack-webmasters.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.multifield.yaml)
+ [`beir-v1.0.0-cqadupstack-wordpress.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.multifield.yaml)
+ [`beir-v1.0.0-quora.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.multifield.yaml)
+ [`beir-v1.0.0-dbpedia-entity.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.multifield.yaml)
+ [`beir-v1.0.0-scidocs.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.multifield.yaml)
+ [`beir-v1.0.0-fever.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.multifield.yaml)
+ [`beir-v1.0.0-climate-fever.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.multifield.yaml)
+ [`beir-v1.0.0-scifact.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.multifield.yaml)

</details>
<details>
<summary>Mr.TyDi (v1.1): BM25 regressions</summary>

+ [`mrtydi-v1.1-ar`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ar.yaml)
+ [`mrtydi-v1.1-bn`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-bn.yaml)
+ [`mrtydi-v1.1-en`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-en.yaml)
+ [`mrtydi-v1.1-fi`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-fi.yaml)
+ [`mrtydi-v1.1-id`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-id.yaml)
+ [`mrtydi-v1.1-ja`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ja.yaml)
+ [`mrtydi-v1.1-ko`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ko.yaml)
+ [`mrtydi-v1.1-ru`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ru.yaml)
+ [`mrtydi-v1.1-sw`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-sw.yaml)
+ [`mrtydi-v1.1-te`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-te.yaml)
+ [`mrtydi-v1.1-th`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-th.yaml)


<div></div>

+ [`mrtydi-v1.1-ar-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ar-aca.yaml)
+ [`mrtydi-v1.1-bn-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-bn-aca.yaml)
+ [`mrtydi-v1.1-en-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-en-aca.yaml)
+ [`mrtydi-v1.1-fi-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-fi-aca.yaml)
+ [`mrtydi-v1.1-id-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-id-aca.yaml)
+ [`mrtydi-v1.1-ja-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ja-aca.yaml)
+ [`mrtydi-v1.1-ko-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ko-aca.yaml)
+ [`mrtydi-v1.1-ru-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ru-aca.yaml)
+ [`mrtydi-v1.1-sw-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-sw-aca.yaml)
+ [`mrtydi-v1.1-te-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-te-aca.yaml)
+ [`mrtydi-v1.1-th-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-th-aca.yaml)

</details>
<details>
<summary>MIRACL (v1.0): BM25 regressions</summary>

+ [`miracl-v1.0-ar`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ar.yaml)
+ [`miracl-v1.0-bn`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-bn.yaml)
+ [`miracl-v1.0-en`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-en.yaml)
+ [`miracl-v1.0-es`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-es.yaml)
+ [`miracl-v1.0-fa`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-fa.yaml)
+ [`miracl-v1.0-fi`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-fi.yaml)
+ [`miracl-v1.0-fr`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-fr.yaml)
+ [`miracl-v1.0-hi`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-hi.yaml)
+ [`miracl-v1.0-id`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-id.yaml)
+ [`miracl-v1.0-ja`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ja.yaml)
+ [`miracl-v1.0-ko`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ko.yaml)
+ [`miracl-v1.0-ru`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ru.yaml)
+ [`miracl-v1.0-sw`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-sw.yaml)
+ [`miracl-v1.0-te`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-te.yaml)
+ [`miracl-v1.0-th`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-th.yaml)
+ [`miracl-v1.0-zh`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-zh.yaml)


<div></div>

+ [`miracl-v1.0-ar-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ar-aca.yaml)
+ [`miracl-v1.0-bn-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-bn-aca.yaml)
+ [`miracl-v1.0-en-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-en-aca.yaml)
+ [`miracl-v1.0-es-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-es-aca.yaml)
+ [`miracl-v1.0-fa-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-fa-aca.yaml)
+ [`miracl-v1.0-fi-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-fi-aca.yaml)
+ [`miracl-v1.0-fr-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-fr-aca.yaml)
+ [`miracl-v1.0-hi-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-hi-aca.yaml)
+ [`miracl-v1.0-id-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-id-aca.yaml)
+ [`miracl-v1.0-ja-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ja-aca.yaml)
+ [`miracl-v1.0-ko-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ko-aca.yaml)
+ [`miracl-v1.0-ru-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ru-aca.yaml)
+ [`miracl-v1.0-sw-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-sw-aca.yaml)
+ [`miracl-v1.0-te-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-te-aca.yaml)
+ [`miracl-v1.0-th-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-th-aca.yaml)
+ [`miracl-v1.0-zh-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-zh-aca.yaml)

</details>
<details>
<summary>Other cross-lingual and multi-lingual regressions</summary>

+ [`ntcir8-zh`](../src/main/resources/reproduce/from-document-collection/configs/ntcir8-zh.yaml)
+ [`clef06-fr`](../src/main/resources/reproduce/from-document-collection/configs/clef06-fr.yaml)
+ [`trec02-ar`](../src/main/resources/reproduce/from-document-collection/configs/trec02-ar.yaml)
+ [`fire12-bn`](../src/main/resources/reproduce/from-document-collection/configs/fire12-bn.yaml)
+ [`fire12-hi`](../src/main/resources/reproduce/from-document-collection/configs/fire12-hi.yaml)
+ [`fire12-en`](../src/main/resources/reproduce/from-document-collection/configs/fire12-en.yaml)


<div></div>

+ [`hc4-v1.0-fa`](../src/main/resources/reproduce/from-document-collection/configs/hc4-v1.0-fa.yaml)
+ [`hc4-v1.0-ru`](../src/main/resources/reproduce/from-document-collection/configs/hc4-v1.0-ru.yaml)
+ [`hc4-v1.0-zh`](../src/main/resources/reproduce/from-document-collection/configs/hc4-v1.0-zh.yaml)
+ [`hc4-neuclir22-fa`](../src/main/resources/reproduce/from-document-collection/configs/hc4-neuclir22-fa.yaml)
+ [`hc4-neuclir22-ru`](../src/main/resources/reproduce/from-document-collection/configs/hc4-neuclir22-ru.yaml)
+ [`hc4-neuclir22-zh`](../src/main/resources/reproduce/from-document-collection/configs/hc4-neuclir22-zh.yaml)
+ [`hc4-neuclir22-fa-en`](../src/main/resources/reproduce/from-document-collection/configs/hc4-neuclir22-fa-en.yaml)
+ [`hc4-neuclir22-ru-en`](../src/main/resources/reproduce/from-document-collection/configs/hc4-neuclir22-ru-en.yaml)
+ [`hc4-neuclir22-zh-en`](../src/main/resources/reproduce/from-document-collection/configs/hc4-neuclir22-zh-en.yaml)


<div></div>

+ [`neuclir22-fa-qt`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-fa-qt.yaml)
+ [`neuclir22-fa-dt`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-fa-dt.yaml)
+ [`neuclir22-ru-qt`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-ru-qt.yaml)
+ [`neuclir22-ru-dt`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-ru-dt.yaml)
+ [`neuclir22-zh-qt`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-zh-qt.yaml)
+ [`neuclir22-zh-dt`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-zh-dt.yaml)
+ [`neuclir22-fa-qt-splade`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-fa-qt-splade.yaml)
+ [`neuclir22-fa-dt-splade`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-fa-dt-splade.yaml)
+ [`neuclir22-ru-qt-splade`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-ru-qt-splade.yaml)
+ [`neuclir22-ru-dt-splade`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-ru-dt-splade.yaml)
+ [`neuclir22-zh-qt-splade`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-zh-qt-splade.yaml)
+ [`neuclir22-zh-dt-splade`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-zh-dt-splade.yaml)


<div></div>

+ [`ciral-v1.0-ha`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-ha.yaml)
+ [`ciral-v1.0-so`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-so.yaml)
+ [`ciral-v1.0-sw`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-sw.yaml)
+ [`ciral-v1.0-yo`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-yo.yaml)
+ [`ciral-v1.0-ha-en`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-ha-en.yaml)
+ [`ciral-v1.0-so-en`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-so-en.yaml)
+ [`ciral-v1.0-sw-en`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-sw-en.yaml)
+ [`ciral-v1.0-yo-en`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-yo-en.yaml)

</details>
<details>
<summary>BRIGHT: BM25</summary>

+ [`bright-aops`](../src/main/resources/reproduce/from-document-collection/configs/bright-aops.yaml)
+ [`bright-biology`](../src/main/resources/reproduce/from-document-collection/configs/bright-biology.yaml)
+ [`bright-earth-science`](../src/main/resources/reproduce/from-document-collection/configs/bright-earth-science.yaml)
+ [`bright-economics`](../src/main/resources/reproduce/from-document-collection/configs/bright-economics.yaml)
+ [`bright-leetcode`](../src/main/resources/reproduce/from-document-collection/configs/bright-leetcode.yaml)
+ [`bright-pony`](../src/main/resources/reproduce/from-document-collection/configs/bright-pony.yaml)
+ [`bright-psychology`](../src/main/resources/reproduce/from-document-collection/configs/bright-psychology.yaml)
+ [`bright-robotics`](../src/main/resources/reproduce/from-document-collection/configs/bright-robotics.yaml)
+ [`bright-stackoverflow`](../src/main/resources/reproduce/from-document-collection/configs/bright-stackoverflow.yaml)
+ [`bright-sustainable-living`](../src/main/resources/reproduce/from-document-collection/configs/bright-sustainable-living.yaml)
+ [`bright-theoremqa-questions`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-questions.yaml)
+ [`bright-theoremqa-theorems`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-theorems.yaml)


<div></div>

+ [`bright-aops.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-aops.bm25qs.yaml)
+ [`bright-biology.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-biology.bm25qs.yaml)
+ [`bright-earth-science.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-earth-science.bm25qs.yaml)
+ [`bright-economics.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-economics.bm25qs.yaml)
+ [`bright-leetcode.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-leetcode.bm25qs.yaml)
+ [`bright-pony.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-pony.bm25qs.yaml)
+ [`bright-psychology.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-psychology.bm25qs.yaml)
+ [`bright-robotics.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-robotics.bm25qs.yaml)
+ [`bright-stackoverflow.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-stackoverflow.bm25qs.yaml)
+ [`bright-sustainable-living.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-sustainable-living.bm25qs.yaml)
+ [`bright-theoremqa-questions.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-questions.bm25qs.yaml)
+ [`bright-theoremqa-theorems.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-theorems.bm25qs.yaml)

</details>
<details>
<summary>BRIGHT: SPLADE-v3</summary>

+ [`bright-aops.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-aops.splade-v3.onnx.yaml)
+ [`bright-biology.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-biology.splade-v3.onnx.yaml)
+ [`bright-earth-science.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-earth-science.splade-v3.onnx.yaml)
+ [`bright-economics.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-economics.splade-v3.onnx.yaml)
+ [`bright-leetcode.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-leetcode.splade-v3.onnx.yaml)
+ [`bright-pony.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-pony.splade-v3.onnx.yaml)
+ [`bright-psychology.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-psychology.splade-v3.onnx.yaml)
+ [`bright-robotics.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-robotics.splade-v3.onnx.yaml)
+ [`bright-stackoverflow.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-stackoverflow.splade-v3.onnx.yaml)
+ [`bright-sustainable-living.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-sustainable-living.splade-v3.onnx.yaml)
+ [`bright-theoremqa-questions.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-questions.splade-v3.onnx.yaml)
+ [`bright-theoremqa-theorems.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-theorems.splade-v3.onnx.yaml)


<div></div>

+ [`bright-aops.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-aops.splade-v3.cached.yaml)
+ [`bright-biology.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-biology.splade-v3.cached.yaml)
+ [`bright-earth-science.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-earth-science.splade-v3.cached.yaml)
+ [`bright-economics.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-economics.splade-v3.cached.yaml)
+ [`bright-leetcode.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-leetcode.splade-v3.cached.yaml)
+ [`bright-pony.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-pony.splade-v3.cached.yaml)
+ [`bright-psychology.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-psychology.splade-v3.cached.yaml)
+ [`bright-robotics.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-robotics.splade-v3.cached.yaml)
+ [`bright-stackoverflow.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-stackoverflow.splade-v3.cached.yaml)
+ [`bright-sustainable-living.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-sustainable-living.splade-v3.cached.yaml)
+ [`bright-theoremqa-questions.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-questions.splade-v3.cached.yaml)
+ [`bright-theoremqa-theorems.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-theorems.splade-v3.cached.yaml)

</details>
<details>
<summary>BRIGHT: BGE-large-en-v1.5</summary>

+ [`bright-aops.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-aops.bge-large-en-v1.5.flat.onnx.yaml)
+ [`bright-biology.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-biology.bge-large-en-v1.5.flat.onnx.yaml)
+ [`bright-earth-science.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-earth-science.bge-large-en-v1.5.flat.onnx.yaml)
+ [`bright-economics.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-economics.bge-large-en-v1.5.flat.onnx.yaml)
+ [`bright-leetcode.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-leetcode.bge-large-en-v1.5.flat.onnx.yaml)
+ [`bright-pony.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-pony.bge-large-en-v1.5.flat.onnx.yaml)
+ [`bright-psychology.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-psychology.bge-large-en-v1.5.flat.onnx.yaml)
+ [`bright-robotics.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-robotics.bge-large-en-v1.5.flat.onnx.yaml)
+ [`bright-stackoverflow.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-stackoverflow.bge-large-en-v1.5.flat.onnx.yaml)
+ [`bright-sustainable-living.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-sustainable-living.bge-large-en-v1.5.flat.onnx.yaml)
+ [`bright-theoremqa-questions.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-questions.bge-large-en-v1.5.flat.onnx.yaml)
+ [`bright-theoremqa-theorems.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-theorems.bge-large-en-v1.5.flat.onnx.yaml)


<div></div>

+ [`bright-aops.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-aops.bge-large-en-v1.5.flat.cached.yaml)
+ [`bright-biology.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-biology.bge-large-en-v1.5.flat.cached.yaml)
+ [`bright-earth-science.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-earth-science.bge-large-en-v1.5.flat.cached.yaml)
+ [`bright-economics.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-economics.bge-large-en-v1.5.flat.cached.yaml)
+ [`bright-leetcode.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-leetcode.bge-large-en-v1.5.flat.cached.yaml)
+ [`bright-pony.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-pony.bge-large-en-v1.5.flat.cached.yaml)
+ [`bright-psychology.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-psychology.bge-large-en-v1.5.flat.cached.yaml)
+ [`bright-robotics.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-robotics.bge-large-en-v1.5.flat.cached.yaml)
+ [`bright-stackoverflow.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-stackoverflow.bge-large-en-v1.5.flat.cached.yaml)
+ [`bright-sustainable-living.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-sustainable-living.bge-large-en-v1.5.flat.cached.yaml)
+ [`bright-theoremqa-questions.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-questions.bge-large-en-v1.5.flat.cached.yaml)
+ [`bright-theoremqa-theorems.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-theorems.bge-large-en-v1.5.flat.cached.yaml)

</details>
<details>
<summary>Other regressions (TREC, etc.)</summary>

+ [`backgroundlinking18`](../src/main/resources/reproduce/from-document-collection/configs/backgroundlinking18.yaml)
+ [`backgroundlinking19`](../src/main/resources/reproduce/from-document-collection/configs/backgroundlinking19.yaml)
+ [`backgroundlinking20`](../src/main/resources/reproduce/from-document-collection/configs/backgroundlinking20.yaml)
+ [`disk12`](../src/main/resources/reproduce/from-document-collection/configs/disk12.yaml)
+ [`disk45`](../src/main/resources/reproduce/from-document-collection/configs/disk45.yaml)
+ [`robust05`](../src/main/resources/reproduce/from-document-collection/configs/robust05.yaml)
+ [`core17`](../src/main/resources/reproduce/from-document-collection/configs/core17.yaml)
+ [`core18`](../src/main/resources/reproduce/from-document-collection/configs/core18.yaml)
+ [`mb11`](../src/main/resources/reproduce/from-document-collection/configs/mb11.yaml)
+ [`mb13`](../src/main/resources/reproduce/from-document-collection/configs/mb13.yaml)
+ [`car17v1.5`](../src/main/resources/reproduce/from-document-collection/configs/car17v1.5.yaml)
+ [`car17v2.0`](../src/main/resources/reproduce/from-document-collection/configs/car17v2.0.yaml)
+ [`car17v2.0-doc2query`](../src/main/resources/reproduce/from-document-collection/configs/car17v2.0-doc2query.yaml)
+ [`wt10g`](../src/main/resources/reproduce/from-document-collection/configs/wt10g.yaml)
+ [`gov2`](../src/main/resources/reproduce/from-document-collection/configs/gov2.yaml)
+ [`cw09b`](../src/main/resources/reproduce/from-document-collection/configs/cw09b.yaml)
+ [`cw12b13`](../src/main/resources/reproduce/from-document-collection/configs/cw12b13.yaml)
+ [`cw12`](../src/main/resources/reproduce/from-document-collection/configs/cw12.yaml)
+ [`fever`](../src/main/resources/reproduce/from-document-collection/configs/fever.yaml)
+ [`wikipedia-dpr-100w-bm25`](../src/main/resources/reproduce/from-document-collection/configs/wikipedia-dpr-100w-bm25.yaml)
+ [`wiki-all-6-3-tamber-bm25`](../src/main/resources/reproduce/from-document-collection/configs/wiki-all-6-3-tamber-bm25.yaml)

</details>

The `--config` option specifies the experiment to run, corresponding to the YAML configuration file in [`src/main/resources/reproduce/from-document-collection/configs`](../src/main/resources/reproduce/from-document-collection/configs).
The three main options are:

+ `--index`: Build the index.
+ `--verify`: Verify index statistics.
+ `--search`: Perform retrieval runs and verify effectiveness.

**Watch out!** The full `cw12` regression can take a couple days to run and generates a 12TB index!

Although the driver is hard-coded to run on Waterloo machines (e.g., hard-coded paths to document collections), the document collection path can be manually specified from the command line with the `--corpus-path` option, for example:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection \
  --index --verify --search --config msmarco-v1-passage --corpus-path /path/to/corpus
```

For example, `--corpus-path collections/msmarco-passage/collection_jsonl`.