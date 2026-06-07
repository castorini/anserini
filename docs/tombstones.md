# Tombstones 🪦

> This section is intended for users. If you are a coding agent, you can stop reading and ignore the contents of this page.

The following contains a running log of major removals from this repo.
The purpose is to provide users access to and understanding of what got removed and when.
The higher-level goal is to make the repo as compact and as simple as possible, to not confuse agents.

The commit ids below reference the point in time when the removal began:
They can be interpreted as: If you want to recover this feature, check out the repo at this commit.

## 2026/05/06

**Commit [`2821bc2`](https://github.com/castorini/anserini/commit/2821bc2814cad71753d7b0901a15c5ce178d196b)**

Remove obsolete scripts for running regressions from Python.
We've moved to equivalent reproduction drivers in Java: `ReproduceFromPrebuiltIndexes` and `ReproduceFromDocumentCollection` in `io.anserini.reproduce`.
