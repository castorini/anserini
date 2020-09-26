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

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.mockito.internal.matchers.Null;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Average Inverse DocumentCollection Term Frequency as defined in
 * Carmel, Yom-Tov Estimating query difficulty for Information Retrieval
 * log(|D| / tf)
 */
public class AvgICTFFeatureExtractor implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(AvgICTFFeatureExtractor.class);

  // Calculate term frequencies, if error returns an empty map, couting all tf = 0
  private float getSumICTF(Terms terms, List<String> queryTokens) {
    float sumICTF = 0.0f;
    float docSize = 0.0f;
    List<Long> termFreqs = new ArrayList<>();
    try {
      TermsEnum termsEnum = terms.iterator();
      while (termsEnum.next() != null) {
        String termString = termsEnum.term().utf8ToString();
        docSize += termsEnum.totalTermFreq();
        if (queryTokens.contains(termString) && termsEnum.totalTermFreq() > 0) {
          termFreqs.add(termsEnum.totalTermFreq());
        }
      }
    } catch (IOException e) {
      LOG.warn("Error retrieving term frequencies");
      return 0.0f;
    }

    for (Long termFreq : termFreqs) {
      sumICTF += Math.log(docSize/termFreq);
    }
    return sumICTF;
  }
  @Override
  public float extract(Document doc, Terms terms, String queryText, List<String> queryTokens, IndexReader reader) {
    // We need docSize, and tf for each term
    float sumIctf = getSumICTF(terms, queryTokens);
    // Compute the average by dividing
    return sumIctf / queryTokens.size();
  }

  @Override
  public String getName() {
    return "AvgICTF";
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    return new AvgICTFFeatureExtractor();
  }
}
