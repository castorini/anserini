# Anserini Experiments on Washington Post Collection

**Indexing**:

```
nohup sh target/appassembler/bin/IndexCollection -collection WashingtonPostCollection \
 -input /path/to/wapo_data -generator LuceneDocumentGenerator \
 -index lucene-index.wapo.pos+docvectors -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -optimize \
 >& log.wapo.pos+docvectors+rawdocs &
```

The directory `/path/to/wapo_data` should be the directory that contains a collection of Washington post corpus files, i.e., ` ls /path/to/wapo_data` should bring up all corpus files, e.g ` TREC_article_2012.txt`. The command above builds a standard positional index 
(`-storePositions`) that's optimized into a single segment (`-optimize`). If you also want to store document vectors 
(e.g., for query expansion), add the `-docvectors` option.  The above command builds an index that stores term positions 
(`-storePositions`) as well as doc vectors for relevance feedback (`-storeDocvectors`), and `-optimize` force merges all 
index segment into one.

**Search**:

After indexing is done, you should be able to perform a retrieval run:

```
sh target/appassembler/bin/SearchCollection \
-topicreader Trec -index lucene-index.wapo.pos+docvectors \
-bm25 -topics src/main/resources/topics-and-qrels/topics.web.251-300.txt  \
-output run.wapo.251-300.bm25.txt
```

For the retrieval model: specify `-bm25` to use BM25, `-ql` to use query likelihood, and add `-rm3` to invoke the RM3 
relevance feedback model (requires docvectors index).

