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
 --output_qrels_file collections/fever/qrels.paragraph.dev.txt \
 --granularity paragraph
```

We can now perform a retrieval run:

```bash
sh target/appassembler/bin/SearchCollection \
 -index indexes/fever/lucene-index-fever-paragraph \
 -topicreader TsvInt -topics collections/fever/queries.paragraph.dev.tsv \
 -output runs/run.fever-paragraph.dev.txt -bm25
```

Note that by default, the above uses the BM25 algorithm with parameters `k1=0.9`, `b=0.4`.

## Evaluating with `trec_eval`

Finally, we can evaluate the retrieved documents using the official TREC evaluation tool, `trec_eval`.

```bash
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall \
 collections/fever/qrels.paragraph.dev.txt runs/run.fever-paragraph.dev.txt
```

The output should be:

```
recall_5              	all	0.6622
recall_10             	all	0.7446
recall_15             	all	0.7834
recall_20             	all	0.8059
recall_30             	all	0.8353
recall_100            	all	0.8974
recall_200            	all	0.9176
recall_500            	all	0.9393
recall_1000           	all	0.9477
```

## Comparing with FEVER Baseline

We can also evaluate our retrieval compared to the TF-IDF baseline described in [the FEVER paper](https://www.aclweb.org/anthology/N18-1074.pdf). Specifically, we want to compare the metrics described in Table 2 of the paper.

We evaluate the run file produced earlier:

```bash
python src/main/python/fever/evaluate_doc_retrieval.py \
 --truth_file collections/fever/paper_dev.jsonl \
 --run_file runs/run.fever-paragraph.dev.txt
```

This run produces the following results:

| k   | Fully Supported | Oracle Accuracy |
|:----|----------------:|----------------:|
| 1   | 0.3887          | 0.5925          |
| 5   | 0.6517          | 0.7678          |
| 10  | 0.7349          | 0.8233          |
| 25  | 0.8117          | 0.8745          |
| 50  | 0.8570          | 0.9047          |
| 100 | 0.8900          | 0.9267          |

Note that this outperforms the TF-IDF baseline in the FEVER paper at every value of k.

### BM25 Tuning

The above retrieval uses BM25 default parameters of `k1=0.9`, `b=0.4`. We can tune these parameters to obtain even better retrieval results.

We tune on a subset of the training split of the dataset. We generate that subset:

```bash
python src/main/python/fever/generate_subset.py \
 --dataset_file collections/fever/train.jsonl \
 --subset_file collections/fever/train-subset.jsonl \
 --length 2000
```

If necessary, to speed up grid search later on, decrease the length of the subset here.

We then generate the queries and qrels files for this subset.

```bash
python src/main/python/fever/generate_queries_and_qrels.py \
 --dataset_file collections/fever/train-subset.jsonl \
 --output_queries_file collections/fever/queries.paragraph.train-subset.tsv \
 --output_qrels_file collections/fever/qrels.paragraph.train-subset.txt \
 --granularity paragraph
```

We tune the BM25 parameters with a grid search of parameter values in 0.1 increments. We save the run files generated by this process to a new folder `runs/fever-bm25` (do not use `runs` here).

```bash
python src/main/python/fever/tune_bm25.py \
 --runs_folder runs/fever-bm25 \
 --index_folder indexes/fever/lucene-index-fever-paragraph \
 --queries_file collections/fever/queries.paragraph.train-subset.tsv \
 --qrels_file collections/fever/qrels.paragraph.train-subset.txt
```

From the grid search, we observe that the parameters `k1=0.9`, `b=0.1` perform fairly well. If we retrieve on the dev set with these parameters:

```bash
sh target/appassembler/bin/SearchCollection \
 -index indexes/fever/lucene-index-fever-paragraph \
 -topicreader TsvInt -topics collections/fever/queries.paragraph.dev.tsv \
 -output runs/run.fever-paragraph-0.9-0.1.dev.txt -bm25 -bm25.k1 0.9 -bm25.b 0.1
```

and we evaluate this run file:

```bash
python src/main/python/fever/evaluate_doc_retrieval.py \
 --truth_file collections/fever/paper_dev.jsonl \
 --run_file runs/run.fever-paragraph-0.9-0.1.dev.txt
```

then we can achieve the following results:

| k   | Fully Supported | Oracle Accuracy |
|:----|----------------:|----------------:|
| 1   | 0.4121          | 0.6081          |
| 5   | 0.6899          | 0.7933          |
| 10  | 0.7636          | 0.8424          |
| 25  | 0.8263          | 0.8842          |
| 50  | 0.8651          | 0.9101          |
| 100 | 0.8932          | 0.9288          |

## Reproduction Log[*](reproducibility.md)

+ Results reproduced by [@LizzyZhang-tutu](https://github.com/LizzyZhang-tutu) on 2020-11-26 (commit [`1b4d0a2`](https://github.com/castorini/anserini/commit/1b4d0a29879a867ca5d1f003f924acc3279455ba))
+ Results reproduced by [@lintool](https://github.com/lintool) on 2021-05-30 (commit [`259d8ec`](https://github.com/castorini/anserini/commit/259d8ecedbba833386f9300a2667ef61b20943d8))
