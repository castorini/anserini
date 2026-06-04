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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.anserini.reproduce.ReproduceFromPrebuiltIndexes.Condition;
import io.anserini.reproduce.ReproduceFromPrebuiltIndexes.Config;
import io.anserini.reproduce.ReproduceFromPrebuiltIndexes.Docgen;
import io.anserini.reproduce.ReproduceFromPrebuiltIndexes.DocgenSummaryColumn;
import io.anserini.reproduce.ReproduceFromPrebuiltIndexes.Topic;
import io.anserini.reproduce.ReproductionUtils;

public class GenerateReproductionDocsFromPrebuiltIndexesTest {
  private static final String COMMAND_INDENT = "    ";
  private static final String CONFIG_DIRECTORY = "src/main/resources/reproduce/from-prebuilt-indexes/configs/";
  private static final String DOCGEN_TEMPLATE_DIRECTORY = "src/main/resources/reproduce/from-prebuilt-indexes/docgen/";
  private static final String REPRODUCE_OUTPUT_DIRECTORY = "docs/reproduce/from-prebuilt-indexes/";
  private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

  private record ReportContext(String yamlPath, String runTag, Config config, String template, File output, String configLink) {}

  private record SummaryColumn(String label, String topicKey, String evalKey, String metric) {
    private boolean matches(Topic topic) {
      if (topicKey != null) {
        return topicKey.equals(topic.topic_key);
      }

      if (evalKey != null) {
        return evalKey.equals(topic.eval_key);
      }

      return true;
    }

    private Double score(Topic topic) {
      if (!matches(topic) || topic.expected_scores == null || metric == null) {
        return null;
      }

      return topic.expected_scores.get(metric);
    }
  }

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

  private static String firstMetric(Topic topic) {
    if (topic.expected_scores == null || topic.expected_scores.isEmpty()) {
      return null;
    }

    return topic.expected_scores.keySet().iterator().next();
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private static List<Topic> topics(Condition condition) {
    return condition.topics == null ? List.of() : condition.topics;
  }

  private static List<Condition> conditions(Config config) {
    return config.conditions == null ? List.of() : config.conditions;
  }

  private static String defaultMetric(Config config) {
    for (Condition condition : conditions(config)) {
      for (Topic topic : topics(condition)) {
        String metric = firstMetric(topic);
        if (metric != null) {
          return metric;
        }
      }
    }

    return null;
  }

  private static String columnLabel(DocgenSummaryColumn column) {
    if (!isBlank(column.label)) {
      return column.label;
    }

    if (!isBlank(column.topic_key)) {
      return column.topic_key;
    }

    if (!isBlank(column.eval_key)) {
      return column.eval_key;
    }

    return column.metric;
  }

  private static boolean matchesColumn(Topic topic, DocgenSummaryColumn column) {
    if (!isBlank(column.topic_key)) {
      return column.topic_key.equals(topic.topic_key);
    }

    if (!isBlank(column.eval_key)) {
      return column.eval_key.equals(topic.eval_key);
    }

    return true;
  }

  private static String columnMetric(Config config, DocgenSummaryColumn column) {
    if (!isBlank(column.metric)) {
      return column.metric;
    }

    for (Condition condition : conditions(config)) {
      for (Topic topic : topics(condition)) {
        if (matchesColumn(topic, column)) {
          return firstMetric(topic);
        }
      }
    }

    return defaultMetric(config);
  }

  private static List<SummaryColumn> summaryColumns(Config config) {
    Docgen docgen = config.docgen;
    if (docgen != null && docgen.columns != null && !docgen.columns.isEmpty()) {
      List<SummaryColumn> columns = new ArrayList<>();
      for (DocgenSummaryColumn column : docgen.columns) {
        columns.add(new SummaryColumn(columnLabel(column), column.topic_key, column.eval_key, columnMetric(config, column)));
      }

      return columns;
    }

    List<SummaryColumn> columns = new ArrayList<>();
    if (!conditions(config).isEmpty()) {
      for (Topic topic : topics(conditions(config).get(0))) {
        columns.add(new SummaryColumn(topic.topic_key, topic.topic_key, null, firstMetric(topic)));
      }
    }

    return columns;
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

    for (Condition condition : conditions(config)) {
      int sectionNumber = row;
      List<Double> scores = new ArrayList<>();
      for (int i = 0; i < columns.size(); i++) {
        scores.add(null);
      }

      for (Topic topic : topics(condition)) {
        if (topic.expected_scores == null) {
          continue;
        }

        for (int i = 0; i < columns.size(); i++) {
          Double score = columns.get(i).score(topic);
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
    for (int i = 0; i < conditions(config).size(); i++) {
      Condition condition = conditions(config).get(i);
      String heading = condition.short_name == null ? condition.display : condition.short_name;
      summary.append(" | ").append(String.format("[%s](#condition-%d)", heading, i + 1));
    }
    summary.append(" |\n");

    summary.append("| ---");
    for (int i = 0; i < conditions(config).size(); i++) {
      summary.append(" | ---");
    }
    summary.append(" |\n");

    if (conditions(config).isEmpty()) {
      summary.append("\n");
      return summary.toString();
    }

    for (Topic topic : topics(conditions(config).get(0))) {
      summary.append(String.format("| %s", topic.topic_key));
      for (Condition condition : conditions(config)) {
        Double score = null;
        for (Topic conditionTopic : topics(condition)) {
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

  private static String topicHeading(Topic topic, Condition condition) {
    long duplicateTopicKeys = topics(condition).stream()
        .filter(conditionTopic -> topic.topic_key.equals(conditionTopic.topic_key))
        .count();
    if (duplicateTopicKeys > 1) {
      return String.format("%s / %s", topic.topic_key, topic.eval_key);
    }

    return topic.topic_key;
  }

  private static String buildCommandSections(ReportContext context) {
    StringBuilder command = new StringBuilder();
    int row = 1;
    for (Condition condition : conditions(context.config())) {
      command.append(String.format("<a id=\"condition-%d\"></a>\n\n### %d. %s\n\n", row, row, condition.display));
      command.append(String.format("**Config**: %s\n\n", context.configLink()));
      row++;

      for (Topic topic : topics(condition)) {
        command.append(String.format("#### %s\n\n", topicHeading(topic, condition)));
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

  private static String buildSummary(Config config) {
    Docgen docgen = config.docgen;
    String summary = docgen == null || isBlank(docgen.summary) ? "rows" : docgen.summary;
    if ("rows".equals(summary)) {
      return buildSummaryInRows(config, summaryColumns(config));
    }

    if ("columns".equals(summary)) {
      String metric = docgen == null || isBlank(docgen.summary_metric) ? defaultMetric(config) : docgen.summary_metric;
      return buildSummaryInColumns(config, metric);
    }

    throw new IllegalArgumentException(String.format("Unsupported docgen summary: %s", summary));
  }

  private static void generateReport(String yamlConfig) throws Exception {
    ReportContext context = loadReportContext(yamlConfig);
    writeReport(context, buildSummary(context.config()), buildCommandSections(context));
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
    generateReport("msmarco-v1-passage.yaml");
  }

  @Test
  public void generateMsMarcoV1DocReport() throws Exception {
    generateReport("msmarco-v1-doc.yaml");
  }

  @Test
  public void generateMsMarcoV2PassageReport() throws Exception {
    generateReport("msmarco-v2-passage.yaml");
  }

  @Test
  public void generateMsMarcoV2DocReport() throws Exception {
    generateReport("msmarco-v2-doc.yaml");
  }

  @Test
  public void generateMsMarcoV21DocReport() throws Exception {
    generateReport("msmarco-v2.1-doc.yaml");
  }

  @Test
  public void generateMsMarcoV21SegmentedDocReport() throws Exception {
    generateReport("msmarco-v2.1-doc-segmented.yaml");
  }

  @Test
  public void generateBrightReport() throws Exception {
    generateReport("bright.yaml");
  }

  @Test
  public void generateBeirReport() throws Exception {
    generateReport("beir.yaml");
  }

  @Test
  public void generateDefaultSummaryWhenDocgenMetadataIsAbsent() {
    Config config = new Config();

    Condition condition = new Condition();
    condition.display = "default condition";

    Topic topic = new Topic();
    topic.topic_key = "default-topic";
    topic.expected_scores = Map.of("MAP", 0.1234);
    condition.topics = List.of(topic);
    config.conditions = List.of(condition);

    String summary = buildSummary(config);
    assertTrue(summary.contains("| # | name | default-topic |"));
    assertTrue(summary.contains("| [1](#condition-1) | default condition | 0.1234 |"));
  }

  @Test
  public void generateMissingConfigFailsClearly() throws Exception {
    try {
      generateReport("missing.yaml");
      fail("Expected missing config to fail");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("missing.yaml"));
    }
  }
}
