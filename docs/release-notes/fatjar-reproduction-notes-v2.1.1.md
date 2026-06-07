# Anserini Fatjar Reproduction Notes (v2.1.1)

The Anserini fatjar v2.1.1 release occurred at the following commit:

```text
commit f536392e74a437a9c4178790c0d880f111b5ae1b (tag: anserini-2.1.1)
Author: lintool <jimmylin@uwaterloo.ca>
Date:   Sat May 16 05:53:01 2026 -0400

    [maven-release-plugin] prepare release anserini-2.1.1
```

Agent skills in `.agents/skills/` capture exactly how to use Anserini.
Repo `HEAD` may have diverged from this specific release, so if you need _exactly_ this release, the best way to ensure consistent behavior is to rewind the repo back to the above commit.

## Cache

Anserini ships with "batteries included": it'll automatically download prebuilt indexes, topics, and qrels on demand.
The base cache path is `~/.cache/pyserini` by default.
The `pyserini.cache` system property and `PYSERINI_CACHE` environment variable override the base cache path.
If neither override is set and a `.cache` directory exists in the current working directory, the base cache path is `.cache/pyserini` in the current directory.

## Reproductions from Prebuilt Indexes

Using Anserini, you can easily reproduce many retrieval runs on standard IR benchmark datasets.
The following commands provide more details.
With the `--dry-run` option, Anserini won't actually perform the runs, but provide an overview of what's available:

```bash
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config beir.core
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config beir.optional

java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config bright.core
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config bright.optional

java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v1-passage.core
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v1-passage.optional
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v1-doc.core
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v1-doc.optional

java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v2-passage.core
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v2-passage.optional
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v2-doc.core
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v2-doc.optional

java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v2.1-doc-segmented.core
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v2.1-doc-segmented.optional
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v2.1-doc.core
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v2.1-doc.optional
```
