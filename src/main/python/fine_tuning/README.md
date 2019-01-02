### Requirements

Python>=2.6 or Python>=3.5
`pip install -r src/main/python/requirements.txt`

### Run

*** Users will need to change the index path at `src/main/resources/fine_tuning/collections.yaml`
(the program will go through the `index_roots` and concatenate with collection's `index_path`. the first match will be the index path)

##### BM25 Robust04 (runs + eval + print results)
```
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25 --n 44 --run --use_drr_fold
```

##### QL Robust04 (runs + eval + print results)
```
python src/main/python/fine_tuning/run_batch.py --collection robust04 --basemodel ql --model ql --n 44 --run --use_drr_fold
```

##### BM25+RM3 Robust04 (runs + eval + print results)
```
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25+rm3 --n 44 --run --use_drr_fold
```

##### BM25+AxiomaticReranking Robust04 (runs + eval + print results)
```
python src/main/python/fine_tuning/run_batch.py --collection robust04 --model bm25+axiom --n 44 --run --use_drr_fold
```

##### Change `robust04` to `cw0b9` for all ClueWeb09b results
