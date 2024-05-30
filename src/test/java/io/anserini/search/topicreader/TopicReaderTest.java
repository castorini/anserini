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

import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TopicReaderTest {

  @Test
  public void testIterateThroughAllEnums() {
    int cnt = 0;
    for (Topics topic : Topics.values()) {
      cnt++; 

      // Verify that we can fetch the TopicReader class given the name of the topic file.
      String path = topic.path;
      assertEquals(topic.readerClass, TopicReader.getTopicReaderClassByFile(path));
    }
    assertEquals(476, cnt);
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
  public void testGetTopicsInvalid() throws IOException {
    TopicReader.getTopics(null);
  }

  @Test
  public void testGetTopicsByFile() {
    SortedMap<Object, Map<String, String>> topics =
        TopicReader.getTopicsByFile("tools/topics-and-qrels/topics.robust04.txt");

    assertNotNull(topics);
    assertEquals(250, topics.size());
    assertEquals(301, (int) topics.firstKey());
    assertEquals("International Organized Crime", topics.get(topics.firstKey()).get("title"));
    assertEquals(700, (int) topics.lastKey());
    assertEquals("gasoline tax U.S.", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void testNewswireTopics() throws IOException {
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
    assertEquals("Impact of foreign textile imports on U.S. textile industry", topics.get(topics.lastKey()).get("title"));

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
  public void testTrecTitleParsing() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.TREC1_ADHOC);
    assertNotNull(topics);
    assertEquals(50, topics.size());

    // Single line titles.
    assertEquals("Airbus Subsidies", topics.get(51).get("title"));
    assertEquals("Controlling the Transfer of High Technology", topics.get(100).get("title"));

    // Multi-line titles.
    assertEquals("Financial crunch for televangelists in the wake of the PTL scandal", topics.get(81).get("title"));
    assertEquals("Criminal Actions Against Officers of Failed Financial Institutions", topics.get(87).get("title"));
    assertEquals("What Backing Does the National Rifle Association Have?", topics.get(93).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2_ADHOC);
    assertNotNull(topics);
    assertEquals(50, topics.size());

    assertEquals("Industrial Espionage", topics.get(149).get("title"));

    assertEquals("Laser Research Applicable to the U.S.'s Strategic Defense Initiative", topics.get(102).get("title"));
    assertEquals("Impact of Government Regulated Grain Farming on International Relations", topics.get(142).get("title"));
  }

  @Test
  public void testNewswireTopics_TopicIdsAsStrings() throws IOException {
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
    assertEquals("Impact of foreign textile imports on U.S. textile industry", topics.get("200").get("title"));

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
  public void testWebTopics() throws IOException {
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

    topics = TopicReader.getTopics(Topics.TREC2007_MILLION_QUERY);
    assertNotNull(topics);
    assertEquals(10000, topics.keySet().size());
    assertEquals(1, (int) topics.firstKey());
    assertEquals("after school program evaluation", topics.get(topics.firstKey()).get("title").trim());
    assertEquals(10000, (int) topics.lastKey());
    assertEquals("californa mission", topics.get(topics.lastKey()).get("title").trim());

    topics = TopicReader.getTopics(Topics.TREC2008_MILLION_QUERY);
    assertNotNull(topics);
    assertEquals(10000, topics.keySet().size());
    assertEquals(10001, (int) topics.firstKey());
    assertEquals("comparability of pay analyses", topics.get(topics.firstKey()).get("title").trim());
    assertEquals(20000, (int) topics.lastKey());
    assertEquals("manchester city hall", topics.get(topics.lastKey()).get("title").trim());

    topics = TopicReader.getTopics(Topics.TREC2009_MILLION_QUERY);
    assertNotNull(topics);
    assertEquals(40000, topics.keySet().size());
    assertEquals(20001, (int) topics.firstKey());
    assertEquals("obama family tree", topics.get(topics.firstKey()).get("title").trim());
    assertEquals("1", topics.get(topics.firstKey()).get("priority").trim());
    assertEquals(60000, (int) topics.lastKey());
    assertEquals("bird shingles", topics.get(topics.lastKey()).get("title").trim());
    assertEquals("4", topics.get(topics.lastKey()).get("priority").trim());

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
  public void testWebTopics_TopicIdsAsStrings() throws IOException {
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
  public void testMicoblogTopics() throws IOException {
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
  public void testMicoblogTopics_TopicIdsAsStrings() throws IOException {
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
  public void testCAR() throws IOException {
    SortedMap<String, Map<String, String>> topics;

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
  public void testCAR_TopicIdsAsStrings() throws IOException {
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
  public void testDprNq() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.DPR_NQ_DEV);
    assertNotNull(topics);
    assertEquals(8757, topics.size());
    assertEquals(0, (int) topics.firstKey());
    assertEquals("who sings does he love me with reba", topics.get(topics.firstKey()).get("title"));
    assertEquals("['Linda Davis']", topics.get(topics.firstKey()).get("answers"));
    assertEquals(8756, (int) topics.lastKey());
    assertEquals("when did the gop take control of the house", topics.get(topics.lastKey()).get("title"));
    assertEquals("['2010']", topics.get(topics.lastKey()).get("answers"));
    assertEquals("who did the artwork for pink floyd 's wall", topics.get(1726).get("title"));

    topics = TopicReader.getTopics(Topics.DPR_NQ_TEST);
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

    topics = TopicReader.getTopics(Topics.DPR_TRIVIA_DEV);
    assertNotNull(topics);
    assertEquals(8837, topics.size());
    assertEquals(0, (int) topics.firstKey());
    assertEquals("The VS-300 was a type of what?", topics.get(topics.firstKey()).get("title"));
    assertEquals("['🚁', 'Helicopters', 'Civilian helicopter', 'Pescara (helicopter)', 'Cargo helicopter', 'Copter', 'Helecopter', 'List of deadliest helicopter crashes', 'Helichopper', 'Helocopter', 'Cargo Helicopter', 'Helicopter', 'Helicoptor', 'Anatomy of a helicopter']", topics.get(topics.firstKey()).get("answers"));
    assertEquals(8836, (int) topics.lastKey());
    assertEquals("Name the artist and the title of this 1978 classic that remains popular today: We were at the beach Everybody had matching towels Somebody went under a dock And there they saw a rock It wasnt a rock", topics.get(topics.lastKey()).get("title"));
    assertEquals("['Rock Lobster by the B-52s']", topics.get(topics.lastKey()).get("answers"));

    topics = TopicReader.getTopics(Topics.DPR_TRIVIA_TEST);
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

    topics = TopicReader.getTopics(Topics.DPR_WQ_TEST);
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

    topics = TopicReader.getTopics(Topics.DPR_CURATED_TEST);
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

    topics = TopicReader.getTopics(Topics.DPR_SQUAD_TEST);
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

    topics = TopicReader.getTopics(Topics.NQ_DEV);
    assertNotNull(topics);
    assertEquals(8757, topics.size());
    assertEquals(0, (int) topics.firstKey());
    assertEquals("who sings does he love me with reba", topics.get(topics.firstKey()).get("title"));
    assertEquals("['Linda Davis']", topics.get(topics.firstKey()).get("answers"));
    assertEquals(8756, (int) topics.lastKey());
    assertEquals("when did the gop take control of the house", topics.get(topics.lastKey()).get("title"));
    assertEquals("['2010']", topics.get(topics.lastKey()).get("answers"));
    assertEquals("who did the artwork for pink floyd's wall", topics.get(1726).get("title"));

    topics = TopicReader.getTopics(Topics.NQ_TEST);
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
    assertEquals(3610, TopicReader.getTopics(Topics.NQ_TEST_GART5_ANSWERS).keySet().size());
    assertEquals(3610, TopicReader.getTopics(Topics.NQ_TEST_GART5_TITLES).keySet().size());
    assertEquals(3610, TopicReader.getTopics(Topics.NQ_TEST_GART5_SENTENCES).keySet().size());
    assertEquals(3610, TopicReader.getTopics(Topics.NQ_TEST_GART5_ALL).keySet().size());
  }

  @Test
  public void testGarT5Trivia() throws IOException {
    assertEquals(11313, TopicReader.getTopics(Topics.DPR_TRIVIA_TEST_GART5_ANSWERS).keySet().size());
    assertEquals(11313, TopicReader.getTopics(Topics.DPR_TRIVIA_TEST_GART5_TITLES).keySet().size());
    assertEquals(11313, TopicReader.getTopics(Topics.DPR_TRIVIA_TEST_GART5_SENTENCES).keySet().size());
    assertEquals(11313, TopicReader.getTopics(Topics.DPR_TRIVIA_TEST_GART5_ALL).keySet().size());
  }

  @Test
  public void testTREC19DL() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.TREC2019_DL_PASSAGE);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals("anthropological definition of environment", topics.get(topics.firstKey()).get("title"));
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals("how is the weather in jamaica", topics.get(topics.lastKey()).get("title"));
    assertEquals("does legionella pneumophila cause pneumonia", topics.get(168216).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2019_DL_PASSAGE_WP);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals("anthropological definition of environment", topics.get(topics.firstKey()).get("title"));
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals("how is the weather in jamaica", topics.get(topics.lastKey()).get("title"));
    assertEquals("does legion ##ella p ##ne ##um ##op ##hila cause pneumonia", topics.get(168216).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2019_DL_PASSAGE_UNICOIL);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(695, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(595, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2019_DL_PASSAGE_UNICOIL_NOEXP);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(668, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(586, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2019_DL_DOC);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals("anthropological definition of environment", topics.get(topics.firstKey()).get("title"));
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals("how is the weather in jamaica", topics.get(topics.lastKey()).get("title"));
    assertEquals("how long to hold bow in yoga", topics.get(1132213).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2019_DL_DOC_WP);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals("anthropological definition of environment", topics.get(topics.firstKey()).get("title"));
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals("how is the weather in jamaica", topics.get(topics.lastKey()).get("title"));
    assertEquals("how long to hold bow in yoga", topics.get(1132213).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2019_DL_DOC_UNICOIL);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(695, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(595, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2019_DL_DOC_UNICOIL_NOEXP);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(668, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(586, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2019_DL_PASSAGE_SPLADE_DISTILL_COCODENSER_MEDIUM);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(1890, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(1382, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2019_DL_PASSAGE_SPLADE_PP_ED);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(28088, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(18791, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2019_DL_PASSAGE_SPLADE_PP_SD);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals(28936, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals(17675, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2019_DL_PASSAGE_COS_DPR_DISTIL);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals("[0.013790097087621689", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals("[-0.024115752428770065", topics.get(topics.lastKey()).get("vector").split(",")[0]);

    topics = TopicReader.getTopics(Topics.TREC2019_DL_PASSAGE_BGE_BASE_EN_15);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals("[0.021483641117811203", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals("[0.031006483361124992", topics.get(topics.lastKey()).get("vector").split(",")[0]);

    topics = TopicReader.getTopics(Topics.TREC2019_DL_PASSAGE_COHERE_EMBED_ENGLISH_30);
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

    topics = TopicReader.getTopics(Topics.TREC2020_DL);
    assertNotNull(topics);
    assertEquals(200, topics.size());
    assertEquals(3505, (int) topics.firstKey());
    assertEquals("how do they do open heart surgery", topics.get(topics.firstKey()).get("title"));
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals("why did the ancient egyptians call their land kemet, or black land?", topics.get(topics.lastKey()).get("title"));
    assertEquals("who is aziz hashim", topics.get(1030303).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2020_DL_WP);
    assertNotNull(topics);
    assertEquals(200, topics.size());
    assertEquals(3505, (int) topics.firstKey());
    assertEquals("how do they do open heart surgery", topics.get(topics.firstKey()).get("title"));
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals("why did the ancient egyptians call their land ke ##met , or black land ?", topics.get(topics.lastKey()).get("title"));
    assertEquals("who is aziz hash ##im", topics.get(1030303).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2020_DL_UNICOIL);
    assertNotNull(topics);
    assertEquals(200, topics.size());
    assertEquals(3505, (int) topics.firstKey());
    assertEquals(706, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals(1169, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2020_DL_UNICOIL_NOEXP);
    assertNotNull(topics);
    assertEquals(200, topics.size());
    assertEquals(3505, (int) topics.firstKey());
    assertEquals(689, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals(1164, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2020_DL_SPLADE_DISTILL_COCODENSER_MEDIUM);
    assertNotNull(topics);
    assertEquals(54, topics.size());
    assertEquals(23849, (int) topics.firstKey());
    assertEquals(2168, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals(2075, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2020_DL_SPLADE_PP_ED);
    assertNotNull(topics);
    assertEquals(200, topics.size());
    assertEquals(3505, (int) topics.firstKey());
    assertEquals(30361, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals(25909, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2020_DL_SPLADE_PP_SD);
    assertNotNull(topics);
    assertEquals(200, topics.size());
    assertEquals(3505, (int) topics.firstKey());
    assertEquals(35114, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals(30994, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2020_DL_COS_DPR_DISTIL);
    assertNotNull(topics);
    assertEquals(200, topics.size());
    assertEquals(3505, (int) topics.firstKey());
    assertEquals("[0.0012954670237377286", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals("[0.06602190434932709", topics.get(topics.lastKey()).get("vector").split(",")[0]);

    topics = TopicReader.getTopics(Topics.TREC2020_DL_BGE_BASE_EN_15);
    assertNotNull(topics);
    assertEquals(54, topics.size());
    assertEquals(23849, (int) topics.firstKey());
    assertEquals("[-0.002988815074786544", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(1136962, (int) topics.lastKey());
    assertEquals("[0.008107579313218594", topics.get(topics.lastKey()).get("vector").split(",")[0]);

    topics = TopicReader.getTopics(Topics.TREC2020_DL_COHERE_EMBED_ENGLISH_30);
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

    topics = TopicReader.getTopics(Topics.TREC2021_DL);
    assertNotNull(topics);
    assertEquals(477, topics.size());
    assertEquals(2082, (int) topics.firstKey());
    assertEquals("At about what age do adults normally begin to lose bone mass?", topics.get(topics.firstKey()).get("title"));
    assertEquals(1136769, (int) topics.lastKey());
    assertEquals("why does lacquered brass tarnish", topics.get(topics.lastKey()).get("title"));
    assertEquals("who killed nicholas ii of russia", topics.get(1043135).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2021_DL_UNICOIL);
    assertNotNull(topics);
    assertEquals(477, topics.size());
    assertEquals(2082, (int) topics.firstKey());
    assertEquals(693, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136769, (int) topics.lastKey());
    assertEquals(712, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2021_DL_UNICOIL_NOEXP);
    assertNotNull(topics);
    assertEquals(477, topics.size());
    assertEquals(2082, (int) topics.firstKey());
    assertEquals(624, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136769, (int) topics.lastKey());
    assertEquals(633, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2021_DL_SPLADE_PP_ED);
    assertNotNull(topics);
    assertEquals(477, topics.size());
    assertEquals(2082, (int) topics.firstKey());
    assertEquals(23936, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136769, (int) topics.lastKey());
    assertEquals(25398, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2021_DL_SPLADE_PP_SD);
    assertNotNull(topics);
    assertEquals(477, topics.size());
    assertEquals(2082, (int) topics.firstKey());
    assertEquals(26369, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1136769, (int) topics.lastKey());
    assertEquals(27149, topics.get(topics.lastKey()).get("title").split(" ").length);
  }

  @Test
  public void testTREC22DL() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.TREC2022_DL);
    assertNotNull(topics);
    assertEquals(500, topics.size());
    assertEquals(588, (int) topics.firstKey());
    assertEquals("1099 b cost basis i sell specific shares", topics.get(topics.firstKey()).get("title"));
    assertEquals(2056473, (int) topics.lastKey());
    assertEquals("is a dairy farm considered as an agriculture", topics.get(topics.lastKey()).get("title"));
    assertEquals("how does magic leap optics work", topics.get(2056323).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2022_DL_UNICOIL);
    assertNotNull(topics);
    assertEquals(500, topics.size());
    assertEquals(588, (int) topics.firstKey());
    assertEquals(1016, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(2056473, (int) topics.lastKey());
    assertEquals(720, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2022_DL_UNICOIL_NOEXP);
    assertNotNull(topics);
    assertEquals(500, topics.size());
    assertEquals(588, (int) topics.firstKey());
    assertEquals(900, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(2056473, (int) topics.lastKey());
    assertEquals(726, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2022_DL_SPLADE_PP_ED);
    assertNotNull(topics);
    assertEquals(500, topics.size());
    assertEquals(588, (int) topics.firstKey());
    assertEquals(25701, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(2056473, (int) topics.lastKey());
    assertEquals(28012, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2022_DL_SPLADE_PP_SD);
    assertNotNull(topics);
    assertEquals(500, topics.size());
    assertEquals(588, (int) topics.firstKey());
    assertEquals(31052, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(2056473, (int) topics.lastKey());
    assertEquals(33891, topics.get(topics.lastKey()).get("title").split(" ").length);
  }

  @Test
  public void testTREC23DL() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.TREC2023_DL);
    assertNotNull(topics);
    assertEquals(700, topics.size());
    assertEquals(2000138, (int) topics.firstKey());
    assertEquals("How does the process of digestion and metabolism of carbohydrates start", topics.get(topics.firstKey()).get("title"));
    assertEquals(3100949, (int) topics.lastKey());
    assertEquals("How do birth control and hormone levels affect menstrual cycle variations?", topics.get(topics.lastKey()).get("title"));
    assertEquals("How do birth control and hormone levels affect menstrual cycle variations?", topics.get(3100949).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2023_DL_UNICOIL);
    assertNotNull(topics);
    assertEquals(700, topics.size());
    assertEquals(2000138, (int) topics.firstKey());
    assertEquals(34407, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(3100949, (int) topics.lastKey());
    assertEquals(31334, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2023_DL_UNICOIL_NOEXP);
    assertNotNull(topics);
    assertEquals(700, topics.size());
    assertEquals(2000138, (int) topics.firstKey());
    assertEquals(37993, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(3100949, (int) topics.lastKey());
    assertEquals(31283, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2023_DL_SPLADE_PP_ED);
    assertNotNull(topics);
    assertEquals(700, topics.size());
    assertEquals(2000138, (int) topics.firstKey());
    assertEquals(138500, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(3100949, (int) topics.lastKey());
    assertEquals(139500, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.TREC2023_DL_SPLADE_PP_SD);
    assertNotNull(topics);
    assertEquals(700, topics.size());
    assertEquals(2000138, (int) topics.firstKey());
    assertEquals(163500, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(3100949, (int) topics.lastKey());
    assertEquals(181700, topics.get(topics.lastKey()).get("title").split(" ").length);
  }

  @Test
  public void testTREC24_RAG() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.TREC2024_RAG_RAGGY_DEV);
    assertNotNull(topics);
    assertEquals(120, topics.size());
    assertEquals(23287, (int) topics.firstKey());
    assertEquals("are landlords liable if someone breaks in a hurts tenant", topics.get(topics.firstKey()).get("title"));
    assertEquals(3100918, (int) topics.lastKey());
    assertEquals("Can older adults gain strength by training once per week?", topics.get(topics.lastKey()).get("title"));
    assertEquals("Can older adults gain strength by training once per week?", topics.get(3100918).get("title"));
  }

  @Test
  public void testTREC24_RAG_RESEARCHY() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.TREC2024_RAG_RESEARCHY_DEV);
    assertNotNull(topics);
    assertEquals(600, topics.size());
    assertEquals(429, (int) topics.firstKey());
    assertEquals("how do cafeteria-style plans increase costs for employers?", topics.get(topics.firstKey()).get("title"));
    assertEquals(1009569, (int) topics.lastKey());
    assertEquals("how do video games improve problem solving", topics.get(topics.lastKey()).get("title"));
    assertEquals("how do video games improve problem solving", topics.get(1009569).get("title"));
  }

  @Test
  public void testMSMARCO_V1() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.MSMARCO_DOC_DEV);
    assertNotNull(topics);
    assertEquals(5193, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("androgen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why do bears hibernate", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.MSMARCO_DOC_DEV_WP);
    assertNotNull(topics);
    assertEquals(5193, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("and ##rogen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why do bears hi ##ber ##nate", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.MSMARCO_DOC_DEV_UNICOIL);
    assertNotNull(topics);
    assertEquals(5193, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(617, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(682, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_DOC_DEV_UNICOIL_NOEXP);
    assertNotNull(topics);
    assertEquals(5193, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(609, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(577, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_DOC_TEST);
    assertNotNull(topics);
    assertEquals(5793, topics.size());
    assertEquals(57, (int) topics.firstKey());
    assertEquals("term service agreement definition", topics.get(topics.firstKey()).get("title"));
    assertEquals(1136966, (int) topics.lastKey());
    assertEquals("#ffffff color code", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("Androgen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why do bears hibernate", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET_WP);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("and ##rogen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why do bears hi ##ber ##nate", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET_DEEPIMPACT);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("receptor androgen define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("why hibernate bears", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET_UNICOIL);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(619, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(686, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET_UNICOIL_NOEXP);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(609, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(577, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET_UNICOIL_TILDE);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(584, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(610, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET_DISTILL_SPLADE_MAX);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(1991, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(2409, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET_SPLADE_DISTILL_COCODENSER_MEDIUM);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(1695, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(1682, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET_SPLADE_PP_ED);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(21944, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(24271, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET_SPLADE_PP_SD);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(25539, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals(30718, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET_COS_DPR_DISTIL);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("[-0.007401271723210812", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("[0.05193052813410759", topics.get(topics.lastKey()).get("vector").split(",")[0]);

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET_BGE_BASE_EN_15);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("[-0.009533700533211231", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("[0.0019505455857142806", topics.get(topics.lastKey()).get("vector").split(",")[0]);

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_DEV_SUBSET_COHERE_EMBED_ENGLISH_30);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("[0.00864410400390625", topics.get(topics.firstKey()).get("vector").split(",")[0]);
    assertEquals(1102400, (int) topics.lastKey());
    assertEquals("[0.0107421875", topics.get(topics.lastKey()).get("vector").split(",")[0]);

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_TEST_SUBSET);
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

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_DOC_DEV);
    assertNotNull(topics);
    assertEquals(4552, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("Androgen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals("why do children get aggressive", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_DOC_DEV_UNICOIL);
    assertNotNull(topics);
    assertEquals(4552, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(617, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals(608, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_DOC_DEV_UNICOIL_NOEXP);
    assertNotNull(topics);
    assertEquals(4552, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(609, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals(533, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_DOC_DEV2);
    assertNotNull(topics);
    assertEquals(5000, topics.size());
    assertEquals(361, (int) topics.firstKey());
    assertEquals(". irritability medical definition", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals("why do a ferritin level", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_DOC_DEV2_UNICOIL);
    assertNotNull(topics);
    assertEquals(5000, topics.size());
    assertEquals(361, (int) topics.firstKey());
    assertEquals(714, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals(664, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_DOC_DEV2_UNICOIL_NOEXP);
    assertNotNull(topics);
    assertEquals(5000, topics.size());
    assertEquals(361, (int) topics.firstKey());
    assertEquals(690, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals(537, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_PASSAGE_DEV);
    assertNotNull(topics);
    assertEquals(3903, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals("Androgen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals("why do children get aggressive", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_PASSAGE_DEV_UNICOIL);
    assertNotNull(topics);
    assertEquals(3903, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(617, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals(608, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_PASSAGE_DEV_UNICOIL_NOEXP);
    assertNotNull(topics);
    assertEquals(3903, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(609, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals(533, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_PASSAGE_DEV_SPLADE_PP_ED);
    assertNotNull(topics);
    assertEquals(3903, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(21944, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals(30978, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_PASSAGE_DEV_SPLADE_PP_SD);
    assertNotNull(topics);
    assertEquals(3903, topics.size());
    assertEquals(2, (int) topics.firstKey());
    assertEquals(25539, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102390, (int) topics.lastKey());
    assertEquals(35354, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_PASSAGE_DEV2);
    assertNotNull(topics);
    assertEquals(4281, topics.size());
    assertEquals(1325, (int) topics.firstKey());
    assertEquals("323 area code zip code", topics.get(topics.firstKey()).get("title"));
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals("why do a ferritin level", topics.get(topics.lastKey()).get("title"));

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_PASSAGE_DEV2_UNICOIL);
    assertNotNull(topics);
    assertEquals(4281, topics.size());
    assertEquals(1325, (int) topics.firstKey());
    assertEquals(671, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals(664, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_PASSAGE_DEV2_UNICOIL_NOEXP);
    assertNotNull(topics);
    assertEquals(4281, topics.size());
    assertEquals(1325, (int) topics.firstKey());
    assertEquals(649, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals(537, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_PASSAGE_DEV2_SPLADE_PP_ED);
    assertNotNull(topics);
    assertEquals(4281, topics.size());
    assertEquals(1325, (int) topics.firstKey());
    assertEquals(14928, topics.get(topics.firstKey()).get("title").split(" ").length);
    assertEquals(1102413, (int) topics.lastKey());
    assertEquals(18984, topics.get(topics.lastKey()).get("title").split(" ").length);

    topics = TopicReader.getTopics(Topics.MSMARCO_V2_PASSAGE_DEV2_SPLADE_PP_SD);
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

    topics = TopicReader.getTopicsWithStringIds(Topics.MSMARCO_DOC_DEV);
    assertNotNull(topics);
    assertEquals(5193, topics.size());
    assertEquals("androgen receptor define", topics.get("2").get("title"));
    assertEquals("why do bears hibernate", topics.get("1102400").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.MSMARCO_DOC_TEST);
    assertNotNull(topics);
    assertEquals(5793, topics.size());
    assertEquals("term service agreement definition", topics.get("57").get("title"));
    assertEquals("#ffffff color code", topics.get("1136966").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.MSMARCO_PASSAGE_DEV_SUBSET);
    assertNotNull(topics);
    assertEquals(6980, topics.size());
    assertEquals("Androgen receptor define", topics.get("2").get("title"));
    assertEquals("why do bears hibernate", topics.get("1102400").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.MSMARCO_PASSAGE_TEST_SUBSET);
    assertNotNull(topics);
    assertEquals(6837, topics.size());
    assertEquals("term service agreement definition", topics.get("57").get("title"));
    assertEquals("#ffffff color code", topics.get("1136966").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.MSMARCO_V2_DOC_DEV);
    assertNotNull(topics);
    assertEquals(4552, topics.size());
    assertEquals("Androgen receptor define", topics.get("2").get("title"));
    assertEquals("why do children get aggressive", topics.get("1102390").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.MSMARCO_V2_DOC_DEV2);
    assertNotNull(topics);
    assertEquals(5000, topics.size());
    assertEquals(". irritability medical definition", topics.get("361").get("title"));
    assertEquals("why do a ferritin level", topics.get("1102413").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.MSMARCO_V2_PASSAGE_DEV);
    assertNotNull(topics);
    assertEquals(3903, topics.size());
    assertEquals("Androgen receptor define", topics.get("2").get("title"));
    assertEquals("why do children get aggressive", topics.get("1102390").get("title"));

    topics = TopicReader.getTopicsWithStringIds(Topics.MSMARCO_V2_PASSAGE_DEV2);
    assertNotNull(topics);
    assertEquals(4281, topics.size());
    assertEquals("323 area code zip code", topics.get("1325").get("title"));
    assertEquals("why do a ferritin level", topics.get("1102413").get("title"));
  }

  @Test
  public void testNonEnglishTopics1() throws IOException {
    SortedMap<String, Map<String, String>> topics;

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
  }

  @Test
  public void testNonEnglishTopics2() throws IOException {
      SortedMap<Integer, Map<String, String>> topics;

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
  public void testNonEnglishTopics_TopicIdsAsStrings() throws IOException {
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
  public void testCovidTopics() throws IOException {
    Map<Integer, Map<String, String>> topics;

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
  public void testCovidTopicsUDel() throws IOException {
    Map<Integer, Map<String, String>> topics;

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
  public void testCovidTopics_TopicIdsAsStrings() throws IOException {
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
  public void testCovidTopicsUDel_TopicIdsAsStrings() throws IOException {
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
  public void testBackgroundLinkingTopics() throws IOException {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.TREC2018_BL);

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

    topics = TopicReader.getTopics(Topics.TREC2019_BL);
    
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

    topics = TopicReader.getTopics(Topics.TREC2020_BL);

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
    consumerTopics = TopicReader.getTopics(Topics.EPIDEMIC_QA_CONSUMER_PRELIM);

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
    expertTopics = TopicReader.getTopics(Topics.EPIDEMIC_QA_EXPERT_PRELIM);

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
    assertEquals(12377, TopicReader.getTopics(Topics.MRTYDI_V11_AR_TRAIN).keySet().size());
    assertEquals(3115, TopicReader.getTopics(Topics.MRTYDI_V11_AR_DEV).keySet().size());
    assertEquals(1081, TopicReader.getTopics(Topics.MRTYDI_V11_AR_TEST).keySet().size());

    assertEquals(1713, TopicReader.getTopics(Topics.MRTYDI_V11_BN_TRAIN).keySet().size());
    assertEquals(440, TopicReader.getTopics(Topics.MRTYDI_V11_BN_DEV).keySet().size());
    assertEquals(111, TopicReader.getTopics(Topics.MRTYDI_V11_BN_TEST).keySet().size());

    assertEquals(3547, TopicReader.getTopics(Topics.MRTYDI_V11_EN_TRAIN).keySet().size());
    assertEquals(878, TopicReader.getTopics(Topics.MRTYDI_V11_EN_DEV).keySet().size());
    assertEquals(744, TopicReader.getTopics(Topics.MRTYDI_V11_EN_TEST).keySet().size());

    assertEquals(6561, TopicReader.getTopics(Topics.MRTYDI_V11_FI_TRAIN).keySet().size());
    assertEquals(1738, TopicReader.getTopics(Topics.MRTYDI_V11_FI_DEV).keySet().size());
    assertEquals(1254, TopicReader.getTopics(Topics.MRTYDI_V11_FI_TEST).keySet().size());

    assertEquals(4902, TopicReader.getTopics(Topics.MRTYDI_V11_ID_TRAIN).keySet().size());
    assertEquals(1224, TopicReader.getTopics(Topics.MRTYDI_V11_ID_DEV).keySet().size());
    assertEquals(829, TopicReader.getTopics(Topics.MRTYDI_V11_ID_TEST).keySet().size());

    assertEquals(3697, TopicReader.getTopics(Topics.MRTYDI_V11_JA_TRAIN).keySet().size());
    assertEquals(928, TopicReader.getTopics(Topics.MRTYDI_V11_JA_DEV).keySet().size());
    assertEquals(720, TopicReader.getTopics(Topics.MRTYDI_V11_JA_TEST).keySet().size());

    assertEquals(1295, TopicReader.getTopics(Topics.MRTYDI_V11_KO_TRAIN).keySet().size());
    assertEquals(303, TopicReader.getTopics(Topics.MRTYDI_V11_KO_DEV).keySet().size());
    assertEquals(421, TopicReader.getTopics(Topics.MRTYDI_V11_KO_TEST).keySet().size());

    assertEquals(5366, TopicReader.getTopics(Topics.MRTYDI_V11_RU_TRAIN).keySet().size());
    assertEquals(1375, TopicReader.getTopics(Topics.MRTYDI_V11_RU_DEV).keySet().size());
    assertEquals(995, TopicReader.getTopics(Topics.MRTYDI_V11_RU_TEST).keySet().size());

    assertEquals(2072, TopicReader.getTopics(Topics.MRTYDI_V11_SW_TRAIN).keySet().size());
    assertEquals(526, TopicReader.getTopics(Topics.MRTYDI_V11_SW_DEV).keySet().size());
    assertEquals(670, TopicReader.getTopics(Topics.MRTYDI_V11_SW_TEST).keySet().size());

    assertEquals(3880, TopicReader.getTopics(Topics.MRTYDI_V11_TE_TRAIN).keySet().size());
    assertEquals(983, TopicReader.getTopics(Topics.MRTYDI_V11_TE_DEV).keySet().size());
    assertEquals(646, TopicReader.getTopics(Topics.MRTYDI_V11_TE_TEST).keySet().size());

    assertEquals(3319, TopicReader.getTopics(Topics.MRTYDI_V11_TH_TRAIN).keySet().size());
    assertEquals(807, TopicReader.getTopics(Topics.MRTYDI_V11_TH_DEV).keySet().size());
    assertEquals(1190, TopicReader.getTopics(Topics.MRTYDI_V11_TH_TEST).keySet().size());
  }

  @Test
  public void testBeirTopics() throws IOException {
    assertEquals(50,    TopicReader.getTopics(Topics.BEIR_V1_0_0_TREC_COVID_TEST).keySet().size());
    assertEquals(500,   TopicReader.getTopics(Topics.BEIR_V1_0_0_BIOASQ_TEST).keySet().size());
    assertEquals(323,   TopicReader.getTopics(Topics.BEIR_V1_0_0_NFCORPUS_TEST).keySet().size());
    assertEquals(3452,  TopicReader.getTopics(Topics.BEIR_V1_0_0_NQ_TEST).keySet().size());
    assertEquals(7405,  TopicReader.getTopics(Topics.BEIR_V1_0_0_HOTPOTQA_TEST).keySet().size());
    assertEquals(648,   TopicReader.getTopics(Topics.BEIR_V1_0_0_FIQA_TEST).keySet().size());
    assertEquals(97,    TopicReader.getTopics(Topics.BEIR_V1_0_0_SIGNAL1M_TEST).keySet().size());
    assertEquals(57,    TopicReader.getTopics(Topics.BEIR_V1_0_0_TREC_NEWS_TEST).keySet().size());
    assertEquals(249,   TopicReader.getTopics(Topics.BEIR_V1_0_0_ROBUST04_TEST).keySet().size());
    assertEquals(1406,  TopicReader.getTopics(Topics.BEIR_V1_0_0_ARGUANA_TEST).keySet().size());
    assertEquals(49,    TopicReader.getTopics(Topics.BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST).keySet().size());
    assertEquals(699,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST).keySet().size());
    assertEquals(1570,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST).keySet().size());
    assertEquals(1595,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST).keySet().size());
    assertEquals(885,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_GIS_TEST).keySet().size());
    assertEquals(804,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST).keySet().size());
    assertEquals(1039,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST).keySet().size());
    assertEquals(876,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST).keySet().size());
    assertEquals(652,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_STATS_TEST).keySet().size());
    assertEquals(2906,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_TEX_TEST).keySet().size());
    assertEquals(1072,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST).keySet().size());
    assertEquals(506,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST).keySet().size());
    assertEquals(541,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST).keySet().size());
    assertEquals(10000, TopicReader.getTopics(Topics.BEIR_V1_0_0_QUORA_TEST).keySet().size());
    assertEquals(400,   TopicReader.getTopics(Topics.BEIR_V1_0_0_DBPEDIA_ENTITY_TEST).keySet().size());
    assertEquals(1000,  TopicReader.getTopics(Topics.BEIR_V1_0_0_SCIDOCS_TEST).keySet().size());
    assertEquals(6666,  TopicReader.getTopics(Topics.BEIR_V1_0_0_FEVER_TEST).keySet().size());
    assertEquals(1535,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CLIMATE_FEVER_TEST).keySet().size());
    assertEquals(300,   TopicReader.getTopics(Topics.BEIR_V1_0_0_SCIFACT_TEST).keySet().size());
  }

  @Test
  public void testBeirSpladeDistillCocodenserTopics() throws IOException {
    assertEquals(50,    TopicReader.getTopics(Topics.BEIR_V1_0_0_TREC_COVID_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(500,   TopicReader.getTopics(Topics.BEIR_V1_0_0_BIOASQ_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(323,   TopicReader.getTopics(Topics.BEIR_V1_0_0_NFCORPUS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(3452,  TopicReader.getTopics(Topics.BEIR_V1_0_0_NQ_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(7405,  TopicReader.getTopics(Topics.BEIR_V1_0_0_HOTPOTQA_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(648,   TopicReader.getTopics(Topics.BEIR_V1_0_0_FIQA_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(97,    TopicReader.getTopics(Topics.BEIR_V1_0_0_SIGNAL1M_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(57,    TopicReader.getTopics(Topics.BEIR_V1_0_0_TREC_NEWS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(249,   TopicReader.getTopics(Topics.BEIR_V1_0_0_ROBUST04_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(1406,  TopicReader.getTopics(Topics.BEIR_V1_0_0_ARGUANA_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(49,    TopicReader.getTopics(Topics.BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(699,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(1570,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(1595,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(885,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_GIS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(804,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(1039,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(876,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(652,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_STATS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(2906,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_TEX_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(1072,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(506,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(541,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(10000, TopicReader.getTopics(Topics.BEIR_V1_0_0_QUORA_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(400,   TopicReader.getTopics(Topics.BEIR_V1_0_0_DBPEDIA_ENTITY_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(1000,  TopicReader.getTopics(Topics.BEIR_V1_0_0_SCIDOCS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(6666,  TopicReader.getTopics(Topics.BEIR_V1_0_0_FEVER_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(1535,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CLIMATE_FEVER_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
    assertEquals(300,   TopicReader.getTopics(Topics.BEIR_V1_0_0_SCIFACT_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM).keySet().size());
  }

  @Test
  public void testBeirWPTopics() throws IOException {
    assertEquals(50,    TopicReader.getTopics(Topics.BEIR_V1_0_0_TREC_COVID_TEST_WP).keySet().size());
    assertEquals(500,   TopicReader.getTopics(Topics.BEIR_V1_0_0_BIOASQ_TEST_WP).keySet().size());
    assertEquals(323,   TopicReader.getTopics(Topics.BEIR_V1_0_0_NFCORPUS_TEST_WP).keySet().size());
    assertEquals(3452,  TopicReader.getTopics(Topics.BEIR_V1_0_0_NQ_TEST_WP).keySet().size());
    assertEquals(7405,  TopicReader.getTopics(Topics.BEIR_V1_0_0_HOTPOTQA_TEST_WP).keySet().size());
    assertEquals(648,   TopicReader.getTopics(Topics.BEIR_V1_0_0_FIQA_TEST_WP).keySet().size());
    assertEquals(97,    TopicReader.getTopics(Topics.BEIR_V1_0_0_SIGNAL1M_TEST_WP).keySet().size());
    assertEquals(57,    TopicReader.getTopics(Topics.BEIR_V1_0_0_TREC_NEWS_TEST_WP).keySet().size());
    assertEquals(249,   TopicReader.getTopics(Topics.BEIR_V1_0_0_ROBUST04_TEST_WP).keySet().size());
    assertEquals(1406,  TopicReader.getTopics(Topics.BEIR_V1_0_0_ARGUANA_TEST_WP).keySet().size());
    assertEquals(49,    TopicReader.getTopics(Topics.BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST_WP).keySet().size());
    assertEquals(699,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST_WP).keySet().size());
    assertEquals(1570,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST_WP).keySet().size());
    assertEquals(1595,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST_WP).keySet().size());
    assertEquals(885,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_GIS_TEST_WP).keySet().size());
    assertEquals(804,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST_WP).keySet().size());
    assertEquals(1039,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST_WP).keySet().size());
    assertEquals(876,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST_WP).keySet().size());
    assertEquals(652,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_STATS_TEST_WP).keySet().size());
    assertEquals(2906,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_TEX_TEST_WP).keySet().size());
    assertEquals(1072,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST_WP).keySet().size());
    assertEquals(506,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST_WP).keySet().size());
    assertEquals(541,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST_WP).keySet().size());
    assertEquals(10000, TopicReader.getTopics(Topics.BEIR_V1_0_0_QUORA_TEST_WP).keySet().size());
    assertEquals(400,   TopicReader.getTopics(Topics.BEIR_V1_0_0_DBPEDIA_ENTITY_TEST_WP).keySet().size());
    assertEquals(1000,  TopicReader.getTopics(Topics.BEIR_V1_0_0_SCIDOCS_TEST_WP).keySet().size());
    assertEquals(6666,  TopicReader.getTopics(Topics.BEIR_V1_0_0_FEVER_TEST_WP).keySet().size());
    assertEquals(1535,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CLIMATE_FEVER_TEST_WP).keySet().size());
    assertEquals(300,   TopicReader.getTopics(Topics.BEIR_V1_0_0_SCIFACT_TEST_WP).keySet().size());
  }

  @Test
  public void testBeirUnicoilNoexpTopics() throws IOException{
    assertEquals(50,    TopicReader.getTopics(Topics.BEIR_V1_0_0_TREC_COVID_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(500,   TopicReader.getTopics(Topics.BEIR_V1_0_0_BIOASQ_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(323,   TopicReader.getTopics(Topics.BEIR_V1_0_0_NFCORPUS_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(3452,  TopicReader.getTopics(Topics.BEIR_V1_0_0_NQ_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(7405,  TopicReader.getTopics(Topics.BEIR_V1_0_0_HOTPOTQA_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(648,   TopicReader.getTopics(Topics.BEIR_V1_0_0_FIQA_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(97,    TopicReader.getTopics(Topics.BEIR_V1_0_0_SIGNAL1M_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(57,    TopicReader.getTopics(Topics.BEIR_V1_0_0_TREC_NEWS_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(249,   TopicReader.getTopics(Topics.BEIR_V1_0_0_ROBUST04_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(1406,  TopicReader.getTopics(Topics.BEIR_V1_0_0_ARGUANA_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(49,    TopicReader.getTopics(Topics.BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(699,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(1570,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(1595,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(885,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_GIS_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(804,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(1039,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(876,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(652,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_STATS_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(2906,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_TEX_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(1072,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(506,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(541,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(10000, TopicReader.getTopics(Topics.BEIR_V1_0_0_QUORA_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(400,   TopicReader.getTopics(Topics.BEIR_V1_0_0_DBPEDIA_ENTITY_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(1000,  TopicReader.getTopics(Topics.BEIR_V1_0_0_SCIDOCS_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(6666,  TopicReader.getTopics(Topics.BEIR_V1_0_0_FEVER_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(1535,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CLIMATE_FEVER_TEST_UNCOIL_NOEXP).keySet().size());
    assertEquals(300,   TopicReader.getTopics(Topics.BEIR_V1_0_0_SCIFACT_TEST_UNCOIL_NOEXP).keySet().size());
  }

  @Test
  public void testBeirSpladePpEdTopics() throws IOException {
    assertEquals(50,    TopicReader.getTopics(Topics.BEIR_V1_0_0_TREC_COVID_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(500,   TopicReader.getTopics(Topics.BEIR_V1_0_0_BIOASQ_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(323,   TopicReader.getTopics(Topics.BEIR_V1_0_0_NFCORPUS_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(3452,  TopicReader.getTopics(Topics.BEIR_V1_0_0_NQ_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(7405,  TopicReader.getTopics(Topics.BEIR_V1_0_0_HOTPOTQA_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(648,   TopicReader.getTopics(Topics.BEIR_V1_0_0_FIQA_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(97,    TopicReader.getTopics(Topics.BEIR_V1_0_0_SIGNAL1M_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(57,    TopicReader.getTopics(Topics.BEIR_V1_0_0_TREC_NEWS_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(249,   TopicReader.getTopics(Topics.BEIR_V1_0_0_ROBUST04_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(1406,  TopicReader.getTopics(Topics.BEIR_V1_0_0_ARGUANA_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(49,    TopicReader.getTopics(Topics.BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(699,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(1570,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(1595,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(885,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_GIS_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(804,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(1039,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(876,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(652,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_STATS_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(2906,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_TEX_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(1072,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(506,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(541,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(10000, TopicReader.getTopics(Topics.BEIR_V1_0_0_QUORA_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(400,   TopicReader.getTopics(Topics.BEIR_V1_0_0_DBPEDIA_ENTITY_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(1000,  TopicReader.getTopics(Topics.BEIR_V1_0_0_SCIDOCS_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(6666,  TopicReader.getTopics(Topics.BEIR_V1_0_0_FEVER_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(1535,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CLIMATE_FEVER_TEST_SPLADE_PP_ED).keySet().size());
    assertEquals(300,   TopicReader.getTopics(Topics.BEIR_V1_0_0_SCIFACT_TEST_SPLADE_PP_ED).keySet().size());
  }

  @Test
  public void testBeirBgeBaseEn15Topics() throws IOException {
    assertEquals(50,    TopicReader.getTopics(Topics.BEIR_V1_0_0_TREC_COVID_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(3743,  TopicReader.getTopics(Topics.BEIR_V1_0_0_BIOASQ_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(323,   TopicReader.getTopics(Topics.BEIR_V1_0_0_NFCORPUS_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(3452,  TopicReader.getTopics(Topics.BEIR_V1_0_0_NQ_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(7405,  TopicReader.getTopics(Topics.BEIR_V1_0_0_HOTPOTQA_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(648,   TopicReader.getTopics(Topics.BEIR_V1_0_0_FIQA_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(97,    TopicReader.getTopics(Topics.BEIR_V1_0_0_SIGNAL1M_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(57,    TopicReader.getTopics(Topics.BEIR_V1_0_0_TREC_NEWS_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(250,   TopicReader.getTopics(Topics.BEIR_V1_0_0_ROBUST04_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(1406,  TopicReader.getTopics(Topics.BEIR_V1_0_0_ARGUANA_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(49,    TopicReader.getTopics(Topics.BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(699,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(1570,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(1595,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(885,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_GIS_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(804,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(1039,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(876,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(652,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_STATS_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(2906,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_TEX_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(1072,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(506,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(541,   TopicReader.getTopics(Topics.BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(10000, TopicReader.getTopics(Topics.BEIR_V1_0_0_QUORA_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(400,   TopicReader.getTopics(Topics.BEIR_V1_0_0_DBPEDIA_ENTITY_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(1000,  TopicReader.getTopics(Topics.BEIR_V1_0_0_SCIDOCS_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(6666,  TopicReader.getTopics(Topics.BEIR_V1_0_0_FEVER_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(1535,  TopicReader.getTopics(Topics.BEIR_V1_0_0_CLIMATE_FEVER_TEST_BGE_BASE_EN_15).keySet().size());
    assertEquals(300,   TopicReader.getTopics(Topics.BEIR_V1_0_0_SCIFACT_TEST_BGE_BASE_EN_15).keySet().size());
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
    assertEquals(10, TopicReader.getTopics(Topics.HC4_V1_0_FA_DEV_TITLE).keySet().size());
    assertEquals(10, TopicReader.getTopics(Topics.HC4_V1_0_FA_DEV_DESC).keySet().size());
    assertEquals(10, TopicReader.getTopics(Topics.HC4_V1_0_FA_DEV_DESC_TITLE).keySet().size());

    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_FA_TEST_TITLE).keySet().size());
    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_FA_TEST_DESC).keySet().size());
    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_FA_TEST_DESC_TITLE).keySet().size());

    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_FA_EN_TEST_TITLE).keySet().size());
    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_FA_EN_TEST_DESC).keySet().size());
    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_FA_EN_TEST_DESC_TITLE).keySet().size());

    assertEquals(4, TopicReader.getTopics(Topics.HC4_V1_0_RU_DEV_TITLE).keySet().size());
    assertEquals(4, TopicReader.getTopics(Topics.HC4_V1_0_RU_DEV_DESC).keySet().size());
    assertEquals(4, TopicReader.getTopics(Topics.HC4_V1_0_RU_DEV_DESC_TITLE).keySet().size());

    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_RU_TEST_TITLE).keySet().size());
    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_RU_TEST_DESC).keySet().size());
    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_RU_TEST_DESC_TITLE).keySet().size());

    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_RU_EN_TEST_TITLE).keySet().size());
    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_RU_EN_TEST_DESC).keySet().size());
    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_RU_EN_TEST_DESC_TITLE).keySet().size());

    assertEquals(10, TopicReader.getTopics(Topics.HC4_V1_0_ZH_DEV_TITLE).keySet().size());
    assertEquals(10, TopicReader.getTopics(Topics.HC4_V1_0_ZH_DEV_DESC).keySet().size());
    assertEquals(10, TopicReader.getTopics(Topics.HC4_V1_0_ZH_DEV_DESC_TITLE).keySet().size());

    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_ZH_TEST_TITLE).keySet().size());
    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_ZH_TEST_DESC).keySet().size());
    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_ZH_TEST_DESC_TITLE).keySet().size());

    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_ZH_EN_TEST_TITLE).keySet().size());
    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_ZH_EN_TEST_DESC).keySet().size());
    assertEquals(50, TopicReader.getTopics(Topics.HC4_V1_0_ZH_EN_TEST_DESC_TITLE).keySet().size());
  }

  @Test
  public void testNeuCLIR22OriginalTopics() throws IOException {
    SortedMap<Integer, Map<String, String>> t, d, dt;

    t = TopicReader.getTopics(Topics.NEUCLIR22_EN_TITLE);
    d = TopicReader.getTopics(Topics.NEUCLIR22_EN_DESC);
    dt = TopicReader.getTopics(Topics.NEUCLIR22_EN_DESC_TITLE);

    assertEquals(114, t.keySet().size());
    assertEquals(114, d.keySet().size());
    assertEquals(114, dt.keySet().size());

    assertEquals(t.keySet(), d.keySet());
    assertEquals(d.keySet(), dt.keySet());

    for (Integer k : t.keySet()) {
      assertEquals(dt.get(k).get("title"), d.get(k).get("title") + " " + t.get(k).get("title"));
    }

    // Persian
    t = TopicReader.getTopics(Topics.NEUCLIR22_FA_HT_TITLE);
    d = TopicReader.getTopics(Topics.NEUCLIR22_FA_HT_DESC);
    dt = TopicReader.getTopics(Topics.NEUCLIR22_FA_HT_DESC_TITLE);

    assertEquals(114, t.keySet().size());
    assertEquals(114, d.keySet().size());
    assertEquals(114, dt.keySet().size());

    assertEquals(t.keySet(), d.keySet());
    assertEquals(d.keySet(), dt.keySet());

    for (Integer k : t.keySet()) {
      assertEquals(dt.get(k).get("title"), d.get(k).get("title") + " " + t.get(k).get("title"));
    }

    t = TopicReader.getTopics(Topics.NEUCLIR22_FA_MT_TITLE);
    d = TopicReader.getTopics(Topics.NEUCLIR22_FA_MT_DESC);
    dt = TopicReader.getTopics(Topics.NEUCLIR22_FA_MT_DESC_TITLE);

    assertEquals(114, t.keySet().size());
    assertEquals(114, d.keySet().size());
    assertEquals(114, dt.keySet().size());

    assertEquals(t.keySet(), d.keySet());
    assertEquals(d.keySet(), dt.keySet());

    for (Integer k : t.keySet()) {
      assertEquals(dt.get(k).get("title"), d.get(k).get("title") + " " + t.get(k).get("title"));
    }

    // Russian
    t = TopicReader.getTopics(Topics.NEUCLIR22_RU_HT_TITLE);
    d = TopicReader.getTopics(Topics.NEUCLIR22_RU_HT_DESC);
    dt = TopicReader.getTopics(Topics.NEUCLIR22_RU_HT_DESC_TITLE);

    assertEquals(114, t.keySet().size());
    assertEquals(114, d.keySet().size());
    assertEquals(114, dt.keySet().size());

    assertEquals(t.keySet(), d.keySet());
    assertEquals(d.keySet(), dt.keySet());

    for (Integer k : t.keySet()) {
      assertEquals(dt.get(k).get("title"), d.get(k).get("title") + " " + t.get(k).get("title"));
    }

    t = TopicReader.getTopics(Topics.NEUCLIR22_RU_MT_TITLE);
    d = TopicReader.getTopics(Topics.NEUCLIR22_RU_MT_DESC);
    dt = TopicReader.getTopics(Topics.NEUCLIR22_RU_MT_DESC_TITLE);

    assertEquals(114, t.keySet().size());
    assertEquals(114, d.keySet().size());
    assertEquals(114, dt.keySet().size());

    assertEquals(t.keySet(), d.keySet());
    assertEquals(d.keySet(), dt.keySet());

    for (Integer k : t.keySet()) {
      assertEquals(dt.get(k).get("title"), d.get(k).get("title") + " " + t.get(k).get("title"));
    }

    // Chinese
    t = TopicReader.getTopics(Topics.NEUCLIR22_ZH_HT_TITLE);
    d = TopicReader.getTopics(Topics.NEUCLIR22_ZH_HT_DESC);
    dt = TopicReader.getTopics(Topics.NEUCLIR22_ZH_HT_DESC_TITLE);

    assertEquals(114, t.keySet().size());
    assertEquals(114, d.keySet().size());
    assertEquals(114, dt.keySet().size());

    assertEquals(t.keySet(), d.keySet());
    assertEquals(d.keySet(), dt.keySet());

    for (Integer k : t.keySet()) {
      assertEquals(dt.get(k).get("title"), d.get(k).get("title") + " " + t.get(k).get("title"));
    }

    t = TopicReader.getTopics(Topics.NEUCLIR22_ZH_MT_TITLE);
    d = TopicReader.getTopics(Topics.NEUCLIR22_ZH_MT_DESC);
    dt = TopicReader.getTopics(Topics.NEUCLIR22_ZH_MT_DESC_TITLE);

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
    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_FA_SPLADE_HT_TITLE).keySet().size());
    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_FA_SPLADE_HT_DESC).keySet().size());
    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_FA_SPLADE_HT_DESC_TITLE).keySet().size());

    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_FA_SPLADE_MT_TITLE).keySet().size());
    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_FA_SPLADE_MT_DESC).keySet().size());
    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_FA_SPLADE_MT_DESC_TITLE).keySet().size());

    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_RU_SPLADE_HT_TITLE).keySet().size());
    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_RU_SPLADE_HT_DESC).keySet().size());
    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_RU_SPLADE_HT_DESC_TITLE).keySet().size());

    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_RU_SPLADE_MT_TITLE).keySet().size());
    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_RU_SPLADE_MT_DESC).keySet().size());
    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_RU_SPLADE_MT_DESC_TITLE).keySet().size());

    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_ZH_SPLADE_HT_TITLE).keySet().size());
    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_ZH_SPLADE_HT_DESC).keySet().size());
    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_ZH_SPLADE_HT_DESC_TITLE).keySet().size());

    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_ZH_SPLADE_MT_TITLE).keySet().size());
    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_ZH_SPLADE_MT_DESC).keySet().size());
    assertEquals(114, TopicReader.getTopics(Topics.NEUCLIR22_ZH_SPLADE_MT_DESC_TITLE).keySet().size());
  }

  @Test
  public void testMIRACLTopics() throws IOException {
    assertEquals(2896, TopicReader.getTopics(Topics.MIRACL_V10_AR_DEV).keySet().size());
    assertEquals(411, TopicReader.getTopics(Topics.MIRACL_V10_BN_DEV).keySet().size());
    assertEquals(799, TopicReader.getTopics(Topics.MIRACL_V10_EN_DEV).keySet().size());
    assertEquals(648, TopicReader.getTopics(Topics.MIRACL_V10_ES_DEV).keySet().size());
    assertEquals(632, TopicReader.getTopics(Topics.MIRACL_V10_FA_DEV).keySet().size());
    assertEquals(1271, TopicReader.getTopics(Topics.MIRACL_V10_FI_DEV).keySet().size());
    assertEquals(343, TopicReader.getTopics(Topics.MIRACL_V10_FR_DEV).keySet().size());
    assertEquals(350, TopicReader.getTopics(Topics.MIRACL_V10_HI_DEV).keySet().size());
    assertEquals(960, TopicReader.getTopics(Topics.MIRACL_V10_ID_DEV).keySet().size());
    assertEquals(860, TopicReader.getTopics(Topics.MIRACL_V10_JA_DEV).keySet().size());
    assertEquals(213, TopicReader.getTopics(Topics.MIRACL_V10_KO_DEV).keySet().size());
    assertEquals(1252, TopicReader.getTopics(Topics.MIRACL_V10_RU_DEV).keySet().size());
    assertEquals(482, TopicReader.getTopics(Topics.MIRACL_V10_SW_DEV).keySet().size());
    assertEquals(828, TopicReader.getTopics(Topics.MIRACL_V10_TE_DEV).keySet().size());
    assertEquals(733, TopicReader.getTopics(Topics.MIRACL_V10_TH_DEV).keySet().size());
    assertEquals(393, TopicReader.getTopics(Topics.MIRACL_V10_ZH_DEV).keySet().size());
    assertEquals(305, TopicReader.getTopics(Topics.MIRACL_V10_DE_DEV).keySet().size());
    assertEquals(119, TopicReader.getTopics(Topics.MIRACL_V10_YO_DEV).keySet().size());
  }

  @Test
  public void testCIRALTopics() throws IOException {
    assertEquals(10, TopicReader.getTopics(Topics.CIRAL_V10_HA_DEV_MONO).keySet().size());
    assertEquals(10, TopicReader.getTopics(Topics.CIRAL_V10_SO_DEV_MONO).keySet().size());
    assertEquals(10, TopicReader.getTopics(Topics.CIRAL_V10_SW_DEV_MONO).keySet().size());
    assertEquals(10, TopicReader.getTopics(Topics.CIRAL_V10_YO_DEV_MONO).keySet().size());
    assertEquals(80, TopicReader.getTopics(Topics.CIRAL_V10_HA_TEST_A).keySet().size());
    assertEquals(99, TopicReader.getTopics(Topics.CIRAL_V10_SO_TEST_A).keySet().size());
    assertEquals(85, TopicReader.getTopics(Topics.CIRAL_V10_SW_TEST_A).keySet().size());
    assertEquals(100, TopicReader.getTopics(Topics.CIRAL_V10_YO_TEST_A).keySet().size());
    assertEquals(80, TopicReader.getTopics(Topics.CIRAL_V10_HA_TEST_A_NATIVE).keySet().size());
    assertEquals(99, TopicReader.getTopics(Topics.CIRAL_V10_SO_TEST_A_NATIVE).keySet().size());
    assertEquals(85, TopicReader.getTopics(Topics.CIRAL_V10_SW_TEST_A_NATIVE).keySet().size());
    assertEquals(100, TopicReader.getTopics(Topics.CIRAL_V10_YO_TEST_A_NATIVE).keySet().size());
    assertEquals(312, TopicReader.getTopics(Topics.CIRAL_V10_HA_TEST_B).keySet().size());
    assertEquals(239, TopicReader.getTopics(Topics.CIRAL_V10_SO_TEST_B).keySet().size());
    assertEquals(113, TopicReader.getTopics(Topics.CIRAL_V10_SW_TEST_B).keySet().size());
    assertEquals(554, TopicReader.getTopics(Topics.CIRAL_V10_YO_TEST_B).keySet().size());
    assertEquals(312, TopicReader.getTopics(Topics.CIRAL_V10_HA_TEST_B_NATIVE).keySet().size());
    assertEquals(239, TopicReader.getTopics(Topics.CIRAL_V10_SO_TEST_B_NATIVE).keySet().size());
    assertEquals(112, TopicReader.getTopics(Topics.CIRAL_V10_SW_TEST_B_NATIVE).keySet().size());
    assertEquals(554, TopicReader.getTopics(Topics.CIRAL_V10_YO_TEST_B_NATIVE).keySet().size());
  }
}
