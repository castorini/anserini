# Anserini: JDIQ 2018 Experiments

This page documents the script used in the following article to compute optimal retrieval effectiveness by grid search over model parameters:

+ Peilin Yang, Hui Fang, and Jimmy Lin. [Anserini: Reproducible Ranking Baselines Using Lucene.](https://dl.acm.org/citation.cfm?doid=3289400.3239571) Journal of Data and Information Quality, 10(4), Article 16, 2018.

**Important note**: We clearly state in the article:

> For all systems, we report results from parameter tuning to optimize average precision (AP) at rank 1000 on the newswire collections, WT10g, and Gov2, and NDCG@20 for the ClueWeb collections.
> There was no separation of training and test data, so these results should be interpreted as oracle settings.

If you're going to refer to these effectiveness results, _please_ be aware of what you're comparing!

**Additional note**: The values produced by these scripts are _slightly_ different than those reported in the article.
The reason for these differences stems from the fact that Anserini evolved throughout the peer review process; the values reported in the article were those generated when the manuscript was submitted.
By the time the article was published, the implementation of Anserini has progressed.
As Anserini continues to improve we will update these scripts, which will lead to further divergences between the published values.
Unfortunately, this is an unavoidable aspect of empirical research on software artifacts.

**Update (12/18/2018)**:
Regression effectiveness values changed at commit [`e71df7a`](https://github.com/castorini/anserini/commit/e71df7aee42c7776a63b9845600a4075632fa11c) with upgrade to Lucene 7.6.

**Update (6/12/2019)**:
With commit [`75e36f9`](https://github.com/castorini/anserini/commit/75e36f97f7037d1ceb20fa9c91582eac5e974131), which upgrades Anserini to Lucene 8.0, we are no longer maintaining the replicability of these experiments.
That is, running these commands will produce results different from the numbers reported here.
The most recent version in which these results are replicable is the [v0.5.1](https://github.com/castorini/anserini/releases) release (6/11/2019).

## Parameter Tuning

Invoke the tuning script on various collections as follows, on `tuna`:

```
nohup python src/main/python/jdiq2018/run_regression.py --collection disk12 >& jdiq2018.disk12.log &
nohup python src/main/python/jdiq2018/run_regression.py --collection robust04 >& jdiq2018.robust04.log &
nohup python src/main/python/jdiq2018/run_regression.py --collection robust05 >& jdiq2018.robust05.log &
nohup python src/main/python/jdiq2018/run_regression.py --collection wt10g >& jdiq2018.wt10g.log &
nohup python src/main/python/jdiq2018/run_regression.py --collection gov2 >& jdiq2018.gov2.log &
nohup python src/main/python/jdiq2018/run_regression.py --collection cw09b --metrics map ndcg20 err20 >& jdiq2018.cw09b.log &
nohup python src/main/python/jdiq2018/run_regression.py --collection cw12b13 --metrics map ndcg20 err20 >& jdiq2018.cw12b13.log &
```

The script assumes hard-coded index directories; modify as appropriate.

## Effectiveness

#### disk12
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.151-200.txt                      | 0.2614    | 0.2512    | 0.2544    | 0.2558    | 0.2571    | 0.2459    |
topics.51-100.txt                       | 0.2274    | 0.2245    | 0.2226    | 0.2226    | 0.2260    | 0.2201    |
topics.101-150.txt                      | 0.2071    | 0.2035    | 0.1967    | 0.2015    | 0.2031    | 0.1840    |


#### robust04
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.robust04.301-450.601-700.txt     | 0.2543    | 0.2516    | 0.2531    | 0.2514    | 0.2523    | 0.2509    |


#### robust05
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.robust05.txt                     | 0.2097    | 0.1998    | 0.2021    | 0.2030    | 0.2023    | 0.1980    |


#### core17
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.core17.txt                       | 0.2052    | 0.2005    | 0.2019    | 0.1943    | 0.2050    | 0.1999    |


#### wt10g
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.451-550.txt                      | 0.2005    | 0.2002    | 0.1880    | 0.2021    | 0.1946    | 0.1704    |


#### gov2
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.701-750.txt                      | 0.2702    | 0.2592    | 0.2726    | 0.2700    | 0.2689    | 0.2734    |
topics.751-800.txt                      | 0.3394    | 0.3195    | 0.3439    | 0.3303    | 0.3342    | 0.3393    |
topics.801-850.txt                      | 0.3085    | 0.2900    | 0.3088    | 0.3013    | 0.3026    | 0.3139    |


#### cw09b
ERR20                                   | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.web.151-200.txt                  | 0.1524    | 0.1387    | 0.1439    | 0.1484    | 0.1524    | 0.1445    |
topics.web.101-150.txt                  | 0.0981    | 0.0935    | 0.0892    | 0.0868    | 0.0944    | 0.0893    |
topics.web.51-100.txt                   | 0.0774    | 0.0776    | 0.0635    | 0.0643    | 0.0725    | 0.0659    |


NDCG20                                  | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.web.151-200.txt                  | 0.1090    | 0.0933    | 0.0927    | 0.0978    | 0.0986    | 0.0933    |
topics.web.101-150.txt                  | 0.1927    | 0.1878    | 0.1765    | 0.1701    | 0.1917    | 0.1758    |
topics.web.51-100.txt                   | 0.1487    | 0.1418    | 0.1217    | 0.1185    | 0.1376    | 0.1252    |


MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.web.151-200.txt                  | 0.1226    | 0.1089    | 0.1170    | 0.1113    | 0.1091    | 0.1163    |
topics.web.101-150.txt                  | 0.1104    | 0.1081    | 0.1067    | 0.1004    | 0.1104    | 0.1063    |
topics.web.51-100.txt                   | 0.1165    | 0.1111    | 0.1103    | 0.1060    | 0.1110    | 0.1099    |


#### cw12b13
ERR20                                   | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.web.251-300.txt                  | 0.1224    | 0.1203    | 0.1109    | 0.1108    | 0.1209    | 0.1135    |
topics.web.201-250.txt                  | 0.0993    | 0.0797    | 0.0933    | 0.0898    | 0.0821    | 0.0940    |


NDCG20                                  | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.web.251-300.txt                  | 0.1247    | 0.1159    | 0.1213    | 0.1209    | 0.1189    | 0.1213    |
topics.web.201-250.txt                  | 0.1384    | 0.1222    | 0.1247    | 0.1168    | 0.1247    | 0.1258    |


MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.web.251-300.txt                  | 0.0237    | 0.0205    | 0.0242    | 0.0246    | 0.0213    | 0.0240    |
topics.web.201-250.txt                  | 0.0481    | 0.0450    | 0.0419    | 0.0398    | 0.0454    | 0.0418    |


#### mb11
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.microblog2012.txt                | 0.2083    | 0.2107    | 0.2046    | 0.2121    | 0.2033    | 0.2055    |
topics.microblog2011.txt                | 0.3643    | 0.3769    | 0.3537    | 0.3607    | 0.3823    | 0.3567    |


#### mb13
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.microblog2013.txt                | 0.2600    | 0.2531    | 0.2524    | 0.2615    | 0.2622    | 0.2530    |
topics.microblog2014.txt                | 0.4195    | 0.3854    | 0.4132    | 0.4200    | 0.4121    | 0.4147    |


