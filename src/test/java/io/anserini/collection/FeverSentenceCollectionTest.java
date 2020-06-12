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

public class FeverSentenceCollectionTest extends DocumentCollectionTest<FeverSentenceCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();
    collectionPath = Paths.get("src/test/resources/sample_docs/fever/collection1");
    collection = new FeverSentenceCollection(collectionPath);
    Path segment = Paths.get("src/test/resources/sample_docs/fever/collection1/segment1.jsonl");

    segmentPaths.add(segment);
    segmentDocCounts.put(segment, 14);

    totalSegments = 1;
    totalDocs = 14;

    // empty document, should be ingested but not indexed
    expected.put("", Map.of("id", "", "content", "", "raw", ""));

    // document with empty content, should be ingested but not indexed
    expected.put("Domain_Range_Ratio_-LRB-DRR-RRB-", Map.of(
            "id", "Domain_Range_Ratio_-LRB-DRR-RRB-",
            "content", "",
            "raw", ""
    ));

    // regular document 1, should be ingested and indexed
    expected.put("Shohei_Otani_0", Map.of(
            "id", "Shohei_Otani_0",
            "content", "is a Japanese baseball pitcher , outfielder , and designated hitter who plays for the " +
                    "Hokkaido Nippon-Ham Fighters of Nippon Professional Baseball 's -LRB- NPB -RRB- Pacific League .",
            "raw", "is a Japanese baseball pitcher , outfielder , and designated hitter who plays for the " +
                    "Hokkaido Nippon-Ham Fighters of Nippon Professional Baseball 's -LRB- NPB -RRB- Pacific League ."
    ));
    expected.put("Shohei_Otani_1", Map.of(
            "id", "Shohei_Otani_1",
            "content", "He was the first pick of the Fighters in the 2012 draft .",
            "raw", "He was the first pick of the Fighters in the 2012 draft ."
    ));
    expected.put("Shohei_Otani_2", Map.of(
            "id", "Shohei_Otani_2",
            "content", "He officially recorded the fastest pitch by a Japanese pitcher and in NPB history at " +
                    "165 km/h .",
            "raw", "He officially recorded the fastest pitch by a Japanese pitcher and in NPB history at 165 " +
                    "km/h ."
    ));
    expected.put("Shohei_Otani_3", Map.of("id", "Shohei_Otani_3", "content", "", "raw", ""));

    // regular document 2, should be ingested and indexed
    expected.put("Kelvin,_North_Dakota_0", Map.of(
            "id", "Kelvin,_North_Dakota_0",
            "content", "Kelvin is an unincorporated community in Rolette County , in the U.S. state of North " +
                    "Dakota .",
            "raw", "Kelvin is an unincorporated community in Rolette County , in the U.S. state of North " +
                    "Dakota ."
    ));
    expected.put("Kelvin,_North_Dakota_1", Map.of("id", "Kelvin,_North_Dakota_1", "content", "", "raw", ""));

    // regular document 3 with unicode characters and empty lines, should be ingested and indexed
    expected.put("Styrkepr\u00f8ven_0", Map.of(
            "id", "Styrkepr\u00f8ven_0",
            "content", "Styrkepr\u00f8ven , also called Den Store Styrkepr\u00f8ven -LRB- The Great Trial of " +
                    "Strength -RRB- , is a 540 km long bicycle cyclosportive which starts in Trondheim and finishes " +
                    "in Oslo , Norway .",
            "raw", "Styrkepr\u00f8ven , also called Den Store Styrkepr\u00f8ven -LRB- The Great Trial of " +
                    "Strength -RRB- , is a 540 km long bicycle cyclosportive which starts in Trondheim and finishes " +
                    "in Oslo , Norway ."
    ));
    expected.put("Styrkepr\u00f8ven_1", Map.of(
            "id", "Styrkepr\u00f8ven_1",
            "content", "It was first held in 1967 , initiaded by Erik Gjems-Onstad and has taken place since " +
                    "then in late June every year .",
            "raw", "It was first held in 1967 , initiaded by Erik Gjems-Onstad and has taken place since " +
                    "then in late June every year ."
    ));
    expected.put("Styrkepr\u00f8ven_2", Map.of("id", "Styrkepr\u00f8ven_2", "content", "", "raw", ""));
    expected.put("Styrkepr\u00f8ven_3", Map.of("id", "Styrkepr\u00f8ven_3", "content", "", "raw", ""));
    expected.put("Styrkepr\u00f8ven_4", Map.of(
            "id", "Styrkepr\u00f8ven_4",
            "content", "In 2012 the record time was 12.51,02 .",
            "raw", "In 2012 the record time was 12.51,02 ."
    ));
    expected.put("Styrkepr\u00f8ven_5", Map.of("id", "Styrkepr\u00f8ven_5", "content", "", "raw", ""));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    FeverSentenceCollection.Document feverDoc = (FeverSentenceCollection.Document) doc;

    assertTrue(feverDoc.indexable());
    if (expected != null) {
      assertEquals(expected.get("id"), feverDoc.id());
      assertEquals(expected.get("content"), feverDoc.contents());
      assertEquals(expected.get("raw"), feverDoc.raw());
    }
  }
}
