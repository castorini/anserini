package io.anserini.analysis;

import junit.framework.JUnit4TestAdapter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BertAnalyzerTest {
  Object[][] examples = new Object[][]{
      {"Ṣé Wàá Fọkàn sí Àwọn Ohun Tá A Ti Kọ Sílẹ̀?",
      new String[] {"se", "wa", "##a", "fo", "##kan", "si", "awon", "oh", "##un", "ta", "a", "ti", "ko", "sile", "?"} }
  };
  
  @Test
  public void basic() throws Exception {
    Analyzer analyzer = new BertAnalyzer();
    
    for (int i = 0; i < examples.length; i++) {
      verify((String[]) examples[i][1], parseKeywords(analyzer, (String) examples[i][0]));
    }
  }
  
  public void verify(String[] truth, List<String> tokens) {
    assertEquals(truth.length, tokens.size());
    for ( int i=0; i<truth.length; i++) {
      assertEquals(truth[i], tokens.get(i));
    }
  }
  
  public List<String> parseKeywords(Analyzer analyzer, String keywords) throws IOException {
    List<String> list = new ArrayList<>();
    
    TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(keywords));
    CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
    tokenStream.reset();
    while (tokenStream.incrementToken()) {
      if (cattr.toString().length() == 0) {
        continue;
      }
      list.add(cattr.toString());
    }
    tokenStream.end();
    tokenStream.close();
    
    return list;
  }
  
  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(TweetTokenizationTest.class);
  }
}
