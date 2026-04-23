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

package io.anserini.index;

import io.anserini.collection.SourceDocument;

import java.util.concurrent.atomic.AtomicLong;

public final class Counters {
  /**
   * Counter for successfully indexed documents.
   */
  public AtomicLong indexed = new AtomicLong();

  /**
   * Counter for empty documents that are not indexed. Empty documents are not necessary errors;
   * it could be the case, for example, that a document is comprised solely of stopwords.
   */
  public AtomicLong empty = new AtomicLong();

  /**
   * Counter for unindexable documents. These are cases where {@link SourceDocument#indexable()}
   * returns false.
   */
  public AtomicLong unindexable = new AtomicLong();

  /**
   * Counter for skipped documents. These are cases documents are skipped as part of normal
   * processing logic, e.g., using a whitelist, not indexing retweets or deleted tweets.
   */
  public AtomicLong skipped = new AtomicLong();

  /**
   * Counter for unexpected errors.
   */
  public AtomicLong errors = new AtomicLong();
}