# Solrini - Solr Integration with Anserini

In order to index collections with Solr, we'll setup a single-node SolrCloud instance running locally.

## Setup

1) From the Solr [archives](https://archive.apache.org/dist/lucene/solr/), download the Solr (non `-src`) version that matches Anserini's [Lucene version](https://github.com/castorini/anserini/blob/master/pom.xml#L36).
2) Extract the archive:
   - `mkdir solrini && tar -zxvf solr*.tgz -C solrini --strip-components=1`
3) Start Solr:
   - `solrini/bin/solr start -c -m 8G`
4) Run the Solr bootstrap script to copy the Anserini JAR into Solr's classpath and upload the configsets to Solr's internal ZooKeeper:
   - `pushd src/main/resources/solr && ./solr.sh ../../../../solrini localhost:9983 && popd`
   
Solr should be available at [http://localhost:8983](http://localhost:8983) for browsing.

Notes:
 - `-m 8G` may need to be updated depending on your machine's memory capacity

## Indexing

Indexing into Solr is similar indexing to disk with Lucene, with a few added parameters.
Most notably, we replace the `-index` parmeter (which specifies the Lucene index path on disk) with Solr parameters.

We'll index [robust04](https://github.com/castorini/Anserini/blob/master/docs/experiments-robust04.md) as an example:

1. Create the `robust04` collection from the Solr [collections page](http://localhost:8983/solr/#/~collections).
    - Make sure the `config set` value is set to `anserini`
2. Run the Solr indexing command for `robust04`:
```
sh target/appassembler/bin/IndexCollection -collection TrecCollection -generator JsoupGenerator \
  -threads 8 -input /path/to/robust04 \
  -solr -solr.cloud -solr.index robust04 -solr.url localhost:9983 \
  -storePositions -storeDocvectors -storeRawDocs
```
Make sure `/path/to/robust04` is updated with the appropriate path.

Once indexing has completed, you should be able to query `robust04` from the Solr [query interface](http://localhost:8983/solr/#/robust04/query).

To index other collections, the above instructions can be followed making appropriate substitutions for paremeters based on the collection's [experiment docs](https://github.com/castorini/anserini/tree/master/docs).
