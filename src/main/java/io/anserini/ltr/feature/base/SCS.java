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
import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;
import io.anserini.rerank.RerankerContext;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SCS = sum (P[t|q]) * log(P[t|q] / P[t|D])
 * page 20 of Carmel, Yom-Tov 2010
 */
public class SCS implements FeatureExtractor {

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    long termCount = context.totalTermFreq;
    float score = 0.0f;
    for (String token : queryContext.queryFreqs.keySet()) {
      float prtq = queryContext.queryFreqs.get(token) / (float) queryContext.querySize;
      long tf = context.getCollectionFreq(token);
      float prtd = (float)tf/termCount;
      if (prtd == 0) continue;
      score += prtq*Math.log(prtq/prtd);
    }
    return score;
  }

  @Override
  public String getName() {
    return "SCS";
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    return new SCS();
  }
}
