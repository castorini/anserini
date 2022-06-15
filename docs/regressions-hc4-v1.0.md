# Anserini Regressions: HC4 (v1.0)

This page documents BM25 regression experiments for [HC4](https://github.com/hltcoe/HC4).

## Indexing

Typical indexing command:

- Russian
    ```
    target/appassembler/bin/IndexCollection \
      -collection NeuClirCollection \
      -input /path/to/hc4-v1.0-rus \
      -index indexes/lucene-index.hc4-v1.0-russian/ \
      -generator DefaultLuceneDocumentGenerator \
      -threads 1 -storePositions -storeDocvectors -storeRaw -language ru \
      >& logs/log.hc4-v1.0-ru &
    ```
- Persian
    ```
    target/appassembler/bin/IndexCollection \
      -collection NeuClirCollection \
      -input /path/to/hc4-v1.0-fas \
      -index indexes/lucene-index.hc4-v1.0-persian/ \
      -generator DefaultLuceneDocumentGenerator \
      -threads 1 -storePositions -storeDocvectors -storeRaw -language fa \
      >& logs/log.hc4-v1.0-ru &
    ```

- Chinese
    ```
    target/appassembler/bin/IndexCollection \
      -collection NeuClirCollection \
      -input /path/to/hc4-v1.0-zho \
      -index indexes/lucene-index.hc4-v1.0-chinese/ \
      -generator DefaultLuceneDocumentGenerator \
      -threads 1 -storePositions -storeDocvectors -storeRaw -language zh \
      >& logs/log.hc4-v1.0-zh &
    ```

See [this page](https://github.com/hltcoe/HC4) for more details about the HC4 corpus.
For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval using human translation queries provided by HC4 as follows:

- Russian
    ```
    target/appassembler/bin/SearchCollection \
      -index indexes/lucene-index.hc4-v1.0-russian/ \
      -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-ru.dev.title.tsv.gz \
      -topicreader TsvInt \
      -output runs/run.hc4-v1.0-ru.bm25.topics.dev.title.txt \
      -bm25 -hits 100 -language ru &
    ```
- Persian
    ```
    target/appassembler/bin/SearchCollection \
      -index indexes/lucene-index.hc4-v1.0-persian/ \
      -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-fa.dev.title.tsv.gz \
      -topicreader TsvInt \
      -output runs/run.hc4-v1.0-fa.bm25.topics.dev.title.txt \
      -bm25 -hits 100 -language fa &
    ```
- Chinese
  ```
    target/appassembler/bin/SearchCollection \
      -index indexes/lucene-index.hc4-v1.0-chinese/ \
      -topics src/main/resources/topics-and-qrels/topics.hc4-v1.0-zh.dev.title.tsv.gz \
      -topicreader TsvInt \
      -output runs/run.hc4-v1.0-zh.bm25.topics.dev.title.txt \
      -bm25 -hits 100 -language zh &
  ```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-ru.dev.txt runs/run.hc4-v1.0-ru.bm25.topics.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-fa.dev.txt runs/run.hc4-v1.0-fa.bm25.topics.dev.title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map src/main/resources/topics-and-qrels/qrels.hc4-v1.0-zh.dev.txt runs/run.hc4-v1.0-zh.bm25.topics.dev.title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following dev scores:

| Russian                                                                                  | BM25(MAP)    |
|:-----------------------------------------------------------------------------------------|-----------|
| [Topic Title](https://github.com/hltcoe/HC4)                                             | 0.2767    |



| Persian                                                                                  | BM25(MAP) |
|:-----------------------------------------------------------------------------------------|-----------|
| [Topic Title](https://github.com/hltcoe/HC4)                                             | 0.2919    |


| Chinese                                                                                  | BM25(MAP) |
|:-----------------------------------------------------------------------------------------|-----------|
| [Topic Title](https://github.com/hltcoe/HC4)                                             | 0.2914    |


