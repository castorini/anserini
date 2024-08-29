# Anserini Regressions: QA on Wikipedia 100-word splits

**Models**: BM25

This page documents QA regression experiments on the `wikipedia-dpr-100w` corpus, which is integrated into Anserini's regression testing framework.

The exact configurations for these regressions are stored in [this YAML file](../../src/main/resources/regression/wikipedia-dpr-100w-bm25.yaml).
Note that this page is automatically generated from [this template](../../src/main/resources/docgen/templates/wikipedia-dpr-100w-bm25.template) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```bash
python src/main/python/run_regression.py --index --verify --search --regression wikipedia-dpr-100w-bm25
```

## Indexing

Typical indexing command:

```bash
bin/run.sh io.anserini.index.IndexCollection \
  -threads 43 \
  -collection JsonCollection \
  -input /path/to/wikipedia-dpr-100w \
  -generator DefaultLuceneDocumentGenerator \
  -index indexes/lucene-index.wikipedia-dpr-100w/ \
  -storeRaw \
  >& logs/log.wikipedia-dpr-100w &
```

The directory `/path/to/wikipedia-dpr-100w/`should be a directory containing the wikipedia-dpr-100w passages collection retrieved from [here](https://dl.fbaipublicfiles.com/dpr/wikipedia_split/psgs_w100.tsv.gz).

For additional details, see explanation of [common indexing options](../../docs/common-indexing-options.md).

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.
The regression experiments here evaluate on the test sets of Natural Questions, TriviaQA, SQuAD, and WebQuestions.

After indexing has completed, you should be able to perform retrieval as follows:

```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wikipedia-dpr-100w/ \
  -topics tools/topics-and-qrels/topics.dpr.nq.test.txt \
  -topicReader DprNq \
  -output runs/run.wikipedia-dpr-100w.bm25.topics.dpr.nq.test.txt \
  -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wikipedia-dpr-100w/ \
  -topics tools/topics-and-qrels/topics.dpr.trivia.test.txt \
  -topicReader DprNq \
  -output runs/run.wikipedia-dpr-100w.bm25.topics.dpr.trivia.test.txt \
  -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wikipedia-dpr-100w/ \
  -topics tools/topics-and-qrels/topics.dpr.squad.test.txt \
  -topicReader DprJsonl \
  -output runs/run.wikipedia-dpr-100w.bm25.topics.dpr.squad.test.txt \
  -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wikipedia-dpr-100w/ \
  -topics tools/topics-and-qrels/topics.dpr.wq.test.txt \
  -topicReader DprJsonl \
  -output runs/run.wikipedia-dpr-100w.bm25.topics.dpr.wq.test.txt \
  -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wikipedia-dpr-100w/ \
  -topics tools/topics-and-qrels/topics.dpr.curated.test.txt \
  -topicReader DprJsonl \
  -output runs/run.wikipedia-dpr-100w.bm25.topics.dpr.curated.test.txt \
  -bm25 &
bin/run.sh io.anserini.search.SearchCollection \
  -index indexes/lucene-index.wikipedia-dpr-100w/ \
  -topics tools/topics-and-qrels/topics.nq.test.txt \
  -topicReader DprNq \
  -output runs/run.wikipedia-dpr-100w.bm25.topics.nq.test.txt \
  -bm25 &
```

The trec format will need to be converted to DPR's JSON format for evaluation:
```bash
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.wikipedia-dpr-100w/ \
  --topics dpr-nq-test \
  --input runs/run.wikipedia-dpr-100w.bm25.topics.dpr.nq.test.txt \
  --output runs/run.wikipedia-dpr-100w.bm25.topics.dpr.nq.test.txt.json \
 &
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.wikipedia-dpr-100w/ \
  --topics dpr-trivia-test \
  --input runs/run.wikipedia-dpr-100w.bm25.topics.dpr.trivia.test.txt \
  --output runs/run.wikipedia-dpr-100w.bm25.topics.dpr.trivia.test.txt.json \
 &
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.wikipedia-dpr-100w/ \
  --topics dpr-squad-test \
  --input runs/run.wikipedia-dpr-100w.bm25.topics.dpr.squad.test.txt \
  --output runs/run.wikipedia-dpr-100w.bm25.topics.dpr.squad.test.txt.json \
 &
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.wikipedia-dpr-100w/ \
  --topics dpr-wq-test \
  --input runs/run.wikipedia-dpr-100w.bm25.topics.dpr.wq.test.txt \
  --output runs/run.wikipedia-dpr-100w.bm25.topics.dpr.wq.test.txt.json \
 &
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.wikipedia-dpr-100w/ \
  --topics dpr-curated-test \
  --input runs/run.wikipedia-dpr-100w.bm25.topics.dpr.curated.test.txt \
  --output runs/run.wikipedia-dpr-100w.bm25.topics.dpr.curated.test.txt.json \
  --regex &
python -m pyserini.eval.convert_trec_run_to_dpr_retrieval_run \
  --index indexes/lucene-index.wikipedia-dpr-100w/ \
  --topics nq-test \
  --input runs/run.wikipedia-dpr-100w.bm25.topics.nq.test.txt \
  --output runs/run.wikipedia-dpr-100w.bm25.topics.nq.test.txt.json \
 &
```

Evaluation can be performed using scripts from pyserini:

```bash
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.wikipedia-dpr-100w.bm25.topics.dpr.nq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.wikipedia-dpr-100w.bm25.topics.dpr.nq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.wikipedia-dpr-100w.bm25.topics.dpr.trivia.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.wikipedia-dpr-100w.bm25.topics.dpr.trivia.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.wikipedia-dpr-100w.bm25.topics.dpr.squad.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.wikipedia-dpr-100w.bm25.topics.dpr.squad.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.wikipedia-dpr-100w.bm25.topics.dpr.wq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.wikipedia-dpr-100w.bm25.topics.dpr.wq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.wikipedia-dpr-100w.bm25.topics.dpr.curated.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.wikipedia-dpr-100w.bm25.topics.dpr.curated.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 20 --retrieval runs/run.wikipedia-dpr-100w.bm25.topics.nq.test.txt.json
python -m pyserini.eval.evaluate_dpr_retrieval --topk 100 --retrieval runs/run.wikipedia-dpr-100w.bm25.topics.nq.test.txt.json
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

| **top_20_accuracy**                                                                                          | **BM25 (default parameters)**|
|:-------------------------------------------------------------------------------------------------------------|-----------|
| [DPR: Natural Questions Test](https://github.com/facebookresearch/DPR)                                       | 0.6294    |
| [DPR: TriviaQA Test](https://github.com/facebookresearch/DPR)                                                | 0.7641    |
| [DPR: SQuAD Test](https://github.com/facebookresearch/DPR)                                                   | 0.7109    |
| [DPR: WebQuestions Test](https://github.com/facebookresearch/DPR)                                            | 0.6240    |
| [DPR: CuratedTREC Test](https://github.com/facebookresearch/DPR)                                             | 0.8069    |
| [EfficientQA: Natural Questions Test](https://efficientqa.github.io/)                                        | 0.6399    |
| **top_100_accuracy**                                                                                         | **BM25 (default parameters)**|
| [DPR: Natural Questions Test](https://github.com/facebookresearch/DPR)                                       | 0.7825    |
| [DPR: TriviaQA Test](https://github.com/facebookresearch/DPR)                                                | 0.8315    |
| [DPR: SQuAD Test](https://github.com/facebookresearch/DPR)                                                   | 0.8184    |
| [DPR: WebQuestions Test](https://github.com/facebookresearch/DPR)                                            | 0.7549    |
| [DPR: CuratedTREC Test](https://github.com/facebookresearch/DPR)                                             | 0.8991    |
| [EfficientQA: Natural Questions Test](https://efficientqa.github.io/)                                        | 0.7922    |

## Reproduction Log[*](../../docs/reproducibility.md)

To add to this reproduction log, modify [this template](../../src/main/resources/docgen/templates/wikipedia-dpr-100w-bm25.template) and run `bin/build.sh` to rebuild the documentation.
