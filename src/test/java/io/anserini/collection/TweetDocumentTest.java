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


public class TweetDocumentTest extends DocumentTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    String doc = "{\"id_str\":\"123456789\",\"text\":\"" + "this is the tweet contents."
        + "\",\"user\":{\"screen_name\":\"foo\",\"name\":\"foo\"," +
        "\"profile_image_url\":\"foo\",\"followers_count\":1,\"friends_count\":1," +
        "\"statuses_count\":1},\"created_at\":\"Fri Feb 01 10:56:07 +0000 2018\"}";

    rawFiles.add(createFile(doc));

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "123456789");
    doc1.put("content", "this is the tweet contents.");
    doc1.put("timestamp_ms", "1517482567000");

    expected.add(doc1);
  }

  @Test
  public void test() throws Exception {
    TweetCollection collection = new TweetCollection();
    for (int i = 0; i < rawFiles.size(); i++) {
      BaseFileSegment<TweetCollection.Document> iter = collection.createFileSegment(rawFiles.get(i));
      while (iter.hasNext()) {
        TweetCollection.Document parsed = iter.next();
        assertEquals(parsed.id(), expected.get(i).get("id"));
        assertEquals(parsed.content(), expected.get(i).get("content"));
        assert(parsed.getTimestampMs().isPresent());
        assertEquals(parsed.getTimestampMs().getAsLong(), Long.parseLong(expected.get(i).get("timestamp_ms")));
      }
    }
  }

  // Tests if the iterator is behaving properly. If it is, we shouldn't have any issues running into
  // NoSuchElementExceptions.
  @Test
  public void testStreamIteration() {
    TweetCollection collection = new TweetCollection();
    try {
      BaseFileSegment<TweetCollection.Document> iter = collection.createFileSegment(rawFiles.get(0));
      AtomicInteger cnt = new AtomicInteger();
      iter.forEachRemaining(d -> cnt.incrementAndGet());
      assertEquals(1, cnt.get());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
