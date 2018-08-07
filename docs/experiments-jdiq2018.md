### JDIQ2018 Effectiveness Scripts

The scripts calculate the optimal performances of all supported ranking models (by grid-searching all possible model parameters).
The main purpose is to reproduce what we have in our JDIQ2018 paper:


_NOTICE: The query topics used in JDIQ paper are combined topics per collection while the numbers generated
by the scripts here are separated_

### Run
On tuna:
```
nohup python src/main/python/jdiq2018_effectiveness/run_batch.py --collection disk12 >& disk12.jdiq2018.log &
nohup python src/main/python/jdiq2018_effectiveness/run_batch.py --collection robust04 >& robust04.jdiq2018.log &
nohup python src/main/python/jdiq2018_effectiveness/run_batch.py --collection robust05 >& robust05.jdiq2018.log &
nohup python src/main/python/jdiq2018_effectiveness/run_batch.py --collection wt10g >& wt10g.jdiq2018.log &
nohup python src/main/python/jdiq2018_effectiveness/run_batch.py --collection gov2 >& gov2.jdiq2018.log &
nohup python src/main/python/jdiq2018_effectiveness/run_batch.py --collection cw09b --metrics map ndcg20 err20 >& cw09b.jdiq2018.log &
nohup python src/main/python/jdiq2018_effectiveness/run_batch.py --collection cw12b13 --metrics map ndcg20 err20 >& cw12b13.jdiq2018.log &
```

### Results

#### disk12
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.151-200.txt                      | 0.2605    | 0.2474    | 0.2524    | 0.2544    | 0.2531    | 0.2448    |
topics.51-100.txt                       | 0.2262    | 0.2216    | 0.2213    | 0.2210    | 0.2230    | 0.2189    |
topics.101-150.txt                      | 0.2062    | 0.1997    | 0.1952    | 0.2017    | 0.1992    | 0.1819    |


#### robust04
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.robust04.301-450.601-700.txt     | 0.2532    | 0.2491    | 0.2521    | 0.2496    | 0.2500    | 0.2502    |


#### robust05
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.robust05.txt                     | 0.2090    | 0.1960    | 0.2006    | 0.2026    | 0.1976    | 0.1969    |


#### core17
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.core17.txt                       | 0.2047    | 0.1986    | 0.2005    | 0.1951    | 0.2041    | 0.1981    |


#### wt10g
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.451-550.txt                      | 0.2012    | 0.1972    | 0.1889    | 0.2034    | 0.1923    | 0.1726    |


#### gov2
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.701-750.txt                      | 0.2684    | 0.2535    | 0.2696    | 0.2636    | 0.2627    | 0.2687    |
topics.751-800.txt                      | 0.3392    | 0.3156    | 0.3428    | 0.3267    | 0.3298    | 0.3386    |
topics.801-850.txt                      | 0.3080    | 0.2845    | 0.3084    | 0.2957    | 0.2970    | 0.3140    |


#### cw09b
ERR20                                   | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.web.151-200.txt                  | 0.1472    | 0.1293    | 0.1431    | 0.1489    | 0.1431    | 0.1435    |
topics.web.101-150.txt                  | 0.1023    | 0.0926    | 0.0910    | 0.0861    | 0.0938    | 0.0908    |
topics.web.51-100.txt                   | 0.0764    | 0.0751    | 0.0635    | 0.0646    | 0.0723    | 0.0665    |


NDCG20                                  | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.web.151-200.txt                  | 0.1038    | 0.0893    | 0.0928    | 0.0997    | 0.0959    | 0.0931    |
topics.web.101-150.txt                  | 0.1937    | 0.1842    | 0.1774    | 0.1687    | 0.1911    | 0.1762    |
topics.web.51-100.txt                   | 0.1459    | 0.1390    | 0.1213    | 0.1170    | 0.1350    | 0.1232    |


MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.web.151-200.txt                  | 0.1202    | 0.1042    | 0.1135    | 0.1091    | 0.1046    | 0.1131    |
topics.web.101-150.txt                  | 0.1117    | 0.1067    | 0.1075    | 0.1002    | 0.1108    | 0.1066    |
topics.web.51-100.txt                   | 0.1147    | 0.1067    | 0.1085    | 0.1040    | 0.1070    | 0.1077    |


#### cw12b13
ERR20                                   | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.web.251-300.txt                  | 0.1271    | 0.1199    | 0.1075    | 0.1088    | 0.1234    | 0.1090    |
topics.web.201-250.txt                  | 0.0959    | 0.0811    | 0.0907    | 0.0883    | 0.0836    | 0.0905    |


NDCG20                                  | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.web.251-300.txt                  | 0.1237    | 0.1135    | 0.1177    | 0.1188    | 0.1174    | 0.1179    |
topics.web.201-250.txt                  | 0.1386    | 0.1225    | 0.1239    | 0.1168    | 0.1244    | 0.1253    |


MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.web.251-300.txt                  | 0.0238    | 0.0201    | 0.0239    | 0.0241    | 0.0212    | 0.0238    |
topics.web.201-250.txt                  | 0.0475    | 0.0434    | 0.0416    | 0.0392    | 0.0446    | 0.0412    |


#### mb11
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.microblog2012.txt                | 0.2083    | 0.2098    | 0.2032    | 0.2120    | 0.2018    | 0.2050    |
topics.microblog2011.txt                | 0.3683    | 0.3770    | 0.3572    | 0.3635    | 0.3823    | 0.3601    |


#### mb13
MAP                                     | BM25      | F2EXP     | PL2       | QL        | F2LOG     | SPL       |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
topics.microblog2013.txt                | 0.2599    | 0.2541    | 0.2519    | 0.2613    | 0.2622    | 0.2536    |
topics.microblog2014.txt                | 0.4203    | 0.3844    | 0.4115    | 0.4201    | 0.4104    | 0.4132    |


