package io.anserini.ltr.feature;

import java.util.List;

public class MinPooler implements Pooler {
  public float pool(List<Float> array) {
    float min = 0;
    for (float v : array) {
      if (v < min)
        min = v;
    }
    return min;
  }

  public Pooler clone() {
    return new MinPooler();
  }

  public String getName() {
    return "min";
  }
}
