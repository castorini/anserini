package io.anserini.qa.passage;

import java.util.List;

public interface PassageScorer {

  void score(List<String> sentences, String output) throws Exception;

  List<ScoredPassage> extractTopPassages();
}
