# Anserini Regressions Log

The following change log details commits to regression tests that alter effectiveness and the addition of new regression tests.
This documentation is useful for figuring why results may have changed over time.

### October 22, 2018

+ [commit 10255e0f15c8caca94f8d5376a2c7c9ad1f5b5fd](https://github.com/castorini/Anserini/commit/10255e0f15c8caca94f8d5376a2c7c9ad1f5b5fd) Mon Oct 22 20:54:28 2018 -0700

Fixed incorrect implementation of `-rm3.fbTerms`.

### September 26, 2018

+ [commit 7c882d310564e27351ed51e0c8a669a13f33b48a](https://github.com/castorini/Anserini/commit/7c882d310564e27351ed51e0c8a669a13f33b48a) Wed Sep 26 20:45:33 2018 -0400

Fixed bug as part of [Issue 429](https://github.com/castorini/Anserini/issues/429): `cw12` and `mb13` regression tests changed slightly in effectiveness.

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
