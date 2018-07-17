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

package io.anserini.integration;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CodecReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FilterCodecReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.SlowCodecReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.LuceneTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

public class IndexerTest extends LuceneTestCase {

  private static Path tempDir1;
  private static Path tempDir2;

  // A very simple example of how to build an index.
  private void buildTestIndex() throws IOException {
    Directory dir = FSDirectory.open(tempDir1);

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

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();

    tempDir1 = createTempDir();
    tempDir2 = createTempDir();
    buildTestIndex();
  }

  @After
  @Override
  public void tearDown() throws Exception {
    super.tearDown();
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
    Directory dir = FSDirectory.open(tempDir1);
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
    System.out.println("Cloning index:");
    Directory dir1 = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir1);

    Directory dir2 = FSDirectory.open(tempDir2);
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
