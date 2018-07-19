/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.collection;

import java.io.BufferedReader;

/**
 * A raw document from a collection to be indexed. We explicitly distinguish a source document from
 * a Lucene document, which is the Lucene representation that is ready to be indexed.
 */
public interface SourceDocument {
  /**
   * Returns the unique identifier of the document.
   *
   * @return the unique identifier of the document
   */
  String id();

  /**
   * Returns the raw content of the document.
   *
   * @return the raw content of the document
   */
  String content();

  /**
   * Returns whether this document is meant to be indexed. Certain collections (e.g., ClueWeb)
   * contained metadata records that aren't meant to be indexed.
   *
   * @return <code>true</code> if this document is meant to be indexed
   */
  boolean indexable();


  /**
   *We assume that one source document contains multiple records.
   * These records are also of type SourceDocument and are split by
   * type-dependent delimiters.
   *
   * @param reader A file BufferedReader to be iterated
   * @return the next record
   * @throws Exception Any exception
   */
  SourceDocument readNextRecord(BufferedReader reader) throws Exception;
}
