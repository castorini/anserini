# Anserini: Experiments on [COVID-19 Open Research Dataset](https://pages.semanticscholar.org/coronavirus-research)

This document describes the steps to index [COVID-19 Open Research Dataset](https://pages.semanticscholar.org/coronavirus-research)

## Data Prep

First, we need to download and extract the [COVID-19 Open Research Dataset](https://pages.semanticscholar.org/coronavirus-research):

```bash
DATA_DIR=./covid
mkdir ${DATA_DIR}

wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/2020-03-20/comm_use_subset.tar.gz -P ${DATA_DIR}
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/2020-03-20/noncomm_use_subset.tar.gz -P ${DATA_DIR}
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/2020-03-20/custom_license.tar.gz -P ${DATA_DIR}
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/2020-03-20/biorxiv_medrxiv.tar.gz -P ${DATA_DIR}
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/2020-03-20/metadata.csv -P ${DATA_DIR}

ls ${DATA_DIR}/*.tar.gz | xargs --replace tar -zxvf {} -C ${DATA_DIR}
```

We can now index these docs as a `CovidCollection` using Anserini:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection CovidCollection -generator CovidGenerator \
  -threads 8 -input ${DATA_DIR} \
  -index ${DATA_DIR}/lucene-index-covid \
  -storePositions -storeDocvectors -storeRawDocs -storeTransformedDocs
```

The output message should be something like this:

```
2020-03-22 00:04:40,382 INFO  [main] index.IndexCollection (IndexCollection.java:845) - Total 44,220 documents indexed in 00:05:06
```
