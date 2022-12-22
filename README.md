
Anserini
========
[![build](https://github.com/castorini/anserini/actions/workflows/maven.yml/badge.svg)](https://github.com/castorini/anserini/actions)
[![codecov](https://codecov.io/gh/castorini/anserini/branch/master/graph/badge.svg)](https://codecov.io/gh/castorini/anserini)
[![Generic badge](https://img.shields.io/badge/Lucene-v9.3.0-brightgreen.svg)](https://archive.apache.org/dist/lucene/java/9.3.0/)
[![Maven Central](https://img.shields.io/maven-central/v/io.anserini/anserini?color=brightgreen)](https://search.maven.org/search?q=a:anserini)
[![LICENSE](https://img.shields.io/badge/license-Apache-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![doi](http://img.shields.io/badge/doi-10.1145%2F3239571-blue.svg?style=flat)](https://doi.org/10.1145/3239571)

Anserini is a toolkit for reproducible information retrieval research.
By building on Lucene, we aim to bridge the gap between academic information retrieval research and the practice of building real-world search applications.
Among other goals, our effort aims to be [the opposite of this](http://phdcomics.com/comics/archive.php?comicid=1689).[*](docs/reproducibility.md)
Anserini grew out of [a reproducibility study of various open-source retrieval engines in 2016](https://cs.uwaterloo.ca/~jimmylin/publications/Lin_etal_ECIR2016.pdf) (Lin et al., ECIR 2016). 
See [Yang et al. (SIGIR 2017)](https://dl.acm.org/authorize?N47337) and [Yang et al. (JDIQ 2018)](https://dl.acm.org/citation.cfm?doid=3289400.3239571) for overviews.

**NOTE**: Anserini was recently upgraded to Lucene 9.3 at commit [`272565`](https://github.com/castorini/anserini/commit/27256551e958f39495b04e89ef55de9d27f33414) (8/2/2022): this upgrade creates backward compatibility issues, see [#1952](https://github.com/castorini/anserini/issues/1952).
Anserini will automatically detect Lucene 8 indexes and disable consistent tie-breaking to avoid runtime errors.
However, Lucene 9 code running on Lucene 8 indexes may give slightly different results than Lucene 8 code running on Lucene 8 indexes.
Lucene 8 code will _not_ run on Lucene 9 indexes.
Pyserini has been upgraded, but similar issues apply: Lucene 9 code running on Lucene 8 indexes may give slightly different results than Lucene 8 code running on Lucene 8 indexes.

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
A simple workaround is to skip tests by adding `-Dmaven.test.skip=true` to the above `mvn` command.
See [#1121](https://github.com/castorini/pyserini/discussions/1121) for additional discussions on debugging Windows build errors.

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

<details>
<summary>MS MARCO V1 Passage Corpus</summary>

### MS MARCO V1 Passage Corpus

|   | dev | DL19 | DL20 |
|---|:---:|:----:|:----:|
| **Unsupervised Lexical** |
| BoW baselines | [+](docs/regressions-msmarco-passage.md) | [+](docs/regressions-dl19-passage.md) | [+](docs/regressions-dl20-passage.md) |
| Quantized BM25 | [✓](docs/regressions-msmarco-passage-bm25-b8.md) | [✓](docs/regressions-dl19-passage-bm25-b8.md) | [✓](docs/regressions-dl20-passage-bm25-b8.md) |
| WP baselines | [+](docs/regressions-msmarco-passage-wp.md) | [+](docs/regressions-dl19-passage-wp.md) | [+](docs/regressions-dl20-passage-wp.md) |
| Huggingface WP baselines | [+](docs/regressions-msmarco-passage-hgf-wp.md) | [+](docs/regressions-dl19-passage-hgf-wp.md) | [+](docs/regressions-dl20-passage-hgf-wp.md) |
| doc2query | [+](docs/regressions-msmarco-passage-doc2query.md) |
| doc2query-T5 | [+](docs/regressions-msmarco-passage-docTTTTTquery.md) | [+](docs/regressions-dl19-passage-docTTTTTquery.md) | [+](docs/regressions-dl20-passage-docTTTTTquery.md) |
| **Learned sparse lexical (uniCOIL family)** |
| uniCOIL noexp | [✓](docs/regressions-msmarco-passage-unicoil-noexp.md) | [✓](docs/regressions-dl19-passage-unicoil-noexp.md) | [✓](docs/regressions-dl20-passage-unicoil-noexp.md) |
| uniCOIL with doc2query-T5 | [✓](docs/regressions-msmarco-passage-unicoil.md) | [✓](docs/regressions-dl19-passage-unicoil.md) | [✓](docs/regressions-dl20-passage-unicoil.md) |
| uniCOIL with TILDE | [✓](docs/regressions-msmarco-passage-unicoil-tilde-expansion.md) |
| **Learned sparse lexical (other)** |
| DeepImpact | [✓](docs/regressions-msmarco-passage-deepimpact.md) |
| SPLADEv2 | [✓](docs/regressions-msmarco-passage-distill-splade-max.md) |
| SPLADE-distill CoCodenser-medium | [✓](docs/regressions-msmarco-passage-splade-distil-cocodenser-medium.md) | [✓](docs/regressions-dl19-passage-splade-distil-cocodenser-medium.md) | [✓](docs/regressions-dl20-passage-splade-distil-cocodenser-medium.md) |

</details>
<details>
<summary>MS MARCO V1 Document Corpus</summary>

### MS MARCO V1 Document Corpus

|   | dev | DL19 | DL20 |
|---|:---:|:----:|:----:|
| **Unsupervised lexical, complete doc**[*](docs/experiments-msmarco-doc-doc2query-details.md) |
| BoW baselines | [+](docs/regressions-msmarco-doc.md) | [+](docs/regressions-dl19-doc.md) | [+](docs/regressions-dl20-doc.md) |
| WP baselines | [+](docs/regressions-msmarco-doc-wp.md) | [+](docs/regressions-dl19-doc-wp.md) | [+](docs/regressions-dl20-doc-wp.md) |
| Huggingface WP baselines | [+](docs/regressions-msmarco-doc-hgf-wp.md) | [+](docs/regressions-dl19-doc-hgf-wp.md) | [+](docs/regressions-dl20-doc-hgf-wp.md) |
| doc2query-T5 | [+](docs/regressions-msmarco-doc-docTTTTTquery.md) | [+](docs/regressions-dl19-doc-docTTTTTquery.md) | [+](docs/regressions-dl20-doc-docTTTTTquery.md) |
| **Unsupervised lexical, segmented doc**[*](docs/experiments-msmarco-doc-doc2query-details.md) |
| BoW baselines | [+](docs/regressions-msmarco-doc-segmented.md) | [+](docs/regressions-dl19-doc-segmented.md) | [+](docs/regressions-dl20-doc-segmented.md) |
| WP baselines | [+](docs/regressions-msmarco-doc-segmented-wp.md) | [+](docs/regressions-dl19-doc-segmented-wp.md) | [+](docs/regressions-dl20-doc-segmented-wp.md) |
| doc2query-T5 | [+](docs/regressions-msmarco-doc-segmented-docTTTTTquery.md) | [+](docs/regressions-dl19-doc-segmented-docTTTTTquery.md) | [+](docs/regressions-dl20-doc-segmented-docTTTTTquery.md) |
| **Learned sparse lexical** |
| uniCOIL noexp | [✓](docs/regressions-msmarco-doc-segmented-unicoil-noexp.md) | [✓](docs/regressions-dl19-doc-segmented-unicoil-noexp.md) | [✓](docs/regressions-dl20-doc-segmented-unicoil-noexp.md) |
| uniCOIL with doc2query-T5 | [✓](docs/regressions-msmarco-doc-segmented-unicoil.md) | [✓](docs/regressions-dl19-doc-segmented-unicoil.md) | [✓](docs/regressions-dl20-doc-segmented-unicoil.md) |

</details>
<details>
<summary>MS MARCO V2 Passage Corpus</summary>

### MS MARCO V2 Passage Corpus

|   | dev | DL21 |                           DL22                            |
|---|:---:|:----:|:---------------------------------------------------------:|
| **Unsupervised lexical, original corpus** |
| baselines | [+](docs/regressions-msmarco-v2-passage.md) | [+](docs/regressions-dl21-passage.md) |          [+](docs/regressions-dl22-passage.md)           |
| doc2query-T5 | [+](docs/regressions-msmarco-v2-passage-d2q-t5.md) | [+](docs/regressions-dl21-passage-d2q-t5.md) |       [+](docs/regressions-dl22-passage-d2q-t5.md)        |
| **Unsupervised lexical, augmented corpus** |
| baselines | [+](docs/regressions-msmarco-v2-passage-augmented.md) | [+](docs/regressions-dl21-passage-augmented.md) |      [+](docs/regressions-dl22-passage-augmented.md)      |
| doc2query-T5 | [+](docs/regressions-msmarco-v2-passage-augmented-d2q-t5.md) | [+](docs/regressions-dl21-passage-augmented-d2q-t5.md) |  [+](docs/regressions-dl22-passage-augmented-d2q-t5.md)   |
| **Learned sparse lexical** |
| uniCOIL noexp zero-shot | [✓](docs/regressions-msmarco-v2-passage-unicoil-noexp-0shot.md) | [✓](docs/regressions-dl21-passage-unicoil-noexp-0shot.md) | [✓](docs/regressions-dl22-passage-unicoil-noexp-0shot.md) |
| uniCOIL with doc2query-T5 zero-shot | [✓](docs/regressions-msmarco-v2-passage-unicoil-0shot.md) | [✓](docs/regressions-dl21-passage-unicoil-0shot.md) |    [✓](docs/regressions-dl22-passage-unicoil-0shot.md)    |

</details>
<details>
<summary>MS MARCO V2 Document Corpus</summary>

### MS MARCO V2 Document Corpus

|   | dev | DL21 |
|---|:---:|:----:|
| **Unsupervised lexical, complete doc** |
| baselines | [+](docs/regressions-msmarco-v2-doc.md) | [+](docs/regressions-dl21-doc.md) |
| doc2query-T5 | [+](docs/regressions-msmarco-v2-doc-d2q-t5.md) | [+](docs/regressions-dl21-doc-d2q-t5.md) |
| **Unsupervised lexical, segmented doc** |
| baselines | [+](docs/regressions-msmarco-v2-doc-segmented.md) | [+](docs/regressions-dl21-doc-segmented.md) |
| doc2query-T5 | [+](docs/regressions-msmarco-v2-doc-segmented-d2q-t5.md) | [+](docs/regressions-dl21-doc-segmented-d2q-t5.md) |
| **Learned sparse lexical** |
| uniCOIL noexp zero-shot | [✓](docs/regressions-msmarco-v2-doc-segmented-unicoil-noexp-0shot-v2.md) | [✓](docs/regressions-dl21-doc-segmented-unicoil-noexp-0shot-v2.md) |
| uniCOIL with doc2query-T5 zero-shot | [✓](docs/regressions-msmarco-v2-doc-segmented-unicoil-0shot-v2.md) | [✓](docs/regressions-dl21-doc-segmented-unicoil-0shot-v2.md) |

</details>
<details>
<summary>Regressions for BEIR (v1.0.0)</summary>

### Regressions for BEIR (v1.0.0)

+ F = "flat" baseline
+ MF = "multifield" baseline
+ UCx = uniCOIL (noexp)
+ SPLADE = SPLADE-distill CoCodenser-medium

| Corpus | flat | flat-wp | multifield | UCx | SPLADE |
|--------|:----:|:-------:|:----------:|:------:|:------:|
| TREC-COVID | [+](docs/regressions-beir-v1.0.0-trec-covid-flat.md)     | [+](docs/regressions-beir-v1.0.0-trec-covid-flat-wp.md)   | [+](docs/regressions-beir-v1.0.0-trec-covid-multifield.md)     | [+](docs/regressions-beir-v1.0.0-trec-covid-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-trec-covid-splade-distil-cocodenser-medium.md) |
| BioASQ     | [+](docs/regressions-beir-v1.0.0-bioasq-flat.md)         | [+](docs/regressions-beir-v1.0.0-bioasq-flat-wp.md)       | [+](docs/regressions-beir-v1.0.0-bioasq-multifield.md)         | [+](docs/regressions-beir-v1.0.0-bioasq-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-bioasq-splade-distil-cocodenser-medium.md) |
| NFCorpus   | [+](docs/regressions-beir-v1.0.0-nfcorpus-flat.md)       | [+](docs/regressions-beir-v1.0.0-nfcorpus-flat-wp.md)     | [+](docs/regressions-beir-v1.0.0-nfcorpus-multifield.md)       | [+](docs/regressions-beir-v1.0.0-nfcorpus-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-nfcorpus-splade-distil-cocodenser-medium.md) |
| NQ         | [+](docs/regressions-beir-v1.0.0-nq-flat.md)             | [+](docs/regressions-beir-v1.0.0-nq-flat-wp.md)           | [+](docs/regressions-beir-v1.0.0-nq-multifield.md)             | [+](docs/regressions-beir-v1.0.0-nq-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-nq-splade-distil-cocodenser-medium.md) |
| HotpotQA   | [+](docs/regressions-beir-v1.0.0-hotpotqa-flat.md)       | [+](docs/regressions-beir-v1.0.0-hotpotqa-flat-wp.md)     | [+](docs/regressions-beir-v1.0.0-hotpotqa-multifield.md)       | [+](docs/regressions-beir-v1.0.0-hotpotqa-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-hotpotqa-splade-distil-cocodenser-medium.md) |
| FiQA-2018  | [+](docs/regressions-beir-v1.0.0-fiqa-flat.md)           | [+](docs/regressions-beir-v1.0.0-fiqa-flat-wp.md)         | [+](docs/regressions-beir-v1.0.0-fiqa-multifield.md)           | [+](docs/regressions-beir-v1.0.0-fiqa-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-fiqa-splade-distil-cocodenser-medium.md) |
| Signal-1M(RT) | [+](docs/regressions-beir-v1.0.0-signal1m-flat.md)    | [+](docs/regressions-beir-v1.0.0-signal1m-flat-wp.md)     | [+](docs/regressions-beir-v1.0.0-signal1m-multifield.md)       | [+](docs/regressions-beir-v1.0.0-signal1m-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-signal1m-splade-distil-cocodenser-medium.md) |
| TREC-NEWS     | [+](docs/regressions-beir-v1.0.0-trec-news-flat.md)   | [+](docs/regressions-beir-v1.0.0-trec-news-flat-wp.md)    | [+](docs/regressions-beir-v1.0.0-trec-news-multifield.md)      | [+](docs/regressions-beir-v1.0.0-trec-news-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-trec-news-splade-distil-cocodenser-medium.md) |
| Robust04      | [+](docs/regressions-beir-v1.0.0-robust04-flat.md)    | [+](docs/regressions-beir-v1.0.0-robust04-flat-wp.md)     | [+](docs/regressions-beir-v1.0.0-robust04-multifield.md)       | [+](docs/regressions-beir-v1.0.0-robust04-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-robust04-splade-distil-cocodenser-medium.md) |
| ArguAna       | [+](docs/regressions-beir-v1.0.0-arguana-flat.md)     | [+](docs/regressions-beir-v1.0.0-arguana-flat-wp.md)      | [+](docs/regressions-beir-v1.0.0-arguana-multifield.md)        | [+](docs/regressions-beir-v1.0.0-arguana-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-arguana-splade-distil-cocodenser-medium.md) |
| Touche2020    | [+](docs/regressions-beir-v1.0.0-webis-touche2020-flat.md)                    | [+](docs/regressions-beir-v1.0.0-webis-touche2020-flat-wp.md)         | [+](docs/regressions-beir-v1.0.0-webis-touche2020-multifield.md)               | [+](docs/regressions-beir-v1.0.0-webis-touche2020-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-webis-touche2020-splade-distil-cocodenser-medium.md) |
| CQADupStack-Android       | [+](docs/regressions-beir-v1.0.0-cqadupstack-android-flat.md)     | [+](docs/regressions-beir-v1.0.0-cqadupstack-android-flat-wp.md)      | [+](docs/regressions-beir-v1.0.0-cqadupstack-android-multifield.md)   | [+](docs/regressions-beir-v1.0.0-cqadupstack-android-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-android-splade-distil-cocodenser-medium.md) |
| CQADupStack-English       | [+](docs/regressions-beir-v1.0.0-cqadupstack-english-flat.md)     | [+](docs/regressions-beir-v1.0.0-cqadupstack-english-flat-wp.md)      | [+](docs/regressions-beir-v1.0.0-cqadupstack-english-multifield.md)   | [+](docs/regressions-beir-v1.0.0-cqadupstack-english-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-english-splade-distil-cocodenser-medium.md) |
| CQADupStack-Gaming        | [+](docs/regressions-beir-v1.0.0-cqadupstack-gaming-flat.md)      | [+](docs/regressions-beir-v1.0.0-cqadupstack-gaming-flat-wp.md)       | [+](docs/regressions-beir-v1.0.0-cqadupstack-gaming-multifield.md)    | [+](docs/regressions-beir-v1.0.0-cqadupstack-gaming-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-gaming-splade-distil-cocodenser-medium.md) |
| CQADupStack-Gis           | [+](docs/regressions-beir-v1.0.0-cqadupstack-gis-flat.md)         | [+](docs/regressions-beir-v1.0.0-cqadupstack-gis-flat-wp.md)          | [+](docs/regressions-beir-v1.0.0-cqadupstack-gis-multifield.md)       | [+](docs/regressions-beir-v1.0.0-cqadupstack-gis-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-gis-splade-distil-cocodenser-medium.md) |
| CQADupStack-Mathematica   | [+](docs/regressions-beir-v1.0.0-cqadupstack-mathematica-flat.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-mathematica-flat-wp.md)  | [+](docs/regressions-beir-v1.0.0-cqadupstack-mathematica-multifield.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-mathematica-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-mathematica-splade-distil-cocodenser-medium.md) |
| CQADupStack-Physics       | [+](docs/regressions-beir-v1.0.0-cqadupstack-physics-flat.md)     | [+](docs/regressions-beir-v1.0.0-cqadupstack-physics-flat-wp.md)      | [+](docs/regressions-beir-v1.0.0-cqadupstack-physics-multifield.md)   | [+](docs/regressions-beir-v1.0.0-cqadupstack-physics-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-physics-splade-distil-cocodenser-medium.md) |
| CQADupStack-Programmers   | [+](docs/regressions-beir-v1.0.0-cqadupstack-programmers-flat.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-programmers-flat-wp.md)  | [+](docs/regressions-beir-v1.0.0-cqadupstack-programmers-multifield.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-programmers-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-programmers-splade-distil-cocodenser-medium.md) |
| CQADupStack-Stats         | [+](docs/regressions-beir-v1.0.0-cqadupstack-stats-flat.md)       | [+](docs/regressions-beir-v1.0.0-cqadupstack-stats-flat-wp.md)        | [+](docs/regressions-beir-v1.0.0-cqadupstack-stats-multifield.md)     | [+](docs/regressions-beir-v1.0.0-cqadupstack-stats-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-stats-splade-distil-cocodenser-medium.md) |
| CQADupStack-Tex           | [+](docs/regressions-beir-v1.0.0-cqadupstack-tex-flat.md)         | [+](docs/regressions-beir-v1.0.0-cqadupstack-tex-flat-wp.md)          | [+](docs/regressions-beir-v1.0.0-cqadupstack-tex-multifield.md)       | [+](docs/regressions-beir-v1.0.0-cqadupstack-tex-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-tex-splade-distil-cocodenser-medium.md) |
| CQADupStack-Unix          | [+](docs/regressions-beir-v1.0.0-cqadupstack-unix-flat.md)        | [+](docs/regressions-beir-v1.0.0-cqadupstack-unix-flat-wp.md)         | [+](docs/regressions-beir-v1.0.0-cqadupstack-unix-multifield.md)      | [+](docs/regressions-beir-v1.0.0-cqadupstack-unix-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-unix-splade-distil-cocodenser-medium.md) |
| CQADupStack-Webmasters    | [+](docs/regressions-beir-v1.0.0-cqadupstack-webmasters-flat.md)  | [+](docs/regressions-beir-v1.0.0-cqadupstack-webmasters-flat-wp.md)   | [+](docs/regressions-beir-v1.0.0-cqadupstack-webmasters-multifield.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-webmasters-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-webmasters-splade-distil-cocodenser-medium.md) |
| CQADupStack-Wordpress     | [+](docs/regressions-beir-v1.0.0-cqadupstack-wordpress-flat.md)   | [+](docs/regressions-beir-v1.0.0-cqadupstack-wordpress-flat-wp.md)    | [+](docs/regressions-beir-v1.0.0-cqadupstack-wordpress-multifield.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-wordpress-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-cqadupstack-wordpress-splade-distil-cocodenser-medium.md) |
| Quora         | [+](docs/regressions-beir-v1.0.0-quora-flat.md)           | [+](docs/regressions-beir-v1.0.0-quora-flat-wp.md)            | [+](docs/regressions-beir-v1.0.0-quora-multifield.md)             | [+](docs/regressions-beir-v1.0.0-quora-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-quora-splade-distil-cocodenser-medium.md) |
| DBPedia       | [+](docs/regressions-beir-v1.0.0-dbpedia-entity-flat.md)  | [+](docs/regressions-beir-v1.0.0-dbpedia-entity-flat-wp.md)   | [+](docs/regressions-beir-v1.0.0-dbpedia-entity-multifield.md)    | [+](docs/regressions-beir-v1.0.0-dbpedia-entity-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-dbpedia-entity-splade-distil-cocodenser-medium.md) |
| SCIDOCS       | [+](docs/regressions-beir-v1.0.0-scidocs-flat.md)         | [+](docs/regressions-beir-v1.0.0-scidocs-flat-wp.md)          | [+](docs/regressions-beir-v1.0.0-scidocs-multifield.md)              | [+](docs/regressions-beir-v1.0.0-scidocs-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-scidocs-splade-distil-cocodenser-medium.md) |
| FEVER         | [+](docs/regressions-beir-v1.0.0-fever-flat.md)           | [+](docs/regressions-beir-v1.0.0-fever-flat-wp.md)            | [+](docs/regressions-beir-v1.0.0-fever-multifield.md)             | [+](docs/regressions-beir-v1.0.0-fever-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-fever-splade-distil-cocodenser-medium.md) |
| Climate-FEVER | [+](docs/regressions-beir-v1.0.0-climate-fever-flat.md)   | [+](docs/regressions-beir-v1.0.0-climate-fever-flat-wp.md)    | [+](docs/regressions-beir-v1.0.0-climate-fever-multifield.md)     | [+](docs/regressions-beir-v1.0.0-climate-fever-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-climate-fever-splade-distil-cocodenser-medium.md) |
| SciFact       | [+](docs/regressions-beir-v1.0.0-scifact-flat.md)         | [+](docs/regressions-beir-v1.0.0-scifact-flat-wp.md)          | [+](docs/regressions-beir-v1.0.0-scifact-multifield.md)          | [+](docs/regressions-beir-v1.0.0-scifact-unicoil-noexp.md) | [+](docs/regressions-beir-v1.0.0-scifact-splade-distil-cocodenser-medium.md) |

</details>
<details>
<summary>Regressions for MIRACL</summary>

### Regressions for MIRACL

|   | BM25 |
|---|:---:|
| Arabic | [+](docs/regressions-miracl-v1.0-ar.md) | 
| Bengali | [+](docs/regressions-miracl-v1.0-bn.md) | 
| English | [+](docs/regressions-miracl-v1.0-en.md) | 
| Spanish | [+](docs/regressions-miracl-v1.0-es.md) | 
| Persian | [+](docs/regressions-miracl-v1.0-fa.md) | 
| Finnish | [+](docs/regressions-miracl-v1.0-fi.md) | 
| French | [+](docs/regressions-miracl-v1.0-fr.md) | 
| Hindi | [+](docs/regressions-miracl-v1.0-hi.md) | 
| Indonesian | [+](docs/regressions-miracl-v1.0-id.md) | 
| Japanese | [+](docs/regressions-miracl-v1.0-ja.md) | 
| Korean | [+](docs/regressions-miracl-v1.0-ko.md) | 
| Russian | [+](docs/regressions-miracl-v1.0-ru.md) | 
| Swahili | [+](docs/regressions-miracl-v1.0-sw.md) | 
| Telugu | [+](docs/regressions-miracl-v1.0-te.md) | 
| Thai | [+](docs/regressions-miracl-v1.0-th.md) | 
| Chinese | [+](docs/regressions-miracl-v1.0-zh.md) | 

</details>
<details>
<summary>Other Cross-Lingual and Multi-Lingual Regressions</summary>

### Other Cross-Lingual and Multi-Lingual Regressions

+ Regressions for [NTCIR-8 ACLIA (IR4QA subtask, Monolingual Chinese)](docs/regressions-ntcir8-zh.md)
+ Regressions for [CLEF 2006 Monolingual French](docs/regressions-clef06-fr.md)
+ Regressions for [TREC 2002 Monolingual Arabic](docs/regressions-trec02-ar.md)
+ Regressions for FIRE 2012: [Monolingual Bengali](docs/regressions-fire12-bn.md), [Monolingual Hindi](docs/regressions-fire12-hi.md), [Monolingual English](docs/regressions-fire12-en.md)
+ Regressions for Mr. TyDi (v1.1) baselines: [ar](docs/regressions-mrtydi-v1.1-ar.md), [bn](docs/regressions-mrtydi-v1.1-bn.md), [en](docs/regressions-mrtydi-v1.1-en.md), [fi](docs/regressions-mrtydi-v1.1-fi.md), [id](docs/regressions-mrtydi-v1.1-id.md), [ja](docs/regressions-mrtydi-v1.1-ja.md), [ko](docs/regressions-mrtydi-v1.1-ko.md), [ru](docs/regressions-mrtydi-v1.1-ru.md), [sw](docs/regressions-mrtydi-v1.1-sw.md), [te](docs/regressions-mrtydi-v1.1-te.md), [th](docs/regressions-mrtydi-v1.1-th.md)
+ Regressions for HC4 (v1.0) baselines on HC4 corpora: [Persian](docs/regressions-hc4-v1.0-fa.md), [Russian](docs/regressions-hc4-v1.0-ru.md), [Chinese](docs/regressions-hc4-v1.0-zh.md)
+ Regressions for HC4 (v1.0) baselines on original NeuCLIR22 corpora: [Persian](docs/regressions-hc4-neuclir22-fa.md), [Russian](docs/regressions-hc4-neuclir22-ru.md), [Chinese](docs/regressions-hc4-neuclir22-zh.md)
+ Regressions for HC4 (v1.0) baselines on translated NeuCLIR22 corpora: [Persian](docs/regressions-hc4-neuclir22-fa-en.md), [Russian](docs/regressions-hc4-neuclir22-ru-en.md), [Chinese](docs/regressions-hc4-neuclir22-zh-en.md)
+ Regressions for TREC 2022 NeuCLIR Track (query translation): [Persian](docs/regressions-neuclir22-fa-qt.md), [Russian](docs/regressions-neuclir22-ru-qt.md), [Chinese](docs/regressions-neuclir22-zh-qt.md)
+ Regressions for TREC 2022 NeuCLIR Track (document translation): [Persian](docs/regressions-neuclir22-fa-dt.md), [Russian](docs/regressions-neuclir22-ru-dt.md), [Chinese](docs/regressions-neuclir22-zh-dt.md)

</details>
<details>
<summary>Other Regressions</summary>

### Other Regressions

+ Regressions for [Disks 1 &amp; 2 (TREC 1-3)](docs/regressions-disk12.md), [Disks 4 &amp; 5 (TREC 7-8, Robust04)](docs/regressions-disk45.md), [AQUAINT (Robust05)](docs/regressions-robust05.md)
+ Regressions for [the New York Times Corpus (Core17)](docs/regressions-core17.md), [the Washington Post Corpus (Core18)](docs/regressions-core18.md)
+ Regressions for [Wt10g](docs/regressions-wt10g.md), [Gov2](docs/regressions-gov2.md)
+ Regressions for [ClueWeb09 (Category B)](docs/regressions-cw09b.md), [ClueWeb12-B13](docs/regressions-cw12b13.md), [ClueWeb12](docs/regressions-cw12.md)
+ Regressions for [Tweets2011 (MB11 &amp; MB12)](docs/regressions-mb11.md), [Tweets2013 (MB13 &amp; MB14)](docs/regressions-mb13.md)
+ Regressions for Complex Answer Retrieval (CAR17): [v1.5](docs/regressions-car17v1.5.md), [v2.0](docs/regressions-car17v2.0.md), [v2.0 with doc2query](docs/regressions-car17v2.0-doc2query.md)
+ Regressions for TREC News Tracks (Background Linking Task): [2018](docs/regressions-backgroundlinking18.md), [2019](docs/regressions-backgroundlinking19.md), [2020](docs/regressions-backgroundlinking20.md)
+ Regressions for [FEVER Fact Verification](docs/regressions-fever.md)
+ Regressions for DPR Wikipedia QA baselines: [100-word splits](docs/regressions-wikipedia-dpr-100w-bm25.md)

</details>

### Available Corpora

<details>
<summary>Variants of MS MARCO V1 and V2 corpora available for download</summary>

| Corpora                                                                                                                                         |   Size | Checksum                           |
|:------------------------------------------------------------------------------------------------------------------------------------------------|-------:|:-----------------------------------|
| [MS MARCO V1 passage: Quantized BM25](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-bm25-b8.tar)                            | 1.2 GB | `0a623e2c97ac6b7e814bf1323a97b435` |
| [MS MARCO V1 passage: uniCOIL (noexp)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-unicoil-noexp.tar)                     | 2.7 GB | `f17ddd8c7c00ff121c3c3b147d2e17d8` |
| [MS MARCO V1 passage: uniCOIL (d2q-T5)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-unicoil.tar)                          | 3.4 GB | `78eef752c78c8691f7d61600ceed306f` |
| [MS MARCO V1 passage: uniCOIL (TILDE)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-unicoil-tilde-expansion.tar)           | 3.9 GB | `12a9c289d94e32fd63a7d39c9677d75c` |
| [MS MARCO V1 passage: DeepImpact](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-deepimpact.tar)                             | 3.6 GB | `73843885b503af3c8b3ee62e5f5a9900` |
| [MS MARCO V1 passage: SPLADEv2](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-distill-splade-max.tar)                       | 9.9 GB | `b5d126f5d9a8e1b3ef3f5cb0ba651725` |
| [MS MARCO V1 passage: SPLADE CoCodenser](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-passage-splade_distil_cocodenser_medium.tar) | 4.9 GB | `f77239a26d08856e6491a34062893b0c` |
| [MS MARCO V1 doc: uniCOIL (noexp)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-doc-segmented-unicoil-noexp.tar)                   |  11 GB | `11b226e1cacd9c8ae0a660fd14cdd710` |
| [MS MARCO V1 doc: uniCOIL (d2q-T5)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco-doc-segmented-unicoil.tar)                        |  19 GB | `6a00e2c0c375cb1e52c83ae5ac377ebb` |
| [MS MARCO V2 passage: uniCOIL (noexp)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_passage_unicoil_noexp_0shot.tar)            |  24 GB | `d9cc1ed3049746e68a2c91bf90e5212d` |
| [MS MARCO V2 passage: uniCOIL (d2q-T5)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_passage_unicoil_0shot.tar)                 |  41 GB | `1949a00bfd5e1f1a230a04bbc1f01539` |
| [MS MARCO V2 doc: uniCOIL (noexp)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_doc_segmented_unicoil_noexp_0shot_v2.tar)       |  55 GB | `97ba262c497164de1054f357caea0c63` |
| [MS MARCO V2 doc: uniCOIL (d2q-T5)](https://rgw.cs.uwaterloo.ca/JIMMYLIN-bucket0/data/msmarco_v2_doc_segmented_unicoil_0shot_v2.tar)            |  72 GB | `c5639748c2cbad0152e10b0ebde3b804` |

</details>

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

+ v0.16.2: December 12, 2022 [[Release Notes](docs/release-notes/release-notes-v0.16.2.md)]
+ v0.16.1: November 2, 2022 [[Release Notes](docs/release-notes/release-notes-v0.16.1.md)]
+ v0.16.0: October 23, 2022 [[Release Notes](docs/release-notes/release-notes-v0.16.0.md)]
+ v0.15.0: September 22, 2022 [[Release Notes](docs/release-notes/release-notes-v0.15.0.md)]
+ v0.14.4: July 31, 2022 [[Release Notes](docs/release-notes/release-notes-v0.14.4.md)]
+ v0.14.3: May 9, 2022 [[Release Notes](docs/release-notes/release-notes-v0.14.3.md)]
+ v0.14.2: March 24, 2022 [[Release Notes](docs/release-notes/release-notes-v0.14.2.md)]
+ v0.14.1: February 27, 2022 [[Release Notes](docs/release-notes/release-notes-v0.14.1.md)]
+ v0.14.0: January 10, 2022 [[Release Notes](docs/release-notes/release-notes-v0.14.0.md)]

<details>
<summary>older... (and historic notes)</summary>

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

+ Anserini was upgraded to Lucene 9.3 at commit [`272565`](https://github.com/castorini/anserini/commit/27256551e958f39495b04e89ef55de9d27f33414) (8/2/2022): this upgrade created backward compatibility issues, see [#1952](https://github.com/castorini/anserini/issues/1952).
+ Anserini was upgraded to Java 11 at commit [`17b702d`](https://github.com/castorini/anserini/commit/17b702d9c3c0971e04eb8386ab83bf2fb2630714) (7/11/2019) from Java 8.
Maven 3.3+ is also required.
+ Anserini was upgraded to Lucene 8.0 as of commit [`75e36f9`](https://github.com/castorini/anserini/commit/75e36f97f7037d1ceb20fa9c91582eac5e974131) (6/12/2019); prior to that, the toolkit uses Lucene 7.6.
Based on [preliminary experiments](docs/lucene7-vs-lucene8.md), query evaluation latency has been much improved in Lucene 8.
As a result of this upgrade, results of all regressions have changed slightly.
To reproducible old results from Lucene 7.6, use [v0.5.1](https://github.com/castorini/anserini/releases).

</details>

## References

+ Jimmy Lin, Matt Crane, Andrew Trotman, Jamie Callan, Ishan Chattopadhyaya, John Foley, Grant Ingersoll, Craig Macdonald, Sebastiano Vigna. [Toward Reproducible Baselines: The Open-Source IR Reproducibility Challenge.](https://cs.uwaterloo.ca/~jimmylin/publications/Lin_etal_ECIR2016.pdf) _ECIR 2016_.
+ Peilin Yang, Hui Fang, and Jimmy Lin. [Anserini: Enabling the Use of Lucene for Information Retrieval Research.](https://dl.acm.org/authorize?N47337) _SIGIR 2017_.
+ Peilin Yang, Hui Fang, and Jimmy Lin. [Anserini: Reproducible Ranking Baselines Using Lucene.](https://dl.acm.org/citation.cfm?doid=3289400.3239571) _Journal of Data and Information Quality_, 10(4), Article 16, 2018.

## Acknowledgments

This research is supported in part by the Natural Sciences and Engineering Research Council (NSERC) of Canada.
Previous support came from the U.S. National Science Foundation under IIS-1423002 and CNS-1405688.
Any opinions, findings, and conclusions or recommendations expressed do not necessarily reflect the views of the sponsors.
