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

public class MsMarcoV2PassageCollectionCompressedTest extends DocumentCollectionTest<MsMarcoV2PassageCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/msmarco_v2_passage_gz/");
    collection = new MsMarcoV2PassageCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/msmarco_v2_passage_gz/docs.json.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 6);

    totalSegments = 1;
    totalDocs = 6;

    expected.put("msmarco_passage_00_0", Map.of("id", "msmarco_passage_00_0", "contents_length", "76"));
    expected.put("msmarco_passage_00_172", Map.of("id", "msmarco_passage_00_172", "contents_length", "295"));
    expected.put("msmarco_passage_00_587", Map.of("id", "msmarco_passage_00_587", "contents_length", "290"));
    expected.put("msmarco_passage_00_997", Map.of("id", "msmarco_passage_00_997", "contents_length", "318"));
    expected.put("msmarco_passage_00_1451", Map.of("id", "msmarco_passage_00_1451", "contents_length", "223"));
    expected.put("msmarco_passage_00_1778", Map.of("id", "msmarco_passage_00_1778", "contents_length", "284"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("contents_length"), doc.contents().length() + "");
  }
}
