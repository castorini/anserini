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

package io.anserini.server;

import io.anserini.index.IndexInfo;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class ControllerTest {

  @Test
  public void testSearch() throws Exception {
    ControllerV1_0 controller = new ControllerV1_0();

    Map<String, Object> results = controller.searchIndex(null, "Albert Einstein", 10, "", null, null, null);
    assertNotNull(results);
    assertTrue(results.get("candidates") instanceof List);

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> candidates = (List<Map<String, Object>>) results.get("candidates");
    assertEquals(10, candidates.size());
    assertEquals("3553430", candidates.get(0).get("docid"));
  }

  @Test
  public void testIndexNotFound() throws Exception {
    ControllerV1_0 controller = new ControllerV1_0();

    assertThrows(RuntimeException.class, () -> {
      Map<String, Object> results = controller.searchIndex("nonexistent-index", "Albert Einstein", 10, "", null, null, null);
    });
  }

  @Test
  public void testListIndexes() throws Exception {
    ControllerV1_0 controller = new ControllerV1_0();
    Map<String, Map<String, Object>> indexes = controller.listIndexes();
    assertEquals(indexes.size(), IndexInfo.values().length);
  }

}
