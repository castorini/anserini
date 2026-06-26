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

package io.anserini.index.prebuilt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.junit.Test;

public class PrebuiltInvertedIndexTest {
  @Test
  public void testInvalidName() {
    PrebuiltInvertedIndex.Entry entry = PrebuiltInvertedIndex.get("fake_index");
    assertNull(entry);
  }

  @Test
  public void testLookupByName() {
    PrebuiltInvertedIndex.Entry entry;

    entry = PrebuiltInvertedIndex.get("msmarco-v1-passage");
    assertNotNull(entry);
    assertEquals("lucene-inverted.msmarco-v1-passage.20221004.252b5e.tar.gz", entry.filename);
    assertEquals("678876e8c99a89933d553609a0fd8793", entry.md5);
    assertEquals(8841823, entry.documents);
    assertEquals(2170758745L, entry.size);

    entry = PrebuiltInvertedIndex.get("msmarco-v1-doc");
    assertNotNull(entry);
    assertEquals("lucene-inverted.msmarco-v1-doc.20221004.252b5e.tar.gz", entry.filename);
    assertEquals("f66020a923df6430007bd5718e53de86", entry.md5);
    assertEquals(3213835, entry.documents);
    assertEquals(13736982339L, entry.size);
  }

  @Test
  public void testTotalCount() {
    assertEquals(109, PrebuiltInvertedIndex.entries().size());
  }

  @Test
  public void testTotalCountForMsMarcoV1() {
    int v1Count = 0;
    for (PrebuiltInvertedIndex.Entry entry : PrebuiltInvertedIndex.entries()) {
      if (entry != null && entry.name != null && entry.name.contains("v1")) {
        v1Count++;
      }
    }
    assertEquals(73, v1Count);
  }

  @Test
  public void testTotalCountForMsMarcoV2() {
    int v2Count = 0;
    for (PrebuiltInvertedIndex.Entry entry : PrebuiltInvertedIndex.entries()) {
      if (entry != null && entry.name != null && entry.name.contains("v2") && !entry.name.contains("v2.1")) {
        v2Count++;
      }
    }
    assertEquals(17, v2Count);
  }

  @Test
  public void testTotalCountForMsMarcoV2_1() {
    int v2_1Count = 0;
    for (PrebuiltInvertedIndex.Entry entry : PrebuiltInvertedIndex.entries()) {
      if (entry != null && entry.name != null && entry.name.contains("v2.1")) {
        v2_1Count++;
      }
    }
    assertEquals(6, v2_1Count);
  }

  @Test
  public void testTotalCountForBeir() {
    int beirCount = 0;
    for (PrebuiltInvertedIndex.Entry entry : PrebuiltInvertedIndex.entries()) {
      if (entry != null && entry.name != null && entry.name.startsWith("beir")) {
        beirCount++;
      }
    }
    assertEquals(58, beirCount);
  }

  @Test
  public void testTotalCountForBright() {
    int brightCount = 0;
    for (PrebuiltInvertedIndex.Entry entry : PrebuiltInvertedIndex.entries()) {
      if (entry != null && entry.name != null && entry.name.startsWith("bright")) {
        brightCount++;
      }
    }
    assertEquals(12, brightCount);
  }

  @Test
  public void testUrls() {
    for (PrebuiltInvertedIndex.Entry entry : PrebuiltInvertedIndex.entries()) {
      for (String url : entry.urls) {
        // check each url status code is 200
        try {
          final URL requestUrl = new URI(url).toURL();
          final HttpURLConnection con = (HttpURLConnection) requestUrl.openConnection();
          assertEquals(200, con.getResponseCode());
          con.disconnect();
        } catch (IOException e) {
          throw new RuntimeException("Error connecting to " + url, e);
        } catch (Exception e) {
          throw new RuntimeException("Malformed URL: " + url, e);
        }
      }
    }
  }

  @Test
  public void testParseEntriesFromMetadataFile() throws Exception {
    List<PrebuiltInvertedIndex.Entry> entries = PrebuiltIndex.parseEntriesFromJson(PrebuiltIndex.IndexType.INVERTED, PrebuiltInvertedIndex.Entry.class,
        "[{\"name\":\"TEST\",\"type\":\"inverted\"},{\"name\":\"SKIP\",\"type\":\"impact\"}]");

    assertEquals(1, entries.size());
    assertEquals("TEST", entries.get(0).name);
  }
}
