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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.anserini.reproduce.ReproduceFromPrebuiltIndexes.Condition;
import io.anserini.reproduce.ReproduceFromPrebuiltIndexes.Config;
import io.anserini.reproduce.ReproduceFromPrebuiltIndexes.Topic;
import io.anserini.reproduce.ReproductionUtils;

public class GenerateReproductionDocsFromPrebuiltIndexesTest {
  private static final String COMMAND_INDENT = "    ";
  private static final String CONFIG_DIRECTORY = "src/main/resources/reproduce/from-prebuilt-indexes/configs/";
  private static final String DOCGEN_TEMPLATE_DIRECTORY = "src/main/resources/reproduce/from-prebuilt-indexes/docgen/";
  private static final String REPRODUCE_OUTPUT_DIRECTORY = "docs/reproduce/from-prebuilt-indexes/";
  private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

  private record ReportContext(String yamlPath, String runTag, Config config, String template, File output, String configLink) {}

  private record SummaryColumn(String label, Function<Topic, Double> scoreExtractor) {}

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
    String command = formatCommand("$fatjar", "$jvm_args", commandTemplate)
        .replace("$threads", "16")
        .replace("$topics", topicKey)
        .replace("$output", runOutputPath(runTag, conditionName, topicKey))
        .replace("$runs_directory", ReproductionUtils.Constants.DEFAULT_RUNS_DIRECTORY);

    return command;
  }

  private static String buildEvalCommands(String runTag, String conditionName, Topic topic) {
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, Double> entry : topic.expected_scores.entrySet()) {
      String command = "java -cp $fatjar trec_eval $metric $evalKey $output"
          .replace("$metric", topic.metric_definitions.get(entry.getKey()))
          .replace("$evalKey", topic.eval_key)
          .replace("$output", runOutputPath(runTag, conditionName, topic.topic_key));
      if (builder.length() > 0) {
        builder.append("\n");
      }
      builder.append(command);
    }

    return builder.toString();
  }

  private static String runOutputPath(String runTag, String conditionName, String topicKey) {
    return String.format("%s/run.%s.%s.%s.txt", ReproductionUtils.Constants.DEFAULT_RUNS_DIRECTORY, runTag, conditionName, topicKey);
  }

  private static String yamlPath(String yamlConfig) {
    return CONFIG_DIRECTORY + yamlConfig;
  }

  private static boolean matchesTopicPrefix(String topicKey, String prefix) {
    return topicKey.equals(prefix) || topicKey.startsWith(prefix + ".");
  }

  private static ReportContext loadReportContext(String yamlConfig) throws Exception {
    String yamlPath = yamlPath(yamlConfig);
    String runTag = new File(yamlPath).getName().replaceFirst("\\.yaml$", "");
    String templatePath = DOCGEN_TEMPLATE_DIRECTORY + runTag + ".template";
    String outputPath = REPRODUCE_OUTPUT_DIRECTORY + runTag + ".md";

    Config config = YAML_MAPPER.readValue(new File(yamlPath), Config.class);
    String template = FileUtils.readFileToString(new File(templatePath), StandardCharsets.UTF_8);

    File output = new File(outputPath);
    FileUtils.forceMkdirParent(output);

    String configLink = String.format("[%s](../../../%s)", new File(yamlPath).getName(), yamlPath);
    return new ReportContext(yamlPath, runTag, config, template, output, configLink);
  }

  private static String buildSummaryInRows(Config config, List<SummaryColumn> columns) {
    StringBuilder summary = new StringBuilder();
    summary.append("| # | name");
    for (SummaryColumn column : columns) {
      summary.append(" | ").append(column.label());
    }
    summary.append(" |\n");

    summary.append("| --- | ---");
    for (int i = 0; i < columns.size(); i++) {
      summary.append(" | ---");
    }
    summary.append(" |\n");

    int row = 1;

    for (Condition condition : config.conditions) {
      int sectionNumber = row;
      List<Double> scores = new ArrayList<>();
      for (int i = 0; i < columns.size(); i++) {
        scores.add(null);
      }

      for (Topic topic : condition.topics) {
        if (topic.expected_scores == null) {
          continue;
        }

        for (int i = 0; i < columns.size(); i++) {
          Double score = columns.get(i).scoreExtractor().apply(topic);
          if (score != null) {
            scores.set(i, score);
          }
        }
      }

      summary.append(String.format("| [%d](#condition-%d) | %s", sectionNumber, sectionNumber, condition.display));
      for (Double score : scores) {
        summary.append(" | ").append(score == null ? "" : String.format("%.4f", score));
      }
      summary.append(" |\n");
      row++;
    }

    summary.append("\n");
    return summary.toString();
  }

  private static String buildSummaryInColumns(Config config, String metric) {
    StringBuilder summary = new StringBuilder();
    summary.append("| corpus");
    for (int i = 0; i < config.conditions.size(); i++) {
      Condition condition = config.conditions.get(i);
      String heading = condition.short_name == null ? condition.display : condition.short_name;
      summary.append(" | ").append(String.format("[%s](#condition-%d)", heading, i + 1));
    }
    summary.append(" |\n");

    summary.append("| ---");
    for (int i = 0; i < config.conditions.size(); i++) {
      summary.append(" | ---");
    }
    summary.append(" |\n");

    for (Topic topic : config.conditions.get(0).topics) {
      summary.append(String.format("| %s", topic.topic_key));
      for (Condition condition : config.conditions) {
        Double score = null;
        for (Topic conditionTopic : condition.topics) {
          if (conditionTopic.topic_key.equals(topic.topic_key) && conditionTopic.expected_scores != null) {
            score = conditionTopic.expected_scores.get(metric);
            break;
          }
        }
        summary.append(" | ").append(score == null ? "" : String.format("%.4f", score));
      }
      summary.append(" |\n");
    }

    summary.append("\n");
    return summary.toString();
  }

  private static String buildCommandSections(ReportContext context, BiFunction<Topic, Condition, String> topicHeading) {
    StringBuilder command = new StringBuilder();
    int row = 1;
    for (Condition condition : context.config().conditions) {
      command.append(String.format("<a id=\"condition-%d\"></a>\n\n### %d. %s\n\n", row, row, condition.display));
      command.append(String.format("**Config**: %s\n\n", context.configLink()));
      row++;

      for (Topic topic : condition.topics) {
        command.append(String.format("#### %s\n\n", topicHeading.apply(topic, condition)));
        command.append("Retrieval command:\n\n");
        command.append(String.format("```bash\n%s\n```\n\n",
            buildCommand(context.runTag(), condition.name, condition.command, topic.topic_key)));
        command.append("Evaluation commands:\n\n");
        command.append(String.format("```bash\n%s\n```\n\n",
            buildEvalCommands(context.runTag(), condition.name, topic)));
      }
    }

    return command.toString();
  }

  private static Double extractExpectedScore(Topic topic, boolean matches, String metric) {
    return matches ? topic.expected_scores.get(metric) : null;
  }

  private static void generateReportInRows(String yamlConfig, List<SummaryColumn> columns,
      BiFunction<Topic, Condition, String> topicHeading) throws Exception {
    ReportContext context = loadReportContext(yamlConfig);
    String commands = buildCommandSections(context, topicHeading);
    writeReport(context, buildSummaryInRows(context.config(), columns), commands);
  }

  private static void generateReportInColumns(String yamlConfig, String metric,
      BiFunction<Topic, Condition, String> topicHeading) throws Exception {
    ReportContext context = loadReportContext(yamlConfig);
    String commands = buildCommandSections(context, topicHeading);
    writeReport(context, buildSummaryInColumns(context.config(), metric), commands);
  }

  private static void writeReport(ReportContext context, String summary, String commands) throws Exception {
    FileUtils.writeStringToFile(context.output(), context.template()
        .replace("${config}", context.configLink())
        .replace("${jvm_args}", ReproductionUtils.Constants.JVM_ARGS)
        .replace("${summary}", summary)
        .replace("${commands}", commands)
        .replace("${command}", commands), StandardCharsets.UTF_8);
  }

  @Test
  public void generateMsMarcoV1PassageReport() throws Exception {
    generateReportInRows("msmarco-v1-passage.yaml", List.of(
        new SummaryColumn("dev", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("msmarco-v1-passage.dev"), "MRR@10")),
        new SummaryColumn("DL19", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("dl19-passage"), "nDCG@10")),
        new SummaryColumn("DL20", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("dl20-passage"), "nDCG@10"))),
        (topic, condition) -> topic.topic_key);
  }

  @Test
  public void generateMsMarcoV1DocReport() throws Exception {
    generateReportInRows("msmarco-v1-doc.yaml", List.of(
        new SummaryColumn("dev", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("msmarco-doc.dev"), "MRR@100")),
        new SummaryColumn("DL19", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("dl19-doc"), "nDCG@10")),
        new SummaryColumn("DL20", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("dl20-doc"), "nDCG@10"))),
        (topic, condition) -> topic.topic_key);
  }

  @Test
  public void generateMsMarcoV2PassageReport() throws Exception {
    generateReportInRows("msmarco-v2-passage.yaml", List.of(
        new SummaryColumn("dev", topic -> extractExpectedScore(topic, matchesTopicPrefix(topic.topic_key, "msmarco-v2-passage.dev"), "MRR@100")),
        new SummaryColumn("dev2", topic -> extractExpectedScore(topic, matchesTopicPrefix(topic.topic_key, "msmarco-v2-passage.dev2"), "MRR@100")),
        new SummaryColumn("DL21", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("dl21"), "nDCG@10")),
        new SummaryColumn("DL22", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("dl22"), "nDCG@10")),
        new SummaryColumn("DL23", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("dl23"), "nDCG@10"))),
        (topic, condition) -> topic.topic_key);
  }

  @Test
  public void generateMsMarcoV2DocReport() throws Exception {
    generateReportInRows("msmarco-v2-doc.yaml", List.of(
        new SummaryColumn("dev", topic -> extractExpectedScore(topic, matchesTopicPrefix(topic.topic_key, "msmarco-v2-doc.dev"), "MRR@100")),
        new SummaryColumn("dev2", topic -> extractExpectedScore(topic, matchesTopicPrefix(topic.topic_key, "msmarco-v2-doc.dev2"), "MRR@100")),
        new SummaryColumn("DL21", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("dl21"), "nDCG@10")),
        new SummaryColumn("DL22", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("dl22"), "nDCG@10")),
        new SummaryColumn("DL23", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("dl23"), "nDCG@10"))),
        (topic, condition) -> topic.topic_key);
  }

  @Test
  public void generateMsMarcoV21DocReport() throws Exception {
    generateReportInRows("msmarco-v2.1-doc.yaml", List.of(
        new SummaryColumn("dev", topic -> extractExpectedScore(topic, topic.topic_key.equals("msmarco-v2-doc.dev"), "MRR@100")),
        new SummaryColumn("dev2", topic -> extractExpectedScore(topic, topic.topic_key.equals("msmarco-v2-doc.dev2"), "MRR@100")),
        new SummaryColumn("DL21", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("dl21-doc"), "nDCG@10")),
        new SummaryColumn("DL22", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("dl22-doc"), "nDCG@10")),
        new SummaryColumn("DL23", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("dl23-doc"), "nDCG@10")),
        new SummaryColumn("RAGgy", topic -> extractExpectedScore(topic, topic.topic_key.startsWith("rag24"), "nDCG@10"))),
        (topic, condition) -> topic.topic_key);
  }

  @Test
  public void generateMsMarcoV21SegmentedDocReport() throws Exception {
    generateReportInRows("msmarco-v2.1-doc-segmented.yaml", List.of(
        new SummaryColumn("RAG24 ☂️", topic -> extractExpectedScore(topic, topic.eval_key.equals("rag24.test-umbrela-all"), "nDCG@20")),
        new SummaryColumn("RAG24 NIST", topic -> extractExpectedScore(topic, topic.eval_key.equals("rag24.test"), "nDCG@20")),
        new SummaryColumn("RAG25 ☂️", topic -> extractExpectedScore(topic, topic.eval_key.equals("rag25.test-umbrela2"), "nDCG@30")),
        new SummaryColumn("RAG25 NIST", topic -> extractExpectedScore(topic, topic.eval_key.equals("rag25.test"), "nDCG@30"))),
        (topic, condition) -> String.format("%s / %s", topic.topic_key, topic.eval_key));
  }

  @Test
  public void generateBrightReport() throws Exception {
    generateReportInColumns("bright.yaml", "nDCG@10",
        (topic, condition) -> topic.topic_key);
  }

  @Test
  public void generateBeirReport() throws Exception {
    generateReportInColumns("beir.yaml", "nDCG@10",
        (topic, condition) -> topic.topic_key);
  }
}
