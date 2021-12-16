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

public class NewYorkTimesCollectionTest extends DocumentCollectionTest<NewYorkTimesCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/nyt/collection1");
    collection = new NewYorkTimesCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/nyt/collection1/segment1.xml");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 1);

    totalSegments = 1;
    totalDocs = 1;

    expected.put("12345678",
        Map.of("id", "12345678",
            "content", "Article Title\nArticle abstract.\nFirst paragraph.\nSecond paragraph.",
            "headline", "Article Title",
            "abstract", "Article abstract.",
            "body", "First paragraph.\nSecond paragraph."));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    NewYorkTimesCollection.Document nyt = (NewYorkTimesCollection.Document) doc;

    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), nyt.id());
    assertEquals(expected.get("content"), nyt.contents());
    assertEquals(expected.get("content"), nyt.raw());
    assertEquals(expected.get("headline"), nyt.getRawDocument().getHeadline());
    assertEquals(expected.get("abstract"), nyt.getRawDocument().getArticleAbstract());
    assertEquals(expected.get("body"), nyt.getRawDocument().getBody());
  }
}
