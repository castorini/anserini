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
import java.util.HashMap;
import java.util.Map;

public class AfribertaCollectionTest extends DocumentCollectionTest<AfribertaCollection.Document> {
  
  @Before
  public void setUp() throws Exception {
    super.setUp();
    
    collectionPath = Paths.get("src/test/resources/sample_docs/afriberta");
    collection = new AfribertaCollection(collectionPath);
    
    Path segment1 = Paths.get("src/test/resources/sample_docs/afriberta/afriberta.collection.sample.txt.zip");
    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);
    
    totalSegments = 1;
    totalDocs = 4;
    
    HashMap<String, String> doc0 = new HashMap<>();
    doc0.put("id", "doc_0");
    expected.put("doc_0", doc0);
    
    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "doc_1");
    expected.put("doc_1", doc1);
    
    HashMap<String, String> doc2 = new HashMap<>();
    doc2.put("id", "doc_2");
    expected.put("doc_2", doc2);
    
    HashMap<String, String> doc3 = new HashMap<>();
    doc3.put("id", "doc_3");
    expected.put("doc_3", doc3);
  }
  
  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
  }
}
