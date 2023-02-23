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

public class SimpleIndexerAppendTest extends LuceneTestCase {

  private class JsonCollectionWrapper {
    JsonCollection collection;

    public JsonCollectionWrapper(String collectionPath) {
      collection = new JsonCollection(Paths.get(collectionPath));
    }

    public int indexWith(SimpleIndexer indexer) {
      int cnt = 0;
      for (FileSegment<JsonCollection.Document> segment : collection) {
        for (JsonCollection.Document doc : segment) {
          indexer.addDocument(doc.raw());
          cnt++;
        }
        segment.close();
      }

      return cnt;
    }
  }

  @Test
  public void testBasic() throws Exception {
    Path tempDir = createTempDir();
    SimpleIndexer indexer;
    int cnt;

    SimpleSearcher searcher;
    SimpleSearcher.Result[] hits;

    indexer = new SimpleIndexer(tempDir.toString());
    cnt = new JsonCollectionWrapper("src/test/resources/sample_docs/json/collection3").indexWith(indexer);
    indexer.close();

    assertEquals(2, cnt);

    searcher = new SimpleSearcher(tempDir.toString());
    assertEquals(2, searcher.get_total_num_docs());
    hits = searcher.search("1", 10);
    assertEquals(1, hits.length);
    assertEquals("doc1", hits[0].docid);
    assertEquals(0.3648, hits[0].score, 1e-4);
    searcher.close();

    // We're going to overwrite the index.
    indexer = new SimpleIndexer(tempDir.toString());
    cnt = new JsonCollectionWrapper("src/test/resources/sample_docs/json/collection4").indexWith(indexer);
    indexer.close();

    assertEquals(2, cnt);

    searcher = new SimpleSearcher(tempDir.toString());
    assertEquals(2, searcher.get_total_num_docs());
    hits = searcher.search("contains", 10);
    assertEquals(1, hits.length);
    assertEquals("doc3", hits[0].docid);
    assertEquals(0.3648, hits[0].score, 1e-4);
    searcher.close();

    // We're going to append to the index.
    indexer = new SimpleIndexer(tempDir.toString(), true);
    cnt = new JsonCollectionWrapper("src/test/resources/sample_docs/json/collection3").indexWith(indexer);
    indexer.close();

    assertEquals(2, cnt);

    searcher = new SimpleSearcher(tempDir.toString());
    assertEquals(4, searcher.get_total_num_docs());
    hits = searcher.search("contains", 10);
    assertEquals(1, hits.length);
    assertEquals("doc3", hits[0].docid);
    assertEquals(0.5960, hits[0].score, 1e-4);

    hits = searcher.search("1", 10);
    assertEquals(1, hits.length);
    assertEquals("doc1", hits[0].docid);
    assertEquals(0.6764, hits[0].score, 1e-4);
    searcher.close();
  }
}
