package io.anserini.ltr.feature;

import java.util.List;

public class MaxPooler implements Pooler {
  public float pool(List<Float> array) {
    float max = 0;
    for (float v : array) {
      if (v > max)
        max = v;
    }
    return max;
  }

  public Pooler clone() {
    return new MaxPooler();
  }

  public String getName() {
    return "max";
  }
}
