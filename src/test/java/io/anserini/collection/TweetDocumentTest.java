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

public class TweetDocumentTest extends DocumentTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    String doc = "{\"id_str\":\"123456789\",\"text\":\"" + "this is the tweet contents."
        + "\",\"user\":{\"screen_name\":\"foo\",\"name\":\"foo\"," +
        "\"profile_image_url\":\"foo\",\"followers_count\":1,\"friends_count\":1," +
        "\"statuses_count\":1},\"created_at\":\"Fri Feb 01 10:56:07 +0000 2018\"}\n" +

        // testing skip behaviour - should skip deleted tweet
        "{\"delete\":{\"status\":{\"id\":123456788," +
        "\"user_id\":123456788,\"id_str\":\"123456788\",\"user_id_str\":\"123456788\"}}}\n" +

        // and continue iteration to parse this tweet
        "{\"created_at\":\"Thu Feb 28 08:00:00 +0000 2013\"," +
        "\"id\":123456787,\"id_str\":\"123456787\"," +
        "\"text\":\"this is the tweet contents, iteration should not have stopped.\"," +
        "\"source\":\"web\",\"truncated\":false,\"user\":{\"id\":123456787," +
        "\"id_str\":\"123456787\",\"name\":\"User Name\"," +
        "\"screen_name\":\"UserName\", \"followers_count\":0,\"friends_count\":0," +
        "\"statuses_count\":0,\"created_at\":\"Thu Feb 21 13:59:38 +0000 2013\"}," +
        "\"favorited\":false,\"retweeted\":false,\"filter_level\":\"medium\"}";

    rawFiles.add(createFile(doc));

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "123456789");
    doc1.put("content", "this is the tweet contents.");
    doc1.put("screen_name", "foo");
    doc1.put("timestamp_ms", "1517482567000");

    HashMap<String, String> doc3 = new HashMap<>();
    doc3.put("id", "123456787");
    doc3.put("content", "this is the tweet contents, iteration should not have stopped.");
    doc3.put("screen_name", "UserName");
    doc3.put("timestamp_ms", "1362038400000");

    expected.add(doc1);
    expected.add(doc3);
  }

  @Test
  public void test() throws Exception {
    TweetCollection collection = new TweetCollection();
    FileSegment<TweetCollection.Document> segment = collection.createFileSegment(rawFiles.get(0));
    Iterator<TweetCollection.Document> iter = segment.iterator();
    AtomicInteger cnt = new AtomicInteger();
    while (iter.hasNext()) {
      int i = cnt.getAndIncrement();
      TweetCollection.Document parsed = iter.next();
      assertEquals(parsed.id(), expected.get(i).get("id"));
      assertEquals(parsed.content(), expected.get(i).get("content"));
      assertEquals(parsed.getScreenName(), expected.get(i).get("screen_name"));
      assert(parsed.getTimestampMs().isPresent());
      assertEquals(parsed.getTimestampMs().getAsLong(), Long.parseLong(expected.get(i).get("timestamp_ms")));
    }
    assertEquals(2, cnt.get());
    assertEquals(1, segment.getSkippedCount());
    assertEquals(false, segment.getErrorStatus());
  }

  // Tests if the iterator is behaving properly. If it is, we shouldn't have any issues running into
  // NoSuchElementExceptions.
  @Test
  public void testStreamIteration() {
    TweetCollection collection = new TweetCollection();
    try {
      FileSegment<TweetCollection.Document> segment = collection.createFileSegment(rawFiles.get(0));
      Iterator<TweetCollection.Document> iter = segment.iterator();
      AtomicInteger cnt = new AtomicInteger();
      iter.forEachRemaining(d -> {
        cnt.incrementAndGet();
      });
      assertEquals(2, cnt.get());
      assertEquals(1, segment.getSkippedCount());
      assertEquals(false, segment.getErrorStatus());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
