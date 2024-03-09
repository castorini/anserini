package io.anserini.tcr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunMsMarco {
  public static final String COLLECTION = "msmarco-v1-passage";

  public static void main(String[] args) throws Exception {
    final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    Config config = mapper.readValue(new FileInputStream("src/main/java/io/anserini/tcr/msmarco-v1-passage.yaml"), Config.class);
    TrecEvalMetricDefinitions metricDefinitions = new TrecEvalMetricDefinitions();

    for (Condition condition : config.conditions) {

      System.out.println("Running condition: " + condition.display);
      for (Topic topic : condition.topics) {
        final String output = "runs/run." + "msmarco." + condition.name + "." + topic.topic_key + ".txt";

        final String command = condition.command
            .replace("$threads", "16")
            .replace("$topics", topic.topic_key)
            .replace("$output", output);

        Process process = Runtime.getRuntime().exec(command);
        // System.out.println("Running retrieval command: " + command);
        int resultCode = process.waitFor();
        if (resultCode == 0) {
          System.out.println("Command executed successfully for topic: " + topic.topic_key);
        } else {
          System.out.println("Command failed for topic: " + topic.topic_key);
        }

        // running the evaluation command
        Map<String, Map<String, String>> evalCommands = metricDefinitions.getMetricDefinitions().get(COLLECTION);
        InputStream stdout = null;

        for (Map<String, Double> expected : topic.scores) {
          for (String metric : expected.keySet()) {
            String evalKey = topic.eval_key;
            String evalCmd = "tools/eval/trec_eval.9.0.4/trec_eval " + evalCommands.get(evalKey).get(metric) + " " + evalKey + " " + output;
            process = Runtime.getRuntime().exec(evalCmd);
            resultCode = process.waitFor();
            stdout = process.getInputStream();
            if (resultCode == 0) {
              String scoreString = new String(stdout.readAllBytes()).replaceAll(".*?(\\d+\\.\\d+)$", "$1").trim();
              Double score = Double.parseDouble(scoreString);
              Double delta = Math.abs(score - expected.get(metric));

              if (delta > 0.001) {
                System.out.println("Expected score: " + expected.get(metric) + " but got: " + score);
              } else {
                System.out.println("Score matches for metric: " + metric + " with value: " + score);
              }
            } else {
              System.out.println("Command failed for metric: " + metric);
            }
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
    }

    public Map<String, Map<String, Map<String, String>>> getMetricDefinitions() {
      return metricDefinitions;
    }
  }
}
