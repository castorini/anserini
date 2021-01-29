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
    assertEquals(null, TopicReader.getTopicReaderClassByFile("topics.unknown.txt"));
  }

  @Test
  public void testGetTopicsByFile() {
    SortedMap<Object, Map<String, String>> topics =
        TopicReader.getTopicsByFile("src/main/resources/topics-and-qrels/topics.robust04.txt");

    assertNotNull(topics);
    assertEquals(250, topics.size());
    assertEquals(301, (int) topics.firstKey());
    assertEquals("International Organized Crime", topics.get(topics.firstKey()).get("title"));
    assertEquals(700, (int) topics.lastKey());
    assertEquals("gasoline tax U.S.", topics.get(topics.lastKey()).get("title"));
  }

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

    topics = TopicReader.getTopics(Topics.MSMARCO_PASSAGE_TEST_SUBSET);
    assertNotNull(topics);
    assertEquals(6837, topics.size());
    assertEquals(57, (int) topics.firstKey());
    assertEquals("term service agreement definition", topics.get(topics.firstKey()).get("title"));
    assertEquals(1136966, (int) topics.lastKey());
    assertEquals("#ffffff color code", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void testDprNq() {
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

    topics = TopicReader.getTopics(Topics.DPR_NQ_TEST);
    assertNotNull(topics);
    assertEquals(3610, topics.size());
    assertEquals(0, (int) topics.firstKey());
    assertEquals("who got the first nobel prize in physics", topics.get(topics.firstKey()).get("title"));
    assertEquals("['Wilhelm Conrad Röntgen']", topics.get(topics.firstKey()).get("answers"));
    assertEquals(3609, (int) topics.lastKey());
    assertEquals("when did computer become widespread in homes and schools", topics.get(topics.lastKey()).get("title"));
    assertEquals("['1980s']", topics.get(topics.lastKey()).get("answers"));
  }

  @Test
  public void testDprTrivia() {
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
  public void testDprWq() {
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
  public void testDprCurated() {
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
  public void testDprSquad() {
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
  public void testTREC19DL() {
    SortedMap<Integer, Map<String, String>> topics;

    topics = TopicReader.getTopics(Topics.TREC2019_DL_PASSAGE);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals("anthropological definition of environment", topics.get(topics.firstKey()).get("title"));
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals("how is the weather in jamaica", topics.get(topics.lastKey()).get("title"));
    assertEquals("does legionella pneumophila cause pneumonia", topics.get(168216).get("title"));

    topics = TopicReader.getTopics(Topics.TREC2019_DL_DOC);
    assertNotNull(topics);
    assertEquals(43, topics.size());
    assertEquals(19335, (int) topics.firstKey());
    assertEquals("anthropological definition of environment", topics.get(topics.firstKey()).get("title"));
    assertEquals(1133167, (int) topics.lastKey());
    assertEquals("how is the weather in jamaica", topics.get(topics.lastKey()).get("title"));
    assertEquals("how long to hold bow in yoga", topics.get(1132213).get("title"));
  }

  @Test
  public void testMSMARO_TopicIdsAsStrings() {
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
  }

  @Test
  public void testEpidemicQATopics() {
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
