# Anserini: Experiments on [Wt10g](http://ir.dcs.gla.ac.uk/test_collections/wt10g.html)

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecwebCollection \
-generator JsoupGenerator -threads 16 -input /path/to/wt10g -index \
lucene-index.wt10g.pos+docvectors -storePositions -storeDocvectors -storeRawDocs \
>& log.wt10g.pos+docvectors+rawdocs &
```

The directory `/path/to/wt10g/` should be the root directory of Wt10g collection, containing a bunch of subdirectories, `WTX001` to `WTX104`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.451-500.txt`: [Topics 451-500 (TREC-9 Web Track)](http://trec.nist.gov/data/topics_eng/topics.451-500.gz)
+ `topics.501-550.txt`: [Topics 501-550 (TREC 2001 Web Track)](http://trec.nist.gov/data/topics_eng/topics.501-550.txt)
+ `qrels.451-500.txt`: [qrels (TREC-9 Web Track)](http://trec.nist.gov/data/qrels_eng/qrels.trec9.main_web.gz)
+ `qrels.501-550.txt`: [qrels (TREC 2001 Web Track)](http://trec.nist.gov/data/qrels_eng/adhoc_qrels.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.wt10g.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.bm25.topics.451-550.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.wt10g.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.bm25+rm3.topics.451-550.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.wt10g.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.bm25+ax.topics.451-550.txt -bm25 -axiom -axiom.beta 0.1 -rerankCutoff 20 -axiom.deterministic &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.wt10g.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.ql.topics.451-550.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.wt10g.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.ql+rm3.topics.451-550.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.wt10g.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.ql+ax.topics.451-550.txt -ql -axiom -axiom.beta 0.1 -rerankCutoff 20 -axiom.deterministic &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.451-550.txt run.wt10g.bm25.topics.451-550.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.451-550.txt run.wt10g.bm25+rm3.topics.451-550.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.451-550.txt run.wt10g.bm25+ax.topics.451-550.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.451-550.txt run.wt10g.ql.topics.451-550.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.451-550.txt run.wt10g.ql+rm3.topics.451-550.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.451-550.txt run.wt10g.ql+ax.topics.451-550.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
Wt10g: Topics 451-550                   | 0.1992    | 0.2163    | 0.2200    | 0.2021    | 0.2148    | 0.2275    |


P30                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
Wt10g: Topics 451-550                   | 0.2218    | 0.2449    | 0.2483    | 0.2180    | 0.2265    | 0.2517    |


