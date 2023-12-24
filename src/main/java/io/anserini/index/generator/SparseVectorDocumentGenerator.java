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

import io.anserini.collection.InvalidContentsException;
import io.anserini.collection.SourceDocument;
import io.anserini.collection.SourceSparseVectorDocument;
import io.anserini.index.Constants;
import io.anserini.index.IndexCollection;
import org.apache.lucene.document.*;
import org.apache.lucene.util.BytesRef;

import java.util.Map;

/**
 * Converts a {@link SourceDocument} into a Lucene {@link Document}, ready to be indexed.
 *
 * @param <T> type of the source document
 */
public class SparseVectorDocumentGenerator<T extends SourceSparseVectorDocument & SourceDocument> implements LuceneDocumentGenerator<T> {
  protected IndexCollection.Args args;

  protected SparseVectorDocumentGenerator() {

  }
  /**
   * Constructor with config and counters
   *
   * @param args configuration arguments
   */
  public SparseVectorDocumentGenerator(IndexCollection.Args args) {
    this.args = args;
  }

  @Override
  public Document createDocument(T src) throws GeneratorException {
    String id = src.id();
    Map<String, Float> vector;
    try {
      vector = src.vector();
    } catch (InvalidContentsException e) {
      // Catch and rethrow; indexer will eat the exception at top level and increment counters accordingly.
      throw new InvalidDocumentException();
    }

    if (vector.size() == 0) {
      throw new EmptyDocumentException();
    }

    // Make a new, empty document.
    final Document document = new Document();

    // Store the collection docid.
    document.add(new StringField(Constants.ID, id, Field.Store.YES));
    // This is needed to break score ties by docid.
    document.add(new BinaryDocValuesField(Constants.ID, new BytesRef(id)));

    if (args.storeRaw) {
      document.add(new StoredField(Constants.RAW, src.raw()));
    }
    for (String term : vector.keySet()){
      document.add(new FeatureField(Constants.CONTENTS, term, vector.get(term)));
    }
    return document;
  }
}
