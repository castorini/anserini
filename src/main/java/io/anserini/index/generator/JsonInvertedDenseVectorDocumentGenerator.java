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
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.util.BytesRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Converts a {@link SourceDocument} into a Lucene {@link Document}.
 *
 * @param <T> type of the source document
 */
public class JsonInvertedDenseVectorDocumentGenerator<T extends SourceDocument> implements LuceneDocumentGenerator<T> {
  private static final Logger LOG = LogManager.getLogger(JsonDenseVectorDocumentGenerator.class);

  public JsonInvertedDenseVectorDocumentGenerator() {
  }

  @Override
  public Document createDocument(T src) throws InvalidDocumentException {
    String id = src.id();
    try {
      float[] contents = src.vector();

      if (contents == null) {
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
    // Store the collection docid.
    document.add(new StringField(Constants.ID, id, Field.Store.YES));
    // This is needed to break score ties by docid.
    document.add(new BinaryDocValuesField(Constants.ID, new BytesRef(id)));

      document.add(new TextField(Constants.VECTOR, sb.toString(), Field.Store.NO));

      return document;
    } catch (InvalidDocumentException e) {
      throw e;
    } catch (Exception e) {
      LOG.error("Unexpected error creating document for ID: " + id, e);
      throw new InvalidDocumentException();
    }
  }
}