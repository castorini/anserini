# Anserini Regressions: QA with Wikipedia-DPR Corpus

**Models**: BM25

This page documents QA regression experiments on the wikipedia-dpr corpus, which is integrated into Anserini's regression testing framework.

The exact configurations for these regressions are stored in [this YAML file](../src/main/resources/regression/wiki-dpr-bm25.yaml).
Note that this page is automatically generated from [this template](../src/main/resources/docgen/templates/wiki-dpr-bm25.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --convert --regression wiki-dpr-bm25
```

## Indexing

Typical indexing command:

```bash
target/appassembler/bin/IndexCollection \
  -collection JsonCollection \
  -input /path/to/odqa-wiki-corpus-100w-splits \
  -index indexes/lucene-index.odqa-wiki-corpus-100w-splits/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 43 -storeRaw \
  >& logs/log.odqa-wiki-corpus-100w-splits &
```

The directory `/path/to/odqa-wiki-corpus-100w-splits/`should be a directory containing the wikipedia-dpr passages collection retrieved from [here](https://dl.fbaipublicfiles.com/dpr/wikipedia_split/psgs_w100.tsv.gz).

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the test set of multiple QA datasets, namely Natural Questions, TriviaQA, SQuAD, and WebQuestions.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.odqa-wiki-corpus-100w-splits/ \
  -topics src/main/resources/topics-and-qrels/topics.dpr.nq.test.txt \
  -topicreader DprNq \
  -output runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.nq.test.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.odqa-wiki-corpus-100w-splits/ \
  -topics src/main/resources/topics-and-qrels/topics.dpr.trivia.test.txt \
  -topicreader DprNq \
  -output runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.trivia.test.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.odqa-wiki-corpus-100w-splits/ \
  -topics src/main/resources/topics-and-qrels/topics.dpr.squad.test.txt \
  -topicreader DprJsonl \
  -output runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.squad.test.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.odqa-wiki-corpus-100w-splits/ \
  -topics src/main/resources/topics-and-qrels/topics.dpr.wq.test.txt \
  -topicreader DprJsonl \
  -output runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.wq.test.txt \
  -bm25 &
target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.odqa-wiki-corpus-100w-splits/ \
  -topics src/main/resources/topics-and-qrels/topics.nq.test.txt \
  -topicreader DprNq \
  -output runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.nq.test.txt \
  -bm25 &
```

The trec format will need to be converted to DPR's .json format for evaluation:
```bash
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.odqa-wiki-corpus-100w-splits/ \
  --topics dpr-nq-test \
  --input runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.nq.test.txt \
  --output runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.nq.test.txt.json \
 &
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.odqa-wiki-corpus-100w-splits/ \
  --topics dpr-trivia-test \
  --input runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.trivia.test.txt \
  --output runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.trivia.test.txt.json \
 &
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.odqa-wiki-corpus-100w-splits/ \
  --topics dpr-squad-test \
  --input runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.squad.test.txt \
  --output runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.squad.test.txt.json \
 &
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.odqa-wiki-corpus-100w-splits/ \
  --topics dpr-wq-test \
  --input runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.wq.test.txt \
  --output runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.wq.test.txt.json \
 &
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.odqa-wiki-corpus-100w-splits/ \
  --topics nq-test \
  --input runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.nq.test.txt \
  --output runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.nq.test.txt.json \
 &
```

Evaluation can be performed using scripts from pyserini:

```bash
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.nq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.nq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.trivia.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.trivia.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.squad.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.squad.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.wq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.dpr.wq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.nq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.odqa-wiki-corpus-100w-splits.bm25.topics.nq.test.txt.json
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **top_20_accuracy**                                                                                          | **BM25 (default parameters)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DPR: Natural Questions Test](https://github.com/facebookresearch/DPR)                                       | 0.6294    |
| [DPR: TriviaQA Test](https://github.com/facebookresearch/DPR)                                                | 0.7641    |
| [DPR: SQuAD Test](https://github.com/facebookresearch/DPR)                                                   | 0.7109    |
| [DPR: WebQuestions Test](https://github.com/facebookresearch/DPR)                                            | 0.6240    |
| [EfficientQA: Natural Questions Test](https://efficientqa.github.io/)                                        | 0.6399    |
| **top_100_accuracy**                                                                                         | **BM25 (default parameters)**|
| [DPR: Natural Questions Test](https://github.com/facebookresearch/DPR)                                       | 0.7825    |
| [DPR: TriviaQA Test](https://github.com/facebookresearch/DPR)                                                | 0.8315    |
| [DPR: SQuAD Test](https://github.com/facebookresearch/DPR)                                                   | 0.8184    |
| [DPR: WebQuestions Test](https://github.com/facebookresearch/DPR)                                            | 0.7549    |
| [EfficientQA: Natural Questions Test](https://efficientqa.github.io/)                                        | 0.7922    |

## Reproduction Log[*](reproducibility.md)

To add to this reproduction log, modify [this template](../src/main/resources/docgen/templates/wiki-dpr-bm25.template) and run `bin/build.sh` to rebuild the documentation.
