## Regression Experiments on Tuna

The machine `tuna.cs.uwaterloo.ca` is a server used internally at Waterloo for development of Anserini.
The regression script `src/main/python/run_regression.py` runs end-to-end regression experiments for various collections, which includes:

+ Building the index from scratch.
+ Running all retrieval runs in Anserini documentation.
+ Verifying results against effectiveness figures stored in `src/main/resources/regression/all.yaml`.

## Requirements

Python>=2.6 or Python>=3.5

```
pip install -r src/main/python/requirements.txt
```

## Invocations

Here's how to run the end-to-end experiments on standard test collections:

+ `python src/main/python/run_regression.py --collection disk12`: [Experiments on Disks 1 &amp; 2](experiments-disk12.md)
+ `python src/main/python/run_regression.py --collection robust04`: [Experiments on Disks 4 &amp; 5 (Robust04)](experiments-robust04.md)
+ `python src/main/python/run_regression.py --collection robust05`: [Experiments on AQUAINT (Robust05)](experiments-robust05.md)
+ `python src/main/python/run_regression.py --collection core17`: [Experiments on Netw York Times (Core17)](experiments-core17.md)
+ `python src/main/python/run_regression.py --collection wt10g`: [Experiments on Wt10g](experiments-wt10g.md)
+ `python src/main/python/run_regression.py --collection gov2`: [Experiments on Gov2](experiments-gov2.md)
+ `python src/main/python/run_regression.py --collection cw09b`: [Experiments on ClueWeb09  (Category B)](experiments-cw09b.md)
+ `python src/main/python/run_regression.py --collection cw12b13`: [Experiments on ClueWeb12-B13](experiments-cw12b13.md)
+ `python src/main/python/run_regression.py --collection cw12`: [Experiments on ClueWeb12](experiments-cw12.md)
+ `python src/main/python/run_regression.py --collection mb11`: [Experiments on Tweets2011 (MB11 &amp; MB12)](experiments-mb11.md)
+ `python src/main/python/run_regression.py --collection mb13`: [Experiments on Tweets2013 (MB13 &amp; MB14)](experiments-mb13.md)
+ `python src/main/python/run_regression.py --collection car17`: [Experiments on Car17](experiments-car17.md)

## Additional Regressions

+ [JDIQ 2018 Experiments](experiments-jdiq2018.md)
+ [TREC 2018 runbook](experiments-trec2018.md)

## Log

The following log details whenever regression tests have changed in terms of effectiveness or new new regressions have been added:

### August 8, 2018

+ [commit d4b3272e7f07fa274e5d8ffd50976beb0c08de52](https://github.com/castorini/Anserini/commit/d4b3272e7f07fa274e5d8ffd50976beb0c08de52) Wed Aug 8 22:33:15 2018 -0400

Added regressions tests for CAR17.

### August 5, 2018

+ [commit c0da5105429a15fb85158d1740e0516305cd9de6](https://github.com/castorini/Anserini/commit/c0da5105429a15fb85158d1740e0516305cd9de6) Sun Aug 5 10:02:18 2018 -0700

This commit adds the effectiveness verification testing for the [JDIQ2018 Paper](experiments-jdiq2018.md).

### July 22, 2018

+ [commit 3a7beee3485526f3146e69f57899a3033e20f504](https://github.com/castorini/Anserini/commit/3a7beee3485526f3146e69f57899a3033e20f504) Sun Jul 22 10:09:46 2018 -0700
+ [commit ec5fd3d7fbee3308cd63321b77231d8b10e495a8](https://github.com/castorini/Anserini/commit/ec5fd3d7fbee3308cd63321b77231d8b10e495a8) Sun Jul 22 04:42:48 2018 -0700
+ [commit 5f8c26d328dd67e6cc538d5f9b4af44acdbc74e5](https://github.com/castorini/Anserini/commit/5f8c26d328dd67e6cc538d5f9b4af44acdbc74e5) Sat Jul 21 08:59:52 2018 -0700

These three commits establish the new regression testing infrastructure with the following tests:

+ [Experiments on Disks 1 &amp; 2](experiments-disk12.md): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
+ [Experiments on Disks 4 &amp; 5 (Robust04)](experiments-robust04.md): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
+ [Experiments on AQUAINT (Robust05)](experiments-robust05.md): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
+ [Experiments on New York Times (Core17)](experiments-core17.md): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
+ [Experiments on Wt10g](experiments-wt10g.md): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
+ [Experiments on Gov2](experiments-gov2.md): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
+ [Experiments on ClueWeb09 (Category B)](experiments-cw09b.md): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30, NDCG@20, ERR@20}
+ [Experiments on ClueWeb12-B13](experiments-cw12b13.md): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30, NDCG@20, ERR@20}
+ [Experiments on ClueWeb12](experiments-cw12.md): {BM25, QL} &#10799; {RM3} &#10799; {AP, P30, NDCG@20, ERR@20}
+ [Experiments on Tweets2011 (MB11 &amp; MB12)](experiments-mb11.md): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
+ [Experiments on Tweets2013 (MB13 &amp; MB14)](experiments-mb13.md): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
