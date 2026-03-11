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
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.ZoneId;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import io.anserini.util.LoggingBootstrap;

public class SummarizeLogsFromCorpus {
  private static final String RUN_REPRODUCTIONS_FROM_CORPUS = "RunReproductionFromCorpus";

  private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd HH:mm:ss")
      .appendLiteral(',')
      .appendFraction(java.time.temporal.ChronoField.NANO_OF_SECOND, 1, 9, false)
      .toFormatter(Locale.ROOT);

  private static final Pattern LOG_TIMESTAMP_PATTERN =
      Pattern.compile("^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{1,9})\\b");

  public static class Args {
    @Option(name = "--logs-directory", metaVar = "[path]", usage = "Path to logs directory.")
    public String logsDirectory = ReproductionUtils.Constants.DEFAULT_LOGS_DIRECTORY;

    @Option(name = "--md", aliases = {"--markdown"}, metaVar = "[boolean]", usage = "Emit output in markdown format.")
    public boolean markdown = false;

    @Option(name = "--text", aliases = {"--plain-text"}, metaVar = "[boolean]", usage = "Emit output in plain text format.")
    public boolean plainText = false;

    @Option(name = "--json", metaVar = "[boolean]", usage = "Emit output in JSON format.")
    public boolean json = false;

    @Option(name = "--help", usage = "Print this help message and exit.")
    public boolean help = false;
  }

  private static final String[] argsOrdering = new String[] {
      "--logs-directory", "--md", "--text", "--json", "--help"};

  public static void main(String[] args) throws Exception {
    LoggingBootstrap.installJulToSlf4jBridge();

    Args parsedArgs = new Args();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(120));

    for (String arg : args) {
      if ("--help".equals(arg)) {
        ReproductionUtils.printUsage(parser, SummarizeLogsFromCorpus.class, argsOrdering);
        return;
      }
    }

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(String.format("Error: %s", e.getMessage()));
      ReproductionUtils.printUsage(parser, SummarizeLogsFromCorpus.class, argsOrdering);
      return;
    }

    int selectedOutputs = (parsedArgs.markdown ? 1 : 0) + (parsedArgs.plainText ? 1 : 0) + (parsedArgs.json ? 1 : 0);
    if (selectedOutputs > 1) {
      throw new IllegalArgumentException("Only one output mode may be specified among --md/--markdown, --text/--plain-text, and --json.");
    }

    run(parsedArgs);
  }

  private static void run(Args args) {
    Path logsDir = Paths.get(args.logsDirectory);
    int totalRegressions = 0;
    String[] statusLabels = {ReproductionUtils.Constants.OK, ReproductionUtils.Constants.OKISH, ReproductionUtils.Constants.FAIL};
    String[] rawStatusLabels = {"[OK]", "[OK*]", "[FAIL]"};
    int[] statusCounters = new int[statusLabels.length];

    Instant startTime = null;
    Instant endTime = null;

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir, "log.*")) {
      for (Path logFile : stream) {
        if (!Files.isRegularFile(logFile)) {
          continue;
        }
        totalRegressions++;

        String firstRunRegressionsLine = null;
        String lastRunRegressionsLine = null;
        try (var lines = Files.lines(logFile, StandardCharsets.UTF_8)) {
          for (String line : (Iterable<String>) lines::iterator) {
            if (!line.contains(RUN_REPRODUCTIONS_FROM_CORPUS)) {
              continue;
            }
            if (firstRunRegressionsLine == null) {
              firstRunRegressionsLine = line;
            }
            lastRunRegressionsLine = line;
          }
        }

        if (firstRunRegressionsLine != null) {
          String timestamp = extractTimestamp(firstRunRegressionsLine);
          Instant dt = parseTimestamp(timestamp);
          if (dt != null) {
            if (startTime == null || dt.isBefore(startTime)) {
              startTime = dt;
            }
          }
        }

        if (lastRunRegressionsLine == null) {
          continue;
        }

        for (int i = 0; i < statusLabels.length; i++) {
          if (lastRunRegressionsLine.contains(statusLabels[i])) {
            statusCounters[i]++;
          }
        }

        String timestamp = extractTimestamp(lastRunRegressionsLine);
        Instant dt = parseTimestamp(timestamp);
        if (dt != null) {
          if (endTime == null || dt.isAfter(endTime)) {
            endTime = dt;
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Error reading log.", e);
    }

    Duration duration = null;
    if (startTime != null && endTime != null) {
      duration = Duration.between(startTime, endTime);
    }

    if (args.json) {
      printSummaryJson(totalRegressions, statusCounters, rawStatusLabels, startTime, endTime, duration);
    } else if (args.markdown) {
      printSummaryMarkdown(totalRegressions, statusCounters, rawStatusLabels, startTime, endTime, duration);
    } else {
      printSummaryPlainText(totalRegressions, statusCounters, statusLabels, startTime, endTime, duration);
    }
  }

  private static void printSummaryPlainText(int totalRegressions, int[] statusCounters, String[] statusLabels,
                                           Instant startTime, Instant endTime, Duration duration) {
    StringBuilder sb = new StringBuilder(256);
    sb.append("Total regressions: ");
    appendThreeWidthRightAligned(sb, totalRegressions);
    sb.append('\n');
    for (int i = 0; i < statusLabels.length; i++) {
      sb.append(' ');
      sb.append(statusLabels[i]);
      sb.append(' ');
      appendThreeWidthRightAligned(sb, statusCounters[i]);
      sb.append('\n');
    }
    sb.append('\n');
    sb.append("Start time: ").append(startTime == null ? "n/a" : ReproductionUtils.formatStartTime(startTime)).append('\n');
    sb.append("End time:   ").append(endTime == null ? "n/a" : ReproductionUtils.formatEndTime(endTime)).append('\n');
    sb.append("Duration:   ").append(duration == null ? "n/a" : ReproductionUtils.formatDuration(duration)).append('\n');
    System.out.print(sb);
  }

  private static void appendThreeWidthRightAligned(StringBuilder sb, int value) {
    String text = Integer.toString(value);
    int padding = 3 - text.length();
    while (padding > 0) {
      sb.append(' ');
      padding--;
    }
    sb.append(text);
  }

  private static void printSummaryMarkdown(int totalRegressions, int[] statusCounters, String[] rawStatusLabels,
                                          Instant startTime, Instant endTime, Duration duration) {
    StringBuilder sb = new StringBuilder(256);
    int statusWidth = "status".length();
    int countWidth = "count".length();
    for (int i = 0; i < rawStatusLabels.length; i++) {
      if (rawStatusLabels[i].length() > statusWidth) {
        statusWidth = rawStatusLabels[i].length();
      }
      int countDigits = String.valueOf(statusCounters[i]).length();
      if (countDigits > countWidth) {
        countWidth = countDigits;
      }
    }

    sb.append(String.format(Locale.ROOT, "Total regressions: %3d%n", totalRegressions));
    sb.append("\n");
    sb.append("| ").append("status");
    if ("status".length() < statusWidth) {
      sb.append(" ".repeat(statusWidth - "status".length()));
    }
    sb.append(" | ").append("count");
    if ("count".length() < countWidth) {
      sb.append(" ".repeat(countWidth - "count".length()));
    }
    sb.append(" |\n");
    sb.append("| ").append("-".repeat(statusWidth)).append(" | ");
    sb.append("-".repeat(Math.max(countWidth - 1, 1))).append(": |\n");
    for (int i = 0; i < statusCounters.length; i++) {
      sb.append("| ").append(rawStatusLabels[i]);
      if (rawStatusLabels[i].length() < statusWidth) {
        sb.append(" ".repeat(statusWidth - rawStatusLabels[i].length()));
      }
      sb.append(" | ")
          .append(" ".repeat(Math.max(countWidth - String.valueOf(statusCounters[i]).length(), 0)))
          .append(statusCounters[i]).append(" |\n");
    }
    sb.append("\n");
    sb.append("Start time: ").append(startTime == null ? "n/a" : ReproductionUtils.formatStartTime(startTime)).append("\n");
    sb.append("End time:   ").append(endTime == null ? "n/a" : ReproductionUtils.formatEndTime(endTime)).append("\n");
    sb.append("Duration:   ").append(duration == null ? "n/a" : ReproductionUtils.formatDuration(duration.toMillis())).append("\n");
    System.out.print(sb);
  }

  private static void printSummaryJson(int totalRegressions, int[] statusCounters, String[] rawStatusLabels,
                                      Instant startTime, Instant endTime, Duration duration) {
    System.out.append("{\n");
    System.out.append("  \"total_regressions\": ").append(String.valueOf(totalRegressions)).append(",\n");
    System.out.append("  \"status_counts\": {\n");
    for (int i = 0; i < statusCounters.length; i++) {
      System.out.append("    \"").append(ReproductionUtils.escapeJson(rawStatusLabels[i]))
          .append("\": ").append(String.valueOf(statusCounters[i]));
      if (i + 1 < statusCounters.length) {
        System.out.append(",\n");
      } else {
        System.out.append("\n");
      }
    }
    System.out.append("  },\n");
    System.out.append("  \"start_time\": \"")
        .append(ReproductionUtils.escapeJson(startTime == null ? "n/a" : ReproductionUtils.formatStartTime(startTime))).append("\",\n");
    System.out.append("  \"end_time\": \"")
        .append(ReproductionUtils.escapeJson(endTime == null ? "n/a" : ReproductionUtils.formatEndTime(endTime))).append("\",\n");
    System.out.append("  \"duration\": \"")
        .append(ReproductionUtils.escapeJson(duration == null ? "n/a" : ReproductionUtils.formatDuration(duration.toMillis()))).append("\"\n");
    System.out.append("}\n");
  }

  private static String extractTimestamp(String line) {
    Matcher matcher = LOG_TIMESTAMP_PATTERN.matcher(line.trim());
    if (!matcher.find()) {
      return null;
    }
    return matcher.group(1);
  }

  private static Instant parseTimestamp(String timestamp) {
    if (timestamp == null) {
      return null;
    }
    return LocalDateTime.parse(timestamp, DATE_FORMAT).atZone(ZoneId.systemDefault()).toInstant();
  }

}
