---
name: install-anserini-fatjar
description: Install and verify Anserini quickly using a locally built `target/anserini-*-fatjar.jar` instead of running a full source workflow. Use when users want fast setup or smoke tests without repeated Maven project compilation.
---

# Install Anserini Fatjar

## Overview

Use this skill to install and verify Anserini quickly from a locally built fatjar in an Anserini checkout. Resolve the jar filename dynamically from `target/anserini-*-fatjar.jar`; do not hardcode a developer-specific path or Anserini version.

## Workflow

1. Verify runtime tools.
2. Resolve the latest local `target/anserini-*-fatjar.jar`.
3. Run smoke test/help command.
4. Hand off to `$use-anserini-cli` for search, catalog, topics, or REST commands.

## 1. Verify Runtime Tools

Run:

```bash
java -version
```

Require:

- Java available on PATH
- Java major version `21`, matching Anserini's current runtime/build requirement

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

If `bin/qbuild.sh` fails because Java, Maven, submodules, or dependency setup is missing, switch to `$install-anserini-dev-env` instead of debugging the fatjar workflow here.

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

For search, prebuilt-index catalog, topics catalog, or REST server examples, use `$use-anserini-cli`.

## Troubleshooting

- No fatjar found: run `bin/qbuild.sh` from the Anserini repository root and resolve `ANSERINI_JAR` again.
- `ClassNotFoundException`: confirm `ANSERINI_JAR` points to an existing fatjar built from the current checkout.
- Java errors: use a supported Java version and re-run.

## Completion Criteria

Treat setup as complete when all are true:

- `ANSERINI_JAR` points to an existing `target/anserini-*-fatjar.jar`.
- `SearchCollection -options` executes successfully.
- The user can run target commands via `java -cp ...`.
