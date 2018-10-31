### Requirements

Python>=2.6 or Python>=3.5
`pip install -r src/main/python/requirements.txt`

### Run

##### BM25 Robust04 (runs + eval + print results)
```
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25 --n 44 --run
```

##### QL Robust04 (runs + eval + print results)
```
python src/main/python/fine_tuning/run_batch.py --collection robust04 --basemodel ql --model ql --n 44 --run
```

##### BM25+Ax Robust04 (runs + eval + print results)
```
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25+axiom --n 44 --run
```

##### BM25+RM3 Robust04 (runs + eval + print results)
```
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25+rm3 --n 44 --run
```

##### Change `robust04` to `cw0b9` for all ClueWeb09b results
