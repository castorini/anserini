# Approximate Nearest-Neighbor Search

With the advent of deep learning and neural approaches to both natural language processing and information retrieval, there is frequently the need to perform approximate nearest-neighbor search on arbitrary dense vectors.
However, Lucene is built around inverted indexes of a document collection's (sparse) term–document matrix, which is incompatible with the lower-dimensional dense vectors that are common in deep learning applications.

To address this gap, we propose techniques that repurpose Lucene's indexing and search pipeline for this task.
That is, we show how to perform approximate nearest-neighbor search on arbitrary dense vectors _directly in Lucene_.
Admittedly, our solutions lack elegance (i.e., they're a bit janky), but they get the job done.
We demonstrate finding similar word embedding vectors (specifically, [GloVe](https://nlp.stanford.edu/projects/glove/)) as a sample application.

Anserini provides implementations of two different techniques: _"fake words"_ or _"lexical LSH"_ encodings.
For additional details, please consult:

+ Tommaso Teofili and Jimmy Lin. [Lucene for Approximate Nearest-Neighbors Search on Arbitrary Dense Vectors](https://arxiv.org/abs/1910.10208) _arXiv:1910.10208_, October 2019.

Our paper actually describes a third technique using Lucene's built-in k-d trees, but the results are so terrible that the implementation is not worth including.
Also, check our [Colab demo](https://colab.research.google.com/drive/1PBrAlthWslK4DBeyMC_GA84vYo00OiYn).

## Overview of Techniques

### "Fake Words" Encoding

1. Split an input vector _v_ into separate tokens _f_<sub>i</sub>, one for each feature.
2. Quantize the value _r_<sub>i</sub> of each _feature token_ _f_<sub>i</sub> by a (configurable) integer factor _q_, (int) _t_<sub>i</sub> = _r_<sub>i</sub> × _q_.
3. Create _t_<sub>i</sub> fake word tokens with the same text value _f_<sub>i</sub>.
 
### "Lexical LSH" Encoding

1. Split an input vector _v_ into separate tokens _f_<sub>i</sub>, one for each feature.
2. Truncate the value _r_<sub>i</sub> of each _feature token_ _f_<sub>i</sub> up to the first decimal place.
3. Add a _feature index_ prefix to each value _r_<sub>i</sub> (e.g., token 0.1 of feature at column 2 becomes `2_0.1`).
4. Optionally aggregate _feature tokens_ to form _n_-grams.
5. Pass the feature or _n_-gram tokens to Lucene's LSH filter (`MinHashFilter`).

## How to Use

## "Fake Words" Encoding

Index:

```bash
$ target/appassembler/bin/IndexVectors -input glove.6B.300d.txt -path glove300-fw -encoding fw -fw.q 60
Loading model glove.6B.300d.txt
Creating index at glove300-fw...
100000 words added
200000 words added
300000 words added
400000 words added
400000 words indexed
Index size: 204MB
Total time: 00:02:11
```

Search:

```bash
$ target/appassembler/bin/ApproximateNearestNeighborSearch -input glove.6B.300d.txt \
   -path glove300-fw -encoding fw -fw.q 60 -word italy
Loading model glove.6B.300d.txt
Reading index at glove300-fw
10 nearest neighbors of 'italy':
1. italy (75.577)
2. italian (53.660)
3. romania (51.432)
4. portugal (51.312)
5. rome (51.158)
6. greece (50.518)
7. spain (49.978)
8. bulgaria (48.710)
9. europe (47.823)
10. belgium (47.761)
Search time: 417ms
```

Evaluate recall at retrieval depth=100:

```bash
$ target/appassembler/bin/ApproximateNearestNeighborEval -input glove.6B.300d.txt -path glove300-fw/ \
   -encoding fw -fw.q 60 -topics src/main/resources/topics-and-qrels/topics.robust04.txt -samples 100 -depth 100
Loading model glove.6B.300d.txt
Reading index at glove300-fw
Evaluating at retrieval depth: 100
R@100: 0.9660
avg query time: 344.67 ms
```

## "Lexical LSH" Encoding

Index:

```bash
$ target/appassembler/bin/IndexVectors -input glove.6B.300d.txt -path glove300-ll -encoding lexlsh
Loading model glove.6B.300d.txt
Creating index at glove300-ll...
100000 words added
200000 words added
300000 words added
400000 words added
400000 words indexed
Index size: 166MB
Total time: 00:03:16
```

Search:

```bash
$ target/appassembler/bin/ApproximateNearestNeighborSearch -input glove.6B.300d.txt \
   -path glove300-ll -encoding lexlsh -word italy
Loading model glove.6B.300d.txt
Reading index at glove300-ll
10 nearest neighbors of 'italy':
1. italy (544.117)
2. italian (203.952)
3. rome (158.511)
4. sicily (143.956)
5. venice (142.096)
6. padua (141.168)
7. switzerland (136.167)
8. portugal (133.959)
9. bologna (133.929)
10. milan (133.882)
Search time: 525ms
```

Evaluate recall at retrieval depth=100:

```bash
$ target/appassembler/bin/ApproximateNearestNeighborEval -input glove.6B.300d.txt -path glove300-ll/ \
   -encoding lexlsh -topics src/main/resources/topics-and-qrels/topics.robust04.txt -samples 100 -depth 100
Loading model glove.6B.300d.txt
Reading index at glove300-ll
Evaluating at retrieval depth: 100
R@100: 0.8750
avg query time: 418.83 ms
```
