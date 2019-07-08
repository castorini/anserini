# Anserini: ECIR 2019 Cross-Collection Relevance Feedback

This page documents code for replicating results from the following paper:

+ Ruifan Yu, Yuhao Xie and Jimmy Lin. [Simple Techniques for Cross-Collection Relevance Transfer.](https://cs.uwaterloo.ca/~jimmylin/publications/Yu_etal_ECIR2019.pdf) _Proceedings of the 41th European Conference on Information Retrieval, Part I (ECIR 2019)_, page 397-409, April 2019, Cologne, Germany.

**Requirements**: The main requirements are:

```
python >= 3.6
numpy  >= 1.15.4
scipy  >= 1.1.0
scikit-learn >= 0.20.1
lightgbm >= 2.2.1
```

We suggest using Conda to manage your Python environment.
For reference, this was the Conda environment for our experiments (after setting up the environment with `conda install -c conda-forge lightgbm`):

```
$ conda list
# packages in environment at /anaconda3/envs/python36:
#
# Name                    Version                   Build  Channel
blas                      1.0                         mkl  
bzip2                     1.0.6                         1    conda-forge
ca-certificates           2018.11.29           ha4d7672_0    conda-forge
certifi                   2018.11.29            py36_1000    conda-forge
clangdev                  4.0.0                 default_0    conda-forge
icu                       58.2                 hfc679d8_0    conda-forge
intel-openmp              2019.1                      144  
libcxx                    4.0.1                hcfea43d_1  
libcxxabi                 4.0.1                hcfea43d_1  
libedit                   3.1.20170329         hb402a30_2  
libffi                    3.2.1                h475c297_4  
libgfortran               3.0.1                h93005f0_2  
libiconv                  1.15                 h470a237_3    conda-forge
libxml2                   2.9.8                h422b904_5    conda-forge
lightgbm                  2.2.1            py36hfc679d8_0    conda-forge
llvmdev                   4.0.0                 default_0    conda-forge
mkl                       2019.1                      144  
mkl_fft                   1.0.10                   py36_0    conda-forge
mkl_random                1.0.2                    py36_0    conda-forge
ncurses                   6.1                  h0a44026_1  
numpy                     1.15.4           py36hacdab7b_0  
numpy-base                1.15.4           py36h6575580_0  
openmp                    4.0.0                         1    conda-forge
openssl                   1.0.2p               h470a237_1    conda-forge
pip                       18.1                     py36_0  
python                    3.6.6                h5001a0f_0    conda-forge
readline                  7.0                  h1de35cc_5  
scikit-learn              0.20.1           py36h27c97d8_0  
scipy                     1.1.0            py36h1410ff5_2  
setuptools                40.6.2                   py36_0  
sqlite                    3.25.3               ha441bb4_0  
tk                        8.6.8                ha441bb4_0  
wheel                     0.32.3                   py36_0  
xz                        5.2.4                h1de35cc_4  
zlib                      1.2.11               h1de35cc_3  
```

## Preparation

Run the following commands to index the `Robust04`, `Robust05`, and `Core17` collections:

```bash
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
 -generator JsoupGenerator -threads 16 -input /path/to/robust04 \
 -index lucene-index.robust04.pos+docvectors+rawdocs \
 -storePositions -storeDocvectors -storeRawDocs >& log.robust04.pos+docvectors+rawdocs &

nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
 -generator JsoupGenerator -threads 16 -input /path/to/robust05 \
 -index lucene-index.robust05.pos+docvectors+rawdocs \
 -storePositions -storeDocvectors -storeRawDocs >& log.robust05.pos+docvectors+rawdocs &

nohup sh target/appassembler/bin/IndexCollection -collection NewYorkTimesCollection \
 -generator JsoupGenerator -threads 16 -input /path/to/core17 \
 -index lucene-index.core17.pos+docvectors+rawdocs \
 -storePositions -storeDocvectors -storeRawDocs >& log.core17.pos+docvectors+rawdocs &
```

Retrieve the top-ranked documents using BM25, BM25 with RM3 (BM25+RM3), and BM25 with axiomatic semantic term matching (BM25+AX) for the three collections:

```bash
nohup target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.robust04.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt \
 -output run.robust04.bm25.topics.robust04.301-450.601-700.txt -bm25 -hits 10000 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.robust04.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt \
 -output run.robust04.bm25+rm3.topics.robust04.301-450.601-700.txt -bm25 -rm3 -hits 10000 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.robust04.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt \
 -output run.robust04.bm25+ax.topics.robust04.301-450.601-700.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic  -hits 10000 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.robust05.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.robust05.txt \
 -output run.robust05.bm25.topics.robust05.txt -bm25 -hits 10000 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.robust05.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.robust05.txt \
 -output run.robust05.bm25+rm3.topics.robust05.txt -bm25 -rm3 -hits 10000 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.robust05.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.robust05.txt \
 -output run.robust05.bm25+ax.topics.robust05.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -hits 10000 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.core17.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.core17.txt \
 -output run.core17.bm25.topics.core17.txt -bm25 -hits 10000 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.core17.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.core17.txt \
 -output run.core17.bm25+rm3.topics.core17.txt -bm25 -rm3 -hits 10000 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.core17.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.core17.txt \
 -output run.core17.bm25+ax.topics.core17.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -hits 10000 &
```

Train classifiers and apply inference for relevance transfer:
Configuration files for different combinations of source and target collections are stored in `src/main/python/ecir2019_ccrf/configs/`.
For each configuration, run the following commands:

```bash
python src/main/python/ecir2019_ccrf/prepare_training_data.py --config $CONFIG_NAME
python src/main/python/ecir2019_ccrf/prepare_test_data.py --config $CONFIG_NAME
python src/main/python/ecir2019_ccrf/rerank.py --config $CONFIG_NAME
python src/main/python/ecir2019_ccrf/generate_runs.py --config $CONFIG_NAME
```

After successfully generating all experimental results, you should have the following folders in your current directory:

```
ccrf.0405_core17/
ccrf.0405_core17.ax/
ccrf.0405_core17.rm3/
ccrf.0417_robust05/
ccrf.0417_robust05.ax/
ccrf.0417_robust05.rm3/
ccrf.04_core17.ax/
ccrf.04_core17.rm3/
ccrf.04_robust05.ax/
ccrf.04_robust05.rm3/
ccrf.0517_robust04/
ccrf.0517_robust04.ax/
ccrf.0517_robust04.rm3/
ccrf.05_core17.ax/
ccrf.05_core17.rm3/
ccrf.05_robust04.ax/
ccrf.05_robust04.rm3/
ccrf.17_robust04.ax/
ccrf.17_robust04.rm3/
ccrf.17_robust05.ax/
ccrf.17_robust05.rm3/
```

## Results

### Baselines

These are commands to generate results in Table 1 of the paper:

```bash
python src/main/python/ecir2019_ccrf/filter_topics.py --input ccrf.0517_robust04/robust04_bm25.txt \
 --output robust04_bm25.cut.txt && \
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt \
 robust04_bm25.cut.txt -m map -m P.10 -M 1000

python src/main/python/ecir2019_ccrf/filter_topics.py --input ccrf.0517_robust04.rm3/robust04_bm25+rm3.txt \
 --output robust04_bm25+rm3.cut.txt && \
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt \
 robust04_bm25+rm3.cut.txt -m map -m P.10 -M 1000

python src/main/python/ecir2019_ccrf/filter_topics.py --input ccrf.0517_robust04.ax/robust04_bm25+ax.txt \
 --output robust04_bm25+ax.cut.txt && \
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt \
 robust04_bm25+ax.cut.txt -m map -m P.10 -M 1000

python src/main/python/ecir2019_ccrf/filter_topics.py --input ccrf.0417_robust05/robust05_bm25.txt \
 --output robust05_bm25.cut.txt && \
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt \
 robust05_bm25.cut.txt -m map -m P.10 -M 1000

python src/main/python/ecir2019_ccrf/filter_topics.py --input ccrf.0417_robust05.rm3/robust05_bm25+rm3.txt \
 --output robust05_bm25+rm3.cut.txt && \
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt \
 robust05_bm25+rm3.cut.txt -m map -m P.10 -M 1000

python src/main/python/ecir2019_ccrf/filter_topics.py --input ccrf.0417_robust05.ax/robust05_bm25+ax.txt \
 --output robust05_bm25+ax.cut.txt && \
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt \
 robust05_bm25+ax.cut.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt \
 ccrf.0405_core17/core17_bm25.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt \
 ccrf.0405_core17.rm3/core17_bm25+rm3.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt \
 ccrf.0405_core17.ax/core17_bm25+ax.txt -m map -m P.10 -M 1000
```

### Main Relevance Transfer Experiment

These are commands to generate results in Table 2 of the paper: training on Robust04 and Robust05, testing on Core17.

The first block of the table contains results of <tt>WCRobust0405</tt> and results copied from Table 1.

The second block of the table contains results from optimal alpha settings.
To determine the optimal settings, use the following commands:

```bash
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt \
 ccrf.0405_core17.rm3/core17.rm3_${clf}_${weight}.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt \
 ccrf.0405_core17.ax/core17.ax_${clf}_${weight}.txt -m map -m P.10 -M 1000
```

The options for `clf` are `lr`, `svm`, `lgb`, and `e3` (ensemble of the three classifiers), and `weight` is [0.0 ... 1.0] in tenth increments.

The third block of the table contains results with alpha = 0.6:

```bash
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt \
 ccrf.0405_core17.rm3/core17.rm3_${clf}_0.6.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt \
 ccrf.0405_core17.ax/core17.ax_${clf}_0.6.txt -m map -m P.10 -M 1000
```

The options for `clf` are `lr`, `svm`, `lgb`, and `e3` (same as above).

### Experiments with Different Source/Target Combinations

These are commands to generate results in Table 3 of the paper.

Relevance transfer to Core17:

```bash
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt \
 ccrf.0405_core17.rm3/core17_bm25+rm3.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt \
 ccrf.0405_core17.rm3/core17.rm3_lr_0.6.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt \
 ccrf.04_core17.rm3/core17.rm3_lr_0.6.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt \
 ccrf.05_core17.rm3/core17.rm3_lr_0.6.txt -m map -m P.10 -M 1000
```

Relevance transfer to Robust04:

```bash
python src/main/python/ecir2019_ccrf/filter_topics.py --input ccrf.0517_robust04.rm3/robust04_bm25+rm3.txt \
 --output robust04_bm25+rm3.cut.txt && \
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt \
 robust04_bm25+rm3.cut.txt -m map -m P.10 -M 1000

python src/main/python/ecir2019_ccrf/filter_topics.py --input ccrf.0517_robust04.rm3/robust04.rm3_lr_0.6.txt \
 --output robust04.rm3_lr_0.6.cut.txt &&  \
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt \
 robust04.rm3_lr_0.6.cut.txt -m map -m P.10 -M 1000

python src/main/python/ecir2019_ccrf/filter_topics.py --input ccrf.05_robust04.rm3/robust04.rm3_lr_0.6.txt \
 --output robust04.rm3_lr_0.6.cut.txt && \
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt \
 robust04.rm3_lr_0.6.cut.txt -m map -m P.10 -M 1000

python src/main/python/ecir2019_ccrf/filter_topics.py --input ccrf.17_robust04.rm3/robust04.rm3_lr_0.6.txt \
 --output robust04.rm3_lr_0.6.cut.txt && \
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt \
 robust04.rm3_lr_0.6.cut.txt -m map -m P.10 -M 1000
```

Relevance transfer to Robust05:

```bash
python src/main/python/ecir2019_ccrf/filter_topics.py --input ccrf.0417_robust05.rm3/robust05_bm25+rm3.txt \
 --output robust05_bm25+rm3.cut.txt && \
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt \
 robust05_bm25+rm3.cut.txt -m map -m P.10 -M 1000

python src/main/python/ecir2019_ccrf/filter_topics.py --input ccrf.0417_robust05.rm3/robust05.rm3_lr_0.6.txt \
 --output robust05.rm3_lr_0.6.cut.txt && \
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt \
 robust05.rm3_lr_0.6.cut.txt -m map -m P.10 -M 1000

python src/main/python/ecir2019_ccrf/filter_topics.py --input ccrf.04_robust05.rm3/robust05.rm3_lr_0.6.txt \
 --output robust05.rm3_lr_0.6.cut.txt && \
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt \
 robust05.rm3_lr_0.6.cut.txt -m map -m P.10 -M 1000

python src/main/python/ecir2019_ccrf/filter_topics.py --input ccrf.17_robust05.rm3/robust05.rm3_lr_0.6.txt \
 --output robust05.rm3_lr_0.6.cut.txt && \
eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt \
 robust05.rm3_lr_0.6.cut.txt -m map -m P.10 -M 1000
```
