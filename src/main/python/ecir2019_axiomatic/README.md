### Requirements

Python>=2.6 or Python>=3.5
`pip install -r src/main/python/requirements.txt`

### Run the Parameter Sensitivity (Fig 1,2,3 in the paper)

*** Users will need to change the index path at `src/main/resources/fine_tuning/collections.yaml`
(the program will go through the `index_roots` and concatenate with collection's `index_path`. the first match will be the index path)


```
python src/main/python/ecir2019_axiomatic/run_batch.py --collection disk12 --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection robust04 --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection robust05 --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection core17 --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection wt10g --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection gov2 --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection cw09b --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection cw12b13 --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection mb11 --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection mb13 --models bm25 ql f2exp --n 32 --run --plot
```


### Run with random seeds (Fig 6 in the paper)

```
python src/main/python/ecir2019_axiomatic/run_batch.py --collection disk12 --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection robust04 --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection robust05 --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection core17 --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection wt10g --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection gov2 --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection cw09b --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection cw12b13 --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection mb11 --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection mb13 --models bm25 --n 32 --run --random --plot
```


### Reproduce F2EXP results in Table 1

One will need to manually check the being generated results: `ecir2019_axiomatic/{collection}/effectiveness_` or run:
``
