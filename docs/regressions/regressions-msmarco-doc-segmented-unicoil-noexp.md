# Anserini Regressions: MS MARCO Document Ranking

**Model**: uniCOIL (without any expansions) on segmented documents (title/segment encoding)

This page describes regression experiments, integrated into Anserini's regression testing framework, using uniCOIL (without any expansions) on the [MS MARCO document ranking task](https://github.com/microsoft/MSMARCO-Document-Ranking).
The uniCOIL model is described in the following paper:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

The experiments on this page are not actually reported in the paper.
However, the model is the same, applied to the MS MARCO _segmented_ document corpus (without any expansions).
Retrieval uses MaxP technique, where we select the score of the highest-scoring passage from a document as the score for that document to produce a document ranking.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-doc-segmented-unicoil-noexp.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-doc-segmented-unicoil-noexp.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and then run `bin/build.sh` to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-segmented-unicoil-noexp
```

We make available a version of the MS MARCO document corpus that has already been processed with uniCOIL, i.e., we have performed model inference on every document and stored the output sparse vectors.
Thus, no neural inference is involved.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression msmarco-doc-segmented-unicoil-noexp
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-doc-segmented-unicoil-noexp.tar -P collections/
tar xvf collections/msmarco-doc-segmented-unicoil-noexp.tar -C collections/
```

To confirm, `msmarco-doc-segmented-unicoil-noexp.tar` is 11 GB and has MD5 checksum `11b226e1cacd9c8ae0a660fd14cdd710`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-doc-segmented-unicoil-noexp \
  --corpus-path collections/msmarco-doc-segmented-unicoil-noexp
```

## Indexing

Sample indexing command:

```bash
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-doc-segmented-unicoil-noexp \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.msmarco-doc-segmented-unicoil-noexp/ \
  -threads 16 -impact -pretokenized -storeDocvectors \
  >& logs/log.msmarco-doc-segmented-unicoil-noexp &
```

The directory `/path/to/msmarco-doc-segmented-unicoil-noexp/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the uniCOIL tokens.
Upon completion, we should have an index with 20,545,677 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 5193 dev set questions.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-doc-segmented-unicoil-noexp/ \
  -topics tools/topics-and-qrels/topics.msmarco-doc.dev.unicoil-noexp.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-doc-segmented-unicoil-noexp.unicoil-noexp-cached_q.topics.msmarco-doc.dev.unicoil-noexp.txt \
  -impact -pretokenized -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 &
```

Evaluation can be performed using `trec_eval`:

```bash
target/appassembler/bin/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-segmented-unicoil-noexp.unicoil-noexp-cached_q.topics.msmarco-doc.dev.unicoil-noexp.txt
target/appassembler/bin/trec_eval -c -M 100 -m recip_rank tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-segmented-unicoil-noexp.unicoil-noexp-cached_q.topics.msmarco-doc.dev.unicoil-noexp.txt
target/appassembler/bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-segmented-unicoil-noexp.unicoil-noexp-cached_q.topics.msmarco-doc.dev.unicoil-noexp.txt
target/appassembler/bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-doc.dev.txt runs/run.msmarco-doc-segmented-unicoil-noexp.unicoil-noexp-cached_q.topics.msmarco-doc.dev.unicoil-noexp.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **uniCOIL (no expansions)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.3413    |
| **RR@100**                                                                                                   | **uniCOIL (no expansions)**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.3409    |
| **R@100**                                                                                                    | **uniCOIL (no expansions)**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.8639    |
| **R@1000**                                                                                                   | **uniCOIL (no expansions)**|
| [MS MARCO Doc: Dev](https://github.com/microsoft/MSMARCO-Document-Ranking)                                   | 0.9420    |

## Additional Notes

Note that due to MaxP and the need to generate runs to different depths, we can set `-hits` and `-selectMaxPassage.hits` differently.
Because of tie-breaking effects, we get slightly different results:

| Condition                                            | AP@1000 | RR@100 | R@100  | R@1000 | MS MARCO MRR @100   |
|:-----------------------------------------------------|:--------|:-------|:-------|:-------|:--------------------|
| `-hits 10000 -selectMaxPassage.hits 1000` (as above) | 0.3413  | 0.3409 | 0.8639 | 0.9420 | 0.34138671941993426 |
| `-hits 10000 -selectMaxPassage.hits 100`             | 0.3409  | 0.3409 | 0.8639 | -      | 0.3410112121151749  |
| `-hits 1000 -selectMaxPassage.hits 100`              | 0.3409  | 0.3409 | 0.8639 | -      | 0.3410112121151749  |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/msmarco-doc-segmented-unicoil-noexp.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2021-06-28 (commit [`1550683`](https://github.com/castorini/anserini/commit/1550683e41cefe89b7e67c0a5f0e147bc70dfcda))
+ Results reproduced by [@JMMackenzie](https://github.com/JMMackenzie) on 2021-07-02 (commit [`e4c5127`](https://github.com/castorini/anserini/commit/e4c51278d375ebad9aa2bf9bde66cab32260d6b4))
+ Results reproduced by [@amallia](https://github.com/amallia) on 2021-07-14 (commit [`dad4b82`](https://github.com/castorini/anserini/commit/dad4b82cba2d879ae20147b2abdd04564331ea6f))
+ Results reproduced by [@ArvinZhuang](https://github.com/ArvinZhuang) on 2021-07-16 (commit [`43ad899`](https://github.com/castorini/anserini/commit/43ad899337ac5e3b219d899bb218c4bcae18b1e6))
+ Results reproduced by [@yuki617](https://github.com/yuki617) on 2022-02-16 (commit [`c7614d2`](https://github.com/castorini/anserini/commit/c7614d212a8f7744b2e7071fd5819c058ab6a09c))
+ Results reproduced by [@mayankanand007](https://github.com/mayankanand007) on 2022-02-23 (commit [`6a70804`](https://github.com/castorini/anserini/commit/6a708047f71528f7d516c0dd45485204a36e6b1d))
+ Results reproduced by [@manveertamber](https://github.com/manveertamber) on 2022-02-25 (commit [`7472d86`](https://github.com/castorini/anserini/commit/7472d862c7311bc8bbd30655c940d6396e27c223))
+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-06-06 (commit [`236b386`](https://github.com/castorini/anserini/commit/236b386ddc11d292b4b736162b59488a02236d6c))
