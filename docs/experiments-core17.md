# Anserini Experiments on TREC Core

Indexing:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCoreCollection \
 -input /path/to/nyt_corpus/ -generator JsoupGenerator \
 -index lucene-index.core.pos+docvectors -threads 16 -storePositions -storeDocvectors -optimize \
 > log.core.pos+docvectors &

```

The directory `/path/to/nyt_corpus/` should be the root directory of TREC Core collection, i.e., `ls /path/to/nyt_corpus/` 
should bring up a bunch of subdirectories, `1987` to `2007`. The command above builds a standard positional index 
(`-storePositions`) that's optimized into a single segment (`-optimize`). If you also want to store document vectors 
(e.g., for query expansion), add the `-docvectors` option.  The above command builds an index that stores term positions 
(`-storePositions`) as well as doc vectors for relevance feedback (`-storeDocvectors`), and `-optimize` force merges all 
index segment into one.

After indexing is done, you should be able to perform a retrieval as follows:

```
sh target/appassembler/bin/SearchWebCollection \
  -topicreader Trec -index lucene-index.core.pos+docvectors -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.core.300-450.600-700.bm25.txt
```

For the retrieval model: specify `-bm25` to use BM25, `-ql` to use query likelihood, and add `-rm3` to invoke the RM3 
relevance feedback model (requires docvectors index).

