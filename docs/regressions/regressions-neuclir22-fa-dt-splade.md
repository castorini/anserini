# Anserini Regressions: NeuCLIR22 &mdash; Persian (Document Translation)

This page presents **document translation** regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Persian, with the following configuration:

+ Queries: English
+ Documents: Machine-translated documents from Persian into English (corpus provided by the organizers)
+ Model: [SPLADE CoCondenser SelfDistil](https://huggingface.co/naver/splade-cocondenser-selfdistil)

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/neuclir22-fa-dt-splade.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/neuclir22-fa-dt-splade.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

We make available a version of the corpus that has already been encoded with [SPLADE CoCondenser SelfDistil](https://huggingface.co/naver/splade-cocondenser-selfdistil), i.e., we performed model inference on every document and stored the output sparse vectors.
Thus, no neural inference is required to reproduce these experiments; see instructions below.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-dt-splade
```

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/neuclir22-fa-en-splade.tar -P collections/
tar xvf collections/neuclir22-fa-en-splade.tar -C collections/
```

To confirm, `neuclir22-fa-en-splade.tar` is 2.8 GB and has MD5 checksum `186d4b7025c4915cf9a33371c4ef37c5`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-fa-dt-splade \
  --corpus-path collections/neuclir22-fa-en-splade
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection JsonVectorCollection \
  -input /path/to/neuclir22-fa-en-splade \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -impact -pretokenized -storeRaw \
  >& logs/log.neuclir22-fa-en-splade &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-title.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt \
  -impact -pretokenized &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-fa-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-fa.txt runs/run.neuclir22-fa-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
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

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/neuclir22-fa-dt-splade.template) and run `bin/build.sh` to rebuild the documentation.

