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
import io.anserini.index.IndexInvertedDenseVectors;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.util.ArrayList;

/**
 * Converts a {@link SourceDocument} into a Lucene {@link Document}.
 *
 * @param <T> type of the source document
 */
public class InvertedDenseVectorDocumentGenerator<T extends SourceDocument> implements LuceneDocumentGenerator<T> {
  protected IndexInvertedDenseVectors.Args args;

  protected InvertedDenseVectorDocumentGenerator() {
  }

  /**
   * Constructor with config and counters
   *
   * @param args configuration arguments
   */
  public InvertedDenseVectorDocumentGenerator(IndexInvertedDenseVectors.Args args) {
    this.args = args;
  }

  private float[] convertJsonArray(String vectorString) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    ArrayList<Float> denseVector = mapper.readValue(vectorString, new TypeReference<>() {});
    int length = denseVector.size();
    float[] vector = new float[length];
    int i = 0;
    for (Float f : denseVector) {
      vector[i++] = f;
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

    StringBuilder sb = new StringBuilder();
    for (double fv : contents) {
      if (sb.length() > 0) {
        sb.append(' ');
      }
      sb.append(fv);
    }

    final Document document = new Document();
    document.add(new StringField(IndexInvertedDenseVectors.FIELD_ID, id, Field.Store.YES));
    document.add(new TextField(IndexInvertedDenseVectors.FIELD_VECTOR, sb.toString(), Field.Store.NO));

    return document;
  }
}