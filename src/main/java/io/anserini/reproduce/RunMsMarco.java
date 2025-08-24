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
    @Option(name = "-collection", usage = "MS MARCO version {'msmarco-v1-passage' (default), 'msmarco-v1-doc', 'msmarco-v2-doc', 'msmarco-v2-passage', 'msmarco-v2.1-doc', 'msmarco-v2.1-doc-segmented'}.")
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
            Arrays.asList("msmarco-v1-doc", "msmarco-v1-passage", "msmarco-v2-doc", "msmarco-v2-passage", "msmarco-v2.1-doc", "msmarco-v2.1-doc-segmented"));
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

      Map<String, Map<String, String>> msmarco_v1_doc = new HashMap<>();
      // msmarco-v1-doc definitions
      Map<String, String> msmarcoDocDevSubsetMetrics = new HashMap<>();
      msmarcoDocDevSubsetMetrics.put("MRR@100", "-c -M 100 -m recip_rank");
      msmarco_v1_doc.put("msmarco-doc.dev", msmarcoDocDevSubsetMetrics);

      Map<String, String> dl19DocMetrics = new HashMap<>();
      dl19DocMetrics.put("AP@100", "-c -M 100 -m map");
      dl19DocMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl19DocMetrics.put("R@1K", "-c -m recall.1000");
      msmarco_v1_doc.put("dl19-doc", dl19DocMetrics);

      Map<String, String> dl20DocMetrics = new HashMap<>();
      dl20DocMetrics.put("AP@100", "-c -M 100 -m map");
      dl20DocMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl20DocMetrics.put("R@1K", "-c -m recall.1000");
      msmarco_v1_doc.put("dl20-doc", dl20DocMetrics);
      metricDefinitions.put("msmarco-v1-doc", msmarco_v1_doc);
  
      Map<String, Map<String, String>> msmarco_v2_doc = new HashMap<>();
      Map<String, Map<String, String>> msmarco_v2_passage = new HashMap<>();
      Map<String, Map<String, String>> msmarco_v21_doc = new HashMap<>();
      // msmarco-v2.1-doc, msmarco-v2-passage definitions
      Map<String, String> msmarco2DevMetrics = new HashMap<>();
      msmarco2DevMetrics.put("MRR@100", "-c -M 100 -m recip_rank");
      msmarco_v2_passage.put("msmarco-v2-passage.dev", msmarco2DevMetrics);
      msmarco_v2_passage.put("msmarco-v2-passage.dev2", msmarco2DevMetrics);
      msmarco_v2_doc.put("msmarco-v2-doc.dev", msmarco2DevMetrics);
      msmarco_v2_doc.put("msmarco-v2-doc.dev2", msmarco2DevMetrics);
      msmarco_v21_doc.put("msmarco-v2.1-doc.dev", msmarco2DevMetrics);
      msmarco_v21_doc.put("msmarco-v2.1-doc.dev2", msmarco2DevMetrics);
  
      Map<String, String> dl21_3PassageMetrics = new HashMap<>();
      dl21_3PassageMetrics.put("MAP@100", "-c -M 100 -m map");
      dl21_3PassageMetrics.put("MRR@100", "-c -M 100 -m recip_rank");
      dl21_3PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl21_3PassageMetrics.put("R@100", "-c -m recall.100");
      dl21_3PassageMetrics.put("R@1K", "-c -m recall.1000");
      msmarco_v21_doc.put("dl21-doc-msmarco-v2.1", dl21_3PassageMetrics);
      msmarco_v21_doc.put("dl22-doc-msmarco-v2.1", dl21_3PassageMetrics);
      msmarco_v21_doc.put("dl23-doc-msmarco-v2.1", dl21_3PassageMetrics);
      msmarco_v21_doc.put("rag24.raggy-dev", dl21_3PassageMetrics);
      metricDefinitions.put("msmarco-v2-passage", msmarco_v2_passage);

      msmarco_v2_doc.put("dl21-doc", dl21_3PassageMetrics);
      msmarco_v2_doc.put("dl22-doc", dl21_3PassageMetrics);
      msmarco_v2_doc.put("dl23-doc", dl21_3PassageMetrics);
      metricDefinitions.put("msmarco-v2-doc", msmarco_v2_doc);

      Map<String, String> dl21_3PassageMetrics2 = new HashMap<>();
      dl21_3PassageMetrics2.put("MAP@100", "-c -M 100 -m map -l 2");
      dl21_3PassageMetrics2.put("MRR@100", "-c -M 100 -m recip_rank -l 2");
      dl21_3PassageMetrics2.put("nDCG@10", "-c -m ndcg_cut.10");
      dl21_3PassageMetrics2.put("R@100", "-c -m recall.100 -l 2");
      dl21_3PassageMetrics2.put("R@1K", "-c -m recall.1000 -l 2");
      msmarco_v2_passage.put("dl21-passage", dl21_3PassageMetrics2);
      msmarco_v2_passage.put("dl22-passage", dl21_3PassageMetrics2);
      msmarco_v2_passage.put("dl23-passage", dl21_3PassageMetrics2);
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
