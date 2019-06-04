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

package io.anserini.eval;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

public class EvalArgs {

  // required arguments
  @Option(name = "-run", metaVar = "[path]", required = true, usage = "The TREC formatted result file")
  public String runPath;

  @Option(name = "-qrels", metaVar = "[path]", required = true, usage = "Path to the qrels file")
  public String qrelPath;

  // optional arguments
  @Option(name = "-m", handler = StringArrayOptionHandler.class, usage = "The metric to be printed. Valid ones are: "
          +"[num_ret|num_rel|num_rel_ret|map|p[.cutoff]|ndcg[.cutoff]]. "
          +"Several metrics can be printed at once - use space to separate them. "
          +"Use \".\" to indicate the cutoff parameter for p (precision), ndcg. "
          +" For example, -m map p.30 ndcg.20")
  public String[] reqMetrics = new String[] {
      "num_ret", "num_rel", "num_rel_ret", "map",
      "p.5", "p.10", "p.20", "p.30",
      "ndcg.10", "ndcg.20"
  };

  @Option(name = "-q", handler = BooleanOptionHandler.class,
      usage = "In additional to print the average performance over all query topics, also " +
          "print the per query performance")
  public boolean printPerQuery = false;

  @Option(name = "-longdocids", handler = BooleanOptionHandler.class,
      usage = "Boolean switch to trun on the option that parses docids into Long type. " +
          "This is useful for Tweets since we can order the docs by their Long docids (which are " +
          "essentially timestamps)")
  public boolean longDocids = false;

  @Option(name = "-asc", handler = BooleanOptionHandler.class,
      usage = "Boolean switch to order docid in ascending order when there are scores tie. " +
          "Default is descending order")
  public boolean asc = false;
}
