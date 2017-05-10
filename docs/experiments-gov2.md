# Anserini Experiments on Gov2

Indexing:

```
nohup sh target/appassembler/bin/IndexCollection -collection Gov2Collection \
 -input /path/to/gov2/ -generator JsoupGenerator \
 -index lucene-index.gov2.pos+docvectors -threads 16 -storePositions -storeDocvectors -optimize \
 > log.gov2.pos+docvectors &

```

The directory `/path/to/gov2/` should be the root directory of Gov2 collection, i.e., `ls /path/to/gov2/` should bring 
up a bunch of subdirectories, `GX000` to `GX272`. The command above builds a standard positional index (`-storePositions`) 
that's optimized into a single segment (`-optimize`). If you also want to store document vectors (e.g., for query 
expansion), add the `-docvectors` option.  The above command builds an index that stores term positions (`-storePositions`) 
as well as doc vectors for relevance feedback (`-storeDocvectors`), and `-optimize` force merges all index segment into one.

After indexing is done, you should be able to perform a retrieval as follows:

```
sh target/appassembler/bin/SearchWebCollection \
  -topicreader Trec -index lucene-index.gov2.pos+docvectors -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.701-750.bm25.txt
```

For the retrieval model: specify `-bm25` to use BM25, `-ql` to use query likelihood, and add `-rm3` to invoke the RM3 
relevance feedback model (requires docvectors index).

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`. Use `trec_eval` to compute AP and P30:

```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.701-750.txt run.gov2.701-750.bm25.txt
```

You should be able to replicate the following results:

MAP                                                                                     | BM25   |BM25+RM3| QL     | QL+RM3
----------------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2004 Terabyte Track: Topics 701-750](http://trec.nist.gov/data/terabyte04.html)   | 0.2673 | 0.2953 | 0.2635 | 0.2800
[TREC 2005 Terabyte Track: Topics 751-800](http://trec.nist.gov/data/terabyte05.html)   | 0.3365 | 0.3837 | 0.3263 | 0.3627
[TREC 2006 Terabyte Track: Topics 801-850](http://trec.nist.gov/data/terabyte06.html)   | 0.3053 | 0.3411 | 0.2955 | 0.3199
**Mean**                                                                                | **0.3030** | **0.3400** | **0.2951** | **0.3209**


P30                                                                                     | BM25   |BM25+RM3|  QL    | QL+RM3
:---------------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2004 Terabyte Track: Topics 701-750](http://trec.nist.gov/data/terabyte04.html)   | 0.4850 | 0.5306 | 0.4673 | 0.4850
[TREC 2005 Terabyte Track: Topics 751-800](http://trec.nist.gov/data/terabyte05.html)   | 0.5520 | 0.5913 | 0.5167 | 0.5660
[TREC 2006 Terabyte Track: Topics 801-850](http://trec.nist.gov/data/terabyte06.html)   | 0.4913 | 0.5260 | 0.4760 | 0.4873
**Mean**                                                                                | **0.5094** | **0.5493** | **0.4867** | **0.5128**

