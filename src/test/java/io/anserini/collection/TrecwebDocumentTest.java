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
        "</DOC>\n" +

        // Test for incomplete tags, should skip this document and continue
        "<DOC>\n" +
        "WEB-0002 </DOCNO>\n" +
        "<DOCHDR>DOCHDR will NOT be \n" +
        " included</DOCHDR>\n" +
        "<html>Wh at ever here will be parsed \n" +
        " <br> asdf <div>\n" +
        "</html>\n" +
        "</DOC>\n" +

        // This document should be parsed
        "<DOC>\n" +
        "<DOCNO> WEB-0003 </DOCNO>\n" +
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
    doc1.put("indexable", "true");
    expected.add(doc1);

    HashMap<String, String> doc3 = new HashMap<>();
    doc3.put("id", "WEB-0003");
    // <DOCHDR> Will NOT be included
    doc3.put("content", "<html>Wh at ever here will be parsed\n" +
            "<br> asdf <div>\n" +
            "</html>");
    doc3.put("indexable", "true");
    expected.add(doc3);
  }

  @Test
  public void test() throws Exception {
    TrecwebCollection collection = new TrecwebCollection();
    FileSegment<TrecwebCollection.Document> segment = collection.createFileSegment(rawFiles.get(0));
    Iterator<TrecwebCollection.Document> iter = segment.iterator();
    AtomicInteger cnt = new AtomicInteger();
    while (iter.hasNext()){
      TrecwebCollection.Document parsed = iter.next();
      int i = cnt.getAndIncrement();
      assertEquals(parsed.id(), expected.get(i).get("id"));
      assertEquals(parsed.content(), expected.get(i).get("content"));
    }
    assertEquals(2, cnt.get());
    assertEquals(1, segment.getSkippedCount());
    assertEquals(false, segment.getErrorStatus());
  }

  // Tests if the iterator is behaving properly. If it is, we shouldn't have any issues running into
  // NoSuchElementExceptions.
  @Test
  public void testStreamIteration() {
    TrecwebCollection collection = new TrecwebCollection();
    try {
      FileSegment<TrecwebCollection.Document> segment = collection.createFileSegment(rawFiles.get(0));
      Iterator<TrecwebCollection.Document> iter = segment.iterator();
      AtomicInteger cnt = new AtomicInteger();
      iter.forEachRemaining(d -> {
        int i = cnt.getAndIncrement();
        assertEquals(d.id(), expected.get(i).get("id"));
        assertEquals(d.content(), expected.get(i).get("content"));
        assertEquals(String.valueOf(d.indexable()), expected.get(i).get("indexable"));
      });
      assertEquals(2, cnt.get());
      assertEquals(1, segment.getSkippedCount());
      assertEquals(false, segment.getErrorStatus());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
