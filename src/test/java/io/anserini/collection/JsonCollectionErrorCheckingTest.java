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

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

// A file in a JsonCollection might not have the required fields that
// we expect. This tests whether the appropriate error is thrown.
public class JsonCollectionErrorCheckingTest extends DocumentTest {
  protected HashMap<String, FileSegment<JsonCollection.Document>> fileSegments = 
    new HashMap<String, FileSegment<JsonCollection.Document>>();

  @Before
  public void setUp() throws Exception {
    super.setUp();

    HashMap<String, String> jsonBlobs = new HashMap<String, String>();

    jsonBlobs.put(
      "idMissing", 
      "[\n" +
      "  {\n" +
      "    \"contents\": \"doc1\"\n" +
      "  }\n" +
      "]"
    );

    jsonBlobs.put(
      "contentMissing",
      "[\n" +
      "  {\n" +
      "    \"id\": \"doc2\"\n" +
      "  }\n" +
      "]"
    );

    for (HashMap.Entry<String, String> entry : jsonBlobs.entrySet()) {
      JsonCollection collection = new JsonCollection();
      fileSegments.put(entry.getKey(), collection.createFileSegment(createFile(entry.getValue())));
    }
  }

  @Test(expected = RuntimeException.class)
  public void missingFieldExceptionForContent() {
    JsonCollection.Document parsed = fileSegments.get("contentMissing").iterator().next();
    parsed.content();
  }
  
  @Test(expected = RuntimeException.class)
  public void missingFieldExceptionForId() {
    JsonCollection.Document parsed = fileSegments.get("idMissing").iterator().next();
    parsed.id();
  }
}
