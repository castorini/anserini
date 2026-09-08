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
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    Files.write(logsDir.resolve("log.from-prebuilt-indexes.betaset.txt"), List.of(
        "Run for beta [OK]",
        "Second line [OK*]",
        "Duration: done (01:03:04)"));

    Files.write(logsDir.resolve("log.from-prebuilt-indexes.alpha.txt"), List.of(
        "Run for alpha [FAIL]",
        "Failure [FAIL]",
        "Duration: 00:00:01"));

    String output = runInTempDirectory("--json");

    assertTrue(output.contains("\"run\": \"alpha\""));
    assertTrue(output.contains("\"run\": \"betaset\""));
    assertTrue(output.indexOf("\"run\": \"alpha\"") < output.indexOf("\"run\": \"betaset\""));

    assertTrue(output.contains("\"[OK]\": 0"));
    assertTrue(output.contains("\"[OK*]\": 0"));
    assertTrue(output.contains("\"[FAIL]\": 2"));
    assertTrue(output.contains("\"elapsed\": \"00:00:01\""));

    assertTrue(output.contains("\"[OK]\": 1"));
    assertTrue(output.contains("\"[OK*]\": 1"));
    assertTrue(output.contains("\"[FAIL]\": 0"));
    assertTrue(output.contains("\"elapsed\": \"01:03:04\""));
  }

  @Test
  public void testSummarizeLogsFromPrebuiltIndexesMarkdown() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    Files.write(logsDir.resolve("log.from-prebuilt-indexes.betaset.txt"), List.of(
        "Run for beta [OK]",
        "Second line [OK*]",
        "Duration: done (01:03:04)"));

    Files.write(logsDir.resolve("log.from-prebuilt-indexes.alpha.txt"), List.of(
        "Run for alpha [FAIL]",
        "Failure [FAIL]",
        "Duration: 00:00:01"));

    String output = runInTempDirectory("--md");

    String[] lines = output.strip().split("\\R");
    assertTrue(lines[0].startsWith("| run"));
    assertTrue(lines[0].contains("[OKish]"));
    assertFalse(output.contains("[OK*]"));
    for (String line : lines) {
      assertEquals(lines[0].length(), line.length());
    }
    assertTrue(lines[1].contains("| ------:"));
    assertTrue(lines[2].matches("\\|\\s*alpha\\s+\\|\\s+0\\s+\\|\\s+0\\s+\\|\\s+2\\s+\\|\\s+00:00:01\\s+\\|"));
    assertTrue(lines[3].matches("\\|\\s*betaset\\s+\\|\\s+1\\s+\\|\\s+1\\s+\\|\\s+0\\s+\\|\\s+01:03:04\\s+\\|"));
    assertTrue(output.indexOf("alpha") < output.indexOf("betaset"));
  }

  @Test
  public void testSummarizeLogsFromPrebuiltIndexesPlainText() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    Files.write(logsDir.resolve("log.from-prebuilt-indexes.betaset.txt"), List.of(
        "Run for beta [OK]",
        "Second line [OK*]",
        "Duration: done (01:03:04)"));

    Files.write(logsDir.resolve("log.from-prebuilt-indexes.alpha.txt"), List.of(
        "Run for alpha [FAIL]",
        "Failure [FAIL]",
        "Duration: 00:00:01"));

    String output = runInTempDirectory("--plain-text");

    String[] lines = output.strip().split("\\R");
    assertTrue(lines.length >= 4);
    assertTrue(!lines[0].contains("|"));
    assertTrue(!lines[1].contains("|"));
    assertTrue(lines[0].matches("\\s*run\\s+\\[OK\\]\\s+\\[OKish\\]\\s+\\[FAIL\\]\\s+elapsed\\s*"));
    assertTrue(lines[2].matches("\\s*alpha\\s+0\\s+0\\s+2\\s+00:00:01\\s*"));
    assertTrue(lines[3].matches("\\s*betaset\\s+1\\s+1\\s+0\\s+01:03:04\\s*"));
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

    Files.write(logsDir.resolve("log.from-prebuilt-indexes.malformed.txt"), List.of(
        "Run for malformed [OK]",
        "Mangled marker [FAILURE] should not count",
        "Duration: 01:02",
        "Another token [OK*] and [OKAY]",
        "No duration value here"));

    String output = runInTempDirectory(logsDir, "--json");

    assertTrue(output.contains("\"run\": \"malformed\""));
    assertTrue(output.contains("\"[OK]\": 1"));
    assertTrue(output.contains("\"[OK*]\": 1"));
    assertTrue(output.contains("\"[FAIL]\": 0"));
    assertTrue(output.contains("\"elapsed\": \"01:02\""));
  }

  @Test
  public void testHistoricalCurrentAndMixedStatusLabels() throws Exception {
    List<List<String>> collections = List.of(
        List.of("[OK*]"), List.of("[OKish]"), List.of("[OK*]", "[OKish]"));
    for (int collection = 0; collection < collections.size(); collection++) {
      Path logsDir = Files.createDirectory(temporaryWorkingDirectory.resolve("logs-" + collection));
      List<String> labels = collections.get(collection);
      for (int i = 0; i < labels.size(); i++) {
        Files.write(logsDir.resolve("log.from-prebuilt-indexes.run-" + i + ".txt"), List.of(
            "Metric " + labels.get(i),
            "Colored metric \u001B[94m" + labels.get(i) + "\u001B[0m",
            "Metric [OK]",
            "Metric [FAIL]",
            "Ignore [OKishness] and [OKAY] and [FAILURE]",
            "Duration: done (00:00:01)"));
      }
      if (labels.size() > 1) {
        Files.write(logsDir.resolve("log.from-prebuilt-indexes.run-2.txt"), List.of(
            "Historical metric [OK*]",
            "Current metric [OKish]",
            "Metric [OK]",
            "Metric [FAIL]",
            "Duration: done (00:00:01)"));
      }
      int runCount = labels.size() > 1 ? 3 : 1;
      JsonNode rows = new ObjectMapper().readTree(runInTempDirectory(logsDir, "--json"));
      assertEquals(runCount, rows.size());
      for (JsonNode row : rows) {
        assertEquals(5, row.size());
        assertEquals(1, row.get("[OK]").asInt());
        assertEquals(2, row.get("[OK*]").asInt());
        assertEquals(1, row.get("[FAIL]").asInt());
        assertFalse(row.has("[OKish]"));
        assertEquals("00:00:01", row.get("elapsed").asText());
      }
      for (String mode : List.of("--md", "--text")) {
        String output = runInTempDirectory(logsDir, mode);
        assertTrue(output.contains("[OKish]"));
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
