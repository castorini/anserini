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
+ `python src/main/python/run_regression.py --collection robust04`: [Robust04 Experiments on Disks 4 &amp; 5](experiments-robust04.md)
+ `python src/main/python/run_regression.py --collection robust05`: [Robust05 Experiments on AQUAINT](experiments-robust05.md)
+ `python src/main/python/run_regression.py --collection wt10g`: [Experiments on Wt10g](experiments-wt10g.md)
+ `python src/main/python/run_regression.py --collection gov2`: [Experiments on Gov2](experiments-gov2.md)
+ `python src/main/python/run_regression.py --collection cw09b`: [Experiments on ClueWeb09  (Category B)](experiments-cw09b.md)
+ `python src/main/python/run_regression.py --collection cw12b13`: [Experiments on ClueWeb12-B13](experiments-cw12b13.md)
+ `python src/main/python/run_regression.py --collection cw12`: [Experiments on ClueWeb12](experiments-cw12.md)
+ `python src/main/python/run_regression.py --collection mb11`: [Experiments on Microblog2011](experiments-mb11.md)
+ `python src/main/python/run_regression.py --collection mb13`: [Experiments on Microblog2013](experiments-mb13.md)
+ `python src/main/python/run_regression.py --collection core17`: [Experiments on Core Track 2017](experiments-core17.md)
