# Anserini: Regressions for [DL20 (Doc)](https://trec.nist.gov/data/deep2020.html) Segmented w/ uniCOIL

This page describes experiments, integrated into Anserini's regression testing framework, for the TREC 2020 Deep Learning Track (Document Ranking Task) on the MS MARCO document collection using relevance judgments from NIST.
These runs use the uniCOIL model described in the following paper:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

The experiments on this page are not actually reported in the paper.
However, the model is the same, applied to the MS MARCO _segmented_ document corpus (with doc2query-T5 expansions).
Retrieval uses MaxP technique, where we select the score of the highest-scoring passage from a document as the score for that document to produce a document ranking.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/dl20-doc-segmented-unicoil.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/dl20-doc-segmented-unicoil.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

## Corpus

We make available a version of the MS MARCO passage corpus that has already been processed with uniCOIL, i.e., gone through document expansion and term reweighting.
Thus, no neural inference is involved.
For details on how to train uniCOIL and perform inference, please see [this guide](https://github.com/luyug/COIL/tree/main/uniCOIL).

Download the corpus and unpack into `collections/`:

```
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-doc-segmented-unicoil.tar -P collections/

tar xvf collections/msmarco-doc-segmented-unicoil.tar -C collections/
```

To confirm, `msmarco-doc-segmented-unicoil.tar` is 18 GB and has MD5 checksum `6a00e2c0c375cb1e52c83ae5ac377ebb`.

## Indexing

Sample indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-doc-segmented-unicoil \
  -index indexes/lucene-index.msmarco-doc-segmented-unicoil/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -impact -pretokenized \
  >& logs/log.msmarco-doc-segmented-unicoil &
```

The directory `/path/to/msmarco-doc-segmented-unicoil/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the uniCOIL tokens.
Upon completion, we should have an index with 20,545,677 documents.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 45 topics for which NIST has provided judgments as part of the TREC 2020 Deep Learning Track.
The original data can be found [here](https://trec.nist.gov/data/deep2020.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-segmented-unicoil/ \
  -topics src/main/resources/topics-and-qrels/topics.dl20.unicoil.0shot.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-doc-segmented-unicoil.unicoil.topics.dl20.unicoil.0shot.txt \
  -impact -pretokenized -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recall.100 -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.dl20-doc.txt runs/run.msmarco-doc-segmented-unicoil.unicoil.topics.dl20.unicoil.0shot.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| MAP                                                                                                          | uniCOIL w/ doc2query-T5 expansion|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.4126    |


| R@100                                                                                                        | uniCOIL w/ doc2query-T5 expansion|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.6210    |


| nDCG@10                                                                                                      | uniCOIL w/ doc2query-T5 expansion|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DL20 (Doc)](https://trec.nist.gov/data/deep2019.html)                                                       | 0.6033    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/dl20-doc-segmented-unicoil.template) and run `bin/build.sh` to rebuild the documentation.
