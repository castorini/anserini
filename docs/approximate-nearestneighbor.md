# Approximate Nearest-Neighbor Search

With the advent of deep learning and neural approaches to both natural language processing and information retrieval, there is frequently the need to perform approximate nearest-neighbor search on dense vectors.
However, Lucene is built around inverted indexes of a document collection’s (sparse) term–document matrix, which is incompatible with the lower-dimensional dense vectors that are common in deep learning applications.

To address this gap, we propose techniques that repurpose Lucene's indexing and search pipeline for this task.
That is, show how to perform approximate nearest-neighbor search on arbitrary dense vectors _directly in Lucene_.
Admittedly, our solutions lack elegance (i.e., they're a bit janky), but they get the job done.
We demonstrate finding similar word embedding vectors (word2vec and GloVe) as a sample application.

Anserini provides implementations of two different techniques: _"fake words"_ or _"lexical LSH"_ encodings.
Additional details, please consult:

+ Tommaso Teofili and Jimmy Lin. [Lucene for Approximate Nearest-Neighbors Search on Arbitrary Dense Vectors](https://arxiv.org/abs/1910.10208) _arXiv:1910.10208_, October 2019.

Our paper actually describes a third technique using Lucene's built-in k-d trees, but the results are so terrible that the implementaiton is not worth including.
Also, check our [Colab demo](https://colab.research.google.com/drive/1PBrAlthWslK4DBeyMC_GA84vYo00OiYn).

## Overview of Techniques

### "Fake Words" Encoding

1. split an input vector _v_ into separate tokens _f_<sub>i</sub>, one for each feature
2. quantize the value _r_<sub>i</sub> of each _feature token_ _f_<sub>i</sub> by a (configurable) integer factor _q_, `int` _t_<sub>i</sub> = _r_<sub>i</sub> * _q_ 
3. create _t_<sub>i</sub> fake word tokens with the same text value `_f_<sub>i</sub>
 
### "Lexical LSH" Encoding

1. split an input vector _v_ into separate tokens _f_<sub>i</sub>, one for each feature
2. truncate the value _r_<sub>i</sub> of each _feature token_ _f_<sub>i</sub> up until the first decimal place
3. add a _feature index_ prefix to each value _r_<sub>i</sub> (e.g. token 0.1 of feature at column 2 becomes `2_0.1`)
4. optionally aggregate _feature tokens_ in n-grams
5. pass the feature / n-gram tokens to a LSH filter (Lucene `MinHashFilter`)   

## How to Use

## "Fake Words" Encoding

Index:

```bash
$ anserini/target/appassembler/bin/IndexVectors -input glove.6B.300d.txt -path glove300-fw \
  -encoding fw -fw.q 60
loading model glove.6B.300d.txt
creating index at glove300-fw
100000 words added
200000 words added
300000 words added
400000 words added
400000 words indexed
index size: 204MB
```

Search:

```bash
$ anserini/target/appassembler/bin/ApproximateNearestNeighborSearch -input glove.6B.300d.txt -path glove300-fw \
  -encoding fw -fw.q 60 -word italy -cutoff 0.2

loading model glove.6B.300d.txt
reading index at glove300-fw
top 10 neighbors of 'italy' (3502ms): [rome, spain, greece, portugal, bulgaria, italian, romania, europe, belgium, italy]
```

## "Lexical LSH" Encoding

Index:

```
$ anserini/appassembler/bin/IndexVectors -input glove.6B.300d.txt -path glove300-ll -encoding lexlsh
loading model glove.6B.300d.txt
creating index at glove300-ll
100000 words added
200000 words added
300000 words added
400000 words added
400000 words indexed
index size: 173MB
```

Search:

```
$ anserini/target/appassembler/bin/ApproximateNearestNeighborSearch -input glove.6B.300d.txt \
  -path glove300-ll -encoding lexlsh -word italy -cutoff 0.2
loading model glove.6B.300d.txt
reading index at glove300-ll
top 10 neighbors of 'italy' (3799ms): [rome, venice, switzerland, portugal, padua, bologna, italian, sicily, milan, italy]
```
