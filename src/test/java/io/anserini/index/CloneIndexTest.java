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

package io.anserini.index;

import io.anserini.IndexerTestBase;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.index.CodecReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FilterCodecReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.SlowCodecReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Accountable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

public class CloneIndexTest extends IndexerTestBase {
  private static Path tempDir2;

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    tempDir2 = createTempDir();
  }

  @After
  @Override
  public void tearDown() throws Exception {
    // Call garbage collector for Windows compatibility
    System.gc();
    super.tearDown();
  }

  @Test
  public void testCloneIndex() throws Exception {
    tempDir2 = createTempDir();

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
    assertEquals(2, reader.docFreq(new Term("contents", "here")));
    assertEquals(2, reader.docFreq(new Term("contents", "more")));
    assertEquals(1, reader.docFreq(new Term("contents", "some")));
    assertEquals(1, reader.docFreq(new Term("contents", "test")));
    assertEquals(2, reader.docFreq(new Term("contents", "text")));

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
    public IndexReader.CacheHelper getCoreCacheHelper() {
      throw new UnsupportedOperationException();
    }

    @Override
    public IndexReader.CacheHelper getReaderCacheHelper() {
      throw new UnsupportedOperationException();
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
