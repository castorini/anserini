# TREC 2018 Runbook: "h2oloo" Group

This is the runbook for TREC 2018 submissions by the "h2oloo" group. Note that Anserini (the system) was used by another group ("Anserini") for a completely different set of runs.
The h2oloo group participated in the Common Core Track.

Note that this document is specifically a **runbook** and does not encode regression experiments. Runbooks are designed to help us (i.e., TREC participants) document the steps taken to generate a run. They are primarily designed to make experiments repeatable (i.e., by ourselves), although they might be helpful for others who wish to replicate our runs.

However, we concede that _repeatability_ of the runs (even by us) is challenging, since the codebase is always evolving, and by the time we add proper documentation, it might be several months later... but we try our best...

**NOTE**: Topics and qrels are currently available only to TREC 2018 participants.
Users will need to download the topics and qrels directly from the NIST website and put them at `src/main/resources/topics-and-qrels/` with file names `topics.core18.txt` and `qrels.core18.txt`.
These files will be checked into the repo after NIST publishes them publicly.

## Data Preparation with Anserini

### Index Construction

**Robust04**:

```
target/appassembler/bin/IndexCollection -collection TrecCollection \
 -generator JsoupGenerator -threads 16 -input /path/to/robust04 \
 -index lucene-index.robust04.pos+docvectors+rawdocs \
 -storePositions -storeDocvectors -storeRawDocs >& log.robust04.pos+docvectors+rawdocs
```

**Robust05**:

```
target/appassembler/bin/IndexCollection -collection TrecCollection \
 -generator JsoupGenerator -threads 16 -input /path/to/robust05 \
 -index lucene-index.robust05.pos+docvectors+rawdocs \
 -storePositions -storeDocvectors -storeRawDocs >& log.robust05.pos+docvectors+rawdocs
```

**Core17**:

```
target/appassembler/bin/IndexCollection -collection NewYorkTimesCollection \
 -generator JsoupGenerator -threads 16 -input /path/to/core17 \
 -index lucene-index.core17.pos+docvectors+rawdocs \
 -storePositions -storeDocvectors -storeRawDocs >& log.core17.pos+docvectors+rawdocs
```

**Core18**:

```
target/appassembler/bin/IndexCollection -collection WashingtonPostCollection \
 -generator WapoGenerator -threads 16 -input /path/to/WashingtonPost.v2/data/ \
 -index lucene-index.core18.pos+docvectors+rawdocs \
 -storePositions -storeDocvectors -storeRawDocs &> log.core18.pos+docvectors+rawdocs
```

### Initial Retrieval

**Robust04**:

``` bash
target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.robust04.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt \
 -output run.robust04.bm25+rm3.topics.robust04.301-450.601-700.txt -bm25 -rm3 -hits 10000

target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.robust04.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt \
 -output run.robust04.bm25+ax.topics.robust04.301-450.601-700.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -hits 10000
```

**Robust05**:

``` bash
target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.robust05.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.robust05.txt \
 -output run.robust05.bm25+rm3.topics.robust05.txt -bm25 -rm3 -hits 10000

target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.robust05.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.robust05.txt \
 -output run.robust05.bm25+ax.topics.robust05.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic
```

**Core17**:

``` bash
target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.core17.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.core17.txt \
 -output run.core17.bm25+rm3.topics.core17.txt -bm25 -rm3 -hits 10000

target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.core17.pos+docvectors+rawdocs \
 -topics src/main/resources/topics-and-qrels/topics.core17.txt \
 -output run.core17.bm25+ax.topics.core17.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -hits 10000
```

**Core18**:

``` bash
target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.core18.pos+docvectors+rawdocs -bm25 \
 -topics src/main/resources/topics-and-qrels/topics.core18.txt \
 -output run.core18.bm25+rm3.topics.core18.txt -rm3 -hits 10000

target/appassembler/bin/SearchCollection -topicreader Trec \
 -index lucene-index.core18.pos+docvectors+rawdocs -bm25 \
 -topics src/main/resources/topics-and-qrels/topics.core18.txt \
 -output run.core18.bm25+ax.topics.core18.txt -axiom \
 -rerankCutoff 20 -axiom.deterministic -hits 10000
```

### Extraction of TF-IDF vectors

**Robust04**:

```bash
target/appassembler/bin/IndexUtils -index lucene-index.robust04.pos+docvectors+rawdocs \
 -dumpAllDocids NONE

target/appassembler/bin/IndexUtils -index lucene-index.robust04.pos+docvectors+rawdocs \
 -dumpDocVectors lucene-index.robust04.pos+docvectors+rawdocs.allDocids.txt \
 -docVectorWeight TF_IDF
```

**Robust05**:

```bash
target/appassembler/bin/IndexUtils -index lucene-index.robust05.pos+docvectors+rawdocs \
 -dumpAllDocids NONE

target/appassembler/bin/IndexUtils -index lucene-index.robust05.pos+docvectors+rawdocs \
 -dumpDocVectors lucene-index.robust05.pos+docvectors+rawdocs.allDocids.txt \
 -docVectorWeight TF_IDF
```

**Core17**:

```bash
target/appassembler/bin/IndexUtils -index lucene-index.core17.pos+docvectors+rawdocs \
 -dumpAllDocids NONE

target/appassembler/bin/IndexUtils -index lucene-index.core17.pos+docvectors+rawdocs \
 -dumpDocVectors lucene-index.core17.pos+docvectors+rawdocs.allDocids.txt \
 -docVectorWeight TF_IDF
```

**Core18**:

```bash
target/appassembler/bin/IndexUtils -index lucene-index.core18.pos+docvectors+rawdocs \
 -dumpAllDocids NONE

target/appassembler/bin/IndexUtils -index lucene-index.core18.pos+docvectors+rawdocs \
 -dumpDocVectors lucene-index.core18.pos+docvectors+rawdocs.allDocids.txt \
 -docVectorWeight TF_IDF
```

## Classifier Training/Inference with Scikit-Learn

Now we are done with Anserini part; everything below is in Python.

Create the following folder structure and populate with files as follows:

```
root
 |
 |--+ main_folder
 |   |-- train
 |   |-- test
 |
 |--+ src
 |  |--+ main_folder
 |     |--+ train
 |     |  |--+ tfidf
 |     |  |  |-- Robust04_tfidf.tar.gz
 |     |  |  |-- Robust05_tfidf.tar.gz
 |     |  |  |-- Core17_tfidf.tar.gz
 |     |  |
 |     |  +--+ rank
 |     |     |-- Robust04_qrels.txt
 |     |     |-- Robust05_qrels.txt
 |     |     |-- Core17_qrels.txt
 |     |
 |     |--+ test
 |        |-- Core18_tfidf.tar.gz
 |        |-- Core18_rerank_ax.txt
 |        |-- Core18_rerank_rm3.txt
 |
 |--+ script
    |-- build_train.py
    |-- build_test.py
    |-- train.py
    |-- submission.py
    |-- utils.py
```

The source Python scripts are in `src/main/python/trec2018/h2oloo-core/`.

### Building Training Data

Run the following command to build the training data:

```bash
python script/build_train.py --tfidf-folder src/main_folder/train/tfidf/ \
 --qrels-folder src/main_folder/train/rank/ --output-folder main_folder/train/
```

The command performs the following:

+ **Building vocabulary**. Training data consists of documents in qrels from Robust04, Robust05, and Core17. Vocabulary contains all words in training data.
+ **Building and Dumping docid index and vocabulary index**. Each document and each vocabulary in training data are indexed for the ease of building feature matrix in next step. They are dumped in `main_folder/train/`.
+ **Building features and labels**. Features are the TF-IDF values with shape (`n_docs`, `n_vocabs`) topic-wise, and labels are the qrels value (changing all 2s to 1s). Features are stored using `scipy.sparse_matrix`
+ **Dump features and labels**. Features are dumped as `.npz` file and labels are as `.npy` file for each topic. These files are named with the topic id, e.g., `321.npz` is the feature file for topic 321 and `321.npy` is the label file for topic 321. They are dumped in `main_folder/train/features` folder.

### Building Test Data

Run the following command to build test data:

``` bash
python script/build_test.py --tfidf-file src/main_folder/test/Core18_tfidf.tar.gz \
 --rank-file src/main_folder/test/Core18_rerank_[rm3|ax].txt \
 --output-folder main_folder/test/ --vocab-folder main_folder/train/
```

The command performs the following:

+ **Building and Dumping docid index**. Test data consists of top 10000 documents generated by BM25+RM3 or BM25+Axiom for each topic. Each document in test data is indexed. The docid index file is dumped in `main_folder/test`.
+ **Building features**. Features are TF-IDF values with shape (n_docs, n_vocabs), where vocabulary is build in the above step.
+ **Dump features**. A single `.npz` file was dumped at this step. In the dev step, we can use `docid index` to find the features for any document. The docid index file is dumped in `main_folder/test`.

### Training and Generating Classifier Results

Run the following command to train classifiers and run inference on Core18 documents topic-wise to rerank the documents.

``` bash
python script/train.py --train-folder main_folder/train/ \
 --test-folder main_folder/test/ \
 --rank-file src/main_folder/test/Core18_rerank_[rm3|ax].txt \
 --classifier clf --output-folder main_folder/output/clf
```

Where `clf` is one of the following:

+ `lr1`
+ `lr2`
+ `svm`
+ `sgdr` (SGD Regressor)
+ `sgdc` (SGD Classifier)
+ `lgb` (lightGBM)
+ `ridge`

The result will be in `main_folder/output/CLASSIFIER` folder, where `CLASSIFIER` is the classifier used for training.

**Note that only one of rm3 or axiom results can be generated at one time and fed into next step (because the output name are fixed).**

### Generating Submission Files

Run the following script to apply classifiers (either a single classifier or an ensemble) to generate the submissions. The argument `--ensemble` can be {1, 3, 7}:

+ with 1, `LR2` is used,
+ with 3, `LR2+SVM+LGB` is used, and
+ with 7, all classifiers are used.

**BM25+Axiom**

- LR2 + SVM + LGB on BM25+axiom ranking, interpolation weight 0.6 (Priority 1)

```bash
python script/submission.py --rank-file src/main_folder/test/Core18_rerank_ax.txt \
 --clf-folder main_folder/output --ratio 0.6 --ensemble 3 \
 --runtag h2oloo_e3ax0.6 --output core18.h2oloo_e3ax0.6.txt
```

- LR2 on BM25+axiom ranking, interpolation weight 0.6 (Priority 3)

``` bash
python script/submission.py --rank-file src/main_folder/test/Core18_rerank_ax.txt \
 --clf-folder main_folder/output --ratio 0.6 --ensemble 1 \
 --runtag h2oloo_LRax0.6 --output core18.h2oloo_LRax0.6.txt
```

- LR2 + SVM + LGB on BM25+axiom ranking, interpolation weight 0.7 (Priority 2)

``` bash
python script/submission.py --rank-file src/main_folder/test/Core18_rerank_ax.txt \
 --clf-folder main_folder/output --ratio 0.7 --ensemble 3 \
 --runtag h2oloo_e3ax0.7 --output core18.h2oloo_e3ax0.7.txt
```

- All classifiers on BM25+axiom ranking, interpolation weight 0.6 (Priority 5)

``` bash
python script/submission.py --rank-file src/main_folder/test/Core18_rerank_ax.txt \
 --clf-folder main_folder/output --ratio 0.6 --ensemble 7 \
 --runtag h2oloo_e7ax0.6 --output core18.h2oloo_e7ax0.6.txt
```

- All classifiers on BM25+axiom ranking, interpolation weight 0.7 (Priority 6)

``` bash
python script/submission.py --rank-file src/main_folder/test/Core18_rerank_ax.txt \
 --clf-folder main_folder/output --ratio 0.7 --ensemble 7 \
 --runtag h2oloo_e7ax0.7 --output core18.h2oloo_e7ax0.7.txt
```

**BM25+RM3**

- LR2 + SVM + LGB on BM25+RM3 ranking, interpolation weight 0.6 (Priority 4)

``` bash
python script/submission.py --rank-file src/main_folder/test/Core18_rerank_rm3.txt \
 --clf-folder main_folder/output --ratio 0.6 --ensemble 3 \
 --runtag h2oloo_e3rm30.6 --output core18.h2oloo_e3rm30.6.txt
```

- All classifiers on BM25+RM3 ranking, interpolation weight 0.6 (Priority 7)

``` bash
python script/submission.py --rank-file src/main_folder/test/Core18_rerank_rm3.txt \
 --clf-folder main_folder/output --ratio 0.6 --ensemble 7 \
 --runtag h2oloo_e7rm30.6 --output core18.h2oloo_e7rm30.6.txt
```

- All classifiers on BM25+RM3 ranking, interpolation weight 0.7 (Priority 8)

``` bash
python script/submission.py --rank-file src/main_folder/test/Core18_rerank_rm3.txt \
 --clf-folder main_folder/output --ratio 0.7 --ensemble 7 \
 --runtag h2oloo_e7rm30.7 --output core18.h2oloo_e7rm30.7.txt
```

- LR2 on BM25+RM3 ranking, interpolation weight 0.7 (Priority 10)

``` bash
python script/submission.py --rank-file src/main_folder/test/Core18_rerank_rm3.txt \
 --clf-folder main_folder/output --ratio 0.7 --ensemble 1 \
 --runtag h2oloo_LRrm0.7 --output core18.h2oloo_LRrm0.7.txt
```

## Effectiveness

Run `trec_eval` to evaluate effectiveness. On the runs based on Axiom:

```
../eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg -m P.10 ../src/main/resources/topics-and-qrels/qrels.core18.txt core18.h2oloo_LRax0.6.txt
../eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg -m P.10 ../src/main/resources/topics-and-qrels/qrels.core18.txt core18.h2oloo_e3ax0.6.txt
../eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg -m P.10 ../src/main/resources/topics-and-qrels/qrels.core18.txt core18.h2oloo_e3ax0.7.txt
../eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg -m P.10 ../src/main/resources/topics-and-qrels/qrels.core18.txt core18.h2oloo_e7ax0.6.txt
../eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg -m P.10 ../src/main/resources/topics-and-qrels/qrels.core18.txt core18.h2oloo_e7ax0.7.txt
```

On the runs based on RM3:

```
../eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg -m P.10 ../src/main/resources/topics-and-qrels/qrels.core18.txt core18.h2oloo_LRrm0.7.txt
../eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg -m P.10 ../src/main/resources/topics-and-qrels/qrels.core18.txt core18.h2oloo_e3rm30.6.txt
../eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg -m P.10 ../src/main/resources/topics-and-qrels/qrels.core18.txt core18.h2oloo_e7rm30.6.txt
../eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg -m P.10 ../src/main/resources/topics-and-qrels/qrels.core18.txt core18.h2oloo_e7rm30.7.txt
```

As of `commit xxxxx` the results of running the above commands yields the following:

Effectiveness     | MAP    | NDCG   | P10    |
:-----------------|-------:|-------:|-------:|
`h2oloo_LRax0.6`  | 0.3343 | 0.6297 | 0.5780 |
`h2oloo_e3ax0.6`  | 0.3507 | 0.6450 | 0.6060 |
`h2oloo_e3ax0.7`  | 0.3477 | 0.6463 | 0.6100 |
`h2oloo_e7ax0.6`  | 0.3464 | 0.6433 | 0.6080 |
`h2oloo_e7ax0.7`  | 0.3455 | 0.6434 | 0.6020 |
`h2oloo_LRrm0.7`  | 0.3504 | 0.6455 | 0.6000 |
`h2oloo_e3rm30.6` | 0.3652 | 0.6533 | 0.6140 |
`h2oloo_e7rm30.6` | 0.3620 | 0.6510 | 0.6100 |
`h2oloo_e7rm30.7` | 0.3609 | 0.6498 | 0.6040 |


