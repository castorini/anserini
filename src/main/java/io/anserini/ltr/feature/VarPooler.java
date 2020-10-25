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
    float avg = sum / array.size();
    return (squareSum / array.size() - avg * avg);
  }

  public Pooler clone() {
    return new VarPooler();
  }

  public String getName() {
    return "var";
  }
}
