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

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Average IDF, idf calculated using log( 1+ (N - N_t + 0.5)/(N_t + 0.5))
 * where N is the total number of docs, calculated like in BM25
 */
public class AvgIDFFeatureExtractor implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(AvgIDFFeatureExtractor.class);

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    if(queryContext.cache.containsKey(getName())){
      return queryContext.cache.get(getName());
    } else {
      float sumIdf = 0.0f;
      long numDocs = context.docSize;
      for(String queryToken : queryContext.queryTokens) {
        long docFreq = context.getCollectionFreq(queryToken);
        sumIdf += Math.log(1 + (numDocs - docFreq + 0.5d) / (docFreq + 0.5d));
      }
      float avgIdf = sumIdf / (float) queryContext.queryTokens.size();
      queryContext.cache.put(getName(), avgIdf);
      return avgIdf;
    }
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
