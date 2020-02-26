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

package io.anserini.collection;

import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

// A file in a JsonCollection can either be:
// (1) A single JSON object (i.e., a single document)
// (2) An array of JSON objects
// (3) JSON Lines (i.e., one JSON object per line)
//
// This is the test case for (1)
public class JsonDocumentObjectTest extends CollectionTest<JsonCollection.Document> {
  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/json/collection1");

    Path p1 = Paths.get("src/test/resources/sample_docs/json/collection1/doc1.json");
    Path p2 = Paths.get("src/test/resources/sample_docs/json/collection1/doc2.json");

    segmentPaths.add(p1);
    segmentDocCounts.put(p1, 1);
    segmentPaths.add(p2);
    segmentDocCounts.put(p2, 1);

    totalSegments = 2;
    totalDocs = 2;

    collection = new JsonCollection(Paths.get("src/test/resources/sample_docs/json/collection1"));

    expected.put("doc1", Map.of("id", "doc1","content", "contents of document 1."));
    expected.put("doc2", Map.of("id", "doc2","content", "Some more \"blah\" text in document2!"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("content"), doc.content());
  }
}
