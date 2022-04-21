Anserini
========
[![build](https://github.com/castorini/anserini/actions/workflows/maven.yml/badge.svg)](https://github.com/castorini/anserini/actions)
[![codecov](https://codecov.io/gh/castorini/anserini/branch/master/graph/badge.svg)](https://codecov.io/gh/castorini/anserini)
[![Generic badge](https://img.shields.io/badge/Lucene-v8.11.0-brightgreen.svg)](https://archive.apache.org/dist/lucene/java/8.11.0/)
[![Maven Central](https://img.shields.io/maven-central/v/io.anserini/anserini?color=brightgreen)](https://search.maven.org/search?q=a:anserini)
[![LICENSE](https://img.shields.io/badge/license-Apache-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![doi](http://img.shields.io/badge/doi-10.1145%2F3239571-blue.svg?style=flat)](https://doi.org/10.1145/3239571)

Anserini is a toolkit for reproducible information retrieval research.
By building on Lucene, we aim to bridge the gap between academic information retrieval research and the practice of building real-world search applications.
Among other goals, our effort aims to be [the opposite of this](http://phdcomics.com/comics/archive.php?comicid=1689).[*](docs/reproducibility.md)
Anserini grew out of [a reproducibility study of various open-source retrieval engines in 2016](https://cs.uwaterloo.ca/~jimmylin/publications/Lin_etal_ECIR2016.pdf) (Lin et al., ECIR 2016). 
See [Yang et al. (SIGIR 2017)](https://dl.acm.org/authorize?N47337) and [Yang et al. (JDIQ 2018)](https://dl.acm.org/citation.cfm?doid=3289400.3239571) for overviews.

## Getting Started

Many Anserini features are exposed in the [Pyserini](http://pyserini.io/) Python interface.
If you're looking for basic indexing and search capabilities, you might want to start there.
A low-effort way to try out Anserini is to look at our [online notebooks](https://github.com/castorini/anserini-notebooks), which will allow you to get started with just a few clicks.
For convenience, we've pre-built a few common indexes, available to download [here](https://git.uwaterloo.ca/jimmylin/anserini-indexes).

You'll need Java 11 and Maven 3.3+ to build Anserini.
Clone our repo with the `--recurse-submodules` option to make sure the `eval/` submodule also gets cloned (alternatively, use `git submodule update --init`).
Then, build using using Maven:

```
mvn clean package appassembler:assemble
```

Note that on Windows, tests may fail due to encoding issues, see [#1466](https://github.com/castorini/anserini/issues/1466).
Solution is to skip tests by adding `-Dmaven.test.skip=true` to the above `mvn` command.

The `tools/` directory, which contains evaluation tools and other scripts, is actually [this repo](https://github.com/castorini/anserini-tools), integrated as a [Git submodule](https://git-scm.com/book/en/v2/Git-Tools-Submodules) (so that it can be shared across related projects).
Build as follows (you might get warnings, but okay to ignore):

```bash
cd tools/eval && tar xvfz trec_eval.9.0.4.tar.gz && cd trec_eval.9.0.4 && make && cd ../../..
cd tools/eval/ndeval && make && cd ../../..
```

With that, you should be ready to go!

## Regression Experiments (+ Reproduction Guides)

Anserini is designed to support experiments on various standard IR test collections out of the box.
The following experiments are backed by [rigorous end-to-end regression tests](docs/regressions.md) with [`run_regression.py`](src/main/python/run_regression.py) and [the Anserini reproducibility promise](docs/regressions.md).
For the most part, these runs are based on [_default_ parameter settings](https://github.com/castorini/Anserini/blob/master/src/main/java/io/anserini/search/SearchArgs.java).

These pages can also serve as guides to reproduce our results.
See individual pages for details!

+ Regressions for [Disks 1 &amp; 2 (TREC 1-3)](docs/regressions-disk12.md), [Disks 4 &amp; 5 (TREC 7-8, Robust04)](docs/regressions-disk45.md), [AQUAINT (Robust05)](docs/regressions-robust05.md)
+ Regressions for [the New York Times Corpus (Core17)](docs/regressions-core17.md), [the Washington Post Corpus (Core18)](docs/regressions-core18.md)
+ Regressions for [Wt10g](docs/regressions-wt10g.md), [Gov2](docs/regressions-gov2.md)
+ Regressions for [ClueWeb09 (Category B)](docs/regressions-cw09b.md), [ClueWeb12-B13](docs/regressions-cw12b13.md), [ClueWeb12](docs/regressions-cw12.md)
+ Regressions for [Tweets2011 (MB11 &amp; MB12)](docs/regressions-mb11.md), [Tweets2013 (MB13 &amp; MB14)](docs/regressions-mb13.md)
+ Regressions for Complex Answer Retrieval (CAR17): [v1.5](docs/regressions-car17v1.5.md), [v2.0](docs/regressions-car17v2.0.md), [v2.0 with doc2query](docs/regressions-car17v2.0-doc2query.md)
+ Regressions for MS MARCO (V1) Passage Ranking:
  + Unsupervised lexical: [BoW baselines](docs/regressions-msmarco-passage.md), [WP baselines](docs/regressions-msmarco-passage-wp.md), [doc2query](docs/regressions-msmarco-passage-doc2query.md), [doc2query-T5](docs/regressions-msmarco-passage-docTTTTTquery.md)
  + Learned sparse lexical (uniCOIL family): [uniCOIL noexp](docs/regressions-msmarco-passage-unicoil-noexp.md), [uniCOIL with d2q-T5](docs/regressions-msmarco-passage-unicoil.md), [uniCOIL with TILDE](docs/regressions-msmarco-passage-unicoil-tilde-expansion.md)
  + Learned sparse lexical (other): [DeepImpact](docs/regressions-msmarco-passage-deepimpact.md), [SPLADEv2](docs/regressions-msmarco-passage-distill-splade-max.md), [SPLADE-distill CoCodenser-medium](docs/regressions-msmarco-passage-splade-distil-cocodenser-medium.md)
+ Regressions for MS MARCO (V1) Document Ranking:
  + Unsupervised lexical, complete doc[*](docs/experiments-msmarco-doc-doc2query-details.md): [BoW baselines](docs/regressions-msmarco-doc.md), [WP baselines](docs/regressions-msmarco-doc-wp.md), [doc2query-T5](docs/regressions-msmarco-doc-docTTTTTquery.md)
  + Unsupervised lexical, segmented doc[*](docs/experiments-msmarco-doc-doc2query-details.md): [BoW baselines](docs/regressions-msmarco-doc-segmented.md), [WP baselines](docs/regressions-msmarco-doc-segmented-wp.md), [doc2query-T5](docs/regressions-msmarco-doc-segmented-docTTTTTquery.md)
  + Learned sparse lexical: [uniCOIL noexp](docs/regressions-msmarco-doc-segmented-unicoil-noexp.md), [uniCOIL with d2q-T5](docs/regressions-msmarco-doc-segmented-unicoil.md)
+ Regressions for TREC 2019 Deep Learning Track, Passage Ranking:
  + Unsupervised lexical: [BoW baselines](docs/regressions-dl19-passage.md), [WP baselines](docs/regressions-dl19-passage-wp.md), [doc2query-T5](docs/regressions-dl19-passage-docTTTTTquery.md)
  + Learned sparse lexical (uniCOIL family): [uniCOIL noexp](docs/regressions-dl19-passage-unicoil-noexp.md), [uniCOIL with d2q-T5](docs/regressions-dl19-passage-unicoil.md)
  + Learned sparse lexical (other): [SPLADE-distill CoCodenser-medium](docs/regressions-dl19-passage-splade-distil-cocodenser-medium.md)
+ Regressions for TREC 2019 Deep Learning Track, Document Ranking:
  + Unsupervised lexical, complete doc[*](docs/experiments-msmarco-doc-doc2query-details.md): [BoW baselines](docs/regressions-dl19-doc.md), [WP baselines](docs/regressions-dl19-doc-wp.md), [doc2query-T5](docs/regressions-dl19-doc-docTTTTTquery.md)
  + Unsupervised lexical, segmented doc[*](docs/experiments-msmarco-doc-doc2query-details.md): [BoW baselines](docs/regressions-dl19-doc-segmented.md), [WP baselines](docs/regressions-dl19-doc-segmented-wp.md), [doc2query-T5](docs/regressions-dl19-doc-segmented-docTTTTTquery.md)
  + Learned sparse lexical: [uniCOIL noexp](docs/regressions-dl19-doc-segmented-unicoil-noexp.md), [uniCOIL with d2q-T5](docs/regressions-dl19-doc-segmented-unicoil.md)
+ Regressions for TREC 2020 Deep Learning Track, Passage Ranking:
  + Unsupervised lexical: [BoW baselines](docs/regressions-dl20-passage.md), [WP baselines](docs/regressions-dl20-passage-wp.md), [doc2query-T5](docs/regressions-dl20-passage-docTTTTTquery.md)
  + Learned sparse lexical (uniCOIL family): [uniCOIL noexp](docs/regressions-dl20-passage-unicoil-noexp.md), [uniCOIL with d2q-T5](docs/regressions-dl20-passage-unicoil.md)
  + Learned sparse lexical (other): [SPLADE-distill CoCodenser-medium](docs/regressions-dl20-passage-splade-distil-cocodenser-medium.md)
+ Regressions for TREC 2020 Deep Learning Track, Document Ranking:
  + Unsupervised lexical, complete doc[*](docs/experiments-msmarco-doc-doc2query-details.md): [BoW baselines](docs/regressions-dl20-doc.md), [WP baselines](docs/regressions-dl20-doc-wp.md), [doc2query-T5](docs/regressions-dl20-doc-docTTTTTquery.md)
  + Unsupervised lexical, segmented doc[*](docs/experiments-msmarco-doc-doc2query-details.md): [BoW baselines](docs/regressions-dl20-doc-segmented.md), [WP baselines](docs/regressions-dl20-doc-segmented-wp.md), [doc2query-T5](docs/regressions-dl20-doc-segmented-docTTTTTquery.md)
  + Learned sparse lexical: [uniCOIL noexp](docs/regressions-dl20-doc-segmented-unicoil-noexp.md), [uniCOIL with d2q-T5](docs/regressions-dl20-doc-segmented-unicoil.md)
+ Regressions for MS MARCO (V2) Passage Ranking:
  + Unsupervised lexical, original corpus: [baselines](docs/regressions-msmarco-v2-passage.md), [doc2query-T5](docs/regressions-msmarco-v2-passage-d2q-t5.md)
  + Unsupervised lexical, augmented corpus: [baselines](docs/regressions-msmarco-v2-passage-augmented.md), [doc2query-T5](docs/regressions-msmarco-v2-passage-augmented-d2q-t5.md)
  + Learned sparse lexical: [uniCOIL noexp zero-shot](docs/regressions-msmarco-v2-passage-unicoil-noexp-0shot.md), [uniCOIL with d2q-T5 zero-shot](docs/regressions-msmarco-v2-passage-unicoil-0shot.md)
+ Regressions for MS MARCO (V2) Document Ranking:
  + Unsupervised lexical, complete doc: [baselines](docs/regressions-msmarco-v2-doc.md), [doc2query-T5](docs/regressions-msmarco-v2-doc-d2q-t5.md)
  + Unsupervised lexical, segmented doc: [baselines](docs/regressions-msmarco-v2-doc-segmented.md), [doc2query-T5](docs/regressions-msmarco-v2-doc-segmented-d2q-t5.md)
  + Learned sparse lexical: [uniCOIL noexp zero-shot](docs/regressions-msmarco-v2-doc-segmented-unicoil-noexp-0shot.md), [uniCOIL with d2q-T5 zero-shot](docs/regressions-msmarco-v2-doc-segmented-unicoil-0shot.md)
+ Regressions for TREC 2021 Deep Learning Track, Passage Ranking:
  + Unsupervised lexical, original corpus: [baselines](docs/regressions-dl21-passage.md), [doc2query-T5](docs/regressions-dl21-passage-d2q-t5.md)
  + Unsupervised lexical, augmented corpus: [baselines](docs/regressions-dl21-passage-augmented.md), [doc2query-T5](docs/regressions-dl21-passage-augmented-d2q-t5.md)
  + Learned sparse lexical: [uniCOIL noexp zero-shot](docs/regressions-dl21-passage-unicoil-noexp-0shot.md), [uniCOIL with d2q-T5 zero-shot](docs/regressions-dl21-passage-unicoil-0shot.md)
+ Regressions for TREC 2021 Deep Learning Track, Document Ranking:
  + Unsupervised lexical, complete doc: [baselines](docs/regressions-dl21-doc.md), [doc2query-T5](docs/regressions-dl21-doc-d2q-t5.md)
  + Unsupervised lexical, segmented doc: [baselines](docs/regressions-dl21-doc-segmented.md), [doc2query-T5](docs/regressions-dl21-doc-segmented-d2q-t5.md)
  + Learned sparse lexical: [uniCOIL noexp zero-shot](docs/regressions-dl21-doc-segmented-unicoil-noexp-0shot.md), [uniCOIL with d2q-T5 zero-shot](docs/regressions-dl21-doc-segmented-unicoil-0shot.md)
+ Regressions for TREC News Tracks (Background Linking Task): [2018](docs/regressions-backgroundlinking18.md), [2019](docs/regressions-backgroundlinking19.md), [2020](docs/regressions-backgroundlinking20.md)
+ Regressions for [FEVER Fact Verification](docs/regressions-fever.md)
+ Regressions for [NTCIR-8 ACLIA (IR4QA subtask, Monolingual Chinese)](docs/regressions-ntcir8-zh.md)
+ Regressions for [CLEF 2006 Monolingual French](docs/regressions-clef06-fr.md)
+ Regressions for [TREC 2002 Monolingual Arabic](docs/regressions-trec02-ar.md)
+ Regressions for FIRE 2012: [Monolingual Bengali](docs/regressions-fire12-bn.md), [Monolingual Hindi](docs/regressions-fire12-hi.md), [Monolingual English](docs/regressions-fire12-en.md)
+ Regressions for Mr. TyDi (v1.1) baselines : [ar](docs/regressions-mrtydi-v1.1-ar.md), [bn](docs/regressions-mrtydi-v1.1-bn.md), [en](docs/regressions-mrtydi-v1.1-en.md), [fi](docs/regressions-mrtydi-v1.1-fi.md), [id](docs/regressions-mrtydi-v1.1-id.md), [ja](docs/regressions-mrtydi-v1.1-ja.md), [ko](docs/regressions-mrtydi-v1.1-ko.md), [ru](docs/regressions-mrtydi-v1.1-ru.md), [sw](docs/regressions-mrtydi-v1.1-sw.md), [te](docs/regressions-mrtydi-v1.1-te.md), [th](docs/regressions-mrtydi-v1.1-th.md)
+ Regressions for BEIR (v1.0.0):
  + TREC-COVID: ["flat" baseline](docs/regressions-beir-v1.0.0-trec-covid-flat.md), [SPLADE-distill CoCodenser-medium](docs/regressions-beir-v1.0.0-trec-covid-splade-distil-cocodenser-medium.md)
  + BioASQ: ["flat" baseline](docs/regressions-beir-v1.0.0-bioasq-flat.md)
  + NFCorpus: ["flat" baseline](docs/regressions-beir-v1.0.0-nfcorpus-flat.md), [SPLADE-distill CoCodenser-medium](docs/regressions-beir-v1.0.0-nfcorpus-splade-distil-cocodenser-medium.md)
  + NQ: ["flat" baseline](docs/regressions-beir-v1.0.0-nq-flat.md), [SPLADE-distill CoCodenser-medium](docs/regressions-beir-v1.0.0-nq-splade-distil-cocodenser-medium.md)
  + HotpotQA: ["flat" baseline](docs/regressions-beir-v1.0.0-hotpotqa-flat.md), [SPLADE-distill CoCodenser-medium](docs/regressions-beir-v1.0.0-hotpotqa-splade-distil-cocodenser-medium.md)
  + FiQA-2018: ["flat" baseline](docs/regressions-beir-v1.0.0-fiqa-flat.md), ["multifield" baseline](docs/regressions-beir-v1.0.0-fiqa-multifield.md), [SPLADE-distill CoCodenser-medium](docs/regressions-beir-v1.0.0-fiqa-splade-distil-cocodenser-medium.md)
  + Signal-1M(RT): ["flat" baseline](docs/regressions-beir-v1.0.0-signal1m-flat.md)
  + TREC-NEWS: ["flat" baseline](docs/regressions-beir-v1.0.0-trec-news-flat.md)
  + Robust04: ["flat" baseline](docs/regressions-beir-v1.0.0-robust04-flat.md)
  + ArguAna: ["flat" baseline](docs/regressions-beir-v1.0.0-arguana-flat.md), ["multifield" baseline](docs/regressions-beir-v1.0.0-arguana-multifield.md), [SPLADE-distill CoCodenser-medium](docs/regressions-beir-v1.0.0-arguana-splade-distil-cocodenser-medium.md)
  + Touche2020: ["flat" baseline](docs/regressions-beir-v1.0.0-webis-touche2020-flat.md), [SPLADE-distill CoCodenser-medium](docs/regressions-beir-v1.0.0-webis-touche2020-splade-distil-cocodenser-medium.md)
  + CQADupStack-Android: ["flat" baseline](docs/regressions-beir-v1.0.0-cqadupstack-android-flat.md)
  + CQADupStack-English: ["flat" baseline](docs/regressions-beir-v1.0.0-cqadupstack-english-flat.md)
  + CQADupStack-Gaming: ["flat" baseline](docs/regressions-beir-v1.0.0-cqadupstack-gaming-flat.md)
  + CQADupStack-Gis: ["flat" baseline](docs/regressions-beir-v1.0.0-cqadupstack-gis-flat.md)
  + CQADupStack-Mathematica: ["flat" baseline](docs/regressions-beir-v1.0.0-cqadupstack-mathematica-flat.md)
  + CQADupStack-Physics: ["flat" baseline](docs/regressions-beir-v1.0.0-cqadupstack-physics-flat.md)
  + CQADupStack-Programmers: ["flat" baseline](docs/regressions-beir-v1.0.0-cqadupstack-programmers-flat.md)
  + CQADupStack-Stats: ["flat" baseline](docs/regressions-beir-v1.0.0-cqadupstack-stats-flat.md)
  + CQADupStack-Tex: ["flat" baseline](docs/regressions-beir-v1.0.0-cqadupstack-tex-flat.md)
  + CQADupStack-Unix: ["flat" baseline](docs/regressions-beir-v1.0.0-cqadupstack-unix-flat.md)
  + CQADupStack-Webmasters: ["flat" baseline](docs/regressions-beir-v1.0.0-cqadupstack-webmasters-flat.md)
  + CQADupStack-Wordpress: ["flat" baseline](docs/regressions-beir-v1.0.0-cqadupstack-wordpress-flat.md)
  + Quora: ["flat" baseline](docs/regressions-beir-v1.0.0-quora-flat.md), [SPLADE-distill CoCodenser-medium](docs/regressions-beir-v1.0.0-quora-splade-distil-cocodenser-medium.md)
  + DBPedia: ["flat" baseline](docs/regressions-beir-v1.0.0-dbpedia-entity-flat.md), [SPLADE-distill CoCodenser-medium](docs/regressions-beir-v1.0.0-dbpedia-entity-splade-distil-cocodenser-medium.md)
  + SCIDOCS: ["flat" baseline](docs/regressions-beir-v1.0.0-scidocs-flat.md), [SPLADE-distill CoCodenser-medium](docs/regressions-beir-v1.0.0-scidocs-splade-distil-cocodenser-medium.md)
  + FEVER: ["flat" baseline](docs/regressions-beir-v1.0.0-fever-flat.md), [SPLADE-distill CoCodenser-medium](docs/regressions-beir-v1.0.0-fever-splade-distil-cocodenser-medium.md)
  + Climate-FEVER: ["flat" baseline](docs/regressions-beir-v1.0.0-climate-fever-flat.md), ["multifield" baseline](docs/regressions-beir-v1.0.0-climate-fever-multifield.md), [SPLADE-distill CoCodenser-medium](docs/regressions-beir-v1.0.0-climate-fever-splade-distil-cocodenser-medium.md)
  + SciFact: ["flat" baseline](docs/regressions-beir-v1.0.0-scifact-flat.md), [SPLADE-distill CoCodenser-medium](docs/regressions-beir-v1.0.0-scifact-splade-distil-cocodenser-medium.md)

## Additional Documentation

The experiments described below are not associated with rigorous end-to-end regression testing and thus provide a lower standard of reproducibility.
For the most part, manual copying and pasting of commands into a shell is required to reproduce our results.

### MS MARCO (V1)

+ Reproducing [BM25 baselines for MS MARCO Passage Ranking](docs/experiments-msmarco-passage.md)
+ Reproducing [BM25 baselines for MS MARCO Document Ranking](docs/experiments-msmarco-doc.md)
+ Reproducing [baselines for the MS MARCO Document Ranking Leaderboard](docs/experiments-msmarco-doc-leaderboard.md)
+ Reproducing [doc2query results](docs/experiments-doc2query.md) (MS MARCO Passage Ranking and TREC-CAR)
+ Reproducing [docTTTTTquery results](docs/experiments-docTTTTTquery.md) (MS MARCO Passage and Document Ranking)
+ Notes about reproduction issues with [MS MARCO Document Ranking w/ docTTTTTquery](docs/experiments-msmarco-doc-doc2query-details.md)

### MS MARCO (V2)

+ Reproducing [BM25 baselines on the MS MARCO V2 Collections](docs/experiments-msmarco-v2.md)

### TREC-COVID and CORD-19

+ [Indexing AI2's COVID-19 Open Research Dataset](docs/experiments-cord19.md)
+ [Baselines for the TREC-COVID Challenge](docs/experiments-covid.md)
+ [Baselines for the TREC-COVID Challenge using doc2query](docs/experiments-covid-doc2query.md)
+ [Ingesting AI2's COVID-19 Open Research Dataset into Solr and Elasticsearch](docs/experiments-cord19-extras.md)

### Other Experiments

+ [Working with the 20 Newsgroups Dataset](docs/experiments-20newsgroups.md)
+ [Guide to BM25 baselines for the FEVER Fact Verification Task](docs/experiments-fever.md)
+ [Guide to reproducing "Neural Hype" Experiments](docs/experiments-forum2018.md)
+ [Guide to running experiments on the AI2 Open Research Corpus](docs/experiments-openresearch.md)
+ [Experiments from Yang et al. (JDIQ 2018)](docs/experiments-jdiq2018.md)
+ Runbooks for TREC 2018: [[Anserini group](docs/runbook-trec2018-anserini.md)] [[h2oloo group](docs/runbook-trec2018-h2oloo.md)]
+ Runbook for [ECIR 2019 paper on axiomatic semantic term matching](docs/runbook-ecir2019-axiomatic.md)
+ Runbook for [ECIR 2019 paper on cross-collection relevance feedback](docs/runbook-ecir2019-ccrf.md)

### Other Features

+ Use Anserini in Python via [Pyserini](http://pyserini.io/)
+ Anserini integrates with SolrCloud via [Solrini](docs/solrini.md)
+ Anserini integrates with Elasticsearch via [Elasterini](docs/elastirini.md)
+ Anserini supports [approximate nearest-neighbor search](docs/approximate-nearestneighbor.md) on arbitrary dense vectors with Lucene

## How Can I Contribute?

If you've found Anserini to be helpful, we have a simple request for you to contribute back.
In the course of [reproducing](docs/reproducibility.md) baseline results on standard test collections, please let us know if you're successful by sending us a pull request with a simple note, like what appears at the bottom of [the page for Disks 4 &amp; 5](docs/regressions-disk45.md).
Reproducibility is important to us, and we'd like to know about successes as well as failures.
Since the regression documentation is auto-generated, pull requests should be sent against the [raw templates](https://github.com/castorini/anserini/tree/master/src/main/resources/docgen/templates).
Then the regression documentation can be generated using the [`bin/build.sh`](bin/build.sh) script.
In turn, you'll be recognized as a [contributor](https://github.com/castorini/anserini/graphs/contributors).

Beyond that, there are always [open issues](https://github.com/castorini/anserini/issues) we would appreciate help on!

## Release History

+ v0.14.2: March 24, 2022 [[Release Notes](docs/release-notes/release-notes-v0.14.2.md)]
+ v0.14.1: February 27, 2022 [[Release Notes](docs/release-notes/release-notes-v0.14.1.md)]
+ v0.14.0: January 10, 2022 [[Release Notes](docs/release-notes/release-notes-v0.14.0.md)]
+ v0.13.5: November 2, 2021 [[Release Notes](docs/release-notes/release-notes-v0.13.5.md)]
+ v0.13.4: October 22, 2021 [[Release Notes](docs/release-notes/release-notes-v0.13.4.md)]
+ v0.13.3: August 22, 2021 [[Release Notes](docs/release-notes/release-notes-v0.13.3.md)]
+ v0.13.2: July 20, 2021 [[Release Notes](docs/release-notes/release-notes-v0.13.2.md)]
+ v0.13.1: June 29, 2021 [[Release Notes](docs/release-notes/release-notes-v0.13.1.md)]
+ v0.13.0: June 22, 2021 [[Release Notes](docs/release-notes/release-notes-v0.13.0.md)]
+ v0.12.0: April 29, 2021 [[Release Notes](docs/release-notes/release-notes-v0.12.0.md)]
+ v0.11.0: February 13, 2021 [[Release Notes](docs/release-notes/release-notes-v0.11.0.md)]
+ v0.10.1: January 8, 2021 [[Release Notes](docs/release-notes/release-notes-v0.10.1.md)]
+ v0.10.0: November 25, 2020 [[Release Notes](docs/release-notes/release-notes-v0.10.0.md)]
+ v0.9.4: June 25, 2020 [[Release Notes](docs/release-notes/release-notes-v0.9.4.md)]
+ v0.9.3: May 26, 2020 [[Release Notes](docs/release-notes/release-notes-v0.9.3.md)]
+ v0.9.2: May 14, 2020 [[Release Notes](docs/release-notes/release-notes-v0.9.2.md)]
+ v0.9.1: May 6, 2020 [[Release Notes](docs/release-notes/release-notes-v0.9.1.md)]
+ v0.9.0: April 18, 2020 [[Release Notes](docs/release-notes/release-notes-v0.9.0.md)]
+ v0.8.1: March 22, 2020 [[Release Notes](docs/release-notes/release-notes-v0.8.1.md)]
+ v0.8.0: March 11, 2020 [[Release Notes](docs/release-notes/release-notes-v0.8.0.md)]
+ v0.7.2: January 25, 2020 [[Release Notes](docs/release-notes/release-notes-v0.7.2.md)]
+ v0.7.1: January 9, 2020 [[Release Notes](docs/release-notes/release-notes-v0.7.1.md)]
+ v0.7.0: December 13, 2019 [[Release Notes](docs/release-notes/release-notes-v0.7.0.md)]
+ v0.6.0: September 6, 2019 [[Release Notes](docs/release-notes/release-notes-v0.6.0.md)][[Known Issues](docs/known-issues/known-issues-v0.6.0.md)]
+ v0.5.1: June 11, 2019 [[Release Notes](docs/release-notes/release-notes-v0.5.1.md)]
+ v0.5.0: June 5, 2019 [[Release Notes](docs/release-notes/release-notes-v0.5.0.md)]
+ v0.4.0: March 4, 2019 [[Release Notes](docs/release-notes/release-notes-v0.4.0.md)]
+ v0.3.0: December 16, 2018 [[Release Notes](docs/release-notes/release-notes-v0.3.0.md)]
+ v0.2.0: September 10, 2018 [[Release Notes](docs/release-notes/release-notes-v0.2.0.md)]
+ v0.1.0: July 4, 2018 [[Release Notes](docs/release-notes/release-notes-v0.1.0.md)]

## Historical Notes

+ Anserini was upgraded to Java 11 at commit [`17b702d`](https://github.com/castorini/anserini/commit/17b702d9c3c0971e04eb8386ab83bf2fb2630714) (7/11/2019) from Java 8.
Maven 3.3+ is also required.
+ Anserini was upgraded to Lucene 8.0 as of commit [`75e36f9`](https://github.com/castorini/anserini/commit/75e36f97f7037d1ceb20fa9c91582eac5e974131) (6/12/2019); prior to that, the toolkit uses Lucene 7.6.
Based on [preliminary experiments](docs/lucene7-vs-lucene8.md), query evaluation latency has been much improved in Lucene 8.
As a result of this upgrade, results of all regressions have changed slightly.
To reproducible old results from Lucene 7.6, use [v0.5.1](https://github.com/castorini/anserini/releases).

## References

+ Jimmy Lin, Matt Crane, Andrew Trotman, Jamie Callan, Ishan Chattopadhyaya, John Foley, Grant Ingersoll, Craig Macdonald, Sebastiano Vigna. [Toward Reproducible Baselines: The Open-Source IR Reproducibility Challenge.](https://cs.uwaterloo.ca/~jimmylin/publications/Lin_etal_ECIR2016.pdf) _ECIR 2016_.
+ Peilin Yang, Hui Fang, and Jimmy Lin. [Anserini: Enabling the Use of Lucene for Information Retrieval Research.](https://dl.acm.org/authorize?N47337) _SIGIR 2017_.
+ Peilin Yang, Hui Fang, and Jimmy Lin. [Anserini: Reproducible Ranking Baselines Using Lucene.](https://dl.acm.org/citation.cfm?doid=3289400.3239571) _Journal of Data and Information Quality_, 10(4), Article 16, 2018.

## Acknowledgments

This research is supported in part by the Natural Sciences and Engineering Research Council (NSERC) of Canada.
Previous support came from the U.S. National Science Foundation under IIS-1423002 and CNS-1405688.
Any opinions, findings, and conclusions or recommendations expressed do not necessarily reflect the views of the sponsors.
