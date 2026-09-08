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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
    writeSampleLogs();
    String output = runInTempDirectory();
    // Keep the ANSI status labels in the expected output to check color and padding together.
    assertEquals("""
        Total regressions:   2
         %s   1
         %s   0
         %s   1

        Start time: 2026-03-01 10:00:00 %s
        End time:   2026-03-01 10:00:04 %s
        Duration:   00:00:04
        """.formatted(ReproductionUtils.Constants.OK, ReproductionUtils.Constants.OKISH,
        ReproductionUtils.Constants.FAIL, sampleTimeZone(), sampleTimeZone()), output);
  }

  @Test
  public void testSummarizeLogsJson() throws Exception {
    writeSampleLogs();
    String output = runInTempDirectory("--json");

    String expected = """
        {
          "total_regressions": 2,
          "status_counts": {
            "[OK]": 1,
            "[OKish]": 0,
            "[FAIL]": 1
          },
          "start_time": "2026-03-01 10:00:00 %s",
          "end_time": "2026-03-01 10:00:04 %s",
          "duration": "00:00:04"
        }
        """.formatted(sampleTimeZone(), sampleTimeZone());
    ObjectMapper mapper = new ObjectMapper();
    assertEquals(mapper.readTree(expected), mapper.readTree(output));
    // Also preserve the public output's field order, indentation, and final newline.
    assertEquals(expected, output);
  }

  @Test
  public void testSummarizeLogsMarkdown() throws Exception {
    writeSampleLogs();
    assertEquals("""
        Total regressions:   2

        | status  | count |
        | ------- | ----: |
        | [OK]    |     1 |
        | [OKish] |     0 |
        | [FAIL]  |     1 |

        Start time: 2026-03-01 10:00:00 %s
        End time:   2026-03-01 10:00:04 %s
        Duration:   00:00:04
        """.formatted(sampleTimeZone(), sampleTimeZone()), runInTempDirectory("--md").replace("\r\n", "\n"));
  }

  @Test
  public void testSummarizeLogsMissingStatusAndTimestamp() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    Files.writeString(logsDir.resolve("log.from-document-collection.empty"), """
        This is not a reproduction line
        another non-matching line
        """);

    Files.writeString(logsDir.resolve("log.from-document-collection.partial"), """
        2026-03-01 10:00:00,100 No timestamp format expected
        ReproduceFromDocumentCollection without timestamp
        """);

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
    writeSampleLogs();
    String expected = runInTempDirectory();
    assertEquals(expected, runInTempDirectory("--text"));
    assertEquals(expected, runInTempDirectory("--plain-text"));
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
          // Historical log fixture retained to verify backward-compatible parsing.
          Files.writeString(logsDir.resolve("log.from-document-collection." + logCount++), """
              2026-03-01 10:00:00,100 ReproduceFromDocumentCollection [FAIL] intermediate check
              2026-03-01 10:00:01,200 ReproduceFromDocumentCollection [OK*] historical check
              2026-03-01 10:00:02,300 ReproduceFromDocumentCollection [OKish] current check
              2026-03-01 10:00:04,500 ReproduceFromDocumentCollection %s Total elapsed time: 4s
              2026-03-01 10:00:05,600 OtherClass [FAIL] unrelated line
              """.formatted(status));
        }
      }
      JsonNode summary = new ObjectMapper().readTree(runInTempDirectory("--json"));
      assertEquals(logCount, summary.get("total_regressions").asInt());
      JsonNode counts = summary.get("status_counts");
      assertEquals(3, counts.size());
      assertEquals(0, counts.get("[OK]").asInt());
      assertEquals(logCount, counts.get("[OKish]").asInt());
      assertEquals(0, counts.get("[FAIL]").asInt());
      // Backward compatibility accepts historical log input; JSON emits only the current key.
      assertFalse(counts.has("[OK*]"));
      assertEquals("00:00:04", summary.get("duration").asText());

      String markdown = runInTempDirectory("--md");
      // Backward compatibility accepts historical log input; all output uses the current label.
      assertFalse(markdown.contains("[OK*]"));
      assertTrue(markdown.contains("| [OKish] |     " + logCount + " |"));
      for (String line : markdown.split("\\R")) {
        if (line.startsWith("|")) {
          assertEquals("| status  | count |".length(), line.length());
        }
      }
      String text = stripAnsi(runInTempDirectory("--text"));
      // Backward compatibility accepts historical log input; all output uses the current label.
      assertFalse(text.contains("[OK*]"));
      assertTrue(text.contains(" [OKish]    " + logCount));
      assertTrue(text.contains("    [OK]    0"));
      assertTrue(text.contains("  [FAIL]    0"));
    }
  }

  @Test
  public void testEmptyLogsJson() throws Exception {
    Files.createDirectory(temporaryWorkingDirectory.resolve("logs"));
    String expected = """
        {
          "total_regressions": 0,
          "status_counts": {"[OK]": 0, "[OKish]": 0, "[FAIL]": 0},
          "start_time": "n/a",
          "end_time": "n/a",
          "duration": "n/a"
        }
        """;
    ObjectMapper mapper = new ObjectMapper();
    assertEquals(mapper.readTree(expected), mapper.readTree(runInTempDirectory("--json")));
  }

  @Test
  public void testLargeCountsPreserveAlignmentAndLocale() {
    Locale previousLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.forLanguageTag("ar-LB"));
      int[] counts = {123456, 12, 3};
      String[] labels = {"[OK]", "[OKish]", "[FAIL]"};
      String markdown = SummarizeLogsFromDocumentCollection.formatSummaryMarkdown(
          123471, counts, labels, null, null, null);
      assertEquals("""
          Total regressions: 123471

          | status  | count  |
          | ------- | -----: |
          | [OK]    | 123456 |
          | [OKish] |     12 |
          | [FAIL]  |      3 |

          Start time: n/a
          End time:   n/a
          Duration:   n/a
          """, markdown.replace("\r\n", "\n"));

      String[] coloredLabels = {ReproductionUtils.Constants.OK, ReproductionUtils.Constants.OKISH,
          ReproductionUtils.Constants.FAIL};
      String text = SummarizeLogsFromDocumentCollection.formatSummaryPlainText(
          123471, counts, coloredLabels, null, null, null);
      assertEquals("""
          Total regressions: 123471
              [OK]  123456
           [OKish]   12
            [FAIL]    3

          Start time: n/a
          End time:   n/a
          Duration:   n/a
          """, stripAnsi(text));
    } finally {
      Locale.setDefault(previousLocale);
    }
  }

  private void writeSampleLogs() throws IOException {
    Path logsDir = Files.createDirectory(temporaryWorkingDirectory.resolve("logs"));
    Files.writeString(logsDir.resolve("log.from-document-collection.1"), """
        2026-03-01 10:00:00,100 Starting ReproduceFromDocumentCollection for topic 1
        2026-03-01 10:00:01,200 ReproduceFromDocumentCollection%s completed topic 1
        """.formatted(ReproductionUtils.Constants.OK));
    Files.writeString(logsDir.resolve("log.from-document-collection.2"), """
        2026-03-01 10:00:02,300 Starting ReproduceFromDocumentCollection for topic 2
        2026-03-01 10:00:04,500 ReproduceFromDocumentCollection%s completed topic 2
        """.formatted(ReproductionUtils.Constants.FAIL));
  }

  private String sampleTimeZone() {
    return ZonedDateTime.of(2026, 3, 1, 10, 0, 0, 0, ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("z", Locale.ROOT));
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
    String[] mainArgs = Arrays.copyOf(args, args.length + 2);
    mainArgs[args.length] = "--logs-directory";
    mainArgs[args.length + 1] = temporaryWorkingDirectory.resolve("logs").toString();

    try (PrintStream redirected = new PrintStream(output, true, StandardCharsets.UTF_8)) {
      System.setOut(redirected);
      System.setErr(redirected);
      SummarizeLogsFromDocumentCollection.main(mainArgs);
    } finally {
      System.setOut(previousOut);
      System.setErr(previousErr);
    }
    return output.toString(StandardCharsets.UTF_8);
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
