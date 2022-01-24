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

public class MrTyDiCollectionArTest extends DocumentCollectionTest<MrTyDiCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/ar");
    collection = new MrTyDiCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/ar/corpus.jsonl.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);

    totalSegments = 1;
    totalDocs = 4;

    expected.put("7#0", Map.of("id", "7#0"));
    expected.put("7#1", Map.of("id", "7#1"));
    expected.put("7#2", Map.of("id", "7#2"));
    expected.put("7#3", Map.of("id", "7#3"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
  }
}
