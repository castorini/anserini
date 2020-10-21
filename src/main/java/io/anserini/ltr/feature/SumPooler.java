package io.anserini.ltr.feature;

import java.util.List;

public class SumPooler implements Pooler {
  public float pool(List<Float> array) {
    float sum = 0;
    for (float v : array) {
      sum += v;
    }
    return sum;
  }

  public Pooler clone() {
    return new SumPooler();
  }

  public String getName() {
    return "sum";
  }
}
