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
import io.anserini.index.prebuilt.PrebuiltFlatIndex;
import io.anserini.index.prebuilt.PrebuiltHnswIndex;
import io.anserini.index.prebuilt.PrebuiltImpactIndex;
import io.anserini.index.prebuilt.PrebuiltInvertedIndex;

public class PrebuiltIndexRegistryTest extends StdOutStdErrRedirectableLuceneTestCase {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final TypeReference<List<Map<String, Object>>> DETAIL_LIST_TYPE =
      new TypeReference<List<Map<String, Object>>>() {};

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
    PrebuiltIndexRegistry.main(new String[] {"-invalid"});
    assertTrue(err.toString().startsWith("Error: \"-invalid\" is not a valid option"));
  }

  @Test
  public void testHelp() {
    PrebuiltIndexRegistry.main(new String[] {"--help"});
    assertTrue(err.toString().contains("Options for PrebuiltIndexRegistry:"));
    assertTrue(err.toString().contains("--help"));
  }

  @Test
  public void testMissingList() {
    PrebuiltIndexRegistry.main(new String[0]);
    assertTrue(err.toString().contains("Options for PrebuiltIndexRegistry:"));
    assertEquals("", out.toString());
  }

  @Test
  public void testList() throws Exception {
    PrebuiltIndexRegistry.main(new String[] {"--list"});

    List<Map<String, Object>> details = MAPPER.readValue(out.toString(), DETAIL_LIST_TYPE);

    int expectedSize = PrebuiltInvertedIndex.entries().size()
        + PrebuiltImpactIndex.entries().size()
        + PrebuiltFlatIndex.entries().size()
        + PrebuiltHnswIndex.entries().size();
    assertEquals(expectedSize, details.size());

    Set<String> names = new TreeSet<>();
    for (Map<String, Object> detail : details) {
      assertNotNull(detail.get("name"));
      assertNotNull(detail.get("type"));
      assertNotNull(detail.get("corpus_index"));
      names.add((String) detail.get("name"));
    }
    assertEquals(expectedSize, names.size());
  }

  @Test
  public void testListFilterByType() throws Exception {
    PrebuiltIndexRegistry.main(new String[] {"--list", "--type", "flat"});

    List<Map<String, Object>> details = MAPPER.readValue(out.toString(), DETAIL_LIST_TYPE);

    assertEquals(PrebuiltFlatIndex.entries().size(), details.size());
    for (Map<String, Object> detail : details) {
      assertEquals("flat", detail.get("type"));
    }
  }

  @Test
  public void testListFilterByTypeAndName() throws Exception {
    PrebuiltIndexRegistry.main(new String[] {"--list", "--type", "inverted", "--filter", "msmarco-v1"});

    List<Map<String, Object>> details = MAPPER.readValue(out.toString(), DETAIL_LIST_TYPE);

    int expectedSize = (int) PrebuiltInvertedIndex.entries().stream()
        .filter((entry) -> entry.name.contains("msmarco-v1"))
        .count();
    assertEquals(expectedSize, details.size());
    for (Map<String, Object> detail : details) {
      assertEquals("inverted", detail.get("type"));
      assertTrue(((String) detail.get("name")).contains("msmarco-v1"));
    }
  }

  @Test
  public void testListWithInvalidFilterRegex() {
    PrebuiltIndexRegistry.main(new String[] {"--list", "--filter", "["});
    assertTrue(err.toString().contains("Error: invalid regular expression \"[\""));
    assertEquals("", out.toString());
  }

  @Test
  public void testInvalidType() {
    PrebuiltIndexRegistry.main(new String[] {"--list", "--type", "dense"});
    assertTrue(err.toString().contains("Error: invalid --type \"dense\""));
  }
}
