# Anserini: Regressions for [MS MARCO](https://github.com/microsoft/MSMARCO-Passage-Ranking) (Passage Expansion)

This page documents regression experiments for the MS MARCO Passage Ranking Task with Doc2query expansions, as proposed in the following paper:

+ Rodrigo Nogueira, Wei Yang, Jimmy Lin, Kyunghyun Cho. [Document Expansion by Query Prediction.](https://arxiv.org/abs/1904.08375) _arxiv:1904.08375_

These experiments are integrated into Anserini's regression testing framework.
For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-doc2query.md).

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection JsonCollection \
-generator LuceneDocumentGenerator -threads 9 -input \
/path/to/msmarco-passage-doc2query -index \
lucene-index.msmarco-passage-doc2query.pos+docvectors+rawdocs -storePositions \
-storeDocvectors -storeRawDocs >& \
log.msmarco-passage-doc2query.pos+docvectors+rawdocs &
```

The directory `/path/to/msmarco-passage/` should be a directory containing `jsonl` files converted from the official passage collection, which is in `tsv` format.
[This page](experiments-msmarco-passage.md) explains how to perform this conversion.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in `src/main/resources/topics-and-qrels/`.
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -topicreader Tsv -index lucene-index.msmarco-passage-doc2query.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -output run.msmarco-passage-doc2query.bm25-default.topics.msmarco-passage.dev-subset.txt -bm25 &

nohup target/appassembler/bin/SearchCollection -topicreader Tsv -index lucene-index.msmarco-passage-doc2query.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -output run.msmarco-passage-doc2query.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt -bm25 -rm3 &

nohup target/appassembler/bin/SearchCollection -topicreader Tsv -index lucene-index.msmarco-passage-doc2query.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -output run.msmarco-passage-doc2query.bm25-tuned.topics.msmarco-passage.dev-subset.txt -bm25 -k1 0.82 -b 0.68 &

nohup target/appassembler/bin/SearchCollection -topicreader Tsv -index lucene-index.msmarco-passage-doc2query.pos+docvectors+rawdocs -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.txt -output run.msmarco-passage-doc2query.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt -bm25 -k1 0.82 -b 0.68 -rm3 &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage-doc2query.bm25-default.topics.msmarco-passage.dev-subset.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage-doc2query.bm25-default+rm3.topics.msmarco-passage.dev-subset.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage-doc2query.bm25-tuned.topics.msmarco-passage.dev-subset.txt

eval/trec_eval.9.0.4/trec_eval -m map -c -m recall.1000 -c src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt run.msmarco-passage-doc2query.bm25-tuned+rm3.topics.msmarco-passage.dev-subset.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Passage Ranking: Dev Queries](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.2270    | 0.2028    | 0.2293    | 0.2077    |


R@1000                                  | BM25 (Default)| +RM3      | BM25 (Tuned)| +RM3      |
:---------------------------------------|-----------|-----------|-----------|-----------|
[MS MARCO Passage Ranking: Dev Queries](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.8900    | 0.8916    | 0.8911    | 0.8957    |



The setting "default" refers the default BM25 settings of `k1=0.9`, `b=0.4`, while "tuned" refers to the tuned setting of `k1=0.82`, `b=0.72`.
See [this page](experiments-msmarco-passage.md) for more details.
Note that these results are slightly different from the above referenced page because those experiments make up "fake" scores when converting runs from MS MARCO format into TREC format for evaluation by `trec_eval`.
