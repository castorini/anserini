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

import java.util.Map;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonVectorCollectionTest extends DocumentCollectionTest<JsonVectorCollection.Document> {
  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("content"), doc.contents());

    // Checking raw is optional
    if (expected.get("raw") != null) {
      assertEquals(expected.get("raw"), doc.raw());
    }

    validateVectorIfPresent(doc, expected.get("content"));
  }

  /**
   * Helper method to validate vector data.
   * Separated from checkDocument for cleaner testing and better error messages.
   */
  protected void validateVectorIfPresent(SourceDocument doc, String content) {
    if (content != null && content.startsWith("[") && content.endsWith("]")) {
      // Should be a dense vector
      float[] vector = doc.vector();
      assertNotNull("Dense vector format should return non-null vector()", vector);

      // Parse expected values from content string
      String[] parts = content.substring(1, content.length() - 1).split(",");
      float[] expected = new float[parts.length];
      for (int i = 0; i < parts.length; i++) {
        expected[i] = Float.parseFloat(parts[i].trim());
      }

      assertArrayEquals("Vector values should match", expected, vector, 0.0001f);
    } else {
      assertNull("Non-array format should return null from vector()", doc.vector());
    }
  }

  /*
   * Verify the cases where the vectorNode is an array
   * If is not, then contents is null
   */
  @Test
  public void testDenseVectorDocument() throws Exception {
    String jsonStr = "{\"id\":\"dense1\",\"vector\":[1.1,2.2,3.3,4.4,5.5,6.6,7.7,8.8,9.9,10.1,11.11,12.12,13.13,14.14,15.15]}";
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(jsonStr);
    JsonVectorCollection.Document doc = new JsonVectorCollection.Document(jsonNode);
    assertEquals("dense1", doc.id());
    assertNull(doc.contents());
    float[] expected = new float[] { 1.1f, 2.2f, 3.3f, 4.4f, 5.5f, 6.6f, 7.7f, 8.8f, 9.9f, 10.1f, 11.11f, 12.12f, 13.13f, 14.14f, 15.15f };
    float[] actual = doc.vector();
    assertNotNull(actual);
    assertArrayEquals(expected, actual, 0.0001f);
  }
}

