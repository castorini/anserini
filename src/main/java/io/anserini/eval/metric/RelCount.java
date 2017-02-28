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
 * get the total number of relevant docs for a query
 */
public class RelCount extends MetricBase {
  @Override
  public String getName() {
    return "num_rel";
  }

  @Override
  public double evaluate(List<ResultDoc> resultList, Map<String, Integer> judgments) {
    int res = 0;
    for (int grade : judgments.values()) {
      if (grade > 0) {
        res++;
      }
    }
    return res;
  }

  @Override
  public String getFormat() {
    return ".0f";
  }
}
