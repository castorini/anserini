# Anserini: Experiments on [Open Research](https://api.semanticscholar.org/corpus/)

This document describes the steps to reproduce the BM25 results from [Bhagavatula et. al (2018)](https://arxiv.org/pdf/1802.08301.pdf)
in Anserini.

## Data Prep

First, we need to download and extract the OpenResearch dataset (2017-02-21):

```
DATA_DIR=./openresearch_data
mkdir ${DATA_DIR}

wget https://s3-us-west-2.amazonaws.com/ai2-s2-research-public/open-corpus-archive/2017-02-21/papers-2017-02-21.zip -P ${DATA_DIR}
unzip ${DATA_DIR}/papers-2017-02-21.zip -d ${DATA_DIR}
```

To confirm, `papers-2017-02-21.json.gz` should have MD5 checksum of `f35c40992e94b458db73fa030a79844b`

Next, we need to convert the OpenResearch jsonlines collection into Anserini's format:

```
python ./src/main/python/openresearch/convert_openresearch_to_anserini_format.py \
  --output_folder=${DATA_DIR}/anserini_format \
  --collection_path=${DATA_DIR} \
  --train_fraction=0.8 \
  --max_docs_per_file=1000000 \
  --use_abstract_in_query
```

The above script should generate 8 jsonl files in `${DATA_DIR}/anserini_format`, each with 1M lines (except for the last one, which should have 210,983 lines).
It should also produce training, dev, and test files for queries and qrels (which contains pairs of query id and relevant docs).  
The option `use_abstract_in_query` enables us to use both title and abstract as queries. 

We can now index these docs as a `JsonCollection` using Anserini:

```
sh ./target/appassembler/bin/IndexCollection -collection JsonCollection \
 -generator LuceneDocumentGenerator -threads 8 -input ${DATA_DIR}/anserini_format/corpus \
 -index ${DATA_DIR}/lucene-index-openresearch -optimize -storePositions -storeDocvectors -storeRawDocs 
```

The output message should be something like this:

```
2019-06-05 21:22:18,827 INFO  [main] index.IndexCollection (IndexCollection.java:615) - Total 7,210,983 documents indexed in 00:04:53
```

Your speed may vary... with a modern desktop machine with an SSD, indexing takes around a minute.

**Optional:** To further replicate the result presented in [Bhagavatula et. al (2018)](https://arxiv.org/pdf/1802.08301.pdf), we could use `key_terms_from_text` method presented in [whoosh](https://whoosh.readthedocs.io/en/latest/). For that purpose, we need to generate whoosh's own index:

```
python ./src/main/python/openresearch/convert_openresearch_to_whoosh_index.py \
  --collection_path=${DATA_DIR} \
  --whoosh_index=${DATA_DIR}/whoosh_index
```
It may take a few hours.

## Retrieving and Evaluating the Test set

Since there are too many queries in the test set (250K), it would take a long time to retrieve all of them. To speed this up, we cap this set by selecting at random 20K queries:

```
shuf -n 20000 ${DATA_DIR}/anserini_format/queries.test.tsv --output ${DATA_DIR}/anserini_format/queries.small.test.tsv
```

We can now retrieve this smaller set of queries:

```
python ./src/main/python/openresearch/retrieve.py \
  --index ${DATA_DIR}/lucene-index-openresearch \
  --qid_queries ${DATA_DIR}/anserini_format/queries.small.test.tsv \
  --output ${DATA_DIR}/anserini_format/run.small.test \
  --hits 1000
```

or, if we would like to use key terms as query:

```
python ./src/main/python/openresearch/retrieve_with_key_terms.py \
  --index ${DATA_DIR}/lucene-index-openresearch \
  --qid_queries ${DATA_DIR}/anserini_format/queries.small.test.tsv \
  --output ${DATA_DIR}/anserini_format/run.small.test \
  --hits 1000 \
  --whoosh_index ${DATA_DIR}/whoosh_index
```


Retrieval speed will vary by machine:
On a modern desktop with an SSD, we can get ~0.04 per query (taking about five minutes) if using only title as query, ~1.5 for title and abstract, ~0.3 for key terms.
On a slower machine with mechanical disks, the entire process might take as long as a couple of hours.

The option `-hits` specifies the of documents per query to be retrieved.
Thus, the output file should have approximately 20,000 * 1,000 = 20M lines. 

Finally, we can evaluate the retrieved documents using the official TREC evaluation script: 

```
./eval/trec_eval.9.0.4/trec_eval -mrecip_rank -mmap -mrecall.20,1000 -mP.20  \
 ${DATA_DIR}/anserini_format/qrels.test ${DATA_DIR}/anserini_format/run.small.test
```


The output of only using title as query should be:

```
map                   	all	0.0401
recip_rank            	all	0.2448
P_20                  	all	0.0539
recall_20             	all	0.0786
recall_1000           	all	0.2866
```

The output of using the concatenation of title and abstract as query should be:  

```
map                   	all	0.0626
recip_rank            	all	0.3512
P_20                  	all	0.0811
recall_20             	all	0.1132
recall_1000           	all	0.3628
```
The output of using key terms in title and abstract as query should be:

```
map                     all 0.0528
recip_rank              all 0.2202
P_20                    all 0.0428
recall_20               all 0.1022
recall_1000             all 0.3344
```


The table below compares our BM25 results against Bhagavatula's et. al (2018):

|                                 | F1@20 |  MRR  |
|----------|:-------------:|------:|
| BM25 (Bhagavatula et. al, 2018) | 0.058 | 0.218 |
| BM25 (Anserini, Ours, title)    | 0.063 | 0.244 |
| BM25 (Anserini, Ours, title+abstract)| 0.095 | 0.351 |
| BM25 (Anserini, Ours, key terms)| 0.060 | 0.220 |


## Extra Baseline on PubMed and DBLP

### PubMed and DBLP dataset

Follow [citeomatic's repo](https://github.com/allenai/citeomatic/tree/44dc210c82515b5d4c5a96f5aafcb9b6e48206af) to download the necessary data.

The steps are similar to run baseline on OpenResearch, to run all three experiments on PubMed and DBLP quickly, run

`./src/main/python/openresearch/run_pubmed_dblp.sh -citeomatic_data <YOUR CITEOMATIC_DATA_ROOT> -output_folder <YOUR_OUTPUT_FOLDER>`

The results are as follows:

The output of using PubMed title as query

```
map                     all     0.1615
recip_rank              all     0.5844
P_20                    all     0.2034
recall_20               all     0.1954
recall_1000             all     0.6536
f1_20                   all     0.199
```

The output of using PubMed key terms from title and abstract as query

```
map                     all     0.1637
recip_rank              all     0.5953
P_20                    all     0.2058
recall_20               all     0.1969
recall_1000             all     0.6041
f1_20                   all     0.201
```

The output of using PubMed title + abstract as query

```
map                     all     0.2361
recip_rank              all     0.7208
P_20                    all     0.2726
recall_20               all     0.2632
recall_1000             all     0.7649
f1_20                   all     0.268
```

The output of using DBLP title as query

```
map                     all     0.1056
recip_rank              all     0.4244
P_20                    all     0.1090
recall_20               all     0.1721
recall_1000             all     0.5511
f1_20                   all     0.133
```

The output of using DBLP key terms from title and abstract as query

```
map                     all     0.1015
recip_rank              all     0.4254
P_20                    all     0.1059
recall_20               all     0.1669
recall_1000             all     0.5099
f1_20                   all     0.130
```

The output of using DBLP title + abstract as query

```
map                     all     0.1687
recip_rank              all     0.5851
P_20                    all     0.1586
recall_20               all     0.2511
recall_1000             all     0.6913
f1_20                   all     0.194
```

The table below compares our BM25 results against Bhagavatula's et. al (2018):

**PubMed**

|                                 | F1@20 |  MRR  |
|----------|:-------------:|------:|
| BM25 (Bhagavatula et. al, 2018) | 0.209 | 0.574 |
| BM25 (Anserini, Ours, title)    | 0.199 | 0.584 |
| BM25 (Anserini, Ours, key terms)| 0.201 | 0.595 |
| BM25 (Anserini, Ours, title+abstract)| 0.268 | 0.720|

**DBLP**

|                                 | F1@20 |  MRR  |
|----------|:-------------:|------:|
| BM25 (Bhagavatula et. al, 2018) | 0.119 | 0.425 |
| BM25 (Anserini, Ours, title)    | 0.133 | 0.424 |
| BM25 (Anserini, Ours, key terms)| 0.130 | 0.425 |
| BM25 (Anserini, Ours, title+abstract)| 0.194 | 0.585 |
