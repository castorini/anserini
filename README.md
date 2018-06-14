Anserini
========
[![Build Status](https://travis-ci.org/castorini/Anserini.svg?branch=master)](https://travis-ci.org/castorini/Anserini)

Build using Maven:

```
mvn clean package appassembler:assemble
```

## Standard IR Experiments

Anserini is designed to support experiments on various standard TREC collections out of the box:

+ [_ad hoc_ retrieval: TREC topics on Disks 1 &amp; 2](docs/experiments-disk12.md)
+ [_ad hoc_ retrieval: TREC Robust04 topics on Disks 4 &amp; 5](docs/experiments-robust04.md)
+ [_ad hoc_ retrieval: TREC Robust05 topics on the AQUAINT collection](docs/experiments-robust05.md)
+ [_ad hoc_ retrieval: TREC CORE17 on the New York Times collection](docs/experiments-core17.md)
+ [_ad hoc_ retrieval: TREC CORE18 on the Washington Post collection](docs/experiments-wapo.md)
+ [web search: Wt10g collection](docs/experiments-wt10g.md)
+ [web search: Gov2 collection](docs/experiments-gov2.md)
+ [web search: ClueWeb09b collection](docs/experiments-clueweb09b.md)
+ [web search: ClueWeb12-B13 collection](docs/experiments-clueweb12-b13.md)
+ [web search: ClueWeb12 collection](docs/experiments-clueweb12.md)
+ [tweet _ad hoc_ retrieval: TREC Microblog topics](docs/experiments-microblog.md)

The `eval/` directory contains evaluation tools and scripts, including `trec_eval`. Before using `trec_eval`, you have to unpack and compile it. Other helpful links:

+ [Index statistics](docs/dumpindex-reference.md) for verification purposes
+ [Source of all topics and qrels](docs/topics-and-qrels.md)

## Tools

+ [IndexCollection](docs/index-collection.md)
+ [DumpIndex](docs/dumpindex.md)
