# Anserini: Experiments on [COVID-19 Open Research Dataset](https://pages.semanticscholar.org/coronavirus-research)

This document describes the steps to index the [COVID-19 Open Research Dataset](https://pages.semanticscholar.org/coronavirus-research) from AI2.
If you don't want to bother building the indexes yourself, we have pre-built indexes that you can directly download (see below).
If you want a very low cost way to get started, check out our [Colab demo using the Title + Abstract Index](https://colab.research.google.com/drive/1mrapJp6-RIB-3u6FaJVa4WEwFdEBOcTe) and [another Colab demo that demonstrates integration with SciBERT](https://colab.research.google.com/drive/1L_yWXM4tOhZsHpMDNIIux-hfp1-pW3RL).

## Data Prep

The latest distribution available is from 2020/03/20.
First, download the data:

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
# If the above doesn't work due to cross-OS compatibility issues with xargs, untar all folders individually
# tar -zxvf "${DATA_DIR}"/comm_use_subset.tar.gz -C "${DATA_DIR}"
# tar -zxvf "${DATA_DIR}"/noncomm_use_subset.tar.gz -C "${DATA_DIR}"
# tar -zxvf "${DATA_DIR}"/custom_license.tar.gz -C "${DATA_DIR}"
# tar -zxvf "${DATA_DIR}"/biorxiv_medrxiv.tar.gz -C "${DATA_DIR}"
```

## Indexing

We can now index this corpus using Anserini.
Currently, we have implemented three different variants, described below.
For a sense of how these different methods stack up, refer to the following paper:

+ Jimmy Lin. [Is Searching Full Text More Effective Than Searching Abstracts?](https://bmcbioinformatics.biomedcentral.com/articles/10.1186/1471-2105-10-46) BMC Bioinformatics, 10:46 (3 February 2009).

The tl;dr &mdash; we'd recommend getting started with title + abstract index since it's the smallest in size and easiest to manipulate. Paragraph indexing is likely to be more effective (i.e., better search results), but a bit more difficult to manipulate since some deduping is required to post-process the raw hits (since multiple paragraphs from the same article might be retrieved).
The full-text index overly biases long documents and isn't really effective; this condition is included here only for completeness.

If you don't want to build the index yourself, you can download a pre-built copies here:

+ [Title + Abstract](https://www.dropbox.com/s/uvjwgy4re2myq5s/lucene-index-covid-2020-03-20.tar.gz?dl=0)
+ [Full-Text](https://www.dropbox.com/s/w74nmpmvdgw7o00/lucene-index-covid-full-text-2020-03-20.tar.gz?dl=0)
+ [Paragraph](https://www.dropbox.com/s/evnhj2ylo02m03f/lucene-index-covid-paragraph-2020-03-20.tar.gz?dl=0)

### Title + Abstract

We can index titles and abstracts only with `CovidCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection CovidCollection -generator CovidGenerator \
  -threads 8 -input "${DATA_DIR}" \
  -index "${DATA_DIR}"/lucene-index-covid-"${DATE}" \
  -storePositions -storeDocvectors -storeContents -storeRaw
```

The output message should be something like this:

```bash
2020-03-22 18:58:33,021 INFO  [main] index.IndexCollection (IndexCollection.java:845) - Total 44,145 documents indexed in 00:01:05
```

The `contents` field of each Lucene document is a concatenation of the article's title and abstract.

### Full-Text

We can index the full text, with `CovidFullTextCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection CovidFullTextCollection -generator CovidGenerator \
  -threads 8 -input "${DATA_DIR}" \
  -index "${DATA_DIR}"/lucene-index-covid-full-text-"${DATE}" \
  -storePositions -storeDocvectors -storeContents -storeRaw
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
  -storePositions -storeDocvectors -storeContents -storeRaw
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
