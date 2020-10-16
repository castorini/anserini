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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.anserini.index.IndexArgs;
import io.anserini.rerank.RerankerContext;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Counts occurrences of all pairs of query tokens
 */
public class OrderedQueryPairsFeatureExtractor implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(OrderedQueryPairsFeatureExtractor.class);
  private int gapSize;

  public OrderedQueryPairsFeatureExtractor(int gapSize) {
    this.gapSize = gapSize;
  }
  public float extract(ContentContext context, QueryContext queryContext) {
    float count = 0;
    List<Pair<String, String>> queryPairs= queryContext.genQueryPair();
    for(Pair<String, String> pair: queryPairs){
      count += context.CountBigram(pair.getLeft(),pair.getRight(),gapSize);
    }
    return count;
  }

  @Override
  public String getName() {
    return "OrderedAllPairs" + this.gapSize;
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    return new OrderedQueryPairsFeatureExtractor(this.gapSize);
  }
}
