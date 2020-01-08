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

package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;

import java.io.IOException;

/**
 * Returns the size of the document
 */
public class DocSizeFeatureExtractor<T> implements FeatureExtractor<T> {
  private static final Logger LOG = LogManager.getLogger(DocSizeFeatureExtractor.class);

  @Override
  public float extract(Document doc, Terms terms, RerankerContext<T> context) {
    float score;
    try {
      score = (float)terms.getSumTotalTermFreq();
      if (score == -1) {
        // try to iterate over the terms
        TermsEnum termsEnum = terms.iterator();
        score = 0.0f;
        while (termsEnum.next()!= null) {
          score += termsEnum.totalTermFreq();
        }
      }
    } catch (IOException e) {
      score = 0.0f;
    }
    return score;
  }

  @Override
  public String getName() {
    return "DocSize";
  }
}
