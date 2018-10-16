### Requirements

Python>=2.6 or Python>=3.5
`pip install -r src/main/python/requirements.txt`

### Run

##### BM25+Ax Robust04 (runs + eval + print results)
```
python src/main/python/fine_tunings/run_batch.py --collection robust04 --model axiom --n 44
```

##### BM25+RM3 Robust04 (runs + eval + print results)
```
python src/main/python/fine_tunings/run_batch.py --collection robust04 --model rm3 --n 44
```

##### QL+Ax Robust04 (runs + eval + print results)
```
python src/main/python/fine_tunings/run_batch.py --collection robust04 --basemodel ql --model axiom --n 44
```

##### QL+RM3 Robust04 (runs + eval + print results)
```
python src/main/python/fine_tunings/run_batch.py --collection robust04 --basemodel ql --model rm3 --n 44
```

##### Change `robust04` to `cw0b9` for all ClueWeb09b results
