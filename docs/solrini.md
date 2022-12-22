# Solrini: Anserini Integration with Solr

This page documents code for reproducing results from the following paper:

> Ryan Clancy, Toke Eskildsen, Nick Ruest, and Jimmy Lin. [Solr Integration in the Anserini Information Retrieval Toolkit.](https://cs.uwaterloo.ca/~jimmylin/publications/Clancy_etal_SIGIR2019a.pdf) _Proceedings of the 42nd Annual International ACM SIGIR Conference on Research and Development in Information Retrieval (SIGIR 2019)_, July 2019, Paris, France.

We provide instructions for setting up a single-node SolrCloud instance running locally and indexing into it from Anserini.
Instructions for setting up SolrCloud clusters can be found by searching the web.

## Setting up a Single-Node SolrCloud Instance

Download Solr version 8.11.2 (binary release) from [here](https://solr.apache.org/downloads.html) and extract the archive:

```bash
mkdir solrini && tar -zxvf solr*.tgz -C solrini --strip-components=1
```

Solr 8.11.2 is the last release in the 8.x series, and unfortunately, these instructions do not work for Solr 9.x.

Start Solr:

```bash
solrini/bin/solr start -c -m 16G
```

When you're done, remember to stop Solr:

```bash
solrini/bin/solr stop
```

Adjust memory usage (i.e., `-m 16G` as appropriate).

Run the Solr bootstrap script to copy the Anserini JAR into Solr's classpath and upload the configsets to Solr's internal ZooKeeper:

```bash
pushd src/main/resources/solr && ./solr.sh ../../../../solrini localhost:9983 && popd
```

Solr should now be available at [http://localhost:8983/](http://localhost:8983/) for browsing.

The Solr index schema can also be modified using the [Schema API](https://lucene.apache.org/solr/guide/8_3/schema-api.html). This is useful for specifying field types and other properties including multiValued fields.
Schemas for setting up specific Solr index schemas can be found in the [src/main/resources/solr/schemas/](../src/main/resources/solr/schemas/) folder.
To set the schema, we can make a request to the Schema API:

```bash
curl -X POST -H 'Content-type:application/json' \
  --data-binary @src/main/resources/solr/schemas/SCHEMA_NAME.json \
  http://localhost:8983/solr/COLLECTION_NAME/schema
```

For Robust04 example below, this isn't necessary.

## Indexing into SolrCloud from Anserini

We can use Anserini as a common "front-end" for indexing into SolrCloud, thus supporting the same range of test collections that's already included in Anserini (when directly building local Lucene indexes).
Indexing into Solr is similar indexing to disk with Lucene, with a few added parameters.
Most notably, we replace the `-index` parameter (which specifies the Lucene index path on disk) with Solr parameters.
Alternatively, Solr can also be configured to read pre-built Lucene indexes, since Solr uses Lucene indexes under the hood (more details below).

We'll index [Robust04](regressions-disk45.md) as an example.
First, create the `robust04` collection in Solr:

```bash
solrini/bin/solr create -n anserini -c robust04
```

Run the Solr indexing command for `robust04`:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection TrecCollection \
  -input /path/to/disk45 \
  -generator DefaultLuceneDocumentGenerator \
  -solr \
  -solr.index robust04 \
  -solr.zkUrl localhost:9983 \
  -threads 8 \
  -storePositions -storeDocvectors -storeRaw
```

Make sure `/path/to/disk45` is updated with the appropriate path for the Robust04 collection.

Once indexing has completed, you should be able to query `robust04` from the Solr [query interface](http://localhost:8983/solr/#/robust04/query).
You can also run the following command to reproduce Anserini BM25 retrieval:

```bash
sh target/appassembler/bin/SearchSolr \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -topicreader Trec \
  -solr.index robust04 \
  -solr.zkUrl localhost:9983 \
  -output runs/run.solr.robust04.bm25.topics.robust04.txt
```

Evaluation can be performed using `trec_eval`:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 \
    src/main/resources/topics-and-qrels/qrels.robust04.txt \
    runs/run.solr.robust04.bm25.topics.robust04.txt

map                   	all	0.2531
P_30                  	all	0.3102
```

Solrini has also been verified to work with following collections as well:

+ [TREC Washington Post Corpus](regressions-core18.md)
+ [MS MARCO passage ranking task](experiments-msmarco-passage.md)
+ [MS MARCO document ranking task](regressions-msmarco-doc.md)

See `run_solr_regression.py` regression script for more details.

## Solr with a Pre-built Lucene Index

It is possible for Solr to read pre-built Lucene indexes.
To achieve this, some housekeeping is required to "install" the pre-built indexes.
The following uses [Robust04](regressions-disk45.md) as an example. 
Let's assume the pre-built index is stored at `indexes/lucene-index.disk45/`.

First, a Solr collection must be created to house the index.
Here, we create a collection `robust04` with configset `anserini`.

```bash
solrini/bin/solr create -n anserini -c robust04
```

Along with the collection, Solr will create a core instance, whose name can be found in the Solr UI under collection overview.
It'll look something like `<collection_name>_shard<id>_replica_<id>` (e.g., `robust04_shard1_replica_n1`).
Solr stores configurations and data for the core instances under Solr home, which for us is `solrini/server/solr/` by default.

Second, make proper Solr schema adjustments if necessary.
Here, `robust04` is a TREC collection whose schema is already handled by [managed-schema](https://github.com/castorini/anserini/blob/master/src/main/resources/solr/anserini/conf/managed-schema) in the Solr configset.
However, for a collection such as `cord19`, remember to make proper adjustments to the Solr schema (also see above):

```bash
curl -X POST -H 'Content-type:application/json' \
  --data-binary @src/main/resources/solr/schemas/SCHEMA_NAME.json \
  http://localhost:8983/solr/COLLECTION_NAME/schema
```

Finally, we can copy the pre-built index to the local where Solr expects them.
Start by removing data that's there:

```bash
rm solrini/server/solr/robust04_shard1_replica_n1/data/index/*
```

Then, simply copy the pre-built Lucene indexes into that location:

```bash
cp indexes/lucene-index.disk45/* solrini/server/solr/robust04_shard1_replica_n1/data/index
```

Restart Solr to make sure changes take effect:

```bash
solrini/bin/solr stop
solrini/bin/solr start -c -m 16G
```

You can confirm that everything works by performing a retrieval run and checking the results (see above).

## Solr integration test

We have an end-to-end integration testing script `run_solr_regression.py`.
See example usage for [Robust04](regressions-disk45.md) below:

```bash
# Check if Solr server is on
python src/main/python/run_solr_regression.py --ping

# Check if robust04 exists
python src/main/python/run_solr_regression.py --check-index-exists robust04

# Create robust04 if it does not exist
python src/main/python/run_solr_regression.py --create-index robust04

# Delete robust04 if it exists
python src/main/python/run_solr_regression.py --delete-index robust04

# Insert documents from /path/to/disk45 into robust04
python src/main/python/run_solr_regression.py --insert-docs robust04 --input /path/to/disk45

# Search and evaluate on robust04
python src/main/python/run_solr_regression.py --evaluate robust04
```

To run end-to-end, issue the following command:

```bash
python src/main/python/run_solr_regression.py --regression robust04 --input /path/to/disk45
```

The regression script has been verified to work for [`robust04`](regressions-disk45.md), [`core18`](regressions-core18.md), [`msmarco-passage`](experiments-msmarco-passage.md), [`msmarco-doc`](regressions-msmarco-doc.md).

## Reproduction Log[*](reproducibility.md)

+ Results reproduced by [@nikhilro](https://github.com/nikhilro) on 2020-01-26 (commit [`1882d84`](https://github.com/castorini/anserini/commit/1882d84236b13cd4673d2d8fa91003438eea2d82)) for both [Washington Post](regressions-core18.md) and [Robust04](regressions-disk45.md)
+ Results reproduced by [@edwinzhng](https://github.com/edwinzhng) on 2020-01-28 (commit [`a79cb62`](https://github.com/castorini/anserini/commit/a79cb62a57a059113a6c3b1523b582b89dccf0a1)) for both [Washington Post](regressions-core18.md) and [Robust04](regressions-disk45.md)
+ Results reproduced by [@nikhilro](https://github.com/nikhilro) on 2020-02-12 (commit [`eff7755`](https://github.com/castorini/anserini/commit/eff7755a611bd20ee1d63ac0167f5c8f38cd3074)) for [Washington Post `core18`](regressions-core18.md), [Robust04 `robust04`](regressions-disk45.md), and [MS Marco Passage `msmarco-passage`](regressions-msmarco-passage.md) using end-to-end [`run_solr_regression`](../src/main/python/run_solr_regression.py)
+ Results reproduced by [@HangCui0510](https://github.com/HangCui0510) on 2020-04-29 (commit [`31d843a`](https://github.com/castorini/anserini/commit/31d843a6073bfd7eff7e326f543e3f11845df7fa)) for [MS Marco Passage `msmarco-passage`](regressions-msmarco-passage.md) using end-to-end [`run_solr_regression`](../src/main/python/run_solr_regression.py)
+ Results reproduced by [@shaneding](https://github.com/shaneding) on 2020-05-26 (commit [`bed8ead`](https://github.com/castorini/anserini/commit/bed8eadad5f2ba859a2ddd2801db4aaeb3c81485)) for [MS Marco Passage `msmarco-passage`](regressions-msmarco-passage.md) using end-to-end [`run_solr_regression`](../src/main/python/run_solr_regression.py)
+ Results reproduced by [@YimingDou](https://github.com/YimingDou) on 2020-05-29 (commit [`2947a16`](https://github.com/castorini/anserini/commit/2947a1622efae35637b83e321aba8e6fccd43489)) for [MS MARCO Passage `msmarco-passage`](regressions-msmarco-passage.md)
+ Results reproduced by [@adamyy](https://github.com/adamyy) on 2020-05-29 (commit [`2947a16`](https://github.com/castorini/anserini/commit/2947a1622efae35637b83e321aba8e6fccd43489)) for [MS Marco Passage `msmarco-passage`](regressions-msmarco-passage.md) and [MS Marco Document `msmarco-doc`](regressions-msmarco-doc.md) using end-to-end [`run_solr_regression`](../src/main/python/run_solr_regression.py)
+ Results reproduced by [@yxzhu16](https://github.com/yxzhu16) on 2020-07-17 (commit [`fad12be`](https://github.com/castorini/anserini/commit/fad12be2e37a075100707c3a674eb67bc0aa57ef)) for [Robust04 `robust04`](regressions-disk45.md), [Washington Post `core18`](regressions-core18.md), and [MS Marco Passage `msmarco-passage`](regressions-msmarco-passage.md) using end-to-end [`run_solr_regression`](../src/main/python/run_solr_regression.py)
+ Results reproduced by [@lintool](https://github.com/lintool) on 2020-11-10 (commit [`e19755b`](https://github.com/castorini/anserini/commit/e19755b5fa976127830597bc9fbca203b9f5ad24)), all commands and end-to-end regression script for all four collections
+ Results reproduced by [@jrzhang12](https://github.com/jrzhang12) on 2021-01-10 (commit [`be4e44d`](https://github.com/castorini/anserini/commit/02c52ee606ba0ebe32c130af1e26d24d8f10566a)) for [MS MARCO Passage](regressions-msmarco-passage.md)
+ Results reproduced by [@tyao-t](https://github.com/tyao-t) on 2021-01-13 (commit [`a62aca0`](https://github.com/castorini/anserini/commit/a62aca06c1603617207c1c148133de0f90f24738)) for [MS MARCO Passage](regressions-msmarco-passage.md) and [MS MARCO Document](regressions-msmarco-doc.md)
+ Results reproduced by [@d1shs0ap](https://github.com/d1shs0ap) on 2022-01-21 (commit [`a81299e`](https://github.com/castorini/anserini/commit/a81299e59eff24512d635e0d49fba6e373286469)) for [MS MARCO Document](regressions-msmarco-doc.md) using end-to-end [`run_solr_regression`](../src/main/python/run_solr_regression.py)
+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-03-21 (commit [`3d1fc34`](https://github.com/castorini/anserini/commit/3d1fc3457b993832b4682c0482b26d8271d02ec6)) for all collections
+ Results reproduced by [@lintool](https://github.com/lintool) on 2022-07-31 (commit [`2a0cb16`](https://github.com/castorini/anserini/commit/2a0cb16829b347e38801b9972b349de498dadf03)) (v0.14.4) for all collections
