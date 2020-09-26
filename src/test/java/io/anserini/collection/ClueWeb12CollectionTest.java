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

public class ClueWeb12CollectionTest extends DocumentCollectionTest<ClueWeb12Collection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/cw12/collection1");
    collection = new ClueWeb12Collection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/cw12/collection1/segment1.warc.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 2);

    totalSegments = 1;
    totalDocs = 2;

    // Note special key "null" to handle special case.
    expected.put("null",
        Map.of("id", "null",
                "date", "2009-03-65T08:43:19-0800",
                "raw",
                "software: Nutch 1.0-dev (modified for clueweb09)\n" +
                "isPartOf: clueweb09-en\n" +
                "description: clueweb09 crawl with WARC output\n" +
                "format: WARC file version 0.18\n" +
                "conformsTo: http://www.archive.org/documents/WarcFileFormat-0.18.html"));

    expected.put("clueweb09-az0000-00-00000",
        Map.of("id", "clueweb09-az0000-00-00000",
                "date", "2009-03-65T08:43:19-0800",
                "url", "http://clueweb09.test.com/",
                "raw", "<html>\nwhatever here will be included\n</html>"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    if (doc.id() == null) {
      assertFalse(doc.indexable());
    } else {
      assertTrue(doc.indexable());
      assertEquals(expected.get("id"), doc.id());
      assertEquals(JsoupStringTransform.SINGLETON.apply(expected.get("raw")), doc.contents());
    }
    assertEquals(expected.get("raw"), doc.raw());
    assertEquals(expected.get("date"), ((ClueWeb12Collection.Document) doc).getDate());
    assertEquals(expected.get("url"), ((ClueWeb12Collection.Document) doc).getURL());
  }
}
