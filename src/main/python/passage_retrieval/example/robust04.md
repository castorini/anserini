## Passage Retrieval Example: Robust04

### Create JSON collection of passages

Example 1: sentences
```
python src/main/python/passage_retrieval/segment.py \
--input {path/to/input/collection} \
--collection TrecCollection \
--generator JsoupGenerator \
--output {path/to/create/output/folder} \
--tokenize split_sentence_minword
```

Example 2: passages of at least 100 words
```
python src/main/python/passage_retrieval/segment.py \
--input {path/to/input/collection} \
--collection TrecCollection \
--generator JsoupGenerator \
--output {path/to/create/output/folder} \
--tokenize split_fixed_minword \
--min 100
```

Example 3: passages of at least 500 words ending on full sentences
```
python src/main/python/passage_retrieval/segment.py \
--input {path/to/input/collection} \
--collection TrecCollection \
--generator JsoupGenerator \
--output {path/to/create/output/folder} \
--tokenize split_sentence_minword \
--min 500
```

### Indexing passage collection
```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection -generator JsoupGenerator 
-threads 16 -input {path/to/json/generated} -index lucene-index.robust04_passages 
-storePositions -storeDocvectors -storeRawDocs >& log.robust04_passages
```	

### Retrieval
```	
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust04_passages 
-topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt 
-output run.robust04_passages.bm25.301-450.601-700.txt -bm25  	
```	

### Combine into document scores and ranks
```
python src/main/python/passage_retrieval/document_scorer.py \
--input run.robust04_passages.bm25.301-450.601-700.txt \
--output run.robust04_passages.bm25.301-450.601-700.maxscore.txt \
--method max_score
```
### Evaluation
```
eval/trec_eval.9.0.4/trec_eval -m P.30 -m ndcg_cut.30 src/main/resources/topics-and-qrels/qrels.robust2004.txt \
run.robust04_passages.bm25.topics.robust04.301-450.601-700.maxscore.txt
```

  
| BM25 All Topics | Document | Sentence | 100 Words | 300 Words | 500 Words | 500 Words  (full sentences) |
|-----------------|:--------:|:--------:|:---------:|:---------:|:---------:|:---------------------------:|
| MAP             |  0.2531  |  0.1859  |   0.2293  |   0.2496  |   0.2540  |            0.2546           |
| P30             |  0.3102  |  0.2494  |   0.2912  |   0.3087  |   0.3137  |            0.3141           |
| NDCG@30         |  0.4073  |  0.3176  |   0.3739  |   0.4018  |   0.4096  |            0.4102           |




