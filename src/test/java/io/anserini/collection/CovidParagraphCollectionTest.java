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
    segmentDocCounts.put(segment, 76);

    totalSegments = 1;
    totalDocs = 76;

    // In the 2020/04/10 version, has_pdf_parse=TRUE, has_pmc_xml_parse=TRUE
    // Should use has_pmc_xml_parse (preferred).
    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "xqhn0vbp");
    expected.put("xqhn0vbp", doc1);

    // has paragraphs 1 ... 41
    for (int i=1; i<42; i++) {
      String id = String.format("xqhn0vbp.%05d", i);
      HashMap<String, String> doc = new HashMap<>();
      doc.put("id", id);
      expected.put(id, doc);
    }

    // In the 2020/04/10 version, has_pdf_parse=FALSE, has_pmc_xml_parse=FALSE
    // No full text.
    HashMap<String, String> doc2 = new HashMap<>();
    doc2.put("id", "28wrp74k");
    expected.put("28wrp74k", doc2);

    // In the 2020/04/10 version, has_pdf_parse=TRUE, has_pmc_xml_parse=FALSE
    // Should back off to pdf_parse.
    HashMap<String, String> doc3 = new HashMap<>();
    doc3.put("id", "a8cps3ko");
    expected.put("a8cps3ko", doc3);

    // has paragraphs 1 ... 32
    for (int i=1; i<33; i++) {
      String id = String.format("a8cps3ko.%05d", i);
      HashMap<String, String> doc = new HashMap<>();
      doc.put("id", id);
      expected.put(id, doc);
    }
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    CovidParagraphCollection.Document covidDoc = (CovidParagraphCollection.Document) doc;

    assertEquals(expected.get("id"), covidDoc.id());
  }
}
