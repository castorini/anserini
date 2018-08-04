Anserini
========
[![Build Status](https://travis-ci.org/castorini/Anserini.svg?branch=master)](https://travis-ci.org/castorini/Anserini)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.anserini/anserini/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.anserini/anserini)
[![LICENSE](https://img.shields.io/badge/license-Apache-blue.svg?style=flat-square)](./LICENSE)

## Getting Started

Build using Maven:

```
mvn clean package appassembler:assemble
```

The `eval/` directory contains evaluation tools and scripts, including
[trec_eval](https://trec.nist.gov/trec_eval/trec_eval_latest.tar.gz),
[gdeval.pl](https://github.com/trec-web/trec-web-2014/tree/master/src/eval),
[ndeval](https://github.com/trec-web/trec-web-2014/tree/master/src/eval).
Before using `trec_eval`, unpack and compile it, as follows:

```
tar xvfz trec_eval.9.0.tar.gz && cd trec_eval.9.0 && make
```
Before using `ndeval`, compile it as follows:
```
cd ndeval && make
```

## Running Standard IR Experiments

Anserini is designed to support experiments on various standard TREC collections out of the box:

### Newswire

+ [Experiments on Disks 1 &amp; 2](docs/experiments-disk12.md)
+ [Experiments on Disks 4 &amp; 5 (Robust04)](docs/experiments-robust04.md)
+ [Experiments on AQUAINT (Robust05)](docs/experiments-robust05.md)
+ [Experiments on New York Times (Core17)](docs/experiments-core17.md)

### Web

+ [Experiments on Wt10g](docs/experiments-wt10g.md)
+ [Experiments on Gov2](docs/experiments-gov2.md)
+ [Experiments on ClueWeb09 (Category B)](docs/experiments-cw09b.md)
+ [Experiments on ClueWeb12-B13](docs/experiments-cw12b13.md)
+ [Experiments on ClueWeb12](docs/experiments-cw12.md)

### Tweets

+ [Experiments on Tweets2011 (MB11 &amp; MB12)](docs/experiments-mb11.md)
+ [Experiments on Tweets2013 (MB13 &amp; MB14)](docs/experiments-mb13.md)

## Tools

+ `IndexUtils` is a powerful utility to interact with an index using the command line, e.g. print index statistics. Refer to `target/appassembler/bin/IndexUtils -h` for more details.
+ [Axiomatic Reranking](docs/axiom-reranking.md)
+ `MapCollections` is a generic mapper framework that processes each file segment in parallel. Developers can build their own mapper that `extends` to `DocumentMapper`. One example is our `CountDocumentMapper` which counts the number of documents in the whole collection:

    ```nohup target/appassembler/bin/MapCollections -collection TrecCollection -threads 16 -input /tuna1/collections/newswire/disk12/ -mapper CountDocumentMapper &> log.disk12.count &```

## Python Interface

Anserini was designed with Python integration in mind, for connecting with popular deep learning toolkits such as PyTorch. This is accomplished via [pyjnius](https://github.com/kivy/pyjnius). The `SimpleSearcher` class provides a simple Python/Java bridge, shown below:

```
import jnius_config
jnius_config.set_classpath("target/anserini-0.1.1-SNAPSHOT-fatjar.jar")

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
