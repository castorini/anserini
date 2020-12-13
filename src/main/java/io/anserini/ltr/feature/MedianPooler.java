package io.anserini.ltr.feature;

import java.util.Collections;
import java.util.List;

public class MedianPooler implements Pooler {
  public float pool(List<Float> array) {
    Collections.sort(array);
    int mid = array.size() / 2;
    if (array.size() % 2 == 0) {
      return (array.get(mid - 1) + array.get(mid)) / 2;
    } else {
      return array.get(mid) / 2;
    }
  }

  public Pooler clone() {
    return new MedianPooler();
  }

  public String getName() {
    return "median";
  }
}
