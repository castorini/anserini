# Anserini: Prebuilt Indexes

Anserini ships with a number of prebuilt indexes.
This means that various indexes (inverted indexes, HNSW indexes, etc.) for common collections used in NLP and IR research have already been built and just needs to be downloaded (from UWaterloo and Hugging Face servers), which Anserini will handle automatically for you.

Bindings for the available prebuilt indexes are in [`io.anserini.index.IndexInfo`](https://github.com/castorini/anserini/blob/master/src/main/java/io/anserini/index/IndexInfo.java) as Java enums.
For example, if you specify `-index msmarco-v1-passage`, Anserini will know that you mean the Lucene index of the MS MARCO V1 passage corpus.
It will then download the index from the specified location(s) and cache locally.
All of this happens automagically!

## Getting Started

To download a prebuilt index and view its statistics, you can use the following command:

```bash
bin/run.sh io.anserini.index.IndexReaderUtils -index cacm -stats
```

The output of the above command will be:

```
Index statistics
----------------
documents:             3204
documents (non-empty): 3204
unique terms:          14363
total terms:           320968
index_path:            /home/jimmylin/.cache/pyserini/indexes/lucene-index.cacm.20221005.252b5e.cfe14d543c6a27f4d742fb2d0099b8e0
total_size:            2.9 MB
```

Note that for inverted indexes, unless the underlying index was built with the `-optimize` option (i.e., merging all index segments into a single segment), `unique_terms` will show -1.
Nope, that's not a bug.

## Managing Indexes

Downloaded indexes are by default stored in `~/.cache/pyserini/indexes/`.
(Yes, `pyserini`, that's not a bug &mdash; this is so prebuilt indexes can be shared between Pyserini and Anserini.)
You can specify a custom cache directory by setting the environment variable `$ANSERINI_INDEX_CACHE` or the system property `anserini.index.cache`.

Another helpful tip is to download and manage the indexes by hand.
As an example, from [`IndexInfo`](https://github.com/castorini/anserini/blob/master/src/main/java/io/anserini/index/IndexInfo.java) you can see that `msmarco-v1-passage` can be downloaded from:

```
https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/resolve/main/passage/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-passage.20221004.252b5e.tar.gz
```

The tarball has an MD5 checksum of `678876e8c99a89933d553609a0fd8793`.

You can download, verify, unpack, and put the index anywhere you want.
With `-index /path/to/index/` you'll get exactly the same output as `-index msmarco-v1-passage`, except now you've got fine-grained control over managing the index.

By manually managing indexes, you can share indexes between multiple users to conserve space.
The schema of the index location in `~/.cache/pyserini/indexes/` is the tarball name (after unpacking), followed by a dot and the checksum, so `msmarco-v1-passage` lives in following location:

```
~/.cache/pyserini/indexes/lucene-inverted.msmarco-v1-passage.20221004.252b5e.678876e8c99a89933d553609a0fd8793
```

You can download the index once, put in a common location, and have each user symlink to the actual index location.
The source of the symlink would conform to the schema above, and the target of the symlink would be where your index actually resides.

## Recovering from Partial Downloads

A common issue is recovering from partial downloads, for example, if you abort the downloading of a large index tarball.
In the standard flow, Anserini downloads the tarball from the servers, verifies the checksum, and then unpacks the tarball.
If this process is interrupted, you'll end up in an inconsistent state.

To recover, go to `~/.cache/pyserini/indexes/` or your custom cache directory and remove any tarballs (i.e., `.tar.gz` files).
If there are any partially unpacked indexes, remove those also.
Then start over (e.g., rerun the command you were running before).

## Available Prebuilt Indexes

Below is a summary of the prebuilt indexes that are currently available.

Note that this page is automatically generated from [this test case](../src/test/java/io/anserini/doc/GeneratePrebuiltIndexesDocTest.java).
This means that the page is updated with every (successful) build.
Therefore, do not modify this page directly; modify the test case instead.

### Lucene HNSW Indexes
<details>
<summary>MS MARCO</summary>
<dl>
<dt></dt><b><code>msmarco-v1-passage.cosdpr-distil.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-hnsw/cosdpr-distil/lucene-hnsw.msmarco-v1-passage.cosdpr-distil.20240108.825148.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of the MS MARCO V1 passage corpus encoded by cos-DPR Distil
</dd>
<dt></dt><b><code>msmarco-v1-passage.cosdpr-distil.hnsw-int8</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-hnsw/cosdpr-distil/lucene-hnsw.msmarco-v1-passage.cosdpr-distil.20240108.825148.README.md">readme</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V1 passage corpus encoded by cos-DPR Distil
</dd>
<dt></dt><b><code>msmarco-v1-passage.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.msmarco-v1-passage.bge-base-en-v1.5.20240117.53514b.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of the MS MARCO V1 passage corpus encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>msmarco-v1-passage.bge-base-en-v1.5.hnsw-int8</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.msmarco-v1-passage.bge-base-en-v1.5.20240117.53514b.README.md">readme</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V1 passage corpus encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>msmarco-v1-passage.cohere-embed-english-v3.0.hnsw</code></b>
[<a href="">readme</a>]
<dd>Anserini Lucene HNSW index of the MS MARCO V1 passage corpus encoded by Cohere embed-english-v3.0
</dd>
<dt></dt><b><code>msmarco-v1-passage.cohere-embed-english-v3.0.hnsw-int8</code></b>
[<a href="">readme</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V1 passage corpus encoded by Cohere embed-english-v3.0
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">readme</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard00) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard01.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">readme</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard01) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard02.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">readme</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard02) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard03.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">readme</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard03) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard04.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">readme</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard04) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard05.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">readme</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard05) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard06.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">readme</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard06) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard07.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">readme</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard07) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard08.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">readme</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard08) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard09.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">readme</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard09) encoded by Snowflake's arctic-embed-l model
</dd>
</dl>
</details>

### Lucene Impact Indexes
<details>
<summary>MS MARCO</summary>
<dl>
<dt></dt><b><code>msmarco-v1-passage.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/splade-pp/lucene-inverted.msmarco-v1-passage.splade-pp.20230524.a59610.README.md">readme</a>]
<dd>Anserini Lucene impact index of the MS MARCO V1 passage corpus encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>msmarco-v1-passage.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/splade-v3/lucene-inverted.msmarco-v1-passage.splade-v3.20250329.4f4c68.README.md">readme</a>]
<dd>Anserini Lucene impact index of the MS MARCO passage corpus encoded by SPLADE-v3
</dd>
<dt></dt><b><code>msmarco-v1-doc-segmented.unicoil-noexp</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v1-doc-segmented.unicoil-noexp.20221005.252b5e.README.md">readme</a>]
<dd>Anserini Lucene impact index of the MS MARCO V1 segmented document corpus for uniCOIL (noexp), with title/segment encoding
</dd>
<dt></dt><b><code>msmarco-v1-doc-segmented.unicoil</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v1-doc-segmented.unicoil.20221005.252b5e.README.md">readme</a>]
<dd>Anserini Lucene impact index of the MS MARCO V1 segmented document corpus for uniCOIL, with title/segment encoding
</dd>
<dt></dt><b><code>msmarco-v2-passage.unicoil-noexp-0shot</code></b>
[<a href="https://github.com/castorini/pyserini/blob/c386df79cb7443361e49a5396a27fcbc713d008c/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.unicoil-noexp-0shot.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene impact index of the MS MARCO V2 passage corpus for uniCOIL (noexp)
</dd>
<dt></dt><b><code>msmarco-v2-passage.unicoil-0shot</code></b>
[<a href="https://github.com/castorini/pyserini/blob/c386df79cb7443361e49a5396a27fcbc713d008c/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.unicoil-0shot.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene impact index of the MS MARCO V2 passage corpus for uniCOIL
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented.unicoil-noexp-0shot</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.unicoil-noexp-0shot.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene impact index of the MS MARCO V2 segmented document corpus for uniCOIL (noexp) with title prepended
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented.unicoil-0shot</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.unicoil-0shot.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene impact index of the MS MARCO V2 segmented document corpus for uniCOIL, with title prepended
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented.splade-v3</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc-segmented.splade-v3.20250707.4039c3.README.md">readme</a>]
<dd>Anserini Lucene impact index of the MS MARCO V2.1 segmented document corpus encoded by SPLADE-v3
</dd>
</dl>
</details>

### Lucene Inverted Indexes
<details>
<summary>MS MARCO</summary>
<dl>
<dt></dt><b><code>msmarco-v1-passage</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-passage.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 passage corpus
</dd>
<dt></dt><b><code>msmarco-v1-passage-slim</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-passage.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 passage corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v1-passage-full</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-passage.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 passage corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v1-passage.d2q-t5</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-passage.d2q-t5.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 passage corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v1-passage.d2q-t5-docvectors</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-passage.d2q-t5.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 passage corpus with doc2query-T5 expansions (with stored docvectors)
</dd>
<dt></dt><b><code>msmarco-v1-doc</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v1-doc.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 document corpus.
</dd>
<dt></dt><b><code>msmarco-v1-doc-slim</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v1-doc.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 document corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v1-doc-full</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v1-doc.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 document corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v1-doc.d2q-t5</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v1-doc.d2q-t5.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 document corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v1-doc.d2q-t5-docvectors</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v1-doc.d2q-t5.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 document corpus with doc2query-T5 expansions (with stored docvectors)
</dd>
<dt></dt><b><code>msmarco-v1-doc-segmented</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v1-doc-segmented.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 segmented document corpus
</dd>
<dt></dt><b><code>msmarco-v1-doc-segmented-slim</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v1-doc-segmented.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 segmented document corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v1-doc-segmented-full</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v1-doc-segmented.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 segmented document corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v1-doc-segmented.d2q-t5</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v1-doc-segmented.d2q-t5.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 segmented document corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v1-doc-segmented.d2q-t5-docvectors</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v1-doc-segmented.d2q-t5.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 segmented document corpus with doc2query-T5 expansions (with stored docvectors)
</dd>
<dt></dt><b><code>msmarco-v2-passage</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 passage corpus
</dd>
<dt></dt><b><code>msmarco-v2-passage-slim</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 passage corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v2-passage-full</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 passage corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v2-passage.d2q-t5</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.d2q-t5.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 passage corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v2-passage.d2q-t5-docvectors</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.d2q-t5.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 passage corpus with doc2query-T5 expansions (with stored docvectors)
</dd>
<dt></dt><b><code>msmarco-v2-doc</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 document corpus
</dd>
<dt></dt><b><code>msmarco-v2-doc-slim</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 document corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v2-doc-full</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 document corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v2-doc.d2q-t5</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc.d2q-t5.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 document corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v2-doc.d2q-t5-docvectors</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc.d2q-t5.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 document corpus with doc2query-T5 expansions (with stored docvectors)
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 segmented document corpus
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented-slim</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 segmented document corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented-full</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 segmented document corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented.d2q-t5</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.d2q-t5.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 segmented document corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented.d2q-t5-docvectors</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.d2q-t5.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 segmented document corpus with doc2query-T5 expansions (with stored docvectors)
</dd>
<dt></dt><b><code>msmarco-v2.1-doc</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc.20240418.4f9675.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2.1 document corpus
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-slim</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc.20240418.4f9675.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2.1 document corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-full</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc.20240418.4f9675.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2.1 document corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc-segmented.20240418.4f9675.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2.1 segmented document corpus
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-slim</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc-segmented.20240418.4f9675.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2.1 segmented document corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-full</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc-segmented.20240418.4f9675.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2.1 segmented document corpus ('full' version)
</dd>
</dl>
</details>
<details>
<summary>Other</summary>
<dl>
<dt></dt><b><code>cacm</code></b>
[<a href="">readme</a>]
<dd>Anserini Lucene inverted index of the CACM corpus
</dd>
</dl>
</details>

