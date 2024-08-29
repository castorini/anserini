# Anserini Regressions: NeuCLIR22 &mdash; Russian (Document Translation)

This page presents **document translation** regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Russian, with the following configuration:

+ Queries: English
+ Documents: Machine-translated documents from Russian into English (corpus provided by the organizers)
+ Model: [SPLADE CoCondenser SelfDistil](https://huggingface.co/naver/splade-cocondenser-selfdistil)

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/neuclir22-ru-dt-splade.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/neuclir22-ru-dt-splade.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

We make available a version of the corpus that has already been encoded with [SPLADE CoCondenser SelfDistil](https://huggingface.co/naver/splade-cocondenser-selfdistil), i.e., we performed model inference on every document and stored the output sparse vectors.
Thus, no neural inference is required to reproduce these experiments; see instructions below.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-dt-splade
```

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/neuclir22-ru-en-splade.tar -P collections/
tar xvf collections/neuclir22-ru-en-splade.tar -C collections/
```

To confirm, `neuclir22-ru-en-splade.tar` is 5.5 GB and has MD5 checksum `828e44e1ab8af19e5e1224539abe347c`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-ru-dt-splade \
  --corpus-path collections/neuclir22-ru-en-splade
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection JsonVectorCollection \
  -input /path/to/neuclir22-ru-en-splade \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.neuclir22-ru-en-splade \
  -impact -pretokenized -storeRaw \
  >& logs/log.neuclir22-ru-en-splade &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-title.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt \
  -impact -pretokenized &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-ru-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-ru.txt runs/run.neuclir22-ru-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **SPLADE**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Russian): title (original English queries)](https://neuclir.github.io/)                       | 0.3745    | 0.3151    | 0.3909    |
| [NeuCLIR 2022 (Russian): desc (original English queries)](https://neuclir.github.io/)                        | 0.3294    | 0.2717    | 0.3378    |
| [NeuCLIR 2022 (Russian): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.3559    | 0.3026    | 0.3659    |
| **nDCG@20**                                                                                                  | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title (original English queries)](https://neuclir.github.io/)                       | 0.4865    | 0.4154    | 0.4836    |
| [NeuCLIR 2022 (Russian): desc (original English queries)](https://neuclir.github.io/)                        | 0.4193    | 0.3552    | 0.4243    |
| [NeuCLIR 2022 (Russian): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.4573    | 0.4000    | 0.4604    |
| **J@20**                                                                                                     | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title (original English queries)](https://neuclir.github.io/)                       | 0.3741    | 0.3469    | 0.3816    |
| [NeuCLIR 2022 (Russian): desc (original English queries)](https://neuclir.github.io/)                        | 0.3702    | 0.3219    | 0.3680    |
| [NeuCLIR 2022 (Russian): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.3838    | 0.3518    | 0.3846    |
| **Recall@1000**                                                                                              | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Russian): title (original English queries)](https://neuclir.github.io/)                       | 0.8538    | 0.8169    | 0.8686    |
| [NeuCLIR 2022 (Russian): desc (original English queries)](https://neuclir.github.io/)                        | 0.8376    | 0.7529    | 0.8238    |
| [NeuCLIR 2022 (Russian): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.8513    | 0.7704    | 0.8544    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/neuclir22-ru-dt-splade.template) and run `bin/build.sh` to rebuild the documentation.

