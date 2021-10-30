# Anserini: BM25 Baselines for the MS MARCO V2 Collections

This guide contains instructions for running baselines on the MS MARCO V2 passage and document test collections, available [here](https://microsoft.github.io/msmarco/TREC-Deep-Learning.html).
Note that Pyserini provides a [comparable guide](https://github.com/castorini/pyserini/blob/msmarco-v2/docs/experiments-msmarco-v2.md), so if you don't like Java, you can get the same results from Python.

If you're having issues downloading the collection via `wget`, try using [AzCopy](https://docs.microsoft.com/en-us/azure/storage/common/storage-use-azcopy-v10).
For example, to download passage collection:

```
azcopy copy https://msmarco.blob.core.windows.net/msmarcoranking/msmarco_v2_passage.tar ./collections
```
The speedup using `azcopy` is significant compared to `wget`, but the actual downloading time will vary based on your location as well as many other factors.
Just download the collections; queries and qrels are already included in this repo.

## Passage Collection

Download and unpack the collection into `collections/`.
Here's the indexing command for the passage collection, which is 21 GB compressed:

```
sh target/appassembler/bin/IndexCollection -collection MsMarcoV2PassageCollection \
 -generator DefaultLuceneDocumentGenerator -threads 18 \
 -input collections/msmarco_v2_passage \
 -index indexes/lucene-index.msmarco-v2-passage \
 -storePositions -storeDocvectors -storeRaw
```

Adjust `-threads` as appropriate.
The above configuration, on a 2017 iMac Pro with SSD, takes around 30 minutes.

The complete index occupies 72 GB (138,364,198 passages).
It's big because it includes postions (for phrase queries), document vectors (for relevance feedback), and a complete copy of the collection itself.
The index size as well as indexing time can be reduced by removing the options `-storePositions`, `-storeDocvectors`, `-storeRaw` as appropriate.
For reference:

+ Without any of the three above option, index size reduces to 12 GB.
+ With just `-storeRaw`, index size reduces to 47 GB. This setting contains the raw JSON document, which makes it suitable for use as first-stage retrieval to support downstream rerankers. Bloat compared to the compressed size of the raw collection is due to support for per-document random access.

Perform runs on the dev queries (both sets):

```
target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-passage \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt \
 -output runs/run.msmarco-v2-passage.dev.txt -bm25 -hits 1000

target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-passage \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt \
 -output runs/run.msmarco-v2-passage.dev2.txt -bm25 -hits 1000
```

Evaluation:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.dev.txt
map                   	all	0.0709
recip_rank            	all	0.0719

$ tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100,1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage.dev.txt
recall_100            	all	0.3397
recall_1000           	all	0.5733

$ tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.dev2.txt
map                   	all	0.0794
recip_rank            	all	0.0802

$ tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100,1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage.dev2.txt
recall_100            	all	0.3459
recall_1000           	all	0.5839
```

Note that we evaluate MAP and MRR at a cutoff of 100 hits to match the official evaluation metrics.
However, we measure recall at both 100 and 1000 hits; the latter is a common setting for reranking.

## Passage Collection (Augmented)

The passage corpus contains only passage texts; it is missing additional information such as the title of the page the passage comes from and the URL of the page.
This information is available in the document collection, and we have written [a Python script](https://github.com/castorini/pyserini/blob/master/scripts/msmarco_v2/augment_passage_corpus.py) to augment the passage collection with these additional fields (specifically `url`, `title`, `headings`).

For convenience, this augmented corpus is being distributed as part of the MS MARCO dataset as part of "additional resources", `msmarco_v2_passage_augmented.tar` (21 GB, MD5 checksum of `69acf3962608b614dbaaeb10282b2ab8`).
The tarball can be downloaded [here](https://msmarco.blob.core.windows.net/msmarcoranking/msmarco_v2_passage_augmented.tar).
Once again, we recommend downloading with [AzCopy](https://docs.microsoft.com/en-us/azure/storage/common/storage-use-azcopy-v10).

Indexing this augmented collection:

```
sh target/appassembler/bin/IndexCollection -collection MsMarcoV2PassageCollection \
 -generator DefaultLuceneDocumentGenerator -threads 18 \
 -input collections/msmarco_v2_passage_augmented \
 -index indexes/lucene-index.msmarco-v2-passage-augmented \
 -storePositions -storeDocvectors -storeRaw
```

There are a total of 138,364,198 passages in the collection (exactly the same as the original passage collection).
Each "document" in the index comprises the url, title, headings, and passage fields concatenated together.
With the above indexing configuration, the index size comes to 162 GB.
However, the index size can be reduced by playing with the indexing options discussed above.
For example, with just the `-storeRaw` option, which supports bag-of-words first-stage retrieval with stored raw documents that can be fetched and passed to a downstream reranker, the index size comes out to 95 GB.

Perform runs on the dev queries (both sets):

```
target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-passage-augmented \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev.txt \
 -output runs/run.msmarco-v2-passage-augmented.dev.txt -bm25 -hits 1000

target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-passage-augmented \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-passage.dev2.txt \
 -output runs/run.msmarco-v2-passage-augmented.dev2.txt -bm25 -hits 1000
```

Evaluation:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-augmented.dev.txt
map                   	all	0.0863
recip_rank            	all	0.0872

$ tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100,1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt runs/run.msmarco-v2-passage-augmented.dev.txt
recall_100            	all	0.4030
recall_1000           	all	0.6925

$ tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-augmented.dev2.txt
map                   	all	0.0904
recip_rank            	all	0.0917

$ tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100,1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt runs/run.msmarco-v2-passage-augmented.dev2.txt
recall_100            	all	0.4159
recall_1000           	all	0.6933
```

We see that adding these additional fields gives a nice bump to effectiveness.

## Document Collection

Download and unpack the collection into `collections/`.
Here's the indexing command for the document collection, which is 33 GB compressed:

```
sh target/appassembler/bin/IndexCollection -collection MsMarcoV2DocCollection \
 -generator DefaultLuceneDocumentGenerator -threads 18 \
 -input collections/msmarco_v2_doc \
 -index indexes/lucene-index.msmarco-v2-doc \
 -storePositions -storeDocvectors -storeRaw
```

Same instructions as above.
On the same machine as described above, indexing takes around 40 minutes.
The complete index with the above configuration occupies 134 GB (11,959,635 documents).
Index size can be reduced by removing the options `-storePositions`, `-storeDocvectors`, `-storeRaw` as appropriate.
For reference:

+ Without any of the three above options, index size reduces to 9.4 GB.
+ With just `-storeRaw`, index size reduces to 73 GB. This setting stores the raw JSON documents, which makes it suitable for use as first-stage retrieval to support downstream rerankers. Bloat compared to the compressed size of the raw collection is due to support for per-document random access; evidently, the JSON documents don't compress well.

Each "document" in the index comprises the url, title, headings, and body fields concatenated together.

Perform runs on the dev queries (both sets):

```
target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-doc \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.txt \
 -output runs/run.msmarco-v2-doc.dev.txt -bm25 -hits 1000

target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-doc \
 -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt \
 -output runs/run.msmarco-v2-doc.dev2.txt -bm25 -hits 1000
```

Evaluation:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.dev.txt
map                   	all	0.1552
recip_rank            	all	0.1572

$ tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100,1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc.dev.txt
recall_100            	all	0.5956
recall_1000           	all	0.8054

$ tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.dev2.txt
map                   	all	0.1639
recip_rank            	all	0.1659

$ tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100,1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc.dev2.txt
recall_100            	all	0.5970
recall_1000           	all	0.8029
```

Similar to the passage case, we evaluate MAP and MRR at a cutoff of 100 hits to match the official evaluation metrics.
However, we measure recall at both 100 and 1000 hits; the latter is a common setting for reranking.

## Document Collection (Segmented)

A well-known limitation of transformer-based rerankers is that they are unable to perform inference over long segments of text all at once.
One standard solution to address this shortcoming is the MaxP technique of [Dai and Callan (2019)](https://dl.acm.org/doi/10.1145/3331184.3331303): perform document retrieval, segment each document into passages, apply inference to each passage independently, then take the passage with the highest score as the representative of the document for ranking.

An alternative to this approach is to segment the collection _prior_ to indexing (i.e., each passage is indexed separately as a "document"), perform retrieval over the passages, and then feed these passages directly to a transformer-based reranker.
We present exactly such a baseline for first-stage retrieval here.

We segmented the document collection with [this Python script](https://github.com/castorini/pyserini/blob/master/scripts/msmarco_v2/segment_docs.py).
As a summary, the script trims each document to 10k characters, and then applies a 10-sentence sliding window with a 5-sentence stride to generate the passages.
This approach is similar to the technique reported in [Pradeep et al. (2021)](https://arxiv.org/abs/2101.05667), which has been demonstrated to be effective.
Sentence chunking is performed with spaCy (v2.3.5); the version is important if you want to _exactly_ reproduce our results from scratch with the Python script.
We have also experimented with _not_ trimming each document to the first 10k characters; the collection becomes much bigger and the results become worse on the dev queries below.

For convenience, this segmented corpus is being distributed as part of the MS MARCO dataset as part of "additional resources", `msmarco_v2_doc_segmented.tar` (26 GB, MD5 checksum of `f18c3a75eb3426efeb6040dca3e885dc`).
The tarball can be downloaded [here](https://msmarco.blob.core.windows.net/msmarcoranking/msmarco_v2_doc_segmented.tar).
Once again, we recommend downloading with [AzCopy](https://docs.microsoft.com/en-us/azure/storage/common/storage-use-azcopy-v10).

The segmented document collection can be indexed with the following command:

```
sh target/appassembler/bin/IndexCollection -collection MsMarcoV2DocCollection \
 -generator DefaultLuceneDocumentGenerator -threads 18 \
 -input collections/msmarco_v2_doc_segmented \
 -index indexes/lucene-index.msmarco-v2-doc-segmented \
 -storePositions -storeDocvectors -storeRaw
```

There are a total of 124,131,414 "documents" in the collection.
Each "document" comprises the url, title, headings, and segment fields concatenated together.
With the above indexing configuration, the index size comes to 226 GB.
However, the index size can be reduced by playing with the indexing options discussed above.
For example, with just the `-storeRaw` option, which supports bag-of-words first-stage retrieval with stored raw documents that can be fetched and passed to a downstream reranker, the index size comes out to 124 GB.

Perform runs on the dev queries (both sets):

```
target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-doc-segmented \
  -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev.txt \
  -output runs/run.msmarco-v2-doc-segmented.dev.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000

target/appassembler/bin/SearchCollection -index indexes/lucene-index.msmarco-v2-doc-segmented \
  -topicreader TsvInt -topics src/main/resources/topics-and-qrels/topics.msmarco-v2-doc.dev2.txt \
  -output runs/run.msmarco-v2-doc-segmented.dev2.txt \
  -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter "#" -selectMaxPassage.hits 1000
```

Evaluation:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented.dev.txt
map                   	all	0.1875
recip_rank            	all	0.1896

$ tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100,1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt runs/run.msmarco-v2-doc-segmented.dev.txt
recall_100            	all	0.6555
recall_1000           	all	0.8542

$ tools/eval/trec_eval.9.0.4/trec_eval -c -M 100 -m map -m recip_rank src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc-segmented.dev2.txt
map                   	all	0.1903
recip_rank            	all	0.1930

$ tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100,1000 src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt runs/run.msmarco-v2-doc-segmented.dev2.txt
recall_100            	all	0.6629
recall_1000           	all	0.8549
```

As we can see, even as first-stage retrieval (i.e., without reranking), retrieval over the segmented collection is more effective than retrieval over the original document collection.

## Reproduction Log[*](reproducibility.md)

+ Results reproduced by [@ronakice](https://github.com/ronakice) on 2021-06-25 (commit [`ce35d61`](https://github.com/castorini/anserini/commit/ce35d61455d5943e164e31880e517ce091fded66))
+ Results reproduced by [@crystina-z](https://github.com/crystina-z) on 2021-06-25 (commit [`ce35d61`](https://github.com/castorini/anserini/commit/ce35d61455d5943e164e31880e517ce091fded66))
+ Results reproduced by [@spacemanidol](https://github.com/spacemanidol) on 2021-06-28 (commit [`ce35d61`](https://github.com/castorini/anserini/commit/ce35d61455d5943e164e31880e517ce091fded66))
+ Results reproduced by [@crystina-z](https://github.com/crystina-z) on 2021-06-25 (commit [`dbc71ee`](https://github.com/castorini/anserini/commit/dbc71ee51fc7dbcdcb9118c9f7ad554b8b753a27))
+ Results reproduced by [@t-k-](https://github.com/t-k-) on 2021-07-29 (commit [`52b76f6`](https://github.com/castorini/anserini/commit/52b76f63b163036e8fad1a6e1b10b431b4ddd06c))
