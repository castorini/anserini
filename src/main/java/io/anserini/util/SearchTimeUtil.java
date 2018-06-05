package io.anserini.util;

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

import io.anserini.rerank.IdentityReranker;
import io.anserini.rerank.RerankerCascade;
import io.anserini.search.SearchCollection;
import io.anserini.search.query.TopicReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.BM25Similarity;


/**
 * Utility to capture Search/Execution Times.
 */
public class SearchTimeUtil {

  public static void main(String[] args) throws IOException, ParseException, ClassNotFoundException,
          NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

    if (args.length != 1) {
      System.err.println("Usage: SearchTimeUtil <indexDir>");
      System.err.println("indexDir: index directory");
      System.exit(1);
    }

    String[] topics = {"topics.web.1-50.txt", "topics.web.51-100.txt", "topics.web.101-150.txt", "topics.web.151-200.txt", "topics.web.201-250.txt", "topics.web.251-300.txt"};

    SearchCollection searcher = new SearchCollection(args[0]);

    for (String topicFile : topics) {
      Path topicsFile = Paths.get("src/resources/topics-and-qrels/", topicFile);
      TopicReader tr = (TopicReader)Class.forName("io.anserini.search.query."+"Webxml"+"TopicReader")
              .getConstructor(Path.class).newInstance(topicsFile);
      SortedMap<String, Map<String, String>> queries = tr.read();
      for (int i = 1; i <= 3; i++) {
        final long start = System.nanoTime();
        String submissionFile = File.createTempFile(topicFile + "_" + i, ".tmp").getAbsolutePath();
        RerankerCascade cascade = new RerankerCascade();
        cascade.add(new IdentityReranker());
        searcher.search(queries, "title", submissionFile, new BM25Similarity(0.9f, 0.4f), 1000, cascade);
        final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        System.out.println(topicFile + "_" + i + " search completed in " + DurationFormatUtils.formatDuration(durationMillis, "mm:ss:SSS"));
      }
    }

    searcher.close();
  }
}
