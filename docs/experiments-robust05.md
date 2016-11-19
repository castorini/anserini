# Anserini Experiments of Robust05

See http://trec.nist.gov/data/t14_robust.html

**Indexing**:

```
nohup sh target/appassembler/bin/IndexCollection -collection Trec -input /path/to/aquaint/ \
 -index lucene-index.aquaint.pos -threads 32 -positions -optimize \
 2> log.aquaint.pos.emptyDocids.txt 1> log.aquaint.pos.recordCounts.txt &
```


The directory `/path/to/aquaint/` should be the root directory of AQUAINT collection, i.e., `ls /path/to/aquaint/disk1/` should bring up subdirectory `NYT` and `ls /path/to/aquaint/disk1/` should bring up subdirectory `APW` and `XIE`. The command above builds a standard positional index (`-positions`) that's optimized into a single segment (`-optimize`). If you also want to store document vectors (e.g., for query expansion), add the `-docvectors` option.

_Hint:_ Anserini ignores the `cr` folder when indexing the disk45. But you can remove `cr` folder by your own too.
_Hint:_ You can use the `DumpIndex` utility to print out the statistics of the index. Please refer to [DumpIndex References](dumpindex-reference.md) for the statistics of the index


**Search**:

After indexing is done, you should be able to perform a retrieval run:

```
sh target/appassembler/bin/SearchWebCollection -topicreader Trec -index lucene-index.aquaint.pos -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.robust05.txt -output run.aquaint.robust05.bm25.txt
```

**Evaluate**:

Evaluation can be done using `trec_eval`:
```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt run.aquaint.robust05.bm25.txt
```

**Effectiveness Reference**:

Metric | BM25   | QL     
-------|--------|--------
MAP    | 0.2004 | 0.2025 
P30    | 0.3667 | 0.3707 
