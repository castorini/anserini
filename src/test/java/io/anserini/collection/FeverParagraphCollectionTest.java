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

public class FeverParagraphCollectionTest extends DocumentCollectionTest<FeverParagraphCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();
    collectionPath = Paths.get("src/test/resources/sample_docs/fever/collection1");
    collection = new FeverParagraphCollection(collectionPath);
    Path segment = Paths.get("src/test/resources/sample_docs/fever/collection1/segment1.jsonl");

    segmentPaths.add(segment);
    segmentDocCounts.put(segment, 5);

    totalSegments = 1;
    totalDocs = 5;

    // empty document, should be ingested but not indexed
    expected.put("", Map.of("id", ""));

    // document with empty content, should be ingested but not indexed
    expected.put("Domain_Range_Ratio_-LRB-DRR-RRB-", Map.of("id", "Domain_Range_Ratio_-LRB-DRR-RRB-"));

    // regular document 1, should be ingested and indexed
    expected.put("Kelvin,_North_Dakota", Map.of(
            "id", "Kelvin,_North_Dakota",
            "content_starts_with", "Kelvin is an unincorporated community in Rolette County , ",
            "content_ends_with", " , in the U.S. state of North Dakota . ",
            "raw_starts_with", "0\tKelvin is an unincorporated community in Rolette County , ",
            "raw_ends_with", "\tU.S. state\tU.S. state\tNorth Dakota\tNorth Dakota\n1\t"
    ));

    // regular document 2 with misplaced newline, should be ingested and indexed
    expected.put("Cumberland_Bandits", Map.of(
            "id", "Cumberland_Bandits",
            "content_starts_with", "The Cumberland Bandits are a Canadian Junior ice hockey team",
            "content_ends_with", "They play in the National Capital Junior Hockey League . ",
            "raw_starts_with", "0\tThe Cumberland Bandits are a Canadian Junior ice hockey team",
            "raw_ends_with", "\tNational Capital Junior Hockey League\tJunior\tjunior ice hockey\n2\t"
    ));

    // regular document 3 with unicode characters and empty lines, should be ingested and indexed
    expected.put("Styrkepr\u00f8ven", Map.of(
            "id", "Styrkepr\u00f8ven",
            "content_starts_with", "Styrkepr\u00f8ven , also called Den Store Styrkepr\u00f8ven",
            "content_ends_with", "in late June every year .   In 2012 the record time was 12.51,02 . ",
            "raw_starts_with", "0\tStyrkepr\u00f8ven , also called Den Store Styrkepr\u00f8ven",
            "raw_ends_with", "\n2\t\n3\t\n4\tIn 2012 the record time was 12.51,02 .\n5\t"
    ));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    FeverParagraphCollection.Document feverDoc = (FeverParagraphCollection.Document) doc;

    assertTrue(feverDoc.indexable());
    if (expected != null) {
      assertEquals(expected.get("id"), feverDoc.id());
      if (expected.containsKey("contents_starts_with")) {
        assertTrue(feverDoc.contents().startsWith(expected.get("contents_starts_with")));
      }
      if (expected.containsKey("contents_ends_with")) {
        assertTrue(feverDoc.contents().endsWith(expected.get("contents_ends_with")));
      }
      if (expected.containsKey("raw_starts_with")) {
        assertTrue(feverDoc.raw().startsWith(expected.get("raw_starts_with")));
      }
      if (expected.containsKey("raw_ends_with")) {
        assertTrue(feverDoc.raw().endsWith(expected.get("raw_ends_with")));
      }
    }
  }
}
