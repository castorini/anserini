package io.anserini.search.topicreader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.SortedMap;

import org.junit.Test;

public class PrioritizedWebTopicReaderTest {
  @Test
  public void testMillionQueryTopics3() throws IOException {
    Path resource = Paths.get("src/main/resources/topics-and-qrels/topics.mq.20001-60000.txt");
    TopicReader<Integer> reader = new PrioritizedWebTopicReader(resource);
    
    SortedMap<Integer, Map<String, String>> topics = reader.read();
    
    assertEquals(20001, (int) topics.firstKey());
    assertEquals("obama family tree", topics.get(topics.firstKey()).get("title").trim());
    assertEquals("1", topics.get(topics.firstKey()).get("priority").trim());

    assertEquals(60000, (int) topics.lastKey());
    assertEquals("bird shingles", topics.get(topics.lastKey()).get("title").trim());
    assertEquals("4", topics.get(topics.lastKey()).get("priority").trim());

    assertEquals(40000, topics.keySet().size());
  }
}
