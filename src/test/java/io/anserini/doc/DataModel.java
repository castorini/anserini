/**
 * Anserini: An information retrieval toolkit built on Lucene
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

import java.nio.file.Paths;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

public class DataModel {
  private Map<String, Object> defaults;
  private Map<String, Map<String, Object>> collections;

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
    private List<String> params;
    private Map<String, List<Float>> results;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, List<Float>> getResults() { return results; }
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

  public Map<String, Object> getDefaults() { return defaults; }
  public void setDefaults(Map<String, Object> defaults) { this.defaults = defaults; }
  public Map<String, Map<String, Object>> getCollections() { return collections; }
  public void setCollections(Map<String, Map<String, Object>> collections) { this.collections = collections; }

  private Object safeGet(Map<String, Object> obj, String key) {
    return obj.getOrDefault(key, defaults.getOrDefault(key, null));
  }

  public String generateIndexingCommand(String collection) {
    Map<String, Object> config = this.collections.get(collection);
    ObjectMapper oMapper = new ObjectMapper();
    StringBuilder builder = new StringBuilder();
    builder.append("nohup sh ");
    builder.append(safeGet(config, "index_command"));
    builder.append(" -collection ").append(safeGet(config, "collection"));
    builder.append(" -generator ").append(safeGet(config, "generator"));
    builder.append(" -threads ").append(safeGet(config, "threads"));
    builder.append(" -input ").append("/path/to/"+collection);
    builder.append(" -index ").append("lucene-index."+safeGet(config, "name")+".pos+docvectors");
    List indexParams = oMapper.convertValue(safeGet(config, "index_options"), List.class);
    for (Object option : indexParams) {
      builder.append(" ").append(option);
    }
    builder.append(String.format(" >& log.%s.pos+docvectors%s &", collection,
      indexParams.contains("-storeRawDocs") ? "+rawdocs" : ""));
    return WordUtils.wrap(builder.toString(), 80, " \\\n", false);
  }

  public String generateRankingCommand(String collection) {
    Map<String, Object> config = this.collections.get(collection);
    StringBuilder builder = new StringBuilder();
    ObjectMapper oMapper = new ObjectMapper();
    List models = oMapper.convertValue((List)safeGet(config, "models"), List.class);
    List topics = oMapper.convertValue((List)safeGet(config, "topics"), List.class);
    for (Object modelObj : models) {
      Model model = oMapper.convertValue(modelObj, Model.class);
      for (Object topicObj : topics) {
        Topic topic = oMapper.convertValue(topicObj, Topic.class);
        builder.append("nohup ");
        builder.append(safeGet(config, "search_command"));
        builder.append(" ").append("-topicreader").append(" ").append(safeGet(config, "topic_reader"));
        builder.append(" ").append("-index").append(" ").append("lucene-index."+safeGet(config, "name")+".pos+docvectors");
        builder.append(" ").append("-topic").append(" ").append(Paths.get((String)safeGet(config, "topic_root"), topic.getPath()).toString());
        builder.append(" ").append("-output").append(" ").append("run."+safeGet(config, "name")+"."+model.getName()+"."+topic.getPath());
        List modelParams = oMapper.convertValue(model.getParams(), List.class);
        if (modelParams != null) {
          for (Object option : modelParams) {
            builder.append(" ").append(option);
          }
        }
        builder.append(" &"); // nohup
        builder.append("\n");
      }
      builder.append("\n");
    }
    builder.delete(builder.lastIndexOf("\n"), builder.length());

    return builder.toString();
  }

  public String generateEvalCommand(String collection) {
    Map<String, Object> config = this.collections.get(collection);
    String allCommandsStr = "";
    Set<String> allEvalCommands = new HashSet<>();
    ObjectMapper oMapper = new ObjectMapper();
    List models = oMapper.convertValue((List)safeGet(config, "models"), List.class);
    List topics = oMapper.convertValue((List)safeGet(config, "topics"), List.class);
    List evals = oMapper.convertValue((List)safeGet(config, "evals"), List.class);
    for (Object modelObj : models) {
      Model model = oMapper.convertValue(modelObj, Model.class);
      for (Object topicObj : topics) {
        Topic topic = oMapper.convertValue(topicObj, Topic.class);
        Map<String, Map<String, List<String>>> combinedEvalCmd = new HashMap<>();
        for (Object evalObj : evals) {
          Eval eval = oMapper.convertValue(evalObj, Eval.class);
          String evalCmd = eval.getCommand();
          List evalParams = oMapper.convertValue(eval.getParams(), List.class);
          String evalCmdOption = "";
          if (evalParams != null) {
            for (Object option : evalParams) {
              evalCmdOption += " "+option;
            }
          }
          String evalCmdResidual = "";
          evalCmdResidual += " "+Paths.get((String)safeGet(config, "qrels_root"), topic.getQrel());
          evalCmdResidual += " -output run."+safeGet(config, "name")+"."+model.getName()+"."+topic.getPath();
          evalCmdResidual += "\n";
          if (eval.isCan_combine() || evalCmdOption.isEmpty()) {
            combinedEvalCmd.putIfAbsent(evalCmd, new HashMap<>());
            combinedEvalCmd.get(evalCmd).putIfAbsent(evalCmdResidual, new ArrayList<>());
            combinedEvalCmd.get(evalCmd).get(evalCmdResidual).add(evalCmdOption);
          } else {
            allCommandsStr += evalCmd + evalCmdOption + evalCmdResidual;
          }
        }
        for (Map.Entry<String, Map<String, List<String>>> entry : combinedEvalCmd.entrySet()) {
          for (Map.Entry<String, List<String>> innerEntry : entry.getValue().entrySet()) {
            allCommandsStr += entry.getKey() + String.join("", innerEntry.getValue()) + innerEntry.getKey();
          }
        }
      }
      allCommandsStr += "\n";
    }

    return allCommandsStr.substring(0, allCommandsStr.lastIndexOf("\n"));
  }

  public String generateEffectiveness(String collection) {
    Map<String, Object> config = this.collections.get(collection);
    StringBuilder builder = new StringBuilder();
    ObjectMapper oMapper = new ObjectMapper();
    List models = oMapper.convertValue((List)safeGet(config, "models"), List.class);
    List topics = oMapper.convertValue((List)safeGet(config, "topics"), List.class);
    List evals = oMapper.convertValue((List)safeGet(config, "evals"), List.class);
    for (Object evalObj : evals) {
      Eval eval = oMapper.convertValue(evalObj, Eval.class);
      builder.append(String.format("%1$-40s|", eval.getMetric().toUpperCase()));
      for (Object modelObj : models) {
        Model model = oMapper.convertValue(modelObj, Model.class);
        builder.append(String.format(" %1$-10s|", model.getName().toUpperCase()));
      }
      builder.append("\n");
      builder.append(":").append(StringUtils.repeat("-", 39)).append("|");
      for (Object modelObj : models) {
        builder.append(StringUtils.repeat("-", 11)).append("|");
      }
      builder.append("\n");
      for (int i = 0; i < topics.size(); i++) {
        Topic topic = oMapper.convertValue(topics.get(i), Topic.class);
        builder.append(String.format("%1$-40s|", topic.getName().toUpperCase()));
        for (Object modelObj : models) {
          Model model = oMapper.convertValue(modelObj, Model.class);
          builder.append(String.format(" %-10.4f|", model.getResults().get(eval.getMetric()).get(i)));
        }
        builder.append("\n");
      }
      builder.append("\n\n");
    }
    builder.delete(builder.lastIndexOf("\n"), builder.length());

    return builder.toString();
  }
}
