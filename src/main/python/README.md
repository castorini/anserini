### Requirements

Python>=3.5
`pip install pyyaml`

### Before Run

Set Anserini root in the yaml file as `default->root`

### Run

```
python src/main/python/run_regression.py --config=src/main/resources/regression/all.yaml --collection disk12 --index
```
