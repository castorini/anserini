# Anserini: doc2query Experiments

This page describes how to replicate the doc2query document expansion experiments in the following paper:

+ Rodrigo Nogueira, Wei Yang, Jimmy Lin, Kyunghyun Cho. [Document Expansion by Query Prediction.](https://arxiv.org/abs/1904.08375) _arxiv:1904.08375_

The basic idea is to train a model, that when given an input document, generates questions that the document might answer (or more broadly, queries for which the document might be relevant).
These predicted questions (or queries) are then appended to the original documents, which are then indexed as before.

For a complete "from scratch" replication (in particularly, training the seq2seq model), see [this code repo](https://github.com/nyu-dl/dl4ir-doc2query).
Here, we run through how to replicate the BM25+doc2query condition with our copy of the predicted queries.

Note that [docTTTTTquery](experiments-docTTTTTquery.md) is an improved version of the doc2query model and has largely superseded this model.
However, these results remain useful as a baseline.

Here's a summary of the datasets referenced in this guide:

File | Size | MD5 | Download
:----|-----:|:----|:-----
`msmarco-passage-pred-test_topk10.tar.gz` | 764 MB | `241608d4d12a0bc595bed2aff0f56ea3` | [[Dropbox](https://www.dropbox.com/s/57g2s9vhthoewty/msmarco-passage-pred-test_topk10.tar.gz?dl=1)] [[GitLab](https://git.uwaterloo.ca/jimmylin/doc2query-data/raw/master/base/msmarco-passage-pred-test_topk10.tar.gz)]
`paragraphCorpus.v2.0.tar.xz` | 4.7 GB | `a404e9256d763ddcacc3da1e34de466a` | [[Dropbox](https://www.dropbox.com/s/1xq559k5i86gk17/paragraphCorpus.v2.0.tar.xz?dl=1)] [[GitLab](https://git.uwaterloo.ca/jimmylin/doc2query-data/raw/master/base/paragraphCorpus.v2.0.tar.xz)]
`trec-car-pred-test_topk10.tar.gz` | 2.7 GB | `b9f98b55e6260c64e830b34d80a7afd7` | [[Dropbox](https://www.dropbox.com/s/rl4r0md0xgxg7d9/trec-car-pred-test_topk10.tar.gz?dl=1)] [[GitLab](https://git.uwaterloo.ca/jimmylin/doc2query-data/raw/master/base/trec-car-pred-test_topk10.tar.gz)]

The GitLab repo is [here](https://git.uwaterloo.ca/jimmylin/doc2query-data/) if you want direct access.

## MS MARCO Passage Ranking

To replicate our doc2query results on the [MS MARCO Passage Ranking Task](https://github.com/microsoft/MSMARCO-Passage-Ranking), follow these instructions.
Before going through this guide, it is recommended that you [replicate our BM25 baselines](experiments-msmarco-passage.md) first.

To start, grab the predicted queries:

```bash
# Grab tarball from either one of two sources:
wget https://www.dropbox.com/s/57g2s9vhthoewty/msmarco-passage-pred-test_topk10.tar.gz -P collections/msmarco-passage
wget https://git.uwaterloo.ca/jimmylin/doc2query-data/raw/master/base/msmarco-passage-pred-test_topk10.tar.gz -P collections/msmarco-passage

# Unpack tarball:
tar -xzvf collections/msmarco-passage/msmarco-passage-pred-test_topk10.tar.gz -C collections/msmarco-passage
```

Check out the file:

```bash
$ wc collections/msmarco-passage/pred-test_topk10.txt
  8841823 536425855 2962345659 collections/msmarco-passage/pred-test_topk10.txt
```

These are the predicted queries based on our seq2seq model, based on top _k_ sampling with 10 samples for each document in the corpus.
There are as many lines in the above file as there are documents; all 10 predicted queries are concatenated on a single line.

Now let's create a new document collection by concatenating the predicted queries to the original documents:

```
python tools/scripts/msmarco/augment_collection_with_predictions.py \
 --collection-path collections/msmarco-passage/collection.tsv \
 --output-folder collections/msmarco-passage/collection_jsonl_expanded_topk10 \
 --predictions collections/msmarco-passage/pred-test_topk10.txt --stride 1
```

To verify (and to track progress), the above script will generate a total of 9 JSON files, `docs00.json` to `docs08.json`.
After the script completes, we can index the expanded documents:

```
sh target/appassembler/bin/IndexCollection -collection JsonCollection \
 -generator DefaultLuceneDocumentGenerator -threads 9 \
 -input collections/msmarco-passage/collection_jsonl_expanded_topk10 \
 -index indexes/msmarco-passage/lucene-index-msmarco-expanded-topk10 \
 -storePositions -storeDocvectors -storeRaw
```

And perform retrieval:

```
python tools/scripts/msmarco/retrieve.py --hits 1000 \
 --index indexes/msmarco-passage/lucene-index-msmarco-expanded-topk10 \
 --queries collections/msmarco-passage/queries.dev.small.tsv \
 --output runs/run.msmarco-passage.dev.small.expanded-topk10.tsv
```

Alternatively, we can use the Java implementation of the above script, which is faster (taking advantage of multi-threaded retrieval with the `-threads` option):

```
sh target/appassembler/bin/SearchMsmarco  -hits 1000 -threads 8 \
 -index indexes/msmarco-passage/lucene-index-msmarco-expanded-topk10 \
 -queries collections/msmarco-passage/queries.dev.small.tsv \
 -output runs/run.msmarco-passage.dev.small.expanded-topk10.tsv
```

Finally, to evaluate:

```
python tools/scripts/msmarco/msmarco_passage_eval.py \
 collections/msmarco-passage/qrels.dev.small.tsv runs/run.msmarco-passage.dev.small.expanded-topk10.tsv
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
The `augment_collection_with_predictions.py` script provides an option `--original-copies` that duplicates the original text _n_ times, which is an easy way to weight the original document by _n_.
For example `--original-copies 2` would yield the following results:

```
#####################
MRR @10: 0.2287041774685029
QueriesRanked: 6980
#####################
```

So, this simple trick improves MRR by a bit over baseline doc2query.

## TREC CAR

We will now describe how to reproduce the TREC CAR results of our model BM25+doc2query presented in the paper.

To start, download the TREC CAR dataset and the predicted queries:

```bash
mkdir collections/trec_car

# Grab tarballs from either one of two sources:
wget https://www.dropbox.com/s/1xq559k5i86gk17/paragraphCorpus.v2.0.tar.xz -P collections/trec_car
wget https://git.uwaterloo.ca/jimmylin/doc2query-data/raw/master/base/paragraphCorpus.v2.0.tar.xz -P collections/trec_car

wget https://www.dropbox.com/s/rl4r0md0xgxg7d9/trec-car-pred-test_topk10.tar.gz -P collections/trec_car
wget https://git.uwaterloo.ca/jimmylin/doc2query-data/raw/master/base/trec-car-pred-test_topk10.tar.gz -P collections/trec_car

# Unpack tarballs:
tar -xf collections/trec_car/paragraphCorpus.v2.0.tar.xz -C collections/trec_car
tar -xf collections/trec_car/trec-car-pred-test_topk10.tar.gz -C collections/trec_car
```

Check out the file:

```bash
$ wc collections/trec_car/pred-test_topk10.txt
 29794697 1767258740 11103530216 collections/trec_car/pred-test_topk10.txt
```

These are the predicted queries based on our seq2seq model, based on top _k_ sampling with 10 samples for each document in the corpus.
There are as many lines in the above file as there are documents; all 10 predicted queries are concatenated on a single line.

Now let's create a new document collection by concatenating the predicted queries to the original documents:

```
python src/main/python/trec_car/augment_collection_with_predictions.py \
 --collection-path collections/trec_car/paragraphCorpus/dedup.articles-paragraphs.cbor \
 --output-folder collections/trec_car/collection_jsonl_expanded_topk10 \
 --predictions collections/trec_car/pred-test_topk10.txt --stride 1
```

To verify (and to track progress), the above script will generate a total of 30 JSON files, `docs00.json` to `docs29.json`.
After the script completes, we can index the expanded documents:

```
sh target/appassembler/bin/IndexCollection -collection JsonCollection \
 -generator DefaultLuceneDocumentGenerator -threads 30 \
 -input collections/trec_car/collection_jsonl_expanded_topk10 \
 -index indexes/trec_car/lucene-index.car17v2.0-expanded-topk10
```

And perform retrieval on the test queries:

```
sh target/appassembler/bin/SearchCollection -topicreader Car \
 -index indexes/trec_car/lucene-index.car17v2.0-expanded-topk10 \
 -topics src/main/resources/topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt \
 -output runs/run.car17v2.0.bm25.expanded-topk10.txt -bm25
```

Evaluation is performed with `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank \
 src/main/resources/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt \
 runs/run.car17v2.0.bm25.expanded-topk10.txt
```

With the above commands, you should be able to replicate the following results:

```
map                   	all	0.1807
recip_rank            	all	0.2750
```

Note that this MAP is slightly higher than the arXiv paper (0.178) because we used
TREC CAR corpus v2.0 in this experiment instead of corpus v1.5 used in the paper.

## Replication Log

+ Results replicated by [@justram](https://github.com/justram) on 2019-08-09 (commit [`5f098f`](https://github.com/justram/Anserini/commit/5f098f23527611bca1224149bc2d155adce1e48))
+ Results replicated by [@ronakice](https://github.com/ronakice) on 2019-08-13 (commit [`5b29d16`](https://github.com/castorini/anserini/commit/5b29d1654abc5e8a014c2230da990ab2f91fb340))
+ Results replicated by [@edwinzhng](https://github.com/edwinzhng) on 2020-01-08 (commit [`5cc923d`](https://github.com/castorini/anserini/commit/5cc923d5c02777d8b25df32ff2e2a59be5badfdd))
+ Results replicated by [@HangCui0510](https://github.com/HangCui0510) on 2020-04-23 (commit [`0ae567d`](https://github.com/castorini/anserini/commit/0ae567df5c8a70ac211efd958c9ca1ff609ff782))
+ Results replicated by [@kelvin-jiang](https://github.com/kelvin-jiang) on 2020-05-25 (commit [`b6e0367`](https://github.com/castorini/anserini/commit/b6e0367ef4e2b4fce9d81c8397ef1188e35971e7))
+ Results replicated by [@lintool](https://github.com/lintool) on 2020-11-09 (commit [`94eae4`](https://github.com/castorini/anserini/commit/94eae4e06678446954446f2d47dae1666efe134f))
