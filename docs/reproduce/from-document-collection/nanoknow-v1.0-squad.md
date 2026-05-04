# Anserini Regressions: NanoKnow v1.0 &mdash; SQuAD

**Models**: BM25

This page documents BM25 retrieval over the NanoKnow v1.0 corpus ([karpathy/fineweb-edu-100b-shuffle](https://huggingface.co/datasets/karpathy/fineweb-edu-100b-shuffle), ~97M documents) with SQuAD validation queries, integrated into Anserini's regression testing framework.
The corpus and qrels come from [NanoKnow](https://github.com/castorini/NanoKnow), a project that uses BM25 over this corpus to study the parametric knowledge of nanochat language models.
Future versions (v2, v3, ...) of the NanoKnow regression are expected to project these query sets onto different corpora (e.g., ClimbMix), keeping the task format fixed and varying the index.

The exact configurations for these regressions are stored in [this YAML file](../../../src/main/resources/reproduce/from-document-collection/configs/nanoknow-v1.0-squad.yaml).
Note that this page is automatically generated from [this template](../../../src/main/resources/reproduce/from-document-collection/docgen/nanoknow-v1.0-squad.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
bin/run.sh io.anserini.reproduce.ReproduceFromDocumentCollection --index --verify --search --config nanoknow-v1.0-squad
```

> :warning: Building the index from scratch reads ~325 GB of Parquet data and takes many hours; the resulting index is also ~325 GB.
> If the index already exists at `indexes/fineweb-edu-100b-official-index/`, omit `--index` and just run `--verify --search`.
> A pre-built index is also available for download from [LingweiGu/NanoKnow-Fineweb-Edu-Index](https://huggingface.co/datasets/LingweiGu/NanoKnow-Fineweb-Edu-Index).

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 32 \
  -collection FineWebCollection \
  -input /path/to/fineweb-edu-100b-karpathy \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/fineweb-edu-100b-official-index/ \
  -storeRaw \
  >& logs/log.fineweb-edu-100b-karpathy &
```

The directory `/path/to/fineweb-edu-100b-karpathy/` should contain the Parquet shards from the [karpathy/fineweb-edu-100b-shuffle](https://huggingface.co/datasets/karpathy/fineweb-edu-100b-shuffle) dataset (~97M documents, ~100B tokens).

For additional details, see explanation of [common indexing options](../../common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule:

+ [`topics.nanoknow-v1.0-squad.supported.tsv`](https://github.com/castorini/anserini-tools/blob/master/topics-and-qrels/topics.nanoknow-v1.0-squad.supported.tsv): 7,490 SQuAD validation questions in `qid\tquery` TSV format.
+ [`qrels.nanoknow-v1.0-squad.supported.txt`](https://github.com/castorini/anserini-tools/blob/master/topics-and-qrels/qrels.nanoknow-v1.0-squad.supported.txt): NanoKnow v1.0 supported-document qrels in TREC format.

The qrels are derived from the [NanoKnow](https://github.com/castorini/NanoKnow) pipeline: for each question, BM25 over this index returns the top-100 documents; documents that contain a normalized form of any official answer string are passed to a Qwen3-8B verifier; documents that the verifier judges as actually answering the question are recorded as relevant.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/fineweb-edu-100b-official-index/ \
  -topics tools/topics-and-qrels/topics.nanoknow-v1.0-squad.supported.tsv \
  -topicReader TsvInt \
  -output runs/run.fineweb-edu-100b-karpathy.bm25.topics.nanoknow-v1.0-squad.supported.txt \
  -bm25 &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m recall.20 tools/topics-and-qrels/qrels.nanoknow-v1.0-squad.supported.txt runs/run.fineweb-edu-100b-karpathy.bm25.topics.nanoknow-v1.0-squad.supported.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **R@20**                                                                                                     | **BM25 (default)**|
|:-------------------------------------------------------------------------------------------------------------|-------------------|
| [NanoKnow v1.0: SQuAD Validation](https://github.com/castorini/NanoKnow)                                     | 0.3106            |

The reported metric is **R@20** (Recall at 20). Because NanoKnow v1.0 qrels are themselves drawn from the BM25 top-100 over this exact index, R@100 is ~1.0 by construction and is not an interesting signal. R@20 instead measures what fraction of the verified answer-bearing documents BM25 ranks in the top-20; this number is sensitive to changes in the BM25 implementation, the index, or the topics, and so serves as a meaningful regression signal for the retrieval pipeline.

This is intended to be a fully reproducible release regression: with the same Anserini code, the same `indexes/fineweb-edu-100b-official-index/` index, and the same NanoKnow v1.0 topics and qrels from `anserini-tools`, the reported R@20 value should remain unchanged across repeated runs.
