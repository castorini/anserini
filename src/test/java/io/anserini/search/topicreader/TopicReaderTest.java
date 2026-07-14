/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.junit.Test;

public class TopicReaderTest {

  @Test
  public void testIterateThroughAllTopics() {
    int cnt = 0;
    for (Topics topic : Topics.entries().values()) {
      cnt++; 

      // Verify that we can fetch the TopicReader class given the name of the topic file.
      String path = topic.path;
      assertEquals(topic.readerClass, TopicReader.getTopicReaderClassByFile(path));
    }
    assertEquals(460, cnt);
  }

  @Test
  public void testTopicReaderClassLookup() {
    assertEquals(TrecTopicReader.class, TopicReader.getTopicReaderClassByFile("tools/topics-and-qrels/topics.robust04.txt"));
    assertEquals(TrecTopicReader.class, TopicReader.getTopicReaderClassByFile("topics.robust04.txt"));

    assertEquals(CovidTopicReader.class, TopicReader.getTopicReaderClassByFile("tools/topics-and-qrels/topics.covid-round1.xml"));
    assertEquals(CovidTopicReader.class, TopicReader.getTopicReaderClassByFile("topics.covid-round1.xml"));

    // Unknown TopicReader class.
    assertNull(TopicReader.getTopicReaderClassByFile("topics.unknown.txt"));
  }

  @Test(expected = NullPointerException.class)
  public void testLoadTopicsInvalid() throws IOException {
    TopicReader.load(null);
  }

  @Test
  public void testLoadFileWithTopicReader() throws IOException {
    Path topicsFile = Files.createTempFile("topics", ".tsv");
    Files.write(topicsFile, List.of("1\ttest query"));

    try {
      SortedMap<Integer, Map<String, String>> topics = TopicReader.load(topicsFile.toString(), "TsvInt");
      assertEquals(1, topics.size());
      assertEquals(Integer.valueOf(1), topics.firstKey());
      assertEquals("test query", topics.get(topics.firstKey()).get("title"));
    } finally {
      Files.deleteIfExists(topicsFile);
    }
  }

  @Test
  public void testLoadFileWithoutTopicReader() throws IOException {
    Path topicsFile = Files.createTempFile("topics", ".txt");

    try {
      TopicReader.load(topicsFile.toString(), null);
      fail("Expected IllegalArgumentException to be thrown");
    } catch (IllegalArgumentException e) {
      assertEquals("Must specify the topic reader using -topicReader.", e.getMessage());
    } finally {
      Files.deleteIfExists(topicsFile);
    }
  }

  @Test
  public void testGetTopicsByFile() {
    SortedMap<Object, Map<String, String>> topics = TopicReader.getTopicsByFile("tools/topics-and-qrels/topics.robust04.txt");

    assertNotNull(topics);
    assertEquals(250, topics.size());
    assertEquals(301, (int) topics.firstKey());
    assertEquals("International Organized Crime", topics.get(topics.firstKey()).get("title"));
    assertEquals(700, (int) topics.lastKey());
    assertEquals("gasoline tax U.S.", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void testCacmTopics() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("cacm"));
    assertNotNull(topics);
    assertEquals(64, topics.size());
    assertEquals(1, (int) topics.firstKey());
    assertTrue(topics.get(topics.firstKey()).get("title").contains("What articles exist which deal with TSS"));
    assertEquals(64, (int) topics.lastKey());
    assertTrue(topics.get(topics.lastKey()).get("title").contains("List all articles on EL1 and ECL"));

    // Test that TopicReader.load(Topics.get("cacm")) and Topics.load("cacm")) give the same thing.
    // No need to check for all topics, just this one is sufficient.
    topics = Topics.load("cacm");
    assertNotNull(topics);
    assertEquals(64, topics.size());
    assertEquals(1, (int) topics.firstKey());
    assertTrue(topics.get(topics.firstKey()).get("title").contains("What articles exist which deal with TSS"));
    assertEquals(64, (int) topics.lastKey());
    assertTrue(topics.get(topics.lastKey()).get("title").contains("List all articles on EL1 and ECL"));
  }

  @Test
  public void testLoadTopicsByPath() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    Path path = TopicReader.getTopicPath(Path.of(Topics.get("cacm").path));
    topics = TopicReader.load(path.toString(), "Cacm");
    assertNotNull(topics);
    assertEquals(64, topics.size());
    assertEquals(1, (int) topics.firstKey());
    assertTrue(topics.get(topics.firstKey()).get("title").contains("What articles exist which deal with TSS"));
    assertEquals(64, (int) topics.lastKey());
    assertTrue(topics.get(topics.lastKey()).get("title").contains("List all articles on EL1 and ECL"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadTopicsByPathInvalidTopicReader() throws IOException {
    Path path = TopicReader.getTopicPath(Path.of(Topics.get("cacm").path));
    TopicReader.load(path.toString(), "xxx");
  }

  @Test
  public void testNewswireTopics() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("adhoc.51-100"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(51, (int) topics.firstKey());
    assertEquals("Airbus Subsidies", topics.get(topics.firstKey()).get("title"));
    assertEquals(100, (int) topics.lastKey());
    assertEquals("Controlling the Transfer of High Technology", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("adhoc.101-150"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(101, (int) topics.firstKey());
    assertEquals("Design of the \"Star Wars\" Anti-missile Defense System", topics.get(topics.firstKey()).get("title"));
    assertEquals(150, (int) topics.lastKey());
    assertEquals("U.S. Political Campaign Financing", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("adhoc.151-200"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(151, (int) topics.firstKey());
    assertEquals("Coping with overcrowded prisons", topics.get(topics.firstKey()).get("title"));
    assertEquals(200, (int) topics.lastKey());
    assertEquals("Impact of foreign textile imports on U.S. textile industry", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("robust04"));
    assertNotNull(topics);
    assertEquals(250, topics.size());
    assertEquals(301, (int) topics.firstKey());
    assertEquals("International Organized Crime", topics.get(topics.firstKey()).get("title"));
    assertEquals(700, (int) topics.lastKey());
    assertEquals("gasoline tax U.S.", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("robust05"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(303, (int) topics.firstKey());
    assertEquals("Hubble Telescope Achievements", topics.get(topics.firstKey()).get("title"));
    assertEquals(689, (int) topics.lastKey());
    assertEquals("family-planning aid", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("core17"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(307, (int) topics.firstKey());
    assertEquals("New Hydroelectric Projects", topics.get(topics.firstKey()).get("title"));
    assertEquals(690, (int) topics.lastKey());
    assertEquals("college education advantage", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("core18"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(321, (int) topics.firstKey());
    assertEquals("Women in Parliaments", topics.get(topics.firstKey()).get("title"));
    assertEquals(825, (int) topics.lastKey());
    assertEquals("ethanol and food prices", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void testDseTopics() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("slidevqa.test"));
    assertNotNull(topics);
    assertEquals(2214, topics.size());

    topics = TopicReader.load(Topics.get("wiki-ss-nq.test"));
    assertNotNull(topics);
    assertEquals(3610, topics.size());
  }
  
  @Test
  public void testTrecTitleParsing() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("adhoc.51-100"));
    assertNotNull(topics);
    assertEquals(50, topics.size());

    // Single line titles.
    assertEquals("Airbus Subsidies", topics.get(51).get("title"));
    assertEquals("Controlling the Transfer of High Technology", topics.get(100).get("title"));

    // Multi-line titles.
    assertEquals("Financial crunch for televangelists in the wake of the PTL scandal", topics.get(81).get("title"));
    assertEquals("Criminal Actions Against Officers of Failed Financial Institutions", topics.get(87).get("title"));
    assertEquals("What Backing Does the National Rifle Association Have?", topics.get(93).get("title"));

    topics = TopicReader.load(Topics.get("adhoc.101-150"));
    assertNotNull(topics);
    assertEquals(50, topics.size());

    assertEquals("Industrial Espionage", topics.get(149).get("title"));

    assertEquals("Laser Research Applicable to the U.S.'s Strategic Defense Initiative", topics.get(102).get("title"));
    assertEquals("Impact of Government Regulated Grain Farming on International Relations", topics.get(142).get("title"));
  }

  @Test
  public void testNewswireTopics_TopicIdsAsStrings() throws IOException {
    Map<String, Map<String, String>> topics;

    topics = TopicReader.getTopicsWithStringIds(Topics.get("adhoc.51-100"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Airbus Subsidies", topics.get("51").get("title"));
    assertEquals("Controlling the Transfer of High Technology", topics.get("100").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("adhoc.101-150"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Design of the \"Star Wars\" Anti-missile Defense System", topics.get("101").get("title"));
    assertEquals("U.S. Political Campaign Financing", topics.get("150").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("adhoc.151-200"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Coping with overcrowded prisons", topics.get("151").get("title"));
    assertEquals("Impact of foreign textile imports on U.S. textile industry", topics.get("200").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("robust04"));
    assertNotNull(topics);
    assertEquals(250, topics.size());
    assertEquals("International Organized Crime", topics.get("301").get("title"));
    assertEquals("gasoline tax U.S.", topics.get("700").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("robust05"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Hubble Telescope Achievements", topics.get("303").get("title"));
    assertEquals("family-planning aid", topics.get("689").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("core17"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("New Hydroelectric Projects", topics.get("307").get("title"));
    assertEquals("college education advantage", topics.get("690").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("core18"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Women in Parliaments", topics.get("321").get("title"));
    assertEquals("ethanol and food prices", topics.get("825").get("title"));
  }

  @Test
  public void testWebTopics() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("adhoc.451-550"));
    assertNotNull(topics);
    assertEquals(100, topics.size());
    assertEquals(451, (int) topics.firstKey());
    assertEquals("What is a Bengals cat?", topics.get(topics.firstKey()).get("title"));
    assertEquals(550, (int) topics.lastKey());
    assertEquals("how are the volcanoes made?", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("terabyte04.701-750"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(701, (int) topics.firstKey());
    assertEquals("U.S. oil industry history", topics.get(topics.firstKey()).get("title"));
    assertEquals(750, (int) topics.lastKey());
    assertEquals("John Edwards womens issues", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("terabyte05.751-800"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(751, (int) topics.firstKey());
    assertEquals("Scrabble Players", topics.get(topics.firstKey()).get("title"));
    assertEquals(800, (int) topics.lastKey());
    assertEquals("Ovarian Cancer Treatment", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("terabyte06.801-850"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(801, (int) topics.firstKey());
    assertEquals("Kudzu Pueraria lobata", topics.get(topics.firstKey()).get("title"));
    assertEquals(850, (int) topics.lastKey());
    assertEquals("Mississippi River flood", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("mq.1-10000"));
    assertNotNull(topics);
    assertEquals(10000, topics.keySet().size());
    assertEquals(1, (int) topics.firstKey());
    assertEquals("after school program evaluation", topics.get(topics.firstKey()).get("title").trim());
    assertEquals(10000, (int) topics.lastKey());
    assertEquals("californa mission", topics.get(topics.lastKey()).get("title").trim());

    topics = TopicReader.load(Topics.get("mq.10001-20000"));
    assertNotNull(topics);
    assertEquals(10000, topics.keySet().size());
    assertEquals(10001, (int) topics.firstKey());
    assertEquals("comparability of pay analyses", topics.get(topics.firstKey()).get("title").trim());
    assertEquals(20000, (int) topics.lastKey());
    assertEquals("manchester city hall", topics.get(topics.lastKey()).get("title").trim());

    topics = TopicReader.load(Topics.get("mq.20001-60000"));
    assertNotNull(topics);
    assertEquals(40000, topics.keySet().size());
    assertEquals(20001, (int) topics.firstKey());
    assertEquals("obama family tree", topics.get(topics.firstKey()).get("title").trim());
    assertEquals("1", topics.get(topics.firstKey()).get("priority").trim());
    assertEquals(60000, (int) topics.lastKey());
    assertEquals("bird shingles", topics.get(topics.lastKey()).get("title").trim());
    assertEquals("4", topics.get(topics.lastKey()).get("priority").trim());

    topics = TopicReader.load(Topics.get("web.51-100"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(51, (int) topics.firstKey());
    assertEquals("horse hooves", topics.get(topics.firstKey()).get("title"));
    assertEquals(100, (int) topics.lastKey());
    assertEquals("rincon puerto rico", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("web.101-150"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(101, (int) topics.firstKey());
    assertEquals("ritz carlton lake las vegas", topics.get(topics.firstKey()).get("title"));
    assertEquals(150, (int) topics.lastKey());
    assertEquals("tn highway patrol", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("web.151-200"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(151, (int) topics.firstKey());
    assertEquals("403b", topics.get(topics.firstKey()).get("title"));
    assertEquals(200, (int) topics.lastKey());
    assertEquals("ontario california airport", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("web.201-250"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(201, (int) topics.firstKey());
    assertEquals("raspberry pi", topics.get(topics.firstKey()).get("title"));
    assertEquals(250, (int) topics.lastKey());
    assertEquals("ford edge problems", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("web.251-300"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(251, (int) topics.firstKey());
    assertEquals("identifying spider bites", topics.get(topics.firstKey()).get("title"));
    assertEquals(300, (int) topics.lastKey());
    assertEquals("how to find the mean", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void testWebTopics_TopicIdsAsStrings() throws IOException {
    Map<String, Map<String, String>> topics;

    topics = TopicReader.getTopicsWithStringIds(Topics.get("adhoc.451-550"));
    assertNotNull(topics);
    assertEquals(100, topics.size());
    assertEquals("What is a Bengals cat?", topics.get("451").get("title"));
    assertEquals("how are the volcanoes made?", topics.get("550").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("terabyte04.701-750"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("U.S. oil industry history", topics.get("701").get("title"));
    assertEquals("John Edwards womens issues", topics.get("750").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("terabyte05.751-800"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Scrabble Players", topics.get("751").get("title"));
    assertEquals("Ovarian Cancer Treatment", topics.get("800").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("terabyte06.801-850"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Kudzu Pueraria lobata", topics.get("801").get("title"));
    assertEquals("Mississippi River flood", topics.get("850").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("mq.1-10000"));
    assertNotNull(topics);
    assertEquals(10000, topics.keySet().size());
    assertEquals("after school program evaluation", topics.get("1").get("title").trim());
    assertEquals("californa mission", topics.get("10000").get("title").trim());

    topics = TopicReader.getTopicsWithStringIds(Topics.get("mq.10001-20000"));
    assertNotNull(topics);
    assertEquals(10000, topics.keySet().size());
    assertEquals("comparability of pay analyses", topics.get("10001").get("title").trim());
    assertEquals("manchester city hall", topics.get("20000").get("title").trim());

    topics = TopicReader.getTopicsWithStringIds(Topics.get("mq.20001-60000"));
    assertNotNull(topics);
    assertEquals(40000, topics.keySet().size());
    assertEquals("obama family tree", topics.get("20001").get("title").trim());
    assertEquals("1", topics.get("20001").get("priority").trim());
    assertEquals("bird shingles", topics.get("60000").get("title").trim());
    assertEquals("4", topics.get("60000").get("priority").trim());

    topics = TopicReader.getTopicsWithStringIds(Topics.get("web.51-100"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("horse hooves", topics.get("51").get("title"));
    assertEquals("rincon puerto rico", topics.get("100").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("web.101-150"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("ritz carlton lake las vegas", topics.get("101").get("title"));
    assertEquals("tn highway patrol", topics.get("150").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("web.151-200"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("403b", topics.get("151").get("title"));
    assertEquals("ontario california airport", topics.get("200").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("web.201-250"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("raspberry pi", topics.get("201").get("title"));
    assertEquals("ford edge problems", topics.get("250").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("web.251-300"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("identifying spider bites", topics.get("251").get("title"));
    assertEquals("how to find the mean", topics.get("300").get("title"));
  }

  @Test
  public void testMicoblogTopics() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("microblog2011"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(1, (int) topics.firstKey());
    assertEquals("BBC World Service staff cuts", topics.get(topics.firstKey()).get("title"));
    assertEquals(50, (int) topics.lastKey());
    assertEquals("war prisoners, Hatch Act", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("microblog2012"));
    assertNotNull(topics);
    assertEquals(60, topics.size());
    assertEquals(51, (int) topics.firstKey());
    assertEquals("British Government cuts", topics.get(topics.firstKey()).get("title"));
    assertEquals(110, (int) topics.lastKey());
    assertEquals("economic trade sanctions", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("microblog2013"));
    assertNotNull(topics);
    assertEquals(60, topics.size());
    assertEquals(111, (int) topics.firstKey());
    assertEquals("water shortages", topics.get(topics.firstKey()).get("title"));
    assertEquals(170, (int) topics.lastKey());
    assertEquals("Tony Mendez", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("microblog2014"));
    assertNotNull(topics);
    assertEquals(55, topics.size());
    assertEquals(171, (int) topics.firstKey());
    assertEquals("Ron Weasley birthday", topics.get(topics.firstKey()).get("title"));
    assertEquals(225, (int) topics.lastKey());
    assertEquals("Barbara Walters, chicken pox", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void testMicoblogTopics_TopicIdsAsStrings() throws IOException {
    Map<String, Map<String, String>> topics;

    topics = TopicReader.getTopicsWithStringIds(Topics.get("microblog2011"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("BBC World Service staff cuts", topics.get("1").get("title"));
    assertEquals("war prisoners, Hatch Act", topics.get("50").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("microblog2012"));
    assertNotNull(topics);
    assertEquals(60, topics.size());
    assertEquals("British Government cuts", topics.get("51").get("title"));
    assertEquals("economic trade sanctions", topics.get("110").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("microblog2013"));
    assertNotNull(topics);
    assertEquals(60, topics.size());
    assertEquals("water shortages", topics.get("111").get("title"));
    assertEquals("Tony Mendez", topics.get("170").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("microblog2014"));
    assertNotNull(topics);
    assertEquals(55, topics.size());
    assertEquals("Ron Weasley birthday", topics.get("171").get("title"));
    assertEquals("Barbara Walters, chicken pox", topics.get("225").get("title"));
  }

  @Test
  public void testCAR() throws IOException {
    SortedMap<String, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("car17v1.5.benchmarkY1test"));
    assertNotNull(topics);
    assertEquals(2125, topics.size());
    assertEquals("Aftertaste/Aftertaste%20processing%20in%20the%20cerebral%20cortex", topics.firstKey());
    assertEquals("Aftertaste/Aftertaste processing in the cerebral cortex", topics.get(topics.firstKey()).get("title"));
    assertEquals("Yellowstone%20National%20Park/Recreation", topics.lastKey());
    assertEquals("Yellowstone National Park/Recreation", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("car17v2.0.benchmarkY1test"));
    assertNotNull(topics);
    assertEquals(2254, topics.size());
    assertEquals("enwiki:Aftertaste", topics.firstKey());
    assertEquals("Aftertaste", topics.get(topics.firstKey()).get("title"));
    assertEquals("enwiki:Yellowstone%20National%20Park/Recreation", topics.lastKey());
    assertEquals("Yellowstone National Park/Recreation", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void testCAR_TopicIdsAsStrings() throws IOException {
    Map<String, Map<String, String>> topics;

    topics = TopicReader.getTopicsWithStringIds(Topics.get("car17v1.5.benchmarkY1test"));
    assertNotNull(topics);
    assertEquals(2125, topics.size());
    assertEquals("Aftertaste/Aftertaste processing in the cerebral cortex",
        topics.get("Aftertaste/Aftertaste%20processing%20in%20the%20cerebral%20cortex").get("title"));
    assertEquals("Yellowstone National Park/Recreation",
        topics.get("Yellowstone%20National%20Park/Recreation").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("car17v2.0.benchmarkY1test"));
    assertNotNull(topics);
    assertEquals(2254, topics.size());
    assertEquals("Aftertaste",
        topics.get("enwiki:Aftertaste").get("title"));
    assertEquals("Yellowstone National Park/Recreation",
        topics.get("enwiki:Yellowstone%20National%20Park/Recreation").get("title"));  }

  @Test
  public void testDprNq() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("dpr.nq.dev"));
    assertNotNull(topics);
    assertEquals(8757, topics.size());
    assertEquals(0, (int) topics.firstKey());
    assertEquals("who sings does he love me with reba", topics.get(topics.firstKey()).get("title"));
    assertEquals("['Linda Davis']", topics.get(topics.firstKey()).get("answers"));
    assertEquals(8756, (int) topics.lastKey());
    assertEquals("when did the gop take control of the house", topics.get(topics.lastKey()).get("title"));
    assertEquals("['2010']", topics.get(topics.lastKey()).get("answers"));
    assertEquals("who did the artwork for pink floyd 's wall", topics.get(1726).get("title"));

    topics = TopicReader.load(Topics.get("dpr.nq.test"));
    assertNotNull(topics);
    assertEquals(3610, topics.size());
    assertEquals(0, (int) topics.firstKey());
    assertEquals("who got the first nobel prize in physics", topics.get(topics.firstKey()).get("title"));
    assertEquals("['Wilhelm Conrad Röntgen']", topics.get(topics.firstKey()).get("answers"));
    assertEquals(3609, (int) topics.lastKey());
    assertEquals("when did computer become widespread in homes and schools", topics.get(topics.lastKey()).get("title"));
    assertEquals("['1980s']", topics.get(topics.lastKey()).get("answers"));
    assertEquals("who sings gim me shelter with mick jagger", topics.get(1756).get("title"));
  }

  @Test
  public void testDprTrivia() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("dpr.trivia.dev"));
    assertNotNull(topics);
    assertEquals(8837, topics.size());
    assertEquals(0, (int) topics.firstKey());
    assertEquals("The VS-300 was a type of what?", topics.get(topics.firstKey()).get("title"));
    assertEquals("['🚁', 'Helicopters', 'Civilian helicopter', 'Pescara (helicopter)', 'Cargo helicopter', 'Copter', 'Helecopter', 'List of deadliest helicopter crashes', 'Helichopper', 'Helocopter', 'Cargo Helicopter', 'Helicopter', 'Helicoptor', 'Anatomy of a helicopter']", topics.get(topics.firstKey()).get("answers"));
    assertEquals(8836, (int) topics.lastKey());
    assertEquals("Name the artist and the title of this 1978 classic that remains popular today: We were at the beach Everybody had matching towels Somebody went under a dock And there they saw a rock It wasnt a rock", topics.get(topics.lastKey()).get("title"));
    assertEquals("['Rock Lobster by the B-52s']", topics.get(topics.lastKey()).get("answers"));

    topics = TopicReader.load(Topics.get("dpr.trivia.test"));
    assertNotNull(topics);
    assertEquals(11313, topics.size());
    assertEquals(0, (int) topics.firstKey());
    assertEquals("Who was the man behind The Chipmunks?", topics.get(topics.firstKey()).get("title"));
    assertEquals("['David Seville']", topics.get(topics.firstKey()).get("answers"));
    assertEquals(11312, (int) topics.lastKey());
    assertEquals("In what outdoor sport, sanctioned by the NHPA, do you score 3 points for a ringer, 2 for a leaner, and the closet scores a point?", topics.get(topics.lastKey()).get("title"));
    assertEquals("['Horseshoe pit', 'Horseshoes (game)', 'Horseshoes', 'Horseshoe Pitching', 'Horse shoes', 'Horseshoe pitching', 'Horseshoe throwing']", topics.get(topics.lastKey()).get("answers"));
  }

  @Test
  public void testDprWq() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("dpr.wq.test"));
    assertNotNull(topics);
    assertEquals(2032, topics.size());
    assertEquals(0, (int) topics.firstKey());
    assertEquals("what does jamaican people speak?", topics.get(topics.firstKey()).get("title"));
    assertEquals("[\"Jamaican Creole English Language\",\"Jamaican English\"]", topics.get(topics.firstKey()).get("answers"));
    assertEquals(2031, (int) topics.lastKey());
    assertEquals("when was father chris riley born?", topics.get(topics.lastKey()).get("title"));
    assertEquals("[\"1967\"]", topics.get(topics.lastKey()).get("answers"));
  }

  @Test
  public void testDprCurated() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("dpr.curated.test"));
    assertNotNull(topics);
    assertEquals(694, topics.size());
    assertEquals(0, (int) topics.firstKey());
    assertEquals("How tall is Mount McKinley?", topics.get(topics.firstKey()).get("title"));
    assertEquals("[\"20\\\\s?,?\\\\s?(32|40)0\\\\s?-?\\\\s?f(ee|oo)t|6,194-meter|20,?237\\\\s*f(oo|ee)?t|20,?073\\\\s*f(oo|ee)?t|6,?168\\\\s*m|6,118\\\\s*m|6\\\\,194 m|20\\\\,322 feet\"]", topics.get(topics.firstKey()).get("answers"));
    assertEquals(693, (int) topics.lastKey());
    assertEquals("What state is the geographic center of the lower 48 states?", topics.get(topics.lastKey()).get("title"));
    assertEquals("[\"Kansas\"]", topics.get(topics.lastKey()).get("answers"));
  }

  @Test
  public void testDprSquad() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("dpr.squad.test"));
    assertNotNull(topics);
    assertEquals(10570, topics.size());
    assertEquals(0, (int) topics.firstKey());
    assertEquals("Which NFL team represented the AFC at Super Bowl 50?", topics.get(topics.firstKey()).get("title"));
    assertEquals("[\"Denver Broncos\",\"Denver Broncos\",\"Denver Broncos\"]", topics.get(topics.firstKey()).get("answers"));
    assertEquals(10569, (int) topics.lastKey());
    assertEquals("What is the seldom used force unit equal to one thousand newtons?", topics.get(topics.lastKey()).get("title"));
    assertEquals("[\"sthène\",\"sthène\",\"sthène\",\"sthène\",\"sthène\"]", topics.get(topics.lastKey()).get("answers"));
  }

  @Test
  public void testNq() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("nq.dev"));
    assertNotNull(topics);
    assertEquals(8757, topics.size());
    assertEquals(0, (int) topics.firstKey());
    assertEquals("who sings does he love me with reba", topics.get(topics.firstKey()).get("title"));
    assertEquals("['Linda Davis']", topics.get(topics.firstKey()).get("answers"));
    assertEquals(8756, (int) topics.lastKey());
    assertEquals("when did the gop take control of the house", topics.get(topics.lastKey()).get("title"));
    assertEquals("['2010']", topics.get(topics.lastKey()).get("answers"));
    assertEquals("who did the artwork for pink floyd's wall", topics.get(1726).get("title"));

    topics = TopicReader.load(Topics.get("nq.test"));
    assertNotNull(topics);
    assertEquals(3610, topics.size());
    assertEquals(0, (int) topics.firstKey());
    assertEquals("who got the first nobel prize in physics", topics.get(topics.firstKey()).get("title"));
    assertEquals("['Wilhelm Conrad Röntgen']", topics.get(topics.firstKey()).get("answers"));
    assertEquals(3609, (int) topics.lastKey());
    assertEquals("when did computer become widespread in homes and schools", topics.get(topics.lastKey()).get("title"));
    assertEquals("['1980s']", topics.get(topics.lastKey()).get("answers"));
    assertEquals("who sings gimme shelter with mick jagger", topics.get(1756).get("title"));
  }

  @Test
  public void testGarT5Nq() throws IOException {
    assertEquals(3610, TopicReader.load(Topics.get("nq.test.gar-t5.answers")).keySet().size());
    assertEquals(3610, TopicReader.load(Topics.get("nq.test.gar-t5.titles")).keySet().size());
    assertEquals(3610, TopicReader.load(Topics.get("nq.test.gar-t5.sentences")).keySet().size());
    assertEquals(3610, TopicReader.load(Topics.get("nq.test.gar-t5.all")).keySet().size());
  }

  @Test
  public void testGarT5Trivia() throws IOException {
    assertEquals(11313, TopicReader.load(Topics.get("dpr.trivia.test.gar-t5.answers")).keySet().size());
    assertEquals(11313, TopicReader.load(Topics.get("dpr.trivia.test.gar-t5.titles")).keySet().size());
    assertEquals(11313, TopicReader.load(Topics.get("dpr.trivia.test.gar-t5.sentences")).keySet().size());
    assertEquals(11313, TopicReader.load(Topics.get("dpr.trivia.test.gar-t5.all")).keySet().size());
  }

  @Test
  public void testTREC19DL() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("dl19-passage"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals("anthropological definition of environment", topics.get(topics.firstKey()).get("title"));
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals("how is the weather in jamaica", topics.get(topics.lastKey()).get("title"));
    assertEquals("does legionella pneumophila cause pneumonia", topics.get(168216).get("title"));

    topics = TopicReader.load(Topics.get("dl19-passage.wp"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals("anthropological definition of environment", topics.get(topics.firstKey()).get("title"));
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals("how is the weather in jamaica", topics.get(topics.lastKey()).get("title"));
    assertEquals("does legion ##ella p ##ne ##um ##op ##hila cause pneumonia", topics.get(168216).get("title"));

    topics = TopicReader.load(Topics.get("dl19-passage.unicoil.0shot"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(695, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(595, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl19-passage.unicoil-noexp.0shot"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(668, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(586, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl19-passage.splade_distil_cocodenser_medium"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(1890, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(1382, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl19-passage.splade-pp-ed"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(28088, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(18791, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl19-passage.splade-pp-sd"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(28936, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(17675, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl19-doc"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals("anthropological definition of environment", topics.get(topics.firstKey()).get("title"));
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals("how is the weather in jamaica", topics.get(topics.lastKey()).get("title"));
    assertEquals("how long to hold bow in yoga", topics.get(1132213).get("title"));

    topics = TopicReader.load(Topics.get("dl19-doc.wp"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals("anthropological definition of environment", topics.get(topics.firstKey()).get("title"));
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals("how is the weather in jamaica", topics.get(topics.lastKey()).get("title"));
    assertEquals("how long to hold bow in yoga", topics.get(1132213).get("title"));

    topics = TopicReader.load(Topics.get("dl19-doc.unicoil.0shot"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(695, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(595, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl19-doc.unicoil-noexp.0shot"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(668, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(586, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl19-passage.splade_distil_cocodenser_medium"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(1890, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(1382, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl19-passage.splade-pp-ed"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(28088, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(18791, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl19-passage.splade-pp-sd"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(28936, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(17675, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl19-passage.cohere-embed-english-v3.0"));
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals("[-0.010772705", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals("[0.016159058", topics.get(topics.lastKey()).get("vector").split(",")[0]);
  }

  @Test
  public void testTREC20DL() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("dl20"));
    assertNotNull(topics);
    assertEquals(200, topics.size());
    assertEquals(3505, (int) topics.firstKey());
    assertEquals("how do they do open heart surgery", topics.get(topics.firstKey()).get("title"));
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals("why did the ancient egyptians call their land kemet, or black land?", topics.get(topics.lastKey()).get("title"));
    assertEquals("who is aziz hashim", topics.get(1030303).get("title"));

    topics = TopicReader.load(Topics.get("dl20.wp"));
    assertNotNull(topics);
    assertEquals(200, topics.size());
    assertEquals(3505, (int) topics.firstKey());
    assertEquals("how do they do open heart surgery", topics.get(topics.firstKey()).get("title"));
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals("why did the ancient egyptians call their land ke ##met , or black land ?", topics.get(topics.lastKey()).get("title"));
    assertEquals("who is aziz hash ##im", topics.get(1030303).get("title"));

    topics = TopicReader.load(Topics.get("dl20.unicoil.0shot"));
    assertNotNull(topics);
    assertEquals(200, topics.size());
    assertEquals(3505, (int) topics.firstKey());
    assertEquals(706, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals(1169, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl20.unicoil-noexp.0shot"));
    assertNotNull(topics);
    assertEquals(200, topics.size());
    assertEquals(3505, (int) topics.firstKey());
    assertEquals(689, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals(1164, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl20.splade_distil_cocodenser_medium"));
    assertNotNull(topics);
    assertEquals(54, topics.size());
    assertEquals(23849, (int) topics.firstKey());
    assertEquals(2168, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals(2075, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl20.splade-pp-ed"));
    assertNotNull(topics);
    assertEquals(200, topics.size());
    assertEquals(3505, (int) topics.firstKey());
    assertEquals(30361, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals(25909, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl20.splade-pp-sd"));
    assertNotNull(topics);
    assertEquals(200, topics.size());
    assertEquals(3505, (int) topics.firstKey());
    assertEquals(35114, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals(30994, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl20.cohere-embed-english-v3.0"));
    assertNotNull(topics);
    assertEquals(200, topics.size());
    assertEquals(3505, (int) topics.firstKey());
    assertEquals("[0.008285522", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals("[0.0056495667", topics.get(topics.lastKey()).get("vector").split(",")[0]);
  }

  @Test
  public void testTREC21DL() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("dl21"));
    assertNotNull(topics);
    assertEquals(477, topics.size());
    assertEquals(2082, (int) topics.firstKey());
    assertEquals("At about what age do adults normally begin to lose bone mass?", topics.get(topics.firstKey()).get("title"));
    assertEquals(1136769, (int) topics.lastKey());
    assertEquals("why does lacquered brass tarnish", topics.get(topics.lastKey()).get("title"));
    assertEquals("who killed nicholas ii of russia", topics.get(1043135).get("title"));

    topics = TopicReader.load(Topics.get("dl21.unicoil.0shot"));
    assertNotNull(topics);
    assertEquals(477, topics.size());
    assertEquals(2082, (int) topics.firstKey());
    assertEquals(693, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136769, (int) topics.lastKey());
    assertEquals(712, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl21.unicoil-noexp.0shot"));
    assertNotNull(topics);
    assertEquals(477, topics.size());
    assertEquals(2082, (int) topics.firstKey());
    assertEquals(624, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136769, (int) topics.lastKey());
    assertEquals(633, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl21.splade-pp-ed"));
    assertNotNull(topics);
    assertEquals(477, topics.size());
    assertEquals(2082, (int) topics.firstKey());
    assertEquals(23936, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136769, (int) topics.lastKey());
    assertEquals(25398, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl21.splade-pp-sd"));
    assertNotNull(topics);
    assertEquals(477, topics.size());
    assertEquals(2082, (int) topics.firstKey());
    assertEquals(26369, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136769, (int) topics.lastKey());
    assertEquals(27149, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl21.snowflake-arctic-embed-l"));
    assertNotNull(topics);
    assertEquals(477, topics.size());
    assertEquals(2082, (int) topics.firstKey());
    assertEquals("[-0.0054801227524876595", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(1136769, (int) topics.lastKey());
    assertEquals("[0.0038787610828876495", topics.get(topics.lastKey()).get("vector").split(",")[0]);
  }

  @Test
  public void testTREC22DL() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("dl22"));
    assertNotNull(topics);
    assertEquals(500, topics.size());
    assertEquals(588, (int) topics.firstKey());
    assertEquals("1099 b cost basis i sell specific shares", topics.get(topics.firstKey()).get("title"));
    assertEquals(2056473, (int) topics.lastKey());
    assertEquals("is a dairy farm considered as an agriculture", topics.get(topics.lastKey()).get("title"));
    assertEquals("how does magic leap optics work", topics.get(2056323).get("title"));

    topics = TopicReader.load(Topics.get("dl22.unicoil.0shot"));
    assertNotNull(topics);
    assertEquals(500, topics.size());
    assertEquals(588, (int) topics.firstKey());
    assertEquals(1016, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(2056473, (int) topics.lastKey());
    assertEquals(720, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl22.unicoil-noexp.0shot"));
    assertNotNull(topics);
    assertEquals(500, topics.size());
    assertEquals(588, (int) topics.firstKey());
    assertEquals(900, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(2056473, (int) topics.lastKey());
    assertEquals(726, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl22.splade-pp-ed"));
    assertNotNull(topics);
    assertEquals(500, topics.size());
    assertEquals(588, (int) topics.firstKey());
    assertEquals(25701, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(2056473, (int) topics.lastKey());
    assertEquals(28012, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl22.splade-pp-sd"));
    assertNotNull(topics);
    assertEquals(500, topics.size());
    assertEquals(588, (int) topics.firstKey());
    assertEquals(31052, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(2056473, (int) topics.lastKey());
    assertEquals(33891, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl22.snowflake-arctic-embed-l"));
    assertNotNull(topics);
    assertEquals(500, topics.size());
    assertEquals(588, (int) topics.firstKey());
    assertEquals("[0.020797204226255417", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(2056473, (int) topics.lastKey());
    assertEquals("[0.005524440202862024", topics.get(topics.lastKey()).get("vector").split(",")[0]);
  }

  @Test
  public void testTREC23DL() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("dl23"));
    assertNotNull(topics);
    assertEquals(700, topics.size());
    assertEquals(2000138, (int) topics.firstKey());
    assertEquals("How does the process of digestion and metabolism of carbohydrates start", topics.get(topics.firstKey()).get("title"));
    assertEquals(3100949, (int) topics.lastKey());
    assertEquals("How do birth control and hormone levels affect menstrual cycle variations?", topics.get(topics.lastKey()).get("title"));
    assertEquals("How do birth control and hormone levels affect menstrual cycle variations?", topics.get(3100949).get("title"));

    topics = TopicReader.load(Topics.get("dl23.unicoil.0shot"));
    assertNotNull(topics);
    assertEquals(700, topics.size());
    assertEquals(2000138, (int) topics.firstKey());
    assertEquals(34407, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(3100949, (int) topics.lastKey());
    assertEquals(31334, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl23.unicoil-noexp.0shot"));
    assertNotNull(topics);
    assertEquals(700, topics.size());
    assertEquals(2000138, (int) topics.firstKey());
    assertEquals(37993, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(3100949, (int) topics.lastKey());
    assertEquals(31283, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl23.splade-pp-ed"));
    assertNotNull(topics);
    assertEquals(700, topics.size());
    assertEquals(2000138, (int) topics.firstKey());
    assertEquals(138500, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(3100949, (int) topics.lastKey());
    assertEquals(139500, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl23.splade-pp-sd"));
    assertNotNull(topics);
    assertEquals(700, topics.size());
    assertEquals(2000138, (int) topics.firstKey());
    assertEquals(163500, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(3100949, (int) topics.lastKey());
    assertEquals(181700, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("dl23.snowflake-arctic-embed-l"));
    assertNotNull(topics);
    assertEquals(700, topics.size());
    assertEquals(2000138, (int) topics.firstKey());
    assertEquals("[0.001558756805025041", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(3100949, (int) topics.lastKey());
    assertEquals("[0.014963677152991295", topics.get(topics.lastKey()).get("vector").split(",")[0]);
  }

  @Test
  public void testTREC24_RAG_RAGGY_DEV() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("rag24.raggy-dev"));
    assertNotNull(topics);
    assertEquals(120, topics.size());
    assertEquals(23287, (int) topics.firstKey());
    assertEquals("are landlords liable if someone breaks in a hurts tenant", topics.get(topics.firstKey()).get("title"));
    assertEquals(3100918, (int) topics.lastKey());
    assertEquals("Can older adults gain strength by training once per week?", topics.get(topics.lastKey()).get("title"));
    assertEquals("Can older adults gain strength by training once per week?", topics.get(3100918).get("title"));

    topics = TopicReader.load(Topics.get("rag24.raggy-dev.snowflake-arctic-embed-l"));
    assertNotNull(topics);
    assertEquals(120, topics.size());
    assertEquals(23287, (int) topics.firstKey());
    assertEquals("[0.008992074057459831", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(3100918, (int) topics.lastKey());
    assertEquals("[0.010409535840153694", topics.get(topics.lastKey()).get("vector").split(",")[0]);
  }

  @Test
  public void testTREC24_RAG_RESEARCHY_DEV() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("rag24.researchy-dev"));
    assertNotNull(topics);
    assertEquals(600, topics.size());
    assertEquals(429, (int) topics.firstKey());
    assertEquals("how do cafeteria-style plans increase costs for employers?", topics.get(topics.firstKey()).get("title"));
    assertEquals(1009569, (int) topics.lastKey());
    assertEquals("how do video games improve problem solving", topics.get(topics.lastKey()).get("title"));
    assertEquals("how do video games improve problem solving", topics.get(1009569).get("title"));

    topics = TopicReader.load(Topics.get("rag24.researchy-dev.snowflake-arctic-embed-l"));
    assertNotNull(topics);
    assertEquals(600, topics.size());
    assertEquals(429, (int) topics.firstKey());
    assertEquals("[0.03783365339040756", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(1009569, (int) topics.lastKey());
    assertEquals("[0.029290692880749702", topics.get(topics.lastKey()).get("vector").split(",")[0]);
  }

  @Test
  public void testTREC24_RAG_TEST() throws IOException {
    SortedMap<String, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("rag24.test"));
    assertNotNull(topics);
    assertEquals(301, topics.size());
    assertEquals("2024-105741", topics.firstKey());
    assertEquals("is it dangerous to have wbc over 15,000 without treatment?", topics.get(topics.firstKey()).get("title"));
    assertEquals("2024-96485", topics.lastKey());
    assertEquals("how would advance electronics course impact students", topics.get(topics.lastKey()).get("title"));
    assertEquals("how the solar eclipse can affect mental health", topics.get("2024-79154").get("title"));

    topics = TopicReader.load(Topics.get("rag24.test.snowflake-arctic-embed-l"));
    assertNotNull(topics);
    assertEquals(301, topics.size());
    assertEquals("2024-105741", topics.firstKey());
    assertEquals("[-0.009175633080303669", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals("2024-96485", topics.lastKey());
    assertEquals("[0.017953362315893173", topics.get(topics.lastKey()).get("vector").split(",")[0]);
  }

  @Test
  public void testTREC25_RAG_TEST() throws IOException {
    SortedMap<String, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("rag25.test"));
    assertNotNull(topics);
    assertEquals(105, topics.size());
    assertEquals("100", topics.firstKey());
    assertEquals("I'm trying to understand the various forms of discrimination and oppression people experience in the US, such as racial, gender, age, and housing. Can you explain their prevalence, how they affect individuals and society, and what laws or actions are in place to address them?", topics.get(topics.firstKey()).get("title"));
    assertEquals("988", topics.lastKey());
    assertEquals("I'm looking into the complex issue of immigration, particularly the challenges surrounding illegal immigration and the struggles immigrants encounter in the US. I'm also interested in how sanctuary cities add further complications to the overall immigration landscape.", topics.get(topics.lastKey()).get("title"));
    assertEquals("I want to deeply understand the Holocaust: what it was, why and how it transpired, who was responsible, and its profound historical and societal impact, particularly on European Jewry. I'm also curious about its conclusion, lasting effects, and how it aligns with other destructive historical events like Sodom and Gomorrah.", topics.get("200").get("title"));
  }

  @Test
  public void testMSMARCO_V1() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("msmarco-doc.dev"));
    assertNotNull(topics);
    assertEquals(5193, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("androgen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why do bears hibernate", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("msmarco-doc.dev.wp"));
    assertNotNull(topics);
    assertEquals(5193, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("and ##rogen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why do bears hi ##ber ##nate", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("msmarco-doc.dev.unicoil"));
    assertNotNull(topics);
    assertEquals(5193, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(617, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(682, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-doc.dev.unicoil-noexp"));
    assertNotNull(topics);
    assertEquals(5193, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(609, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(577, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-doc.test"));
    assertNotNull(topics);
    assertEquals(5793, topics.size());
    assertEquals(57, (int) topics.firstKey());
    assertEquals("term service agreement definition", topics.get(topics.firstKey()).get("title"));
    assertEquals(1136966, (int) topics.lastKey());
    assertEquals("#ffffff color code", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("msmarco-passage.dev-subset"));
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("Androgen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why do bears hibernate", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("msmarco-passage.dev-subset.wp"));
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("and ##rogen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why do bears hi ##ber ##nate", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("msmarco-passage.dev-subset.deepimpact"));
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("receptor androgen define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why hibernate bears", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("msmarco-passage.dev-subset.unicoil"));
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(619, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(686, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-passage.dev-subset.unicoil-noexp"));
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(609, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(577, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-passage.dev-subset.unicoil-tilde-expansion"));
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(584, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(610, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-passage.dev-subset.distill-splade-max"));
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(1991, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(2409, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-passage.dev-subset.splade_distil_cocodenser_medium"));
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(1695, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(1682, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-passage.dev-subset.splade-pp-ed"));
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(21944, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(24271, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-passage.dev-subset.splade-pp-sd"));
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(25539, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(30718, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-passage.dev-subset.cohere-embed-english-v3.0"));
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("[0.00864410400390625", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("[0.0107421875", topics.get(topics.lastKey()).get("vector").split(",")[0]);

    topics = TopicReader.load(Topics.get("msmarco-passage.test-subset"));
    assertNotNull(topics);
    assertEquals(6837, topics.size());
    assertEquals(57, (int) topics.firstKey());
    assertEquals("term service agreement definition", topics.get(topics.firstKey()).get("title"));
    assertEquals(1136966, (int) topics.lastKey());
    assertEquals("#ffffff color code", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void testMSMARCO_V2() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("msmarco-v2-doc.dev"));
    assertNotNull(topics);
    assertEquals(4552, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("Androgen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals("why do children get aggressive", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("msmarco-v2-doc.dev.unicoil.0shot"));
    assertNotNull(topics);
    assertEquals(4552, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(617, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals(608, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-v2-doc.dev.unicoil-noexp.0shot"));
    assertNotNull(topics);
    assertEquals(4552, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(609, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals(533, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-v2-doc.dev.snowflake-arctic-embed-l"));
    assertNotNull(topics);
    assertEquals(4552, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("[0.02950862981379032", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals("[-0.04409797489643097", topics.get(topics.lastKey()).get("vector").split(",")[0]);

    topics = TopicReader.load(Topics.get("msmarco-v2-doc.dev2"));
    assertNotNull(topics);
    assertEquals(5000, topics.size());
    assertEquals(361, (int) topics.firstKey());
    assertEquals(". irritability medical definition", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals("why do a ferritin level", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("msmarco-v2-doc.dev2.unicoil.0shot"));
    assertNotNull(topics);
    assertEquals(5000, topics.size());
    assertEquals(361, (int) topics.firstKey());
    assertEquals(714, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals(664, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-v2-doc.dev2.unicoil-noexp.0shot"));
    assertNotNull(topics);
    assertEquals(5000, topics.size());
    assertEquals(361, (int) topics.firstKey());
    assertEquals(690, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals(537, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-v2-doc.dev2.snowflake-arctic-embed-l"));
    assertNotNull(topics);
    assertEquals(5000, topics.size());
    assertEquals(361, (int) topics.firstKey());
    assertEquals("[0.002593959914520383", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals("[0.006848456338047981", topics.get(topics.lastKey()).get("vector").split(",")[0]);

    topics = TopicReader.load(Topics.get("msmarco-v2-passage.dev"));
    assertNotNull(topics);
    assertEquals(3903, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("Androgen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals("why do children get aggressive", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("msmarco-v2-passage.dev.unicoil.0shot"));
    assertNotNull(topics);
    assertEquals(3903, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(617, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals(608, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-v2-passage.dev.unicoil-noexp.0shot"));
    assertNotNull(topics);
    assertEquals(3903, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(609, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals(533, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-v2-passage.dev.splade-pp-ed"));
    assertNotNull(topics);
    assertEquals(3903, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(21944, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals(30978, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-v2-passage.dev.splade-pp-sd"));
    assertNotNull(topics);
    assertEquals(3903, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(25539, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals(35354, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-v2-passage.dev2"));
    assertNotNull(topics);
    assertEquals(4281, topics.size());
    assertEquals(1325, (int) topics.firstKey());
    assertEquals("323 area code zip code", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals("why do a ferritin level", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("msmarco-v2-passage.dev2.unicoil.0shot"));
    assertNotNull(topics);
    assertEquals(4281, topics.size());
    assertEquals(1325, (int) topics.firstKey());
    assertEquals(671, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals(664, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-v2-passage.dev2.unicoil-noexp.0shot"));
    assertNotNull(topics);
    assertEquals(4281, topics.size());
    assertEquals(1325, (int) topics.firstKey());
    assertEquals(649, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals(537, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-v2-passage.dev2.splade-pp-ed"));
    assertNotNull(topics);
    assertEquals(4281, topics.size());
    assertEquals(1325, (int) topics.firstKey());
    assertEquals(14928, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals(18984, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.load(Topics.get("msmarco-v2-passage.dev2.splade-pp-sd"));
    assertNotNull(topics);
    assertEquals(4281, topics.size());
    assertEquals(1325, (int) topics.firstKey());
    assertEquals(15862, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals(23387, topics.get(topics.lastKey()).get("title").split(" ").length);
  }

  @Test
  public void testMSMARO_TopicIdsAsStrings() throws IOException {
    Map<String, Map<String, String>> topics;

    topics = TopicReader.getTopicsWithStringIds(Topics.get("msmarco-doc.dev"));
    assertNotNull(topics);
    assertEquals(5193, topics.size());
    assertEquals("androgen receptor define", topics.get("2").get("title"));
    assertEquals("why do bears hibernate", topics.get("1102400").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("msmarco-doc.test"));
    assertNotNull(topics);
    assertEquals(5793, topics.size());
    assertEquals("term service agreement definition", topics.get("57").get("title"));
    assertEquals("#ffffff color code", topics.get("1136966").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("msmarco-passage.dev-subset"));
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals("Androgen receptor define", topics.get("2").get("title"));
    assertEquals("why do bears hibernate", topics.get("1102400").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("msmarco-passage.test-subset"));
    assertNotNull(topics);
    assertEquals(6837, topics.size());
    assertEquals("term service agreement definition", topics.get("57").get("title"));
    assertEquals("#ffffff color code", topics.get("1136966").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("msmarco-v2-doc.dev"));
    assertNotNull(topics);
    assertEquals(4552, topics.size());
    assertEquals("Androgen receptor define", topics.get("2").get("title"));
    assertEquals("why do children get aggressive", topics.get("1102390").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("msmarco-v2-doc.dev2"));
    assertNotNull(topics);
    assertEquals(5000, topics.size());
    assertEquals(". irritability medical definition", topics.get("361").get("title"));
    assertEquals("why do a ferritin level", topics.get("1102413").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("msmarco-v2-passage.dev"));
    assertNotNull(topics);
    assertEquals(3903, topics.size());
    assertEquals("Androgen receptor define", topics.get("2").get("title"));
    assertEquals("why do children get aggressive", topics.get("1102390").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("msmarco-v2-passage.dev2"));
    assertNotNull(topics);
    assertEquals(4281, topics.size());
    assertEquals("323 area code zip code", topics.get("1325").get("title"));
    assertEquals("why do a ferritin level", topics.get("1102413").get("title"));
  }

  @Test
  public void testNonEnglishTopics1() throws IOException {
    SortedMap<String, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("ntcir8zh.eval"));
    assertNotNull(topics);
    assertEquals(73, topics.size());
    assertEquals("ACLIA2-CS-0002", topics.firstKey());
    assertEquals("《千里走单骑》和张艺谋是什么关系？", topics.get(topics.firstKey()).get("title"));
    assertEquals("ACLIA2-CS-0100", topics.lastKey());
    assertEquals("为什么美军占领了巴格达？", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("clef06fr.mono.fr"));
    assertNotNull(topics);
    assertEquals(49, topics.size());
    assertEquals("301-AH", topics.firstKey());
    assertEquals("Les Produits Nestlé", topics.get(topics.firstKey()).get("title"));
    assertEquals("350-AH", topics.lastKey());
    assertEquals("Le Décès d'Ayrton Senna", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void testNonEnglishTopics2() throws IOException {
      SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("trec02ar-ar"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(26, (int) topics.firstKey());
    assertEquals("مجلس المقاومة الوطني الكردستاني", topics.get(topics.firstKey()).get("title"));
    assertEquals(75, (int) topics.lastKey());
    assertEquals("فيروسات الكمبيوتر في الوطن العربي", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("fire12bn.176-225"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(176, (int) topics.firstKey());
    assertEquals("ওয়াই এস আর রেড্ডির মৃত্যু", topics.get(topics.firstKey()).get("title"));
    assertEquals(225, (int) topics.lastKey());
    assertEquals("স্যাটানিক ভার্সেস বিতর্ক", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("fire12hi.176-225"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(176, (int) topics.firstKey());
    assertEquals("वाई एस आर रेड्डी की मौत", topics.get(topics.firstKey()).get("title"));
    assertEquals(225, (int) topics.lastKey());
    assertEquals("सेटेनिक वर्सेज विवाद", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.load(Topics.get("fire12en.176-225"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals(176, (int) topics.firstKey());
    assertEquals("YSR Reddy death", topics.get(topics.firstKey()).get("title"));
    assertEquals(225, (int) topics.lastKey());
    assertEquals("Satanic Verses controversy", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void testNonEnglishTopics_TopicIdsAsStrings() throws IOException {
    Map<String, Map<String, String>> topics;

    topics = TopicReader.getTopicsWithStringIds(Topics.get("adhoc.51-100"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("Airbus Subsidies", topics.get("51").get("title"));
    assertEquals("Controlling the Transfer of High Technology", topics.get("100").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("ntcir8zh.eval"));
    assertNotNull(topics);
    assertEquals(73, topics.size());
    assertEquals("《千里走单骑》和张艺谋是什么关系？", topics.get("ACLIA2-CS-0002").get("title"));
    assertEquals("为什么美军占领了巴格达？", topics.get("ACLIA2-CS-0100").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("clef06fr.mono.fr"));
    assertNotNull(topics);
    assertEquals(49, topics.size());
    assertEquals("Les Produits Nestlé", topics.get("301-AH").get("title"));
    assertEquals("Le Décès d'Ayrton Senna", topics.get("350-AH").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("trec02ar-ar"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("مجلس المقاومة الوطني الكردستاني", topics.get("26").get("title"));
    assertEquals("فيروسات الكمبيوتر في الوطن العربي", topics.get("75").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("fire12bn.176-225"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("ওয়াই এস আর রেড্ডির মৃত্যু", topics.get("176").get("title"));
    assertEquals("স্যাটানিক ভার্সেস বিতর্ক", topics.get("225").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("fire12hi.176-225"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("वाई एस आर रेड्डी की मौत", topics.get("176").get("title"));
    assertEquals("सेटेनिक वर्सेज विवाद", topics.get("225").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.get("fire12en.176-225"));
    assertNotNull(topics);
    assertEquals(50, topics.size());
    assertEquals("YSR Reddy death", topics.get("176").get("title"));
    assertEquals("Satanic Verses controversy", topics.get("225").get("title"));
  }

  @Test
  public void testCovidTopics() throws IOException {
    Map<Integer, Map<String, String>> topics;

    // Round 1
    topics = TopicReader.load(Topics.get("covid-round1"));
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
    topics = TopicReader.load(Topics.get("covid-round2"));
    assertEquals(35, topics.keySet().size());

    assertEquals("coronavirus public datasets", topics.get(35).get("query"));

    // Round 3
    topics = TopicReader.load(Topics.get("covid-round3"));
    assertEquals(40, topics.keySet().size());

    assertEquals("coronavirus mutations", topics.get(40).get("query"));

    // Round 4
    topics = TopicReader.load(Topics.get("covid-round4"));
    assertEquals(45, topics.keySet().size());

    assertEquals("coronavirus mental health impact", topics.get(45).get("query"));

    // Round 5
    topics = TopicReader.load(Topics.get("covid-round5"));
    assertEquals(50, topics.keySet().size());

    assertEquals("mRNA vaccine coronavirus", topics.get(50).get("query"));
  }

  @Test
  public void testCovidTopicsUDel() throws IOException {
    Map<Integer, Map<String, String>> topics;

    // Round 1
    topics = TopicReader.load(Topics.get("covid-round1-udel"));
    assertEquals(30, topics.keySet().size());

    assertEquals("coronavirus remdesivir remdesivir effective treatment COVID-19",
        topics.get(30).get("query"));

    // Round 2
    topics = TopicReader.load(Topics.get("covid-round2-udel"));
    assertEquals(35, topics.keySet().size());

    assertEquals("coronavirus public datasets public datasets COVID-19",
        topics.get(35).get("query"));

    // Round 3
    topics = TopicReader.load(Topics.get("covid-round3-udel"));
    assertEquals(40, topics.keySet().size());

    assertEquals("coronavirus mutations observed mutations SARS-CoV-2 genome mutations",
        topics.get(40).get("query"));

    // Round 4
    topics = TopicReader.load(Topics.get("covid-round4-udel"));
    assertEquals(45, topics.keySet().size());

    assertEquals("coronavirus mental health impact COVID-19 pandemic impacted mental health",
        topics.get(45).get("query"));

    // Round 5
    topics = TopicReader.load(Topics.get("covid-round5-udel"));
    assertEquals(50, topics.keySet().size());

    assertEquals("mRNA vaccine coronavirus mRNA vaccine SARS-CoV-2 virus",
            topics.get(50).get("query"));
  }

  @Test
  public void testCovidTopics_TopicIdsAsStrings() throws IOException {
    Map<String, Map<String, String>> topics;

    // Round 1
    topics = TopicReader.getTopicsWithStringIds(Topics.get("covid-round1"));

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
    topics = TopicReader.getTopicsWithStringIds(Topics.get("covid-round2"));
    assertEquals(35, topics.keySet().size());

    assertEquals("coronavirus public datasets", topics.get("35").get("query"));

    // Round 3
    topics = TopicReader.getTopicsWithStringIds(Topics.get("covid-round3"));
    assertEquals(40, topics.keySet().size());

    assertEquals("coronavirus mutations", topics.get("40").get("query"));

    // Round 4
    topics = TopicReader.getTopicsWithStringIds(Topics.get("covid-round4"));
    assertEquals(45, topics.keySet().size());

    assertEquals("coronavirus mental health impact", topics.get("45").get("query"));

    // Round 5
    topics = TopicReader.getTopicsWithStringIds(Topics.get("covid-round5"));
    assertEquals(50, topics.keySet().size());

    assertEquals("mRNA vaccine coronavirus", topics.get("50").get("query"));
  }

  @Test
  public void testCovidTopicsUDel_TopicIdsAsStrings() throws IOException {
    Map<String, Map<String, String>> topics;

    // Round 1
    topics = TopicReader.getTopicsWithStringIds(Topics.get("covid-round1-udel"));
    assertEquals(30, topics.keySet().size());

    assertEquals("coronavirus remdesivir remdesivir effective treatment COVID-19",
        topics.get("30").get("query"));

    // Round 2
    topics = TopicReader.getTopicsWithStringIds(Topics.get("covid-round2-udel"));
    assertEquals(35, topics.keySet().size());

    assertEquals("coronavirus public datasets public datasets COVID-19",
        topics.get("35").get("query"));

    // Round 3
    topics = TopicReader.getTopicsWithStringIds(Topics.get("covid-round3-udel"));
    assertEquals(40, topics.keySet().size());

    assertEquals("coronavirus mutations observed mutations SARS-CoV-2 genome mutations",
        topics.get("40").get("query"));

    // Round 4
    topics = TopicReader.getTopicsWithStringIds(Topics.get("covid-round4-udel"));
    assertEquals(45, topics.keySet().size());

    assertEquals("coronavirus mental health impact COVID-19 pandemic impacted mental health",
        topics.get("45").get("query"));

    // Round 5
    topics = TopicReader.getTopicsWithStringIds(Topics.get("covid-round5-udel"));
    assertEquals(50, topics.keySet().size());

    assertEquals("mRNA vaccine coronavirus mRNA vaccine SARS-CoV-2 virus",
            topics.get("50").get("query"));
  }

  @Test
  public void testBackgroundLinkingTopics() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.load(Topics.get("backgroundlinking18"));

    assertEquals(50, topics.keySet().size());
    assertEquals(321, (int) topics.firstKey());
    assertEquals("9171debc316e5e2782e0d2404ca7d09d", topics.get(topics.firstKey()).get("title"));
    assertEquals("https://www.washingtonpost.com/news/worldviews/wp/2016/09/01/" +
        "women-are-half-of-the-world-but-only-22-percent-of-its-parliaments/",
        topics.get(topics.firstKey()).get("url"));

    assertEquals(825, (int) topics.lastKey());
    assertEquals("a1c41a70-35c7-11e3-8a0e-4e2cf80831fc", topics.get(topics.lastKey()).get("title"));
    assertEquals("https://www.washingtonpost.com/business/economy/" +
        "cellulosic-ethanol-once-the-way-of-the-future-is-off-to-a-delayed-boisterous-start/" +
        "2013/11/08/a1c41a70-35c7-11e3-8a0e-4e2cf80831fc_story.html", topics.get(topics.lastKey()).get("url"));

    topics = TopicReader.load(Topics.get("backgroundlinking19"));
    
    assertEquals(60, topics.keySet().size());
    assertEquals(826, (int) topics.firstKey());
    assertEquals("96ab542e-6a07-11e6-ba32-5a4bf5aad4fa", topics.get(topics.firstKey()).get("title"));
    assertEquals("https://www.washingtonpost.com/sports/nationals/" +
        "the-minor-leagues-life-in-pro-baseballs-shadowy-corner/" +
        "2016/08/26/96ab542e-6a07-11e6-ba32-5a4bf5aad4fa_story.html", topics.get(topics.firstKey()).get("url"));

    assertEquals(885, (int) topics.lastKey());
    assertEquals("5ae44bfd66a49bcad7b55b29b55d63b6", topics.get(topics.lastKey()).get("title"));
    assertEquals("https://www.washingtonpost.com/news/capital-weather-gang/wp/2017/07/14/" +
        "sun-erupts-to-mark-another-bastille-day-aurora-possible-in-new-england-sunday-night/",
        topics.get(topics.lastKey()).get("url"));

    topics = TopicReader.load(Topics.get("backgroundlinking20"));

    assertEquals(50, topics.keySet().size());
    assertEquals(886, (int) topics.firstKey());
    assertEquals("AEQZNZSVT5BGPPUTTJO7SNMOLE", topics.get(topics.firstKey()).get("title"));
    assertEquals("https://www.washingtonpost.com/politics/2019/06/05/" +
        "trump-says-transgender-troops-cant-serve-because-troops-cant-take-any-drugs-hes-wrong-many-ways/",
        topics.get(topics.firstKey()).get("url"));

    assertEquals(935, (int) topics.lastKey());
    assertEquals("CCUJNXOJNFEJFBL57GD27EHMWI", topics.get(topics.lastKey()).get("title"));
    assertEquals("https://www.washingtonpost.com/news/to-your-health/wp/2018/05/30/" +
        "this-mock-pandemic-killed-150-million-people-next-time-it-might-not-be-a-drill/",
        topics.get(topics.lastKey()).get("url"));
  }

  @Test
  public void testEpidemicQATopics() throws IOException {
    SortedMap<Integer, Map<String, String>> consumerTopics;
    consumerTopics = TopicReader.load(Topics.get("epidemic-qa.consumer.prelim"));

    // No consumer questions from CQ035 to CQ037
    assertEquals(42, consumerTopics.keySet().size());
    assertEquals(1, (int) consumerTopics.firstKey());
    assertEquals("what is the origin of COVID-19",
                 consumerTopics.get(consumerTopics.firstKey()).get("question"));
    assertEquals("CQ001", consumerTopics.get(consumerTopics.firstKey()).get("question_id"));
    assertEquals("coronavirus origin", consumerTopics.get(consumerTopics.firstKey()).get("query"));
    // There's a typo in this but the same typo is present in the topics 
    // document.
    assertEquals("seeking information about whether the virus was designed in a lab or occured "+
                 "naturally in animals and how it got to humans",
                 consumerTopics.get(consumerTopics.firstKey()).get("background"));

    assertEquals(45, (int) consumerTopics.lastKey());
    assertEquals("how has the COVID-19 pandemic impacted mental health?",
                 consumerTopics.get(consumerTopics.lastKey()).get("question"));
    assertEquals("CQ045", consumerTopics.get(consumerTopics.lastKey()).get("question_id"));
    assertEquals("coronavirus mental health impact",
                 consumerTopics.get(consumerTopics.lastKey()).get("query"));
    assertEquals("seeking information about psychological effects of COVID-19 and "+
                 "COVID-19 effect on mental health and pre-existing conditions",
                 consumerTopics.get(consumerTopics.lastKey()).get("background"));

    SortedMap<Integer, Map<String, String>> expertTopics;
    expertTopics = TopicReader.load(Topics.get("epidemic-qa.expert.prelim"));

    assertEquals(45, expertTopics.keySet().size());

    assertEquals(1, (int) expertTopics.firstKey());
    assertEquals("what is the origin of COVID-19",
                 expertTopics.get(expertTopics.firstKey()).get("question"));
    assertEquals("EQ001", expertTopics.get(expertTopics.firstKey()).get("question_id"));
    assertEquals("coronavirus origin", expertTopics.get(expertTopics.firstKey()).get("query"));
    assertEquals("seeking range of information about the SARS-CoV-2 virus's origin, " + 
                 "including its evolution, animal source, and first transmission into humans",
                 expertTopics.get(expertTopics.firstKey()).get("background"));

    assertEquals(45, (int) expertTopics.lastKey());
    assertEquals("How has the COVID-19 pandemic impacted mental health?",
                 expertTopics.get(expertTopics.lastKey()).get("question"));
    assertEquals("EQ045", expertTopics.get(expertTopics.lastKey()).get("question_id"));
    assertEquals("coronavirus mental health impact",
                 expertTopics.get(expertTopics.lastKey()).get("query"));
    assertEquals("Includes increasing/decreasing rates of depression, anxiety, panic disorder, "+
                 "and other psychiatric and mental health conditions.",
                 expertTopics.get(expertTopics.lastKey()).get("background"));
  }

  @Test
  public void testMrTyDiTopics() throws IOException {
    assertEquals(12377, TopicReader.load(Topics.get("mrtydi-v1.1-ar.train")).keySet().size());
    assertEquals(3115, TopicReader.load(Topics.get("mrtydi-v1.1-ar.dev")).keySet().size());
    assertEquals(1081, TopicReader.load(Topics.get("mrtydi-v1.1-ar.test")).keySet().size());

    assertEquals(1713, TopicReader.load(Topics.get("mrtydi-v1.1-bn.train")).keySet().size());
    assertEquals(440, TopicReader.load(Topics.get("mrtydi-v1.1-bn.dev")).keySet().size());
    assertEquals(111, TopicReader.load(Topics.get("mrtydi-v1.1-bn.test")).keySet().size());

    assertEquals(3547, TopicReader.load(Topics.get("mrtydi-v1.1-en.train")).keySet().size());
    assertEquals(878, TopicReader.load(Topics.get("mrtydi-v1.1-en.dev")).keySet().size());
    assertEquals(744, TopicReader.load(Topics.get("mrtydi-v1.1-en.test")).keySet().size());

    assertEquals(6561, TopicReader.load(Topics.get("mrtydi-v1.1-fi.train")).keySet().size());
    assertEquals(1738, TopicReader.load(Topics.get("mrtydi-v1.1-fi.dev")).keySet().size());
    assertEquals(1254, TopicReader.load(Topics.get("mrtydi-v1.1-fi.test")).keySet().size());

    assertEquals(4902, TopicReader.load(Topics.get("mrtydi-v1.1-id.train")).keySet().size());
    assertEquals(1224, TopicReader.load(Topics.get("mrtydi-v1.1-id.dev")).keySet().size());
    assertEquals(829, TopicReader.load(Topics.get("mrtydi-v1.1-id.test")).keySet().size());

    assertEquals(3697, TopicReader.load(Topics.get("mrtydi-v1.1-ja.train")).keySet().size());
    assertEquals(928, TopicReader.load(Topics.get("mrtydi-v1.1-ja.dev")).keySet().size());
    assertEquals(720, TopicReader.load(Topics.get("mrtydi-v1.1-ja.test")).keySet().size());

    assertEquals(1295, TopicReader.load(Topics.get("mrtydi-v1.1-ko.train")).keySet().size());
    assertEquals(303, TopicReader.load(Topics.get("mrtydi-v1.1-ko.dev")).keySet().size());
    assertEquals(421, TopicReader.load(Topics.get("mrtydi-v1.1-ko.test")).keySet().size());

    assertEquals(5366, TopicReader.load(Topics.get("mrtydi-v1.1-ru.train")).keySet().size());
    assertEquals(1375, TopicReader.load(Topics.get("mrtydi-v1.1-ru.dev")).keySet().size());
    assertEquals(995, TopicReader.load(Topics.get("mrtydi-v1.1-ru.test")).keySet().size());

    assertEquals(2072, TopicReader.load(Topics.get("mrtydi-v1.1-sw.train")).keySet().size());
    assertEquals(526, TopicReader.load(Topics.get("mrtydi-v1.1-sw.dev")).keySet().size());
    assertEquals(670, TopicReader.load(Topics.get("mrtydi-v1.1-sw.test")).keySet().size());

    assertEquals(3880, TopicReader.load(Topics.get("mrtydi-v1.1-te.train")).keySet().size());
    assertEquals(983, TopicReader.load(Topics.get("mrtydi-v1.1-te.dev")).keySet().size());
    assertEquals(646, TopicReader.load(Topics.get("mrtydi-v1.1-te.test")).keySet().size());

    assertEquals(3319, TopicReader.load(Topics.get("mrtydi-v1.1-th.train")).keySet().size());
    assertEquals(807, TopicReader.load(Topics.get("mrtydi-v1.1-th.dev")).keySet().size());
    assertEquals(1190, TopicReader.load(Topics.get("mrtydi-v1.1-th.test")).keySet().size());
  }

  @Test
  public void testBeirTopics() throws IOException {
    assertEquals(50,    TopicReader.load(Topics.get("beir-v1.0.0-trec-covid.test")).keySet().size());
    assertEquals(500,   TopicReader.load(Topics.get("beir-v1.0.0-bioasq.test")).keySet().size());
    assertEquals(323,   TopicReader.load(Topics.get("beir-v1.0.0-nfcorpus.test")).keySet().size());
    assertEquals(3452,  TopicReader.load(Topics.get("beir-v1.0.0-nq.test")).keySet().size());
    assertEquals(7405,  TopicReader.load(Topics.get("beir-v1.0.0-hotpotqa.test")).keySet().size());
    assertEquals(648,   TopicReader.load(Topics.get("beir-v1.0.0-fiqa.test")).keySet().size());
    assertEquals(97,    TopicReader.load(Topics.get("beir-v1.0.0-signal1m.test")).keySet().size());
    assertEquals(57,    TopicReader.load(Topics.get("beir-v1.0.0-trec-news.test")).keySet().size());
    assertEquals(249,   TopicReader.load(Topics.get("beir-v1.0.0-robust04.test")).keySet().size());
    assertEquals(1406,  TopicReader.load(Topics.get("beir-v1.0.0-arguana.test")).keySet().size());
    assertEquals(49,    TopicReader.load(Topics.get("beir-v1.0.0-webis-touche2020.test")).keySet().size());
    assertEquals(699,   TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-android.test")).keySet().size());
    assertEquals(1570,  TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-english.test")).keySet().size());
    assertEquals(1595,  TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-gaming.test")).keySet().size());
    assertEquals(885,   TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-gis.test")).keySet().size());
    assertEquals(804,   TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-mathematica.test")).keySet().size());
    assertEquals(1039,  TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-physics.test")).keySet().size());
    assertEquals(876,   TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-programmers.test")).keySet().size());
    assertEquals(652,   TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-stats.test")).keySet().size());
    assertEquals(2906,  TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-tex.test")).keySet().size());
    assertEquals(1072,  TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-unix.test")).keySet().size());
    assertEquals(506,   TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-webmasters.test")).keySet().size());
    assertEquals(541,   TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-wordpress.test")).keySet().size());
    assertEquals(10000, TopicReader.load(Topics.get("beir-v1.0.0-quora.test")).keySet().size());
    assertEquals(400,   TopicReader.load(Topics.get("beir-v1.0.0-dbpedia-entity.test")).keySet().size());
    assertEquals(1000,  TopicReader.load(Topics.get("beir-v1.0.0-scidocs.test")).keySet().size());
    assertEquals(6666,  TopicReader.load(Topics.get("beir-v1.0.0-fever.test")).keySet().size());
    assertEquals(1535,  TopicReader.load(Topics.get("beir-v1.0.0-climate-fever.test")).keySet().size());
    assertEquals(300,   TopicReader.load(Topics.get("beir-v1.0.0-scifact.test")).keySet().size());
  }

  @Test
  public void testBeirWPTopics() throws IOException {
    assertEquals(50,    TopicReader.load(Topics.get("beir-v1.0.0-trec-covid.test.wp")).keySet().size());
    assertEquals(500,   TopicReader.load(Topics.get("beir-v1.0.0-bioasq.test.wp")).keySet().size());
    assertEquals(323,   TopicReader.load(Topics.get("beir-v1.0.0-nfcorpus.test.wp")).keySet().size());
    assertEquals(3452,  TopicReader.load(Topics.get("beir-v1.0.0-nq.test.wp")).keySet().size());
    assertEquals(7405,  TopicReader.load(Topics.get("beir-v1.0.0-hotpotqa.test.wp")).keySet().size());
    assertEquals(648,   TopicReader.load(Topics.get("beir-v1.0.0-fiqa.test.wp")).keySet().size());
    assertEquals(97,    TopicReader.load(Topics.get("beir-v1.0.0-signal1m.test.wp")).keySet().size());
    assertEquals(57,    TopicReader.load(Topics.get("beir-v1.0.0-trec-news.test.wp")).keySet().size());
    assertEquals(249,   TopicReader.load(Topics.get("beir-v1.0.0-robust04.test.wp")).keySet().size());
    assertEquals(1406,  TopicReader.load(Topics.get("beir-v1.0.0-arguana.test.wp")).keySet().size());
    assertEquals(49,    TopicReader.load(Topics.get("beir-v1.0.0-webis-touche2020.test.wp")).keySet().size());
    assertEquals(699,   TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-android.test.wp")).keySet().size());
    assertEquals(1570,  TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-english.test.wp")).keySet().size());
    assertEquals(1595,  TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-gaming.test.wp")).keySet().size());
    assertEquals(885,   TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-gis.test.wp")).keySet().size());
    assertEquals(804,   TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-mathematica.test.wp")).keySet().size());
    assertEquals(1039,  TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-physics.test.wp")).keySet().size());
    assertEquals(876,   TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-programmers.test.wp")).keySet().size());
    assertEquals(652,   TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-stats.test.wp")).keySet().size());
    assertEquals(2906,  TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-tex.test.wp")).keySet().size());
    assertEquals(1072,  TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-unix.test.wp")).keySet().size());
    assertEquals(506,   TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-webmasters.test.wp")).keySet().size());
    assertEquals(541,   TopicReader.load(Topics.get("beir-v1.0.0-cqadupstack-wordpress.test.wp")).keySet().size());
    assertEquals(10000, TopicReader.load(Topics.get("beir-v1.0.0-quora.test.wp")).keySet().size());
    assertEquals(400,   TopicReader.load(Topics.get("beir-v1.0.0-dbpedia-entity.test.wp")).keySet().size());
    assertEquals(1000,  TopicReader.load(Topics.get("beir-v1.0.0-scidocs.test.wp")).keySet().size());
    assertEquals(6666,  TopicReader.load(Topics.get("beir-v1.0.0-fever.test.wp")).keySet().size());
    assertEquals(1535,  TopicReader.load(Topics.get("beir-v1.0.0-climate-fever.test.wp")).keySet().size());
    assertEquals(300,   TopicReader.load(Topics.get("beir-v1.0.0-scifact.test.wp")).keySet().size());
  }

  @Test
  public void testBrightTopics() throws IOException {
    assertEquals(103, TopicReader.load(Topics.get("bright-biology")).keySet().size());
    assertEquals(116, TopicReader.load(Topics.get("bright-earth-science")).keySet().size());
    assertEquals(103, TopicReader.load(Topics.get("bright-economics")).keySet().size());
    assertEquals(101, TopicReader.load(Topics.get("bright-psychology")).keySet().size());
    assertEquals(101, TopicReader.load(Topics.get("bright-robotics")).keySet().size());
    assertEquals(117, TopicReader.load(Topics.get("bright-stackoverflow")).keySet().size());
    assertEquals(108, TopicReader.load(Topics.get("bright-sustainable-living")).keySet().size());
    assertEquals(112, TopicReader.load(Topics.get("bright-pony")).keySet().size());
    assertEquals(142, TopicReader.load(Topics.get("bright-leetcode")).keySet().size());
    assertEquals(111, TopicReader.load(Topics.get("bright-aops")).keySet().size());
    assertEquals(76, TopicReader.load(Topics.get("bright-theoremqa-theorems")).keySet().size());
    assertEquals(194, TopicReader.load(Topics.get("bright-theoremqa-questions")).keySet().size());
  }

  @Test
  public void testBrightOriginalTopics() throws IOException {
    assertEquals(103, TopicReader.load(Topics.get("bright-biology-original")).keySet().size());
    assertEquals(116, TopicReader.load(Topics.get("bright-earth-science-original")).keySet().size());
    assertEquals(103, TopicReader.load(Topics.get("bright-economics-original")).keySet().size());
    assertEquals(101, TopicReader.load(Topics.get("bright-psychology-original")).keySet().size());
    assertEquals(101, TopicReader.load(Topics.get("bright-robotics-original")).keySet().size());
    assertEquals(117, TopicReader.load(Topics.get("bright-stackoverflow-original")).keySet().size());
    assertEquals(108, TopicReader.load(Topics.get("bright-sustainable-living-original")).keySet().size());
    assertEquals(112, TopicReader.load(Topics.get("bright-pony-original")).keySet().size());
    assertEquals(142, TopicReader.load(Topics.get("bright-leetcode-original")).keySet().size());
    assertEquals(111, TopicReader.load(Topics.get("bright-aops-original")).keySet().size());
    assertEquals(76, TopicReader.load(Topics.get("bright-theoremqa-theorems-original")).keySet().size());
    assertEquals(194, TopicReader.load(Topics.get("bright-theoremqa-questions-original")).keySet().size());
  }

  @Test
  public void testMBEIRTopics() throws IOException {
    assertEquals(4170, TopicReader.load(Topics.get("mbeir-cirr_task7.test")).keySet().size());
    assertEquals(3241, TopicReader.load(Topics.get("mbeir-edis_task2.test")).keySet().size());
    assertEquals(1719, TopicReader.load(Topics.get("mbeir-fashion200k_task0.test")).keySet().size());
    assertEquals(4889, TopicReader.load(Topics.get("mbeir-fashion200k_task3.test")).keySet().size());
    assertEquals(6003, TopicReader.load(Topics.get("mbeir-fashioniq_task7.test")).keySet().size());
    assertEquals(11323, TopicReader.load(Topics.get("mbeir-infoseek_task6.test")).keySet().size());
    assertEquals(17593, TopicReader.load(Topics.get("mbeir-infoseek_task8.test")).keySet().size());
    assertEquals(24809, TopicReader.load(Topics.get("mbeir-mscoco_task0.test")).keySet().size());
    assertEquals(5000, TopicReader.load(Topics.get("mbeir-mscoco_task3.test")).keySet().size());
    assertEquals(2120, TopicReader.load(Topics.get("mbeir-nights_task4.test")).keySet().size());
    assertEquals(50004, TopicReader.load(Topics.get("mbeir-oven_task6.test")).keySet().size());
    assertEquals(14741, TopicReader.load(Topics.get("mbeir-oven_task8.test")).keySet().size());
    assertEquals(19995, TopicReader.load(Topics.get("mbeir-visualnews_task0.test")).keySet().size());
    assertEquals(20000, TopicReader.load(Topics.get("mbeir-visualnews_task3.test")).keySet().size());
    assertEquals(2455, TopicReader.load(Topics.get("mbeir-webqa_task1.test")).keySet().size());
    assertEquals(2511, TopicReader.load(Topics.get("mbeir-webqa_task2.test")).keySet().size());
  }

  @Test public void testMMEBVisDocTopics() throws IOException {
    assertEquals(500, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_arxivqa.test")).keySet().size());
    assertEquals(451, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_docvqa.test")).keySet().size());
    assertEquals(494, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_infovqa.test")).keySet().size());
    assertEquals(100, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_shiftproject.test")).keySet().size());
    assertEquals(100, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_syntheticDocQA_artificial_intelligence.test")).keySet().size());
    assertEquals(100, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_syntheticDocQA_energy.test")).keySet().size());
    assertEquals(100, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_syntheticDocQA_government_reports.test")).keySet().size());
    assertEquals(100, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_syntheticDocQA_healthcare_industry.test")).keySet().size());
    assertEquals(280, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_tabfquad.test")).keySet().size());
    assertEquals(1646, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_tatdqa.test")).keySet().size());
    assertEquals(160, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_biomedical_lectures_v2.test")).keySet().size());
    assertEquals(640, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_biomedical_lectures_v2_multilingual.test")).keySet().size());
    assertEquals(58, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_economics_reports_v2.test")).keySet().size());
    assertEquals(232, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_economics_reports_v2_multilingual.test")).keySet().size());
    assertEquals(52, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_esg_reports_human_labeled_v2.test")).keySet().size());
    assertEquals(57, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_esg_reports_v2.test")).keySet().size());
    assertEquals(228, TopicReader.load(Topics.get("mmeb-visdoc-ViDoRe_esg_reports_v2_multilingual.test")).keySet().size());
    assertEquals(816, TopicReader.load(Topics.get("mmeb-visdoc-VisRAG_ArxivQA.train")).keySet().size());
    assertEquals(63, TopicReader.load(Topics.get("mmeb-visdoc-VisRAG_ChartQA.train")).keySet().size());
    assertEquals(718, TopicReader.load(Topics.get("mmeb-visdoc-VisRAG_InfoVQA.train")).keySet().size());
    assertEquals(591, TopicReader.load(Topics.get("mmeb-visdoc-VisRAG_MP-DocVQA.train")).keySet().size());
    assertEquals(863, TopicReader.load(Topics.get("mmeb-visdoc-VisRAG_PlotQA.train")).keySet().size());
    assertEquals(556, TopicReader.load(Topics.get("mmeb-visdoc-VisRAG_SlideVQA.train")).keySet().size());
    assertEquals(1142, TopicReader.load(Topics.get("mmeb-visdoc-ViDoSeek-doc.test")).keySet().size());
    assertEquals(1142, TopicReader.load(Topics.get("mmeb-visdoc-ViDoSeek-page.test")).keySet().size());
    assertEquals(838, TopicReader.load(Topics.get("mmeb-visdoc-MMLongBench-doc.test")).keySet().size());
    assertEquals(838, TopicReader.load(Topics.get("mmeb-visdoc-MMLongBench-page.test")).keySet().size());
  }

  @Test
  public void testGetTopicsWithStringIdsFromFileWithTopicReader() {
    Map<String, Map<String, String>> topics;

    topics = TopicReader.getTopicsWithStringIdsFromFileWithTopicReaderClass(TrecTopicReader.class.getName(),
        "tools/topics-and-qrels/topics.robust04.txt");

    assertNotNull(topics);
    assertEquals(250, topics.size());
    assertEquals("International Organized Crime", topics.get("301").get("title"));
    assertEquals("gasoline tax U.S.", topics.get("700").get("title"));

    topics = TopicReader.getTopicsWithStringIdsFromFileWithTopicReaderClass(TsvIntTopicReader.class.getName(),
        "tools/topics-and-qrels/topics.msmarco-doc.dev.txt");
    assertNotNull(topics);
    assertEquals(5193, topics.size());
    assertEquals("androgen receptor define", topics.get("2").get("title"));
    assertEquals("why do bears hibernate", topics.get("1102400").get("title"));
  }
  
  @Test
  public void testHC4Topics() throws IOException {
    assertEquals(10, TopicReader.load(Topics.get("hc4-v1.0-fa.dev.title")).keySet().size());
    assertEquals(10, TopicReader.load(Topics.get("hc4-v1.0-fa.dev.desc")).keySet().size());
    assertEquals(10, TopicReader.load(Topics.get("hc4-v1.0-fa.dev.desc.title")).keySet().size());

    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-fa.test.title")).keySet().size());
    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-fa.test.desc")).keySet().size());
    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-fa.test.desc.title")).keySet().size());

    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-fa.en.test.title")).keySet().size());
    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-fa.en.test.desc")).keySet().size());
    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-fa.en.test.desc.title")).keySet().size());

    assertEquals(4, TopicReader.load(Topics.get("hc4-v1.0-ru.dev.title")).keySet().size());
    assertEquals(4, TopicReader.load(Topics.get("hc4-v1.0-ru.dev.desc")).keySet().size());
    assertEquals(4, TopicReader.load(Topics.get("hc4-v1.0-ru.dev.desc.title")).keySet().size());

    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-ru.test.title")).keySet().size());
    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-ru.test.desc")).keySet().size());
    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-ru.test.desc.title")).keySet().size());

    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-ru.en.test.title")).keySet().size());
    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-ru.en.test.desc")).keySet().size());
    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-ru.en.test.desc.title")).keySet().size());

    assertEquals(10, TopicReader.load(Topics.get("hc4-v1.0-zh.dev.title")).keySet().size());
    assertEquals(10, TopicReader.load(Topics.get("hc4-v1.0-zh.dev.desc")).keySet().size());
    assertEquals(10, TopicReader.load(Topics.get("hc4-v1.0-zh.dev.desc.title")).keySet().size());

    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-zh.test.title")).keySet().size());
    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-zh.test.desc")).keySet().size());
    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-zh.test.desc.title")).keySet().size());

    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-zh.en.test.title")).keySet().size());
    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-zh.en.test.desc")).keySet().size());
    assertEquals(50, TopicReader.load(Topics.get("hc4-v1.0-zh.en.test.desc.title")).keySet().size());
  }

  @Test
  public void testNeuCLIR22OriginalTopics() throws IOException {
    SortedMap<Integer, Map<String, String>> t, d, dt;

    t = TopicReader.load(Topics.get("neuclir22-en.original-title"));
    d = TopicReader.load(Topics.get("neuclir22-en.original-desc"));
    dt = TopicReader.load(Topics.get("neuclir22-en.original-desc_title"));

    assertEquals(114, t.keySet().size());
    assertEquals(114, d.keySet().size());
    assertEquals(114, dt.keySet().size());

    assertEquals(t.keySet(), d.keySet());
    assertEquals(d.keySet(), dt.keySet());

    for (Integer k : t.keySet()) {
      assertEquals(dt.get(k).get("title"), d.get(k).get("title") + " " + t.get(k).get("title"));
    }

    // Persian
    t = TopicReader.load(Topics.get("neuclir22-fa.ht-title"));
    d = TopicReader.load(Topics.get("neuclir22-fa.ht-desc"));
    dt = TopicReader.load(Topics.get("neuclir22-fa.ht-desc_title"));

    assertEquals(114, t.keySet().size());
    assertEquals(114, d.keySet().size());
    assertEquals(114, dt.keySet().size());

    assertEquals(t.keySet(), d.keySet());
    assertEquals(d.keySet(), dt.keySet());

    for (Integer k : t.keySet()) {
      assertEquals(dt.get(k).get("title"), d.get(k).get("title") + " " + t.get(k).get("title"));
    }

    t = TopicReader.load(Topics.get("neuclir22-fa.mt-title"));
    d = TopicReader.load(Topics.get("neuclir22-fa.mt-desc"));
    dt = TopicReader.load(Topics.get("neuclir22-fa.mt-desc_title"));

    assertEquals(114, t.keySet().size());
    assertEquals(114, d.keySet().size());
    assertEquals(114, dt.keySet().size());

    assertEquals(t.keySet(), d.keySet());
    assertEquals(d.keySet(), dt.keySet());

    for (Integer k : t.keySet()) {
      assertEquals(dt.get(k).get("title"), d.get(k).get("title") + " " + t.get(k).get("title"));
    }

    // Russian
    t = TopicReader.load(Topics.get("neuclir22-ru.ht-title"));
    d = TopicReader.load(Topics.get("neuclir22-ru.ht-desc"));
    dt = TopicReader.load(Topics.get("neuclir22-ru.ht-desc_title"));

    assertEquals(114, t.keySet().size());
    assertEquals(114, d.keySet().size());
    assertEquals(114, dt.keySet().size());

    assertEquals(t.keySet(), d.keySet());
    assertEquals(d.keySet(), dt.keySet());

    for (Integer k : t.keySet()) {
      assertEquals(dt.get(k).get("title"), d.get(k).get("title") + " " + t.get(k).get("title"));
    }

    t = TopicReader.load(Topics.get("neuclir22-ru.mt-title"));
    d = TopicReader.load(Topics.get("neuclir22-ru.mt-desc"));
    dt = TopicReader.load(Topics.get("neuclir22-ru.mt-desc_title"));

    assertEquals(114, t.keySet().size());
    assertEquals(114, d.keySet().size());
    assertEquals(114, dt.keySet().size());

    assertEquals(t.keySet(), d.keySet());
    assertEquals(d.keySet(), dt.keySet());

    for (Integer k : t.keySet()) {
      assertEquals(dt.get(k).get("title"), d.get(k).get("title") + " " + t.get(k).get("title"));
    }

    // Chinese
    t = TopicReader.load(Topics.get("neuclir22-zh.ht-title"));
    d = TopicReader.load(Topics.get("neuclir22-zh.ht-desc"));
    dt = TopicReader.load(Topics.get("neuclir22-zh.ht-desc_title"));

    assertEquals(114, t.keySet().size());
    assertEquals(114, d.keySet().size());
    assertEquals(114, dt.keySet().size());

    assertEquals(t.keySet(), d.keySet());
    assertEquals(d.keySet(), dt.keySet());

    for (Integer k : t.keySet()) {
      assertEquals(dt.get(k).get("title"), d.get(k).get("title") + " " + t.get(k).get("title"));
    }

    t = TopicReader.load(Topics.get("neuclir22-zh.mt-title"));
    d = TopicReader.load(Topics.get("neuclir22-zh.mt-desc"));
    dt = TopicReader.load(Topics.get("neuclir22-zh.mt-desc_title"));

    assertEquals(114, t.keySet().size());
    assertEquals(114, d.keySet().size());
    assertEquals(114, dt.keySet().size());

    assertEquals(t.keySet(), d.keySet());
    assertEquals(d.keySet(), dt.keySet());

    for (Integer k : t.keySet()) {
      assertEquals(dt.get(k).get("title"), d.get(k).get("title") + " " + t.get(k).get("title"));
    }
  }

  @Test
  public void testNeuCLIR22SpladeTopics() throws IOException {
    assertEquals(114, TopicReader.load(Topics.get("neuclir22-fa.splade.ht-title")).keySet().size());
    assertEquals(114, TopicReader.load(Topics.get("neuclir22-fa.splade.ht-desc")).keySet().size());
    assertEquals(114, TopicReader.load(Topics.get("neuclir22-fa.splade.ht-desc_title")).keySet().size());

    assertEquals(114, TopicReader.load(Topics.get("neuclir22-fa.splade.mt-title")).keySet().size());
    assertEquals(114, TopicReader.load(Topics.get("neuclir22-fa.splade.mt-desc")).keySet().size());
    assertEquals(114, TopicReader.load(Topics.get("neuclir22-fa.splade.mt-desc_title")).keySet().size());

    assertEquals(114, TopicReader.load(Topics.get("neuclir22-ru.splade.ht-title")).keySet().size());
    assertEquals(114, TopicReader.load(Topics.get("neuclir22-ru.splade.ht-desc")).keySet().size());
    assertEquals(114, TopicReader.load(Topics.get("neuclir22-ru.splade.ht-desc_title")).keySet().size());

    assertEquals(114, TopicReader.load(Topics.get("neuclir22-ru.splade.mt-title")).keySet().size());
    assertEquals(114, TopicReader.load(Topics.get("neuclir22-ru.splade.mt-desc")).keySet().size());
    assertEquals(114, TopicReader.load(Topics.get("neuclir22-ru.splade.mt-desc_title")).keySet().size());

    assertEquals(114, TopicReader.load(Topics.get("neuclir22-zh.splade.ht-title")).keySet().size());
    assertEquals(114, TopicReader.load(Topics.get("neuclir22-zh.splade.ht-desc")).keySet().size());
    assertEquals(114, TopicReader.load(Topics.get("neuclir22-zh.splade.ht-desc_title")).keySet().size());

    assertEquals(114, TopicReader.load(Topics.get("neuclir22-zh.splade.mt-title")).keySet().size());
    assertEquals(114, TopicReader.load(Topics.get("neuclir22-zh.splade.mt-desc")).keySet().size());
    assertEquals(114, TopicReader.load(Topics.get("neuclir22-zh.splade.mt-desc_title")).keySet().size());
  }

  @Test
  public void testMIRACLTopics() throws IOException {
    assertEquals(2896, TopicReader.load(Topics.get("miracl-v1.0-ar-dev")).keySet().size());
    assertEquals(411, TopicReader.load(Topics.get("miracl-v1.0-bn-dev")).keySet().size());
    assertEquals(799, TopicReader.load(Topics.get("miracl-v1.0-en-dev")).keySet().size());
    assertEquals(648, TopicReader.load(Topics.get("miracl-v1.0-es-dev")).keySet().size());
    assertEquals(632, TopicReader.load(Topics.get("miracl-v1.0-fa-dev")).keySet().size());
    assertEquals(1271, TopicReader.load(Topics.get("miracl-v1.0-fi-dev")).keySet().size());
    assertEquals(343, TopicReader.load(Topics.get("miracl-v1.0-fr-dev")).keySet().size());
    assertEquals(350, TopicReader.load(Topics.get("miracl-v1.0-hi-dev")).keySet().size());
    assertEquals(960, TopicReader.load(Topics.get("miracl-v1.0-id-dev")).keySet().size());
    assertEquals(860, TopicReader.load(Topics.get("miracl-v1.0-ja-dev")).keySet().size());
    assertEquals(213, TopicReader.load(Topics.get("miracl-v1.0-ko-dev")).keySet().size());
    assertEquals(1252, TopicReader.load(Topics.get("miracl-v1.0-ru-dev")).keySet().size());
    assertEquals(482, TopicReader.load(Topics.get("miracl-v1.0-sw-dev")).keySet().size());
    assertEquals(828, TopicReader.load(Topics.get("miracl-v1.0-te-dev")).keySet().size());
    assertEquals(733, TopicReader.load(Topics.get("miracl-v1.0-th-dev")).keySet().size());
    assertEquals(393, TopicReader.load(Topics.get("miracl-v1.0-zh-dev")).keySet().size());
    assertEquals(305, TopicReader.load(Topics.get("miracl-v1.0-de-dev")).keySet().size());
    assertEquals(119, TopicReader.load(Topics.get("miracl-v1.0-yo-dev")).keySet().size());
  }

  @Test
  public void testCIRALTopics() throws IOException {
    assertEquals(10, TopicReader.load(Topics.get("ciral-v1.0-ha-dev-native")).keySet().size());
    assertEquals(10, TopicReader.load(Topics.get("ciral-v1.0-so-dev-native")).keySet().size());
    assertEquals(10, TopicReader.load(Topics.get("ciral-v1.0-sw-dev-native")).keySet().size());
    assertEquals(10, TopicReader.load(Topics.get("ciral-v1.0-yo-dev-native")).keySet().size());
    assertEquals(80, TopicReader.load(Topics.get("ciral-v1.0-ha-test-a")).keySet().size());
    assertEquals(99, TopicReader.load(Topics.get("ciral-v1.0-so-test-a")).keySet().size());
    assertEquals(85, TopicReader.load(Topics.get("ciral-v1.0-sw-test-a")).keySet().size());
    assertEquals(100, TopicReader.load(Topics.get("ciral-v1.0-yo-test-a")).keySet().size());
    assertEquals(80, TopicReader.load(Topics.get("ciral-v1.0-ha-test-a-native")).keySet().size());
    assertEquals(99, TopicReader.load(Topics.get("ciral-v1.0-so-test-a-native")).keySet().size());
    assertEquals(85, TopicReader.load(Topics.get("ciral-v1.0-sw-test-a-native")).keySet().size());
    assertEquals(100, TopicReader.load(Topics.get("ciral-v1.0-yo-test-a-native")).keySet().size());
    assertEquals(312, TopicReader.load(Topics.get("ciral-v1.0-ha-test-b")).keySet().size());
    assertEquals(239, TopicReader.load(Topics.get("ciral-v1.0-so-test-b")).keySet().size());
    assertEquals(113, TopicReader.load(Topics.get("ciral-v1.0-sw-test-b")).keySet().size());
    assertEquals(554, TopicReader.load(Topics.get("ciral-v1.0-yo-test-b")).keySet().size());
    assertEquals(312, TopicReader.load(Topics.get("ciral-v1.0-ha-test-b-native")).keySet().size());
    assertEquals(239, TopicReader.load(Topics.get("ciral-v1.0-so-test-b-native")).keySet().size());
    assertEquals(112, TopicReader.load(Topics.get("ciral-v1.0-sw-test-b-native")).keySet().size());
    assertEquals(554, TopicReader.load(Topics.get("ciral-v1.0-yo-test-b-native")).keySet().size());
  }
}
