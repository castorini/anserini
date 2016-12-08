# Train a Word2Vec model

With `TrainW2V` you can train a word2vec model from raw text
Possible parameters are:

```
-input (required)
```

Path of the raw file

```
-output (required)
```

Path of the output model

```
-dimension (optional: positive integer)
```

Desired dimension of the vectors (default: 100)

```
-iter (optional: positive integer)
```

Number of iterations (default: 1)

```
-minfreq (optional: positive integer)
```

Minimum term frequency (default: 5).

_NOTICE:_ any term with lower frequency than the specified frequency will not be considered while training


```
-seed (optional: positive integer)
```

Seed size (default: 42)


```
-window (optional: positive integer)
```

Window size (default: 5)


# Search a Word2Vec model

With `SearchW2V` you can train a word2vec model from raw text
Possible parameters are:

```
-model
```

Path of the word2vec model

```
-gmodel
```

Specify if the model was trained with Google format.


```
-input (optional)
```

Path to the input file. Note that the file should have a word/line format.

```
-term (optional)
```

Input a word to find its word embedding.
_NOTICE:_ both term and input file can't be empty

```
-output (optional)
```

Path of the output file.

```
-nearest (optional: positive integer)
```

Number of nearest word embedding (default: 10)
