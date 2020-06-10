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

    Path segment1 = Paths.get("src/test/resources/sample_docs/commoncrawl/warc/collection1/segmant1.warc.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 2);

    totalSegments = 1;
    totalDocs = 2;

    // Note special key "null" to handle special case.
    expected.put("null",
            Map.of("id", "null", "raw",
                    "software:: StormCrawler 1.15 http://stormcrawler.net/\n" +
                    "description: News crawl for Common Crawl\n" +
                    "http-header-user-agent: CCBot/3.0 (http://commoncrawl.org/faq/; info@commoncrawl.org)\n" +
                    "http-header-from: info@commoncrawl.org\n" +
                    "robots: checked by crawler-commons 1.0 (https://github.com/crawler-commons/crawler-commons)\n" +
                    "format: WARC File Format 1.1\n" +
                    "conformsTo: https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/\n" +
                    "\n")
    );

    expected.put("<urn:uuid:b92fd779-f40f-4edc-8332-ffddbdbce74b>",
            Map.of("id", "<urn:uuid:b92fd779-f40f-4edc-8332-ffddbdbce74b>",
                    "raw", "<html>\nwhatever here will be included\n</html>\n"));

  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {

    if (doc.id().isEmpty()) {
      assertFalse(doc.indexable());
      assertEquals(expected.get("raw"), doc.raw());
    } else {
      assertTrue(doc.indexable());
      assertEquals(expected.get("id"), doc.id());
      assertEquals(JsoupStringTransform.SINGLETON.apply(expected.get("raw")), doc.contents());
      assertEquals(expected.get("raw"), doc.raw());
    }
  }
}
