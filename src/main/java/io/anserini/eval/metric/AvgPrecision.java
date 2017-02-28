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

package io.anserini.eval.metric;

import io.anserini.eval.ResultDoc;

import java.util.List;
import java.util.Map;

/**
 * Average Precision
 */
public class AvgPrecision extends MetricBase {
  @Override
  public String getName() {
    return "ap";
  }

  @Override
  public double evaluate(List<ResultDoc> resultList, Map<String, Integer> judgments) {
    // first check whether there are at least one rel doc in the judgments
    int num_rel = (int)new RelCount().evaluate(resultList, judgments);
    if (num_rel == 0) {
      return 0.0;
    }

    double res = 0.0;
    int totalRelCount = 0;

    for (int i = 0; i < resultList.size(); i++) {
      ResultDoc doc = resultList.get(i);
      String docid = doc.getDocid();
      if (judgments.containsKey(docid) && (judgments.get(docid) > 0)) {
        totalRelCount++;
        res += (double)totalRelCount / (i+1);
      }
    }
    return res / num_rel;
  }

  @Override
  public String getFormat() {
    return ".4f";
  }
}
