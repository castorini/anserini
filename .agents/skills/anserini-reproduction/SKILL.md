---
name: anserini-reproduction
description: Reproduce experimental results with Anserini. Use when Codex needs to run or explain Anserini reproduction workflows for published or reported results, including reproductions with prebuilt indexes, reproductions from raw document collections, reproduction YAMLs, run generation, evaluation, and metric verification.
metadata:
  version: v0.2.0
---

# Anserini Reproduction

## Overview

Use this skill to reproduce experimental results with Anserini after the source
checkout or fatjar is available. Prefer established reproduction commands,
reproduction definitions, and checked evaluation tools over ad hoc command
construction.

Do not run reproductions that trigger large index or collection downloads unless
the user explicitly asks to execute them.

When the user asks broadly about reproduction types, experiment types, or
related terminology, follow progressive disclosure: first summarize only the
two main reproduction types, then ask which one they want to dive into:

1. Reproductions with Prebuilt Indexes
2. Reproductions from Raw Document Collections

Keep the first answer concise. Do not enumerate command-line options or
implementation details until the user chooses a type or asks for more detail.

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
   - use `$anserini-cli` for command syntax, catalog lookup, search, and REST examples
3. Prefer checked reproduction definitions bundled with Anserini when available.
4. Run the reproduction, capture the run output path, evaluate with the
   appropriate tool, and compare against expected metrics.
5. Report exact commands, generated run files, metrics, and any deviation from expected results.

## Reproductions with Prebuilt Indexes

Use main class `io.anserini.reproduce.ReproduceFromPrebuiltIndexes` for
reproductions that start from Anserini prebuilt indexes rather than rebuilding
indexes from raw document collections.

For current source-checkout workflows, the latest supported configs, generated
reproduction pages, and command guidance are maintained at:

```text
https://github.com/castorini/anserini/blob/master/docs/ref-reproduce-from-prebuilt-indexes.md
```

Consult that page before giving detailed config lists, exact commands, or
dataset/model coverage. For pinned release or fatjar workflows, prefer the docs
bundled with or tagged for that release when they differ from `master`.

Useful commands:

- Run with `--help` to inspect the current command-line options.
- List available configs:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromPrebuiltIndexes --list
```

- Print a specific config:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromPrebuiltIndexes --config <config> --show
```

- Preview commands, expected scores, and referenced prebuilt-index sizes:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromPrebuiltIndexes --config <config> --dry-run
```

High-level behavior:

- Loads a YAML config.
- Reads configured retrieval conditions, topic sets, eval/qrels keys, metrics,
  metric-specific `trec_eval` arguments, and expected scores.
- Expands command placeholders such as `$fatjar`, `$threads`, `$topics`,
  `$output`, and `$runs_directory`.
- Runs the configured retrieval command for each condition/topic pair.
- Writes each result as a TREC run file.
- Runs `trec_eval` for each expected metric.
- Compares observed scores against expected values and reports whether each
  metric matches, is close, or fails.

## Reproductions from Raw Document Collections

Use main class `io.anserini.reproduce.ReproduceFromDocumentCollection` for
reproductions that start from raw document collections and build indexes
locally.

For current source-checkout workflows, the latest supported configs, generated
reproduction pages, and command guidance are maintained at:

```text
https://github.com/castorini/anserini/blob/master/docs/ref-reproduce-from-document-collections.md
```

Consult that page before giving detailed config lists, exact commands, or
dataset/model coverage. For pinned release or fatjar workflows, prefer the docs
bundled with or tagged for that release when they differ from `master`.

Config discovery:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --list
```

The list is emitted as JSON. Use `jq` to browse or filter it, for example:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --list | jq -r '.[]'
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --list | jq -r '.[] | select(test("msmarco-v1-passage"))'
```

Document pages deterministically map from config name to:

```text
https://github.com/castorini/anserini/blob/master/docs/reproduce/from-document-collection/<config>.md
```

For example, config `msmarco-v1-passage` maps to:

```text
https://github.com/castorini/anserini/blob/master/docs/reproduce/from-document-collection/msmarco-v1-passage.md
```

Useful commands:

- Run with `--help` to inspect the current command-line options.
- `--config <config> --show`: print a specific config.
- Use `--dry-run` before expensive indexing, search, or download work.
- Combine workflow stages such as `--download`, `--index`, `--verify`, and
  `--search` as needed.

High-level behavior:

- Loads a YAML config.
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
