# Approximate Nearest Neighbor
How to use
==========
Anserini can be used to index and search dense vectors using either _"fake words"_ or _"lexical LSH"_ encodings.

Fake Words Encoding
==========
1. split an input vector `v` into separate tokens `f_i`, one for each feature
2. quantize the value `r_i` of each _feature token_ `f_i` by a (configurable) integer factor `q`, `int t_i = r_i * q` 
3. create `t_i` fake word tokens with the same text value `f_i`
 
Lexical LSH Encoding
==========
1. split an input vector `v` into separate tokens `f_i`, one for each feature
2. truncate the value `r_i` of each _feature token_ `f_i` up until the first decimal place
3. add a _feature index_ prefix to each value `r_i` (e.g. token _0.1_ of feature at column 2 becomes _2_0.1_)
4. optionally aggregate _feature tokens_ in n-grams
5. pass the feature / n-gram tokens to a LSH filter (Lucene `MinHashFilter`)   

Indexing Word Vectors from GloVe with Fake Words Encoding
==========
```
anserini/target/appassembler/bin/IndexVectors -input glove.6B.300d.txt -path glove300-fw -encoding fw -fw.q 60

loading model glove.6B.300d.txt
creating index at glove300-fw
100000 words added
200000 words added
300000 words added
400000 words added
400000 words indexed
index size: 204MB
```

Approximate Nearest Word Search on GloVe Vectors with Fake Words Encoding
==========
```
anserini/target/appassembler/bin/ApproximateNearestNeighborSearch -input glove.6B.300d.txt -path glove300-fw -encoding fw -fw.q 60 -word italy -cutoff 0.2

loading model glove.6B.300d.txt
reading index at glove300-fw
top 10 neighbors of 'italy' (3502ms): [rome, spain, greece, portugal, bulgaria, italian, romania, europe, belgium, italy]
```

Indexing Word Vectors from GloVe with Lexical LSH Encoding
==========
```
anserini/appassembler/bin/IndexVectors -input glove.6B.300d.txt -path glove300-ll -encoding lexlsh
loading model glove.6B.300d.txt
creating index at glove300-ll
100000 words added
200000 words added
300000 words added
400000 words added
400000 words indexed
index size: 173MB
```

Approximate Nearest Word Search on GloVe Vectors with Lexical LSH Encoding
==========
```
anserini/target/appassembler/bin/ApproximateNearestNeighborSearch -input glove.6B.300d.txt -path glove300-ll -encoding lexlsh -word italy -cutoff 0.2
loading model glove.6B.300d.txt
reading index at glove300-ll
top 10 neighbors of 'italy' (3799ms): [rome, venice, switzerland, portugal, padua, bologna, italian, sicily, milan, italy]
```

Notes
==========
Check the [Colab demo](https://colab.research.google.com/drive/1PBrAlthWslK4DBeyMC_GA84vYo00OiYn).

Please refer to the paper [[Teofili et al, 2019] Teofili, T., and Lin, J. (2019). Lucene for Approximate Nearest-NeighborsSearch on Arbitrary Dense Vectors.]() for more details.