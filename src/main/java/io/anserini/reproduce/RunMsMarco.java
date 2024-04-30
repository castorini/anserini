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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import io.anserini.reproduce.RunRepro.TrecEvalMetricDefinitions;

public class RunMsMarco {
  public static class Args {
    @Option(name = "-options", usage = "Print information about options.")
    public Boolean options = false;
    @Option(name = "-v", metaVar = "[int]", usage = "MsMarco Version (1/2), where 2 is for V2.1. Default 1.")
    public int MsMarcoVersion = 1;
  }

  public static void main(String[] args) throws Exception {

    // check for cmd option
    String COLLECTION = "msmarco-v1-passage";
    Args MsMarcoArgs = new Args();
    CmdLineParser parser = new CmdLineParser(MsMarcoArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      if (MsMarcoArgs.options) {
        System.err.printf("Options for %s:\n\n", RunMsMarco.class.getSimpleName());
        parser.printUsage(System.err);

        List<String> required = new ArrayList<>();
        parser.getOptions().forEach((option) -> {
          if (option.option.required()) {
            required.add(option.option.toString());
          }
        });

        System.err.printf("\nRequired options are %s\n", required);
      } else {
        System.err.printf("Error: %s. For help, use \"-options\" to print out information about options.\n", e.getMessage());
      }

      return;
    }
    switch (MsMarcoArgs.MsMarcoVersion) {
        case 2:
            COLLECTION = "msmarco-v2.1-doc";
            break;
        default: // MsMarcoVersion == 1
            COLLECTION = "msmarco-v1-passage";
            break;
    }

    RunRepro repro = new RunRepro(COLLECTION, new MsMarcoMetricDefinitions());
    repro.run();
  }

  public static class MsMarcoMetricDefinitions extends TrecEvalMetricDefinitions {
    public MsMarcoMetricDefinitions() {
      super();

      Map<String, Map<String, String>> msmarcoV1Passage = new HashMap<>();
  
      // msmarco-v1-passage definitions
      Map<String, String> msmarcoDevSubsetMetrics = new HashMap<>();
      msmarcoDevSubsetMetrics.put("MRR@10", "-c -M 10 -m recip_rank");
      msmarcoDevSubsetMetrics.put("R@1K", "-c -m recall.1000");
      msmarcoV1Passage.put("msmarco-passage.dev-subset",
          msmarcoDevSubsetMetrics);
  
      Map<String, String> dl19PassageMetrics = new HashMap<>();
      dl19PassageMetrics.put("MAP", "-c -l 2 -m map");
      dl19PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl19PassageMetrics.put("R@1K", "-c -l 2 -m recall.1000");
      msmarcoV1Passage.put("dl19-passage", dl19PassageMetrics);

      Map<String, String> dl20PassageMetrics = new HashMap<>();
      dl20PassageMetrics.put("MAP", "-c -l 2 -m map");
      dl20PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl20PassageMetrics.put("R@1K", "-c -l 2 -m recall.1000");
      msmarcoV1Passage.put("dl20-passage", dl20PassageMetrics);

      metricDefinitions.put("msmarco-v1-passage", msmarcoV1Passage);
  
      Map<String, Map<String, String>> msmarcoV2Passage = new HashMap<>();
  
      // msmarco-v2.1-doc definitions
      Map<String, String> msmarco2Dev1Metrics = new HashMap<>();
      msmarco2Dev1Metrics.put("MRR@10", "-c -M 100 -m recip_rank");
      msmarcoV2Passage.put("msmarco-v2.1-doc.dev",
          msmarco2Dev1Metrics);
      
      Map<String, String> msmarco2Dev2Metrics = new HashMap<>();
      msmarco2Dev2Metrics.put("MRR@10", "-c -M 100 -m recip_rank");
      msmarcoV2Passage.put("msmarco-v2.1-doc.dev2",
          msmarco2Dev2Metrics);
  
      Map<String, String> dl21PassageMetrics = new HashMap<>();
      dl21PassageMetrics.put("MAP", "-c -M 100 -m map");
      dl21PassageMetrics.put("MRR@10", "-c -M 100 -m recip_rank");
      dl21PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl21PassageMetrics.put("R@100", "-c -m recall.100");
      dl21PassageMetrics.put("R@1K", "-c -m recall.1000");
      msmarcoV2Passage.put("dl21-doc-msmarco-v2.1", dl21PassageMetrics);
  
      Map<String, String> dl22PassageMetrics = new HashMap<>();
      dl22PassageMetrics.put("MAP", "-c -M 100 -m map");
      dl22PassageMetrics.put("MRR@10", "-c -M 100 -m recip_rank");
      dl22PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl22PassageMetrics.put("R@100", "-c -m recall.100");
      dl22PassageMetrics.put("R@1K", "-c -m recall.1000");
      msmarcoV2Passage.put("dl22-doc-msmarco-v2.1", dl22PassageMetrics);
  
      Map<String, String> dl23PassageMetrics = new HashMap<>();
      dl23PassageMetrics.put("MAP", "-c -M 100 -m map");
      dl23PassageMetrics.put("MRR@10", "-c -M 100 -m recip_rank");
      dl23PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl23PassageMetrics.put("R@100", "-c -m recall.100");
      dl23PassageMetrics.put("R@1K", "-c -m recall.1000");
      msmarcoV2Passage.put("dl23-doc-msmarco-v2.1", dl23PassageMetrics);
  
      metricDefinitions.put("msmarco-v2.1-doc", msmarcoV2Passage);
    }
  }

}
