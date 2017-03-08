package io.anserini.qa.passage;

import java.util.Map;

public class Context {
  private Map<String, Double> sentenceScore;
g
  public Context(){
    sentenceScore = null;
  }

  public void setState(Map score){
    this.sentenceScore = score;
  }

  public Map<String, Double> getState(){
    return sentenceScore;
  }
}
