
# ⚗️ Anserini: Reproductions from Prebuilt Indexes

Anserini ships with many [prebuilt indexes](prebuilt-indexes.md), which allows anyone to reproduce experimental results without needing access to the document collection.
These experiments are grouped around configs, each of which focus on a specific corpus.

## Reproduction Status and Log Summaries

Reproduction checks report `[OK]`, `[OKish]`, or `[FAIL]`.
`[OKish]` is the intermediate status for an acceptable deviation from the expected score; it replaces the historical `[OK*]` label without changing acceptance thresholds or metric classification.

`io.anserini.reproduce.SummarizeLogsFromPrebuiltIndexes` counts metric checks per run and supports `--logs-directory`, `--text` (the default), `--md`, and `--json`.
It accepts both `[OK*]` and `[OKish]`, including mixed log collections, and displays `[OKish]` in plain-text and Markdown summaries.
For compatibility, JSON output retains the existing `[OK*]` key for the combined intermediate-status count; it does not add an `[OKish]` key.
Historical logs do not need to be rewritten.

## Table of Contents

+ [MS MARCO V1 Passage](reproduce/from-prebuilt-indexes/msmarco-v1-passage.md)
+ [MS MARCO V1 Doc](reproduce/from-prebuilt-indexes/msmarco-v1-doc.md)
+ [MS MARCO V2 Passage](reproduce/from-prebuilt-indexes/msmarco-v2-passage.md)
+ [MS MARCO V2 Doc](reproduce/from-prebuilt-indexes/msmarco-v2-doc.md)
+ [MS MARCO V2.1 Segmented Doc](reproduce/from-prebuilt-indexes/msmarco-v2.1-doc-segmented.md)
+ [MS MARCO V2.1 Doc](reproduce/from-prebuilt-indexes/msmarco-v2.1-doc.md)
+ [BEIR](reproduce/from-prebuilt-indexes/beir.md)
+ [BRIGHT](reproduce/from-prebuilt-indexes/bright.md)
