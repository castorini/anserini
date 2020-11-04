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
    TopicReader<Integer> consumer_reader =
        new EpidemicQATopicReader(
            Paths.get("src/main/resources/topics-and-qrels/topics.epidemic-qa.consumer.prelim.json"));

    SortedMap<Integer, Map<String, String>> consumer_topics = consumer_reader.read();

    // No consumer questions from CQ035 to CQ037
    assertEquals(42, consumer_topics.keySet().size());
    assertEquals(1, (int) consumer_topics.firstKey());
    assertEquals("what is the origin of COVID-19",
                 consumer_topics.get(consumer_topics.firstKey()).get("question"));
    assertEquals("CQ001", consumer_topics.get(consumer_topics.firstKey()).get("question_id"));
    assertEquals("coronavirus origin", consumer_topics.get(consumer_topics.firstKey()).get("query"));
    assertEquals("seeking information about whether the virus was designed in a lab or occured "
                 + "naturally in animals and how it got to humans",
                 consumer_topics.get(consumer_topics.firstKey()).get("background"));

    assertEquals(45, (int) consumer_topics.lastKey());
    assertEquals("how has the COVID-19 pandemic impacted mental health?",
                 consumer_topics.get(consumer_topics.lastKey()).get("question"));
    assertEquals("CQ045", consumer_topics.get(consumer_topics.lastKey()).get("question_id"));
    assertEquals("coronavirus mental health impact",
                 consumer_topics.get(consumer_topics.lastKey()).get("query"));
    assertEquals("seeking information about psychological effects of COVID-19 and "
                 + "COVID-19 effect on mental health and pre-existing conditions",
                 consumer_topics.get(consumer_topics.lastKey()).get("background"));

    TopicReader<Integer> expert_reader =
        new EpidemicQATopicReader(
            Paths.get("src/main/resources/topics-and-qrels/topics.epidemic-qa.expert.prelim.json"));

    SortedMap<Integer, Map<String, String>> expert_topics = expert_reader.read();

    assertEquals(45, expert_topics.keySet().size());

    assertEquals(1, (int) expert_topics.firstKey());
    assertEquals("what is the origin of COVID-19",
                 expert_topics.get(expert_topics.firstKey()).get("question"));
    assertEquals("EQ001", expert_topics.get(expert_topics.firstKey()).get("question_id"));
    assertEquals("coronavirus origin", expert_topics.get(expert_topics.firstKey()).get("query"));
    assertEquals("seeking range of information about the SARS-CoV-2 virus's origin,"
                 + " including its evolution, animal source, and first transmission into humans",
                 expert_topics.get(expert_topics.firstKey()).get("background"));

    assertEquals(45, (int) expert_topics.lastKey());
    assertEquals("How has the COVID-19 pandemic impacted mental health?",
                 expert_topics.get(expert_topics.lastKey()).get("question"));
    assertEquals("EQ045", expert_topics.get(expert_topics.lastKey()).get("question_id"));
    assertEquals("coronavirus mental health impact",
                 expert_topics.get(expert_topics.lastKey()).get("query"));
    assertEquals("Includes increasing/decreasing rates of depression, anxiety, panic disorder,"
                 + " and other psychiatric and mental health conditions.",
                 expert_topics.get(expert_topics.lastKey()).get("background"));
  }
}
