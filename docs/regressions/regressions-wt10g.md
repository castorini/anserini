# Anserini Regressions: Wt10g

**Models**: various bag-of-words approaches

This page describes regressions for the TREC-9 Web Track and the TREC 2001 Web Track, which uses the [Wt10g collection](http://ir.dcs.gla.ac.uk/test_collections/wt10g.html).
The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/wt10g.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/wt10g.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression wt10g
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 16 \
  -collection TrecwebCollection \
  -input /path/to/wt10g \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.wt10g/ \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.wt10g &
```

The directory `/path/to/wt10g/` should be the root directory of the [Wt10g collection](http://ir.dcs.gla.ac.uk/test_collections/wt10g.html), containing a bunch of subdirectories, `WTX001` to `WTX104`.

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
They are downloaded from NIST:

+ [`topics.adhoc.451-550.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.adhoc.451-550.txt): topics for the [TREC-9 Web Track](http://trec.nist.gov/data/topics_eng/topics.451-500.gz) and the [TREC 2001 Web Track](http://trec.nist.gov/data/topics_eng/topics.501-550.txt)
+ [`qrels.adhoc.451-550.txt`](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/qrels.adhoc.451-550.txt): qrels for the [TREC-9 Web Track](http://trec.nist.gov/data/qrels_eng/qrels.trec9.main_web.gz) and the [TREC 2001 Web Track](http://trec.nist.gov/data/qrels_eng/adhoc_qrels.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wt10g/ \
  -topics tools/topics-and-qrels/topics.adhoc.451-550.txt \
  -topicReader Trec \
  -output runs/run.wt10g.bm25.topics.adhoc.451-550.txt \
  -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wt10g/ \
  -topics tools/topics-and-qrels/topics.adhoc.451-550.txt \
  -topicReader Trec \
  -output runs/run.wt10g.bm25+rm3.topics.adhoc.451-550.txt \
  -bm25 -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wt10g/ \
  -topics tools/topics-and-qrels/topics.adhoc.451-550.txt \
  -topicReader Trec \
  -output runs/run.wt10g.bm25+ax.topics.adhoc.451-550.txt \
  -bm25 -axiom -axiom.beta 0.1 -axiom.deterministic -rerankCutoff 20 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wt10g/ \
  -topics tools/topics-and-qrels/topics.adhoc.451-550.txt \
  -topicReader Trec \
  -output runs/run.wt10g.ql.topics.adhoc.451-550.txt \
  -qld &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wt10g/ \
  -topics tools/topics-and-qrels/topics.adhoc.451-550.txt \
  -topicReader Trec \
  -output runs/run.wt10g.ql+rm3.topics.adhoc.451-550.txt \
  -qld -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wt10g/ \
  -topics tools/topics-and-qrels/topics.adhoc.451-550.txt \
  -topicReader Trec \
  -output runs/run.wt10g.ql+ax.topics.adhoc.451-550.txt \
  -qld -axiom -axiom.beta 0.1 -axiom.deterministic -rerankCutoff 20 &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.451-550.txt runs/run.wt10g.bm25.topics.adhoc.451-550.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.451-550.txt runs/run.wt10g.bm25+rm3.topics.adhoc.451-550.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.451-550.txt runs/run.wt10g.bm25+ax.topics.adhoc.451-550.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.451-550.txt runs/run.wt10g.ql.topics.adhoc.451-550.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.451-550.txt runs/run.wt10g.ql+rm3.topics.adhoc.451-550.txt

bin/trec_eval -m map -m P.30 tools/topics-and-qrels/qrels.adhoc.451-550.txt runs/run.wt10g.ql+ax.topics.adhoc.451-550.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25**  | **+RM3**  | **+Ax**   | **QL**    | **+RM3**  | **+Ax**   |
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|
| [Wt10g (Topics 451-550)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.adhoc.451-550.txt)| 0.1991    | 0.2243    | 0.2134    | 0.2021    | 0.2190    | 0.2266    |
| **P30**                                                                                                      | **BM25**  | **+RM3**  | **+Ax**   | **QL**    | **+RM3**  | **+Ax**   |
| [Wt10g (Topics 451-550)](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels/topics.adhoc.451-550.txt)| 0.2211    | 0.2381    | 0.2463    | 0.2180    | 0.2310    | 0.2459    |
