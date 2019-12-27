/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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
  private String name;
  private String index_command;
  private String index_utils_command;
  private String search_command;
  private String topic_root;
  private String qrels_root;
  private String index_root;
  private String ranking_root;
  private String collection;
  private String generator;
  private int threads;
  private String topic_reader;
  private List<String> input_roots;
  private String input;
  private String index_path;
  private List<String> index_options;
  private List<String> search_options;
  private Map<String, Long> index_stats;
  private List<Model> models;
  private List<Topic> topics;
  private List<Eval> evals;

  public Map<String, Long> getIndex_stats() {
    return index_stats;
  }

  public void setIndex_stats(Map<String, Long> index_stats) {
    this.index_stats = index_stats;
  }

  public List<Eval> getEvals() {
    return evals;
  }

  public void setEvals(List<Eval> evals) {
    this.evals = evals;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIndex_command() {
    return index_command;
  }

  public void setIndex_command(String index_command) {
    this.index_command = index_command;
  }

  public String getIndex_utils_command() {
    return index_utils_command;
  }

  public void setIndex_utils_command(String index_utils_command) {
    this.index_utils_command = index_utils_command;
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
  
  public List<String> getInput_roots() {
    return input_roots;
  }
  
  public void setInput_roots(List<String> input_roots) {
    this.input_roots = input_roots;
  }
  
  public String getInput() {
    return input;
  }

  public void setInput(String input) {
    this.input = input;
  }

  public String getIndex_path() {
    return index_path;
  }

  public void setIndex_path(String index_path) {
    this.index_path = index_path;
  }

  static class Topic {
    private String name;
    private String path;
    private String qrel;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getQrel() { return qrel; }
    public void setQrel(String qrel) { this.qrel = qrel; }
  }

  static class Model {
    private String name;
    private String display;
    private List<String> params;
    private Map<String, List<Float>> results;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, List<Float>> getResults() { return results; }
    public String getDisplay() { return display; }
    public void setResults(Map<String, List<Float>> results) { this.results = results; }
    public List<String> getParams() { return params; }
    public void setParams(List<String> params) { this.params = params; }
  }

  static class Eval {
    private String command;
    private List<String> params;
    private String separator;
    private int parse_index;
    private String metric;
    private int metric_precision;
    private boolean can_combine;

    public boolean isCan_combine() { return can_combine; }
    public void setCan_combine(boolean can_combine) { this.can_combine = can_combine; }
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    public List<String> getParams() { return params; }
    public void setParams(List<String> params) { this.params = params; }
    public String getSeparator() { return separator; }
    public void setSeparator(String separator) { this.separator = separator; }
    public int getParse_index() { return parse_index; }
    public void setParse_index(int parse_index) { this.parse_index = parse_index; }
    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }
    public int getMetric_precision() { return metric_precision; }
    public void setMetric_precision(int metric_precision) { this.metric_precision = metric_precision; }
  }

  public List<String> getIndex_options() {
    return index_options;
  }

  public void setIndex_options(List<String> index_options) {
    this.index_options = index_options;
  }

  public List<String> getSearch_options() {
    return search_options;
  }

  public void setSearch_options(List<String> search_options) {
    this.search_options = search_options;
  }

  public String generateIndexingCommand(String collection) {
    boolean containRawDocs = false;
    for (String option : getIndex_options()) {
      if (option.contains("-storeRawDocs")) {
        containRawDocs = true;
      }
    }
    StringBuilder builder = new StringBuilder();
    builder.append("nohup sh ");
    builder.append(getIndex_command());
    builder.append(" -collection ").append(getCollection());
    builder.append(" -input ").append("/path/to/"+collection).append(" \\\n");
    builder.append(" -index ").append("lucene-index."+getName()+".pos+docvectors"+(containRawDocs ? "+rawdocs" : ""));
    builder.append(" -generator ").append(getGenerator());
    builder.append(" -threads ").append(getThreads()).append(" \\\n");
    for (String option : getIndex_options()) {
      builder.append(" ").append(option);
    }
    builder.append(String.format(" >& log.%s.pos+docvectors%s &", collection, containRawDocs ? "+rawdocs" : ""));
    return builder.toString();
  }

  public String generateRankingCommand(String collection) {
    boolean containRawDocs = false;
    for (String option : getIndex_options()) {
      if (option.contains("-storeRawDocs")) {
        containRawDocs = true;
      }
    }
    StringBuilder builder = new StringBuilder();
    for (Model model : getModels()) {
      for (Topic topic : getTopics()) {
        builder.append("nohup ");
        builder.append(getSearch_command());
        builder.append(" ").append("-index").append(" ").append("lucene-index."+collection+".pos+docvectors"+(containRawDocs ? "+rawdocs" : "")).append(" \\\n");
        builder.append(" ").append("-topicreader").append(" ").append(getTopic_reader());
        builder.append(" ").append("-topics").append(" ").append(Paths.get(getTopic_root(), topic.getPath()).toString()).append(" \\\n");
        if (getSearch_options() != null) {
          for (String option : getSearch_options()) {
            builder.append(" ").append(option);
          }
        }
        if (model.getParams() != null) {
          for (String option : model.getParams()) {
            builder.append(" ").append(option);
          }
        }
        builder.append(" ").append("-output").append(" ").append("run."+collection+"."+model.getName()+"."+topic.getPath());
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
        for (Eval eval : getEvals()) {
          String evalCmd = eval.getCommand();
          String evalCmdOption = "";
          if (eval.getParams() != null) {
            for (String option : eval.getParams()) {
              evalCmdOption += " "+option;
            }
          }
          String evalCmdResidual = "";
          evalCmdResidual += " "+Paths.get(getQrels_root(), topic.getQrel());
          evalCmdResidual += " run."+collection+"."+model.getName()+"."+topic.getPath();
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
    for (Eval eval : getEvals()) {
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
