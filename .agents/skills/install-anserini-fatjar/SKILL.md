---
name: install-anserini-fatjar
description: Install and verify Anserini quickly by downloading the published fatjar from Maven Central instead of cloning or building the source repository. Use when users want fast setup, smoke tests, or CLI examples from a released Anserini jar.
---

# Install Anserini Fatjar

## Overview

Use this skill to install and verify Anserini quickly from a published fatjar. Do not clone the repository and do not build from source in this workflow. Fatjar reproduction workflows are designed to run directly from the Maven Central fatjar, so a source checkout is not required. Download the released `anserini-*-fatjar.jar` from Maven Central, set `ANSERINI_JAR` to the downloaded file, and smoke test it.

If the user needs source development, local code changes, or a snapshot jar built from the current checkout, use `$install-anserini-dev-env` instead.

## Workflow

1. Verify runtime tools.
2. Download the released fatjar from Maven Central.
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

## 2. Download Fatjar

Choose the requested Anserini release. If the user does not specify a version, verify the latest Maven Central release before choosing `ANSERINI_VERSION`. Check:

```text
https://repo1.maven.org/maven2/io/anserini/anserini/maven-metadata.xml
```

A quick command-line check is:

```bash
curl -sS https://repo1.maven.org/maven2/io/anserini/anserini/maven-metadata.xml \
  | sed -n 's:.*<release>\(.*\)</release>.*:\1:p'
```

Then download that release:

```bash
ANSERINI_VERSION=2.0.0
wget "https://repo1.maven.org/maven2/io/anserini/anserini/${ANSERINI_VERSION}/anserini-${ANSERINI_VERSION}-fatjar.jar"
ANSERINI_JAR="anserini-${ANSERINI_VERSION}-fatjar.jar"
test -f "$ANSERINI_JAR"
```

If `wget` is unavailable, use `curl`:

```bash
curl -fL -o "anserini-${ANSERINI_VERSION}-fatjar.jar" \
  "https://repo1.maven.org/maven2/io/anserini/anserini/${ANSERINI_VERSION}/anserini-${ANSERINI_VERSION}-fatjar.jar"
ANSERINI_JAR="anserini-${ANSERINI_VERSION}-fatjar.jar"
test -f "$ANSERINI_JAR"
```

All subsequent commands assume `ANSERINI_JAR` points to the downloaded fatjar. Keep commands pinned to this jar unless the user asks to change versions.

## 3. Smoke Test

Run:

```bash
java -cp "$ANSERINI_JAR" io.anserini.search.SearchCollection -options
```

Treat options output as proof the runtime is ready. Note that current jars reject `-help` for
`SearchCollection` and require `-options` instead.

## 4. Command Execution

Run Anserini commands against the downloaded jar:

```bash
java -cp "$ANSERINI_JAR" <main-class> <args>
```

Keep all commands pinned to the same jar version unless the user asks to change versions.

For search, prebuilt-index catalog, topics catalog, or REST server examples, use `$use-anserini-cli`.

## Troubleshooting

- No fatjar found: download the release fatjar from Maven Central and set `ANSERINI_JAR` to that path.
- Download returns 404: confirm the requested Anserini version exists on Maven Central.
- `ClassNotFoundException`: confirm `ANSERINI_JAR` points to the downloaded `anserini-*-fatjar.jar`, not the thin jar.
- Java errors: use a supported Java version and re-run.

## Completion Criteria

Treat setup as complete when all are true:

- `ANSERINI_JAR` points to an existing downloaded `anserini-*-fatjar.jar`.
- `SearchCollection -options` executes successfully.
- The user can run target commands via `java -cp ...`.
