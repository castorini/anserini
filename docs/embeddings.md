# Building a Lucene index for a Word2Vec model

Since the pre-trained word2vec models are often in `.bin` format, first convert the existing model into 
.tsv format. You can do this using one of the existing tools such as [this](https://github.com/marekrei/convertvec.git).

With `IndexW2v` you can build a Lucene index for a word2vec model from raw text.

Possible parameters are:

```
-input (required)
```

Path of the raw file

```
-index (required)
```

Path of the index file

Example command:

```
sh target/appassembler/bin/IndexW2V -input GoogleNews-vectors-negative300.txt -index lucene.GoogleNews.index 
```

# Search the index for embeddings

With `SearchW2V` you can search for the word embeddings.

Possible parameters are:

```
-index (required)
```

Path of the index file

```
-term (required)
```

Get the embeddings corresponding to the term

Example command:

```
sh target/appassembler/bin/SearchW2V -index lucene.GoogleNews.index -term "hello"
```