# Indexing the ACL Anthology with Anserini

Anserini provides code for indexing the ACL anthology with Solr in order to host an ACL anthology [Blacklight](https://github.com/projectblacklight/blacklight) instance.

## Deploying Solr

Set up Solr locally by following the instructions for setting up a single-node SolrCloud instance in [solrini.md](solrini.md).

## Generating ACL Anthology Data

First, clone the ACL anthology repository containing the raw XML data:

```
git clone git@github.com:acl-org/acl-anthology.git
```
 
Next, navigate to the `acl-anthology` folder and install dependencies (with Python 3.7+):

```
pip install -r requirements.txt
```

Generate cleaned YAML data (again with Python 3.7+):

```
python bin/create_hugo_yaml.py
```

Generated ACL files can now be found in `acl-anthology/build/data/`

## Indexing Data

Once we have a local instance of SolrCloud up and running, we can index using Solr through Solrini.

First, let's create the index collection in Solr using the default config.

```
solrini/bin/solr create -n anserini -c acl
```

Modify the `acl` Solr schema to allow multiValued fields for facets and set explicit field types:

```
sh src/main/resources/solr/setup/acl-anthology.sh
```

Run the Solr indexing command for `acl`:

```
sh target/appassembler/bin/IndexCollection \
  -collection AclAnthologyCollection -generator AclAnthologyGenerator \
  -threads 8 -input /path/to/acl-anthology/build/data/ \
  -solr -solr.index acl -solr.zkUrl localhost:9983 \
  -storePositions -storeDocvectors -storeRawDocs -storeTransformedDocs
```
