# Anserini: Guide to Working with the MS MARCO V2 Collections

This guide presents information for working with V2 of the MS MARCO passage and document test collections.

If you're having issues downloading the collection via `wget`, try using [AzCopy](https://docs.microsoft.com/en-us/azure/storage/common/storage-use-azcopy-v10).

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

Perform a run on the dev queries:

```
target/appassembler/bin/SearchCollection -index indexes/msmarco-doc-v2 \
 -topicreader TsvInt -topics collections/docv2_dev_queries.tsv \
 -output runs/run.msmarco-doc-v2.dev.txt \
 -bm25 -hits 100
```

Evaluation:

```bash
$ tools/eval/trec_eval.9.0.4/trec_eval -c -m map -m recall.100 -m recip_rank collections/docv2_dev_qrels.uniq.tsv runs/run.msmarco-doc-v2.dev.txt
map                   	all	0.1552
recip_rank            	all	0.1572
recall_100            	all	0.5956
```

Currently (06/26/2021), indexing doesn't work in [Pyserini](http://pyserini.io/) yet (will work once we push next release).
However, Pyserini _can_ work directly with an index built in Java by Anserini; just pass the index path to `SimpleSearcher`.


## Reproduction Log[*](reproducibility.md)
+ Results reproduced by [@crystina-z](https://github.com/crystina-z) on 2021-06-25 (commit [`ce35d61`](https://github.com/castorini/anserini/commit/ce35d61455d5943e164e31880e517ce091fded66))
+  Results reproduced by [@spacemanidol](https://github.com/spacemanidol) on 2021-06-28 (commit [`ce35d61`](https://github.com/castorini/anserini/commit/ce35d61455d5943e164e31880e517ce091fded66))
