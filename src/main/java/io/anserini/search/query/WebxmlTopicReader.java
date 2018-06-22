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

package io.anserini.search.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class WebxmlTopicReader extends TopicReader {
  public WebxmlTopicReader(Path topicFile) {
    super(topicFile);
  }

  private String extract(String line, String tag) {
    int i = line.indexOf(tag);
    if (i == -1) throw new IllegalArgumentException("line does not contain the tag : " + tag);
    int j = line.indexOf("\"", i + tag.length() + 2);
    if (j == -1) throw new IllegalArgumentException("line does not contain quotation");
    return line.substring(i + tag.length() + 2, j);
  }

  /**
   * Read topics of TREC Web Tracks from 2009 to 2014 including:
   * topics.web.1-50.txt
   * topics.web.51-100.txt
   * topics.web.101-150.txt
   * topics.web.151-200.txt
   * topics.web.201-250.txt
   * topics.web.251-300.txt
   * @return SortedMap where keys are query/topic IDs and values are title portions of the topics
   * @throws IOException
   */
  @Override
  public SortedMap<Integer, Map<String, String>> read(BufferedReader bRdr) throws IOException {
    SortedMap<Integer, Map<String, String>> map = new TreeMap<>();
    Map<String,String> fields = new HashMap<>();

    String number = "";
    String query = "";

    String line;

    while ((line = bRdr.readLine()) != null) {
      line = line.trim();
      if (line.startsWith("<topic")) {
        number = extract(line, "number");
      }
      if (line.startsWith("<query>") && line.endsWith("</query>")) {
        query = line.substring(7, line.length() - 8).trim();
        fields.put("title", query);
      }
      if (line.startsWith("</topic>")) {
        map.put(Integer.valueOf(number), fields);
        fields = new HashMap<>();
      }
    }

    return map;
  }
}
