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
 * Topic reader for queries in tsv format, such as the MS MARCO queries.
 *
 * <pre>
 * 174249 does xpress bet charge to deposit money in your account
 * 320792 how much is a cost to run disneyland
 * 1090270  botulinum definition
 * 1101279	do physicians pay for insurance from their salaries?
 * 201376 here there be dragons comic
 * 54544  blood diseases that are sexually transmitted
 * ...
 * </pre>
 */
public class TsvIntTopicReader extends TopicReader<Integer> {
  public TsvIntTopicReader(Path topicFile) {
    super(topicFile);
  }

  @Override
  public SortedMap<Integer, Map<String, String>> read(BufferedReader reader) throws IOException {
    SortedMap<Integer, Map<String, String>> map = new TreeMap<>();

    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      String[] arr = line.split("\\t");

      Map<String,String> fields = new HashMap<>();
      fields.put("title", arr[1].trim());
      map.put(Integer.valueOf(arr[0]), fields);
    }

    return map;
  }
}
