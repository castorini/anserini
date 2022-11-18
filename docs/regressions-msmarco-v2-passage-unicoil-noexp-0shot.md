# Anserini Regressions: MS MARCO (V2) Passage Ranking

**Model**: uniCOIL (without any expansions) zero-shot

This page describes regression experiments for passage ranking on the MS MARCO (V2) passage corpus using the dev queries, which is integrated into Anserini's regression testing framework.
Here, we cover experiments with the uniCOIL model trained on the MS MARCO V1 passage ranking test collection, applied in a zero-shot manner, without any expansions.

The uniCOIL model is described in the following paper:

> Jimmy Lin and Xueguang Ma. [A Few Brief Notes on DeepImpact, COIL, and a Conceptual Framework for Information Retrieval Techniques.](https://arxiv.org/abs/2106.14807) _arXiv:2106.14807_.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-v2-passage-unicoil-noexp-0shot.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-v2-passage-unicoil-noexp-0shot.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-unicoil-noexp-0shot
```

We make available a version of the MS MARCO passage corpus that has already been processed with uniCOIL, i.e., we have performed model inference on every document and stored the output sparse vectors.
Thus, no neural inference is involved.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression msmarco-v2-passage-unicoil-noexp-0shot
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
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v2-passage-unicoil-noexp-0shot \
  --corpus-path collections/msmarco-v2-passage-unicoil-noexp-0shot
```

## Indexing

Sample indexing command:

```bash
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-v2-passage-unicoil-noexp-0shot \
  -index indexes/lucene-index.msmarco-v2-passage-unicoil-noexp-0shot/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 24 -impact -pretokenized -storeRaw \
  >& logs/log.msmarco-v2-passage-unicoil-noexp-0shot &
```

The path `/path/to/msmarco-v2-passage-unicoil-noexp-0shot/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doclengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the uniCOIL tokens.
Upon completion, we should have an index with 138,364,198 documents.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
These regression experiments use the [dev queries](../src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt) and the [dev2 queries](../src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt).

After indexing has completed, you should be able to perform retrieval as follows:

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-unicoil-noexp-0shot/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.txt \
  -parallelism 16 -impact -pretokenized &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-unicoil-noexp-0shot/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.txt \
  -parallelism 16 -impact -pretokenized &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-unicoil-noexp-0shot/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rm3.topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.txt \
  -parallelism 16 -impact -pretokenized -rm3 -collection JsonVectorCollection &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-unicoil-noexp-0shot/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rm3.topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.txt \
  -parallelism 16 -impact -pretokenized -rm3 -collection JsonVectorCollection &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-unicoil-noexp-0shot/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rocchio.topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.txt \
  -parallelism 16 -impact -pretokenized -rocchio -collection JsonVectorCollection &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-v2-passage-unicoil-noexp-0shot/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rocchio.topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.txt \
  -parallelism 16 -impact -pretokenized -rocchio -collection JsonVectorCollection &
```

Evaluation can be performed using `trec_eval`:

```bash
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot.topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rm3.topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rm3.topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rm3.topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rm3.topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rm3.topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rm3.topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rocchio.topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rocchio.topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rocchio.topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rocchio.topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rocchio.topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -c -M 100 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-unicoil-noexp-0shot.unicoil-noexp-0shot+rocchio.topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP@100**                                                                                                  | **uniCOIL (noexp) zero-shot**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.1333    | 0.1115    | 0.1163    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.1374    | 0.1190    | 0.1179    |
| **MRR@100**                                                                                                  | **uniCOIL (noexp) zero-shot**| **+RM3**  | **+Rocchio**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.1342    | 0.1124    | 0.1172    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.1385    | 0.1197    | 0.1186    |
| **R@100**                                                                                                    | **uniCOIL (noexp) zero-shot**| **+RM3**  | **+Rocchio**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.4976    | 0.5006    | 0.5082    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.5217    | 0.5129    | 0.5218    |
| **R@1000**                                                                                                   | **uniCOIL (noexp) zero-shot**| **+RM3**  | **+Rocchio**|
| [MS MARCO V2 Passage: Dev](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                      | 0.7010    | 0.7258    | 0.7299    |
| [MS MARCO V2 Passage: Dev2](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html)                     | 0.7114    | 0.7346    | 0.7387    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/msmarco-v2-passage-unicoil-noexp-0shot.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-06-06 (commit [`236b386`](https://github.com/castorini/anserini/commit/236b386ddc11d292b4b736162b59488a02236d6c))
