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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunMsMarco {
  // ANSI escape code for red text
  private static final String RED = "\u001B[31m";
  // ANSI escape code to reset to the default text color
  private static final String RESET = "\u001B[0m";

  private static final String FAIL = RED + "[FAIL]" + RESET;

  private static String COLLECTION = "msmarco-v1-passage";

  public static void main(String[] args) throws Exception {

    // check for cmd option
    if (args.length >= 2 && "-v".equals(args[0])) {
      String collectionVersion = args[1];
      switch (collectionVersion) {
          case "1":
              COLLECTION = "msmarco-v1-passage";
              break;
          case "2":
              COLLECTION = "msmarco-v2.1-doc";
              break;
          case "2seg":
              COLLECTION = "msmarco-v2.1-doc-segmented";
              break;
          default:
              System.out.println(FAIL + " Invalid MsMarco version; please specify (1/2/2seg). Using default: " + COLLECTION);
              break;
      }
    }

    final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    Config config = mapper.readValue(RunMsMarco.class.getClassLoader()
    .getResourceAsStream("reproduce/" + COLLECTION + ".yaml"), Config.class);
  
    TrecEvalMetricDefinitions metricDefinitions = new TrecEvalMetricDefinitions();

    for (Condition condition : config.conditions) {
      System.out.println(String.format("# Running condition \"%s\": %s \n", condition.name, condition.display));
      for (Topic topic : condition.topics) {
        System.out.println("  - topic_key: " + topic.topic_key + "\n");

        final String output = String.format("runs/run.%s.%s.%s.txt", COLLECTION, condition.name, topic.topic_key);

        final String command = condition.command
            .replace("$threads", "16")
            .replace("$topics", topic.topic_key)
            .replace("$output", output);

        System.out.println("    Running retrieval command: " + command);

        ProcessBuilder pb = new ProcessBuilder(command.split(" "));
        Process process = pb.start();
        int resultCode = process.waitFor();
        if (resultCode == 0) {
          System.out.println("    Run successfully completed!");
        } else {
          System.out.println("    Run failed!");
        }
        System.out.println("");

        // running the evaluation command
        Map<String, Map<String, String>> evalCommands = metricDefinitions.getMetricDefinitions().get(COLLECTION);
        InputStream stdout = null;

        for (Map<String, Double> expected : topic.scores) {
          for (String metric : expected.keySet()) {
            String evalKey = topic.eval_key;
            String evalCmd = "bin/trec_eval " + evalCommands.get(evalKey).get(metric) + " " + evalKey + " " + output;

            pb = new ProcessBuilder(evalCmd.split(" "));
            process = pb.start();

            resultCode = process.waitFor();
            stdout = process.getInputStream();
            if (resultCode == 0) {
              String scoreString = new String(stdout.readAllBytes()).replaceAll(".*?(\\d+\\.\\d+)$", "$1").trim();
              Double score = Double.parseDouble(scoreString);
              Double delta = Math.abs(score - expected.get(metric));

              if (delta > 0.00005) {
                System.out.println(String.format("    %7s: %.4f %s expected %.4f", metric, score, FAIL, expected.get(metric)));
              } else {
                System.out.println("Evaluation command failed for metric: " + metric);
              }
            }
            System.out.println("");
          }
        }
      }
    }
  }

  public static class Config {
    @JsonProperty
    public List<Condition> conditions;
  }

  public static class Condition {
    @JsonProperty
    public String name;

    @JsonProperty
    public String display;

    @JsonProperty
    public String display_html;

    @JsonProperty
    public String display_row;

    @JsonProperty
    public String command;

    @JsonProperty
    public List<Topic> topics;
  }

  public static class Topic {
    @JsonProperty
    public String topic_key;

    @JsonProperty
    public String eval_key;

    @JsonProperty
    public List<Map<String, Double>> scores;
  }

  public static class TrecEvalMetricDefinitions {
    public Map<String, Map<String, Map<String, String>>> metricDefinitions;

    public TrecEvalMetricDefinitions() {
      metricDefinitions = new HashMap<>();

      Map<String, Map<String, String>> msmarcoV1Passage = new HashMap<>();

      // msmarco-v1-passage definitions
      Map<String, String> msmarcoDevSubsetMetrics = new HashMap<>();
      msmarcoDevSubsetMetrics.put("MRR@10", "-c -M 10 -m recip_rank");
      msmarcoDevSubsetMetrics.put("R@1K", "-c -m recall.1000");
      msmarcoV1Passage.put("tools/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt",
          msmarcoDevSubsetMetrics);

      Map<String, String> dl19PassageMetrics = new HashMap<>();
      dl19PassageMetrics.put("MAP", "-c -l 2 -m map");
      dl19PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl19PassageMetrics.put("R@1K", "-c -l 2 -m recall.1000");
      msmarcoV1Passage.put("tools/topics-and-qrels/qrels.dl19-passage.txt", dl19PassageMetrics);

      Map<String, String> dl20PassageMetrics = new HashMap<>();
      dl20PassageMetrics.put("MAP", "-c -l 2 -m map");
      dl20PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl20PassageMetrics.put("R@1K", "-c -l 2 -m recall.1000");
      msmarcoV1Passage.put("tools/topics-and-qrels/qrels.dl20-passage.txt", dl20PassageMetrics);

      metricDefinitions.put("msmarco-v1-passage", msmarcoV1Passage);

      Map<String, Map<String, String>> msmarcoV2Passage = new HashMap<>();

      // msmarco-v2.1-doc definitions
      Map<String, String> msmarco2Dev1Metrics = new HashMap<>();
      msmarco2Dev1Metrics.put("MRR@10", "-c -M 100 -m recip_rank");
      msmarcoV2Passage.put("tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev.txt",
          msmarco2Dev1Metrics);
      
      Map<String, String> msmarco2Dev2Metrics = new HashMap<>();
      msmarco2Dev2Metrics.put("MRR@10", "-c -M 100 -m recip_rank");
      msmarcoV2Passage.put("tools/topics-and-qrels/qrels.msmarco-v2.1-doc.dev2.txt",
          msmarco2Dev2Metrics);

      Map<String, String> dl21PassageMetrics = new HashMap<>();
      dl21PassageMetrics.put("MAP", "-c -M 100 -m map");
      dl21PassageMetrics.put("MRR@10", "-c -M 100 -m recip_rank");
      dl21PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl21PassageMetrics.put("R@100", "-c -m recall.100");
      dl21PassageMetrics.put("R@1K", "-c -m recall.1000");
      msmarcoV2Passage.put("tools/topics-and-qrels/qrels.dl21-doc-msmarco-v2.1.txt", dl21PassageMetrics);

      Map<String, String> dl22PassageMetrics = new HashMap<>();
      dl22PassageMetrics.put("MAP", "-c -M 100 -m map");
      dl22PassageMetrics.put("MRR@10", "-c -M 100 -m recip_rank");
      dl22PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl22PassageMetrics.put("R@100", "-c -m recall.100");
      dl22PassageMetrics.put("R@1K", "-c -m recall.1000");
      msmarcoV2Passage.put("tools/topics-and-qrels/qrels.dl22-doc-msmarco-v2.1.txt", dl22PassageMetrics);

      Map<String, String> dl23PassageMetrics = new HashMap<>();
      dl23PassageMetrics.put("MAP", "-c -M 100 -m map");
      dl23PassageMetrics.put("MRR@10", "-c -M 100 -m recip_rank");
      dl23PassageMetrics.put("nDCG@10", "-c -m ndcg_cut.10");
      dl23PassageMetrics.put("R@100", "-c -m recall.100");
      dl23PassageMetrics.put("R@1K", "-c -m recall.1000");
      msmarcoV2Passage.put("tools/topics-and-qrels/qrels.dl23-doc-msmarco-v2.1.txt", dl23PassageMetrics);

      metricDefinitions.put("msmarco-v2.1-doc", msmarcoV2Passage);

      // msmarco-v2.1-doc-segmented definitions; same as msmarco-v2.1-doc
      metricDefinitions.put("msmarco-v2.1-doc-segmented", metricDefinitions.get("msmarco-v2.1-doc"));
    }

    public Map<String, Map<String, Map<String, String>>> getMetricDefinitions() {
      return metricDefinitions;
    }
  }
}
