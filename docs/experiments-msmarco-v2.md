# Anserini: Guide to Working with the MS MARCO V2 Collections

Indexing the passage collection:

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
Index size can be reduced by removing the options `-storePositions`, `-storeDocvectors`, `-storeRaw` as appropriate.

Indexing the document collection:

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

Perform a run on the dev queries:

```
target/appassembler/bin/SearchCollection -index indexes/msmarco-doc-v2 \
 -topicreader TsvInt -topics collections/docv2_dev_queries.tsv \
 -output runs/run.msmarco-doc-v2.dev.txt \
 -bm25 -hits 100
```

Evaluation:

```
$ tools/eval/trec_eval.9.0.4/trec_eval collections/docv2_dev_qrels.uniq.tsv runs/run.msmarco-doc-v2.dev.txt
runid                 	all	Anserini
num_q                 	all	4552
num_ret               	all	455200
num_rel               	all	4702
num_rel_ret           	all	2779
map                   	all	0.1552
gm_map                	all	0.0026
Rprec                 	all	0.0839
bpref                 	all	0.5956
recip_rank            	all	0.1572
```


## Reproduction Log[*](reproducibility.md)

