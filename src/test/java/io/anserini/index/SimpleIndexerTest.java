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

package io.anserini.index;

import io.anserini.collection.FileSegment;
import io.anserini.collection.JsonCollection;
import io.anserini.search.SimpleSearcher;
import org.apache.lucene.tests.util.LuceneTestCase;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SimpleIndexerTest extends LuceneTestCase {

  @Test
  public void testBasic() throws IOException {
    Path tempDir = createTempDir();

    Path collectionPath = Paths.get("src/test/resources/sample_docs/json/collection3");
    JsonCollection collection = new JsonCollection(collectionPath);

    int cnt = 0;
    SimpleIndexer indexer = new SimpleIndexer(tempDir.toString());
    for (FileSegment<JsonCollection.Document> segment : collection ) {
      for (JsonCollection.Document doc : segment) {
        indexer.addDocument(doc.raw());
        cnt++;
      }
      segment.close();
    }

    indexer.close();
    assertEquals(2, cnt);

    SimpleSearcher searcher = new SimpleSearcher(tempDir.toString());
    SimpleSearcher.Result[] hits = searcher.search("1", 10);
    assertEquals(1, hits.length);
    assertEquals("doc1", hits[0].docid);
    assertEquals(0.3648, hits[0].score, 1e-4);

    searcher.close();
  }

}
