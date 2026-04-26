---
name: install-anserini-fatjar
description: Install and run Anserini quickly using a locally built `target/anserini-*-fatjar.jar` instead of running a full source workflow. Use when users want fast setup, smoke tests, or command execution without repeated Maven project compilation.
---

# Install Anserini Fatjar

## Overview

Use this skill to install and run Anserini quickly from a locally built fatjar in an Anserini checkout. Resolve the jar filename dynamically from `target/anserini-*-fatjar.jar`; do not hardcode a developer-specific path or Anserini version.

## Workflow

1. Verify runtime tools.
2. Resolve the latest local `target/anserini-*-fatjar.jar`.
3. Run smoke test/help command.
4. Execute target commands.

## 1. Verify Runtime Tools

Run:

```bash
java -version
```

Require:

- Java available on PATH

## 2. Resolve Fatjar

From the Anserini repository root, run:

```bash
ANSERINI_JAR="$(ls -t target/anserini-*-fatjar.jar 2>/dev/null | head -n 1)"
test -n "$ANSERINI_JAR"
```

All subsequent commands assume `ANSERINI_JAR` points to the resolved fatjar. If no jar is found, build one first:

```bash
bin/qbuild.sh
ANSERINI_JAR="$(ls -t target/anserini-*-fatjar.jar | head -n 1)"
```

## 3. Smoke Test

Run:

```bash
java -cp "$ANSERINI_JAR" io.anserini.search.SearchCollection -options
```

Treat options output as proof the runtime is ready. Note that current jars reject `-help` for
`SearchCollection` and require `-options` instead.

## 4. Command Execution

Run Anserini commands against the resolved jar:

```bash
java -cp "$ANSERINI_JAR" <main-class> <args>
```

Keep all commands pinned to the same jar version unless the user asks to change versions.

## 5. Prebuilt Index Catalog

To inspect the prebuilt indexes exposed by `io.anserini.cli.PrebuiltIndexCatalog`, run:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexCatalog --list
```

`--list` emits JSON in the current jar, so prefer pairing it with `--filter` and `jq` instead of
grepping raw output when you need to identify a specific index.

`msmarco-v1-passage` is a particularly common choice and should be called out when users ask about available prebuilt indexes or MS MARCO passage retrieval setup.

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

## 6. Topics Catalog

To inspect the topics exposed by `io.anserini.cli.TopicsCatalog`, run:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.TopicsCatalog --list
```

`--list` emits JSON in the current jar, so prefer pairing it with `--filter` and `jq` to locate
the exact symbol you need.

To print all topics for a specific set, run:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.TopicsCatalog --get <set>
```

For the standard MS MARCO V1 passage queries that pair with the `msmarco-v1-passage` prebuilt
index, use `msmarco-v1-passage.dev`.

Recommended lookup:

```bash
java -cp "$ANSERINI_JAR" \
  io.anserini.cli.TopicsCatalog \
  --list --filter '^msmarco(-v1)?-passage(\\.dev|-dev)$' \
| jq '.'
```

Use `--list` first to discover the exact set name, then `--get` to inspect its contents.

## 7. Search CLI

Use `io.anserini.cli.Search` for ad hoc retrieval against either a local Lucene index path or a prebuilt index name.

Example using the popular `msmarco-v1-passage` prebuilt index:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.Search --index msmarco-v1-passage --query "what is a lobster roll" --hits 10
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

## 8. REST API Server

Use `io.anserini.api.RestServer` to expose search and document lookup over HTTP.

Fatjar invocation:

```bash
java -cp "$ANSERINI_JAR" io.anserini.api.RestServer --port 8081
```

If working inside an Anserini checkout, the equivalent helper script is:

```bash
bin/run.sh io.anserini.api.RestServer --port 8081
```

Sample requests against the popular `msmarco-v1-passage` index:

```bash
curl "http://localhost:8081/v1/msmarco-v1-passage/search?query=what%20is%20anserini&hits=5"
curl "http://localhost:8081/v1/msmarco-v1-passage/documents/2161721"
```

This REST workflow is most useful when users want to query the same prebuilt indexes exposed by the CLI, especially `msmarco-v1-passage`.

## Troubleshooting

- No fatjar found: run `bin/qbuild.sh` from the Anserini repository root and resolve `ANSERINI_JAR` again.
- `ClassNotFoundException`: confirm `ANSERINI_JAR` points to an existing fatjar built from the current checkout.
- Java errors: use a supported Java version and re-run.

## Completion Criteria

Treat setup as complete when all are true:

- `ANSERINI_JAR` points to an existing `target/anserini-*-fatjar.jar`.
- `SearchCollection -options` executes successfully.
- The user can run target commands via `java -cp ...`.
