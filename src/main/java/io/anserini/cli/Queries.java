/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

package io.anserini.cli;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Collectors;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.search.topicreader.Topics;

/**
 * Minimal CLI wrapper to list available query topics.
 */
public final class Queries {
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  public static class Args {
    @Option(name = "--list", usage = "List available query topics.")
    public Boolean list = false;

    @Option(name = "--get", metaVar = "[topic]", usage = "Get all queries for a topic.")
    public String get = null;
  }

  public static class Entry {
    public final String name;
    public final String path;
    public final String reader;

    Entry(Topics topic) {
      this.name = topic.name();
      this.path = topic.path;
      this.reader = topic.readerClass.getSimpleName();
    }
  }

  private static List<Entry> getAllDetails() {
    return Arrays.stream(Topics.values())
        .map(Entry::new)
        .sorted(Comparator.comparing((entry) -> entry.name))
        .collect(Collectors.toList());
  }

  private static SortedMap<?, Map<String, String>> getAllQueriesForTopic(String topicName) {
    Topics topic = Topics.getByName(topicName);
    if (topic == null) {
      return null;
    }

    try {
      return io.anserini.search.topicreader.TopicReader.getTopics(topic);
    } catch (Exception e) {
      System.err.printf("Error: unable to read topic \"%s\": %s%n", topicName, e.getMessage());
      return null;
    }
  }

  public static void main(String[] args) {
    Args parsed = new Args();
    CmdLineParser parser = new CmdLineParser(parsed, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.printf("Error: %s%n", e.getMessage());
      System.err.printf("For help, use \"--list\" or \"--get\".%n");
      return;
    }

    boolean hasList = Boolean.TRUE.equals(parsed.list);
    boolean hasGet = parsed.get != null;
    if (hasList == hasGet) {
      System.err.println("Error: exactly one of --list or --get must be specified");
      return;
    }

    try {
      if (hasList) {
        System.out.println(JSON_MAPPER.writeValueAsString(getAllDetails()));
      } else {
        SortedMap<?, Map<String, String>> queries = getAllQueriesForTopic(parsed.get);
        if (queries == null) {
          if (Topics.getByName(parsed.get) == null) {
            System.err.printf("Error: unknown topic \"%s\"%n", parsed.get);
          }
          return;
        }
        System.out.println(JSON_MAPPER.writeValueAsString(queries));
      }
    } catch (JsonProcessingException e) {
      System.err.printf("Error: %s%n", e.getMessage());
    }
  }
}
