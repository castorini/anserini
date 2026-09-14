/*
 * Anserini: The Apache Lucene toolkit for reproducible IR research.
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
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SummarizeLogsFromPrebuiltIndexesTest {
  private Path temporaryWorkingDirectory;

  @Before
  public void setUp() throws Exception {
    temporaryWorkingDirectory = Files.createTempDirectory("summarize-prebuilt-indexes");
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

    assertTrue(output.contains("Options for SummarizeLogsFromPrebuiltIndexes:"));
    assertTrue(output.contains("--help"));
  }

  @Test
  public void testInvalidOptionShowsUsage() throws Exception {
    Files.createDirectory(temporaryWorkingDirectory.resolve("logs"));
    String output = runInTempDirectory("--not-a-real-option");

    assertTrue(output.contains("Error:"));
    assertTrue(output.contains("not a valid option"));
    assertTrue(output.contains("Options for SummarizeLogsFromPrebuiltIndexes:"));
  }

  @Test
  public void testSummarizeLogsFromPrebuiltIndexesJson() throws Exception {
    writeSampleLogs();
    String output = runInTempDirectory("--json");

    String expected = """
        [
          {
            "run": "alpha",
            "[OK]": 0,
            "[OKish]": 0,
            "[FAIL]": 2,
            "elapsed": "00:00:01"
          },
          {
            "run": "betaset",
            "[OK]": 1,
            "[OKish]": 1,
            "[FAIL]": 0,
            "elapsed": "01:03:04"
          }
        ]
        """;
    ObjectMapper mapper = new ObjectMapper();
    assertEquals(mapper.readTree(expected), mapper.readTree(output));
    // Also preserve the public output's field order, indentation, and final newline.
    assertEquals(expected, output);
  }

  @Test
  public void testSummarizeLogsFromPrebuiltIndexesMarkdown() throws Exception {
    writeSampleLogs();
    assertEquals("""
        | run     |    [OK] | [OKish] |  [FAIL] | elapsed  |
        | ------- | ------: | ------: | ------: | -------- |
        | alpha   |       0 |       0 |       2 | 00:00:01 |
        | betaset |       1 |       1 |       0 | 01:03:04 |
        """, runInTempDirectory("--md"));
  }

  @Test
  public void testSummarizeLogsFromPrebuiltIndexesPlainText() throws Exception {
    writeSampleLogs();
    // Explicit spaces preserve the existing trailing padding and final blank line.
    assertEquals(String.join("\n",
        "run          [OK]   [OKish]    [FAIL]   elapsed   ",
        "-------   -------   -------   -------   --------  ",
        "alpha           0         0         2   00:00:01  ",
        "betaset         1         1         0   01:03:04  ",
        "",
        ""), runInTempDirectory("--plain-text"));
  }

  @Test
  public void testSummarizeLogsFromPrebuiltIndexesInvalidOptionCombinations() throws Exception {
    assertInvalidOption("--json", "--md");
    assertInvalidOption("--json", "--plain-text");
    assertInvalidOption("--md", "--plain-text");
    assertInvalidOption("--json", "--md", "--plain-text");
  }

  @Test
  public void testSummarizeLogsFromPrebuiltIndexesMissingLogsDirectory() throws Exception {
    Path missingLogsDirectory = temporaryWorkingDirectory.resolve("logs-missing");

    String output = runInTempDirectory(missingLogsDirectory, "--json");

    assertTrue(output.contains("No logs directory found: " + missingLogsDirectory));
    assertTrue(!output.contains("\"run\""));
  }

  @Test
  public void testSummarizeLogsFromPrebuiltIndexesNoMatchingLogs() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);
    Files.write(logsDir.resolve("unrelated.txt"), List.of("not a prebuilt log"));

    String output = runInTempDirectory(logsDir, "--md");

    assertTrue(output.contains("No prebuilt-index logs found in: " + logsDir + " (pattern: log.from-prebuilt-indexes.*)"));
    assertTrue(!output.contains("| "));
  }

  @Test
  public void testSummarizeLogsFromPrebuiltIndexesMalformedAndUnexpectedContent() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    // Historical log fixture retained to verify backward-compatible parsing.
    Files.writeString(logsDir.resolve("log.from-prebuilt-indexes.malformed.txt"), """
        Run for malformed [OK]
        Mangled marker [FAILURE] should not count
        Duration: 01:02
        Another token [OK*] and [OKAY]
        No duration value here
        """);

    String expected = String.join("\n",
        "[{\"run\": \"malformed\", \"[OK]\": 1, \"[OKish]\": 1, \"[FAIL]\": 0, \"elapsed\": \"01:02\"}]",
        "");
    ObjectMapper mapper = new ObjectMapper();
    assertEquals(mapper.readTree(expected), mapper.readTree(runInTempDirectory(logsDir, "--json")));
  }

  @Test
  public void testHistoricalCurrentAndMixedStatusLabels() throws Exception {
    // Cover backward compatibility with historical logs, current logs, and mixed collections.
    List<List<String>> collections = List.of(List.of("[OK*]"), List.of("[OKish]"), List.of("[OK*]", "[OKish]"));
    for (int collection = 0; collection < collections.size(); collection++) {
      Path logsDir = Files.createDirectory(temporaryWorkingDirectory.resolve("logs-" + collection));
      List<String> labels = collections.get(collection);
      for (int i = 0; i < labels.size(); i++) {
        Files.writeString(logsDir.resolve("log.from-prebuilt-indexes.run-" + i + ".txt"), """
            Metric %s
            Colored metric \u001B[94m%s\u001B[0m
            Metric [OK]
            Metric [FAIL]
            Ignore [OKishness] and [OKAY] and [FAILURE]
            Duration: done (00:00:01)
            """.formatted(labels.get(i), labels.get(i)));
      }
      if (labels.size() > 1) {
        // Historical log fixture retained to verify backward-compatible parsing.
        Files.writeString(logsDir.resolve("log.from-prebuilt-indexes.run-2.txt"), """
            Historical metric [OK*]
            Current metric [OKish]
            Metric [OK]
            Metric [FAIL]
            Duration: done (00:00:01)
            """);
      }
      int runCount = labels.size() > 1 ? 3 : 1;
      JsonNode rows = new ObjectMapper().readTree(runInTempDirectory(logsDir, "--json"));
      assertEquals(runCount, rows.size());
      for (JsonNode row : rows) {
        assertEquals(5, row.size());
        assertEquals(1, row.get("[OK]").asInt());
        assertEquals(2, row.get("[OKish]").asInt());
        assertEquals(1, row.get("[FAIL]").asInt());
        // Backward compatibility accepts historical log input; JSON emits only the current key.
        assertFalse(row.has("[OK*]"));
        assertEquals("00:00:01", row.get("elapsed").asText());
      }
      for (String mode : List.of("--md", "--text")) {
        String output = runInTempDirectory(logsDir, mode);
        assertTrue(output.contains("[OKish]"));
        // Backward compatibility accepts historical log input; all output uses the current label.
        assertFalse(output.contains("[OK*]"));
        String[] lines = output.stripTrailing().split("\\R");
        assertEquals(runCount + 2, lines.length);
        for (int i = 0; i < runCount; i++) {
          assertEquals(List.of("run-" + i, "1", "2", "1", "00:00:01"),
              List.of(lines[i + 2].replace('|', ' ').trim().split("\\s+")));
        }
      }
    }
  }

  @Test
  public void testJsonEscaping() throws Exception {
    Path logsDir = Files.createDirectory(temporaryWorkingDirectory.resolve("logs"));
    String runId = "quoted\"\\\n\r\t\b\f-é";
    String elapsed = "done \"quoted\" \\\t\b\f-é";
    Files.writeString(logsDir.resolve("log.from-prebuilt-indexes." + runId + ".txt"),
        "Metric [OK]\nDuration: " + elapsed + "\n");

    JsonNode rows = new ObjectMapper().readTree(runInTempDirectory("--json"));
    assertEquals(1, rows.size());
    assertEquals(runId, rows.get(0).get("run").asText());
    assertEquals(elapsed, rows.get(0).get("elapsed").asText());
  }

  @Test
  public void testJsonPreservesLegacyControlCharacterOutput() throws Exception {
    Path logsDir = Files.createDirectory(temporaryWorkingDirectory.resolve("logs"));
    String runId = "control-\u0001-\u000b-\u001f";
    Files.writeString(logsDir.resolve("log.from-prebuilt-indexes." + runId + ".txt"), "Metric [OK]\n");

    // Preserve the existing raw control characters rather than changing the escaping policy in this refactor.
    String expected = """
        [
          {
            "run": "control-\u0001-\u000b-\u001f",
            "[OK]": 1,
            "[OKish]": 0,
            "[FAIL]": 0,
            "elapsed": "n/a"
          }
        ]
        """;
    assertEquals(expected, runInTempDirectory("--json"));
  }

  @Test
  public void testTableExpandsForLongNamesAndLargeCounts() {
    Locale previousLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.forLanguageTag("ar-LB"));
      String output = SummarizeLogsFromPrebuiltIndexes.formatTable(List.<String[]>of(
          new String[]{"a-very-long-reproduction-run", "123456789", "12", "3", "100:00:00"}));
      assertEquals("""
          | run                          |      [OK] |   [OKish] |    [FAIL] | elapsed   |
          | ---------------------------- | --------: | --------: | --------: | --------- |
          | a-very-long-reproduction-run | 123456789 |        12 |         3 | 100:00:00 |
          """, output);
    } finally {
      Locale.setDefault(previousLocale);
    }
  }

  private void writeSampleLogs() throws Exception {
    Path logsDir = Files.createDirectory(temporaryWorkingDirectory.resolve("logs"));
    // Historical log fixture retained to verify backward-compatible parsing.
    Files.writeString(logsDir.resolve("log.from-prebuilt-indexes.betaset.txt"), """
        Run for beta [OK]
        Second line [OK*]
        Duration: done (01:03:04)
        """);
    Files.writeString(logsDir.resolve("log.from-prebuilt-indexes.alpha.txt"), """
        Run for alpha [FAIL]
        Failure [FAIL]
        Duration: 00:00:01
        """);
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
    return runInTempDirectory(temporaryWorkingDirectory.resolve("logs"), args);
  }

  private String runInTempDirectory(Path logsDirectory, String... args) throws Exception {
    return runMain(withLogsDirectoryArg(logsDirectory, args));
  }

  private String runMain(String... args) throws Exception {
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    PrintStream newOut = new PrintStream(stdout);

    PrintStream originalOut = System.out;
    PrintStream originalErr = System.err;

    try {
      System.setOut(newOut);
      System.setErr(newOut);
      SummarizeLogsFromPrebuiltIndexes.main(args);
      return stdout.toString(StandardCharsets.UTF_8);
    } finally {
      System.setOut(originalOut);
      System.setErr(originalErr);
      newOut.close();
    }
  }

  private String[] withLogsDirectoryArg(Path logsDirectory, String... args) {
    String[] mainArgs = new String[args.length + 2];
    System.arraycopy(args, 0, mainArgs, 0, args.length);
    mainArgs[args.length] = "--logs-directory";
    mainArgs[args.length + 1] = logsDirectory.toString();
    return mainArgs;
  }
}
