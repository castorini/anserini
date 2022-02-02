# Anserini: Regressions for DeepImpact on [MS MARCO Passage](https://github.com/microsoft/MSMARCO-Passage-Ranking)

This page documents regression experiments for DeepImpact on the MS MARCO Passage Ranking Task, which is integrated into Anserini's regression testing framework.
DeepImpact is described in the following paper:

> Antonio Mallia, Omar Khattab, Nicola Tonellotto, and Torsten Suel. [Learning Passage Impacts for Inverted Indexes.](https://dl.acm.org/doi/10.1145/3404835.3463030) _SIGIR 2021_.

For more complete instructions on how to run end-to-end experiments, refer to [this page](experiments-msmarco-passage-deepimpact.md).

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/msmarco-passage-deepimpact.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/msmarco-passage-deepimpact.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/msmarco-passage-deepimpact \
  -index indexes/lucene-index.msmarco-passage-deepimpact/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 16 -impact -pretokenized \
  >& logs/log.msmarco-passage-deepimpact &
```

The directory `/path/to/msmarco-passage-deepimpact/` should be a directory containing the compressed `jsonl` files that comprise the corpus.
See [this page](experiments-msmarco-passage-deepimpact.md) for additional details.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 6980 dev set questions; see [this page](experiments-msmarco-passage.md) for more details.

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.msmarco-passage-deepimpact/ \
  -topics src/main/resources/topics-and-qrels/topics.msmarco-passage.dev-subset.deepimpact.tsv.gz -topicreader TsvInt \
  -output runs/run.msmarco-passage-deepimpact.deepimpact.topics.msmarco-passage.dev-subset.deepimpact.tsv.gz \
  -impact -pretokenized &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m map -c -m recip_rank -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt runs/run.msmarco-passage-deepimpact.deepimpact.topics.msmarco-passage.dev-subset.deepimpact.tsv.gz
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

MAP                                     | DeepImpact|
:---------------------------------------|-----------|
[MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.3334    |


MRR                                     | DeepImpact|
:---------------------------------------|-----------|
[MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.3386    |


R@1000                                  | DeepImpact|
:---------------------------------------|-----------|
[MS MARCO Passage: Dev](https://github.com/microsoft/MSMARCO-Passage-Ranking)| 0.9476    |

The above runs are in TREC output format and evaluated with `trec_eval`.
In order to reproduce results reported in the paper, we need to convert to MS MARCO output format and then evaluate:

```bash
python tools/scripts/msmarco/convert_trec_to_msmarco_run.py \
   --input runs/run.msmarco-passage-deepimpact.deepimpact.topics.msmarco-passage.dev-subset.deepimpact.tsv.gz \
   --output runs/run.msmarco-passage-deepimpact.deepimpact.topics.msmarco-passage.dev-subset.deepimpact.tsv.gz.msmarco --quiet

python tools/scripts/msmarco/msmarco_passage_eval.py \
   tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt \
   runs/run.msmarco-passage-deepimpact.deepimpact.topics.msmarco-passage.dev-subset.deepimpact.tsv.gz.msmarco
```

The results should be as follows:

```
#####################
MRR @10: 0.3252764133351524
QueriesRanked: 6980
#####################
```

The final evaluation metric is very close to the one reported in the paper (0.326).
