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

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class TrecwebDocumentTest extends DocumentTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    String doc = "<DOC>\n" +
        "<DOCNO> WEB-0001 </DOCNO>\n" +
        "<DOCHDR>DOCHDR will NOT be \n" +
        " included</DOCHDR>\n" +
        "<html>Wh at ever here will be parsed \n" +
        " <br> asdf <div>\n" +
        "</html>\n" +
        "</DOC>\n";

    rawFiles.add(createFile(doc));

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "WEB-0001");
    // <DOCHDR> Will NOT be included
    doc1.put("content", "<html>Wh at ever here will be parsed\n" +
        "<br> asdf <div>\n" +
        "</html>");
    expected.add(doc1);
  }

  @Test
  public void test() throws Exception {
    TrecwebCollection collection = new TrecwebCollection();
    for (int i = 0; i < rawFiles.size(); i++) {
      BaseFileSegment<TrecwebCollection.Document> iter = collection.createFileSegment(rawFiles.get(i));
      while (iter.hasNext()) {
        TrecCollection.Document parsed = iter.next();
        assertEquals(parsed.id(), expected.get(i).get("id"));
        assertEquals(parsed.content(), expected.get(i).get("content"));
      }
    }
  }

  // Tests if the iterator is behaving properly. If it is, we shouldn't have any issues running into
  // NoSuchElementExceptions.
  @Test
  public void testStreamIteration() {
    TrecwebCollection collection = new TrecwebCollection();
    try {
      BaseFileSegment<TrecwebCollection.Document> iter = collection.createFileSegment(rawFiles.get(0));
      AtomicInteger cnt = new AtomicInteger();
      iter.forEachRemaining(d -> cnt.incrementAndGet());
      assertEquals(1, cnt.get());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
