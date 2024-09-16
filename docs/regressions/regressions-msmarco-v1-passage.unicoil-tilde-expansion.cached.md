# Anserini Regressions: MS MARCO Passage Ranking

**Model**: uniCOIL with TILDE expansions (using cached queries)

This page describes regression experiments, integrated into Anserini's regression testing framework, using uniCOIL (with TILDE expansions) on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking).
The uniCOIL+TILDE model is described in the following paper:

> Shengyao Zhuang and Guido Zuccon. [Fast Passage Re-ranking with Contextualized Exact Term Matching and Efficient Passage Expansion.](https://arxiv.org/pdf/2108.08513) _arXiv:2108.08513_.

In these experiments, we are using cached queries (i.e., cached results of query encoding).

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/msmarco-v1-passage.unicoil-tilde-expansion.cached.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/msmarco-v1-passage.unicoil-tilde-expansion.cached.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.unicoil-tilde-expansion.cached
```

We make available a version of the MS MARCO Passage Corpus that has already been processed with uniCOIL + TILDE expansions, i.e., performed model inference on every document and stored the output sparse vectors.
Thus, no neural inference is involved.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression msmarco-v1-passage.unicoil-tilde-expansion.cached
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-unicoil-tilde-expansion.tar -P collections/
tar xvf collections/msmarco-passage-unicoil-tilde-expansion.tar -C collections/
```

To confirm, `msmarco-passage-unicoil-tilde-expansion.tar` is 3.9 GB and has MD5 checksum `12a9c289d94e32fd63a7d39c9677d75c`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-v1-passage.unicoil-tilde-expansion.cached \
  --corpus-path collections/msmarco-passage-unicoil-tilde-expansion
```

## Indexing

Sample indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-passage-unicoil-tilde-expansion \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-inverted.msmarco-v1-passage.unicoil-tilde-expansion/ \
  -impact -pretokenized \
  >& logs/log.msmarco-passage-unicoil-tilde-expansion &
```

The path `/path/to/msmarco-passage-unicoil-tilde-expansion/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doc lengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the SPLADEv2 tokens.
Upon completion, we should have an index with 8,841,823 documents.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the 6980 dev set questions; see [this page](../../docs/experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-inverted.msmarco-v1-passage.unicoil-tilde-expansion/ \
  -topics tools/topics-and-qrels/topics.msmarco-passage.dev-subset.unicoil-tilde-expansion.tsv.gz \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage-unicoil-tilde-expansion.unicoil-tilde-expansion-cached_q.topics.msmarco-passage.dev-subset.unicoil-tilde-expansion.txt \
  -impact -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```bash
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-unicoil-tilde-expansion.unicoil-tilde-expansion-cached_q.topics.msmarco-passage.dev-subset.unicoil-tilde-expansion.txt
bin/trec_eval -c -M 10 -m recip_rank tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-unicoil-tilde-expansion.unicoil-tilde-expansion-cached_q.topics.msmarco-passage.dev-subset.unicoil-tilde-expansion.txt
bin/trec_eval -c -m recall.100 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-unicoil-tilde-expansion.unicoil-tilde-expansion-cached_q.topics.msmarco-passage.dev-subset.unicoil-tilde-expansion.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-unicoil-tilde-expansion.unicoil-tilde-expansion-cached_q.topics.msmarco-passage.dev-subset.unicoil-tilde-expansion.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **uniCOIL (with TILDE expansions)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3561    |
| **RR@10**                                                                                                    | **uniCOIL (with TILDE expansions)**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3496    |
| **R@100**                                                                                                    | **uniCOIL (with TILDE expansions)**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.8678    |
| **R@1000**                                                                                                   | **uniCOIL (with TILDE expansions)**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.9646    |

The above runs are in TREC output format and evaluated with `trec_eval`.
In order to reproduce results reported in the paper, we need to convert to MS MARCO output format and then evaluate:

```bash
python tools/scripts/msmarco/convert_trec_to_msmarco_run.py \
   --input runs/run.msmarco-passage-unicoil-tilde-expansion.unicoil-tilde-expansion.topics.msmarco-passage.dev-subset.unicoil-tilde-expansion.txt \
   --output runs/run.msmarco-passage-unicoil-tilde-expansion.unicoil-tilde-expansion.topics.msmarco-passage.dev-subset.unicoil-tilde-expansion.tsv --quiet

python tools/scripts/msmarco/msmarco_passage_eval.py \
   tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt \
   runs/run.msmarco-passage-unicoil-tilde-expansion.unicoil-tilde-expansion.topics.msmarco-passage.dev-subset.unicoil-tilde-expansion.tsv
```

The results should be as follows:

```
#####################
MRR @10: 0.34957184927457136
QueriesRanked: 6980
#####################
```

This corresponds to the effectiveness reported in the paper.

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/msmarco-v1-passage.unicoil-tilde-expansion.cached.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@MXueguang](https://github.com/MXueguang) on 2021-09-14 (commit [`a05fc52`](https://github.com/castorini/anserini/commit/a05fc5215a6d9de77bd5f4b8f874f608442024a3))
+ Results reproduced by [@jmmackenzie](https://github.com/jmmackenzie) on 2021-10-15 (commit [`52b76f6`](https://github.com/castorini/anserini/commit/52b76f63b163036e8fad1a6e1b10b431b4ddd06c))
+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-06-14 (commit [`dc07344`](https://github.com/castorini/anserini/commit/dc073447c8a0c07b53d979c49bf1e2e018200508))
