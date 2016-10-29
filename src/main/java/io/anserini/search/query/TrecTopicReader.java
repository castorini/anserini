package io.anserini.search.query;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.benchmark.quality.QualityQuery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class TrecTopicReader extends TopicReader{

  public TrecTopicReader(Path topicFile) {
    super(topicFile);
  }

  private final String newline = System.getProperty("line.separator");

  /**
   * Read quality queries from trec format topics file.
   * @param reader where queries are read from.
   * @return the result quality queries.
   * @throws IOException if cannot read the queries.
   */
  public QualityQuery[] readQueries(BufferedReader reader) throws IOException {
    ArrayList<QualityQuery> res = new ArrayList<>();
    StringBuilder sb;
    try {
      while (null!=(sb=read(reader,"<top>",null,false,false))) {
        HashMap<String,String> fields = new HashMap<>();
        // id
        sb = read(reader,"<num>",null,true,false);
        int k = sb.indexOf(":");
        String id = sb.substring(k+1).trim();
        // title
        sb = read(reader,"<title>",null,true,false);
        k = sb.indexOf(">");
        String title = sb.substring(k+1).trim();
        // description
        read(reader,"<desc>",null,false,false);
        sb.setLength(0);
        String line = null;
        while ((line = reader.readLine()) != null) {
          if (line.startsWith("<narr>"))
            break;
          if (sb.length() > 0) sb.append(' ');
          sb.append(line);
        }
        String description = sb.toString().trim();
        // narrative
        sb.setLength(0);
        while ((line = reader.readLine()) != null) {
          if (line.startsWith("</top>"))
            break;
          if (sb.length() > 0) sb.append(' ');
          sb.append(line);
        }
        String narrative = sb.toString().trim();
        // we got a topic!
        fields.put("title",title);
        fields.put("description",description);
        fields.put("narrative", narrative);
        QualityQuery topic = new QualityQuery(id,fields);
        res.add(topic);
      }
    } finally {
      reader.close();
    }
    // sort result array (by ID)
    QualityQuery qq[] = res.toArray(new QualityQuery[0]);
    Arrays.sort(qq);
    return qq;
  }

  // read until finding a line that starts with the specified prefix
  protected StringBuilder read (BufferedReader reader, String prefix, StringBuilder sb,
                                boolean collectMatchLine, boolean collectAll) throws IOException {
    sb = (sb==null ? new StringBuilder() : sb);
    String sep = "";
    while (true) {
      String line = reader.readLine();
      if (line==null) {
        return null;
      }
      if (line.startsWith(prefix)) {
        if (collectMatchLine) {
          sb.append(sep+line);
          sep = newline;
        }
        break;
      }
      if (collectAll) {
        sb.append(sep+line);
        sep = newline;
      }
    }
    //System.out.println("read: "+sb);
    return sb;
  }

  @Override
  public SortedMap<Integer, String> read() throws IOException {
    SortedMap<Integer, String> map = new TreeMap<>();
    // prepare topics
    InputStream topics = Files.newInputStream(topicFile, StandardOpenOption.READ);
    BufferedReader bRdr = new BufferedReader(new InputStreamReader(topics, StandardCharsets.UTF_8));
    StringBuilder sb;
    try {
      while (null!=(sb=read(bRdr,"<top>",null,false,false))) {
        HashMap<String,String> fields = new HashMap<>();
        // id
        sb = read(bRdr,"<num>",null,true,false);
        int k = sb.indexOf(":");
        String id = sb.substring(k+1).trim();
        // title
        sb = read(bRdr,"<title>",null,true,false);
        k = sb.indexOf(":");
        if (k == -1) {
          k = sb.indexOf(">");
        }
        String title = sb.substring(k+1).trim();
        // description
        read(bRdr,"<desc>",null,false,false);
        sb.setLength(0);
        String line = null;
        while ((line = bRdr.readLine()) != null) {
          if (line.startsWith("<narr>"))
            break;
          if (sb.length() > 0) sb.append(' ');
          sb.append(line);
        }
        String description = sb.toString().trim();
        // narrative
        sb.setLength(0);
        while ((line = bRdr.readLine()) != null) {
          if (line.startsWith("</top>"))
            break;
          if (sb.length() > 0) sb.append(' ');
          sb.append(line);
        }
        String narrative = sb.toString().trim();
        // we got a topic!
        fields.put("title",title);
        fields.put("description",description);
        fields.put("narrative", narrative);
        QualityQuery topic = new QualityQuery(id,fields);
        map.put(Integer.parseInt(id), fields.get("title"));
      }
    } finally {
      bRdr.close();
    }

    return map;
  }
}
