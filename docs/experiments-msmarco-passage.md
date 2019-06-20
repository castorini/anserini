# Anserini: Experiments on [MS MARCO (Passage)](https://github.com/microsoft/MSMARCO-Passage-Ranking)

This page contains basic instructions for getting started on the MS MARCO *passage* ranking task.
Note that there is a separate [MS MARCO *document* ranking task](experiments-msmarco-doc.md).

## Data Prep

We're going to use `msmarco-passage/` as the working directory.
First, we need to download and extract the MS MARCO passage dataset:

```
mkdir msmarco-passage

wget https://msmarco.blob.core.windows.net/msmarcoranking/collectionandqueries.tar.gz -P msmarco-passage
tar -xzvf msmarco-passage/collectionandqueries.tar.gz -C msmarco-passage
```

To confirm, `collectionandqueries.tar.gz` should have MD5 checksum of `31644046b18952c1386cd4564ba2ae69`.

Next, we need to convert the MS MARCO tsv collection into Anserini's jsonl files (which have one json object per line):

```
python ./src/main/python/msmarco/convert_collection_to_jsonl.py \
 --collection_path msmarco-passage/collection.tsv --output_folder msmarco-passage/collection_jsonl
```

The above script should generate 9 jsonl files in `msmarco-passage/collection_jsonl`, each with 1M lines (except for the last one, which should have 841,823 lines).

We can now index these docs as a `JsonCollection` using Anserini:

```
sh ./target/appassembler/bin/IndexCollection -collection JsonCollection \
 -generator LuceneDocumentGenerator -threads 9 -input msmarco-passage/collection_jsonl \
 -index msmarco-passage/lucene-index-msmarco -storePositions -storeDocvectors -storeRawDocs 
```

Upon completion, we should have an index with 8,841,823 documents.
The indexing speed may vary... on a modern desktop with an SSD, indexing takes less than two minutes.

## Retrieving and Evaluating the Dev set

Since queries of the set are too many (+100k), it would take a long time to retrieve all of them. To speed this up, we use only the queries that are in the qrels file: 

```
python ./src/main/python/msmarco/filter_queries.py --qrels msmarco-passage/qrels.dev.small.tsv \
 --queries msmarco-passage/queries.dev.tsv --output_queries msmarco-passage/queries.dev.small.tsv
```

The output queries file should contain 6980 lines.

We can now retrieve this smaller set of queries:

```
python ./src/main/python/msmarco/retrieve.py --hits 1000 --index msmarco-passage/lucene-index-msmarco \
 --qid_queries msmarco-passage/queries.dev.small.tsv --output msmarco-passage/run.dev.small.tsv
```

Note that by default, the above script uses BM25 with tuned parameters `k1=0.82`, `b=0.68` (more details below).
The option `-hits` specifies the of documents per query to be retrieved.
Thus, the output file should have approximately 6980 * 1000 = 6.9M lines. 

Retrieval speed will vary by machine:
On a modern desktop with an SSD, we can get ~0.06 s/query (taking about seven minutes).
Alternatively, we can run the same script implemented in Java, which is a bit faster:

```
./target/appassembler/bin/SearchMsmarco  -hits 1000 -index msmarco-passage/lucene-index-msmarco \
 -qid_queries msmarco-passage/queries.dev.small.tsv -output msmarco-passage/run.dev.small.tsv
```

Finally, we can evaluate the retrieved documents using this the official MS MARCO evaluation script: 

```
python ./src/main/python/msmarco/msmarco_eval.py \
 msmarco-passage/qrels.dev.small.tsv msmarco-passage/run.dev.small.tsv
```

And the output should be like this:

```
#####################
MRR @10: 0.18741227770955546
QueriesRanked: 6980
#####################
```

We can also use the official TREC evaluation tool, `trec_eval`, to compute other metrics than MRR@10. 
For that we first need to convert runs and qrels files to the TREC format:

```
python ./src/main/python/msmarco/convert_msmarco_to_trec_run.py \
 --input_run msmarco-passage/run.dev.small.tsv --output_run msmarco-passage/run.dev.small.trec

python ./src/main/python/msmarco/convert_msmarco_to_trec_qrels.py \
 --input_qrels msmarco-passage/qrels.dev.small.tsv --output_qrels msmarco-passage/qrels.dev.small.trec
```

And run the `trec_eval` tool:

```
./eval/trec_eval.9.0.4/trec_eval -c -mrecall.1000 -mmap \
 msmarco-passage/qrels.dev.small.trec msmarco-passage/run.dev.small.trec
```

The output should be:

```
map                   	all	0.1957
recall_1000           	all	0.8573
```

Average precision and recall@1000 are the two metrics we care about the most.

## BM25 Tuning

Note that this figure differs slightly from the value reported in [Document Expansion by Query Prediction](https://arxiv.org/abs/1904.08375), which uses the Anserini (system-wide) default of `k1=0.9`, `b=0.4`.

Tuning was accomplished with the `tune_bm25.py` script, using the queries found [here](https://github.com/castorini/Anserini-data/tree/master/MSMARCO).
There are five different sets of 10k samples (from the `shuf` command).
We tune on each individual set and then average parameter values across all five sets (this has the effect of regularization).
Note that we are currently optimizing recall@1000 since Anserini output will serve as input to later stage rerankers (e.g., based on BERT), and we want to maximize the number of relevant documents the rerankers have to work with.
The tuned parameters using this method are `k1=0.82`, `b=0.68`.

Here's the comparison between the Anserini default and tuned parameters:

Setting                     | MRR@10 | MAP    | Recall@1000 |
:---------------------------|-------:|-------:|------------:|
Default (`k1=0.9`, `b=0.4`) | 0.1840 | 0.1926 | 0.8526
Tuned (`k1=0.82`, `b=0.68`) | 0.1874 | 0.1957 | 0.8573

Anserini was upgraded to Lucene 8.0 as of commit [`75e36f9`](https://github.com/castorini/anserini/commit/75e36f97f7037d1ceb20fa9c91582eac5e974131) (6/12/2019); prior to that, the toolkit uses Lucene 7.6.
The above results are based on Lucene 8.0, but Lucene 7.6 results can be replicated with [v0.5.1](https://github.com/castorini/anserini/releases);
the effectiveness differences are very small.
For convenience, here are the effectiveness numbers with Lucene 7.6 (v0.5.1):

Setting                     | MRR@10 | MAP    | Recall@1000 |
:---------------------------|-------:|-------:|------------:|
Default (`k1=0.9`, `b=0.4`) | 0.1839 | 0.1925 | 0.8526
Tuned (`k1=0.82`, `b=0.72`) | 0.1875 | 0.1956 | 0.8578


## Document Expansion by Query Prediction: Doc2query

This section describes how to replicate the document expansion experiments in following paper:

+ Rodrigo Nogueira, Wei Yang, Jimmy Lin, Kyunghyun Cho. [Document Expansion by Query Prediction.](https://arxiv.org/abs/1904.08375) _arxiv:1904.08375_

For a complete "from scratch" replication (in particularly, training the seq2seq model), see [this code repo](https://github.com/nyu-dl/dl4ir-doc2query).
Here, we run through how to replicate the BM25+Doc2query condition with our copy of the predicted queries.

First, grab the predicted queries (i.e., document expansions):

```
wget https://www.dropbox.com/s/709q495d9hohcmh/pred-test_topk10.tar.gz -P msmarco-passage
tar -xzvf msmarco-passage/pred-test_topk10.tar.gz -C msmarco-passage
```

To confirm, `pred-test_topk10.tar.gz` should have an MD5 checksum of `241608d4d12a0bc595bed2aff0f56ea3`.

Check out the file:

```
$ wc msmarco-passage/pred-test_topk10.txt
 8841823 536446170 2962345659 msmarco-passage/pred-test_topk10.txt
```

These are the predicted queries based on our seq2seq model, based on top _k_ sampling with a beam size of 10.
There are as many lines in the above file as there are documents; all 10 predicted queries are concatenated on a single line.

Now let's create a new document collection by concatenating the predicted queries to the original documents:

```
python src/main/python/msmarco/augment_collection_with_predictions.py \
  --collection_path msmarco-passage/collection.tsv --output_folder msmarco-passage/collection_jsonl_expanded_topk10 \
  --predictions msmarco-passage/pred-test_topk10.txt --stride 1
```

We can then reindex the collection:

```
sh ./target/appassembler/bin/IndexCollection -collection JsonCollection \
 -generator LuceneDocumentGenerator -threads 9 -input msmarco-passage/collection_jsonl_expanded_topk10 \
 -index msmarco-passage/lucene-index-msmarco-expanded-topk10 -storePositions -storeDocvectors -storeRawDocs
```

And run retrieval (same as above):

```
python ./src/main/python/msmarco/retrieve.py --hits 1000 --index msmarco-passage/lucene-index-msmarco-expanded-topk10 \
 --qid_queries msmarco-passage/queries.dev.small.tsv --output msmarco-passage/run.dev.small.expanded-topk10.tsv
```

Finally, to evaluate:

```
python ./src/main/python/msmarco/msmarco_eval.py \
 msmarco-passage/qrels.dev.small.tsv msmarco-passage/run.dev.small.expanded-topk10.tsv
```

The output should be:

```
#####################
MRR @10: 0.2213412471005586
QueriesRanked: 6980
#####################
```

Note that these figures are slightly higher than the values reported in our arXiv paper (0.218) due to BM25 parameter tuning (see above) and an upgrade from Lucene 7.6 to Lucene 8.0 (experiments in the paper were run with Lucene 7.6).

One additional trick not explored in our arXiv paper is to weight the original document and predicted queries differently.
The `augment_collection_with_predictions.py` script provides an option `--original_copies` that duplicates the original text _n_ times, which is an easy way to weight the original document by _n_.
For example `--original_copies 2` would yield the following results:

```
#####################
MRR @10: 0.2287041774685029
QueriesRanked: 6980
#####################
```

So, this simple trick improves MRR by a bit over baseline Doc2query.
