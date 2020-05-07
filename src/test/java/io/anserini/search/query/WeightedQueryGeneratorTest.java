/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

package io.anserini.search.query;

import io.anserini.analysis.DefaultEnglishAnalyzer;
import io.anserini.index.IndexCollection;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.LuceneTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WeightedQueryGeneratorTest extends LuceneTestCase {

  private Path tempDir1;
  private final String field = "contents";
  private final Analyzer analyzer = DefaultEnglishAnalyzer.newDefaultInstance();

  private void buildTestIndex() throws IOException {
    Directory dir = FSDirectory.open(tempDir1);
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setSimilarity(new BM25Similarity());
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, config);

    FieldType textOptions = new FieldType();
    textOptions.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    textOptions.setStored(true);
    textOptions.setTokenized(true);

    Document doc1 = new Document();
    doc1.add(new Field(field, "john fox information river chicken bush frank retrieval world", textOptions));
    writer.addDocument(doc1);

    writer.commit();
    writer.forceMerge(1);
    writer.close();
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();

    tempDir1 = createTempDir();
    buildTestIndex();
  }

  @After
  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  @Test
  public void buildWeightedQueryTest() {
    Analyzer analyzer = IndexCollection.DEFAULT_ANALYZER;
    WeightedQueryGenerator weightedQueryGenerator = new WeightedQueryGenerator();
    weightedQueryGenerator.addTermsWithWeight("information", 1F);
    weightedQueryGenerator.addTermsWithWeight("retrieval",2F);
    Query query = weightedQueryGenerator.buildQuery("contents", analyzer, "test");

    assertTrue(query instanceof BooleanQuery);

    List<BooleanClause> bc = ((BooleanQuery) query).clauses();
    assertEquals(bc.size(), 3);

    assertEquals(((TermQuery) ((BoostQuery) bc.get(0).getQuery()).getQuery()).getTerm().text(), "inform");
    assertEquals(((TermQuery) ((BoostQuery) bc.get(0).getQuery()).getQuery()).getTerm().field(), "contents");
    assertEquals(((BoostQuery) bc.get(0).getQuery()).getBoost(), 1F, 0.0001);

    assertEquals(((TermQuery) ((BoostQuery) bc.get(1).getQuery()).getQuery()).getTerm().text(), "retriev");
    assertEquals(((TermQuery) ((BoostQuery) bc.get(1).getQuery()).getQuery()).getTerm().field(), "contents");
    assertEquals(((BoostQuery) bc.get(1).getQuery()).getBoost(), 2F, 0.0001);

    assertEquals(((TermQuery) ((BoostQuery) bc.get(2).getQuery()).getQuery()).getTerm().text(), "test");
    assertEquals(((TermQuery) ((BoostQuery) bc.get(2).getQuery()).getQuery()).getTerm().field(), "contents");
    assertEquals(((BoostQuery) bc.get(2).getQuery()).getBoost(), 1F, 0.0001);
  }

  @Test
  public void buildWeightedQueryTest2() throws IOException {
    Directory dir = FSDirectory.open(this.tempDir1);
    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = newSearcher(reader);
    searcher.setSimilarity(new BM25Similarity());

    Analyzer analyzer = IndexCollection.DEFAULT_ANALYZER;
    WeightedQueryGenerator weightedQueryGenerator = new WeightedQueryGenerator();
    weightedQueryGenerator.addTermsWithWeight("information", 1F);
    weightedQueryGenerator.addTermsWithWeight("retrieval", 1F);
    Query query = weightedQueryGenerator.buildQuery("contents", analyzer, "");

    assertTrue(query instanceof BooleanQuery);

    QueryGenerator queryGenerator = new BagOfWordsQueryGenerator();
    Query query2 = queryGenerator.buildQuery("contents", analyzer, "information retrieval");

    TopDocs rs1 = searcher.search(query, 10);
    TopDocs rs2 = searcher.search(query2, 10);

    assertEquals(rs1.scoreDocs[0].score, rs2.scoreDocs[0].score, 0.0001);
  }

  @Test
  public void buildWeightedQueryTest3() throws IOException {
    Directory dir = FSDirectory.open(this.tempDir1);
    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = newSearcher(reader);
    searcher.setSimilarity(new BM25Similarity());

    Analyzer analyzer = IndexCollection.DEFAULT_ANALYZER;
    WeightedQueryGenerator weightedQueryGenerator = new WeightedQueryGenerator();
    weightedQueryGenerator.addTermsWithWeight("information", 1F);
    weightedQueryGenerator.addTermsWithWeight("retrieval", 2F);
    Query query = weightedQueryGenerator.buildQuery("contents", analyzer, "");

    assertTrue(query instanceof BooleanQuery);

    QueryGenerator queryGenerator = new BagOfWordsQueryGenerator();
    Query query2 = queryGenerator.buildQuery("contents", analyzer, "information retrieval");

    TopDocs rs1 = searcher.search(query, 10);
    TopDocs rs2 = searcher.search(query2, 10);

    assertNotEquals(rs1.scoreDocs[0].score, rs2.scoreDocs[0].score, 0.0001);
  }
}
