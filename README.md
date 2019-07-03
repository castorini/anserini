Anserini
========
[![Generic badge](https://img.shields.io/badge/Lucene-v8.0.0-yellow.svg)](https://archive.apache.org/dist/lucene/java/8.0.0/)
[![Build Status](https://travis-ci.org/castorini/anserini.svg?branch=master)](https://travis-ci.org/castorini/Anserini)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.anserini/anserini/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.anserini/anserini)
[![LICENSE](https://img.shields.io/badge/license-Apache-blue.svg?style=flat-square)](./LICENSE)

Anserini is an open-source information retrieval toolkit built on Lucene that aims to bridge the gap between academic information retrieval research and the practice of building real-world search applications. 
This effort grew out of [a reproducibility study of various open-source retrieval engines in 2016](https://cs.uwaterloo.ca/~jimmylin/publications/Lin_etal_ECIR2016.pdf) (Lin et al., ECIR 2016). 
See [Yang et al. (SIGIR 2017)](https://dl.acm.org/authorize?N47337) and [Yang et al. (JDIQ 2018)](https://dl.acm.org/citation.cfm?doid=3289400.3239571) for overviews.

Anserini was upgraded to Lucene 8.0 as of commit [`75e36f9`](https://github.com/castorini/anserini/commit/75e36f97f7037d1ceb20fa9c91582eac5e974131) (6/12/2019); prior to that, the toolkit uses Lucene 7.6.
Based on [preliminary experiments](docs/lucene7-vs-lucene8.md), query evaluation latency has been much improved in Lucene 8.
As a result of this upgrade, results of all regressions have changed slightly.
To replicate old results from Lucene 7.6, use [v0.5.1](https://github.com/castorini/anserini/releases).

If you've found Anserini to be helpful, we have a simple request for you to contribute back.
In the course of replicating baseline results on standard test collections, please let us know if you're successful by sending us a pull request with a simple note, like what appears at the bottom of [the Robust04 page](docs/regressions-robust04.md).
Replicability is important to us, and we'd like to know about successes as well as failures.
Since the regression documentation is auto-generated, pull requests should be sent against the [raw templates](https://github.com/castorini/anserini/tree/master/src/main/resources/docgen/templates).
In turn, you'll be recognized as a [contributor](https://github.com/castorini/anserini/graphs/contributors).

## Getting Started

A zero effort way to try out Anserini is to look at our online [colab demo](https://colab.research.google.com/drive/1s44ylhEkXDzqNgkJSyXDYetGIxO9TWZn)!
Click "Open in Playground" and you'll be able to replicate our baselines from the TREC 2004 Robust Track just from the browser!

Anserini currently uses Java 8 (note that there are [known issues with Java 10 and Java 11](https://github.com/castorini/Anserini/issues/445)) and Maven 3.3+.
Oracle JVM is necessary to replicate our regression results; there are known issues with OpenJDK (see [this](https://github.com/castorini/Anserini/pull/590) and [this](https://github.com/castorini/Anserini/issues/592)).
We are planning an upgrade to a more recent JDK (see [#710](https://github.com/castorini/anserini/issues/710)).

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

+ [Regressions for Disks 1 &amp; 2](docs/regressions-disk12.md)
+ [Regressions for Disks 4 &amp; 5 (Robust04)](docs/regressions-robust04.md) [[Colab demo](https://colab.research.google.com/drive/1s44ylhEkXDzqNgkJSyXDYetGIxO9TWZn)]
+ [Regressions for AQUAINT (Robust05)](docs/regressions-robust05.md)
+ [Regressions for the New York Times (Core17)](docs/regressions-core17.md)
+ [Regressions for the Washington Post (Core18)](docs/regressions-core18.md)
+ [Regressions for Wt10g](docs/regressions-wt10g.md)
+ [Regressions for Gov2](docs/regressions-gov2.md)
+ [Regressions for ClueWeb09 (Category B)](docs/regressions-cw09b.md)
+ [Regressions for ClueWeb12-B13](docs/regressions-cw12b13.md)
+ [Regressions for ClueWeb12](docs/regressions-cw12.md)
+ [Regressions for Tweets2011 (MB11 &amp; MB12)](docs/regressions-mb11.md)
+ [Regressions for Tweets2013 (MB13 &amp; MB14)](docs/regressions-mb13.md)
+ [Regressions for Complex Answer Retrieval v1.5 (CAR17)](docs/regressions-car17v1.5.md)
+ [Regressions for Complex Answer Retrieval v2.0 (CAR17)](docs/regressions-car17v2.0.md)
+ [Regressions for the MS MARCO Passage Task](docs/regressions-msmarco-passage.md)
+ [Regressions for the MS MARCO Passage Task with doc2query expansion](docs/regressions-msmarco-passage-doc2query.md)
+ [Regressions for the MS MARCO Document Task](docs/regressions-msmarco-doc.md)

Other experiments:

+ [Replicating "Neural Hype" Experiments](docs/experiments-forum2018.md)
+ [Guide to running BM25 baselines on the MS MARCO Passage Task](docs/experiments-msmarco-passage.md)
+ [Guide to running BM25 baselines on the MS MARCO Document Task](docs/experiments-msmarco-doc.md)
+ [Guide to replicating document expansion by query prediction (Doc2query) results](docs/experiments-doc2query.md)
+ [Guide to running experiments on the AI2 Open Research Corpus](docs/experiments-openresearch.md)
+ [Experiments from Yang et al. (JDIQ 2018)](docs/experiments-jdiq2018.md)
+ Runbooks for TREC 2018: [[Anserini group](docs/runbook-trec2018-anserini.md)] [[h2oloo group](docs/runbook-trec2018-h2oloo.md)]
+ Runbook for [ECIR 2019 paper on axiomatic semantic term matching](docs/runbook-ecir2019-axiomatic.md)
+ Runbook for [ECIR 2019 paper on cross-collection relevance feedback](docs/runbook-ecir2019-ccrf.md)

See [this page](docs/additional.md) for additional documentation.

## Integrations

+ Use Anserini in Python via [Pyserini](docs/pyserini.md)!
+ Anserini integrates with SolrCloud via [Solrini](docs/solrini.md)!
+ Anserini integrates with Elasticsearch via [Elasterini](docs/elastirini.md)!

## Release History

+ v0.5.1: June 11, 2019 [[Release Notes](docs/release-notes/release-notes-v0.5.1.md)]
+ v0.5.0: June 5, 2019 [[Release Notes](docs/release-notes/release-notes-v0.5.0.md)]
+ v0.4.0: March 4, 2019 [[Release Notes](docs/release-notes/release-notes-v0.4.0.md)]
+ v0.3.0: December 16, 2018 [[Release Notes](docs/release-notes/release-notes-v0.3.0.md)]
+ v0.2.0: September 10, 2018 [[Release Notes](docs/release-notes/release-notes-v0.2.0.md)]
+ v0.1.0: July 4, 2018 [[Release Notes](docs/release-notes/release-notes-v0.1.0.md)]

## References

+ Jimmy Lin, Matt Crane, Andrew Trotman, Jamie Callan, Ishan Chattopadhyaya, John Foley, Grant Ingersoll, Craig Macdonald, Sebastiano Vigna. [Toward Reproducible Baselines: The Open-Source IR Reproducibility Challenge.](https://cs.uwaterloo.ca/~jimmylin/publications/Lin_etal_ECIR2016.pdf) _ECIR 2016_.
+ Peilin Yang, Hui Fang, and Jimmy Lin. [Anserini: Enabling the Use of Lucene for Information Retrieval Research.](https://dl.acm.org/authorize?N47337) _SIGIR 2017_.
+ Peilin Yang, Hui Fang, and Jimmy Lin. [Anserini: Reproducible Ranking Baselines Using Lucene.](https://dl.acm.org/citation.cfm?doid=3289400.3239571) _Journal of Data and Information Quality_, 10(4), Article 16, 2018.
+ Wei Yang, Haotian Zhang, and Jimmy Lin. [Simple Applications of BERT for Ad Hoc Document Retrieval.](https://arxiv.org/abs/1903.10972) _arXiv:1903.10972_, March 2019.
+ Rodrigo Nogueira, Wei Yang, Jimmy Lin, and Kyunghyun Cho. [Document Expansion by Query Prediction.](https://arxiv.org/abs/1904.08375) _arXiv:1904.08375_, April 2019.
+ Peilin Yang and Jimmy Lin. [Reproducing and Generalizing Semantic Term Matching in Axiomatic Information Retrieval.](https://cs.uwaterloo.ca/~jimmylin/publications/Yang_Lin_ECIR2019.pdf) _ECIR 2019_.
+ Ruifan Yu, Yuhao Xie and Jimmy Lin. [Simple Techniques for Cross-Collection Relevance Transfer.](https://cs.uwaterloo.ca/~jimmylin/publications/Yu_etal_ECIR2019.pdf) _ECIR 2019_.
+ Wei Yang, Yuqing Xie, Aileen Lin, Xingyu Li, Luchen Tan, Kun Xiong, Ming Li, and Jimmy Lin. [End-to-End Open-Domain Question Answering with BERTserini.](https://aclweb.org/anthology/papers/N/N19/N19-4013/) _NAACL-HLT 2019 Demos_.
+ Ryan Clancy, Toke Eskildsen, Nick Ruest, and Jimmy Lin. [Solr Integration in the Anserini Information Retrieval Toolkit.](https://cs.uwaterloo.ca/~jimmylin/publications/Clancy_etal_SIGIR2019a.pdf) _SIGIR 2019_.
+ Ryan Clancy, Jaejun Lee, Zeynep Akkalyoncu Yilmaz, and Jimmy Lin. [Information Retrieval Meets Scalable Text Analytics: Solr Integration with Spark.](https://cs.uwaterloo.ca/~jimmylin/publications/Clancy_etal_SIGIR2019b.pdf) _SIGIR 2019_.
+ Jimmy Lin and Peilin Yang. [The Impact of Score Ties on Repeatability in Document Ranking.](https://cs.uwaterloo.ca/~jimmylin/publications/Lin_Yang_SIGIR2019.pdf) _SIGIR 2019_.
+ Wei Yang, Kuang Lu, Peilin Yang, and Jimmy Lin. [Critically Examining the "Neural Hype": Weak Baselines and the Additivity of Effectiveness Gains from Neural Ranking Models.](https://cs.uwaterloo.ca/~jimmylin/publications/Lin_Yang_SIGIR2019.pdf) _SIGIR 2019_.

## Acknowledgments

This research is supported in part by the Natural Sciences and Engineering Research Council (NSERC) of Canada.
Previous support came from the U.S. National Science Foundation under IIS-1423002 and CNS-1405688.
Any opinions, findings, and conclusions or recommendations expressed do not necessarily reflect the views of the sponsors.
