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

import io.anserini.eval.TrecEval;
import io.anserini.index.IndexCollection;
import io.anserini.search.SearchCollection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RunRegressionFromCorpus {
  private static final Logger LOG = LogManager.getLogger(RunRegressionFromCorpus.class);

  private static final String[] CORPUS_ROOTS = new String[] {
      "./",
      "/collection/",
      "/",
      "/mnt/",
      "/tuna1/",
      "/store/",
      "/scratch/",
      "/u4/jimmylin/",
      "/System/Volumes/Data/store"
  };

  private static final String INDEX_COMMAND = "bin/run.sh io.anserini.index.IndexCollection";
  private static final String INDEX_FLAT_DENSE_COMMAND = "bin/run.sh io.anserini.index.IndexFlatDenseVectors";
  private static final String INDEX_HNSW_DENSE_COMMAND = "bin/run.sh io.anserini.index.IndexHnswDenseVectors";
  private static final String INDEX_INVERTED_DENSE_COMMAND = "bin/run.sh io.anserini.index.IndexInvertedDenseVectors";
  private static final String INDEX_STATS_COMMAND = "bin/run.sh io.anserini.index.IndexReaderUtils";
  private static final String SEARCH_COMMAND = "bin/run.sh io.anserini.search.SearchCollection";
  private static final String SEARCH_FLAT_DENSE_COMMAND = "bin/run.sh io.anserini.search.SearchFlatDenseVectors";
  private static final String SEARCH_HNSW_DENSE_COMMAND = "bin/run.sh io.anserini.search.SearchHnswDenseVectors";
  private static final String SEARCH_INVERTED_DENSE_COMMAND = "bin/run.sh io.anserini.search.SearchInvertedDenseVectors";

  private static final String RED = "\u001B[91m";
  private static final String BLUE = "\u001B[94m";
  private static final String RESET = "\u001B[0m";

  public static class Args {
    @Option(name = "--regression", required = true, usage = "Name of the regression test.")
    public String regression;

    @Option(name = "--corpus-path", usage = "Override corpus path from YAML.")
    public String corpusPath = "";

    @Option(name = "--download", usage = "Download corpus.")
    public boolean download = false;

    @Option(name = "--index", usage = "Build index.")
    public boolean index = false;

    @Option(name = "--index-threads", usage = "Override number of indexing threads from YAML.")
    public int indexThreads = -1;

    @Option(name = "--verify", usage = "Verify index statistics.")
    public boolean verify = false;

    @Option(name = "--search", usage = "Search and verify results.")
    public boolean search = false;

    @Option(name = "--search-pool", usage = "Number of ranking runs to execute in parallel.")
    public int searchPool = 4;

    @Option(name = "--convert-pool", usage = "Number of converting runs to execute in parallel.")
    public int convertPool = 4;

    @Option(name = "--dry-run", usage = "Output commands without execution.")
    public boolean dryRun = false;
  }

  public static void main(String[] args) throws Exception {
    Args parsed = new Args();
    CmdLineParser parser = new CmdLineParser(parsed, ParserProperties.defaults().withUsageWidth(120));
    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    Path yamlPath = Paths.get("src/main/resources/regression", parsed.regression + ".yaml");
    if (!Files.exists(yamlPath)) {
      throw new IllegalArgumentException("Missing regression file: " + yamlPath);
    }
    JsonNode yaml = mapper.readTree(Files.newInputStream(yamlPath));

    long start = System.nanoTime();

    if (parsed.download) {
      // TODO: If collection already exists, skip.
      LOG.info("========== Downloading Corpus ==========");
      JsonNode downloadUrl = yaml.get("download_url");
      if (downloadUrl == null || downloadUrl.asText().isEmpty()) {
        throw new IllegalArgumentException("Corpus download URL unknown!");
      }
      String url = downloadUrl.asText();
      Path collectionsDir = Paths.get("collections");
      Files.createDirectories(collectionsDir);
      String localTarball = downloadUrl(url, collectionsDir, null, textOrNull(yaml.get("download_checksum")), false, true);
      LOG.info("Extracting {}...", localTarball);
      extractTarball(Paths.get(localTarball), collectionsDir);

      if (yaml.has("download_corpus")) {
        Path src = collectionsDir.resolve(yaml.get("download_corpus").asText());
        Path dest = collectionsDir.resolve(yaml.get("corpus").asText());
        LOG.info("Renaming {} to {}", src, dest);
        File srcFile = src.toFile();
        srcFile.setReadable(true, true);
        srcFile.setWritable(true, true);
        srcFile.setExecutable(true, true);
        Files.move(src, dest);
      }

      Path path = collectionsDir.resolve(yaml.get("corpus").asText());
      LOG.info("Corpus path is {}", path);
      parsed.corpusPath = path.toString();
    }

    if (parsed.index) {
      LOG.info("========== Indexing ==========");
      String command = constructIndexingCommand(yaml, parsed);
      if (!parsed.dryRun) {
        runCommand(command);
      }
    }

    if (parsed.verify) {
      LOG.info("========== Verifying Index ==========");
      JsonNode indexType = yaml.get("index_type");
      if (indexType != null && "hnsw".equals(indexType.asText())) {
        LOG.info("Skipping verification step for HNSW dense indexes.");
      } else if (indexType != null && "flat".equals(indexType.asText())) {
        LOG.info("Skipping verification step for flat dense indexes.");
      } else {
        StringBuilder cmd = new StringBuilder();
        cmd.append(INDEX_STATS_COMMAND)
            .append(" -index ").append(constructIndexPath(yaml))
            .append(" -stats");
        if (indexType != null && "inverted-dense".equals(indexType.asText())) {
          cmd.append(" -field vector");
        }
        String output = checkOutput(cmd.toString());
        String[] lines = output.split("\n");
        JsonNode stats = yaml.get("index_stats");
        if (stats != null && stats.isObject()) {
          for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length < 2) {
              continue;
            }
            String stat = parts[0].trim();
            if (stats.has(stat)) {
              long value = Long.parseLong(parts[1].trim());
              long expected = stats.get(stat).asLong();
              if (value != expected) {
                System.out.printf("%s: expected=%d, actual=%d%n", stat, expected, value);
                throw new AssertionError(stat + " mismatch");
              }
              LOG.info(line);
            }
          }
          LOG.info("Index statistics successfully verified!");
        }
      }
    }

    if (parsed.search) {
      LOG.info("========== Ranking ==========");
      List<String> searchCmds = constructSearchCommands(yaml);
      if (parsed.dryRun) {
        for (String cmd : searchCmds) {
          LOG.info(cmd);
        }
      } else {
        runCommandsInPool(searchCmds, parsed.searchPool);
      }

      JsonNode conversions = yaml.get("conversions");
      if (conversions != null && conversions.isArray() && conversions.size() > 0) {
        LOG.info("========== Converting ==========");
        List<String> convertCmds = constructConvertCommands(yaml);
        if (parsed.dryRun) {
          for (String cmd : convertCmds) {
            LOG.info(cmd);
          }
        } else {
          runCommandsInPool(convertCmds, parsed.convertPool);
        }
      }

      evaluateAndVerify(yaml, parsed, start);
    }
  }

  private static boolean isClose(double a, double b, double relTol, double absTol) {
    return Math.abs(a - b) <= Math.max(relTol * Math.max(Math.abs(a), Math.abs(b)), absTol);
  }

  private static String checkOutput(String command) throws IOException, InterruptedException {
    //LOG.info("Eval command: " + command);

    if ( command.contains("trec_eval")) {
      String[] parts = command.trim().split("\\s+");
      String[] args = Arrays.copyOfRange(parts, 1, parts.length);
      //System.out.println(Arrays.toString(args));

      String[][] out = new TrecEval().runAndGetOutput(args);
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < out.length; i++) {
        if (i > 0) {
          sb.append('\n');
        }
        String[] row = out[i];
        for (int j = 0; j < row.length; j++) {
          if (j > 0) {
            sb.append('\t');
          }
          sb.append(row[j]);
        }
      }
      //System.out.println("OUT ---> " + sb.toString());
      return sb.toString();
    }

    ProcessBuilder pb = new ProcessBuilder("bash", "-lc", command);
    pb.redirectErrorStream(true);
    Process p = pb.start();
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    try (InputStream in = p.getInputStream()) {
      in.transferTo(buffer);
    }
    int code = p.waitFor();
    if (code != 0) {
      throw new RuntimeException("Command failed: " + command);
    }
    return buffer.toString(StandardCharsets.UTF_8);
  }

  private static String constructIndexPath(JsonNode yaml) {
    String indexPath = Objects.requireNonNull(yaml.get("index_path")).asText();

    if (!Files.exists(Paths.get(indexPath))) {
      for (String root : CORPUS_ROOTS) {
        Path candidate = Paths.get(root, indexPath);
        if (Files.exists(candidate)) {
          indexPath = candidate.toString();
          break;
        }
      }
    }

    return indexPath;
  }

  private static String constructIndexingCommand(JsonNode yaml, Args args) throws IOException {
    String corpusPath = null;
    if (args.corpusPath != null && !args.corpusPath.isEmpty()) {
      if (Files.exists(Paths.get(args.corpusPath))) {
        corpusPath = args.corpusPath;
      }
    } else {
      String yamlCorpusPath = Objects.requireNonNull(yaml.get("corpus_path")).asText();
      for (String root : CORPUS_ROOTS) {
        Path candidate = Paths.get(root, yamlCorpusPath);
        if (Files.exists(candidate)) {
          corpusPath = candidate.toString();
          break;
        }
      }
    }

    if (corpusPath == null) {
      throw new RuntimeException(String.format("Unable to find the corpus '%s' at %s: looked in %s",
          yaml.get("corpus").asText(), yaml.get("corpus_path").asText(), Arrays.toString(CORPUS_ROOTS)));
    }

    int threads = args.indexThreads != -1 ? args.indexThreads : Objects.requireNonNull(yaml.get("index_threads")).asInt();
    Files.createDirectories(Paths.get("indexes"));

    JsonNode indexType = yaml.get("index_type");
    String rootCmd = INDEX_COMMAND;
    if (indexType != null && "inverted-dense".equals(indexType.asText())) {
      rootCmd = INDEX_INVERTED_DENSE_COMMAND;
    } else if (indexType != null && "hnsw".equals(indexType.asText())) {
      rootCmd = INDEX_HNSW_DENSE_COMMAND;
    } else if (indexType != null && "flat".equals(indexType.asText())) {
      rootCmd = INDEX_FLAT_DENSE_COMMAND;
    }

    StringBuilder cmd = new StringBuilder();
    cmd.append(rootCmd)
        .append(" -collection ").append(Objects.requireNonNull(yaml.get("collection_class")).asText())
        .append(" -input ").append(corpusPath)
        .append(" -generator ").append(Objects.requireNonNull(yaml.get("generator_class")).asText())
        .append(" -index ").append(Objects.requireNonNull(yaml.get("index_path")).asText())
        .append(" -threads ").append(threads);
    JsonNode indexOptions = yaml.get("index_options");
    if (indexOptions != null && !indexOptions.asText().isBlank()) {
      cmd.append(" ").append(indexOptions.asText().trim());
    }

    return cmd.toString();
  }

  private static String constructRunfilePath(String index, String id, String modelName) {
    String[] parts = index.split("/");
    String indexPart = index;

    if (parts.length > 1) {
      String candidate = parts[1];
      String[] split = candidate.split("-", 2);
      if (split.length == 2) {
        indexPart = split[1];
      } else {
        indexPart = candidate;
      }
    }

    return Paths.get("runs", String.format("run.%s.%s.%s", indexPart, id, modelName)).toString();
  }

  private static List<String> constructSearchCommands(JsonNode yaml) {
    List<String> cmds = new ArrayList<>();
    JsonNode models = Objects.requireNonNull(yaml.get("models"));
    JsonNode topics = Objects.requireNonNull(yaml.get("topics"));

    for (JsonNode model : models) {
      String modelType = textOrNull(model.get("type"));
      String rootCmd;
      if ("inverted-dense".equals(modelType)) {
        rootCmd = SEARCH_INVERTED_DENSE_COMMAND;
      } else if ("hnsw".equals(modelType)) {
        rootCmd = SEARCH_HNSW_DENSE_COMMAND;
      } else if ("flat".equals(modelType)) {
        rootCmd = SEARCH_FLAT_DENSE_COMMAND;
      } else {
        rootCmd = SEARCH_COMMAND;
      }

      for (JsonNode topic : topics) {
        String topicReader = Objects.requireNonNull(yaml.get("topic_reader")).asText();
        String output = constructRunfilePath(Objects.requireNonNull(yaml.get("index_path")).asText(),
            Objects.requireNonNull(topic.get("id")).asText(),
            Objects.requireNonNull(model.get("name")).asText());

        // Janky special case for now: we run cacm in the test suite, and on GitHub CI tools/topics-and-qrels is not checked out.
        String topicPath;
        if ("topics.cacm.txt".equals(topic.get("path").asText())) {
          topicPath = "cacm";
        } else {
          topicPath = Paths.get("tools/topics-and-qrels", topic.get("path").asText()).toString();
        }

        StringBuilder cmd = new StringBuilder();
        cmd.append(rootCmd)
            .append(" -index ").append(constructIndexPath(yaml))
            .append(" -topics ").append(topicPath)
            .append(" -topicReader ").append(topicReader)
            .append(" -output ").append(output);
        String params = textOrNull(model.get("params"));
        if (params != null && !params.isBlank()) {
          cmd.append(" ").append(params.trim());
        }
        cmds.add(cmd.toString());
      }
    }

    return cmds;
  }

  private static List<String> constructConvertCommands(JsonNode yaml) {
    List<String> cmds = new ArrayList<>();
    JsonNode models = yaml.get("models");
    JsonNode topics = yaml.get("topics");
    JsonNode conversions = yaml.get("conversions");
    if (models == null || topics == null || conversions == null) {
      return cmds;
    }
    for (JsonNode model : models) {
      for (JsonNode topic : topics) {
        for (JsonNode conversion : conversions) {
          String inFile = constructRunfilePath(yaml.get("index_path").asText(),
              topic.get("id").asText(), model.get("name").asText()) + conversion.get("in_file_ext").asText();
          String outFile = constructRunfilePath(yaml.get("index_path").asText(),
              topic.get("id").asText(), model.get("name").asText()) + conversion.get("out_file_ext").asText();
          StringBuilder cmd = new StringBuilder();
          cmd.append(conversion.get("command").asText())
              .append(" --index ").append(constructIndexPath(yaml))
              .append(" --topics ").append(topic.get("id").asText())
              .append(" --input ").append(inFile)
              .append(" --output ").append(outFile);
          String params = textOrNull(conversion.get("params"));
          if (params != null && !params.isBlank()) {
            cmd.append(" ").append(params.trim());
          }
          String convertParams = textOrNull(topic.get("convert_params"));
          if (convertParams != null && !convertParams.isBlank()) {
            cmd.append(" ").append(convertParams.trim());
          }
          cmds.add(cmd.toString());
        }
      }
    }
    return cmds;
  }

  private static void evaluateAndVerify(JsonNode yaml, Args args, long startNanos) throws IOException, InterruptedException {
    String failStr = RED + "[FAIL]" + RESET + " ";
    String okStr = "   [OK] ";
    String okishStr = "  " + BLUE + "[OK*]" + RESET + " ";
    boolean failures = false;
    boolean okish = false;

    LOG.info("========== Verifying Results: {} ==========", yaml.get("corpus").asText());
    JsonNode models = Objects.requireNonNull(yaml.get("models"));
    JsonNode topics = Objects.requireNonNull(yaml.get("topics"));
    JsonNode metrics = Objects.requireNonNull(yaml.get("metrics"));

    for (JsonNode model : models) {
      for (int i = 0; i < topics.size(); i++) {
        JsonNode topic = topics.get(i);
        for (JsonNode metric : metrics) {
          String metricName = metric.get("metric").asText();
          String command = metric.get("command").asText();
          String params = textOrNull(metric.get("params"));
          String separator = metric.get("separator").asText();
          int parseIndex = metric.get("parse_index").asInt();
          int precision = metric.get("metric_precision").asInt();
          String qrel = textOrNull(topic.get("qrel"));
          String qrelPath = qrel == null ? "" : Paths.get("tools/topics-and-qrels", qrel).toString();

          String outputPath = constructRunfilePath(yaml.get("index_path").asText(),
              topic.get("id").asText(), model.get("name").asText());
          JsonNode conversions = yaml.get("conversions");
          if (conversions != null && conversions.isArray() && conversions.size() > 0) {
            JsonNode last = conversions.get(conversions.size() - 1);
            String ext = textOrNull(last.get("out_file_ext"));
            if (ext != null) {
              outputPath = outputPath + ext;
            }
          }

          StringBuilder evalCmd = new StringBuilder();
          evalCmd.append(command);
          if (params != null && !params.isBlank()) {
            evalCmd.append(" ").append(params.trim());
          }
          if (!qrelPath.isEmpty()) {
            evalCmd.append(" ").append(qrelPath);
          }
          evalCmd.append(" ").append(outputPath);

          if (args.dryRun) {
            LOG.info(evalCmd.toString());
            continue;
          }

          String out = checkOutput(evalCmd.toString());
          String[] lines = out.split("\n");
          String last = null;
          for (String line : lines) {
            if (!line.trim().isEmpty()) {
              last = line;
            }
          }
          if (last == null) {
            continue;
          }
          String[] fields = last.trim().split(separator);
          if (parseIndex >= fields.length) {
            continue;
          }
          double actual = round(Double.parseDouble(fields[parseIndex]), precision);

          JsonNode results = model.get("results");
          if (results == null || !results.has(metricName)) {
            continue;
          }
          double expected = round(results.get(metricName).get(i).asDouble(), precision);

          String modelType = textOrNull(model.get("type"));
          boolean usingHnsw = "hnsw".equals(modelType);
          boolean usingFlat = "flat".equals(modelType);

          double toleranceOk = 0.0;
          JsonNode tolerance = model.get("tolerance");
          if (tolerance != null && tolerance.has(metricName)) {
            toleranceOk = tolerance.get(metricName).get(i).asDouble();
          }

          String resultStr;
          if (usingFlat || usingHnsw) {
            resultStr = String.format(Locale.ROOT,
                "expected: %.4f actual: %.4f (delta=%.4f, tolerance=%.4f) - metric: %-8s model: %s topics: %s",
                expected, actual, expected - actual, toleranceOk, metricName, model.get("name").asText(),
                topic.get("id").asText());
          } else {
            resultStr = String.format(Locale.ROOT,
                "expected: %.4f actual: %.4f (delta=%.4f) - metric: %-8s model: %s topics: %s",
                expected, actual, Math.abs(expected - actual), metricName, model.get("name").asText(),
                topic.get("id").asText());
          }

          if (isClose(expected, actual, 1e-9, 0.0) || actual > expected ||
              (usingFlat && isClose(expected, actual, 1e-9, toleranceOk)) ||
              (usingHnsw && isClose(expected, actual, 1e-9, toleranceOk))) {
            LOG.info(okStr + resultStr);
          } else if ((usingFlat && isClose(expected, actual, 1e-9, toleranceOk * 1.5)) ||
              (usingHnsw && isClose(expected, actual, 1e-9, toleranceOk * 1.5))) {
            LOG.info(okishStr + resultStr);
            okish = true;
          } else {
            LOG.error(failStr + resultStr);
            failures = true;
          }
        }
      }
    }

    if (!args.dryRun) {
      long elapsed = Duration.ofNanos(System.nanoTime() - startNanos).toSeconds();
      if (failures) {
        LOG.error("{}Total elapsed time: {}s", failStr, elapsed);
      } else if (okish) {
        LOG.info("{}Total elapsed time: {}s", okishStr, elapsed);
      } else {
        LOG.info("All Tests Passed! Total elapsed time: {}s", elapsed);
      }
    }
  }

  private static String[] extractArgsAfterClass(String clazz, String cmd) {
    String[] parts = cmd.trim().split("\\s+");
    for (int i = 0; i < parts.length; i++) {
      if (clazz.equals(parts[i])) {
        return Arrays.copyOfRange(parts, i + 1, parts.length);
      }
    }

    return null;
  }

  private static void runCommand(String command) throws IOException, InterruptedException {
    LOG.info(command);

    if (command.contains("io.anserini.index.IndexCollection")) {
      LOG.info("Calling IndexCollection.main directly instead of starting a new process.");
      String[] args = extractArgsAfterClass("io.anserini.index.IndexCollection", command);
      LOG.info(Arrays.toString(args));

      try {
        IndexCollection.main(args);
      } catch (Exception e) {
        throw new RuntimeException("Command failed: " + command, e);
      }
      return;
    } if (command.contains("io.anserini.search.SearchCollection")) {
      LOG.info("Calling SearchCollection.main directly instead of starting a new process.");
      String[] args = extractArgsAfterClass("io.anserini.search.SearchCollection", command);
      LOG.info(Arrays.toString(args));

      try {
        SearchCollection.main(args);
      } catch (Exception e) {
        throw new RuntimeException("Command failed: " + command, e);
      }
      return;
    } else {
      ProcessBuilder pb = new ProcessBuilder("bash", "-lc", command);
      pb.inheritIO();
      Process p = pb.start();
      int code = p.waitFor();
      if (code != 0) {
        throw new RuntimeException("Command failed: " + command);
      }
    }
  }

  private static void runCommandsInPool(List<String> commands, int poolSize) throws InterruptedException {
    ExecutorService exec = Executors.newFixedThreadPool(poolSize);
    List<Future<?>> futures = new ArrayList<>();
    for (String cmd : commands) {
      futures.add(exec.submit(() -> {
        try {
          runCommand(cmd);
        } catch (IOException | InterruptedException e) {
          throw new RuntimeException(e);
        }
      }));
    }
    exec.shutdown();
    exec.awaitTermination(7, TimeUnit.DAYS);
    for (Future<?> future : futures) {
      try {
        future.get();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static String downloadUrl(String url, Path saveDir, String localFilename, String md5, boolean force, boolean verbose)
      throws IOException, NoSuchAlgorithmException {
    String filename = localFilename;
    if (filename == null || filename.isBlank()) {
      filename = url.substring(url.lastIndexOf('/') + 1);
      if (filename.endsWith("?dl=1")) {
        filename = filename.substring(0, filename.length() - "?dl=1".length());
      }
    }

    Path destination = saveDir.resolve(filename);
    if (verbose) {
      LOG.info("Downloading {} to {}...", url, destination);
    }
    if (Files.exists(destination)) {
      if (verbose) {
        LOG.info("{} already exists!", destination);
      }
      if (!force) {
        if (verbose) {
          LOG.info("Skipping download.");
        }
        return destination.toString();
      }
      Files.delete(destination);
    }

    URL remote = new URL(url);
    try (InputStream in = new BufferedInputStream(remote.openStream());
         OutputStream out = new BufferedOutputStream(Files.newOutputStream(destination))) {
      byte[] buffer = new byte[1024 * 1024];
      int read;
      while ((read = in.read(buffer)) >= 0) {
        out.write(buffer, 0, read);
      }
    }

    if (md5 != null && !md5.isBlank()) {
      String computed = computeMd5(destination);
      if (!computed.equalsIgnoreCase(md5)) {
        throw new IllegalStateException(destination + " does not match checksum! Expecting " + md5 + " got " + computed + ".");
      }
    }
    return destination.toString();
  }

  private static String computeMd5(Path file) throws IOException, NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("MD5");
    try (InputStream in = new BufferedInputStream(Files.newInputStream(file));
         DigestInputStream dis = new DigestInputStream(in, md)) {
      byte[] buffer = new byte[1024 * 1024];
      while (dis.read(buffer) >= 0) {
        // digest updated
      }
    }

    byte[] digest = md.digest();
    StringBuilder sb = new StringBuilder();
    for (byte b : digest) {
      sb.append(String.format("%02x", b));
    }

    return sb.toString();
  }

  private static void extractTarball(Path tarball, Path destDir) throws IOException {
    try (InputStream fileIn = new BufferedInputStream(Files.newInputStream(tarball));
         InputStream compressorIn = openMaybeGzip(tarball, fileIn);
         TarArchiveInputStream tarIn = new TarArchiveInputStream(compressorIn)) {
      TarArchiveEntry entry;
      byte[] buffer = new byte[1024 * 1024];
      while ((entry = tarIn.getNextTarEntry()) != null) {
        Path target = destDir.resolve(entry.getName()).normalize();
        if (!target.startsWith(destDir)) {
          throw new IOException("Blocked suspicious entry: " + entry.getName());
        }
        if (entry.isDirectory()) {
          Files.createDirectories(target);
          continue;
        }
        Files.createDirectories(target.getParent());
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(target))) {
          int read;
          while ((read = tarIn.read(buffer)) != -1) {
            out.write(buffer, 0, read);
          }
        }
      }
    }
  }

  private static InputStream openMaybeGzip(Path tarball, InputStream fileIn) throws IOException {
    String name = tarball.getFileName().toString().toLowerCase(Locale.ROOT);
    if (name.endsWith(".gz") || name.endsWith(".tgz")) {
      return new GzipCompressorInputStream(fileIn);
    }
    return fileIn;
  }

  private static double round(double value, int precision) {
    double scale = Math.pow(10.0, precision);
    return Math.round(value * scale) / scale;
  }

  private static String textOrNull(JsonNode node) {
    return node == null || node.isNull() ? null : node.asText();
  }
}
