# Anserini Regression Experiments

Internally at Waterloo, `tuna.cs.uwaterloo.ca` is used for the development of Anserini and is set up to run the regression experiments described here.
The regression script `src/main/python/run_regression.py` runs end-to-end regression experiments for various collections, which includes:

+ Building the index from scratch.
+ Running all retrieval runs in Anserini documentation.
+ Verifying results against effectiveness figures stored in `src/main/resources/regression/`.

We keep a [change log](regressions-log.md) whenever effectiveness changes or when new regressions are added.

## Invocations

tl;dr - Copy and paste the following lines into console on `tuna` to run the regressions without building indexes from scratch:

```
nohup python src/main/python/run_regression.py --collection disk12 >& log.disk12 &
nohup python src/main/python/run_regression.py --collection robust04 >& log.robust04 &
nohup python src/main/python/run_regression.py --collection robust05 >& log.robust05 &
nohup python src/main/python/run_regression.py --collection core17 >& log.core17 &
nohup python src/main/python/run_regression.py --collection core18 >& log.core18 &

nohup python src/main/python/run_regression.py --collection mb11 >& log.mb11 &
nohup python src/main/python/run_regression.py --collection mb13 >& log.mb13 &

nohup python src/main/python/run_regression.py --collection wt10g >& log.wt10g &
nohup python src/main/python/run_regression.py --collection gov2 >& log.gov2 &
nohup python src/main/python/run_regression.py --collection cw09b >& log.cw09b &
nohup python src/main/python/run_regression.py --collection cw12b13 >& log.cw12b13 &
nohup python src/main/python/run_regression.py --collection cw12 >& log.cw12 &

nohup python src/main/python/run_regression.py --collection car17v1.5 >& log.car17v1.5 &
nohup python src/main/python/run_regression.py --collection car17v2.0 >& log.car17v2.0 &

nohup python src/main/python/run_regression.py --collection msmarco-passage >& log.msmarco-passage &
nohup python src/main/python/run_regression.py --collection msmarco-passage-doc2query >& log.msmarco-passage-doc2query &
nohup python src/main/python/run_regression.py --collection msmarco-doc >& log.msmarco-doc &

nohup python src/main/python/run_regression.py --collection ntcir8-zh >& log.ntcir8-zh &
```

Copy and paste the following lines into console on `tuna` to run the regressions from the raw collection, which includes building indexes from scratch (note difference is the additional `--index` option):

```
nohup python src/main/python/run_regression.py --index --collection disk12 >& log.disk12 &
nohup python src/main/python/run_regression.py --index --collection robust04 >& log.robust04 &
nohup python src/main/python/run_regression.py --index --collection robust05 >& log.robust05 &
nohup python src/main/python/run_regression.py --index --collection core17 >& log.core17 &
nohup python src/main/python/run_regression.py --index --collection core18 >& log.core18 &

nohup python src/main/python/run_regression.py --index --collection mb11 >& log.mb11 &
nohup python src/main/python/run_regression.py --index --collection mb13 >& log.mb13 &

nohup python src/main/python/run_regression.py --index --collection wt10g >& log.wt10g &
nohup python src/main/python/run_regression.py --index --collection gov2 >& log.gov2 &
nohup python src/main/python/run_regression.py --index --collection cw09b >& log.cw09b &
nohup python src/main/python/run_regression.py --index --collection cw12b13 >& log.cw12b13 &
nohup python src/main/python/run_regression.py --index --collection cw12 >& log.cw12 &

nohup python src/main/python/run_regression.py --index --collection car17v1.5 >& log.car17v1.5 &
nohup python src/main/python/run_regression.py --index --collection car17v2.0 >& log.car17v2.0 &

nohup python src/main/python/run_regression.py --index --collection msmarco-passage >& log.msmarco-passage &
nohup python src/main/python/run_regression.py --index --collection msmarco-passage-doc2query >& log.msmarco-passage-doc2query &
nohup python src/main/python/run_regression.py --index --collection msmarco-doc >& log.msmarco-doc &

nohup python src/main/python/run_regression.py --index --collection ntcir8-zh >& log.ntcir8-zh &
```

Watch out: the full `cw12` regress takes a couple days to run and generates a 12TB index!
