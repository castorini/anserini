# Working with the [COVID-19 Open Research Dataset](https://pages.semanticscholar.org/coronavirus-research)

This document describes various tools for working with the [COVID-19 Open Research Dataset (CORD-19)](https://pages.semanticscholar.org/coronavirus-research) from the [Allen Institute for AI](https://allenai.org/).
For an easy way to get started, check out our Colab demos, also available [here](https://github.com/castorini/anserini-notebooks):

+ [Colab demo using the title + abstract index](https://github.com/castorini/anserini-notebooks/blob/master/pyserini_covid19_default.ipynb)
+ [Colab demo using the paragraph index](https://github.com/castorini/anserini-notebooks/blob/master/pyserini_covid19_paragraph.ipynb)
+ [Colab demo that demonstrates integration with SciBERT](https://github.com/castorini/anserini-notebooks/blob/master/Pyserini+SciBERT_on_COVID_19_Demo.ipynb)

We provide instructions on how to build Lucene indexes for the collection using Anserini below, but if you don't want to bother building the indexes yourself, we have pre-built indexes that you can directly download:

If you don't want to build the index yourself, you can download the latest pre-built copies here:

| Version    | Type      | Size  | Link | Checksum |
|:-----------|:----------|:------|:-----|:---------|
| 2020-05-12 | Abstract  |  1.3G | [[Dropbox]](https://www.dropbox.com/s/jbgvryz6njbfzzp/lucene-index-cord19-abstract-2020-05-12.tar.gz)  | `dfd09e70cd672bbe15a63437351e1f74`
| 2020-05-12 | Full-Text |  2.5G | [[Dropbox]](https://www.dropbox.com/s/2ip7ldupwtbq3pb/lucene-index-cord19-full-text-2020-05-12.tar.gz) | `5b914e8ae579195185cf28a60051236d`
| 2020-05-12 | Paragraph |  3.6G | [[Dropbox]](https://www.dropbox.com/s/s3bylw97cf0t2wq/lucene-index-cord19-paragraph-2020-05-12.tar.gz) | `a2cb36762078ef9373f0ddaf52618e7f`

"Size" refers to the output of `ls -lh`, "Version" refers to the dataset release date from AI2.
For our answer to the question, "which one should I use?" see below.

We've kept around older versions of the index for archival purposes &mdash; scroll all the way down to the bottom of the page to see those.

## Data Prep

The latest distribution available is from 2020/05/12.
First, download the data:

```bash
DATE=2020-05-12
DATA_DIR=./cord19-"${DATE}"
mkdir "${DATA_DIR}"

wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/latest/document_parses.tar.gz -P "${DATA_DIR}"
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/latest/metadata.csv -P "${DATA_DIR}"

ls "${DATA_DIR}"/document_parses.tar.gz | xargs -I {} tar -zxvf {} -C "${DATA_DIR}"
rm "${DATA_DIR}"/document_parses.tar.gz
```

## Building Local Lucene Indexes

We can now index this corpus using Anserini.
Currently, we have implemented three different variants, described below.
For a sense of how these different methods stack up, refer to the following paper:

+ Jimmy Lin. [Is Searching Full Text More Effective Than Searching Abstracts?](https://bmcbioinformatics.biomedcentral.com/articles/10.1186/1471-2105-10-46) BMC Bioinformatics, 10:46 (3 February 2009).

The tl;dr &mdash; we'd recommend getting started with title + abstract index since it's the smallest in size and easiest to manipulate. Paragraph indexing is likely to be more effective (i.e., better search results), but a bit more difficult to manipulate since some deduping is required to post-process the raw hits (since multiple paragraphs from the same article might be retrieved).
The full-text index overly biases long documents and isn't really effective; this condition is included here only for completeness.

### Abstract

We can index abstracts (and titles, of course) with `Cord19AbstractCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection Cord19AbstractCollection -generator Cord19Generator \
  -threads 8 -input "${DATA_DIR}" \
  -index "${DATA_DIR}"/lucene-index-cord19-abstract-"${DATE}" \
  -storePositions -storeDocvectors -storeContents -storeRaw -optimize > log.cord19-abstract.${DATE}.txt
```

The log should end with something like this:

```bash
2020-05-14 07:14:36,804 INFO  [main] index.IndexCollection (IndexCollection.java:874) - Indexing Complete! 63,557 documents indexed
2020-05-14 07:14:36,804 INFO  [main] index.IndexCollection (IndexCollection.java:875) - ============ Final Counter Values ============
2020-05-14 07:14:36,804 INFO  [main] index.IndexCollection (IndexCollection.java:876) - indexed:           63,557
2020-05-14 07:14:36,804 INFO  [main] index.IndexCollection (IndexCollection.java:877) - unindexable:            0
2020-05-14 07:14:36,805 INFO  [main] index.IndexCollection (IndexCollection.java:878) - empty:                 10
2020-05-14 07:14:36,805 INFO  [main] index.IndexCollection (IndexCollection.java:879) - skipped:                4
2020-05-14 07:14:36,805 INFO  [main] index.IndexCollection (IndexCollection.java:880) - errors:                 0
2020-05-14 07:14:36,809 INFO  [main] index.IndexCollection (IndexCollection.java:883) - Total 63,557 documents indexed in 00:01:28
```

The `contents` field of each Lucene document is a concatenation of the article's title and abstract.

### Full-Text

We can index the full text, with `Cord19FullTextCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection Cord19FullTextCollection -generator Cord19Generator \
  -threads 8 -input "${DATA_DIR}" \
  -index "${DATA_DIR}"/lucene-index-cord19-full-text-"${DATE}" \
  -storePositions -storeDocvectors -storeContents -storeRaw -optimize > log.cord19-full-text.${DATE}.txt
```

The log should end with something like this:

```bash
2020-05-14 07:19:01,578 INFO  [main] index.IndexCollection (IndexCollection.java:874) - Indexing Complete! 63,560 documents indexed
2020-05-14 07:19:01,579 INFO  [main] index.IndexCollection (IndexCollection.java:875) - ============ Final Counter Values ============
2020-05-14 07:19:01,579 INFO  [main] index.IndexCollection (IndexCollection.java:876) - indexed:           63,560
2020-05-14 07:19:01,579 INFO  [main] index.IndexCollection (IndexCollection.java:877) - unindexable:            0
2020-05-14 07:19:01,579 INFO  [main] index.IndexCollection (IndexCollection.java:878) - empty:                  7
2020-05-14 07:19:01,580 INFO  [main] index.IndexCollection (IndexCollection.java:879) - skipped:                4
2020-05-14 07:19:01,580 INFO  [main] index.IndexCollection (IndexCollection.java:880) - errors:                 0
2020-05-14 07:19:01,584 INFO  [main] index.IndexCollection (IndexCollection.java:883) - Total 63,560 documents indexed in 00:04:23
```

The `contents` field of each Lucene document is a concatenation of the article's title and abstract, and the full text JSON (if available).

### Paragraph

We can build a paragraph index with `Cord19ParagraphCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection Cord19ParagraphCollection -generator Cord19Generator \
  -threads 8 -input "${DATA_DIR}" \
  -index "${DATA_DIR}"/lucene-index-cord19-paragraph-"${DATE}" \
  -storePositions -storeDocvectors -storeContents -storeRaw -optimize > log.cord19-paragraph.${DATE}.txt
```

The log should end with something like this:

```bash
2020-05-14 07:32:02,701 INFO  [main] index.IndexCollection (IndexCollection.java:874) - Indexing Complete! 1,783,327 documents indexed
2020-05-14 07:32:02,701 INFO  [main] index.IndexCollection (IndexCollection.java:875) - ============ Final Counter Values ============
2020-05-14 07:32:02,701 INFO  [main] index.IndexCollection (IndexCollection.java:876) - indexed:        1,783,327
2020-05-14 07:32:02,701 INFO  [main] index.IndexCollection (IndexCollection.java:877) - unindexable:            0
2020-05-14 07:32:02,702 INFO  [main] index.IndexCollection (IndexCollection.java:878) - empty:                 10
2020-05-14 07:32:02,702 INFO  [main] index.IndexCollection (IndexCollection.java:879) - skipped:            1,666
2020-05-14 07:32:02,702 INFO  [main] index.IndexCollection (IndexCollection.java:880) - errors:                 0
2020-05-14 07:32:02,706 INFO  [main] index.IndexCollection (IndexCollection.java:883) - Total 1,783,327 documents indexed in 00:13:00
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
solrini/bin/solr create -n anserini -c cord19
```

Adjust the schema (if there are errors, follow the instructions below and come back):

```
curl -X POST -H 'Content-type:application/json' --data-binary @src/main/resources/solr/schemas/covid.json http://localhost:8983/solr/cord19/schema
```

*Note:* if there are errors from field conflicts, you'll need to reset the configset and recreate the collection (select [All] for the fields to replace):
```
solrini/bin/solr delete -c cord19
pushd src/main/resources/solr && ./solr.sh ../../../../solrini localhost:9983 && popd
solrini/bin/solr create -n anserini -c cord19
```

We can now index into Solr:

```
DATE=2020-05-12
DATA_DIR=./cord19-"${DATE}"

sh target/appassembler/bin/IndexCollection -collection Cord19AbstractCollection -generator Cord19Generator \
   -threads 8 -input "${DATA_DIR}" \
   -solr -solr.index cord19 -solr.zkUrl localhost:9983 \
   -storePositions -storeDocvectors -storeContents -storeRaw
```

Once indexing is complete, you can query in Solr at [`http://localhost:8983/solr/#/cord19/query`](http://localhost:8983/solr/#/cord19/query).

## Pre-Built Indexes (All Versions)

All versions of pre-built indexes:

| Version    | Type      | Size  | Link | Checksum |
|:-----------|:----------|:------|:-----|:---------|
| 2020-05-12 | Abstract  |  1.3G | [[Dropbox]](https://www.dropbox.com/s/jbgvryz6njbfzzp/lucene-index-cord19-abstract-2020-05-12.tar.gz)  | `dfd09e70cd672bbe15a63437351e1f74`
| 2020-05-12 | Full-Text |  2.5G | [[Dropbox]](https://www.dropbox.com/s/2ip7ldupwtbq3pb/lucene-index-cord19-full-text-2020-05-12.tar.gz) | `5b914e8ae579195185cf28a60051236d`
| 2020-05-12 | Paragraph |  3.6G | [[Dropbox]](https://www.dropbox.com/s/s3bylw97cf0t2wq/lucene-index-cord19-paragraph-2020-05-12.tar.gz) | `a2cb36762078ef9373f0ddaf52618e7f`
| 2020-05-01 | Abstract  |  1.2G | [[Dropbox]](https://www.dropbox.com/s/wxjoe4g71zt5za2/lucene-index-cord19-abstract-2020-05-01.tar.gz)  | `a06e71a98a68d31148cb0e97e70a2ee1`
| 2020-05-01 | Full-Text |  2.4G | [[Dropbox]](https://www.dropbox.com/s/di27r5o2g5kat5k/lucene-index-cord19-full-text-2020-05-01.tar.gz) | `e7eca1b976cdf2cd80e908c9ac2263cb`
| 2020-05-01 | Paragraph |  3.6G | [[Dropbox]](https://www.dropbox.com/s/6ib71scm925mclk/lucene-index-cord19-paragraph-2020-05-01.tar.gz) | `8f9321757a03985ac1c1952b2fff2c7d`
| 2020-04-24 | Abstract  |  1.3G | [[Dropbox]](https://www.dropbox.com/s/ntfg6ykr3ed3acn/lucene-index-cord19-abstract-2020-04-24.tar.gz)  | `93540ae00e166ee433db7531e1bb51c8`
| 2020-04-24 | Full-Text |  2.4G | [[Dropbox]](https://www.dropbox.com/s/twb1defsb19ss4x/lucene-index-cord19-full-text-2020-04-24.tar.gz) | `fa927b0fc9cf1cd382413039cdc7b736`
| 2020-04-24 | Paragraph |  5.0G | [[Dropbox]](https://www.dropbox.com/s/xg2b4aapjvmx3ve/lucene-index-cord19-paragraph-2020-04-24.tar.gz) | `7c6de6298e0430b8adb3e03310db32d8`
| 2020-04-17 | Abstract  |  1.2G | [[Dropbox]](https://www.dropbox.com/s/xogxcrvyx75vxoj/lucene-index-covid-2020-04-17.tar.gz)            | `d57b17eadb1b44fc336b4121c139a598`
| 2020-04-17 | Full-Text |  2.2G | [[Dropbox]](https://www.dropbox.com/s/gs054ecxna5xm0f/lucene-index-covid-full-text-2020-04-17.tar.gz)  | `677546e0a1b7855a48eee8b6fbd7d7af`
| 2020-04-17 | Paragraph |  4.7G | [[Dropbox]](https://www.dropbox.com/s/u3a0z53pdaxekfe/lucene-index-covid-paragraph-2020-04-17.tar.gz)  | `c11e46230b744a46747f84e49acc9c2b`
| 2020-04-10 | Abstract  |  1.2G | [[Dropbox]](https://www.dropbox.com/s/j55t617yhvmegy8/lucene-index-covid-2020-04-10.tar.gz)            | `ec239d56498c0e7b74e3b41e1ce5d42a`
| 2020-04-10 | Full-Text |  3.3G | [[Dropbox]](https://www.dropbox.com/s/gtq2c3xq81mjowk/lucene-index-covid-full-text-2020-04-10.tar.gz)  | `401a6f5583b0f05340c73fbbeb3279c8`
| 2020-04-10 | Paragraph |  3.4G | [[Dropbox]](https://www.dropbox.com/s/ivk87journyajw3/lucene-index-covid-paragraph-2020-04-10.tar.gz)  | `8b87a2c55bc0a15b87f11e796860216a`
| 2020-04-03 | Abstract  |  1.1G | [[Dropbox]](https://www.dropbox.com/s/d6v9fensyi7q3gb/lucene-index-covid-2020-04-03.tar.gz)            | `5d0d222e746d522a75f94240f5ab9f23`
| 2020-04-03 | Full-Text |  3.0G | [[Dropbox]](https://www.dropbox.com/s/abhuqks7aa1xs79/lucene-index-covid-full-text-2020-04-03.tar.gz)  | `9aafb86fec39e0882bd9ef0688d7a9cc`
| 2020-04-03 | Paragraph |  3.1G | [[Dropbox]](https://www.dropbox.com/s/rfzxrrstwlck4wh/lucene-index-covid-paragraph-2020-04-03.tar.gz)  | `523894cfb52fc51c4202e76af79e1b10`
| 2020-03-27 | Abstract  |  1.1G | [[Dropbox]](https://www.dropbox.com/s/j1epbu4ufunbbzv/lucene-index-covid-2020-03-27.tar.gz)            | `c5f7247e921c80f41ac6b54ff38eb229`
| 2020-03-27 | Full-Text |  2.9G | [[Dropbox]](https://www.dropbox.com/s/hjsf7qldn4t10vm/lucene-index-covid-full-text-2020-03-27.tar.gz)  | `3c126344f9711720e6cf627c9bc415eb`
| 2020-03-27 | Paragraph |  3.1G | [[Dropbox]](https://www.dropbox.com/s/o95pehyzem0yalp/lucene-index-covid-paragraph-2020-03-27.tar.gz)  | `8e02de859317918af4829c6188a89086`
| 2020-03-20 | Abstract  |  1.0G | [[Dropbox]](https://www.dropbox.com/s/uvjwgy4re2myq5s/lucene-index-covid-2020-03-20.tar.gz)            | `281c632034643665d52a544fed23807a`
| 2020-03-20 | Full-Text |  2.6G | [[Dropbox]](https://www.dropbox.com/s/w74nmpmvdgw7o00/lucene-index-covid-full-text-2020-03-20.tar.gz)  | `30cae90b85fa8f1b53acaa62413756e3`
| 2020-03-20 | Paragraph |  2.9G | [[Dropbox]](https://www.dropbox.com/s/evnhj2ylo02m03f/lucene-index-covid-paragraph-2020-03-20.tar.gz)  | `4c78e9ede690dbfac13e25e634c70ae4`

