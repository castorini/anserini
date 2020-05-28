## Ingesting CORD-19 into Solr and Blacklight
To begin, ensure that you have:

- Ruby 2.6.5, and Ruby on Rails 6.0+ installed.

## Getting the data
The latest distribution of cord19 available is from 2020/05/26.
First, download the data:

```bash
DATE=2020-05-26
DATA_DIR=./collections/cord19-"${DATE}"
mkdir "${DATA_DIR}"

wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/latest/document_parses.tar.gz -P "${DATA_DIR}"
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/latest/metadata.csv -P "${DATA_DIR}"

ls "${DATA_DIR}"/document_parses.tar.gz | xargs -I {} tar -zxvf {} -C "${DATA_DIR}"
rm "${DATA_DIR}"/document_parses.tar.gz
```

## Indexing into Solr

From the Solr [archives](https://archive.apache.org/dist/lucene/solr/), download the Solr (non `-src`) version that matches Anserini's [Lucene version](https://github.com/castorini/anserini/blob/master/pom.xml#L36) to the `anserini/` directory.

Extract the archive:

```bash
mkdir solrini && tar -zxvf solr*.tgz -C solrini --strip-components=1
```

Start Solr (adjust memory usage with `-m` as appropriate):

```
solrini/bin/solr start -c -m 8G
```

Run the Solr bootstrap script to copy the Anserini JAR into Solr's classpath and upload the configsets to Solr's internal ZooKeeper:

```
pushd src/main/resources/solr && ./solr.sh ../../../../solrini localhost:9983 && popd
```

Solr should now be available at [http://localhost:8983/](http://localhost:8983/) for browsing.

Next, create the collection:

```
solrini/bin/solr create -n anserini -c cord19
```

Adjust the schema (if there are errors, follow the instructions below and come back):

```
curl -X POST -H 'Content-type:application/json' --data-binary @src/main/resources/solr/schemas/cord19.json http://localhost:8983/solr/cord19/schema
```

*Note:* if there are errors from field conflicts, you'll need to reset the configset and recreate the collection (select [All] for the fields to replace):
```
solrini/bin/solr delete -c cord19
pushd src/main/resources/solr && ./solr.sh ../../../../solrini localhost:9983 && popd
solrini/bin/solr create -n anserini -c cord19
```

We can now index into Solr:

```
DATE=2020-05-26
DATA_DIR=./collections/cord19-"${DATE}"

sh target/appassembler/bin/IndexCollection -collection Cord19AbstractCollection -generator Cord19Generator \
   -threads 8 -input "${DATA_DIR}" \
   -solr -solr.index cord19 -solr.zkUrl localhost:9983 \
   -storePositions -storeDocvectors -storeContents -storeRaw
```

Once indexing is complete, you can query in Solr at [`http://localhost:8983/solr/#/cord19/query`](http://localhost:8983/solr/#/cord19/query).

## Starting the Rails app:
Once the approriate ruby and ruby on rails version is installed, navigate to a directory outside of anserini and clone the gooselight repo (frontend for Cord19 Solr indexes):

```
cd ..
git clone https://github.com/castorini/gooselight2.git
```
Then navigate into the `gooselight2/covid` directory, and run the following commands, if a `yarn` error occurs with `rails db:migrate` run `yarn install --check-files` to update yarn:

```
bundle install
rails db:migrate
rails s
```

The rails should now be avaliable on http://localhost:3000
