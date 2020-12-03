# Anserini: BM25 Baselines for FEVER Fact Verification

This page contains instructions for running BM25 baselines on the [FEVER fact verification task](https://fever.ai/).

## Data Prep

We are going to use the repository's root directory as the working directory.

First, we need to download and extract the FEVER dataset:

```bash
mkdir collections/fever
mkdir indexes/fever

wget https://s3-eu-west-1.amazonaws.com/fever.public/wiki-pages.zip -P collections/fever
unzip collections/fever/wiki-pages.zip -d collections/fever

wget https://s3-eu-west-1.amazonaws.com/fever.public/train.jsonl -P collections/fever
wget https://s3-eu-west-1.amazonaws.com/fever.public/paper_dev.jsonl -P collections/fever
```

To confirm, `wiki-pages.zip` should have MD5 checksum of `ed8bfd894a2c47045dca61f0c8dc4c07`.

## Building Lucene Indexes

Next, we want to index the Wikipedia dump (`wiki-pages.zip`) using Anserini. Note that this Wikipedia dump consists of Wikipedia articles' introductions only, which we will refer to as "paragraphs" from this point onward.

We will consider two variants: (1) Paragraph Indexing and (2) Sentence Indexing.

### Paragraph Indexing

We can index paragraphs with `FeverParagraphCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
 -collection FeverParagraphCollection -generator DefaultLuceneDocumentGenerator \
 -threads 9 -input collections/fever/wiki-pages \
 -index indexes/fever/lucene-index-fever-paragraph -storePositions -storeDocvectors -storeRaw 
```

Upon completion, we should have an index with 5,396,106 documents (paragraphs).

### Sentence Indexing

We can index sentences with `FeverSentenceCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
 -collection FeverSentenceCollection -generator DefaultLuceneDocumentGenerator \
 -threads 9 -input collections/fever/wiki-pages \
 -index indexes/fever/lucene-index-fever-sentence -storePositions -storeDocvectors -storeRaw 
```

Upon completion, we should have an index with 25,247,887 documents (sentences).

## Performing Retrieval on the Dev Queries

Note that while we use paragraph indexing for this section, these steps can easily be modified for sentence indexing.

Before we can retrieve with our index, we need to generate the queries and qrels files for the dev split of the FEVER dataset:

```bash
python src/main/python/fever/generate_queries_and_qrels.py \
 --dataset_file collections/fever/paper_dev.jsonl \
 --output_queries_file collections/fever/queries.paragraph.dev.tsv \
 --output_qrels_file collections/fever/qrels.paragraph.dev.tsv \
 --granularity paragraph
```

We can now perform a retrieval run:

```bash
python tools/scripts/msmarco/retrieve.py \
 --hits 1000 --threads 1 \
 --index indexes/fever/lucene-index-fever-paragraph \
 --queries collections/fever/queries.paragraph.dev.tsv \
 --output runs/run.fever-paragraph.dev.tsv
```

Note that by default, the above script uses BM25 with tuned parameters `k1=0.82`, `b=0.68`.

## Evaluating with `trec_eval`

Finally, we can evaluate the retrieved documents using the official TREC evaluation tool, `trec_eval`.

We first need to convert the runs and qrels files to the TREC format:

```bash
python tools/scripts/msmarco/convert_msmarco_to_trec_run.py \
 --input runs/run.fever-paragraph.dev.tsv \
 --output runs/run.fever-paragraph.dev.trec

python tools/scripts/msmarco/convert_msmarco_to_trec_qrels.py \
 --input collections/fever/qrels.paragraph.dev.tsv \
 --output collections/fever/qrels.paragraph.dev.trec
```

Then we run the `trec_eval` tool:

```bash
tools/eval/trec_eval.9.0.4/trec_eval -c -m all_trec \
 collections/fever/qrels.paragraph.dev.trec runs/run.fever-paragraph.dev.trec
```

Within the output, we should see:

```
recall_1000           	all	0.9417
```

## Comparing with FEVER Baseline

We can also evaluate our retrieval compared to the TF-IDF baseline described in [the FEVER paper](https://www.aclweb.org/anthology/N18-1074.pdf). Specifically, we want to compare the metrics described in Table 2 of the paper.

We evaluate the run file produced earlier:

```bash
python src/main/python/fever/evaluate_doc_retrieval.py \
 --truth_file collections/fever/paper_dev.jsonl \
 --run_file runs/run.fever-paragraph.dev.tsv
```

This run produces the following results:

| k   | Fully Supported | Oracle Accuracy |
|:----|----------------:|----------------:|
| 1   | 0.3272          | 0.5515          |
| 5   | 0.5656          | 0.7104          |
| 10  |	0.6542          | 0.7695          |
| 25  |	0.7459          | 0.8306          |
| 50  |	0.8098          | 0.8732          |
| 100 |	0.8561          | 0.9041          |

### BM25 Tuning

The above retrieval uses the MS MARCO default BM25 parameters of `k1=0.82`, `b=0.68`. We can tune these parameters to outperform the results of the TF-IDF baseline in the paper.

We tune on a subset of the training split of the dataset. We generate that subset:

```bash
python src/main/python/fever/generate_subset.py \
 --dataset_file collections/fever/train.jsonl \
 --subset_file collections/fever/train-subset.jsonl
```

We then generate the queries and qrels files for this subset.

```bash
python src/main/python/fever/generate_queries_and_qrels.py \
 --dataset_file collections/fever/train-subset.jsonl \
 --output_queries_file collections/fever/queries.paragraph.train-subset.tsv \
 --output_qrels_file collections/fever/qrels.paragraph.train-subset.tsv \
 --granularity paragraph
```

We tune the BM25 parameters with a grid search of parameter values in 0.1 increments. We save the run files generated by this process to a new folder `runs/fever-bm25` (do not use `runs` here).

```bash
python src/main/python/fever/tune_bm25.py \
 --runs_folder runs/fever-bm25 \
 --index_folder indexes/fever/lucene-index-fever-paragraph \
 --queries_file collections/fever/queries.paragraph.train-subset.tsv \
 --qrels_file collections/fever/qrels.paragraph.train-subset.tsv
```

From the grid search, we observe that the parameters `k1=0.6`, `b=0.5` perform fairly well. If we retrieve on the dev set with these parameters:

```bash
python tools/scripts/msmarco/retrieve.py \
 --hits 1000 --threads 1 \
 --index indexes/fever/lucene-index-fever-paragraph \
 --queries collections/fever/queries.paragraph.dev.tsv \
 --output runs/run.fever-paragraph-0.6-0.5.dev.tsv \
 --k1 0.6 --b 0.5
```

and we evaluate this run file:

```bash
python src/main/python/fever/evaluate_doc_retrieval.py \
 --truth_file collections/fever/paper_dev.jsonl \
 --run_file runs/run.fever-paragraph-0.6-0.5.dev.tsv
```

then we can achieve the following results:

| k   | Fully Supported | Oracle Accuracy |
|:----|----------------:|----------------:|
| 1   | 0.3857          | 0.5905          |
| 5   | 0.6367          | 0.7578          |
| 10  | 0.7193          | 0.8129          |
| 25  | 0.8003          | 0.8669          |
| 50  | 0.8473          | 0.8982          |
| 100 | 0.8804          | 0.9203          |

which outperforms the TF-IDF baseline in the FEVER paper at every tested value of k.

## Replication Log
+ Results replicated by [@LizzyZhang-tutu](https://github.com/LizzyZhang-tutu) on 2020-11-26 (commit [`1b4d0a2`](https://github.com/castorini/anserini/commit/1b4d0a29879a867ca5d1f003f924acc3279455ba))
