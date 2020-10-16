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
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a feature extractor that will calculate the
 * unordered count of phrases in the window specified
 */
public class UnorderedSequentialPairsFeatureExtractor implements FeatureExtractor {
  protected int gapSize;

  // If this windowSize is 2, then we will look at a window [i-2, i+2] for the second term if the first occurs at i
  public UnorderedSequentialPairsFeatureExtractor(int gapSize) {
    this.gapSize= gapSize;
  }

  public float extract(ContentContext context, QueryContext queryContext) {
    float count = 0;
    List<Pair<String, String>> queryPairs= queryContext.genQueryBigram();
    for(Pair<String, String> pair: queryPairs){
      count += context.CountBigram(pair.getLeft(),pair.getRight(),gapSize);
      count += context.CountBigram(pair.getRight(),pair.getLeft(),gapSize);
    }
    return count;
  }

  @Override
  public String getName() {
    return "UnorderedSequentialPairs" + this.gapSize;
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    return new UnorderedSequentialPairsFeatureExtractor(this.gapSize);
  }
}
