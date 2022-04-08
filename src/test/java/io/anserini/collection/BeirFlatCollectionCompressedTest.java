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

public class BeirFlatCollectionCompressedTest extends DocumentCollectionTest<BeirFlatCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/beir/collection2");
    collection = new BeirFlatCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/beir/collection2/segment1.jsonl.gz");
    Path segment2 = Paths.get("src/test/resources/sample_docs/beir/collection2/segment2.jsonl.gz");

    segmentPaths.add(segment1);
    segmentPaths.add(segment2);

    segmentDocCounts.put(segment1, 2);
    segmentDocCounts.put(segment2, 1);

    totalSegments = 2;
    totalDocs = 3;

    expected.put("doc1", Map.of("id", "doc1", "contents", "doc1 title\ndoc1 text"));
    expected.put("doc2", Map.of("id", "doc2", "contents", "doc2 title\ndoc2 text"));
    expected.put("doc3", Map.of("id", "doc3", "contents", "doc3 title\ndoc3 text"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("contents"), doc.contents());
    // No additional fields to index.
    assertEquals(0, ((BeirFlatCollection.Document) doc).fields().size());
  }
}
