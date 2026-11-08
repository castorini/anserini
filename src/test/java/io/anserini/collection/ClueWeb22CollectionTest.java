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

public class ClueWeb22CollectionTest extends DocumentCollectionTest<ClueWeb22Collection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/clueweb22/");
    collection = new ClueWeb22Collection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/clueweb22/txt/en/en00/en0000/en0000-30.json.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 2);

    totalSegments = 1;
    totalDocs = 2;

    expected.put("clueweb22-en0000-30-00000", Map.of("id", "clueweb22-en0000-30-00000"));
    expected.put("clueweb22-en0000-30-00001", Map.of("id", "clueweb22-en0000-30-00001"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertNotNull(doc.contents());
    assertTrue(doc.contents().length() > 0);
  }
}
