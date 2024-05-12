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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ControllerTest {

  @Test
  public void testSearch() throws Exception {
    Controller controller = new Controller();

    List<QueryResult> results = controller.search(null, "Albert Einstein");

    assertEquals(results.size(), 10);
    assertEquals(results.get(0).getDocid(), "3075155");
  }

  @Test
  public void testIndexNotFound() throws Exception {
    Controller controller = new Controller();

    assertThrows(RuntimeException.class, () -> {
      List<QueryResult> results = controller.search("nonexistent-index", "Albert Einstein");
    });
  }

}
