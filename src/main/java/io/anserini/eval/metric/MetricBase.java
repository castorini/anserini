/**
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

package io.anserini.eval.metric;

import io.anserini.eval.ResultDoc;

import java.util.List;
import java.util.Map;

/**
 * The abstract class for all metrics.
 * This applies to a single query.
 */
public abstract class MetricBase {
  public MetricBase() {}

  /*
  * get the metric name
  */
  public abstract String getName();
  /*
  *
  * evaluate a single query
  */
  public abstract double evaluate(List<ResultDoc> resultList, Map<String, Integer> judgments);

  /*
  * the format of print string
  */
  public abstract String getFormat();
}
