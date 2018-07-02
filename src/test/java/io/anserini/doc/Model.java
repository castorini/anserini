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
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

public class Model {
  private Map<String, Object> defaults;
  private Map<String, Map<String, Object>> collections;

  public Map<String, Object> getDefaults() {
    return defaults;
  }

  public void setDefaults(Map<String, Object> defaults) {
    this.defaults = defaults;
  }

  public Map<String, Map<String, Object>> getCollections() {
    return collections;
  }

  public void setCollections(Map<String, Map<String, Object>> collections) {
    this.collections = collections;
  }

  private Object safeGet(Map<String, Object> obj, String key) {
    return obj.getOrDefault(key, defaults.getOrDefault(key, null));
  }

  public String generateIndexingCommand(String collection) {
    Map<String, Object> config = this.collections.get(collection);
    ObjectMapper oMapper = new ObjectMapper();
    StringBuilder builder = new StringBuilder();
    builder.append("nohup ");
    builder.append(safeGet(config, "index_command"));
    builder.append(" -collection ").append(safeGet(config, "collection"));
    builder.append(" -generator ").append(safeGet(config, "generator"));
    builder.append(" -threads ").append(safeGet(config, "threads"));
    builder.append(" -input ").append("/path/to/"+collection);
    builder.append(" -index ").append("lucene-index."+safeGet(config, "name")+".pos+docvectors");
    List<String> indexParams = oMapper.convertValue(safeGet(config, "index_options"), List.class);
    for (String option : indexParams) {
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
    List<Map<String, Object>> models = oMapper.convertValue((List)safeGet(config, "models"), List.class);
    List<Map<String, String>> topics = oMapper.convertValue((List)safeGet(config, "topics"), List.class);
    for (Map<String, Object> model : models) {
      for (Map<String, String> topic : topics) {
        builder.append("nohup ");
        builder.append(safeGet(config, "search_command"));
        builder.append(" ").append("-topicreader").append(" ").append(safeGet(config, "topic_reader"));
        builder.append(" ").append("-index").append(" ").append("lucene-index."+safeGet(config, "name")+".pos+docvectors");
        builder.append(" ").append("-topic").append(" ").append(Paths.get((String)safeGet(config, "topic_root"), topic.get("path")).toString());
        builder.append(" ").append("-output").append(" ").append("run."+safeGet(config, "name")+"."+model.get("name")+"."+topic.get("path"));
        List<String> modelParams = oMapper.convertValue(model.get("params"), List.class);
        for (String option : modelParams) {
          builder.append(" ").append(option);
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
    StringBuilder builder = new StringBuilder();
    ObjectMapper oMapper = new ObjectMapper();
    List<Map<String, Object>> models = oMapper.convertValue((List)safeGet(config, "models"), List.class);
    List<Map<String, String>> topics = oMapper.convertValue((List)safeGet(config, "topics"), List.class);
    List<Map<String, Object>> evals = oMapper.convertValue((List)safeGet(config, "evals"), List.class);
    for (Map<String, Object> model : models) {
      for (Map<String, String> topic : topics) {
        for (Map<String, Object> eval : evals) {
          builder.append(eval.get("command"));
          List<String> evalParams = oMapper.convertValue(eval.get("params"), List.class);
          for (String option : evalParams) {
            builder.append(" ").append(option);
          }
          builder.append(" ").append(Paths.get((String)safeGet(config, "qrels_root"), topic.get("qrel")).toString());
          builder.append(" ").append("-output").append(" ").append("run."+safeGet(config, "name")+"."+model.get("name")+"."+topic.get("path"));
          builder.append("\n");
        }
      }
      builder.append("\n");
    }
    builder.delete(builder.lastIndexOf("\n"), builder.length());

    return builder.toString();
  }

  public String generateEffectiveness(String collection) {
    Map<String, Object> config = this.collections.get(collection);
    StringBuilder builder = new StringBuilder();
    ObjectMapper oMapper = new ObjectMapper();
    List<Map<String, Object>> models = oMapper.convertValue((List)safeGet(config, "models"), List.class);
    List<Map<String, String>> topics = oMapper.convertValue((List)safeGet(config, "topics"), List.class);
    List<Map<String, Object>> evals = oMapper.convertValue((List)safeGet(config, "evals"), List.class);
    for (Map<String, Object> eval : evals) {
      builder.append(String.format("%1$-40s|", eval.get("metric").toString().toUpperCase()));
      for (Map<String, Object> model : models) {
        builder.append(String.format(" %1$-10s|", model.get("name").toString().toUpperCase()));
      }
      builder.append("\n");
      builder.append(":").append(StringUtils.repeat("-", 39)).append("|");
      for (Map<String, Object> model : models) {
        builder.append(StringUtils.repeat("-", 11)).append("|");
      }
      builder.append("\n");
      for (int i = 0; i < topics.size(); i++) {
        builder.append(String.format("%1$-40s|", topics.get(i).get("name").toString().toUpperCase()));
        for (Map<String, Object> model : models) {
          Map<String, List<Float>> results = oMapper.convertValue(model.get("results"), Map.class);
          builder.append(String.format(" %-10.4f|", results.get(eval.get("metric").toString()).get(i)));
        }
        builder.append("\n");
      }
      builder.append("\n\n");
    }
    builder.delete(builder.lastIndexOf("\n"), builder.length());

    return builder.toString();
  }
}
