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
  private static final String COMMAND_INDENT = "    ";
  private static final String CONFIG_DIRECTORY = "src/main/resources/reproduce/from-prebuilt-indexes/configs/";
  private static final String DOCGEN_TEMPLATE_DIRECTORY = "src/main/resources/reproduce/from-prebuilt-indexes/docgen/";
  private static final String REPRODUCE_OUTPUT_DIRECTORY = "docs/reproduce/from-prebuilt-indexes/";

  private static String formatCommand(String fatjarPlaceholder, String jvmArgs, String commandTemplate) {
    List<String> lines = new ArrayList<>();

    String[] tokens = commandTemplate.split("\\s+");
    if (tokens.length > 0) {
      lines.add(ReproductionUtils.Constants.JAVA_PREFIX + " " + fatjarPlaceholder + " " + jvmArgs + " " + tokens[0]);
    } else {
      lines.add(ReproductionUtils.Constants.JAVA_PREFIX + " " + fatjarPlaceholder + " " + jvmArgs);
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

  private static String buildCommand(String runTag, String conditionName, String commandTemplate, String topicKey) {
    String output = String.format("%s/run.%s.%s.%s.txt",
        ReproductionUtils.Constants.DEFAULT_RUNS_DIRECTORY, runTag, conditionName, topicKey);

    String command = formatCommand("$fatjar", "$jvm_args", commandTemplate)
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

  private static String buildEvalCommands(String runTag, String conditionName, Topic topic) {
    String output = String.format("%s/run.%s.%s.%s.txt",
        ReproductionUtils.Constants.DEFAULT_RUNS_DIRECTORY, runTag, conditionName, topic.topic_key);

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

  private static String yamlPath(String yamlConfig) {
    return CONFIG_DIRECTORY + yamlConfig;
  }

  private static boolean matchesTopicPrefix(String topicKey, String prefix) {
    return topicKey.equals(prefix) || topicKey.startsWith(prefix + ".");
  }

  private static void generateMsMarcoV1PassageReport(String yamlConfig) throws Exception {
    String yamlPath = yamlPath(yamlConfig);
    String runTag = new File(yamlPath).getName().replaceFirst("\\.yaml$", "");
    String templatePath = DOCGEN_TEMPLATE_DIRECTORY + runTag + ".template";
    String outputPath = REPRODUCE_OUTPUT_DIRECTORY + runTag + ".md";

    Config config = new ObjectMapper(new YAMLFactory()).readValue(new File(yamlPath), Config.class);
    String template = FileUtils.readFileToString(new File(templatePath), StandardCharsets.UTF_8);

    File output = new File(outputPath);
    FileUtils.forceMkdirParent(output);

    StringBuilder summary = new StringBuilder();
    summary.append("| # | name | dev | DL19 | DL20 |\n");
    summary.append("| --- | --- | --- | --- | --- |\n");

    int row = 1;

    for (Condition condition : config.conditions) {
      int sectionNumber = row;
      Double devScore = null;
      Double dl19Score = null;
      Double dl20Score = null;

      for (Topic topic : condition.topics) {
        if (topic.expected_scores == null) {
          continue;
        }

        if (topic.topic_key.startsWith("msmarco-v1-passage.dev")) {
          devScore = topic.expected_scores.get("MRR@10");
        }

        if (topic.topic_key.startsWith("dl19-passage")) {
          dl19Score = topic.expected_scores.get("nDCG@10");
        }

        if (topic.topic_key.startsWith("dl20-passage")) {
          dl20Score = topic.expected_scores.get("nDCG@10");
        }
      }

      summary.append(String.format("| [%d](#condition-%d) | %s | %s | %s | %s |\n",
          sectionNumber, sectionNumber, condition.display,
          devScore == null ? "" : String.format("%.4f", devScore),
          dl19Score == null ? "" : String.format("%.4f", dl19Score),
          dl20Score == null ? "" : String.format("%.4f", dl20Score)));
      row++;
    }

    summary.append("\n");

    String configLink = String.format("[%s](../../../%s)", new File(yamlPath).getName(), yamlPath);
    StringBuilder command = new StringBuilder();
    row = 1;
    for (Condition condition : config.conditions) {
      command.append(String.format("<a id=\"condition-%d\"></a>\n\n### %d. %s\n\n", row, row, condition.display));
      command.append(String.format("**Config**: %s\n\n", configLink));
      row++;

      for (Topic topic : condition.topics) {
        command.append(String.format("#### %s\n\n", topic.topic_key));
        command.append("Retrieval command:\n\n");
        command.append(String.format("```bash\n%s\n```\n\n", buildCommand(runTag, condition.name, condition.command, topic.topic_key)));
        command.append("Evaluation commands:\n\n");
        command.append(String.format("```bash\n%s\n```\n\n", buildEvalCommands(runTag, condition.name, topic)));
      }
    }

    FileUtils.writeStringToFile(output, template
        .replace("${config}", configLink)
        .replace("${jvm_args}", ReproductionUtils.Constants.JVM_ARGS)
        .replace("${summary}", summary.toString())
        .replace("${commands}", command.toString())
        .replace("${command}", command.toString()), StandardCharsets.UTF_8);
  }

  @Test
  public void generateMsMarcoV1PassageCoreReport() throws Exception {
    generateMsMarcoV1PassageReport("msmarco-v1-passage.core.yaml");
  }

  @Test
  public void generateMsMarcoV1PassageOptionalReport() throws Exception {
    generateMsMarcoV1PassageReport("msmarco-v1-passage.optional.yaml");
  }

  private static void generateMsMarcoV1DocReport(String yamlConfig) throws Exception {
    String yamlPath = yamlPath(yamlConfig);
    String runTag = new File(yamlPath).getName().replaceFirst("\\.yaml$", "");
    String templatePath = DOCGEN_TEMPLATE_DIRECTORY + runTag + ".template";
    String outputPath = REPRODUCE_OUTPUT_DIRECTORY + runTag + ".md";

    Config config = new ObjectMapper(new YAMLFactory()).readValue(new File(yamlPath), Config.class);
    String template = FileUtils.readFileToString(new File(templatePath), StandardCharsets.UTF_8);

    File output = new File(outputPath);
    FileUtils.forceMkdirParent(output);

    StringBuilder summary = new StringBuilder();
    summary.append("| # | name | dev | DL19 | DL20 |\n");
    summary.append("| --- | --- | --- | --- | --- |\n");

    int row = 1;

    for (Condition condition : config.conditions) {
      int sectionNumber = row;
      Double devScore = null;
      Double dl19Score = null;
      Double dl20Score = null;

      for (Topic topic : condition.topics) {
        if (topic.expected_scores == null) {
          continue;
        }

        if (topic.topic_key.startsWith("msmarco-doc.dev")) {
          devScore = topic.expected_scores.get("MRR@100");
        }

        if (topic.topic_key.startsWith("dl19-doc")) {
          dl19Score = topic.expected_scores.get("nDCG@10");
        }

        if (topic.topic_key.startsWith("dl20-doc") || topic.topic_key.startsWith("dl20")) {
          dl20Score = topic.expected_scores.get("nDCG@10");
        }
      }

      summary.append(String.format("| [%d](#condition-%d) | %s | %s | %s | %s |\n",
          sectionNumber, sectionNumber, condition.display,
          devScore == null ? "" : String.format("%.4f", devScore),
          dl19Score == null ? "" : String.format("%.4f", dl19Score),
          dl20Score == null ? "" : String.format("%.4f", dl20Score)));
      row++;
    }

    summary.append("\n");

    StringBuilder command = new StringBuilder();
    row = 1;
    for (Condition condition : config.conditions) {
      command.append(String.format("<a id=\"condition-%d\"></a>\n\n### %d. %s\n\n",
          row, row, condition.display));
      row++;

      for (Topic topic : condition.topics) {
        command.append(String.format("#### %s\n\n", topic.topic_key));
        command.append("Retrieval command:\n\n");
        command.append(String.format("```bash\n%s\n```\n\n", buildCommand(runTag, condition.name, condition.command, topic.topic_key)));
        command.append("Evaluation commands:\n\n");
        command.append(String.format("```bash\n%s\n```\n\n", buildEvalCommands(runTag, condition.name, topic)));
      }
    }

    String configLink = String.format("[%s](../../../%s)", new File(yamlPath).getName(), yamlPath);
    FileUtils.writeStringToFile(output, template
        .replace("${config}", configLink)
        .replace("${jvm_args}", ReproductionUtils.Constants.JVM_ARGS)
        .replace("${summary}", summary.toString())
        .replace("${commands}", command.toString())
        .replace("${command}", command.toString()), StandardCharsets.UTF_8);
  }

  @Test
  public void generateMsMarcoV1DocCoreReport() throws Exception {
    generateMsMarcoV1DocReport("msmarco-v1-doc.core.yaml");
  }

  @Test
  public void generateMsMarcoV1DocOptionalReport() throws Exception {
    generateMsMarcoV1DocReport("msmarco-v1-doc.optional.yaml");
  }

  private static void generateMsMarcoV2PassageReport(String yamlConfig) throws Exception {
    String yamlPath = yamlPath(yamlConfig);
    String runTag = new File(yamlPath).getName().replaceFirst("\\.yaml$", "");
    String templatePath = DOCGEN_TEMPLATE_DIRECTORY + runTag + ".template";
    String outputPath = REPRODUCE_OUTPUT_DIRECTORY + runTag + ".md";

    Config config = new ObjectMapper(new YAMLFactory()).readValue(new File(yamlPath), Config.class);
    String template = FileUtils.readFileToString(new File(templatePath), StandardCharsets.UTF_8);

    File output = new File(outputPath);
    FileUtils.forceMkdirParent(output);

    StringBuilder summary = new StringBuilder();
    summary.append("| # | name | dev | dev2 | DL21 | DL22 | DL23 |\n");
    summary.append("| --- | --- | --- | --- | --- | --- | --- |\n");

    int row = 1;

    for (Condition condition : config.conditions) {
      int sectionNumber = row;
      Double devScore = null;
      Double dev2Score = null;
      Double dl21Score = null;
      Double dl22Score = null;
      Double dl23Score = null;

      for (Topic topic : condition.topics) {
        if (topic.expected_scores == null) {
          continue;
        }

        if (matchesTopicPrefix(topic.topic_key, "msmarco-v2-passage.dev")) {
          devScore = topic.expected_scores.get("MRR@100");
        }

        if (matchesTopicPrefix(topic.topic_key, "msmarco-v2-passage.dev2")) {
          dev2Score = topic.expected_scores.get("MRR@100");
        }

        if (topic.topic_key.startsWith("dl21")) {
          dl21Score = topic.expected_scores.get("nDCG@10");
        }

        if (topic.topic_key.startsWith("dl22")) {
          dl22Score = topic.expected_scores.get("nDCG@10");
        }

        if (topic.topic_key.startsWith("dl23")) {
          dl23Score = topic.expected_scores.get("nDCG@10");
        }
      }

      summary.append(String.format("| [%d](#condition-%d) | %s | %s | %s | %s | %s | %s |\n",
          sectionNumber, sectionNumber, condition.display,
          devScore == null ? "" : String.format("%.4f", devScore),
          dev2Score == null ? "" : String.format("%.4f", dev2Score),
          dl21Score == null ? "" : String.format("%.4f", dl21Score),
          dl22Score == null ? "" : String.format("%.4f", dl22Score),
          dl23Score == null ? "" : String.format("%.4f", dl23Score)));
      row++;
    }

    summary.append("\n");

    String configLink = String.format("[%s](../../../%s)", new File(yamlPath).getName(), yamlPath);
    StringBuilder command = new StringBuilder();
    row = 1;
    for (Condition condition : config.conditions) {
      command.append(String.format("<a id=\"condition-%d\"></a>\n\n### %d. %s\n\n", row, row, condition.display));
      command.append(String.format("**Config**: %s\n\n", configLink));
      row++;

      for (Topic topic : condition.topics) {
        command.append(String.format("#### %s\n\n", topic.topic_key));
        command.append("Retrieval command:\n\n");
        command.append(String.format("```bash\n%s\n```\n\n", buildCommand(runTag, condition.name, condition.command, topic.topic_key)));
        command.append("Evaluation commands:\n\n");
        command.append(String.format("```bash\n%s\n```\n\n", buildEvalCommands(runTag, condition.name, topic)));
      }
    }

    FileUtils.writeStringToFile(output, template
        .replace("${config}", configLink)
        .replace("${jvm_args}", ReproductionUtils.Constants.JVM_ARGS)
        .replace("${summary}", summary.toString())
        .replace("${commands}", command.toString())
        .replace("${command}", command.toString()), StandardCharsets.UTF_8);
  }

  @Test
  public void generateMsMarcoV2PassageCoreReport() throws Exception {
    generateMsMarcoV2PassageReport("msmarco-v2-passage.core.yaml");
  }

  @Test
  public void generateMsMarcoV2PassageOptionalReport() throws Exception {
    generateMsMarcoV2PassageReport("msmarco-v2-passage.optional.yaml");
  }

  private static void generateMsMarcoV2DocReport(String yamlConfig) throws Exception {
    String yamlPath = yamlPath(yamlConfig);
    String runTag = new File(yamlPath).getName().replaceFirst("\\.yaml$", "");
    String templatePath = DOCGEN_TEMPLATE_DIRECTORY + runTag + ".template";
    String outputPath = REPRODUCE_OUTPUT_DIRECTORY + runTag + ".md";

    Config config = new ObjectMapper(new YAMLFactory()).readValue(new File(yamlPath), Config.class);
    String template = FileUtils.readFileToString(new File(templatePath), StandardCharsets.UTF_8);

    File output = new File(outputPath);
    FileUtils.forceMkdirParent(output);

    StringBuilder summary = new StringBuilder();
    summary.append("| # | name | dev | dev2 | DL21 | DL22 | DL23 |\n");
    summary.append("| --- | --- | --- | --- | --- | --- | --- |\n");

    int row = 1;

    for (Condition condition : config.conditions) {
      int sectionNumber = row;
      Double devScore = null;
      Double dev2Score = null;
      Double dl21Score = null;
      Double dl22Score = null;
      Double dl23Score = null;

      for (Topic topic : condition.topics) {
        if (topic.expected_scores == null) {
          continue;
        }

        if (matchesTopicPrefix(topic.topic_key, "msmarco-v2-doc.dev")) {
          devScore = topic.expected_scores.get("MRR@100");
        }

        if (matchesTopicPrefix(topic.topic_key, "msmarco-v2-doc.dev2")) {
          dev2Score = topic.expected_scores.get("MRR@100");
        }

        if (topic.topic_key.startsWith("dl21")) {
          dl21Score = topic.expected_scores.get("nDCG@10");
        }

        if (topic.topic_key.startsWith("dl22")) {
          dl22Score = topic.expected_scores.get("nDCG@10");
        }

        if (topic.topic_key.startsWith("dl23")) {
          dl23Score = topic.expected_scores.get("nDCG@10");
        }
      }

      summary.append(String.format("| [%d](#condition-%d) | %s | %s | %s | %s | %s | %s |\n",
          sectionNumber, sectionNumber, condition.display,
          devScore == null ? "" : String.format("%.4f", devScore),
          dev2Score == null ? "" : String.format("%.4f", dev2Score),
          dl21Score == null ? "" : String.format("%.4f", dl21Score),
          dl22Score == null ? "" : String.format("%.4f", dl22Score),
          dl23Score == null ? "" : String.format("%.4f", dl23Score)));
      row++;
    }

    summary.append("\n");

    String configLink = String.format("[%s](../../../%s)", new File(yamlPath).getName(), yamlPath);
    StringBuilder command = new StringBuilder();
    row = 1;
    for (Condition condition : config.conditions) {
      command.append(String.format("<a id=\"condition-%d\"></a>\n\n### %d. %s\n\n", row, row, condition.display));
      command.append(String.format("**Config**: %s\n\n", configLink));
      row++;

      for (Topic topic : condition.topics) {
        command.append(String.format("#### %s\n\n", topic.topic_key));
        command.append("Retrieval command:\n\n");
        command.append(String.format("```bash\n%s\n```\n\n", buildCommand(runTag, condition.name, condition.command, topic.topic_key)));
        command.append("Evaluation commands:\n\n");
        command.append(String.format("```bash\n%s\n```\n\n", buildEvalCommands(runTag, condition.name, topic)));
      }
    }

    FileUtils.writeStringToFile(output, template
        .replace("${config}", configLink)
        .replace("${jvm_args}", ReproductionUtils.Constants.JVM_ARGS)
        .replace("${summary}", summary.toString())
        .replace("${commands}", command.toString())
        .replace("${command}", command.toString()), StandardCharsets.UTF_8);
  }

  @Test
  public void generateMsMarcoV2DocCoreReport() throws Exception {
    generateMsMarcoV2DocReport("msmarco-v2-doc.core.yaml");
  }

  @Test
  public void generateMsMarcoV2DocOptionalReport() throws Exception {
    generateMsMarcoV2DocReport("msmarco-v2-doc.optional.yaml");
  }

  private static void generateMsMarcoV21DocReport(String yamlConfig) throws Exception {
    String yamlPath = yamlPath(yamlConfig);
    String runTag = new File(yamlPath).getName().replaceFirst("\\.yaml$", "");
    String templatePath = DOCGEN_TEMPLATE_DIRECTORY + runTag + ".template";
    String outputPath = REPRODUCE_OUTPUT_DIRECTORY + runTag + ".md";

    Config config = new ObjectMapper(new YAMLFactory()).readValue(new File(yamlPath), Config.class);
    String template = FileUtils.readFileToString(new File(templatePath), StandardCharsets.UTF_8);

    File output = new File(outputPath);
    FileUtils.forceMkdirParent(output);

    StringBuilder summary = new StringBuilder();
    summary.append("| # | name | dev | dev2 | DL21 | DL22 | DL23 | RAG24 |\n");
    summary.append("| --- | --- | --- | --- | --- | --- | --- | --- |\n");

    int row = 1;

    for (Condition condition : config.conditions) {
      int sectionNumber = row;
      Double devScore = null;
      Double dev2Score = null;
      Double dl21Score = null;
      Double dl22Score = null;
      Double dl23Score = null;
      Double rag24Score = null;

      for (Topic topic : condition.topics) {
        if (topic.expected_scores == null) {
          continue;
        }

        if (topic.topic_key.equals("msmarco-v2-doc.dev")) {
          devScore = topic.expected_scores.get("MRR@100");
        }

        if (topic.topic_key.equals("msmarco-v2-doc.dev2")) {
          dev2Score = topic.expected_scores.get("MRR@100");
        }

        if (topic.topic_key.startsWith("dl21-doc")) {
          dl21Score = topic.expected_scores.get("nDCG@10");
        }

        if (topic.topic_key.startsWith("dl22-doc")) {
          dl22Score = topic.expected_scores.get("nDCG@10");
        }

        if (topic.topic_key.startsWith("dl23-doc")) {
          dl23Score = topic.expected_scores.get("nDCG@10");
        }

        if (topic.topic_key.startsWith("rag24")) {
          rag24Score = topic.expected_scores.get("nDCG@10");
        }
      }

      summary.append(String.format("| [%d](#condition-%d) | %s | %s | %s | %s | %s | %s | %s |\n",
          sectionNumber, sectionNumber, condition.display,
          devScore == null ? "" : String.format("%.4f", devScore),
          dev2Score == null ? "" : String.format("%.4f", dev2Score),
          dl21Score == null ? "" : String.format("%.4f", dl21Score),
          dl22Score == null ? "" : String.format("%.4f", dl22Score),
          dl23Score == null ? "" : String.format("%.4f", dl23Score),
          rag24Score == null ? "" : String.format("%.4f", rag24Score)));
      row++;
    }

    summary.append("\n");

    String configLink = String.format("[%s](../../../%s)", new File(yamlPath).getName(), yamlPath);
    StringBuilder command = new StringBuilder();
    row = 1;
    for (Condition condition : config.conditions) {
      command.append(String.format("<a id=\"condition-%d\"></a>\n\n### %d. %s\n\n", row, row, condition.display));
      command.append(String.format("**Config**: %s\n\n", configLink));
      row++;

      for (Topic topic : condition.topics) {
        command.append(String.format("#### %s\n\n", topic.topic_key));
        command.append("Retrieval command:\n\n");
        command.append(String.format("```bash\n%s\n```\n\n", buildCommand(runTag, condition.name, condition.command, topic.topic_key)));
        command.append("Evaluation commands:\n\n");
        command.append(String.format("```bash\n%s\n```\n\n", buildEvalCommands(runTag, condition.name, topic)));
      }
    }

    FileUtils.writeStringToFile(output, template
        .replace("${config}", configLink)
        .replace("${jvm_args}", ReproductionUtils.Constants.JVM_ARGS)
        .replace("${summary}", summary.toString())
        .replace("${commands}", command.toString())
        .replace("${command}", command.toString()), StandardCharsets.UTF_8);
  }

  @Test
  public void generateMsMarcoV21DocCoreReport() throws Exception {
    generateMsMarcoV21DocReport("msmarco-v2.1-doc.core.yaml");
  }

  @Test
  public void generateMsMarcoV21DocOptionalReport() throws Exception {
    generateMsMarcoV21DocReport("msmarco-v2.1-doc.optional.yaml");
  }

  private static void generateMsMarcoV21SegmentedDocReport(String yamlConfig) throws Exception {
    String yamlPath = yamlPath(yamlConfig);
    String runTag = new File(yamlPath).getName().replaceFirst("\\.yaml$", "");
    String templatePath = DOCGEN_TEMPLATE_DIRECTORY + runTag + ".template";
    String outputPath = REPRODUCE_OUTPUT_DIRECTORY + runTag + ".md";

    Config config = new ObjectMapper(new YAMLFactory()).readValue(new File(yamlPath), Config.class);
    String template = FileUtils.readFileToString(new File(templatePath), StandardCharsets.UTF_8);

    File output = new File(outputPath);
    FileUtils.forceMkdirParent(output);

    StringBuilder summary = new StringBuilder();
    summary.append("| # | name | RAG24 ☂️ | RAG24 NIST | RAG25 ☂️ | RAG25 NIST |\n");
    summary.append("| --- | --- | --- | --- | --- | --- |\n");

    int row = 1;

    for (Condition condition : config.conditions) {
      int sectionNumber = row;
      Double rag24UmbrelaAll = null;
      Double rag24 = null;
      Double rag25Umbrela2 = null;
      Double rag25 = null;

      for (Topic topic : condition.topics) {
        if (topic.expected_scores == null) {
          continue;
        }

        if ("rag24.test-umbrela-all".equals(topic.eval_key)) {
          rag24UmbrelaAll = topic.expected_scores.get("nDCG@20");
        } else if ("rag24.test".equals(topic.eval_key)) {
          rag24 = topic.expected_scores.get("nDCG@20");
        } else if ("rag25.test-umbrela2".equals(topic.eval_key)) {
          rag25Umbrela2 = topic.expected_scores.get("nDCG@30");
        } else if ("rag25.test".equals(topic.eval_key)) {
          rag25 = topic.expected_scores.get("nDCG@30");
        }
      }

      summary.append(String.format("| [%d](#condition-%d) | %s | %s | %s | %s | %s |\n",
          sectionNumber, sectionNumber, condition.display,
          rag24UmbrelaAll == null ? "" : String.format("%.4f", rag24UmbrelaAll),
          rag24 == null ? "" : String.format("%.4f", rag24),
          rag25Umbrela2 == null ? "" : String.format("%.4f", rag25Umbrela2),
          rag25 == null ? "" : String.format("%.4f", rag25)));
      row++;
    }

    summary.append("\n");

    String configLink = String.format("[%s](../../../%s)", new File(yamlPath).getName(), yamlPath);
    StringBuilder command = new StringBuilder();
    row = 1;
    for (Condition condition : config.conditions) {
      command.append(String.format("<a id=\"condition-%d\"></a>\n\n### %d. %s\n\n", row, row, condition.display));
      command.append(String.format("**Config**: %s\n\n", configLink));
      row++;

      for (Topic topic : condition.topics) {
        command.append(String.format("#### %s / %s\n\n", topic.topic_key, topic.eval_key));
        command.append("Retrieval command:\n\n");
        command.append(String.format("```bash\n%s\n```\n\n", buildCommand(runTag, condition.name, condition.command, topic.topic_key)));
        command.append("Evaluation commands:\n\n");
        command.append(String.format("```bash\n%s\n```\n\n", buildEvalCommands(runTag, condition.name, topic)));
      }
    }

    FileUtils.writeStringToFile(output, template
        .replace("${config}", configLink)
        .replace("${jvm_args}", ReproductionUtils.Constants.JVM_ARGS)
        .replace("${summary}", summary.toString())
        .replace("${commands}", command.toString())
        .replace("${command}", command.toString()), StandardCharsets.UTF_8);
  }

  @Test
  public void generateMsMarcoV21SegmentedDocCoreReport() throws Exception {
    generateMsMarcoV21SegmentedDocReport("msmarco-v2.1-doc-segmented.core.yaml");
  }

  @Test
  public void generateMsMarcoV21SegmentedDocOptionalReport() throws Exception {
    generateMsMarcoV21SegmentedDocReport("msmarco-v2.1-doc-segmented.optional.yaml");
  }
}
