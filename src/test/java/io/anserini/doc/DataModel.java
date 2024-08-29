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
  private static final String INDEX_COMMAND = "bin/run.sh io.anserini.index.IndexCollection";
  private static final String INDEX_HNSW_COMMAND = "bin/run.sh io.anserini.index.IndexHnswDenseVectors";
  private static final String INDEX_FLAT_COMMAND = "bin/run.sh io.anserini.index.IndexFlatDenseVectors";
  private static final String INDEX_INVERTED_DENSE_COMMAND = "bin/run.sh io.anserini.index.IndexInvertedDenseVectors";

  private static final String SEARCH_COMMAND = "bin/run.sh io.anserini.search.SearchCollection";
  private static final String SEARCH_HNSW_COMMAND = "bin/run.sh io.anserini.search.SearchHnswDenseVectors";
  private static final String SEARCH_FLAT_COMMAND = "bin/run.sh io.anserini.search.SearchFlatDenseVectors";
  private static final String SEARCH_INVERTED_DENSE_COMMAND = "bin/run.sh io.anserini.search.SearchInvertedDenseVectors";

  private String corpus;
  private String corpus_path;

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

  private String download_url;
  private String download_checksum;
  private String download_corpus;

  public String getDownload_url() {
    return download_url;
  }

  public void setDownload_url(String download_url) {
    this.download_url = download_url;
  }

  public String getDownload_checksum() {
    return download_checksum;
  }

  public void setDownload_checksum(String download_checksum) {
    this.download_checksum = download_checksum;
  }

  public String getDownload_corpus() {
    return download_corpus;
  }

  public void setDownload_corpus(String download_corpus) {
    this.download_corpus = download_corpus;
  }

  private String index_path;
  private String index_type;
  private String collection_class;
  private String generator_class;
  private int index_threads;
  private String index_options;
  private Map<String, Long> index_stats;

  public String getIndex_path() {
    return index_path;
  }

  public void setIndex_path(String index_path) {
    this.index_path = index_path;
  }

  public String getIndex_type() {
    return index_type;
  }

  public void setIndex_type(String index_type) {
    this.index_type = index_type;
  }

  public String getCollection_class() {
    return collection_class;
  }

  public void setCollection_class(String collection_class) {
    this.collection_class = collection_class;
  }

  public String getGenerator_class() {
    return generator_class;
  }

  public void setGenerator_class(String generator_class) {
    this.generator_class = generator_class;
  }

  public int getIndex_threads() {
    return index_threads;
  }

  public void setIndex_threads(int index_threads) {
    this.index_threads = index_threads;
  }

  public String getIndex_options() {
    return index_options;
  }

  public void setIndex_options(String index_options) {
    this.index_options = index_options;
  }

  public Map<String, Long> getIndex_stats() {
    return index_stats;
  }

  public void setIndex_stats(Map<String, Long> index_stats) {
    this.index_stats = index_stats;
  }

  private String topic_reader;

  public String getTopic_reader() {
    return topic_reader;
  }

  public void setTopic_reader(String topic_reader) {
    this.topic_reader = topic_reader;
  }

  private List<Metric> metrics;
  private List<Model> models;
  private List<Topic> topics;
  private List<Conversion> conversions;

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

  public List<Conversion> getConversions() {
    return conversions;
  }

  public void setConversions(List<Conversion> conversions) {
    this.conversions = conversions;
  }

  static public class Topic {
    private String name;
    private String id;
    private String path;
    private String qrel;
    private String topic_reader;
    private String convert_params;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getQrel() { return qrel; }
    public void setQrel(String qrel) { this.qrel = qrel; }
    public String getTopic_reader() { return topic_reader; }
    public void setTopic_reader(String topic_reader) { this.topic_reader = topic_reader; }
    public String getConvert_params() { return convert_params; }
    public void setConvert_params(String convert_params) { this.convert_params = convert_params; }
  }

  static public class Model {
    private String name;
    private String display;
    private String type;
    private String params;
    private Map<String, List<Float>> results;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDisplay() { return display; }
    public void setDisplay(String display) { this.display = display; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Map<String, List<Float>> getResults() { return results; }
    public void setResults(Map<String, List<Float>> results) { this.results = results; }
    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }
  }

  static public class Conversion {
    private String command;
    private String in_file_ext;
    private String out_file_ext;
    private String params;

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    public String getIn_file_ext() { return in_file_ext; }
    public void setIn_file_ext(String in_file_ext) { this.in_file_ext = in_file_ext; }
    public String getOut_file_ext() { return out_file_ext; }
    public void setOut_file_ext(String out_file_ext) { this.out_file_ext = out_file_ext; }
    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }
  }

  static public class Metric {
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
    String indexCommand = INDEX_COMMAND;
    if ("hnsw".equals(getIndex_type())) {
      indexCommand = INDEX_HNSW_COMMAND;
    } else if ("flat".equals(getIndex_type())) {
      indexCommand = INDEX_FLAT_COMMAND;
    } else if ("inverted-dense".equals(getIndex_type())) {
      indexCommand = INDEX_INVERTED_DENSE_COMMAND;
    }

    return indexCommand + " \\\n" +
        "  -threads " + getIndex_threads() + " \\\n" +
        "  -collection " + getCollection_class() + " \\\n" +
        "  -input " + "/path/to/" + collection + " \\\n" +
        "  -generator " + getGenerator_class() + " \\\n" +
        "  -index " + getIndex_path() + " \\\n" +
        (getIndex_options().isEmpty() ? "" : "  " + getIndex_options() + " \\\n" ) +
        String.format("  >& logs/log.%s &", collection);
  }

  private String generateRunFile(String collection, Model model, Topic topic) {
    // Strip suffixes (e.g., gz) to avoid confusion.
    String modifiedPath = topic.getPath();
    if (modifiedPath.endsWith(".gz")) {
      modifiedPath = modifiedPath.substring(0, modifiedPath.lastIndexOf(".gz"));
    }
    if (modifiedPath.endsWith(".txt")) {
      modifiedPath = modifiedPath.substring(0, modifiedPath.lastIndexOf(".txt"));
    }
    if (modifiedPath.endsWith(".tsv")) {
      modifiedPath = modifiedPath.substring(0, modifiedPath.lastIndexOf(".tsv"));
    }

    return "runs/run."+collection+"."+model.getName()+"."+modifiedPath+ ".txt";
  }

  public String generateRankingCommand(String collection) {
    StringBuilder builder = new StringBuilder();
    for (Model model : getModels()) {
      for (Topic topic : getTopics()) {
        String searchCommand = SEARCH_COMMAND;
        if ("hnsw".equals(model.getType())) {
          searchCommand = SEARCH_HNSW_COMMAND;
        } else if ("flat".equals(model.getType())) {
          searchCommand = SEARCH_FLAT_COMMAND;
        } else if ("inverted-dense".equals(model.getType())) {
          searchCommand = SEARCH_INVERTED_DENSE_COMMAND;
        }
        builder.append(searchCommand).append(" \\\n");
        builder.append("  -index").append(" ").append(getIndex_path()).append(" \\\n");
        builder.append("  -topics").append(" ").append(Paths.get("tools/topics-and-qrels", topic.getPath())).append(" \\\n");
        builder.append("  -topicReader").append(" ").append((topic.getTopic_reader() == null) ? getTopic_reader() : topic.getTopic_reader()).append(" \\\n");
        builder.append("  -output").append(" ").append(generateRunFile(collection, model, topic)).append(" \\\n");
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

  public String generateConvertingCommand(String collection) {
    StringBuilder builder = new StringBuilder();
    if(getConversions() != null){
      for(Conversion conversion : getConversions()) {
        for (Model model : getModels()) {
          for (Topic topic : getTopics()) {
            builder.append(conversion.getCommand()).append(" \\\n");
            builder.append("  --index").append(" ").append(getIndex_path()).append(" \\\n");
            builder.append("  --topics").append(" ").append(topic.getId()).append(" \\\n");
            builder.append("  --input").append(" ").append(generateRunFile(collection, model, topic)).append((conversion.getIn_file_ext() == null) ? "" : conversion.getIn_file_ext()).append(" \\\n");
            builder.append("  --output").append(" ").append(generateRunFile(collection, model, topic)).append(conversion.getOut_file_ext()).append(" \\\n");
            if (conversion.getParams() != null) {
              builder.append("  ").append(conversion.getParams());
            }
            if (topic.getConvert_params() != null) {
              builder.append("  ").append(topic.getConvert_params());
            }
            builder.append(" &"); // nohup
            builder.append("\n");
          }
          builder.append("\n");
        }
      }
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
            evalCmdOption += " " + eval.getParams();
          }
          String evalCmdResidual = "";
          if(topic.getQrel() != null){
            evalCmdResidual += " " + Paths.get("tools/topics-and-qrels", topic.getQrel());
          }
          evalCmdResidual += " " + generateRunFile(collection, model, topic);
          List<Conversion> conversions = getConversions();
          if(conversions != null){
            Conversion lastConversion = conversions.get(conversions.size() - 1);
            evalCmdResidual += lastConversion.getOut_file_ext();
          }
          evalCmdResidual += "\n";
          if (eval.isCan_combine() || evalCmdOption.isEmpty()) {
            combinedEvalCmd.putIfAbsent(evalCmd, new HashMap<>());
            combinedEvalCmd.get(evalCmd).putIfAbsent(evalCmdResidual, new ArrayList<>());
            combinedEvalCmd.get(evalCmd).get(evalCmdResidual).add(evalCmdOption);
          } else {
            builder.append(evalCmd).append(evalCmdOption).append(evalCmdResidual);
          }
        }
        for (Map.Entry<String, Map<String, List<String>>> entry : combinedEvalCmd.entrySet()) {
          for (Map.Entry<String, List<String>> innerEntry : entry.getValue().entrySet()) {
            builder.append(entry.getKey()).append(String.join("", innerEntry.getValue())).append(innerEntry.getKey());
          }
        }
      }
      builder.append("\n");
    }

    return builder.toString().trim();
  }

  public String generateEffectiveness(String collection) {
    int cnt = 0;
    StringBuilder builder = new StringBuilder();
    for (Metric eval : getMetrics()) {
      builder.append(String.format("| %1$-109s|", String.format("**%s**", eval.getMetric())));
      for (Model model : getModels()) {
        if (model.getDisplay() == null) {
          builder.append(String.format(" %1$-10s|", String.format("**%s**", model.getName())));
        } else {
          builder.append(String.format(" %1$-10s|", String.format("**%s**", model.getDisplay())));
        }
      }
      builder.append("\n");
      // Only print for the first "block" of the table.
      if (cnt == 0) {
        builder.append("|:").append(StringUtils.repeat("-", 109)).append("|");
        for (Model model : getModels()) {
          builder.append(StringUtils.repeat("-", 11)).append("|");
        }
        builder.append("\n");
      }
      for (int i = 0; i < topics.size(); i++) {
        Topic topic = getTopics().get(i);
        builder.append(String.format("| %1$-109s|", topic.getName()));
        for (Model model : getModels()) {
          // 3 digits for HNSW, 4 otherwise:
          if ("hnsw".equals(getIndex_type())) {
            builder.append(String.format(" %-10.3f|", model.getResults().get(eval.getMetric()).get(i)));
          } else {
            builder.append(String.format(" %-10.4f|", model.getResults().get(eval.getMetric()).get(i)));
          }
        }
        builder.append("\n");
      }
      cnt++;
    }

    return builder.toString().trim();
  }
}
