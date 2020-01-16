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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SuppressWarnings("unchecked")
public class JDIQ2018EffectivenessDocsTest {
  static class Model {
    private Map<String, Object> models;
    public Map<String, Object> getModels() {
      return models;
    }
    public void setModels(Map<String, Object> models) {
      this.models = models;
    }
    public Map<String, Object> transform() {
      Map<String, Object> converted = new HashMap<>();
      for (Map.Entry<String, Object> entry1 : models.entrySet()) {
        String model = entry1.getKey();
        Map<String, Object> value = (Map<String, Object>)entry1.getValue();
        Map<String, Object> collection_performance = (Map<String, Object>)value.get("expected");
        for (Map.Entry<String, Object> entry2 : collection_performance.entrySet()) {
          String collection = entry2.getKey();
          converted.putIfAbsent(collection, new HashMap<String, Object>());
          Map<String, Object> metric_performance = (Map<String, Object>)entry2.getValue();
          for (Map.Entry<String, Object> entry3 : metric_performance.entrySet()) {
            String metric = entry3.getKey();
            ((Map<String, Object>)converted.get(collection)).putIfAbsent(metric, new HashMap<String, Object>());
            Map<String, Object> topic_performance = (Map<String, Object>)entry3.getValue();
            for (Map.Entry<String, Object> entry4 : topic_performance.entrySet()) {
              String topic = entry4.getKey();
              ((Map<String, Object>)((Map<String, Object>)converted.get(collection)).get(metric)).putIfAbsent(topic, new HashMap<String, Float>());
              Double performance = (Double) entry4.getValue();
              ((Map<String, Double>)(((Map<String, Object>)((Map<String, Object>)converted.get(collection)).get(metric)).get(topic))).put(model, performance);
            }
          }
        }
      }
      return converted;
    }
    public String generateEffectiveness() {
      Map<String, Object> data = transform();
      StringBuilder builder = new StringBuilder();
      for (String collection: Arrays.asList(new String[] { "disk12", "robust04", "robust05", "core17",
          "wt10g", "gov2", "cw09b", "cw12b13", "mb11", "mb13"})) {
        builder.append("#### "+collection+"\n");
        for (Map.Entry<String, Object> entry2 : ((Map<String, Object>)data.get(collection)).entrySet()) {
          String metric = entry2.getKey();
          builder.append(String.format("%1$-40s|", metric.toUpperCase()));
          for (Map.Entry<String, Object> entry3 : ((Map<String, Object>)entry2.getValue()).entrySet()) {
            String topic = entry3.getKey();
            for (Map.Entry<String, Object> entry4 : ((Map<String, Object>)entry3.getValue()).entrySet()) {
              String model = entry4.getKey();
              builder.append(String.format(" %1$-10s|", model.toUpperCase()));
            }
            break;
          }
          builder.append("\n");
          builder.append(":").append(StringUtils.repeat("-", 39)).append("|");
          for (Map.Entry<String, Object> entry3 : ((Map<String, Object>)entry2.getValue()).entrySet()) {
            String topic = entry3.getKey();
            for (Map.Entry<String, Object> entry4 : ((Map<String, Object>)entry3.getValue()).entrySet()) {
              String model = entry4.getKey();
              builder.append(StringUtils.repeat("-", 11)).append("|");
            }
            break;
          }
          builder.append("\n");
          for (Map.Entry<String, Object> entry3 : ((Map<String, Object>)entry2.getValue()).entrySet()) {
            String topic = entry3.getKey();
            builder.append(String.format("%1$-40s|", topic));
            for (Map.Entry<String, Object> entry4 : ((Map<String, Object>)entry3.getValue()).entrySet()) {
              String model = entry4.getKey();
              Double value = (Double)entry4.getValue();
              builder.append(String.format(" %-10.4f|", value));
            }
            builder.append("\n");
          }
          builder.append("\n\n");
        }
      }
      builder.delete(builder.lastIndexOf("\n"), builder.length());
    
      return builder.toString();
    }
  }
  
  @Test
  public void main() throws Exception {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    URL yaml = JDIQ2018EffectivenessDocsTest.class.getResource("/jdiq2018/models.yaml");
    Model data = mapper.readValue(new File(yaml.toURI()), Model.class);
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("results", data.generateEffectiveness());
    StrSubstitutor sub = new StrSubstitutor(valuesMap);
    URL template = GenerateRegressionDocsTest.class.getResource("/jdiq2018/doc.template");
    Scanner scanner = new Scanner(new File(template.toURI()), "UTF-8");
    String text = scanner.useDelimiter("\\A").next();
    scanner.close();
    String resolvedString = sub.replace(text);
    FileUtils.writeStringToFile(new File("docs/experiments-jdiq2018.md"),
        resolvedString, "UTF-8");
  }
}
