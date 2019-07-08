# Anserini: ECIR 2019 Axiomatic Semantic Term Matching

This page documents code for replicating results from the following paper:

+ Peilin Yang and Jimmy Lin. [Reproducing and Generalizing Semantic Term Matching in Axiomatic Information Retrieval](https://cs.uwaterloo.ca/~jimmylin/publications/Yang_Lin_ECIR2019.pdf). _Proceedings of the 41th European Conference on Information Retrieval, Part I (ECIR 2019)_, pages 369-381, April 2019, Cologne, Germany.

**Requirements**: With Python>=2.6 or Python>=3.5:

```
pip install -r src/main/python/requirements.txt
```

## Parameter Sensitivity Plots

These are plots in Figures 1, 2, and 3 of the paper.

First, change the index path at `src/main/resources/ecir2019_axiomatic/collections.yaml`
Our script will go through the `index_roots` property and concatenate with the collection's `index_path`, taking the first match as the location of the index.

Run the following commands:

```
python src/main/python/ecir2019_axiomatic/run_batch.py --collection disk12 --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection robust04 --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection robust05 --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection core17 --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection wt10g --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection gov2 --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection cw09b --models bm25 ql f2exp --n 32 --metrics ndcg20 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection cw12b13 --models bm25 ql f2exp --n 32 --metrics ndcg20 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection mb11 --models bm25 ql f2exp --n 32 --run --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection mb13 --models bm25 ql f2exp --n 32 --run --plot
```

## Qrels Coverage

These are plots in Figure 4 of the paper.

```
python src/main/python/ecir2019_axiomatic/run_batch.py --collection disk12 --models bm25 --cal_coverage --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection robust04 --models bm25 --cal_coverage --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection robust05 --models bm25 --cal_coverage --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection core17 --models bm25 --cal_coverage --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection wt10g --models bm25 --cal_coverage --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection gov2 --models bm25 --cal_coverage --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection cw09b --models bm25 --cal_coverage --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection cw12b13 --models bm25 --cal_coverage --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection mb11 --models bm25 --cal_coverage --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection mb13 --models bm25 --cal_coverage --plot
```

## Per-Topic Analysis

These are plots in Figure 5 of the paper.

Assuming you've already run the above "Parameter Sensitivity Plots" successfully, the following commands will generate the relevant data:

```
mkdir -p ecir2019_axiomatic/disk12/per_topic_analysis && awk 'FNR==NR{a[FNR]=$3; next} {print $2, a[FNR], $3}' ecir2019_axiomatic/disk12/eval_files/map/bm25_axiom_k1\:0.9\,b\:0.4\,axiom.r\:20\,axiom.n\:30\,axiom.beta\:0.50\,axiom.top\:20 ecir2019_axiomatic/disk12/eval_files/map/bm25_baseline_bm25 | awk '{print $1 "," ($2 - $3)}' | sort -rn -k2 | grep -v all > ecir2019_axiomatic/disk12/per_topic_analysis/beta_0.5.csv
python src/main/python/ecir2019_axiomatic/run_batch.py --collection disk12 --per_topic_analysis
mkdir -p ecir2019_axiomatic/wt10g/per_topic_analysis && awk 'FNR==NR{a[FNR]=$3; next} {print $2, a[FNR], $3}' ecir2019_axiomatic/wt10g/eval_files/map/bm25_axiom_k1\:0.9\,b\:0.4\,axiom.r\:20\,axiom.n\:30\,axiom.beta\:0.10\,axiom.top\:20 ecir2019_axiomatic/wt10g/eval_files/map/bm25_baseline_bm25 | awk '{print $1 "," ($2 - $3)}' | sort -rn -k2 | grep -v all > ecir2019_axiomatic/wt10g/per_topic_analysis/beta_0.1.csv
python src/main/python/ecir2019_axiomatic/run_batch.py --collection wt10g --per_topic_analysis
mkdir -p ecir2019_axiomatic/mb13/per_topic_analysis && awk 'FNR==NR{a[FNR]=$3; next} {print $2, a[FNR], $3}' ecir2019_axiomatic/mb13/eval_files/map/bm25_axiom_k1\:0.9\,b\:0.4\,axiom.r\:20\,axiom.n\:30\,axiom.beta\:1.00\,axiom.top\:20 ecir2019_axiomatic/mb13/eval_files/map/bm25_baseline_bm25 | awk '{print $1 "," ($2 - $3)}' | sort -rn -k2 | grep -v all > ecir2019_axiomatic/mb13/per_topic_analysis/beta_1.0.csv
python src/main/python/ecir2019_axiomatic/run_batch.py --collection mb13 --per_topic_analysis
```

## Runs with Random Seeds

These are plots in Figure 6 of the paper.

```
python src/main/python/ecir2019_axiomatic/run_batch.py --collection disk12 --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection robust04 --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection robust05 --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection core17 --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection wt10g --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection gov2 --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection cw09b --models bm25 --n 32 --metrics ndcg20 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection cw12b13 --models bm25 --n 32 --metrics ndcg20 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection mb11 --models bm25 --n 32 --run --random --plot
python src/main/python/ecir2019_axiomatic/run_batch.py --collection mb13 --models bm25 --n 32 --run --random --plot
```

## F2EXP Results

This is Table 1 in the paper.

Assuming you've already run the above "Parameter Sensitivity Plots" successfully, results will be in `ecir2019_axiomatic/{collection}/effectiveness_files/axiom_paras_sensitivity_map.csv`.
Alternatively, run the following:

```
awk -F',' '/f2exp,-1/' ecir2019_axiomatic/robust0*/effectiveness_files/axiom_paras_sensitivity_map.csv
awk -F',' '$3>max[$1]{max[$1]=$3; row[$1]=$0} END{for (i in row) print row[i]}' ecir2019_axiomatic/robust0*/effectiveness_files/axiom_paras_sensitivity_map.csv | grep f2exp
```
