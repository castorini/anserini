# Anserini: Regressions on MS MARCO Passage with uniCOIL+TILDE

This page describes regression experiments, integrated into Anserini's regression testing framework, with uniCOIL+TILDE on the [MS MARCO Passage Ranking Task](https://github.com/microsoft/MSMARCO-Passage-Ranking).
The uniCOIL+TILDE model is described in the following paper:

> Shengyao Zhuang and Guido Zuccon. [Fast Passage Re-ranking with Contextualized Exact Term Matching and Efficient Passage Expansion.](https://arxiv.org/pdf/2108.08513) _arXiv:2108.08513_.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-passage-unicoil-tilde-expansion.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-passage-unicoil-tilde-expansion.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-unicoil-tilde-expansion
```

## Corpus

We make available a version of the MS MARCO passage corpus that has already been processed with the model (i.e., with inference applied to generate the lexical representations).
Thus, no neural inference is involved.

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-unicoil-tilde-expansion.tar -P collections/

tar xvf collections/msmarco-passage-unicoil-tilde-expansion.tar -C collections/
```

To confirm, `msmarco-passage-unicoil-tilde-expansion.tar` is 3.9 GB and has MD5 checksum `1685aee10071441987ad87f2e91f1706`.

With the corpus downloaded, the following command will perform the complete regression, end to end, on any machine:

```
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-unicoil-tilde-expansion \
  --corpus-path collections/msmarco-passage-unicoil-tilde-expansion
```

Alternatively, you can simply copy/paste from the commands below and obtain the same results.

## Indexing

Sample indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-passage-unicoil-tilde-expansion \
  -index indexes/lucene-index.msmarco-passage-unicoil-tilde-expansion/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -impact -pretokenized \
  >& logs/log.msmarco-passage-unicoil-tilde-expansion &
```

The path `/path/to/msmarco-passage-unicoil-tilde-expansion/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doc lengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the SPLADEv2 tokens.
Upon completion, we should have an index with 8,841,823 documents.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-unicoil-tilde-expansion/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.unicoil-tilde-expansion.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-unicoil-tilde-expansion.unicoil-tilde-expansion.topics.msmarco-passage.dev-subset.unicoil-tilde-expansion.txt \
  -impact -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-unicoil-tilde-expansion.unicoil-tilde-expansion.topics.msmarco-passage.dev-subset.unicoil-tilde-expansion.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| MAP                                                                                                          | uniCOIL (with TILDE expansions)|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3561    |


| MRR                                                                                                          | uniCOIL (with TILDE expansions)|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3607    |


| R@1000                                                                                                       | uniCOIL (with TILDE expansions)|
|:-------------------------------------------------------------------------------------------------------------|-----------|
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

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/msmarco-passage-unicoil-tilde-expansion.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@MXueguang](https://github.com/MXueguang) on 2021-09-14 (commit [`a05fc52`](https://github.com/castorini/anserini/commit/a05fc5215a6d9de77bd5f4b8f874f608442024a3))
+ Results reproduced by [@jmmackenzie](https://github.com/jmmackenzie) on 2021-10-15 (commit [`52b76f6`](https://github.com/castorini/anserini/commit/52b76f63b163036e8fad1a6e1b10b431b4ddd06c))
