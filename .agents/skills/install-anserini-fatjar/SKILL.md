---
name: install-anserini-fatjar
description: Install and verify Anserini quickly by downloading the published fatjar from Maven Central instead of cloning or building the source repository. Use when users want fast setup, smoke tests, or CLI examples from a released Anserini jar.
metadata:
  version: v0.2.0
---

# Install Anserini Fatjar

## Overview

Use this skill to install and verify Anserini quickly from a published fatjar.
Fatjar reproduction workflows are designed to run directly from the Maven
Central fatjar, so a source checkout is not required. Download the released
`anserini-*-fatjar.jar` from Maven Central, set `ANSERINI_JAR` to the downloaded
file, and smoke test it.

Do not clone or build the source repository in this workflow.

If the user needs source development, local code changes, or a snapshot jar
built from the current checkout, use `$install-anserini-dev-env` instead.

## Workflow

1. Verify runtime tools.
2. Download the released fatjar from Maven Central.
3. Run the CACM prebuilt-index smoke test.
4. Use `$anserini-cli` for CLI examples after setup.

## 1. Verify Runtime Tools

Run:

```bash
java -version
```

Require:

- Java available on PATH
- Java major version `21`, matching Anserini's current runtime/build
  requirement

## 2. Download Fatjar

Choose the requested Anserini release. If the user does not specify a version,
verify the latest Maven Central release before choosing `ANSERINI_VERSION`.
Check:

```text
https://repo1.maven.org/maven2/io/anserini/anserini/maven-metadata.xml
```

A quick command-line check is:

```bash
ANSERINI_VERSION="$(curl -sS https://repo1.maven.org/maven2/io/anserini/anserini/maven-metadata.xml \
  | sed -n 's:.*<release>\(.*\)</release>.*:\1:p')"
test -n "$ANSERINI_VERSION"
```

Then download that release:

```bash
curl -fL -o "anserini-${ANSERINI_VERSION}-fatjar.jar" \
  "https://repo1.maven.org/maven2/io/anserini/anserini/${ANSERINI_VERSION}/anserini-${ANSERINI_VERSION}-fatjar.jar"
ANSERINI_JAR="anserini-${ANSERINI_VERSION}-fatjar.jar"
test -f "$ANSERINI_JAR"
```

If the user requested a specific release, set `ANSERINI_VERSION` to that version
instead of the discovered latest release before downloading.

If `curl` is unavailable, use `wget`:

```bash
wget "https://repo1.maven.org/maven2/io/anserini/anserini/${ANSERINI_VERSION}/anserini-${ANSERINI_VERSION}-fatjar.jar"
ANSERINI_JAR="anserini-${ANSERINI_VERSION}-fatjar.jar"
test -f "$ANSERINI_JAR"
```

All subsequent commands assume `ANSERINI_JAR` points to the downloaded fatjar.
Keep commands pinned to this jar unless the user asks to change versions.

## 3. Smoke Test

Run:

```bash
java -cp "$ANSERINI_JAR" io.anserini.search.SearchCollection \
  -threads 1 \
  -index cacm \
  -topics cacm \
  -output run.cacm.bm25.txt \
  -hits 1000 \
  -bm25
```

Treat a successful run and generated `run.cacm.bm25.txt` file as proof the
runtime is ready. This command follows the CACM prebuilt-index reproduction
config and may download the small CACM prebuilt index and topics on first use.

Then evaluate the run with Anserini's Java `trec_eval` wrapper:

```bash
java -cp "$ANSERINI_JAR" io.anserini.eval.TrecEval \
  -c \
  -m map \
  -m P.30 \
  cacm \
  run.cacm.bm25.txt
```

Expected scores are:

```text
map     all     0.3123
P_30    all     0.1942
```

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

## 4. Command Execution

Run Anserini commands against the downloaded jar:

```bash
java -cp "$ANSERINI_JAR" <main-class> <args>
```

Keep all commands pinned to the same jar version unless the user asks to change
versions.

## Troubleshooting

- No fatjar found: download the release fatjar from Maven Central and set
  `ANSERINI_JAR` to that path.
- Download returns 404: confirm the requested Anserini version exists on Maven Central.
- `ClassNotFoundException`: confirm `ANSERINI_JAR` points to the downloaded
  `anserini-*-fatjar.jar`, not the thin jar.
- Java errors: use a supported Java version and re-run.

## Completion Criteria

Treat setup as complete when all are true:

- `ANSERINI_JAR` points to an existing downloaded `anserini-*-fatjar.jar`.
- The CACM `SearchCollection` smoke test executes successfully and writes
  `run.cacm.bm25.txt`.
- Java `TrecEval` verifies MAP `0.3123` and P30 `0.1942` for the CACM run.
- The user can run target commands via `java -cp ...`.
