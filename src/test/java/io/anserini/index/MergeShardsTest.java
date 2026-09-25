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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MergeShardsTest extends IndexerTestBase {
  private final static PrintStream standardOut = System.out;
  private final static PrintStream standardErr = System.err;
  private final static ByteArrayOutputStream outputCaptor = new ByteArrayOutputStream();
  private final static ByteArrayOutputStream errorCaptor = new ByteArrayOutputStream();
  
  private Path shardDir1;
  private Path shardDir2;
  private Path mergedDir;

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    shardDir1 = createTempDir();
    shardDir2 = createTempDir();
    mergedDir = createTempDir();
    
    System.setOut(new PrintStream(outputCaptor));
    System.setErr(new PrintStream(errorCaptor));
    
    buildShardIndex(shardDir1, "shard1-doc1", "shard1-doc2");
    buildShardIndex(shardDir2, "shard2-doc1", "shard2-doc2");
  }
  
  @After
  @Override
  public void tearDown() throws Exception {
    System.setOut(standardOut);
    System.setErr(standardErr);
    System.gc();
    super.tearDown();
  }
  
  private void buildShardIndex(Path path, String id1, String id2) throws IOException {
    Directory dir = FSDirectory.open(path);
    
    Analyzer analyzer = new EnglishAnalyzer();
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    
    IndexWriter writer = new IndexWriter(dir, config);
    
    FieldType textOptions = new FieldType();
    textOptions.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    textOptions.setStored(true);
    textOptions.setTokenized(true);
    textOptions.setStoreTermVectors(true);
    textOptions.setStoreTermVectorPositions(true);
    
    Document doc1 = new Document();
    String doc1Text = "here is some text for " + id1;
    doc1.add(new StringField(Constants.ID, id1, Field.Store.YES));
    doc1.add(new BinaryDocValuesField(Constants.ID, new BytesRef(id1.getBytes())));
    doc1.add(new Field(Constants.CONTENTS, doc1Text, textOptions));
    doc1.add(new StoredField(Constants.RAW, String.format("{\"contents\": \"%s\"}", doc1Text)));
    writer.addDocument(doc1);
    
    Document doc2 = new Document();
    String doc2Text = "more texts for " + id2;
    doc2.add(new StringField(Constants.ID, id2, Field.Store.YES));
    doc2.add(new BinaryDocValuesField(Constants.ID, new BytesRef(id2.getBytes())));
    doc2.add(new Field(Constants.CONTENTS, doc2Text, textOptions));
    doc2.add(new StoredField(Constants.RAW, String.format("{\"contents\": \"%s\"}", doc2Text)));
    writer.addDocument(doc2);
    
    writer.commit();
    writer.forceMerge(1);
    writer.close();
    
    dir.close();
  }
  
  @Test
  public void testMergeShards() throws Exception {
    String[] args = new String[] {
        mergedDir.toString(),
        shardDir1.toString(),
        shardDir2.toString()
    };
    
    MergeShards.main(args);
    
    String output = outputCaptor.toString();
    assertTrue(output.contains("Adding index: " + shardDir1.toString()));
    assertTrue(output.contains("Adding index: " + shardDir2.toString()));
    assertTrue(output.contains("Merging..."));
    assertTrue(output.contains("Done. Merged index at: " + mergedDir.toString()));
    
    Directory dir = FSDirectory.open(mergedDir);
    IndexReader reader = DirectoryReader.open(dir);
    
    assertEquals(4, reader.numDocs());
    
    assertEquals(1, reader.docFreq(new Term(Constants.ID, "shard1-doc1")));
    assertEquals(1, reader.docFreq(new Term(Constants.ID, "shard1-doc2")));
    assertEquals(1, reader.docFreq(new Term(Constants.ID, "shard2-doc1")));
    assertEquals(1, reader.docFreq(new Term(Constants.ID, "shard2-doc2")));
    
    assertEquals(4, reader.docFreq(new Term(Constants.CONTENTS, "text")));
    assertEquals(4, reader.docFreq(new Term(Constants.CONTENTS, "for")));
    
    reader.close();
    dir.close();
  }
  
  @Test
  public void testValidateArguments() throws Exception {
    String[] args = new String[] { mergedDir.toString() };
    
    errorCaptor.reset();
    outputCaptor.reset();
    
    try {
      MergeShards.main(args);
    } catch (Exception e) {
      // MergeShards might exit, we'll just catch any exception
    }
    
    String errorOutput = errorCaptor.toString();
    assertTrue(errorOutput.contains("Usage: MergeShards <output_dir> <shard_dir1> [<shard_dir2> ...]"));
  }
}
