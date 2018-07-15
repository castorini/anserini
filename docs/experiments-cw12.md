# Anserini: Experiments on [ClueWeb12](http://lemurproject.org/clueweb12.php/)

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection ClueWeb12Collection \
-generator JsoupGenerator -threads 44 -input /path/to/cw12 -index \
lucene-index.cw12.pos+docvectors -storePositions -storeDocvectors >& \
log.cw12.pos+docvectors &
```

The directory `/path/to/cw12/` should be the root directory of ClueWeb12 collection, i.e., `/path/to/cw12/` should contain
`Disk1`, `Disk2`, `Disk3`, `Disk4`.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`, downloaded from NIST:

+ `topics.web.201-250.txt`: [Topics 201-250 (TREC 2013 Web Track)](http://trec.nist.gov/data/web/2013/trec2013-topics.xml)
+ `topics.web.251-300.txt`: [Topics 251-300 (TREC 2014 Web Track)](http://trec.nist.gov/data/web/2014/trec2014-topics.xml)
+ `qrels.web.201-250.txt`: [one aspect per topic qrels for Topics 201-250 (TREC 2013 Web Track)](http://trec.nist.gov/data/web/2013/qrels.adhoc.txt)
+ `qrels.web.251-300.txt`: [one aspect per topic qrels for Topics 251-300 (TREC 2014 Web Track)](http://trec.nist.gov/data/web/2014/qrels.adhoc.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topic src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12.bm25.topics.web.201-250.txt -bm25 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topic src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12.bm25.topics.web.251-300.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topic src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12.bm25+rm3.topics.web.201-250.txt -bm25 -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topic src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12.bm25+rm3.topics.web.251-300.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topic src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12.bm25+ax.topics.web.201-250.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topic src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12.bm25+ax.topics.web.251-300.txt -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topic src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12.ql.topics.web.201-250.txt -ql &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topic src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12.ql.topics.web.251-300.txt -ql &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topic src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12.ql+rm3.topics.web.201-250.txt -ql -rm3 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topic src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12.ql+rm3.topics.web.251-300.txt -ql -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topic src/main/resources/topics-and-qrels/topics.web.201-250.txt -output run.cw12.ql+ax.topics.web.201-250.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 &
nohup target/appassembler/bin/SearchCollection -topicreader Webxml -index lucene-index.cw12.pos+docvectors -topic src/main/resources/topics-and-qrels/topics.web.251-300.txt -output run.cw12.ql+ax.topics.web.251-300.txt -ql -axiom -rerankCutoff 20 -axiom.deterministic -axiom.beta 0.1 &

```

Evaluation can be performed using `trec_eval` and `gdeval.pl`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt -output run.cw12.bm25.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt -output run.cw12.bm25.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt -output run.cw12.bm25.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt -output run.cw12.bm25.topics.web.251-300.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt -output run.cw12.bm25+rm3.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt -output run.cw12.bm25+rm3.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt -output run.cw12.bm25+rm3.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt -output run.cw12.bm25+rm3.topics.web.251-300.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt -output run.cw12.bm25+ax.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt -output run.cw12.bm25+ax.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt -output run.cw12.bm25+ax.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt -output run.cw12.bm25+ax.topics.web.251-300.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt -output run.cw12.ql.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt -output run.cw12.ql.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt -output run.cw12.ql.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt -output run.cw12.ql.topics.web.251-300.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt -output run.cw12.ql+rm3.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt -output run.cw12.ql+rm3.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt -output run.cw12.ql+rm3.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt -output run.cw12.ql+rm3.topics.web.251-300.txt

eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.201-250.txt -output run.cw12.ql+ax.topics.web.201-250.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.201-250.txt -output run.cw12.ql+ax.topics.web.201-250.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.web.251-300.txt -output run.cw12.ql+ax.topics.web.251-300.txt
eval/gdeval.pl src/main/resources/topics-and-qrels/qrels.web.251-300.txt -output run.cw12.ql+ax.topics.web.251-300.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 WEB TRACK: TOPICS 201-250](HTTP://TREC.NIST.GOV/DATA/WEB2013.HTML)| 0.1673    | 0.1483    | 0.0411    | 0.1438    | 0.1245    | 0.0354    |
[TREC 2014 WEB TRACK: TOPICS 251-300](HTTP://TREC.NIST.GOV/DATA/WEB2014.HTML)| 0.2432    | 0.2460    | 0.0177    | 0.2401    | 0.2302    | 0.0188    |


P30                                     | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 WEB TRACK: TOPICS 201-250](HTTP://TREC.NIST.GOV/DATA/WEB2013.HTML)| 0.2827    | 0.2360    | 0.1800    | 0.2507    | 0.2053    | 0.1520    |
[TREC 2014 WEB TRACK: TOPICS 251-300](HTTP://TREC.NIST.GOV/DATA/WEB2014.HTML)| 0.4500    | 0.4167    | 0.1167    | 0.4367    | 0.3880    | 0.1180    |


NDCG20                                  | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 WEB TRACK: TOPICS 201-250](HTTP://TREC.NIST.GOV/DATA/WEB2013.HTML)| 0.2066    | 0.1752    | 0.1245    | 0.1905    | 0.1563    | 0.1117    |
[TREC 2014 WEB TRACK: TOPICS 251-300](HTTP://TREC.NIST.GOV/DATA/WEB2014.HTML)| 0.2646    | 0.2479    | 0.0970    | 0.2327    | 0.2139    | 0.0997    |


ERR20                                   | BM25      | BM25+RM3  | BM25+AX   | QL        | QL+RM3    | QL+AX     |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
[TREC 2013 WEB TRACK: TOPICS 201-250](HTTP://TREC.NIST.GOV/DATA/WEB2013.HTML)| 0.0000    | 0.0000    | 0.0916    | 0.0000    | 0.0000    | 0.0705    |
[TREC 2014 WEB TRACK: TOPICS 251-300](HTTP://TREC.NIST.GOV/DATA/WEB2014.HTML)| 0.0000    | 0.0000    | 0.0959    | 0.0000    | 0.0000    | 0.0988    |


