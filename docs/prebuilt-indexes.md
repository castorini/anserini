# Prebuilt Indexes

Anserini ships with a number of prebuilt indexes.
This means that various indexes (inverted indexes, HNSW indexes, etc.) for common collections used in NLP and IR research have already been built and just needs to be downloaded (from UWaterloo servers), which Anserini will handle automatically for you.

Bindings for the available prebuilt indexes are in [`io.anserini.index.IndexInfo`](https://github.com/castorini/anserini/blob/master/src/main/java/io/anserini/index/IndexInfo.java).
For example, if you specify `-index msmarco-v1-passage`, Anserini will know that you mean the Lucene index of the MS MARCO V1 passage corpus.
It will then download the index from our servers at UWaterloo and cache locally.
All of this happens automagically!

## Changing the Index Location

The automagic download of prebuilt indexes works great for (relatively) small indexes!

However, larger indexes can cause issues.
For example, the `msmarco-v2.1-doc` prebuilt index is 63 GB uncompressed and the `msmarco-v2.1-doc-segmented` prebuilt index is 84 GB uncompressed.
And these are only the inverted indexes (e.g., for BM25).
The HNSW indexes for dense retrieval models are even larger, for example, the Arctic-Embed-L indexes for the entire MS MARCO V2.1 segmented document corpus is around 550 GB.

The prebuilt indexes are automatically downloaded to `~/.cache/pyserini/indexes/`, which may not be the best location for you.
(Yes, `pyserini`; this is so prebuilt indexes from both Pyserini and Anserini can live in the same location.)

You can customize the location of the cache directory using environment variables or system properties. See the [cache directories documentation](cache-directories.md) for detailed information on how to customize where Anserini stores its cached resources.

## Managing Indexes Manually

Another helpful tip is to download and manage the indexes by hand.
All relevant information is stored in [`IndexInfo`](https://github.com/castorini/anserini/blob/master/src/main/java/io/anserini/index/IndexInfo.java).
For example, `msmarco-v1-passage` can be downloaded from:

```
https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.msmarco-v1-passage.20221004.252b5e.tar.gz
```

and has an MD5 checksum of `678876e8c99a89933d553609a0fd8793`.
You can download, verify, and put anywhere you want.
With `-index /path/to/index/` you'll get exactly the same output as `-index msmarco-v1-passage`, except now you've got fine-grained control over managing the index.

By manually manging the indexes, you can share indexes between multiple users to conserve space.
The schema of the index location in `~/.cache/pyserini/indexes/` is the tarball name (after unpacking), followed by a dot and the checksum, so `msmarco-v1-passage` lives in following location:

```
~/.cache/pyserini/indexes/lucene-inverted.msmarco-v1-passage.20221004.252b5e.678876e8c99a89933d553609a0fd8793
```

You can download the index once, put in a common location, and have each user symlink to the actual index location.
Source would conform to the schema above, target would be where your index actually resides.

## Recovering from Partial Downloads

A common issue is recovering from partial downloads, for example, if you abort the downloading of a large index tarball.
In the standard flow, Anserini downloads the tarball from UWaterloo servers, verifies the checksum, and then unpacks the tarball.
If this process is interrupted, you'll end up in an inconsistent state.

To recover, go to `~/.cache/pyserini/indexes/` and remove any tarballs (i.e., `.tar.gz` files).
If there are any partially unpacked indexes, remove those also.
Then start over (e.g., rerun the command you were running before).
