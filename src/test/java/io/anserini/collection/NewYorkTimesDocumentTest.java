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

public class NewYorkTimesDocumentTest extends DocumentTest {
  @Before
  public void setUp() throws Exception {
    super.setUp();

    String doc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE nitf SYSTEM \"http://www.nitf.org/IPTC/NITF/3.3/specification/dtd/nitf-3-3.dtd\">\n" +
            "<nitf version=\"-//IPTC//DTD NITF 3.3//EN\" change.time=\"19:30\" change.date=\"June 10, 2005\">\n" +
            "<head>\n" +
            "<title>Article Title</title>\n" +
            "<meta name=\"slug\" content=\"foo\"/>\n" +
            "<meta name=\"publication_day_of_month\" content=\"1\"/>\n" +
            "<meta name=\"publication_month\" content=\"2\"/>\n" +
            "<meta name=\"publication_year\" content=\"2007\"/>\n" +
            "<meta name=\"publication_day_of_week\" content=\"Thursday\"/>\n" +
            "<docdata>\n" +
            "<doc-id id-string=\"12345678\"/>\n" +
            "<doc.copyright year=\"2007\" holder=\"The New York Times\"/>\n" +
            "</docdata>\n" +
            "</head>\n" +
            "<body>\n" +
            "<body.head>\n" +
            "<hedline>\n" +
            "<hl1>Article Title</hl1>\n" +
            "</hedline>\n" +
            "<byline class=\"print_byline\">By Some One; Compiled by Another Person</byline>\n" +
            "<byline class=\"normalized_byline\">One, Some</byline>\n" +
            "<abstract>\n" +
            "<p>Article abstract.</p>\n" +
            "</abstract>" +
            "</body.head>\n" +
            "<body.content>" +
            "<block class=\"lead_paragraph\">" +
            "<p>First paragraph.</p>\n" +
            "</block>\n" +
            "<block class=\"full_text\">\n" +
            "<p>First paragraph.</p>\n" +
            "<p>Second paragraph.</p>\n" +
            "</block>\n" +
            "</body.content>\n" +
            "</body>\n" +
            "</nitf>";

    rawFiles.add(createFile(doc));

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "12345678");
    doc1.put("content", "Article Title\nArticle abstract.\nFirst paragraph.\nSecond paragraph.");
    doc1.put("headline", "Article Title");
    doc1.put("abstract", "Article abstract.");
    doc1.put("body", "First paragraph.\nSecond paragraph.");
    expected.add(doc1);
  }

  @Test
  public void test() throws IOException {
    NewYorkTimesCollection collection = new NewYorkTimesCollection();
    Iterator<NewYorkTimesCollection.Document> iter = collection.createFileSegment(rawFiles.get(0)).iterator();
    AtomicInteger cnt = new AtomicInteger();
    while (iter.hasNext()) {
      int i = cnt.getAndIncrement();
      NewYorkTimesCollection.Document parsed = iter.next();
      assertEquals(parsed.id(), expected.get(i).get("id"));
      // Tests for what to include in content: should be concatenation of headline, abstract, and body for now
      assertEquals(parsed.content(), expected.get(i).get("content"));
      assertEquals(parsed.getRawDocument().getHeadline(), expected.get(i).get("headline"));
      assertEquals(parsed.getRawDocument().getArticleAbstract(), expected.get(i).get("abstract"));
      assertEquals(parsed.getRawDocument().getBody(), expected.get(i).get("body"));
    }
  }

  // Tests if the iterator is behaving properly. If it is, we shouldn't have any issues running into
  // NoSuchElementExceptions.
  @Test
  public void testStreamIteration() {
    NewYorkTimesCollection collection = new NewYorkTimesCollection();
    try {
      FileSegment<NewYorkTimesCollection.Document> segment = collection.createFileSegment(rawFiles.get(0));
      Iterator<NewYorkTimesCollection.Document> iter = segment.iterator();
      AtomicInteger cnt = new AtomicInteger();
      iter.forEachRemaining(d -> {
        int i = cnt.getAndIncrement();
        assertEquals(d.id(), expected.get(i).get("id"));
        // Tests for what to include in content: should be concatenation of headline, abstract, and body for now
        assertEquals(d.content(), expected.get(i).get("content"));
        assertEquals(d.getRawDocument().getHeadline(), expected.get(i).get("headline"));
        assertEquals(d.getRawDocument().getArticleAbstract(), expected.get(i).get("abstract"));
        assertEquals(d.getRawDocument().getBody(), expected.get(i).get("body"));
      });
      assertEquals(1, cnt.get());
      assertEquals(false, segment.getErrorStatus());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
