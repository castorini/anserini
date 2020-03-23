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

package io.anserini.index.generator;

import io.anserini.collection.MultifieldSourceDocument;
import io.anserini.collection.SourceDocument;
import io.anserini.index.IndexArgs;
import io.anserini.index.IndexCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;

/**
 * Converts a {@link SourceDocument} into a Lucene {@link Document}, ready to be indexed.
 *
 * @param <T> type of the source document
 */
public class DefaultLuceneDocumentGenerator<T extends SourceDocument> extends LuceneDocumentGenerator<T> {
  private static final Logger LOG = LogManager.getLogger(DefaultLuceneDocumentGenerator.class);

  protected IndexCollection.Counters counters;
  protected IndexArgs args;

  /**
   * Default constructor.
   */
  public DefaultLuceneDocumentGenerator() {}

  /**
   * Constructor with config and counters
   *
   * @param args configuration arguments
   * @param counters counters
   */
  public DefaultLuceneDocumentGenerator(IndexArgs args, IndexCollection.Counters counters) {
    config(args);
    setCounters(counters);
  }

  public void config(IndexArgs args) {
    this.args = args;
  }

  public void setCounters(IndexCollection.Counters counters) {
    this.counters = counters;
  }

  public Document createDocument(T src) {
    String id = src.id();
    String contents = src.content();

    if (contents.trim().length() == 0) {
      counters.empty.incrementAndGet();
      return null;
    }

    // Make a new, empty document.
    final Document document = new Document();

    // Store the collection docid.
    document.add(new StringField(IndexArgs.ID, id, Field.Store.YES));
    // This is needed to break score ties by docid.
    document.add(new SortedDocValuesField(IndexArgs.ID, new BytesRef(id)));

    if (args.storeRawDocs) {
      document.add(new StoredField(IndexArgs.RAW, src.content()));
    }

    FieldType fieldType = new FieldType();
    fieldType.setStored(args.storeTransformedDocs);

    // Are we storing document vectors?
    if (args.storeDocvectors) {
      fieldType.setStoreTermVectors(true);
      fieldType.setStoreTermVectorPositions(true);
    }

    // Are we building a "positional" or "count" index?
    if (args.storePositions) {
      fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    } else {
      fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
    }

    document.add(new Field(IndexArgs.CONTENTS, contents, fieldType));

    // If this document has other fields, then we want to index it also.
    // Currently we just use all the settings of the main "content" field.
    if (src instanceof MultifieldSourceDocument) {
      ((MultifieldSourceDocument) src).fields().forEach((k, v) -> {
        document.add(new Field(k, v, fieldType));
      });
    }

    return document;
  }
}
