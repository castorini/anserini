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

public class NeuClirCollectionTest extends DocumentCollectionTest<NeuClirCollection.Document> {
  
  @Before
  public void setUp() throws Exception {
    super.setUp();
    
    collectionPath = Paths.get("src/test/resources/sample_docs/neuclir/collection");
    collection = new NeuClirCollection(collectionPath);
    
    Path segment1 = Paths.get("src/test/resources/sample_docs/neuclir/collection/sample_rus_collection.jsonl");
    
    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);
    
    totalSegments = 1;
    totalDocs = 4;
    
    expected.put("ffaac72f-e0c2-4432-8720-efa084432783", Map.of("id", "ffaac72f-e0c2-4432-8720-efa084432783"));
    expected.put("b66d72f0-d2ec-4322-a0c7-b7bac63734bd", Map.of("id", "b66d72f0-d2ec-4322-a0c7-b7bac63734bd"));
    expected.put("94ef0502-6e34-43df-8a95-f172f6c0ec65", Map.of("id", "94ef0502-6e34-43df-8a95-f172f6c0ec65"));
    expected.put("07587ab2-b0e2-491c-a084-6b770c4dffed", Map.of("id", "07587ab2-b0e2-491c-a084-6b770c4dffed"));
  }
  
  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
  }
}
