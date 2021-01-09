# Anserini: Regressions for [MS MARCO Passage Retrieval](https://github.com/microsoft/MSMARCO-Passage-Ranking)

This page documents regression experiments for the MS MARCO Passage Retrieval Task with [docTTTTTquery](https://github.com/castorini/docTTTTTquery) expansions.
These experiments are integrated into Anserini's regression testing framework.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-passage-docTTTTTquery.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-passage-docTTTTTquery.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection \
 -input /path/to/msmarco-passage-docTTTTTquery \
 -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
 -generator DefaultLuceneDocumentGenerator \
 -threads 9 -storePositions -storeDocvectors -storeRaw \
  >& logs/log.msmarco-passage-docTTTTTquery &
```

The directory `/path/to/msmarco-passage/` should be a directory containing `jsonl` files converted from the official passage collection, appended with the docTTTTTquery expansions.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -output runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.msmarco-passage.dev-subset.txt \
 -bm25 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -output runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt \
 -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.msmarco-passage.dev-subset.txt \
 -bm25 -bm25.k1 0.82 -bm25.b 0.68 &

nohup target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -output runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt \
 -bm25 -bm25.k1 0.82 -bm25.b 0.68 -rm3 &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.msmarco-passage.dev-subset.txt

tools/eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Passage Ranking: Dev Queries](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.2805    | 0.2243    | 0.2850    | 0.2266    |


R@1000                                  | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Passage Ranking: Dev Queries](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.9470    | 0.9463    | 0.9471    | 0.9479    |

The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`, while "tuned" refers to the tuned setting of `k1=0.82`, `b=0.72` _on the original passages_.
See [this page](experiments-msmarco-passage.md) for more details.

To replicate the _exact_ conditions for a leaderboard submission, retrieve using the following command:

```bash
wget https://www.dropbox.com/s/hq6xjhswiz60siu/queries.dev.small.tsv

sh target/appassembler/bin/SearchMsmarco -threads 8 \
 -index indexes/lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+raw \
 -queries queries.dev.small.tsv \
 -output runs/run.msmarco-passage-docTTTTTquery -hits 1000
```

Evaluate using the MS MARCO eval script:

```bash
wget https://www.dropbox.com/s/khsplt2fhqwjs0v/qrels.dev.small.tsv

python tools/scripts/msmarco/msmarco_passage_eval.py qrels.dev.small.tsv runs/run.msmarco-passage-docTTTTTquery
```

The results should be:

```
#####################
MRR @10: 0.27680089370991834
QueriesRanked: 6980
#####################
```

Which matches the score described in [the docTTTTTquery repo](https://github.com/castorini/docTTTTTquery) and also on the official [MS MARCO leaderboard](http://www.msmarco.org/).
