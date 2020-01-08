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

import java.util.Map;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TopicReaderTest {

  @Test
  public void testNewswireTopics() {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.TREC1_ADHOC);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(51, (int) topics.firstKey());
    assertEquals("Airbus Subsidies", topics.get(topics.firstKey()).get("title"));
    assertEquals(100, (int) topics.lastKey());
    assertEquals("Controlling the Transfer of High Technology", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2_ADHOC);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(101, (int) topics.firstKey());
    assertEquals("Design of the \"Star Wars\" Anti-missile Defense System", topics.get(topics.firstKey()).get("title"));
    assertEquals(150, (int) topics.lastKey());
    assertEquals("U.S. Political Campaign Financing", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.TREC3_ADHOC);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(151, (int) topics.firstKey());
    assertEquals("Coping with overcrowded prisons", topics.get(topics.firstKey()).get("title"));
    assertEquals(200, (int) topics.lastKey());
    assertEquals("Impact of foreign textile imports on U.S.", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.ROBUST04);
    assertNotNull(topics);
    assertEquals(250, topics.size());
    assertEquals(301, (int) topics.firstKey());
    assertEquals("International Organized Crime", topics.get(topics.firstKey()).get("title"));
    assertEquals(700, (int) topics.lastKey());
    assertEquals("gasoline tax U.S.", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.ROBUST05);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(303, (int) topics.firstKey());
    assertEquals("Hubble Telescope Achievements", topics.get(topics.firstKey()).get("title"));
    assertEquals(689, (int) topics.lastKey());
    assertEquals("family-planning aid", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.CORE17);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(307, (int) topics.firstKey());
    assertEquals("New Hydroelectric Projects", topics.get(topics.firstKey()).get("title"));
    assertEquals(690, (int) topics.lastKey());
    assertEquals("college education advantage", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.CORE18);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(321, (int) topics.firstKey());
    assertEquals("Women in Parliaments", topics.get(topics.firstKey()).get("title"));
    assertEquals(825, (int) topics.lastKey());
    assertEquals("ethanol and food prices", topics.get(topics.lastKey()).get("title"));
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
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.WT10G);
    assertNotNull(topics);
    assertEquals(100, topics.size());
    assertEquals(451, (int) topics.firstKey());
    assertEquals("What is a Bengals cat?", topics.get(topics.firstKey()).get("title"));
    assertEquals(550, (int) topics.lastKey());
    assertEquals("how are the volcanoes made?", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2004_TERABYTE);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(701, (int) topics.firstKey());
    assertEquals("U.S. oil industry history", topics.get(topics.firstKey()).get("title"));
    assertEquals(750, (int) topics.lastKey());
    assertEquals("John Edwards womens issues", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2005_TERABYTE);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(751, (int) topics.firstKey());
    assertEquals("Scrabble Players", topics.get(topics.firstKey()).get("title"));
    assertEquals(800, (int) topics.lastKey());
    assertEquals("Ovarian Cancer Treatment", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2006_TERABYTE);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(801, (int) topics.firstKey());
    assertEquals("Kudzu Pueraria lobata", topics.get(topics.firstKey()).get("title"));
    assertEquals(850, (int) topics.lastKey());
    assertEquals("Mississippi River flood", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2010_WEB);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(51, (int) topics.firstKey());
    assertEquals("horse hooves", topics.get(topics.firstKey()).get("title"));
    assertEquals(100, (int) topics.lastKey());
    assertEquals("rincon puerto rico", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2011_WEB);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(101, (int) topics.firstKey());
    assertEquals("ritz carlton lake las vegas", topics.get(topics.firstKey()).get("title"));
    assertEquals(150, (int) topics.lastKey());
    assertEquals("tn highway patrol", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2012_WEB);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(151, (int) topics.firstKey());
    assertEquals("403b", topics.get(topics.firstKey()).get("title"));
    assertEquals(200, (int) topics.lastKey());
    assertEquals("ontario california airport", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2013_WEB);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(201, (int) topics.firstKey());
    assertEquals("raspberry pi", topics.get(topics.firstKey()).get("title"));
    assertEquals(250, (int) topics.lastKey());
    assertEquals("ford edge problems", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2014_WEB);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(251, (int) topics.firstKey());
    assertEquals("identifying spider bites", topics.get(topics.firstKey()).get("title"));
    assertEquals(300, (int) topics.lastKey());
    assertEquals("how to find the mean", topics.get(topics.lastKey()).get("title"));
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
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.MB11);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(1, (int) topics.firstKey());
    assertEquals("BBC World Service staff cuts", topics.get(topics.firstKey()).get("title"));
    assertEquals(50, (int) topics.lastKey());
    assertEquals("war prisoners, Hatch Act", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.MB12);
    assertNotNull(topics);
    assertEquals(60, topics.size());
    assertEquals(51, (int) topics.firstKey());
    assertEquals("British Government cuts", topics.get(topics.firstKey()).get("title"));
    assertEquals(110, (int) topics.lastKey());
    assertEquals("economic trade sanctions", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.MB13);
    assertNotNull(topics);
    assertEquals(60, topics.size());
    assertEquals(111, (int) topics.firstKey());
    assertEquals("water shortages", topics.get(topics.firstKey()).get("title"));
    assertEquals(170, (int) topics.lastKey());
    assertEquals("Tony Mendez", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.MB14);
    assertNotNull(topics);
    assertEquals(55, topics.size());
    assertEquals(171, (int) topics.firstKey());
    assertEquals("Ron Weasley birthday", topics.get(topics.firstKey()).get("title"));
    assertEquals(225, (int) topics.lastKey());
    assertEquals("Barbara Walters, chicken pox", topics.get(topics.lastKey()).get("title"));
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
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.CAR17V15_BENCHMARK_Y1_TEST);
    assertNotNull(topics);
    assertEquals(2125, topics.size());
    assertEquals("Aftertaste/Aftertaste%20processing%20in%20the%20cerebral%20cortex", topics.firstKey());
    assertEquals("Aftertaste/Aftertaste processing in the cerebral cortex", topics.get(topics.firstKey()).get("title"));
    assertEquals("Yellowstone%20National%20Park/Recreation", topics.lastKey());
    assertEquals("Yellowstone National Park/Recreation", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.CAR17V20_BENCHMARK_Y1_TEST);
    assertNotNull(topics);
    assertEquals(2254, topics.size());
    assertEquals("enwiki:Aftertaste", topics.firstKey());
    assertEquals("Aftertaste", topics.get(topics.firstKey()).get("title"));
    assertEquals("enwiki:Yellowstone%20National%20Park/Recreation", topics.lastKey());
    assertEquals("Yellowstone National Park/Recreation", topics.get(topics.lastKey()).get("title"));
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
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.MSMARCO_DOC_DEV);
    assertNotNull(topics);
    assertEquals(5193, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("androgen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why do bears hibernate", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("Androgen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why do bears hibernate", topics.get(topics.lastKey()).get("title"));
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
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.NTCIR8_ZH);
    assertNotNull(topics);
    assertEquals(73, topics.size());
    assertEquals("ACLIA2-CS-0002", topics.firstKey());
    assertEquals("《千里走单骑》和张艺谋是什么关系？", topics.get(topics.firstKey()).get("title"));
    assertEquals("ACLIA2-CS-0100", topics.lastKey());
    assertEquals("为什么美军占领了巴格达？", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.CLEF2006_FR);
    assertNotNull(topics);
    assertEquals(49, topics.size());
    assertEquals("301-AH", topics.firstKey());
    assertEquals("Les Produits Nestlé", topics.get(topics.firstKey()).get("title"));
    assertEquals("350-AH", topics.lastKey());
    assertEquals("Le Décès d'Ayrton Senna", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2002_AR);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(26, (int) topics.firstKey());
    assertEquals("مجلس المقاومة الوطني الكردستاني", topics.get(topics.firstKey()).get("title"));
    assertEquals(75, (int) topics.lastKey());
    assertEquals("فيروسات الكمبيوتر في الوطن العربي", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.FIRE2012_BN);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(176, (int) topics.firstKey());
    assertEquals("ওয়াই এস আর রেড্ডির মৃত্যু", topics.get(topics.firstKey()).get("title"));
    assertEquals(225, (int) topics.lastKey());
    assertEquals("স্যাটানিক ভার্সেস বিতর্ক", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.FIRE2012_HI);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(176, (int) topics.firstKey());
    assertEquals("वाई एस आर रेड्डी की मौत", topics.get(topics.firstKey()).get("title"));
    assertEquals(225, (int) topics.lastKey());
    assertEquals("सेटेनिक वर्सेज विवाद", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.FIRE2012_EN);
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(176, (int) topics.firstKey());
    assertEquals("YSR Reddy death", topics.get(topics.firstKey()).get("title"));
    assertEquals(225, (int) topics.lastKey());
    assertEquals("Satanic Verses controversy", topics.get(topics.lastKey()).get("title"));
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
}
