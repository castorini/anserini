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

public class WashingtonPostDocumentTest extends DocumentTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    String doc = "{\"id\": \"5f992bbc-4b9f-11e2-a6a6-aabac85e8036\", " +

                "\"article_url\": " +
                "\"https://www.washingtonpost.com/national/controlled-exposure-to" +
                "-light-can-ease-jet-lags-effects-before-and-after-a-trip/2012/12/24/" +
                "5f992bbc-4b9f-11e2-a6a6-aabac85e8036_story.html\", " +

                "\"title\": " +
                "\"Controlled exposure to light can ease jet lag’s effects before and after a trip\", " +

                "\"author\": \"Mike\", " +

                "\"published_date\": 1356999181000, " +

                "\"contents\": " +
                    "[{\"content\": \"National\", \"mime\": \"text/plain\", \"type\": \"kicker\"}, " +
                    "{\"content\": \"Controlled exposure to light can ease jet lag’s effects before and after a trip\", " +
                        "\"mime\": \"text/plain\", \"type\": \"title\"}, " +
                    "{\"content\": \"Using light to help reset your body clock\", \"subtype\": \"subhead\", \"type\": \"sanitized_html\", \"mime\": \"text/plain\"}, " +
                    "{\"content\": \"When traveling east:\", \"subtype\": \"paragraph\", \"type\": \"sanitized_html\", \"mime\": \"text/plain\"}, " +
                    "{\"content\": \"A few days before you leave, start exposing yourself to bright light in the morning.\", " +
                        "\"subtype\": \"paragraph\", \"type\": \"sanitized_html\", \"mime\": \"text/plain\"}, " +
                    "{\"content\": \"When traveling west:\", \"subtype\": \"paragraph\", \"type\": \"sanitized_html\", \"mime\": \"text/plain\"}, " +
                    "{\"content\": \"When you arrive, expose yourself to light during the evening hours.\", \"subtype\": \"paragraph\", \"type\": \"tweet\", \"mime\": \"text/plain\"}], " +

                "\"type\": \"article\", \"source\": \"The Washington Post\"}";

    rawFiles.add(createFile(doc));

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "5f992bbc-4b9f-11e2-a6a6-aabac85e8036");
    doc1.put("title", "Controlled exposure to light can ease jet lag’s effects before and after a trip");
    doc1.put("author", "Mike");
    doc1.put("article_url", "https://www.washingtonpost.com/national/controlled-exposure-to-light-can-ease-jet-lags-effects-before-and-after-a-trip/2012/12/24/5f992bbc-4b9f-11e2-a6a6-aabac85e8036_story.html");
    // Only "sanitized_html" and "tweet" of <type> subtag in <content> tag will be included
    doc1.put("content", doc);
    doc1.put("published_date", "1356999181000");

    expected.add(doc1);
  }

  @Test
  public void test() throws Exception {
    WashingtonPostCollection collection = new WashingtonPostCollection();
    for (int i = 0; i < rawFiles.size(); i++) {
      Iterator<WashingtonPostCollection.Document> iter = collection.createFileSegment(rawFiles.get(i)).iterator();
      while (iter.hasNext()) {
        WashingtonPostCollection.Document parsed = iter.next();
        assertEquals(parsed.id(), expected.get(i).get("id"));
        assertEquals(parsed.getArticleUrl().get(), expected.get(i).get("article_url"));
        assertEquals(parsed.getTitle().get(), expected.get(i).get("title"));
        assertEquals(parsed.getAuthor().get(), expected.get(i).get("author"));
        assertEquals(parsed.getPublishDate(), Long.parseLong(expected.get(i).get("published_date")));
        assertEquals(parsed.getContent(), expected.get(i).get("content"));
      }
    }
  }

  // Tests if the iterator is behaving properly. If it is, we shouldn't have any issues running into
  // NoSuchElementExceptions.
  @Test
  public void testStreamIteration() {
    WashingtonPostCollection collection = new WashingtonPostCollection();
    try {
      Iterator<WashingtonPostCollection.Document> iter = collection.createFileSegment(rawFiles.get(0)).iterator();
      AtomicInteger cnt = new AtomicInteger();
      iter.forEachRemaining(d -> cnt.incrementAndGet());
      assertEquals(1, cnt.get());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
