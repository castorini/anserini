# Anserini Regressions: NeuCLIR22 &mdash; Chinese (Document Translation)

This page presents **document translation** regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Chinese, with the following configuration:

+ Queries: English
+ Documents: Machine-translated documents from Chinese into English (corpus provided by the organizers)
+ Model: BM25

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/neuclir22-zh-dt.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/neuclir22-zh-dt.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-dt
```

## Corpus Download

The NeuCLIR 2022 corpus can be downloaded following the instructions [here](https://neuclir.github.io/).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-dt \
  --corpus-path collections/neuclir22-zh-en
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection NeuClirCollection \
  -input /path/to/neuclir22-zh-en \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.neuclir22-zh-en \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.neuclir22-zh-en &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-title.txt \
  -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-desc.txt \
  -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-desc_title.txt \
  -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-title.txt \
  -bm25 -rm3 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-desc.txt \
  -bm25 -rm3 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-desc_title.txt \
  -bm25 -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-title.txt \
  -bm25 -rocchio &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-desc.txt \
  -bm25 -rocchio &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-desc_title.txt \
  -bm25 -rocchio &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default.topics.neuclir22-en.original-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rm3.topics.neuclir22-en.original-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en.bm25-default+rocchio.topics.neuclir22-en.original-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Chinese): title (original English queries)](https://neuclir.github.io/)                       | 0.2658    | 0.2950    | 0.2944    |
| [NeuCLIR 2022 (Chinese): desc (original English queries)](https://neuclir.github.io/)                        | 0.2168    | 0.2564    | 0.2489    |
| [NeuCLIR 2022 (Chinese): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.2750    | 0.2821    | 0.2924    |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title (original English queries)](https://neuclir.github.io/)                       | 0.3705    | 0.3808    | 0.3802    |
| [NeuCLIR 2022 (Chinese): desc (original English queries)](https://neuclir.github.io/)                        | 0.3070    | 0.3182    | 0.3206    |
| [NeuCLIR 2022 (Chinese): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.3723    | 0.3580    | 0.3806    |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title (original English queries)](https://neuclir.github.io/)                       | 0.3908    | 0.4211    | 0.4197    |
| [NeuCLIR 2022 (Chinese): desc (original English queries)](https://neuclir.github.io/)                        | 0.3412    | 0.3978    | 0.3974    |
| [NeuCLIR 2022 (Chinese): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.3899    | 0.4154    | 0.4184    |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title (original English queries)](https://neuclir.github.io/)                       | 0.7567    | 0.8070    | 0.8129    |
| [NeuCLIR 2022 (Chinese): desc (original English queries)](https://neuclir.github.io/)                        | 0.6639    | 0.7519    | 0.7404    |
| [NeuCLIR 2022 (Chinese): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.7567    | 0.7959    | 0.8011    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/neuclir22-zh-dt.template) and run `bin/build.sh` to rebuild the documentation.

