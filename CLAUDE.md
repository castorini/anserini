# CLAUDE.md — Anserini Development Guide

This document describes the architecture, conventions, and best practices for working with the Anserini codebase.

## Project Overview

Anserini is a Java toolkit for reproducible information retrieval (IR) research built on [Apache Lucene](https://lucene.apache.org/). It supports BM25 (sparse), learned sparse (e.g., SPLADE), and dense vector retrieval. The project is maintained by the [Castorini group](https://github.com/castorini) at the University of Waterloo.

## Build and Run

**Requirements:** Java 21 (exactly), Maven 3.3+

```bash
# Build (creates both thin JAR and fatjar)
mvn clean package

# Build with suppressed SLF4J verbosity (preferred)
bash bin/build.sh

# Run any main class via the fatjar (allocates up to 192G heap)
bash bin/run.sh io.anserini.search.SearchCollection [args...]
```

The fatjar is produced by `maven-shade-plugin` with classifier `-fatjar`. It bundles all dependencies and uses `ServicesResourceTransformer` for Lucene codec discovery.

## Running Tests

```bash
mvn test                         # Run all tests
mvn test -Dtest=ClassName        # Run a specific test class
mvn test -Dtest=ClassName#method # Run a specific test method
```

Tests use JUnit 4 with the Lucene test framework (`lucene-test-framework`). The Maven Surefire plugin is configured with 8 GB heap for test execution.

## Repository Layout

```
src/main/java/io/anserini/
├── analysis/       # Text analyzers, tokenizers, and filters
├── collection/     # Document collection abstractions (50+ collection types)
├── encoder/        # Dense and sparse vector encoders (ONNX-based)
├── eval/           # Evaluation utilities
├── fusion/         # Result fusion
├── index/          # Indexing pipeline and index utilities
│   └── generator/  # Document-to-Lucene-Document generators
├── rerank/         # Reranking framework and implementations
├── reproduce/      # Reproducibility tooling
├── search/         # Search pipeline, query generators, topic readers
│   ├── query/      # Query generation strategies
│   ├── similarity/ # Custom Lucene similarity implementations
│   └── topicreader/# Topic/query file readers
└── util/           # Miscellaneous utilities

src/test/java/io/anserini/
├── collection/     # Unit tests for collections
├── encoder/        # Encoder tests
├── index/          # Indexer and index utility tests
├── integration/    # End-to-end integration tests
├── search/         # Search and topic reader tests
└── ...

src/main/resources/
├── regression/     # Regression test definitions (YAML)
├── reproduce/      # Reproduction scripts
├── prebuilt-indexes/ # Prebuilt index metadata
└── ...

docs/               # 90+ markdown files documenting experiments and regressions
```

## Architecture

### Core Pipelines

**Indexing pipeline:**
```
DocumentCollection → FileSegment(s) → SourceDocument(s)
    → LuceneDocumentGenerator → Lucene Document → IndexWriter
```

**Search pipeline:**
```
TopicReader (reads queries) → QueryGenerator (builds Lucene Query)
    → IndexSearcher → ScoredDocs → RerankerCascade → RunOutputWriter (TREC format)
```

### Key Abstractions

| Abstraction | Location | Purpose |
|---|---|---|
| `DocumentCollection<T>` | `collection/` | Discovers files and iterates `FileSegment`s |
| `FileSegment<T>` | `collection/` | Reads a file into `SourceDocument`s |
| `SourceDocument` | `collection/` | Interface: `id()`, `contents()`, `raw()`, `indexable()` |
| `LuceneDocumentGenerator` | `index/generator/` | Converts `SourceDocument` → Lucene `Document` |
| `AbstractIndexer` | `index/` | Base class for all indexers; manages threading |
| `TopicReader` | `search/topicreader/` | Reads query topics from files |
| `QueryGenerator` | `search/query/` | Converts topic text to Lucene `Query` |
| `Reranker<T>` | `rerank/` | Interface for result reranking |
| `RerankerCascade` | `rerank/` | Chains multiple rerankers |

### Lucene Field Constants

Defined in `io.anserini.index.Constants`:

```java
Constants.ID       = "id"       // Document ID field
Constants.CONTENTS = "contents" // Default searchable field
Constants.RAW      = "raw"      // Stored raw document text
Constants.ENTITY   = "entity"   // Entity field
Constants.VECTOR   = "vector"   // Dense vector field
```

### Entry Points

| Task | Main Class |
|---|---|
| Indexing | `io.anserini.index.IndexCollection` |
| Sparse/BM25 search | `io.anserini.search.SearchCollection` |
| Dense HNSW search | `io.anserini.search.SearchHnswDenseVectors` |
| Dense flat search | `io.anserini.search.SearchFlatDenseVectors` |
| Inverted dense search | `io.anserini.search.SearchInvertedDenseVectors` |

## Code Conventions

### CLI Argument Pattern

All command-line tools use **args4j** with a nested static `Args` class:

```java
public class MyTool {
  public static class Args {
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Index path.")
    public String index;

    @Option(name = "-threads", metaVar = "[num]", usage = "Number of threads.")
    public int threads = 4;
  }

  public static void main(String[] args) throws Exception {
    Args myArgs = new Args();
    CmdLineParser parser = new CmdLineParser(myArgs, ParserProperties.defaults().withUsageWidth(120));
    parser.parseArgument(args);
    // ...
  }
}
```

When extending existing tools, add new `@Option` fields to the appropriate `Args` class. Inherit from parent `Args` classes (e.g., `AbstractIndexer.Args`, `BaseSearchArgs`) when applicable.

### Naming

- **Packages:** lowercase `io.anserini.<module>`
- **Classes:** PascalCase — `IndexCollection`, `SimpleSearcher`, `BM25Similarity`
- **Methods:** camelCase — `createDocument()`, `getCollectionPath()`
- **Constants:** UPPER_SNAKE_CASE — `Constants.ID`, `Constants.CONTENTS`
- **Public POJO fields:** camelCase — `ScoredDoc.docid`, `ScoredDoc.score`

### File Headers

Every Java source file must include the Apache 2.0 license header:

```java
/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```

### Logging

Use Log4j 2 via `LogManager`:

```java
private static final Logger LOG = LogManager.getLogger(MyClass.class);
```

Use standard levels: `LOG.info()`, `LOG.debug()`, `LOG.warn()`, `LOG.error()`. The `LoggingBootstrap.installJulToSlf4jBridge()` utility bridges java.util.logging to SLF4J.

### Imports

Use explicit imports (no wildcards). Organize imports in standard groups: `io.anserini.*`, then third-party, then `java.*`.

### Generics and Type Safety

The codebase uses bounded generics extensively:

```java
public abstract class DocumentCollection<T extends SourceDocument> implements Iterable<FileSegment<T>> { ... }
```

Maintain type safety when extending collection or generator abstractions.

### Threading

`AbstractIndexer` uses `ThreadPoolExecutor` for parallel segment processing. Each thread creates its own `LuceneDocumentGenerator` instance for thread safety. Use `AtomicInteger` for shared counters.

### Reflection-Based Plugin Loading

Generators, collections, and encoders are loaded by class name at runtime:

```java
generatorClass.getDeclaredConstructor(Args.class).newInstance(args)
```

When adding a new collection or generator, ensure the class is discoverable by the reflection-based loading mechanism.

## Adding a New Collection

Follow the steps documented in `DocumentCollection.java`:

1. Create a subclass of `DocumentCollection<T>`.
2. Implement `FileSegment<T>` as an inner class (by convention named `Segment`). See `TrecCollection.Segment`.
3. Create a `SourceDocument` implementation (by convention named `Document`). See `TrecCollection.Document`.
4. Optionally create a custom `LuceneDocumentGenerator` if the default generator is insufficient.
5. Add unit tests in `src/test/java/io/anserini/collection/`.

## Testing Practices

### Unit Tests

- Located in `src/test/java/io/anserini/` mirroring the main source tree.
- Extend `SuppressedLoggingLuceneTestCase` or `StdOutStdErrRedirectableLuceneTestCase` (which extend Lucene's `LuceneTestCase`).
- Use `@Before`/`@After` for setup and teardown.

### Integration / End-to-End Tests

- Located in `src/test/java/io/anserini/integration/`.
- Extend the abstract `EndToEndTest` class.
- Pattern: index a small test collection → run search → compare output against reference.
- Annotated with `@TestRuleLimitSysouts.Limit(bytes = 20000)` to cap test output.
- Define reference data: `referenceRunOutput`, `referenceDocs`, `referenceDocTokens`.
- Verify index integrity: field counts, term counts, total frequencies, positions.

### Test Utilities

- `TestUtils.checkFile()` — verifies a file exists and is non-empty.
- `TestUtils.checkRunFileApproximate()` — approximate comparison of TREC run files.
- Use `ByteArrayOutputStream` to capture and verify stdout/stderr.

## Dependency Highlights

| Dependency | Version | Purpose |
|---|---|---|
| Apache Lucene | 9.9.1 | Core IR engine (indexing, search, analysis) |
| Log4j 2 | 2.25.3 | Logging |
| Jackson | 2.20.0 | JSON/YAML parsing |
| ONNX Runtime | 1.17.0 | Neural model inference |
| DJL HuggingFace | 0.34.0 | Tokenizer integration |
| args4j | — | CLI argument parsing |
| JUnit 4 | 4.13.2 | Testing |

## CI/CD

GitHub Actions (`.github/workflows/maven.yml`):
- Triggers on push/PR to `master`.
- Runs on `ubuntu-22.04` with JDK 21 (Temurin).
- Executes `mvn -B package`.
- Uploads code coverage to Codecov.

## Custom Exception Types

| Exception | When Thrown |
|---|---|
| `EmptyDocumentException` | Document has no indexable content |
| `SkippedDocumentException` | Document should be intentionally skipped |
| `InvalidDocumentException` | Document is malformed or invalid |
| `NotStoredException` | Attempting to access a field that was not stored |
| `GeneratorException` | General generator failure |

## Regression and Reproducibility

- Regression definitions live in `src/main/resources/regression/` as YAML files.
- Reproduction scripts are in `src/main/resources/reproduce/`.
- Prebuilt index metadata is in `src/main/resources/prebuilt-indexes/`.
- The `docs/` directory contains 90+ markdown files documenting experiments.
- Every new retrieval experiment should have corresponding regression documentation.

## Common Pitfalls

- **Java version:** Anserini requires exactly Java 21. Other versions may compile but produce incorrect results or fail at runtime.
- **Fatjar codec discovery:** The shade plugin uses `ServicesResourceTransformer` to merge Lucene `META-INF/services` files. If you add a new Lucene codec, ensure it is picked up.
- **Thread safety:** Generators are instantiated per-thread. Do not share mutable state across generator instances without synchronization.
- **Field storage:** By default, fields are not stored. Pass `-storeRaw`, `-storeContents`, `-storePositions`, or `-storeDocvectors` explicitly when indexing.
- **Analyzer selection:** The analyzer cascade (HuggingFace tokenizer → language-specific → pretokenized → default English) depends on CLI flags. Verify the correct analyzer is being used when debugging search quality issues.
