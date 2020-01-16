/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.search.topicreader;

/**
 * An enumeration comprising standard sets of topics from various evaluations.
 */
public enum Topics {
  TREC1_ADHOC(TrecTopicReader.class, "topics-and-qrels/topics.adhoc.51-100.txt"),
  TREC2_ADHOC(TrecTopicReader.class, "topics-and-qrels/topics.adhoc.101-150.txt"),
  TREC3_ADHOC(TrecTopicReader.class, "topics-and-qrels/topics.adhoc.151-200.txt"),
  ROBUST04(TrecTopicReader.class, "topics-and-qrels/topics.robust04.txt"),
  ROBUST05(TrecTopicReader.class, "topics-and-qrels/topics.robust05.txt"),
  CORE17(TrecTopicReader.class, "topics-and-qrels/topics.core17.txt"),
  CORE18(TrecTopicReader.class, "topics-and-qrels/topics.core18.txt"),
  WT10G(TrecTopicReader.class, "topics-and-qrels/topics.adhoc.451-550.txt"),
  TREC2004_TERABYTE(TrecTopicReader.class, "topics-and-qrels/topics.terabyte04.701-750.txt"),
  TREC2005_TERABYTE(TrecTopicReader.class, "topics-and-qrels/topics.terabyte05.751-800.txt"),
  TREC2006_TERABYTE(TrecTopicReader.class, "topics-and-qrels/topics.terabyte06.801-850.txt"),
  TREC2010_WEB(WebxmlTopicReader.class, "topics-and-qrels/topics.web.51-100.txt"),
  TREC2011_WEB(WebxmlTopicReader.class, "topics-and-qrels/topics.web.101-150.txt"),
  TREC2012_WEB(WebxmlTopicReader.class, "topics-and-qrels/topics.web.151-200.txt"),
  TREC2013_WEB(WebxmlTopicReader.class, "topics-and-qrels/topics.web.201-250.txt"),
  TREC2014_WEB(WebxmlTopicReader.class, "topics-and-qrels/topics.web.251-300.txt"),
  MB11(MicroblogTopicReader.class, "topics-and-qrels/topics.microblog2011.txt"),
  MB12(MicroblogTopicReader.class, "topics-and-qrels/topics.microblog2012.txt"),
  MB13(MicroblogTopicReader.class, "topics-and-qrels/topics.microblog2013.txt"),
  MB14(MicroblogTopicReader.class, "topics-and-qrels/topics.microblog2014.txt"),
  CAR17V15_BENCHMARK_Y1_TEST(CarTopicReader.class, "topics-and-qrels/topics.car17v1.5.benchmarkY1test.txt"),
  CAR17V20_BENCHMARK_Y1_TEST(CarTopicReader.class, "topics-and-qrels/topics.car17v2.0.benchmarkY1test.txt"),
  MSMARCO_DOC_DEV(TsvIntTopicReader.class,"topics-and-qrels/topics.msmarco-doc.dev.txt"),
  MSMARCO_PASSAGE_DEV_SUBSET(TsvIntTopicReader.class, "topics-and-qrels/topics.msmarco-passage.dev-subset.txt"),
  NTCIR8_ZH(TsvStringTopicReader.class, "topics-and-qrels/topics.ntcir8zh.eval.txt"),
  CLEF2006_FR(TsvStringTopicReader.class, "topics-and-qrels/topics.clef06fr.mono.fr.txt"),
  TREC2002_AR(TrecTopicReader.class, "topics-and-qrels/topics.trec02ar-ar.txt"),
  FIRE2012_BN(TrecTopicReader.class, "topics-and-qrels/topics.fire12bn.176-225.txt"),
  FIRE2012_HI(TrecTopicReader.class, "topics-and-qrels/topics.fire12hi.176-225.txt"),
  FIRE2012_EN(TrecTopicReader.class, "topics-and-qrels/topics.fire12en.176-225.txt");

  public final String path;
  public final Class readerClass;

  Topics(Class c, String path) {
    this.readerClass = c;
    this.path = path;
  }
}