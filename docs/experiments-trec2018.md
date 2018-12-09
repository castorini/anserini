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
target/appassembler/bin/SearchCollection -index lucene-index.cw12.pos+docvectors+rawdocs -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -bm25 -rerankCutoff 20 -axiom -axiom.deterministic -axiom.beta 0.5 -runtag Anserini-UDInfolabWEB1-1 -output Anserini-UDInfolabWEB1-1.txt
target/appassembler/bin/SearchCollection -index lucene-index.cw12.pos+docvectors+rawdocs -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -bm25 -rerankCutoff 20 -axiom -axiom.deterministic -axiom.beta 0.5 -axiom.index lucene-index.snippets2018.pos+docvectors+rawdocs -runtag Anserini-UDInfolabWEB1-2 -output Anserini-UDInfolabWEB1-2.txt
target/appassembler/bin/SearchCollection -index lucene-index.cw12.pos+docvectors+rawdocs -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -bm25 -rerankCutoff 20 -axiom -axiom.deterministic -axiom.beta 0.5 -axiom.index lucene-index.wiki.pos+docvectors -runtag Anserini-UDInfolabWEB1-3 -output Anserini-UDInfolabWEB1-3.txt
target/appassembler/bin/SearchCollection -index lucene-index.cw12lite2013.pos+docvectors+rawdocs -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -ql -rerankCutoff 20 -axiom -axiom.deterministic -axiom.beta 1.7 -runtag Anserini-UDInfolabWEB2-1 -output Anserini-UDInfolabWEB2-1.txt
target/appassembler/bin/SearchCollection -index lucene-index.cw12lite2018.pos+docvectors+rawdocs -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -ql -rerankCutoff 20 -axiom -axiom.deterministic -axiom.beta 1.7 -runtag Anserini-UDInfolabWEB2-2 -output Anserini-UDInfolabWEB2-2.txt
target/appassembler/bin/SearchCollection -index lucene-index.cw12lite2018.pos+docvectors+rawdocs -topicreader Webxml -topics src/main/resources/topics-and-qrels/topics.web.201-250.txt -bm25 -rerankCutoff 20 -axiom -axiom.deterministic -axiom.beta 0.5 -axiom.index lucene-index.wiki.pos+docvectors -runtag Anserini-UDInfolabWEB2-3 -output Anserini-UDInfolabWEB2-3.txt
```

_Users of Anserini will need to download the NEW qrels (old ones are already included in Anserini) directly from TREC's from NIST and put it at `src/main/resources/topics-and-qrels/qrels.web.201-250.new.pruned.txt`. Anserini will include the new qrels file after NIST officially publishes it._

### Effectiveness

Evaluation can be performed using `trec_eval` and `gdeval.pl`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.new.pruned.txt Anserini-UDInfolabWEB1-1.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.new.pruned.txt Anserini-UDInfolabWEB1-2.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.new.pruned.txt Anserini-UDInfolabWEB1-3.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.new.pruned.txt Anserini-UDInfolabWEB2-1.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.new.pruned.txt Anserini-UDInfolabWEB2-2.txt
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.new.pruned.txt Anserini-UDInfolabWEB2-3.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.new.pruned.txt Anserini-UDInfolabWEB1-1.txt
eval/gdeval.pl -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.new.pruned.txt Anserini-UDInfolabWEB1-2.txt
eval/gdeval.pl -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.new.pruned.txt Anserini-UDInfolabWEB1-3.txt
eval/gdeval.pl -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.new.pruned.txt Anserini-UDInfolabWEB2-1.txt
eval/gdeval.pl -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.new.pruned.txt Anserini-UDInfolabWEB2-2.txt
eval/gdeval.pl -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.new.pruned.txt Anserini-UDInfolabWEB2-3.txt
```

With the above commands, you should be able to replicate the following results:


MAP                                     | Anserini-UDInfolabWEB1-1 | Anserini-UDInfolabWEB1-2 | Anserini-UDInfolabWEB1-3 | Anserini-UDInfolabWEB2-1 | Anserini-UDInfolabWEB2-2 | Anserini-UDInfolabWEB2-3 |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
Old Qrels                               | 0.1029    | 0.1772    | 0.0645    | 0.0920    | 0.0592    | 0.0583    |
New Qrels                               | 0.0910    | 0.1929    | 0.1079    | 0.0774    | 0.1333    | 0.1000    |


P30                                     | Anserini-UDInfolabWEB1-1 | Anserini-UDInfolabWEB1-2 | Anserini-UDInfolabWEB1-3 | Anserini-UDInfolabWEB2-1 | Anserini-UDInfolabWEB2-2 | Anserini-UDInfolabWEB2-3 |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
Old Qrels                               | 0.1940    | 0.3007    | 0.1400    | 0.2427    | 0.2293    | 0.1407    |
New Qrels                               | 0.2667    | 0.4520    | 0.3400    | 0.2960    | 0.2293    | 0.3467    |

NDCG20                                  | Anserini-UDInfolabWEB1-1 | Anserini-UDInfolabWEB1-2 | Anserini-UDInfolabWEB1-3 | Anserini-UDInfolabWEB2-1 | Anserini-UDInfolabWEB2-2 | Anserini-UDInfolabWEB2-3 |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
Old Qrels                               | 0.16760    | 0.25233    | 0.11001    | 0.19950    | 0.12996    | 0.10229    |
New Qrels                               | 0.11223    | 0.20426    | 0.16492    | 0.10746    | 0.10144    | 0.14627    |


ERR20                                   | Anserini-UDInfolabWEB1-1 | Anserini-UDInfolabWEB1-2 | Anserini-UDInfolabWEB1-3 | Anserini-UDInfolabWEB2-1 | Anserini-UDInfolabWEB2-2 | Anserini-UDInfolabWEB2-3 |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
Old Qrels                               | 0.10018    | 0.13991    | 0.06524    | 0.12447    | 0.09758    | 0.06108    |
New Qrels                               | 0.12962    | 0.17499    | 0.15344    | 0.08373    | 0.12700    | 0.15017    |


## Core Track

### Build the index
```
nohup target/appassembler/bin/IndexCollection -collection WashingtonPostCollection \
-input WashingtonPost.v2/data/ -generator WapoGenerator -index lucene-index.wash18.pos+docvectors+rawdocs \
-threads 44 -storePositions -storeDocvectors -storeRawDocs -optimize &> log.wash18.pos+docvectors+rawdocs &
```

### Submitted Runs
_Need to have your own topics file before TREC officially releases it_
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

_Users of Anserini will need to download the topics and qrels directly from TREC's from NIST and put them at `src/main/resources/topics-and-qrels/` with file names `topics.core18.txt` and `qrels.core18.txt`. We will include the topics and qrels files after NIST publishes them._

### Effectiveness

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt wash18_bm25.txt
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt wash18_bm25_rm3.txt
```

With the above commands, you should be able to replicate the following results:


MAP                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
All Topics                              | 0.2487    | 0.2911    | 0.2919    | 0.2504    | 0.2754    | 0.2976    |


P30                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
All Topics                              | 0.3640    | 0.4087    | 0.4033    | 0.3620    | 0.3773    | 0.4067    |


## News Track - Background Linking

### Build the index
The same as Core Track

### Submitted Runs
```
target/appassembler/bin/SearchCollection -searchnewsbackground -index lucene-index.wash18.pos+docvectors+rawdocs -topicreader NewsTrackBL -topics ~/newsir18-background-linking-topics.v2.xml -bm25 -hits 100 -backgroundlinking.k 1000 -backgroundlinking.weighted -runtag anserini_1000w -output tfidf_1000_weighted_bm25.txt
target/appassembler/bin/SearchCollection -searchnewsbackground -index lucene-index.wash18.pos+docvectors+rawdocs -topicreader NewsTrackBL -topics ~/newsir18-background-linking-topics.v2.xml -bm25 -axiom -axiom.deterministic -axiom.top 1000 -hits 100 -backgroundlinking.k 100 -runtag anserini_nax -output unweighted_bm25_ax_1000.txt
target/appassembler/bin/SearchCollection -searchnewsbackground -index lucene-index.wash18.pos+docvectors+rawdocs -topicreader NewsTrackBL -topics ~/newsir18-background-linking-topics.v2.xml -bm25 -sdm -hits 100 -backgroundlinking.k 1000 -runtag anserini_nsdm -output unweighted_bm25_sdm_1000.txt
target/appassembler/bin/SearchCollection -searchnewsbackground -index lucene-index.wash18.pos+docvectors+rawdocs -topicreader NewsTrackBL -topics ~/newsir18-background-linking-topics.v2.xml -bm25 -sdm -hits 100 -backgroundlinking.k 1000 -backgroundlinking.paragraph -runtag anserini_sdmp -output unweighted_bm25_sdm_paragraph.txt
target/appassembler/bin/SearchCollection -searchnewsbackground -index lucene-index.wash18.pos+docvectors+rawdocs -topicreader NewsTrackBL -topics ~/newsir18-background-linking-topics.v2.xml -bm25 -axiom -axiom.deterministic -axiom.top 1000 -hits 100 -backgroundlinking.k 1000 -backgroundlinking.paragraph -runtag anserini_axp -output unweighted_bm25_ax_paragraph.txt
=======

## News Track (Background Linking Task)

### Build the index (same with the core track)
```
target/appassembler/bin/IndexCollection -collection WashingtonPostCollection \
-input WashingtonPost.v2/data/ -generator JsoupGenerator -index lucene-index.wash18.pos+docvectors+rawdocs \
-threads 44 -storePositions -storeDocvectors -storeRawDocs -optimize &>log.wash18.pos+docvectors+rawdocs
```

### Submitted Runs
_Need to have your own topics file before TREC officially releases it_
```
target/appassembler/bin/SearchCollection -index lucene-index.wash18.pos+docvectors+rawdocs -searchnewsbackground -topicreader NewsTrackBL -topics newsir18-background-linking-topics.v2.xml -bm25 -hits 100 -newsBL.k 1000 -newsBL.weighted -runtag anserini_1000w -output tfidf_1000_weighted_bm25.txt
target/appassembler/bin/SearchCollection -index lucene-index.wash18.pos+docvectors+rawdocs -searchnewsbackground -topicreader NewsTrackBL -topics newsir18-background-linking-topics.v2.xml -bm25 -sdm -hits 100 -newsBL.k 1000 -runtag anserini_sdm -output tfidf_1000_unweighted_bm25_sdm.txt
target/appassembler/bin/SearchCollection -index lucene-index.wash18.pos+docvectors+rawdocs -searchnewsbackground -topicreader NewsTrackBL -topics newsir18-background-linking-topics.v2.xml -bm25 -axiom -axiom.deterministic -hits 100 -newsBL.k 1000 -runtag anserini_ax -output tfidf_1000_unweighted_bm25_ax.txt
target/appassembler/bin/SearchCollection -index lucene-index.wash18.pos+docvectors+rawdocs -searchnewsbackground -topicreader NewsTrackBL -topics newsir18-background-linking-topics.v2.xml -bm25 -sdm -hits 100 -newsBL.k 1000 -newsBL.paragraph -runtag anserini_sdmp -output tfidf_1000_unweighted_bm25_sdm_paragraph.txt
target/appassembler/bin/SearchCollection -index lucene-index.wash18.pos+docvectors+rawdocs -searchnewsbackground -topicreader NewsTrackBL -topics newsir18-background-linking-topics.v2.xml -bm25 -axiom -axiom.deterministic -hits 100 -newsBL.k 1000 -newsBL.paragraph -runtag anserini_axp -output tfidf_1000_unweighted_bm25_ax_paragraph.txt
```
