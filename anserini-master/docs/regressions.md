# Anserini Regression Experiments

Regression experiments in Anserini are hooked into a rigorous end-to-end regression framework driven by the [`run_regression.py`](../src/main/python/run_regression.py) script.
This script executes experiments end to end and verifies effectiveness numbers without requiring any human intervention.
Specifically, the script includes:

+ Building the index from scratch.
+ Running all retrieval runs in Anserini documentation.
+ Verifying results against effectiveness figures stored in [`src/main/resources/regression/`](../src/main/resources/regression/).

Furthermore, the regression documentation pages are auto-generated based on [raw templates](../src/main/resources/docgen/templates).

Internally at Waterloo, we are continuously running these regression tests to ensure that new commits do not break any existing experimental runs (see below).
We keep a [change log](regressions-log.md) whenever effectiveness changes or when new regressions are added.

## The Anserini Reproducibility Promise

It is the highest priority of the project to ensure that all regression experiments are reproducible _all the time_.
This means that anyone with the document collection should be able to reproduce _exactly_ the effectiveness scores we report in our regression documentation pages.

We hold this ideal in such high esteem and are so dedicated to reproducibility that if you discover a broken regression before we do, Jimmy Lin will buy you a beverage of choice (coffee, beer, etc.) at the next event you see him (e.g., SIGIR, TREC, etc.).

Here's how you can help:
In the course of reproducing one of our results, please let us know you've been successful by sending a pull request with a simple note, like what appears at the bottom of [the Robust04 page](regressions-robust04.md).
Since the regression documentation is auto-generated, pull requests should be sent against the [raw templates](../src/main/resources/docgen/templates).
In turn, you'll be recognized as a [contributor](https://github.com/castorini/anserini/graphs/contributors).

## Invocations

Internally at Waterloo, we have two machines (`tuna.cs.uwaterloo.ca` and `orca.cs.uwaterloo.ca`) for the development of Anserini and is set up to run the regression experiments.
However, anyone can run these regressions, with only minor changes to the `input_roots` section of the [YAML file](../src/main/resources/regression/robust04.yaml) to tell the regression script where to find the collection.

Copy and paste the following lines into console to run the regressions from the raw collection, which includes building indexes from scratch (note the `--index` option):

```
nohup python src/main/python/run_regression.py --index --collection disk12 >& logs/log.disk12 &
nohup python src/main/python/run_regression.py --index --collection disk45 >& logs/log.disk45 &
nohup python src/main/python/run_regression.py --index --collection robust05 >& logs/log.robust05 &
nohup python src/main/python/run_regression.py --index --collection core17 >& logs/log.core17 &
nohup python src/main/python/run_regression.py --index --collection core18 >& logs/log.core18 &

nohup python src/main/python/run_regression.py --index --collection mb11 >& logs/log.mb11 &
nohup python src/main/python/run_regression.py --index --collection mb13 >& logs/log.mb13 &

nohup python src/main/python/run_regression.py --index --collection wt10g >& logs/log.wt10g &
nohup python src/main/python/run_regression.py --index --collection gov2 >& logs/log.gov2 &
nohup python src/main/python/run_regression.py --index --collection cw09b >& logs/log.cw09b &
nohup python src/main/python/run_regression.py --index --collection cw12b13 >& logs/log.cw12b13 &
nohup python src/main/python/run_regression.py --index --collection cw12 >& logs/log.cw12 &

nohup python src/main/python/run_regression.py --index --collection car17v1.5 >& logs/log.car17v1.5 &
nohup python src/main/python/run_regression.py --index --collection car17v2.0 >& logs/log.car17v2.0 &
nohup python src/main/python/run_regression.py --index --collection car17v2.0-doc2query >& logs/log.car17v2.0-doc2query &

nohup python src/main/python/run_regression.py --index --collection msmarco-passage >& logs/log.msmarco-passage &
nohup python src/main/python/run_regression.py --index --collection msmarco-passage-doc2query >& logs/log.msmarco-passage-doc2query &
nohup python src/main/python/run_regression.py --index --collection msmarco-passage-docTTTTTquery >& logs/log.msmarco-passage-docTTTTTquery &

nohup python src/main/python/run_regression.py --index --collection msmarco-doc >& logs/log.msmarco-doc &
nohup python src/main/python/run_regression.py --index --collection msmarco-doc-per-passage >& logs/log.msmarco-doc-per-passage &
nohup python src/main/python/run_regression.py --index --collection msmarco-doc-per-passage-v2 >& logs/log.msmarco-doc-per-passage-v2 &
nohup python src/main/python/run_regression.py --index --collection msmarco-doc-per-passage-v3 >& logs/log.msmarco-doc-per-passage-v3 &
nohup python src/main/python/run_regression.py --index --collection msmarco-doc-docTTTTTquery-per-doc >& logs/log.msmarco-doc-docTTTTTquery-per-doc &
nohup python src/main/python/run_regression.py --index --collection msmarco-doc-docTTTTTquery-per-passage >& logs/log.msmarco-doc-docTTTTTquery-per-passage &
nohup python src/main/python/run_regression.py --index --collection msmarco-doc-docTTTTTquery-per-passage-v3 >& logs/log.msmarco-doc-docTTTTTquery-per-passage-v3 &

nohup python src/main/python/run_regression.py --index --collection msmarco-passage-deepimpact >& logs/log.msmarco-passage-deepimpact &
nohup python src/main/python/run_regression.py --index --collection msmarco-passage-unicoil >& logs/log.msmarco-passage-unicoil &
nohup python src/main/python/run_regression.py --index --collection msmarco-passage-unicoil-tilde-expansion >& logs/log.msmarco-passage-unicoil-tilde-expansion &
nohup python src/main/python/run_regression.py --index --collection msmarco-passage-distill-splade-max >& logs/log.msmarco-passage-distill-splade-max &

nohup python src/main/python/run_regression.py --index --collection dl19-passage >& logs/log.dl19-passage &
nohup python src/main/python/run_regression.py --index --collection dl19-passage-docTTTTTquery >& logs/log.dl19-passage-docTTTTTquery &
nohup python src/main/python/run_regression.py --index --collection dl19-doc >& logs/log.dl19-doc &
nohup python src/main/python/run_regression.py --index --collection dl19-doc-per-passage >& logs/log.dl19-doc-per-passage &
nohup python src/main/python/run_regression.py --index --collection dl19-doc-docTTTTTquery-per-doc >& logs/log.dl19-doc-docTTTTTquery-per-doc &
nohup python src/main/python/run_regression.py --index --collection dl19-doc-docTTTTTquery-per-passage >& logs/log.dl19-doc-docTTTTTquery-per-passage &

nohup python src/main/python/run_regression.py --index --collection dl20-passage >& logs/log.dl20-passage &
nohup python src/main/python/run_regression.py --index --collection dl20-passage-docTTTTTquery >& logs/log.dl20-passage-docTTTTTquery &
nohup python src/main/python/run_regression.py --index --collection dl20-doc >& logs/log.dl20-doc &
nohup python src/main/python/run_regression.py --index --collection dl20-doc-per-passage >& logs/log.dl20-doc-per-passage &
nohup python src/main/python/run_regression.py --index --collection dl20-doc-docTTTTTquery-per-doc >& logs/log.dl20-doc-docTTTTTquery-per-doc &
nohup python src/main/python/run_regression.py --index --collection dl20-doc-docTTTTTquery-per-passage >& logs/log.dl20-doc-docTTTTTquery-per-passage &

nohup python src/main/python/run_regression.py --index --collection msmarco-v2-doc >& logs/log.msmarco-v2-doc &
nohup python src/main/python/run_regression.py --index --collection msmarco-v2-doc-segmented >& logs/log.msmarco-v2-doc-segmented &
nohup python src/main/python/run_regression.py --index --collection msmarco-v2-passage >& logs/log.msmarco-v2-passage &
nohup python src/main/python/run_regression.py --index --collection msmarco-v2-passage-augmented >& logs/log.msmarco-v2-passage-augmented &

nohup python src/main/python/run_regression.py --index --collection msmarco-v2-passage-unicoil-noexp-0shot >& logs/log.msmarco-v2-passage-unicoil-noexp-0shot &
nohup python src/main/python/run_regression.py --index --collection msmarco-v2-doc-segmented-unicoil-noexp-0shot >& logs/log.msmarco-v2-doc-segmented-unicoil-noexp-0shot &

nohup python src/main/python/run_regression.py --index --collection dl21-passage >& logs/log.dl21-passage &
nohup python src/main/python/run_regression.py --index --collection dl21-passage-augmented >& logs/log.dl21-passage-augmented &
nohup python src/main/python/run_regression.py --index --collection dl21-passage-unicoil-noexp-0shot >& logs/log.dl21-passage-unicoil-noexp-0shot &
nohup python src/main/python/run_regression.py --index --collection dl21-doc >& logs/log.dl21-doc &
nohup python src/main/python/run_regression.py --index --collection dl21-doc-segmented >& logs/log.dl21-doc-segmented &
nohup python src/main/python/run_regression.py --index --collection dl21-doc-segmented-unicoil-noexp-0shot >& logs/log.dl21-doc-segmented-unicoil-noexp-0shot &

nohup python src/main/python/run_regression.py --index --collection backgroundlinking18 >& logs/log.backgroundlinking18 &
nohup python src/main/python/run_regression.py --index --collection backgroundlinking19 >& logs/log.backgroundlinking19 &
nohup python src/main/python/run_regression.py --index --collection backgroundlinking20 >& logs/log.backgroundlinking20 &

nohup python src/main/python/run_regression.py --index --collection fever >& logs/log.fever &

nohup python src/main/python/run_regression.py --index --collection ntcir8-zh >& logs/log.ntcir8-zh &
nohup python src/main/python/run_regression.py --index --collection clef06-fr >& logs/log.clef06-fr &
nohup python src/main/python/run_regression.py --index --collection trec02-ar >& logs/log.trec02-ar &
nohup python src/main/python/run_regression.py --index --collection fire12-bn >& logs/log.fire12-bn &
nohup python src/main/python/run_regression.py --index --collection fire12-hi >& logs/log.fire12-hi &
nohup python src/main/python/run_regression.py --index --collection fire12-en >& logs/log.fire12-en &
```

Watch out: the full `cw12` regression takes a couple days to run and generates a 12TB index!
To skip building the indexes from scratch, remove the `--index` option.
