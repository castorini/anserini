#!/usr/bin/bash

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage -topics msmarco-v2-passage.dev -output runs/run.msmarco-v2-passage.bm25.msmarco-v2-passage.dev.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage -topics msmarco-v2-passage.dev2 -output runs/run.msmarco-v2-passage.bm25.msmarco-v2-passage.dev2.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage -topics dl21 -output runs/run.msmarco-v2-passage.bm25.dl21.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage -topics dl22 -output runs/run.msmarco-v2-passage.bm25.dl22.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage -topics dl23 -output runs/run.msmarco-v2-passage.bm25.dl23.base.txt -hits 1000 -bm25

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage-slim -topics msmarco-v2-passage.dev -output runs/run.msmarco-v2-passage.bm25.msmarco-v2-passage.dev.slim.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage-slim -topics msmarco-v2-passage.dev2 -output runs/run.msmarco-v2-passage.bm25.msmarco-v2-passage.dev2.slim.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage-slim -topics dl21 -output runs/run.msmarco-v2-passage.bm25.dl21.slim.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage-slim -topics dl22 -output runs/run.msmarco-v2-passage.bm25.dl22.slim.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage-slim -topics dl23 -output runs/run.msmarco-v2-passage.bm25.dl23.slim.txt -hits 1000 -bm25

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage-full -topics msmarco-v2-passage.dev -output runs/run.msmarco-v2-passage.bm25.msmarco-v2-passage.dev.full.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage-full -topics msmarco-v2-passage.dev2 -output runs/run.msmarco-v2-passage.bm25.msmarco-v2-passage.dev2.full.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage-full -topics dl21 -output runs/run.msmarco-v2-passage.bm25.dl21.full.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage-full -topics dl22 -output runs/run.msmarco-v2-passage.bm25.dl22.full.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage-full -topics dl23 -output runs/run.msmarco-v2-passage.bm25.dl23.full.txt -hits 1000 -bm25

##

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage.d2q-t5 -topics msmarco-v2-passage.dev -output runs/run.msmarco-v2-passage.bm25-d2q-t5.msmarco-v2-passage.dev.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage.d2q-t5 -topics msmarco-v2-passage.dev2 -output runs/run.msmarco-v2-passage.bm25-d2q-t5.msmarco-v2-passage.dev2.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage.d2q-t5 -topics dl21 -output runs/run.msmarco-v2-passage.bm25-d2q-t5.dl21.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage.d2q-t5 -topics dl22 -output runs/run.msmarco-v2-passage.bm25-d2q-t5.dl22.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage.d2q-t5 -topics dl23 -output runs/run.msmarco-v2-passage.bm25-d2q-t5.dl23.base.txt -hits 1000 -bm25

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage.d2q-t5-docvectors -topics msmarco-v2-passage.dev -output runs/run.msmarco-v2-passage.bm25-d2q-t5.msmarco-v2-passage.dev.docvectors.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage.d2q-t5-docvectors -topics msmarco-v2-passage.dev2 -output runs/run.msmarco-v2-passage.bm25-d2q-t5.msmarco-v2-passage.dev2.docvectors.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage.d2q-t5-docvectors -topics dl21 -output runs/run.msmarco-v2-passage.bm25-d2q-t5.dl21.docvectors.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage.d2q-t5-docvectors -topics dl22 -output runs/run.msmarco-v2-passage.bm25-d2q-t5.dl22.docvectors.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-passage.d2q-t5-docvectors -topics dl23 -output runs/run.msmarco-v2-passage.bm25-d2q-t5.dl23.docvectors.txt -hits 1000 -bm25

##

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2-doc.bm25-doc-default.msmarco-v2-doc.dev.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2-doc.bm25-doc-default.msmarco-v2-doc.dev2.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc -topics dl21 -output runs/run.msmarco-v2-doc.bm25-doc-default.dl21.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc -topics dl22 -output runs/run.msmarco-v2-doc.bm25-doc-default.dl22.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc -topics dl23 -output runs/run.msmarco-v2-doc.bm25-doc-default.dl23.base.txt -hits 1000 -bm25

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-slim -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2-doc.bm25-doc-default.msmarco-v2-doc.dev.slim.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-slim -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2-doc.bm25-doc-default.msmarco-v2-doc.dev2.slim.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-slim -topics dl21 -output runs/run.msmarco-v2-doc.bm25-doc-default.dl21.slim.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-slim -topics dl22 -output runs/run.msmarco-v2-doc.bm25-doc-default.dl22.slim.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-slim -topics dl23 -output runs/run.msmarco-v2-doc.bm25-doc-default.dl23.slim.txt -hits 1000 -bm25

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-full -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2-doc.bm25-doc-default.msmarco-v2-doc.dev.full.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-full -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2-doc.bm25-doc-default.msmarco-v2-doc.dev2.full.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-full -topics dl21 -output runs/run.msmarco-v2-doc.bm25-doc-default.dl21.full.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-full -topics dl22 -output runs/run.msmarco-v2-doc.bm25-doc-default.dl22.full.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-full -topics dl23 -output runs/run.msmarco-v2-doc.bm25-doc-default.dl23.full.txt -hits 1000 -bm25

##

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc.d2q-t5 -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-default.msmarco-v2-doc.dev.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc.d2q-t5 -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-default.msmarco-v2-doc.dev2.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc.d2q-t5 -topics dl21 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-default.dl21.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc.d2q-t5 -topics dl22 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-default.dl22.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc.d2q-t5 -topics dl23 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-default.dl23.base.txt -hits 1000 -bm25

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc.d2q-t5-docvectors -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-default.msmarco-v2-doc.dev.docvectors.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc.d2q-t5-docvectors -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-default.msmarco-v2-doc.dev2.docvectors.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc.d2q-t5-docvectors -topics dl21 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-default.dl21.docvectors.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc.d2q-t5-docvectors -topics dl22 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-default.dl22.docvectors.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc.d2q-t5-docvectors -topics dl23 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-default.dl23.docvectors.txt -hits 1000 -bm25

##

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.msmarco-v2-doc.dev.base.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.msmarco-v2-doc.dev2.base.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented -topics dl21 -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.dl21.base.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented -topics dl22 -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.dl22.base.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented -topics dl23 -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.dl23.base.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented-slim -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.msmarco-v2-doc.dev.slim.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented-slim -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.msmarco-v2-doc.dev2.slim.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented-slim -topics dl21 -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.dl21.slim.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented-slim -topics dl22 -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.dl22.slim.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented-slim -topics dl23 -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.dl23.slim.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented-full -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.msmarco-v2-doc.dev.full.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented-full -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.msmarco-v2-doc.dev2.full.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented-full -topics dl21 -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.dl21.full.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented-full -topics dl22 -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.dl22.full.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented-full -topics dl23 -output runs/run.msmarco-v2-doc.bm25-doc-segmented-default.dl23.full.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000

##

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented.d2q-t5 -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-segmented-default.msmarco-v2-doc.dev.base.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented.d2q-t5 -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-segmented-default.msmarco-v2-doc.dev2.base.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented.d2q-t5 -topics dl21 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-segmented-default.dl21.base.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented.d2q-t5 -topics dl22 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-segmented-default.dl22.base.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented.d2q-t5 -topics dl23 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-segmented-default.dl23.base.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented.d2q-t5-docvectors -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-segmented-default.msmarco-v2-doc.dev.docvectors.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented.d2q-t5-docvectors -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-segmented-default.msmarco-v2-doc.dev2.docvectors.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented.d2q-t5-docvectors -topics dl21 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-segmented-default.dl21.docvectors.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented.d2q-t5-docvectors -topics dl22 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-segmented-default.dl22.docvectors.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2-doc-segmented.d2q-t5-docvectors -topics dl23 -output runs/run.msmarco-v2-doc.bm25-d2q-t5-doc-segmented-default.dl23.docvectors.txt -bm25 -hits 10000 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000

##

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2.1-doc.bm25-doc.msmarco-v2-doc.dev.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2.1-doc.bm25-doc.msmarco-v2-doc.dev2.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc -topics dl21-doc -output runs/run.msmarco-v2.1-doc.bm25-doc.dl21-doc.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc -topics dl22-doc -output runs/run.msmarco-v2.1-doc.bm25-doc.dl22-doc.base.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc -topics dl23-doc -output runs/run.msmarco-v2.1-doc.bm25-doc.dl23-doc.base.txt -hits 1000 -bm25

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-slim -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2.1-doc.bm25-doc.msmarco-v2-doc.dev.slim.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-slim -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2.1-doc.bm25-doc.msmarco-v2-doc.dev2.slim.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-slim -topics dl21-doc -output runs/run.msmarco-v2.1-doc.bm25-doc.dl21-doc.slim.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-slim -topics dl22-doc -output runs/run.msmarco-v2.1-doc.bm25-doc.dl22-doc.slim.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-slim -topics dl23-doc -output runs/run.msmarco-v2.1-doc.bm25-doc.dl23-doc.slim.txt -hits 1000 -bm25

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-full -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2.1-doc.bm25-doc.msmarco-v2-doc.dev.full.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-full -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2.1-doc.bm25-doc.msmarco-v2-doc.dev2.full.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-full -topics dl21-doc -output runs/run.msmarco-v2.1-doc.bm25-doc.dl21-doc.full.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-full -topics dl22-doc -output runs/run.msmarco-v2.1-doc.bm25-doc.dl22-doc.full.txt -hits 1000 -bm25
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-full -topics dl23-doc -output runs/run.msmarco-v2.1-doc.bm25-doc.dl23-doc.full.txt -hits 1000 -bm25

##

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.msmarco-v2-doc.dev.base.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.msmarco-v2-doc.dev2.base.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented -topics dl21-doc -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.dl21-doc.base.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented -topics dl22-doc -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.dl22-doc.base.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented -topics dl23-doc -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.dl23-doc.base.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented -topics rag24.raggy-dev -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.rag24.raggy-dev.base.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented-slim -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.msmarco-v2-doc.dev.slim.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented-slim -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.msmarco-v2-doc.dev2.slim.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented-slim -topics dl21-doc -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.dl21-doc.slim.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented-slim -topics dl22-doc -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.dl22-doc.slim.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented-slim -topics dl23-doc -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.dl23-doc.slim.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented-slim -topics rag24.raggy-dev -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.rag24.raggy-dev.slim.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented-full -topics msmarco-v2-doc.dev -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.msmarco-v2-doc.dev.full.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented-full -topics msmarco-v2-doc.dev2 -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.msmarco-v2-doc.dev2.full.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented-full -topics dl21-doc -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.dl21-doc.full.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented-full -topics dl22-doc -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.dl22-doc.full.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented-full -topics dl23-doc -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.dl23-doc.full.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000
java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented-full -topics rag24.raggy-dev -output runs/run.msmarco-v2.1-doc.bm25-segmented-doc.rag24.raggy-dev.full.txt -hits 10000 -bm25 -selectMaxPassage -selectMaxPassage.delimiter \# -selectMaxPassage.hits 1000

##

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented -topics rag24.test -output runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.base.txt -hits 1000 -bm25

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented-slim -topics rag24.test -output runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.slim.txt -hits 1000 -bm25

java -cp `ls target/*-fatjar.jar` io.anserini.search.SearchCollection -threads 16 -index msmarco-v2.1-doc-segmented-full -topics rag24.test -output runs/run.msmarco-v2.1-doc-segmented.bm25.rag24.test.full.txt -hits 1000 -bm25

