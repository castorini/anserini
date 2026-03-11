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
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;
import io.anserini.search.topicreader.Topics;

public class QueriesTest extends StdOutStdErrRedirectableLuceneTestCase {
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
  public void testList() throws Exception {
    Queries.main(new String[] {"--list"});

    java.util.List<java.util.Map<String, Object>> details =
        new ObjectMapper().readValue(out.toString(), java.util.List.class);

    int expectedSize = Topics.values().length;
    assertEquals(expectedSize, details.size());

    Set<String> names = new TreeSet<>();
    for (java.util.Map<String, Object> detail : details) {
      assertNotNull(detail.get("name"));
      assertNotNull(detail.get("path"));
      assertNotNull(detail.get("reader"));
      names.add((String) detail.get("name"));
    }
    assertEquals(expectedSize, names.size());
  }

  @Test
  public void testMissingRequiredOption() {
    Queries.main(new String[] {});
    assertTrue(err.toString().contains("Error: exactly one of --list or --get must be specified"));
  }

  @Test
  public void testGet() throws Exception {
    Path topicFile = Path.of("topics.dl19-passage.txt");
    Files.writeString(topicFile, "1\tquery one\n2\tquery two\n", StandardCharsets.UTF_8);

    try {
      Queries.main(new String[] {"--get", "TREC2019_DL_PASSAGE"});

      java.util.Map<String, java.util.Map<String, String>> queries =
          new ObjectMapper().readValue(out.toString(), java.util.Map.class);

      assertEquals(2, queries.size());
      assertEquals("query one", queries.get("1").get("title"));
      assertEquals("query two", queries.get("2").get("title"));
    } finally {
      Files.deleteIfExists(topicFile);
    }
  }

  @Test
  public void testGetInvalidTopic() {
    Queries.main(new String[] {"--get", "NOT_A_TOPIC"});
    assertTrue(err.toString().contains("Error: unknown topic \"NOT_A_TOPIC\""));
  }
}
