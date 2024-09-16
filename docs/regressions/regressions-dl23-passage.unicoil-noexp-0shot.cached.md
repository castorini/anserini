# Anserini Regressions: TREC 2023 Deep Learning Track (Passage)

**Model**: uniCOIL (without any expansions) zero-shot

This page describes baseline experiments, integrated into Anserini's regression testing framework, on the [TREC 2023 Deep Learning Track passage ranking task](https://trec.nist.gov/data/deep2023.html) using the MS MARCO V2 passage corpus.
Here, we cover experiments with the uniCOIL model trained on the MS MARCO V1 passage ranking test collection, applied in a zero-shot manner, without any expansions.

The uniCOIL model is described in the following paper:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

For additional instructions on working with the MS MARCO V2 passage corpus, refer to [this page](../../docs/experiments-msmarco-v2.md).

Note that the NIST relevance judgments provide far more relevant passages per topic, unlike the "sparse" judgments provided by Microsoft (these are sometimes called "dense" judgments to emphasize this contrast).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/dl23-passage.unicoil-noexp-0shot.cached.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/dl23-passage.unicoil-noexp-0shot.cached.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl23-passage.unicoil-noexp-0shot.cached
```

We make available a version of the corpus that has already been processed with uniCOIL, i.e., we have performed model inference on every document and stored the output sparse vectors.
Thus, no neural inference is involved.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression dl23-passage.unicoil-noexp-0shot.cached
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download, unpack, and prepare the corpus:

```bash
# Download
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_passage_unicoil_noexp_0shot.tar -P collections/

# Unpack
tar -xvf collections/msmarco_v2_passage_unicoil_noexp_0shot.tar -C collections/

# Rename (indexer is expecting corpus under a slightly different name)
mv collections/msmarco_v2_passage_unicoil_noexp_0shot collections/msmarco-v2-passage-unicoil-noexp-0shot
```

To confirm, `msmarco_v2_passage_unicoil_noexp_0shot.tar` is 24 GB and has an MD5 checksum of `d9cc1ed3049746e68a2c91bf90e5212d`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression dl23-passage.unicoil-noexp-0shot.cached \
  --corpus-path collections/msmarco-v2-passage-unicoil-noexp-0shot
```

## Indexing

Sample indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 24 \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-v2-passage-unicoil-noexp-0shot \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v2-passage.unicoil-noexp-0shot/ \
  -impact -pretokenized -storeRaw \
  >& logs/log.msmarco-v2-passage-unicoil-noexp-0shot &
```

The path `/path/to/msmarco-v2-passage-unicoil-noexp-0shot/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the uniCOIL tokens.
Upon completion, we should have an index with 138,364,198 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 82 topics for which NIST has provided judgments as part of the [TREC 2023 Deep Learning Track](https://trec.nist.gov/data/deep2023.html).

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage.unicoil-noexp-0shot/ \
  -topics tools/topics-and-qrels/topics.dl23.unicoil-noexp.0shot.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached.topics.dl23.unicoil-noexp.0shot.txt \
  -impact -pretokenized &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage.unicoil-noexp-0shot/ \
  -topics tools/topics-and-qrels/topics.dl23.unicoil-noexp.0shot.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached+rm3.topics.dl23.unicoil-noexp.0shot.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v2-passage.unicoil-noexp-0shot/ \
  -topics tools/topics-and-qrels/topics.dl23.unicoil-noexp.0shot.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached+rocchio.topics.dl23.unicoil-noexp.0shot.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached.topics.dl23.unicoil-noexp.0shot.txt
bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached.topics.dl23.unicoil-noexp.0shot.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached.topics.dl23.unicoil-noexp.0shot.txt
bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached.topics.dl23.unicoil-noexp.0shot.txt
bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached.topics.dl23.unicoil-noexp.0shot.txt

bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached+rm3.topics.dl23.unicoil-noexp.0shot.txt
bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached+rm3.topics.dl23.unicoil-noexp.0shot.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached+rm3.topics.dl23.unicoil-noexp.0shot.txt
bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached+rm3.topics.dl23.unicoil-noexp.0shot.txt
bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached+rm3.topics.dl23.unicoil-noexp.0shot.txt

bin/trec_eval -c -M 100 -m map -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached+rocchio.topics.dl23.unicoil-noexp.0shot.txt
bin/trec_eval -c -M 100 -m recip_rank -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached+rocchio.topics.dl23.unicoil-noexp.0shot.txt
bin/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached+rocchio.topics.dl23.unicoil-noexp.0shot.txt
bin/trec_eval -c -m recall.100 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached+rocchio.topics.dl23.unicoil-noexp.0shot.txt
bin/trec_eval -c -m recall.1000 -l 2 tools/topics-and-qrels/qrels.dl23-passage.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot-cached+rocchio.topics.dl23.unicoil-noexp.0shot.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **uniCOIL (noexp) zero-shot**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.1111    | 0.1049    | 0.1153    |
| **MRR@100**                                                                                                  | **uniCOIL (noexp) zero-shot**| **+RM3**  | **+Rocchio**|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.5913    | 0.5314    | 0.5270    |
| **nDCG@10**                                                                                                  | **uniCOIL (noexp) zero-shot**| **+RM3**  | **+Rocchio**|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.3262    | 0.3126    | 0.3239    |
| **R@100**                                                                                                    | **uniCOIL (noexp) zero-shot**| **+RM3**  | **+Rocchio**|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.2761    | 0.2811    | 0.2904    |
| **R@1000**                                                                                                   | **uniCOIL (noexp) zero-shot**| **+RM3**  | **+Rocchio**|
| [DL23 (Passage)](https://microsoft.github.io/msmarco/TREC-Deep-Learning)                                     | 0.5070    | 0.5076    | 0.5423    |
