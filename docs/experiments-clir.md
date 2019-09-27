# Cross-lingual Information Retrieval Experiments

This page contains instructions for running BM25 baselines on the NTCIR 8 *IR4QA* task.

## Data Prep

First, we need to convert the corpus into jsonline file format.

```
python src/main/python/clir/convert_collection_to_jsonl.py \
--language zh \
--corpus_directory /directory/to/ntcir-collection/ \
--output_path /path/to/dump
```
## Document Ranking with BM25

Run the command

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection \
-generator LuceneDocumentGenerator -threads 1 \
-input /directory/to/dump \
-index /directory/to/index/lucene-index.clir_zh.pos+docvectors+rawdocs -storePositions -storeDocvectors \
-storeRawDocs -language zh >& log.clir_zh.pos+docvectors+rawdocs &
```

to index the documents.

## Retrieval

To do the document retrieval, run

```
nohup target/appassembler/bin/SearchCollection -topicreader TsvStringKey \
-index lucene-index.clir_zh.pos+docvectors+rawdocs/ \
-topics src/main/resources/topics-and-qrels/topics.ntcir8zh.eval.txt \
-output run.clir-zh.bm25-default.zh.topics.txt -bm25 -language zh &
```

## Evaluation

To evalutate, run

```
eval/trec_eval.9.0.4/trec_eval -m map \
src/main/resources/topics-and-qrels/qrels.ntcir8.eval.txt \
run.clir-zh.bm25-default.zh.topics.txt
```

| Collection |  MAP  |
|:----------:|:-----:|
| NTCIR-8 ZH | 0.3568|