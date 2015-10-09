Anserini
========

Build using Maven:

```
mvn clean package appassembler:assemble
```

### Index Gov2 (count index):

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


### ClueWeb09 Category B (CW09B):

``` sh
sh target/appassembler/bin/IndexCW09B -dataDir /path/to/cw09b/ \
-indexPath lucene-index.cw09b.cnt/index -threadCount 15
```

The directory `/path/to/cw09b/` should be the root directory of ClueWeb09B collection, i.e., `ls /path/to/cw09b/` should bring up a bunch of subdirectories, `en0000` to `enwp03`.

After indexing is done, you should be able to perform a retrieval run:

``` bash
sh target/appassembler/bin/RunCW09B src/resources/topics-and-qrels/topics.web.151-200.txt \
run.web.151-200.txt lucene-index.cw09b.cnt/index 
```

Then you can evaluate the runs:

``` bash
trec_eval -M1000 src/resources/topics-and-qrels/qrels.web.151-200.txt run.web.151-200.txt
```

To record search/running times :
``` bash
sh target/appassembler/bin/Time lucene-index.cw09b.cnt/index
```