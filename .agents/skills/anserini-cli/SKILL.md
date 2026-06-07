---
name: anserini-cli
description: Run Anserini command-line and REST workflows from either a built fatjar or an Anserini source checkout. Use for PrebuiltIndexRegistry, TopicsRegistry, ad hoc search, interactive search, output formats, and RestServer examples.
metadata:
  version: v0.2.0
---

# Use Anserini CLI

## Overview

Use this skill when Anserini is already available through either a resolved
fatjar or a source checkout. This skill covers command usage, not environment
setup or builds. If no usable fatjar or checkout is present, use
`$install-anserini-fatjar` or `$install-anserini-dev-env` first.

Do not run commands that trigger large prebuilt-index downloads unless the user
explicitly asks for retrieval experiments or index downloads.

Examples below use the fatjar form:

```bash
java -cp "$ANSERINI_JAR" <main-class> <args>
```

From an Anserini source checkout, replace `java -cp "$ANSERINI_JAR"` with
`bin/run.sh`:

```bash
bin/run.sh <main-class> <args>
```

Keep commands pinned to the same jar or checkout unless the user asks to change versions.

## Runtime Check

For a fatjar workflow, confirm `ANSERINI_JAR` is set and points to an existing
jar:

```bash
test -n "$ANSERINI_JAR"
test -f "$ANSERINI_JAR"
```

For a checkout workflow, confirm `bin/run.sh` is available:

```bash
test -x bin/run.sh
```

A useful functional smoke test is:

```bash
java -cp "$ANSERINI_JAR" io.anserini.search.SearchCollection \
  -threads 1 \
  -index cacm \
  -topics cacm \
  -output run.cacm.bm25.txt \
  -hits 1000 \
  -bm25
```

This command may download the small CACM prebuilt index and topics on first use.

## Prebuilt Index Registry

To inspect prebuilt indexes exposed by `io.anserini.cli.PrebuiltIndexRegistry`,
run:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexRegistry --list
```

`--list` emits JSON in current jars, so prefer `--filter` and `jq` instead of
grepping raw output. If `jq` is not available, ask the user whether it should be
installed before relying on `jq` examples.

`msmarco-v1-passage` is a common choice and should be called out when users ask
about available prebuilt indexes or MS MARCO passage retrieval setup.

Recommended lookup for the standard MS MARCO V1 passage inverted index:

```bash
java -cp "$ANSERINI_JAR" \
  io.anserini.cli.PrebuiltIndexRegistry \
  --list --filter '^msmarco-v1-passage$' \
| jq '.[0] | {name, type, description, filename}'
```

Useful variants:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexRegistry --help
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexRegistry --list --filter 'msmarco.*passage' | jq '.[].name'
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexRegistry --type flat --list
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexRegistry --type inverted --list
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexRegistry --type impact --list
java -cp "$ANSERINI_JAR" io.anserini.cli.PrebuiltIndexRegistry --type hnsw --list
```

## Topics Registry

To inspect topics exposed by `io.anserini.cli.TopicsRegistry`, run:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.TopicsRegistry --list
```

`--list` emits JSON in current jars, so prefer `--filter` and `jq` to locate the
exact symbol. If `jq` is not available, ask the user whether it should be
installed before relying on `jq` examples.

To print all topics for a specific set, run:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.TopicsRegistry --get <set>
```

For the standard MS MARCO V1 passage queries that pair with the
`msmarco-v1-passage` prebuilt index, use `msmarco-v1-passage.dev`.

Recommended lookup:

```bash
java -cp "$ANSERINI_JAR" \
  io.anserini.cli.TopicsRegistry \
  --list --filter '^msmarco(-v1)?-passage(\\.dev|-dev)$' \
| jq '.'
```

Use `--list` first to discover the exact set name, then `--get` to inspect its
contents.

## Search CLI

Use `io.anserini.cli.Search` for ad hoc retrieval against either a local Lucene
index path or a prebuilt index name.

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

## Get Document CLI

Use `io.anserini.cli.GetDocument` to fetch the stored raw document for a
collection docid from either a local Lucene index path or a prebuilt index name.

Example using the popular `msmarco-v1-passage` prebuilt index:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.GetDocument --index msmarco-v1-passage --docid 2161721
```

Interactive mode reads docids from stdin:

```bash
java -cp "$ANSERINI_JAR" io.anserini.cli.GetDocument --index msmarco-v1-passage --interactive
```

This command prints the document's stored raw field. It reports an error when
the docid is not found or when the index does not store raw documents.

## SearchCollection

Use `io.anserini.search.SearchCollection` for batch retrieval over a topic set.
It writes TREC run files and supports retrieval-model flags such as `-bm25`,
`-rm3`, `-rocchio`, `-hits`, and `-threads`. Use `io.anserini.cli.Search` instead
for single-query or interactive inspection.

Canonical CACM example using a prebuilt index and built-in topic symbol:

```bash
java -cp "$ANSERINI_JAR" io.anserini.search.SearchCollection \
  -index cacm \
  -topics cacm \
  -output run.cacm.bm25.txt \
  -hits 1000 \
  -bm25
```

Evaluate the CACM run with Anserini's Java `trec_eval` wrapper:

```bash
java -cp "$ANSERINI_JAR" io.anserini.eval.TrecEval \
  -c \
  -m map \
  -m P.30 \
  cacm \
  run.cacm.bm25.txt
```

Expected scores are MAP `0.3123` and P30 `0.1942`.

To verify them mechanically:

```bash
java -cp "$ANSERINI_JAR" io.anserini.eval.TrecEval \
  -c \
  -m map \
  -m P.30 \
  cacm \
  run.cacm.bm25.txt | tee eval.cacm.bm25.txt

grep -q $'map\tall\t0.3123' eval.cacm.bm25.txt
grep -q $'P_30\tall\t0.1942' eval.cacm.bm25.txt
```

## REST API Server

Use `io.anserini.api.RestServer` to expose search and document lookup over HTTP.

Fatjar invocation:

```bash
java -cp "$ANSERINI_JAR" io.anserini.api.RestServer --port 8081
```

Sample requests against the popular `msmarco-v1-passage` index:

```bash
curl "http://localhost:8081/v1/msmarco-v1-passage/search?query=what%20is%20anserini&hits=5"
curl "http://localhost:8081/v1/msmarco-v1-passage/doc/2161721"
```

This REST workflow is most useful when users want to query the same prebuilt
indexes exposed by the CLI, especially `msmarco-v1-passage`.

## Troubleshooting

- No fatjar found: use `$install-anserini-fatjar` to download a released Maven
  Central fatjar, or `$install-anserini-dev-env` if the user needs a jar built
  from the source checkout.
- Missing `bin/run.sh`: use `$install-anserini-dev-env` from an Anserini checkout.
- `ClassNotFoundException`: confirm the jar or checkout was built from the
  expected Anserini version.
- `RestServer` reports `Port already in use` for unused ports in a sandboxed
  Codex session: local socket binding may be blocked by sandbox permissions.
  Rerun the server command with escalation, and use an available high local port
  if the documented port is occupied.
