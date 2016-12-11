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

package io.anserini.eval;

import io.anserini.eval.metric.MetricBase;
import io.anserini.eval.metric.MetricFactory;

import java.util.*;

/**
 * The abstract class for all batch eval.
 * batch eval applies to a set of queries
 */
public class BatchEval {
  protected String metricName;
  protected MetricBase metricBase;
  protected Map<String, Double> evals;
  protected double aggregated;

  public BatchEval(String metricName) {
    this.metricName = metricName;
    metricBase = MetricFactory.instance(metricName);
    evals = new TreeMap<>();
  }

  public String getMetricName() {
    return metricName;
  }

  public MetricBase getMetricBase() {
    return metricBase;
  }

  public Map<String, Double> getEvals() {
    return evals;
  }

  public double getAggregated() {
    return aggregated;
  }

  /*
      *
      * evaluate a bunch of queries
      */
  public Map<String, Double> evaluate(Map<String, List<ResultDoc>> resultLists, Map<String, Map<String, Integer>> judgments) {
    evals.clear();
    for (String query : resultLists.keySet()) {
      List<ResultDoc> qResList = resultLists.get(query);
      Map<String, Integer> qJudge = judgments.get(query);
      if (qResList != null && qJudge != null) {
        double score = metricBase.evaluate(qResList, qJudge);
        evals.put(query, score);
      }
    }

    if (metricName.equals("num_ret") || metricName.equals("num_rel") || metricName.equals("num_rel_ret")) {
      aggregated = evals.values().stream().mapToDouble(a->a).sum();
    } else {
      aggregated = evals.values().stream().mapToDouble(a->a).average().getAsDouble();
    }

    return evals;
  }

  public Map<String, Double> evaluate(RankingResults resultLists, QueryJudgments judgments) {
    return evaluate(resultLists.getRankingList(), judgments.getQrels());
  }

  public String getFormat() {
    return metricBase.getFormat();
  }
}
