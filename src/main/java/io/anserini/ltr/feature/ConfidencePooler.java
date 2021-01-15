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
    //todo need discuss this
    if(qlen == 0) return 0;
    double avg = sum / qlen;
    double std = Math.sqrt(Math.max(squareSum / array.size() - avg * avg,0f));
    //q.tfidf_confidence = ZETA * (q.tfidf_std_dev / (sqrt(q.len_stopped)));
    float interval = (float) (1.96 * (std / Math.sqrt(qlen)));
    return interval;
  }

  public Pooler clone() {
    return new ConfidencePooler();
  }

  public String getName() {
    return "confidence";
  }
}
