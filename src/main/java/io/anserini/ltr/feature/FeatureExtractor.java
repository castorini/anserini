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

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A feature extractor.
 */
public interface FeatureExtractor {

  float extract(DocumentContext context, QueryContext queryContext) throws FileNotFoundException, IOException;

  float postEdit(DocumentContext context, QueryContext queryContext);

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

  /**
   * @return the query field this feature extractor needs to load
   */
  String getQField();

}
