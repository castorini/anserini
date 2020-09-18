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

public class CommonCrawlWarcCollectionTest extends DocumentCollectionTest<CommonCrawlWarcCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/commoncrawl/warc/collection1");
    collection = new CommonCrawlWarcCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/commoncrawl/warc/collection1/segment1.warc.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 1);

    totalSegments = 1;
    totalDocs = 1;

    // Note special key "null" to handle special case.
    expected.put("<urn:uuid:b92fd779-f40f-4edc-8332-ffddbdbce74b>",
            Map.of("id", "<urn:uuid:b92fd779-f40f-4edc-8332-ffddbdbce74b>",
                    "date", "2020-03-01T04:50:41Z",
                    "url", "https://www.suncommunitynews.com/sports/eagles-claim-bowling-crown/",
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
    assertEquals(expected.get("date"), ((CommonCrawlWarcCollection.Document) doc).getDate());
    assertEquals(expected.get("url"), ((CommonCrawlWarcCollection.Document) doc).getURL());
  }
}
