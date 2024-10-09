# Fusion Regression Test Setup

This document provides instructions for setting up and downloading the necessary run files to perform fusion regression tests.


## Perform two regression runs for test fusion-regression-bge-flat-int8-robust04-2

One could generate the runs necessary for test fusion-regression-bge-flat-int8-robust04-2 following 
- https://github.com/castorini/anserini/blob/master/docs/regressions/regressions-beir-v1.0.0-robust04.bge-base-en-v1.5.flat-int8.cached.md
- https://github.com/castorini/anserini/blob/master/docs/regressions/regressions-beir-v1.0.0-robust04.bge-base-en-v1.5.flat.cached.md

## Run fuse-regression script with two yaml tests
```bash
python src/main/python/run_fusion_regression.py --regression fusion-regression-bge-flat-int8-robust04-2
```