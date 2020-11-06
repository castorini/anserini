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

package io.anserini.ltr.feature;

import io.anserini.rerank.RerankerContext;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A feature extractor.
 */
public interface FeatureExtractor {

  /**
   * @param doc the document we work on
   * @param terms a iterator to the term vector of the content field
   * @param queryText original query text
   * @param queryTokens tokenized query text
   * @param reader in case the extractor need some global information
   * @return feature value
   */
  float extract(Document doc, Terms terms, String queryText, List<String> queryTokens, IndexReader reader);

  /**
   * we need to make sure each thread has a thread-local copy of extractors
   * otherwise we will have concurrency problems
   * @return a copy with the same set up
   */
  FeatureExtractor clone();

  /**
   * used for tell the corresponding feature name for each column in the feature vector
   * @return feature name
   */
  String getName();

  /**
   * @return the field this feature extractor needs to load
   */
  String getField();

}
