# Anserini: Experiments on [COVID-19 Open Research Dataset](https://pages.semanticscholar.org/coronavirus-research)

This document describes the steps to index [COVID-19 Open Research Dataset](https://pages.semanticscholar.org/coronavirus-research)

## Data Prep

First, we need to download and extract the [COVID-19 Open Research Dataset](https://pages.semanticscholar.org/coronavirus-research):

```bash
DATE=2020-03-20
DATA_DIR=./covid-"${DATE}"
mkdir "${DATA_DIR}"

wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/comm_use_subset.tar.gz -P "${DATA_DIR}"
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/noncomm_use_subset.tar.gz -P "${DATA_DIR}"
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/custom_license.tar.gz -P "${DATA_DIR}"
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/biorxiv_medrxiv.tar.gz -P "${DATA_DIR}"
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/metadata.csv -P "${DATA_DIR}"

ls "${DATA_DIR}"/*.tar.gz | xargs -I {} tar -zxvf {} -C "${DATA_DIR}"
# If the above doesn't work due to cross compatibility issues with xargs, untar all folders individually
# tar -zxvf "${DATA_DIR}"/comm_use_subset.tar.gz -C "${DATA_DIR}"
# tar -zxvf "${DATA_DIR}"/noncomm_use_subset.tar.gz -C "${DATA_DIR}"
# tar -zxvf "${DATA_DIR}"/custom_license.tar.gz -C "${DATA_DIR}"
# tar -zxvf "${DATA_DIR}"/biorxiv_medrxiv.tar.gz -C "${DATA_DIR}"
```

We can now index these docs as using Anserini; we have three versions:

* `CovidCollection` which adds `title` + `abstract` to Lucene Document's `content`

  ```bash
  sh target/appassembler/bin/IndexCollection \
    -collection CovidCollection -generator CovidGenerator \
    -threads 8 -input "${DATA_DIR}" \
    -index "${DATA_DIR}"/lucene-index-covid \
    -storePositions -storeDocvectors -storeRawDocs -storeTransformedDocs
  ```

  The output message should be something like this:

  ```bash
  2020-03-22 16:55:00,711 INFO  [main] index.IndexCollection (IndexCollection.java:845) - Total 44,145 documents indexed in 00:01:07
  ```

* `CovidFullTextCollection` which adds `title` + `abstract` + `full json text` to Lucene Document's `content`

  ```bash
  sh target/appassembler/bin/IndexCollection \
    -collection CovidFullTextCollection -generator CovidGenerator \
    -threads 8 -input "${DATA_DIR}" \
    -index "${DATA_DIR}"/lucene-index-covid-full-text \
    -storePositions -storeDocvectors -storeRawDocs -storeTransformedDocs
  ```

  The output message should be something like this:

  ```bash
  2020-03-22 16:55:00,711 INFO  [main] index.IndexCollection (IndexCollection.java:845) - Total 44,145 documents indexed in 00:01:07
  ```

* `CovidParagraphCollection` which adds `title` + `abstract` + `paragraph number x` to Lucene Document's `content`. And there will be multiple Lucene Documents for each record. Specifically, one for each paragraph in the full text for the record, hence `paragraph number x`.

  ```bash
  sh target/appassembler/bin/IndexCollection \
    -collection CovidParagraphCollection -generator CovidGenerator \
    -threads 8 -input "${DATA_DIR}" \
    -index "${DATA_DIR}"/lucene-index-covid-paragraph \
    -storePositions -storeDocvectors -storeRawDocs -storeTransformedDocs
  ```

  The output message should be something like this:

  ```bash
  2020-03-22 15:24:49,305 INFO  [main] index.IndexCollection (IndexCollection.java:845) - Total 1,096,241 documents indexed in 00:11:35
  ```
