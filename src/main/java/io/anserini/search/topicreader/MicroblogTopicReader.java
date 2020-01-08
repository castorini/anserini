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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MicroblogTopicReader extends TopicReader<Integer> {
  public MicroblogTopicReader(Path topicFile) {
    super(topicFile);
  }

  private static final Pattern TOP_PATTERN =
      Pattern.compile("<top(.*?)</top>", Pattern.DOTALL);
  private static final Pattern NUM_PATTERN =
      Pattern.compile("<num> Number: MB(\\d+) </num>", Pattern.DOTALL);
  // TREC 2011 topics uses <title> tag
  private static final Pattern TITLE_PATTERN =
      Pattern.compile("<title>\\s*(.*?)\\s*</title>", Pattern.DOTALL);
  // TREC 2012 topics use <query> tag
  private static final Pattern TITLE_PATTERN2 =
      Pattern.compile("<query>\\s*(.*?)\\s*</query>", Pattern.DOTALL);
  private static final Pattern TWEETTIME_PATTERN =
      Pattern.compile("<querytweettime>\\s*(\\d+)\\s*</querytweettime>", Pattern.DOTALL);

  /**
   * Read topics of TREC Microblog Tracks from 2011 to 2014 including:
   * topics.microblog2011.txt
   * topics.microblog2012.txt
   * topics.microblog2013.txt
   * topics.microblog2014.txt
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
      if (line.startsWith("<query>") && line.endsWith("</query>")
          || line.startsWith("<title>") && line.endsWith("</title>")) {
        m = TITLE_PATTERN.matcher(line);
        if (!m.find()) {
          m = TITLE_PATTERN2.matcher(line);
          if (!m.find()) {
            throw new IOException("Error parsing " + line);
          }
        }
        fields.put("title", m.group(1));
      }
      if (line.startsWith("<querytweettime>") && line.endsWith("</querytweettime>")) {
        m = TWEETTIME_PATTERN.matcher(line);
        if (!m.find()) {
          throw new IOException("Error parsing " + line);
        }
        fields.put("time", m.group(1));
      }
      if (line.startsWith("</top>")) {
        map.put(Integer.valueOf(number), fields);
        fields = new HashMap<>();
      }
    }

    return map;
  }
}
