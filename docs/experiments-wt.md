## Anserini Experiments on WT collections (WT2G & WT10G)

**Indexing**:

```
nohup sh target/appassembler/bin/IndexCollection -collection WtCollection \
 -input /path/to/wt_collection/ -generator JsoupGenerator \
 -index lucene-index.wt.pos -threads 32 -storePositions -optimize > log.wt.cnt+pos &
```

The directory `/path/to/wt/` should be the root directory of WT collection, i.e., `ls /path/to/wt/` should bring up a 
bunch of subdirectories, `WTX001` to `WTX104` (for WT10G) or `Wt0.tar.gz` to `Wt2.tar.gz` (for WT2G). The command above 
builds a standard positional index (`-storePositions`) that's optimized into a single segment (`-optimize`). If you 
also want to store document vectors (e.g., for query expansion), add the `-docvectors` option.

**Search**:

After indexing is done, you should be able to perform a retrieval run:

```
sh target/appassembler/bin/SearchWebCollection -topicreader Trec -index lucene-index.wt2g.pos -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.401-450.txt -output run.wt2g.401-450.bm25.txt
```
or 
```
sh target/appassembler/bin/SearchWebCollection -topicreader Trec -index lucene-index.wt10g.pos -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.451-550.bm25.txt
```

**Evaluate**:

Evaluation can be done using `trec_eval`:
```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.401-450.txt run.wt2g.401-450.bm25.txt
```
or
```
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.451-550.txt run.wt10g.451-550.bm25.txt
```

**Effectiveness Reference**:

##### no stopwords (default)

MAP                    | BM25   | QL     
-----------------------|--------|--------
WT2G: Topics 401-450   | 0.3015 | 0.2922 
WT10G: Topics 451-550  | 0.1981 | 0.2015 

P30                    | BM25   | QL     
-----------------------|--------|--------
WT2G: Topics 401-450   | 0.3220 | 0.3233 
WT10G: Topics 451-550  | 0.2207 | 0.2184  

##### keep stopwords (with `-keepstopwords` option in both `IndexCollection` and `SearchWebCollection`)

MAP                    | BM25   | QL     
-----------------------|--------|--------
WT2G: Topics 401-450   | 0.3028 | 0.2849 
WT10G: Topics 451-550  | 0.1934 | 0.1964 

P30                    | BM25   | QL     
-----------------------|--------|--------
WT2G: Topics 401-450   | 0.3207 | 0.3160 
WT10G: Topics 451-550  | 0.2255 | 0.2184  