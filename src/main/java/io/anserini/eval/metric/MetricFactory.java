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

public class MetricFactory {

  public static MetricBase instance(String metric) {
    String lower = metric.toLowerCase();
    switch (lower) {
      case "num_ret":
        return new RetCount();
      case "num_rel":
        return new RelCount();
      case "num_rel_ret":
        return new RelRetCount();
      case "map":
      case "ap":
        return new AvgPrecision();
    }

    if(lower.startsWith("p") || lower.startsWith("ndcg") ) {
      int cutoff = Integer.MAX_VALUE;
      if (lower.contains(".")) {
        cutoff = Integer.parseInt(lower.split("\\.")[1]);
      }
      if (lower.startsWith("p")) {
        return new Precision(cutoff);
      } else if (lower.startsWith("ndcg")) {
        return new NDCG(cutoff);
      }
    }

    throw new RuntimeException("Metric " + metric + " is not a valid metric.");
  }
}
