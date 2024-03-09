package io.anserini.doc;
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

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringSubstitutor;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.anserini.tcr.RunMsMarco.Condition;
import io.anserini.tcr.RunMsMarco.Config;
import io.anserini.tcr.RunMsMarco.Topic;
import io.anserini.tcr.RunMsMarco.TrecEvalMetricDefinitions;

public class Generate2crDocsTest {

  public final static String[] MODELS = { "BoW-baselines", "splade-pp-ed-preencoded", "splade-pp-ed-onnx",
      "cos-dpr-distil-preencoded", "cos-dpr-distil-onnx", "bge-base-en-15-preencoded", "bge-base-en-15-onnx" };

  public static String findMsMarcoTableTopicSetKeyV1(String topicKey) {
    String key = "";
    if (topicKey.startsWith("TREC2019")) {
      key = "dl19";
    } else if (topicKey.startsWith("TREC2020")) {
      key = "dl20";
    } else if (topicKey.startsWith("MSMARCO")) {
      key = "dev";
    }
    return key;
  }

  // def format_command(raw):
  // return raw.replace('-topics', '\\\n -topics') \
  // .replace('-threads', '\\\n -threads') \
  // .replace('-index', '\\\n -index') \
  // .replace('-output ', '\\\n -output ') \
  // .replace('-encoder', '\\\n -encoder') \
  // .replace('.txt ', '.txt \\\n ')
  public static String formatCommand(String command) {
    return command.replace("-topics", "\\\n  -topics")
        .replace("-threads", "\\\n  -threads")
        .replace("-index", "\\\n  -index")
        .replace("-output ", "\\\n  -output ")
        .replace("-encoder", "\\\n  -encoder")
        .replace(".txt ", ".txt \\\n  ");
  }

  public static String formatEvalCommand(String command) {
    return command.replace("run.", "\\\n  run.");
  }

  public static void generateReport() throws Exception {
    String yamlFile = "src/main/java/io/anserini/tcr/msmarco-v1-passage.yaml";
    String htmlTemplate = "src/main/java/io/anserini/tcr/msmarco_html_v1_passage.template";
    String rowTemplate = "src/main/java/io/anserini/tcr/msmarco_html_row_v1.template";

    Map<String, Map<String, Map<String, Double>>> table = new HashMap<>();
    Map<String, Map<String, String>> commands = new HashMap<>();
    Map<String, Map<String, String>> evalCommands = new HashMap<>();

    Map<String, String> tableKeys = new HashMap<>();
    Map<String, String> rowIds = new HashMap<>();
    Map<String, String> nameTopicMap = new HashMap<>();

    final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    Config config = mapper.readValue(new FileInputStream("src/main/java/io/anserini/tcr/msmarco-v1-passage.yaml"),
        Config.class);

    Map<String, Map<String, String>> evalCommandMap = new TrecEvalMetricDefinitions().getMetricDefinitions()
        .get("msmarco-v1-passage");

    for (Condition cond : config.conditions) {
      // Implementation logic based on the provided Python code
      final String name = cond.name;
      final String display = cond.display_html;
      final String rowId = cond.display_row;
      final String cmdTemplate = cond.command;

      rowIds.put(name, rowId);
      tableKeys.put(name, display);

      Map<String, String> tempCommands = new HashMap<>();
      Map<String, String> tempEvalCommands = new HashMap<>();
      Map<String, Map<String, Double>> topicMetricMap = new HashMap<>();

      for (Topic topic : cond.topics) {
        Map<String, Double> metricScoreMap = new HashMap<>();
        final String topicKey = topic.topic_key;
        final String evalKey = topic.eval_key;
        final String shortTopicKey = findMsMarcoTableTopicSetKeyV1(topicKey);
        final String runFile = "runs/run." + "msmarco." + name + "." + shortTopicKey + ".txt";
        final String commandString = cmdTemplate
            .replace("$threads", "16")
            .replace("$topics", topicKey)
            .replace("$output", runFile);

        tempCommands.put(shortTopicKey, commandString);
        String evalCommandString = "";
        for (Map<String, Double> score : topic.scores) {
          // eval_cmd = f'tools/eval/trec_eval.9.0.4/trec_eval ' +
          // f'{trec_eval_metric_definitions[args.collection][eval_key][metric]}
          // {eval_key} {runfile}'
          final String tempEvalCommand = "tools/eval/trec_eval.9.0.4/trec_eval "
              + evalCommandMap.get(evalKey).get(score.keySet().toArray()[0]) + " " + evalKey + " " + runFile;
          evalCommandString += tempEvalCommand + "\n";
          metricScoreMap.put(score.keySet().toArray()[0].toString(), (Double) score.values().toArray()[0]);
        }
        tempEvalCommands.put(shortTopicKey, evalCommandString);
        topicMetricMap.put(shortTopicKey, metricScoreMap);

      }
      commands.put(name, tempCommands);
      evalCommands.put(name, tempEvalCommands);
      table.put(name, topicMetricMap);
    }
    // Additional logic to generate report

    int rowCounter = 1;
    String htmlString = "";
    Scanner rowScanner = new Scanner(new File(rowTemplate), "UTF-8");
    String rowTemplateString = rowScanner.useDelimiter("\\A").next();
    rowScanner.close();

    for (String model : MODELS) {
      Map<String, String> valuesMap = new HashMap<>(); 
      valuesMap.put("row_cnt", String.valueOf(rowCounter));
      valuesMap.put("condition_name", tableKeys.get(model));
      valuesMap.put("row", rowIds.get(model));
      valuesMap.put("s1", String.format("%.4f", table.get(model).get("dl19").get("MAP")));
      valuesMap.put("s2", String.format("%.4f", table.get(model).get("dl19").get("nDCG@10")));
      valuesMap.put("s3", String.format("%.4f", table.get(model).get("dl19").get("R@1K")));
      valuesMap.put("s4", String.format("%.4f", table.get(model).get("dl20").get("MAP")));
      valuesMap.put("s5", String.format("%.4f", table.get(model).get("dl20").get("nDCG@10")));
      valuesMap.put("s6", String.format("%.4f", table.get(model).get("dl20").get("R@1K")));
      valuesMap.put("s7", String.format("%.4f", table.get(model).get("dev").get("MRR@10")));
      valuesMap.put("s8", String.format("%.4f", table.get(model).get("dev").get("R@1K")));
      valuesMap.put("cmd1", formatCommand(commands.get(model).get("dl19")));
      valuesMap.put("cmd2", formatCommand(commands.get(model).get("dl20")));
      valuesMap.put("cmd3", formatCommand(commands.get(model).get("dev")));
      valuesMap.put("eval_cmd1", formatEvalCommand(evalCommands.get(model).get("dl19")));
      valuesMap.put("eval_cmd2", formatEvalCommand(evalCommands.get(model).get("dl20")));
      valuesMap.put("eval_cmd3", formatEvalCommand(evalCommands.get(model).get("dev")));

      StringSubstitutor sub = new StringSubstitutor(valuesMap);
      htmlString += sub.replace(rowTemplateString) + "\n";
    }
    System.out.println("htmlString: " + htmlString);

    Scanner htmlScanner = new Scanner(new File(htmlTemplate), "UTF-8");
    String htmlTemplateString = htmlScanner.useDelimiter("\\A").next();
    htmlScanner.close();
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("rows", htmlString);
    valuesMap.put("title", "MS MARCO V1 Passage");

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    String resolvedString = sub.replace(htmlTemplateString);
    FileUtils.writeStringToFile(new File("docs/2cr/msmarco-v1-passage.html"), resolvedString, "UTF-8");
  }

  @Test
  public void main() throws Exception {
    generateReport();
  }
}
