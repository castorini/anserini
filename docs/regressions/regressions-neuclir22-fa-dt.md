# Anserini Regressions: NeuCLIR22 &mdash; Persian (Document Translation)

This page presents **document translation** regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Persian, with the following configuration:

+ Queries: English
+ Documents: Machine-translated documents from Persian into English (corpus provided by the organizers)
+ Model: BM25

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/neuclir22-fa-dt.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/neuclir22-fa-dt.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-dt
```

## Corpus Download

The NeuCLIR 2022 corpus can be downloaded following the instructions [here](https://neuclir.github.io/).

With the corpus downloaded, unpack into `collections/` and run the following command to perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-dt \
  --corpus-path collections/neuclir22-fa-en
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection NeuClirCollection \
  -input /path/to/neuclir22-fa-en \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.neuclir22-fa-en \
  -storePositions -storeDocvectors -storeRaw \
  >& logs/log.neuclir22-fa-en &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-title.txt \
  -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-desc.txt \
  -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-desc_title.txt \
  -bm25 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-title.txt \
  -bm25 -rm3 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-desc.txt \
  -bm25 -rm3 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-desc_title.txt \
  -bm25 -rm3 &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-title.txt \
  -bm25 -rocchio &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-desc.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-desc.txt \
  -bm25 -rocchio &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en \
  -topics tools/topics-and-qrels/topics.neuclir22-en.original-desc_title.txt \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-desc_title.txt \
  -bm25 -rocchio &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default.topics.neuclir22-en.original-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rm3.topics.neuclir22-en.original-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en.bm25-default+rocchio.topics.neuclir22-en.original-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **BM25 (default)**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Persian): title (original English queries)](https://neuclir.github.io/)                       | 0.2197    | 0.2341    | 0.2375    |
| [NeuCLIR 2022 (Persian): desc (original English queries)](https://neuclir.github.io/)                        | 0.1663    | 0.2054    | 0.2103    |
| [NeuCLIR 2022 (Persian): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.2345    | 0.2519    | 0.2631    |
| **nDCG@20**                                                                                                  | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (original English queries)](https://neuclir.github.io/)                       | 0.3665    | 0.3449    | 0.3532    |
| [NeuCLIR 2022 (Persian): desc (original English queries)](https://neuclir.github.io/)                        | 0.2889    | 0.3144    | 0.3127    |
| [NeuCLIR 2022 (Persian): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.3670    | 0.3616    | 0.3720    |
| **J@20**                                                                                                     | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (original English queries)](https://neuclir.github.io/)                       | 0.3680    | 0.3974    | 0.3961    |
| [NeuCLIR 2022 (Persian): desc (original English queries)](https://neuclir.github.io/)                        | 0.3048    | 0.3614    | 0.3561    |
| [NeuCLIR 2022 (Persian): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.3706    | 0.3939    | 0.3930    |
| **Recall@1000**                                                                                              | **BM25 (default)**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Persian): title (original English queries)](https://neuclir.github.io/)                       | 0.7335    | 0.7942    | 0.8111    |
| [NeuCLIR 2022 (Persian): desc (original English queries)](https://neuclir.github.io/)                        | 0.6319    | 0.7663    | 0.7638    |
| [NeuCLIR 2022 (Persian): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.7652    | 0.8180    | 0.8248    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/neuclir22-fa-dt.template) and run `bin/build.sh` to rebuild the documentation.

