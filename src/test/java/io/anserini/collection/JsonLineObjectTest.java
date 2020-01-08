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
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// A file in a JsonCollection can either be:
// (1) A single JSON object (i.e., a single document)
// (2) An array of JSON objects
// (3) JSON Lines (i.e., one JSON object per line)
//
// This is the test case for (3)
//
// Note that we're testing the multifield capability here and only here, since the codepath is shared.
public class JsonLineObjectTest extends DocumentTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    String doc =
      "{" +
      "  \"id\": \"doc1\"," +
      "  \"contents\": \"this is the contents 1.\"," +
      "  \"field1\": \"doc1 field1 content\"," +
      "  \"field2\": \"doc1 field2 content\"" +
      "}\n" +
      "{ " +
      "  \"id\": \"doc2\"," +
      "  \"contents\": \"this is the contents 2.\"," +
      "  \"field1\": \"doc2 field1 content\"," +
      "  \"field2\": \"doc2 field2 content\"" +
      "}";

    rawFiles.add(createFile(doc));

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "doc1");
    doc1.put("content", "this is the contents 1.");
    doc1.put("field1", "doc1 field1 content");
    doc1.put("field2", "doc1 field2 content");
    expected.add(doc1);
    HashMap<String, String> doc2 = new HashMap<>();
    doc2.put("id", "doc2");
    doc2.put("content", "this is the contents 2.");
    doc2.put("field1", "doc2 field1 content");
    doc2.put("field2", "doc2 field2 content");
    expected.add(doc2);
  }

  @Test
  public void test() throws IOException {
    JsonCollection collection = new JsonCollection();
    for (int i = 0; i < rawFiles.size(); i++) {
      Iterator<JsonCollection.Document> iter =
              collection.createFileSegment(rawFiles.get(i)).iterator();
      int j = 0;
      while (iter.hasNext()) {
        JsonCollection.Document parsed = iter.next();
        assertEquals(parsed.id(), expected.get(j).get("id"));
        assertEquals(parsed.content(), expected.get(j).get("content"));
        for (Map.Entry<String, String> e : parsed.fields().entrySet()) {
          assertEquals(e.getValue(), expected.get(j).get(e.getKey()));
        }
        j++;
      }
    }
  }

  // Tests if the iterator is behaving properly. If it is, we shouldn't have any issues running into
  // NoSuchElementExceptions.
  @Test
  public void testStreamIteration() {
    JsonCollection collection = new JsonCollection();
    try {
      Iterator<JsonCollection.Document> iter =
              collection.createFileSegment(rawFiles.get(0)).iterator();
      AtomicInteger cnt = new AtomicInteger();
      iter.forEachRemaining(d -> cnt.incrementAndGet());
      assertEquals(2, cnt.get());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
