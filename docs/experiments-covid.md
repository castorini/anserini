# Anserini: Experiments on [COVID-19 Open Research Dataset](https://pages.semanticscholar.org/coronavirus-research)

This document describes the steps to index the [COVID-19 Open Research Dataset](https://pages.semanticscholar.org/coronavirus-research)

## Data Prep

The latest distribution available is from 2020/03/20.
First, download the data:

```bash
DATE=20200320
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

## Indexing

We can now index this corpus using Anserini.
Currently, we have implemented three differen variants:

### Title + Abstract

We can titles and abstracts only with `CovidCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection CovidCollection -generator CovidGenerator \
  -threads 8 -input "${DATA_DIR}" \
  -index "${DATA_DIR}"/lucene-index-covid-20200320 \
  -storePositions -storeDocvectors -storeRawDocs -storeTransformedDocs
```

The output message should be something like this:

```bash
2020-03-22 16:55:00,711 INFO  [main] index.IndexCollection (IndexCollection.java:845) - Total 44,145 documents indexed in 00:01:07
```

The `contents` field of each Lucene document is a concatenation of the article's title and abstract.


### Full-Text

We can index the full text, with  `CovidFullTextCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection CovidFullTextCollection -generator CovidGenerator \
  -threads 8 -input "${DATA_DIR}" \
  -index "${DATA_DIR}"/lucene-index-covid-full-text-20200320 \
  -storePositions -storeDocvectors -storeRawDocs -storeTransformedDocs
```

The output message should be something like this:

```bash
2020-03-22 16:55:00,711 INFO  [main] index.IndexCollection (IndexCollection.java:845) - Total 44,145 documents indexed in 00:01:07
  ```

The `contents` field of each Lucene document is a concatenation of the article's title and abstract, and the full text JSON (if available).

### Paragraph

We can build a paragraph index with `CovidParagraphCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection CovidParagraphCollection -generator CovidGenerator \
  -threads 8 -input "${DATA_DIR}" \
  -index "${DATA_DIR}"/lucene-index-covid-paragraph-20200320 \
  -storePositions -storeDocvectors -storeRawDocs -storeTransformedDocs
```

The output message should be something like this:

```bash
2020-03-22 15:24:49,305 INFO  [main] index.IndexCollection (IndexCollection.java:845) - Total 1,096,241 documents indexed in 00:11:35
```

In this configuration, the indexer creates multiple Lucene Documents for each source article:

+ `docid.00001`: title + abstract + 1st paragraph
+ `docid.00002`: title + abstract + 2nd paragraph
+ `docid.00003`: title + abstract + 3rd paragraph
+ ...

The suffix of the docid, `.XXXXX` identifies which paragraph is being indexed.
