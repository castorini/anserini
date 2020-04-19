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

public class CovidFullTextCollectionTest extends DocumentCollectionTest<CovidFullTextCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/covid/sample1");
    collection = new CovidFullTextCollection(collectionPath);

    Path segment = Paths.get("src/test/resources/sample_docs/covid/sample1/metadata.csv");

    segmentPaths.add(segment);
    segmentDocCounts.put(segment, 2);

    totalSegments = 1;
    totalDocs = 2;

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "xqhn0vbp");
    doc1.put("contents_starts_with", "Airborne rhinovirus detection and effect of ultraviolet irradiation");
    doc1.put("contents_ends_with", "The pre-publication history for this paper can be accessed here:\n");
    doc1.put("contents_length", "22834");
    doc1.put("raw_length", "80042");
    expected.put("xqhn0vbp", doc1);

    HashMap<String, String> doc2 = new HashMap<>();
    doc2.put("id", "28wrp74k");
    doc2.put("contents_starts_with", "SARS and Population Health Technology");
    doc2.put("contents_ends_with", "The need for critical evaluation of all of these technologies is stressed.");
    doc2.put("contents_length", "1264");
    doc2.put("raw_length", "1711");
    expected.put("28wrp74k", doc2);
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    CovidFullTextCollection.Document covidDoc = (CovidFullTextCollection.Document) doc;

    assertEquals(expected.get("id"), covidDoc.id());
    assertTrue(covidDoc.contents().startsWith(expected.get("contents_starts_with")));
    assertTrue(covidDoc.contents().endsWith(expected.get("contents_ends_with")));
    assertEquals(Integer.parseInt(expected.get("contents_length")), covidDoc.contents().length());

    // Make sure raw() is a JSON, and check length.
    assertTrue(covidDoc.raw().startsWith("{"));
    assertTrue(covidDoc.raw().endsWith("}"));
    assertEquals(Integer.parseInt(expected.get("raw_length")), covidDoc.raw().length());
  }
}
