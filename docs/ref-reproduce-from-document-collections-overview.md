# ⚗️ Anserini: Reproductions from Document Collections: Pipeline

Reproduction experiments in Anserini are coordinated by a rigorous end-to-end framework implemented in [`io.anserini.reproduce.ReproduceFromDocumentCollection`](../src/main/java/io/anserini/reproduce/ReproduceFromDocumentCollection.java).
The driver automatically runs experiments based on configuration files stored in [`src/main/resources/reproduce/from-document-collection/configs`](../src/main/resources/reproduce/from-document-collection/configs), performing the following actions:

+ Build the index from scratch (i.e., from the raw document collection).
+ Verify index statistics (sanity check that the index has been built properly).
+ Perform retrieval runs with different settings.
+ Evaluate the runs and verify effectiveness results.

Furthermore, documentation pages are auto-generated based on [raw templates](../src/main/resources/reproduce/from-document-collection/docgen).

## Invocations

All invocations of reproductions from raw document collections have the following form:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config cacm
```

The `--config` option specifies the experiment to run, corresponding to the YAML configuration file in [`src/main/resources/reproduce/from-document-collection/configs`](../src/main/resources/reproduce/from-document-collection/configs).

The three main phases of the driver program are:

+ `--index`: build the index.
+ `--verify`: verify index statistics.
+ `--search`: perform retrieval runs and verify effectiveness.

Although the driver is hard-coded to run on Waterloo machines (e.g., hard-coded paths to document collections), the document collection path can be manually specified from the command line with the `--corpus-path` option, for example:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config msmarco-v1-passage --corpus-path /path/to/corpus
```

For example, `--corpus-path collections/msmarco-passage/collection_jsonl`.

For the complete list of configurations (i.e., `--config` settings), see [this catalog](./ref-reproduce-from-document-collections-catalog.md).

## Continuous Reproductions

Internally at Waterloo, we have two machines (`tuna.cs.uwaterloo.ca` and `orca.cs.uwaterloo.ca`) for the development of Anserini.
They are both set up to run these reproductions, and we are continuously running these reproductions to ensure that new commits do not break any existing features.
We keep a [change log](./ref-reproduce-from-document-collections-log.md) to document substantive changes.
