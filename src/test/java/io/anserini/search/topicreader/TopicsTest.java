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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.junit.Test;

public class TopicsTest {

  @Test
  public void testTotalCount() {
    assertEquals(478, Topics.names().size());
  }

  @Test
  public void testBasic() {
    // Not intended to be exhaustive, just spot checks.
    assertEquals("topics.msmarco-passage.dev-subset.txt", Topics.get("msmarco-passage.dev-subset").path);
    assertEquals("topics.dl19-passage.txt", Topics.get("dl19-passage").path);
    assertEquals("topics.dl20.txt", Topics.get("dl20").path);
  }

  @Test
  public void testSymbols() {
    // Not practical to exhaustively test all aliases, just spot checks.
    assertEquals(Topics.get("msmarco-passage.dev-subset"), Topics.get("msmarco-passage-dev"));
    assertEquals(Topics.get("msmarco-passage.dev-subset"), Topics.get("msmarco-v1-passage-dev"));
    assertEquals(Topics.get("msmarco-passage.dev-subset"), Topics.get("msmarco-v1-passage.dev"));

    assertEquals(Topics.get("dl20"), Topics.get("dl20-passage"));
    assertEquals(Topics.get("dl20"), Topics.get("dl20-doc"));
    assertEquals(Topics.get("dl21"), Topics.get("dl21-passage"));
    assertEquals(Topics.get("dl21"), Topics.get("dl21-doc"));
    assertEquals(Topics.get("dl22"), Topics.get("dl22-passage"));
    assertEquals(Topics.get("dl22"), Topics.get("dl22-doc"));
    assertEquals(Topics.get("dl23"), Topics.get("dl23-passage"));
    assertEquals(Topics.get("dl23"), Topics.get("dl23-doc"));
  }

  @Test
  public void testLocalAliasesExtendExternalAliases() {
    Topics canonical = Topics.get("msmarco-passage.dev-subset");
    assertEquals(canonical, Topics.get("msmarco-passage-dev"));
    assertEquals(canonical, Topics.get("dummy.msmarco-passage.dev-subset"));
  }

  @Test
  public void testAdhocAliases() {
    Topics trec7 = Topics.get("adhoc.351-400");
    assertTrue(Topics.names().contains("adhoc.351-400"));
    assertFalse(Topics.names().contains("trec7-adhoc"));
    assertEquals(trec7, Topics.get("trec7-adhoc"));

    Topics trec8 = Topics.get("adhoc.401-450");
    assertTrue(Topics.names().contains("adhoc.401-450"));
    assertFalse(Topics.names().contains("trec8-adhoc"));
    assertEquals(trec8, Topics.get("trec8-adhoc"));
  }

  @Test
  public void testLocalBindings() {
    // Canonical should be in names().
    assertTrue(Topics.names().contains("dummy"));
    assertDummyTopics("dummy");

    // Aliases shouldn't be in names().
    assertFalse(Topics.names().contains("dummy.1"));
    assertFalse(Topics.names().contains("dummy.2"));

    assertEquals(List.of("dummy.1", "dummy.2"), Topics.aliases("dummy"));
    assertEquals(List.of("dummy.1", "dummy.2"), Topics.aliases("dummy.1"));

    // But they should be available via get.
    assertDummyTopics("dummy.1");
    assertDummyTopics("dummy.2");
  }

  private void assertDummyTopics(String name) {
    Topics binding = Topics.get(name);
    assertEquals("dummy", binding.name());
    assertEquals("topics.dummy.tsv", binding.path);
    assertEquals(TsvStringTopicReader.class, binding.readerClass);

    SortedMap<String, Map<String, String>> topics = Topics.load(name);
    assertEquals(2, topics.size());
    assertEquals("blue whale migration", topics.get("1").get("title"));
    assertEquals("ancient roman aqueducts", topics.get("2").get("title"));
  }

  @Test
  public void testLoadMsMarcoV1Passage1() {
    SortedMap<Integer, Map<String, String>> topics = Topics.load("msmarco-v1-passage.dev");

    assertEquals(6980, topics.size());
    assertEquals(Integer.valueOf(2), topics.firstKey());
    assertEquals("Androgen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(Integer.valueOf(1102400), topics.lastKey());
    assertEquals("why do bears hibernate", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void testLoadMsMarcoV1Passage2() {
    SortedMap<Integer, Map<String, String>> topics = Topics.load("msmarco-passage.dev-subset");

    assertEquals(6980, topics.size());
    assertEquals(Integer.valueOf(2), topics.firstKey());
    assertEquals("Androgen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(Integer.valueOf(1102400), topics.lastKey());
    assertEquals("why do bears hibernate", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void testLoadFromTopic() {
    SortedMap<Integer, Map<String, String>> topics = Topics.load(Topics.get("msmarco-passage.dev-subset"));

    assertEquals(6980, topics.size());
    assertEquals(Integer.valueOf(2), topics.firstKey());
    assertEquals("Androgen receptor define", topics.get(topics.firstKey()).get("title"));
    assertEquals(Integer.valueOf(1102400), topics.lastKey());
    assertEquals("why do bears hibernate", topics.get(topics.lastKey()).get("title"));
  }

  @Test
  public void testLoadInvalidTopics() {
    String invalidTopics = "this-is-not-valid-topics";

    try {
      Topics.load(invalidTopics);
      fail("Expected IllegalArgumentException to be thrown");
    } catch (IllegalArgumentException e) {
      assertEquals("\"" + invalidTopics + "\" does not refer to valid topics.", e.getMessage());
    }
  }

  @Test
  public void testResolveRegisteredTopicPathWithUnknownName() throws IOException {
    String invalidTopics = "this-topic-name-does-not-exist";

    try {
      Topics.resolveRegisteredTopicPath(invalidTopics);
      fail("Expected IllegalArgumentException to be thrown");
    } catch (IllegalArgumentException e) {
      assertEquals("Unknown topics name: " + invalidTopics, e.getMessage());
    }
  }

  @Test
  public void testAliasesWithUnknownName() {
    String invalidTopics = "this-topic-name-does-not-exist";

    try {
      Topics.aliases(invalidTopics);
      fail("Expected IllegalArgumentException to be thrown");
    } catch (IllegalArgumentException e) {
      assertEquals("Unknown topics name: " + invalidTopics, e.getMessage());
    }
  }
}
