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

package io.anserini.search.topicreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class CarTopicReader extends TopicReader {

  public CarTopicReader(Path topicFile) {
    super(topicFile);
  }

  /**
   * Read topics in the topic file of TREC CAR Track 2018
   * @return SortedMap where keys are query/topic IDs and values are title portions of the topics
   * @throws IOException any io exception
   */
  @Override
  public SortedMap<String, Map<String, String>> read(BufferedReader bRdr) throws IOException {
    SortedMap<String, Map<String, String>> map = new TreeMap<>();

    String line;
    while ((line = bRdr.readLine()) != null) {
      Map<String,String> fields = new HashMap<>();
      line = line.trim();
      // topic file
      if (line.indexOf('%') > -1 || line.indexOf('/') > -1) {
        String id = line;
        String title = null;
        try {
          String title_url;
          if (line.startsWith("enwiki:")) {
            title_url = line.substring(7);
          }
          else {
            title_url = line;
          }
          title_url = title_url.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
          title_url = title_url.replaceAll("\\+", "%2B");
          title = java.net.URLDecoder.decode(title_url, "utf-8")
              .replace("/", " ").replace("(", " ").replace(")", " ");
          fields.put("title", title);
          map.put(id, fields);
        } catch (UnsupportedEncodingException e) {
          System.out.println(line);
          e.printStackTrace();
        } catch (IllegalArgumentException e) {
          System.out.println(line);
          e.printStackTrace();
        }
      }
      // title file
      else if (line.length() != 0) {
        String title = line;
        String id = "enwiki:" + java.net.URLEncoder.encode(line, "utf-8")
            .replace("+", "%20").replace("%28", "(")
            .replace("%29", ")").replace("%27", "'")
            .replace("%2C", ",");
        fields.put("title", title);
        map.put(id, fields);
      }
    }
    return map;
  }
}
