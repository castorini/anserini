# Anserini: Experiments on the [Washington Post](https://trec.nist.gov/data/wapost/) ([Core18](https://trec-core.github.io/2018/))

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection \
WashingtonPostCollection -generator WapoGenerator -threads 16 -input \
/path/to/core18 -index lucene-index.core18.pos+docvectors -storePositions \
-storeDocvectors -storeRawDocs >& log.core18.pos+docvectors+rawdocs &
```

The directory `/path/to/core18/` should be the root directory of TREC Core2018 collection, i.e., `ls /path/to/core18/`
should bring up a single JSON file.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

** Users of Anserini will need to download the topics and qrels directly from TREC's from NIST and put them
at `src/main/resources/topics-and-qrels/` with file names `topics.core18.txt` and `qrels.core18.txt`.
** We will include the topics and qrels files after NIST publishes them.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core18.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.core18.txt -output run.core18.bm25.topics.core18.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core18.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.core18.txt -output run.core18.bm25+rm3.topics.core18.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core18.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.core18.txt -output run.core18.bm25+ax.topics.core18.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core18.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.core18.txt -output run.core18.ql.topics.core18.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core18.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.core18.txt -output run.core18.ql+rm3.topics.core18.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.core18.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.core18.txt -output run.core18.ql+ax.topics.core18.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt run.core18.bm25.topics.core18.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt run.core18.bm25+rm3.topics.core18.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt run.core18.bm25+ax.topics.core18.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt run.core18.ql.topics.core18.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt run.core18.ql+rm3.topics.core18.txt

eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.core18.txt run.core18.ql+ax.topics.core18.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
All Topics                              | 0.2487    | 0.2911    | 0.2919    | 0.2504    | 0.2754    | 0.2976    |


P30                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
All Topics                              | 0.3640    | 0.4087    | 0.4033    | 0.3620    | 0.3773    | 0.4067    |


