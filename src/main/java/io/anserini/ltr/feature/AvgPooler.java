package io.anserini.ltr.feature;

import java.util.List;

public class AvgPooler implements Pooler {
  public float pool(List<Float> array) {
    float sum = 0;
    for (float v : array) {
      sum += v;
    }
    return sum / array.size();
  }

  public Pooler clone() {
    return new AvgPooler();
  }

  public String getName() {
    return "avg";
  }
}
