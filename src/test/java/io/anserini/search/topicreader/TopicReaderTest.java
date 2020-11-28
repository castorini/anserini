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

import org.junit.Test;

import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TopicReaderTest {

  @Test
  public void testTopicReaderClassLookup() {
    assertEquals(TrecTopicReader.class,
        TopicReader.getTopicReaderClassByFile("src/main/resources/topics-and-qrels/topics.robust04.txt"));
    assertEquals(TrecTopicReader.class,
        TopicReader.getTopicReaderClassByFile("topics.robust04.txt"));

    assertEquals(CovidTopicReader.class,
        TopicReader.getTopicReaderClassByFile("src/main/resources/topics-and-qrels/topics.covid-round1.xml"));
    assertEquals(CovidTopicReader.class,
        TopicReader.getTopicReaderClassByFile("topics.covid-round1.xml"));

    // Unknown TopicReader class.
    assertEquals(null,
        TopicReader.getTopicReaderClassByFile("topics.unknown.txt"));
  }

  @Test
  public void testGetTopicsByFile() {
    Map<Object, Map<String, String>> topics =
        TopicReader.getTopicsByFile("src/main/resources/topics-and-qrels/topics.robust04.txt");

    assertNotNull(topics);

    Object[] keys = topics.keySet().toArray();
    Object firstKey = keys[0];
    Object lastKey = keys[keys.length - 1];

    assertEquals(250, topics.size());
    assertEquals(301, (int) firstKey);
    assertEquals("International Organized Crime", topics.get(firstKey).get("title"));
    assertEquals(700, (int) lastKey);
    assertEquals("gasoline tax U.S.", topics.get(lastKey).get("title"));
  }

  @Test
  public void testNewswireTopics() {
    Map<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.TREC1_ADHOC);

    assertNotNull(topics);

    Integer[] keys = topics.keySet().toArray(new Integer[0]);
    Integer firstKey = keys[0];
    Integer lastKey = keys[keys.length - 1];

    assertEquals(50, topics.size());
    assertEquals(51, (int) firstKey);
    assertEquals("Airbus Subsidies", topics.get(firstKey).get("title"));
    assertEquals(100, (int) lastKey);
    assertEquals("Controlling the Transfer of High Technology", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2_ADHOC);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];

    assertEquals(50, topics.size());
    assertEquals(101, (int) firstKey);
    assertEquals("Design of the \"Star Wars\" Anti-missile Defense System", topics.get(firstKey).get("title"));
    assertEquals(150, (int) lastKey);
    assertEquals("U.S. Political Campaign Financing", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.TREC3_ADHOC);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(50, topics.size());
    assertEquals(151, (int) firstKey);
    assertEquals("Coping with overcrowded prisons", topics.get(firstKey).get("title"));
    assertEquals(200, (int) lastKey);
    assertEquals("Impact of foreign textile imports on U.S.", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.ROBUST04);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(250, topics.size());
    assertEquals(301, (int) firstKey);
    assertEquals("International Organized Crime", topics.get(firstKey).get("title"));
    assertEquals(700, (int) lastKey);
    assertEquals("gasoline tax U.S.", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.ROBUST05);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(50, topics.size());
    assertEquals(303, (int) firstKey);
    assertEquals("Hubble Telescope Achievements", topics.get(firstKey).get("title"));
    assertEquals(689, (int) lastKey);
    assertEquals("family-planning aid", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.CORE17);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(50, topics.size());
    assertEquals(307, (int) firstKey);
    assertEquals("New Hydroelectric Projects", topics.get(firstKey).get("title"));
    assertEquals(690, (int) lastKey);
    assertEquals("college education advantage", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.CORE18);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(50, topics.size());
    assertEquals(321, (int) firstKey);
    assertEquals("Women in Parliaments", topics.get(firstKey).get("title"));
    assertEquals(825, (int) lastKey);
    assertEquals("ethanol and food prices", topics.get(lastKey).get("title"));
  }

  @Test
  public void testNewswireTopics_TopicIdsAsStrings() {
    Map<String, Map<String, String>> topics;

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC1_ADHOC);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Airbus Subsidies", topics.get("51").get("title"));
    assertEquals("Controlling the Transfer of High Technology", topics.get("100").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC2_ADHOC);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Design of the \"Star Wars\" Anti-missile Defense System", topics.get("101").get("title"));
    assertEquals("U.S. Political Campaign Financing", topics.get("150").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC3_ADHOC);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Coping with overcrowded prisons", topics.get("151").get("title"));
    assertEquals("Impact of foreign textile imports on U.S.", topics.get("200").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.ROBUST04);
    assertNotNull(topics);
    assertEquals(250, topics.size());
    assertEquals("International Organized Crime", topics.get("301").get("title"));
    assertEquals("gasoline tax U.S.", topics.get("700").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.ROBUST05);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Hubble Telescope Achievements", topics.get("303").get("title"));
    assertEquals("family-planning aid", topics.get("689").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.CORE17);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("New Hydroelectric Projects", topics.get("307").get("title"));
    assertEquals("college education advantage", topics.get("690").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.CORE18);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Women in Parliaments", topics.get("321").get("title"));
    assertEquals("ethanol and food prices", topics.get("825").get("title"));
  }

  @Test
  public void testWebTopics() {
    Map<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.WT10G);
    assertNotNull(topics);

    Integer[] keys = topics.keySet().toArray(new Integer[0]);
    Integer firstKey = keys[0];
    Integer lastKey = keys[keys.length - 1];

    assertEquals(100, topics.size());
    assertEquals(451, (int) firstKey);
    assertEquals("What is a Bengals cat?", topics.get(firstKey).get("title"));
    assertEquals(550, (int) lastKey);
    assertEquals("how are the volcanoes made?", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2004_TERABYTE);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(50, topics.size());
    assertEquals(701, (int) firstKey);
    assertEquals("U.S. oil industry history", topics.get(firstKey).get("title"));
    assertEquals(750, (int) lastKey);
    assertEquals("John Edwards womens issues", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2005_TERABYTE);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(50, topics.size());
    assertEquals(751, (int) firstKey);
    assertEquals("Scrabble Players", topics.get(firstKey).get("title"));
    assertEquals(800, (int) lastKey);
    assertEquals("Ovarian Cancer Treatment", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2006_TERABYTE);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(50, topics.size());
    assertEquals(801, (int) firstKey);
    assertEquals("Kudzu Pueraria lobata", topics.get(firstKey).get("title"));
    assertEquals(850, (int) lastKey);
    assertEquals("Mississippi River flood", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2007_MILLION_QUERY);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(10000, topics.keySet().size());
    assertEquals(1, (int) firstKey);
    assertEquals("after school program evaluation", topics.get(firstKey).get("title").trim());
    assertEquals(10000, (int) lastKey);
    assertEquals("californa mission", topics.get(lastKey).get("title").trim());

    topics = TopicReader.getTopics(Topics.TREC2008_MILLION_QUERY);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(10000, topics.keySet().size());
    assertEquals(10001, (int) firstKey);
    assertEquals("comparability of pay analyses", topics.get(firstKey).get("title").trim());
    assertEquals(20000, (int) lastKey);
    assertEquals("manchester city hall", topics.get(lastKey).get("title").trim());

    topics = TopicReader.getTopics(Topics.TREC2009_MILLION_QUERY);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(40000, topics.keySet().size());
    assertEquals(20001, (int) firstKey);
    assertEquals("obama family tree", topics.get(firstKey).get("title").trim());
    assertEquals("1", topics.get(firstKey).get("priority").trim());
    assertEquals(60000, (int) lastKey);
    assertEquals("bird shingles", topics.get(lastKey).get("title").trim());
    assertEquals("4", topics.get(lastKey).get("priority").trim());

    topics = TopicReader.getTopics(Topics.TREC2010_WEB);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(50, topics.size());
    assertEquals(51, (int) firstKey);
    assertEquals("horse hooves", topics.get(firstKey).get("title"));
    assertEquals(100, (int) lastKey);
    assertEquals("rincon puerto rico", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2011_WEB);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(50, topics.size());
    assertEquals(101, (int) firstKey);
    assertEquals("ritz carlton lake las vegas", topics.get(firstKey).get("title"));
    assertEquals(150, (int) lastKey);
    assertEquals("tn highway patrol", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2012_WEB);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(50, topics.size());
    assertEquals(151, (int) firstKey);
    assertEquals("403b", topics.get(firstKey).get("title"));
    assertEquals(200, (int) lastKey);
    assertEquals("ontario california airport", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2013_WEB);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(50, topics.size());
    assertEquals(201, (int) firstKey);
    assertEquals("raspberry pi", topics.get(firstKey).get("title"));
    assertEquals(250, (int) lastKey);
    assertEquals("ford edge problems", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2014_WEB);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(50, topics.size());
    assertEquals(251, (int) firstKey);
    assertEquals("identifying spider bites", topics.get(firstKey).get("title"));
    assertEquals(300, (int) lastKey);
    assertEquals("how to find the mean", topics.get(lastKey).get("title"));
  }

  @Test
  public void testWebTopics_TopicIdsAsStrings() {
    Map<String, Map<String, String>> topics;

    topics = TopicReader.getTopicsWithStringIds(Topics.WT10G);
    assertNotNull(topics);
    assertEquals(100, topics.size());
    assertEquals("What is a Bengals cat?", topics.get("451").get("title"));
    assertEquals("how are the volcanoes made?", topics.get("550").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC2004_TERABYTE);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("U.S. oil industry history", topics.get("701").get("title"));
    assertEquals("John Edwards womens issues", topics.get("750").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC2005_TERABYTE);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Scrabble Players", topics.get("751").get("title"));
    assertEquals("Ovarian Cancer Treatment", topics.get("800").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC2006_TERABYTE);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Kudzu Pueraria lobata", topics.get("801").get("title"));
    assertEquals("Mississippi River flood", topics.get("850").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC2007_MILLION_QUERY);
    assertNotNull(topics);
    assertEquals(10000, topics.keySet().size());
    assertEquals("after school program evaluation", topics.get("1").get("title").trim());
    assertEquals("californa mission", topics.get("10000").get("title").trim());

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC2008_MILLION_QUERY);
    assertNotNull(topics);
    assertEquals(10000, topics.keySet().size());
    assertEquals("comparability of pay analyses", topics.get("10001").get("title").trim());
    assertEquals("manchester city hall", topics.get("20000").get("title").trim());

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC2009_MILLION_QUERY);
    assertNotNull(topics);
    assertEquals(40000, topics.keySet().size());
    assertEquals("obama family tree", topics.get("20001").get("title").trim());
    assertEquals("1", topics.get("20001").get("priority").trim());
    assertEquals("bird shingles", topics.get("60000").get("title").trim());
    assertEquals("4", topics.get("60000").get("priority").trim());

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC2010_WEB);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("horse hooves", topics.get("51").get("title"));
    assertEquals("rincon puerto rico", topics.get("100").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC2011_WEB);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("ritz carlton lake las vegas", topics.get("101").get("title"));
    assertEquals("tn highway patrol", topics.get("150").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC2012_WEB);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("403b", topics.get("151").get("title"));
    assertEquals("ontario california airport", topics.get("200").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC2013_WEB);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("raspberry pi", topics.get("201").get("title"));
    assertEquals("ford edge problems", topics.get("250").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC2014_WEB);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("identifying spider bites", topics.get("251").get("title"));
    assertEquals("how to find the mean", topics.get("300").get("title"));
  }

  @Test
  public void testMicoblogTopics() {
    Map<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.MB11);
    assertNotNull(topics);

    Integer[] keys = topics.keySet().toArray(new Integer[0]);
    Integer firstKey = keys[0];
    Integer lastKey = keys[keys.length - 1];

    assertEquals(50, topics.size());
    assertEquals(1, (int) firstKey);
    assertEquals("BBC World Service staff cuts", topics.get(firstKey).get("title"));
    assertEquals(50, (int) lastKey);
    assertEquals("war prisoners, Hatch Act", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.MB12);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(60, topics.size());
    assertEquals(51, (int) firstKey);
    assertEquals("British Government cuts", topics.get(firstKey).get("title"));
    assertEquals(110, (int) lastKey);
    assertEquals("economic trade sanctions", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.MB13);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(60, topics.size());
    assertEquals(111, (int) firstKey);
    assertEquals("water shortages", topics.get(firstKey).get("title"));
    assertEquals(170, (int) lastKey);
    assertEquals("Tony Mendez", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.MB14);
    assertNotNull(topics);
    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];
    assertEquals(55, topics.size());
    assertEquals(171, (int) firstKey);
    assertEquals("Ron Weasley birthday", topics.get(firstKey).get("title"));
    assertEquals(225, (int) lastKey);
    assertEquals("Barbara Walters, chicken pox", topics.get(lastKey).get("title"));
  }

  @Test
  public void testMicoblogTopics_TopicIdsAsStrings() {
    Map<String, Map<String, String>> topics;

    topics = TopicReader.getTopicsWithStringIds(Topics.MB11);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("BBC World Service staff cuts", topics.get("1").get("title"));
    assertEquals("war prisoners, Hatch Act", topics.get("50").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.MB12);
    assertNotNull(topics);
    assertEquals(60, topics.size());
    assertEquals("British Government cuts", topics.get("51").get("title"));
    assertEquals("economic trade sanctions", topics.get("110").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.MB13);
    assertNotNull(topics);
    assertEquals(60, topics.size());
    assertEquals("water shortages", topics.get("111").get("title"));
    assertEquals("Tony Mendez", topics.get("170").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.MB14);
    assertNotNull(topics);
    assertEquals(55, topics.size());
    assertEquals("Ron Weasley birthday", topics.get("171").get("title"));
    assertEquals("Barbara Walters, chicken pox", topics.get("225").get("title"));
  }

  @Test
  public void testCAR() {
    Map<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.CAR17V15_BENCHMARK_Y1_TEST);
    assertNotNull(topics);
    String[] keys = topics.keySet().toArray(new String[0]);
    String firstKey = keys[0];
    String lastKey = keys[keys.length - 1];

    assertEquals(2125, topics.size());
    assertEquals("Aftertaste/Aftertaste%20processing%20in%20the%20cerebral%20cortex", firstKey);
    assertEquals("Aftertaste/Aftertaste processing in the cerebral cortex", topics.get(firstKey).get("title"));
    assertEquals("Yellowstone%20National%20Park/Recreation", lastKey);
    assertEquals("Yellowstone National Park/Recreation", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.CAR17V20_BENCHMARK_Y1_TEST);
    assertNotNull(topics);

    keys = topics.keySet().toArray(new String[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];

    assertEquals(2254, topics.size());
    assertEquals("enwiki:Aftertaste", firstKey);
    assertEquals("Aftertaste", topics.get(firstKey).get("title"));
    assertEquals("enwiki:Yellowstone%20National%20Park/Recreation", lastKey);
    assertEquals("Yellowstone National Park/Recreation", topics.get(lastKey).get("title"));
  }

  @Test
  public void testCAR_TopicIdsAsStrings() {
    Map<String, Map<String, String>> topics;

    topics = TopicReader.getTopicsWithStringIds(Topics.CAR17V15_BENCHMARK_Y1_TEST);
    assertNotNull(topics);
    assertEquals(2125, topics.size());
    assertEquals("Aftertaste/Aftertaste processing in the cerebral cortex",
        topics.get("Aftertaste/Aftertaste%20processing%20in%20the%20cerebral%20cortex").get("title"));
    assertEquals("Yellowstone National Park/Recreation",
        topics.get("Yellowstone%20National%20Park/Recreation").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.CAR17V20_BENCHMARK_Y1_TEST);
    assertNotNull(topics);
    assertEquals(2254, topics.size());
    assertEquals("Aftertaste",
        topics.get("enwiki:Aftertaste").get("title"));
    assertEquals("Yellowstone National Park/Recreation",
        topics.get("enwiki:Yellowstone%20National%20Park/Recreation").get("title"));  }

  @Test
  public void testMSMARO() {
    Map<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.MSMARCO_DOC_DEV);
    assertNotNull(topics);

    Integer[] keys = topics.keySet().toArray(new Integer[0]);
    Integer firstKey = keys[0];
    Integer lastKey = keys[keys.length - 1];

    assertEquals(5193, topics.size());
    assertEquals(2, (int) firstKey);
    assertEquals("androgen receptor define", topics.get(firstKey).get("title"));
    assertEquals(1102400, (int) lastKey);
    assertEquals("why do bears hibernate", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET);
    assertNotNull(topics);

    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];

    assertEquals(6980, topics.size());
    assertEquals(2, (int) firstKey);
    assertEquals("Androgen receptor define", topics.get(firstKey).get("title"));
    assertEquals(1102400, (int) lastKey);
    assertEquals("why do bears hibernate", topics.get(lastKey).get("title"));
  }

  @Test
  public void testMSMARO_TopicIdsAsStrings() {
    Map<String, Map<String, String>> topics;

    topics = TopicReader.getTopicsWithStringIds(Topics.MSMARCO_DOC_DEV);
    assertNotNull(topics);
    assertEquals(5193, topics.size());
    assertEquals("androgen receptor define", topics.get("2").get("title"));
    assertEquals("why do bears hibernate", topics.get("1102400").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.MSMARCO_PASSAGE_DEV_SUBSET);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals("Androgen receptor define", topics.get("2").get("title"));
    assertEquals("why do bears hibernate", topics.get("1102400").get("title"));
  }

  @Test
  public void testNonEnglishTopics() {
    Map<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.NTCIR8_ZH);
    assertNotNull(topics);

    String[] keys = topics.keySet().toArray(new String[0]);
    String firstKey = keys[0];
    String lastKey = keys[keys.length - 1];

    assertEquals(73, topics.size());
    assertEquals("ACLIA2-CS-0002", firstKey);
    assertEquals("《千里走单骑》和张艺谋是什么关系？", topics.get(firstKey).get("title"));
    assertEquals("ACLIA2-CS-0100", lastKey);
    assertEquals("为什么美军占领了巴格达？", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.CLEF2006_FR);
    assertNotNull(topics);

    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];

    assertEquals(49, topics.size());
    assertEquals("301-AH", firstKey);
    assertEquals("Les Produits Nestlé", topics.get(firstKey).get("title"));
    assertEquals("350-AH", lastKey);
    assertEquals("Le Décès d'Ayrton Senna", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2002_AR);
    assertNotNull(topics);

    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];

    assertEquals(50, topics.size());
    assertEquals(26, (int) firstKey);
    assertEquals("مجلس المقاومة الوطني الكردستاني", topics.get(firstKey).get("title"));
    assertEquals(75, (int) lastKey);
    assertEquals("فيروسات الكمبيوتر في الوطن العربي", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.FIRE2012_BN);
    assertNotNull(topics);

    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];

    assertEquals(50, topics.size());
    assertEquals(176, (int) firstKey);
    assertEquals("ওয়াই এস আর রেড্ডির মৃত্যু", topics.get(firstKey).get("title"));
    assertEquals(225, (int) lastKey);
    assertEquals("স্যাটানিক ভার্সেস বিতর্ক", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.FIRE2012_HI);
    assertNotNull(topics);

    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];

    assertEquals(50, topics.size());
    assertEquals(176, (int) firstKey);
    assertEquals("वाई एस आर रेड्डी की मौत", topics.get(firstKey).get("title"));
    assertEquals(225, (int) lastKey);
    assertEquals("सेटेनिक वर्सेज विवाद", topics.get(lastKey).get("title"));

    topics = TopicReader.getTopics(Topics.FIRE2012_EN);
    assertNotNull(topics);

    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];

    assertEquals(50, topics.size());
    assertEquals(176, (int) firstKey);
    assertEquals("YSR Reddy death", topics.get(firstKey).get("title"));
    assertEquals(225, (int) lastKey);
    assertEquals("Satanic Verses controversy", topics.get(lastKey).get("title"));
  }

  @Test
  public void testNonEnglishTopics_TopicIdsAsStrings() {
    Map<String, Map<String, String>> topics;

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC1_ADHOC);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Airbus Subsidies", topics.get("51").get("title"));
    assertEquals("Controlling the Transfer of High Technology", topics.get("100").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.NTCIR8_ZH);
    assertNotNull(topics);
    assertEquals(73, topics.size());
    assertEquals("《千里走单骑》和张艺谋是什么关系？", topics.get("ACLIA2-CS-0002").get("title"));
    assertEquals("为什么美军占领了巴格达？", topics.get("ACLIA2-CS-0100").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.CLEF2006_FR);
    assertNotNull(topics);
    assertEquals(49, topics.size());
    assertEquals("Les Produits Nestlé", topics.get("301-AH").get("title"));
    assertEquals("Le Décès d'Ayrton Senna", topics.get("350-AH").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.TREC2002_AR);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("مجلس المقاومة الوطني الكردستاني", topics.get("26").get("title"));
    assertEquals("فيروسات الكمبيوتر في الوطن العربي", topics.get("75").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.FIRE2012_BN);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("ওয়াই এস আর রেড্ডির মৃত্যু", topics.get("176").get("title"));
    assertEquals("স্যাটানিক ভার্সেস বিতর্ক", topics.get("225").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.FIRE2012_HI);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("वाई एस आर रेड्डी की मौत", topics.get("176").get("title"));
    assertEquals("सेटेनिक वर्सेज विवाद", topics.get("225").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.FIRE2012_EN);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("YSR Reddy death", topics.get("176").get("title"));
    assertEquals("Satanic Verses controversy", topics.get("225").get("title"));
  }

  @Test
  public void testCovidTopics() {
    Map<String, Map<String, String>> topics;

    // Round 1
    topics = TopicReader.getTopics(Topics.COVID_ROUND1);
    assertEquals(30, topics.keySet().size());

    assertEquals("coronavirus origin", topics.get(1).get("query"));
    assertEquals("what is the origin of COVID-19", topics.get(1).get("question"));
    assertEquals("seeking range of information about the SARS-CoV-2 virus's origin, " +
            "including its evolution, animal source, and first transmission into humans",
        topics.get(1).get("narrative"));

    assertEquals("coronavirus remdesivir", topics.get(30).get("query"));
    assertEquals("is remdesivir an effective treatment for COVID-19", topics.get(30).get("question"));
    assertEquals(
        "seeking specific information on clinical outcomes in COVID-19 patients treated with remdesivir",
        topics.get(30).get("narrative"));

    // Round 2
    topics = TopicReader.getTopics(Topics.COVID_ROUND2);
    assertEquals(35, topics.keySet().size());

    assertEquals("coronavirus public datasets", topics.get(35).get("query"));

    // Round 3
    topics = TopicReader.getTopics(Topics.COVID_ROUND3);
    assertEquals(40, topics.keySet().size());

    assertEquals("coronavirus mutations", topics.get(40).get("query"));

    // Round 4
    topics = TopicReader.getTopics(Topics.COVID_ROUND4);
    assertEquals(45, topics.keySet().size());

    assertEquals("coronavirus mental health impact", topics.get(45).get("query"));

    // Round 5
    topics = TopicReader.getTopics(Topics.COVID_ROUND5);
    assertEquals(50, topics.keySet().size());

    assertEquals("mRNA vaccine coronavirus", topics.get(50).get("query"));
  }

  @Test
  public void testCovidTopicsUDel() {
    Map<String, Map<String, String>> topics;

    // Round 1
    topics = TopicReader.getTopics(Topics.COVID_ROUND1_UDEL);
    assertEquals(30, topics.keySet().size());

    assertEquals("coronavirus remdesivir remdesivir effective treatment COVID-19",
        topics.get(30).get("query"));

    // Round 2
    topics = TopicReader.getTopics(Topics.COVID_ROUND2_UDEL);
    assertEquals(35, topics.keySet().size());

    assertEquals("coronavirus public datasets public datasets COVID-19",
        topics.get(35).get("query"));

    // Round 3
    topics = TopicReader.getTopics(Topics.COVID_ROUND3_UDEL);
    assertEquals(40, topics.keySet().size());

    assertEquals("coronavirus mutations observed mutations SARS-CoV-2 genome mutations",
        topics.get(40).get("query"));

    // Round 4
    topics = TopicReader.getTopics(Topics.COVID_ROUND4_UDEL);
    assertEquals(45, topics.keySet().size());

    assertEquals("coronavirus mental health impact COVID-19 pandemic impacted mental health",
        topics.get(45).get("query"));

    // Round 5
    topics = TopicReader.getTopics(Topics.COVID_ROUND5_UDEL);
    assertEquals(50, topics.keySet().size());

    assertEquals("mRNA vaccine coronavirus mRNA vaccine SARS-CoV-2 virus",
            topics.get(50).get("query"));
  }

  @Test
  public void testCovidTopics_TopicIdsAsStrings() {
    Map<String, Map<String, String>> topics;

    // Round 1
    topics = TopicReader.getTopicsWithStringIds(Topics.COVID_ROUND1);

    assertEquals(30, topics.keySet().size());

    assertEquals("coronavirus origin", topics.get("1").get("query"));
    assertEquals("what is the origin of COVID-19", topics.get("1").get("question"));
    assertEquals("seeking range of information about the SARS-CoV-2 virus's origin, " +
            "including its evolution, animal source, and first transmission into humans",
        topics.get("1").get("narrative"));

    assertEquals("coronavirus remdesivir", topics.get("30").get("query"));
    assertEquals("is remdesivir an effective treatment for COVID-19", topics.get("30").get("question"));
    assertEquals(
        "seeking specific information on clinical outcomes in COVID-19 patients treated with remdesivir",
        topics.get("30").get("narrative"));

    // Round 2
    topics = TopicReader.getTopicsWithStringIds(Topics.COVID_ROUND2);
    assertEquals(35, topics.keySet().size());

    assertEquals("coronavirus public datasets", topics.get("35").get("query"));

    // Round 3
    topics = TopicReader.getTopicsWithStringIds(Topics.COVID_ROUND3);
    assertEquals(40, topics.keySet().size());

    assertEquals("coronavirus mutations", topics.get("40").get("query"));

    // Round 4
    topics = TopicReader.getTopicsWithStringIds(Topics.COVID_ROUND4);
    assertEquals(45, topics.keySet().size());

    assertEquals("coronavirus mental health impact", topics.get("45").get("query"));

    // Round 5
    topics = TopicReader.getTopicsWithStringIds(Topics.COVID_ROUND5);
    assertEquals(50, topics.keySet().size());

    assertEquals("mRNA vaccine coronavirus", topics.get("50").get("query"));
  }

  @Test
  public void testCovidTopicsUDel_TopicIdsAsStrings() {
    Map<String, Map<String, String>> topics;

    // Round 1
    topics = TopicReader.getTopicsWithStringIds(Topics.COVID_ROUND1_UDEL);
    assertEquals(30, topics.keySet().size());

    assertEquals("coronavirus remdesivir remdesivir effective treatment COVID-19",
        topics.get("30").get("query"));

    // Round 2
    topics = TopicReader.getTopicsWithStringIds(Topics.COVID_ROUND2_UDEL);
    assertEquals(35, topics.keySet().size());

    assertEquals("coronavirus public datasets public datasets COVID-19",
        topics.get("35").get("query"));

    // Round 3
    topics = TopicReader.getTopicsWithStringIds(Topics.COVID_ROUND3_UDEL);
    assertEquals(40, topics.keySet().size());

    assertEquals("coronavirus mutations observed mutations SARS-CoV-2 genome mutations",
        topics.get("40").get("query"));

    // Round 4
    topics = TopicReader.getTopicsWithStringIds(Topics.COVID_ROUND4_UDEL);
    assertEquals(45, topics.keySet().size());

    assertEquals("coronavirus mental health impact COVID-19 pandemic impacted mental health",
        topics.get("45").get("query"));

    // Round 5
    topics = TopicReader.getTopicsWithStringIds(Topics.COVID_ROUND5_UDEL);
    assertEquals(50, topics.keySet().size());

    assertEquals("mRNA vaccine coronavirus mRNA vaccine SARS-CoV-2 virus",
            topics.get("50").get("query"));
  }

  @Test
  public void testBackgroundLinkingTopics() {
    Map<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.TREC2018_BL);

    Integer[] keys = topics.keySet().toArray(new Integer[0]);
    Integer firstKey = keys[0];
    Integer lastKey = keys[keys.length - 1];

    assertEquals(50, topics.keySet().size());
    assertEquals(321, (int) firstKey);
    assertEquals("9171debc316e5e2782e0d2404ca7d09d", topics.get(firstKey).get("title"));
    assertEquals("https://www.washingtonpost.com/news/worldviews/wp/2016/09/01/" +
        "women-are-half-of-the-world-but-only-22-percent-of-its-parliaments/",
        topics.get(firstKey).get("url"));

    assertEquals(825, (int) lastKey);
    assertEquals("a1c41a70-35c7-11e3-8a0e-4e2cf80831fc", topics.get(lastKey).get("title"));
    assertEquals("https://www.washingtonpost.com/business/economy/" +
        "cellulosic-ethanol-once-the-way-of-the-future-is-off-to-a-delayed-boisterous-start/" +
        "2013/11/08/a1c41a70-35c7-11e3-8a0e-4e2cf80831fc_story.html", topics.get(lastKey).get("url"));

    topics = TopicReader.getTopics(Topics.TREC2019_BL);

    keys = topics.keySet().toArray(new Integer[0]);
    firstKey = keys[0];
    lastKey = keys[keys.length - 1];

    assertEquals(60, topics.keySet().size());
    assertEquals(826, (int) firstKey);
    assertEquals("96ab542e-6a07-11e6-ba32-5a4bf5aad4fa", topics.get(firstKey).get("title"));
    assertEquals("https://www.washingtonpost.com/sports/nationals/" +
        "the-minor-leagues-life-in-pro-baseballs-shadowy-corner/" +
        "2016/08/26/96ab542e-6a07-11e6-ba32-5a4bf5aad4fa_story.html", topics.get(firstKey).get("url"));

    assertEquals(885, (int) lastKey);
    assertEquals("5ae44bfd66a49bcad7b55b29b55d63b6", topics.get(lastKey).get("title"));
    assertEquals("https://www.washingtonpost.com/news/capital-weather-gang/wp/2017/07/14/" +
        "sun-erupts-to-mark-another-bastille-day-aurora-possible-in-new-england-sunday-night/",
        topics.get(lastKey).get("url"));
  }

  @Test
  public void testEpidemicQATopics() {
    Map<Integer, Map<String, String>> consumerTopics;
    consumerTopics = TopicReader.getTopics(Topics.EPIDEMIC_QA_CONSUMER_PRELIM);

    Integer[] consumerKeys = consumerTopics.keySet().toArray(new Integer[0]);
    Integer consumerFirstKey = consumerKeys[0];
    Integer consumerLastKey = consumerKeys[consumerKeys.length - 1];

    // No consumer questions from CQ035 to CQ037
    assertEquals(42, consumerTopics.keySet().size());
    assertEquals(1, (int) consumerFirstKey);
    assertEquals("what is the origin of COVID-19",
                 consumerTopics.get(consumerFirstKey).get("question"));
    assertEquals("CQ001", consumerTopics.get(consumerFirstKey).get("question_id"));
    assertEquals("coronavirus origin", consumerTopics.get(consumerFirstKey).get("query"));
    // There's a typo in this but the same typo is present in the topics
    // document.
    assertEquals("seeking information about whether the virus was designed in a lab or occured "+
                 "naturally in animals and how it got to humans",
                 consumerTopics.get(consumerFirstKey).get("background"));

    assertEquals(45, (int) consumerLastKey);
    assertEquals("how has the COVID-19 pandemic impacted mental health?",
                 consumerTopics.get(consumerLastKey).get("question"));
    assertEquals("CQ045", consumerTopics.get(consumerLastKey).get("question_id"));
    assertEquals("coronavirus mental health impact",
                 consumerTopics.get(consumerLastKey).get("query"));
    assertEquals("seeking information about psychological effects of COVID-19 and "+
                 "COVID-19 effect on mental health and pre-existing conditions",
                 consumerTopics.get(consumerLastKey).get("background"));

    Map<Integer, Map<String, String>> expertTopics;
    expertTopics = TopicReader.getTopics(Topics.EPIDEMIC_QA_EXPERT_PRELIM);

    Integer[] expertKeys = expertTopics.keySet().toArray(new Integer[0]);
    Integer expertFirstKey = expertKeys[0];
    Integer expertLastKey = expertKeys[expertKeys.length - 1];

    assertEquals(45, expertTopics.keySet().size());

    assertEquals(1, (int) expertFirstKey);
    assertEquals("what is the origin of COVID-19",
                 expertTopics.get(expertFirstKey).get("question"));
    assertEquals("EQ001", expertTopics.get(expertFirstKey).get("question_id"));
    assertEquals("coronavirus origin", expertTopics.get(expertFirstKey).get("query"));
    assertEquals("seeking range of information about the SARS-CoV-2 virus's origin, " +
                 "including its evolution, animal source, and first transmission into humans",
                 expertTopics.get(expertFirstKey).get("background"));

    assertEquals(45, (int) expertLastKey);
    assertEquals("How has the COVID-19 pandemic impacted mental health?",
                 expertTopics.get(expertLastKey).get("question"));
    assertEquals("EQ045", expertTopics.get(expertLastKey).get("question_id"));
    assertEquals("coronavirus mental health impact",
                 expertTopics.get(expertLastKey).get("query"));
    assertEquals("Includes increasing/decreasing rates of depression, anxiety, panic disorder, "+
                 "and other psychiatric and mental health conditions.",
                 expertTopics.get(expertLastKey).get("background"));
  }

  public void testGetTopicsWithStringIdsFromFileWithTopicReader() {
    Map<String, Map<String, String>> topics;

    topics = TopicReader.getTopicsWithStringIdsFromFileWithTopicReaderClass(TrecTopicReader.class.getName(),
        "src/main/resources/topics-and-qrels/topics.robust04.txt");

    assertNotNull(topics);
    assertEquals(250, topics.size());
    assertEquals("International Organized Crime", topics.get("301").get("title"));
    assertEquals("gasoline tax U.S.", topics.get("700").get("title"));

    topics = TopicReader.getTopicsWithStringIdsFromFileWithTopicReaderClass(TsvIntTopicReader.class.getName(),
        "src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt");
    assertNotNull(topics);
    assertEquals(5193, topics.size());
    assertEquals("androgen receptor define", topics.get("2").get("title"));
    assertEquals("why do bears hibernate", topics.get("1102400").get("title"));
  }
}
