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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Cord19FullTextCollectionTest extends DocumentCollectionTest<Cord19FullTextCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/cord19/sample1");
    collection = new Cord19FullTextCollection(collectionPath);

    Path segment = Paths.get("src/test/resources/sample_docs/cord19/sample1/metadata.csv");

    segmentPaths.add(segment);
    segmentDocCounts.put(segment, 3);

    totalSegments = 1;
    totalDocs = 3;

    // Should use pmc_json_files (preferred).
    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "xqhn0vbp");
    doc1.put("contents_starts_with", "Airborne rhinovirus detection and effect of ultraviolet irradiation");
    doc1.put("contents_ends_with", "The pre-publication history for this paper can be accessed here:\n");
    doc1.put("contents_length", "22834");
    doc1.put("has_full_text", "true");
    doc1.put("metadata_length", "2416");
    doc1.put("raw_length", "48080");
    expected.put("xqhn0vbp", doc1);

    // No full text.
    HashMap<String, String> doc2 = new HashMap<>();
    doc2.put("id", "ipllfog3");
    doc2.put("contents_starts_with", "SARS, Mars and chocolate bars");
    doc2.put("contents_ends_with", "SARS, Mars and chocolate bars");
    doc2.put("contents_length", "29");
    doc2.put("has_full_text", "false");
    doc2.put("metadata_length", "428");
    doc2.put("raw_length", "489");
    expected.put("ipllfog3", doc2);

    // Should back off to pdf_json_files since there are no pmc_json_files.
    HashMap<String, String> doc3 = new HashMap<>();
    doc3.put("id", "a8cps3ko");
    // This particular entry doesn't have an abstract in the CSV
    doc3.put("contents_starts_with", "Beyond Picomolar Affinities:");
    doc3.put("contents_ends_with", "Copyright 2005 Americal Chemical Society. ");
    doc3.put("contents_length", "33583");
    doc3.put("has_full_text", "true");
    doc3.put("metadata_length", "694");
    doc3.put("raw_length", "94682");
    expected.put("a8cps3ko", doc3);
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    Cord19FullTextCollection.Document covidDoc = (Cord19FullTextCollection.Document) doc;

    assertEquals(expected.get("id"), covidDoc.id());
    assertTrue(covidDoc.contents().startsWith(expected.get("contents_starts_with")));
    assertTrue(covidDoc.contents().endsWith(expected.get("contents_ends_with")));
    assertEquals(Integer.parseInt(expected.get("contents_length")), covidDoc.contents().length());

    // Make sure raw() is a JSON containing proper fields, and check length.
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode jsonNode = mapper.readTree(covidDoc.raw());
      assertEquals(expected.get("id"), jsonNode.get("cord_uid").asText());
      assertEquals(expected.get("has_full_text"), jsonNode.get("has_full_text").asText());
      assertEquals(Integer.parseInt(expected.get("metadata_length")),
        jsonNode.get("csv_metadata").toString().length());
    } catch (Exception e) {
      assertTrue("Failed to parse raw JSON", false);
    }
    assertEquals(Integer.parseInt(expected.get("raw_length")), covidDoc.raw().length());
  }
}
