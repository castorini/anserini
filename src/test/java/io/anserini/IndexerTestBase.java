package io.anserini;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.LuceneTestCase;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Path;

public class IndexerTestBase extends LuceneTestCase {
  protected static Path tempDir1;

  // A very simple example of how to build an index.
  private void buildTestIndex() throws IOException {
    Directory dir = FSDirectory.open(tempDir1);

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
    doc1.add(new StringField("id", "doc1", Field.Store.YES));
    doc1.add(new SortedDocValuesField("id", new BytesRef("doc1".getBytes())));
    doc1.add(new Field("contents", "here is some text here is some more text", textOptions));
    writer.addDocument(doc1);

    Document doc2 = new Document();
    doc2.add(new StringField("id", "doc2", Field.Store.YES));
    doc2.add(new SortedDocValuesField("id", new BytesRef("doc2".getBytes())));
    doc2.add(new Field("contents", "more text", textOptions));
    writer.addDocument(doc2);

    Document doc3 = new Document();
    doc3.add(new StringField("id", "doc3", Field.Store.YES));
    doc3.add(new SortedDocValuesField("id", new BytesRef("doc3".getBytes())));
    doc3.add(new Field("contents", "here is a test", textOptions));
    writer.addDocument(doc3);

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
    // Call garbage collector for Windows compatibility
    System.gc();
    super.tearDown();
  }
}
