package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.FeatureExtractor;
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
public class SimplifiedClarityFeatureExtractor implements FeatureExtractor{

  private String lastQueryProcessed = "";
  private float lastComputedScore = 0.0f;

  private Map<String, Integer> queryTermMap(List<String> queryTokens) {
    Map<String, Integer> map = new HashMap<>();
    for (String token : queryTokens) {
      if (map.containsKey(token)) {
        map.put(token, map.get(token) + 1);
      } else {
        map.put(token, 1);
      }
    }
    return map;
  }

  private float sumSC(IndexReader reader, Map<String, Integer> queryTokenMap,
                      int queryLength, String field) throws IOException {

    long termCount = reader.getSumTotalTermFreq(field);
    // We now have a doc size, compute the actual value
    float score = 0.0f;
    for (String token : queryTokenMap.keySet()) {
      float prtq = queryTokenMap.get(token) / (float) queryLength;
      long tf = reader.totalTermFreq(new Term(field, token));
      float prtd = (float)tf /termCount;
      if (prtd == 0 || prtq == 0) continue;
      score += prtq * Math.log(prtq / prtd);
    }
    return score;
  }

  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {

    if (!this.lastQueryProcessed.equals(context.getQueryText())) {
      this.lastQueryProcessed = context.getQueryText();
      this.lastComputedScore = 0.0f;

      Map<String, Integer> queryCountMap = queryTermMap(context.getQueryTokens());
      try {
        float score = sumSC(context.getIndexSearcher().getIndexReader(),
                queryCountMap, context.getQueryTokens().size(),
                context.getField());
        this.lastComputedScore = score;
      } catch (IOException e) {
        this.lastComputedScore = 0.0f;
      }
    }

    return this.lastComputedScore;
  }

  @Override
  public String getName() {
    return "SimplifiedClarityScore";
  }
}
