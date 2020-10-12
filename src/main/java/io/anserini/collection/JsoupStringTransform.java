/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.collection;

import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

/**
 * String transform that uses Jsoup to extract plain text out of HTML documents.
 */
public class JsoupStringTransform extends StringTransform {
  // Singleton instance for convenience.
  public final static JsoupStringTransform SINGLETON = new JsoupStringTransform();

  @Override
  public String apply(String s) {
    return Jsoup.parse(s).text();
  }

  /**
   * Cleans HTML (removes <a> tags and lines with a single word) and preserves lines.
   * @param s content of HTML
   * @return cleaned
   */
  public String clean(String s) {
    Document doc = Jsoup.parse(s);
    doc.select("a, script").remove();
    doc.select("br").append("\n");
    doc.select("p").prepend("\n\n");
    String str = doc.html();
    str = Jsoup.clean(str, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    str = str.replaceAll("(?m)^[ \\t]*\\r?\\n", "\n").replaceAll("[\r\n]+", "\n").replaceAll("\\n\\s+", "\n");
    str = str.replaceAll("(?m)^(\\S+)[\\s]+$", "").replaceAll("[\r\n]+", "\n"); // remove lines with single word

    return str;
  }
}
