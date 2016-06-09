package io.anserini.ltr.feature;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.anserini.rerank.RerankerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;

import java.io.IOException;
import java.lang.reflect.Type;
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

  protected static ArrayList<Integer> gapSizes = new ArrayList<>();
  protected static Map<Integer, CountBigramPairs.PhraseCounter> counters = new HashMap<>();

  protected static Map<String, Integer> singleCountMap = new HashMap<>();
  protected static Map<String, Set<String>> queryPairMap = new HashMap<>();
  protected static String lastProcessedId = "";
  protected static Document lastProcessedDoc = null;

  public static class Deserializer implements JsonDeserializer<OrderedQueryPairsFeatureExtractor>
  {
    @Override
    public OrderedQueryPairsFeatureExtractor
    deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
      int gapSize = ((JsonObject) json).get("gapSize").getAsInt();
      return new OrderedQueryPairsFeatureExtractor(gapSize);
    }
  }

  public OrderedQueryPairsFeatureExtractor(int gapSize) {
    this.gapSize = gapSize;
    // Add a window to the counters
    counters.put(gapSize, new CountBigramPairs.PhraseCounter());
    gapSizes.add(gapSize);
  }

  private static void resetCounters(String newestQuery, Document newestDoc) {
    singleCountMap.clear();
    queryPairMap.clear();
    for (int i : counters.keySet()) {
      counters.get(i).phraseCountMap.clear();
    }
    lastProcessedId = newestQuery;
    lastProcessedDoc = newestDoc;
  }

  protected int gapSize;

  protected void populateQueryPairMap(List<String> queryTokens) {
    // Construct a count map and a map of phrase pair x y, x->y
    for (int i = 0; i < queryTokens.size() - 1; i++) {
      Set<String> secondTokens = new HashSet<>();
      for (int j = i +1; j < queryTokens.size(); j++) {
        secondTokens.add(queryTokens.get(j));
      }
      queryPairMap.put(queryTokens.get(i), secondTokens);
      singleCountMap.put(queryTokens.get(i), 0);
    }
  }

  protected float computeOrderedFrequencyScore(Document doc, Terms terms, RerankerContext context) throws IOException {

    // Only compute the score once for all window sizes on the same document
    if (!context.getQueryId().equals(lastProcessedId) || lastProcessedDoc != doc) {
      resetCounters(context.getQueryId(), doc);

      List<String> queryTokens = context.getQueryTokens();
      populateQueryPairMap(queryTokens);

      // Now make the call to the static method
      CountBigramPairs.countPairs(singleCountMap, queryPairMap, gapSizes, counters, terms);
    }

    float score = 0.0f;
    // Smoothing count of 1
    Map<String, Integer> phraseCountMap = counters.get(this.gapSize).phraseCountMap;
    for (String queryToken : queryPairMap.keySet()) {
      float countToUse = phraseCountMap.containsKey(queryToken) ? phraseCountMap.get(queryToken) : 0;
      score += countToUse;
    }

    return score;
  }

  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {
    try {
      return computeOrderedFrequencyScore(doc, terms, context);
    } catch (IOException e) {
      LOG.error("IOException, returning 0.0f");
      return 0.0f;
    }
  }

  @Override
  public String getName() {
    return "OrderedAllPairs" + this.gapSize;
  }
}
