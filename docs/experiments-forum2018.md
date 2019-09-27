# Anserini: "Neural Hype" Baseline Experiments

This page provides documentation for replicating results from two "neural hype" papers, which questioned whether neural ranking models actually represent improvements in _ad hoc_ retrieval effectiveness over well-tuned "competitive baselines" in limited data scenarios:

+ Jimmy Lin. [The Neural Hype and Comparisons Against Weak Baselines.](http://sigir.org/wp-content/uploads/2019/01/p040.pdf) SIGIR Forum, 52(2):40-51, 2018.
+ Wei Yang, Kuang Lu, Peilin Yang, and Jimmy Lin. [Critically Examining the "Neural Hype": Weak Baselines and the Additivity of Effectiveness Gains from Neural Ranking Models.](https://cs.uwaterloo.ca/~jimmylin/publications/Yang_etal_SIGIR2019.pdf) _SIGIR 2019_.

The "competitive baseline" referenced in the two above papers is BM25+RM3, with proper parameter tuning, on the test collection from the TREC 2004 Robust Track (Robust04).
Scripts referenced on this page encode automated regressions that allow users to recreate and verify the results reported below.

The SIGIR Forum article references commit [`2c8cd7a`](https://github.com/castorini/Anserini/commit/2c8cd7a550faca0fc450e4159a4a874d4795ac25) (11/16/2018), the results of which changed slightly with an upgrade to Lucene 7.6 at commit [`e71df7a`](https://github.com/castorini/Anserini/commit/e71df7aee42c7776a63b9845600a4075632fa11c) (12/18/2018).
The SIGIR 2019 paper contains experiments performed post upgrade.

The Anserini upgrade to Lucene 8.0 at commit [`75e36f9`](https://github.com/castorini/anserini/commit/75e36f97f7037d1ceb20fa9c91582eac5e974131) (6/12/2019) broke the regression tests, which was later fixed at commit [`64bae9c`](https://github.com/castorini/anserini/commit/64bae9c8b87ad56bc8cf6ea0c5405eb2a82b3682) (7/3/2019).
This commit represents the latest state of the code and the results that can be currently replicated.
See summary in "History" section below.


## Expected Results

Retrieval models are tuned with respect to following fold definitions:

+ [Folds for 2-fold cross-validation used in "paper 1"](../src/main/resources/fine_tuning/robust04-paper1-folds.json)
+ [Folds for 5-fold cross-validation used in "paper 2"](../src/main/resources/fine_tuning/robust04-paper2-folds.json)

Here are expected results for various retrieval models:

AP                 | Paper 1 | Paper 2 |
:------------------|---------|---------|
BM25 (default)     |  0.2531 |  0.2531 |
BM25 (tuned)       |  0.2539 |  0.2531 |
QL (default)       |  0.2467 |  0.2467 |
QL (tuned)         |  0.2520 |  0.2499 |
BM25+RM3 (default) |  0.2903 |  0.2903 |
BM25+RM3 (tuned)   |  0.3043 |  0.3021 |
BM25+Ax (default)  |  0.2896 |  0.2896 |
BM25+Ax (tuned)    |  0.2940 |  0.2950 |


## Parameter Tuning

Before starting, modify the index path at `src/main/resources/fine_tuning/collections.yaml`.
The tuning script will go through the `index_roots`, concatenate with the collection's `index_path`, and take the first match as the location of the index.

Tuning BM25:

```
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25 --threads 18 --run
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25 --threads 18 --run --fold_settings src/main/resources/fine_tuning/robust04-paper1-folds.json --verbose
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25 --threads 18 --run --fold_settings src/main/resources/fine_tuning/robust04-paper2-folds.json --verbose
```

The first command runs the parameter sweeps and prints general statistics.
The second and third commands use a specific fold setting to perform cross-validation and print out model parameters.

Tuning QL (commands similarly organized):

```
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model ql --threads 18 --run
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model ql --threads 18 --run --fold_settings src/main/resources/fine_tuning/robust04-paper1-folds.json --verbose
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model ql --threads 18 --run --fold_settings src/main/resources/fine_tuning/robust04-paper2-folds.json --verbose
```

Tuning BM25+RM3 (commands similarly organized):

```
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25+rm3 --threads 18 --run
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25+rm3 --threads 18 --run --fold_settings src/main/resources/fine_tuning/robust04-paper1-folds.json --verbose
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25+rm3 --threads 18 --run --fold_settings src/main/resources/fine_tuning/robust04-paper2-folds.json --verbose
```

Tuning BM25+Ax (commands similarly organized):

```
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25+axiom --threads 18 --run
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25+axiom --threads 18 --run --fold_settings src/main/resources/fine_tuning/robust04-paper1-folds.json --verbose
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25+axiom --threads 18 --run --fold_settings src/main/resources/fine_tuning/robust04-paper2-folds.json --verbose
```


## Tuned Runs

Tuned parameter values for BM25+RM3:

+ [For the 2-fold cross-validation used in "paper 1", in terms of MAP](../src/main/resources/fine_tuning/params/params.map.robust04-paper1-folds.bm25+rm3.json)
+ [For the 5-fold cross-validation used in "paper 2", in terms of MAP](../src/main/resources/fine_tuning/params/params.map.robust04-paper2-folds.bm25+rm3.json)

To be clear, these are the tuned parameters on _that_ fold, trained on the remaining folds.

The following script will reconstruct the tuned runs for BM25+RM3:

```
python src/main/python/fine_tuning/reconstruct_robus04_tuned_run.py \
 --index lucene-index.robust04.pos+docvectors+rawdocs \
 --folds src/main/resources/fine_tuning/robust04-paper1-folds.json \
 --params src/main/resources/fine_tuning/params/params.map.robust04-paper1-folds.bm25+rm3.json \
 --output run.robust04.bm25+rm3.paper1.txt
```

Change `paper1` to `paper2` to reconstruct using the folds in paper 2.

To reconstruct runs from other retrieval models, use the parameter definitions in [`src/main/resources/fine_tuning/params/`](../src/main/resources/fine_tuning/params/), plugging them into the above command as appropriate.

Note that applying `trec_eval` to these reconstructed runs might yield AP that is a tiny bit different from the values reported above (difference of 0.0001 at the most).
This difference arises from rounding when averaging across the folds.


## History

The following documents commits that have altered effectiveness figures:


+ commit [`64bae9c`](https://github.com/castorini/anserini/commit/64bae9c8b87ad56bc8cf6ea0c5405eb2a82b3682) (7/3/2019) - Regression experiments here fixed.
+ commit [`75e36f9`](https://github.com/castorini/anserini/commit/75e36f97f7037d1ceb20fa9c91582eac5e974131) (6/12/2019) - Upgrade to Lucene 8.0 breaks regression experiments here.
+ commit [`407f308`](https://github.com/castorini/Anserini/commit/407f308cc543286e39701caf0acd1afab39dde2c) (1/2/2019) - Added results for axiomatic semantic term matching.
+ commit [`e71df7a`](https://github.com/castorini/Anserini/commit/e71df7aee42c7776a63b9845600a4075632fa11c) (12/18/2018) - Upgrade to Lucene 7.6.
+ commit [`2c8cd7a`](https://github.com/castorini/Anserini/commit/2c8cd7a550faca0fc450e4159a4a874d4795ac25) (11/16/2018) - commit id referenced in SIGIR Forum article.


