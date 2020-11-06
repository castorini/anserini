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

  protected ArrayList<Integer> gapSizes = new ArrayList<>();
  protected Map<Integer, Map<String, Integer>> counters = new HashMap<>();

  protected Map<String, Integer> singleCountMap = new HashMap<>();
  protected Map<String, Set<String>> queryPairMap = new HashMap<>();
  protected Map<String, Set<String>> backQueryPairMap = new HashMap<>();
  protected Document lastProcessedDoc = null;

  private void resetCounters(Document newestDoc) {

    singleCountMap.clear();
    backQueryPairMap.clear();
    queryPairMap.clear();
    for (int i : counters.keySet()) {
      counters.get(i).clear();
    }
    lastProcessedDoc = newestDoc;
  }

  protected int gapSize;

  // If this windowSize is 2, then we will look at a window [i-2, i+2] for the second term if the first occurs at i
  public UnorderedSequentialPairsFeatureExtractor(int gapSize) {
    this.gapSize= gapSize;

    counters.put(gapSize, new HashMap<>());
    gapSizes.add(gapSize);
  }

  /**
   * Method will dictate which pairs of tokens we will count for
   * can be overridden for different implementations, ei consecutive pairs, or all
   * pairs
   * @param queryTokens query tokens
   */
  protected void populateQueryMaps(List<String> queryTokens) {
    // Construct a count map and a map of phrase pair x y, x->y
    for (int i = 0; i < queryTokens.size() - 1; i++) {
      if (queryPairMap.containsKey(queryTokens.get(i))) {
        queryPairMap.get(queryTokens.get(i)).add(queryTokens.get(i+1));
      } else {
        queryPairMap.put(queryTokens.get(i), new HashSet<>(Arrays.asList(queryTokens.get(i + 1))));
      }

      if (backQueryPairMap.containsKey(queryTokens.get(i+1))) {
        backQueryPairMap.get(queryTokens.get(i+1)).add(queryTokens.get(i));
      } else {
        backQueryPairMap.put(queryTokens.get(i + 1), new HashSet<>(Arrays.asList(queryTokens.get(i))));
      }
      // This will serve as our smoothing param
      singleCountMap.put(queryTokens.get(i), 0);
    }
    singleCountMap.put(queryTokens.get(queryTokens.size() -1), 0);
  }

  protected float computeUnorderedFrequencyScore(Document doc, Terms terms, List<String> queryTokens) throws IOException {

    if (doc != lastProcessedDoc) {
      resetCounters(doc);

      populateQueryMaps(queryTokens);

      CountBigramPairs.countPairs(singleCountMap, queryPairMap, backQueryPairMap,gapSizes, counters, terms);
    }

    float score = 0.0f;
    Map<String, Integer> phraseCountMap = counters.get(gapSize);
    // Smoothing count of 1
    for (String queryToken : queryPairMap.keySet()) {
      float countToUse = phraseCountMap.getOrDefault(queryToken, 0);
      score += countToUse;
    }

    return score;
  }

  @Override
  public float extract(Document doc, Terms terms, String queryText, List<String> queryTokens, IndexReader reader) {
    try {
      return computeUnorderedFrequencyScore(doc, terms, queryTokens);
    } catch (IOException e) {
      e.printStackTrace();
      return 0.0f;
    }
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
