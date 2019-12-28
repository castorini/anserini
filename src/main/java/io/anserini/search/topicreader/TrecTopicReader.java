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
 * Topic reader for standard TREC <i>ad hoc</i> topics, with title, description, and narrative fields.
 */
public class TrecTopicReader extends TopicReader<Integer> {

  private final String newline = System.getProperty("line.separator");

  public TrecTopicReader(Path topicFile) {
    super(topicFile);
  }

  // read until finding a line that starts with the specified prefix
  protected StringBuilder read(BufferedReader reader, String prefix, StringBuilder sb,
                               boolean collectMatchLine, boolean collectAll) throws IOException {
    sb = (sb == null ? new StringBuilder() : sb);
    String sep = "";
    while (true) {
      String line = reader.readLine();
      if (line == null) {
        return null;
      }
      if (line.startsWith(prefix)) {
        if (collectMatchLine) {
          sb.append(sep + line);
          sep = newline;
        }
        break;
      }
      if (collectAll) {
        sb.append(sep + line);
        sep = newline;
      }
    }
    return sb;
  }

  @Override
  public SortedMap<Integer, Map<String, String>> read(BufferedReader bRdr) throws IOException {
    SortedMap<Integer, Map<String, String>> map = new TreeMap<>();
    StringBuilder sb;
    try {

      // Note that TREC topics begin with <top> (e.g., Robust04), but FIRE topics begin with a language code,
      // e.g., <top lang='bn'>, we we only search for the prefix '<top'
      while (null != (sb = read(bRdr, "<top", null, false, false))) {
        Map<String, String> fields = new HashMap<>();
        // Read the topic id
        sb = read(bRdr, "<num>", null, true, false);

        // Note that TREC topics are numbered like '<num> Number: 301'
        int k = sb.indexOf(":");
        String id = null;
        if (k != -1) {
          id = sb.substring(k + 1).trim();
        } else {
          // But, FIRE topics are numbered like '<num>176</num>', so we need to deal with both variants.
          k = sb.indexOf(">");
          id = sb.substring(k + 1).trim();
        }

        // title
        sb = read(bRdr, "<title>", null, true, false);
        k = sb.indexOf(":");
        if (k == -1) {
          k = sb.indexOf(">");
        }
        String title = sb.substring(k + 1).trim();

        //malformed titles, read again
        if (title.isEmpty()) {
          sb = read(bRdr, "", null, true, false);
          k = sb.indexOf(":");
          if (k == -1) {
            k = sb.indexOf(">");
          }
          title = sb.substring(k + 1).trim();
        }

        // Read the description...
        read(bRdr, "<desc>", null, false, false);
        sb.setLength(0);
        String line = null;
        while ((line = bRdr.readLine()) != null) {
          if (line.startsWith("<narr>"))
            break;
          if (sb.length() > 0) sb.append(' ');
          sb.append(line);
        }
        String description = sb.toString().trim();

        // Read the narrative...
        sb.setLength(0);
        if (line.endsWith("</narr>")) {
          // This means that the narrative is on a single line, like '<narr>....</narr>'
          sb.append(line);
        } else {
          // Otherwise, read until closing '</top>' tag.
          while ((line = bRdr.readLine()) != null) {
            if (line.startsWith("</top>"))
              break;
            if (sb.length() > 0) sb.append(' ');
            sb.append(line);
          }
        }
        String narrative = sb.toString().trim();

        // we got a topic!
        // this is for core track 2018 fix
        id = id.replaceAll("</num>", "").trim();
        title = title.replaceAll("</title>", "");
        description = description.replaceAll("</desc>", "");
        narrative = narrative.replaceAll("</narr>", "");
        // this is for core track 2018 fix
        fields.put("title", title);
        fields.put("description", description);
        fields.put("narrative", narrative);

        // CLIR topics, e.g., TREC 2002 Monolingual Arabic, may have a prefix, e.g., "AR26".
        // This is a hack around that:
        id = id.replaceAll("[^0-9]", "");

        map.put(Integer.valueOf(id), fields);
      }
    } finally {
      bRdr.close();
    }

    return map;
  }
}
