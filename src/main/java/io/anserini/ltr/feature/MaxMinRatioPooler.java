package io.anserini.ltr.feature;

import java.util.List;

public class MaxMinRatioPooler implements Pooler {
  public float pool(List<Float> array) {
    float max = 1;
    float min = 1;
    for (float v : array) {
      if (v > max)
        max = v;
      if (v < max)
        min = v;
    }
    //gamma2  return max / min;
    return max/min;
  }

  public Pooler clone() {
    return new MaxMinRatioPooler();
  }

  public String getName() {
    return "maxminratio";
  }
}
