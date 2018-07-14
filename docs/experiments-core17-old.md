# Anserini Experiments on TREC Core

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCoreCollection \
 -input /path/to/nyt_corpus/ -generator JsoupGenerator \
 -index lucene-index.nyt.pos+docvectors+rawdocs -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -optimize \
 >& log.nyt.pos+docvectors+rawdocs &
```

The directory `/path/to/nyt_corpus/` should be the root directory of TREC Core collection, i.e., `ls /path/to/nyt_corpus/` 
should bring up a bunch of subdirectories, `1987` to `2007`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.core17.txt`: [Topics that were assessed by NIST assessors (TREC 2017 NYT)](https://trec.nist.gov/data/core/core_nist.txt)
+ `qrels.core17.txt`: [qrels judgments produced by NIST assessors (TREC 2017 NYT)](https://trec.nist.gov/data/core/qrels.txt)

After indexing is done, you should be able to perform a retrieval as follows:

```
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.nyt.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.core17.txt -output run.core17.bm25.txt -bm25 &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.nyt.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.core17.txt -output run.core17.bm25+rm3.txt -bm25 -rm3 &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.nyt.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.core17.txt -output run.core17.ql.txt -ql &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.nyt.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.core17.txt -output run.core17.ql+rm3.txt -ql -rm3 &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt run.core17.bm25.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt run.core17.bm25+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt run.core17.ql.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt run.core17.ql+rm3.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP        | BM25   | BM25+RM3 | QL     | QL+RM3 |
:----------|--------|----------|--------|--------|
All Topics | 0.1996 | 0.2633   | 0.1928 | 0.2409 |


P30        | BM25   | BM25+RM3 | QL     | QL+RM3 |
:----------|--------|----------|--------|--------|
All Topics | 0.4207 | 0.4893   | 0.4327 | 0.4647 |

