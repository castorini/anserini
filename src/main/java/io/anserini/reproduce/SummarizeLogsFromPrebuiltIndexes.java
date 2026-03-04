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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SummarizeLogsFromPrebuiltIndexes {
  private static final Pattern CONDITION_PATTERN = Pattern.compile("^# Running condition \"([^\"]+)\":");
  private static final Pattern DURATION_PATTERN = Pattern.compile("^Duration:\\s+.*\\((\\d{2}:\\d{2}:\\d{2})\\)\\s*$");
  private static final String LOG_GLOB = "log.from-prebuilt-indexes.*.txt";
  private static final String LOG_PREFIX = "log.from-prebuilt-indexes.";
  private static final String LOG_SUFFIX = ".txt";

  public static void main(String[] args) {
    Path logsDir = Paths.get("logs");
    List<String[]> rows = new ArrayList<>();

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir, LOG_GLOB)) {
      for (Path logFile : stream) {
        if (!Files.isRegularFile(logFile)) {
          continue;
        }

        int okCount = 0;
        int okishCount = 0;
        int failCount = 0;
        String duration = "n/a";
        Set<String> conditions = new LinkedHashSet<>();

        try (var lines = Files.lines(logFile, StandardCharsets.UTF_8)) {
          for (String line : (Iterable<String>) lines::iterator) {
            if (line.contains("[OK*]")) {
              okishCount++;
            } else if (line.contains("[OK]")) {
              okCount++;
            } else if (line.contains("[FAIL]")) {
              failCount++;
            }

            Matcher conditionMatcher = CONDITION_PATTERN.matcher(line);
            if (conditionMatcher.find()) {
              conditions.add(conditionMatcher.group(1));
            }

            Matcher durationMatcher = DURATION_PATTERN.matcher(line);
            if (durationMatcher.find()) {
              duration = durationMatcher.group(1);
            } else if (line.startsWith("Duration:")) {
              duration = line.replaceFirst("^Duration:\\s*", "").trim();
            }
          }
        }

        String fileName = logFile.getFileName().toString();
        String runId = fileName.startsWith(LOG_PREFIX) && fileName.endsWith(LOG_SUFFIX)
            ? fileName.substring(LOG_PREFIX.length(), fileName.length() - LOG_SUFFIX.length())
            : fileName;
        String condition = conditions.isEmpty() ? "(none)" : String.join(",", conditions);
        rows.add(new String[]{
            runId,
            condition,
            Integer.toString(okCount),
            Integer.toString(okishCount),
            Integer.toString(failCount),
            duration
        });
      }
    } catch (IOException e) {
      throw new RuntimeException("Error reading prebuilt-index logs.", e);
    }

    final String[] headers = {"run", "condition", "[OK]", "[OK*]", "[FAIL]", "elapsed"};
    int[] widths = new int[headers.length];
    for (int i = 0; i < headers.length; i++) {
      widths[i] = headers[i].length();
    }
    for (String[] row : rows) {
      for (int i = 0; i < headers.length; i++) {
        widths[i] = Math.max(widths[i], row[i].length());
      }
    }

    StringBuilder sb = new StringBuilder(Math.max(256, rows.size() * 112));
    appendRow(sb, headers, widths);
    appendSeparator(sb, widths);
    for (String[] row : rows) {
      appendRow(sb, row, widths);
    }
    System.out.print(sb);
  }

  private static void appendRow(StringBuilder sb, String[] row, int[] widths) {
    sb.append("| ");
    for (int i = 0; i < row.length; i++) {
      if (i == 2 || i == 3 || i == 4) {
        sb.append(" ".repeat(widths[i] - row[i].length())).append(row[i]);
      } else {
        sb.append(row[i]).append(" ".repeat(widths[i] - row[i].length()));
      }
      sb.append(i == row.length - 1 ? " |\n" : " | ");
    }
  }

  private static void appendSeparator(StringBuilder sb, int[] widths) {
    sb.append("| ");
    for (int i = 0; i < widths.length; i++) {
      sb.append("-".repeat(widths[i])).append(i == widths.length - 1 ? " |\n" : " | ");
    }
  }
}
