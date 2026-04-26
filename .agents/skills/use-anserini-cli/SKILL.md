---
name: use-anserini-cli
description: Run Anserini command-line and REST workflows from either a built fatjar or an Anserini source checkout. Use for PrebuiltIndexCatalog, TopicsCatalog, ad hoc search, interactive search, output formats, and RestServer examples.
---

# Use Anserini CLI

## Overview

Use this skill when Anserini is already available through either a resolved fatjar or a source checkout. This skill covers command usage, not environment setup or builds. If no usable fatjar or checkout is present, use `$install-anserini-fatjar` or `$install-anserini-dev-env` first.

Prefer the invocation form that matches the user's environment:

```bash
java -cp "$ANSERINI_JAR" <main-class> <args>
```

or, from an Anserini checkout:

```bash
bin/run.sh <main-class> <args>
```

Keep commands pinned to the same jar or checkout unless the user asks to change versions.

## Runtime Check

For a fatjar workflow, confirm `ANSERINI_JAR` is set and points to an existing jar:

```bash
test -n "$ANSERINI_JAR"
test -f "$ANSERINI_JAR"
```

For a checkout workflow, confirm `bin/run.sh` is available:

```bash
test -x bin/run.sh
```

A useful smoke test is:

```bash
java -cp "$ANSERINI_JAR" io.anserini.search.SearchCollection -options
```

or:

```bash
bin/run.sh io.anserini.search.SearchCollection -options
```

Current jars reject `-help` for `SearchCollection`; use `-options`.

## Prebuilt Index Catalog

To inspect prebuilt indexes exposed by `io.anserini.cli.PrebuiltIndexCatalog`, run:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexCatalog --list
```

or:

```bash
bin/run.sh io.anserini.cli.PrebuiltIndexCatalog --list
```

`--list` emits JSON in current jars, so prefer `--filter` and `jq` instead of grepping raw output.

`msmarco-v1-passage` is a common choice and should be called out when users ask about available prebuilt indexes or MS MARCO passage retrieval setup.

Recommended lookup for the standard MS MARCO V1 passage inverted index:

```bash
java -cp "$ANSERINI_JAR" \
  io.anserini.cli.PrebuiltIndexCatalog \
  --list --filter '^msmarco-v1-passage$' \
| jq '.[0] | {name, type, description, filename}'
```

Useful variants:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexCatalog --help
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexCatalog --list --filter 'msmarco.*passage' | jq '.[].name'
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexCatalog --type flat --list
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexCatalog --type inverted --list
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexCatalog --type impact --list
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexCatalog --type hnsw --list
```

Translate the examples to `bin/run.sh io.anserini.cli.PrebuiltIndexCatalog ...` when working from a checkout without `ANSERINI_JAR`.

## Topics Catalog

To inspect topics exposed by `io.anserini.cli.TopicsCatalog`, run:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.TopicsCatalog --list
```

or:

```bash
bin/run.sh io.anserini.cli.TopicsCatalog --list
```

`--list` emits JSON in current jars, so prefer `--filter` and `jq` to locate the exact symbol.

To print all topics for a specific set, run:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.TopicsCatalog --get <set>
```

For the standard MS MARCO V1 passage queries that pair with the `msmarco-v1-passage` prebuilt index, use `msmarco-v1-passage.dev`.

Recommended lookup:

```bash
java -cp "$ANSERINI_JAR" \
  io.anserini.cli.TopicsCatalog \
  --list --filter '^msmarco(-v1)?-passage(\\.dev|-dev)$' \
| jq '.'
```

Use `--list` first to discover the exact set name, then `--get` to inspect its contents.

## Search CLI

Use `io.anserini.cli.Search` for ad hoc retrieval against either a local Lucene index path or a prebuilt index name.

Example using the popular `msmarco-v1-passage` prebuilt index:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.Search --index msmarco-v1-passage --query "what is a lobster roll" --hits 10
```

Checkout equivalent:

```bash
bin/run.sh io.anserini.cli.Search --index msmarco-v1-passage --query "what is a lobster roll" --hits 10
```

Interactive mode:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.Search --index msmarco-v1-passage --interactive
```

Useful output variants:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.Search --index msmarco-v1-passage --query "what is a lobster roll" --json
java -cp "$ANSERINI_JAR" io.anserini.cli.Search --index msmarco-v1-passage --query "what is a lobster roll" --trec
```

## REST API Server

Use `io.anserini.api.RestServer` to expose search and document lookup over HTTP.

Fatjar invocation:

```bash
java -cp "$ANSERINI_JAR" io.anserini.api.RestServer --port 8081
```

Checkout invocation:

```bash
bin/run.sh io.anserini.api.RestServer --port 8081
```

Sample requests against the popular `msmarco-v1-passage` index:

```bash
curl "http://localhost:8081/v1/msmarco-v1-passage/search?query=what%20is%20anserini&hits=5"
curl "http://localhost:8081/v1/msmarco-v1-passage/doc/2161721"
```

This REST workflow is most useful when users want to query the same prebuilt indexes exposed by the CLI, especially `msmarco-v1-passage`.

## Troubleshooting

- No fatjar found: use `$install-anserini-fatjar` to download a released Maven Central fatjar, or `$install-anserini-dev-env` if the user needs a jar built from the source checkout.
- Missing `bin/run.sh`: use `$install-anserini-dev-env` from an Anserini checkout.
- `ClassNotFoundException`: confirm the jar or checkout was built from the expected Anserini version.
- `RestServer` reports `Port already in use` for unused ports in a sandboxed Codex session: local socket binding may be blocked by sandbox permissions. Rerun the server command with escalation, and use an available high local port if the documented port is occupied.
- Large downloads: prebuilt indexes can download on demand; avoid commands that trigger large retrieval assets unless the user explicitly asks.
