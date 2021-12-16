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

public class MsMarcoV2DocSegmentedCollectionTest extends DocumentCollectionTest<MsMarcoV2DocCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/msmarco_v2_doc_segmented/");
    collection = new MsMarcoV2DocCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/msmarco_v2_doc_segmented/docs0.json.gz");
    Path segment2 = Paths.get("src/test/resources/sample_docs/msmarco_v2_doc_segmented/docs1.json.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 6);

    segmentPaths.add(segment2);
    segmentDocCounts.put(segment2, 2);

    totalSegments = 2;
    totalDocs = 8;

    expected.put("msmarco_doc_00_0#0", Map.of("id", "msmarco_doc_00_0#0"));
    expected.put("msmarco_doc_00_0#1", Map.of("id", "msmarco_doc_00_0#1"));
    expected.put("msmarco_doc_00_0#2", Map.of("id", "msmarco_doc_00_0#2"));
    expected.put("msmarco_doc_00_4806#0", Map.of("id", "msmarco_doc_00_4806#0"));
    expected.put("msmarco_doc_00_4806#1", Map.of("id", "msmarco_doc_00_4806#1"));
    expected.put("msmarco_doc_00_4806#2", Map.of("id", "msmarco_doc_00_4806#2"));
    expected.put("msmarco_doc_06_0#0", Map.of("id", "msmarco_doc_06_0#0"));
    expected.put("msmarco_doc_06_0#1", Map.of("id", "msmarco_doc_06_0#1"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
  }
}
