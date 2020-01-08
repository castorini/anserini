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

package io.anserini.search.topicreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Topic reader for simple web topics, like the efficiency queries from the TREC 2005 Terabyte Track:
 *
 * <pre>
 * 1:pierson s twin lakes marina
 * 2:nurseries in woodbridge new jersey
 * 3:miami white pages
 * 4:delta air lines
 * 5:hsn
 * 6:ironman ivan stewart s super off road
 * 7:pajaro carpintero
 * 8:kitchen canister sets
 * 9:buy pills online
 * 10:hotel meistertrunk
 * ...
 * </pre>
 */
public class WebTopicReader extends TopicReader<Integer> {
  public WebTopicReader(Path topicFile) {
    super(topicFile);
  }

  @Override
  public SortedMap<Integer, Map<String, String>> read(BufferedReader bRdr) throws IOException {
    SortedMap<Integer, Map<String, String>> map = new TreeMap<>();

    String line;
    while ((line = bRdr.readLine()) != null) {
      line = line.trim();
      String[] arr = line.split(":");

      Map<String,String> fields = new HashMap<>();
      fields.put("title", arr[1]);
      map.put(Integer.valueOf(arr[0]), fields);
    }

    return map;
  }
}
