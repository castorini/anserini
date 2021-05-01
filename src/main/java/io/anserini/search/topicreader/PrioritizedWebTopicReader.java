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

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class PrioritizedWebTopicReader extends TopicReader<Integer> {

  public PrioritizedWebTopicReader(Path topicFile) {
    super(topicFile);
  }
  
  @Override
  public SortedMap<Integer, Map<String, String>> read(BufferedReader bRdr) throws IOException {
    SortedMap<Integer, Map<String, String>> ret =  new TreeMap<>();

    String line;
    while ((line = bRdr.readLine()) != null) {
      Map<String,String> fields = new HashMap<>();
      fields.put("title", StringUtils.substringAfterLast(line.trim(), ":"));
      fields.put("priority", StringUtils.substringBetween(line.trim(), ":"));
      ret.put(Integer.valueOf(StringUtils.substringBefore(line.trim(), ":")), fields);
    }

    return ret;
  }
}
