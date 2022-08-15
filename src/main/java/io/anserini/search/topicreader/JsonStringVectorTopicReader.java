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

 
package io.anserini.search.topicreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonStringVectorTopicReader extends TopicReader<String> {

  public JsonStringVectorTopicReader(Path topicFile) {
    super(topicFile);
  }

  @Override
  public SortedMap<String, Map<String, String>> read(BufferedReader reader) throws IOException {
    SortedMap<String, Map<String, String>> map = new TreeMap<>();
    String line;
    ObjectMapper mapper = new ObjectMapper();
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      JsonNode lineNode = mapper.readerFor(JsonNode.class).readTree(line);
      String topicID = lineNode.get("qid").asText();
      Map<String, String> fields = new HashMap<>();
      fields.put("vector", lineNode.get("vector").toString());
      map.put(topicID, fields);
    }
    return map;
  }
}
