# Anserini: Experiments on [AQUAINT](https://tac.nist.gov//data/data_desc.html#AQUAINT) (Robust05)

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
-generator JsoupGenerator -threads 16 -input /path/to/robust05 -index \
lucene-index.robust05.pos+docvectors -storePositions -storeDocvectors \
-storeRawDocs >& log.robust05.pos+docvectors+rawdocs &
```

The directory `/path/to/aquaint/` should be the root directory of AQUAINT collection; under subdirectory `disk1/` there should be `NYT/` and under subdirectory `disk2/` there should be `APW/` and `XIE/`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.robust05.txt`: [Hard Topics of ROBUST04 (TREC 2005 Robust Track)](http://trec.nist.gov/data/robust/05/05.50.topics.txt)
+ `qrels.robust2005.txt`: [qrels (TREC 2005 Robust Track)](http://trec.nist.gov/data/robust/05/TREC2005.qrels.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust05.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.robust05.txt -output run.robust05.bm25.topics.robust05.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust05.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.robust05.txt -output run.robust05.bm25+rm3.topics.robust05.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust05.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.robust05.txt -output run.robust05.bm25+ax.topics.robust05.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust05.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.robust05.txt -output run.robust05.ql.topics.robust05.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust05.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.robust05.txt -output run.robust05.ql+rm3.topics.robust05.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.robust05.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.robust05.txt -output run.robust05.ql+ax.topics.robust05.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2005.txt run.robust05.bm25.topics.robust05.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2005.txt run.robust05.bm25+rm3.topics.robust05.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2005.txt run.robust05.bm25+ax.topics.robust05.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2005.txt run.robust05.ql.topics.robust05.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2005.txt run.robust05.ql+rm3.topics.robust05.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.robust2005.txt run.robust05.ql+ax.topics.robust05.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
All Topics                              | 0.2031    | 0.2514    | 0.2584    | 0.2028    | 0.2474    | 0.2476    |


P30                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
All Topics                              | 0.3693    | 0.3993    | 0.4120    | 0.3653    | 0.4047    | 0.4113    |


