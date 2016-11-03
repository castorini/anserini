# Anserini Experiments on Disk12

See http://trec.nist.gov/data/test_coll.html

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

MAP                                   | BM25   | QL     
--------------------------------------|--------|--------
TREC-1 Ad Hoc Track: Topics 51-100    | 0.2224 | 0.2165 
TREC-2 Ad Hoc Track: Topics 101-150   | 0.2008 | 0.2017 
TREC-3 Ad Hoc Track: Topics 151-200   | 0.2570 | 0.2275 
**Mean**.                             | **0.2267** | **0.2152** 


P30                                   | BM25   | QL     
--------------------------------------|--------|--------
TREC-1 Ad Hoc Track: Topics 51-100    | 0.4473 | 0.4447 
TREC-2 Ad Hoc Track: Topics 101-150   | 0.4220 | 0.4180 
TREC-3 Ad Hoc Track: Topics 151-200   | 0.4707 | 0.4247
**Mean.**                                | **0.4467** | **0.4291** 
