# Anserini Experiments on TREC Complext Answer Retrieval

Indexing:

```
nohup sh target/appassembler/bin/IndexCollection -collection CarCollection -input /path/to/car_corpus/ \
  -generator LuceneDocumentGenerator -index lucene-index-all.car18 -threads 80 -storePositions \
  -optimize >& log_all.car18 &
```

The directory `/path/to/car_corpus/` should be the root directory of TREC CAR paragraph collection, 
i.e., `ls /path/to/car_corpus/` contains the `*paragraph.cbor` file. 
The command above builds a standard positional index (`-storePositions`) that's optimized into a 
single segment (`-optimize`). If you also want to store document vectors (e.g., for query expansion), 
add the `-docvectors` option.  The above command builds an index that stores term positions 
(`-storePositions`) as well as doc vectors for relevance feedback (`-storeDocvectors`), and 
`-optimize` force merges all index segment into one.

After indexing is done, you should be able to perform a retrieval as follows:

```
sh target/appassembler/bin/SearchCollection -topicreader Car -index lucene-index-all.car18 \
  -bm25 -topics /path/to/car_topics -output run.car18.bm25.txt -hits 1000
```

to run retrieval on the topic file or 

```
sh target/appassembler/bin/SearchCollection -topicreader CarTitle -index lucene-index-all.car18 \
  -bm25 -topics /path/to/car_titles -output run.car18_title.bm25.txt -hits 1000
```

to run retrieval on the title file.

For the retrieval model: specify `-bm25` to use BM25, `-ql` to use query likelihood, and add `-rm3` to invoke the RM3 
relevance feedback model (requires docvectors index).

