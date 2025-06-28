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

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class RunRepro {
  // ANSI escape code for red text
  private static final String RED = "\u001B[31m";
  // ANSI escape code for blue text
  private static final String BLUE = "\u001B[94m";
  // ANSI escape code to reset to the default text color
  private static final String RESET = "\u001B[0m";

  private static final String FAIL = RED + "[FAIL]" + RESET;
  private static final String OKAY_ISH = BLUE + "[OK*]" + RESET;

  private final String COLLECTION;
  private final TrecEvalMetricDefinitions metricDefinitions;

  public RunRepro(String collection, TrecEvalMetricDefinitions metrics) {
    COLLECTION = collection;
    metricDefinitions = metrics;
  }

  public void run() throws StreamReadException, DatabindException, IOException, InterruptedException, URISyntaxException {
    if (!new File("runs").exists()) {
      new File("runs").mkdir();
    }

    String fatjarPath = new File(RunRepro.class.getProtectionDomain()
                                .getCodeSource().getLocation().toURI()).getPath();

    final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    Config config = mapper.readValue(RunRepro.class.getClassLoader()
        .getResourceAsStream("reproduce/" + COLLECTION + ".yaml"), Config.class);

    for (Condition condition : config.conditions) {
      System.out.printf("# Running condition \"%s\": %s \n%n", condition.name, condition.display);
      for (Topic topic : condition.topics) {
        System.out.println("  - topic_key: " + topic.topic_key + "\n");

        final String output = String.format("runs/run.%s.%s.%s.txt", COLLECTION, condition.name, topic.topic_key);

        final String command = condition.command
            .replace("$fatjar", fatjarPath)
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
        System.out.println();

        // running the evaluation command
        Map<String, Map<String, String>> evalCommands = metricDefinitions.getMetricDefinitions().get(COLLECTION);
        InputStream stdout = null;

        for (Map<String, Double> expected : topic.scores) {
          for (String metric : expected.keySet()) {
            String evalKey = topic.eval_key;
            if (!evalCommands.get(evalKey).containsKey(metric)) {
              continue; // skip metric unintended to test
            }

            String evalCmd = "java -cp $fatjarPath trec_eval $metric $evalKey $output"
                .replace("$fatjarPath", fatjarPath)
                .replace("$metric", evalCommands.get(evalKey).get(metric))
                .replace("$evalKey", evalKey)
                .replace("$output", output);

            pb = new ProcessBuilder(evalCmd.split(" "));
            process = pb.start();

            resultCode = process.waitFor();
            stdout = process.getInputStream();
            if (resultCode == 0) {
              String scoreString = new String(stdout.readAllBytes()).replaceAll(".*?(\\d+\\.\\d+)$", "$1").trim();
              double score = Double.parseDouble(scoreString);
              double delta = Math.abs(score - expected.get(metric));

              if ( score > expected.get(metric)) {
                System.out.printf("    %7s: %.4f %s expected %.4f%n", metric, score, OKAY_ISH, expected.get(metric));
              } else if (delta < 0.00001) {
                System.out.printf("    %7s: %.4f [OK]%n", metric, score);
              } else if (delta < 0.0002) {
                System.out.printf("    %7s: %.4f %s expected %.4f%n", metric, score, OKAY_ISH, expected.get(metric));
              } else {
                System.out.printf("    %7s: %.4f %s expected %.4f%n", metric, score, FAIL, expected.get(metric));
              }
            } else {
              System.out.println("Evaluation command failed for metric: " + metric);
            }
          }
          System.out.println();
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
    }

    public Map<String, Map<String, Map<String, String>>> getMetricDefinitions() {
      return metricDefinitions;
    }
  }
}
