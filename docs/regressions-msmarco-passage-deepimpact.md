# Anserini Regressions: MS MARCO Passage Ranking

**Model**: DeepImpact

This page describes regression experiments, integrated into Anserini's regression testing framework, using DeepImpact on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking).
The DeepImpact model is described in the following paper:

> Antonio Mallia, Omar Khattab, Nicola Tonellotto, and Torsten Suel. [Learning Passage Impacts for Inverted Indexes.](https://dl.acm.org/doi/10.1145/3404835.3463030) _SIGIR 2021_.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-passage-deepimpact.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-passage-deepimpact.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-deepimpact
```

We make available a version of the MS MARCO passage corpus that has already been processed with DeepImpact, i.e., we have applied neural inference and stored the output sparse vectors.

From any machine, the following command will download the corpus and perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --download --index --verify --search --regression msmarco-passage-deepimpact
```

The `run_regression.py` script automates the following steps, but if you want to perform each step manually, simply copy/paste from the commands below and you'll obtain the same regression results.

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-deepimpact.tar -P collections/
tar xvf collections/msmarco-passage-deepimpact.tar -C collections/
```

To confirm, `msmarco-passage-deepimpact.tar` is 3.6 GB and has MD5 checksum `73843885b503af3c8b3ee62e5f5a9900`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression msmarco-passage-deepimpact \
  --corpus-path collections/msmarco-passage-deepimpact
```

## Indexing

Sample indexing command:

```bash
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-passage-deepimpact \
  -index indexes/lucene-index.msmarco-passage-deepimpact/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -impact -pretokenized \
  >& logs/log.msmarco-passage-deepimpact &
```

The path `/path/to/msmarco-passage-deepimpact/` should point to the corpus downloaded above.

The important indexing options to note here are `-impact -pretokenized`: the first tells Anserini not to encode BM25 doc lengths into Lucene's norms (which is the default) and the second option says not to apply any additional tokenization on the SPLADEv2 tokens.
Upon completion, we should have an index with 8,841,823 documents.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-deepimpact/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.deepimpact.tsv.gz \
  -topicreader TsvInt \
  -output runs/run.msmarco-passage-deepimpact.deepimpact.topics.msmarco-passage.dev-subset.deepimpact.txt \
  -impact -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```bash
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-deepimpact.deepimpact.topics.msmarco-passage.dev-subset.deepimpact.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -M 10 -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-deepimpact.deepimpact.topics.msmarco-passage.dev-subset.deepimpact.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-deepimpact.deepimpact.topics.msmarco-passage.dev-subset.deepimpact.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-deepimpact.deepimpact.topics.msmarco-passage.dev-subset.deepimpact.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **AP@1000**                                                                                                  | **DeepImpact**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3334    |
| **RR@10**                                                                                                    | **DeepImpact**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.3274    |
| **R@100**                                                                                                    | **DeepImpact**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.8421    |
| **R@1000**                                                                                                   | **DeepImpact**|
| [MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)                                | 0.9476    |

The above runs are in TREC output format and evaluated with `trec_eval`.
In order to reproduce results reported in the paper, we need to convert to MS MARCO output format and then evaluate:

```bash
python tools/scripts/msmarco/convert_trec_to_msmarco_run.py \
   --input runs/run.msmarco-passage-deepimpact.deepimpact.topics.msmarco-passage.dev-subset.deepimpact.txt \
   --output runs/run.msmarco-passage-deepimpact.deepimpact.topics.msmarco-passage.dev-subset.deepimpact.tsv --quiet

python tools/scripts/msmarco/msmarco_passage_eval.py \
   tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt \
   runs/run.msmarco-passage-deepimpact.deepimpact.topics.msmarco-passage.dev-subset.deepimpact.tsv
```

The results should be as follows:

```
#####################
MRR @10: 0.3252764133351524
QueriesRanked: 6980
#####################
```

The final evaluation metric is very close to the one reported in the paper (0.326).

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/msmarco-passage-deepimpact.template) and run `bin/build.sh` to rebuild the documentation.

+ Results reproduced by [@MXueguang](https://github.com/MXueguang) on 2021-06-17 (commit [`ff618db`](https://github.com/castorini/anserini/commit/ff618dbf87feee0ad75dc42c72a361c05984097d))
+ Results reproduced by [@JMMackenzie](https://github.com/jmmackenzie) on 2021-06-22 (commit [`4904341`](https://github.com/castorini/anserini/commit/490434172a035b6eade8c17771aed83cc7f5d996))
+ Results reproduced by [@amyxie361](https://github.com/amyxie361) on 2021-06-22 (commit [`6f9352f`](https://github.com/castorini/anserini/commit/6f9352fc5d6a4938fadc2bda9d0c428056eec5f0))
+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-06-14 (commit [`dc07344`](https://github.com/castorini/anserini/commit/dc073447c8a0c07b53d979c49bf1e2e018200508))
