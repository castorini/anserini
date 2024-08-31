# Anserini Regressions: NeuCLIR22 &mdash; Chinese (Document Translation)

This page presents **document translation** regression experiments for the [TREC 2022 NeuCLIR Track](https://neuclir.github.io/), Chinese, with the following configuration:

+ Queries: English
+ Documents: Machine-translated documents from Chinese into English (corpus provided by the organizers)
+ Model: [SPLADE CoCondenser SelfDistil](https://huggingface.co/naver/splade-cocondenser-selfdistil)

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/neuclir22-zh-dt-splade.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/neuclir22-zh-dt-splade.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

We make available a version of the corpus that has already been encoded with [SPLADE CoCondenser SelfDistil](https://huggingface.co/naver/splade-cocondenser-selfdistil), i.e., we performed model inference on every document and stored the output sparse vectors.
Thus, no neural inference is required to reproduce these experiments; see instructions below.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-dt-splade
```

## Corpus Download

Download the corpus and unpack into `collections/`:

```bash
wget https://rgw.cs.uwaterloo.ca/pyserini/data/neuclir22-zh-en-splade.tar -P collections/
tar xvf collections/neuclir22-zh-en-splade.tar -C collections/
```

To confirm, `neuclir22-zh-en-splade.tar` is 4.0 GB and has MD5 checksum `3ca6540bd4312db359975b9f90fad069`.
With the corpus downloaded, the following command will perform the remaining steps below:

```bash
python src/main/python/run_regression.py --index --verify --search --regression neuclir22-zh-dt-splade \
  --corpus-path collections/neuclir22-zh-en-splade
```

## Indexing

Typical indexing command:

```
bin/run.sh io.anserini.index.IndexCollection \
  -threads 8 \
  -collection JsonVectorCollection \
  -input /path/to/neuclir22-zh-en-splade \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.neuclir22-zh-en-splade \
  -impact -pretokenized -storeRaw \
  >& logs/log.neuclir22-zh-en-splade &
```

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

After indexing has completed, you should be able to perform retrieval as follows:

```
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-title.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt \
  -impact -pretokenized &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt \
  -impact -pretokenized &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt \
  -impact -pretokenized -rm3 -collection JsonVectorCollection &

bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.neuclir22-zh-en-splade \
  -topics tools/topics-and-qrels/topics.neuclir22-en.splade.original-desc_title.txt.gz \
  -topicReader TsvInt \
  -output runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt \
  -impact -pretokenized -rocchio -collection JsonVectorCollection &
```

Evaluation can be performed using `trec_eval`:

```
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rm3.topics.neuclir22-en.splade.original-desc_title.txt

bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-title.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc.txt
bin/trec_eval -c -m ndcg_cut.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
python -m pyserini.eval.trec_eval -c -m judged.20 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m recall.1000 tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
bin/trec_eval -c -m map tools/topics-and-qrels/qrels.neuclir22-zh.txt runs/run.neuclir22-zh-en-splade.splade+rocchio.topics.neuclir22-en.splade.original-desc_title.txt
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **MAP**                                                                                                      | **SPLADE**| **+RM3**  | **+Rocchio**|
|:-------------------------------------------------------------------------------------------------------------|-----------|-----------|-----------|
| [NeuCLIR 2022 (Chinese): title (original English queries)](https://neuclir.github.io/)                       | 0.3068    | 0.2843    | 0.3151    |
| [NeuCLIR 2022 (Chinese): desc (original English queries)](https://neuclir.github.io/)                        | 0.3108    | 0.2631    | 0.3108    |
| [NeuCLIR 2022 (Chinese): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.3034    | 0.2820    | 0.3092    |
| **nDCG@20**                                                                                                  | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title (original English queries)](https://neuclir.github.io/)                       | 0.4233    | 0.3816    | 0.4204    |
| [NeuCLIR 2022 (Chinese): desc (original English queries)](https://neuclir.github.io/)                        | 0.4299    | 0.3496    | 0.4142    |
| [NeuCLIR 2022 (Chinese): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.4236    | 0.3770    | 0.4206    |
| **J@20**                                                                                                     | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title (original English queries)](https://neuclir.github.io/)                       | 0.3851    | 0.3575    | 0.3860    |
| [NeuCLIR 2022 (Chinese): desc (original English queries)](https://neuclir.github.io/)                        | 0.3825    | 0.3531    | 0.3825    |
| [NeuCLIR 2022 (Chinese): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.3961    | 0.3675    | 0.3969    |
| **Recall@1000**                                                                                              | **SPLADE**| **+RM3**  | **+Rocchio**|
| [NeuCLIR 2022 (Chinese): title (original English queries)](https://neuclir.github.io/)                       | 0.7997    | 0.7546    | 0.8038    |
| [NeuCLIR 2022 (Chinese): desc (original English queries)](https://neuclir.github.io/)                        | 0.7597    | 0.6969    | 0.7623    |
| [NeuCLIR 2022 (Chinese): desc+title (original English queries)](https://neuclir.github.io/)                  | 0.7922    | 0.7481    | 0.8067    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/neuclir22-zh-dt-splade.template) and run `bin/build.sh` to rebuild the documentation.

