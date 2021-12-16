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

public class TrecwebCollectionTest extends DocumentCollectionTest<TrecwebCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/trecweb/collection1");
    collection = new TrecwebCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/trecweb/collection1/segment1.txt");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 2);

    totalSegments = 1;
    totalDocs = 2;

    // Note, <DOCHDR> Will NOT be included.
    expected.put("WEB-0001",
        Map.of("id", "WEB-0001",
            "raw", "<html>Wh at ever here will be parsed\n<br> asdf <div>\n</html>"));

    // Note, <DOCHDR> Will NOT be included.
    expected.put("WEB-0003",
      Map.of("id", "WEB-0003",
          "raw", "<html>Wh at ever here will be parsed\n<br> asdf <div>\n</html>"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(JsoupStringTransform.SINGLETON.apply(expected.get("raw")), doc.contents());
    assertEquals(expected.get("raw"), doc.raw());
  }
}
