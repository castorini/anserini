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

package io.anserini.index.codecs;
import java.io.IOException;

import org.apache.lucene.codecs.KnnFieldVectorsWriter;
import org.apache.lucene.codecs.KnnVectorsFormat;
import org.apache.lucene.codecs.KnnVectorsReader;
import org.apache.lucene.codecs.KnnVectorsWriter;
import org.apache.lucene.codecs.hnsw.DefaultFlatVectorScorer;
import org.apache.lucene.codecs.hnsw.FlatVectorsFormat;
import org.apache.lucene.codecs.hnsw.FlatVectorsReader;
import org.apache.lucene.codecs.hnsw.FlatVectorsWriter;
import org.apache.lucene.codecs.lucene99.Lucene99FlatVectorsFormat;
import org.apache.lucene.index.ByteVectorValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FloatVectorValues;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.Sorter;
import org.apache.lucene.search.KnnCollector;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.hnsw.OrdinalTranslatedKnnCollector;
import org.apache.lucene.util.hnsw.RandomVectorScorer;

public class AnseriniLucene99FlatVectorFormat extends KnnVectorsFormat {

  static final String NAME = "AnseriniLucene99FlatVectorFormat";

  private final FlatVectorsFormat format = new Lucene99FlatVectorsFormat(new DefaultFlatVectorScorer());

  /**
   * Sole constructor
   */
  public AnseriniLucene99FlatVectorFormat() {
    super(NAME);
  }

  @Override
  public KnnVectorsWriter fieldsWriter(SegmentWriteState state) throws IOException {
    return new AnseriniLucene99FlatVectorWriter(format.fieldsWriter(state));
  }

  @Override
  public KnnVectorsReader fieldsReader(SegmentReadState state) throws IOException {
    return new AnseriniLucene99FlatVectorReader(format.fieldsReader(state));
  }

  public static class AnseriniLucene99FlatVectorWriter extends KnnVectorsWriter {

    private final FlatVectorsWriter writer;

    public AnseriniLucene99FlatVectorWriter(FlatVectorsWriter writer) {
      super();
      this.writer = writer;
    }

    @Override
    public KnnFieldVectorsWriter<?> addField(FieldInfo fieldInfo) throws IOException {
      return writer.addField(fieldInfo);
    }

    @Override
    public void flush(int maxDoc, Sorter.DocMap sortMap) throws IOException {
      writer.flush(maxDoc, sortMap);
    }

    @Override
    public void finish() throws IOException {
      writer.finish();
    }

    @Override
    public void close() throws IOException {
      writer.close();
    }

    @Override
    public long ramBytesUsed() {
      return writer.ramBytesUsed();
    }

    @Override
    public void mergeOneField(FieldInfo fieldInfo, MergeState mergeState) throws IOException {
      writer.mergeOneField(fieldInfo, mergeState);
    }
  }

  public static class AnseriniLucene99FlatVectorReader extends KnnVectorsReader {

    private final FlatVectorsReader reader;

    public AnseriniLucene99FlatVectorReader(FlatVectorsReader reader) {
      super();
      this.reader = reader;
    }

    @Override
    public void checkIntegrity() throws IOException {
      reader.checkIntegrity();
    }

    @Override
    public FloatVectorValues getFloatVectorValues(String field) throws IOException {
      return reader.getFloatVectorValues(field);
    }

    @Override
    public ByteVectorValues getByteVectorValues(String field) throws IOException {
      return reader.getByteVectorValues(field);
    }

    @Override
    public void search(String field, float[] target, KnnCollector knnCollector, Bits acceptDocs) throws IOException {
      collectAllMatchingDocs(knnCollector, acceptDocs, reader.getRandomVectorScorer(field, target));
    }

    private void collectAllMatchingDocs(KnnCollector knnCollector, Bits acceptDocs, RandomVectorScorer scorer) throws IOException {
      OrdinalTranslatedKnnCollector collector = new OrdinalTranslatedKnnCollector(knnCollector, scorer::ordToDoc);
      Bits acceptedOrds = scorer.getAcceptOrds(acceptDocs);
      for (int i = 0; i < scorer.maxOrd(); i++) {
        if (acceptedOrds == null || acceptedOrds.get(i)) {
          collector.collect(i, scorer.score(i));
          collector.incVisitedCount(1);
        }
      }
      assert collector.earlyTerminated() == false;
    }

    @Override
    public void search(String field, byte[] target, KnnCollector knnCollector, Bits acceptDocs) throws IOException {
      collectAllMatchingDocs(knnCollector, acceptDocs, reader.getRandomVectorScorer(field, target));
    }

    @Override
    public void close() throws IOException {
      reader.close();
    }

    @Override
    public long ramBytesUsed() {
      return reader.ramBytesUsed();
    }
  }
}