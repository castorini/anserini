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


public class TrecDocumentTest extends DocumentTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    String doc = "<DOC>\n" +
        "<DOCNO> AP-0001 </DOCNO>\n" +
        "<FILEID>field id test and should NOT be included</FILEID>\n" +
        "<FIRST>first test and should NOT be included</FIRST>\n" +
        "<SECOND>second test and should NOT be included</SECOND>\n" +
        "<HEAD>This is head and should NOT be included</HEAD>\n" +
        "<HEADLINE>This is headline and should be included</HEADLINE>\n" +
        "<DATELINE>AP</DATELINE>\n" +
        "<TEXT>\n" +
        "   Hopefully we \n" +
        "get this\n" +
        " right\n" +
        "</TEXT>\n" +
        "</DOC>\n";

    rawFiles.add(createFile(doc));

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "AP-0001");
    // ONLY "<TEXT>", "<HEADLINE>", "<TITLE>", "<HL>", "<HEAD>",
    // "<TTL>", "<DD>", "<DATE>", "<LP>", "<LEADPARA>" will be included
    doc1.put("content", "<HEAD>This is head and should NOT be included</HEAD>\n" +
        "<HEADLINE>This is headline and should be included</HEADLINE>\n" +
        "<TEXT>\n" +
        "Hopefully we\n" +
        "get this\n" +
        "right\n" +
        "</TEXT>");
    expected.add(doc1);
  }

  @Test
  public void test() throws Exception {
    TrecCollection collection = new TrecCollection();
    for (int i = 0; i < rawFiles.size(); i++) {
      BaseFileSegment<TrecCollection.Document> iter = collection.createFileSegment(rawFiles.get(i));
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
    TrecCollection collection = new TrecCollection();
    try {
      BaseFileSegment<TrecCollection.Document> iter = collection.createFileSegment(rawFiles.get(0));
      AtomicInteger cnt = new AtomicInteger();
      iter.forEachRemaining(d -> cnt.incrementAndGet());
      assertEquals(1, cnt.get());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
