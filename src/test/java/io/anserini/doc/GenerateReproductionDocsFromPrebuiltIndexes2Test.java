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

package io.anserini.doc;

import io.anserini.reproduce.ReproduceFromPrebuiltIndexes.Condition;
import io.anserini.reproduce.ReproduceFromPrebuiltIndexes.Config;
import io.anserini.reproduce.ReproduceFromPrebuiltIndexes.Topic;
import io.anserini.reproduce.ReproductionUtils;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenerateReproductionDocsFromPrebuiltIndexes2Test {
  public final static String YAML_PATH = "src/main/resources/reproduce/from-prebuilt-indexes/configs/msmarco-v1-passage.core.yaml";
  private static final String COMMAND_INDENT = "    ";

  private static String formatCommand(String fatjarPlaceholder, String jvmArgs, String commandTemplate) {
    List<String> lines = new ArrayList<>();
    lines.add(ReproductionUtils.Constants.JAVA_PREFIX + " " + fatjarPlaceholder + " " + jvmArgs);

    String[] tokens = commandTemplate.split("\\s+");
    if (tokens.length > 0) {
      lines.add(COMMAND_INDENT + tokens[0]);
    }

    for (int i = 1; i < tokens.length; i++) {
      String token = tokens[i];
      if (token.startsWith("-")) {
        if (i + 1 < tokens.length && !tokens[i + 1].startsWith("-")) {
          lines.add(COMMAND_INDENT + token + " " + tokens[++i]);
        } else {
          lines.add(COMMAND_INDENT + token);
        }
      } else {
        lines.add(COMMAND_INDENT + token);
      }
    }

    return String.join(" \\\n", lines);
  }

  private static String buildCommand(String conditionName, String commandTemplate, String topicKey) {
    String output = String.format("%s/run.msmarco-v1-passage.core.%s.%s.txt",
        ReproductionUtils.Constants.DEFAULT_RUNS_DIRECTORY, conditionName, topicKey);

    String command = formatCommand("$fatjar", ReproductionUtils.Constants.JVM_ARGS, commandTemplate)
        .replace("$threads", "16")
        .replace("$topics", topicKey)
        .replace("$output", output)
        .replace("$runs_directory", ReproductionUtils.Constants.DEFAULT_RUNS_DIRECTORY);

    if ("bge-base-en-v1.5.hnsw.onnx".equals(conditionName) || "bge-base-en-v1.5.hnsw.cached".equals(conditionName)) {
      String efSearch = switch (topicKey) {
        case "bioasq" -> "11000";
        case "nq" -> "2000";
        case "hotpotqa", "fever" -> "6000";
        default -> null;
      };
      if (efSearch != null) {
        command = command.replaceFirst("(?<=\\s-efSearch\\s)\\d+", efSearch);
      }
    }

    return command;
  }

  private static String buildEvalCommands(String conditionName, Topic topic) {
    String output = String.format("%s/run.msmarco-v1-passage.core.%s.%s.txt",
        ReproductionUtils.Constants.DEFAULT_RUNS_DIRECTORY, conditionName, topic.topic_key);

    StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, Double> entry : topic.expected_scores.entrySet()) {
      String command = "java -cp $fatjar trec_eval $metric $evalKey $output"
          .replace("$fatjarPath", "$fatjarPath")
          .replace("$metric", topic.metric_definitions.get(entry.getKey()))
          .replace("$evalKey", topic.eval_key)
          .replace("$output", output);
      if (builder.length() > 0) {
        builder.append("\n");
      }
      builder.append(command);
    }

    return builder.toString();
  }

  @Test
  public void generateMsMarcoV1PassageReport() throws Exception {
    Config config = new ObjectMapper(new YAMLFactory()).readValue(new File(YAML_PATH), Config.class);
    String template = FileUtils.readFileToString(
        new File("src/main/resources/reproduce/from-prebuilt-indexes/docgen/msmarco-v1-passage.core.template"),
        StandardCharsets.UTF_8);

    File output = new File("docs/reproduce/from-prebuilt-indexes/msmarco-v1-passage.core.md");
    FileUtils.forceMkdirParent(output);

    StringBuilder builder = new StringBuilder();
    builder.append("| # | name | dev | DL19 | DL20 |\n");
    builder.append("| --- | --- | --- | --- | --- |\n");

    int row = 1;

    for (Condition condition : config.conditions) {
      int sectionNumber = row;
      Double devScore = null;
      Double dl19Score = null;
      Double dl20Score = null;

      for (Topic topic : condition.topics) {
        if ("msmarco-v1-passage.dev".equals(topic.topic_key) && topic.expected_scores != null) {
          devScore = topic.expected_scores.get("MRR@10");
        }

        if ("dl19-passage".equals(topic.topic_key) && topic.expected_scores != null) {
          dl19Score = topic.expected_scores.get("nDCG@10");
        }

        if ("dl20-passage".equals(topic.topic_key) && topic.expected_scores != null) {
          dl20Score = topic.expected_scores.get("nDCG@10");
        }
      }

      builder.append("| ")
          .append("[").append(sectionNumber).append("](#condition-").append(sectionNumber).append(")");
      row++;
      builder.append(" | ")
          .append(condition.display)
          .append(" | ")
          .append(devScore == null ? "" : String.format("%.4f", devScore))
          .append(" | ")
          .append(dl19Score == null ? "" : String.format("%.4f", dl19Score))
          .append(" | ")
          .append(dl20Score == null ? "" : String.format("%.4f", dl20Score))
          .append(" |\n");
    }

    builder.append("\n");

    row = 1;
    for (Condition condition : config.conditions) {
      builder.append("<a id=\"condition-").append(row).append("\"></a>\n\n");
      builder.append("### ").append(row++).append(". ").append(condition.display).append("\n\n");

      for (Topic topic : condition.topics) {
        builder.append("#### ").append(topic.topic_key).append("\n\n");
        builder.append("Retrieval command:\n\n");
        builder.append("```bash\n")
            .append(buildCommand(condition.name, condition.command, topic.topic_key))
            .append("\n```\n\n");
        builder.append("Evaluation commands:\n\n");
        builder.append("```bash\n")
            .append(buildEvalCommands(condition.name, topic))
            .append("\n```\n\n");
      }
    }

    String configLink = String.format("[%s](../../%s)", new File(YAML_PATH).getName(), YAML_PATH);
    FileUtils.writeStringToFile(output, template
        .replace("${config}", configLink)
        .replace("${contents}", builder.toString()), StandardCharsets.UTF_8);
  }
}
