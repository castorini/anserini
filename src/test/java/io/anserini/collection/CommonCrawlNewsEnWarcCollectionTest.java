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

public class CommonCrawlNewsEnWarcCollectionTest extends DocumentCollectionTest<CommonCrawlNewsEnWarcCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/commoncrawlnewsen/collection1");
    collection = new CommonCrawlNewsEnWarcCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/commoncrawlnewsen/collection1/segment1.warc.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 1);

    totalSegments = 1;
    totalDocs = 1;

    expected.put("CC-NEWS-12345678901234-56789-0",
            Map.of("id", "CC-NEWS-12345678901234-56789-0",
                    "date", "2017-01-01T11:26:44Z",
                    "url", "http://www.testcaseurl.com/ccnewsen",
                    "raw", "<html>\nwhatever here will be included\n</html>"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    if (doc.id()==null) {
      assertFalse(doc.indexable());
    } else {
      assertTrue(doc.indexable());
      assertEquals(expected.get("id"), doc.id());
      assertEquals(JsoupStringTransform.SINGLETON.apply(expected.get("raw")), doc.contents());
    }
    assertEquals(expected.get("raw"), doc.raw());
    assertEquals(expected.get("date"), ((CommonCrawlNewsEnWarcCollection.Document) doc).getDate());
    assertEquals(expected.get("url"), ((CommonCrawlNewsEnWarcCollection.Document) doc).getURL());
  }
}
