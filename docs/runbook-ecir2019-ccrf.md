# ECIR 2019 Cross-Collection Relevance Transfer

This page documents code for replicating results from the following paper:

- Ruifan Yu, Yuhao Xie and Jimmy Lin. Simple Techniques for Cross-Collection Relevance Transfer. Proceedings of the 41th European Conference on Information Retrieval (ECIR 2019), April 2019, Cologne, Germany.

**Requirements**: The main requirements are

```
python >= 3.6
numpy  >= 1.15.4
scipy  >= 1.1.0
scikit-learn >= 0.20.1
lightgbm >= 2.2.1
```

We suggest use conda environment, and for reference, this was the conda environment (setting up the environment with `conda install -c conda-forge lightgbm`):

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

### Indexing

Run the following commands to index `Robust04`, `Robust05`, and `Core17` collection.

```bash
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
-generator JsoupGenerator -threads 16 -input /path/to/robust04 -index \
lucene-index.robust04.pos+docvectors+rawdocs -storePositions -storeDocvectors \
-storeRawDocs >& log.robust04.pos+docvectors+rawdocs &

nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
-generator JsoupGenerator -threads 16 -input /path/to/robust05 -index \
lucene-index.robust05.pos+docvectors+rawdocs -storePositions -storeDocvectors \
-storeRawDocs >& log.robust05.pos+docvectors+rawdocs &

nohup sh target/appassembler/bin/IndexCollection -collection \
NewYorkTimesCollection -generator JsoupGenerator -threads 16 -input \
/path/to/core17 -index lucene-index.core17.pos+docvectors+rawdocs \
-storePositions -storeDocvectors -storeRawDocs >& \
log.core17.pos+docvectors+rawdocs &
```

### Retrieval

Retrieve the top-relevant documents using `BM25`only, BM25 with RM3 reranking (`BM25+RM3`) and BM25 with axiomatic reranking (`BM25+AX`) for the three collections.

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

### Rerank

Run the following commands to generate one combination of relevance transfer experiment.

```bash
python src/main/python/ccrf/prepare_training_data.py --config $CONFIG_NAME
python src/main/python/ccrf/prepare_test_data.py --config $CONFIG_NAME
python src/main/python/ccrf/rerank.py --config $CONFIG_NAME
python src/main/python/ccrf/generate_runs.py --config $CONFIG_NAME
```

where the configs are in `src/main/python/ccrf/configs`. Use the config file name as the argument in the above commands.

After successfully generating all experiment results, you should have the following following folders in your current directory:

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

## Baseline Retrieval Results

These are the results in Table 1 of the paper.

```bash
head -n 440668 ccrf.0517_robust04/robust04_bm25.txt > robust04_bm25.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt robust04_bm25.cut.txt -m map -m P.10 -M 1000

head -n 500000 ccrf.0517_robust04.rm3/robust04_bm25+rm3.txt > robust04_bm25+rm3.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt robust04_bm25+rm3.cut.txt -m map -m P.10 -M 1000

head -n 500000 ccrf.0517_robust04.ax/robust04_bm25+ax.txt > robust04_bm25+ax.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt robust04_bm25+ax.cut.txt -m map -m P.10 -M 1000

head -n 316234 ccrf.0417_robust05/robust05_bm25.txt > robust05_bm25.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt robust05_bm25.cut.txt -m map -m P.10 -M 1000

head -n 330000 ccrf.0417_robust05.rm3/robust05_bm25+rm3.txt > robust05_bm25+rm3.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt robust05_bm25+rm3.cut.txt -m map -m P.10 -M 1000

head -n 330000 ccrf.0417_robust05.ax/robust05_bm25+ax.txt > robust05_bm25+ax.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt robust05_bm25+ax.cut.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt ccrf.0405_core17/core17_bm25.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt ccrf.0405_core17.rm3/core17_bm25+rm3.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt ccrf.0405_core17.ax/core17_bm25+ax.txt -m map -m P.10 -M 1000
```

## Relevance Transfer Results

These are the results in Table 2 of the paper

- The first block is just a copy of <tt>WCRobust0405</tt> and the results from `Table 1`.
- The second block is to run the following commands and find the optimal interpolation weight $\alpha$ with the highest score.

```bash
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt ccrf.0405_core17.rm3/core17.rm3_${clf}_${weight}.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt ccrf.0405_core17.ax/core17.ax_${clf}_${weight}.txt -m map -m P.10 -M 1000
```

where the options for `clf` are `lr`, `svm`, `lgb`, and `e3` (represents the ensemble of the three classifiers), and `weight` are `0.0`, `0.1`, `0.2`, `0.3`, `0.4`, `0.5`, `0.6`, `0.7`, `0.8`, `0.9`,`1.0`. 

- The third block's results are from the following commands:

```bash
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt ccrf.0405_core17.rm3/core17.rm3_${clf}_0.6.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt ccrf.0405_core17.ax/core17.ax_${clf}_0.6.txt -m map -m P.10 -M 1000
```

where the options for `clf` are `lr`, `svm`, `lgb`, and `e3`.

## Results on different combinations of source and target collections

There are the results in Table 3 of the paper.

```bash
eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt ccrf.0405_core17.rm3/core17_bm25+rm3.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt ccrf.0405_core17.rm3/core17.rm3_lr_0.6.txt -m map -m P.10 -M 1000

eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt ccrf.04_core17.rm3/core17.rm3_lr_0.6.txt -m map -m P.10 -M 1000

head -n 330000 ccrf.05_core17.rm3/core17.rm3_lr_0.6.txt > core17.rm3_lr_0.6.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.core17.txt core17.rm3_lr_0.6.cut.txt -m map -m P.10 -M 1000

head -n 500000 ccrf.0517_robust04.rm3/robust04_bm25+rm3.txt > robust04_bm25+rm3.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt robust04_bm25+rm3.cut.txt -m map -m P.10 -M 1000

head -n 500000 ccrf.0517_robust04.rm3/robust04.rm3_lr_0.6.txt > robust04.rm3_lr_0.6.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt robust04.rm3_lr_0.6.cut.txt -m map -m P.10 -M 1000

head -n 330000 ccrf.05_robust04.rm3/robust04.rm3_lr_0.6.txt > robust04.rm3_lr_0.6.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt robust04.rm3_lr_0.6.cut.txt -m map -m P.10 -M 1000

head -n 500000 ccrf.17_robust04.rm3/robust04.rm3_lr_0.6.txt > robust04.rm3_lr_0.6.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt robust04.rm3_lr_0.6.cut.txt -m map -m P.10 -M 1000

head -n 330000 ccrf.0417_robust05.rm3/robust05_bm25+rm3.txt > robust05_bm25+rm3.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt robust05_bm25+rm3.cut.txt -m map -m P.10 -M 1000

head -n 330000 ccrf.0417_robust05.rm3/robust05.rm3_lr_0.6.txt > robust05.rm3_lr_0.6.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt robust05.rm3_lr_0.6.cut.txt -m map -m P.10 -M 1000

head -n 330000 ccrf.04_robust05.rm3/robust05.rm3_lr_0.6.txt > robust05.rm3_lr_0.6.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt robust05.rm3_lr_0.6.cut.txt -m map -m P.10 -M 1000

head -n 330000 ccrf.17_robust05.rm3/robust05.rm3_lr_0.6.txt > robust05.rm3_lr_0.6.cut.txt && eval/trec_eval.9.0/trec_eval src/main/resources/topics-and-qrels/qrels.robust2005.txt robust05.rm3_lr_0.6.cut.txt -m map -m P.10 -M 1000

```