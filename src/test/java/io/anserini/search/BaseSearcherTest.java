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

public class BaseSearcherTest extends IndexerTestBase {
  @Test
  public void testProcessLuceneTopDocs() throws Exception {
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

    // Now we can test BaseSearcher itself:
    BaseSearcher<String> baseSearcher = new BaseSearcher<>(new BaseSearchArgs(), indexSearcher);
    ScoredDoc[] scoredDocs = baseSearcher.processLuceneTopDocs("q1", topDocs);

    assertEquals(1, scoredDocs.length);
    assertEquals(2, scoredDocs[0].lucene_docid);
    assertEquals(0.57024956f, scoredDocs[0].score, 10e-8);
    assertEquals("doc3", scoredDocs[0].docid);
    assertEquals("here is a test", scoredDocs[0].lucene_document.get(Constants.CONTENTS));
    assertEquals("{\"contents\": \"here is a test\"}", scoredDocs[0].lucene_document.get(Constants.RAW));

    scoredDocs = baseSearcher.processLuceneTopDocs("q1", topDocs, false);
    assertEquals(1, scoredDocs.length);
    assertEquals(2, scoredDocs[0].lucene_docid);
    assertEquals(0.57024956f, scoredDocs[0].score, 10e-8);
    assertEquals("doc3", scoredDocs[0].docid);
    assertNull(scoredDocs[0].lucene_document);
  }

  @Test
  public void processScoredDocs() throws Exception {
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

    // Now we can test BaseSearcher itself:
    BaseSearcher<String> baseSearcher = new BaseSearcher<>(new BaseSearchArgs(), indexSearcher);

    ScoredDoc[] scoredDocs = baseSearcher.processScoredDocs("q1", ScoredDocs.fromTopDocs(topDocs, indexSearcher), true);
    assertEquals(1, scoredDocs.length);
    assertEquals(2, scoredDocs[0].lucene_docid);
    assertEquals(0.57024956f, scoredDocs[0].score, 10e-8);
    assertEquals("doc3", scoredDocs[0].docid);
    assertEquals("here is a test", scoredDocs[0].lucene_document.get(Constants.CONTENTS));
    assertEquals("{\"contents\": \"here is a test\"}", scoredDocs[0].lucene_document.get(Constants.RAW));

    scoredDocs = baseSearcher.processScoredDocs("q1", ScoredDocs.fromTopDocs(topDocs, indexSearcher), false);
    assertEquals(1, scoredDocs.length);
    assertEquals(2, scoredDocs[0].lucene_docid);
    assertEquals(0.57024956f, scoredDocs[0].score, 10e-8);
    assertEquals("doc3", scoredDocs[0].docid);
    assertNull(scoredDocs[0].lucene_document);
  }
}
