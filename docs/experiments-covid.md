# Working with the [COVID-19 Open Research Dataset](https://pages.semanticscholar.org/coronavirus-research)

This document describes various tools for working with the [COVID-19 Open Research Dataset (CORD-19)](https://pages.semanticscholar.org/coronavirus-research) (2020/03/27 version) from the [Allen Institute for AI](https://allenai.org/).
For an easy way to get started, check out our Colab demos, also available [here](https://github.com/castorini/anserini-notebooks):

+ [Colab demo using the title + abstract index](https://colab.research.google.com/drive/1mrapJp6-RIB-3u6FaJVa4WEwFdEBOcTe)
+ [Colab demo using the paragraph index](https://colab.research.google.com/drive/1VvUR8P2CZvmdwC_J3AvRH5GvtMld8_zN)
+ [Colab demo that demonstrates integration with SciBERT](https://colab.research.google.com/drive/1L_yWXM4tOhZsHpMDNIIux-hfp1-pW3RL)

We provide instructions on how to build Lucene indexes for the collection using Anserini below, but if you don't want to bother building the indexes yourself, we have pre-built indexes that you can directly download:

If you don't want to build the index yourself, you can download a pre-built copies here:

| Type | Version | Size | Link| Checksum |
|:-----|:--------|:-----|:----|:---------|
| Title + Abstract | 2020-03-27 | 1.1G | [[Dropbox]](https://www.dropbox.com/s/j1epbu4ufunbbzv/lucene-index-covid-2020-03-27.tar.gz?dl=0) | `c5f7247e921c80f41ac6b54ff38eb229`
| Title + Abstract | 2020-03-20 | 984M | [[Dropbox]](https://www.dropbox.com/s/uvjwgy4re2myq5s/lucene-index-covid-2020-03-20.tar.gz?dl=0) | `281c632034643665d52a544fed23807a`
| Full-Text | 2020-03-27 | 2.9G | [[Dropbox]](https://www.dropbox.com/s/hjsf7qldn4t10vm/lucene-index-covid-full-text-2020-03-27.tar.gz?dl=0) | `3c126344f9711720e6cf627c9bc415eb`
| Full-Text | 2020-03-20 | 2.6G | [[Dropbox]](https://www.dropbox.com/s/w74nmpmvdgw7o00/lucene-index-covid-full-text-2020-03-20.tar.gz?dl=0) | `30cae90b85fa8f1b53acaa62413756e3`
| Paragraph | 2020-03-27 | 3.1G| [[Dropbox]](https://www.dropbox.com/s/o95pehyzem0yalp/lucene-index-covid-paragraph-2020-03-27.tar.gz?dl=0) | `8e02de859317918af4829c6188a89086`
| Paragraph | 2020-03-20 | 2.9G| [[Dropbox]](https://www.dropbox.com/s/evnhj2ylo02m03f/lucene-index-covid-paragraph-2020-03-20.tar.gz?dl=0) | `4c78e9ede690dbfac13e25e634c70ae4`

"Size" refers to the output of `ls -lh`, "Version" refers to the dataset release date from AI2.
For our answer to the question, "which one should I use?" see below.

## Data Prep

The latest distribution available is from 2020/03/20.
First, download the data:

```bash
DATE=2020-03-27
DATA_DIR=./covid-"${DATE}"
mkdir "${DATA_DIR}"

wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/comm_use_subset.tar.gz -P "${DATA_DIR}"
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/noncomm_use_subset.tar.gz -P "${DATA_DIR}"
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/custom_license.tar.gz -P "${DATA_DIR}"
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/biorxiv_medrxiv.tar.gz -P "${DATA_DIR}"
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/metadata.csv -P "${DATA_DIR}"

ls "${DATA_DIR}"/*.tar.gz | xargs -I {} tar -zxvf {} -C "${DATA_DIR}"
# If the above doesn't work due to cross-OS compatibility issues with xargs, untar all folders individually
# tar -zxvf "${DATA_DIR}"/comm_use_subset.tar.gz -C "${DATA_DIR}"
# tar -zxvf "${DATA_DIR}"/noncomm_use_subset.tar.gz -C "${DATA_DIR}"
# tar -zxvf "${DATA_DIR}"/custom_license.tar.gz -C "${DATA_DIR}"
# tar -zxvf "${DATA_DIR}"/biorxiv_medrxiv.tar.gz -C "${DATA_DIR}"
```

## Building Local Lucene Indexes

We can now index this corpus using Anserini.
Currently, we have implemented three different variants, described below.
For a sense of how these different methods stack up, refer to the following paper:

+ Jimmy Lin. [Is Searching Full Text More Effective Than Searching Abstracts?](https://bmcbioinformatics.biomedcentral.com/articles/10.1186/1471-2105-10-46) BMC Bioinformatics, 10:46 (3 February 2009).

The tl;dr &mdash; we'd recommend getting started with title + abstract index since it's the smallest in size and easiest to manipulate. Paragraph indexing is likely to be more effective (i.e., better search results), but a bit more difficult to manipulate since some deduping is required to post-process the raw hits (since multiple paragraphs from the same article might be retrieved).
The full-text index overly biases long documents and isn't really effective; this condition is included here only for completeness.

### Title + Abstract

We can index titles and abstracts only with `CovidCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection CovidCollection -generator CovidGenerator \
  -threads 8 -input "${DATA_DIR}" \
  -index "${DATA_DIR}"/lucene-index-covid-"${DATE}" \
  -storePositions -storeDocvectors -storeRawDocs -storeTransformedDocs
```

The output message should be something like this:

```bash
2020-03-22 18:58:33,021 INFO  [main] index.IndexCollection (IndexCollection.java:845) - Total 44,145 documents indexed in 00:01:05
```

The `contents` field of each Lucene document is a concatenation of the article's title and abstract.

### Full-Text

We can index the full text, with  `CovidFullTextCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection CovidFullTextCollection -generator CovidGenerator \
  -threads 8 -input "${DATA_DIR}" \
  -index "${DATA_DIR}"/lucene-index-covid-full-text-"${DATE}" \
  -storePositions -storeDocvectors -storeRawDocs -storeTransformedDocs
```

The output message should be something like this:

```bash
2020-03-22 19:04:49,120 INFO  [main] index.IndexCollection (IndexCollection.java:845) - Total 44,155 documents indexed in 00:05:32
```

The `contents` field of each Lucene document is a concatenation of the article's title and abstract, and the full text JSON (if available).

### Paragraph

We can build a paragraph index with `CovidParagraphCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection CovidParagraphCollection -generator CovidGenerator \
  -threads 8 -input "${DATA_DIR}" \
  -index "${DATA_DIR}"/lucene-index-covid-paragraph-"${DATE}" \
  -storePositions -storeDocvectors -storeRawDocs -storeTransformedDocs
```

The output message should be something like this:

```bash
2020-03-22 19:21:50,365 INFO  [main] index.IndexCollection (IndexCollection.java:845) - Total 1,096,241 documents indexed in 00:14:21
```

In this configuration, the indexer creates multiple Lucene Documents for each source article:

+ `docid`: title + abstract
+ `docid.00001`: title + abstract + 1st paragraph
+ `docid.00002`: title + abstract + 2nd paragraph
+ `docid.00003`: title + abstract + 3rd paragraph
+ ...

The suffix of the `docid`, `.XXXXX` identifies which paragraph is being indexed.
The original raw JSON full text is stored in the `raw` field of `docid` (without the suffix).


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
solrini/bin/solr create -n anserini -c covid
```

Adjust the schema (if there are errors, follow the instructions below and come back):

```
curl -X POST -H 'Content-type:application/json' --data-binary @src/main/resources/solr/schemas/covid.json http://localhost:8983/solr/covid/schema
```

*Note:* if there are errors from field conflicts, you'll need to reset the configset and recreate the collection (select [All] for the fields to replace):
```
solrini/bin/solr delete -c covid
pushd src/main/resources/solr && ./solr.sh ../../../../solrini localhost:9983 && popd
solrini/bin/solr create -n anserini -c covid
```

We can now index into Solr:

```
DATE=2020-03-27
DATA_DIR=./covid-"${DATE}"

sh target/appassembler/bin/IndexCollection -collection CovidCollection -generator CovidGenerator \
   -threads 8 -input "${DATA_DIR}" \
   -solr -solr.index covid -solr.zkUrl localhost:9983 \
   -storePositions -storeDocvectors -storeTransformedDocs
```

Once indexing is complete, you can query in Solr at [`http://localhost:8983/solr/#/covid/query`](http://localhost:8983/solr/#/covid/query).
