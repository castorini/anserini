# Anserini Fatjar Download for Users

## What about retrieval with (learned) _dense_ vector representations?

Want to do dense vector search?
Anserini's got you covered.
For example, same as above (MS MARCO passage, dev queries) using the BGE model (en, v1.5):

```bash
java -cp anserini-2.0.0-fatjar.jar io.anserini.search.SearchHnswDenseVectors \
  -index msmarco-v1-passage.bge-base-en-v1.5.hnsw \
  -topics msmarco-v1-passage.dev \
  -encoder BgeBaseEn15  \
  -output run.msmarco-v1-passage.dev.bge.txt \
  -efSearch 1000
```

To evaluate:

```bash
java -cp anserini-2.0.0-fatjar.jar trec_eval -c -M 10 -m recip_rank msmarco-v1-passage.dev \
  run.msmarco-v1-passage.dev.bge.txt
```

You should get an MRR (`recip_rank`) of 0.3575.

## What about retrieval with (learned) _sparse_ vector representations?

Want to do retrieval with (learned) sparse vector representations?
Anserini's also got you covered.
For example, same as above (MS MARCO passage, dev queries) using SPLADE-v3:

```bash
java -cp anserini-2.0.0-fatjar.jar io.anserini.search.SearchCollection \
  -index msmarco-v1-passage.splade-v3 \
  -topics msmarco-v1-passage.dev \
  -encoder SpladeV3 \
  -output run.msmarco-v1-passage.dev.splade-v3.txt \
  -impact -pretokenized
```

To evaluate:

```bash
java -cp anserini-2.0.0-fatjar.jar trec_eval -c -M 10 -m recip_rank msmarco-v1-passage.dev \
  run.msmarco-v1-passage.dev.splade-v3.txt
```

You should get an MRR (`recip_rank`) of 0.4000.

## Older instructions

+ [Anserini v1.7.1](fatjar-regressions/fatjar-regressions-v1.7.1.md)
+ [Anserini v1.7.0](fatjar-regressions/fatjar-regressions-v1.7.0.md)
+ [Anserini v1.6.0](fatjar-regressions/fatjar-regressions-v1.6.0.md)
+ [Anserini v1.5.0](fatjar-regressions/fatjar-regressions-v1.5.0.md)
+ [Anserini v1.4.0](fatjar-regressions/fatjar-regressions-v1.4.0.md)
+ [Anserini v1.3.0](fatjar-regressions/fatjar-regressions-v1.3.0.md)
+ [Anserini v1.2.2](fatjar-regressions/fatjar-regressions-v1.2.2.md)
+ [Anserini v1.2.1](fatjar-regressions/fatjar-regressions-v1.2.1.md)
+ [Anserini v1.2.0](fatjar-regressions/fatjar-regressions-v1.2.0.md)
+ [Anserini v1.1.1](fatjar-regressions/fatjar-regressions-v1.1.1.md)
+ [Anserini v1.1.0](fatjar-regressions/fatjar-regressions-v1.1.0.md)
+ [Anserini v1.0.0](fatjar-regressions/fatjar-regressions-v1.0.0.md)
+ [Anserini v0.39.0](fatjar-regressions/fatjar-regressions-v0.39.0.md)
+ [Anserini v0.38.0](fatjar-regressions/fatjar-regressions-v0.38.0.md)
+ [Anserini v0.37.0](fatjar-regressions/fatjar-regressions-v0.37.0.md)
+ [Anserini v0.36.1](fatjar-regressions/fatjar-regressions-v0.36.1.md)
+ [Anserini v0.36.0](fatjar-regressions/fatjar-regressions-v0.36.0.md)
+ [Anserini v0.35.1](fatjar-regressions/fatjar-regressions-v0.35.1.md)
+ [Anserini v0.35.0](fatjar-regressions/fatjar-regressions-v0.35.0.md)
