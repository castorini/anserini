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

public class MsMarcoV2PassageAugmentedCollectionTest extends DocumentCollectionTest<MsMarcoV2PassageCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/msmarco_v2_passage_augmented/");
    collection = new MsMarcoV2PassageCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/msmarco_v2_passage_augmented/docs0.json.gz");
    Path segment2 = Paths.get("src/test/resources/sample_docs/msmarco_v2_passage_augmented/docs1.json.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 2);

    segmentPaths.add(segment2);
    segmentDocCounts.put(segment2, 2);

    totalSegments = 2;
    totalDocs = 4;

    expected.put("msmarco_passage_00_0", Map.of("id", "msmarco_passage_00_0", "contents_length", "206"));
    expected.put("msmarco_passage_00_172", Map.of("id", "msmarco_passage_00_172", "contents_length", "425"));
    expected.put("msmarco_passage_07_2393", Map.of("id", "msmarco_passage_07_2393", "contents_length", "978"));
    expected.put("msmarco_passage_07_2852", Map.of("id", "msmarco_passage_07_2852", "contents_length", "992"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("contents_length"), doc.contents().length() + "");
  }
}
