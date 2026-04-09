# Anserini: Reproduce Experimental Results from Document Collections

Reproduction experiments in Anserini are coordinated by a rigorous end-to-end framework implemented in [`io.anserini.reproduce.ReproduceFromDocumentCollection`](../src/main/java/io/anserini/reproduce/ReproduceFromDocumentCollection.java).
The driver automatically runs experiments based on configuration files stored in [`src/main/resources/reproduce/from-document-collection/configs`](../src/main/resources/reproduce/from-document-collection/configs), performing the following actions:

+ Build the index from scratch (i.e., from the raw document collection).
+ Verify index statistics (sanity check that the index has been built properly).
+ Perform retrieval runs with different settings.
+ Evaluate the runs and verify effectiveness results.

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
<summary>MS MARCO V1 + DL19-20 regressions</summary>

+ [`msmarco-v1-passage`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.md)]
+ [`msmarco-v1-passage.bm25-b8`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bm25-b8.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.bm25-b8.md)]
+ [`msmarco-v1-passage.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.wp-tok.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.wp-tok.md)]
+ [`msmarco-v1-passage.wp-hgf`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.wp-hgf.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.wp-hgf.md)]
+ [`msmarco-v1-passage.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.wp-ca.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.wp-ca.md)]
+ [`msmarco-v1-passage.doc2query`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.doc2query.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.doc2query.md)]
+ [`msmarco-v1-passage.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.docTTTTTquery.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.docTTTTTquery.md)]


<div></div>

+ [`msmarco-v1-passage.deepimpact.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.deepimpact.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.deepimpact.cached.md)]
+ [`msmarco-v1-passage.unicoil.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.unicoil.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.unicoil.cached.md)]
+ [`msmarco-v1-passage.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.unicoil-noexp.cached.md)]
+ [`msmarco-v1-passage.unicoil-tilde-expansion.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.unicoil-tilde-expansion.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.unicoil-tilde-expansion.cached.md)]
+ [`msmarco-v1-passage.distill-splade-max.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.distill-splade-max.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.distill-splade-max.cached.md)]
+ [`msmarco-v1-passage.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.splade-pp-ed.cached.md)]
+ [`msmarco-v1-passage.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.splade-pp-ed.onnx.md)]
+ [`msmarco-v1-passage.splade-pp-sd.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.splade-pp-sd.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.splade-pp-sd.cached.md)]
+ [`msmarco-v1-passage.splade-pp-sd.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.splade-pp-sd.onnx.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.splade-pp-sd.onnx.md)]
+ [`msmarco-v1-passage.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.splade-v3.onnx.md)]
+ [`msmarco-v1-passage.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.splade-v3.cached.md)]


<div></div>

+ [`msmarco-v1-passage.cos-dpr-distil.parquet.fw`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.fw.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.cos-dpr-distil.parquet.fw.md)]
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.lexlsh`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.lexlsh.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.cos-dpr-distil.parquet.lexlsh.md)]
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.cos-dpr-distil.parquet.flat.cached.md)]
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.cos-dpr-distil.parquet.flat.onnx.md)]
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.cos-dpr-distil.parquet.flat-sqv.cached.md)]
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.cos-dpr-distil.parquet.flat-sqv.onnx.md)]
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.cos-dpr-distil.parquet.hnsw.cached.md)]
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.cos-dpr-distil.parquet.hnsw.onnx.md)]
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.cos-dpr-distil.parquet.hnsw-sqv.cached.md)]
+ [`msmarco-v1-passage.cos-dpr-distil.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cos-dpr-distil.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.cos-dpr-distil.parquet.hnsw-sqv.onnx.md)]
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`msmarco-v1-passage.openai-ada2.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.openai-ada2.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.openai-ada2.parquet.flat.cached.md)]
+ [`msmarco-v1-passage.openai-ada2.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.openai-ada2.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.openai-ada2.parquet.flat-sqv.cached.md)]
+ [`msmarco-v1-passage.openai-ada2.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.openai-ada2.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.openai-ada2.parquet.hnsw.cached.md)]
+ [`msmarco-v1-passage.openai-ada2.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.openai-ada2.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.openai-ada2.parquet.hnsw-sqv.cached.md)]
+ [`msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat.cached.md)]
+ [`msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.cohere-embed-english-v3.0.parquet.flat-sqv.cached.md)]
+ [`msmarco-v1-passage.cohere-embed-english-v3.0.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cohere-embed-english-v3.0.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.cohere-embed-english-v3.0.parquet.hnsw.cached.md)]
+ [`msmarco-v1-passage.cohere-embed-english-v3.0.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-passage.cohere-embed-english-v3.0.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-passage.cohere-embed-english-v3.0.parquet.hnsw-sqv.cached.md)]


<div></div>

+ [`msmarco-v1-doc`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-doc.md)]
+ [`msmarco-v1-doc.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc.wp-tok.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-doc.wp-tok.md)]
+ [`msmarco-v1-doc.wp-hgf`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc.wp-hgf.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-doc.wp-hgf.md)]
+ [`msmarco-v1-doc.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc.wp-ca.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-doc.wp-ca.md)]
+ [`msmarco-v1-doc.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc.docTTTTTquery.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-doc.docTTTTTquery.md)]


<div></div>

+ [`msmarco-v1-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc-segmented.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-doc-segmented.md)]
+ [`msmarco-v1-doc-segmented.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc-segmented.wp-tok.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-doc-segmented.wp-tok.md)]
+ [`msmarco-v1-doc-segmented.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc-segmented.wp-ca.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-doc-segmented.wp-ca.md)]
+ [`msmarco-v1-doc-segmented.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc-segmented.docTTTTTquery.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-doc-segmented.docTTTTTquery.md)]
+ [`msmarco-v1-doc-segmented.unicoil.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc-segmented.unicoil.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-doc-segmented.unicoil.cached.md)]
+ [`msmarco-v1-doc-segmented.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v1-doc-segmented.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v1-doc-segmented.unicoil-noexp.cached.md)]


<div></div>

+ [`dl19-passage`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.yaml) [[docs](reproduce/from-document-collection/dl19-passage.md)]
+ [`dl19-passage.bm25-b8`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bm25-b8.yaml) [[docs](reproduce/from-document-collection/dl19-passage.bm25-b8.md)]
+ [`dl19-passage.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.wp-tok.yaml) [[docs](reproduce/from-document-collection/dl19-passage.wp-tok.md)]
+ [`dl19-passage.wp-hgf`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.wp-hgf.yaml) [[docs](reproduce/from-document-collection/dl19-passage.wp-hgf.md)]
+ [`dl19-passage.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.wp-ca.yaml) [[docs](reproduce/from-document-collection/dl19-passage.wp-ca.md)]
+ [`dl19-passage.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.docTTTTTquery.yaml) [[docs](reproduce/from-document-collection/dl19-passage.docTTTTTquery.md)]


<div></div>

+ [`dl19-passage.unicoil.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.unicoil.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.unicoil.cached.md)]
+ [`dl19-passage.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.unicoil-noexp.cached.md)]
+ [`dl19-passage.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.splade-pp-ed.cached.md)]
+ [`dl19-passage.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/dl19-passage.splade-pp-ed.onnx.md)]
+ [`dl19-passage.splade-pp-sd.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.splade-pp-sd.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.splade-pp-sd.cached.md)]
+ [`dl19-passage.splade-pp-sd.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.splade-pp-sd.onnx.yaml) [[docs](reproduce/from-document-collection/dl19-passage.splade-pp-sd.onnx.md)]
+ [`dl19-passage.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/dl19-passage.splade-v3.onnx.md)]
+ [`dl19-passage.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.splade-v3.cached.md)]


<div></div>

+ [`dl19-passage.cos-dpr-distil.parquet.fw`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.fw.yaml) [[docs](reproduce/from-document-collection/dl19-passage.cos-dpr-distil.parquet.fw.md)]
+ [`dl19-passage.cos-dpr-distil.parquet.lexlsh`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.lexlsh.yaml) [[docs](reproduce/from-document-collection/dl19-passage.cos-dpr-distil.parquet.lexlsh.md)]
+ [`dl19-passage.cos-dpr-distil.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.cos-dpr-distil.parquet.flat.cached.md)]
+ [`dl19-passage.cos-dpr-distil.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/dl19-passage.cos-dpr-distil.parquet.flat.onnx.md)]
+ [`dl19-passage.cos-dpr-distil.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.cos-dpr-distil.parquet.flat-sqv.cached.md)]
+ [`dl19-passage.cos-dpr-distil.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/dl19-passage.cos-dpr-distil.parquet.flat-sqv.onnx.md)]
+ [`dl19-passage.cos-dpr-distil.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.cos-dpr-distil.parquet.hnsw.cached.md)]
+ [`dl19-passage.cos-dpr-distil.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/dl19-passage.cos-dpr-distil.parquet.hnsw.onnx.md)]
+ [`dl19-passage.cos-dpr-distil.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.cos-dpr-distil.parquet.hnsw-sqv.cached.md)]
+ [`dl19-passage.cos-dpr-distil.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cos-dpr-distil.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/dl19-passage.cos-dpr-distil.parquet.hnsw-sqv.onnx.md)]
+ [`dl19-passage.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`dl19-passage.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/dl19-passage.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`dl19-passage.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`dl19-passage.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/dl19-passage.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`dl19-passage.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`dl19-passage.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/dl19-passage.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`dl19-passage.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`dl19-passage.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/dl19-passage.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`dl19-passage.openai-ada2.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.openai-ada2.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.openai-ada2.parquet.flat.cached.md)]
+ [`dl19-passage.openai-ada2.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.openai-ada2.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.openai-ada2.parquet.flat-sqv.cached.md)]
+ [`dl19-passage.openai-ada2.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.openai-ada2.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.openai-ada2.parquet.hnsw.cached.md)]
+ [`dl19-passage.openai-ada2.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.openai-ada2.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.openai-ada2.parquet.hnsw-sqv.cached.md)]
+ [`dl19-passage.cohere-embed-english-v3.0.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cohere-embed-english-v3.0.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.cohere-embed-english-v3.0.parquet.flat.cached.md)]
+ [`dl19-passage.cohere-embed-english-v3.0.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cohere-embed-english-v3.0.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.cohere-embed-english-v3.0.parquet.flat-sqv.cached.md)]
+ [`dl19-passage.cohere-embed-english-v3.0.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cohere-embed-english-v3.0.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.cohere-embed-english-v3.0.parquet.hnsw.cached.md)]
+ [`dl19-passage.cohere-embed-english-v3.0.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-passage.cohere-embed-english-v3.0.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl19-passage.cohere-embed-english-v3.0.parquet.hnsw-sqv.cached.md)]


<div></div>

+ [`dl19-doc`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc.yaml) [[docs](reproduce/from-document-collection/dl19-doc.md)]
+ [`dl19-doc.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc.wp-tok.yaml) [[docs](reproduce/from-document-collection/dl19-doc.wp-tok.md)]
+ [`dl19-doc.wp-hgf`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc.wp-hgf.yaml) [[docs](reproduce/from-document-collection/dl19-doc.wp-hgf.md)]
+ [`dl19-doc.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc.wp-ca.yaml) [[docs](reproduce/from-document-collection/dl19-doc.wp-ca.md)]
+ [`dl19-doc.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc.docTTTTTquery.yaml) [[docs](reproduce/from-document-collection/dl19-doc.docTTTTTquery.md)]


<div></div>

+ [`dl19-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc-segmented.yaml) [[docs](reproduce/from-document-collection/dl19-doc-segmented.md)]
+ [`dl19-doc-segmented.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc-segmented.wp-tok.yaml) [[docs](reproduce/from-document-collection/dl19-doc-segmented.wp-tok.md)]
+ [`dl19-doc-segmented.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc-segmented.wp-ca.yaml) [[docs](reproduce/from-document-collection/dl19-doc-segmented.wp-ca.md)]
+ [`dl19-doc-segmented.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc-segmented.docTTTTTquery.yaml) [[docs](reproduce/from-document-collection/dl19-doc-segmented.docTTTTTquery.md)]
+ [`dl19-doc-segmented.unicoil.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc-segmented.unicoil.cached.yaml) [[docs](reproduce/from-document-collection/dl19-doc-segmented.unicoil.cached.md)]
+ [`dl19-doc-segmented.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl19-doc-segmented.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/dl19-doc-segmented.unicoil-noexp.cached.md)]


<div></div>

+ [`dl20-passage`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.yaml) [[docs](reproduce/from-document-collection/dl20-passage.md)]
+ [`dl20-passage.bm25-b8`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bm25-b8.yaml) [[docs](reproduce/from-document-collection/dl20-passage.bm25-b8.md)]
+ [`dl20-passage.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.wp-tok.yaml) [[docs](reproduce/from-document-collection/dl20-passage.wp-tok.md)]
+ [`dl20-passage.wp-hgf`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.wp-hgf.yaml) [[docs](reproduce/from-document-collection/dl20-passage.wp-hgf.md)]
+ [`dl20-passage.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.wp-ca.yaml) [[docs](reproduce/from-document-collection/dl20-passage.wp-ca.md)]
+ [`dl20-passage.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.docTTTTTquery.yaml) [[docs](reproduce/from-document-collection/dl20-passage.docTTTTTquery.md)]


<div></div>

+ [`dl20-passage.unicoil.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.unicoil.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.unicoil.cached.md)]
+ [`dl20-passage.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.unicoil-noexp.cached.md)]
+ [`dl20-passage.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.splade-pp-ed.cached.md)]
+ [`dl20-passage.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/dl20-passage.splade-pp-ed.onnx.md)]
+ [`dl20-passage.splade-pp-sd.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.splade-pp-sd.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.splade-pp-sd.cached.md)]
+ [`dl20-passage.splade-pp-sd.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.splade-pp-sd.onnx.yaml) [[docs](reproduce/from-document-collection/dl20-passage.splade-pp-sd.onnx.md)]
+ [`dl20-passage.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/dl20-passage.splade-v3.onnx.md)]
+ [`dl20-passage.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.splade-v3.cached.md)]


<div></div>

+ [`dl20-passage.cos-dpr-distil.parquet.fw`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.fw.yaml) [[docs](reproduce/from-document-collection/dl20-passage.cos-dpr-distil.parquet.fw.md)]
+ [`dl20-passage.cos-dpr-distil.parquet.lexlsh`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.lexlsh.yaml) [[docs](reproduce/from-document-collection/dl20-passage.cos-dpr-distil.parquet.lexlsh.md)]
+ [`dl20-passage.cos-dpr-distil.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.cos-dpr-distil.parquet.flat.cached.md)]
+ [`dl20-passage.cos-dpr-distil.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/dl20-passage.cos-dpr-distil.parquet.flat.onnx.md)]
+ [`dl20-passage.cos-dpr-distil.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.cos-dpr-distil.parquet.flat-sqv.cached.md)]
+ [`dl20-passage.cos-dpr-distil.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/dl20-passage.cos-dpr-distil.parquet.flat-sqv.onnx.md)]
+ [`dl20-passage.cos-dpr-distil.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.cos-dpr-distil.parquet.hnsw.cached.md)]
+ [`dl20-passage.cos-dpr-distil.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/dl20-passage.cos-dpr-distil.parquet.hnsw.onnx.md)]
+ [`dl20-passage.cos-dpr-distil.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.cos-dpr-distil.parquet.hnsw-sqv.cached.md)]
+ [`dl20-passage.cos-dpr-distil.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cos-dpr-distil.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/dl20-passage.cos-dpr-distil.parquet.hnsw-sqv.onnx.md)]
+ [`dl20-passage.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`dl20-passage.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/dl20-passage.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`dl20-passage.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`dl20-passage.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/dl20-passage.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`dl20-passage.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`dl20-passage.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/dl20-passage.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`dl20-passage.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`dl20-passage.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/dl20-passage.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`dl20-passage.openai-ada2.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.openai-ada2.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.openai-ada2.parquet.flat.cached.md)]
+ [`dl20-passage.openai-ada2.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.openai-ada2.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.openai-ada2.parquet.flat-sqv.cached.md)]
+ [`dl20-passage.openai-ada2.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.openai-ada2.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.openai-ada2.parquet.hnsw.cached.md)]
+ [`dl20-passage.openai-ada2.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.openai-ada2.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.openai-ada2.parquet.hnsw-sqv.cached.md)]
+ [`dl20-passage.cohere-embed-english-v3.0.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cohere-embed-english-v3.0.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.cohere-embed-english-v3.0.parquet.flat.cached.md)]
+ [`dl20-passage.cohere-embed-english-v3.0.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cohere-embed-english-v3.0.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.cohere-embed-english-v3.0.parquet.flat-sqv.cached.md)]
+ [`dl20-passage.cohere-embed-english-v3.0.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cohere-embed-english-v3.0.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.cohere-embed-english-v3.0.parquet.hnsw.cached.md)]
+ [`dl20-passage.cohere-embed-english-v3.0.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-passage.cohere-embed-english-v3.0.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/dl20-passage.cohere-embed-english-v3.0.parquet.hnsw-sqv.cached.md)]


<div></div>

+ [`dl20-doc`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc.yaml) [[docs](reproduce/from-document-collection/dl20-doc.md)]
+ [`dl20-doc.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc.wp-tok.yaml) [[docs](reproduce/from-document-collection/dl20-doc.wp-tok.md)]
+ [`dl20-doc.wp-hgf`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc.wp-hgf.yaml) [[docs](reproduce/from-document-collection/dl20-doc.wp-hgf.md)]
+ [`dl20-doc.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc.wp-ca.yaml) [[docs](reproduce/from-document-collection/dl20-doc.wp-ca.md)]
+ [`dl20-doc.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc.docTTTTTquery.yaml) [[docs](reproduce/from-document-collection/dl20-doc.docTTTTTquery.md)]


<div></div>

+ [`dl20-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc-segmented.yaml) [[docs](reproduce/from-document-collection/dl20-doc-segmented.md)]
+ [`dl20-doc-segmented.wp-tok`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc-segmented.wp-tok.yaml) [[docs](reproduce/from-document-collection/dl20-doc-segmented.wp-tok.md)]
+ [`dl20-doc-segmented.wp-ca`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc-segmented.wp-ca.yaml) [[docs](reproduce/from-document-collection/dl20-doc-segmented.wp-ca.md)]
+ [`dl20-doc-segmented.docTTTTTquery`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc-segmented.docTTTTTquery.yaml) [[docs](reproduce/from-document-collection/dl20-doc-segmented.docTTTTTquery.md)]
+ [`dl20-doc-segmented.unicoil.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc-segmented.unicoil.cached.yaml) [[docs](reproduce/from-document-collection/dl20-doc-segmented.unicoil.cached.md)]
+ [`dl20-doc-segmented.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl20-doc-segmented.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/dl20-doc-segmented.unicoil-noexp.cached.md)]

</details>
<details>
<summary>MS MARCO V2 + DL21-23 regressions</summary>

+ [`msmarco-v2-passage`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-passage.md)]
+ [`msmarco-v2-passage.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.d2q-t5.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-passage.d2q-t5.md)]
+ [`msmarco-v2-passage.unicoil-noexp-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.unicoil-noexp-0shot.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-passage.unicoil-noexp-0shot.cached.md)]
+ [`msmarco-v2-passage.unicoil-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.unicoil-0shot.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-passage.unicoil-0shot.cached.md)]
+ [`msmarco-v2-passage.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-passage.splade-pp-ed.cached.md)]
+ [`msmarco-v2-passage.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-passage.splade-pp-ed.onnx.md)]
+ [`msmarco-v2-passage.splade-pp-sd.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.splade-pp-sd.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-passage.splade-pp-sd.cached.md)]
+ [`msmarco-v2-passage.splade-pp-sd.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage.splade-pp-sd.onnx.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-passage.splade-pp-sd.onnx.md)]
+ [`msmarco-v2-passage-augmented`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage-augmented.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-passage-augmented.md)]
+ [`msmarco-v2-passage-augmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-passage-augmented.d2q-t5.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-passage-augmented.d2q-t5.md)]
+ [`msmarco-v2-doc`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-doc.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-doc.md)]
+ [`msmarco-v2-doc.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-doc.d2q-t5.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-doc.d2q-t5.md)]
+ [`msmarco-v2-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-doc-segmented.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-doc-segmented.md)]
+ [`msmarco-v2-doc-segmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-doc-segmented.d2q-t5.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-doc-segmented.d2q-t5.md)]
+ [`msmarco-v2-doc-segmented.unicoil-noexp-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-doc-segmented.unicoil-noexp-0shot-v2.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-doc-segmented.unicoil-noexp-0shot-v2.cached.md)]
+ [`msmarco-v2-doc-segmented.unicoil-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2-doc-segmented.unicoil-0shot-v2.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v2-doc-segmented.unicoil-0shot-v2.cached.md)]


<div></div>

+ [`dl21-passage`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.yaml) [[docs](reproduce/from-document-collection/dl21-passage.md)]
+ [`dl21-passage.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.d2q-t5.yaml) [[docs](reproduce/from-document-collection/dl21-passage.d2q-t5.md)]
+ [`dl21-passage.unicoil-noexp-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.unicoil-noexp-0shot.cached.yaml) [[docs](reproduce/from-document-collection/dl21-passage.unicoil-noexp-0shot.cached.md)]
+ [`dl21-passage.unicoil-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.unicoil-0shot.cached.yaml) [[docs](reproduce/from-document-collection/dl21-passage.unicoil-0shot.cached.md)]
+ [`dl21-passage.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/dl21-passage.splade-pp-ed.cached.md)]
+ [`dl21-passage.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/dl21-passage.splade-pp-ed.onnx.md)]
+ [`dl21-passage.splade-pp-sd.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.splade-pp-sd.cached.yaml) [[docs](reproduce/from-document-collection/dl21-passage.splade-pp-sd.cached.md)]
+ [`dl21-passage.splade-pp-sd.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage.splade-pp-sd.onnx.yaml) [[docs](reproduce/from-document-collection/dl21-passage.splade-pp-sd.onnx.md)]
+ [`dl21-passage-augmented`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage-augmented.yaml) [[docs](reproduce/from-document-collection/dl21-passage-augmented.md)]
+ [`dl21-passage-augmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl21-passage-augmented.d2q-t5.yaml) [[docs](reproduce/from-document-collection/dl21-passage-augmented.d2q-t5.md)]
+ [`dl21-doc`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc.yaml) [[docs](reproduce/from-document-collection/dl21-doc.md)]
+ [`dl21-doc.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc.d2q-t5.yaml) [[docs](reproduce/from-document-collection/dl21-doc.d2q-t5.md)]
+ [`dl21-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented.yaml) [[docs](reproduce/from-document-collection/dl21-doc-segmented.md)]
+ [`dl21-doc-segmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented.d2q-t5.yaml) [[docs](reproduce/from-document-collection/dl21-doc-segmented.d2q-t5.md)]
+ [`dl21-doc-segmented.unicoil-noexp-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented.unicoil-noexp-0shot-v2.cached.yaml) [[docs](reproduce/from-document-collection/dl21-doc-segmented.unicoil-noexp-0shot-v2.cached.md)]
+ [`dl21-doc-segmented.unicoil-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented.unicoil-0shot-v2.cached.yaml) [[docs](reproduce/from-document-collection/dl21-doc-segmented.unicoil-0shot-v2.cached.md)]


<div></div>

+ [`dl22-passage`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.yaml) [[docs](reproduce/from-document-collection/dl22-passage.md)]
+ [`dl22-passage.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.d2q-t5.yaml) [[docs](reproduce/from-document-collection/dl22-passage.d2q-t5.md)]
+ [`dl22-passage.unicoil-noexp-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.unicoil-noexp-0shot.cached.yaml) [[docs](reproduce/from-document-collection/dl22-passage.unicoil-noexp-0shot.cached.md)]
+ [`dl22-passage.unicoil-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.unicoil-0shot.cached.yaml) [[docs](reproduce/from-document-collection/dl22-passage.unicoil-0shot.cached.md)]
+ [`dl22-passage.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/dl22-passage.splade-pp-ed.cached.md)]
+ [`dl22-passage.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/dl22-passage.splade-pp-ed.onnx.md)]
+ [`dl22-passage.splade-pp-sd.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.splade-pp-sd.cached.yaml) [[docs](reproduce/from-document-collection/dl22-passage.splade-pp-sd.cached.md)]
+ [`dl22-passage.splade-pp-sd.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage.splade-pp-sd.onnx.yaml) [[docs](reproduce/from-document-collection/dl22-passage.splade-pp-sd.onnx.md)]
+ [`dl22-passage-augmented`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage-augmented.yaml) [[docs](reproduce/from-document-collection/dl22-passage-augmented.md)]
+ [`dl22-passage-augmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl22-passage-augmented.d2q-t5.yaml) [[docs](reproduce/from-document-collection/dl22-passage-augmented.d2q-t5.md)]
+ [`dl22-doc`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc.yaml) [[docs](reproduce/from-document-collection/dl22-doc.md)]
+ [`dl22-doc.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc.d2q-t5.yaml) [[docs](reproduce/from-document-collection/dl22-doc.d2q-t5.md)]
+ [`dl22-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-segmented.yaml) [[docs](reproduce/from-document-collection/dl22-doc-segmented.md)]
+ [`dl22-doc-segmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-segmented.d2q-t5.yaml) [[docs](reproduce/from-document-collection/dl22-doc-segmented.d2q-t5.md)]
+ [`dl22-doc-segmented.unicoil-noexp-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-segmented.unicoil-noexp-0shot-v2.cached.yaml) [[docs](reproduce/from-document-collection/dl22-doc-segmented.unicoil-noexp-0shot-v2.cached.md)]
+ [`dl22-doc-segmented.unicoil-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-segmented.unicoil-0shot-v2.cached.yaml) [[docs](reproduce/from-document-collection/dl22-doc-segmented.unicoil-0shot-v2.cached.md)]


<div></div>

+ [`dl23-passage`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.yaml) [[docs](reproduce/from-document-collection/dl23-passage.md)]
+ [`dl23-passage.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.d2q-t5.yaml) [[docs](reproduce/from-document-collection/dl23-passage.d2q-t5.md)]
+ [`dl23-passage.unicoil-noexp-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.unicoil-noexp-0shot.cached.yaml) [[docs](reproduce/from-document-collection/dl23-passage.unicoil-noexp-0shot.cached.md)]
+ [`dl23-passage.unicoil-0shot.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.unicoil-0shot.cached.yaml) [[docs](reproduce/from-document-collection/dl23-passage.unicoil-0shot.cached.md)]
+ [`dl23-passage.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/dl23-passage.splade-pp-ed.cached.md)]
+ [`dl23-passage.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/dl23-passage.splade-pp-ed.onnx.md)]
+ [`dl23-passage.splade-pp-sd.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.splade-pp-sd.cached.yaml) [[docs](reproduce/from-document-collection/dl23-passage.splade-pp-sd.cached.md)]
+ [`dl23-passage.splade-pp-sd.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage.splade-pp-sd.onnx.yaml) [[docs](reproduce/from-document-collection/dl23-passage.splade-pp-sd.onnx.md)]
+ [`dl23-passage-augmented`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage-augmented.yaml) [[docs](reproduce/from-document-collection/dl23-passage-augmented.md)]
+ [`dl23-passage-augmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl23-passage-augmented.d2q-t5.yaml) [[docs](reproduce/from-document-collection/dl23-passage-augmented.d2q-t5.md)]
+ [`dl23-doc`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc.yaml) [[docs](reproduce/from-document-collection/dl23-doc.md)]
+ [`dl23-doc.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc.d2q-t5.yaml) [[docs](reproduce/from-document-collection/dl23-doc.d2q-t5.md)]
+ [`dl23-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-segmented.yaml) [[docs](reproduce/from-document-collection/dl23-doc-segmented.md)]
+ [`dl23-doc-segmented.d2q-t5`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-segmented.d2q-t5.yaml) [[docs](reproduce/from-document-collection/dl23-doc-segmented.d2q-t5.md)]
+ [`dl23-doc-segmented.unicoil-noexp-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-segmented.unicoil-noexp-0shot-v2.cached.yaml) [[docs](reproduce/from-document-collection/dl23-doc-segmented.unicoil-noexp-0shot-v2.cached.md)]
+ [`dl23-doc-segmented.unicoil-0shot-v2.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-segmented.unicoil-0shot-v2.cached.yaml) [[docs](reproduce/from-document-collection/dl23-doc-segmented.unicoil-0shot-v2.cached.md)]

</details>
<details>
<summary>MS MARCO V2.1 + RAG24-25 regressions</summary>

+ [`rag24-doc-segmented-test-umbrela`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-umbrela.md)]
+ [`rag24-doc-segmented-test-umbrela.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-umbrela.splade-v3.onnx.md)]
+ [`rag24-doc-segmented-test-umbrela.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-umbrela.splade-v3.cached.md)]
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard00.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard00.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard00.flat.onnx.md)]
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard01.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard01.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard01.flat.onnx.md)]
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard02.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard02.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard02.flat.onnx.md)]
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard03.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard03.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard03.flat.onnx.md)]
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard04.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard04.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard04.flat.onnx.md)]
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard05.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard05.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard05.flat.onnx.md)]
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard06.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard06.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard06.flat.onnx.md)]
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard07.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard07.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard07.flat.onnx.md)]
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard08.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard08.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard08.flat.onnx.md)]
+ [`rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard09.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard09.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-umbrela.arctic-embed-l.parquet.shard09.flat.onnx.md)]
+ [`rag24-doc-segmented-test-nist`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-nist.md)]
+ [`rag24-doc-segmented-test-nist.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-nist.splade-v3.onnx.md)]
+ [`rag24-doc-segmented-test-nist.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-nist.splade-v3.cached.md)]
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard00.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard00.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard00.flat.onnx.md)]
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard01.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard01.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard01.flat.onnx.md)]
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard02.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard02.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard02.flat.onnx.md)]
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard03.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard03.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard03.flat.onnx.md)]
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard04.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard04.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard04.flat.onnx.md)]
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard05.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard05.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard05.flat.onnx.md)]
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard06.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard06.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard06.flat.onnx.md)]
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard07.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard07.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard07.flat.onnx.md)]
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard08.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard08.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard08.flat.onnx.md)]
+ [`rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard09.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard09.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-test-nist.arctic-embed-l.parquet.shard09.flat.onnx.md)]


<div></div>

+ [`rag25-doc-segmented-test-umbrela2`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-umbrela2.md)]
+ [`rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard00.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard00.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard00.flat.onnx.md)]
+ [`rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard01.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard01.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard01.flat.onnx.md)]
+ [`rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard02.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard02.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard02.flat.onnx.md)]
+ [`rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard03.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard03.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard03.flat.onnx.md)]
+ [`rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard04.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard04.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard04.flat.onnx.md)]
+ [`rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard05.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard05.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard05.flat.onnx.md)]
+ [`rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard06.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard06.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard06.flat.onnx.md)]
+ [`rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard07.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard07.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard07.flat.onnx.md)]
+ [`rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard08.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard08.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard08.flat.onnx.md)]
+ [`rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard09.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard09.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-umbrela2.arctic-embed-l.parquet.shard09.flat.onnx.md)]
+ [`rag25-doc-segmented-test-nist`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-nist.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-nist.md)]
+ [`rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard00.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard00.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard00.flat.onnx.md)]
+ [`rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard01.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard01.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard01.flat.onnx.md)]
+ [`rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard02.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard02.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard02.flat.onnx.md)]
+ [`rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard03.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard03.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard03.flat.onnx.md)]
+ [`rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard04.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard04.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard04.flat.onnx.md)]
+ [`rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard05.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard05.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard05.flat.onnx.md)]
+ [`rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard06.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard06.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard06.flat.onnx.md)]
+ [`rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard07.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard07.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard07.flat.onnx.md)]
+ [`rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard08.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard08.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard08.flat.onnx.md)]
+ [`rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard09.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard09.flat.onnx.yaml) [[docs](reproduce/from-document-collection/rag25-doc-segmented-test-nist.arctic-embed-l.parquet.shard09.flat.onnx.md)]


<div></div>

+ [`msmarco-v2.1-doc`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2.1-doc.yaml) [[docs](reproduce/from-document-collection/msmarco-v2.1-doc.md)]
+ [`msmarco-v2.1-doc-segmented`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2.1-doc-segmented.yaml) [[docs](reproduce/from-document-collection/msmarco-v2.1-doc-segmented.md)]
+ [`msmarco-v2.1-doc-segmented.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2.1-doc-segmented.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/msmarco-v2.1-doc-segmented.splade-v3.onnx.md)]
+ [`msmarco-v2.1-doc-segmented.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/msmarco-v2.1-doc-segmented.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/msmarco-v2.1-doc-segmented.splade-v3.cached.md)]


<div></div>

+ [`dl21-doc-msmarco-v2.1`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-msmarco-v2.1.yaml) [[docs](reproduce/from-document-collection/dl21-doc-msmarco-v2.1.md)]
+ [`dl21-doc-segmented-msmarco-v2.1`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented-msmarco-v2.1.yaml) [[docs](reproduce/from-document-collection/dl21-doc-segmented-msmarco-v2.1.md)]
+ [`dl21-doc-segmented-msmarco-v2.1.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented-msmarco-v2.1.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/dl21-doc-segmented-msmarco-v2.1.splade-v3.onnx.md)]
+ [`dl21-doc-segmented-msmarco-v2.1.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl21-doc-segmented-msmarco-v2.1.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/dl21-doc-segmented-msmarco-v2.1.splade-v3.cached.md)]
+ [`dl22-doc-msmarco-v2.1`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-msmarco-v2.1.yaml) [[docs](reproduce/from-document-collection/dl22-doc-msmarco-v2.1.md)]
+ [`dl22-doc-segmented-msmarco-v2.1`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-segmented-msmarco-v2.1.yaml) [[docs](reproduce/from-document-collection/dl22-doc-segmented-msmarco-v2.1.md)]
+ [`dl22-doc-segmented-msmarco-v2.1.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-segmented-msmarco-v2.1.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/dl22-doc-segmented-msmarco-v2.1.splade-v3.onnx.md)]
+ [`dl22-doc-segmented-msmarco-v2.1.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl22-doc-segmented-msmarco-v2.1.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/dl22-doc-segmented-msmarco-v2.1.splade-v3.cached.md)]
+ [`dl23-doc-msmarco-v2.1`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-msmarco-v2.1.yaml) [[docs](reproduce/from-document-collection/dl23-doc-msmarco-v2.1.md)]
+ [`dl23-doc-segmented-msmarco-v2.1`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-segmented-msmarco-v2.1.yaml) [[docs](reproduce/from-document-collection/dl23-doc-segmented-msmarco-v2.1.md)]
+ [`dl23-doc-segmented-msmarco-v2.1.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-segmented-msmarco-v2.1.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/dl23-doc-segmented-msmarco-v2.1.splade-v3.onnx.md)]
+ [`dl23-doc-segmented-msmarco-v2.1.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/dl23-doc-segmented-msmarco-v2.1.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/dl23-doc-segmented-msmarco-v2.1.splade-v3.cached.md)]


<div></div>

+ [`rag24-doc-raggy-dev`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-raggy-dev.yaml) [[docs](reproduce/from-document-collection/rag24-doc-raggy-dev.md)]
+ [`rag24-doc-segmented-raggy-dev`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-raggy-dev.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-raggy-dev.md)]
+ [`rag24-doc-segmented-raggy-dev.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-raggy-dev.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-raggy-dev.splade-v3.onnx.md)]
+ [`rag24-doc-segmented-raggy-dev.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/rag24-doc-segmented-raggy-dev.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/rag24-doc-segmented-raggy-dev.splade-v3.cached.md)]

</details>
<details>
<summary>BEIR (v1.0.0): BGE-base-en-v1.5</summary>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat.cached.md)]
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat.cached.md)]


<div></div>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat.onnx.md)]
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat.onnx.md)]


<div></div>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat-sqv.cached.md)]


<div></div>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.flat-sqv.onnx.md)]


<div></div>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw.cached.md)]
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw.cached.md)]


<div></div>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw.onnx.md)]
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw.onnx.md)]


<div></div>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw-sqv.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw-sqv.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw-sqv.cached.md)]


<div></div>

+ [`beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]
+ [`beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw-sqv.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.bge-base-en-v1.5.parquet.hnsw-sqv.onnx.md)]

</details>
<details>
<summary>BEIR (v1.0.0): SPLADE-v3</summary>

+ [`beir-v1.0.0-trec-covid.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.splade-v3.cached.md)]
+ [`beir-v1.0.0-bioasq.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.splade-v3.cached.md)]
+ [`beir-v1.0.0-nfcorpus.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.splade-v3.cached.md)]
+ [`beir-v1.0.0-nq.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.splade-v3.cached.md)]
+ [`beir-v1.0.0-hotpotqa.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.splade-v3.cached.md)]
+ [`beir-v1.0.0-fiqa.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.splade-v3.cached.md)]
+ [`beir-v1.0.0-signal1m.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.splade-v3.cached.md)]
+ [`beir-v1.0.0-trec-news.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.splade-v3.cached.md)]
+ [`beir-v1.0.0-robust04.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.splade-v3.cached.md)]
+ [`beir-v1.0.0-arguana.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.splade-v3.cached.md)]
+ [`beir-v1.0.0-webis-touche2020.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.splade-v3.cached.md)]
+ [`beir-v1.0.0-cqadupstack-android.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.splade-v3.cached.md)]
+ [`beir-v1.0.0-cqadupstack-english.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.splade-v3.cached.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.splade-v3.cached.md)]
+ [`beir-v1.0.0-cqadupstack-gis.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.splade-v3.cached.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.splade-v3.cached.md)]
+ [`beir-v1.0.0-cqadupstack-physics.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.splade-v3.cached.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.splade-v3.cached.md)]
+ [`beir-v1.0.0-cqadupstack-stats.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.splade-v3.cached.md)]
+ [`beir-v1.0.0-cqadupstack-tex.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.splade-v3.cached.md)]
+ [`beir-v1.0.0-cqadupstack-unix.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.splade-v3.cached.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.splade-v3.cached.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.splade-v3.cached.md)]
+ [`beir-v1.0.0-quora.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.splade-v3.cached.md)]
+ [`beir-v1.0.0-dbpedia-entity.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.splade-v3.cached.md)]
+ [`beir-v1.0.0-scidocs.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.splade-v3.cached.md)]
+ [`beir-v1.0.0-fever.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.splade-v3.cached.md)]
+ [`beir-v1.0.0-climate-fever.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.splade-v3.cached.md)]
+ [`beir-v1.0.0-scifact.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.splade-v3.cached.md)]


<div></div>

+ [`beir-v1.0.0-trec-covid.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.splade-v3.onnx.md)]
+ [`beir-v1.0.0-bioasq.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.splade-v3.onnx.md)]
+ [`beir-v1.0.0-nfcorpus.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.splade-v3.onnx.md)]
+ [`beir-v1.0.0-nq.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.splade-v3.onnx.md)]
+ [`beir-v1.0.0-hotpotqa.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.splade-v3.onnx.md)]
+ [`beir-v1.0.0-fiqa.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.splade-v3.onnx.md)]
+ [`beir-v1.0.0-signal1m.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.splade-v3.onnx.md)]
+ [`beir-v1.0.0-trec-news.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.splade-v3.onnx.md)]
+ [`beir-v1.0.0-robust04.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.splade-v3.onnx.md)]
+ [`beir-v1.0.0-arguana.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.splade-v3.onnx.md)]
+ [`beir-v1.0.0-webis-touche2020.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.splade-v3.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-android.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.splade-v3.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-english.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.splade-v3.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.splade-v3.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-gis.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.splade-v3.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.splade-v3.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-physics.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.splade-v3.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.splade-v3.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-stats.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.splade-v3.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-tex.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.splade-v3.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-unix.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.splade-v3.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.splade-v3.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.splade-v3.onnx.md)]
+ [`beir-v1.0.0-quora.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.splade-v3.onnx.md)]
+ [`beir-v1.0.0-dbpedia-entity.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.splade-v3.onnx.md)]
+ [`beir-v1.0.0-scidocs.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.splade-v3.onnx.md)]
+ [`beir-v1.0.0-fever.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.splade-v3.onnx.md)]
+ [`beir-v1.0.0-climate-fever.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.splade-v3.onnx.md)]
+ [`beir-v1.0.0-scifact.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.splade-v3.onnx.md)]

</details>
<details>
<summary>BEIR (v1.0.0): SPLADE++ CoCondenser-EnsembleDistil</summary>

+ [`beir-v1.0.0-trec-covid.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-bioasq.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-nfcorpus.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-nq.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-hotpotqa.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-fiqa.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-signal1m.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-trec-news.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-robust04.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-arguana.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-webis-touche2020.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-cqadupstack-android.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-cqadupstack-english.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-cqadupstack-gis.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-cqadupstack-physics.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-cqadupstack-stats.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-cqadupstack-tex.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-cqadupstack-unix.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-quora.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-dbpedia-entity.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-scidocs.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-fever.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-climate-fever.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.splade-pp-ed.cached.md)]
+ [`beir-v1.0.0-scifact.splade-pp-ed.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.splade-pp-ed.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.splade-pp-ed.cached.md)]


<div></div>

+ [`beir-v1.0.0-trec-covid.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-bioasq.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-nfcorpus.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-nq.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-hotpotqa.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-fiqa.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-signal1m.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-trec-news.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-robust04.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-arguana.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-webis-touche2020.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-android.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-english.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-gis.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-physics.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-stats.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-tex.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-unix.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-quora.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-dbpedia-entity.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-scidocs.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-fever.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-climate-fever.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.splade-pp-ed.onnx.md)]
+ [`beir-v1.0.0-scifact.splade-pp-ed.onnx`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.splade-pp-ed.onnx.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.splade-pp-ed.onnx.md)]

</details>
<details>
<summary>BEIR (v1.0.0): uniCOIL (noexp)</summary>

+ [`beir-v1.0.0-trec-covid.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-bioasq.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-nfcorpus.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-nq.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-hotpotqa.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-fiqa.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-signal1m.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-trec-news.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-robust04.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-arguana.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-webis-touche2020.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-cqadupstack-android.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-cqadupstack-english.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-cqadupstack-gis.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-cqadupstack-physics.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-cqadupstack-stats.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-cqadupstack-tex.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-cqadupstack-unix.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-quora.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-dbpedia-entity.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-scidocs.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-fever.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-climate-fever.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.unicoil-noexp.cached.md)]
+ [`beir-v1.0.0-scifact.unicoil-noexp.cached`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.unicoil-noexp.cached.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.unicoil-noexp.cached.md)]

</details>
<details>
<summary>BEIR (v1.0.0): "flat" baseline</summary>

+ [`beir-v1.0.0-trec-covid.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.flat.md)]
+ [`beir-v1.0.0-bioasq.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.flat.md)]
+ [`beir-v1.0.0-nfcorpus.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.flat.md)]
+ [`beir-v1.0.0-nq.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.flat.md)]
+ [`beir-v1.0.0-hotpotqa.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.flat.md)]
+ [`beir-v1.0.0-fiqa.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.flat.md)]
+ [`beir-v1.0.0-signal1m.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.flat.md)]
+ [`beir-v1.0.0-trec-news.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.flat.md)]
+ [`beir-v1.0.0-robust04.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.flat.md)]
+ [`beir-v1.0.0-arguana.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.flat.md)]
+ [`beir-v1.0.0-webis-touche2020.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.flat.md)]
+ [`beir-v1.0.0-cqadupstack-android.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.flat.md)]
+ [`beir-v1.0.0-cqadupstack-english.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.flat.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.flat.md)]
+ [`beir-v1.0.0-cqadupstack-gis.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.flat.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.flat.md)]
+ [`beir-v1.0.0-cqadupstack-physics.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.flat.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.flat.md)]
+ [`beir-v1.0.0-cqadupstack-stats.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.flat.md)]
+ [`beir-v1.0.0-cqadupstack-tex.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.flat.md)]
+ [`beir-v1.0.0-cqadupstack-unix.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.flat.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.flat.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.flat.md)]
+ [`beir-v1.0.0-quora.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.flat.md)]
+ [`beir-v1.0.0-dbpedia-entity.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.flat.md)]
+ [`beir-v1.0.0-scidocs.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.flat.md)]
+ [`beir-v1.0.0-fever.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.flat.md)]
+ [`beir-v1.0.0-climate-fever.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.flat.md)]
+ [`beir-v1.0.0-scifact.flat`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.flat.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.flat.md)]

</details>
<details>
<summary>BEIR (v1.0.0): "flat" baseline with WordPiece tokenization</summary>

+ [`beir-v1.0.0-trec-covid.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.flat-wp.md)]
+ [`beir-v1.0.0-bioasq.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.flat-wp.md)]
+ [`beir-v1.0.0-nfcorpus.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.flat-wp.md)]
+ [`beir-v1.0.0-nq.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.flat-wp.md)]
+ [`beir-v1.0.0-hotpotqa.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.flat-wp.md)]
+ [`beir-v1.0.0-fiqa.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.flat-wp.md)]
+ [`beir-v1.0.0-signal1m.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.flat-wp.md)]
+ [`beir-v1.0.0-trec-news.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.flat-wp.md)]
+ [`beir-v1.0.0-robust04.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.flat-wp.md)]
+ [`beir-v1.0.0-arguana.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.flat-wp.md)]
+ [`beir-v1.0.0-webis-touche2020.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.flat-wp.md)]
+ [`beir-v1.0.0-cqadupstack-android.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.flat-wp.md)]
+ [`beir-v1.0.0-cqadupstack-english.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.flat-wp.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.flat-wp.md)]
+ [`beir-v1.0.0-cqadupstack-gis.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.flat-wp.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.flat-wp.md)]
+ [`beir-v1.0.0-cqadupstack-physics.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.flat-wp.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.flat-wp.md)]
+ [`beir-v1.0.0-cqadupstack-stats.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.flat-wp.md)]
+ [`beir-v1.0.0-cqadupstack-tex.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.flat-wp.md)]
+ [`beir-v1.0.0-cqadupstack-unix.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.flat-wp.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.flat-wp.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.flat-wp.md)]
+ [`beir-v1.0.0-quora.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.flat-wp.md)]
+ [`beir-v1.0.0-dbpedia-entity.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.flat-wp.md)]
+ [`beir-v1.0.0-scidocs.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.flat-wp.md)]
+ [`beir-v1.0.0-fever.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.flat-wp.md)]
+ [`beir-v1.0.0-climate-fever.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.flat-wp.md)]
+ [`beir-v1.0.0-scifact.flat-wp`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.flat-wp.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.flat-wp.md)]

</details>
<details>
<summary>BEIR (v1.0.0): "multifield" baseline</summary>

+ [`beir-v1.0.0-trec-covid.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-covid.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-covid.multifield.md)]
+ [`beir-v1.0.0-bioasq.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-bioasq.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-bioasq.multifield.md)]
+ [`beir-v1.0.0-nfcorpus.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nfcorpus.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nfcorpus.multifield.md)]
+ [`beir-v1.0.0-nq.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-nq.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-nq.multifield.md)]
+ [`beir-v1.0.0-hotpotqa.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-hotpotqa.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-hotpotqa.multifield.md)]
+ [`beir-v1.0.0-fiqa.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fiqa.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fiqa.multifield.md)]
+ [`beir-v1.0.0-signal1m.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-signal1m.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-signal1m.multifield.md)]
+ [`beir-v1.0.0-trec-news.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-trec-news.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-trec-news.multifield.md)]
+ [`beir-v1.0.0-robust04.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-robust04.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-robust04.multifield.md)]
+ [`beir-v1.0.0-arguana.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-arguana.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-arguana.multifield.md)]
+ [`beir-v1.0.0-webis-touche2020.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-webis-touche2020.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-webis-touche2020.multifield.md)]
+ [`beir-v1.0.0-cqadupstack-android.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-android.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-android.multifield.md)]
+ [`beir-v1.0.0-cqadupstack-english.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-english.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-english.multifield.md)]
+ [`beir-v1.0.0-cqadupstack-gaming.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gaming.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gaming.multifield.md)]
+ [`beir-v1.0.0-cqadupstack-gis.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-gis.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-gis.multifield.md)]
+ [`beir-v1.0.0-cqadupstack-mathematica.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-mathematica.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-mathematica.multifield.md)]
+ [`beir-v1.0.0-cqadupstack-physics.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-physics.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-physics.multifield.md)]
+ [`beir-v1.0.0-cqadupstack-programmers.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-programmers.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-programmers.multifield.md)]
+ [`beir-v1.0.0-cqadupstack-stats.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-stats.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-stats.multifield.md)]
+ [`beir-v1.0.0-cqadupstack-tex.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-tex.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-tex.multifield.md)]
+ [`beir-v1.0.0-cqadupstack-unix.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-unix.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-unix.multifield.md)]
+ [`beir-v1.0.0-cqadupstack-webmasters.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-webmasters.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-webmasters.multifield.md)]
+ [`beir-v1.0.0-cqadupstack-wordpress.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-cqadupstack-wordpress.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-cqadupstack-wordpress.multifield.md)]
+ [`beir-v1.0.0-quora.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-quora.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-quora.multifield.md)]
+ [`beir-v1.0.0-dbpedia-entity.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-dbpedia-entity.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-dbpedia-entity.multifield.md)]
+ [`beir-v1.0.0-scidocs.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scidocs.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scidocs.multifield.md)]
+ [`beir-v1.0.0-fever.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-fever.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-fever.multifield.md)]
+ [`beir-v1.0.0-climate-fever.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-climate-fever.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-climate-fever.multifield.md)]
+ [`beir-v1.0.0-scifact.multifield`](../src/main/resources/reproduce/from-document-collection/configs/beir-v1.0.0-scifact.multifield.yaml) [[docs](reproduce/from-document-collection/beir-v1.0.0-scifact.multifield.md)]

</details>
<details>
<summary>Mr.TyDi (v1.1): BM25 regressions</summary>

+ [`mrtydi-v1.1-ar`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ar.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-ar.md)]
+ [`mrtydi-v1.1-bn`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-bn.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-bn.md)]
+ [`mrtydi-v1.1-en`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-en.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-en.md)]
+ [`mrtydi-v1.1-fi`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-fi.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-fi.md)]
+ [`mrtydi-v1.1-id`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-id.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-id.md)]
+ [`mrtydi-v1.1-ja`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ja.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-ja.md)]
+ [`mrtydi-v1.1-ko`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ko.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-ko.md)]
+ [`mrtydi-v1.1-ru`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ru.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-ru.md)]
+ [`mrtydi-v1.1-sw`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-sw.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-sw.md)]
+ [`mrtydi-v1.1-te`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-te.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-te.md)]
+ [`mrtydi-v1.1-th`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-th.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-th.md)]


<div></div>

+ [`mrtydi-v1.1-ar-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ar-aca.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-ar-aca.md)]
+ [`mrtydi-v1.1-bn-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-bn-aca.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-bn-aca.md)]
+ [`mrtydi-v1.1-en-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-en-aca.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-en-aca.md)]
+ [`mrtydi-v1.1-fi-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-fi-aca.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-fi-aca.md)]
+ [`mrtydi-v1.1-id-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-id-aca.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-id-aca.md)]
+ [`mrtydi-v1.1-ja-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ja-aca.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-ja-aca.md)]
+ [`mrtydi-v1.1-ko-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ko-aca.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-ko-aca.md)]
+ [`mrtydi-v1.1-ru-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-ru-aca.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-ru-aca.md)]
+ [`mrtydi-v1.1-sw-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-sw-aca.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-sw-aca.md)]
+ [`mrtydi-v1.1-te-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-te-aca.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-te-aca.md)]
+ [`mrtydi-v1.1-th-aca`](../src/main/resources/reproduce/from-document-collection/configs/mrtydi-v1.1-th-aca.yaml) [[docs](reproduce/from-document-collection/mrtydi-v1.1-th-aca.md)]

</details>
<details>
<summary>MIRACL (v1.0): BM25 regressions</summary>

+ [`miracl-v1.0-ar`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ar.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-ar.md)]
+ [`miracl-v1.0-bn`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-bn.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-bn.md)]
+ [`miracl-v1.0-en`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-en.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-en.md)]
+ [`miracl-v1.0-es`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-es.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-es.md)]
+ [`miracl-v1.0-fa`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-fa.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-fa.md)]
+ [`miracl-v1.0-fi`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-fi.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-fi.md)]
+ [`miracl-v1.0-fr`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-fr.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-fr.md)]
+ [`miracl-v1.0-hi`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-hi.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-hi.md)]
+ [`miracl-v1.0-id`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-id.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-id.md)]
+ [`miracl-v1.0-ja`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ja.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-ja.md)]
+ [`miracl-v1.0-ko`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ko.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-ko.md)]
+ [`miracl-v1.0-ru`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ru.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-ru.md)]
+ [`miracl-v1.0-sw`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-sw.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-sw.md)]
+ [`miracl-v1.0-te`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-te.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-te.md)]
+ [`miracl-v1.0-th`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-th.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-th.md)]
+ [`miracl-v1.0-zh`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-zh.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-zh.md)]


<div></div>

+ [`miracl-v1.0-ar-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ar-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-ar-aca.md)]
+ [`miracl-v1.0-bn-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-bn-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-bn-aca.md)]
+ [`miracl-v1.0-en-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-en-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-en-aca.md)]
+ [`miracl-v1.0-es-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-es-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-es-aca.md)]
+ [`miracl-v1.0-fa-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-fa-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-fa-aca.md)]
+ [`miracl-v1.0-fi-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-fi-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-fi-aca.md)]
+ [`miracl-v1.0-fr-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-fr-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-fr-aca.md)]
+ [`miracl-v1.0-hi-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-hi-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-hi-aca.md)]
+ [`miracl-v1.0-id-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-id-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-id-aca.md)]
+ [`miracl-v1.0-ja-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ja-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-ja-aca.md)]
+ [`miracl-v1.0-ko-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ko-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-ko-aca.md)]
+ [`miracl-v1.0-ru-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-ru-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-ru-aca.md)]
+ [`miracl-v1.0-sw-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-sw-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-sw-aca.md)]
+ [`miracl-v1.0-te-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-te-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-te-aca.md)]
+ [`miracl-v1.0-th-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-th-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-th-aca.md)]
+ [`miracl-v1.0-zh-aca`](../src/main/resources/reproduce/from-document-collection/configs/miracl-v1.0-zh-aca.yaml) [[docs](reproduce/from-document-collection/miracl-v1.0-zh-aca.md)]

</details>
<details>
<summary>Other cross-lingual and multi-lingual regressions</summary>

+ [`ntcir8-zh`](../src/main/resources/reproduce/from-document-collection/configs/ntcir8-zh.yaml) [[docs](reproduce/from-document-collection/ntcir8-zh.md)]
+ [`clef06-fr`](../src/main/resources/reproduce/from-document-collection/configs/clef06-fr.yaml) [[docs](reproduce/from-document-collection/clef06-fr.md)]
+ [`trec02-ar`](../src/main/resources/reproduce/from-document-collection/configs/trec02-ar.yaml) [[docs](reproduce/from-document-collection/trec02-ar.md)]
+ [`fire12-bn`](../src/main/resources/reproduce/from-document-collection/configs/fire12-bn.yaml) [[docs](reproduce/from-document-collection/fire12-bn.md)]
+ [`fire12-hi`](../src/main/resources/reproduce/from-document-collection/configs/fire12-hi.yaml) [[docs](reproduce/from-document-collection/fire12-hi.md)]
+ [`fire12-en`](../src/main/resources/reproduce/from-document-collection/configs/fire12-en.yaml) [[docs](reproduce/from-document-collection/fire12-en.md)]


<div></div>

+ [`hc4-v1.0-fa`](../src/main/resources/reproduce/from-document-collection/configs/hc4-v1.0-fa.yaml) [[docs](reproduce/from-document-collection/hc4-v1.0-fa.md)]
+ [`hc4-v1.0-ru`](../src/main/resources/reproduce/from-document-collection/configs/hc4-v1.0-ru.yaml) [[docs](reproduce/from-document-collection/hc4-v1.0-ru.md)]
+ [`hc4-v1.0-zh`](../src/main/resources/reproduce/from-document-collection/configs/hc4-v1.0-zh.yaml) [[docs](reproduce/from-document-collection/hc4-v1.0-zh.md)]
+ [`hc4-neuclir22-fa`](../src/main/resources/reproduce/from-document-collection/configs/hc4-neuclir22-fa.yaml) [[docs](reproduce/from-document-collection/hc4-neuclir22-fa.md)]
+ [`hc4-neuclir22-ru`](../src/main/resources/reproduce/from-document-collection/configs/hc4-neuclir22-ru.yaml) [[docs](reproduce/from-document-collection/hc4-neuclir22-ru.md)]
+ [`hc4-neuclir22-zh`](../src/main/resources/reproduce/from-document-collection/configs/hc4-neuclir22-zh.yaml) [[docs](reproduce/from-document-collection/hc4-neuclir22-zh.md)]
+ [`hc4-neuclir22-fa-en`](../src/main/resources/reproduce/from-document-collection/configs/hc4-neuclir22-fa-en.yaml) [[docs](reproduce/from-document-collection/hc4-neuclir22-fa-en.md)]
+ [`hc4-neuclir22-ru-en`](../src/main/resources/reproduce/from-document-collection/configs/hc4-neuclir22-ru-en.yaml) [[docs](reproduce/from-document-collection/hc4-neuclir22-ru-en.md)]
+ [`hc4-neuclir22-zh-en`](../src/main/resources/reproduce/from-document-collection/configs/hc4-neuclir22-zh-en.yaml) [[docs](reproduce/from-document-collection/hc4-neuclir22-zh-en.md)]


<div></div>

+ [`neuclir22-fa-qt`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-fa-qt.yaml) [[docs](reproduce/from-document-collection/neuclir22-fa-qt.md)]
+ [`neuclir22-fa-dt`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-fa-dt.yaml) [[docs](reproduce/from-document-collection/neuclir22-fa-dt.md)]
+ [`neuclir22-ru-qt`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-ru-qt.yaml) [[docs](reproduce/from-document-collection/neuclir22-ru-qt.md)]
+ [`neuclir22-ru-dt`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-ru-dt.yaml) [[docs](reproduce/from-document-collection/neuclir22-ru-dt.md)]
+ [`neuclir22-zh-qt`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-zh-qt.yaml) [[docs](reproduce/from-document-collection/neuclir22-zh-qt.md)]
+ [`neuclir22-zh-dt`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-zh-dt.yaml) [[docs](reproduce/from-document-collection/neuclir22-zh-dt.md)]
+ [`neuclir22-fa-qt-splade`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-fa-qt-splade.yaml) [[docs](reproduce/from-document-collection/neuclir22-fa-qt-splade.md)]
+ [`neuclir22-fa-dt-splade`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-fa-dt-splade.yaml) [[docs](reproduce/from-document-collection/neuclir22-fa-dt-splade.md)]
+ [`neuclir22-ru-qt-splade`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-ru-qt-splade.yaml) [[docs](reproduce/from-document-collection/neuclir22-ru-qt-splade.md)]
+ [`neuclir22-ru-dt-splade`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-ru-dt-splade.yaml) [[docs](reproduce/from-document-collection/neuclir22-ru-dt-splade.md)]
+ [`neuclir22-zh-qt-splade`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-zh-qt-splade.yaml) [[docs](reproduce/from-document-collection/neuclir22-zh-qt-splade.md)]
+ [`neuclir22-zh-dt-splade`](../src/main/resources/reproduce/from-document-collection/configs/neuclir22-zh-dt-splade.yaml) [[docs](reproduce/from-document-collection/neuclir22-zh-dt-splade.md)]


<div></div>

+ [`ciral-v1.0-ha`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-ha.yaml) [[docs](reproduce/from-document-collection/ciral-v1.0-ha.md)]
+ [`ciral-v1.0-so`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-so.yaml) [[docs](reproduce/from-document-collection/ciral-v1.0-so.md)]
+ [`ciral-v1.0-sw`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-sw.yaml) [[docs](reproduce/from-document-collection/ciral-v1.0-sw.md)]
+ [`ciral-v1.0-yo`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-yo.yaml) [[docs](reproduce/from-document-collection/ciral-v1.0-yo.md)]
+ [`ciral-v1.0-ha-en`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-ha-en.yaml) [[docs](reproduce/from-document-collection/ciral-v1.0-ha-en.md)]
+ [`ciral-v1.0-so-en`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-so-en.yaml) [[docs](reproduce/from-document-collection/ciral-v1.0-so-en.md)]
+ [`ciral-v1.0-sw-en`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-sw-en.yaml) [[docs](reproduce/from-document-collection/ciral-v1.0-sw-en.md)]
+ [`ciral-v1.0-yo-en`](../src/main/resources/reproduce/from-document-collection/configs/ciral-v1.0-yo-en.yaml) [[docs](reproduce/from-document-collection/ciral-v1.0-yo-en.md)]

</details>
<details>
<summary>BRIGHT: BM25</summary>

+ [`bright-aops`](../src/main/resources/reproduce/from-document-collection/configs/bright-aops.yaml) [[docs](reproduce/from-document-collection/bright-aops.md)]
+ [`bright-biology`](../src/main/resources/reproduce/from-document-collection/configs/bright-biology.yaml) [[docs](reproduce/from-document-collection/bright-biology.md)]
+ [`bright-earth-science`](../src/main/resources/reproduce/from-document-collection/configs/bright-earth-science.yaml) [[docs](reproduce/from-document-collection/bright-earth-science.md)]
+ [`bright-economics`](../src/main/resources/reproduce/from-document-collection/configs/bright-economics.yaml) [[docs](reproduce/from-document-collection/bright-economics.md)]
+ [`bright-leetcode`](../src/main/resources/reproduce/from-document-collection/configs/bright-leetcode.yaml) [[docs](reproduce/from-document-collection/bright-leetcode.md)]
+ [`bright-pony`](../src/main/resources/reproduce/from-document-collection/configs/bright-pony.yaml) [[docs](reproduce/from-document-collection/bright-pony.md)]
+ [`bright-psychology`](../src/main/resources/reproduce/from-document-collection/configs/bright-psychology.yaml) [[docs](reproduce/from-document-collection/bright-psychology.md)]
+ [`bright-robotics`](../src/main/resources/reproduce/from-document-collection/configs/bright-robotics.yaml) [[docs](reproduce/from-document-collection/bright-robotics.md)]
+ [`bright-stackoverflow`](../src/main/resources/reproduce/from-document-collection/configs/bright-stackoverflow.yaml) [[docs](reproduce/from-document-collection/bright-stackoverflow.md)]
+ [`bright-sustainable-living`](../src/main/resources/reproduce/from-document-collection/configs/bright-sustainable-living.yaml) [[docs](reproduce/from-document-collection/bright-sustainable-living.md)]
+ [`bright-theoremqa-questions`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-questions.yaml) [[docs](reproduce/from-document-collection/bright-theoremqa-questions.md)]
+ [`bright-theoremqa-theorems`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-theorems.yaml) [[docs](reproduce/from-document-collection/bright-theoremqa-theorems.md)]


<div></div>

+ [`bright-aops.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-aops.bm25qs.yaml) [[docs](reproduce/from-document-collection/bright-aops.bm25qs.md)]
+ [`bright-biology.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-biology.bm25qs.yaml) [[docs](reproduce/from-document-collection/bright-biology.bm25qs.md)]
+ [`bright-earth-science.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-earth-science.bm25qs.yaml) [[docs](reproduce/from-document-collection/bright-earth-science.bm25qs.md)]
+ [`bright-economics.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-economics.bm25qs.yaml) [[docs](reproduce/from-document-collection/bright-economics.bm25qs.md)]
+ [`bright-leetcode.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-leetcode.bm25qs.yaml) [[docs](reproduce/from-document-collection/bright-leetcode.bm25qs.md)]
+ [`bright-pony.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-pony.bm25qs.yaml) [[docs](reproduce/from-document-collection/bright-pony.bm25qs.md)]
+ [`bright-psychology.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-psychology.bm25qs.yaml) [[docs](reproduce/from-document-collection/bright-psychology.bm25qs.md)]
+ [`bright-robotics.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-robotics.bm25qs.yaml) [[docs](reproduce/from-document-collection/bright-robotics.bm25qs.md)]
+ [`bright-stackoverflow.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-stackoverflow.bm25qs.yaml) [[docs](reproduce/from-document-collection/bright-stackoverflow.bm25qs.md)]
+ [`bright-sustainable-living.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-sustainable-living.bm25qs.yaml) [[docs](reproduce/from-document-collection/bright-sustainable-living.bm25qs.md)]
+ [`bright-theoremqa-questions.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-questions.bm25qs.yaml) [[docs](reproduce/from-document-collection/bright-theoremqa-questions.bm25qs.md)]
+ [`bright-theoremqa-theorems.bm25qs`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-theorems.bm25qs.yaml) [[docs](reproduce/from-document-collection/bright-theoremqa-theorems.bm25qs.md)]

</details>
<details>
<summary>BRIGHT: SPLADE-v3</summary>

+ [`bright-aops.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-aops.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/bright-aops.splade-v3.onnx.md)]
+ [`bright-biology.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-biology.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/bright-biology.splade-v3.onnx.md)]
+ [`bright-earth-science.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-earth-science.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/bright-earth-science.splade-v3.onnx.md)]
+ [`bright-economics.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-economics.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/bright-economics.splade-v3.onnx.md)]
+ [`bright-leetcode.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-leetcode.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/bright-leetcode.splade-v3.onnx.md)]
+ [`bright-pony.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-pony.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/bright-pony.splade-v3.onnx.md)]
+ [`bright-psychology.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-psychology.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/bright-psychology.splade-v3.onnx.md)]
+ [`bright-robotics.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-robotics.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/bright-robotics.splade-v3.onnx.md)]
+ [`bright-stackoverflow.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-stackoverflow.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/bright-stackoverflow.splade-v3.onnx.md)]
+ [`bright-sustainable-living.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-sustainable-living.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/bright-sustainable-living.splade-v3.onnx.md)]
+ [`bright-theoremqa-questions.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-questions.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/bright-theoremqa-questions.splade-v3.onnx.md)]
+ [`bright-theoremqa-theorems.splade-v3.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-theorems.splade-v3.onnx.yaml) [[docs](reproduce/from-document-collection/bright-theoremqa-theorems.splade-v3.onnx.md)]


<div></div>

+ [`bright-aops.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-aops.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/bright-aops.splade-v3.cached.md)]
+ [`bright-biology.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-biology.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/bright-biology.splade-v3.cached.md)]
+ [`bright-earth-science.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-earth-science.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/bright-earth-science.splade-v3.cached.md)]
+ [`bright-economics.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-economics.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/bright-economics.splade-v3.cached.md)]
+ [`bright-leetcode.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-leetcode.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/bright-leetcode.splade-v3.cached.md)]
+ [`bright-pony.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-pony.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/bright-pony.splade-v3.cached.md)]
+ [`bright-psychology.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-psychology.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/bright-psychology.splade-v3.cached.md)]
+ [`bright-robotics.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-robotics.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/bright-robotics.splade-v3.cached.md)]
+ [`bright-stackoverflow.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-stackoverflow.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/bright-stackoverflow.splade-v3.cached.md)]
+ [`bright-sustainable-living.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-sustainable-living.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/bright-sustainable-living.splade-v3.cached.md)]
+ [`bright-theoremqa-questions.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-questions.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/bright-theoremqa-questions.splade-v3.cached.md)]
+ [`bright-theoremqa-theorems.splade-v3.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-theorems.splade-v3.cached.yaml) [[docs](reproduce/from-document-collection/bright-theoremqa-theorems.splade-v3.cached.md)]

</details>
<details>
<summary>BRIGHT: BGE-large-en-v1.5</summary>

+ [`bright-aops.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-aops.bge-large-en-v1.5.flat.onnx.yaml) [[docs](reproduce/from-document-collection/bright-aops.bge-large-en-v1.5.flat.onnx.md)]
+ [`bright-biology.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-biology.bge-large-en-v1.5.flat.onnx.yaml) [[docs](reproduce/from-document-collection/bright-biology.bge-large-en-v1.5.flat.onnx.md)]
+ [`bright-earth-science.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-earth-science.bge-large-en-v1.5.flat.onnx.yaml) [[docs](reproduce/from-document-collection/bright-earth-science.bge-large-en-v1.5.flat.onnx.md)]
+ [`bright-economics.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-economics.bge-large-en-v1.5.flat.onnx.yaml) [[docs](reproduce/from-document-collection/bright-economics.bge-large-en-v1.5.flat.onnx.md)]
+ [`bright-leetcode.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-leetcode.bge-large-en-v1.5.flat.onnx.yaml) [[docs](reproduce/from-document-collection/bright-leetcode.bge-large-en-v1.5.flat.onnx.md)]
+ [`bright-pony.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-pony.bge-large-en-v1.5.flat.onnx.yaml) [[docs](reproduce/from-document-collection/bright-pony.bge-large-en-v1.5.flat.onnx.md)]
+ [`bright-psychology.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-psychology.bge-large-en-v1.5.flat.onnx.yaml) [[docs](reproduce/from-document-collection/bright-psychology.bge-large-en-v1.5.flat.onnx.md)]
+ [`bright-robotics.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-robotics.bge-large-en-v1.5.flat.onnx.yaml) [[docs](reproduce/from-document-collection/bright-robotics.bge-large-en-v1.5.flat.onnx.md)]
+ [`bright-stackoverflow.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-stackoverflow.bge-large-en-v1.5.flat.onnx.yaml) [[docs](reproduce/from-document-collection/bright-stackoverflow.bge-large-en-v1.5.flat.onnx.md)]
+ [`bright-sustainable-living.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-sustainable-living.bge-large-en-v1.5.flat.onnx.yaml) [[docs](reproduce/from-document-collection/bright-sustainable-living.bge-large-en-v1.5.flat.onnx.md)]
+ [`bright-theoremqa-questions.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-questions.bge-large-en-v1.5.flat.onnx.yaml) [[docs](reproduce/from-document-collection/bright-theoremqa-questions.bge-large-en-v1.5.flat.onnx.md)]
+ [`bright-theoremqa-theorems.bge-large-en-v1.5.flat.onnx`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-theorems.bge-large-en-v1.5.flat.onnx.yaml) [[docs](reproduce/from-document-collection/bright-theoremqa-theorems.bge-large-en-v1.5.flat.onnx.md)]


<div></div>

+ [`bright-aops.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-aops.bge-large-en-v1.5.flat.cached.yaml) [[docs](reproduce/from-document-collection/bright-aops.bge-large-en-v1.5.flat.cached.md)]
+ [`bright-biology.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-biology.bge-large-en-v1.5.flat.cached.yaml) [[docs](reproduce/from-document-collection/bright-biology.bge-large-en-v1.5.flat.cached.md)]
+ [`bright-earth-science.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-earth-science.bge-large-en-v1.5.flat.cached.yaml) [[docs](reproduce/from-document-collection/bright-earth-science.bge-large-en-v1.5.flat.cached.md)]
+ [`bright-economics.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-economics.bge-large-en-v1.5.flat.cached.yaml) [[docs](reproduce/from-document-collection/bright-economics.bge-large-en-v1.5.flat.cached.md)]
+ [`bright-leetcode.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-leetcode.bge-large-en-v1.5.flat.cached.yaml) [[docs](reproduce/from-document-collection/bright-leetcode.bge-large-en-v1.5.flat.cached.md)]
+ [`bright-pony.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-pony.bge-large-en-v1.5.flat.cached.yaml) [[docs](reproduce/from-document-collection/bright-pony.bge-large-en-v1.5.flat.cached.md)]
+ [`bright-psychology.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-psychology.bge-large-en-v1.5.flat.cached.yaml) [[docs](reproduce/from-document-collection/bright-psychology.bge-large-en-v1.5.flat.cached.md)]
+ [`bright-robotics.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-robotics.bge-large-en-v1.5.flat.cached.yaml) [[docs](reproduce/from-document-collection/bright-robotics.bge-large-en-v1.5.flat.cached.md)]
+ [`bright-stackoverflow.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-stackoverflow.bge-large-en-v1.5.flat.cached.yaml) [[docs](reproduce/from-document-collection/bright-stackoverflow.bge-large-en-v1.5.flat.cached.md)]
+ [`bright-sustainable-living.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-sustainable-living.bge-large-en-v1.5.flat.cached.yaml) [[docs](reproduce/from-document-collection/bright-sustainable-living.bge-large-en-v1.5.flat.cached.md)]
+ [`bright-theoremqa-questions.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-questions.bge-large-en-v1.5.flat.cached.yaml) [[docs](reproduce/from-document-collection/bright-theoremqa-questions.bge-large-en-v1.5.flat.cached.md)]
+ [`bright-theoremqa-theorems.bge-large-en-v1.5.flat.cached`](../src/main/resources/reproduce/from-document-collection/configs/bright-theoremqa-theorems.bge-large-en-v1.5.flat.cached.yaml) [[docs](reproduce/from-document-collection/bright-theoremqa-theorems.bge-large-en-v1.5.flat.cached.md)]

</details>
<details>
<summary>Other regressions (TREC, etc.)</summary>

+ [`backgroundlinking18`](../src/main/resources/reproduce/from-document-collection/configs/backgroundlinking18.yaml) [[docs](reproduce/from-document-collection/backgroundlinking18.md)]
+ [`backgroundlinking19`](../src/main/resources/reproduce/from-document-collection/configs/backgroundlinking19.yaml) [[docs](reproduce/from-document-collection/backgroundlinking19.md)]
+ [`backgroundlinking20`](../src/main/resources/reproduce/from-document-collection/configs/backgroundlinking20.yaml) [[docs](reproduce/from-document-collection/backgroundlinking20.md)]
+ [`disk12`](../src/main/resources/reproduce/from-document-collection/configs/disk12.yaml) [[docs](reproduce/from-document-collection/disk12.md)]
+ [`disk45`](../src/main/resources/reproduce/from-document-collection/configs/disk45.yaml) [[docs](reproduce/from-document-collection/disk45.md)]
+ [`robust05`](../src/main/resources/reproduce/from-document-collection/configs/robust05.yaml) [[docs](reproduce/from-document-collection/robust05.md)]
+ [`core17`](../src/main/resources/reproduce/from-document-collection/configs/core17.yaml) [[docs](reproduce/from-document-collection/core17.md)]
+ [`core18`](../src/main/resources/reproduce/from-document-collection/configs/core18.yaml) [[docs](reproduce/from-document-collection/core18.md)]
+ [`mb11`](../src/main/resources/reproduce/from-document-collection/configs/mb11.yaml) [[docs](reproduce/from-document-collection/mb11.md)]
+ [`mb13`](../src/main/resources/reproduce/from-document-collection/configs/mb13.yaml) [[docs](reproduce/from-document-collection/mb13.md)]
+ [`car17v1.5`](../src/main/resources/reproduce/from-document-collection/configs/car17v1.5.yaml) [[docs](reproduce/from-document-collection/car17v1.5.md)]
+ [`car17v2.0`](../src/main/resources/reproduce/from-document-collection/configs/car17v2.0.yaml) [[docs](reproduce/from-document-collection/car17v2.0.md)]
+ [`car17v2.0-doc2query`](../src/main/resources/reproduce/from-document-collection/configs/car17v2.0-doc2query.yaml) [[docs](reproduce/from-document-collection/car17v2.0-doc2query.md)]
+ [`wt10g`](../src/main/resources/reproduce/from-document-collection/configs/wt10g.yaml) [[docs](reproduce/from-document-collection/wt10g.md)]
+ [`gov2`](../src/main/resources/reproduce/from-document-collection/configs/gov2.yaml) [[docs](reproduce/from-document-collection/gov2.md)]
+ [`cw09b`](../src/main/resources/reproduce/from-document-collection/configs/cw09b.yaml) [[docs](reproduce/from-document-collection/cw09b.md)]
+ [`cw12b13`](../src/main/resources/reproduce/from-document-collection/configs/cw12b13.yaml) [[docs](reproduce/from-document-collection/cw12b13.md)]
+ [`cw12`](../src/main/resources/reproduce/from-document-collection/configs/cw12.yaml) [[docs](reproduce/from-document-collection/cw12.md)]
+ [`fever`](../src/main/resources/reproduce/from-document-collection/configs/fever.yaml) [[docs](reproduce/from-document-collection/fever.md)]
+ [`wikipedia-dpr-100w-bm25`](../src/main/resources/reproduce/from-document-collection/configs/wikipedia-dpr-100w-bm25.yaml) [[docs](reproduce/from-document-collection/wikipedia-dpr-100w-bm25.md)]
+ [`wiki-all-6-3-tamber-bm25`](../src/main/resources/reproduce/from-document-collection/configs/wiki-all-6-3-tamber-bm25.yaml) [[docs](reproduce/from-document-collection/wiki-all-6-3-tamber-bm25.md)]

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