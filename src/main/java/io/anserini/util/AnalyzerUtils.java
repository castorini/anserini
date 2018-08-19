package io.anserini.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class AnalyzerUtils {
  static public List<String> tokenize(Analyzer analyzer, String s) {
    List<String> list = new ArrayList<>();

    try {
      TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(s));
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
    } catch (IOException e) {
      e.printStackTrace();
    }

    return list;
  }
}
