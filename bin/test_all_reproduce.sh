#!/usr/bin/bash

java -cp `ls target/*-fatjar.jar` io.anserini.reproduce.RunMsMarco -collection msmarco-v1-passage >& logs/log.msmarco-v1-passage
java -cp `ls target/*-fatjar.jar` io.anserini.reproduce.RunMsMarco -collection msmarco-v1-doc >& logs/log.msmarco-v1-doc
java -cp `ls target/*-fatjar.jar` io.anserini.reproduce.RunMsMarco -collection msmarco-v2-passage >& logs/log.msmarco-v2-passage
java -cp `ls target/*-fatjar.jar` io.anserini.reproduce.RunMsMarco -collection msmarco-v2-doc >& logs/log.msmarco-v2-doc
java -cp `ls target/*-fatjar.jar` io.anserini.reproduce.RunMsMarco -collection msmarco-v2.1-doc >& logs/log.msmarco-v2.1-doc
java -cp `ls target/*-fatjar.jar` io.anserini.reproduce.RunMsMarco -collection msmarco-v2.1-doc-segmented >& logs/log.msmarco-v2.1-doc-segmented
java -cp `ls target/*-fatjar.jar` io.anserini.reproduce.RunBeir >& logs/log.beir
java -cp `ls target/*-fatjar.jar` io.anserini.reproduce.RunBright >& logs/log.bright
