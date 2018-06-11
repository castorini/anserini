# Anserini Experiments on TREC Core

Indexing:

```
nohup sh target/appassembler/bin/IndexCollection -collection TrecCoreCollection \
 -input /path/to/nyt_corpus/ -generator JsoupGenerator \
 -index lucene-index.core.pos+docvectors -threads 16 -storePositions -storeDocvectors -optimize \
 > log.core.pos+docvectors &

```

The directory `/path/to/nyt_corpus/` should be the root directory of TREC Core collection, i.e., `ls /path/to/nyt_corpus/` 
should bring up a bunch of subdirectories, `1987` to `2007`. The command above builds a standard positional index 
(`-storePositions`) that's optimized into a single segment (`-optimize`). If you also want to store document vectors 
(e.g., for query expansion), add the `-docvectors` option.  The above command builds an index that stores term positions 
(`-storePositions`) as well as doc vectors for relevance feedback (`-storeDocvectors`), and `-optimize` force merges all 
index segment into one.

After indexing is done, you should be able to perform a retrieval as follows:

```
sh target/appassembler/bin/SearchCollection \
  -topicreader Trec -index lucene-index.core.pos+docvectors -bm25 \
  -topics src/main/resources/topics-and-qrels/topics.core17.nist.txt -output run.core17.nist.bm25.txt
```

For the retrieval model: specify `-bm25` to use BM25, `-ql` to use query likelihood, and add `-rm3` to invoke the RM3 
relevance feedback model (requires docvectors index).



**Evaluate**:

Evaluation can be done using `trec_eval`:

```
eval/trec_eval.9.0/trec_eval -m map -m P.30 \ 
src/main/resources/topics-and-qrels/qrels.core17.nist.txt run.core17.nist.bm25.txt
```

**Effectiveness Reference**:

If everything goes correctly, you should be able to replicate the following results:

| Metric | BM25   | QL     | RM3    |
| ------ | ------ | ------ | ------ |
| MAP    | 0.1979 | 0.1908 | 0.2329 |
| P30    | 0.4187 | 0.4280 | 0.4600 |

