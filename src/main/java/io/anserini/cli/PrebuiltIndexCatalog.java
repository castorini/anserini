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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import io.anserini.index.prebuilt.PrebuiltFlatIndex;
import io.anserini.index.prebuilt.PrebuiltHnswIndex;
import io.anserini.index.prebuilt.PrebuiltIndex;
import io.anserini.index.prebuilt.PrebuiltImpactIndex;
import io.anserini.index.prebuilt.PrebuiltInvertedIndex;
import io.anserini.util.LoggingBootstrap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class PrebuiltIndexCatalog {
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  public static class Args {
    @Option(name = "--list", usage = "List available prebuilt indexes.")
    public boolean list = false;

    @Option(name = "--filter", metaVar = "[regexp]", usage = "Filter prebuilt indexes by regular expression.")
    public String filter = null;

    @Option(name = "--type", metaVar = "[flat|inverted|impact|hnsw]", usage = "Filter prebuilt indexes by type.")
    public String type = null;

    @Option(name = "--help", help = true, usage = "Print this help message and exit.")
    public boolean help = false;
  }

  private static final String[] argsOrdering = new String[] {
      "--list", "--filter", "--type", "--help"};

  public static void main(String[] args) {
    LoggingBootstrap.installJulToSlf4jBridge();

    Args parsedArgs = new Args();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(String.format("Error: %s", e.getMessage()));
      CliUtils.printUsage(parser, PrebuiltIndexCatalog.class, argsOrdering);
      return;
    }

    if (parsedArgs.help) {
      CliUtils.printUsage(parser, PrebuiltIndexCatalog.class, argsOrdering);
      return;
    }

    if (!parsedArgs.list) {
      CliUtils.printUsage(parser, PrebuiltIndexCatalog.class, argsOrdering);
      return;
    }

    run(parsedArgs);
  }

  private static void run(Args args) {
    String typeFilter = args.type == null ? null : args.type.toLowerCase(Locale.ROOT);
    if (typeFilter != null && !isValidType(typeFilter)) {
      System.err.printf("Error: invalid --type \"%s\" (must be one of: flat, inverted, impact, hnsw)%n", args.type);
      return;
    }

    Pattern nameFilter = null;
    if (args.filter != null) {
      try {
        nameFilter = Pattern.compile(args.filter);
      } catch (PatternSyntaxException e) {
        System.err.printf("Error: invalid regular expression \"%s\": %s%n", args.filter, e.getMessage());
        return;
      }
    }

    List<PrebuiltIndex.Entry> details = getAllDetails();
    if (typeFilter != null) {
      details = details.stream().filter((entry) -> typeFilter.equals(entry.type)).collect(Collectors.toList());
    }
    if (nameFilter != null) {
      Pattern finalNameFilter = nameFilter;
      details = details.stream().filter((entry) -> finalNameFilter.matcher(entry.name).find()).collect(Collectors.toList());
    }

    try {
      System.out.println(JSON_MAPPER.writeValueAsString(details));
    } catch (JsonProcessingException e) {
      System.err.printf("Error: %s%n", e.getMessage());
    }
  }

  private static List<PrebuiltIndex.Entry> getAllEntries() {
    List<PrebuiltIndex.Entry> entries = new ArrayList<>();
    entries.addAll(PrebuiltInvertedIndex.entries());
    entries.addAll(PrebuiltImpactIndex.entries());
    entries.addAll(PrebuiltFlatIndex.entries());
    entries.addAll(PrebuiltHnswIndex.entries());
    return entries;
  }

  private static List<PrebuiltIndex.Entry> getAllDetails() {
    return getAllEntries().stream()
        .sorted(Comparator.comparing((entry) -> entry.name))
        .collect(Collectors.toList());
  }

  private static boolean isValidType(String type) {
    return "flat".equals(type) || "inverted".equals(type) || "impact".equals(type) || "hnsw".equals(type);
  }
}
