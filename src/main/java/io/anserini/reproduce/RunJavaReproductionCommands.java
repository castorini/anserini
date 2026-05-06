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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import io.anserini.cli.CliUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.util.LoggingBootstrap;

public class RunJavaReproductionCommands {
  private static final Logger LOG = LogManager.getLogger(RunJavaReproductionCommands.class);
  private static final String[] COMMAND_CONFIG_DIRECTORIES = new String[] {
      "reproduce/from-document-collection/commands",
      "reproduce/from-prebuilt-indexes/commands"
  };

  public static class Args {
    @Option(name = "--config", metaVar = "[config]", usage = "Config file with regression commands.")
    public String config;

    @Option(name = "--list", usage = "List available configs as a JSON array and exit.")
    public boolean list = false;
  
    @Option(name = "--sleep", metaVar = "[seconds]", usage = "Sleep interval before checking load.")
    public int sleep = 30;

    @Option(name = "--load", metaVar = "[threshold]", usage = "Maximum load threshold; won't launch commands above this load.")
    public int load = 10;

    @Option(name = "--max", metaVar = "[num]", usage = "Maximum number of concurrent jobs.")
    public int max = 4;

    @Option(name = "--logs-directory", metaVar = "[path]", usage = "Directory for command logs.")
    public String logsDirectory = ReproductionUtils.Constants.DEFAULT_LOGS_DIRECTORY;

    @Option(name = "--runs-directory", metaVar = "[path]", usage = "Directory for runs.")
    public String runsDirectory = ReproductionUtils.Constants.DEFAULT_RUNS_DIRECTORY;

    @Option(name = "--dry-run", metaVar = "[boolean]", usage = "Print commands without executing them.")
    public boolean dryRun = false;

    @Option(name = "--help", help = true, usage = "Print this help message and exit.")
    public boolean help = false;
  }

  private static final String[] argsOrdering = new String[] {
      "--config", "--list", "--sleep", "--load", "--max", "--logs-directory", "--runs-directory", "--dry-run", "--help"};

  public static void main(String[] args) throws Exception {
    LoggingBootstrap.installJulToSlf4jBridge();

    Args parsedArgs = new Args();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(String.format("Error: %s", e.getMessage()));
      CliUtils.printUsage(parser, RunJavaReproductionCommands.class, argsOrdering);
      return;
    }

    if (parsedArgs.help) {
      CliUtils.printUsage(parser, RunJavaReproductionCommands.class, argsOrdering);
      return;
    }

    if (parsedArgs.list) {
      List<String> configs = listCommandConfigs();
      System.out.println(new ObjectMapper().writeValueAsString(configs));
      return;
    }

    if (parsedArgs.config == null || parsedArgs.config.isBlank()) {
      System.err.println("Error: Option \"--config\" is required unless \"--list\" is specified.");
      CliUtils.printUsage(parser, RunJavaReproductionCommands.class, argsOrdering);
      return;
    }

    if (parsedArgs.sleep < 0) {
      throw new IllegalArgumentException("--sleep must be non-negative.");
    }

    run(parsedArgs);
  }

  private static void run(Args args) throws IOException, URISyntaxException, InterruptedException {
    List<String> commands = loadCommands(args.config, args.logsDirectory, args.runsDirectory);
    LOG.info("Running commands in {}", args.config);
    LOG.info("Logs directory: {}", args.logsDirectory);
    LOG.info("Runs directory: {}", args.runsDirectory);
    LOG.info("Sleep interval: {}", args.sleep);
    LOG.info("Threshold load: {}", args.load);
    LOG.info("Max concurrent jobs: {}", args.max);
    LOG.info("Dry run: {}", args.dryRun);

    if (args.dryRun) {
      for (String command : commands) {
        LOG.info("Command: {}", command);
      }
      return;
    }

    Path logsDir = Paths.get(args.logsDirectory);
    if (!Files.exists(logsDir)) {
      Files.createDirectories(logsDir);
    }

    List<Process> active = new ArrayList<>();
    int nextCommand = 0;

    while (nextCommand < commands.size() || !active.isEmpty()) {
      active.removeIf(p -> !p.isAlive());

      double currentLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
      boolean loadAvailable = currentLoad >= 0;
      boolean canLaunchByMax = active.size() < args.max;
      // Always allow launching when there are no active jobs, otherwise high load can deadlock progress.
      boolean canLaunchByLoad = !loadAvailable || currentLoad < args.load || active.isEmpty();

      if (nextCommand < commands.size() && canLaunchByMax && canLaunchByLoad) {
        String command = commands.get(nextCommand);
        LOG.info("Launching: {}", command);
        active.add(launch(command));
        nextCommand++;
      }

      String loadString = loadAvailable ? String.format("%.1f", currentLoad) : "N/A";
      LOG.info("Current load: {} (threshold = {}), active jobs: {} (max = {})", loadString, args.load, active.size(), args.max);

      if (active.size() > 0) {
        Thread.sleep(args.sleep * 1000L);
      }
    }

    LOG.info("All jobs completed!");
  }

  private static List<String> listCommandConfigs() throws IOException, URISyntaxException {
    Path codePath = Paths.get(RunJavaReproductionCommands.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    Set<String> configs = new LinkedHashSet<>();

    if (Files.isRegularFile(codePath) && codePath.toString().endsWith(".jar")) {
      try (java.util.jar.JarFile jarFile = new java.util.jar.JarFile(codePath.toFile())) {
        java.util.Enumeration<java.util.jar.JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
          java.util.jar.JarEntry entry = entries.nextElement();
          if (entry.isDirectory()) {
            continue;
          }
          String name = entry.getName();
          for (String dir : COMMAND_CONFIG_DIRECTORIES) {
            String prefix = dir + "/";
            if (name.startsWith(prefix) && name.endsWith(".txt")) {
              String configName = name.substring(prefix.length(), name.length() - ".txt".length());
              if (!configName.contains("/")) {
                configs.add(configName);
              }
            }
          }
        }
      }
    } else {
      for (String dir : COMMAND_CONFIG_DIRECTORIES) {
        Path configDir = codePath.resolve(dir);
        if (!Files.exists(configDir)) {
          continue;
        }
        try (java.util.stream.Stream<Path> paths = Files.list(configDir)) {
          paths.filter(Files::isRegularFile)
              .map(path -> path.getFileName().toString())
              .filter(name -> name.endsWith(".txt"))
              .map(name -> name.substring(0, name.length() - ".txt".length()))
              .forEach(configs::add);
        }
      }
    }

    List<String> sortedConfigs = new ArrayList<>(configs);
    Collections.sort(sortedConfigs);
    return sortedConfigs;
  }

  private static List<String> loadCommands(String resource, String logsDirectory, String runsDirectory) throws IOException, URISyntaxException {
    List<String> commands = new ArrayList<>();

    InputStream commandStream = null;
    IllegalArgumentException lastException = null;

    Path localPath = Path.of(resource);
    if (Files.exists(localPath)) {
      commandStream = Files.newInputStream(localPath);
    } else {
      String[] resourceCandidates = new String[] {
          resource,
          "reproduce/from-document-collection/commands/" + resource + ".txt",
          "reproduce/from-prebuilt-indexes/commands/" + resource + ".txt"
      };

      for (String resourcePath : resourceCandidates) {
        try {
          commandStream = ReproductionUtils.loadResourceStream(resourcePath, RunJavaReproductionCommands.class);
          break;
        } catch (IllegalArgumentException e) {
          lastException = e;
        }
      }
    }

    if (commandStream == null && lastException != null) {
      throw lastException;
    }

    if (commandStream == null) {
      throw new IllegalArgumentException("Could not load command resource: " + resource);
    }

    String fatjarPath = new File(RunJavaReproductionCommands.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();

    try (InputStream in = commandStream;
         BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String command = line.trim();
        if (command.isEmpty() || command.startsWith("#")) {
          continue;
        }

        String configName = null;
        String[] commandParts = command.split("\\s+");
        for (int i = 0; i < commandParts.length - 1; i++) {
          if ("--config".equals(commandParts[i])) {
            configName = commandParts[i + 1];
            break;
          }
        }

        boolean fromPrebuilt = resource.contains("prebuilt");
        if (fromPrebuilt && !command.contains("--runs-directory")) {
          command = String.format("%s --runs-directory %s", command, runsDirectory);
        }

        String logFile = Paths.get(logsDirectory, String.format("log.%s.%s.txt", fromPrebuilt ? "from-prebuilt-indexes" : "from-document-collection", configName)).toString();
        commands.add(String.format("%s %s %s %s > %s 2>&1", ReproductionUtils.Constants.JAVA_PREFIX, fatjarPath, ReproductionUtils.Constants.JVM_ARGS, command, logFile));
      }
    }

    return commands;
  }

  private static Process launch(String command) throws IOException {
    Process process = new ProcessBuilder("bash", "-lc", command)
        .redirectInput(ProcessBuilder.Redirect.PIPE)
        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
        .redirectError(ProcessBuilder.Redirect.DISCARD)
        .start();
    process.getOutputStream().close();
    return process;
  }
}
