Anserini
========

Build using Maven:

```
mvn clean package appassembler:assemble
```

## Standard IR Experiments

Anserini is designed to support experiments on various standard TREC collections out of the box:

+ [Disk12](docs/experiments-disk12.md)
+ [Robust04](docs/experiments-robust04.md)
+ [Robust05](docs/experiments-robust05.md)
+ [WT2G & WT10G](docs/experiments-wt.md)
+ [Gov2](docs/experiments-gov2.md)
+ [ClueWeb09b](docs/experiments-clueweb09b.md)
+ [ClueWeb12-B13](docs/experiments-clueweb12-b13.md)
+ [ClueWeb12](docs/experiments-clueweb12.md)
+ [TrecCore](docs/experiments-core17.md)

The `eval/` directory contains evaluation tools and scripts, including `trec_eval`. Before using `trec_eval`, you have to unpack and compile it. Other helpful links:

+ [Index statistics](docs/dumpindex-reference.md) for verification purposes
+ [Source of all topics and qrels](docs/topics-and-qrels.md)

## Tools

+ [IndexCollection](docs/index-collection.md)
+ [DumpIndex](docs/dumpindex.md)
+ [SearchCollection](docs/search-collection.md)
+ [Word embedding](docs/embeddings.md) for Anserini 

## Other Features

+ [Twitter (Near) Real-Time Search](docs/twitter-nrts.md)
+ [YoGosling](docs/yogosling.md)
+ How to [add a new collection class](docs/add-collection-class.md)
+ How to [add a new topic reader](docs/add-topic-reader.md)
+ Create `trec_eval` style run file from TrecQA set and [evaluation](docs/end2end-TrecQa-eval.md)  