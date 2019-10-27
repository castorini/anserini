# Anserini: Regressions for [NTCIR-8 Monolingual Chinese](http://research.nii.ac.jp/ntcir/ntcir-ws8/ws-en.html)

This page documents regression experiments for [NTCIR-8 ACLIA (IR4QA subtask, Chinese monolingual topics)](http://research.nii.ac.jp/ntcir/ntcir-ws8/ws-en.html).
The description of the document collection can be found in the [NTCIR-8 data page](http://research.nii.ac.jp/ntcir/permission/ntcir-8/perm-en-ACLIA.html): Xinhua articles from 2002-2005, totalling 308,845 documents, from [LDC2007T38: Chinese Gigaword Third Edition](https://catalog.ldc.upenn.edu/LDC2007T38).
We build the index directly from the raw LDC data: `data/xin_cmn/xin_cmn_200[2-5]*` (48 files).

## Indexing

Typical indexing command:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCollection -input /path/to/ntcir8-zh \
 -index lucene-index.ntcir8-zh.pos+docvectors+rawdocs -generator LuceneDocumentGenerator -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs -language zh -uniqueDocid -optimize >& log.ntcir8-zh.pos+docvectors+rawdocs &
```

The directory `/path/to/ntcir8-zh/` should be a directory containing the collection, 48 gzipped files matching the pattern `xin_cmn_200[2-5]*` from LDC2007T38.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/).
The regression experiments here evaluate on the 73 questions.

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -index lucene-index.ntcir8-zh.pos+docvectors+rawdocs \
 -topicreader TsvString -topics src/main/resources/topics-and-qrels/topics.ntcir8zh.eval.txt \
 -language zh -bm25 -output run.ntcir8-zh.bm25.topics.ntcir8zh.eval.txt &

```

Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -m map -m P.30 src/main/resources/topics-and-qrels/qrels.ntcir8.eval.txt run.ntcir8-zh.bm25.topics.ntcir8zh.eval.txt

```

## Effectiveness

With the above commands, you should be able to replicate the following results:

MAP                                     | BM25      |
:---------------------------------------|-----------|
[NTCIR-8 ACLIA (IR4QA subtask, Chinese monolingual)](http://research.nii.ac.jp/ntcir/ntcir-ws8/ws-en.html)| 0.4014    |


P30                                     | BM25      |
:---------------------------------------|-----------|
[NTCIR-8 ACLIA (IR4QA subtask, Chinese monolingual)](http://research.nii.ac.jp/ntcir/ntcir-ws8/ws-en.html)| 0.3365    |


