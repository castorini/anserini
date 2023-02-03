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

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.ann.IndexDenseAnnVectors;
import io.anserini.collection.SourceDocument;
import io.anserini.index.IndexDenseVectors;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

/**
 * Converts a {@link SourceDocument} into a Lucene {@link Document}, ready to be indexed.
 *
 * @param <T> type of the source document
 */
public class LuceneAnnVectorDocumentGenerator<T extends SourceDocument> implements LuceneDocumentGenerator<T> {
  protected IndexDenseAnnVectors.Args args;

  protected LuceneAnnVectorDocumentGenerator() {
  }

  /**
   * Constructor with config and counters
   *
   * @param args configuration arguments
   */
  public LuceneAnnVectorDocumentGenerator(IndexDenseAnnVectors.Args args) {
    this.args = args;
  }

  private float[] convertJsonArray(String vectorString) throws JsonMappingException, JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    ArrayList<Float> denseVector = mapper.readValue(vectorString, new TypeReference<ArrayList<Float>>(){});
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
    // Make a new, empty document.
    final Document document = new Document();

    // Store the collection docid.
    document.add(new StringField(IndexDenseVectors.Args.ID, id, Field.Store.YES));
    document.add(new TextField(IndexDenseVectors.Args.VECTOR, sb.toString(), args.stored ? Field.Store.YES : Field.Store.NO));
    if (args.storeRaw) {
      document.add(new StoredField(IndexDenseVectors.Args.RAW, src.raw()));
    }
    return document;
  }
}
