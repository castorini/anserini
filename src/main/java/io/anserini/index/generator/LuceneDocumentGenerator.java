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
import io.anserini.collection.StringTransform;
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
 * Prior to the creation of the Lucene document, this class will apply an optional
 * {@link StringTransform} to, for example, clean HTML document.
 *
 * @param <T> type of the source document
 */
public interface LuceneDocumentGenerator<T extends SourceDocument> {
  Document createDocument(T src);
}
