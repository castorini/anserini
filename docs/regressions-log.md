# Anserini Regressions Log

The following change log details commits to regression tests that alter effectiveness and the addition of new regression tests.
This documentation is useful for figuring why results may have changed over time.


### May 11, 2019

+ [commit 3eef2fb4808d88608d2c467418b77516eba3538a](https://github.com/castorini/anserini/commit/3eef2fb4808d88608d2c467418b77516eba3538a) Sat May 11 14:34:46 2019 -0400
+ [commit 2ba2b9582ee942aee714301b78015a2ded16da8c](https://github.com/castorini/anserini/commit/2ba2b9582ee942aee714301b78015a2ded16da8c) Sat May 11 11:20:47 2019 -0400
+ [commit d911bba180aceabbe052624b7f715a2189e25dbb](https://github.com/castorini/anserini/commit/d911bba180aceabbe052624b7f715a2189e25dbb) Fri May 10 09:12:26 2019 -0400

CAR regression refactoring: added v2.0 regression and renamed existing regression to v1.5. Both use `benchmarkY1-test` to support consistent comparisons.

### January 2, 2019

+ [commit 407f308cc543286e39701caf0acd1afab39dde2c](https://github.com/castorini/Anserini/commit/407f308cc543286e39701caf0acd1afab39dde2c) Wed Jan 2 06:34:57 2019 -0800

Added fine tuning results (i.e., SIGIR Forum article experiments) for axiomatic semantic term matching.

### December 24, 2018

+ [commit 1aa3970bd32b456025ada608389f7e4896eff19e](https://github.com/castorini/Anserini/commit/1aa3970bd32b456025ada608389f7e4896eff19e) Mon Dec 24 07:22:20 2018 -0500

Changed RM3 defaults to match settings in Indri.

### December 20, 2018

+ [commit e71df7aee42c7776a63b9845600a4075632fa11c](https://github.com/castorini/Anserini/commit/26fbb3936cb2db1d69f02ad990d83e773e7d87c2) Thu Dec 20 08:20:42 2018 -0800

Added Axiomatic F2Exp and F2Log ranking models back into Anserini (previously, we were using the default Lucene implementation as part of version 7.6 upgrade).

### December 18, 2018

+ [commit e71df7aee42c7776a63b9845600a4075632fa11c](https://github.com/castorini/Anserini/commit/e71df7aee42c7776a63b9845600a4075632fa11c) Tue Dec 18 07:45:30 2018 -0500

Upgrade to Lucene 7.6.

### November 30, 2018

+ [commit e5b87f0d6c16b47d0be6cc8fd587acd20e3fbb0d](https://github.com/castorini/Anserini/commit/e5b87f0d6c16b47d0be6cc8fd587acd20e3fbb0d) Fri Nov 30 04:57:04 2018 -0800

Added default regressions for TREC 2018 Common Core Track.

### November 16, 2018

+ [commit 2c8cd7a550faca0fc450e4159a4a874d4795ac25](https://github.com/castorini/Anserini/commit/2c8cd7a550faca0fc450e4159a4a874d4795ac25) Fri Nov 16 16:18:23 2018 -0500

This is the commit id references in the [SIGIR Forum 2018 article](http://sigir.org/wp-content/uploads/2019/01/p040.pdf).
Note that [commit 18c3211117f35f72cbc1019c125ff885f51056ea](https://github.com/castorini/Anserini/commit/18c3211117f35f72cbc1019c125ff885f51056ea) (Sun Dec 9 03:25:49 2018 -0800) contains minor fixes to the code.

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
