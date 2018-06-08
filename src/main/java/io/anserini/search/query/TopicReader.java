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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.SortedMap;

public abstract class TopicReader {
  protected Path topicFile;

  public TopicReader() {

  }

  public TopicReader(Path topicFile) {
    this.topicFile = topicFile;
  }

  public<K, V> SortedMap<K, Map<String, String>> read() throws IOException {
    InputStream topics = Files.newInputStream(topicFile, StandardOpenOption.READ);
    BufferedReader bRdr = new BufferedReader(new InputStreamReader(topics, StandardCharsets.UTF_8));
    return read(bRdr);
  }

  public<K, V> SortedMap<K, Map<String, String>> read(String str) throws IOException {
    BufferedReader bRdr = new BufferedReader(new StringReader(str));
    return read(bRdr);
  }

  abstract public<K, V> SortedMap<K, Map<String, String>> read(BufferedReader bRdr) throws IOException;
}
