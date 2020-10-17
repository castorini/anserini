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
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Average Inverse DocumentCollection Term Frequency as defined in
 * Carmel, Yom-Tov Estimating query difficulty for Information Retrieval
 * log(|D| / tf)
 */
public class AvgICTF implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(AvgICTF.class);

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    // We need docSize, and tf for each term
    long docSize = context.docSize;
    float sumIctf = 0;
    for(String queryToken: queryContext.queryTokens) {
      long tf = context.getTermFreq(queryToken);
      if(tf!=0)
        sumIctf += Math.log((double)docSize/tf);
    }
    // Compute the average by dividing
    return sumIctf / queryContext.queryTokens.size();
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
    return new AvgICTF();
  }
}
