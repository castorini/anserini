# Solrini: Solr Integration with Anserini

This page documents code for replicating results from the following paper:

+ Ryan Clancy, Toke Eskildsen, Nick Ruest, and Jimmy Lin. [Solr Integration in the Anserini Information Retrieval Toolkit.](https://cs.uwaterloo.ca/~jimmylin/publications/Clancy_etal_SIGIR2019a.pdf) _Proceedings of the 42nd Annual International ACM SIGIR Conference on Research and Development in Information Retrieval (SIGIR 2019)_, July 2019, Paris, France.

We provide instructions for setting up a single-node SolrCloud instance running locally and indexing into it from Anserini.
Instructions for setting up SolrCloud clusters can be found by searching the web.

## Setting up a Single-Node SolrCloud Instance

From the Solr [archives](https://archive.apache.org/dist/lucene/solr/), download the Solr (non `-src`) version that matches Anserini's [Lucene version](https://github.com/castorini/anserini/blob/master/pom.xml#L36).

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


## Indexing into SolrCloud from Anserini

We can use Anserini as a common "frontend" for indexing into SolrCloud, thus supporting the same range of test collections that's already included in Anserini (when directly building local Lucene indexes).
Indexing into Solr is similar indexing to disk with Lucene, with a few added parameters.
Most notably, we replace the `-index` parameter (which specifies the Lucene index path on disk) with Solr parameters.

We'll index [robust04](https://github.com/castorini/Anserini/blob/master/docs/experiments-robust04.md) as an example:

Create the `robust04` collection in Solr:
```
solrini/bin/solr create -n anserini -c robust04
```

Run the Solr indexing command for `robust04`:

```
sh target/appassembler/bin/IndexCollection -collection TrecCollection -generator JsoupGenerator \
  -threads 8 -input /path/to/robust04 \
  -solr -solr.index robust04 -solr.zkUrl localhost:9983 \
  -storePositions -storeDocvectors -storeRawDocs
```

Make sure `/path/to/robust04` is updated with the appropriate path.

Once indexing has completed, you should be able to query `robust04` from the Solr [query interface](http://localhost:8983/solr/#/robust04/query).

Other collections can be indexed by substituting the appropriate parameters; see each collection's [experiment docs](https://github.com/castorini/anserini/tree/master/docs).
