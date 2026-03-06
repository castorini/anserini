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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

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

  private String runInTempDirectory(String... args) throws Exception {
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
    command.add("io.anserini.reproduce.SummarizeLogsFromPrebuiltIndexes");
    for (String arg : args) {
      command.add(arg);
    }
    command.add("--logs");
    command.add("logs");

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
}
