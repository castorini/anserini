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

import io.anserini.search.ScoredDoc;
import io.anserini.search.SimpleSearcher;
import io.anserini.index.IndexReaderUtils;
import io.anserini.util.LoggingBootstrap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

/**
 * Minimal CLI wrapper around {@link SimpleSearcher}.
 */
public final class Search {
  public static class Args {
    @Option(name = "--index", metaVar = "[path|name]", required = true, usage = "Path to Lucene index or prebuilt index name")
    public String index;

    @Option(name = "--query", metaVar = "[text]", usage = "Query string")
    public String query;

    @Option(name = "--hits", metaVar = "[number]", usage = "Number of hits to return")
    public int hits = 10;

    @Option(name = "--options", usage = "Print information about options.")
    public Boolean options = false;

    @Option(name = "--interactive", usage = "Read queries from stdin until user quits.")
    public Boolean interactive = false;

    @Option(name = "--json", usage = "Emit JSON output.")
    public Boolean json = false;

    @Option(name = "--trec", usage = "Emit TREC output.")
    public Boolean trec = false;
  }

  private enum OutputMode {
    JSON,
    TREC
  }

  private static void run(Args parsed) {
    OutputMode outputMode = parsed.json ? OutputMode.JSON : OutputMode.TREC;

    if (parsed.interactive) {
      runInteractive(parsed, outputMode);
    } else {
      runSearcher(parsed.query, parsed.hits, parsed.index, outputMode);
    }
  }

  private static void runInteractive(Args parsed, OutputMode outputMode) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
         SimpleSearcher searcher = new SimpleSearcher(IndexReaderUtils.getIndex(parsed.index).toString())) {
      String query;
      while (true) {
        query = reader.readLine();
        if (query == null) {
          return;
        }

        query = query.trim();
        if ("quit".equalsIgnoreCase(query) || "exit".equalsIgnoreCase(query)) {
          return;
        }

        if (query.isBlank()) {
          continue;
        }

        printResults(searcher, query, parsed.hits, outputMode);
      }
    } catch (IOException e) {
      System.err.printf("Error: %s%n", e.getMessage());
    }
  }

  private static void runSearcher(String query, int hits, String index, OutputMode outputMode) {
    try (SimpleSearcher searcher = new SimpleSearcher(IndexReaderUtils.getIndex(index).toString())) {
      printResults(searcher, query, hits, outputMode);
    } catch (IOException e) {
      System.err.printf("Error: %s%n", e.getMessage());
    }
  }

  private static void printResults(SimpleSearcher searcher, String query, int hits, OutputMode outputMode) {
    try {
      ScoredDoc[] results = searcher.search(query, hits);

      for (int rank = 0; rank < results.length; rank++) {
        ScoredDoc hit = results[rank];
        if (outputMode == OutputMode.TREC) {
          System.out.printf("1 Q0 %s %d %.6f anserini%n", hit.docid, rank + 1, hit.score);
        } else {
          System.out.println(toJsonResult(query, rank + 1, hit));
        }
      }
    } catch (IOException e) {
      System.err.printf("Error: %s%n", e.getMessage());
    }
  }

  private static String toJsonResult(String query, int rank, ScoredDoc hit) {
    return String.format("{\"query\":\"%s\",\"rank\":%d,\"docid\":\"%s\",\"score\":%.6f}",
        escapeJson(query), rank, escapeJson(hit.docid), hit.score);
  }

  private static String escapeJson(String value) {
    if (value == null) {
      return "";
    }

    StringBuilder builder = new StringBuilder(value.length() + 8);
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      switch (c) {
        case '\\':
          builder.append("\\\\");
          break;
        case '\"':
          builder.append("\\\"");
          break;
        case '\b':
          builder.append("\\b");
          break;
        case '\f':
          builder.append("\\f");
          break;
        case '\n':
          builder.append("\\n");
          break;
        case '\r':
          builder.append("\\r");
          break;
        case '\t':
          builder.append("\\t");
          break;
        default:
          if (c < 0x20) {
            builder.append(String.format("\\u%04x", (int) c));
          } else {
            builder.append(c);
          }
      }
    }
    return builder.toString();
  }

  public static void main(String[] args) {
    LoggingBootstrap.installJulToSlf4jBridge();

    Args parsed = new Args();
    CmdLineParser parser = new CmdLineParser(parsed, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      if (parsed.options) {
        System.err.printf("Options for %s:%n%n", Search.class.getSimpleName());
        parser.printUsage(System.err);

        List<String> required = new ArrayList<>();
        parser.getOptions().forEach((option) -> {
          if (option.option.required()) {
            required.add(option.option.toString());
          }
        });

        System.err.printf("%nRequired options are %s%n", required);
      } else {
        System.err.printf("Error: %s%n", e.getMessage());
        System.err.printf("For help, use \"--options\" to print out information about options.%n");
      }
      return;
    }

    if (parsed.hits <= 0) {
      System.err.println("Error: --hits must be positive");
      return;
    }

    if (parsed.interactive && parsed.query != null) {
      System.err.println("Error: --interactive and --query are mutually exclusive");
      return;
    }

    if (!parsed.interactive && parsed.query == null) {
      System.err.println("Error: --query is required when not running in interactive mode");
      return;
    }
    if (parsed.json == parsed.trec) {
      System.err.println("Error: exactly one of --json or --trec must be specified");
      return;
    }

    run(parsed);
  }
}
