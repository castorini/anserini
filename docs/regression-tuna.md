## Regression Experiments on Tuna

The machine `tuna.cs.uwaterloo.ca` is a server used internally at Waterloo for development of Anserini. There are a number regression scripts in `src/main/python/` for running end-to-end regression experiments, which include:

+ Building the index from scratch.
+ Running all retrieval runs in Anserini documentation.
+ Verifying results against effectiveness figures stored in `src/main/resources/regression/all.yaml`.

## Requirements

Python>=2.6 or Python>=3.5
`pip install -r src/main/python/requirements.txt`

## Run

The scripts and the end-to-end experiments they replicate are as follows:

+ `python src/main/python/run_regression.py --collection disk12`: [Experiments on Disks 1 &amp; 2](experiments-disk12.md)
+ `python src/main/python/run_regression.py --collection cw09b`: [Experiments on ClueWeb09 (Category B)](experiments-cw09b.md)

###Old Script:

+ `run_regression_disk12.py`: [Experiments on Disks 1 &amp; 2](experiments-disk12-old.md)
+ `run_regression_robust04.py`: [Robust04 Experiments on Disks 4 &amp; 5](experiments-robust04-old.md)
+ `run_regression_robust05.py`: [Robust05 Experiments on AQUAINT](experiments-robust05-old.md)
+ `run_regression_wt10g.py`: [Experiments on Wt10g](experiments-wt10g-old.md)
+ `run_regression_gov2.py`: [Experiments on Gov2](experiments-gov2-old.md)
+ `run_regression_clueweb09b.py`: [Experiments on ClueWeb09b](experiments-clueweb09b-old.md)
+ `run_regression_clueweb12-b13.py`: [Experiments on ClueWeb12-B13](experiments-clueweb12-b13-old.md)
+ `run_regression_clueweb12.py`: [Experiments on ClueWeb12](experiments-clueweb12-old.md)
