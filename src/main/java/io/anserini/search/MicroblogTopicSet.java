/**
 * Twitter Tools
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

package io.anserini.search;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MicroblogTopicSet implements Iterable<MicroblogTopic>{
  private static final Logger LOG = LogManager.getLogger(MicroblogTopicSet.class);

  private List<MicroblogTopic> queries = Lists.newArrayList();

  private MicroblogTopicSet() {}

  private void add(MicroblogTopic q) {
    queries.add(q);
  }

  @Override
  public Iterator<MicroblogTopic> iterator() {
    return queries.iterator();
  }

  private static final Pattern TOP_PATTERN = Pattern.compile("<top(.*?)</top>", Pattern.DOTALL);
  private static final Pattern NUM_PATTERN = Pattern.compile("<num> Number: (MB\\d+) </num>", Pattern.DOTALL);

  // TREC 2011 topics uses <title> tag
  private static final Pattern TITLE_PATTERN = Pattern.compile("<title>\\s*(.*?)\\s*</title>", Pattern.DOTALL);
  // TREC 2012 topics use <query> tag
  private static final Pattern TITLE_PATTERN2 = Pattern.compile("<query>\\s*(.*?)\\s*</query>", Pattern.DOTALL);

  private static final Pattern TWEETTIME_PATTERN = Pattern.compile("<querytweettime>\\s*(\\d+)\\s*</querytweettime>", Pattern.DOTALL);

  public static MicroblogTopicSet fromFile(File f) throws IOException {
    Preconditions.checkNotNull(f);
    Preconditions.checkArgument(f.exists());

    String s = Joiner.on("\n").join(Files.readLines(f, Charsets.UTF_8));
    MicroblogTopicSet queries = new MicroblogTopicSet();

    Matcher matcher = TOP_PATTERN.matcher(s);
    while (matcher.find()) {
      String top = matcher.group(0);

      Matcher m = NUM_PATTERN.matcher(top);
      if (!m.find()) {
        throw new IOException("Error parsing " + f);
      }
      String id = m.group(1);
      // Topics from 2012 are inconsistently numbered,
      // e.g., MB051 should match the qrels, which has MB51
      if (id.matches("MB0\\d\\d")) {
        id = id.replace("MB0", "MB");
      }

      m = TITLE_PATTERN.matcher(top);
      if (!m.find()) {
        m = TITLE_PATTERN2.matcher(top);
        if (!m.find()) {
          throw new IOException("Error parsing " + f);
        }
      }
      String text = m.group(1);

      m = TWEETTIME_PATTERN.matcher(top);
      if (!m.find()) {
        throw new IOException("Error parsing " + f);
      }
      long time = Long.parseLong(m.group(1));
      queries.add(new MicroblogTopic(id, text, time));
    }
    return queries;
  }

  public Map<String, String> toMap() {
      HashMap<String, String> topicsMap = new HashMap<>(this.queries.size());

      // The twitter topics are labeled with MBXX as the query id while the qrel files use integers to
      // denote query ids, so we will conform to fomat here
      for (MicroblogTopic topic : this.queries) {
          topicsMap.put(topic.getId().replace("MB0", "").replace("MB",""), topic.getQuery());
      }

      return topicsMap;
  }
}
