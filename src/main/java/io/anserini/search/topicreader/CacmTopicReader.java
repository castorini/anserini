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

public class CacmTopicReader extends TopicReader<Integer> {
  private final String DOCNO = "<DOCNO>";
  private final String TERMINATING_DOCNO = "</DOCNO>";

  private final String DOC = "<DOC>";
  private final String TERMINATING_DOC = "</DOC>";

  private static final Pattern QUERY_ID_PATTERN =
      Pattern.compile("<DOCNO>\\s*(.*?)\\s*</DOCNO>", Pattern.DOTALL);

  public CacmTopicReader(Path topicFile) {
    super(topicFile);
  }

  @Override
  public SortedMap<Integer, Map<String, String>> read(BufferedReader bRdr) throws IOException {
    SortedMap<Integer, Map<String, String>> map = new TreeMap<>();

    try {
      boolean found = false;
      String line;
      while ((line=bRdr.readLine()) != null) {
        line = line.trim();
        if (line.startsWith(DOC)) {
          found = true;
          Map<String,String> fields = new HashMap<>();
          String qid = "";
          // continue to read DOCNO
          while ((line = bRdr.readLine()) != null) {
            if (line.startsWith(DOCNO)) {
              Matcher m = QUERY_ID_PATTERN.matcher(line);
              if (!m.find()) {
                throw new IOException("Error parsing " + line);
              }
              qid = m.group(1);
              break;
            }
          }
          StringBuilder sb = new StringBuilder();
          while ((line = bRdr.readLine()) != null) {
            if (line.startsWith(TERMINATING_DOC)) {
              fields.put("title", sb.toString());
              map.put(Integer.valueOf(qid), fields);
              break;
            }
            sb.append(line).append('\n');
          }
        }
      }
    } finally {
      bRdr.close();
    }

    return map;
  }
}
