package io.anserini.index.transform;

import org.apache.lucene.benchmark.byTask.feeds.DemoHTMLParser;
import org.apache.lucene.benchmark.byTask.feeds.DocData;

import java.io.StringReader;

/**
 * Created by jimmylin on 3/25/17.
 */
public class LuceneHtmlParserTransform extends StringTransformation {
  private final DemoHTMLParser dhp = new DemoHTMLParser();

  @Override
  public String apply(String s) {
    try {
      DocData dd = new DocData();
      dd = dhp.parse(dd, "", null, new StringReader(s), null);
      return dd.getTitle() + "\n" + dd.getBody();
    } catch (Exception e) {
      return "";
    }
  }
}
