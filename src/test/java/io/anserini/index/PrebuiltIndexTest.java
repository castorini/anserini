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

package io.anserini.index;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrebuiltIndexTest {

  // test checksum validity
  @Test
  public void testChecksum() {
    for (IndexInfo info : IndexInfo.values()) {
      // check each checksum is valid
      assertEquals(32, info.md5.length());
      assertTrue(info.md5.matches("[a-fA-F0-9]+"));
    }
  }

  // test url validity
  @Test
  public void testUrls() {
    for (IndexInfo info : IndexInfo.values()) {
      for (String url : info.urls) {
        // check each url status code is 200
        try {
          final URL requestUrl = new URL("http://example.com");
          final HttpURLConnection con = (HttpURLConnection) requestUrl.openConnection();
          assertEquals(200, con.getResponseCode());
        } catch (IOException e) {
          throw new RuntimeException("Error connecting to " + url, e);
        } catch (Exception e) {
          throw new RuntimeException("Malformed URL: " + url, e);
        }
      }
    }
  }

  // test number of prebuilt-indexes
  @Test
  public void testNumPrebuiltIndexes() {
    assertEquals(94, IndexInfo.values().length);
  }
}
