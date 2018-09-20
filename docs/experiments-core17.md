# Anserini: Experiments on [New York Times](https://catalog.ldc.upenn.edu/ldc2008t19) ([Core17](https://trec-core.github.io/2017/))

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection \
NewYorkTimesCollection -generator JsoupGenerator -threads 16 -input \
/path/to/core17 -index lucene-index.core17.pos+docvectors -storePositions \
-storeDocvectors -storeRawDocs >& log.core17.pos+docvectors+rawdocs &
```

The directory `/path/to/nyt_corpus/` should be the root directory of TREC Core collection, i.e., `ls /path/to/nyt_corpus/`
should bring up a bunch of subdirectories, `1987` to `2007`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.core17.txt`: [Topics that were assessed by NIST assessors (TREC 2017 NYT)](https://trec.nist.gov/data/core/core_nist.txt)
+ `qrels.core17.txt`: [qrels judgments produced by NIST assessors (TREC 2017 NYT)](https://trec.nist.gov/data/core/qrels.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core17.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.core17.txt -output run.core17.bm25.topics.core17.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core17.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.core17.txt -output run.core17.bm25+rm3.topics.core17.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core17.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.core17.txt -output run.core17.bm25+ax.topics.core17.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core17.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.core17.txt -output run.core17.ql.topics.core17.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core17.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.core17.txt -output run.core17.ql+rm3.topics.core17.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core17.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.core17.txt -output run.core17.ql+ax.topics.core17.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt run.core17.bm25.topics.core17.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt run.core17.bm25+rm3.topics.core17.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt run.core17.bm25+ax.topics.core17.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt run.core17.ql.topics.core17.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt run.core17.ql+rm3.topics.core17.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core17.txt run.core17.ql+ax.topics.core17.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
All Topics                              | 0.1977    | 0.2593    | 0.2700    | 0.1913    | 0.2396    | 0.2514    |


P30                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
All Topics                              | 0.4160    | 0.4860    | 0.4927    | 0.4373    | 0.4593    | 0.4827    |


