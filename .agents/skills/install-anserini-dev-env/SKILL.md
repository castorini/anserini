---
name: install-anserini-dev-env
description: Set up and verify Anserini source-development environments. Use for JDK 21, Maven 3.9+, submodules, Anserini build scripts, smoke tests, and Java/Maven troubleshooting in castorini/anserini.
metadata:
  version: v0.2.0
---

# Install Anserini Dev Env

## Overview

Use this skill to prepare Anserini for source development, not just fatjar
execution. Prefer normal Java tooling over Python-style virtual environments:
pin/select JDK 21, use Maven 3.9+, clone submodules, then build with Anserini's
scripts or Maven.

Do not assume a released fatjar is sufficient for source-development requests;
use the checkout when local code, tests, or snapshots matter.

## Workflow

1. Verify requirements first:
   - Require `java` to report major version `21`.
   - Require `mvn` to report version `3.9` or newer.
   - Check for `git` and `make`; `make` is needed for bundled evaluation tools.

```bash
java -version
mvn -v
git --version
make --version
git submodule status --recursive
test -d tools/eval
```
2. If Java is missing or not exactly 21, select or install JDK 21 with the
   user's existing version manager when possible:
   - Prefer `mise`, `sdkman`, `asdf`, or `jenv` if already present.
   - On macOS, Homebrew Temurin/OpenJDK 21 is acceptable when no Java version
     manager is in use.
   - Do not create a Python virtual environment for Java development.
3. If Maven is missing or older than 3.9, update Maven using the host's package
   manager or version manager. Anserini does not currently rely on a checked-in
   Maven wrapper.
4. Clone with submodules:

```bash
git clone --recurse-submodules https://github.com/castorini/anserini.git
```

If the user has already provided an empty destination directory and wants the
checkout there, clone into the current directory:

```bash
git clone --recurse-submodules https://github.com/castorini/anserini.git .
```

For an existing checkout, run:

```bash
git submodule update --init --recursive
```

5. Build from the repository root. Prefer Anserini's checked-in build scripts
   when present:

```bash
bin/qbuild.sh
```

Use `bin/qbuild.sh` for a quick build; it skips tests and Javadocs.

After a successful quick build, expect the shaded artifact at
`target/anserini-*-fatjar.jar`.

```bash
bin/build.sh
```

Use `bin/build.sh` for a full build; it runs all tests and can take a while.
While it runs, provide periodic progress updates. Track completed Surefire tests
from Maven output or `target/surefire-reports` when possible, and report the
final aggregate `Tests run: N` count when the build finishes.

If a script is unavailable or the user explicitly asks for Maven, use:

```bash
mvn clean package
```

6. Build evaluation tools only when needed for evaluation workflows:
   ensure `tools/` is initialized first because these commands assume the
   `anserini-tools` submodule is present.

```bash
git submodule update --init --recursive
tar xvfz tools/eval/trec_eval.9.0.4.tar.gz -C tools/eval
make -C tools/eval/trec_eval.9.0.4
make -C tools/eval/ndeval
```

Use separate shell commands when executing this workflow in Codex so failures are
visible at the exact step.

## Verification

After setup, re-run the explicit requirement checks. Then run the lightest
verification that matches the user's goal: `bin/qbuild.sh` for a build-ready
checkout, `bin/build.sh` for full validation, or targeted Maven tests for a code
change. If dependency downloads fail because network access is sandboxed, rerun
the build command with escalation instead of changing project files.

If a build was run, a concise final sanity check is:

```bash
git status --short --branch --ignored
git submodule status --recursive
ls -lh target/*fatjar.jar
```

For a functional smoke test after a successful build, run the CACM prebuilt-index
reproduction command:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index cacm \
  -topics cacm \
  -output run.cacm.bm25.txt \
  -hits 1000 \
  -bm25
```

Treat a successful run and generated `run.cacm.bm25.txt` file as proof that the
checkout can execute Anserini search end to end. This command may download the
small CACM prebuilt index and topics on first use, so skip it when the user only
wants a local build check.

Then evaluate the run with Anserini's Java `trec_eval` wrapper:

```bash
bin/run.sh io.anserini.eval.TrecEval \
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
bin/run.sh io.anserini.eval.TrecEval \
  -c \
  -m map \
  -m P.30 \
  cacm \
  run.cacm.bm25.txt | tee eval.cacm.bm25.txt

grep -q $'map\tall\t0.3123' eval.cacm.bm25.txt
grep -q $'P_30\tall\t0.1942' eval.cacm.bm25.txt
```

For CLI examples after setup, use `$anserini-cli`.

## Troubleshooting

- If `java -version` and `mvn -v` disagree about Java versions, fix `JAVA_HOME`
  and `PATH` so Maven uses JDK 21.
- On macOS, `trec_eval` and `ndeval` may emit warnings from older C code,
  including `bzero` or `bcopy` macro redefinitions, deprecated non-prototype
  functions, or `printf` format-security warnings. Treat these as expected if
  `make` exits successfully and the binaries are produced.
- On Windows, use WSL2 for Anserini builds.
- Avoid commands that trigger large prebuilt index downloads unless the user
  explicitly asks for retrieval experiments; Anserini can download large indexes
  on demand.
- Treat Anserini's current README as the source of truth for version
  requirements if it differs from this skill.
