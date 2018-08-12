/**
 * Anserini: An information retrieval toolkit built on Lucene
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

import java.io.*;
import java.util.HashMap;
import java.util.NoSuchElementException;


public class JsonDocumentArrayTest extends DocumentTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    String doc =
      "[\n" +
      "  {\n" +
      "    \"id\": \"doc1\",\n" +
      "    \"contents\": \"this is the contents 1.\"\n" +
      "  },\n" +
      "  {\n" +
      "    \"id\": \"doc2\",\n" +
      "    \"contents\": \"this is the contents 2.\"\n" +
      "  }\n" +
      "]";

    rawFiles.add(createFile(doc));

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "doc1");
    doc1.put("content", "this is the contents 1.");
    expected.add(doc1);
    HashMap<String, String> doc2 = new HashMap<>();
    doc2.put("id", "doc2");
    doc2.put("content", "this is the contents 2.");
    expected.add(doc2);
  }

  @Test
  public void test() throws IOException {
    JsonCollection collection = new JsonCollection();
    int j = 0;
    for (int i = 0; i < rawFiles.size(); i++) {
      BaseFileSegment<JsonCollection.Document> iter = collection.createFileSegment(rawFiles.get(i));
      while (true) {
        boolean hasNext;
        try {
          hasNext = iter.hasNext();
        } catch (NoSuchElementException e) {
          break;
        }

        if (!hasNext) {
          break;
        }

        JsonCollection.Document parsed = iter.next();
        assertEquals(parsed.id(), expected.get(j).get("id"));
        assertEquals(parsed.content(), expected.get(j).get("content"));
        j++;
      }
    }
  }
}
