---
name: install-anserini-dev-env
description: Set up and verify Anserini source-development environments. Use for JDK 21, Maven 3.9+, submodules, Anserini build scripts, smoke tests, and Java/Maven troubleshooting in castorini/anserini.
---

# Install Anserini Dev Env

## Overview

Use this skill to prepare Anserini for source development, not just fatjar execution. Prefer normal Java tooling over Python-style virtual environments: pin/select JDK 21, use Maven 3.9+, clone submodules, then build with Anserini's scripts or Maven.

## Workflow

1. Verify requirements first:
   - Run `scripts/check_anserini_dev_env.sh` from this skill when checking an existing machine.
   - Require `java` to report major version `21`.
   - Require `mvn` to report version `3.9` or newer.
   - Check for `git` and `make`; `make` is needed for bundled evaluation tools.
2. If Java is missing or not exactly 21, select or install JDK 21 with the user's existing version manager when possible:
   - Prefer `mise`, `sdkman`, `asdf`, or `jenv` if already present.
   - On macOS, Homebrew Temurin/OpenJDK 21 is acceptable when no Java version manager is in use.
   - Do not create a Python virtual environment for Java development.
3. If Maven is missing or older than 3.9, update Maven using the host's package manager or version manager. Anserini does not currently rely on a checked-in Maven wrapper.
4. Clone with submodules:

```bash
git clone --recurse-submodules https://github.com/castorini/anserini.git
```

If the user has already provided an empty destination directory and wants the checkout there, clone into the current directory:

```bash
git clone --recurse-submodules https://github.com/castorini/anserini.git .
```

For an existing checkout, run:

```bash
git submodule update --init --recursive
```

5. Build from the repository root. Prefer Anserini's checked-in build scripts when present:

```bash
bin/qbuild.sh
```

Use `bin/qbuild.sh` for a quick build; it skips tests and Javadocs.

After a successful quick build, expect the shaded artifact at `target/anserini-*-fatjar.jar`.

```bash
bin/build.sh
```

Use `bin/build.sh` for a full build; it runs all tests and can take a while.

If a script is unavailable or the user explicitly asks for Maven, use:

```bash
mvn clean package
```

6. Build evaluation tools only when needed for evaluation workflows:

```bash
tar xvfz tools/eval/trec_eval.9.0.4.tar.gz -C tools/eval
make -C tools/eval/trec_eval.9.0.4
make -C tools/eval/ndeval
```

Use separate shell commands when executing this workflow in Codex so failures are visible at the exact step.

## Verification

After setup, run the check script again. Then run the lightest verification that matches the user's goal: `bin/qbuild.sh` for a build-ready checkout, `bin/build.sh` for full validation, or targeted Maven tests for a code change. If dependency downloads fail because network access is sandboxed, rerun the build command with escalation instead of changing project files.

If a build was run, a concise final sanity check is:

```bash
git status --short --branch --ignored
git submodule status --recursive
ls -lh target/*fatjar.jar
```

For search, prebuilt-index registry, topics registry, or REST server examples after setup, use `$use-anserini-cli`.

## Troubleshooting

- If `java -version` and `mvn -v` disagree about Java versions, fix `JAVA_HOME` and `PATH` so Maven uses JDK 21.
- On macOS, `trec_eval` and `ndeval` may emit warnings from older C code, including `bzero` or `bcopy` macro redefinitions, deprecated non-prototype functions, or `printf` format-security warnings. Treat these as expected if `make` exits successfully and the binaries are produced.
- On Windows, use WSL2 for Anserini builds.
- Avoid commands that trigger large prebuilt index downloads unless the user explicitly asks for retrieval experiments; Anserini can download large indexes on demand.
- Treat Anserini's current README as the source of truth for version requirements if it differs from this skill.
