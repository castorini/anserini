# Neural Information Retrieval with MatchZoo

This is the document for the intergration between Anserini and MatchZoo. Currently, we support two datasets: Microblog and Robust04.

## Retrieval + Rerank Pipeline

### Index Construction

**Robust04**:

```
target/appassembler/bin/IndexCollection -collection TrecCollection \
 -generator JsoupGenerator -threads 16 -input /path/to/robust04 \
 -index lucene-index.robust04.pos+docvectors+rawdocs \
 -storePositions -storeDocvectors -storeRawDocs >& log.robust04.pos+docvectors+rawdocs
```

### Prepare Data for MatchZoo

**Initial Retrieval and Export Data for Neural IR Models**

``` bash
python src/main/python/rerank/scripts/export_robust04_dataset.py
```

**Clone MatchZoo**:

```bash
cd src/main/python/rerank/
git clone git@github.com:Victor0118/MatchZoo.git
git checkout rerank
```

**Prepare Word Vectors**:

1. Download the embedding from https://github.com/mmihaltz/word2vec-GoogleNews-vectors
2. Transform the embedding from the word2vec format into the glove format and put it in `src/main/python/rerank/MatchZoo/data/robust04`

```python
from gensim.models.keyedvectors import KeyedVectors

model = KeyedVectors.load_word2vec_format('path/to/GoogleNews-vectors-negative300.bin', binary=True)
model.save_word2vec_format('path/to/GoogleNews-vectors-negative300.txt', binary=False)
```

**Move and Process Data**:

```bash
cd data/robust04
python prepare_mz_data.py --data_path /path/to/data --train_file /data_path/train_file --dev_file /data_path/dev_file --test_file /data_path/test_file
python gen_w2v.py glove.GoogleNews-vectors-negative300.txt word_dict.txt embed_glove_d300
cat word_stats.txt | cut -d ' ' -f 1,4 > embed.idf
python gen_hist4drmm.py 20 # histagram bin size
cd ../..
```

**Train, Test and Evaluation**:

1. Train DRMM:
```bash
python matchzoo/main.py --phase train --model_file ./examples/robust04/config/drmm_robust04.config
``` 

2. Inference on the test set
```bash
python matchzoo/main.py --phase predict --model_file ./examples/robust04/config/drmm_robust04.config
```

3. Modify the `drmm_robust04.config` (predict -> relation_file/hist_feats_file and outputs -> save_path) to do the inference on the dev set using the same command above.

4. Tune the interpolation hyper-parameter on the dev set and apply it on the test set:

```bash
cd ../../../../../
python src/main/python/rerank/scripts/interpolate.py
```

Commond above already prints out the final score. If you want to print it without tuning again, you can run the following command:
```bash
./eval/trec_eval.9.0.4/trec_eval src/main/resources/topics-and-qrels/qrels.robust2004.txt predict.inter.test.drmm -m ndcg_cut.20 -m map -m recip_rank -m P.20,30
```

