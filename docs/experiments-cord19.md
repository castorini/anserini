# Working with the COVID-19 Open Research Dataset

This document describes various tools for working with the [COVID-19 Open Research Dataset (CORD-19)](https://pages.semanticscholar.org/coronavirus-research) from the [Allen Institute for AI](https://allenai.org/).
For an easy way to get started, check out our Colab demos, also available [here](https://github.com/castorini/anserini-notebooks):

+ [Colab demo using the title + abstract index](https://github.com/castorini/anserini-notebooks/blob/master/pyserini_covid19_default.ipynb)
+ [Colab demo using the paragraph index](https://github.com/castorini/anserini-notebooks/blob/master/pyserini_covid19_paragraph.ipynb)
+ [Colab demo that demonstrates integration with SciBERT](https://github.com/castorini/anserini-notebooks/blob/master/Pyserini+SciBERT_on_COVID_19_Demo.ipynb)

We provide instructions on how to build Lucene indexes for the collection using Anserini below, but if you don't want to bother building the indexes yourself, we have pre-built indexes that you can directly download:

If you don't want to build the index yourself, you can download the latest pre-built copies here:

| Version    | Type      | Size  | Link | Checksum |
|:-----------|:----------|:------|:-----|:---------|
| 2020-06-19 | Abstract  |  2.0G | [[Dropbox]](https://www.dropbox.com/s/bj6lx80wwiy5hxf/lucene-index-cord19-abstract-2020-06-19.tar.gz)  | `029bd55daba8800fbae2be9e5fcd7b33`
| 2020-06-19 | Full-Text |  3.8G | [[Dropbox]](https://www.dropbox.com/s/vkhhxj8u36rgdu9/lucene-index-cord19-full-text-2020-06-19.tar.gz) | `3d0eb12094a24cff9bcacd1f17c3ea1c`
| 2020-06-19 | Paragraph |  5.5G | [[Dropbox]](https://www.dropbox.com/s/yk6egw6op4jccpi/lucene-index-cord19-paragraph-2020-06-19.tar.gz) | `5cd8cd6998177bed7a3e0057ef8b3595`

"Size" refers to the output of `ls -lh`, "Version" refers to the dataset release date from AI2.
For our answer to the question, "which one should I use?" see below.

We've kept around older versions of the index for archival purposes &mdash; scroll all the way down to the bottom of the page to see those.

Note that starting 2020/05/27, AI2 has switched to daily releases of CORD-19, and as a result, it has become impractical to share pre-built indexes.
Thus, we will only be providing pre-built indexes "occasionally".

However, we have written a simple script that will largely automate all the instructions on this page:

```
$ python src/main/python/trec-covid/index_cord19.py --date 2020-06-19 --all
```

The script will:

+ Download a specific release of CORD-19 (`--download`).
+ Build abstract, full-text, and paragraph indexes (`--index`).
+ Verify the indexes with topics and qrels from TREC-COVID (`--verify`).

The `--date` argument takes the format of `YYYY-MM-DD`, corresponding to a release [here](https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/historical_releases.html).
The `--all` argument runs all three steps above (or use each argument individually to run just a specific step).
By default, the script does not overwrite existing data, unless `--force` is specified.

After the above script completes successfully, the output will be something like:

```
## Effectiveness Summary

CORD-19 release: 2020-06-19
Topics/Qrels: TREC-COVID Round 3
Whitelist: TREC-COVID Round 3 valid docids

                    NDCG@10  Judged@10
Abstract index       0.5520    0.8675
Full-text index      0.3462    0.6725
Paragraph index      0.5086    0.8650
```

The instructions below walk through, essentially, what the script does, step by step.

## Data Prep

These instructions work with the dataset release from 2020/06/12.
First, download the data:

```bash
DATE=2020-06-19
DATA_DIR=./collections/cord19-"${DATE}"
mkdir "${DATA_DIR}"

wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/document_parses.tar.gz -P "${DATA_DIR}"
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/changelog -P "${DATA_DIR}"
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/metadata.csv -P "${DATA_DIR}"

ls "${DATA_DIR}"/document_parses.tar.gz | xargs -I {} tar -zxvf {} -C "${DATA_DIR}"
rm "${DATA_DIR}"/document_parses.tar.gz
```

## Building Local Lucene Indexes

We can now index this corpus using Anserini.
Currently, we have implemented three different variants, described below.
For a sense of how these different methods stack up, refer to the following paper:

+ Jimmy Lin. [Is Searching Full Text More Effective Than Searching Abstracts?](https://bmcbioinformatics.biomedcentral.com/articles/10.1186/1471-2105-10-46) BMC Bioinformatics, 10:46 (3 February 2009).

The tl;dr &mdash; we'd recommend getting started with abstract index since it's the smallest in size and easiest to manipulate. Paragraph indexing is likely to be more effective (i.e., better search results), but a bit more difficult to manipulate since some deduping is required to post-process the raw hits (since multiple paragraphs from the same article might be retrieved).
The full-text index overly biases long documents and isn't really effective; this condition is included here only for completeness.

Note that as of TREC-COVID Round 1, there is some evidence that the abstract index is more effective for search, see results of experiments [here](experiments-covid.md).

### Abstract

We can index abstracts (and titles, of course) with `Cord19AbstractCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection Cord19AbstractCollection -generator Cord19Generator \
  -threads 8 -input "${DATA_DIR}" \
  -index indexes/lucene-index-cord19-abstract-"${DATE}" \
  -storePositions -storeDocvectors -storeContents -storeRaw -optimize > logs/log.cord19-abstract.${DATE}.txt
```

The log should end with something like this:

```bash
2020-06-23 19:21:30,278 INFO  [main] index.IndexCollection (IndexCollection.java:874) - Indexing Complete! 158,226 documents indexed
2020-06-23 19:21:30,278 INFO  [main] index.IndexCollection (IndexCollection.java:875) - ============ Final Counter Values ============
2020-06-23 19:21:30,278 INFO  [main] index.IndexCollection (IndexCollection.java:876) - indexed:          158,226
2020-06-23 19:21:30,278 INFO  [main] index.IndexCollection (IndexCollection.java:877) - unindexable:            0
2020-06-23 19:21:30,279 INFO  [main] index.IndexCollection (IndexCollection.java:878) - empty:                 42
2020-06-23 19:21:30,279 INFO  [main] index.IndexCollection (IndexCollection.java:879) - skipped:                6
2020-06-23 19:21:30,279 INFO  [main] index.IndexCollection (IndexCollection.java:880) - errors:                 0
2020-06-23 19:21:30,284 INFO  [main] index.IndexCollection (IndexCollection.java:883) - Total 158,226 documents indexed in 00:02:51
```

The `contents` field of each Lucene document is a concatenation of the article's title and abstract.

### Full-Text

We can index the full text, with `Cord19FullTextCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection Cord19FullTextCollection -generator Cord19Generator \
  -threads 8 -input "${DATA_DIR}" \
  -index indexes/lucene-index-cord19-full-text-"${DATE}" \
  -storePositions -storeDocvectors -storeContents -storeRaw -optimize > logs/log.cord19-full-text.${DATE}.txt
```

The log should end with something like this:

```bash
2020-06-23 19:29:03,713 INFO  [main] index.IndexCollection (IndexCollection.java:874) - Indexing Complete! 158,227 documents indexed
2020-06-23 19:29:03,713 INFO  [main] index.IndexCollection (IndexCollection.java:875) - ============ Final Counter Values ============
2020-06-23 19:29:03,713 INFO  [main] index.IndexCollection (IndexCollection.java:876) - indexed:          158,227
2020-06-23 19:29:03,714 INFO  [main] index.IndexCollection (IndexCollection.java:877) - unindexable:            0
2020-06-23 19:29:03,714 INFO  [main] index.IndexCollection (IndexCollection.java:878) - empty:                 41
2020-06-23 19:29:03,714 INFO  [main] index.IndexCollection (IndexCollection.java:879) - skipped:                6
2020-06-23 19:29:03,714 INFO  [main] index.IndexCollection (IndexCollection.java:880) - errors:                 0
2020-06-23 19:29:03,718 INFO  [main] index.IndexCollection (IndexCollection.java:883) - Total 158,227 documents indexed in 00:07:32
```

The `contents` field of each Lucene document is a concatenation of the article's title and abstract, and the full text JSON (if available).

### Paragraph

We can build a paragraph index with `Cord19ParagraphCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection Cord19ParagraphCollection -generator Cord19Generator \
  -threads 8 -input "${DATA_DIR}" \
  -index indexes/lucene-index-cord19-paragraph-"${DATE}" \
  -storePositions -storeDocvectors -storeContents -storeRaw -optimize > logs/log.cord19-paragraph.${DATE}.txt
```

The log should end with something like this:

```bash
2020-06-23 19:50:02,419 INFO  [main] index.IndexCollection (IndexCollection.java:874) - Indexing Complete! 2,781,172 documents indexed
2020-06-23 19:50:02,420 INFO  [main] index.IndexCollection (IndexCollection.java:875) - ============ Final Counter Values ============
2020-06-23 19:50:02,420 INFO  [main] index.IndexCollection (IndexCollection.java:876) - indexed:        2,781,172
2020-06-23 19:50:02,420 INFO  [main] index.IndexCollection (IndexCollection.java:877) - unindexable:            0
2020-06-23 19:50:02,420 INFO  [main] index.IndexCollection (IndexCollection.java:878) - empty:                 42
2020-06-23 19:50:02,420 INFO  [main] index.IndexCollection (IndexCollection.java:879) - skipped:            2,660
2020-06-23 19:50:02,420 INFO  [main] index.IndexCollection (IndexCollection.java:880) - errors:                 0
2020-06-23 19:50:02,425 INFO  [main] index.IndexCollection (IndexCollection.java:883) - Total 2,781,172 documents indexed in 00:20:57
```

In this configuration, the indexer creates multiple Lucene Documents for each source article:

+ `docid`: title + abstract
+ `docid.00001`: title + abstract + 1st paragraph
+ `docid.00002`: title + abstract + 2nd paragraph
+ `docid.00003`: title + abstract + 3rd paragraph
+ ...

The suffix of the `docid`, `.XXXXX` identifies which paragraph is being indexed.
The original raw JSON full text is stored in the `raw` field of `docid` (without the suffix).

## Pre-Built Indexes (All Versions)

All versions of pre-built indexes:

| Version    | Type      | Size  | Link | Checksum |
|:-----------|:----------|:------|:-----|:---------|
| 2020-06-12 | Abstract  |  1.9G | [[Dropbox]](https://www.dropbox.com/s/7uy406atbcu7f2l/lucene-index-cord19-abstract-2020-06-12.tar.gz)  | `e0d9d312a83d67c21069717957a56f47`
| 2020-06-12 | Full-Text |  3.7G | [[Dropbox]](https://www.dropbox.com/s/glh8n0c3odd6prm/lucene-index-cord19-full-text-2020-06-12.tar.gz) | `72018ee46556cc72d01885203ea386dc`
| 2020-06-12 | Paragraph |  5.3G | [[Dropbox]](https://www.dropbox.com/s/cbjxc89ti4fd218/lucene-index-cord19-paragraph-2020-06-12.tar.gz) | `72732d298885c2c317236af33b08197c`
| 2020-05-26 | Abstract  |  1.7G | [[Dropbox]](https://www.dropbox.com/s/w1q4dqe6fz3derg/lucene-index-cord19-abstract-2020-05-26.tar.gz)  | `2dc054f4ca7db281e9f5e0d4836df14c`
| 2020-05-26 | Full-Text |  3.3G | [[Dropbox]](https://www.dropbox.com/s/ro8qb6al692po1r/lucene-index-cord19-full-text-2020-05-26.tar.gz) | `9b9fd4b97f75fa295e3345d0cf7914e3`
| 2020-05-26 | Paragraph |  4.7G | [[Dropbox]](https://www.dropbox.com/s/ng4hwlr9414o4ju/lucene-index-cord19-paragraph-2020-05-26.tar.gz) | `72eb265c1c9983f02f1e79a2ba19befb`
| 2020-05-19 | Abstract  |  1.7G | [[Dropbox]](https://www.dropbox.com/s/3ld34ms35zfb4m9/lucene-index-cord19-abstract-2020-05-19.tar.gz)  | `37bb97d0c41d650ba8e135fd75ae8fd8`
| 2020-05-19 | Full-Text |  3.3G | [[Dropbox]](https://www.dropbox.com/s/qih3tjsir3xulrn/lucene-index-cord19-full-text-2020-05-19.tar.gz) | `f5711915a66cd2b511e0fb8d03e4c325`
| 2020-05-19 | Paragraph |  4.9G | [[Dropbox]](https://www.dropbox.com/s/7z8szogu5neuhqe/lucene-index-cord19-paragraph-2020-05-19.tar.gz) | `012ab1f804382b2275c433a74d7d31f2`
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

## Known Issues

+ Release of 2020/05/19: Missing URLs for several articles due to a known issue with the CORD-19 dataset release.
