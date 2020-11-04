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
import java.util.Map;

// A file in a JsonCollection can either be:
// (1) A single JSON object (i.e., a single document)
// (2) An array of JSON objects
// (3) JSON Lines (i.e., one JSON object per line)
//
// This is the test case for (3)
//
// Note that we're testing the multifield capability here and only here, since the codepath is shared.
public class JsonCollectionLineObjectTest extends JsonCollectionTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/json/collection3");
    collection = new JsonCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/json/collection3/segment1.json");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 2);

    totalSegments = 1;
    totalDocs = 2;

    expected.put("doc1", Map.of("id", "doc1",
        "content", "this is the contents 1.",
        "raw", "{\n" +
                    "  \"id\" : \"doc1\",\n" +
                    "  \"contents\" : \"this is the contents 1.\",\n" +
                    "  \"field1\" : \"doc1 field1 content\",\n" +
                    "  \"field2\" : \"doc1 field2 content\"\n" +
                    "}",
        "field1", "doc1 field1 content",
        "field2", "doc1 field2 content"));
    expected.put("doc2", Map.of("id", "doc2",
        "content", "this is the contents 2.",
        "raw", "{\n" +
                    "  \"id\" : \"doc2\",\n" +
                    "  \"contents\" : \"this is the contents 2.\",\n" +
                    "  \"field1\" : \"doc2 field1 content\",\n" +
                    "  \"field2\" : \"doc2 field2 content\"\n" +
                    "}",
        "field1", "doc2 field1 content",
        "field2", "doc2 field2 content"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("content"), doc.contents());
    assertEquals(expected.get("raw"), doc.raw());
    assertEquals(expected.get("field1"), ((JsonCollection.Document) doc).fields().get("field1"));
    assertEquals(expected.get("field2"), ((JsonCollection.Document) doc).fields().get("field2"));
  }
}
