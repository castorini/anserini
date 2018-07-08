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

package io.anserini.document;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * A Wikipedia paragraph. The article title and paragraph index together serve as an id.
 */

public class WikipediaParagraph extends WikipediaArticle {
  protected final int paragraphIndex;

  public WikipediaParagraph(String title, int paragraphIndex, String contents) {
    super(title, contents);
    this.paragraphIndex = paragraphIndex;
  }

  @Override
  public String id() {
    return title + "-" + String.valueOf(paragraphIndex);
  }

  public int paragraphIndex() {
    return paragraphIndex;
  }
}
