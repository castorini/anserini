# Anserini Regressions Log

The following change log details commits to regression tests that alter effectiveness and the addition of new regression tests.
This documentation is useful for figuring why results may have changed over time.

### January 20, 2022

+ commit [`1be47b`](https://github.com/castorini/anserini/commit/1be47b95cbc722fff1b883a6b91a92632a00461e) (01/20/2022)

Added MS MARCO (V2) {doc, segmented doc, passage, augmented passage} regressions for doc2query-T5 expansions (both dev/dev2 queries as well as TREC 2021 DL Track).

### January 8, 2022

+ commit [`6fcb89`](https://github.com/castorini/anserini/commit/6fcb896c61e2b8cf2f235def3e95dda5fe4cd2fc) (01/08/2022)
+ commit [`f0502c`](https://github.com/castorini/anserini/commit/f0502cc7cf4978b266b6427a594e6c57e524fd8e) (11/16/2022)

Rebuilt all MS MARCO (V1) doc regressions from scratch to fix segmentation issues described [here](experiments-msmarco-doc-doc2query-details.md).

### December 15, 2021

+ commit [`151404`](https://github.com/castorini/anserini/commit/15140448d6b6dbf12d7461fc4d90cfb3f4529f69) (12/15/2021)
+ commit [`aee51a`](https://github.com/castorini/anserini/commit/aee51adefe9d2b8f178df37abc5b236b185c5bab) (12/05/2021)

Added regressions for Mr.TyDi (v1.1).

### December 13, 2021

+ commit [`64f4d1`](https://github.com/castorini/anserini/commit/64f4d1e226e5e478ebb5cec93f3e4705d09d4326) (12/13/2021)
+ commit [`12149f`](https://github.com/castorini/anserini/commit/12149f87457f4eeff3818a76be1331f5a8a60c75) (12/09/2021)

Expanded regressions for TREC Disks 4 &amp; 5.

### November 25, 2021

+ commit [`47685b`](https://github.com/castorini/anserini/commit/47685b1c42375c1a46404effdd866dc01c424358) (11/25/2021)
+ commit [`1c5f64`](https://github.com/castorini/anserini/commit/1c5f640ee566c74d45f9c558f518b188e6473748) (11/18/2021)

Added regressions for MS MARCO V2 (dev2) and TREC 2021 DL Track queries; add uniCOIL noexp zero-shot results.

### October 18, 2021

+ commit [`828d05`](https://github.com/castorini/anserini/commit/828d05f77e3e0655c059cba07e576c67a9378c77) (10/18/2021)
+ commit [`cf5c4f`](https://github.com/castorini/anserini/commit/cf5c4fb9e8d2d8a8d883b0f0f9b778908cb49a6d) (10/16/2021)

Refactored regressions for DeepImpact and uniCOIL on MS MARCO passage, added SPLADEv2.

### October 9, 2021

+ commit [`f8b7cd`](https://github.com/castorini/anserini/commit/f8b7cd9f8d1d11ca8b5415cf868b60676aff5472) (10/09/2021)

Major refactoring of MS MARCO V2 naming conventions.

### September 5, 2021

+ commit [`f79fb6`](https://github.com/castorini/anserini/commit/f79fb67845b4b68b8c177eacb5832c209847dc29) (09/05/2021)

Added regressions for DeepImpact and uniCOIL on MS MARCO passage.

### September 4, 2021

+ commit [`112438`](https://github.com/castorini/anserini/commit/112438c85f642c3b6b5006af68f82457a43d602f) (09/04/2021)

Added regressions for MS MARCO V2 corpora, standard BM25 + PRF configurations w/ default parameters:

+ raw passage corpus, augmented passage corpus
+ raw doc corpus, segmented doc corpus

### September 2, 2021

+ commit [`f86e4e`](https://github.com/castorini/anserini/commit/f86e4e193eee806e935720c2ea8c549babb02c53) (09/02/2021)

Upgraded jsoup from v1.8.3 to v1.14.2 to address a security vulnerability.
Minor changes to the following regressions: `backgroundlinking18`, `backgroundlinking19`, `backgroundlinking20`, `core18`, `cw09b`, `cw12`, `cw12b13`, `disk12`, `gov2`, `wt10g`.

### June 14, 2021

+ commit [`b58c85`](https://github.com/castorini/anserini/commit/b58c8559b4fc473e857b9ce5ca73523d8d017b41) (06/14/2021)

Overhauled regressions for MS MARCO {passage, doc} and DL {19, 20}:
+ MS MARCO passage + {doc2query, docTTTTTquery}
+ MS MARCO doc {per-doc, per-passage} x {doc2query, docTTTTTquery}
+ {DL19, DL20} passage + {doc2query, docTTTTTquery}
+ {DL19, DL20} doc {per-doc, per passage} x {doc2query, docTTTTTquery}

### April 13, 2021

+ commit [`868afe`](https://github.com/castorini/anserini/commit/868afe9ec07fa477ce817d7a43dd5723cb4c8a86) (04/13/2021)

Updated regressions for the MS MARCO doc ranking task, we now have the complete cross product of {doc indexing, passage indexing} and {no expansion, expansion}.
Regressions now use tuned parameters.

### March 30, 3021

+ commit [`c75c63`](https://github.com/castorini/anserini/commit/c75c63b0d625c88049952fc1c72360f2b6bb07c6) (03/30/2021)

Added regressions for Anserini submissions to TREC 2020 News Track, background linking task.

### March 19, 2021

+ commit [`e9af6e`](https://github.com/castorini/anserini/commit/e9af6eca16290ed222f23fcaa9cb1547f8a10d38) (03/19/2021)

Added regressions for Anserini submissions to TREC 2020 Deep Learning Track: passage ranking (also with docTTTTTquery) and document ranking (also with per-document docTTTTTquery).

### February 24, 2021

+ commit [`90d3aa`](https://github.com/castorini/anserini/commit/90d3aa0099990cf26a54afa8458d03b30b3ace02) (02/24/2021)

Fixed bug where multi-line TREC topic titles weren't being fully parsed ([#1482](https://github.com/castorini/anserini/pull/1482)).
Affects [regressions for Disks 1 & 2](https://github.com/castorini/anserini/blob/master/docs/regressions-disk12.md).

### November 16, 2020

+ commit [`f87c94`](https://github.com/castorini/anserini/commit/f87c945fd1c1e4174468194c72e3c05688dc45dd) (11/16/2020)
+ commit [`9a8e8b`](https://github.com/castorini/anserini/commit/9a8e8b4a569036de3d68daabebf42ae302069eca) (11/12/2020)

Added regressions for MS MARCO document ranking with per-passage and per-document docTTTTTquery expansions.

### April 12, 2020

+ commit [`35f9f8`](https://github.com/castorini/anserini/commit/35f9f82f13fa4ab9b6fba494044cc7d5a3915b02) (04/12/2020)

Regression results for Core18 (Washington Post) changed due to refactoring to conform to clarified definitions of `contents()` and `raw()` in `SourceDocument`, per [Issue #1048](https://github.com/castorini/anserini/issues/1048).
Previously, both `contents()` and `raw()` returned the raw JSON, and the `WashingtonPostGenerator` extracted the article contents for indexing.
Now, `raw()` returns the raw JSON and `contents()` returns the extracted article contents for indexing (i.e., the logic for parsing the JSON has been moved from `WashingtonPostGenerator` into the collection itself).
This conforms to the principle that every collection should "know" how to parse its own contents.

Regression values went down slightly for `Ax` as a result of this refactoring.
The difference is that, before, the "empty document check" was performed on the JSON, so it never triggered (since the JSON was never empty).
With this new processing logic, the "empty document check" is performed on `contents()` (hence, the parsed article contents), and so the number of empty documents is now accurate (there are six based on the current parsing logic).
From these changes and those below, it seems that `Ax` is very sensitive to tiny collection differences.

### April 7, 2020

+ commit [`9a28a0`](https://github.com/castorini/anserini/commit/9a28a098dfd85366be29a6feb385c9e2493f988c) (04/07/2020)

Regression results for Core17 (New York Times) changed as the result of a bug fix.
Previously, Core17 used the `NewYorkTimesCollection` and was indexed with `JsoupGenerator` as the generator, which assumes that the input is HTML (or XML) and removes tags.
However, this was unnecessary, because the collection implementation already removes tags internally.
As a result, angle brackets in the text were interpreted as tags and removed.
Fixing this bug increased the number of terms in the collection (and a document that was previously empty is no longer empty).
However, effectiveness of `bm25+ax` and `ql+ax` decreased slightly; `bm25`/`bm25+rm3` and `ql`/`ql+rm3` remain unchanged.

### March 6, 2020

+ commit [`10ff01`](https://github.com/castorini/anserini/commit/10ff01a429bbfca196c8a012f1577b09ea476d8a) (03/06/2020)

Added regressions for background linking task from the TREC 2018 and 2019 News Tracks.

### Febrary 25, 2020

+ commit [`a62004`](https://github.com/castorini/anserini/commit/a62004b9179f6fa8a8e50e30008d3f8bc8f9d234) (02/25/2020)
+ commit [`0d42d3`](https://github.com/castorini/anserini/commit/0d42d309b7d0b2b4827fadd6d8bde39fa6bcd4cd) (02/25/2020)

Added regressions for the TREC 2019 Deep Learning Track, both document and passage ranking task.

### November 27, 2019

+ commit [`411618`](https://github.com/castorini/anserini/commit/4116188e327b92469da8c53e35f9c88fd28b88b6) (11/27/2019)
+ commit [`b9264d`](https://github.com/castorini/anserini/commit/b9264da049c355f8cc8d304f0d799b4435011f43) (11/27/2019)

Added regressions for TREC 2002 (Arabic), CLEF 2006 (French), and FIRE 2012 (English, Bengali and Hindi).

### October 11, 2019

+ commit [`445bb45`](https://github.com/castorini/anserini/commit/445bb458da825c9919d7a4e92de5ce87c929af7d) (10/11/2019)

Add regressions for NTCIR-8 ACLIA (IR4QA subtask, Monolingual Chinese).

### September 5, 2019

+ commit [`e88b931`](https://github.com/castorini/anserini/commit/e88b931d9fdb0a2b285ed5ef666889ce0965e5e0) (9/5/2019)

As it turns out, we were incorrect in entry below (commit [`2f1b665`](https://github.com/castorini/anserini/commit/2f1b66586073f1fc4e8913d1119fbbf478745013)). Regressions numbers after BM25prf fix _did_ change slightly.

### August 14, 2019

+ commit [`2f1b665`](https://github.com/castorini/anserini/commit/2f1b66586073f1fc4e8913d1119fbbf478745013) (8/14/2019)

Resolves inconsistent tie-breaking for BM25prf that leads to non-deterministic results, per [#774](https://github.com/castorini/anserini/issues/774). Note that regression numbers did not change.

### August 9, 2019

+ commit [`1217d47`](https://github.com/castorini/anserini/commit/1217d475c88cc4782ff3056506afc43d71bf31fb) (8/9/2019)
+ commit [`75dfaa6`](https://github.com/castorini/anserini/commit/75dfaa6989ed36f76422d7be0d9d424d85705ee3) (8/9/2019)

Added new doc2query regression `car17v2.0-doc2query` to reproduce [Nogueira et al. (arXiv 2019)](https://arxiv.org/abs/1904.08375) on the TREC 2017 Complex Answer Retrieval (CAR) section-level passage retrieval task (v2.0).
Added +Ax and +PRF regressions with both tuned and default BM25 parameters for MS MARCO passage ranking task.

### August 5, 2019

+ commit [`80c5447`](https://github.com/castorini/anserini/commit/80c54479d16fea901e474d31b906344300443c02) (8/5/2019)

Added +Ax and +PRF regressions with both tuned and default BM25 parameters for MS MARCO document ranking task.

### June 20, 2019

+ commit [`86be3d2`](https://github.com/castorini/anserini/commit/86be3d21ea8bdf9309ca5f85362c2782c3898a19) (6/20/2019)
+ commit [`b656da3`](https://github.com/castorini/anserini/commit/b656da3ed0ec3fa385dfdb9df0d153cd9a78bd7d) (6/20/2019)

Added new doc2query regression `msmarco-passage-doc2query` to reproduce [Nogueira et al. (arXiv 2019)](https://arxiv.org/abs/1904.08375) on the MS MARCO passage ranking task.
Added tuned BM25 parameters to `msmarco-doc` regression.
Associated documentation updated.

### June 12, 2019

+ commit [`75e36f9`](https://github.com/castorini/anserini/commit/75e36f97f7037d1ceb20fa9c91582eac5e974131) (6/9/2019)

Upgrade to Lucene 8: minor changes to all regression experiments.
[JDIQ 2018](experiments-jdiq2018.md) experiments are no longer maintained.

### June 9. 2019

+ commit [`93f8f3c`](https://github.com/castorini/anserini/commit/93f8f3c769bc9f95b2c351dc8347f0bbe940d1b2) (6/9/2019)
+ commit [`781d9ed`](https://github.com/castorini/anserini/commit/781d9ed111b41883c2342ff4d367ca77cfd9ebb2) (6/8/2019)

Added regressions for MS MARCO passage and document ranking tasks.

### June 3, 2019

+ commit [`3545350`](https://github.com/castorini/anserini/commit/3545350f3a12bd55a616e41057aa51d3b5cfaed2) (6/3/2019)
+ commit [`a3ccdef`](https://github.com/castorini/anserini/commit/a3ccdef9486816b756c6e34052f43e6db20b6afb) (6/3/2019)

Fixed bug in topic reader for CAR. Better parsing of New York Times documents. Regression numbers in both cases improved slightly.

### May 31, 2019

+ commit [`27493ed`](https://github.com/castorini/anserini/commit/27493ed999b230db6153e98a4113b5a44ff362d9) (5/31/2019)

Per [#658](https://github.com/castorini/anserini/issues/658): fixed broken regression in Core18 introduced by commit [`c4ab6b`](https://github.com/castorini/anserini/commit/c4ab6bfbe38648337ad824503e8945f5111ac673) (4/18/2019).

### May 11, 2019

+ commit [`3eef2fb`](https://github.com/castorini/anserini/commit/3eef2fb4808d88608d2c467418b77516eba3538a) (5/11/2019)
+ commit [`2ba2b95`](https://github.com/castorini/anserini/commit/2ba2b9582ee942aee714301b78015a2ded16da8c) (5/11/2019)
+ commit [`d911bba`](https://github.com/castorini/anserini/commit/d911bba180aceabbe052624b7f715a2189e25dbb) (5/10/2019)

CAR regression refactoring: added v2.0 regression and renamed existing regression to v1.5. Both use `benchmarkY1-test` to support consistent comparisons.

### January 2, 2019

+ commit [`407f308`](https://github.com/castorini/Anserini/commit/407f308cc543286e39701caf0acd1afab39dde2c) (1/2/2019)

Added fine tuning results (i.e., SIGIR Forum article experiments) for axiomatic semantic term matching.

### December 24, 2018

+ commit [`1aa3970`](https://github.com/castorini/Anserini/commit/1aa3970bd32b456025ada608389f7e4896eff19e) (12/24/2018)

Changed RM3 defaults to match settings in Indri.

### December 20, 2018

+ commit [`e71df7a`](https://github.com/castorini/Anserini/commit/26fbb3936cb2db1d69f02ad990d83e773e7d87c2) (12/20/2018)

Added Axiomatic F2Exp and F2Log ranking models back into Anserini (previously, we were using the default Lucene implementation as part of version 7.6 upgrade).

### December 18, 2018

+ commit [`e71df7a`](https://github.com/castorini/Anserini/commit/e71df7aee42c7776a63b9845600a4075632fa11c) (12/18/2018)

Upgrade to Lucene 7.6.

### November 30, 2018

+ commit [`e5b87f0`](https://github.com/castorini/Anserini/commit/e5b87f0d6c16b47d0be6cc8fd587acd20e3fbb0d) (11/30/2018)

Added default regressions for TREC 2018 Common Core Track.

### November 16, 2018

+ commit [`2c8cd7a`](https://github.com/castorini/Anserini/commit/2c8cd7a550faca0fc450e4159a4a874d4795ac25) (11/16/2018)

This is the commit id references in the [SIGIR Forum 2018 article](http://sigir.org/wp-content/uploads/2019/01/p040.pdf).
Note that commit [`18c3211`](https://github.com/castorini/Anserini/commit/18c3211117f35f72cbc1019c125ff885f51056ea) (12/9/2018) contains minor fixes to the code.

### October 22, 2018

+ commit [`10255e0`](https://github.com/castorini/Anserini/commit/10255e0f15c8caca94f8d5376a2c7c9ad1f5b5fd) (10/22/2018)

Fixed incorrect implementation of `-rm3.fbTerms`.

### September 26, 2018

+ commit [`7c882d3`](https://github.com/castorini/Anserini/commit/7c882d310564e27351ed51e0c8a669a13f33b48a) (9/26/2018)

Fixed bug as part of [#429](https://github.com/castorini/Anserini/issues/429): `cw12` and `mb13` regression tests changed slightly in effectiveness.

### August 8, 2018

+ commit [`d4b3272`](https://github.com/castorini/Anserini/commit/d4b3272e7f07fa274e5d8ffd50976beb0c08de52) (8/8/2018)

Added regressions tests for CAR17.

### August 5, 2018

+ commit [`c0da510`](https://github.com/castorini/Anserini/commit/c0da5105429a15fb85158d1740e0516305cd9de6) (8/5/2018)

This commit adds the effectiveness verification testing for the [JDIQ2018 Paper](experiments-jdiq2018.md).

### July 22, 2018

+ commit [`3a7beee`](https://github.com/castorini/Anserini/commit/3a7beee3485526f3146e69f57899a3033e20f504) (7/22/2018)
+ commit [`ec5fd3d`](https://github.com/castorini/Anserini/commit/ec5fd3d7fbee3308cd63321b77231d8b10e495a8) (7/22/2018)
+ commit [`5f8c26d3`](https://github.com/castorini/Anserini/commit/5f8c26d328dd67e6cc538d5f9b4af44acdbc74e5) (7/22/2018)

These three commits establish the new regression testing infrastructure with the following tests:

+ Experiments on Disks 1 &amp; 2: {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
+ Experiments on Disks 4 &amp; 5 (Robust04): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
+ Experiments on AQUAINT (Robust05): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
+ Experiments on New York Times (Core17): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
+ Experiments on Wt10g: {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
+ Experiments on Gov2: {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
+ Experiments on ClueWeb09 (Category B): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30, NDCG@20, ERR@20}
+ Experiments on ClueWeb12-B13: {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30, NDCG@20, ERR@20}
+ Experiments on ClueWeb12: {BM25, QL} &#10799; {RM3} &#10799; {AP, P30, NDCG@20, ERR@20}
+ Experiments on Tweets2011 (MB11 &amp; MB12): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
+ Experiments on Tweets2013 (MB13 &amp; MB14): {BM25, QL} &#10799; {RM3, Ax} &#10799; {AP, P30}
