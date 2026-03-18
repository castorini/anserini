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

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.search.topicreader.TopicReader;
import io.anserini.search.topicreader.Topics;
import io.anserini.util.LoggingBootstrap;

public final class TopicsCatalog {
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  public static class Args {
    @Option(name = "--list", usage = "List available topics.")
    public boolean list = false;

    @Option(name = "--filter", metaVar = "[regexp]", usage = "Filter topics by regular expression.")
    public String filter = null;

    @Option(name = "--get", metaVar = "[topics]", usage = "Enumerate topics.")
    public String get = null;

    @Option(name = "--help", help = true, usage = "Print this help message and exit.")
    public boolean help = false;
  }

  private static final String[] argsOrdering = new String[] {
      "--list", "--filter", "--get", "--help"};

  public static void main(String[] args) {
    LoggingBootstrap.installJulToSlf4jBridge();

    Args parsedArgs = new Args();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(String.format("Error: %s", e.getMessage()));
      CliUtils.printUsage(parser, TopicsCatalog.class, argsOrdering);
      return;
    }

    if (parsedArgs.help) {
      CliUtils.printUsage(parser, TopicsCatalog.class, argsOrdering);
      return;
    }

    if (parsedArgs.list == (parsedArgs.get != null)) {
      System.err.println("Error: exactly one of --list or --get must be specified");
      CliUtils.printUsage(parser, TopicsCatalog.class, argsOrdering);
      return;
    }

    if (!parsedArgs.list && parsedArgs.filter != null) {
      System.err.println("Error: --filter only works with --list");
      CliUtils.printUsage(parser, TopicsCatalog.class, argsOrdering);
      return;
    }

    run(parsedArgs);
  }

  private static void run(Args args) {
    try {
      if (args.list) {
        TreeSet<String> names = new TreeSet<>(Topics.getSymbolDictionaryKeys());
        for (Topics topic : Topics.values()) {
          names.add(topic.name());
        }

        if (args.filter != null) {
          Pattern pattern;
          try {
            pattern = Pattern.compile(args.filter);
          } catch (PatternSyntaxException e) {
            System.err.printf("Error: invalid regular expression \"%s\": %s%n", args.filter, e.getMessage());
            return;
          }

          names.removeIf(name -> !pattern.matcher(name).find());
        }

        System.out.println(JSON_MAPPER.writeValueAsString(List.copyOf(names)));
      } else {
        SortedMap<?, Map<String, String>> queries = getAllQueriesForTopic(args.get);
        if (queries == null) {
          if (Topics.getByName(args.get) == null) {
            System.err.printf("Error: unknown topics \"%s\"%n", args.get);
          }
          return;
        }

        System.out.println(JSON_MAPPER.writeValueAsString(queries));
      }
    } catch (JsonProcessingException e) {
      System.err.printf("Error: %s%n", e.getMessage());
    }
  }

  private static SortedMap<?, Map<String, String>> getAllQueriesForTopic(String topicName) {
    Topics topic = Topics.getByName(topicName);
    if (topic == null) {
      return null;
    }

    try {
      return TopicReader.getTopics(topic);
    } catch (Exception e) {
      System.err.printf("Error: unable to read topics \"%s\": %s%n", topicName, e.getMessage());
      return null;
    }
  }
}
