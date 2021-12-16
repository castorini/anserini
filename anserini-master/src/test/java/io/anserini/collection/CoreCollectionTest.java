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

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreCollectionTest extends DocumentCollectionTest<CoreCollection.Document> {
  List<Map<String, JsonNode>> expectedJsonFields;

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/core/");
    collection = new CoreCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/core/segment1.json.xz");
    Path segment2 = Paths.get("src/test/resources/sample_docs/core/segment2.json");

    segmentPaths.add(segment1);
    segmentPaths.add(segment2);
    segmentDocCounts.put(segment1, 2);
    segmentDocCounts.put(segment2, 1);

    totalSegments = 2;
    totalDocs = 3;

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "coreDoc1");
    doc1.put("title", "this is the title 1");
    doc1.put("abstract", "this is the abstract 1");
    expected.put("coreDoc1", doc1);

    HashMap<String, String> doc2 = new HashMap<>();
    doc2.put("id", "doi2");
    doc2.put("title", "this is the title 2");
    doc2.put("abstract", "this is the abstract 2");
    expected.put("doi2", doc2);

    HashMap<String, String> doc3 = new HashMap<>();
    doc3.put("id", "fullCoreDoc");
    doc3.put("title", "Full CORE doc");
    doc3.put("abstract", "");
    doc3.put("doi", "");
    doc3.put("oai", "");
    doc3.put("identifiers", "");
    doc3.put("authors", "");
    doc3.put("enrichments", "");
    doc3.put("contributors", "");
    doc3.put("datePublished", "");
    doc3.put("abstract", "");
    doc3.put("downloadUrl", "");
    doc3.put("fullTextIdentifier", "");
    doc3.put("pdfHashValue", "");
    doc3.put("publisher", "");
    doc3.put("rawRecordXml", "");
    doc3.put("journals", "");
    doc3.put("language", "");
    doc3.put("relations", "");
    doc3.put("year", "");
    doc3.put("topics", "");
    doc3.put("subjects", "");
    doc3.put("fullText", "");
    expected.put("fullCoreDoc", doc3);
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    CoreCollection.Document coreDoc = (CoreCollection.Document) doc;    
    for (Map.Entry<String, String> entry : expected.entrySet()) {
      String expectedKey = entry.getKey();
      String expectedValue = entry.getValue();
      if (expectedKey.equals("id")) {
        assertEquals(expectedValue, coreDoc.id());
      } else if (expectedKey.equals("contents")) {
        assertEquals(expected.get("title") + " " + expected.get("abstract"), coreDoc.contents());
        assertEquals(expected.get("title") + " " + expected.get("abstract"), coreDoc.raw());
      } else {
        assert(coreDoc.jsonNode().has(expectedKey));
      }
    }
  }
}

