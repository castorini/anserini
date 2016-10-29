# Anserini Experiments on Gov2

Indexing:

```
nohup sh target/appassembler/bin/IndexCollection -collection Gov2 -input /path/to/gov2/ \
 -index lucene-index.gov2.pos -threads 32 -positions -optimize \
 2> log.gov2.pos.emptyDocids.txt 1> log.gov2.pos.recordCounts.txt &
```

The directory `/path/to/gov2/` should be the root directory of Gov2 collection, i.e., `ls /path/to/gov2/` should bring up a bunch of subdirectories, `GX000` to `GX272`. The command above builds a standard positional index (`-positions`) that's optimized into a single segment (`-optimize`). If you also want to store document vectors (e.g., for query expansion), add the `-docvectors` option.

After indexing is done, you should be able to perform a retrieval run:

```
sh target/appassembler/bin/SearchWebCollection -topicreader Trec -index lucene-index.gov2.pos -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.701-750.txt -output run.gov2.701-750.bm25.txt
```

For the retrieval model: specify `-bm25` to use BM25, `-ql` to use query likelihood, and add `-rm3` to invoke the RM3 relevance feedback model (requires docvectors index).

A copy of `trec_eval` is included in `eval/`. Unpack and compile it. Then you can evaluate the runs:

```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.701-750.txt run.gov2.701-750.bm25.txt
```

With the topics and qrels in `src/main/resources/topics-and-qrels/`, you should be able to replicate the following results:


MAP                                                                                     | BM25   |BM25+RM3| QL     | QL+RM3
----------------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2004 Terabyte Track: Topics 701-750](http://trec.nist.gov/data/terabyte04.html)   | 0.2673 | 0.2952 | 0.2636 | 0.2800
[TREC 2005 Terabyte Track: Topics 751-800](http://trec.nist.gov/data/terabyte05.html)   | 0.3364 | 0.3839 | 0.3263 | 0.3628
[TREC 2006 Terabyte Track: Topics 801-850](http://trec.nist.gov/data/terabyte06.html)   | 0.3053 | 0.3408 | 0.2956 | 0.3198


P30                                                                                     | BM25   |BM25+RM3|  QL    | QL+RM3
----------------------------------------------------------------------------------------|--------|--------|--------|--------
[TREC 2004 Terabyte Track: Topics 701-750](http://trec.nist.gov/data/terabyte04.html)   | 0.4850 | 0.5306 | 0.4673 | 0.4850
[TREC 2005 Terabyte Track: Topics 751-800](http://trec.nist.gov/data/terabyte05.html)   | 0.5520 | 0.5927 | 0.5167 | 0.5673
[TREC 2006 Terabyte Track: Topics 801-850](http://trec.nist.gov/data/terabyte06.html)   | 0.4913 | 0.5253 | 0.4760 | 0.4873

