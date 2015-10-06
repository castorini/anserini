Anserini
========

Build using Maven:

```
mvn clean package appassembler:assemble
```

Index Gov2 (count index):

```
sh target/appassembler/bin/IndexGov2 -dataDir /path/to/gov2/ \
 -indexPath lucene-index.gov2.cnt -threadCount 32 -docCountLimit -1 
```

The directory `/path/to/gov2/` should be the root directory of Gov2 collection, i.e., `ls /path/to/gov2/` should bring up a bunch of subdirectories, `GX000` to `GX272`.

After indexing is done, you should be able to peform a retrieval run:

```
sh target/appassembler/bin/RunGov2 src/resources/topics-and-qrels/topics.701-750.txt \
 src/resources/topics-and-qrels/qrels.701-750.txt run.701-750.txt lucene-index.gov2.cnt/index
```

A copy of `trec_eval` is included in `eval/`. Unpack and compile it. Then you can evaluate the runs:

```
eval/trec_eval.9.0/trec_eval src/resources/topics-and-qrels/qrels.701-750.txt run.701-750.txt
```
