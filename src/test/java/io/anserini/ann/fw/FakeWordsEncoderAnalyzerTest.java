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

package io.anserini.ann.fw;

import io.anserini.analysis.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CommonTermsQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;
import static org.junit.Assert.assertEquals;

public class FakeWordsEncoderAnalyzerTest {

  @Test
  public void testBinaryFVIndexAndSearch() throws Exception {
    FakeWordsEncoderAnalyzer analyzer = new FakeWordsEncoderAnalyzer(30);
    Directory directory = new ByteBuffersDirectory();
    IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer));
    DirectoryReader reader = null;
    try {
      List<Double> values = new LinkedList<>();
      values.add(0.1d);
      values.add(0.3d);
      values.add(0.5d);
      values.add(0.7d);
      values.add(0.11d);
      values.add(0.13d);
      values.add(0.17d);
      values.add(0.19d);
      values.add(0.23d);
      values.add(0.29d);

      byte[] bytes = toByteArray(values);
      String fvString = toDoubleString(bytes);

      String fieldName = "fvs";
      Document document = new Document();
      document.add(new TextField(fieldName, fvString, Field.Store.YES));
      writer.addDocument(document);
      writer.commit();

      reader = DirectoryReader.open(writer);
      assertSimQuery(analyzer, fieldName, fvString, reader);
    } finally {
      if (reader != null) {
        reader.close();
      }
      writer.close();
      directory.close();
    }
  }

  private void assertSimQuery(Analyzer analyzer, String fieldName, String text, DirectoryReader reader) throws IOException {
    IndexSearcher searcher = new IndexSearcher(reader);
    CommonTermsQuery simQuery = new CommonTermsQuery(SHOULD, SHOULD, 1);
    for (String token : AnalyzerUtils.tokenize(analyzer, text)) {
      simQuery.add(new Term(fieldName, token));
    }
    TopDocs topDocs = searcher.search(simQuery, 1);
    assertEquals(1, topDocs.totalHits.value);
  }

  private byte[] toByteArray(List<Double> values) {
    int blockSize = Double.SIZE / Byte.SIZE;
    byte[] bytes = new byte[values.size() * blockSize];
    for (int i = 0, j = 0; i < values.size(); i++, j += blockSize) {
      ByteBuffer.wrap(bytes, j, blockSize).putDouble(values.get(i));
    }
    return bytes;
  }

  private String toDoubleString(byte[] bytes) {
    double[] a = toDoubleArray(bytes);
    StringBuilder builder = new StringBuilder();
    for (Double d : a) {
      if (builder.length() > 0) {
        builder.append(' ');
      }
      builder.append(d);
    }
    return builder.toString();
  }

  private double[] toDoubleArray(byte[] array) {
    int blockSize = Double.SIZE / Byte.SIZE;
    ByteBuffer wrap = ByteBuffer.wrap(array);
    int capacity = array.length / blockSize;
    double[] doubles = new double[capacity];
    for (int i = 0; i < capacity; i++) {
      double e = wrap.getDouble(i * blockSize);
      doubles[i] = e;
    }
    return doubles;
  }

}