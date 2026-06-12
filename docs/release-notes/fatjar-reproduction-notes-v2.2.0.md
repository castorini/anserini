# Anserini Fatjar Reproduction Notes (v2.2.0)

The Anserini fatjar v2.2.0 release occurred at the following commit:

```text
commit dbd176b936a6edbd76aedeb247dbff4c206ced1c (tag: anserini-2.2.0)
Author: lintool <jimmylin@uwaterloo.ca>
Date:   Sun Jun 7 13:25:57 2026 -0400

    [maven-release-plugin] prepare release anserini-2.2.0
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
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config beir
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config bright
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v1-passage
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v1-doc
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v2-passage
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v2-doc
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v2.1-doc-segmented
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --dry-run --config msmarco-v2.1-doc
```
