# Anserini: Experiments on Wt10g

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection WtCollection \
 -input /path/to/wt10g/ -generator JsoupGenerator \
 -index lucene-index.wt10g.pos+docvectors -threads 16 \
 -storePositions -storeDocvectors -optimize \
 >& log.wt10g.pos+docvectors &
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
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.wt10g.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.451-550.bm25.txt -bm25 &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.wt10g.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.451-550.bm25+rm3.txt -bm25 -rm3 &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.wt10g.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.451-550.ql.txt -ql &
nohup sh target/appassembler/bin/SearchCollection -topicreader Trec -index lucene-index.wt10g.pos+docvectors -topics src/main/resources/topics-and-qrels/topics.451-550.txt -output run.wt10g.451-550.ql+rm3.txt -ql -rm3 &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.451-550.txt run.wt10g.451-550.bm25.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.451-550.txt run.wt10g.451-550.bm25+rm3.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.451-550.txt run.wt10g.451-550.ql.txt
eval/trec_eval.9.0/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.451-550.txt run.wt10g.451-550.ql+rm3.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                    | BM25   | BM25+RM3 | QL     | QL+RM3 |
:----------------------|--------|----------|--------|--------|
Wt10g: Topics 451-550  | 0.1981 | 0.2166   | 0.2015 | 0.2173 |

P30                    | BM25   | BM25+RM3 | QL     | QL+RM3 |
:----------------------|--------|----------|--------|--------|
Wt10g: Topics 451-550  | 0.2201 | 0.2452   | 0.2184 | 0.2344 |
