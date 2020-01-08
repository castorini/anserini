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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.LuceneTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

public class SdmQueryTest extends LuceneTestCase {

  private Path tempDir1;
  private final String field = "text";
  private final Analyzer analyzer = new EnglishAnalyzer();

  // A very simple example of how to build an index.
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
  public void spanQueriesTest() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = newSearcher(reader);
    searcher.setSimilarity(new BM25Similarity());
    SpanNearQuery q;
    TopDocs rs;

    SpanTermQuery t1 = new SpanTermQuery(new Term(field, "john"));
    SpanTermQuery t2 = new SpanTermQuery(new Term(field, "bush"));

    q = new SpanNearQuery(new SpanQuery[]{t1, t2}, 3, true);
    rs = searcher.search(q, 1);
    assertEquals(rs.scoreDocs.length, 0);

    q = new SpanNearQuery(new SpanQuery[]{t1, t2}, 8, true);
    rs = searcher.search(q, 1);
    assertEquals(rs.scoreDocs.length, 1);

    q = new SpanNearQuery(new SpanQuery[]{t2, t1}, 8, true);
    rs = searcher.search(q, 1);
    assertEquals(rs.scoreDocs.length, 0);

    q = new SpanNearQuery(new SpanQuery[]{t2, t1}, 8, false);
    rs = searcher.search(q, 1);
    assertEquals(rs.scoreDocs.length, 1);

    q = new SpanNearQuery(new SpanQuery[]{t2, t1}, 16, false);
    rs = searcher.search(q, 1);
    assertEquals(rs.scoreDocs.length, 1);
    
    String sdmQueryStr = "fox information river";
    Query sdmQuery1 = new SdmQueryGenerator(1.0f, 0.0f, 0.0f).buildQuery(field, analyzer, sdmQueryStr);
    assertEquals(sdmQuery1.toString(), "(text:fox text:inform text:river)^1.0 " +
            "(spanNear([text:fox, text:inform], 1, true) spanNear([text:inform, text:river], 1, true))^0.0 " +
            "(spanNear([text:fox, text:inform], 8, false) spanNear([text:inform, text:river], 8, false))^0.0");
    TopDocs rs1 = searcher.search(sdmQuery1, 1);
    Query termQuery = new BagOfWordsQueryGenerator().buildQuery(field, analyzer, sdmQueryStr);
    TopDocs rsTerm = searcher.search(termQuery, 1);
    assertEquals(rs1.scoreDocs[0].score, rsTerm.scoreDocs[0].score, 1e-6f);

    /////////
    Query sdmQuery2 = new SdmQueryGenerator(0.0f, 1.0f, 0.0f).buildQuery(field, analyzer, sdmQueryStr);
    assertEquals(sdmQuery2.toString(), "(text:fox text:inform text:river)^0.0 " +
            "(spanNear([text:fox, text:inform], 1, true) spanNear([text:inform, text:river], 1, true))^1.0 " +
            "(spanNear([text:fox, text:inform], 8, false) spanNear([text:inform, text:river], 8, false))^0.0");
    TopDocs rs2 = searcher.search(sdmQuery2, 1);
    Query orderedWindowQuery1 = new SpanNearQuery(new SpanQuery[]{
            new SpanTermQuery(new Term(field, "fox")),
            new SpanTermQuery(new Term(field, "inform"))}, 1, true);
    Query orderedWindowQuery2 = new SpanNearQuery(new SpanQuery[]{
            new SpanTermQuery(new Term(field, "inform")),
            new SpanTermQuery(new Term(field, "river"))}, 1, true);
    TopDocs rsOrderedWindow1 = searcher.search(orderedWindowQuery1, 1);
    TopDocs rsOrderedWindow2 = searcher.search(orderedWindowQuery2, 1);
    assertEquals(rs2.scoreDocs[0].score, rsOrderedWindow1.scoreDocs[0].score + rsOrderedWindow2.scoreDocs[0].score, 1e-6f);

    ////////
    Query sdmQuery3 = new SdmQueryGenerator(0.0f, 0.0f, 1.0f).buildQuery(field, analyzer, sdmQueryStr);
    assertEquals(sdmQuery3.toString(), "(text:fox text:inform text:river)^0.0 " +
            "(spanNear([text:fox, text:inform], 1, true) spanNear([text:inform, text:river], 1, true))^0.0 " +
            "(spanNear([text:fox, text:inform], 8, false) spanNear([text:inform, text:river], 8, false))^1.0");
    TopDocs rs3 = searcher.search(sdmQuery3, 1);
    Query unorderedWindowQuery1 = new SpanNearQuery(new SpanQuery[]{
            new SpanTermQuery(new Term(field, "fox")),
            new SpanTermQuery(new Term(field, "inform"))}, 8, false);
    Query unorderedWindowQuery2 = new SpanNearQuery(new SpanQuery[]{
            new SpanTermQuery(new Term(field, "inform")),
            new SpanTermQuery(new Term(field, "river"))}, 8, false);
    TopDocs rsUnorderedWindow1 = searcher.search(unorderedWindowQuery1, 1);
    TopDocs rsUnorderedWindow2 = searcher.search(unorderedWindowQuery2, 1);
    assertEquals(rs3.scoreDocs[0].score, rsUnorderedWindow1.scoreDocs[0].score + rsUnorderedWindow2.scoreDocs[0].score, 1e-6f);

    //////////
    Query sdmQuery4 = new SdmQueryGenerator(0.85f, 0.1f, 0.05f).buildQuery(field, analyzer, sdmQueryStr);
    assertEquals(sdmQuery4.toString(), "(text:fox text:inform text:river)^0.85 " +
            "(spanNear([text:fox, text:inform], 1, true) spanNear([text:inform, text:river], 1, true))^0.1 " +
            "(spanNear([text:fox, text:inform], 8, false) spanNear([text:inform, text:river], 8, false))^0.05");
    TopDocs rs4 = searcher.search(sdmQuery4, 1);
    assertEquals(rs4.scoreDocs[0].score, rsTerm.scoreDocs[0].score*0.85f
        + (rsOrderedWindow1.scoreDocs[0].score + rsOrderedWindow2.scoreDocs[0].score)*0.1f
        + (rsUnorderedWindow1.scoreDocs[0].score + rsUnorderedWindow2.scoreDocs[0].score)*0.05f, 1e-6f);

    reader.close();
  }
}
