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

package io.anserini.cli;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;
import io.anserini.search.topicreader.Topics;

public class TopicsCatalogTest extends StdOutStdErrRedirectableLuceneTestCase {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final TypeReference<List<String>> NAME_LIST_TYPE =
      new TypeReference<List<String>>() {};
  private static final TypeReference<Map<String, Map<String, String>>> QUERY_MAP_TYPE =
      new TypeReference<Map<String, Map<String, String>>>() {};

  @Before
  public void setUp() throws Exception {
    redirectStdOut();
    redirectStdErr();
    super.setUp();
  }

  @After
  public void tearDown() throws Exception {
    restoreStdOut();
    restoreStdErr();
    super.tearDown();
  }

  @Test
  public void testInvalidOption() {
    TopicsCatalog.main(new String[] {"--invalid"});
    assertTrue(err.toString().contains("Error:"));
    assertTrue(err.toString().contains("--invalid"));
    assertTrue(err.toString().contains("Options for TopicsCatalog:"));
  }

  @Test
  public void testHelp() {
    TopicsCatalog.main(new String[] {"--help"});
    assertTrue(err.toString().contains("Options for TopicsCatalog:"));
    assertTrue(err.toString().contains("--help"));
    assertFalse(err.toString().contains("Error:"));
  }

  @Test
  public void testFilterRequiresList() {
    TopicsCatalog.main(new String[] {"--get", "TREC2019_DL_PASSAGE", "--filter", "DL"});
    assertTrue(err.toString().contains("Error: --filter only works with --list"));
  }

  @Test
  public void testListWithFilter() throws Exception {
    TopicsCatalog.main(new String[] {"--list", "--filter", "msmarco"});

    List<String> names = MAPPER.readValue(out.toString(), NAME_LIST_TYPE);

    Set<String> expectedNames = new TreeSet<>(Topics.getSymbolDictionaryKeys());
    for (Topics topic : Topics.values()) {
      expectedNames.add(topic.name());
    }
    expectedNames.removeIf(name -> !name.contains("msmarco"));

    assertEquals(expectedNames.size(), names.size());
    assertEquals(expectedNames, new TreeSet<>(names));
  }

  @Test
  public void testMissingRequiredOption() {
    TopicsCatalog.main(new String[] {});
    assertTrue(err.toString().contains("Error: exactly one of --list or --get must be specified"));
  }

  @Test
  public void testList() throws Exception {
    TopicsCatalog.main(new String[] {"--list"});

    List<String> names = MAPPER.readValue(out.toString(), NAME_LIST_TYPE);

    Set<String> expectedNames = new TreeSet<>(Topics.getSymbolDictionaryKeys());
    for (Topics topic : Topics.values()) {
      expectedNames.add(topic.name());
    }

    assertEquals(expectedNames.size(), names.size());
    assertEquals(expectedNames, new TreeSet<>(names));
  }

  @Test
  public void testGet() throws Exception {
    Path topicFile = Path.of("topics.dl19-passage.txt");
    Files.writeString(topicFile, "1\tquery one\n2\tquery two\n", StandardCharsets.UTF_8);

    try {
      TopicsCatalog.main(new String[] {"--get", "TREC2019_DL_PASSAGE"});

      Map<String, Map<String, String>> queries = MAPPER.readValue(out.toString(), QUERY_MAP_TYPE);

      assertEquals(2, queries.size());
      assertEquals("query one", queries.get("1").get("title"));
      assertEquals("query two", queries.get("2").get("title"));
    } finally {
      Files.deleteIfExists(topicFile);
    }
  }

  @Test
  public void testGetInvalidTopic() {
    TopicsCatalog.main(new String[] {"--get", "NOT_A_TOPIC"});
    assertTrue(err.toString().contains("Error: unknown topics \"NOT_A_TOPIC\""));
  }
}
