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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SummarizeLogsFromCorpusTest {
  private Path temporaryWorkingDirectory;

  @Before
  public void setUp() throws Exception {
    temporaryWorkingDirectory = Files.createTempDirectory("summarize-logs-from-corpus");
  }

  @After
  public void tearDown() throws Exception {
    if (temporaryWorkingDirectory != null) {
      FileUtils.deleteDirectory(new File(temporaryWorkingDirectory.toString()));
    }
  }

  @Test
  public void testSummarizeLogsFromCorpus() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    writeLog(logsDir.resolve("log.corpus.1"), List.of(
        "2026-03-01 10:00:00,100 Starting RunReproductionFromCorpus for topic 1",
        "2026-03-01 10:00:01,200 RunReproductionFromCorpus" + Constants.OK + " completed topic 1"));

    writeLog(logsDir.resolve("log.corpus.2"), List.of(
        "2026-03-01 10:00:02,300 Starting RunReproductionFromCorpus for topic 2",
        "2026-03-01 10:00:04,500 RunReproductionFromCorpus" + Constants.FAIL + " completed topic 2"));

    String output = runInTempDirectory();
    assertTrue(output.contains("Total regressions:   2"));
    assertEquals(1, countForStatusLine(output, Constants.OK));
    assertEquals(0, countForStatusLine(output, Constants.OKISH));
    assertEquals(1, countForStatusLine(output, Constants.FAIL));

    assertTrue(output.contains("Start time: 2026-03-01 10:00:00,100"));
    assertTrue(output.contains("End time:   2026-03-01 10:00:04,500"));
    assertTrue(Pattern.compile("Duration: .*~0\\.0h").matcher(output).find());
  }

  @Test
  public void testSummarizeLogsFromCorpusMissingStatusAndTimestamp() throws Exception {
    Path logsDir = temporaryWorkingDirectory.resolve("logs");
    Files.createDirectory(logsDir);

    writeLog(logsDir.resolve("log.corpus.empty"), List.of(
        "This is not a reproduction line",
        "another non-matching line"));

    writeLog(logsDir.resolve("log.corpus.partial"), List.of(
        "2026-03-01 10:00:00,100 No timestamp format expected",
        "RunReproductionFromCorpus without timestamp"));

    String output = runInTempDirectory();

    assertTrue(output.contains("Total regressions:   2"));
    assertEquals(0, countForStatusLine(output, Constants.OK));
    assertEquals(0, countForStatusLine(output, Constants.OKISH));
    assertEquals(0, countForStatusLine(output, Constants.FAIL));

    assertTrue(output.contains("Start time: "));
    assertTrue(output.contains("End time:   "));
    assertTrue(output.contains("Duration: n/a"));
  }

  private String runInTempDirectory() throws Exception {
    Path javaExecutable = Paths.get(System.getProperty("java.home"), "bin", "java");
    Path classesDir = Paths.get("target/classes");
    Path testClassesDir = Paths.get("target/test-classes");
    LinkedHashSet<String> classPathEntries = new LinkedHashSet<>();
    classPathEntries.add(classesDir.toString());
    classPathEntries.add(testClassesDir.toString());

    String existingClassPath = System.getProperty("java.class.path");
    if (existingClassPath != null && !existingClassPath.isBlank()) {
      for (String element : existingClassPath.split(Pattern.quote(System.getProperty("path.separator")))) {
        if (!element.isBlank()) {
          classPathEntries.add(element);
        }
      }
    }

    ArrayList<String> command = new ArrayList<>();
    command.add(javaExecutable.toString());
    command.add("-cp");
    command.add(String.join(System.getProperty("path.separator"), classPathEntries));
    command.add("io.anserini.reproduce.SummarizeLogsFromCorpus");

    Path outputLog = temporaryWorkingDirectory.resolve("summary.log");
    ProcessBuilder builder = new ProcessBuilder(command);
    builder.directory(new File(temporaryWorkingDirectory.toString()));
    builder.redirectErrorStream(true);
    builder.redirectOutput(outputLog.toFile());

    Process process = builder.start();
    assertTrue("Summarize command timed out", process.waitFor(1, TimeUnit.MINUTES));
    assertEquals(0, process.exitValue());

    return Files.readString(outputLog);
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
