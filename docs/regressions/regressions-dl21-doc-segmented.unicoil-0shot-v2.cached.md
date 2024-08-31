# Anserini Regressions: TREC 2021 Deep Learning Track (Document)

**Model**: uniCOIL (with doc2query-T5 expansions) zero-shot on segmented documents (title/segment encoding)

This page describes experiments, integrated into Anserini's regression testing framework, on the [TREC 2021 Deep Learning Track document ranking task](https://trec.nist.gov/data/deep2021.html) using the MS MARCO V2 _segmented_ document corpus.
Here, we cover experiments with the uniCOIL model trained on the MS MARCO V1 passage ranking test collection, applied in a zero-shot manner, with doc2query-T5 expansions.

The uniCOIL model is described in the following paper:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

**NOTE**: As an important detail, there is the question of what text we feed into the encoder to generate document representations.
Initially, we fed only the segment text, but later we realized that prepending the title of the document improves effectiveness.
This regression captures the latter title/segment encoding, which for clarity we call v2, distinguished from segment-only encoding, which is documented [here](regressions-dl21-doc-segmented-unicoil-0shot.md).
The segment-only encoding results are deprecated and kept around primarily for archival purposes and ablation experiments.
You probably don't want to use them.

For additional instructions on working with the MS MARCO V2 document corpus, refer to [this page](../../docs/experiments-msmarco-v2.md).

Note that the NIST relevance judgments provide far more relevant documents per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl21-doc-segmented.unicoil-0shot-v2.cached.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl21-doc-segmented.unicoil-0shot-v2.cached.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented.unicoil-0shot-v2.cached
```

We make available a version of the MS MARCO document corpus that has already been processed with uniCOIL (per above), i.e., we have applied doc2query-T5 expansions, performed model inference on every document, and stored the output sparse vectors.
Thus, no neural inference is involved.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression dl21-doc-segmented.unicoil-0shot-v2.cached
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download, unpack, and prepare the corpus:

```bash
# Download
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_doc_segmented_unicoil_0shot_v2.tar -P collections/

# Unpack
tar -xvf collections/msmarco_v2_doc_segmented_unicoil_0shot_v2.tar -C collections/

# Rename (indexer is expecting corpus under a slightly different name)
mv collections/msmarco_v2_doc_segmented_unicoil_0shot_v2 collections/msmarco-v2-doc-segmented-unicoil-0shot-v2
```

To confirm, `msmarco_v2_doc_segmented_unicoil_0shot_v2.tar` is 72 GB and has an MD5 checksum of `c5639748c2cbad0152e10b0ebde3b804`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl21-doc-segmented.unicoil-0shot-v2.cached \
  --corpus-path collections/msmarco-v2-doc-segmented-unicoil-0shot-v2
```

## Indexing

Sample indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-v2-doc-segmented-unicoil-0shot-v2 \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2-doc-segmented.unicoil-0shot-v2/ \
  -impact -pretokenized -storeRaw \
  >& logs/log.msmarco-v2-doc-segmented-unicoil-0shot-v2 &
```

The path `/path/to/msmarco-v2-doc-segmented-unicoil-0shot-v2/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the uniCOIL tokens.
Upon completion, we should have an index with 124,131,414 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 57 topics for which NIST has provided judgments as part of the [TREC 2021 Deep Learning Track](https://trec.nist.gov/data/deep2021.html).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-doc-segmented.unicoil-0shot-v2/ \
  -topics tools/topics-and-qrels/topics.dl21.unicoil.0shot.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached.topics.dl21.unicoil.0shot.txt \
  -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 -impact -pretokenized &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-doc-segmented.unicoil-0shot-v2/ \
  -topics tools/topics-and-qrels/topics.dl21.unicoil.0shot.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached+rm3.topics.dl21.unicoil.0shot.txt \
  -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 -impact -pretokenized -rm3 -collection JsonVectorCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-doc-segmented.unicoil-0shot-v2/ \
  -topics tools/topics-and-qrels/topics.dl21.unicoil.0shot.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached+rocchio.topics.dl21.unicoil.0shot.txt \
  -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000 -impact -pretokenized -rocchio -collection JsonVectorCollection &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached.topics.dl21.unicoil.0shot.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached.topics.dl21.unicoil.0shot.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached.topics.dl21.unicoil.0shot.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached.topics.dl21.unicoil.0shot.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached+rm3.topics.dl21.unicoil.0shot.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached+rm3.topics.dl21.unicoil.0shot.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached+rm3.topics.dl21.unicoil.0shot.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached+rm3.topics.dl21.unicoil.0shot.txt

bin/trec_eval -c -M 100 -m map tools/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached+rocchio.topics.dl21.unicoil.0shot.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached+rocchio.topics.dl21.unicoil.0shot.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached+rocchio.topics.dl21.unicoil.0shot.txt
bin/trec_eval -c -M 100 -m recip_rank -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl21-doc.txt runs/run.msmarco-v2-doc-segmented-unicoil-0shot-v2.unicoil-0shot-cached+rocchio.topics.dl21.unicoil.0shot.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **uniCOIL (with doc2query-T5) zero-shot**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.2718    | 0.3293    | 0.3434    |
| **MRR@100**                                                                                                  | **uniCOIL (with doc2query-T5) zero-shot**| **+RM3**  | **+Rocchio**|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.9684    | 0.9357    | 0.9649    |
| **nDCG@10**                                                                                                  | **uniCOIL (with doc2query-T5) zero-shot**| **+RM3**  | **+Rocchio**|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.6783    | 0.6980    | 0.7061    |
| **R@100**                                                                                                    | **uniCOIL (with doc2query-T5) zero-shot**| **+RM3**  | **+Rocchio**|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.3700    | 0.4233    | 0.4374    |
| **R@1000**                                                                                                   | **uniCOIL (with doc2query-T5) zero-shot**| **+RM3**  | **+Rocchio**|
| [DL21 (Doc)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                         | 0.7069    | 0.7611    | 0.7809    |
