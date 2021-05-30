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

public class CleanTrecCollectionTest extends DocumentCollectionTest<CleanTrecCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/clean_trec/collection1");
    collection = new CleanTrecCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/clean_trec/collection1/segment1.txt");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 2);

    totalSegments = 1;
    totalDocs = 2;

    expected.put("doc1", Map.of("id", "doc1",
        "raw","<TEXT>\nhere is a bit of text, and the <p>tags should not be removed.</p>\n</TEXT>"));
    expected.put("doc2", Map.of("id", "doc2",
        "raw", "<TEXT>\nhere is some text.\n</TEXT>"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("raw"), doc.contents());
    assertEquals(expected.get("raw"), doc.raw());
  }
}
