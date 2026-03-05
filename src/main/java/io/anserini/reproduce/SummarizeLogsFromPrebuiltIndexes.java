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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

public class SummarizeLogsFromPrebuiltIndexes {
  private static final Pattern DURATION_PATTERN = Pattern.compile("^Duration:\\s+.*\\((\\d{2}:\\d{2}:\\d{2})\\)\\s*$");
  private static final String LOG_GLOB = "log.from-prebuilt-indexes.*";
  private static final String LOG_PREFIX = "log.from-prebuilt-indexes.";
  private static final String LOG_SUFFIX = ".txt";

  public static class Args {
    @Option(name = "--logs", usage = "Path to log directory (default: logs).")
    public String logs = Constants.DEFAULT_LOGS_DIRECTORY;

    @Option(name = "--md", aliases = {"--markdown"}, usage = "Emit output in markdown table format.")
    public boolean markdown = false;

    @Option(name = "--plain-text", usage = "Emit output in plain text table format.")
    public boolean plainText = false;

    @Option(name = "--json", usage = "Emit output in JSON format.")
    public boolean json = false;
  }

  public static void main(String[] args) {
    Args parsedArgs = new Args();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(120));
    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    int selectedOutputs = (parsedArgs.markdown ? 1 : 0) + (parsedArgs.plainText ? 1 : 0) + (parsedArgs.json ? 1 : 0);
    if (selectedOutputs > 1) {
      throw new IllegalArgumentException("Only one output mode may be specified among --md, --plain-text, and --json.");
    }

    Path logsDir = Paths.get(parsedArgs.logs);
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

        try (var lines = Files.lines(logFile, StandardCharsets.UTF_8)) {
          for (String line : (Iterable<String>) lines::iterator) {
            if (line.contains("[OK*]")) {
              okishCount++;
            } else if (line.contains("[OK]")) {
              okCount++;
            } else if (line.contains("[FAIL]")) {
              failCount++;
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
        String runId = fileName;
        if (runId.startsWith(LOG_PREFIX)) {
          runId = runId.substring(LOG_PREFIX.length());
        }
        if (runId.endsWith(LOG_SUFFIX)) {
          runId = runId.substring(0, runId.length() - LOG_SUFFIX.length());
        }
        rows.add(new String[]{
            runId,
            Integer.toString(okCount),
            Integer.toString(okishCount),
            Integer.toString(failCount),
            duration
        });
      }
    } catch (IOException e) {
      throw new RuntimeException("Error reading prebuilt-index logs.", e);
    }

    rows.sort((left, right) -> left[0].compareTo(right[0]));

    final String[] headers = {"run", "[OK]", "[OK*]", "[FAIL]", "elapsed"};
    int[] widths = new int[headers.length];
    for (int i = 0; i < headers.length; i++) {
      widths[i] = headers[i].length();
    }
    for (String[] row : rows) {
      for (int i = 0; i < headers.length; i++) {
        widths[i] = Math.max(widths[i], row[i].length());
      }
    }
    int statusWidth = Math.max(widths[1], Math.max(widths[2], widths[3]));
    widths[1] = statusWidth;
    widths[2] = statusWidth;
    widths[3] = statusWidth;

    StringBuilder sb = new StringBuilder(Math.max(256, rows.size() * 112));
    appendHeaderRow(sb, headers, widths);
    appendSeparator(sb, widths);
    for (String[] row : rows) {
      appendRow(sb, row, widths);
    }
    if (parsedArgs.json) {
      System.out.print(rowsToJson(rows));
    } else if (parsedArgs.markdown) {
      System.out.print(sb);
    } else {
      System.out.print(stripTableDelimiters(sb));
    }
  }

  private static void appendRow(StringBuilder sb, String[] row, int[] widths) {
    sb.append("| ");
    for (int i = 0; i < row.length; i++) {
      if (i >= 1 && i <= 3) {
        sb.append(" ".repeat(widths[i] - row[i].length())).append(row[i]);
      } else {
        sb.append(row[i]).append(" ".repeat(widths[i] - row[i].length()));
      }
      sb.append(i == row.length - 1 ? " |\n" : " | ");
    }
  }

  private static void appendHeaderRow(StringBuilder sb, String[] row, int[] widths) {
    sb.append("| ");
    for (int i = 0; i < row.length; i++) {
      if (i >= 1 && i <= 3) {
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
      if (i >= 1 && i <= 3) {
        sb.append("-".repeat(Math.max(1, widths[i] - 1))).append(":");
      } else {
        sb.append("-".repeat(widths[i]));
      }
      sb.append(i == widths.length - 1 ? " |\n" : " | ");
    }
  }

  private static StringBuilder stripTableDelimiters(StringBuilder sb) {
    StringBuilder plainText = new StringBuilder();
    for (String line : sb.toString().split("\\R", -1)) {
      if (line.isEmpty()) {
        plainText.append('\n');
        continue;
      }
      String transformed = line.replace('|', ' ');
      String trimmed = transformed.trim();
      if (trimmed.startsWith("-")) {
        transformed = transformed.replaceAll("(-+):(?=\\s|$)", "$1-");
      }
      plainText.append(transformed.replaceFirst("^\\s+", "")).append('\n');
    }
    return plainText;
  }

  private static String rowsToJson(List<String[]> rows) {
    StringBuilder sb = new StringBuilder();
    sb.append("[\n");
    for (int i = 0; i < rows.size(); i++) {
      String[] row = rows.get(i);
      sb.append("  {\n")
          .append("    \"run\": \"").append(escapeJson(row[0])).append("\",\n")
          .append("    \"[OK]\": ").append(row[1]).append(",\n")
          .append("    \"[OK*]\": ").append(row[2]).append(",\n")
          .append("    \"[FAIL]\": ").append(row[3]).append(",\n")
          .append("    \"elapsed\": \"").append(escapeJson(row[4])).append("\"\n")
          .append("  }");
      if (i < rows.size() - 1) {
        sb.append(",\n");
      } else {
        sb.append('\n');
      }
    }
    sb.append("]\n");
    return sb.toString();
  }

  private static String escapeJson(String value) {
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\b", "\\b")
        .replace("\f", "\\f")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t");
  }
}
