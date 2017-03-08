package io.anserini.qa.passage;

import java.util.List;

public interface PassageScorer {
  void score(List<String> sentences, String index, String output, Context context) throws Exception;
}
