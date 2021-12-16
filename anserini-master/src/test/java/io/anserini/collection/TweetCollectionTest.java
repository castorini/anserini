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

package io.anserini.collection;

import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class TweetCollectionTest extends DocumentCollectionTest<TweetCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/tweets/collection2");
    collection = new TweetCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/tweets/collection2/tweets.jsonl");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 2);

    totalSegments = 1;
    totalDocs = 2;

    expected.put("123456789",
        Map.of("id", "123456789",
            "content", "this is the tweet contents.",
            "screen_name", "foo",
            "timestamp_ms", "1517482567000"));

    expected.put("123456787",
        Map.of("id", "123456787",
            "content", "this is the tweet contents, iteration should not have stopped." ,
            "screen_name", "UserName",
            "timestamp_ms", "1362038400000"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("content"), doc.contents());
    assertEquals(expected.get("content"), doc.raw());
    assertEquals(expected.get("screen_name"), ((TweetCollection.Document) doc).getScreenName());
    assertEquals((long) Long.valueOf(expected.get("timestamp_ms")),
        ((TweetCollection.Document) doc).getTimestampMs().getAsLong());
  }
}
