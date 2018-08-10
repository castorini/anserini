## CENTRE Track

### Task 2: Web Track 2013

Steps to deterministically reproduce the results:

#### Get code and data:
```
git clone https://github.com/castorini/Anserini.git && cd Anserini && mvn clean package appassembler:assemble
git clone https://github.com/castorini/Anserini-data.git
```
#### Build the indexes:
  - index of ClueWeb12 Full:
      ```
      nohup sh Anserini/target/appassembler/bin/IndexCollection -collection ClueWeb12Collection \
      -generator JsoupGenerator -threads 44 -input /path/to/cw12 -index lucene-index.cw12.pos+docvectors+rawdocs \
      -storePositions -storeDocvectors -storeRawDocs >& log.cw12.pos+docvectors+rawdocs &
      ```
  - index of CW12Lite2013:
    - If you already have the ClubWeb12 Full index with `-storeRawDocs` option enabled when indexing:
      ```
      nohup Anserini/target/appassembler/bin/IndexUtils -index lucene-index.cw12.pos+docvectors+rawdocs \
      -dumpRawDocs Anserini-data/TREC2018/CENTRE/task2/cw12lite2013_docids &
      ```
      about 19GB in raw size
      ```
      nohup Anserini/target/appassembler/bin/IndexCollection -collection HtmlCollection \
      -input cw12lite2013_docids_rawdocs.dump -index lucene-index.cw12lite2013.pos+docvectors+rawdocs \
      -generator JsoupGenerator -threads 44 -storePositions -storeDocvectors -storeRawDocs -optimize \
      >& log.cw12lite2013.pos+docvectors+rawdocs &
      ```
      about 8.3GB of the index
    - If you would like to build the index from scratch (e.g. you do not have clueweb12 full index):
      ```
      nohup sh Anserini/target/appassembler/bin/IndexCollection -collection ClueWeb12Collection \
      -generator JsoupGenerator -threads 44 -input /path/to/cw12 -index \
      lucene-index.cw12lite2013.pos+docvectors+rawdocs -storePositions -storeDocvectors -storeRawDocs \
      -whitelist Anserini-data/TREC2018/CENTRE/task2/cw12lite2013_docids >& log.cw12lite2013.pos+docvectors+rawdocs &
      ```
  - index of CW12Lite2018:
    - If you already have the ClubWeb12 Full index with `-storeRawDocs` option enabled when indexing:
      ```
      nohup Anserini/target/appassembler/bin/IndexUtils -index lucene-index.cw12.pos+docvectors+rawdocs \
      -dumpRawDocs Anserini-data/TREC2018/CENTRE/task2/cw12lite2018_docids &
      ```
      about 28GB in raw size
      ```
      nohup Anserini/target/appassembler/bin/IndexCollection -collection HtmlCollection \
      -input cw12lite2018_docids_rawdocs.dump -index lucene-index.cw12lite2018.pos+docvectors+rawdocs \
      -generator JsoupGenerator -threads 44 -storePositions -storeDocvectors -storeRawDocs \
      -optimize >& log.cw12lite2018.pos+docvectors+rawdocs &
      ```
      about 12GB of the index
    - If you would like to build the index from scratch (e.g. you do not have clueweb12 full index):
      ```
      nohup sh Anserini/target/appassembler/bin/IndexCollection -collection ClueWeb12Collection \
      -generator JsoupGenerator -threads 44 -input /path/to/cw12 -index \
      lucene-index.cw12lite2018.pos+docvectors+rawdocs -storePositions -storeDocvectors -storeRawDocs \
      -whitelist Anserini-data/TREC2018/CENTRE/task2/cw12lite2013_docids >& log.cw12lite2018.pos+docvectors+rawdocs &
      ```
  - index of snippets 2013
    ```
    sh Anserini/target/appassembler/bin/IndexCollection -input \
    Anserini-data/TREC2018/CENTRE/task2/searchengine_snippets/snippets2013_anserini/ \
    -collection JsonCollection -index lucene-index.snippets2013.pos+docvectors+rawdocs \
    -generator JsoupGenerator -threads 8 -uniqueDocid -storePositions -storeDocvectors \
    -storeRawDocs -optimize >& log.snippets2013.pos+docvectors+rawdocs
    ```
  - index of snippets 2018
    ```
    Anserini/target/appassembler/bin/IndexCollection -input \
    Anserini-data/TREC2018/CENTRE/task2/searchengine_snippets/snippets2018_anserini/ \
    -collection JsonCollection -index lucene-index.snippets2018.pos+docvectors+rawdocs \
    -generator JsoupGenerator -threads 8 -uniqueDocid -storePositions -storeDocvectors \
    -storeRawDocs -optimize >& log.snippets2018.pos+docvectors+rawdocs
    ```
#### (Optional) Get Wikipedia Dump:
+ `curl -O https://dumps.wikimedia.org/enwiki/20180620/enwiki-20180620-pages-articles.xml.bz2`
+ verification md5 `bca0ceb72e000105cc97fb54fef70cc3`
+ index of Wikipedia Dump
```
nohup sh Anserini/target/appassembler/bin/IndexCollection -collection WikipediaCollection \
-generator JsoupGenerator -threads 16 -input enwiki-20180620-pages-articles.xml.bz2 -index \
lucene-index.wiki.pos+docvectors -storePositions -storeDocvectors >& log.wiki.pos+docvectors &
```
#### Retrieval && Evaluation && Plot:
```
python src/main/python/trec2018/centre/task2/main.py --target_index lucene-index.cw12lite2018.pos+docvectors+rawdocs --expansion_index lucene-index.cw12lite2018.pos+docvectors+rawdocs  --retrieval
python src/main/python/trec2018/centre/task2/main.py --target_index lucene-index.cw12lite2018.pos+docvectors+rawdocs --expansion_index lucene-index.snippets2018.pos+docvectors+rawdocs  --retrieval
python src/main/python/trec2018/centre/task2/main.py --target_index lucene-index.cw12lite2013.pos+docvectors+rawdocs --expansion_index lucene-index.cw12lite2013.pos+docvectors+rawdocs  --retrieval
python src/main/python/trec2018/centre/task2/main.py --target_index lucene-index.cw12lite2013.pos+docvectors+rawdocs --expansion_index lucene-index.snippets2013.pos+docvectors+rawdocs  --retrieval
python src/main/python/trec2018/centre/task2/main.py --target_index lucene-index.cw12.pos+docvectors+rawdocs --expansion_index lucene-index.cw12.pos+docvectors+rawdocs  --retrieval
python src/main/python/trec2018/centre/task2/main.py --target_index lucene-index.cw12.pos+docvectors+rawdocs --expansion_index lucene-index.snippets2013.pos+docvectors+rawdocs  --retrieval
python src/main/python/trec2018/centre/task2/main.py --target_index lucene-index.cw12.pos+docvectors+rawdocs --expansion_index lucene-index.snippets2018.pos+docvectors+rawdocs  --retrieval
(Optional)python src/main/python/trec2018/centre/task2/main.py --target_index lucene-index.cw12lite2018.pos+docvectors+rawdocs --expansion_index lucene-index.wiki.pos+docvectors+rawdocs  --retrieval
(Optional)python src/main/python/trec2018/centre/task2/main.py --target_index lucene-index.cw12lite2013.pos+docvectors+rawdocs --expansion_index lucene-index.wiki.pos+docvectors+rawdocs  --retrieval
(Optional) python src/main/python/trec2018/centre/task2/main.py --target_index lucene-index.cw12.pos+docvectors+rawdocs --expansion_index lucene-index.wiki.pos+docvectors+rawdocs  --retrieval
python src/main/python/trec2018/centre/task2/main.py --eval --plot
```

#### Submitted Runs Only
The above commands generate _ALL_ results we have put in the notebook paper. To reproduce the submitted runs _ONLY_:
```
TBD
```


## Core Track

### Build the index
```
target/appassembler/bin/IndexCollection -collection WashingtonPostCollection \
-input WashingtonPost.v2/data/ -generator JsoupGenerator -index lucene-index.wash18.pos+docvectors+rawdocs \
-threads 44 -storePositions -storeDocvectors -storeRawDocs -optimize &>log.wash18.pos+docvectors+rawdocs
```

### Submitted Runs
```
target/appassembler/bin/SearchCollection -index lucene-index.wash18.pos+docvectors+rawdocs -topicreader Trec -topics 2018-test-topics.txt -bm25 -hits 10000 -runtag anserini_bm25 -output wash18_bm25.txt
target/appassembler/bin/SearchCollection -index lucene-index.wash18.pos+docvectors+rawdocs -topicreader Trec -topics 2018-test-topics.txt -bm25 -hits 10000 -runtag anserini_rm3 -rm3 -output wash18_bm25_rm3.txt
target/appassembler/bin/SearchCollection -index lucene-index.wash18.pos+docvectors+rawdocs -topicreader Trec -topics 2018-test-topics.txt -bm25 -hits 10000 -runtag anserini_ax -axiom -axiom.deterministic -rerankCutoff 20 -output wash18_bm25_ax.txt
target/appassembler/bin/SearchCollection -index lucene-index.wash18.pos+docvectors+rawdocs -topicreader Trec -topics 2018-test-topics.txt -bm25 -hits 10000 -runtag anserini_ax17 -axiom -axiom.deterministic -rerankCutoff 0 -axiom.index lucene-index.core17.pos+docvectors+rawdocs -output wash18_ax17.txt
target/appassembler/bin/SearchCollection -index lucene-index.wash18.pos+docvectors+rawdocs -topicreader Trec -topics 2018-test-topics.txt -ql -hits 10000 -runtag anserini_ql -output wash18_ql.txt
target/appassembler/bin/SearchCollection -index lucene-index.wash18.pos+docvectors+rawdocs -topicreader Trec -topics 2018-test-topics.txt -ql -hits 10000 -runtag anserini_qlrm3 -rm3 -output wash18_ql_rm3.txt
target/appassembler/bin/SearchCollection -index lucene-index.wash18.pos+docvectors+rawdocs -topicreader Trec -topics 2018-test-topics.txt -ql -hits 10000 -runtag anserini_qlax -axiom -axiom.deterministic -rerankCutoff 20 -output wash18_ql_ax.txt
target/appassembler/bin/SearchCollection -index lucene-index.wash18.pos+docvectors+rawdocs -topicreader Trec -topics 2018-test-topics.txt -ql -hits 10000 -runtag anserini_qlax17 -axiom -axiom.deterministic -rerankCutoff 0 -axiom.index lucene-index.core17.pos+docvectors+rawdocs -output wash18_ql_ax17.txt
```
