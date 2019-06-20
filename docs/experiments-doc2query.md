# Anserini: Doc2query Experiments

This page describes how to replicate the document expansion experiments in following paper:

+ Rodrigo Nogueira, Wei Yang, Jimmy Lin, Kyunghyun Cho. [Document Expansion by Query Prediction.](https://arxiv.org/abs/1904.08375) _arxiv:1904.08375_

For a complete "from scratch" replication (in particularly, training the seq2seq model), see [this code repo](https://github.com/nyu-dl/dl4ir-doc2query).
Here, we run through how to replicate the BM25+Doc2query condition with our copy of the predicted queries.

## MS MARCO Passage Ranking

To replicate our Doc2query results on the [MS MARCO Passage Ranking Task](https://github.com/microsoft/MSMARCO-Passage-Ranking), follow these instructions.
Before going through this guide, it might make sense to [replicate our BM25 baselines](experiments-msmarco-passage.md) first.

To start, grab the predicted queries (i.e., document expansions):

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

## TREC CAR

Instructions coming soon!

