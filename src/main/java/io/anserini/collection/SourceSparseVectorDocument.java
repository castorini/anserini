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

package io.anserini.collection;

import java.util.Map;

/**
 * A raw document from a collection. A {@code SourceDocument} is explicitly distinguish a from a
 * Lucene {@link org.apache.lucene.document.Document}, which is the Lucene representation that
 * can be directly inserted into an index.
 */
public interface SourceSparseVectorDocument {
  /**
   * Return the vector containing term and weight
   * @return a map that map term to weight
   */
  Map<String, Float> vector();
}
