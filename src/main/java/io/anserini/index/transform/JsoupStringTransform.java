package io.anserini.index.transform;

import org.jsoup.Jsoup;

/**
 * Created by jimmylin on 3/25/17.
 */
public class JsoupStringTransform extends StringTransformation {
  @Override
  public String apply(String s) {
    return Jsoup.parse(s).text();
  }
}
