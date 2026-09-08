/*
 * Anserini: An information retrieval toolkit for reproducible research.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SummarizeLogsFromDocumentCollectionTest {
  private Path temporaryWorkingDirectory;

  @Before
  public void setUp() throws Exception {
    temporaryWorkingDirectory = Files.createTempDirectory("summarize-logs-from-document-collection");
  }

  @After
  public void tearDown() throws Exception {
    if (temporaryWorkingDirectory != null) {
      FileUtils.deleteDirectory(new File(temporaryWorkingDirectory.toString()));
    }
  }

  @Test
  public void testHelp() throws Exception {
    Files.createDirectory(temporaryWorkingDirectory.resolve("logs"));
    String output = runInTempDirectory("--help");

    assertTrue(output.contains("Options for SummarizeLogsFromDocumentCollection:"));
    assertTrue(output.contains("--help"));
  }

  @Test
  public void testInvalidOptionShowsUsage() throws Exception {
    Files.createDirectory(temporaryWorkingDirectory.resolve("logs"));
    String output = runInTempDirectory("--not-a-real-option");

    assertTrue(output.contains("Error:"));
    assertTrue(output.contains("not a valid option"));
    assertTrue(output.contains("Options for SummarizeLogsFromDocumentCollection:"));
  }

  @Test
  public void testHelpOutputOmitsBooleanMetaVarForAliasedOptions() throws Exception {
    Files.createDirectory(temporaryWorkingDirectory.resolve("logs"));
    String output = runInTempDirectory("--help");

    assertTrue(output.contains("--md, --markdown"));
    assertTrue(output.contains("--text, --plain-text"));
    assertTrue(output.contains("--json"));
    assertTrue(!output.contains("--json [boolean]"));
    assertTrue(!output.contains("--md [boolean], --markdown [boolean]"));
    assertTrue(!output.contains("--text [boolean], --plain-text [boolean]"));
  }

  @Test
  public void testSummarizeLogs() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    writeLog(logsDir.resolve("log.from-document-collection.1"), List.of(
        "2026-03-01 10:00:00,100 Starting ReproduceFromDocumentCollection for topic 1",
        "2026-03-01 10:00:01,200 ReproduceFromDocumentCollection" + ReproductionUtils.Constants.OK + " completed topic 1"));

    writeLog(logsDir.resolve("log.from-document-collection.2"), List.of(
        "2026-03-01 10:00:02,300 Starting ReproduceFromDocumentCollection for topic 2",
        "2026-03-01 10:00:04,500 ReproduceFromDocumentCollection" + ReproductionUtils.Constants.FAIL + " completed topic 2"));

    String output = runInTempDirectory();
    assertTrue(Pattern.compile("Total regressions:\\s+2").matcher(output).find());
    assertEquals(1, countForStatusLine(output, ReproductionUtils.Constants.OK));
    assertEquals(0, countForStatusLine(output, ReproductionUtils.Constants.OKISH));
    assertEquals(1, countForStatusLine(output, ReproductionUtils.Constants.FAIL));

    assertTrue(Pattern.compile("(?m)^\\s*Start time:\\s+\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}(?:,\\d+)?\\s+.+$").matcher(output).find());
    assertTrue(Pattern.compile("(?m)^\\s*End time:\\s+\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}(?:,\\d+)?\\s+.+$").matcher(output).find());
    assertTrue(Pattern.compile("Duration:\\s+00:00:04").matcher(output).find());
  }

  @Test
  public void testSummarizeLogsJson() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    writeLog(logsDir.resolve("log.from-document-collection.1"), List.of(
        "2026-03-01 10:00:00,100 Starting ReproduceFromDocumentCollection for topic 1",
        "2026-03-01 10:00:01,200 ReproduceFromDocumentCollection" + ReproductionUtils.Constants.OK + " completed topic 1"));

    writeLog(logsDir.resolve("log.from-document-collection.2"), List.of(
        "2026-03-01 10:00:02,300 Starting ReproduceFromDocumentCollection for topic 2",
        "2026-03-01 10:00:04,500 ReproduceFromDocumentCollection" + ReproductionUtils.Constants.FAIL + " completed topic 2"));

    String output = runInTempDirectory("--json");

    assertTrue(output.contains("\"total_regressions\": 2"));
    assertTrue(output.contains("\"status_counts\": {"));
    assertTrue(output.contains("\"[OK]\": 1"));
    // Retain the historical JSON key for backward compatibility with downstream consumers.
    assertTrue(output.contains("\"[OK*]\": 0"));
    assertTrue(output.contains("\"[FAIL]\": 1"));
    assertTrue(output.contains("\"start_time\": "));
    assertTrue(output.contains("\"end_time\": "));
    assertTrue(output.contains("\"duration\": \"00:00:04\""));
  }

  @Test
  public void testSummarizeLogsMarkdown() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    writeLog(logsDir.resolve("log.from-document-collection.1"), List.of(
        "2026-03-01 10:00:00,100 Starting ReproduceFromDocumentCollection for topic 1",
        "2026-03-01 10:00:01,200 ReproduceFromDocumentCollection" + ReproductionUtils.Constants.OK + " completed topic 1"));

    writeLog(logsDir.resolve("log.from-document-collection.2"), List.of(
        "2026-03-01 10:00:02,300 Starting ReproduceFromDocumentCollection for topic 2",
        "2026-03-01 10:00:04,500 ReproduceFromDocumentCollection" + ReproductionUtils.Constants.FAIL + " completed topic 2"));

    String output = runInTempDirectory("--md");

    assertTrue(Pattern.compile("Total regressions:\\s+2").matcher(output).find());
    assertTrue(output.contains("| status  | count |"));
    assertTrue(output.contains("| ------- | ----: |"));
    assertTrue(Pattern.compile("\\| \\[OK\\]\\s+\\|\\s+1 \\|").matcher(output).find());
    assertTrue(Pattern.compile("\\| \\[OKish\\]\\s*\\|\\s+0 \\|").matcher(output).find());
    assertTrue(Pattern.compile("\\| \\[FAIL\\]\\s+\\|\\s+1 \\|").matcher(output).find());
    assertTrue(Pattern.compile("(?m)^Start time:").matcher(output).find());
    assertTrue(Pattern.compile("(?m)^End time:").matcher(output).find());
    assertTrue(Pattern.compile("Duration:\\s+00:00:04").matcher(output).find());
  }

  @Test
  public void testSummarizeLogsMissingStatusAndTimestamp() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    writeLog(logsDir.resolve("log.from-document-collection.empty"), List.of(
        "This is not a reproduction line",
        "another non-matching line"));

    writeLog(logsDir.resolve("log.from-document-collection.partial"), List.of(
        "2026-03-01 10:00:00,100 No timestamp format expected",
        "ReproduceFromDocumentCollection without timestamp"));

    String output = runInTempDirectory();

    assertTrue(Pattern.compile("Total regressions:\\s+2").matcher(output).find());
    assertEquals(0, countForStatusLine(output, ReproductionUtils.Constants.OK));
    assertEquals(0, countForStatusLine(output, ReproductionUtils.Constants.OKISH));
    assertEquals(0, countForStatusLine(output, ReproductionUtils.Constants.FAIL));

    assertTrue(Pattern.compile("Start time:\\s+n/a").matcher(output).find());
    assertTrue(Pattern.compile("End time:\\s+n/a").matcher(output).find());
    assertTrue(Pattern.compile("Duration:\\s+n/a").matcher(output).find());
  }

  @Test
  public void testSummarizeLogsInvalidOptionCombinations() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    assertInvalidOption("--json", "--md");
    assertInvalidOption("--json", "--plain-text");
    assertInvalidOption("--md", "--plain-text");
    assertInvalidOption("--json", "--text");
    assertInvalidOption("--md", "--text");
    assertInvalidOption("--json", "--md", "--plain-text");
  }

  @Test
  public void testSummarizeLogsTextAlias() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    writeLog(logsDir.resolve("log.from-document-collection.1"), List.of(
        "2026-03-01 10:00:00,100 Starting ReproduceFromDocumentCollection for topic 1",
        "2026-03-01 10:00:01,200 ReproduceFromDocumentCollection" + ReproductionUtils.Constants.OK + " completed topic 1"));

    String output = runInTempDirectory("--text");
    assertTrue(Pattern.compile("Total regressions:\\s+1").matcher(output).find());

    output = runInTempDirectory("--plain-text");
    assertTrue(Pattern.compile("Total regressions:\\s+1").matcher(output).find());
  }

  @Test
  public void testStatusLabelsRemainAligned() {
    assertEquals("   [OK] ", stripAnsi(ReproductionUtils.Constants.OK));
    assertEquals("[OKish] ", stripAnsi(ReproductionUtils.Constants.OKISH));
    assertEquals(" [FAIL] ", stripAnsi(ReproductionUtils.Constants.FAIL));
  }

  @Test
  public void testHistoricalCurrentAndMixedStatusLabels() throws Exception {
    Path logsDir = Files.createDirectory(temporaryWorkingDirectory.resolve("logs"));
    // Cover backward compatibility with historical logs, current logs, and mixed collections.
    List<List<String>> collections = List.of(List.of("[OK*]"), List.of("[OKish]"), List.of("[OK*]", "[OKish]"));
    for (List<String> labels : collections) {
      FileUtils.cleanDirectory(logsDir.toFile());
      int logCount = 0;
      for (String label : labels) {
        for (String status : List.of(label, "\u001B[94m  " + label + " \u001B[0m")) {
          writeLog(logsDir.resolve("log.from-document-collection." + logCount++), List.of(
              "2026-03-01 10:00:00,100 ReproduceFromDocumentCollection [FAIL] intermediate check",
              // Historical log fixture retained to verify backward-compatible parsing.
              "2026-03-01 10:00:01,200 ReproduceFromDocumentCollection [OK*] historical check",
              "2026-03-01 10:00:02,300 ReproduceFromDocumentCollection [OKish] current check",
              "2026-03-01 10:00:04,500 ReproduceFromDocumentCollection " + status + " Total elapsed time: 4s",
              "2026-03-01 10:00:05,600 OtherClass [FAIL] unrelated line"));
        }
      }
      JsonNode summary = new ObjectMapper().readTree(runInTempDirectory("--json"));
      assertEquals(logCount, summary.get("total_regressions").asInt());
      JsonNode counts = summary.get("status_counts");
      assertEquals(3, counts.size());
      assertEquals(0, counts.get("[OK]").asInt());
      // Retain the historical JSON key for backward compatibility with downstream consumers.
      assertEquals(logCount, counts.get("[OK*]").asInt());
      assertEquals(0, counts.get("[FAIL]").asInt());
      assertFalse(counts.has("[OKish]"));
      assertEquals("00:00:04", summary.get("duration").asText());

      String markdown = runInTempDirectory("--md");
      // Backward compatibility preserves log input and JSON keys; readable output uses the current label.
      assertFalse(markdown.contains("[OK*]"));
      assertTrue(markdown.contains("| [OKish] |     " + logCount + " |"));
      for (String line : markdown.split("\\R")) {
        if (line.startsWith("|")) {
          assertEquals("| status  | count |".length(), line.length());
        }
      }
      String text = stripAnsi(runInTempDirectory("--text"));
      // Backward compatibility preserves log input and JSON keys; readable output uses the current label.
      assertFalse(text.contains("[OK*]"));
      assertTrue(text.contains(" [OKish]    " + logCount));
      assertTrue(text.contains("    [OK]    0"));
      assertTrue(text.contains("  [FAIL]    0"));
    }
  }

  private String stripAnsi(String text) {
    return text.replaceAll("\\x1B\\[[0-9;]*m", "");
  }

  private void assertInvalidOption(String... args) throws Exception {
    try {
      runInTempDirectory(args);
      fail("Expected IllegalArgumentException for invalid output combination");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Only one output mode may be specified"));
    }
  }

  private String runInTempDirectory(String... args) throws Exception {
    PrintStream previousOut = System.out;
    PrintStream previousErr = System.err;
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    Path workingDirLogs = Paths.get("logs");
    Path backupLogs = null;
    Path sourceLogs = temporaryWorkingDirectory.resolve("logs");

    try (PrintStream redirectedOut = new PrintStream(output, true, StandardCharsets.UTF_8);
         PrintStream redirectedErr = new PrintStream(output, true, StandardCharsets.UTF_8)) {
      if (Files.exists(workingDirLogs)) {
        backupLogs = temporaryWorkingDirectory.resolve("logs-backup-" + System.nanoTime());
        FileUtils.moveDirectory(workingDirLogs.toFile(), backupLogs.toFile());
      }
      FileUtils.copyDirectory(sourceLogs.toFile(), workingDirLogs.toFile());

      System.setOut(redirectedOut);
      System.setErr(redirectedErr);

      SummarizeLogsFromDocumentCollection.main(args);
    } finally {
      System.setOut(previousOut);
      System.setErr(previousErr);
      if (Files.exists(workingDirLogs)) {
        FileUtils.deleteDirectory(workingDirLogs.toFile());
      }
      if (backupLogs != null) {
        FileUtils.moveDirectory(backupLogs.toFile(), workingDirLogs.toFile());
      }
    }

    return output.toString(StandardCharsets.UTF_8);
  }

  private void writeLog(Path path, List<String> lines) throws IOException {
    Files.write(path, lines);
  }

  private int countForStatusLine(String output, String statusLabel) {
    for (String line : output.split("\\R")) {
      if (line.contains(statusLabel)) {
        String[] tokens = line.trim().split("\\s+");
        return Integer.parseInt(tokens[tokens.length - 1]);
      }
    }
    fail("Missing status line for label: " + statusLabel);
    return -1;
  }
}
