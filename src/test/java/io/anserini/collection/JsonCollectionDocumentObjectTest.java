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
// This is the test case for (1)
public class JsonCollectionDocumentObjectTest extends JsonCollectionTest {
  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/json/collection1");
    collection = new JsonCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/json/collection1/doc1.json");
    Path segment2 = Paths.get("src/test/resources/sample_docs/json/collection1/doc2.json");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 1);
    segmentPaths.add(segment2);
    segmentDocCounts.put(segment2, 1);

    totalSegments = 2;
    totalDocs = 2;

    expected.put("doc1", Map.of("id", "doc1",
            "content", "contents of document 1.",
            "raw", "{\n" +
                    "  \"id\" : \"doc1\",\n" +
                    "  \"contents\" : \"contents of document 1.\"\n" +
                    "}"));
    expected.put("doc2", Map.of("id", "doc2",
            "content", "Some more \"blah\" text in document2!",
            "raw", "{\n" +
                    "  \"id\" : \"doc2\",\n" +
                    "  \"contents\" : \"Some more \\\"blah\\\" text in document2!\"\n" +
                    "}"));
  }
}
