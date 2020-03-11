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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class WashingtonPostCollectionTest extends DocumentCollectionTest<WashingtonPostCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/washingtonpost/collection1/");
    collection = new WashingtonPostCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/washingtonpost/collection1/articles.jl");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 1);

    totalSegments = 1;
    totalDocs = 1;

    expected.put("5f992bbc-4b9f-11e2-a6a6-aabac85e8036",
        Map.of("id", "5f992bbc-4b9f-11e2-a6a6-aabac85e8036",
            "title", "Controlled exposure to light can ease jet lag’s effects before and after a trip",
            "author", "Mike",
            "article_url", "https://www.washingtonpost.com/national/" +
                "controlled-exposure-to-light-can-ease-jet-lags-effects-before-and-after-a-trip/" +
                "2012/12/24/5f992bbc-4b9f-11e2-a6a6-aabac85e8036_story.html",
            "published_date", "1356999181000"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("title"), ((WashingtonPostCollection.Document) doc).getTitle().get());
    assertEquals(expected.get("author"), ((WashingtonPostCollection.Document) doc).getAuthor().get());
    assertEquals(expected.get("article_url"), ((WashingtonPostCollection.Document) doc).getArticleUrl().get());
    assertEquals((long) Long.valueOf(expected.get("published_date")),
        ((WashingtonPostCollection.Document) doc).getPublishDate());
  }
}
