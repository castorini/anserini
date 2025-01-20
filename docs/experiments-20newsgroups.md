# Anserini: Working the 20 Newsgroups Dataset

This page contains instructions for how to index the [20 Newsgroups dataset](http://qwone.com/~jason/20Newsgroups/).

## Data Prep

There are many versions of the 20 Newsgroups dataset available on the web, we're specifically going to use [this one](http://qwone.com/~jason/20Newsgroups/) (the "bydate" version).
We're going to use `collections/20newsgroups/` as the working directory.
First, we need to download and extract the dataset:

```bash
mkdir -p collections/20newsgroups/
wget -nc http://qwone.com/~jason/20Newsgroups/20news-bydate.tar.gz -P collections/20newsgroups
tar -xvzf collections/20newsgroups/20news-bydate.tar.gz -C collections/20newsgroups
```

To confirm, `20news-bydate.tar.gz` should have MD5 checksum of `d6e9e45cb8cb77ec5276dfa6dfc14318`.

After unpacking, you should see the following two folders:

```bash
ls collections/20newsgroups/20news-bydate-test
ls collections/20newsgroups/20news-bydate-train
```

There are docs with the same id in different categories.
For example, doc `123` exists in `misc.forsale` and `sci.crypt`, with different texts.
Since we assume unique docids when building an index, we need to clean the the dataset first.
To prune and merge both train and test splits into one folder:

```bash
python src/main/python/20newsgroups/prune_and_merge.py \
 --paths collections/20newsgroups/20news-bydate-test collections/20newsgroups/20news-bydate-train \
 --out collections/20newsgroups/20news-bydate
```

Now you should see the train and test splits merged into one folder in `collections/20newsgroups/20news-bydate/`.

## Indexing

To index train and test together:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -collection TwentyNewsgroupsCollection \
  -input collections/20newsgroups/20news-bydate \
  -index indexes/lucene-index.20newsgroups.all \
  -generator DefaultLuceneDocumentGenerator -threads 2 \
  -storePositions -storeDocvectors -storeRaw -optimize
```

To index just the train set:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -collection TwentyNewsgroupsCollection \
  -input collections/20newsgroups/20news-bydate-train \
  -index indexes/lucene-index.20newsgroups.train \
  -generator DefaultLuceneDocumentGenerator -threads 2 \
  -storePositions -storeDocvectors -storeRaw -optimize
```

To index just the test set:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -collection TwentyNewsgroupsCollection \
  -input collections/20newsgroups/20news-bydate-test \
  -index indexes/lucene-index.20newsgroups.test \
  -generator DefaultLuceneDocumentGenerator -threads 2 \
  -storePositions -storeDocvectors -storeRaw -optimize
```

Indexing should take just a few seconds.

You can check the document count (for train and test together, or train/test individually) with:

```bash
bin/run.sh io.anserini.index.IndexReaderUtils \
  -index indexes/lucene-index.20newsgroups.all \
  -stats
```

Which should output:

```
Index statistics
----------------
documents:             18846
documents (non-empty): 18846
unique terms:          165633
total terms:           4219956
```

For reference, the number of docs indexed should be exactly as follows:

|               | # of docs |
|---------------|-----------|
| Train         |    11,314 |
| Test          |     7,532 |
| Train + Test  |    18,846 |

For convenience, we also provide pre-built indexes above.

## Reproduction Log[*](reproducibility.md)

+ Results reproduced by [@stephaniewhoo](http://github.com/stephaniewhoo) on 2020-11-24 (commit [`b7f1f08`](https://github.com/castorini/anserini/commit/b7f1f08689014159c1d5b2c9b9905b363af1cbbf))
+ Results reproduced by [@b8zhong](http://github.com/b8zhong) on 2024-11-27 (commit [`a5e6771`](https://github.com/castorini/anserini/commit/a5e6771a0aedcfb1c394e345636236d536c8c57d))

