package io.anserini.ltr.feature;

import java.util.List;

public class VarPooler implements Pooler {
  public float pool(List<Float> array) {
    float sum = 0;
    float squareSum = 0;
    for (float v : array) {
      sum += v;
      squareSum += v * v;
    }
    return (squareSum - sum * sum) / array.size();
  }

  public Pooler clone() {
    return new VarPooler();
  }

  public String getName() {
    return "var";
  }
}
