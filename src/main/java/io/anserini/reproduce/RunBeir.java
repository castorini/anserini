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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import io.anserini.reproduce.RunMsMarco.Config;
import io.anserini.reproduce.RunMsMarco.Condition;
import io.anserini.reproduce.RunMsMarco.Topic;

public class RunBeir {
    // ANSI escape code for red text
    private static final String RED = "\u001B[31m";
    // ANSI escape code to reset to the default text color
    private static final String RESET = "\u001B[0m";
  
    private static final String FAIL = RED + "[FAIL]" + RESET;
  
    private static String COLLECTION = "beir";
  
    public static void main(String[] args) throws Exception {
  
      final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      Config config = mapper.readValue(RunBeir.class.getClassLoader()
      .getResourceAsStream("reproduce/beir.yaml"), Config.class);
        
      TrecEvalMetricDefinitions metricDefinitions = new TrecEvalMetricDefinitions();
  
      for (Condition condition : config.conditions) {
        System.out.println(String.format("# Running condition \"%s\": %s \n", condition.name, condition.display));
        for (Topic topic : condition.topics) {
          System.out.println("  - topic_key: " + topic.topic_key + "\n");
  
          final String output = String.format("runs/run.%s.%s.%s.txt", COLLECTION, topic.topic_key, condition.name);
  
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
                  System.out.println(String.format("    %7s: %.4f [OK]", metric, score));
                }
              } else {
                System.out.println("Evaluation command failed for metric: " + metric);
              }
            }
            System.out.println("");
          }
        }
      }
    }
  
    public static class TrecEvalMetricDefinitions {
      public Map<String, Map<String, Map<String, String>>> metricDefinitions;
  
      public TrecEvalMetricDefinitions() {
        metricDefinitions = new HashMap<>();
  
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
            beir.put("tools/topics-and-qrels/qrels.beir-v1.0.0-" + corpus + ".test.txt", corpusMap);
        }

        metricDefinitions.put("beir", beir);
      }
  
      public Map<String, Map<String, Map<String, String>>> getMetricDefinitions() {
        return metricDefinitions;
      }
    }
  }
  