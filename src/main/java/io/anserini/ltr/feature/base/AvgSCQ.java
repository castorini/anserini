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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;

import java.io.IOException;
import java.util.List;

/**
 * This feature computes collection query similarity, avgSCQ defined as
 * Avg( (1 + log(tf(t,D))) * idf(t)) found on page 33 of Carmel, Yom-Tov 2010
 * D is the collection term frequency
 */
public class AvgSCQ implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(AvgSCQ.class);

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
        long numDocs = context.numDocs;
        float scq = 0.0f;

        for (String token : queryContext.queryTokens) {
          long docFreq = context.getDocFreq(token);
          long termFreq = context.getCollectionFreq(token);
          if (termFreq == 0) continue;
          scq += (1+Math.log(termFreq))*Math.log(1+(numDocs-docFreq+0.5d)/(docFreq + 0.5d));
        }
        return scq/queryContext.queryTokens.size();


  }

  @Override
  public String getName() {
    return "AvgSCQ";
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    return new AvgSCQ();
  }
}
