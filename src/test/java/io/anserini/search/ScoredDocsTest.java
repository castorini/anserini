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

package io.anserini.search;

import io.anserini.index.Constants;
import io.anserini.index.IndexCollection;
import io.anserini.index.IndexerTestBase;
import io.anserini.search.query.BagOfWordsQueryGenerator;
import io.anserini.search.query.QueryGenerator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

public class ScoredDocsTest extends IndexerTestBase {
  @Test
  public void testFromTopDocs() throws Exception {
    // Query using SimpleSearcher
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    ScoredDoc[] hits = searcher.search("test", 10);
    assertEquals(1, hits.length);
    assertEquals("doc3", hits[0].docid);
    assertEquals(2, hits[0].lucene_docid);
    assertEquals(0.57020f, hits[0].score, 10e-5);
    assertEquals("here is a test", searcher.doc_contents(hits[0].docid));
    assertEquals("{\"contents\": \"here is a test\"}", searcher.doc_raw(hits[0].docid));
    searcher.close();

    // Verify result results when putting "Lucene pieces" together by hand:
    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(super.tempDir1.toString())));
    Similarity similarity = new BM25Similarity(0.9f, 0.4f);
    Analyzer analyzer = IndexCollection.DEFAULT_ANALYZER;
    IndexSearcher indexSearcher = new IndexSearcher(reader);
    indexSearcher.setSimilarity(similarity);

    QueryGenerator generator = new BagOfWordsQueryGenerator();
    Query query = generator.buildQuery(Constants.CONTENTS, analyzer, "test");

    TopDocs topDocs = indexSearcher.search(query, 10);
    assertEquals(1, topDocs.scoreDocs.length);
    assertEquals(2, topDocs.scoreDocs[0].doc);
    assertEquals(0.57024956f, topDocs.scoreDocs[0].score, 10e-8);

    // Now we can test ScoredDocs itself:
    ScoredDocs scoredDocs = ScoredDocs.fromTopDocs(topDocs, indexSearcher);
    assertNotNull(scoredDocs);
    assertNotNull(scoredDocs.docids);
    assertNotNull(scoredDocs.lucene_documents);
    assertNotNull(scoredDocs.lucene_docids);
    assertNotNull(scoredDocs.scores);

    assertEquals(1, scoredDocs.docids.length);
    assertEquals(1, scoredDocs.lucene_documents.length);
    assertEquals(1, scoredDocs.lucene_docids.length);
    assertEquals(1, scoredDocs.scores.length);

    assertEquals(2, scoredDocs.lucene_docids[0]);
    assertEquals(0.57024956f, scoredDocs.scores[0], 10e-8);
    assertEquals("doc3", scoredDocs.docids[0]);
    assertEquals("here is a test", scoredDocs.lucene_documents[0].get(Constants.CONTENTS));
    assertEquals("{\"contents\": \"here is a test\"}", scoredDocs.lucene_documents[0].get(Constants.RAW));
  }

  @Test(expected = RuntimeException.class)
  public void testFromTopDocsError() throws Exception {
    // Query using SimpleSearcher
    SimpleSearcher searcher = new SimpleSearcher(super.tempDir1.toString());
    ScoredDoc[] hits = searcher.search("test", 10);
    assertEquals(1, hits.length);
    assertEquals("doc3", hits[0].docid);
    assertEquals(2, hits[0].lucene_docid);
    assertEquals(0.57020f, hits[0].score, 10e-5);
    assertEquals("here is a test", searcher.doc_contents(hits[0].docid));
    assertEquals("{\"contents\": \"here is a test\"}", searcher.doc_raw(hits[0].docid));
    searcher.close();

    // Verify result results when putting "Lucene pieces" together by hand:
    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(super.tempDir1.toString())));
    Similarity similarity = new BM25Similarity(0.9f, 0.4f);
    Analyzer analyzer = IndexCollection.DEFAULT_ANALYZER;
    IndexSearcher indexSearcher = new IndexSearcher(reader);
    indexSearcher.setSimilarity(similarity);

    QueryGenerator generator = new BagOfWordsQueryGenerator();
    Query query = generator.buildQuery(Constants.CONTENTS, analyzer, "test");

    TopDocs topDocs = indexSearcher.search(query, 10);
    assertEquals(1, topDocs.scoreDocs.length);
    assertEquals(2, topDocs.scoreDocs[0].doc);
    assertEquals(0.57024956f, topDocs.scoreDocs[0].score, 10e-8);

    // Now we can test ScoredDocs itself - note injecting null deliberately.
    ScoredDocs scoredDocs = ScoredDocs.fromTopDocs(topDocs, null);
  }

  @Test
  public void testFromQrels() throws Exception {
    Map<String, Integer> qrels = new TreeMap<>();
    qrels.put("doc1", 1);
    qrels.put("doc2", 1);
    qrels.put("doc3", 0);

    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(super.tempDir1.toString())));

    ScoredDocs scoredDocs = ScoredDocs.fromQrels(qrels, reader);
    assertNotNull(scoredDocs);
    assertNotNull(scoredDocs.docids);
    assertNotNull(scoredDocs.lucene_documents);
    assertNotNull(scoredDocs.lucene_docids);
    assertNotNull(scoredDocs.scores);

    assertEquals(3, scoredDocs.docids.length);
    assertEquals(3, scoredDocs.lucene_documents.length);
    assertEquals(3, scoredDocs.lucene_docids.length);
    assertEquals(3, scoredDocs.scores.length);

    assertEquals("doc1", scoredDocs.docids[0]);
    assertEquals("doc2", scoredDocs.docids[1]);
    assertEquals("doc3", scoredDocs.docids[2]);

    assertEquals("doc1", scoredDocs.lucene_documents[0].get(Constants.ID));
    assertEquals("doc2", scoredDocs.lucene_documents[1].get(Constants.ID));
    assertEquals("doc3", scoredDocs.lucene_documents[2].get(Constants.ID));

    assertEquals("{\"contents\": \"here is some text here is some more text. city.\"}", scoredDocs.lucene_documents[0].get(Constants.RAW));
    assertEquals("{\"contents\": \"more texts\"}", scoredDocs.lucene_documents[1].get(Constants.RAW));
    assertEquals("{\"contents\": \"here is a test\"}", scoredDocs.lucene_documents[2].get(Constants.RAW));

    assertEquals(1.0f, scoredDocs.scores[0], 10e-8);
    assertEquals(1.0f, scoredDocs.scores[1], 10e-8);
    assertEquals(0.0f, scoredDocs.scores[2], 10e-8);
  }

  @Test(expected = RuntimeException.class)
  public void testFromQrelsError() throws Exception {
    Map<String, Integer> qrels = new TreeMap<>();
    qrels.put("doc1", 1);
    qrels.put("doc2", 1);
    qrels.put("doc3", 0);

    ScoredDocs.fromQrels(qrels, null);
  }
}
