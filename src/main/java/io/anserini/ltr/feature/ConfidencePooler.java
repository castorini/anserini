package io.anserini.ltr.feature;

import java.util.List;

public class ConfidencePooler implements Pooler  {
  public float pool(List<Float> array) {
    double sum = 0;
    double squareSum = 0;
    for (float v : array) {
      sum += v;
      squareSum += v * v;
    }
    double qlen = array.size();
    double avg = sum / qlen;
    double std = Math.sqrt(squareSum / array.size() - avg * avg);
    //q.tfidf_confidence = ZETA * (q.tfidf_std_dev / (sqrt(q.len_stopped)));
    return (float) (1.96 * (std / Math.sqrt(qlen)));
  }

  public Pooler clone() {
    return new ConfidencePooler();
  }

  public String getName() {
    return "confidence";
  }
}
