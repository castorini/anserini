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

package io.anserini.index.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.collection.SourceDocument;
import io.anserini.index.Constants;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.KnnFloatVectorField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.VectorSimilarityFunction;
import org.apache.lucene.util.BytesRef;

import java.util.ArrayList;

/**
 * Converts a {@link SourceDocument} into a Lucene {@link Document}, ready to be indexed.
 *
 * @param <T> type of the source document
 */
public class DenseVectorDocumentGenerator<T extends SourceDocument> implements LuceneDocumentGenerator<T> {
  public DenseVectorDocumentGenerator() {
  }

  private float[] convertJsonArray(String vectorString) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    ArrayList<Float> denseVector = mapper.readValue(vectorString, new TypeReference<>(){});

    int length = denseVector.size();
    float[] vector = new float[length];
    for (int i=0; i<length; i++) {
      vector[i] = denseVector.get(i);
    }

    return vector;
  }

  @Override
  public Document createDocument(T src) throws InvalidDocumentException {
    String id = src.id();
    float[] contents;

    try {
      contents = convertJsonArray(src.contents());
    } catch (Exception e) {
      throw new InvalidDocumentException();
    }

    // Make a new, empty document.
    final Document document = new Document();

    // Store the collection docid.
    document.add(new StringField(Constants.ID, id, Field.Store.YES));
    // This is needed to break score ties by docid.
    document.add(new BinaryDocValuesField(Constants.ID, new BytesRef(id)));

    document.add(new KnnFloatVectorField(Constants.VECTOR, contents, VectorSimilarityFunction.DOT_PRODUCT));

    return document;
  }
}
