Anserini
========
[![Build Status](https://travis-ci.org/castorini/Anserini.svg?branch=master)](https://travis-ci.org/castorini/Anserini)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.anserini/anserini/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.anserini/anserini)
[![LICENSE](https://img.shields.io/badge/license-Apache-blue.svg?style=flat-square)](./LICENSE)

Anserini is an open-source information retrieval toolkit built on Lucene that aims to bridge the gap between academic information retrieval research and the practice of building real-world search applications. This effort grew out of [a reproducibility study of various open-source retrieval engines in 2016](https://cs.uwaterloo.ca/~jimmylin/publications/Lin_etal_ECIR2016.pdf) (Lin et al., ECIR 2016). Additional details can be found [in a short paper](https://dl.acm.org/authorize?N47337) (Yang et al., SIGIR 2017) and a [journal article](https://dl.acm.org/citation.cfm?doid=3289400.3239571) (Yang et al., JDIQ 2018).

## Getting Started

Anserini requires Java 8 (note that there are [known issues with Java 10 and Java 11](https://github.com/castorini/Anserini/issues/445)) and Maven 3.3+. Build using Maven:

```
mvn clean package appassembler:assemble
```

The `eval/` directory contains evaluation tools and scripts, including
[trec_eval](https://trec.nist.gov/trec_eval/trec_eval_latest.tar.gz),
[gdeval.pl](https://github.com/trec-web/trec-web-2014/tree/master/src/eval),
[ndeval](https://github.com/trec-web/trec-web-2014/tree/master/src/eval).
Before using `trec_eval`, unpack and compile it, as follows:

```
tar xvfz trec_eval.9.0.4.tar.gz && cd trec_eval.9.0.4 && make
```

Before using `ndeval`, compile it as follows:

```
cd ndeval && make
```

## Running Standard IR Experiments

Anserini is designed to support experiments on various standard TREC collections out of the box.
Each collection is associated with [regression tests](docs/regressions.md) for replicability.
Note that these regressions capture the "out of the box" experience, based on [_default_ parameter settings](https://github.com/castorini/Anserini/blob/master/src/main/java/io/anserini/search/SearchArgs.java).

+ [Experiments on Disks 1 &amp; 2](docs/experiments-disk12.md)
+ [Experiments on Disks 4 &amp; 5 (Robust04)](docs/experiments-robust04.md)
+ [Experiments on AQUAINT (Robust05)](docs/experiments-robust05.md)
+ [Experiments on the New York Times (Core17)](docs/experiments-core17.md)
+ [Experiments on the Washington Post (Core18)](docs/experiments-core18.md)
+ [Experiments on Wt10g](docs/experiments-wt10g.md)
+ [Experiments on Gov2](docs/experiments-gov2.md)
+ [Experiments on ClueWeb09 (Category B)](docs/experiments-cw09b.md)
+ [Experiments on ClueWeb12-B13](docs/experiments-cw12b13.md)
+ [Experiments on ClueWeb12](docs/experiments-cw12.md)
+ [Experiments on Tweets2011 (MB11 &amp; MB12)](docs/experiments-mb11.md)
+ [Experiments on Tweets2013 (MB13 &amp; MB14)](docs/experiments-mb13.md)
+ [Experiments on CAR17](docs/experiments-car17.md)

Additional regressions:

+ [Experiments from JDIQ 2018 article](docs/experiments-jdiq2018.md): parameter tuning experiments for baseline "bag-of-words" retrieval models
+ Runbooks for TREC 2018: [[Anserini group](docs/runbook-trec2018-anserini.md)] [[h2oloo group](docs/runbook-trec2018-h2oloo.md)]

## Additional Documentation

+ [Axiomatic Reranking](docs/axiom-reranking.md)
+ `IndexUtils` is a utility to interact with an index using the command line (e.g., print index statistics). Refer to `target/appassembler/bin/IndexUtils -h` for more details.
+ `MapCollections` is a generic mapper framework for processing a document collection in parallel. Developers can write their own mappers for different tasks: one simple example is `CountDocumentMapper` which counts the number of documents in a collection:

   ```
   target/appassembler/bin/MapCollections -collection ClueWeb09Collection \
     -threads 16 -input ~/collections/web/ClueWeb09b/ClueWeb09_English_1/ \
     -mapper CountDocumentMapper -context CountDocumentMapperContext
   ```

## Python Interface

Anserini was designed with Python integration in mind, for connecting with popular deep learning toolkits such as PyTorch. This is accomplished via [pyjnius](https://github.com/kivy/pyjnius). The `SimpleSearcher` class provides a simple Python/Java bridge, shown below:

```
import jnius_config
jnius_config.set_classpath("target/anserini-0.2.1-SNAPSHOT-fatjar.jar")

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

+ v0.2.0: September 10, 2018 [[Release Notes](docs/release-notes/release-notes-v0.2.0.md)]
+ v0.1.0: July 4, 2018 [[Release Notes](docs/release-notes/release-notes-v0.1.0.md)]

## References

+ Jimmy Lin, Matt Crane, Andrew Trotman, Jamie Callan, Ishan Chattopadhyaya, John Foley, Grant Ingersoll, Craig Macdonald, Sebastiano Vigna. [Toward Reproducible Baselines: The Open-Source IR Reproducibility Challenge.](https://cs.uwaterloo.ca/~jimmylin/publications/Lin_etal_ECIR2016.pdf) _Proceedings of the 38th European Conference on Information Retrieval (ECIR 2016)_, pages 408-420, March 2016, Padua, Italy.

+ Peilin Yang, Hui Fang, and Jimmy Lin. [Anserini: Enabling the Use of Lucene for Information Retrieval Research.](https://dl.acm.org/authorize?N47337) _Proceedings of the 40th Annual International ACM SIGIR Conference on Research and Development in Information Retrieval (SIGIR 2017)_, pages 1253-1256, August 2017, Tokyo, Japan.

+ Peilin Yang, Hui Fang, and Jimmy Lin. [Anserini: Reproducible Ranking Baselines Using Lucene.](https://dl.acm.org/citation.cfm?doid=3289400.3239571) Journal of Data and Information Quality, 10(4), Article 16, 2018.

## Acknowledgments

This research has been supported in part by the Natural Sciences and Engineering Research Council (NSERC) of Canada and the U.S. National Science Foundation under IIS-1423002 and CNS-1405688. Any opinions, findings, and conclusions or recommendations expressed do not necessarily reflect the views of the sponsors.
