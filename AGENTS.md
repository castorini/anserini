# Repo Project Instructions

## Scope and Stack
- This repository is **Anserini** (`io.anserini:anserini`), a Java toolkit for reproducible IR research built on Apache Lucene.
- Primary language: Java (`src/main/java`).
- Build system: Maven (`pom.xml`), artifact includes both thin jar and shaded `-fatjar`.
- Required runtime/build versions: **Java 21** and Maven **3.9+**.

## Task-Specific Skills
- Use `$install-anserini-dev-env` when the user needs source checkout setup, Java/Maven verification, submodules, full or quick builds, Maven troubleshooting, or eval tool setup.
- Use `$install-anserini-fatjar` when the user only needs to download a released Maven Central fatjar and run fatjar smoke tests, without cloning or building the source repository.
- Use `$use-anserini-cli` only after Anserini is available, for catalog, topics, search, run output, and REST server commands.
- Use `$anserini-reproduction` whenever the user mentions reproductions, reproducibility, reproducing results, reported results, or experimental result verification; load the skill and provide workflow information from there.
- If a task spans setup and CLI usage, use the relevant setup skill first, then `$use-anserini-cli`.

## Repository Layout
- This repository follows a standard Maven/Java project layout.
- `src/main/resources/reproduce`: YAML definitions and templates for reproduction workflows.
- `tools/`: git submodule (`anserini-tools`) containing eval scripts/assets.

## Build, Test, and Run
- For source builds, prefer checked-in scripts:
  - quick build: `bin/qbuild.sh`
  - full build: `bin/build.sh`
- When running the full build script, track and report the final Maven Surefire test count (for example, `Tests run: N`) so the user knows how many tests are present in the suite.
- While `bin/build.sh` is running, provide periodic progress updates to the user, especially during long or quiet test phases; include `X/Y tests completed` when the completed count and total suite size are known or can be inferred from Maven output.
- Use Maven directly when requested or when scripts are unavailable:
  - `mvn clean package`
- Run main classes from checkout with:
  - `bin/run.sh <main-class> [args...]`

## Testing Expectations
- Unit/integration tests run via Maven Surefire; do not assume changes are safe without at least targeted tests.
- For Java changes, run targeted tests first, then broaden as needed:
  - `mvn -Dtest=ClassName test`
  - `mvn test`
- Reproducibility is a core project contract; for reproductions or result verification, use `$anserini-reproduction`.

## Submodules and External Tools
- `tools/` is a required git submodule. After clone:
  - `git submodule update --init --recursive`
- If evaluation binaries are needed locally, use `$install-anserini-dev-env`; it contains the exact checked setup steps for `tools/eval`.

## Editing and Contribution Guardrails
- Prefer minimal, behavior-preserving changes unless behavior change is explicitly intended.
- Keep CLI compatibility stable (argument names and expected output formats are part of downstream workflows).
- Preserve reproducibility-oriented checks and verification paths; do not remove result checks without clear replacement.
- Align with existing coding style and package organization; avoid introducing new frameworks/build systems.
- Never force push, including with `--force` or `--force-with-lease`; preserve remote history and coordinate any branch rewrite explicitly outside normal contribution flow.

## Linting/Static Checks Reality
- No dedicated Checkstyle/Spotless config is wired in current root build.
- Compiler is configured with `-Xlint:unchecked`; keep builds warning-clean where practical.
- Coverage is collected with JaCoCo and uploaded in CI.

## Practical Workflow for Changes
1. Confirm Java 21, Maven 3.9+, and submodule state; use `$install-anserini-dev-env` for detailed setup checks.
2. Implement focused code/doc/template updates.
3. Run targeted Maven tests for impacted classes/packages.
4. Run broader `mvn test`/`mvn package` if change scope warrants.
5. For retrieval/indexing logic changes that affect reported results, use `$anserini-reproduction` to select the relevant reproduction workflow.

## GitHub Review Workflow
- When addressing a pull request review comment, update the code or docs, push the fix, and click "Resolve conversation" on the addressed review thread.

## High-Risk Areas (Handle Carefully)
- Index format/index stats assumptions (can break verification).
- Search defaults and scoring params (can shift published metrics).
- Topic reader/qrels wiring and run output formats (can break eval pipeline).
- Prebuilt index metadata/resources used by fatjar flows.
