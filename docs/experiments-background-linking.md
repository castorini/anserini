# Anserini: Baselines on [TREC News Background Linking](http://trec-news.org/)

This page contains instructions for running baselines on the TREC News background linking task, which is part of the news track.
The background linking task uses the [TREC Washington Post Corpus](https://trec.nist.gov/data/wapost/).

## Indexing

The indexing command is the same as building one for [core18](regressions-core18.md):

```
nohup sh target/appassembler/bin/IndexCollection -collection WashingtonPostCollection -input /path/to/core18 \
 -index lucene-index.core18.pos+docvectors+rawdocs -generator WashingtonPostGenerator -threads 16 \
 -storePositions -storeDocvectors -storeRawDocs >& log.core18.pos+docvectors+rawdocs &
```

The directory `/path/to/core18/` should be the root directory of the [TREC Washington Post Corpus](https://trec.nist.gov/data/wapost/), i.e., `ls /path/to/core18/`
should bring up a single JSON file.

For additional details, see explanation of [common indexing options](common-indexing-options.md).

## Retrieval

Topics and qrels are stored in [`src/main/resources/topics-and-qrels/`](../src/main/resources/topics-and-qrels/), downloaded from NIST:

+ 2018:
    + [`topics.backgroundlinking18.txt`](../src/main/resources/topics-and-qrels/topics.backgroundlinking18.txt): [topics for the background linking task of the TREC 2018 News Track](https://trec.nist.gov/data/news/2018/newsir18-topics.txt)
    + [`qrels.backgroundlinking18.txt`](../src/main/resources/topics-and-qrels/qrels.backgroundlinking18.txt): [qrels for the background linking task of the TREC 2018 News Track](https://trec.nist.gov/data/news/2018/bqrels.exp-gains.txt)

+ 2019:
    + [`topics.backgroundlinking19.txt`](../src/main/resources/topics-and-qrels/topics.backgroundlinking19.txt): [topics for the background linking task of the TREC 2019 News Track]()
    + [`qrels.backgroundlinking19.txt`](../src/main/resources/topics-and-qrels/qrels.backgroundlinking19.txt): [qrels for the background linking task of the TREC 2019 News Track](https://trec.nist.gov/data/news/2019/newsir19-qrels-background.txt)

After indexing has completed, you should be able to perform retrieval as follows:

```
nohup target/appassembler/bin/SearchCollection -searchnewsbackground -index lucene-index.core18.pos+docvectors+rawdocs -topicreader NewsBackgroundLinking -topics src/main/resources/topics-and-qrels/topics.backgroundlinking18.txt -bm25 -hits 100 -backgroundlinking.k 100 -runtag bl_bm25 -output run.bl.bm25.topics.backgroundlinking18.txt

nohup target/appassembler/bin/SearchCollection -searchnewsbackground -index lucene-index.core18.pos+docvectors+rawdocs -topicreader NewsBackgroundLinking -topics src/main/resources/topics-and-qrels/topics.backgroundlinking18.txt -bm25 -rm3 -hits 100 -backgroundlinking.k 100 -runtag bl_bm25_rm3 -output run.bl.bm25+rm3.topics.backgroundlinking18.txt

nohup target/appassembler/bin/SearchCollection -searchnewsbackground -index lucene-index.core18.pos+docvectors+rawdocs -topicreader NewsBackgroundLinking -topics src/main/resources/topics-and-qrels/topics.backgroundlinking18.txt -bm25 -rm3 -backgroundlinking.datefilter -hits 100 -backgroundlinking.k 100 -runtag bl_bm25_rm3_df -output run.bl.bm25+rm3+df.topics.backgroundlinking18.txt

nohup target/appassembler/bin/SearchCollection -searchnewsbackground -index lucene-index.core18.pos+docvectors+rawdocs -topicreader NewsBackgroundLinking -topics src/main/resources/topics-and-qrels/topics.backgroundlinking19.txt -bm25 -hits 100 -backgroundlinking.k 100 -runtag bl_bm25 -output run.bl.bm25.topics.backgroundlinking19.txt

nohup target/appassembler/bin/SearchCollection -searchnewsbackground -index lucene-index.core18.pos+docvectors+rawdocs -topicreader NewsBackgroundLinking -topics src/main/resources/topics-and-qrels/topics.backgroundlinking19.txt -bm25 -rm3 -hits 100 -backgroundlinking.k 100 -runtag bl_bm25_rm3 -output run.bl.bm25+rm3.topics.backgroundlinking19.txt

nohup target/appassembler/bin/SearchCollection -searchnewsbackground -index lucene-index.core18.pos+docvectors+rawdocs -topicreader NewsBackgroundLinking -topics src/main/resources/topics-and-qrels/topics.backgroundlinking19.txt -bm25 -rm3 -backgroundlinking.datefilter -hits 100 -backgroundlinking.k 100 -runtag bl_bm25_rm3_df -output run.bl.bm25+rm3+df.topics.backgroundlinking19.txt
```

## Evaluation can be performed using `trec_eval`:

```
eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg_cut.5 src/main/resources/topics-and-qrels/qrels.backgroundlinking18.txt run.bl.bm25.topics.backgroundlinking18.txt

eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg_cut.5 src/main/resources/topics-and-qrels/qrels.backgroundlinking18.txt run.bl.bm25+rm3.topics.backgroundlinking18.txt

eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg_cut.5 src/main/resources/topics-and-qrels/qrels.backgroundlinking18.txt run.bl.bm25+rm3+df.topics.backgroundlinking18.txt

eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg_cut.5 src/main/resources/topics-and-qrels/qrels.backgroundlinking19.txt run.bl.bm25.topics.backgroundlinking19.txt

eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg_cut.5 src/main/resources/topics-and-qrels/qrels.backgroundlinking19.txt run.bl.bm25+rm3.topics.backgroundlinking19.txt

eval/trec_eval.9.0.4/trec_eval -c -M1000 -m map -m ndcg_cut.5 src/main/resources/topics-and-qrels/qrels.backgroundlinking19.txt run.bl.bm25+rm3+df.topics.backgroundlinking19.txt
```

## Effectiveness
With the above commands, you should be able to replicate the following results:

NDCG@5                                  | BM25      | +RM3      | +RM3+DF  |
:---------------------------------------|-----------|-----------|----------|
[TREC 2018 New Track BL Topics](../src/main/resources/topics-and-qrels/topics.backgroundlinking18.txt)|  0.3293  |  0.3526  |  0.4171  |
[TREC 2019 New Track BL Topics](../src/main/resources/topics-and-qrels/topics.backgroundlinking19.txt)|  0.4785  |  0.5217  |  0.5051  |

MAP                                     | BM25      | +RM3      | +RM3+DF  |
:---------------------------------------|-----------|-----------|----------|
[TREC 2018 New Track BL Topics](../src/main/resources/topics-and-qrels/topics.backgroundlinking18.txt)|  0.2490  |  0.2642  |  0.2692  |
[TREC 2019 New Track BL Topics](../src/main/resources/topics-and-qrels/topics.backgroundlinking19.txt)|  0.3027  |  0.3790  |  0.3158  |