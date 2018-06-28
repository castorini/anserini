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

package io.anserini.document;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;


public class TweetDocumentTest extends DocumentTest<TweetDocument> {

  @Before
  public void setUP() throws Exception {
    super.setUp();
    dType = new TweetDocument();

    rawDocs.add("{\"id\":" + 123456789 + ",\"text\":\"" + "this is the tweet contents."
        + "\",\"user\":{\"screen_name\":\"foo\",\"name\":\"foo\"," +
        "\"profile_image_url\":\"foo\",\"followers_count\":1,\"friends_count\":1," +
        "\"statuses_count\":1},\"created_at\":\"Fri Feb 01 10:56:07 +0000 2018\"}"
    );

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "123456789");
    doc1.put("content", "this is the tweet contents.");
    doc1.put("timestamp_ms", "1517482567000");

    expected.add(doc1);
  }

  @Test
  public void test() throws Exception {
    for (int i = 0; i < rawDocs.size(); i++) {
      TweetDocument parsed = parse(rawDocs.get(i));
      assertEquals(parsed.id(), expected.get(i).get("id"));
      assertEquals(parsed.content(), expected.get(i).get("content"));
      assertEquals(parsed.getTimestampMs(), Long.parseLong(expected.get(i).get("timestamp_ms")));
    }
  }
}
