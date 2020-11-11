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

package io.anserini.ltr.feature.twitter;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;

import java.util.List;

public class HashtagCount implements FeatureExtractor {

  @Override
  public float extract(Document doc, Terms terms, String queryText, List<String> queryTokens, IndexReader reader) {
    String str = doc.getField(IndexArgs.CONTENTS).stringValue();
    final String matchStr = "#";

    int lastIndex = 0;
    int count = 0;

    while (lastIndex != -1) {
      lastIndex = str.indexOf(matchStr, lastIndex);
      if (lastIndex != -1) {
        count++;
        lastIndex += matchStr.length();
      }
    }

    return (float) count;
  }

  @Override
  public String getName() {
    return "TwitterHashtagCount";
  }

  @Override
  public String getField() {
    return IndexArgs.CONTENTS;
  }

  @Override
  public FeatureExtractor clone() {
    return new HashtagCount();
  }
}
