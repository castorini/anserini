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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RunRegressionCommands {
  private static final Logger LOG = LogManager.getLogger(RunRegressionCommands.class);

  public static class Args {
    @Option(name = "--file", metaVar = "[path]", required = true, usage = "File with regression commands.")
    public String file;

    @Option(name = "--sleep", metaVar = "[seconds]", usage = "Sleep interval before checking load.")
    public int sleep = 30;

    @Option(name = "--load", metaVar = "[threshold]", usage = "Maximum load threshold (won't launch commands above this load).")
    public int load = 10;

    @Option(name = "--max", metaVar = "[num]", usage = "Maximum number of concurrent jobs (0 means no limit).")
    public int max = 0;
  }

  public static void main(String[] argv) throws Exception {
    Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(120));
    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    if (args.sleep < 0) {
      throw new IllegalArgumentException("--sleep must be non-negative.");
    }

    List<String> commands = loadCommands(Path.of(args.file));
    LOG.info("Running commands in {}", args.file);
    LOG.info("Sleep interval: {}", args.sleep);
    LOG.info("Threshold load: {}", args.load);
    LOG.info("Max concurrent jobs: {}", args.max > 0 ? args.max : "unlimited");

    List<Process> active = new ArrayList<>();
    int nextCommand = 0;

    while (nextCommand < commands.size() || !active.isEmpty()) {
      active.removeIf(p -> !p.isAlive());

      double currentLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
      boolean loadAvailable = currentLoad >= 0;
      boolean canLaunchByMax = args.max <= 0 || active.size() < args.max;
      boolean canLaunchByLoad = !loadAvailable || currentLoad < args.load;

      if (nextCommand < commands.size() && canLaunchByMax && canLaunchByLoad) {
        String command = commands.get(nextCommand);
        LOG.info("Launching: {}", command);
        active.add(launch(command));
        nextCommand++;
      }

      String loadString = loadAvailable ? String.format("%.1f", currentLoad) : "N/A";
      LOG.info("Current load: {} (threshold = {}), active jobs: {} (max = {})",
          loadString, args.load, active.size(), args.max > 0 ? args.max : "unlimited");
      Thread.sleep(args.sleep * 1000L);
    }

    LOG.info("All jobs completed!");
  }

  private static List<String> loadCommands(Path path) throws IOException {
    List<String> commands = new ArrayList<>();
    for (String line : Files.readAllLines(path)) {
      String command = line.trim();
      if (command.isEmpty() || command.startsWith("#")) {
        continue;
      }
      commands.add(command);
    }
    return commands;
  }

  private static Process launch(String command) throws IOException {
    return new ProcessBuilder("bash", "-lc", command)
        .inheritIO()
        .start();
  }
}
