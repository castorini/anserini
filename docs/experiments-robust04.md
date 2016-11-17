# Anserini Experiments of Robust04

See http://trec.nist.gov/data/t13_robust.html

**Indexing**:

```
nohup sh target/appassembler/bin/IndexCollection -collection Trec -input /path/to/disk45/ \
 -index lucene-index.disk45.pos -threads 32 -positions -optimize \
 2> log.disk45.pos.emptyDocids.txt 1> log.disk45.pos.recordCounts.txt &
```


The directory `/path/to/disk45/` should be the root directory of Disk4 and Disk5 collection, i.e., `ls /path/to/disk4/` should bring up subdirectories like `ft`, `fr94`. The command above builds a standard positional index (`-positions`) that's optimized into a single segment (`-optimize`). If you also want to store document vectors (e.g., for query expansion), add the `-docvectors` option.

_Hint:_ Anserini ignores the `cr` folder when indexing the disk45. But you can remove `cr` folder by your own too.
_Hint:_ You can use the `DumpIndex` utility to print out the statistics of the index. Please refer to [DumpIndex References](dumpindex-reference.md) for the statistics of the index


**Search**:

After indexing is done, you should be able to perform a retrieval run:

```
sh target/appassembler/bin/SearchWebCollection -topicreader Trec -index lucene-index.disk45.pos -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt -output run.disk45.301-450.601-700.bm25.txt
```

**Evaluate**:

Evaluation can be done using `trec_eval`:
```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt run.disk45.301-450.601-700.bm25.txt
```

**Effectiveness Reference**:

Metric | BM25   | QL     
-------|--------|--------
MAP    | 0.2500 | 0.2465 
P30    | 0.3120 | 0.3078 
