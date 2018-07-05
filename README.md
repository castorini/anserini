Anserini
========
[![Build Status](https://travis-ci.org/castorini/Anserini.svg?branch=master)](https://travis-ci.org/castorini/Anserini)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.anserini/anserini/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.anserini/anserini)
[![LICENSE](https://img.shields.io/badge/license-Apache-blue.svg?style=flat-square)](./LICENSE)

## Getting Started

Build using Maven:

```
mvn clean package
```

The `eval/` directory contains evaluation tools and scripts, including `trec_eval`. Before using `trec_eval`, unpack and compile it, as follows:

```
tar xvfz trec_eval.9.0.tar.gz && cd trec_eval.9.0 && make
```

## Running Standard IR Experiments

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

## Tools

+ [IndexCollection](docs/index-collection.md)
+ [DumpIndex](docs/dumpindex.md)
+ [Axiomatic Reranking](docs/axiom-reranking.md)

## Python Interface

Anserini was designed with Python integration in mind, for connecting with popular deep learning toolkits such as PyTorch. This is accomplished via [pyjnius](https://github.com/kivy/pyjnius). To make this work, tell Maven to explicitly build the fat jar, as follows:

```
mvn clean package shade:shade
```

The `SimpleSearcher` class provides a simple Python/Java bridge, shown below:

```
import jnius_config
jnius_config.set_classpath("target/anserini-0.0.1-SNAPSHOT-fatjar.jar")

from jnius import autoclass
JString = autoclass('java.lang.String')
JSearcher = autoclass('io.anserini.search.SimpleSearcher')

searcher = JSearcher(JString('lucene-index.robust04.pos+docvectors+rawdocs'))
hits = searcher.search(JString('hubble space telescope'))

# the docid of the 1st hit
hits[0].docid

# the internal Lucene docid of the 1st hit
hits[0].ldocid

# the score of the 1st hit
hits[0].score

# the full document of the 1st hit
hits[0].content
```

## Release History

+ v0.1.0: July 4, 2018 [[Release Notes](docs/release-notes/release-notes-v0.1.0.md)]
