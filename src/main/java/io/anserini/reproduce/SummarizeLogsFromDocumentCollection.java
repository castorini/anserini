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
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import io.anserini.cli.CliUtils;
import io.anserini.util.LoggingBootstrap;

public class SummarizeLogsFromDocumentCollection {
  private static final String RUN_REPRODUCTIONS_FROM_COLLECTION = "ReproduceFromDocumentCollection";

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

    @Option(name = "--help", help = true, usage = "Print this help message and exit.")
    public boolean help = false;
  }

  private static final String[] argsOrdering = new String[] {
      "--logs-directory", "--md", "--text", "--json", "--help"};

  public static void main(String[] args) throws Exception {
    LoggingBootstrap.installJulToSlf4jBridge();

    Args parsedArgs = new Args();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(String.format(Locale.ROOT, "Error: %s", e.getMessage()));
      CliUtils.printUsage(parser, SummarizeLogsFromDocumentCollection.class, argsOrdering);
      return;
    }

    if (parsedArgs.help) {
      CliUtils.printUsage(parser, SummarizeLogsFromDocumentCollection.class, argsOrdering);
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
    String[] rawStatusLabels = {"[OK]", "[OKish]", "[FAIL]"};
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
            if (!line.contains(RUN_REPRODUCTIONS_FROM_COLLECTION)) {
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
          // Accept historical log labels for backward compatibility alongside the current label.
          if (lastRunRegressionsLine.contains(rawStatusLabels[i]) || (i == 1 && lastRunRegressionsLine.contains("[OK*]"))) {
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
      System.out.print(formatSummaryJson(totalRegressions, statusCounters, rawStatusLabels, startTime, endTime, duration));
    } else if (args.markdown) {
      System.out.print(formatSummaryMarkdown(totalRegressions, statusCounters, rawStatusLabels, startTime, endTime, duration));
    } else {
      System.out.print(formatSummaryPlainText(totalRegressions, statusCounters, statusLabels, startTime, endTime, duration));
    }
  }

  static String formatSummaryPlainText(int totalRegressions, int[] statusCounters, String[] statusLabels,
                                       Instant startTime, Instant endTime, Duration duration) {
    StringBuilder sb = new StringBuilder(String.format(Locale.ROOT, "Total regressions: %3d\n", totalRegressions));
    for (int i = 0; i < statusLabels.length; i++) {
      sb.append(String.format(Locale.ROOT, " %s %3d\n", statusLabels[i], statusCounters[i]));
    }
    return sb.append(formatTiming(startTime, endTime,
        duration == null ? "n/a" : ReproductionUtils.formatDuration(duration))).toString();
  }

  static String formatSummaryMarkdown(int totalRegressions, int[] statusCounters, String[] statusLabels,
                                      Instant startTime, Instant endTime, Duration duration) {
    int statusWidth = "status".length();
    int countWidth = "count".length();
    for (int i = 0; i < statusLabels.length; i++) {
      statusWidth = Math.max(statusWidth, statusLabels[i].length());
      countWidth = Math.max(countWidth, Integer.toString(statusCounters[i]).length());
    }

    String rowFormat = "| %-" + statusWidth + "s | %" + countWidth + "s |\n";
    StringBuilder sb = new StringBuilder(String.format(Locale.ROOT, "Total regressions: %3d%n\n", totalRegressions));
    sb.append(String.format(Locale.ROOT, "| %-" + statusWidth + "s | %-" + countWidth + "s |\n", "status", "count"));
    sb.append(String.format(Locale.ROOT, rowFormat, "-".repeat(statusWidth), "-".repeat(countWidth - 1) + ":"));
    for (int i = 0; i < statusCounters.length; i++) {
      sb.append(String.format(Locale.ROOT, rowFormat, statusLabels[i], statusCounters[i]));
    }
    return sb.append(formatTiming(startTime, endTime,
        duration == null ? "n/a" : ReproductionUtils.formatDuration(duration.toMillis()))).toString();
  }

  private static String formatTiming(Instant startTime, Instant endTime, String duration) {
    return String.format(Locale.ROOT, """

        Start time: %s
        End time:   %s
        Duration:   %s
        """, startTime == null ? "n/a" : ReproductionUtils.formatStartTime(startTime),
        endTime == null ? "n/a" : ReproductionUtils.formatEndTime(endTime),
        duration);
  }

  private static String formatSummaryJson(int totalRegressions, int[] statusCounters, String[] statusLabels,
                                          Instant startTime, Instant endTime, Duration duration) {
    StringJoiner counts = new StringJoiner(",\n");
    for (int i = 0; i < statusCounters.length; i++) {
      counts.add(String.format(Locale.ROOT, "    \"%s\": %d",
          ReproductionUtils.escapeJson(statusLabels[i]), statusCounters[i]));
    }
    return String.format(Locale.ROOT, """
        {
          "total_regressions": %d,
          "status_counts": {
        %s
          },
          "start_time": "%s",
          "end_time": "%s",
          "duration": "%s"
        }
        """, totalRegressions, counts,
        ReproductionUtils.escapeJson(startTime == null ? "n/a" : ReproductionUtils.formatStartTime(startTime)),
        ReproductionUtils.escapeJson(endTime == null ? "n/a" : ReproductionUtils.formatEndTime(endTime)),
        ReproductionUtils.escapeJson(duration == null ? "n/a" : ReproductionUtils.formatDuration(duration.toMillis())));
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
