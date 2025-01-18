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
import static org.junit.Assert.assertArrayEquals;

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

      // Compare actual values
      assertArrayEquals("Vector values should match", expected, vector, 0.0001f);
    } else {
      // Non-array format should return null
      assertNull("Non-array format should return null from vector()", doc.vector());
    }
  }
}
