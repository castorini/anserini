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

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataModel {
  private static final String INDEX_COMMAND = "target/appassembler/bin/IndexCollection";
  private static final String SEARCH_COMMAND = "target/appassembler/bin/SearchCollection";

  private String corpus;
  private String corpus_path;

  private String search_command;
  private String topic_root;
  private String qrels_root;
  private String index_root;
  private String ranking_root;
  private String collection;
  private String generator;
  private int threads;
  private String topic_reader;
  private String index_path;
  private String index_options;
  private List<String> search_options;
  private Map<String, Long> index_stats;
  private List<Model> models;
  private List<Topic> topics;
  private List<Metric> metrics;

  public String getCorpus() {
    return corpus;
  }

  public void setCorpus(String corpus) {
    this.corpus = corpus;
  }

  public String getCorpus_path() {
    return corpus_path;
  }

  public void setCorpus_path(String corpus_path) {
    this.corpus_path = corpus_path;
  }

  public Map<String, Long> getIndex_stats() {
    return index_stats;
  }

  public void setIndex_stats(Map<String, Long> index_stats) {
    this.index_stats = index_stats;
  }

  public List<Metric> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<Metric> evals) {
    this.metrics = evals;
  }

  public List<Topic> getTopics() {
    return topics;
  }

  public void setTopics(List<Topic> topics) {
    this.topics = topics;
  }

  public List<Model> getModels() {
    return models;
  }

  public void setModels(List<Model> models) {
    this.models = models;
  }

  public String getSearch_command() {
    return search_command;
  }

  public void setSearch_command(String search_command) {
    this.search_command = search_command;
  }

  public String getTopic_root() {
    return topic_root;
  }

  public void setTopic_root(String topic_root) {
    this.topic_root = topic_root;
  }

  public String getQrels_root() {
    return qrels_root;
  }

  public void setQrels_root(String qrels_root) {
    this.qrels_root = qrels_root;
  }

  public String getIndex_root() {
    return index_root;
  }

  public void setIndex_root(String index_root) {
    this.index_root = index_root;
  }

  public String getRanking_root() {
    return ranking_root;
  }

  public void setRanking_root(String ranking_root) {
    this.ranking_root = ranking_root;
  }

  public String getCollection() {
    return collection;
  }

  public void setCollection(String collection) {
    this.collection = collection;
  }

  public String getGenerator() {
    return generator;
  }

  public void setGenerator(String generator) {
    this.generator = generator;
  }

  public int getThreads() {
    return threads;
  }

  public void setThreads(int threads) {
    this.threads = threads;
  }

  public String getTopic_reader() {
    return topic_reader;
  }

  public void setTopic_reader(String topic_reader) {
    this.topic_reader = topic_reader;
  }
  
  public String getIndex_path() {
    return index_path;
  }

  public void setIndex_path(String index_path) {
    this.index_path = index_path;
  }

  public String getIndex_options() {
    return index_options;
  }

  public void setIndex_options(String index_options) {
    this.index_options = index_options;
  }

  static class Topic {
    private String name;
    private String id;
    private String path;
    private String qrel;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getQrel() { return qrel; }
    public void setQrel(String qrel) { this.qrel = qrel; }
  }

  static class Model {
    private String name;
    private String display;
    private String params;
    private Map<String, List<Float>> results;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, List<Float>> getResults() { return results; }
    public void setDisplay(String display) { this.display = display; }
    public String getDisplay() { return display; }
    public void setResults(Map<String, List<Float>> results) { this.results = results; }
    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }
  }

  static class Metric {
    private String command;
    private String params;
    private String separator;
    private int parse_index;
    private String metric;
    private int metric_precision;
    private boolean can_combine;

    public boolean isCan_combine() { return can_combine; }
    public void setCan_combine(boolean can_combine) { this.can_combine = can_combine; }
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }

    public String getSeparator() { return separator; }
    public void setSeparator(String separator) { this.separator = separator; }
    public int getParse_index() { return parse_index; }
    public void setParse_index(int parse_index) { this.parse_index = parse_index; }
    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }
    public int getMetric_precision() { return metric_precision; }
    public void setMetric_precision(int metric_precision) { this.metric_precision = metric_precision; }
  }

  public String generateIndexingCommand(String collection) {
    StringBuilder builder = new StringBuilder();
    builder.append("nohup sh ");
    builder.append(INDEX_COMMAND).append(" \\\n");
    builder.append("  -collection ").append(getCollection()).append(" \\\n");
    builder.append("  -input ").append("/path/to/"+collection).append(" \\\n");
    builder.append("  -index ").append(getIndex_path()).append(" \\\n");
    builder.append("  -generator ").append(getGenerator()).append(" \\\n");
    builder.append("  -threads ").append(getThreads());
    builder.append(" ").append(getIndex_options()).append(" \\\n");
    builder.append(String.format("  >& logs/log.%s &", collection));
    return builder.toString();
  }

  public String generateRankingCommand(String collection) {
    StringBuilder builder = new StringBuilder();
    for (Model model : getModels()) {
      for (Topic topic : getTopics()) {
        builder.append("nohup ");
        builder.append(SEARCH_COMMAND).append(" \\\n");
        builder.append("  -index").append(" ").append(getIndex_path()).append(" \\\n");
        builder.append("  -topicreader").append(" ").append(getTopic_reader());
        builder.append("  -topics").append(" ").append(Paths.get(getTopic_root(), topic.getPath()).toString()).append(" \\\n");
        builder.append("  -output").append(" ").append("runs/run."+collection+"."+model.getName()+"."+topic.getPath()).append(" \\\n");
        if (model.getParams() != null) {
          builder.append("  ").append(model.getParams());
        }
        builder.append(" &"); // nohup
        builder.append("\n");
      }
      builder.append("\n");
    }

    return builder.toString().trim();
  }

  public String generateEvalCommand(String collection) {
    StringBuilder builder = new StringBuilder();
    for (Model model : getModels()) {
      for (Topic topic : getTopics()) {
        Map<String, Map<String, List<String>>> combinedEvalCmd = new HashMap<>();
        for (Metric eval : getMetrics()) {
          String evalCmd = eval.getCommand();
          String evalCmdOption = "";
          if (eval.getParams() != null) {
            evalCmdOption += " "+eval.getParams();
          }
          String evalCmdResidual = "";
          evalCmdResidual += " "+Paths.get(getQrels_root(), topic.getQrel());
          evalCmdResidual += " runs/run."+collection+"."+model.getName()+"."+topic.getPath();
          evalCmdResidual += "\n";
          if (eval.isCan_combine() || evalCmdOption.isEmpty()) {
            combinedEvalCmd.putIfAbsent(evalCmd, new HashMap<>());
            combinedEvalCmd.get(evalCmd).putIfAbsent(evalCmdResidual, new ArrayList<>());
            combinedEvalCmd.get(evalCmd).get(evalCmdResidual).add(evalCmdOption);
          } else {
            builder.append(evalCmd + evalCmdOption + evalCmdResidual);
          }
        }
        for (Map.Entry<String, Map<String, List<String>>> entry : combinedEvalCmd.entrySet()) {
          for (Map.Entry<String, List<String>> innerEntry : entry.getValue().entrySet()) {
            builder.append(entry.getKey() + String.join("", innerEntry.getValue()) + innerEntry.getKey());
          }
        }
      }
      builder.append("\n");
    }

    return builder.toString().trim();
  }

  public String generateEffectiveness(String collection) {
    StringBuilder builder = new StringBuilder();
    for (Metric eval : getMetrics()) {
      builder.append(String.format("%1$-40s|", eval.getMetric().toUpperCase()));
      for (Model model : getModels()) {
        if (model.getDisplay() == null) {
          builder.append(String.format(" %1$-10s|", model.getName().toUpperCase()));
        } else {
          builder.append(String.format(" %1$-10s|", model.getDisplay()));
        }
      }
      builder.append("\n");
      builder.append(":").append(StringUtils.repeat("-", 39)).append("|");
      for (Model model : getModels()) {
        builder.append(StringUtils.repeat("-", 11)).append("|");
      }
      builder.append("\n");
      for (int i = 0; i < topics.size(); i++) {
        Topic topic = getTopics().get(i);
        builder.append(String.format("%1$-40s|", topic.getName()));
        for (Model model : getModels()) {
          builder.append(String.format(" %-10.4f|", model.getResults().get(eval.getMetric()).get(i)));
        }
        builder.append("\n");
      }
      builder.append("\n\n");
    }

    return builder.toString().trim();
  }
}
