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

import io.anserini.collection.CAR18Collection;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CAR18TopicReader extends TopicReader {
  private static final Logger LOG = LogManager.getLogger(CAR18Collection.class);

  public CAR18TopicReader(Path topicFile) {
    super(topicFile);
  }

  /**
   * Read topics of TREC CAR Track 2018
   * @return SortedMap where keys are query/topic IDs and values are title portions of the topics
   * @throws IOException
   */
  @Override
  public SortedMap<String, Map<String, String>> read(BufferedReader bRdr) throws IOException {
    SortedMap<String, Map<String, String>> map = new TreeMap<>();

    String line;
    while ((line = bRdr.readLine()) != null) {
      Map<String,String> fields = new HashMap<>();
      String id;
      line = line.trim();
      if (line.startsWith("enwiki:")) {
        id = line;
        String title = String.join(" ", line.substring(7).split("\\/|(%20)")); //
        fields.put("title", title);
        map.put(id, fields);
      }
    }
    return map;
  }
}
