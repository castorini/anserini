# Anserini Regression Experiments

Internally at Waterloo, `tuna.cs.uwaterloo.ca` is used for the development of Anserini and is set up to run the regression experiments described here.
The regression script `src/main/python/run_regression.py` runs end-to-end regression experiments for various collections, which includes:

+ Building the index from scratch.
+ Running all retrieval runs in Anserini documentation.
+ Verifying results against effectiveness figures stored in `src/main/resources/regression/`.

We keep a [change log](regressions-log.md) whenever effectiveness changes or when new regressions are added.

## Requirements

Python>=2.6 or Python>=3.5

```
pip install -r src/main/python/requirements.txt
```

## Invocations

tl;dr - Copy and paste the following lines into console on `tuna` to run the regressions without building indexes from scratch:

```
nohup python src/main/python/run_regression.py --collection disk12 >& log.disk12 &
nohup python src/main/python/run_regression.py --collection robust04 >& log.robust04 &
nohup python src/main/python/run_regression.py --collection robust05 >& log.robust05 &
nohup python src/main/python/run_regression.py --collection core17 >& log.core17 &

nohup python src/main/python/run_regression.py --collection mb11 >& log.mb11 &
nohup python src/main/python/run_regression.py --collection mb13 >& log.mb13 &

nohup python src/main/python/run_regression.py --collection wt10g >& log.wt10g &
nohup python src/main/python/run_regression.py --collection gov2 >& log.gov2 &
nohup python src/main/python/run_regression.py --collection cw09b >& log.cw09b &
nohup python src/main/python/run_regression.py --collection cw12b13 >& log.cw12b13 &
nohup python src/main/python/run_regression.py --collection cw12 >& log.cw12 &

nohup python src/main/python/run_regression.py --collection car17 >& log.car17 &
```

Copy and paste the following lines into console on `tuna` to run the regressions from the raw collection, which includes building indexes from scratch (note difference is the additional `--index` option):

```
nohup python src/main/python/run_regression.py --collection disk12 --index >& log.disk12 &
nohup python src/main/python/run_regression.py --collection robust04 --index >& log.robust04 &
nohup python src/main/python/run_regression.py --collection robust05 --index >& log.robust05 &
nohup python src/main/python/run_regression.py --collection core17 --index >& log.core17 &

nohup python src/main/python/run_regression.py --collection mb11 --index >& log.mb11 &
nohup python src/main/python/run_regression.py --collection mb13 --index >& log.mb13 &

nohup python src/main/python/run_regression.py --collection wt10g --index >& log.wt10g &
nohup python src/main/python/run_regression.py --collection gov2 --index >& log.gov2 &
nohup python src/main/python/run_regression.py --collection cw09b --index >& log.cw09b &
nohup python src/main/python/run_regression.py --collection cw12b13 --index >& log.cw12b13 &
nohup python src/main/python/run_regression.py --collection cw12 --index >& log.cw12 &

nohup python src/main/python/run_regression.py --collection car17 --index >& log.car17 &
```

Watch out: the full `cw12` regress takes a couple days to run and generates a 12TB index!

Details of each specific regression:

+ `disk12`: [Experiments on Disks 1 &amp; 2](experiments-disk12.md)
+ `robust04`: [Experiments on Disks 4 &amp; 5 (Robust04)](experiments-robust04.md)
+ `robust05`: [Experiments on AQUAINT (Robust05)](experiments-robust05.md)
+ `core17`: [Experiments on Netw York Times (Core17)](experiments-core17.md)
+ `wt10g`: [Experiments on Wt10g](experiments-wt10g.md)
+ `gov2`: [Experiments on Gov2](experiments-gov2.md)
+ `cw09b`: [Experiments on ClueWeb09  (Category B)](experiments-cw09b.md)
+ `cw12b13`: [Experiments on ClueWeb12-B13](experiments-cw12b13.md)
+ `cw12`: [Experiments on ClueWeb12](experiments-cw12.md)
+ `mb11`: [Experiments on Tweets2011 (MB11 &amp; MB12)](experiments-mb11.md)
+ `mb13`: [Experiments on Tweets2013 (MB13 &amp; MB14)](experiments-mb13.md)
+ `car17`: [Experiments on Car17](experiments-car17.md)

## Additional Regressions

+ [JDIQ 2018 Experiments](experiments-jdiq2018.md)
+ [TREC 2018 runbook](experiments-trec2018.md)
