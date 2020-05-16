# Anserini: 20 Newsgroup

This page contains instructions for how to index the 20 Newsgroup dataset.

## Data Prep

We're going to use `20newsgroup/` as the working directory.
First, we need to download and extract the dataset:

```sh
mkdir collections/20newsgroup/
wget -nc http://qwone.com/~jason/20Newsgroups/20news-bydate.tar.gz -P collections/20newsgroup
tar -xvzf collections/20newsgroup/20news-bydate.tar.gz -C collections/20newsgroup
```

To confirm, `20news-bydate.tar.gz` should have MD5 checksum of `d6e9e45cb8cb77ec5276dfa6dfc14318`.

After untaring, you should see the following two folders:
```
ls collections/20newsgroup/20news-bydate-test
ls collections/20newsgroup/20news-bydate-train
```

There are docs with the same id in different categories.
For example, doc `123` can exists in `misc.forsale` & `sci.crypt` even if the two docs have different text. Hence we need prune the dataset by ensuring each doc has a unique id.
To prune and merge them into one folder:
```
python src/main/python/20newsgroup/prune_and_merge.py \
 --paths collections/20newsgroup/20news-bydate-test   \
         collections/20newsgroup/20news-bydate-train  \
 --out collections/20newsgroup/20news-bydate
```

Now you should see train & test merged into one folder in `20newsgroup/20news-bydate/`.

# Indexing

To index train & test together:
```
sh target/appassembler/bin/IndexCollection -collection TwentyNewsgroupsCollection \
 -input collections/20newsgroup/20news-bydate \
 -index indexes/lucene-index.20newsgroup.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator -threads 2 \
 -storePositions -storeDocvectors -storeRaw
```

To index the train set:
```
sh target/appassembler/bin/IndexCollection -collection TwentyNewsgroupsCollection \
 -input collections/20newsgroup/20news-bydate-train \
 -index indexes/lucene-index.20newsgroup.train.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator -threads 2 \
 -storePositions -storeDocvectors -storeRaw
```

To index the test set:
```
sh target/appassembler/bin/IndexCollection -collection TwentyNewsgroupsCollection \
 -input collections/20newsgroup/20news-bydate-test \
 -index indexes/lucene-index.20newsgroup.test.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator -threads 2 \
 -storePositions -storeDocvectors -storeRaw
```

You should see similar states as the table below.

|               | Index Duration  | # of docs |
|---------------|-----------------|-----------|
| Train         |     ~12 seconds |    11,314 |
| Test          |      ~6 seconds |     7,532 |
| Train + Test  |     ~15 seconds |    18,846 |
