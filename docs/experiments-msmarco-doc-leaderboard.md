# Anserini: Baselines for MS MARCO Document Leaderboard

The page provides instructions for replicating Anserini baseline runs for the [MS MARCO Document Leaderboard](https://microsoft.github.io/MSMARCO-Document-Ranking-Submissions/leaderboard/).
Prebuilt indexes can be found [here](https://git.uwaterloo.ca/jimmylin/anserini-indexes/).
For convenience, we use Pyserini's [feature to automatically download prebuilt indexes](https://github.com/castorini/pyserini/blob/master/docs/prebuilt-indexes.md) to fetch the right indexes, which are downloaded to `~/.cache/pyserini/indexes/`.

Note that we are only able to evaluate on the dev queries.
Scores on the test topics are only available via submission to the official leaderboard.

## BM25 Baselines

We have two BM25 baselines, a "per-document" configuration (indexing each document individually, as you'd expect) and a "per-passage" configuration (where we break up each document into passages and index each passage separately, see [here](https://github.com/castorini/docTTTTTquery#per-passage-expansion)).

To fetch the indexes:

```
python -c "from pyserini.search import SimpleSearcher; SimpleSearcher.from_prebuilt_index('msmarco-doc')"
python -c "from pyserini.search import SimpleSearcher; SimpleSearcher.from_prebuilt_index('msmarco-doc-per-passage')"
```

### Per-Document

Run with **per-document** configuration, BM25 default parameters:

```bash
mkdir runs/bm25base/

sh target/appassembler/bin/SearchMsmarco -hits 100 -k1 0.9 -b 0.4 -threads 9 \
 -index ~/.cache/pyserini/indexes/index-msmarco-doc-20201117-f87c94.ac747860e7a37aed37cc30ed3990f273 \
 -queries src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt -output runs/bm25base/dev.txt &

sh target/appassembler/bin/SearchMsmarco -hits 100 -k1 0.9 -b 0.4 -threads 9 \
 -index ~/.cache/pyserini/indexes/index-msmarco-doc-20201117-f87c94.ac747860e7a37aed37cc30ed3990f273 \
 -queries src/main/resources/topics-and-qrels/topics.msmarco-doc.test.txt -output runs/bm25base/eval.txt &
```

Evaluation:

```bash
$ python tools/scripts/msmarco/msmarco_doc_eval.py --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt --run runs/bm25base/dev.txt
#####################
MRR @100: 0.23005723505603573
QueriesRanked: 5193
#####################
```

Run with **per-document** configuration, BM25 tuned parameters, optimized for recall@100 (`k1=4.46`, `b=0.82`):

```bash
mkdir runs/bm25tuned/

sh target/appassembler/bin/SearchMsmarco -hits 100 -k1 4.46 -b 0.82 -threads 9 \
 -index ~/.cache/pyserini/indexes/index-msmarco-doc-20201117-f87c94.ac747860e7a37aed37cc30ed3990f273 \
 -queries src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt -output runs/bm25tuned/dev.txt &

sh target/appassembler/bin/SearchMsmarco -hits 100 -k1 4.46 -b 0.82 -threads 9 \
 -index ~/.cache/pyserini/indexes/index-msmarco-doc-20201117-f87c94.ac747860e7a37aed37cc30ed3990f273 \
 -queries src/main/resources/topics-and-qrels/topics.msmarco-doc.test.txt -output runs/bm25tuned/eval.txt &
```

Evaluation:

```bash
$ python tools/scripts/msmarco/msmarco_doc_eval.py --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt --run runs/bm25tuned/dev.txt
#####################
MRR @100: 0.2770296928568702
QueriesRanked: 5193
#####################
```

### Per-Passage

Run with **per-passage** configuration, BM25 default parameters:

```bash
mkdir runs/bm25pbase/

target/appassembler/bin/SearchCollection -topicreader TsvString -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -index ~/.cache/pyserini/indexes/index-msmarco-doc-per-passage-20201204-f50dcc.797367406a7542b649cefa6b41cf4c33/ \
  -output runs/bm25pbase/dev.trec.txt \
  -bm25 -bm25.k1 0.9 -bm25.b 0.4 -hits 1000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100 &

python tools/scripts/msmarco/convert_trec_to_msmarco_run.py --input runs/bm25pbase/dev.trec.txt --output runs/bm25pbase/dev.txt

target/appassembler/bin/SearchCollection -topicreader TsvString -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.test.txt \
  -index ~/.cache/pyserini/indexes/index-msmarco-doc-per-passage-20201204-f50dcc.797367406a7542b649cefa6b41cf4c33/ \
  -output runs/bm25pbase/eval.trec.txt \
  -bm25 -bm25.k1 0.9 -bm25.b 0.4 -hits 1000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100 &

python tools/scripts/msmarco/convert_trec_to_msmarco_run.py --input runs/bm25pbase/eval.trec.txt --output runs/bm25pbase/eval.txt
```

Evaluation:

```bash
$ python tools/scripts/msmarco/msmarco_doc_eval.py --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt --run runs/bm25pbase/dev.txt
#####################
MRR @100: 0.2682349308946578
QueriesRanked: 5193
#####################
```

Run with **per-passage** configuration, BM25 tuned parameters, optimized for recall@100 (`k1=2.16`, `b=0.61`):

```bash
mkdir runs/bm25ptuned/

target/appassembler/bin/SearchCollection -topicreader TsvString -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -index ~/.cache/pyserini/indexes/index-msmarco-doc-per-passage-20201204-f50dcc.797367406a7542b649cefa6b41cf4c33/ \
  -output runs/bm25ptuned/dev.trec.txt \
  -bm25 -bm25.k1 2.16 -bm25.b 0.61 -hits 1000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100 &

python tools/scripts/msmarco/convert_trec_to_msmarco_run.py --input runs/bm25ptuned/dev.trec.txt --output runs/bm25ptuned/dev.txt

target/appassembler/bin/SearchCollection -topicreader TsvString -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.test.txt \
  -index ~/.cache/pyserini/indexes/index-msmarco-doc-per-passage-20201204-f50dcc.797367406a7542b649cefa6b41cf4c33/ \
  -output runs/bm25ptuned/eval.trec.txt \
  -bm25 -bm25.k1 2.16 -bm25.b 0.61 -hits 1000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100 &

python tools/scripts/msmarco/convert_trec_to_msmarco_run.py --input runs/bm25ptuned/eval.trec.txt --output runs/bm25ptuned/eval.txt
```

Evaluation:

```bash
$ python tools/scripts/msmarco/msmarco_doc_eval.py --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt --run runs/bm25ptuned/dev.txt
#####################
MRR @100: 0.2751202109946902
QueriesRanked: 5193
#####################
```

## Document Expansion Baselines

To fetch the indexes:

```
python -c "from pyserini.search import SimpleSearcher; SimpleSearcher.from_prebuilt_index('msmarco-doc-expanded-per-passage')"
python -c "from pyserini.search import SimpleSearcher; SimpleSearcher.from_prebuilt_index('msmarco-doc-expanded-per-doc')"
```

### Per-Document

Anserini's BM25 + doc2query-T5 expansion (per document), parameters tuned for recall@100 (k1=4.68, b=0.87):

```bash
mkdir runs/doc2query-t5-per-doc/

sh target/appassembler/bin/SearchMsmarco -hits 100 -k1 4.68 -b 0.87 -threads 9 \
 -index ~/.cache/pyserini/indexes/index-msmarco-doc-expanded-per-doc-20201126-1b4d0a.f7056191842ab77a01829cff68004782 \
 -queries src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt -output runs/doc2query-t5-per-doc/dev.txt &

sh target/appassembler/bin/SearchMsmarco -hits 100 -k1 4.68 -b 0.87 -threads 9 \
 -index ~/.cache/pyserini/indexes/index-msmarco-doc-expanded-per-doc-20201126-1b4d0a.f7056191842ab77a01829cff68004782 \
 -queries src/main/resources/topics-and-qrels/topics.msmarco-doc.test.txt -output runs/doc2query-t5-per-doc/eval.txt &
```

Evaluation:

```bash
$ python tools/scripts/msmarco/msmarco_doc_eval.py --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt --run runs/doc2query-t5-per-doc/dev.txt
#####################
MRR @100: 0.3265190296491929
QueriesRanked: 5193
#####################
```

### Per-Passage

Anserini's BM25 + doc2query-T5 expansion (per passage), parameters tuned for recall@100 (k1=2.56, b=0.59):

```bash
mkdir runs/doc2query-t5-per-passage/

target/appassembler/bin/SearchCollection -topicreader TsvString -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
  -index ~/.cache/pyserini/indexes/index-msmarco-doc-expanded-per-passage-20201126-1b4d0a.54ea30c64515edf3c3741291b785be53 \
  -output runs/doc2query-t5-per-passage/dev.trec.txt \
  -bm25 -bm25.k1 2.56 -bm25.b 0.59 -hits 1000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100 &

python tools/scripts/msmarco/convert_trec_to_msmarco_run.py --input runs/doc2query-t5-per-passage/dev.trec.txt --output runs/doc2query-t5-per-passage/dev.txt

target/appassembler/bin/SearchCollection -topicreader TsvString -topics src/main/resources/topics-and-qrels/topics.msmarco-doc.test.txt \
  -index ~/.cache/pyserini/indexes/index-msmarco-doc-expanded-per-passage-20201126-1b4d0a.54ea30c64515edf3c3741291b785be53 \
  -output runs/doc2query-t5-per-passage/eval.trec.txt \
  -bm25 -bm25.k1 2.56 -bm25.b 0.59 -hits 1000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100 &

python tools/scripts/msmarco/convert_trec_to_msmarco_run.py --input runs/doc2query-t5-per-passage/eval.trec.txt --output runs/doc2query-t5-per-passage/eval.txt
```

Note that the passage retrieval functionality is only available in `SearchCollection`; we use a simple script to convert back into MS MARCO format.

Evaluation:

```bash
$ python tools/scripts/msmarco/msmarco_doc_eval.py --judgments src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt --run runs/doc2query-t5-per-passage/dev.txt
#####################
MRR @100: 0.32081861579183746
QueriesRanked: 5193
#####################
```

## Replication Log

+ Results replicated by [@MXueguang](https://github.com/MXueguang) on 2021-01-06 (commit [`6674291`](https://github.com/castorini/anserini/commit/667429183323b15790a86ef186272216f92ffcbc))
+ Results replicated by [@ronakice](https://github.com/ronakice) on 2021-01-19 (commit [`5f5dba9`](https://github.com/castorini/anserini/commit/5f5dba948b5cbbf6f73010f2a11d4fe3cca445d6))
