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

package io.anserini.reproduce;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.anserini.cli.CliUtils;

import io.anserini.index.IndexReaderUtils;
import io.anserini.util.LoggingBootstrap;
import io.anserini.util.PrebuiltIndexHandler;

public class ReproduceFromPrebuiltIndexes {
  private static final String CONFIG_DIRECTORY = "reproduce/from-prebuilt-indexes/configs";

  public static class Args {
    @Option(name = "--config", metaVar = "[config]", usage = "Name of the configuration to run.")
    public String config;

    @Option(name = "--list", usage = "List available configs as a JSON array and exit.")
    public boolean list = false;

    @Option(name = "--runs-directory", metaVar = "[path]", usage = "Directory for output runs.")
    public String runsDirectory = ReproductionUtils.Constants.DEFAULT_RUNS_DIRECTORY;

    @Option(name = "--compute-index-size", usage = "Compute total size of all unique indexes referenced by runs.")
    public boolean computeIndexSize = false;

    @Option(name = "--print-commands", usage = "Print commands.")
    public boolean printCommands = false;

    @Option(name = "--dry-run", usage = "Output commands without execution.")
    public boolean dryRun = false;

    @Option(name = "--help", help = true, usage = "Print this help message and exit.")
    public boolean help = false;
  }

  private static final String[] argsOrdering =
      new String[] {"--config", "--list", "--runs-directory", "--compute-index-size", "--print-commands", "--dry-run", "--help"};

  public static void main(String[] args) throws Exception {
    LoggingBootstrap.installJulToSlf4jBridge();

    Args parsedArgs = new Args();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException exception) {
      System.err.println(String.format("Error: %s", exception.getMessage()));
      CliUtils.printUsage(parser, ReproduceFromPrebuiltIndexes.class, argsOrdering);

      return;
    }

    if (parsedArgs.help) {
      CliUtils.printUsage(parser, ReproduceFromPrebuiltIndexes.class, argsOrdering);
      return;
    }

    if (parsedArgs.list) {
      List<String> configs = ReproductionUtils.listYamlConfigs(ReproduceFromPrebuiltIndexes.class, CONFIG_DIRECTORY);
      System.out.println(new ObjectMapper().writeValueAsString(configs));
      return;
    }

    if (parsedArgs.config == null || parsedArgs.config.isBlank()) {
      System.err.println("Error: Option \"--config\" is required unless \"--list\" is specified.");
      CliUtils.printUsage(parser, ReproduceFromPrebuiltIndexes.class, argsOrdering);
      return;
    }

    run(parsedArgs);
  }

  private static void run(Args args) throws IOException, InterruptedException, URISyntaxException {
    String configName = args.config;
    boolean printCommands = args.printCommands;
    boolean dryRun = args.dryRun;
    boolean computeIndexSize = args.computeIndexSize;

    Path runsDir = Paths.get(args.runsDirectory);
    if (!Files.exists(runsDir)) {
      Files.createDirectories(runsDir);
    }

    String fatjarPath = new File(ReproduceFromPrebuiltIndexes.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();

    final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    String resourceName = String.format("%s/%s.yaml", CONFIG_DIRECTORY, configName);
    Config config;
    try (InputStream yamlStream = ReproductionUtils.loadResourceStream(resourceName, ReproduceFromPrebuiltIndexes.class)) {
      config = mapper.readValue(yamlStream, Config.class);
    }

    ProcessBuilder pb;
    Process process;

    final Instant startTime = Instant.now();

    // Pre-scan all commands to gather unique indexes referenced.
    Set<String> uniqueIndexNames = new LinkedHashSet<>();
    for (Condition condition : config.conditions) {
      for (Topic topic : condition.topics) {
        // Note that we don't need to reconstruct the full command here, but we do need to substitute $topics to get the actual index path.
        String indexPath = extractIndexPath(condition.command.replace("$topics", topic.topic_key));
        if (indexPath != null) {
          uniqueIndexNames.add(indexPath);
        }
      }
    }

    // If requested, print index summary before any runs.
    if (computeIndexSize && !uniqueIndexNames.isEmpty()) {
      System.out.printf("Indexes referenced by this run (%d total):%n", uniqueIndexNames.size());

      // First pass: compute rows and totals so we can size columns dynamically.
      long totalBytes = 0L;
      long totalDownloadBytes = 0L;
      int presentCount = 0;
      java.util.List<String[]> rows = new java.util.ArrayList<>(); // [name, sizeOnDisk, downloadSize, path]

      for (String idx : uniqueIndexNames) {
        String name = idx;
        String sizeOnDiskStr = "-";
        String downloadSizeStr = "-";
        String pathStr = "-";

        if (PrebuiltIndexHandler.get(idx) != null) {
          // Prebuilt alias
          PrebuiltIndexHandler indexHandler = PrebuiltIndexHandler.get(idx);
          if (indexHandler.getCompressedSize() > 0) {
            downloadSizeStr = IndexReaderUtils.formatSize(indexHandler.getCompressedSize());
            totalDownloadBytes += indexHandler.getCompressedSize();
          }
          Path prebuiltPath = expectedPrebuiltPath(idx);
          pathStr = prebuiltPath == null ? "-" : prebuiltPath.toAbsolutePath().toString();
          long sz = -1L;
          if (prebuiltPath != null && Files.exists(prebuiltPath)) {
            // Ignore symlinks per request; measure directory as-is
            sz = IndexReaderUtils.findDirectorySize(prebuiltPath);
          }
          if (sz > 0L) {
            totalBytes += sz;
            presentCount++;
            sizeOnDiskStr = IndexReaderUtils.formatSize(sz);
          } else {
            sizeOnDiskStr = "-"; // treat empty or missing as not downloaded
          }
        } else {
          // Literal local path
          Path p = Paths.get(idx);
          pathStr = p.toAbsolutePath().toString();
          if (Files.exists(p)) {
            Path pathForSize = resolveSingleSymlinkChild(p);
            long sz = IndexReaderUtils.findDirectorySize(pathForSize);
            if (sz > 0L) {
              totalBytes += sz;
              presentCount++;
              sizeOnDiskStr = IndexReaderUtils.formatSize(sz);
            }
          }
        }

        rows.add(new String[] { name, sizeOnDiskStr, downloadSizeStr, pathStr });
      }

      // Compute dynamic widths for first and last columns.
      int nameWidth = Math.max("name".length(), uniqueIndexNames.stream().mapToInt(String::length).max().orElse(4));
      nameWidth = Math.max(nameWidth, "total".length());
      int pathWidth = "path".length();
      for (String[] r : rows) {
        if (r[3] != null) pathWidth = Math.max(pathWidth, r[3].length());
      }

      final String fmt = "%-" + nameWidth + "s  %12s  %12s  %-" + pathWidth + "s%n";
      System.out.printf(fmt, "name", "size on disk", "download size", "path");
      System.out.printf(fmt, repeat('-', nameWidth), repeat('-', 12), repeat('-', 12), repeat('-', pathWidth));

      for (String[] r : rows) {
        System.out.printf(fmt, r[0], r[1], r[2], r[3]);
      }
      // Add total row at the end with no path.
      System.out.printf(fmt, "total", IndexReaderUtils.formatSize(totalBytes), IndexReaderUtils.formatSize(totalDownloadBytes), "-");

      System.out.printf("%nTotal size across %d of %d indexes: %s%n%n", presentCount, uniqueIndexNames.size(), IndexReaderUtils.formatSize(totalBytes));
    }

    for (Condition condition : config.conditions) {
      System.out.printf("# Running condition \"%s\": %s \n%n", condition.name, condition.display);
      for (Topic topic : condition.topics) {
        System.out.println("  - topic_key: " + topic.topic_key + "\n");

        final String output = runsDir.resolve(String.format("run.%s.%s.%s.txt", configName, condition.name, topic.topic_key)).toString();

        final String command = String.format("%s $fatjar %s %s", ReproductionUtils.Constants.JAVA_PREFIX, ReproductionUtils.Constants.JVM_ARGS, condition.command)
            .replace("$fatjar", fatjarPath)
            .replace("$threads", "16")
            .replace("$topics", topic.topic_key)
            .replace("$output", output)
            .replace("$runs_directory", runsDir.toString());

        // Note that there's a hidden dependency for fusion runs, where the command specifies the run to fuse by -runs run1 run2 ...
        // The runs directory can be set using $runs_directory, but the run names are hard-coded.

        if (printCommands) {
          System.out.println("    Retrieval command: " + command);
        }

        if (!dryRun) {
          pb = new ProcessBuilder(command.split(" "));
          process = pb.start();
          int resultCode = process.waitFor();
          if (resultCode == 0) {
            System.out.println("    Run successfully completed!");
          } else {
            System.out.println("    Run failed!");
          }
          System.out.println();
        }

        InputStream stdout;

        Map<String, Double> expected = topic.expected_scores;
        Map<String, String> evalCommands = new LinkedHashMap<>();
        Map<String, String> metricDefinitions = topic.metric_definitions;

        // Go through and gather the eval commands in a first pass, so that we can print all at once if desired.
        for (String metric : expected.keySet()) {
          String evalKey = topic.eval_key;
          String metricDefinition = Objects.requireNonNull(metricDefinitions.get(metric));

          // For the eval command, running `java -cp fatjar ...` is fine since we're just running trec_eval.
          evalCommands.put(metric, "java -cp $fatjarPath trec_eval $metric $evalKey $output"
              .replace("$fatjarPath", fatjarPath)
              .replace("$metric", metricDefinition)
              .replace("$evalKey", evalKey)
              .replace("$output", output));
        }

        // Print the commands all at once if desired.
        if (printCommands) {
          for (Map.Entry<String, String> entry : evalCommands.entrySet()) {
            System.out.println("    Eval command: " + entry.getValue());
          }
          System.out.println();
        }

        // We've already gathered the eval commands, so just run them now and check.
        for (Map.Entry<String, String> entry : evalCommands.entrySet()) {
          String metric = entry.getKey();
          String cmd = entry.getValue();

          if (!dryRun) {
            pb = new ProcessBuilder(cmd.split(" "));
            process = pb.start();

            int resultCode = process.waitFor();
            stdout = process.getInputStream();
            if (resultCode == 0) {
              String scoreString = new String(stdout.readAllBytes()).replaceAll(".*?(\\d+\\.\\d+)$", "$1").trim();
              double score = Double.parseDouble(scoreString);
              double delta = Math.abs(score - expected.get(metric));

              if (score > expected.get(metric)) {
                System.out.printf("    %8s: %.4f %s expected %.4f%n", metric, score, ReproductionUtils.Constants.OKISH, expected.get(metric));
              } else if (delta < 0.00001) {
                System.out.printf("    %8s: %.4f %s%n", metric, score, ReproductionUtils.Constants.OK);
              } else if (delta < 0.0002) {
                System.out.printf("    %8s: %.4f %s expected %.4f%n", metric, score, ReproductionUtils.Constants.OKISH, expected.get(metric));
              } else {
                System.out.printf("    %8s: %.4f %s expected %.4f%n", metric, score, ReproductionUtils.Constants.FAIL, expected.get(metric));
              }
            } else {
              System.out.println("Evaluation command failed for metric: " + metric);
            }
          }
        }
        System.out.println();
      }
    }

    final Instant endTime = Instant.now();
    final long durationMillis = endTime.toEpochMilli() - startTime.toEpochMilli();

    System.out.println("Start time: " + ReproductionUtils.formatStartTime(startTime));
    System.out.println("End time:   " + ReproductionUtils.formatEndTime(endTime));
    System.out.println("Duration:   " + ReproductionUtils.formatDuration(durationMillis));
  }

  private static String extractIndexPath(String command) {
    // Split on whitespace and find token after '-index'.
    String[] parts = command.split(" ");
    for (int i = 0; i < parts.length; i++) {
      if ("-index".equals(parts[i]) && i + 1 < parts.length) {
        return parts[i + 1];
      }
    }
    return null;
  }

  private static Path expectedPrebuiltPath(String indexName) {
    try {
      PrebuiltIndexHandler handler = PrebuiltIndexHandler.get(indexName);
      String cacheRoot = getCacheRoot();
      String base = handler.getFilename();
      if (base.endsWith(".tar.gz")) {
        base = base.substring(0, base.length() - ".tar.gz".length());
      } else if (base.endsWith(".gz")) {
        base = base.substring(0, base.length() - ".gz".length());
      }
      return Path.of(cacheRoot, base + "." + handler.getMD5());
    } catch (Exception e) {
      return null;
    }
  }

  private static Path resolveSingleSymlinkChild(Path p) throws IOException {
    Path pathForSize = p;
    if (Files.isDirectory(p)) {
      try (java.util.stream.Stream<Path> children = Files.list(p)) {
        java.util.List<Path> entries = children.toList();
        if (entries.size() == 1 && Files.isSymbolicLink(entries.get(0))) {
          Path link = entries.get(0);
          // Try to resolve to an absolute path using toRealPath if possible.
          try {
            Path real = link.toRealPath();
            if (Files.exists(real)) {
              return real;
            }
          } catch (IOException ignored) { }
          // Fallback: best effort using the stored link target.
          Path linkTarget = Files.readSymbolicLink(link);
          Path resolved = link.getParent().resolve(linkTarget).normalize();
          if (Files.exists(resolved)) {
            return resolved;
          }
        }
      }
    }
    return pathForSize;
  }

  private static String getCacheRoot() {
    String cacheDir = System.getProperty("anserini.index.cache");
    if (cacheDir == null || cacheDir.isEmpty()) {
      cacheDir = System.getenv("ANSERINI_INDEX_CACHE");
    }
    if (cacheDir == null || cacheDir.isEmpty()) {
      cacheDir = java.nio.file.Path.of(System.getProperty("user.home"), ".cache", "pyserini", "indexes").toString();
    }
    return cacheDir;
  }

  private static String repeat(char c, int n) {
    StringBuilder sb = new StringBuilder(n);
    for (int i = 0; i < n; i++) sb.append(c);
    return sb.toString();
  }

  public static class Config {
    @JsonProperty
    public List<Condition> conditions;
  }

  public static class Condition {
    @JsonProperty
    public String name;

    @JsonProperty
    public String display;

    @JsonProperty
    public String display_html;

    @JsonProperty
    public String display_row;

    @JsonProperty
    public String command;

    @JsonProperty
    public List<Topic> topics;
  }

  public static class Topic {
    @JsonProperty
    public String topic_key;

    @JsonProperty
    public String eval_key;

    @JsonProperty
    public Map<String, Double> expected_scores;

    @JsonProperty
    public Map<String, String> metric_definitions;
  }

}
