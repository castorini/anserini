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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.anserini.index.IndexReaderUtils;
import io.anserini.index.IndexInfo;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

public class RunRepro {
  // ANSI escape code for red text
  private static final String RED = "\u001B[31m";
  // ANSI escape code for blue text
  private static final String BLUE = "\u001B[94m";
  // ANSI escape code to reset to the default text color
  private static final String RESET = "\u001B[0m";

  private static final String FAIL = RED + "[FAIL]" + RESET;
  private static final String OKAY_ISH = BLUE + "[OK*]" + RESET;

  private final String collection;
  private final TrecEvalMetricDefinitions metricDefinitions;
  private final boolean printCommands;
  private final boolean dryRun;
  private final boolean computeIndexSize;

  public static class Args {
    @Option(name = "-printCommands", usage = "Print commands.")
    public Boolean printCommands = false;

    @Option(name = "-dryRun", usage = "Dry run.")
    public Boolean dryRun = false;

    @Option(name = "-options", usage = "Print information about options.")
    public Boolean options = false;

    @Option(name = "-computeIndexSize", usage = "Compute total size of all unique indexes referenced by runs.")
    public Boolean computeIndexSize = false;
  }

  public RunRepro(String collection, TrecEvalMetricDefinitions metrics, boolean printCommands, boolean dryRun, boolean computeIndexSize) {
    this.collection = collection;
    this.metricDefinitions = metrics;
    this.printCommands = printCommands;
    this.dryRun = dryRun;
    this.computeIndexSize = computeIndexSize;
  }

  public void run() throws IOException, InterruptedException, URISyntaxException {
    if (!new File("runs").exists()) {
      new File("runs").mkdir();
    }

    String fatjarPath = new File(RunRepro.class.getProtectionDomain()
        .getCodeSource().getLocation().toURI()).getPath();

    final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    Config config = mapper.readValue(RunRepro.class.getClassLoader()
        .getResourceAsStream("reproduce/" + collection + ".yaml"), Config.class);

    ProcessBuilder pb;
    Process process;

    final long start = System.nanoTime();

    // Pre-scan all commands to gather unique indexes referenced.
    java.util.Set<String> uniqueIndexNames = new java.util.LinkedHashSet<>();
    for (Condition condition : config.conditions) {
      for (Topic topic : condition.topics) {
        final String output = String.format("runs/run.%s.%s.%s.txt", collection, condition.name, topic.topic_key);
        final String command = condition.command
            .replace("$fatjar", fatjarPath)
            .replace("$threads", "16")
            .replace("$topics", topic.topic_key)
            .replace("$output", output);
        String indexPath = extractIndexPath(command);
        if (indexPath != null) {
          uniqueIndexNames.add(indexPath);
        }
      }
    }

    // If requested, print index summary before any runs.
    if (computeIndexSize && !uniqueIndexNames.isEmpty()) {
      System.out.printf("Indexes referenced by this run (%d total):%n", uniqueIndexNames.size());

      // Simple table header; allow path to extend naturally for easy copy/paste.
      final String fmt = "%-70s  %12s  %12s  %s%n";
      System.out.printf(fmt, "name", "size on disk", "download size", "path");
      System.out.printf(fmt, repeat('-', 70), repeat('-', 12), repeat('-', 12), repeat('-', 30));

      long totalBytes = 0L;
      int presentCount = 0;
      for (String idx : uniqueIndexNames) {
        String name = idx;
        String sizeOnDiskStr = "-";
        String downloadSizeStr = "-";
        String pathStr = "-";

        if (IndexInfo.contains(idx)) {
          // Prebuilt alias
          IndexInfo info = IndexInfo.get(idx);
          if (info.size > 0) {
            downloadSizeStr = IndexReaderUtils.formatSize(info.size);
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

        System.out.printf(fmt, name, sizeOnDiskStr, downloadSizeStr, pathStr);
      }

      System.out.printf("%nTotal size across %d of %d indexes: %s%n%n", presentCount, uniqueIndexNames.size(), IndexReaderUtils.formatSize(totalBytes));
    }

    for (Condition condition : config.conditions) {
      System.out.printf("# Running condition \"%s\": %s \n%n", condition.name, condition.display);
      for (Topic topic : condition.topics) {
        System.out.println("  - topic_key: " + topic.topic_key + "\n");

        final String output = String.format("runs/run.%s.%s.%s.txt", collection, condition.name, topic.topic_key);

        final String command = condition.command
            .replace("$fatjar", fatjarPath)
            .replace("$threads", "16")
            .replace("$topics", topic.topic_key)
            .replace("$output", output);

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

        // running the evaluation command
        Map<String, Map<String, String>> evalDefinitions = metricDefinitions.getMetricDefinitions().get(collection);
        InputStream stdout;

        for (Map<String, Double> expected : topic.scores) {
          Map<String, String> evalCommands = new LinkedHashMap<>();

          // Go through and gather the eval commands in a first pass, so that we can print all at once if desired.
          for (String metric : expected.keySet()) {
            String evalKey = topic.eval_key;
            if (!evalDefinitions.get(evalKey).containsKey(metric)) {
              throw new RuntimeException("Invalid metric: " + metric);
            }

            evalCommands.put(metric, "java -cp $fatjarPath trec_eval $metric $evalKey $output"
                .replace("$fatjarPath", fatjarPath)
                .replace("$metric", evalDefinitions.get(evalKey).get(metric))
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
                  System.out.printf("    %8s: %.4f %s expected %.4f%n", metric, score, OKAY_ISH, expected.get(metric));
                } else if (delta < 0.00001) {
                  System.out.printf("    %8s: %.4f [OK]%n", metric, score);
                } else if (delta < 0.0002) {
                  System.out.printf("    %8s: %.4f %s expected %.4f%n", metric, score, OKAY_ISH, expected.get(metric));
                } else {
                  System.out.printf("    %8s: %.4f %s expected %.4f%n", metric, score, FAIL, expected.get(metric));
                }
              } else {
                System.out.println("Evaluation command failed for metric: " + metric);
              }
            }
          }
          System.out.println();
        }
      }
    }

    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    System.out.println("Total run time: " + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
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
      IndexInfo info = IndexInfo.get(indexName);
      String cacheRoot = getCacheRoot();
      String base = info.filename;
      if (base.endsWith(".tar.gz")) {
        base = base.substring(0, base.length() - ".tar.gz".length());
      } else if (base.endsWith(".gz")) {
        base = base.substring(0, base.length() - ".gz".length());
      }
      return Path.of(cacheRoot, base + "." + info.md5);
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

  // Intentionally no per-column wrapping: keeping path fully visible makes copy/paste easier.


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
    public List<Map<String, Double>> scores;
  }

  public static class TrecEvalMetricDefinitions {
    public Map<String, Map<String, Map<String, String>>> metricDefinitions;

    public TrecEvalMetricDefinitions() {
      metricDefinitions = new HashMap<>();
    }

    public Map<String, Map<String, Map<String, String>>> getMetricDefinitions() {
      return metricDefinitions;
    }
  }
}
