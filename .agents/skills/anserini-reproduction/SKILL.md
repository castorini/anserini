---
name: anserini-reproduction
version: v0.1.0
description: Reproduce experimental results with Anserini. Use when Codex needs to run or explain Anserini reproduction workflows for published or reported results, including reproductions with prebuilt indexes, reproductions from raw document collections, reproduction YAMLs, run generation, evaluation, and metric verification.
---

# Anserini Reproduction

## Overview

Use this skill to reproduce experimental results with Anserini after the source checkout or fatjar is available. Prefer established reproduction commands, reproduction definitions, and checked evaluation tools over ad hoc command construction.

## Workflow

1. Identify the reproduction target:
   - dataset/collection
   - index type or prebuilt index name
   - retrieval model and parameters
   - topics and qrels
   - expected metrics and tolerances
2. Confirm the environment is ready:
   - use `$install-anserini-dev-env` for source builds, submodules, and evaluation tools
   - use `$install-anserini-fatjar` for released fatjar-only reproduction
   - use `$use-anserini-cli` for command syntax, catalog lookup, search, and REST examples
3. Prefer checked reproduction definitions under `src/main/resources/reproduce` when available.
4. Run the reproduction, capture the run output path, evaluate with the appropriate tool, and compare against expected metrics.
5. Report exact commands, generated run files, metrics, and any deviation from expected results.

## Reproductions with Prebuilt Indexes

Use main class `io.anserini.reproduce.ReproduceFromPrebuiltIndexes` for
reproductions that start from Anserini prebuilt indexes rather than rebuilding
indexes from raw document collections.

High-level behavior:

- Loads a named YAML config from
  `src/main/resources/reproduce/from-prebuilt-indexes/configs`.
- Reads configured retrieval conditions, topic sets, eval/qrels keys, metrics,
  metric-specific `trec_eval` arguments, and expected scores.
- Expands command placeholders such as `$fatjar`, `$threads`, `$topics`,
  `$output`, and `$runs_directory`.
- Runs the configured retrieval command for each condition/topic pair.
- Writes each result as a TREC run file.
- Runs `trec_eval` for each expected metric.
- Compares observed scores against expected values and reports whether each
  metric matches, is close, or fails.

Useful commands:

- Run with `--help` to inspect the current command-line options.
- `--list`: enumerate available configs.
- Use `--dry-run --print-commands` before large reproductions.
- Use `--compute-index-size` before runs that may trigger large prebuilt-index
  downloads.

Operational guidance:

- Use this workflow for reproducing published or reported results with
  already-packaged Anserini indexes.
- Run `--list` first if the config name is unknown.
- Prefer `--dry-run --print-commands` before large configs.
- Use `--compute-index-size` before runs that may trigger large prebuilt-index
  downloads.
- Preserve generated run files and report observed metrics alongside expected
  scores and any deviations.

## Reproductions from Raw Document Collections

Use main class `io.anserini.reproduce.ReproduceFromDocumentCollection` for
reproductions that start from raw document collections and build indexes
locally.

High-level behavior:

- Loads a named YAML config from
  `src/main/resources/reproduce/from-document-collection/configs`.
- Reads the configured corpus, indexing, search, evaluation, and
  expected-result settings from the YAML file.
- Optionally downloads and extracts the configured corpus with `--download`.
- Builds the configured index with `--index`.
- Verifies expected index statistics with `--verify`, using `IndexReaderUtils`
  for supported index types.
- Runs configured retrieval models over configured topics with `--search`.
- Runs optional conversion commands after search when the config defines
  conversions.
- Evaluates generated run files using the configured metric commands.
- Compares observed scores against expected values and reports whether each
  metric matches, is close, or fails.
- Reports total elapsed time for non-dry-run executions.

Useful commands:

- Run with `--help` to inspect the current command-line options.
- `--list`: enumerate available configs.
- Use `--dry-run` before expensive indexing, search, or download work.
- Combine workflow stages such as `--download`, `--index`, `--verify`, and
  `--search` as needed.

Operational guidance:

- Use this workflow when reproducing results requires building local indexes
  from raw document collections.
- Run `--list` first if the config name is unknown.
- Prefer `--dry-run` before expensive indexing or search runs.
- Use `--corpus-path` when the collection is already available outside the
  configured search roots.
- Do not use `--download` unless the user explicitly wants to fetch the
  configured collection.
- Prefer `--index --verify --search` for an end-to-end reproduction from an
  already available collection.
- Capture generated index paths, run files, verification output, observed
  metrics, expected scores, and any deviations.
