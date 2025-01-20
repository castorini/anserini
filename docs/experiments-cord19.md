# Indexing the COVID-19 Open Research Dataset

This document describes how to use Anserini to index the [COVID-19 Open Research Dataset (CORD-19)](https://pages.semanticscholar.org/coronavirus-research) from the [Allen Institute for AI](https://allenai.org/).

**Note**: With the conclusion of the [TREC-COVID challenge](https://ir.nist.gov/covidSubmit/), we no longer have the resources to keep this page up to date with the latest distribution of CORD-19.
The state of this page has mostly remained unchanged since mid-July 2020, when we built the indexes for Round 5 of the TREC-COVID challenge, with some updates in mid-November 2020.

For an easy way to get started, check out our (unfortunately, out of date) Colab demos, also available [here](https://github.com/castorini/anserini-notebooks):

+ [Colab demo using the title + abstract index](https://github.com/castorini/anserini-notebooks/blob/master/pyserini_covid19_default.ipynb)
+ [Colab demo using the paragraph index](https://github.com/castorini/anserini-notebooks/blob/master/pyserini_covid19_paragraph.ipynb)
+ [Colab demo that demonstrates integration with SciBERT](https://github.com/castorini/anserini-notebooks/blob/master/Pyserini+SciBERT_on_COVID_19_Demo.ipynb)

We provide instructions on how to build Lucene indexes for the collection using Anserini below, but if you don't want to bother building the indexes yourself, we have pre-built indexes that you can directly download:

| Version    | Type      | Size  |
|:-----------|:----------|:------|
| 2020-07-16 | Abstract  |  2.1G |
| 2020-07-16 | Full-Text |  4.1G |
| 2020-07-16 | Paragraph |  5.9G |

"Size" refers to the output of `ls -lh`, "Version" refers to the dataset release date from AI2.
For our answer to the question, "which one should I use?" see below.

We've kept around older versions of indexes for archival purposes &mdash; scroll all the way down to the bottom of the page to see those.

Note that starting 2020/05/27, AI2 switched to daily releases of CORD-19 (from weekly), and as a result, it has become impractical to share pre-built indexes for every single update.
Thus, we will only be providing pre-built indexes "occasionally".

However, we have written a simple script that will largely automate all the instructions on this page:

```
$ python src/main/python/trec-covid/index_cord19.py --date 2020-07-16 --all
```

This script was updated in mid-November 2020 (on the July distribution above and the latest version available) and has been verified to work at the time.

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

CORD-19 release: 2020-07-16
Topics/Qrels: TREC-COVID Round 3
Whitelist: TREC-COVID Round 3 valid docids

                    NDCG@10  Judged@10
Abstract index       0.5431    0.8675
Full-text index      0.3379    0.6625
Paragraph index      0.4923    0.8575
```

The instructions below walk through, essentially, what the script does, step by step.

## Data Prep

These instructions work with the dataset release from 2020/07/16.
First, download the data:

```bash
DATE=2020-07-16

wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/historical_releases/cord-19_"${DATE}".tar.gz

tar xvfz cord-19_"${DATE}".tar.gz -C collections
tar xvfz collections/"${DATE}"/document_parses.tar.gz -C collections/"${DATE}"
mv collections/"${DATE}" collections/cord19-"${DATE}"
```

## Building Local Lucene Indexes

We can now index this corpus using Anserini.
Currently, we have implemented three different variants, described below.
For a sense of how these different methods stack up, refer to the following paper:

+ Jimmy Lin. [Is Searching Full Text More Effective Than Searching Abstracts?](https://bmcbioinformatics.biomedcentral.com/articles/10.1186/1471-2105-10-46) BMC Bioinformatics, 10:46 (3 February 2009).

The tl;dr &mdash; we'd recommend getting started with abstract index since it's the smallest in size and easiest to manipulate. Paragraph indexing is likely to be more effective (i.e., better search results), but a bit more difficult to manipulate since some deduping is required to post-process the raw hits (since multiple paragraphs from the same article might be retrieved).
The full-text index overly biases long documents and isn't really effective; this condition is included here only for completeness.

Note that as of TREC-COVID Round 1, there is some evidence that the abstract index is more effective for search, see results of experiments [here](experiments-covid.md).
(Update, mid-November 2020: this statement was made back after Round 1; there is now considerably more evidence regarding the effectiveness of each approach; see link for details.)

### Abstract

We can index abstracts (and titles, of course) with `Cord19AbstractCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection Cord19AbstractCollection -generator Cord19Generator \
  -threads 8 -input collections/cord19-"${DATE}" \
  -index indexes/lucene-index-cord19-abstract-"${DATE}" \
  -storePositions -storeDocvectors -storeContents -storeRaw -optimize > logs/log.cord19-abstract.${DATE}.txt
```

The log should end with something like this:

```bash
2020-07-22 08:15:07,372 INFO  [main] index.IndexCollection (IndexCollection.java:874) - Indexing Complete! 192,459 documents indexed
2020-07-22 08:15:07,372 INFO  [main] index.IndexCollection (IndexCollection.java:875) - ============ Final Counter Values ============
2020-07-22 08:15:07,372 INFO  [main] index.IndexCollection (IndexCollection.java:876) - indexed:          192,459
2020-07-22 08:15:07,372 INFO  [main] index.IndexCollection (IndexCollection.java:877) - unindexable:            0
2020-07-22 08:15:07,372 INFO  [main] index.IndexCollection (IndexCollection.java:878) - empty:                 44
2020-07-22 08:15:07,372 INFO  [main] index.IndexCollection (IndexCollection.java:879) - skipped:                6
2020-07-22 08:15:07,372 INFO  [main] index.IndexCollection (IndexCollection.java:880) - errors:                 0
2020-07-22 08:15:07,378 INFO  [main] index.IndexCollection (IndexCollection.java:883) - Total 192,459 documents indexed in 00:02:46
```

The `contents` field of each Lucene document is a concatenation of the article's title and abstract.

### Full-Text

We can index the full text, with `Cord19FullTextCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection Cord19FullTextCollection -generator Cord19Generator \
  -threads 8 -input collections/cord19-"${DATE}" \
  -index indexes/lucene-index-cord19-full-text-"${DATE}" \
  -storePositions -storeDocvectors -storeContents -storeRaw -optimize > logs/log.cord19-full-text.${DATE}.txt
```

The log should end with something like this:

```bash
2020-07-22 08:23:04,801 INFO  [main] index.IndexCollection (IndexCollection.java:874) - Indexing Complete! 192,460 documents indexed
2020-07-22 08:23:04,801 INFO  [main] index.IndexCollection (IndexCollection.java:875) - ============ Final Counter Values ============
2020-07-22 08:23:04,801 INFO  [main] index.IndexCollection (IndexCollection.java:876) - indexed:          192,460
2020-07-22 08:23:04,801 INFO  [main] index.IndexCollection (IndexCollection.java:877) - unindexable:            0
2020-07-22 08:23:04,801 INFO  [main] index.IndexCollection (IndexCollection.java:878) - empty:                 43
2020-07-22 08:23:04,801 INFO  [main] index.IndexCollection (IndexCollection.java:879) - skipped:                6
2020-07-22 08:23:04,801 INFO  [main] index.IndexCollection (IndexCollection.java:880) - errors:                 0
2020-07-22 08:23:04,806 INFO  [main] index.IndexCollection (IndexCollection.java:883) - Total 192,460 documents indexed in 00:07:56
```

The `contents` field of each Lucene document is a concatenation of the article's title and abstract, and the full text JSON (if available).

### Paragraph

We can build a paragraph index with `Cord19ParagraphCollection`, as follows:

```bash
sh target/appassembler/bin/IndexCollection \
  -collection Cord19ParagraphCollection -generator Cord19Generator \
  -threads 8 -input collections/cord19-"${DATE}" \
  -index indexes/lucene-index-cord19-paragraph-"${DATE}" \
  -storePositions -storeDocvectors -storeContents -storeRaw -optimize > logs/log.cord19-paragraph.${DATE}.txt
```

The log should end with something like this:

```bash
2020-07-22 08:46:57,535 INFO  [main] index.IndexCollection (IndexCollection.java:874) - Indexing Complete! 3,010,497 documents indexed
2020-07-22 08:46:57,535 INFO  [main] index.IndexCollection (IndexCollection.java:875) - ============ Final Counter Values ============
2020-07-22 08:46:57,536 INFO  [main] index.IndexCollection (IndexCollection.java:876) - indexed:        3,010,497
2020-07-22 08:46:57,536 INFO  [main] index.IndexCollection (IndexCollection.java:877) - unindexable:            0
2020-07-22 08:46:57,536 INFO  [main] index.IndexCollection (IndexCollection.java:878) - empty:                 44
2020-07-22 08:46:57,536 INFO  [main] index.IndexCollection (IndexCollection.java:879) - skipped:            2,660
2020-07-22 08:46:57,536 INFO  [main] index.IndexCollection (IndexCollection.java:880) - errors:                 0
2020-07-22 08:46:57,543 INFO  [main] index.IndexCollection (IndexCollection.java:883) - Total 3,010,497 documents indexed in 00:23:51
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

| Version    | Type      | Size  |
|:-----------|:----------|:------|
| 2020-07-16 | Abstract  |  2.1G |
| 2020-07-16 | Full-Text |  4.1G |
| 2020-07-16 | Paragraph |  5.9G |
| 2020-06-19 | Abstract  |  2.0G |
| 2020-06-19 | Full-Text |  3.8G |
| 2020-06-19 | Paragraph |  5.5G |
| 2020-06-12 | Abstract  |  1.9G |
| 2020-06-12 | Full-Text |  3.7G |
| 2020-06-12 | Paragraph |  5.3G |
| 2020-05-26 | Abstract  |  1.7G |
| 2020-05-26 | Full-Text |  3.3G |
| 2020-05-26 | Paragraph |  4.7G |
| 2020-05-19 | Abstract  |  1.7G |
| 2020-05-19 | Full-Text |  3.3G |
| 2020-05-19 | Paragraph |  4.9G |
| 2020-05-12 | Abstract  |  1.3G |
| 2020-05-12 | Full-Text |  2.5G |
| 2020-05-12 | Paragraph |  3.6G |
| 2020-05-01 | Abstract  |  1.2G |
| 2020-05-01 | Full-Text |  2.4G |
| 2020-05-01 | Paragraph |  3.6G |
| 2020-04-24 | Abstract  |  1.3G |
| 2020-04-24 | Full-Text |  2.4G |
| 2020-04-24 | Paragraph |  5.0G |
| 2020-04-17 | Abstract  |  1.2G |
| 2020-04-17 | Full-Text |  2.2G |
| 2020-04-17 | Paragraph |  4.7G |
| 2020-04-10 | Abstract  |  1.2G |
| 2020-04-10 | Full-Text |  3.3G |
| 2020-04-10 | Paragraph |  3.4G |
| 2020-04-03 | Abstract  |  1.1G |
| 2020-04-03 | Full-Text |  3.0G |
| 2020-04-03 | Paragraph |  3.1G |
| 2020-03-27 | Abstract  |  1.1G |
| 2020-03-27 | Full-Text |  2.9G |
| 2020-03-27 | Paragraph |  3.1G |
| 2020-03-20 | Abstract  |  1.0G |
| 2020-03-20 | Full-Text |  2.6G |
| 2020-03-20 | Paragraph |  2.9G |

These indexes are also mirrored [here](https://git.uwaterloo.ca/jimmylin/cord19-indexes/).

## Known Issues

+ Release of 2020/05/19: Missing URLs for several articles due to a known issue with the CORD-19 dataset release.
