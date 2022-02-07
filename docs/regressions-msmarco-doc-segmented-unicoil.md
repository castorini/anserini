# Anserini: Regressions for uniCOIL on [MS MARCO Document](https://github.com/microsoft/MSMARCO-Document-Ranking)

This page documents regression experiments for uniCOIL on the MS MARCO Document Ranking Task, which is integrated into Anserini's regression testing framework.
The uniCOIL model is described in the following paper:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

The paper does not actually report on the experiments here; these were created after the paper was posted on arXiv.
However, the model is the same, applied to the MS MARCO _segmented_ document corpus (with doc2query-T5 expansions).
Retrieval uses MaxP technique, where we select the score of the highest-scoring passage from a document as the score for that document to produce a document ranking.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-doc-segmented-unicoil.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-doc-segmented-unicoil.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-doc-segmented-unicoil \
  -index indexes/lucene-index.msmarco-doc-segmented-unicoil/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -impact -pretokenized \
  >& logs/log.msmarco-doc-segmented-unicoil &
```

The directory `/path/to/msmarco-doc-segmented-unicoil/` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](experiments-msmarco-unicoil.md) for additional details.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-segmented-unicoil/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.unicoil.tsv.gz -topicreader TsvInt \
  -output runs/run.msmarco-doc-segmented-unicoil.unicoil.topics.msmarco-doc.dev.unicoil.tsv.gz \
  -impact -pretokenized -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.100 -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-segmented-unicoil.unicoil.topics.msmarco-doc.dev.unicoil.tsv.gz
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | uniCOIL w/ doc2query-T5 expansion|
:---------------------------------------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.3535    |


R@100                                   | uniCOIL w/ doc2query-T5 expansion|
:---------------------------------------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.8858    |


R@1000                                  | uniCOIL w/ doc2query-T5 expansion|
:---------------------------------------|-----------|
[MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)| 0.9546    |
