# Anserini: Regressions for [MS MARCO Passage Retrieval](https://github.com/microsoft/MSMARCO-Passage-Ranking)

This page documents regression experiments for the MS MARCO Passage Retrieval Task with [docTTTTTquery](https://github.com/castorini/docTTTTTquery) expansions.
These experiments are integrated into Anserini's regression testing framework.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-passage-docTTTTTquery.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-passage-docTTTTTquery.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection -input /path/to/msmarco-passage-docTTTTTquery \
 -index lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+rawdocs -generator LuceneDocumentGenerator -threads 9 \
 -storePositions -storeDocvectors -storeRawDocs >& log.msmarco-passage-docTTTTTquery.pos+docvectors+rawdocs &
```

The directory `/path/to/msmarco-passage/` should be a directory containing `jsonl` files converted from the official passage collection, appended with the docTTTTTquery expansions.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -bm25 -output run.msmarco-passage-docTTTTTquery.bm25-default.topics.msmarco-passage.dev-subset.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -bm25 -rm3 -output run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -bm25 -k1 0.82 -b 0.68 -output run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.msmarco-passage.dev-subset.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -bm25 -k1 0.82 -b 0.68 -rm3 -output run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage-docTTTTTquery.bm25-default.topics.msmarco-passage.dev-subset.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage-docTTTTTquery.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage-docTTTTTquery.bm25-tuned.topics.msmarco-passage.dev-subset.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage-docTTTTTquery.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt
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
Note that these results are slightly different from the above referenced page because those experiments make up "fake" scores when converting runs from MS MARCO format into TREC format for evaluation by `trec_eval`.

To replicate the _exact_ conditions for a leaderboard submission, retrieve using the following command:

```bash
wget https://storage.googleapis.com/doctttttquery_git/queries.dev.small.tsv
sh target/appassembler/bin/SearchMsmarco \
  -index lucene-index.msmarco-passage-docTTTTTquery.pos+docvectors+rawdocs \
  -qid_queries queries.dev.small.tsv \
  -output run.msmarco-passage-docTTTTTquery -hits 1000
```

Evaluate using the MS MARCO eval script:

```bash
wget https://storage.googleapis.com/doctttttquery_git/qrels.dev.small.tsv
python src/main/python/msmarco/msmarco_eval.py qrels.dev.small.tsv run.msmarco-passage-docTTTTTquery
```

The results should be:

```
#####################
MRR @10: 0.2767497271114737
QueriesRanked: 6980
#####################
```

Which matches the score described in [the docTTTTTquery repo](https://github.com/castorini/docTTTTTquery) and also on the official [MS MARCO leaderboard](http://www.msmarco.org/leaders.aspx).
