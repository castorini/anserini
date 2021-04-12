# Solrini: Anserini Integration with Solr

This page documents code for replicating results from the following paper:

+ Ryan Clancy, Toke Eskildsen, Nick Ruest, and Jimmy Lin. [Solr Integration in the Anserini Information Retrieval Toolkit.](https://cs.uwaterloo.ca/~jimmylin/publications/Clancy_etal_SIGIR2019a.pdf) _Proceedings of the 42nd Annual International ACM SIGIR Conference on Research and Development in Information Retrieval (SIGIR 2019)_, July 2019, Paris, France.

We provide instructions for setting up a single-node SolrCloud instance running locally and indexing into it from Anserini.
Instructions for setting up SolrCloud clusters can be found by searching the web.

## Setting up a Single-Node SolrCloud Instance

From the Solr [archives](https://archive.apache.org/dist/lucene/solr/), download the Solr (non `-src`) version that matches Anserini's [Lucene version](https://github.com/castorini/anserini/blob/master/pom.xml#L36) to the `anserini/` directory.

Extract the archive:

```
mkdir solrini && tar -zxvf solr*.tgz -C solrini --strip-components=1
```

Start Solr:

```
solrini/bin/solr start -c -m 8G
```

Adjust memory usage (i.e., `-m 8G` as appropriate).

Run the Solr bootstrap script to copy the Anserini JAR into Solr's classpath and upload the configsets to Solr's internal ZooKeeper:

```
pushd src/main/resources/solr && ./solr.sh ../../../../solrini localhost:9983 && popd
```

Solr should now be available at [http://localhost:8983/](http://localhost:8983/) for browsing.

The Solr index schema can also be modified using the [Schema API](https://lucene.apache.org/solr/guide/8_3/schema-api.html). This is useful for specifying field types and other properties including multiValued fields.

Schemas for setting up specific Solr index schemas can be found in the [src/main/resources/solr/schemas/](../src/main/resources/solr/schemas/) folder.

To set the schema, we can make a request to the Schema API:

```
curl -X POST -H 'Content-type:application/json' --data-binary @src/main/resources/solr/schemas/SCHEMA_NAME.json http://localhost:8983/solr/COLLECTION_NAME/schema
```

For Robust04 example below, this isn't necessary.

## Indexing into SolrCloud from Anserini

We can use Anserini as a common "front-end" for indexing into SolrCloud, thus supporting the same range of test collections that's already included in Anserini (when directly building local Lucene indexes).
Indexing into Solr is similar indexing to disk with Lucene, with a few added parameters.
Most notably, we replace the `-index` parameter (which specifies the Lucene index path on disk) with Solr parameters.
Alternatively, Solr can also be configured to [read prebuilt Lucene index](#solr-with-prebuilt-lucene-index), since Solr uses Lucene indexes under the hood.

We'll index [Robust04](regressions-robust04.md) as an example.
First, create the `robust04` collection in Solr:

```
solrini/bin/solr create -n anserini -c robust04
```

Run the Solr indexing command for `robust04`:

```
sh target/appassembler/bin/IndexCollection -collection TrecCollection -generator DefaultLuceneDocumentGenerator \
  -threads 8 -input /path/to/disk45 \
  -solr -solr.index robust04 -solr.zkUrl localhost:9983 \
  -storePositions -storeDocvectors -storeRaw
```

Make sure `/path/to/disk45` is updated with the appropriate path for the Robust04 collection.

Once indexing has completed, you should be able to query `robust04` from the Solr [query interface](http://localhost:8983/solr/#/robust04/query).

You can also run the following command to replicate Anserini BM25 retrieval:

```
sh target/appassembler/bin/SearchSolr -topicreader Trec \
  -solr.index robust04 -solr.zkUrl localhost:9983 \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -output runs/run.solr.robust04.bm25.topics.robust04.txt
```

Evaluation can be performed using `trec_eval`:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt runs/run.solr.robust04.bm25.topics.robust04.txt
map                   	all	0.2531
P_30                  	all	0.3102
```

Solrini has also been verified to work with following collections as well:

+ [TREC Washington Post Corpus](regressions-core18.md)
+ [MS MARCO Passage Retrieval Corpus](experiments-msmarco-passage.md)
+ [MS MARCO document](regressions-msmarco-doc.md)

See `run_solr_regression.py` regression script for more details.

## Solr with a Pre-built Lucene Index

It is possible for Solr to read pre-built Lucene indexes.
To achieve this, some housekeeping is required to "install" the pre-built indexes.
The following uses [Robust04](regressions-robust04.md) as an example. 
Let's assume the pre-built index is stored at `indexes/lucene-index.robust04.pos+docvectors+raw/`.

First, a Solr collection must be created to house the index.
Here, we create a collection `robust04` with configset `anserini`.

```
solrini/bin/solr create -n anserini -c robust04
```

Along with the collection, Solr will create a core instance, whose name can be found in the Solr UI under collection overview.
It'll look something like `<collection_name>_shard<id>_replica_<id>` (e.g., `robust04_shard1_replica_n1`).
Solr stores configurations and data for the core instances under Solr home, which for us is `solrini/server/solr/` by default.

Second, make proper Solr schema adjustments if necessary.
Here, `robust04` is a TREC collection whose schema is already handled by [managed-schema](https://github.com/castorini/anserini/blob/master/src/main/resources/solr/anserini/conf/managed-schema) in the Solr configset.
However, for a collection such as `cord19`, remember to make proper adjustments to the Solr schema (also see above):

```
curl -X POST -H 'Content-type:application/json' --data-binary @src/main/resources/solr/schemas/SCHEMA_NAME.json http://localhost:8983/solr/COLLECTION_NAME/schema
```

Finally, we can copy the pre-built index to the local where Solr expects them.
Start by removing data that's there:

```
rm solrini/server/solr/robust04_shard1_replica_n1/data/index/*
```

Then, simply copy the pre-built Lucene indexes into that location:

```
cp indexes/lucene-index.robust04.pos+docvectors+raw/* solrini/server/solr/robust04_shard1_replica_n1/data/index
```

Restart Solr to make sure changes take effect:

```
solrini/bin/solr stop
solrini/bin/solr start -c -m 8G
```

You can confirm that everything works by performing a retrieval run and checking the results (see above).

## Solr integration test

We have an end-to-end integration testing script `run_solr_regression.py`.
See example usage for [Robust04](regressions-robust04.md) below:

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
python src/main/python/run_solr_regression.py --insert-docs core18 --input /path/to/disk45

# Search and evaluate on robust04
python src/main/python/run_solr_regression.py --evaluate robust04
```

To run end-to-end, issue the following command:

```bash
python src/main/python/run_solr_regression.py --regression robust04 --input /path/to/disk45
```

The regression script has been verified to work for [`robust04`](regressions-robust04.md), [`core18`](regressions-core18.md), [`msmarco-passage`](experiments-msmarco-passage.md), [`msmarco-doc`](regressions-msmarco-doc.md).

## Replication Log

+ Results replicated by [@nikhilro](https://github.com/nikhilro) on 2020-01-26 (commit [`1882d84`](https://github.com/castorini/anserini/commit/1882d84236b13cd4673d2d8fa91003438eea2d82)) for both [Washington Post](regressions-core18.md) and [Robust04](regressions-robust04.md)
+ Results replicated by [@edwinzhng](https://github.com/edwinzhng) on 2020-01-28 (commit [`a79cb62`](https://github.com/castorini/anserini/commit/a79cb62a57a059113a6c3b1523b582b89dccf0a1)) for both [Washington Post](regressions-core18.md) and [Robust04](regressions-robust04.md)
+ Results replicated by [@nikhilro](https://github.com/nikhilro) on 2020-02-12 (commit [`eff7755`](https://github.com/castorini/anserini/commit/eff7755a611bd20ee1d63ac0167f5c8f38cd3074)) for [Washington Post `core18`](regressions-core18.md), [Robust04 `robust04`](regressions-robust04.md), and [MS Marco Passage `msmarco-passage`](regressions-msmarco-passage.md) using end-to-end [`run_solr_regression`](../src/main/python/run_solr_regression.py)
+ Results replicated by [@yuki617](https://github.com/yuki617) on 2020-03-30 (commit [`ec8ee41`](https://github.com/castorini/anserini/commit/ec8ee4145edf6db767cb86fa0d244d17e652eb2e)) for [MS Marco Passage `msmarco-passage`](regressions-msmarco-passage.md) using end-to-end [`run_solr_regression`](../src/main/python/run_solr_regression.py)
+ Results replicated by [@HangCui0510](https://github.com/HangCui0510) on 2020-04-29 (commit [`31d843a`](https://github.com/castorini/anserini/commit/31d843a6073bfd7eff7e326f543e3f11845df7fa)) for [MS Marco Passage `msmarco-passage`](regressions-msmarco-passage.md) using end-to-end [`run_solr_regression`](../src/main/python/run_solr_regression.py)
+ Results replicated by [@shaneding](https://github.com/shaneding) on 2020-05-26 (commit [`bed8ead`](https://github.com/castorini/anserini/commit/bed8eadad5f2ba859a2ddd2801db4aaeb3c81485)) for [MS Marco Passage `msmarco-passage`](regressions-msmarco-passage.md) using end-to-end [`run_solr_regression`](../src/main/python/run_solr_regression.py)
+ Results replicated by [@YimingDou](https://github.com/YimingDou) on 2020-05-29 (commit [`2947a16`](https://github.com/castorini/anserini/commit/2947a1622efae35637b83e321aba8e6fccd43489)) for [MS MARCO Passage `msmarco-passage`](regressions-msmarco-passage.md)
+ Results replicated by [@adamyy](https://github.com/adamyy) on 2020-05-29 (commit [`2947a16`](https://github.com/castorini/anserini/commit/2947a1622efae35637b83e321aba8e6fccd43489)) for [MS Marco Passage `msmarco-passage`](regressions-msmarco-passage.md) and [MS Marco Document `msmarco-doc`](regressions-msmarco-doc.md) using end-to-end [`run_solr_regression`](../src/main/python/run_solr_regression.py)
+ Results replicated by [@yxzhu16](https://github.com/yxzhu16) on 2020-07-17 (commit [`fad12be`](https://github.com/castorini/anserini/commit/fad12be2e37a075100707c3a674eb67bc0aa57ef)) for [Robust04 `robust04`](regressions-robust04.md), [Washington Post `core18`](regressions-core18.md), and [MS Marco Passage `msmarco-passage`](regressions-msmarco-passage.md) using end-to-end [`run_solr_regression`](../src/main/python/run_solr_regression.py)
+ Results replicated by [@lintool](https://github.com/lintool) on 2020-11-10 (commit [`e19755`](https://github.com/castorini/anserini/commit/e19755b5fa976127830597bc9fbca203b9f5ad24)), all commands and end-to-end regression script for all four collections
 Results replicated by [@jrzhang12](https://github.com/jrzhang12) on 2021-01-10 (commit [`be4e44d`](https://github.com/castorini/anserini/commit/02c52ee606ba0ebe32c130af1e26d24d8f10566a)) for [MS MARCO Passage](regressions-msmarco-passage.md)
