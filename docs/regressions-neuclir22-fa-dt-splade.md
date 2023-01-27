# Anserini Regressions: NeuCLIR22 &mdash; Persian (Document Translation)

This page presents **document translation** regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Persian, with the following configuration:

+ Queries: English
+ Documents: Machine-translated documents from Persian into English (corpus provided by the organizers)
+ Model: SPLADE

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/neuclir22-fa-dt-splade.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/neuclir22-fa-dt-splade.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-dt-splade
```

## Corpus Download

TODO

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection JsonVectorCollection \
  -input /path/to/neuclir22-fa-en-splade \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -generator DefaultLuceneDocumentGenerator \
  -threads 8 -impact -pretokenized -storeRaw \
  >& logs/log.neuclir22-fa-en-splade &
```

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.splade.original-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-title.txt \
  -impact -pretokenized &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.splade.original-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt \
  -impact -pretokenized &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.splade.original-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt \
  -impact -pretokenized &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.splade.original-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.splade.original-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.splade.original-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &

target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.splade.original-title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.splade.original-desc.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics src/main/resources/topics-and-qrels/topics.neuclir22-en.splade.original-desc_title.txt.gz \
  -topicreader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
```

Evaluation can be performed using `trec_eval`:

```
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
tools/eval/trec_eval.9.0.4/trec_eval -c -m map src/main/resources/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **SPLADE**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Persian): title (original English queries)](https://neuclir.github.io/)                       | 0.2977    | 0.2800    | 0.2835    |
| [NeuCLIR 2022 (Persian): desc (original English queries)](https://neuclir.github.io/)                        | 0.3057    | 0.2956    | 0.3275    |
| [NeuCLIR 2022 (Persian): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.3203    | 0.3002    | 0.3267    |
| **nDCG@20**                                                                                                  | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (original English queries)](https://neuclir.github.io/)                       | 0.4627    | 0.4258    | 0.4438    |
| [NeuCLIR 2022 (Persian): desc (original English queries)](https://neuclir.github.io/)                        | 0.4618    | 0.4480    | 0.4675    |
| [NeuCLIR 2022 (Persian): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.4802    | 0.4477    | 0.4645    |
| **J@20**                                                                                                     | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (original English queries)](https://neuclir.github.io/)                       | 0.3768    | 0.3496    | 0.3781    |
| [NeuCLIR 2022 (Persian): desc (original English queries)](https://neuclir.github.io/)                        | 0.3882    | 0.3504    | 0.3860    |
| [NeuCLIR 2022 (Persian): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.3917    | 0.3610    | 0.3908    |
| **Recall@1000**                                                                                              | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (original English queries)](https://neuclir.github.io/)                       | 0.8478    | 0.8018    | 0.8592    |
| [NeuCLIR 2022 (Persian): desc (original English queries)](https://neuclir.github.io/)                        | 0.8796    | 0.8061    | 0.8735    |
| [NeuCLIR 2022 (Persian): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.8860    | 0.7948    | 0.8703    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/neuclir22-fa-dt-splade.template) and run `bin/build.sh` to rebuild the documentation.

