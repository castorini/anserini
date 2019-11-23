/**
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

import io.anserini.IndexerTestBase;
import io.anserini.search.SimpleSearcher.Result;
import org.junit.Test;

public class SimpleSearcherTest extends IndexerTestBase {

  @Test
  public void test1() throws Exception {
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    Result[] results;

    results = searcher.search("text", 1);
    assertEquals(1, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals(0.2912999987602234f, results[0].score, 10e-6);

    results = searcher.search("text");
    assertEquals(2, results.length);
    assertEquals("doc1", results[0].docid);
    assertEquals("doc2", results[1].docid);
    assertEquals(0.2912999987602234f, results[0].score, 10e-6);
    assertEquals(0.27070000767707825f, results[1].score, 10e-6);

    results = searcher.search("test");
    assertEquals(1, results.length);
    assertEquals("doc3", results[0].docid);
    assertEquals(0.5648999810218811f, results[0].score, 10e-6);
  }
}
