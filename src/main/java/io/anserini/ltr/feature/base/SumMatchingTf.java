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
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.List;

/**
 * Computes the sum of the term frequencies of the matching terms. That is, if there are two query
 * terms and the first occurs twice in the document and the second occurs once in the document, the
 * sum of the matching term frequencies is three.
 */
public class SumMatchingTf<T> implements FeatureExtractor<T> {

  @Override
  public float extract(Document doc, Terms terms, RerankerContext<T> context) {
    try {
      List<String> queryTokens = context.getQueryTokens();
      TermsEnum termsEnum = terms.iterator();
      int sum = 0;

      BytesRef text = null;
      while ((text = termsEnum.next()) != null) {
        String term = text.utf8ToString();
        if (queryTokens.contains(term)) {
          sum += (int) termsEnum.totalTermFreq();
        }
      }
      return sum;

    } catch (IOException e) {
      return 0;
    }
  }

  @Override
  public String getName() {
    return "SumMatchingTf";
  }
}
