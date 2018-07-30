### Oracle Effectiveness Scripts

The scripts calculate the optimal performances of all supported ranking models (by grid-searching all possible model parameters).
The numbers are in our JDIQ paper.

_NOTICE: The query topics used in JDIQ paper are combined topics per collection while the numbers generated
by the scripts here are separated_

### Run
On tuna:
```
nohup python src/main/python/oracle_effectiveness/run_batch.py --collection disk12 >& disk12.oracle.log &
nohup python src/main/python/oracle_effectiveness/run_batch.py --collection robust04 >& robust04.oracle.log &
nohup python src/main/python/oracle_effectiveness/run_batch.py --collection robust05 >& robust05.oracle.log &
nohup python src/main/python/oracle_effectiveness/run_batch.py --collection wt10g >& wt10g.oracle.log &
nohup python src/main/python/oracle_effectiveness/run_batch.py --collection gov2 >& gov2.oracle.log &
nohup python src/main/python/oracle_effectiveness/run_batch.py --collection cw09b --metrics map ndcg20 err20 >& cw09b.oracle.log &
nohup python src/main/python/oracle_effectiveness/run_batch.py --collection cw12b13 --metrics map ndcg20 err20 >& cw12b13.oracle.log &
```

Results are automatically generated at [Here](/docs/oracle-effectiveness.md)
