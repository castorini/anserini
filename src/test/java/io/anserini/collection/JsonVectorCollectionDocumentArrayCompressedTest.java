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

// A file in a JsonVectorCollection can either be:
// (1) A single JSON object (i.e., a single document)
// (2) An array of JSON objects
// (3) JSON Lines (i.e., one JSON object per line)
//
// This is the test case for (2)
public class JsonVectorCollectionDocumentArrayCompressedTest extends JsonVectorCollectionTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/json_vector/collection2_gz");
    collection = new JsonVectorCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/json_vector/collection2_gz/segment1.json.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 2);

    totalSegments = 1;
    totalDocs = 2;

    expected.put("doc1", Map.of("id", "doc1",
        "content", "f1 f2 f2 f3 f4 f4 f4 f4 f5 "));
    expected.put("doc2", Map.of("id", "doc2",
        "content", "f4 f4 f4 f5 f9 f9 f22 f22 f22 f22 f22 f22 f35 f35 f35 f35 f35 f35 f35 f35 "));
  }
}
