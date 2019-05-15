## Sentencing Example: Robust04

### Creating sentence collection
```
python src/main/python/passage_retrieval/segment.py \
--input "path/to/input/collection" \
--collection TrecCollection \
--generator JsoupGenerator \
--output "path/to/create/output/folder" \
--threads 1 \
--tokenize text_sentencer
```
### Indexing sentence collection
```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection -generator JsoupGenerator 
-threads 16 -input "path/to/json/generated" -index lucene-index.robust04_sentences 
-storePositions -storeDocvectors -storeRawDocs >& log.robust04_sentences 
```	
### Retrieval and evaluation
```	
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust04_sentences 
-topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt 
-output run.robust04_sentences.bm25.301-450.601-700.txt -bm25 

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index  lucene-index.robust04_sentences 
-topics src/main/resources/topics-and-qrels/topics.robust04.301-450.601-700.txt 
-output run.robust04_sentences.ql.301-450.601-700.txt -ql 	
```	

- run `doc_score.py` to combine sentence rank/score into document rank/score by taking max score of sentences for each document for now.

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2004.txt 
run.robust04_sentences_maxscore.bm25.topics.robust04.301-450.601-700.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2004.txt 
run.robust04_sentences_maxscore.ql.topics.robust04.301-450.601-700.txt
```
  
  MAP                                     | BM25      | QL        |
:---------------------------------------|-----------|-----------|
All Topics                              |  0.1859    |  0.1831    |

P30                                     | BM25      | QL        |
:---------------------------------------|-----------|-----------|
All Topics                              |  0.2494    | 0.2369    |
