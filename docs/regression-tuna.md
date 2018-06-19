# Regression Experiments on Tuna

The machine `tuna.cs.uwaterloo.ca` is a server used internally at Waterloo for development of Anserini. There are a number regression scripts in `src/main/python/` for running end-to-end regression experiments, which include:

+ Building the index from scratch.
+ Running all retrieval runs in Anserini documentation.
+ Verifying results against effectiveness figures reported in the Anserini documentation.

The scripts and the end-to-end experiments they replicate are as follows:

+ `run_regression_disks12.py`: [Experiments on Disks 1 &amp; 2](experiments-disk12.md)
+ `run_regression_robust04.py`: [Robust04 Experiments on Disks 4 &amp; 5](experiments-robust04.md)
+ `run_regression_robust05.py`: [Robust05 Experiments on AQUAINT](experiments-robust05.md)
+ `run_regression_wt10g.py`: [Experiments on Wt10g](experiments-wt10g.md)
+ `run_regression_gov2.py`: [Experiments on Gov2](experiments-gov2.md)
+ `run_regression_clueweb09b.py`: [Experiments on ClueWeb09 (Category B)](experiments-clueweb09b.md)

These scripts work with either Python 2 or Python 3.
