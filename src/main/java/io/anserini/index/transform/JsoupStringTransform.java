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

package io.anserini.index.transform;

import org.jsoup.Jsoup;

/**
 * String transform that uses Jsoup to extract plain text out of HTML documents.
 */
public class JsoupStringTransform extends StringTransform {
  @Override
  public String apply(String s) {
    return Jsoup.parse(s).text();
  }
}
