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
import io.anserini.rerank.RerankerContext;
import org.apache.lucene.document.Document;
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
 * Counts all unordered pairs of query tokens
 */
public class UnorderedQueryPairsFeatureExtractor<T> implements FeatureExtractor<T> {
  protected static ArrayList<Integer> gapSizes = new ArrayList<>();
  protected static Map<Integer, CountBigramPairs.PhraseCounter> counters = new HashMap<>();

  protected static Map<String, Integer> singleCountMap = new HashMap<>();
  protected static Map<String, Set<String>> queryPairMap = new HashMap<>();
  protected static Map<String, Set<String>> backQueryPairMap = new HashMap<>();
  protected static String lastProcessedId = "";
  protected static Document lastProcessedDoc = null;

  public static class Deserializer extends StdDeserializer<UnorderedQueryPairsFeatureExtractor>
  {
    public Deserializer() {
      this(null);
    }

    public Deserializer(Class<?> vc) {
      super(vc);
    }

    @Override
    public UnorderedQueryPairsFeatureExtractor
    deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException
    {
      JsonNode node = jsonParser.getCodec().readTree(jsonParser);
      int gapSize = node.get("gapSize").asInt();
      return new UnorderedQueryPairsFeatureExtractor(gapSize);
    }
  }

  private static void resetCounters(String newestQuery, Document newestDoc) {

    singleCountMap.clear();
    backQueryPairMap.clear();
    queryPairMap.clear();
    for (int i : counters.keySet()) {
      counters.get(i).phraseCountMap.clear();
    }
    lastProcessedId = newestQuery;
    lastProcessedDoc = newestDoc;
  }

  protected int gapSize;

  // If this windowSize is 2, then we will look at a window [i-2, i+2] for the second term if the first occurs at i
  public UnorderedQueryPairsFeatureExtractor(int gapSize) {
    this.gapSize= gapSize;

    counters.put(gapSize, new CountBigramPairs.PhraseCounter());
    gapSizes.add(gapSize);
  }

  protected void populateQueryMaps(List<String> queryTokens) {
    for (int i = 0; i < queryTokens.size() - 1; i++) {
      for (int j = i + 1; j < queryTokens.size(); j ++) {
        if (queryPairMap.containsKey(queryTokens.get(i))) {
          queryPairMap.get(queryTokens.get(i)).add(queryTokens.get(j));
        } else {
          queryPairMap.put(queryTokens.get(i), new HashSet<>(Arrays.asList(queryTokens.get(j))));
        }

        if (backQueryPairMap.containsKey(queryTokens.get(j))) {
          backQueryPairMap.get(queryTokens.get(j)).add(queryTokens.get(i));
        } else {
          backQueryPairMap.put(queryTokens.get(j), new HashSet<>(Arrays.asList(queryTokens.get(i))));
        }
      }
      // This will serve as our smoothing param
      singleCountMap.put(queryTokens.get(i), 0);
    }
    singleCountMap.put(queryTokens.get(queryTokens.size() - 1), 0);

  }
  protected float computeUnorderedFrequencyScore(Document doc, Terms terms, RerankerContext<T> context) throws IOException {

    if (!context.getQueryId().equals(lastProcessedId) || doc != lastProcessedDoc) {
      resetCounters(context.getQueryId().toString(), doc);
      List<String> queryTokens = context.getQueryTokens();

      populateQueryMaps(queryTokens);

      CountBigramPairs.countPairs(singleCountMap, queryPairMap, backQueryPairMap, gapSizes, counters, terms);
    }

    float score = 0.0f;
    Map<String, Integer> phraseCountMap = counters.get(gapSize).phraseCountMap;
    // Smoothing count of 1
    for (String queryToken : queryPairMap.keySet()) {
      float countToUse = phraseCountMap.getOrDefault(queryToken, 0);
      score += countToUse;
    }

    return score;
  }
  @Override
  public float extract(Document doc, Terms terms, RerankerContext<T> context) {
    try {
      return computeUnorderedFrequencyScore(doc, terms, context);
    } catch (IOException e) {
      e.printStackTrace();
      return 0.0f;
    }
  }

  @Override
  public String getName() {
    return "UnorderedQueryTokenPairs" + this.gapSize;
  }
}
