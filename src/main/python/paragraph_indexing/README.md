# Paragraph Indexing



## Segment

Segment each raw document into paragraph and dump out into seperate .json file named with DOCID in json format, e.g.

```
[
    {
        'id':'{$DOCNO}.0001',
        'content':'content0001'
	},
    {
        'id':'{$DOCNO}.0002',
        'content':'content0001'
    }
]
```

This is done by calling `seg_${collection}.py`, where supported collections so far are `robust04` and `core17`



### Example:

Run 

```
python seg_robust04.py \
 --input lucene-index.robust04.pos+docvectors+rawdocs.allDocids.txt.output.tar.gz \
 --output robust04.paragraphs/
```

All documents will be segmented into paragraph and stored in folder `./robust04.paragraphs/`



### Input file

The input raw documents should be a `tar.gz` file containing each document in a seperate file named as DOCID. This file can be generated through following command (e.g Robust04)

Suppose you're under Anserini directory. First indexing

```bash
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection \
 -input /path/to/disk45/ -generator JsoupGenerator \
 -index lucene-index.robust04.pos+docvectors+rawdocs -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -optimize \
 >& log.robust04.pos+docvectors+rawdocs &
```

and then dump the raw documents by the following two steps:
1. dump all docids of the collection
2. feed the docids file to dump raw documents

```bash
sh target/appassembler/bin/IndexUtils \
 -index lucene-index.robust04.pos+docvectors+rawdocs \
 -dumpAllDocids NONE &&
sh target/appassembler/bin/IndexUtils \
 -index lucene-index.robust04.pos+docvectors+rawdocs \
 -dumpRawDocs lucene-index.robust04.pos+docvectors+rawdocs.allDocids.txt
```

and the output `tar.gz` file will be named as 

```
lucene-index.robust04.pos+docvectors+rawdocs.allDocids.txt.output.tar.gz
```



## Paragraph Indexing

The json file can be indexed using `JsonCollection` in Anserini. Run

```bash
sh target/appassembler/bin/IndexCollection -collection JsonCollection \
 -input /path/to/robust04.paragraphs -generator LuceneDocumentGenerator \
 -index lucene-index.robust04.paragraphs.pos+docvectors+rawdocs -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -optimize  &&
```

to index each paragraph for Robust04 collection. `-input` should be the output folder of the paragraph segmentation
