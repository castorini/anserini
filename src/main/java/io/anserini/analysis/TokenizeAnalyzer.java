package io.anserini.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class TokenizeAnalyzer extends Analyzer {

  @Override
  protected TokenStreamComponents createComponents(String fieldName) {
    StandardTokenizer source = new StandardTokenizer();
    return new TokenStreamComponents(source, source);
  }
}
