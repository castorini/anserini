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

import org.apache.lucene.codecs.KnnFieldVectorsWriter;
import org.apache.lucene.codecs.KnnVectorsFormat;
import org.apache.lucene.codecs.KnnVectorsReader;
import org.apache.lucene.codecs.KnnVectorsWriter;
import org.apache.lucene.codecs.lucene99.Lucene99ScalarQuantizedVectorsFormat;
import org.apache.lucene.index.ByteVectorValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FloatVectorValues;
import org.apache.lucene.index.KnnVectorValues;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.Sorter;
import org.apache.lucene.index.VectorSimilarityFunction;
import org.apache.lucene.search.AcceptDocs;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.KnnCollector;
import org.apache.lucene.util.Bits;

import java.io.IOException;

public class Anserini20FlatScalarQuantizedVectorsFormat extends KnnVectorsFormat {

  static final String NAME = "Anserini20FlatScalarQuantizedVectorsFormat";

  private final KnnVectorsFormat format = new Lucene99ScalarQuantizedVectorsFormat();

  /**
   * Sole constructor
   */
  public Anserini20FlatScalarQuantizedVectorsFormat() {
    super(NAME);
  }

  @Override
  public int getMaxDimensions(String fieldName) {
    return 4096;
  }

  @Override
  public KnnVectorsWriter fieldsWriter(SegmentWriteState state) throws IOException {
    return new Anserini20FlatScalarQuantizedVectorsWriter(format.fieldsWriter(state));
  }

  @Override
  public KnnVectorsReader fieldsReader(SegmentReadState state) throws IOException {
    return new Anserini20FlatScalarQuantizedVectorsReader(format.fieldsReader(state));
  }

  public static class Anserini20FlatScalarQuantizedVectorsWriter extends KnnVectorsWriter {

    private final KnnVectorsWriter writer;

    public Anserini20FlatScalarQuantizedVectorsWriter(KnnVectorsWriter writer) {
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

  public static class Anserini20FlatScalarQuantizedVectorsReader extends KnnVectorsReader {

    private final KnnVectorsReader reader;

    public Anserini20FlatScalarQuantizedVectorsReader(KnnVectorsReader reader) {
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
    public void search(String field, float[] target, KnnCollector knnCollector, AcceptDocs acceptDocs) throws IOException {
      FloatVectorValues vectors = reader.getFloatVectorValues(field);
      if (vectors == null) {
        return;
      }
      VectorSimilarityFunction similarity = VectorSimilarityFunction.DOT_PRODUCT;
      FloatVectorValues vectorValues = vectors.copy();
      KnnVectorValues.DocIndexIterator it = vectorValues.iterator();
      Bits bits = acceptDocs == null ? null : acceptDocs.bits();
      for (int doc = it.nextDoc(); doc != DocIdSetIterator.NO_MORE_DOCS; doc = it.nextDoc()) {
        if (bits == null || bits.get(doc)) {
          int ord = it.index();
          float score = similarity.compare(target, vectorValues.vectorValue(ord));
          knnCollector.collect(doc, score);
        }
        knnCollector.incVisitedCount(1);
      }
    }

    @Override
    public void search(String field, byte[] target, KnnCollector knnCollector, AcceptDocs acceptDocs) throws IOException {
      ByteVectorValues vectors = reader.getByteVectorValues(field);
      if (vectors == null) {
        return;
      }
      VectorSimilarityFunction similarity = VectorSimilarityFunction.DOT_PRODUCT;
      ByteVectorValues vectorValues = vectors.copy();
      KnnVectorValues.DocIndexIterator it = vectorValues.iterator();
      Bits bits = acceptDocs == null ? null : acceptDocs.bits();
      for (int doc = it.nextDoc(); doc != DocIdSetIterator.NO_MORE_DOCS; doc = it.nextDoc()) {
        if (bits == null || bits.get(doc)) {
          int ord = it.index();
          float score = similarity.compare(target, vectorValues.vectorValue(ord));
          knnCollector.collect(doc, score);
        }
        knnCollector.incVisitedCount(1);
      }
    }

    @Override
    public void close() throws IOException {
      reader.close();
    }
  }
}
