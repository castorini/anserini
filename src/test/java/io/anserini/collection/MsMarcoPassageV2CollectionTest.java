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

public class MsMarcoPassageV2CollectionTest extends DocumentCollectionTest<MsMarcoPassageV2Collection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/msmarco_passage_v2/");
    collection = new MsMarcoPassageV2Collection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/msmarco_passage_v2/docs.json");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 6);

    totalSegments = 1;
    totalDocs = 6;

    expected.put("msmarco_passage_00_0", Map.of("id", "msmarco_passage_00_0"));
    expected.put("msmarco_passage_00_172", Map.of("id", "msmarco_passage_00_172"));
    expected.put("msmarco_passage_00_587", Map.of("id", "msmarco_passage_00_587"));
    expected.put("msmarco_passage_00_997", Map.of("id", "msmarco_passage_00_997"));
    expected.put("msmarco_passage_00_1451", Map.of("id", "msmarco_passage_00_1451"));
    expected.put("msmarco_passage_00_1778", Map.of("id", "msmarco_passage_00_1778"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
  }
}
