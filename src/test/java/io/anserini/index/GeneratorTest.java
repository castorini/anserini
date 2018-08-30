/**
 * Anserini: An information retrieval toolkit built on Lucene
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

import io.anserini.collection.BaseFileSegment;
import io.anserini.collection.ClueWeb12Collection;
import io.anserini.index.generator.JsoupGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.LuceneTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class GeneratorTest extends LuceneTestCase {

  private Path tempDir;
  private String testingDocPath = "src/test/resources/sample_docs/clueweb/clueweb12-1";
  private List<String> fields = Arrays.asList(new String[]{"url", "title", "anchor_text", "html_body"});
  private IndexReader reader;
  
  private void buildTestIndex() throws IOException {
    Directory dir = FSDirectory.open(tempDir);
    
    IndexWriterConfig config = new IndexWriterConfig();
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setSimilarity(new BM25Similarity());
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setRAMBufferSizeMB(8);
    config.setUseCompoundFile(false);
    config.setMergeScheduler(new ConcurrentMergeScheduler());
    
    IndexWriter writer = new IndexWriter(dir, config);

    FieldType textOptions = new FieldType();
    textOptions.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
    textOptions.setStored(true);
    textOptions.setTokenized(true);
    textOptions.setStoreTermVectors(true);
    textOptions.setStoreTermVectorPositions(true);
    
    ClueWeb12Collection collection = new ClueWeb12Collection();
    JsoupGenerator generator = new JsoupGenerator();
    String rawDoc = FileUtils.readFileToString(new File(testingDocPath), StandardCharsets.UTF_8);
    BaseFileSegment<ClueWeb12Collection.Document> iter = collection.createFileSegment(rawDoc);
    while (iter.hasNext()) {
      ClueWeb12Collection.Document parsed = iter.next();
      Document d = generator.createDocument(parsed,
          parsed.getAdditionalFields(fields));
      writer.addDocument(d);
    }
    writer.commit();
    writer.forceMerge(1);
    writer.close();
    
    reader = DirectoryReader.open(FSDirectory.open(tempDir));
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    tempDir = createTempDir();
    buildTestIndex();
  }

  @After
  @Override
  public void tearDown() throws Exception {
    reader.close();
    super.tearDown();
  }
  
  @Test
  public void testIndexIncludesFields() throws Exception {
    Directory dir = FSDirectory.open(tempDir);
    IndexReader reader = DirectoryReader.open(dir);
    FieldInfos fieldInfos = MultiFields.getMergedFieldInfos(reader);
    Fields indexedFields = MultiFields.getFields(reader);
    Set<String> indexedFieldsSet = new HashSet<>();
    int i = 0;
    for (String fd : indexedFields) {
      if (fields.contains(fd)) {
        indexedFieldsSet.add(fd);
      }
    }
    assertEquals(new HashSet<String>(fields), indexedFieldsSet);
  
    Document d = reader.document(0);
    assertEquals(d.getField("url").stringValue(), "http://clueweb09.test.com/");
    assertEquals(d.getField("title").stringValue(), "fox fox river");
    assertEquals(d.getField("anchor_text").stringValue(), " anchor text fox");
    // HTML BODY is NOT stored!!!!
  }

  @Test
  public void testSearchFields() throws Exception {
    class TFSimilarity extends Similarity {
      class TFSimWeight extends SimWeight {
        @Override
        public float getValueForNormalization() {
          // we return a TF-IDF like normalization to be nice, but we don't actually normalize ourselves.
          return 1;
        }
        @Override
        public void normalize(float queryNorm, float boost) {}
      }
      @Override
      public final SimWeight computeWeight(CollectionStatistics collectionStats, TermStatistics... termStats) {
        return new TFSimWeight();
      }
      @Override
      public final SimScorer simScorer(SimWeight stats, LeafReaderContext context) throws IOException {
        class TFDocScorer extends SimScorer {
          @Override
          public float score(int doc, float freq) {
            return freq;
          }
    
          @Override
          public Explanation explain(int doc, Explanation freq) {
            return null;
          }
    
          @Override
          public float computeSlopFactor(int distance) {
            return 1.0f / (distance + 1);
          }
    
          @Override
          public float computePayloadFactor(int doc, int start, int end, BytesRef payload) {
            return 1;
          }
        }
        return new TFDocScorer();
      }
      @Override
      public final long computeNorm(FieldInvertState state) {
        return 1;
      }
    }
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(new TFSimilarity());
    StandardQueryParser p = new StandardQueryParser();
    TopDocs rs;
    rs = searcher.search( p.parse("fox", "html_body"), 1);
    assertEquals(rs.scoreDocs.length, 1);
    assertEquals(rs.scoreDocs[0].score, 4, 1e-8);
    rs = searcher.search( p.parse("fox", "anchor_text"), 1);
    assertEquals(rs.scoreDocs.length, 1);
    assertEquals(rs.scoreDocs[0].score, 1, 1e-8);
    rs = searcher.search( p.parse("fox", "title"), 1);
    assertEquals(rs.scoreDocs.length, 1);
    assertEquals(rs.scoreDocs[0].score, 2, 1e-8);
  }
}
