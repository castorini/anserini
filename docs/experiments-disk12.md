# Anserini Experiments on Disk12

See http://trec.nist.gov/data/test_coll.html

**Indexing**:

```
nohup sh target/appassembler/bin/IndexCollection -collection Trec -input /path/to/disk12/ \
 -index lucene-index.disk12.pos -threads 32 -positions -optimize \
 2> log.disk12.pos.emptyDocids.txt 1> log.disk12.pos.recordCounts.txt &
```


The directory `/path/to/disk12/` should be the root directory of Disk1 and Disk2 collection, i.e., `ls /path/to/disk1/` should bring up subdirectories like `doe`, `wsj`. The command above builds a standard positional index (`-positions`) that's optimized into a single segment (`-optimize`). If you also want to store document vectors (e.g., for query expansion), add the `-docvectors` option.

_Hint:_ You can use the `DumpIndex` utility to print out the statistics of the index. Please refer to [DumpIndex References](dumpindex-reference.md) for the statistics of the index


**Search**:

After indexing is done, you should be able to perform a retrieval run:

```
sh target/appassembler/bin/SearchWebCollection -topicreader Trec -index lucene-index.disk12.pos -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.51-100.txt -output run.disk12.51-100.bm25.txt
```

**Evaluate**:

Evaluation can be done using `trec_eval`:
```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.51-100.txt run.disk12.51-100.bm25.txt
```

**Effectiveness Reference**:

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
