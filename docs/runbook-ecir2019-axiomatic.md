# Anserini: ECIR 2019 Axiomatic Semantic Term Matching

This page documents code for replicating results from the following paper:

+ Peilin Yang and Jimmy Lin. Reproducing and Generalizing Semantic Term Matching in Axiomatic Information Retrieval. Proceedings of the 41th European Conference on Information Retrieval (ECIR 2019), April 2019, Cologne, Germany.

**Requirements**: Python>=2.6 or Python>=3.5 `pip install -r src/main/python/requirements.txt`

## Parameter Sensitivity Plots

These are plots in Figures 1, 2, and 3 of the paper.

First, change the index path at `src/main/resources/ecir2019_axiomatic/collections.yaml`
The script will go through the `index_roots` and concatenate with the collection's `index_path` and take the first match as the index path.

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
