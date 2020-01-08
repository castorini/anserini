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


## Indexing into SolrCloud from Anserini

We can use Anserini as a common "frontend" for indexing into SolrCloud, thus supporting the same range of test collections that's already included in Anserini (when directly building local Lucene indexes).
Indexing into Solr is similar indexing to disk with Lucene, with a few added parameters.
Most notably, we replace the `-index` parameter (which specifies the Lucene index path on disk) with Solr parameters.

We'll index [robust04](regressions-robust04.md) as an example.
First, create the `robust04` collection in Solr:

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

You can also run the following command to replicate Anserini BM25 retrieval:

```
sh target/appassembler/bin/SearchSolr -topicreader Trec \
  -solr.index robust04 -solr.zkUrl localhost:9983 \
  -topics src/main/resources/topics-and-qrels/topics.robust04.txt \
  -output run.solr.robust04.bm25.topics.robust04.txt
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust04.txt run.solr.robust04.bm25.topics.robust04.txt
```

We've verified that these instructions can be straightforwardly adapted to work with [Washington Post](regressions-core18.md):

```
sh target/appassembler/bin/IndexCollection -collection WashingtonPostCollection -generator WapoGenerator \
   -threads 8 -input /path/to/WashingtonPost \
   -solr -solr.index core18 -solr.zkUrl localhost:9983 \
   -storePositions -storeDocvectors -storeTransformedDocs
```

Make sure `core18` collection is created and `/path/to/WashingtonPost` is updated with the appropriate path.

Other collections can be indexed by substituting the appropriate parameters; see each collection's [experiment docs](https://github.com/castorini/anserini/tree/master/docs).

## Solr integration test

We have an end-to-end integration testing script `run_solr_regression.py` for [Washington Post](regressions-core18.md). Its functionalities are described below.

```
# Check if Solr server is on
python src/main/python/run_solr_regression.py --ping
# Check if core18 exists
python src/main/python/run_solr_regression.py --check-index-exists core18
# Create core18 if it does not exist
python src/main/python/run_solr_regression.py --create-index core18
# Delete core18 if it exists
python src/main/python/run_solr_regression.py --delete-index core18
# Insert documents from /path/to/WashingtonPost into core18
python src/main/python/run_solr_regression.py --insert-docs core18 --input /path/to/WashingtonPost
# Search and evaluate on core18
python src/main/python/run_solr_regression.py --evaluate core18

# Run end to end
python src/main/python/run_solr_regression.py --regression core18 --input /path/to/WashingtonPost
```
