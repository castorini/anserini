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

import io.anserini.reproduce.RunRepro.TrecEvalMetricDefinitions;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.util.*;

public class RunMsMarco {
  public static class Args extends RunRepro.Args {
    @Option(name = "-collection", usage = "MS MARCO version {'msmarco-v1-passage' (default), 'msmarco-v2.1-doc', 'msmarco-v2.1-doc-segmented'}.")
    public String MsMarcoVersion = "msmarco-v1-passage";
  }

  public static void main(String[] args) throws Exception {
    // check for cmd option
    Args msmarcoArgs = new Args();
    CmdLineParser parser = new CmdLineParser(msmarcoArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      if (msmarcoArgs.options) {
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

    Set<String> allowedVersions = new HashSet<>(
            Arrays.asList("msmarco-v1-passage", "msmarco-v2.1-doc", "msmarco-v2.1-doc-segmented"));
    if (!allowedVersions.contains(msmarcoArgs.MsMarcoVersion)) {
        System.err.println("Invalid MS MARCO version: " + msmarcoArgs.MsMarcoVersion);
        System.exit(1);
    }

    RunRepro repro = new RunRepro(msmarcoArgs.MsMarcoVersion, new MsMarcoMetricDefinitions(),
            msmarcoArgs.printCommands, msmarcoArgs.dryRun, msmarcoArgs.computeIndexSize);
    repro.run();
  }

  public static class MsMarcoMetricDefinitions extends TrecEvalMetricDefinitions {
    public MsMarcoMetricDefinitions() {
      super();

      Map<String, Map<String, String>> msmarco_v1_passage = new HashMap<>();
  
      // msmarco-v1-passage definitions
      Map<String, String> msmarcoDevSubsetMetrics = new HashMap<>();
      msmarcoDevSubsetMetrics.put("MRR@10", "-c -M 10 -m recip_rank");
      msmarcoDevSubsetMetrics.put("R@1K", "-c -m recall.1000");
      msmarco_v1_passage.put("msmarco-passage.dev", msmarcoDevSubsetMetrics);
  
      Map<String, String> dl19PassageMetrics = new HashMap<>();
      dl19PassageMetrics.put("MAP", "-c -l 2 -m map");
      dl19PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl19PassageMetrics.put("R@1K", "-c -l 2 -m recall.1000");
      msmarco_v1_passage.put("dl19-passage", dl19PassageMetrics);

      Map<String, String> dl20PassageMetrics = new HashMap<>();
      dl20PassageMetrics.put("MAP", "-c -l 2 -m map");
      dl20PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl20PassageMetrics.put("R@1K", "-c -l 2 -m recall.1000");
      msmarco_v1_passage.put("dl20-passage", dl20PassageMetrics);

      metricDefinitions.put("msmarco-v1-passage", msmarco_v1_passage);
  
      Map<String, Map<String, String>> msmarco_v21_doc = new HashMap<>();
  
      // msmarco-v2.1-doc definitions
      Map<String, String> msmarco2Dev1Metrics = new HashMap<>();
      msmarco2Dev1Metrics.put("MRR@10", "-c -M 100 -m recip_rank");
      msmarco_v21_doc.put("msmarco-v2.1-doc.dev", msmarco2Dev1Metrics);
      
      Map<String, String> msmarco2Dev2Metrics = new HashMap<>();
      msmarco2Dev2Metrics.put("MRR@10", "-c -M 100 -m recip_rank");
      msmarco_v21_doc.put("msmarco-v2.1-doc.dev2", msmarco2Dev2Metrics);
  
      Map<String, String> dl21PassageMetrics = new HashMap<>();
      dl21PassageMetrics.put("MAP", "-c -M 100 -m map");
      dl21PassageMetrics.put("MRR@10", "-c -M 100 -m recip_rank");
      dl21PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl21PassageMetrics.put("R@100", "-c -m recall.100");
      dl21PassageMetrics.put("R@1K", "-c -m recall.1000");
      msmarco_v21_doc.put("dl21-doc-msmarco-v2.1", dl21PassageMetrics);
  
      Map<String, String> dl22PassageMetrics = new HashMap<>();
      dl22PassageMetrics.put("MAP", "-c -M 100 -m map");
      dl22PassageMetrics.put("MRR@10", "-c -M 100 -m recip_rank");
      dl22PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl22PassageMetrics.put("R@100", "-c -m recall.100");
      dl22PassageMetrics.put("R@1K", "-c -m recall.1000");
      msmarco_v21_doc.put("dl22-doc-msmarco-v2.1", dl22PassageMetrics);
  
      Map<String, String> dl23PassageMetrics = new HashMap<>();
      dl23PassageMetrics.put("MAP", "-c -M 100 -m map");
      dl23PassageMetrics.put("MRR@10", "-c -M 100 -m recip_rank");
      dl23PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl23PassageMetrics.put("R@100", "-c -m recall.100");
      dl23PassageMetrics.put("R@1K", "-c -m recall.1000");
      msmarco_v21_doc.put("dl23-doc-msmarco-v2.1", dl23PassageMetrics);

      Map<String, String> rag24RaggyMetrics = new HashMap<>();
      rag24RaggyMetrics.put("MAP", "-c -M 100 -m map");
      rag24RaggyMetrics.put("MRR@10", "-c -M 100 -m recip_rank");
      rag24RaggyMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      rag24RaggyMetrics.put("R@100", "-c -m recall.100");
      rag24RaggyMetrics.put("R@1K", "-c -m recall.1000");
      msmarco_v21_doc.put("rag24.raggy-dev", rag24RaggyMetrics);

      metricDefinitions.put("msmarco-v2.1-doc", msmarco_v21_doc);

      Map<String, Map<String, String>> msmarco_v21_doc_segmented = new HashMap<>();

      // msmarco-v2.1-segmented-doc definitions
      Map<String, String> rag24testMetricsUmbrela = new HashMap<>();
      rag24testMetricsUmbrela.put("nDCG@20", "-c -m ndcg_cut.20");
      rag24testMetricsUmbrela.put("nDCG@100", "-c -m ndcg_cut.100");
      rag24testMetricsUmbrela.put("R@100", "-c -m recall.100");
      msmarco_v21_doc_segmented.put("rag24.test-umbrela-all", rag24testMetricsUmbrela);

      Map<String, String> rag24testMetricsNist = new HashMap<>();
      rag24testMetricsNist.put("nDCG@20", "-c -m ndcg_cut.20");
      rag24testMetricsNist.put("nDCG@100", "-c -m ndcg_cut.100");
      rag24testMetricsNist.put("R@100", "-c -m recall.100");
      msmarco_v21_doc_segmented.put("rag24.test", rag24testMetricsNist);

      metricDefinitions.put("msmarco-v2.1-doc-segmented", msmarco_v21_doc_segmented);
    }
  }

}
