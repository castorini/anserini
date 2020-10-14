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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import java.io.IOException;
import java.util.List;

/**
 * Average IDF, idf calculated using log( 1+ (N - N_t + 0.5)/(N_t + 0.5))
 * where N is the total number of docs, calculated like in BM25
 */
public class AvgIDFFeatureExtractor implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(AvgIDFFeatureExtractor.class);

  private float sumIdf(IndexReader reader, List<String> queryTokens,
                       long numDocs, String field) throws IOException {
    float sumIdf = 0.0f;
    for(String token : queryTokens) {
      int docFreq = reader.docFreq(new Term(field, token));
      sumIdf += Math.log(1 + (numDocs - docFreq + 0.5d) / (docFreq + 0.5d));
    }
    return sumIdf;
  }

  @Override
  public float extract(ContentContext context, String queryText, List<String> queryTokens) {
    float sumIdf = 0.0f;
    long numDocs = context.docSize;
    for(String queryToken : queryTokens) {
      long docFreq = context.getCollectionFreq(queryToken);
      sumIdf += Math.log(1 + (numDocs - docFreq + 0.5d) / (docFreq + 0.5d));
    }
    return sumIdf / (float) queryTokens.size();
  }

  @Override
  public String getName() {
    return "AvgIDF";
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    return new AvgIDFFeatureExtractor();
  }
}
