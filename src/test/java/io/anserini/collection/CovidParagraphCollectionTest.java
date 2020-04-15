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
import java.util.HashMap;
import java.util.Map;

public class CovidParagraphCollectionTest extends DocumentCollectionTest<CovidParagraphCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/covid/sample1");
    collection = new CovidParagraphCollection(collectionPath);

    Path segment = Paths.get("src/test/resources/sample_docs/covid/sample1/metadata.csv");

    segmentPaths.add(segment);
    segmentDocCounts.put(segment, 34);

    totalSegments = 1;
    totalDocs = 34;

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "xqhn0vbp");
    expected.put("xqhn0vbp", doc1);

    for (int i=1; i<34; i++) {
      String id = String.format("xqhn0vbp.%05d", i);
      HashMap<String, String> doc = new HashMap<>();
      doc.put("id", id);
      expected.put(id, doc);
    }
    //"xqhn0vbp.00001"

  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    CovidParagraphCollection.Document covidDoc = (CovidParagraphCollection.Document) doc;

    assertEquals(expected.get("id"), covidDoc.id());
  }
}
