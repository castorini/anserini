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

public class RunBright {

  public static void main(String[] args) throws Exception {
    Args brightArgs = new RunRepro.Args();
    CmdLineParser parser = new CmdLineParser(brightArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException exception) {
      System.err.println(exception.getMessage());
      return;
    }

    RunRepro repro = new RunRepro("bright", new BrightMetricDefinitions(), brightArgs.printCommands, brightArgs.dryRun, brightArgs.computeIndexSize);
    repro.run();
  }

  public static class BrightMetricDefinitions extends TrecEvalMetricDefinitions {
    public BrightMetricDefinitions() {
      super();
      Map<String, Map<String, String>> bright = new HashMap<>();
      String[] corpora = {
          "biology", "earth-science", "economics", "psychology", "robotics", "stackoverflow", 
          "sustainable-living", "leetcode", "pony", "aops", "theoremqa-theorems", "theoremqa-questions"
      };
  
      // Populate the main map with key-value pairs
      for (String corpus : corpora) {
        Map<String, String> corpusMap = new HashMap<>();
        corpusMap.put("nDCG@10", "-c -m ndcg_cut.10");
        bright.put("bright-" + corpus, corpusMap);
      }
  
      metricDefinitions.put("bright", bright);
    }
  }

}
