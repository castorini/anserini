package io.anserini.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class IndexerTest {
  private static final String INDEX_PATH1 = "test-index1";
  private static final String INDEX_PATH2 = "test-index2";

  // A very simple example of how to build an index.
  private void buildTestIndex() throws IOException {
    Directory dir = FSDirectory.open(Paths.get(INDEX_PATH1));

    Analyzer analyzer = new EnglishAnalyzer();
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

    IndexWriter writer = new IndexWriter(dir, config);

    FieldType textOptions = new FieldType();
    textOptions.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
    textOptions.setStored(true);
    textOptions.setTokenized(true);

    Document doc1 = new Document();
    doc1.add(new TextField("docid", "doc1", Field.Store.YES));
    doc1.add(new Field("text", "here is some text here is some more text", textOptions));
    writer.addDocument(doc1);

    Document doc2 = new Document();
    doc2.add(new TextField("docid", "doc2", Field.Store.YES));
    doc2.add(new Field("text", "more text", textOptions));
    writer.addDocument(doc2);

    Document doc3 = new Document();
    doc3.add(new TextField("docid", "doc3", Field.Store.YES));
    doc3.add(new Field("text", "here is a test", textOptions));
    writer.addDocument(doc3);

    writer.commit();
    writer.forceMerge(1);
    writer.close();
  }

  // A very simple example of how to iterate through terms in an index and dump out postings.
  private void dumpPostings(IndexReader reader) throws IOException {
    // This is how you iterate through terms in the postings list.
    LeafReader leafReader = reader.leaves().get(0).reader();
    TermsEnum termsEnum = leafReader.terms("text").iterator();
    BytesRef bytesRef = termsEnum.next();
    while (bytesRef != null) {
      // This is the current term in the dictionary.
      String token = bytesRef.utf8ToString();
      Term term = new Term("text", token);
      System.out.print(token + " (df = " + reader.docFreq(term) + "):");

      PostingsEnum postingsEnum = leafReader.postings(term);
      while (postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
        System.out.print(String.format(" (%s, %s)", postingsEnum.docID(), postingsEnum.freq()));
      }
      System.out.println("");

      bytesRef = termsEnum.next();
    }
  }

  @Test
  public void testReadingPostings() throws Exception {
    buildTestIndex();

    Directory dir = FSDirectory.open(Paths.get(INDEX_PATH1));
    IndexReader reader = DirectoryReader.open(dir);
    assertEquals(3, reader.numDocs());
    assertEquals(1, reader.leaves().size());

    System.out.println("Dumping out postings...");
    dumpPostings(reader);

    assertEquals(2, reader.docFreq(new Term("text", "here")));
    assertEquals(2, reader.docFreq(new Term("text", "more")));
    assertEquals(1, reader.docFreq(new Term("text", "some")));
    assertEquals(1, reader.docFreq(new Term("text", "test")));
    assertEquals(2, reader.docFreq(new Term("text", "text")));

    reader.close();
  }


  @Test
  public void testCloneIndex() throws Exception {
    buildTestIndex();

    System.out.println("Cloning index:");
    Directory dir1 = FSDirectory.open(Paths.get(INDEX_PATH1));
    IndexReader reader = DirectoryReader.open(dir1);

    Directory dir2 = FSDirectory.open(Paths.get(INDEX_PATH2));
    IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir2, config);

    LeafReader leafReader = reader.leaves().get(0).reader();
    CodecReader codecReader = SlowCodecReaderWrapper.wrap(leafReader);
    writer.addIndexes(new MyFilterCodecReader(codecReader));
    writer.commit();
    writer.forceMerge(1);
    writer.close();

    reader.close();

    // Open up the cloned index and verify it.
    reader = DirectoryReader.open(dir2);
    assertEquals(3, reader.numDocs());
    assertEquals(1, reader.leaves().size());

    System.out.println("Dumping out postings...");
    dumpPostings(reader);

    assertEquals(2, reader.docFreq(new Term("text", "here")));
    assertEquals(2, reader.docFreq(new Term("text", "more")));
    assertEquals(1, reader.docFreq(new Term("text", "some")));
    assertEquals(1, reader.docFreq(new Term("text", "test")));
    assertEquals(2, reader.docFreq(new Term("text", "text")));

    reader.close();
  }

  // Custom class so we can intercept calls and potentially alter behavior.
  private static class MyFilterCodecReader extends FilterCodecReader {
    final private CodecReader in;

    public MyFilterCodecReader(CodecReader in) {
      super(in);
      this.in = in;
    }

    @Override
    public FieldsProducer getPostingsReader() {
      System.out.println("Getting custom postings reader...");
      return new MyFieldsProducer(in.getPostingsReader());
    }

    @Override
    public CacheHelper getReaderCacheHelper() {
      return null;
    }

    @Override
    public CacheHelper getCoreCacheHelper() {
      return null;
    }
  }

  // Custom class so we can intercept calls and potentially alter behavior.
  private static class MyFieldsProducer extends FieldsProducer {
    final private FieldsProducer fieldsProducer;

    public MyFieldsProducer(FieldsProducer fieldsProducer) {
      this.fieldsProducer = fieldsProducer;
    }

    @Override
    public void close() throws IOException {
      fieldsProducer.close();
    }

    @Override
    public void checkIntegrity() throws IOException {
      fieldsProducer.iterator();
    }

    @Override
    public Iterator<String> iterator() {
      return fieldsProducer.iterator();
    }

    @Override
    public Terms terms(String s) throws IOException {
      System.out.println("Intercepting call to method 'terms': " + s);

      return fieldsProducer.terms(s);
    }

    @Override
    public int size() {
      return fieldsProducer.size();
    }

    @Override
    public long ramBytesUsed() {
      return fieldsProducer.ramBytesUsed();
    }

    @Override
    public Collection<Accountable> getChildResources() {
      return fieldsProducer.getChildResources();
    }
  }
}
