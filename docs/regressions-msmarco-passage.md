# Anserini: Regressions for [MS MARCO Passage Retrieval](https://github.com/microsoft/MSMARCO-Passage-Ranking)

This page documents regression experiments for the MS MARCO Passage Retrieval Task, which is integrated into Anserini's regression testing framework.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-msmarco-passage.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-passage.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-passage.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection -input /path/to/msmarco-passage \
 -index lucene-index.msmarco-passage.pos+docvectors+rawdocs -generator LuceneDocumentGenerator -threads 9 \
 -storePositions -storeDocvectors -storeRawDocs >& log.msmarco-passage.pos+docvectors+rawdocs &
```

The directory `/path/to/msmarco-passage/` should be a directory containing `jsonl` files converted from the official passage collection, which is in `tsv` format.
[This page](experiments-msmarco-passage.md) explains how to perform this conversion.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-passage.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -bm25 -output run.msmarco-passage.bm25-default.topics.msmarco-passage.dev-subset.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-passage.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -bm25 -rm3 -output run.msmarco-passage.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-passage.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -bm25 -axiom -rerankCutoff 20 -axiom.deterministic -output run.msmarco-passage.bm25-default+ax.topics.msmarco-passage.dev-subset.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-passage.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -bm25 -bm25prf -output run.msmarco-passage.bm25-default+prf.topics.msmarco-passage.dev-subset.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-passage.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -bm25 -k1 0.82 -b 0.68 -output run.msmarco-passage.bm25-tuned.topics.msmarco-passage.dev-subset.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-passage.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -bm25 -k1 0.82 -b 0.68 -rm3 -output run.msmarco-passage.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-passage.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -bm25 -k1 0.82 -b 0.68 -axiom -rerankCutoff 20 -axiom.deterministic -output run.msmarco-passage.bm25-tuned+ax.topics.msmarco-passage.dev-subset.txt &

nohup target/appassembler/bin/SearchCollection -index lucene-index.msmarco-passage.pos+docvectors+rawdocs \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt \
 -bm25 -k1 0.82 -b 0.68 -bm25prf -output run.msmarco-passage.bm25-tuned+prf.topics.msmarco-passage.dev-subset.txt &
```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage.bm25-default.topics.msmarco-passage.dev-subset.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage.bm25-default+ax.topics.msmarco-passage.dev-subset.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage.bm25-default+prf.topics.msmarco-passage.dev-subset.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage.bm25-tuned.topics.msmarco-passage.dev-subset.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage.bm25-tuned+ax.topics.msmarco-passage.dev-subset.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage.bm25-tuned+prf.topics.msmarco-passage.dev-subset.txt
```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Passage Ranking: Dev Queries](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.1926    | 0.1661    | 0.1625    | 0.1520    | 0.1958    | 0.1762    | 0.1699    | 0.1582    |


R@1000                                  | BM25 (Default)| +RM3      | +Ax       | +PRF      | BM25 (Tuned)| +RM3      | +Ax       | +PRF      |
:---------------------------------------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|
[MS MARCO Passage Ranking: Dev Queries](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.8526    | 0.8606    | 0.8747    | 0.8537    | 0.8573    | 0.8687    | 0.8809    | 0.8561    |

The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`, while "tuned" refers to the tuned setting of `k1=0.82`, `b=0.68`.
See [this page](experiments-msmarco-passage.md) for more details.
Note that these results are slightly different from the above referenced page because those experiments make up "fake" scores when converting runs from MS MARCO format into TREC format for evaluation by `trec_eval`.
