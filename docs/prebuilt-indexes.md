# Anserini: Prebuilt Indexes

Anserini ships with a number of prebuilt indexes.
This means that various indexes (inverted indexes, HNSW indexes, etc.) for common collections used in NLP and IR research have already been built and just needs to be downloaded (from UWaterloo and Hugging Face servers), which Anserini will handle automatically for you.

Bindings for available prebuilt indexes can be found in the package [`io.anserini.index.prebuilt`](https://github.com/castorini/anserini/tree/master/src/main/java/io/anserini/index/prebuilt) under the right type, e.g., [`PrebuiltInvertedIndex`](https://github.com/castorini/anserini/blob/master/src/main/java/io/anserini/index/prebuilt/PrebuiltInvertedIndex.java) for inverted indexes.
For example, if you specify `-index msmarco-v1-passage`, Anserini will know that you mean the Lucene index of the MS MARCO V1 passage corpus.
It will then download the index from the specified location(s) and cache locally.
All of this happens auto-magically!

## Getting Started

To download a prebuilt index and view its statistics, you can use the following command:

```bash
bin/run.sh io.anserini.index.IndexReaderUtils -index cacm -stats
```

The output of the above command will be:

```text
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

Downloaded indexes are stored in the first matching cache location:

1. If the system property `pyserini.cache` is set, downloaded indexes are stored in `indexes/` under that base cache directory.
2. Otherwise, if the environment variable `$PYSERINI_CACHE` is set, downloaded indexes are stored in `indexes/` under that base cache directory.
3. Otherwise, if a `.cache/` directory exists in the current working directory, downloaded indexes are stored in `.cache/pyserini/indexes/` under the current working directory.
4. Otherwise, downloaded indexes are stored in `~/.cache/pyserini/indexes/`.

Yes, `pyserini`, that's not a bug &mdash; this is so prebuilt indexes can be shared between Pyserini and Anserini.

Another helpful tip is to download and manage the indexes by hand.
As an example, from the metadata in [`msmarco-v1-passage-inverted.json`](https://github.com/castorini/prebuilt-indexes/blob/main/lucene/msmarco-v1-passage-inverted.json), you can see that `msmarco-v1-passage` can be downloaded from:

```text
https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/resolve/main/passage/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-passage.20221004.252b5e.tar.gz
```

The tarball has an MD5 checksum of `678876e8c99a89933d553609a0fd8793`.

You can download, verify, unpack, and put the index anywhere you want.
With `-index /path/to/index/` you'll get exactly the same output as `-index msmarco-v1-passage`, except now you've got fine-grained control over managing the index.

By manually managing indexes, you can share indexes between multiple users to conserve space.
The schema of the index location in `~/.cache/pyserini/indexes/` is the tarball name (after unpacking), followed by a dot and the checksum, so `msmarco-v1-passage` lives in following location:

```text
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

### Standard Inverted Indexes

<details>
<summary>MS MARCO</summary>

<dl>
<dt></dt><b><code>msmarco-v1-passage</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-passage.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 passage corpus
</dd>
<dt></dt><b><code>msmarco-v1-passage-full</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-passage.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 passage corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v1-passage-slim</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-passage.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 passage corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v1-passage.d2q-t5</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/d2q-t5/lucene-inverted/tf/lucene-inverted.msmarco-v1-passage.d2q-t5.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 passage corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v1-passage.d2q-t5-docvectors</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/d2q-t5/lucene-inverted/tf/lucene-inverted.msmarco-v1-passage.d2q-t5.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 passage corpus with doc2query-T5 expansions (with stored docvectors)
</dd>
<dt></dt><b><code>msmarco-v1-doc</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/doc/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-doc.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 document corpus.
</dd>
<dt></dt><b><code>msmarco-v1-doc-full</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/doc/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-doc.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 document corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v1-doc-slim</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/doc/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-doc.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 document corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v1-doc.d2q-t5</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/doc/d2q-t5/lucene-inverted/tf/lucene-inverted.msmarco-v1-doc.d2q-t5.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 document corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v1-doc.d2q-t5-docvectors</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/doc/d2q-t5/lucene-inverted/tf/lucene-inverted.msmarco-v1-doc.d2q-t5.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 document corpus with doc2query-T5 expansions (with stored docvectors)
</dd>
<dt></dt><b><code>msmarco-v1-doc-segmented</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/doc/segmented/lucene-inverted/tf/lucene-inverted.msmarco-v1-doc-segmented.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 segmented document corpus
</dd>
<dt></dt><b><code>msmarco-v1-doc-segmented-full</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/doc/segmented/lucene-inverted/tf/lucene-inverted.msmarco-v1-doc-segmented.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 segmented document corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v1-doc-segmented-slim</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/doc/segmented/lucene-inverted/tf/lucene-inverted.msmarco-v1-doc-segmented.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 segmented document corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v1-doc-segmented.d2q-t5</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/doc/segmented-d2q-t5/lucene-inverted/tf/lucene-inverted.msmarco-v1-doc-segmented.d2q-t5.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 segmented document corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v1-doc-segmented.d2q-t5-docvectors</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/doc/segmented-d2q-t5/lucene-inverted/tf/lucene-inverted.msmarco-v1-doc-segmented.d2q-t5.20221004.252b5e.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V1 segmented document corpus with doc2query-T5 expansions (with stored docvectors)
</dd>
<dt></dt><b><code>msmarco-v2-passage</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 passage corpus
</dd>
<dt></dt><b><code>msmarco-v2-passage-augmented</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage-augmented.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 augmented passage corpus
</dd>
<dt></dt><b><code>msmarco-v2-passage-augmented-full</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage-augmented.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 augmented passage corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v2-passage-augmented-slim</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage-augmented.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 augmented passage corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v2-passage-augmented.d2q-t5</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage-augmented.d2q-t5.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 augmented passage corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v2-passage-augmented.d2q-t5-docvectors</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage-augmented.d2q-t5.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 augmented passage corpus with doc2query-T5 expansions (with stored docvectors)
</dd>
<dt></dt><b><code>msmarco-v2-passage-full</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 passage corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v2-passage-slim</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 passage corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v2-passage.d2q-t5</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.d2q-t5.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 passage corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v2-passage.d2q-t5-docvectors</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.d2q-t5.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 passage corpus with doc2query-T5 expansions (with stored docvectors)
</dd>
<dt></dt><b><code>msmarco-v2-doc</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 document corpus
</dd>
<dt></dt><b><code>msmarco-v2-doc-full</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 document corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v2-doc-slim</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 document corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v2-doc.d2q-t5</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc.d2q-t5.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 document corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v2-doc.d2q-t5-docvectors</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc.d2q-t5.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 document corpus with doc2query-T5 expansions (with stored docvectors)
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 segmented document corpus
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented-full</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 segmented document corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented-slim</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 segmented document corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented.d2q-t5</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.d2q-t5.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 segmented document corpus with doc2query-T5 expansions
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented.d2q-t5-docvectors</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.d2q-t5.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2 segmented document corpus with doc2query-T5 expansions (with stored docvectors)
</dd>
<dt></dt><b><code>msmarco-v2.1-doc</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc.20240418.4f9675.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2.1 document corpus
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-full</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc.20240418.4f9675.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2.1 document corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-slim</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc.20240418.4f9675.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2.1 document corpus ('slim' version)
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc-segmented.20240418.4f9675.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2.1 segmented document corpus
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-full</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc-segmented.20240418.4f9675.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2.1 segmented document corpus ('full' version)
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-slim</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2.1-doc-segmented.20240418.4f9675.README.md">README</a>]
<dd>Anserini Lucene inverted index of the MS MARCO V2.1 segmented document corpus ('slim' version)
</dd>
</dl>

</details>

<details>
<summary>BEIR</summary>

<dl>
<dt></dt><b><code>beir-v1.0.0-arguana.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'arguana'
</dd>
<dt></dt><b><code>beir-v1.0.0-arguana.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'arguana'
</dd>
<dt></dt><b><code>beir-v1.0.0-bioasq.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'bioasq'
</dd>
<dt></dt><b><code>beir-v1.0.0-bioasq.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'bioasq'
</dd>
<dt></dt><b><code>beir-v1.0.0-climate-fever.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'climate-fever'
</dd>
<dt></dt><b><code>beir-v1.0.0-climate-fever.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'climate-fever'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-android.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-android'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-android.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-android'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-english.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-english'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-english.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-english'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gaming.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-gaming'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gaming.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-gaming'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gis.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-gis'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gis.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-gis'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-mathematica.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-mathematica'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-mathematica.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-mathematica'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-physics.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-physics'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-physics.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-physics'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-programmers.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-programmers'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-programmers.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-programmers'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-stats.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-stats'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-stats.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-stats'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-tex.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-tex'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-tex.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-tex'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-unix.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-unix'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-unix.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-unix'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-webmasters.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-webmasters'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-webmasters.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-webmasters'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-wordpress.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'cqadupstack-wordpress'
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-wordpress.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-wordpress'
</dd>
<dt></dt><b><code>beir-v1.0.0-dbpedia-entity.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'dbpedia-entity'
</dd>
<dt></dt><b><code>beir-v1.0.0-dbpedia-entity.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'dbpedia-entity'
</dd>
<dt></dt><b><code>beir-v1.0.0-fever.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'fever'
</dd>
<dt></dt><b><code>beir-v1.0.0-fever.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'fever'
</dd>
<dt></dt><b><code>beir-v1.0.0-fiqa.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'fiqa'
</dd>
<dt></dt><b><code>beir-v1.0.0-fiqa.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'fiqa'
</dd>
<dt></dt><b><code>beir-v1.0.0-hotpotqa.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'hotpotqa'
</dd>
<dt></dt><b><code>beir-v1.0.0-hotpotqa.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'hotpotqa'
</dd>
<dt></dt><b><code>beir-v1.0.0-nfcorpus.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'nfcorpus'
</dd>
<dt></dt><b><code>beir-v1.0.0-nfcorpus.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'nfcorpus'
</dd>
<dt></dt><b><code>beir-v1.0.0-nq.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'nq'
</dd>
<dt></dt><b><code>beir-v1.0.0-nq.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'nq'
</dd>
<dt></dt><b><code>beir-v1.0.0-quora.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'quora'
</dd>
<dt></dt><b><code>beir-v1.0.0-quora.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'quora'
</dd>
<dt></dt><b><code>beir-v1.0.0-robust04.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'robust04'
</dd>
<dt></dt><b><code>beir-v1.0.0-robust04.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'robust04'
</dd>
<dt></dt><b><code>beir-v1.0.0-scidocs.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'scidocs'
</dd>
<dt></dt><b><code>beir-v1.0.0-scidocs.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'scidocs'
</dd>
<dt></dt><b><code>beir-v1.0.0-scifact.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'scifact'
</dd>
<dt></dt><b><code>beir-v1.0.0-scifact.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'scifact'
</dd>
<dt></dt><b><code>beir-v1.0.0-signal1m.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'signal1m'
</dd>
<dt></dt><b><code>beir-v1.0.0-signal1m.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'signal1m'
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-covid.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'trec-covid'
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-covid.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'trec-covid'
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-news.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'trec-news'
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-news.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'trec-news'
</dd>
<dt></dt><b><code>beir-v1.0.0-webis-touche2020.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/flat/lucene-inverted.beir-v1.0.0-flat.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'flat' index of BEIR collection 'webis-touche2020'
</dd>
<dt></dt><b><code>beir-v1.0.0-webis-touche2020.multifield</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/multifield/lucene-inverted.beir-v1.0.0-multifield.20221116.505594.README.md">README</a>]
<dd>Anserini Lucene inverted 'multifield' index of BEIR collection 'webis-touche2020'
</dd>
</dl>

</details>

<details>
<summary>BRIGHT</summary>

<dl>
<dt></dt><b><code>bright-aops</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">README</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'aops'
</dd>
<dt></dt><b><code>bright-biology</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">README</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'biology'
</dd>
<dt></dt><b><code>bright-earth-science</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">README</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'earth-science'
</dd>
<dt></dt><b><code>bright-economics</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">README</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'economics'
</dd>
<dt></dt><b><code>bright-leetcode</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">README</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'leetcode'
</dd>
<dt></dt><b><code>bright-pony</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">README</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'pony'
</dd>
<dt></dt><b><code>bright-psychology</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">README</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'psychology'
</dd>
<dt></dt><b><code>bright-robotics</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">README</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'robotics'
</dd>
<dt></dt><b><code>bright-stackoverflow</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">README</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'stackoverflow'
</dd>
<dt></dt><b><code>bright-sustainable-living</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">README</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'sustainable-living'
</dd>
<dt></dt><b><code>bright-theoremqa-questions</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">README</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'theoremqa-questions'
</dd>
<dt></dt><b><code>bright-theoremqa-theorems</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/tf/lucene-inverted.bright.20250705.44ae8e.README.md">README</a>]
<dd>Anserini Lucene inverted index of BRIGHT collection 'theoremqa-theorems'
</dd>
</dl>

</details>

<details>
<summary>Other</summary>

<dl>
<dt></dt><b><code>aquaint</code></b>
[<a href="lucene-inverted.aquaint.20240803.36f7e3.README.md">README</a>]
<dd>Lucene index of the AQUAINT collection, used in the TREC 2005 Robust Track.
</dd>
<dt></dt><b><code>atomic_image_v0.2_base</code></b>
[<a href="lucene-index.atomic.20231018.ae6ff6.README.md">README</a>]
<dd>Lucene index for AToMiC Images v0.2 base setting on validation set
</dd>
<dt></dt><b><code>atomic_image_v0.2_large</code></b>
[<a href="lucene-index.atomic.20231018.ae6ff6.README.md">README</a>]
<dd>Lucene index for AToMiC Images v0.2 large setting on validation set
</dd>
<dt></dt><b><code>atomic_image_v0.2_small_validation</code></b>
[<a href="lucene-index.atomic.20231018.ae6ff6.README.md">README</a>]
<dd>Lucene index for AToMiC Images v0.2 small setting on validation set
</dd>
<dt></dt><b><code>atomic_text_v0.2.1_base</code></b>
[<a href="lucene-index.atomic.20231018.ae6ff6.README.md">README</a>]
<dd>Lucene index for AToMiC Text v0.2.1 base setting on validation set
</dd>
<dt></dt><b><code>atomic_text_v0.2.1_large</code></b>
[<a href="lucene-index.atomic.20231018.ae6ff6.README.md">README</a>]
<dd>Lucene index for AToMiC Text v0.2.1 large setting on validation set
</dd>
<dt></dt><b><code>atomic_text_v0.2.1_small_validation</code></b>
[<a href="lucene-index.atomic.20231018.ae6ff6.README.md">README</a>]
<dd>Lucene index for AToMiC Text v0.2.1 small setting on validation set
</dd>
<dt></dt><b><code>cacm</code></b>
<dd>Anserini Lucene inverted index of the CACM corpus
</dd>
<dt></dt><b><code>cast2019</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC 2019 CaST
</dd>
<dt></dt><b><code>ciral-v1.0-ha</code></b>
[<a href="lucene-index.ciral-v1.0.20230721.e850ea.README.md">README</a>]
<dd>Lucene index for CIRAL v1.0 (Hausa).
</dd>
<dt></dt><b><code>ciral-v1.0-ha-en</code></b>
[<a href="lucene-index.ciral-v1.0-en.20240212.2154e7.README.md">README</a>]
<dd>Lucene index for CIRAL v1.0 English Translations (Hausa).
</dd>
<dt></dt><b><code>ciral-v1.0-so</code></b>
[<a href="lucene-index.ciral-v1.0.20230721.e850ea.README.md">README</a>]
<dd>Lucene index for CIRAL v1.0 (Somali).
</dd>
<dt></dt><b><code>ciral-v1.0-so-en</code></b>
[<a href="lucene-index.ciral-v1.0-en.20240212.2154e7.README.md">README</a>]
<dd>Lucene index for CIRAL v1.0 English Translations (Somali).
</dd>
<dt></dt><b><code>ciral-v1.0-sw</code></b>
[<a href="lucene-index.ciral-v1.0.20230721.e850ea.README.md">README</a>]
<dd>Lucene index for CIRAL v1.0 (Swahili).
</dd>
<dt></dt><b><code>ciral-v1.0-sw-en</code></b>
[<a href="lucene-index.ciral-v1.0-en.20240212.2154e7.README.md">README</a>]
<dd>Lucene index for CIRAL v1.0 English Translations (Swahili).
</dd>
<dt></dt><b><code>ciral-v1.0-yo</code></b>
[<a href="lucene-index.ciral-v1.0.20230721.e850ea.README.md">README</a>]
<dd>Lucene index for CIRAL v1.0 (Yoruba).
</dd>
<dt></dt><b><code>ciral-v1.0-yo-en</code></b>
[<a href="lucene-index.ciral-v1.0-en.20240212.2154e7.README.md">README</a>]
<dd>Lucene index for CIRAL v1.0 English Translations (Yoruba).
</dd>
<dt></dt><b><code>disk45</code></b>
[<a href="lucene-inverted.disk45.20240803.36f7e3.README.md">README</a>]
<dd>Lucene index of TREC Disks 4 & 5 (minus Congressional Records), used in the TREC 2004 Robust Track.
</dd>
<dt></dt><b><code>enwiki-paragraphs</code></b>
[<a href="null">README</a>]
<dd>Lucene index of English Wikipedia for BERTserini
</dd>
<dt></dt><b><code>hc4-v1.0-fa</code></b>
[<a href="lucene-index.hc4-v1.0.20221025.c4a8d0.README.md">README</a>]
<dd>Lucene index for HC4 v1.0 (Persian).
</dd>
<dt></dt><b><code>hc4-v1.0-ru</code></b>
[<a href="lucene-index.hc4-v1.0.20221025.c4a8d0.README.md">README</a>]
<dd>Lucene index for HC4 v1.0 (Russian).
</dd>
<dt></dt><b><code>hc4-v1.0-zh</code></b>
[<a href="lucene-index.hc4-v1.0.20221025.c4a8d0.README.md">README</a>]
<dd>Lucene index for HC4 v1.0 (Chinese).
</dd>
<dt></dt><b><code>m-beir-cirr_task7</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR CIRR task 7 corpus.
</dd>
<dt></dt><b><code>m-beir-edis_task2</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR EDIS task 2 corpus.
</dd>
<dt></dt><b><code>m-beir-fashion200k_task0</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR Fashion200K task 0 corpus.
</dd>
<dt></dt><b><code>m-beir-fashion200k_task3</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR Fashion200K task 3 corpus.
</dd>
<dt></dt><b><code>m-beir-fashioniq_task7</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR FashionIQ task 7 corpus.
</dd>
<dt></dt><b><code>m-beir-infoseek_task6</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR InfoSeek task 6 corpus.
</dd>
<dt></dt><b><code>m-beir-infoseek_task8</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR InfoSeek task 8 corpus.
</dd>
<dt></dt><b><code>m-beir-mscoco_task0</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR MSCOCO task 0 corpus.
</dd>
<dt></dt><b><code>m-beir-mscoco_task3</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR MSCOCO task 3 corpus.
</dd>
<dt></dt><b><code>m-beir-nights_task4</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR NIGHTS task 4 corpus.
</dd>
<dt></dt><b><code>m-beir-oven_task6</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR OVEN task 6 corpus.
</dd>
<dt></dt><b><code>m-beir-oven_task8</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR OVEN task 8 corpus.
</dd>
<dt></dt><b><code>m-beir-visualnews_task0</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR VisualNews task 0 corpus.
</dd>
<dt></dt><b><code>m-beir-visualnews_task3</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR VisualNews task 3 corpus.
</dd>
<dt></dt><b><code>m-beir-webqa_task1</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR WebQA task 1 corpus.
</dd>
<dt></dt><b><code>m-beir-webqa_task2</code></b>
[<a href="lucene-inverted.m-beir.20251227.1c5cd3.README.md">README</a>]
<dd>Lucene index for M-BEIR WebQA task 2 corpus.
</dd>
<dt></dt><b><code>miracl-v1.0-ar</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Arabic (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-bn</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Bengali (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-de</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - German (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-en</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - English (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-es</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Spanish (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-fa</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Persian (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-fi</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Finnish (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-fr</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - French (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-hi</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Hindi (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-id</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Indonesian (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-ja</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Japanese (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-ko</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Korean (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-ru</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Russian (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-sw</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Swahili (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-te</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Telugu (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-th</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Thai (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-yo</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Yoruba (Lucene 10.4.0)
</dd>
<dt></dt><b><code>miracl-v1.0-zh</code></b>
[<a href="lucene-inverted.miracl-v1.0.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for MIRACL v1.0 - Chinese (Lucene 10.4.0)
</dd>
<dt></dt><b><code>mrtydi-v1.1-ar</code></b>
[<a href="lucene-inverted.mrtydi-v1.1.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for Mr.TyDi v1.1 - Arabic (Lucene 10.4.0)
</dd>
<dt></dt><b><code>mrtydi-v1.1-bn</code></b>
[<a href="lucene-inverted.mrtydi-v1.1.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for Mr.TyDi v1.1 - Bengali (Lucene 10.4.0)
</dd>
<dt></dt><b><code>mrtydi-v1.1-en</code></b>
[<a href="lucene-inverted.mrtydi-v1.1.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for Mr.TyDi v1.1 - English (Lucene 10.4.0)
</dd>
<dt></dt><b><code>mrtydi-v1.1-fi</code></b>
[<a href="lucene-inverted.mrtydi-v1.1.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for Mr.TyDi v1.1 - Finnish (Lucene 10.4.0)
</dd>
<dt></dt><b><code>mrtydi-v1.1-id</code></b>
[<a href="lucene-inverted.mrtydi-v1.1.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for Mr.TyDi v1.1 - Indonesian (Lucene 10.4.0)
</dd>
<dt></dt><b><code>mrtydi-v1.1-ja</code></b>
[<a href="lucene-inverted.mrtydi-v1.1.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for Mr.TyDi v1.1 - Japanese (Lucene 10.4.0)
</dd>
<dt></dt><b><code>mrtydi-v1.1-ko</code></b>
[<a href="lucene-inverted.mrtydi-v1.1.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for Mr.TyDi v1.1 - Korean (Lucene 10.4.0)
</dd>
<dt></dt><b><code>mrtydi-v1.1-ru</code></b>
[<a href="lucene-inverted.mrtydi-v1.1.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for Mr.TyDi v1.1 - Russian (Lucene 10.4.0)
</dd>
<dt></dt><b><code>mrtydi-v1.1-sw</code></b>
[<a href="lucene-inverted.mrtydi-v1.1.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for Mr.TyDi v1.1 - Swahili (Lucene 10.4.0)
</dd>
<dt></dt><b><code>mrtydi-v1.1-te</code></b>
[<a href="lucene-inverted.mrtydi-v1.1.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for Mr.TyDi v1.1 - Telugu (Lucene 10.4.0)
</dd>
<dt></dt><b><code>mrtydi-v1.1-th</code></b>
[<a href="lucene-inverted.mrtydi-v1.1.20260604.558ae2c.README.md">README</a>]
<dd>Lucene index for Mr.TyDi v1.1 - Thai (Lucene 10.4.0)
</dd>
<dt></dt><b><code>neuclir22-fa</code></b>
[<a href="lucene-index.neuclir22.20221025.c4a8d0.README.md">README</a>]
<dd>Lucene index for NeuCLIR 2022 corpus (Persian).
</dd>
<dt></dt><b><code>neuclir22-fa-en</code></b>
[<a href="lucene-index.neuclir22-en.20221025.c4a8d0.README.md">README</a>]
<dd>Lucene index for NeuCLIR 2022 corpus (official English translation from Persian).
</dd>
<dt></dt><b><code>neuclir22-ru</code></b>
[<a href="lucene-index.neuclir22.20221025.c4a8d0.README.md">README</a>]
<dd>Lucene index for NeuCLIR 2022 corpus (Russian).
</dd>
<dt></dt><b><code>neuclir22-ru-en</code></b>
[<a href="lucene-index.neuclir22-en.20221025.c4a8d0.README.md">README</a>]
<dd>Lucene index for NeuCLIR 2022 corpus (official English translation from Russian).
</dd>
<dt></dt><b><code>neuclir22-zh</code></b>
[<a href="lucene-index.neuclir22.20221025.c4a8d0.README.md">README</a>]
<dd>Lucene index for NeuCLIR 2022 corpus (Chinese).
</dd>
<dt></dt><b><code>neuclir22-zh-en</code></b>
[<a href="lucene-index.neuclir22-en.20221025.c4a8d0.README.md">README</a>]
<dd>Lucene index for NeuCLIR 2022 corpus (official English translation from Chinese).
</dd>
<dt></dt><b><code>nyt</code></b>
[<a href="lucene-inverted.nyt.20240803.36f7e3.README.md">README</a>]
<dd>Lucene index of the New York Times Annotated Corpus, used in the TREC 2017 Common Core Track.
</dd>
<dt></dt><b><code>trec-covid-r1-abstract</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 1: abstract index
</dd>
<dt></dt><b><code>trec-covid-r1-full-text</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 1: full-text index
</dd>
<dt></dt><b><code>trec-covid-r1-paragraph</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 1: paragraph index
</dd>
<dt></dt><b><code>trec-covid-r2-abstract</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 2: abstract index
</dd>
<dt></dt><b><code>trec-covid-r2-full-text</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 2: full-text index
</dd>
<dt></dt><b><code>trec-covid-r2-paragraph</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 2: paragraph index
</dd>
<dt></dt><b><code>trec-covid-r3-abstract</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 3: abstract index
</dd>
<dt></dt><b><code>trec-covid-r3-full-text</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 3: full-text index
</dd>
<dt></dt><b><code>trec-covid-r3-paragraph</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 3: paragraph index
</dd>
<dt></dt><b><code>trec-covid-r4-abstract</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 4: abstract index
</dd>
<dt></dt><b><code>trec-covid-r4-full-text</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 4: full-text index
</dd>
<dt></dt><b><code>trec-covid-r4-paragraph</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 4: paragraph index
</dd>
<dt></dt><b><code>trec-covid-r5-abstract</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 5: abstract index
</dd>
<dt></dt><b><code>trec-covid-r5-full-text</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 5: full-text index
</dd>
<dt></dt><b><code>trec-covid-r5-paragraph</code></b>
[<a href="null">README</a>]
<dd>Lucene index for TREC-COVID Round 5: paragraph index
</dd>
<dt></dt><b><code>wapo.v2</code></b>
[<a href="lucene-inverted.wapo.v2.20240803.36f7e3.README.md">README</a>]
<dd>Lucene index of the TREC Washington Post Corpus, used in the TREC 2018 Common Core Track.
</dd>
<dt></dt><b><code>wiki-all-6-3-tamber</code></b>
[<a href="null">README</a>]
<dd>Lucene index of wiki-all-6-3-tamber from castorini/odqa-wiki-corpora on Huggingface Datasets (Lucene 10.4.0)
</dd>
<dt></dt><b><code>wikipedia-dpr-100w</code></b>
[<a href="null">README</a>]
<dd>Lucene index of Wikipedia with DPR 100-word splits (Lucene 10.4.0)
</dd>
<dt></dt><b><code>zhwiki-paragraphs</code></b>
[<a href="null">README</a>]
<dd>Lucene index of Chinese Wikipedia for BERTserini
</dd>
</dl>

</details>

### Impact Indexes

<details>
<summary>MS MARCO</summary>

<dl>
<dt></dt><b><code>msmarco-v1-passage.deepimpact</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/deepimpact/lucene-inverted.msmarco-v1-passage.deepimpact.20221005.252b5e.README.md">README</a>]
<dd>Lucene impact index of the MS MARCO V1 passage corpus encoded by DeepImpact.
</dd>
<dt></dt><b><code>msmarco-v1-passage.distill-splade-max</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/distill-splade-max/lucene-inverted.msmarco-v1-passage.distill-splade-max.20221005.252b5e.README.md">README</a>]
<dd>Lucene impact index of the MS MARCO V1 passage corpus encoded by distill-splade-max.
</dd>
<dt></dt><b><code>msmarco-v1-passage.slimr</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/slimr/lucene-inverted.msmarco-v1-passage.slimr.20230925.md">README</a>]
<dd>Anserini Lucene impact index of the MS MARCO V1 passage corpus encoded by SLIM trained with BM25 negatives
</dd>
<dt></dt><b><code>msmarco-v1-passage.slimr-pp</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/slimr-pp/lucene-inverted.msmarco-v1-passage.slimr-pp.20230925.md">README</a>]
<dd>Anserini Lucene impact index of the MS MARCO V1 passage corpus encoded by SLIM trained with cross-encoder distillation and hard-negative mining
</dd>
<dt></dt><b><code>msmarco-v1-passage.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/splade-pp/lucene-inverted.msmarco-v1-passage.splade-pp.20230524.a59610.README.md">README</a>]
<dd>Anserini Lucene impact index of the MS MARCO V1 passage corpus encoded by SPLADE++ CoCondenser-EnsembleDistil
</dd>
<dt></dt><b><code>msmarco-v1-passage.splade-pp-ed-docvectors</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/splade-pp/lucene-inverted.msmarco-v1-passage.splade-pp.20230524.a59610.README.md">README</a>]
<dd>Lucene impact index (with docvectors) of the MS MARCO passage corpus encoded by SPLADE++ CoCondenser-EnsembleDistil.
</dd>
<dt></dt><b><code>msmarco-v1-passage.splade-pp-ed-text</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/splade-pp/lucene-inverted.msmarco-v1-passage.splade-pp.20230524.a59610.README.md">README</a>]
<dd>Lucene impact index (with text) of the MS MARCO passage corpus encoded by SPLADE++ CoCondenser-EnsembleDistil.
</dd>
<dt></dt><b><code>msmarco-v1-passage.splade-pp-sd</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/splade-pp/lucene-inverted.msmarco-v1-passage.splade-pp.20230524.a59610.README.md">README</a>]
<dd>Lucene impact index of the MS MARCO passage corpus encoded by SPLADE++ CoCondenser-SelfDistil.
</dd>
<dt></dt><b><code>msmarco-v1-passage.splade-pp-sd-docvectors</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/splade-pp/lucene-inverted.msmarco-v1-passage.splade-pp.20230524.a59610.README.md">README</a>]
<dd>Lucene impact index (with docvectors) of the MS MARCO passage corpus encoded by SPLADE++ CoCondenser-SelfDistil.
</dd>
<dt></dt><b><code>msmarco-v1-passage.splade-pp-sd-text</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/splade-pp/lucene-inverted.msmarco-v1-passage.splade-pp.20230524.a59610.README.md">README</a>]
<dd>Lucene impact index (with text) of the MS MARCO passage corpus encoded by SPLADE++ CoCondenser-SelfDistil.
</dd>
<dt></dt><b><code>msmarco-v1-passage.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/splade-v3/lucene-inverted.msmarco-v1-passage.splade-v3.20250329.4f4c68.README.md">README</a>]
<dd>Anserini Lucene impact index of the MS MARCO passage corpus encoded by SPLADE-v3
</dd>
<dt></dt><b><code>msmarco-v1-passage.splade-v3-docvectors</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/splade-v3/lucene-inverted.msmarco-v1-passage.splade-v3.20250329.4f4c68.README.md">README</a>]
<dd>Lucene impact index (with docvectors) of the MS MARCO passage corpus encoded by SPLADEv3.
</dd>
<dt></dt><b><code>msmarco-v1-passage.splade-v3-text</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/splade-v3/lucene-inverted.msmarco-v1-passage.splade-v3.20250329.4f4c68.README.md">README</a>]
<dd>Lucene impact index (with text) of the MS MARCO passage corpus encoded by SPLADEv3.
</dd>
<dt></dt><b><code>msmarco-v1-passage.unicoil</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/unicoil/lucene-inverted.msmarco-v1-passage.unicoil.20221005.252b5e.README.md">README</a>]
<dd>Lucene impact index of the MS MARCO V1 passage corpus for uniCOIL.
</dd>
<dt></dt><b><code>msmarco-v1-passage.unicoil-noexp</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/unicoil-noexp/lucene-inverted.msmarco-v1-passage.unicoil-noexp.20221005.252b5e.README.md">README</a>]
<dd>Lucene impact index of the MS MARCO V1 passage corpus for uniCOIL (noexp).
</dd>
<dt></dt><b><code>msmarco-v1-passage.unicoil-tilde</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-inverted/unicoil-tilde/lucene-inverted.msmarco-v1-passage.unicoil-tilde.20221005.252b5e.README.md">README</a>]
<dd>Lucene impact index of the MS MARCO V1 passage corpus encoded by uniCOIL-TILDE.
</dd>
<dt></dt><b><code>msmarco-v1-doc-segmented.unicoil</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/doc/segmented/lucene-inverted/unicoil/lucene-inverted.msmarco-v1-doc-segmented.unicoil.20221005.252b5e.README.md">README</a>]
<dd>Anserini Lucene impact index of the MS MARCO V1 segmented document corpus for uniCOIL, with title/segment encoding
</dd>
<dt></dt><b><code>msmarco-v1-doc-segmented.unicoil-noexp</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/doc/segmented/lucene-inverted/unicoil-noexp/lucene-inverted.msmarco-v1-doc-segmented.unicoil-noexp.20221005.252b5e.README.md">README</a>]
<dd>Anserini Lucene impact index of the MS MARCO V1 segmented document corpus for uniCOIL (noexp), with title/segment encoding
</dd>
<dt></dt><b><code>msmarco-v2-passage.slimr-pp</code></b>
[<a href="null">README</a>]
<dd>Lucene impact index of the MS MARCO V2 passage corpus encoded by SLIM (norefine) trained with cross-encoder distillation and hard-negative mining.
</dd>
<dt></dt><b><code>msmarco-v2-passage.unicoil-0shot</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.unicoil-0shot.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene impact index of the MS MARCO V2 passage corpus for uniCOIL
</dd>
<dt></dt><b><code>msmarco-v2-passage.unicoil-noexp-0shot</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-passage.unicoil-noexp-0shot.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene impact index of the MS MARCO V2 passage corpus for uniCOIL (noexp)
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented.unicoil-0shot</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.unicoil-0shot.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene impact index of the MS MARCO V2 segmented document corpus for uniCOIL, with title prepended
</dd>
<dt></dt><b><code>msmarco-v2-doc-segmented.unicoil-noexp-0shot</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/lucene-inverted.msmarco-v2-doc-segmented.unicoil-noexp-0shot.20220808.4d6d2a.README.md">README</a>]
<dd>Anserini Lucene impact index of the MS MARCO V2 segmented document corpus for uniCOIL (noexp) with title prepended
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented.splade-v3</code></b>
<dd>Anserini Lucene impact index of the MS MARCO V2.1 segmented document corpus encoded by SPLADE-v3
</dd>
</dl>

</details>

<details>
<summary>BEIR</summary>

<dl>
<dt></dt><b><code>beir-v1.0.0-arguana.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'arguana' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-arguana.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'arguana' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-bioasq.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'bioasq' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-bioasq.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'bioasq' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-climate-fever.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'climate-fever' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-climate-fever.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'climate-fever' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-android.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-android' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-android.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-android' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-english.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-english' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-english.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-english' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gaming.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-gaming' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gaming.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-gaming' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gis.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-gis' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gis.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-gis' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-mathematica' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-mathematica.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-mathematica' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-physics.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-physics' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-physics.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-physics' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-programmers.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-programmers' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-programmers.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-programmers' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-stats.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-stats' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-stats.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-stats' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-tex.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-tex' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-tex.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-tex' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-unix.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-unix' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-unix.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-unix' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-webmasters' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-webmasters.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-webmasters' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-wordpress' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-wordpress.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'cqadupstack-wordpress' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-dbpedia-entity.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'dbpedia-entity' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-dbpedia-entity.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'dbpedia-entity' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-fever.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'fever' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-fever.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'fever' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-fiqa.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'fiqa' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-fiqa.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'fiqa' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-hotpotqa.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'hotpotqa' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-hotpotqa.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'hotpotqa' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-nfcorpus.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'nfcorpus' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-nfcorpus.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'nfcorpus' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-nq.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'nq' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-nq.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'nq' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-quora.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'quora' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-quora.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'quora' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-robust04.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'robust04' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-robust04.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'robust04' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-scidocs.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'scidocs' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-scidocs.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'scidocs' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-scifact.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'scifact' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-scifact.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'scifact' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-signal1m.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'signal1m' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-signal1m.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'signal1m' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-covid.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'trec-covid' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-covid.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'trec-covid' collection 'trec-covid' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-news.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'trec-news' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-news.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'trec-news' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>beir-v1.0.0-webis-touche2020.splade-pp-ed</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-pp-ed/lucene-inverted.beir-v1.0.0-splade-pp-ed.20231124.a66f86f.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'webis-touche2020' encoded by SPLADE++ CoCondenser-EnsembleDistil (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-webis-touche2020.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-inverted/splade-v3/lucene-inverted.beir-v1.0.0-splade-v3.20250603.168a2d.README.md">README</a>]
<dd>Anserini Lucene impact index of BEIR collection 'webis-touche2020' encoded by SPLADE-v3
</dd>
</dl>

</details>

<details>
<summary>BRIGHT</summary>

<dl>
<dt></dt><b><code>bright-aops.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">README</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'aops' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-biology.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">README</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'biology' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-earth-science.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">README</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'earth-science' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-economics.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">README</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'economics' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-leetcode.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">README</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'leetcode' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-pony.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">README</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'pony' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-psychology.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">README</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'psychology' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-robotics.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">README</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'robotics' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-stackoverflow.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">README</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'stackoverflow' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-sustainable-living.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">README</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'sustainable-living' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-theoremqa-questions.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">README</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'theoremqa-questions' encoded by SPLADE-v3
</dd>
<dt></dt><b><code>bright-theoremqa-theorems.splade-v3</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-inverted/splade-v3/lucene-inverted.bright.splade-v3.20250808.c6674a.README.md">README</a>]
<dd>Anserini Lucene impact index of BRIGHT collection 'theoremqa-theorems' encoded by SPLADE-v3
</dd>
</dl>

</details>

### Flat Vector Indexes

<details>
<summary>BEIR</summary>

<dl>
<dt></dt><b><code>beir-v1.0.0-arguana.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'arguana' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-bioasq.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'bioasq' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-climate-fever.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'climate-fever' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-android' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-english' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-gaming' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-gis' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-mathematica' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-physics' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-programmers' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-stats' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-tex' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-unix' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-webmasters' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'cqadupstack-wordpress' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'dbpedia-entity' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-fever.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'fever' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-fiqa.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'fiqa' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-hotpotqa.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'hotpotqa' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-nfcorpus.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'nfcorpus' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-nq.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'nq' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-quora.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'quora' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-robust04.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'robust04' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-scidocs.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'scidocs' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-scifact.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'scifact' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-signal1m.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'signal1m' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-covid.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'trec-covid' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-news.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'trec-news' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
<dt></dt><b><code>beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-flat/bge-base-en-v1.5/lucene-flat.beir-v1.0.0.bge-base-en-v1.5.20260425.bb3d65.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BEIR collection 'webis-touche2020' encoded by BGE-base-en-v1.5 (Lucene 10.4.0)
</dd>
</dl>

</details>

<details>
<summary>BRIGHT</summary>

<dl>
<dt></dt><b><code>bright-aops.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'aops' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-biology.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'biology' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-earth-science.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'earth-science' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-economics.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'economics' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-leetcode.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'leetcode' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-pony.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'pony' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-psychology.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'psychology' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-robotics.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'robotics' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-stackoverflow.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'stackoverflow' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-sustainable-living.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'sustainable-living' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-theoremqa-questions.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'theoremqa-questions' encoded by BGE-large-en-v1.5
</dd>
<dt></dt><b><code>bright-theoremqa-theorems.bge-large-en-v1.5.flat</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-bright/blob/main/lucene-flat/bge-large-en-v1.5/lucene-flat.bright.bge-large-en-v1.5.20250819.e5ee76.README.md">README</a>]
<dd>Anserini Lucene flat vector index of BRIGHT collection 'theoremqa-theorems' encoded by BGE-large-en-v1.5
</dd>
</dl>

</details>

### HNSW Vector Indexes

<details>
<summary>MS MARCO</summary>

<dl>
<dt></dt><b><code>msmarco-v1-passage.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.msmarco-v1-passage.bge-base-en-v1.5.20240117.53514b.README.md">README</a>]
<dd>Anserini Lucene HNSW index of the MS MARCO V1 passage corpus encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>msmarco-v1-passage.bge-base-en-v1.5.hnsw-int8</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.msmarco-v1-passage.bge-base-en-v1.5.20240117.53514b.README.md">README</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V1 passage corpus encoded by BGE-base-en-v1.5
</dd>
<dt></dt><b><code>msmarco-v1-passage.cohere-embed-english-v3.0.hnsw</code></b>
<dd>Anserini Lucene HNSW index of the MS MARCO V1 passage corpus encoded by Cohere embed-english-v3.0
</dd>
<dt></dt><b><code>msmarco-v1-passage.cohere-embed-english-v3.0.hnsw-int8</code></b>
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V1 passage corpus encoded by Cohere embed-english-v3.0
</dd>
<dt></dt><b><code>msmarco-v1-passage.cosdpr-distil.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-hnsw/cosdpr-distil/lucene-hnsw.msmarco-v1-passage.cosdpr-distil.20240108.825148.README.md">README</a>]
<dd>Anserini Lucene HNSW index of the MS MARCO V1 passage corpus encoded by cos-DPR Distil
</dd>
<dt></dt><b><code>msmarco-v1-passage.cosdpr-distil.hnsw-int8</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/blob/main/passage/original/lucene-hnsw/cosdpr-distil/lucene-hnsw.msmarco-v1-passage.cosdpr-distil.20240108.825148.README.md">README</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V1 passage corpus encoded by cos-DPR Distil
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">README</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard00) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard01.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">README</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard01) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard02.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">README</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard02) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard03.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">README</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard03) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard04.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">README</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard04) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard05.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">README</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard05) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard06.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">README</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard06) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard07.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">README</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard07) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard08.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">README</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard08) encoded by Snowflake's arctic-embed-l model
</dd>
<dt></dt><b><code>msmarco-v2.1-doc-segmented-shard09.arctic-embed-l.hnsw-int8</code></b>
[<a href="https://github.com/castorini/pyserini/tree/master/pyserini/resources/index-metadata/faiss-flat.msmarco-v2.1-doc.arctic-embed-l.20240824.README.md">README</a>]
<dd>Anserini Lucene quantized (int8) HNSW index of the MS MARCO V2.1 segmented document corpus (shard09) encoded by Snowflake's arctic-embed-l model
</dd>
</dl>

</details>

<details>
<summary>BEIR</summary>

<dl>
<dt></dt><b><code>beir-v1.0.0-arguana.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'arguana' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-bioasq.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'bioasq' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-climate-fever.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'climate-fever' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-android' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-english' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-gaming' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-gis' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-mathematica' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-physics' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-programmers' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-stats' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-tex' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-unix' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-webmasters' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'cqadupstack-wordpress' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'dbpedia-entity' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-fever.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'fever' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-fiqa.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'fiqa' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-hotpotqa.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'hotpotqa' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-nfcorpus.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'nfcorpus' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-nq.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'nq' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-quora.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'quora' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-robust04.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'robust04' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-scidocs.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'scidocs' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-scifact.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'scifact' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-signal1m.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'signal1m' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-covid.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'trec-covid' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-trec-news.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'trec-news' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
<dt></dt><b><code>beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.hnsw</code></b>
[<a href="https://huggingface.co/datasets/castorini/prebuilt-indexes-beir/blob/main/lucene-hnsw/bge-base-en-v1.5/lucene-hnsw.beir-v1.0.0.bge-base-en-v1.5.20240223.43c9ec.README.md">README</a>]
<dd>Anserini Lucene HNSW index of BEIR collection 'webis-touche2020' encoded by BGE-base-en-v1.5 (Lucene 9)
</dd>
</dl>

</details>

