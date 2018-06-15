Anserini
========
[![Build Status](https://travis-ci.org/castorini/Anserini.svg?branch=master)](https://travis-ci.org/castorini/Anserini)

Build using Maven:

```
mvn clean package appassembler:assemble
```

## Standard IR Experiments

Anserini is designed to support experiments on various standard TREC collections out of the box:

+ [_ad hoc_ retrieval: Experiments on Disks 1 &amp; 2](docs/experiments-disk12.md)
+ [_ad hoc_ retrieval: Robust04 experiments on Disks 4 &amp; 5](docs/experiments-robust04.md)
+ [_ad hoc_ retrieval: Robust05 experiments on the AQUAINT collection](docs/experiments-robust05.md)
+ [_ad hoc_ retrieval: CORE17 experiments on the New York Times collection](docs/experiments-core17.md)
+ [_ad hoc_ retrieval: CORE18 experiments on the Washington Post collection](docs/experiments-wapo.md)
+ [_ad hoc_ tweet retrieval: TREC Microblog experiments](docs/experiments-microblog.md)
+ [web search: Wt10g collection](docs/experiments-wt10g.md)
+ [web search: Gov2 collection](docs/experiments-gov2.md)
+ [web search: ClueWeb09b collection](docs/experiments-clueweb09b.md)
+ [web search: ClueWeb12-B13 collection](docs/experiments-clueweb12-b13.md)
+ [web search: ClueWeb12 collection](docs/experiments-clueweb12.md)

The `eval/` directory contains evaluation tools and scripts, including `trec_eval`. Before using `trec_eval`, you have to unpack and compile it.

## Tools

+ [IndexCollection](docs/index-collection.md)
+ [DumpIndex](docs/dumpindex.md)
