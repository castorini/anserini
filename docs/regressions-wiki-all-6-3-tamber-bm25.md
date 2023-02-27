# Anserini Regressions: QA with wiki-all-6-3-tamber Corpus

**Models**: BM25

This page documents QA regression experiments on the wiki-all-6-3-tamber corpus, which is integrated into Anserini's regression testing framework.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/wiki-all-6-3-tamber-bm25.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/wiki-all-6-3-tamber-bm25.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression wiki-all-6-3-tamber-bm25
```

## Indexing

Typical indexing command:

```bash
target/appassembler/bin/IndexCollection \
  -collection MrTyDiCollection \
  -input /path/to/wiki-all-6-3-tamber \
  -index indexes/lucene-index.wiki-all-6-3-tamber/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 20 -storeRaw \
  >& logs/log.wiki-all-6-3-tamber &
```

The directory `/path/to/wiki-all-6-3-tamber/`should be a directory containing the wiki-all-6-3-tamber passages collection retrieved from [here](https://huggingface.co/datasets/castorini/odqa-wiki-corpora).

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the test set of multiple QA datasets, namely Natural Questions, TriviaQA, SQuAD, and WebQuestions.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wiki-all-6-3-tamber/ \
  -topics src/main/resources/topics-and-qrels/topics.dpr.nq.test.txt \
  -topicreader DprNq \
  -output runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.nq.test.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wiki-all-6-3-tamber/ \
  -topics src/main/resources/topics-and-qrels/topics.dpr.trivia.test.txt \
  -topicreader DprNq \
  -output runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.trivia.test.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wiki-all-6-3-tamber/ \
  -topics src/main/resources/topics-and-qrels/topics.dpr.squad.test.txt \
  -topicreader DprJsonl \
  -output runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.squad.test.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wiki-all-6-3-tamber/ \
  -topics src/main/resources/topics-and-qrels/topics.dpr.wq.test.txt \
  -topicreader DprJsonl \
  -output runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.wq.test.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wiki-all-6-3-tamber/ \
  -topics src/main/resources/topics-and-qrels/topics.dpr.curated.test.txt \
  -topicreader DprJsonl \
  -output runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.curated.test.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.wiki-all-6-3-tamber/ \
  -topics src/main/resources/topics-and-qrels/topics.nq.test.txt \
  -topicreader DprNq \
  -output runs/run.wiki-all-6-3-tamber.bm25.topics.nq.test.txt \
  -bm25 &
```

The trec format will need to be converted to DPR's JSON format for evaluation:
```bash
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.wiki-all-6-3-tamber/ \
  --topics dpr-nq-test \
  --input runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.nq.test.txt \
  --output runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.nq.test.txt.json \
  --combine-title-text &
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.wiki-all-6-3-tamber/ \
  --topics dpr-trivia-test \
  --input runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.trivia.test.txt \
  --output runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.trivia.test.txt.json \
  --combine-title-text &
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.wiki-all-6-3-tamber/ \
  --topics dpr-squad-test \
  --input runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.squad.test.txt \
  --output runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.squad.test.txt.json \
  --combine-title-text &
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.wiki-all-6-3-tamber/ \
  --topics dpr-wq-test \
  --input runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.wq.test.txt \
  --output runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.wq.test.txt.json \
  --combine-title-text &
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.wiki-all-6-3-tamber/ \
  --topics dpr-curated-test \
  --input runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.curated.test.txt \
  --output runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.curated.test.txt.json \
  --combine-title-text  --regex &
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.wiki-all-6-3-tamber/ \
  --topics nq-test \
  --input runs/run.wiki-all-6-3-tamber.bm25.topics.nq.test.txt \
  --output runs/run.wiki-all-6-3-tamber.bm25.topics.nq.test.txt.json \
  --combine-title-text &
```

Evaluation can be performed using scripts from pyserini:

```bash
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.nq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.nq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.trivia.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.trivia.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.squad.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.squad.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.wq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.wq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.curated.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.wiki-all-6-3-tamber.bm25.topics.dpr.curated.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.wiki-all-6-3-tamber.bm25.topics.nq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.wiki-all-6-3-tamber.bm25.topics.nq.test.txt.json
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **top_20_accuracy**                                                                                          | **BM25 (default parameters)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DPR: Natural Questions Test](https://github.com/facebookresearch/DPR)                                       | 0.6604    |
| [DPR: TriviaQA Test](https://github.com/facebookresearch/DPR)                                                | 0.7832    |
| [DPR: SQuAD Test](https://github.com/facebookresearch/DPR)                                                   | 0.7265    |
| [DPR: WebQuestions Test](https://github.com/facebookresearch/DPR)                                            | 0.6403    |
| [DPR: CuratedTREC Test](https://github.com/facebookresearch/DPR)                                             | 0.8055    |
| [EfficientQA: Natural Questions Test](https://efficientqa.github.io/)                                        | 0.6665    |
| **top_100_accuracy**                                                                                         | **BM25 (default parameters)**|
| [DPR: Natural Questions Test](https://github.com/facebookresearch/DPR)                                       | 0.8083    |
| [DPR: TriviaQA Test](https://github.com/facebookresearch/DPR)                                                | 0.8482    |
| [DPR: SQuAD Test](https://github.com/facebookresearch/DPR)                                                   | 0.8325    |
| [DPR: WebQuestions Test](https://github.com/facebookresearch/DPR)                                            | 0.7874    |
| [DPR: CuratedTREC Test](https://github.com/facebookresearch/DPR)                                             | 0.9135    |
| [EfficientQA: Natural Questions Test](https://efficientqa.github.io/)                                        | 0.8166    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/wiki-all-6-3-tamber-bm25.template) and run `bin/build.sh` to rebuild the documentation.
