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

package io.anserini.collection;

import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class MrTyDiCollectionThTest extends DocumentCollectionTest<MrTyDiCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/th");
    collection = new MrTyDiCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/th/corpus.jsonl.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);

    totalSegments = 1;
    totalDocs = 4;

    expected.put("1#0", Map.of("id", "1#0"));
    expected.put("545#0", Map.of("id", "545#0"));
    expected.put("545#1", Map.of("id", "545#1"));
    expected.put("545#2", Map.of("id", "545#2"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
  }
}
