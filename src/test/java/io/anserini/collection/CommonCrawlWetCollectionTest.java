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

public class CommonCrawlWetCollectionTest extends DocumentCollectionTest<CommonCrawlWetCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/commoncrawl/wet/collection1");
    collection = new CommonCrawlWetCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/commoncrawl/wet/collection1/segment1.warc.wet.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 2);

    totalSegments = 1;
    totalDocs = 2;

    // Note special key "null" to handle special case.
    expected.put("null",
            Map.of("id", "null", 
                    "date", "2020-02-18T17:21:42Z",
                    "raw",
                    "Software-Info: ia-web-commons.1.1.10-SNAPSHOT-20200126100433\n" +
                            "Extracted-Date: Tue, 18 Feb 2020 17:21:42 GMT\n" +
                            "robots: checked by crawler-commons 1.0 (https://github.com/crawler-commons/crawler-commons)\n" +
                            "description: News crawl for Common Crawl")
    );

    expected.put("<urn:uuid:81401709-eb1f-46bc-af26-c3535b35a644>",
            Map.of("id", "<urn:uuid:81401709-eb1f-46bc-af26-c3535b35a644>",
                    "date", "2019-12-26T23:38:34Z",
                    "url", "https://www.commoncrawl.test.com",
                    "raw", "hello"));

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
    assertEquals(expected.get("date"), ((CommonCrawlWetCollection.Document) doc).getDate());
    assertEquals(expected.get("url"), ((CommonCrawlWetCollection.Document) doc).getURL());
  }
}
