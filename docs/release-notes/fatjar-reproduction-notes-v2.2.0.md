# Anserini Fatjar Reproduction Notes (v2.2.0)

❗ **As of August 2026, the Anserini fatjar v2.2.0 release is broken (and likely previous releases also).**

Detailed explanation:

Anserini previously included [a submodule checkout](https://github.com/castorini/eval/) at `tools/`. This was removed at [`anserini#3382`](https://github.com/castorini/anserini/pull/3382) to eliminate an external dependency. This has a few implications:

+ At commit [`43add83`](https://github.com/castorini/eval/commit/43add835e20bd66b48f9a640be9bad95a4762d82) (2026/08/09), (in what used to be `tools/`) `topics-and-qrels/` was refactored into separate `topics/` and `qrels/` directories.
At the same time, the repo was renamed from `anserini-tools` to `eval`.
The associated PR is [`eval#118`](https://github.com/castorini/eval/pull/118).
This breaks consumers that depend on fetching a stable `topics-and-qrels/` path (on `master`).
Note that the most obvious solution to add symlinks won't work, as `raw.githubusercontent.com` URLs do not automatically redirect.
+ Anserini commit [`9bfc04b`](https://github.com/castorini/anserini/commit/9bfc04b2d5f22e3acf56edf43d06c1efa5fe2783) (2026/08/11) was the first commit that pinned a specific commit (hence ensuring stability).
The associated PR is [`anserini#3369`](https://github.com/castorini/anserini/pull/3369).
This means that any state of the repo before that commit is likely broken.

---

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
