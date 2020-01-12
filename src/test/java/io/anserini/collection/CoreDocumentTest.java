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

import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Before;
import org.junit.Test;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CoreDocumentTest extends DocumentTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    String doc =
      "{" +
      "  \"coreId\": \"coreDoc1\"," +
      "  \"doi\": null," +
      "  \"title\": \"this is the title 1\"," +
      "  \"abstract\": \"this is the abstract 1\"," +
      "  \"topics\": [\"Topic 1\", \"Other\"]," +
      "  \"year\": 2020," +
      "  \"field1\": \"doc1 field1 content\"," +
      "  \"field2\": \"doc1 field2 content\"" +
      "}\n" +
      "{ " +
      "  \"coreId\": \"coreDoc2\"," +
      "  \"doi\": \"doi2\"," +
      "  \"title\": \"this is the title 2\"," +
      "  \"abstract\": \"this is the abstract 2\"," +
      "  \"topics\": [\"Topic 2\", \"Other\"]," +
      "  \"year\": 2010," +
      "  \"field1\": \"doc2 field1 content\"," +
      "  \"field2\": \"doc2 field2 content\"" +
      "}";

    rawFiles.add(createTmpFile(doc));

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("coreId", "coreDoc1");
    doc1.put("doi", "null");
    doc1.put("title", "this is the title 1");
    doc1.put("abstract", "this is the abstract 1");
    doc1.put("topics", "Topic 1::Other");
    doc1.put("year", "2020");
    doc1.put("field1", "doc1 field1 content");
    doc1.put("field2", "doc1 field2 content");
    expected.add(doc1);
    HashMap<String, String> doc2 = new HashMap<>();
    doc2.put("coreId", "coreDoc2");
    doc2.put("doi", "doi2");
    doc2.put("title", "this is the title 2");
    doc2.put("abstract", "this is the abstract 2");
    doc2.put("topics", "Topic 2::Other");
    doc2.put("year", "2010");
    doc2.put("field1", "doc2 field1 content");
    doc2.put("field2", "doc2 field2 content");
    expected.add(doc2);
  }

  private Path createTmpFile(String doc) {
    Path tmpPath = null;
    try {
      tmpPath = createTempFile();
      OutputStream fout = Files.newOutputStream(tmpPath);
      BufferedOutputStream tmpOut = new BufferedOutputStream(fout);
      XZOutputStream out = new XZOutputStream(tmpOut, new LZMA2Options());
      StringInputStream in = new StringInputStream(doc);
      final byte[] buffer = new byte[2048];
      int n = 0;
      while (-1 != (n = in.read(buffer))) {
        out.write(buffer, 0, n);
      }
      out.finish();
      out.close();
    } catch (IOException e) {}
    return tmpPath;
  }

  @Test
  public void test() throws IOException {
    CoreCollection collection = new CoreCollection();


    for (int i = 0; i < rawFiles.size(); i++) {
      Iterator<CoreCollection.Document> iter =
              collection.createFileSegment(rawFiles.get(i)).iterator();
      int j = 0;
      while (iter.hasNext()) {
        CoreCollection.Document parsed = iter.next();
        assertEquals(parsed.id(), ((expected.get(j).get("doi").equals("null")) ? expected.get(j).get("coreId") :
                "doi:"+expected.get(j).get("doi")));
        assertEquals(parsed.content(), expected.get(j).get("title") + "\n" + expected.get(j).get("abstract"));
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
    CoreCollection collection = new CoreCollection();
    try {
      Iterator<CoreCollection.Document> iter =
              collection.createFileSegment(rawFiles.get(0)).iterator();
      AtomicInteger cnt = new AtomicInteger();
      iter.forEachRemaining(d -> cnt.incrementAndGet());
      assertEquals(2, cnt.get());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

