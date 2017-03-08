package io.anserini.passage;

import java.util.List;

/**
 * Created by royalsequeira on 2017-03-07.
 */

public interface PassageScorer {
  void score(List<String> sentences);
}
