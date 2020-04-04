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
package io.anserini.search;

import io.anserini.ann.IndexVectorsTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SimpleNearestNeighborSearcherTest {

  @Test
  public void testSearchingFW() throws Exception {
    String idxPath = "target/ast" + System.currentTimeMillis();
    IndexVectorsTest.createIndex(idxPath, "fw", true);
    SimpleNearestNeighborSearcher simpleNearestNeighborSearcher = new SimpleNearestNeighborSearcher(idxPath);
    SimpleNearestNeighborSearcher.Result[][] results = simpleNearestNeighborSearcher.search("text", 2);
    assertNotNull(results);
    assertEquals(1, results.length);
    assertEquals(2, results[0].length);
  }

  @Test
  public void testSearchingLL() throws Exception {
    String idxPath = "target/ast" + System.currentTimeMillis();
    IndexVectorsTest.createIndex(idxPath, "lexlsh", true);
    SimpleNearestNeighborSearcher simpleNearestNeighborSearcher = new SimpleNearestNeighborSearcher(idxPath, "lexlsh");
    SimpleNearestNeighborSearcher.Result[][] results = simpleNearestNeighborSearcher.search("text", 2);
    assertNotNull(results);
    assertEquals(1, results.length);
    assertEquals(1, results[0].length);
  }
}