# Anserini: 20-Newsgroup

This page contains instructions for how to index the 20-Newsgroup dataset.

## Data Prep

We're going to use `20-newsgroup/` as the working directory.
First, we need to download and extract the dataset:

```sh
mkdir 20-newsgroup/
wget -nc http://qwone.com/~jason/20Newsgroups/20news-bydate.tar.gz -P 20-newsgroup
tar -xvzf 20-newsgroup/20news-bydate.tar.gz -C 20-newsgroup
```

To confirm, `20news-bydate.tar.gz` should have MD5 checksum of `d6e9e45cb8cb77ec5276dfa6dfc14318`.

After untaring, you should see the following two folders:
```
ls 20-newsgroup/20news-bydate-test
ls 20-newsgroup/20news-bydate-train
```

There are docs with the same id in different categories.
For example, doc `123` can exists in `misc.forsale` & `sci.crypt` even if the two docs have different text. Hence we need prune the dataset by ensuring each doc has a unique id.
To prune and merge them into one folder:
```
python src/main/python/20-newsgroup/prune_and_merge.py --paths 20-newsgroup/20news-bydate-test 20-newsgroup/20news-bydate-train --out 20-newsgroup/20news-bydate
```

Now you should see train & test merged into one folder in `20-newsgroup/20news-bydate/`.

# Indexing

To index train & test together:
```
sh target/appassembler/bin/IndexCollection -collection TwentyNewsgroupsCollection \
 -input 20-newsgroup/20news-bydate \
 -index 20-newsgroup/lucene-index.20newsgroup.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator -threads 2 \
 -storePositions -storeDocvectors -storeRaw
```

To index the train set:
```
sh target/appassembler/bin/IndexCollection -collection TwentyNewsgroupsCollection \
 -input 20-newsgroup/20news-bydate-train \
 -index 20-newsgroup/lucene-index.20newsgroup.train.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator -threads 2 \
 -storePositions -storeDocvectors -storeRaw
```

To index the test set:
```
sh target/appassembler/bin/IndexCollection -collection TwentyNewsgroupsCollection \
 -input 20-newsgroup/20news-bydate-test \
 -index 20-newsgroup/lucene-index.20newsgroup.test.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator -threads 2 \
 -storePositions -storeDocvectors -storeRaw
```

|               | Index Duration  | # of docs |
|---------------|-----------------|-----------|
| Train         | ~12 seconds     | 11,314    |
| Test          | ~6 seconds      | 7,532     |
| Train + Test  | ~15 seconds     | 18,846    |
