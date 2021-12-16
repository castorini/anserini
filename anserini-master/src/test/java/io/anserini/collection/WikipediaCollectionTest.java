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

public class WikipediaCollectionTest extends DocumentCollectionTest<WikipediaCollection.Document> {
  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/wikipedia/collection1/");
    collection = new WikipediaCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/wikipedia/collection1/articles.xml.bz2");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 1);

    totalSegments = 1;
    totalDocs = 1;

    expected.put("Wiktionary:Welcome, newcomers",
        Map.of("id", "Wiktionary:Welcome, newcomers",
            "content", "Wiktionary:Welcome, newcomers.\nthis is the   real content"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("content"), doc.contents());
    assertEquals(expected.get("content"), doc.raw());
  }
}
