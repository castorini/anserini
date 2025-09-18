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

### Lucene Flat Vector Indexes
<details>
<summary>BEIR</summary>
<dl>
<dt></dt><b><code>beir-v1.0.0-trec-covid.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'trec-covid' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-bioasq.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'bioasq' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-nfcorpus.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'nfcorpus' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-nq.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'nq' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-hotpotqa.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'hotpotqa' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-fiqa.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'fiqa' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-signal1m.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'signal1m' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-news.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'trec-news' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-robust04.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'robust04' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-arguana.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'arguana' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'webis-touche2020' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-android' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-english' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-gaming' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-gis' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-mathematica' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-physics' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-programmers' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-stats' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-tex' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-unix' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-webmasters' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-wordpress' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-quora.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'quora' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'dbpedia-entity' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-scidocs.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'scidocs' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-fever.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'fever' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-climate-fever.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'climate-fever' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-scifact.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20240618.6cf601.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'scifact' encoded by BGE-base-en-v1.5
</dd>
</dl>
</details>
<details>
<summary>BRIGHT</summary>
<dl>
<dt></dt><b><code>bright-biology.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'biology' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-earth-science.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'earth-science' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-economics.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'economics' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-psychology.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'psychology' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-robotics.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'robotics' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-stackoverflow.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'stackoverflow' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-sustainable-living.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'sustainable-living' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-pony.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'pony' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-leetcode.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'leetcode' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-aops.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'aops' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-theoremqa-theorems.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'theoremqa-theorems' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-theoremqa-questions.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">readme</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'theoremqa-questions' encoded by BGE-large-en-v1.5
</dd>
</dl>
</details>

### Lucene HNSW Indexes
<details>
<summary>BEIR</summary>
<dl>
<dt></dt><b><code>beir-v1.0.0-trec-covid.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'trec-covid' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-bioasq.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'bioasq' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-nfcorpus.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'nfcorpus' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-nq.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'nq' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-hotpotqa.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'hotpotqa' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-fiqa.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'fiqa' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-signal1m.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'signal1m' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-news.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'trec-news' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-robust04.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'robust04' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-arguana.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'arguana' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'webis-touche2020' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-android' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-english' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-gaming' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-gis' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-mathematica' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-physics' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-programmers' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-stats' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-tex' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-unix' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-webmasters' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-wordpress' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-quora.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'quora' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'dbpedia-entity' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-scidocs.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'scidocs' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-fever.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'fever' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-climate-fever.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'climate-fever' encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>beir-v1.0.0-scifact.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">readme</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'scifact' encoded by BGE-base-en-v1.5
</dd>
</dl>
</details>
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
<summary>BEIR</summary>
<dl>
<dt></dt><b><code>beir-v1.0.0-trec-covid.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'trec-covid' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-bioasq.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'bioasq' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-nfcorpus.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'nfcorpus' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-nq.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'nq' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-hotpotqa.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'hotpotqa' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-fiqa.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'fiqa' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-signal1m.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'signal1m' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-news.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'trec-news' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-robust04.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'robust04' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-arguana.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'arguana' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-webis-touche2020.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'webis-touche2020' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-android.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-android' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-english.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-english' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gaming.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-gaming' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gis.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-gis' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-mathematica' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-physics.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-physics' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-programmers.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-programmers' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-stats.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-stats' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-tex.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-tex' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-unix.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-unix' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-webmasters' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-wordpress' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-quora.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'quora' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-dbpedia-entity.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'dbpedia-entity' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-scidocs.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'scidocs' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-fever.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'fever' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-climate-fever.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'climate-fever' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-scifact.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'scifact' encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-covid.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'trec-covid' collection 'trec-covid' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-bioasq.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'bioasq' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-nfcorpus.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'nfcorpus' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-nq.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'nq' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-hotpotqa.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'hotpotqa' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-fiqa.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'fiqa' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-signal1m.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'signal1m' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-news.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'trec-news' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-robust04.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'robust04' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-arguana.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'arguana' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-webis-touche2020.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'webis-touche2020' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-android.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-android' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-english.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-english' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gaming.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-gaming' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gis.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-gis' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-mathematica.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-mathematica' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-physics.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-physics' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-programmers.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-programmers' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-stats.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-stats' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-tex.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-tex' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-unix.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-unix' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-webmasters.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-webmasters' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-wordpress.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-wordpress' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-quora.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'quora' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-dbpedia-entity.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'dbpedia-entity' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-scidocs.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'scidocs' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-fever.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'fever' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-climate-fever.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'climate-fever' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-scifact.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">readme</a>]
<dd>Anserini Lucene impact index of BEIR collection 'scifact' encoded by SPLADE-v3
</dd>
</dl>
</details>
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
<details>
<summary>BRIGHT</summary>
<dl>
<dt></dt><b><code>bright-biology.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">readme</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'biology' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-earth-science.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">readme</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'earth-science' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-economics.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">readme</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'economics' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-psychology.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">readme</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'psychology' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-robotics.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">readme</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'robotics' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-stackoverflow.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">readme</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'stackoverflow' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-sustainable-living.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">readme</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'sustainable-living' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-pony.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">readme</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'pony' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-leetcode.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">readme</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'leetcode' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-aops.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">readme</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'aops' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-theoremqa-theorems.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">readme</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'theoremqa-theorems' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-theoremqa-questions.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">readme</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'theoremqa-questions' encoded by SPLADE-v3
</dd>
</dl>
</details>

### Lucene Inverted Indexes
<details>
<summary>BEIR</summary>
<dl>
<dt></dt><b><code>beir-v1.0.0-trec-covid.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'trec-covid'
</dd>
<dt></dt><b><code>beir-v1.0.0-bioasq.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'bioasq'
</dd>
<dt></dt><b><code>beir-v1.0.0-nfcorpus.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'nfcorpus'
</dd>
<dt></dt><b><code>beir-v1.0.0-nq.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'nq'
</dd>
<dt></dt><b><code>beir-v1.0.0-hotpotqa.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'hotpotqa'
</dd>
<dt></dt><b><code>beir-v1.0.0-fiqa.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'fiqa'
</dd>
<dt></dt><b><code>beir-v1.0.0-signal1m.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'signal1m'
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-news.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'trec-news'
</dd>
<dt></dt><b><code>beir-v1.0.0-robust04.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'robust04'
</dd>
<dt></dt><b><code>beir-v1.0.0-arguana.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'arguana'
</dd>
<dt></dt><b><code>beir-v1.0.0-webis-touche2020.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'webis-touche2020'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-android.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-android'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-english.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-english'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gaming.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-gaming'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gis.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-gis'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-mathematica.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-mathematica'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-physics.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-physics'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-programmers.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-programmers'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-stats.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-stats'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-tex.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-tex'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-unix.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-unix'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-webmasters.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-webmasters'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-wordpress.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-wordpress'
</dd>
<dt></dt><b><code>beir-v1.0.0-quora.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'quora'
</dd>
<dt></dt><b><code>beir-v1.0.0-dbpedia-entity.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'dbpedia-entity'
</dd>
<dt></dt><b><code>beir-v1.0.0-scidocs.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'scidocs'
</dd>
<dt></dt><b><code>beir-v1.0.0-fever.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'fever'
</dd>
<dt></dt><b><code>beir-v1.0.0-climate-fever.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'climate-fever'
</dd>
<dt></dt><b><code>beir-v1.0.0-scifact.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'scifact'
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-covid.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'trec-covid'
</dd>
<dt></dt><b><code>beir-v1.0.0-bioasq.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'bioasq'
</dd>
<dt></dt><b><code>beir-v1.0.0-nfcorpus.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'nfcorpus'
</dd>
<dt></dt><b><code>beir-v1.0.0-nq.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'nq'
</dd>
<dt></dt><b><code>beir-v1.0.0-hotpotqa.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'hotpotqa'
</dd>
<dt></dt><b><code>beir-v1.0.0-fiqa.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'fiqa'
</dd>
<dt></dt><b><code>beir-v1.0.0-signal1m.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'signal1m'
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-news.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'trec-news'
</dd>
<dt></dt><b><code>beir-v1.0.0-robust04.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'robust04'
</dd>
<dt></dt><b><code>beir-v1.0.0-arguana.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'arguana'
</dd>
<dt></dt><b><code>beir-v1.0.0-webis-touche2020.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'webis-touche2020'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-android.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-android'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-english.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-english'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gaming.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-gaming'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gis.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-gis'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-mathematica.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-mathematica'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-physics.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-physics'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-programmers.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-programmers'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-stats.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-stats'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-tex.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-tex'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-unix.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-unix'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-webmasters.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-webmasters'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-wordpress.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-wordpress'
</dd>
<dt></dt><b><code>beir-v1.0.0-quora.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'quora'
</dd>
<dt></dt><b><code>beir-v1.0.0-dbpedia-entity.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'dbpedia-entity'
</dd>
<dt></dt><b><code>beir-v1.0.0-scidocs.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'scidocs'
</dd>
<dt></dt><b><code>beir-v1.0.0-fever.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'fever'
</dd>
<dt></dt><b><code>beir-v1.0.0-climate-fever.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'climate-fever'
</dd>
<dt></dt><b><code>beir-v1.0.0-scifact.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">readme</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'scifact'
</dd>
</dl>
</details>
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
<dt></dt><b><code>msmarco-v1-doc-segmented.d2q-t5</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v1-doc-segmented.d2q-t5.20221004.252b5e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 segmented document corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v2-passage</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 passage corpus
</dd>
<dt></dt><b><code>msmarco-v2-doc</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 document corpus
</dd>
<dt></dt><b><code>msmarco-v2-doc.d2q-t5</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc.d2q-t5.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 document corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 segmented document corpus
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented.d2q-t5</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.d2q-t5.20220808.4d6d2a.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 segmented document corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v2.1-doc</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc.20240418.4f9675.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2.1 document corpus
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented</code></b>
[<a href="https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc-segmented.20240418.4f9675.README.md">readme</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2.1 segmented document corpus
</dd>
</dl>
</details>
<details>
<summary>BRIGHT</summary>
<dl>
<dt></dt><b><code>bright-biology</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'biology'
</dd>
<dt></dt><b><code>bright-earth-science</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'earth-science'
</dd>
<dt></dt><b><code>bright-economics</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'economics'
</dd>
<dt></dt><b><code>bright-psychology</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'psychology'
</dd>
<dt></dt><b><code>bright-robotics</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'robotics'
</dd>
<dt></dt><b><code>bright-stackoverflow</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'stackoverflow'
</dd>
<dt></dt><b><code>bright-sustainable-living</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'sustainable-living'
</dd>
<dt></dt><b><code>bright-pony</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'pony'
</dd>
<dt></dt><b><code>bright-leetcode</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'leetcode'
</dd>
<dt></dt><b><code>bright-aops</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'aops'
</dd>
<dt></dt><b><code>bright-theoremqa-theorems</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'theoremqa-theorems'
</dd>
<dt></dt><b><code>bright-theoremqa-questions</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">readme</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'theoremqa-questions'
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

