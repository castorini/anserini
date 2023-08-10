# Anserini Regressions: OpenAI-ada2 for MS MARCO Passage Ranking

**Model**: OpenAI-ada2 (using pre-encoded queries) with HNSW indexes

This page describes regression experiments, integrated into Anserini's regression testing framework, using the OpenAI-ada2 model on the [MS MARCO passage ranking task](https://github.com/microsoft/MSMARCO-Passage-Ranking).

In these experiments, we are using pre-encoded queries (i.e., cached results of query encoding).

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/msmarco-passage-openai-ada2.tar -P collections/
tar xvf collections/msmarco-passage-openai-ada2.tar -C collections/
```

With the corpus downloaded, the following command will perform the remaining steps below:

## Indexing

Sample indexing command, building HNSW indexes:

```bash
target/appassembler/bin/IndexHnswDenseVectors \
  -collection JsonDenseVectorCollection \
  -input /path/to/msmarco-passage-openai-ada2 \
  -index indexes/lucene-hnsw.msmarco-passage-openai-ada2/ \
  -generator LuceneDenseVectorDocumentGenerator \
  -threads 16 -M 16 -efC 100 \
  >& logs/log.msmarco-passage-openai-ada2 &
```

The path `/path/to/msmarco-passage-openai-ada2/` should point to the corpus downloaded above.

Upon completion, we should have an index with 8,841,823 documents.

<!-- For additional details, see explanation of [common indexing options](common-indexing-options.md). -->

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows using HNSW indexes, replacing `{SETTING}` with the desired setting out of [`msmarco-passage.dev-subset.openai-ada2`, `dl19-passage.openai-ada2`, `dl19-passage.openai-ada2`, `dl19-passage.openai-ada2-hyde`, `dl20-passage.openai-ada2-hyde`]:

```bash
target/appassembler/bin/SearchHnswDenseVectors \
  -index indexes/lucene-hnsw.msmarco-passage-openai-ada2/ \
  -topics tools/topics-and-qrels/topics.{SETTING}.jsonl.gz \
  -topicreader JsonIntVector \
  -output runs/run.{SETTING}.txt \
  -querygenerator VectorQueryGenerator -topicfield vector -threads 16 -hits 1000 -efSearch 1000 &
```

## Evaluation

Evaluation can be performed using `trec_eval`.

For `msmarco-passage.dev-subset.openai-ada2`:
```bash
tools/eval/trec_eval.9.0.4/trec_eval -c -M 10 -m recip_rank tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.dev-subset.openai-ada2.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage.dev-subset.openai-ada2.txt
```

Otherwise, set `{QRELS}` as `dl19-passage` or `dl20-passage` according to the `{SETTING}` and run:
```bash
tools/eval/trec_eval.9.0.4/trec_eval -c -l 2 -m map tools/topics-and-qrels/qrels.{QRELS}.txt runs/run.{SETTING}.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 tools/topics-and-qrels/qrels.{QRELS}.txt runs/run.{SETTING}.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -l 2 -m recall.1000 tools/topics-and-qrels/qrels.{QRELS}.txt runs/run.{SETTING}.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

`msmarco-passage.dev-subset.openai-ada2`
```
recip_rank              all     0.3434
recall_1000             all     0.9841
```

`dl19-passage.openai-ada2`
```
map                     all     0.4786
ndcg_cut_10             all     0.7035
recall_1000             all     0.8625
```

`dl20-passage.openai-ada2`
```
map                     all     0.4771
ndcg_cut_10             all     0.6759
recall_1000             all     0.8705
```

`dl19-passage.openai-ada2-hyde`
```
map                     all     0.5124
ndcg_cut_10             all     0.7163
recall_1000             all     0.8968
```

`dl20-passage.openai-ada2-hyde`
```
map                     all     0.4938
ndcg_cut_10             all     0.6666
recall_1000             all     0.8919
```

Note that due to the non-deterministic nature of HNSW indexing, results may differ slightly between each experimental run.
Nevertheless, scores are generally stable to the third digit after the decimal point.

## Reproduction Log[*](reproducibility.md)

