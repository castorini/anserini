# Repo Project Instructions

## Scope and Stack
- This repository is **Anserini** (`io.anserini:anserini`), a Java toolkit for reproducible IR research built on Apache Lucene.
- Primary language: Java (`src/main/java`).
- Secondary tooling/scripts: Python (`src/main/python`) for regression orchestration and analysis.
- Build system: Maven (`pom.xml`), artifact includes both thin jar and shaded `-fatjar`.
- Required runtime/build versions: **Java 21** and Maven **3.9+**.

## Repository Layout
- `src/main/java`: core indexing/search/eval/reranking implementations.
- `src/test/java`: JUnit/Lucene test framework tests, including end-to-end integration tests.
- `src/main/resources/regression`: YAML definitions that drive regression runs.
- `src/main/resources/docgen/templates`: source templates for auto-generated regression docs.
- `docs/regressions`: generated regression pages; edit templates, not generated pages.
- `src/main/python/run_regression.py`: end-to-end regression driver.
- `bin/`: convenience scripts (`build.sh`, `qbuild.sh`, `run.sh`, etc.).
- `tools/`: git submodule (`anserini-tools`) containing eval scripts/assets.

## Build, Test, and Run
- Full build (default local and CI path):
  - `mvn clean package`
- Fast local build (skip tests/javadocs):
  - `bin/qbuild.sh`
- Run a main class from fatjar:
  - `bin/run.sh io.anserini.search.SearchCollection [args...]`
- CI workflow (`.github/workflows/maven.yml`) runs:
  - `mvn -B package --file pom.xml`

## Testing Expectations
- Unit/integration tests run via Maven Surefire; do not assume changes are safe without at least targeted tests.
- For Java changes, run targeted tests first, then broaden as needed:
  - `mvn -Dtest=ClassName test`
  - `mvn test`
- End-to-end experimental verification uses regression machinery, not only unit tests:
  - `python src/main/python/run_regression.py --index --verify --search --regression <name>`
- Reproducibility is a core project contract; any change affecting metrics/indexing/search defaults should be validated against relevant regressions.

## Submodules and External Tools
- `tools/` is a required git submodule. After clone:
  - `git submodule update --init --recursive`
- If evaluation binaries are needed locally, build as documented in README (`tools/eval` and `tools/eval/ndeval`).

## Editing and Contribution Guardrails
- Prefer minimal, behavior-preserving changes unless behavior change is explicitly intended.
- Keep CLI compatibility stable (argument names and expected output formats are part of downstream workflows).
- When touching regression docs, edit `src/main/resources/docgen/templates` and regenerate; avoid hand-editing generated docs in `docs/regressions`.
- Preserve reproducibility-oriented checks and verification paths; do not remove result checks without clear replacement.
- Align with existing coding style and package organization; avoid introducing new frameworks/build systems.

## Linting/Static Checks Reality
- No dedicated Checkstyle/Spotless config is wired in current root build.
- Compiler is configured with `-Xlint:unchecked`; keep builds warning-clean where practical.
- Coverage is collected with JaCoCo and uploaded in CI.

## Practical Workflow for Changes
1. Confirm Java 21 + submodule state.
2. Implement focused code/doc/template updates.
3. Run targeted Maven tests for impacted classes/packages.
4. Run broader `mvn test`/`mvn package` if change scope warrants.
5. For retrieval/indexing logic changes, run relevant `run_regression.py` configs.
6. If regression-facing docs change, regenerate from templates before finalizing.

## High-Risk Areas (Handle Carefully)
- Index format/index stats assumptions (can break verification).
- Search defaults and scoring params (can shift published metrics).
- Topic reader/qrels wiring and run output formats (can break eval pipeline).
- Prebuilt index metadata/resources used by fatjar flows.
