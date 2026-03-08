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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
  public void testSummarizeLogsFromPrebuiltIndexesJson() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    writeLog(logsDir.resolve("log.from-prebuilt-indexes.betaset.txt"), List.of(
        "Run for beta [OK]",
        "Second line [OK*]",
        "Duration: done (01:03:04)"));

    writeLog(logsDir.resolve("log.from-prebuilt-indexes.alpha.txt"), List.of(
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

    writeLog(logsDir.resolve("log.from-prebuilt-indexes.betaset.txt"), List.of(
        "Run for beta [OK]",
        "Second line [OK*]",
        "Duration: done (01:03:04)"));

    writeLog(logsDir.resolve("log.from-prebuilt-indexes.alpha.txt"), List.of(
        "Run for alpha [FAIL]",
        "Failure [FAIL]",
        "Duration: 00:00:01"));

    String output = runInTempDirectory("--md");

    String[] lines = output.strip().split("\\R");
    assertTrue(lines[0].startsWith("| run"));
    assertTrue(lines[1].contains("| -----:"));
    assertTrue(lines[2].matches("\\|\\s*alpha\\s+\\|\\s+0\\s+\\|\\s+0\\s+\\|\\s+2\\s+\\|\\s+00:00:01\\s+\\|"));
    assertTrue(lines[3].matches("\\|\\s*betaset\\s+\\|\\s+1\\s+\\|\\s+1\\s+\\|\\s+0\\s+\\|\\s+01:03:04\\s+\\|"));
    assertTrue(output.indexOf("alpha") < output.indexOf("betaset"));
  }

  @Test
  public void testSummarizeLogsFromPrebuiltIndexesPlainText() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    writeLog(logsDir.resolve("log.from-prebuilt-indexes.betaset.txt"), List.of(
        "Run for beta [OK]",
        "Second line [OK*]",
        "Duration: done (01:03:04)"));

    writeLog(logsDir.resolve("log.from-prebuilt-indexes.alpha.txt"), List.of(
        "Run for alpha [FAIL]",
        "Failure [FAIL]",
        "Duration: 00:00:01"));

    String output = runInTempDirectory("--plain-text");

    String[] lines = output.strip().split("\\R");
    assertTrue(lines.length >= 4);
    assertTrue(!lines[0].contains("|"));
    assertTrue(!lines[1].contains("|"));
    assertTrue(lines[0].matches("\\s*run\\s+\\[OK\\]\\s+\\[OK\\*\\]\\s+\\[FAIL\\]\\s+elapsed\\s*"));
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
    writeLog(logsDir.resolve("unrelated.txt"), List.of("not a prebuilt log"));

    String output = runInTempDirectory(logsDir, "--md");

    assertTrue(output.contains("No prebuilt-index logs found in: " + logsDir + " (pattern: log.from-prebuilt-indexes.*)"));
    assertTrue(!output.contains("| "));
  }

  @Test
  public void testSummarizeLogsFromPrebuiltIndexesMalformedAndUnexpectedContent() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    writeLog(logsDir.resolve("log.from-prebuilt-indexes.malformed.txt"), List.of(
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
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    PrintStream newOut = new PrintStream(stdout);

    PrintStream originalOut = System.out;
    PrintStream originalErr = System.err;

    try {
      System.setOut(newOut);
      System.setErr(newOut);
      String[] mainArgs = new String[args.length + 2];
      for (int i = 0; i < args.length; i++) {
        mainArgs[i] = args[i];
      }
      mainArgs[args.length] = "--logs-directory";
      mainArgs[args.length + 1] = logsDirectory.toString();

      SummarizeLogsFromPrebuiltIndexes.main(mainArgs);
      return stdout.toString(StandardCharsets.UTF_8);
    } finally {
      System.setOut(originalOut);
      System.setErr(originalErr);
      newOut.close();
    }
  }

  private void writeLog(Path path, List<String> lines) throws IOException {
    Files.write(path, lines);
  }
}
