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
import java.util.HashMap;
import java.util.Map;

public class Cord19ParagraphCollectionTest extends DocumentCollectionTest<Cord19ParagraphCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/cord19/sample1");
    collection = new Cord19ParagraphCollection(collectionPath);

    Path segment = Paths.get("src/test/resources/sample_docs/cord19/sample1/metadata.csv");

    segmentPaths.add(segment);
    segmentDocCounts.put(segment, 76);

    totalSegments = 1;
    totalDocs = 76;

    // Should use pmc_json_files (preferred).
    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "xqhn0vbp");
    expected.put("xqhn0vbp", doc1);

    // has paragraphs 1 ... 41
    for (int i = 1; i < 42; i++) {
      String id = String.format("xqhn0vbp.%05d", i);
      HashMap<String, String> doc = new HashMap<>();
      doc.put("id", id);
      expected.put(id, doc);
    }

    // No full text.
    HashMap<String, String> doc2 = new HashMap<>();
    doc2.put("id", "ipllfog3");
    expected.put("ipllfog3", doc2);

    // Should back off to pdf_json_files since there are no pmc_json_files.
    HashMap<String, String> doc3 = new HashMap<>();
    doc3.put("id", "a8cps3ko");
    expected.put("a8cps3ko", doc3);

    // has paragraphs 1 ... 32
    for (int i = 1; i < 33; i++) {
      String id = String.format("a8cps3ko.%05d", i);
      HashMap<String, String> doc = new HashMap<>();
      doc.put("id", id);
      expected.put(id, doc);
    }
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    Cord19ParagraphCollection.Document covidDoc = (Cord19ParagraphCollection.Document) doc;

    assertEquals(expected.get("id"), covidDoc.id());
  }

  @Override
  public void checkRawtoContent() {
    // no content info stored in raw field in this collection, skip the test
    assertEquals("", "");
  }
}
