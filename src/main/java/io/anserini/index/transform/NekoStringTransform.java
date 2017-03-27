/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.index.transform;

import org.apache.lucene.benchmark.byTask.feeds.DemoHTMLParser;
import org.apache.lucene.benchmark.byTask.feeds.DocData;

import java.io.StringReader;

/**
 * String transform that uses Lucene's Demo HTML Parser to extract plain text out of HTML
 * documents. According to the documentation, the parser is based on NekoHTML.
 */
public class NekoStringTransform extends StringTransform {
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
