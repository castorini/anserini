# Anserini: Guide to Working with the MS MARCO V2 Collections

This guide presents information for working with V2 of the MS MARCO passage and document test collections, available [here](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html).

If you're having issues downloading the collection via `wget`, try using [AzCopy](https://docs.microsoft.com/en-us/azure/storage/common/storage-use-azcopy-v10).

## Passage Collection

Download and unpack the collection into `collections/`:
Indexing the passage collection, which is 20 GB compressed:

```
sh target/appassembler/bin/IndexCollection -collection MsMarcoPassageV2Collection \
 -generator DefaultLuceneDocumentGenerator -threads 18 \
 -input collections/msmarco_v2_passage \
 -index indexes/msmarco-passage-v2 \
 -storePositions -storeDocvectors -storeRaw
```

Adjust `-threads` as appropriate.
The above configuration, on a 2017 iMac Pro with SSD, takes around 30min.

The complete index occupies 72 GB (138,364,198 passages).
It's big because it includes postions (for phrase queries), document vectors (for relevance feedback), and a complete copy of the collection itself.
The index size can be reduced by removing the options `-storePositions`, `-storeDocvectors`, `-storeRaw` as appropriate.
For reference:

+ Without any of the three above option, index size reduces to 12 GB.
+ With just `-storeRaw`, index size reduces to 47 GB. This setting contains the raw JSON document, which makes it suitable for use as first-stage retrieval to support downstream rerankers. Bloat compared to compressed size of raw collection is due to support for per-document random access.

Download the queries and qrels:

```
wget https://msmarco.blob.core.windows.net/msmarcoranking/passv2_dev_queries.tsv -P collections/
wget https://msmarco.blob.core.windows.net/msmarcoranking/passv2_dev_qrels.tsv -P collections/
wget https://msmarco.blob.core.windows.net/msmarcoranking/passv2_dev2_queries.tsv -P collections/
wget https://msmarco.blob.core.windows.net/msmarcoranking/passv2_dev2_qrels.tsv -P collections/
```

Perform runs on the dev queries (both sets):

```
target/appassembler/bin/SearchCollection -index indexes/msmarco-passage-v2 \
 -topicreader TsvInt -topics collections/passv2_dev_queries.tsv \
 -output runs/run.msmarco-passage-v2.dev1.txt -bm25 -hits 100

target/appassembler/bin/SearchCollection -index indexes/msmarco-passage-v2 \
 -topicreader TsvInt -topics collections/passv2_dev2_queries.tsv \
 -output runs/run.msmarco-passage-v2.dev2.txt -bm25 -hits 100
```

Evaluation:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -c -m map -m recall.100 -m recip_rank collections/passv2_dev_qrels.tsv runs/run.msmarco-passage-v2.dev1.txt
map                   	all	0.0709
recip_rank            	all	0.0719
recall_100            	all	0.3397

$ tools/eval/trec_eval.9.0.4/trec_eval -c -m map -m recall.100 -m recip_rank collections/passv2_dev2_qrels.tsv runs/run.msmarco-passage-v2.dev2.txt
map                   	all	0.0794
recip_rank            	all	0.0802
recall_100            	all	0.3459
```

## Passage Collection (Augmented)

We have constructed an augmented version of the passage collection; we haven't figured out how to distribute it yet, so these experiments are only reproducible on a Waterloo machine (`orca` to be exact):

Indexing:

```
sh target/appassembler/bin/IndexCollection -collection MsMarcoPassageV2Collection \
 -generator DefaultLuceneDocumentGenerator -threads 10 \
 -input /store/collections/msmarco/msmarco_v2_passage_augmented \
 -index indexes/msmarco-passage-v2-augmented \
 -storePositions -storeDocvectors -storeRaw
```

Perform runs on the dev queries (both sets):

```
target/appassembler/bin/SearchCollection -index indexes/msmarco-passage-v2-augmented \
 -topicreader TsvInt -topics collections/passv2_dev_queries.tsv \
 -output runs/run.msmarco-passage-v2-augmented.dev1.txt -bm25 -hits 100

target/appassembler/bin/SearchCollection -index indexes/msmarco-passage-v2-augmented \
 -topicreader TsvInt -topics collections/passv2_dev2_queries.tsv \
 -output runs/run.msmarco-passage-v2-augmented.dev2.txt -bm25 -hits 100
```

Evaluation:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -c -m map -m recall.100 -m recip_rank collections/passv2_dev_qrels.tsv runs/run.msmarco-passage-v2-augmented.dev1.txt
map                   	all	0.0863
recip_rank            	all	0.0872
recall_100            	all	0.4030

$ tools/eval/trec_eval.9.0.4/trec_eval -c -m map -m recall.100 -m recip_rank collections/passv2_dev2_qrels.tsv runs/run.msmarco-passage-v2-augmented.dev2.txt
map                   	all	0.0904
recip_rank            	all	0.0917
recall_100            	all	0.4159
```

## Document Collection

Download and unpack the collection into `collections/`:
Indexing the document collection, which is 32 GB compressed:

```
sh target/appassembler/bin/IndexCollection -collection MsMarcoDocV2Collection \
 -generator DefaultLuceneDocumentGenerator -threads 18 \
 -input collections/msmarco_v2_doc \
 -index indexes/msmarco-doc-v2 \
 -storePositions -storeDocvectors -storeRaw
```

Same instructions as above.
On the same machine, indexing takes around 40min.
Complete index occupies 134 GB (11,959,635 documents).
Index size can be reduced by removing the options `-storePositions`, `-storeDocvectors`, `-storeRaw` as appropriate.
For reference:

+ Without any of the three above option, index size reduces to 9.4 GB.
+ With just `-storeRaw`, index size reduces to 73 GB. This setting contains the raw JSON document, which makes it suitable for use as first-stage retrieval to support downstream rerankers. Bloat compared to compressed size of raw collection is due to support for per-document random access; evidently, the JSON docs don't compress well.

Each "document" in the index comprises the url, title, headings, and body fields concatenated together.

Download the queries and qrels:

```bash
wget https://msmarco.blob.core.windows.net/msmarcoranking/docv2_dev_queries.tsv -P collections/
wget https://msmarco.blob.core.windows.net/msmarcoranking/docv2_dev_qrels.tsv -P collections/
wget https://msmarco.blob.core.windows.net/msmarcoranking/docv2_dev2_queries.tsv -P collections/
wget https://msmarco.blob.core.windows.net/msmarcoranking/docv2_dev2_qrels.tsv -P collections/
```

Perform runs on the dev queries (both sets):

```
target/appassembler/bin/SearchCollection -index indexes/msmarco-doc-v2 \
 -topicreader TsvInt -topics collections/docv2_dev_queries.tsv \
 -output runs/run.msmarco-doc-v2.dev1.txt -bm25 -hits 100

target/appassembler/bin/SearchCollection -index indexes/msmarco-doc-v2 \
 -topicreader TsvInt -topics collections/docv2_dev2_queries.tsv \
 -output runs/run.msmarco-doc-v2.dev2.txt -bm25 -hits 100
```

Evaluation:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -c -m map -m recall.100 -m recip_rank collections/docv2_dev_qrels.tsv runs/run.msmarco-doc-v2.dev1.txt
map                   	all	0.1552
recip_rank            	all	0.1572
recall_100            	all	0.5956

$ tools/eval/trec_eval.9.0.4/trec_eval -c -m map -m recall.100 -m recip_rank collections/docv2_dev2_qrels.tsv runs/run.msmarco-doc-v2.dev2.txt
map                   	all	0.1639
recip_rank            	all	0.1659
recall_100            	all	0.5970
```

## Document Collection (Segmented)

We have constructed a segmented version of the document collection; we haven't figured out how to distribute it yet, so these experiments are only reproducible on a Waterloo machine (`orca` to be exact):

Indexing:

```
sh target/appassembler/bin/IndexCollection -collection MsMarcoDocV2Collection \
 -generator DefaultLuceneDocumentGenerator -threads 10 \
 -input /store/collections/msmarco/msmarco_v2_doc_segmented \
 -index indexes/msmarco-doc-v2-segmented
```

Each "document" in the index comprises the url, title, headings, and segment fields concatenated together.

Perform runs on the dev queries (both sets):

```
target/appassembler/bin/SearchCollection -index indexes/msmarco-doc-v2-segmented \
  -topicreader TsvInt -topics collections/docv2_dev_queries.tsv -output runs/run.msmarco-doc-v2-segmented.dev1.txt \
  -bm25 -hits 1000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100

target/appassembler/bin/SearchCollection -index indexes/msmarco-doc-v2-segmented \
  -topicreader TsvInt -topics collections/docv2_dev2_queries.tsv -output runs/run.msmarco-doc-v2-segmented.dev2.txt \
  -bm25 -hits 1000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 100
```

Evaluation:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -c -m map -m recall.100 -m recip_rank collections/docv2_dev_qrels.tsv runs/run.msmarco-doc-v2-segmented.dev1.txt
map                   	all	0.1875
recip_rank            	all	0.1896
recall_100            	all	0.6555

$ tools/eval/trec_eval.9.0.4/trec_eval -c -m map -m recall.100 -m recip_rank collections/docv2_dev2_qrels.tsv runs/run.msmarco-doc-v2-segmented.dev2.txt
map                   	all	0.1903
recip_rank            	all	0.1930
recall_100            	all	0.6629
```

## Reproduction Log[*](reproducibility.md)

+ Results reproduced by [@ronakice](https://github.com/ronakice) on 2021-06-25 (commit [`ce35d61`](https://github.com/castorini/anserini/commit/ce35d61455d5943e164e31880e517ce091fded66))
+ Results reproduced by [@crystina-z](https://github.com/crystina-z) on 2021-06-25 (commit [`ce35d61`](https://github.com/castorini/anserini/commit/ce35d61455d5943e164e31880e517ce091fded66))
+ Results reproduced by [@spacemanidol](https://github.com/spacemanidol) on 2021-06-28 (commit [`ce35d61`](https://github.com/castorini/anserini/commit/ce35d61455d5943e164e31880e517ce091fded66))
+ Results reproduced by [@crystina-z](https://github.com/crystina-z) on 2021-06-25 (commit [`dbc71ee`](https://github.com/castorini/anserini/commit/dbc71ee51fc7dbcdcb9118c9f7ad554b8b753a27))
