/*
* Anserini: A Lucene toolkit for reproducible information retrieval research
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

package io.anserini.reproduce;

import io.anserini.reproduce.RunRepro.Args;
import io.anserini.reproduce.RunRepro.TrecEvalMetricDefinitions;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

import java.util.HashMap;
import java.util.Map;

public class RunBeir {

  public static void main(String[] args) throws Exception {
    Args beirArgs = new RunRepro.Args();
    CmdLineParser parser = new CmdLineParser(beirArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException exception) {
      System.err.println(exception.getMessage());
      return;
    }

    RunRepro repro = new RunRepro("beir", new BeirMetricDefinitions(), beirArgs.printCommands, beirArgs.dryRun);
    repro.run();
  }

  public static class BeirMetricDefinitions extends TrecEvalMetricDefinitions {
    public BeirMetricDefinitions() {
      super();
      Map<String, Map<String, String>> beir = new HashMap<>();
      String[] corpora = {
          "trec-covid", "bioasq", "nfcorpus", "nq", "hotpotqa", "fiqa", "signal1m", "trec-news",
          "robust04", "arguana", "webis-touche2020", "cqadupstack-android", "cqadupstack-english",
          "cqadupstack-gaming", "cqadupstack-gis", "cqadupstack-mathematica", "cqadupstack-physics",
          "cqadupstack-programmers", "cqadupstack-stats", "cqadupstack-tex", "cqadupstack-unix",
          "cqadupstack-webmasters", "cqadupstack-wordpress", "quora", "dbpedia-entity", "scidocs",
          "fever", "climate-fever", "scifact"
      };
  
      // Populate the main map with key-value pairs
      for (String corpus : corpora) {
        Map<String, String> corpusMap = new HashMap<>();
        corpusMap.put("nDCG@10", "-c -m ndcg_cut.10");
        beir.put("beir-v1.0.0-" + corpus + ".test", corpusMap);
      }
  
      metricDefinitions.put("beir", beir);
    }
  }

}
