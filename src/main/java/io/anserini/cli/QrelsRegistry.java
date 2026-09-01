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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.eval.Qrels;
import io.anserini.util.LoggingBootstrap;

public final class QrelsRegistry {
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  public static class Args {
    @Option(name = "--list", usage = "List available qrels.")
    public boolean list = false;

    @Option(name = "--filter", metaVar = "[regexp]", usage = "Filter qrels by regular expression.")
    public String filter = null;

    @Option(name = "--get", metaVar = "[qrels]", usage = "Print qrels.")
    public String get = null;

    @Option(name = "--metadata", metaVar = "[qrels]", usage = "Print qrels metadata.")
    public String metadata = null;

    @Option(name = "--help", help = true, usage = "Print this help message and exit.")
    public boolean help = false;
  }

  private static final String[] argsOrdering = new String[] {"--list", "--filter", "--get", "--metadata", "--help"};

  public static void main(String[] args) {
    LoggingBootstrap.installJulToSlf4jBridge();

    Args parsedArgs = new Args();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(String.format("Error: %s", e.getMessage()));
      CliUtils.printUsage(parser, QrelsRegistry.class, argsOrdering);
      return;
    }

    if (parsedArgs.help) {
      CliUtils.printUsage(parser, QrelsRegistry.class, argsOrdering);
      return;
    }

    int actions = (parsedArgs.list ? 1 : 0) + (parsedArgs.get != null ? 1 : 0) + (parsedArgs.metadata != null ? 1 : 0);
    if (actions != 1) {
      System.err.println("Error: exactly one of --list, --get, or --metadata must be specified");
      CliUtils.printUsage(parser, QrelsRegistry.class, argsOrdering);
      return;
    }

    if (!parsedArgs.list && parsedArgs.filter != null) {
      System.err.println("Error: --filter only works with --list");
      CliUtils.printUsage(parser, QrelsRegistry.class, argsOrdering);
      return;
    }

    run(parsedArgs);
  }

  private static void run(Args args) {
    if (args.list) {
      TreeSet<String> names = new TreeSet<>(Qrels.names());

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

      try {
        System.out.println(JSON_MAPPER.writeValueAsString(List.copyOf(names)));
      } catch (JsonProcessingException e) {
        System.err.printf("Error: %s%n", e.getMessage());
      }
    } else if (args.get != null) {
      try {
        Path path = Qrels.resolveRegisteredQrelsPath(args.get);
        Files.copy(path, System.out);
        System.out.flush();
      } catch (IllegalArgumentException e) {
        System.err.printf("Error: unknown qrels \"%s\"%n", args.get);
      } catch (IOException e) {
        System.err.printf("Error: unable to read qrels \"%s\": %s%n", args.get, e.getMessage());
      }
    } else {
      try {
        Path localPath = Qrels.resolveRegisteredQrelsPath(args.metadata);
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("name", Qrels.getCanonicalName(args.metadata));
        metadata.put("path", Qrels.getRegisteredPath(args.metadata));
        metadata.put("local_path", localPath.toAbsolutePath().toString());
        metadata.put("aliases", Qrels.aliases(args.metadata));
        System.out.println(JSON_MAPPER.writeValueAsString(metadata));
      } catch (IllegalArgumentException e) {
        System.err.printf("Error: unknown qrels \"%s\"%n", args.metadata);
      } catch (IOException e) {
        System.err.printf("Error: unable to read qrels \"%s\": %s%n", args.metadata, e.getMessage());
      }
    }
  }
}
