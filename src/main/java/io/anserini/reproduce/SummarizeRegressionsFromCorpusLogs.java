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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;

public class SummarizeRegressionsFromCorpusLogs {
  private static final String RUN_REGRESSIONS_FROM_CORPUS = "RunRegressionsFromCorpus";

  private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd HH:mm:ss")
      .appendLiteral(',')
      .appendFraction(java.time.temporal.ChronoField.NANO_OF_SECOND, 1, 9, false)
      .toFormatter(Locale.ROOT);

  public static void main(String[] args) {
    Path logsDir = Paths.get("logs");
    int totalRegressions = 0;
    int passed = 0;
    int ok = 0;

    LocalDateTime startDate = null;
    LocalDateTime endDate = null;
    String startDateStr = null;
    String endDateStr = null;

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir, "log.*")) {
      for (Path logFile : stream) {
        if (!Files.isRegularFile(logFile)) {
          continue;
        }
        totalRegressions++;

        List<String> logLines = Files.readAllLines(logFile);
        String firstRunRegressionsLine = firstLineContaining(logLines, RUN_REGRESSIONS_FROM_CORPUS);
        if (firstRunRegressionsLine != null) {
          String timestamp = extractTimestamp(firstRunRegressionsLine);
          if (timestamp != null) {
            LocalDateTime dt = LocalDateTime.parse(timestamp, DATE_FORMAT);
            if (startDate == null || dt.isBefore(startDate)) {
              startDate = dt;
              startDateStr = timestamp;
            }
          }
        }

        String lastLine = lastLineContaining(logLines, RUN_REGRESSIONS_FROM_CORPUS);
        if (lastLine == null) {
          continue;
        }
        if (lastLine.contains(RegressionConstants.OK)) {
          passed++;
        }
        if (lastLine.contains(RegressionConstants.OKISH)) {
          ok++;
        }
        String timestamp = extractTimestamp(lastLine);
        if (timestamp != null) {
          LocalDateTime dt = LocalDateTime.parse(timestamp, DATE_FORMAT);
          if (endDate == null || dt.isAfter(endDate)) {
            endDate = dt;
            endDateStr = timestamp;
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Error reading log.", e);
    }

    System.out.printf("Total regressions: %3d%n", totalRegressions);
    System.out.printf(" - Passed:         %3d%n", passed);
    System.out.printf(" - OK:             %3d%n", ok);

    System.out.println();
    System.out.printf("Start time: %s%n", startDateStr == null ? "" : startDateStr);
    System.out.printf("End time:   %s%n", endDateStr == null ? "" : endDateStr);
    System.out.println();

    if (startDate != null && endDate != null) {
      Duration duration = Duration.between(startDate, endDate);
      double hours = duration.toMillis() / 3600000.0;
      System.out.printf("Duration: %s ~%.1fh%n", formatDuration(duration), hours);
    } else {
      System.out.println("Duration: n/a");
    }
  }

  private static String firstLineContaining(List<String> lines, String target) {
    for (String line : lines) {
      if (line.contains(target)) {
        return line;
      }
    }
    return null;
  }

  private static String lastLineContaining(List<String> lines, String target) {
    for (int i = lines.size() - 1; i >= 0; i--) {
      String line = lines.get(i);
      if (target == null || line.contains(target)) {
        return line.trim();
      }
    }
    return null;
  }

  private static String extractTimestamp(String line) {
    String[] parts = line.trim().split("\\s+");
    if (parts.length < 2) {
      return null;
    }
    return parts[0] + " " + parts[1];
  }

  private static String formatDuration(Duration duration) {
    long seconds = duration.getSeconds();
    long absSeconds = Math.abs(seconds);
    long hours = absSeconds / 3600;
    long minutes = (absSeconds % 3600) / 60;
    long secs = absSeconds % 60;

    return String.format("%s%d:%02d:%02d", seconds < 0 ? "-" : "", hours, minutes, secs);
  }
}
