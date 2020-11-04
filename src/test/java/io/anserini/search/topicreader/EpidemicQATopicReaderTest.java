package io.anserini.search.topicreader;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;

public class EpidemicQATopicReaderTest {
  @Test
  public void test() throws IOException {
    TopicReader<Integer> consumerReader =
        new EpidemicQATopicReader(
            Paths.get("src/main/resources/topics-and-qrels/topics.epidemic-qa.consumer.prelim.json"));

    SortedMap<Integer, Map<String, String>> consumerTopics = consumerReader.read();

    // No consumer questions from CQ035 to CQ037
    assertEquals(42, consumerTopics.keySet().size());
    assertEquals(1, (int) consumerTopics.firstKey());
    assertEquals("what is the origin of COVID-19",
                 consumerTopics.get(consumerTopics.firstKey()).get("question"));
    assertEquals("CQ001", consumerTopics.get(consumerTopics.firstKey()).get("question_id"));
    assertEquals("coronavirus origin", consumerTopics.get(consumerTopics.firstKey()).get("query"));
    assertEquals("seeking information about whether the virus was designed in a lab or occured " +
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

    TopicReader<Integer> expertReader =
        new EpidemicQATopicReader(
            Paths.get("src/main/resources/topics-and-qrels/topics.epidemic-qa.expert.prelim.json"));

    SortedMap<Integer, Map<String, String>> expertTopics = expertReader.read();

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
    assertEquals("Includes increasing/decreasing rates of depression, anxiety, panic disorder, " +
                 "and other psychiatric and mental health conditions.",
                 expertTopics.get(expertTopics.lastKey()).get("background"));
  }
}
