package io.anserini.qa.passage;

public class ScoredPassage implements Comparable<ScoredPassage> {
  String sentence;
  double score;

  public ScoredPassage(String sentence, double score) {
    this.sentence = sentence;
    this.score = score;
  }

  public String getSentence() {
    return sentence;
  }

  public double getScore() {
    return score;
  }

  @Override
  public int compareTo(ScoredPassage o) {
    if(score > o.score) {
      return -1;
    } else if(score < o.score) {
      return 1;
    } else {
      return 0;
    }
  }
}
