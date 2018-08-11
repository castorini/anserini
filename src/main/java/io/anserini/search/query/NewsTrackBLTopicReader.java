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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Topic reader for TREC2018 news track background linking task
 */
public class NewsTrackBLTopicReader extends TopicReader<Integer> {
  public NewsTrackBLTopicReader(Path topicFile) {
    super(topicFile);
  }

  private static final Pattern TOP_PATTERN =
      Pattern.compile("<top(.*?)</top>", Pattern.DOTALL);
  private static final Pattern NUM_PATTERN =
      Pattern.compile("<num> Number: (\\d+) </num>", Pattern.DOTALL);
  // TREC 2011 topics uses <title> tag
  private static final Pattern DOCID_PATTERN =
      Pattern.compile("<docid>\\s*(.*?)\\s*</docid>", Pattern.DOTALL);
  // TREC 2012 topics use <query> tag
  private static final Pattern URL_PATTERN =
      Pattern.compile("<url>\\s*(.*?)\\s*</url>", Pattern.DOTALL);

  /**
   * @return SortedMap where keys are query/topic IDs and values are title portions of the topics
   * @throws IOException any io exception
   */
  @Override
  public SortedMap<Integer, Map<String, String>> read(BufferedReader bRdr) throws IOException {
    SortedMap<Integer, Map<String, String>> map = new TreeMap<>();
    Map<String,String> fields = new HashMap<>();

    String number = "";
    Matcher m;
    String line;
    while ((line = bRdr.readLine()) != null) {
      line = line.trim();
      if (line.startsWith("<num>") && line.endsWith("</num>")) {
        m = NUM_PATTERN.matcher(line);
        if (!m.find()) {
          throw new IOException("Error parsing " + line);
        }
        number = m.group(1);
      }
      if (line.startsWith("<docid>") && line.endsWith("</docid>")) {
        m = DOCID_PATTERN.matcher(line);
        if (!m.find()) {
          throw new IOException("Error parsing " + line);
        }
        fields.put("title", m.group(1));
      }
      if (line.startsWith("<url>") && line.endsWith("</url>")) {
        m = URL_PATTERN.matcher(line);
        if (!m.find()) {
          throw new IOException("Error parsing " + line);
        }
        fields.put("url", m.group(1));
      }
      if (line.startsWith("</top>")) {
        map.put(Integer.valueOf(number), fields);
        fields = new HashMap<>();
      }
    }

    return map;
  }
}
