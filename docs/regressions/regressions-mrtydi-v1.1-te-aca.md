# Anserini Regressions: Mr. TyDi (v1.1) &mdash; Telugu

**Models**: bag-of-words approaches using `AutoCompositeAnalyzer`

This page documents BM25 regression experiments for [Mr. TyDi (v1.1) &mdash; Telugu](https://github.com/castorini/mr.tydi) using `AutoCompositeAnalyzer`.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/mrtydi-v1.1-te-aca.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/mrtydi-v1.1-te-aca.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression mrtydi-v1.1-te-aca
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 1 \
  -collection MrTyDiCollection \
  -input /path/to/mrtydi-v1.1-te \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.mrtydi-v1.1-telugu-aca/ \
  -storePositions -storeDocvectors -storeRaw -language te -useAutoCompositeAnalyzer \
  >& logs/log.mrtydi-v1.1-te &
```

See [this page](https://github.com/castorini/mr.tydi) for more details about the Mr. TyDi corpus.
For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-telugu-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-te.train.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-te.bm25.topics.mrtydi-v1.1-te.train.txt \
  -bm25 -hits 100 -language te -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-telugu-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-te.dev.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-te.bm25.topics.mrtydi-v1.1-te.dev.txt \
  -bm25 -hits 100 -language te -useAutoCompositeAnalyzer &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.mrtydi-v1.1-telugu-aca/ \
  -topics tools/topics-and-qrels/topics.mrtydi-v1.1-te.test.txt.gz \
  -topicReader TsvInt \
  -output runs/run.mrtydi-v1.1-te.bm25.topics.mrtydi-v1.1-te.test.txt \
  -bm25 -hits 100 -language te -useAutoCompositeAnalyzer &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-te.train.txt runs/run.mrtydi-v1.1-te.bm25.topics.mrtydi-v1.1-te.train.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-te.dev.txt runs/run.mrtydi-v1.1-te.bm25.topics.mrtydi-v1.1-te.dev.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m recall.100 tools/topics-and-qrels/qrels.mrtydi-v1.1-te.test.txt runs/run.mrtydi-v1.1-te.bm25.topics.mrtydi-v1.1-te.test.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MRR@100**                                                                                                  | **BM25**  |
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [Mr. TyDi (Telugu): train](https://github.com/castorini/mr.tydi)                                             | 0.4063    |
| [Mr. TyDi (Telugu): dev](https://github.com/castorini/mr.tydi)                                               | 0.4131    |
| [Mr. TyDi (Telugu): test](https://github.com/castorini/mr.tydi)                                              | 0.5096    |
| **R@100**                                                                                                    | **BM25**  |
| [Mr. TyDi (Telugu): train](https://github.com/castorini/mr.tydi)                                             | 0.8379    |
| [Mr. TyDi (Telugu): dev](https://github.com/castorini/mr.tydi)                                               | 0.8332    |
| [Mr. TyDi (Telugu): test](https://github.com/castorini/mr.tydi)                                              | 0.9110    |
