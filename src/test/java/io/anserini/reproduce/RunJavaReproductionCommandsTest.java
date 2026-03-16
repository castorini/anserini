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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;

public class RunJavaReproductionCommandsTest extends StdOutStdErrRedirectableLuceneTestCase {
  @BeforeClass
  public static void setupClass() {
    suppressJvmLogging();
    Configurator.setLevel(RunJavaReproductionCommands.class.getName(), Level.OFF);
  }

  @Before
  public void setUp() throws Exception {
    redirectStdOut();
    redirectStdErr();
    super.setUp();
  }

  @After
  public void tearDown() throws Exception {
    restoreStdOut();
    restoreStdErr();
    super.tearDown();
  }

  @Test
  public void testInvalidOptionPrintsUsageMessage() throws Exception {
    RunJavaReproductionCommands.main(new String[] {"--does-not-exist"});

    String output = err.toString(StandardCharsets.UTF_8);
    assertFalse(output.isEmpty());
    assertTrue(output, output.contains("--config"));
  }

  @Test
  public void testMissingConfigPrintsUsageMessage() throws Exception {
    RunJavaReproductionCommands.main(new String[] {"--sleep", "1"});

    String output = err.toString(StandardCharsets.UTF_8);
    assertFalse(output.isEmpty());
    assertTrue(output, output.contains("--config"));
  }

  @Test
  public void testInvalidConfigPrintsUsageMessage() throws Exception {
    RunJavaReproductionCommands.main(new String[] {"--config"});

    String output = err.toString(StandardCharsets.UTF_8);
    assertFalse(output.isEmpty());
    assertTrue(output, output.contains("--config"));
  }

  @Test
  public void testNegativeSleepValueThrowsException() {
    IllegalArgumentException e = expectThrows(IllegalArgumentException.class, () ->
        RunJavaReproductionCommands.main(new String[] {
            "--config", "ignored-config",
            "--sleep", "-1"
        }));
    assertEquals("--sleep must be non-negative.", e.getMessage());
  }

  @Test
  public void testDryRunFromCorpusBatch01PrintsAllJavaCommands() throws Exception {
    int expectedCommandCount;
    try (InputStream in = ReproductionUtils.loadResourceStream(
        "reproduce/from-document-collection/commands/from-document-collection.batch01.txt", RunJavaReproductionCommands.class);
         BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
      expectedCommandCount = (int) reader.lines()
          .map(String::trim)
          .filter(line -> !line.isEmpty() && !line.startsWith("#"))
          .count();
    }

    Method loadCommands = RunJavaReproductionCommands.class.getDeclaredMethod("loadCommands", String.class, String.class, String.class);
    loadCommands.setAccessible(true);
    @SuppressWarnings("unchecked")
    int actualCommandCount = ((List<String>) loadCommands.invoke(null, "from-document-collection.batch01", "logs", "runs")).size();

    assertEquals(expectedCommandCount, actualCommandCount);
  }

  @Test
  public void testRunDummyCommandAndCheckOutput() throws Exception {
    Path targetDir = Paths.get("target");
    Files.createDirectories(targetDir);

    Path commandFile = Files.createTempFile(targetDir, "run-reproduction-commands-", ".txt");
    String configName = "run-reproduction-commands-test";
    Path logFile = Paths.get("logs", "log.from-document-collection." + configName + ".txt");

    Files.deleteIfExists(logFile);

    Files.writeString(commandFile, "does.not.Exist --config " + configName + "\n", StandardCharsets.UTF_8);

    RunJavaReproductionCommands.main(new String[] {
        "--config", commandFile.toString(),
        "--sleep", "1"
    });

    assertTrue("Missing log file: " + logFile, Files.exists(logFile));

    String output = Files.readString(logFile, StandardCharsets.UTF_8).trim();
    assertFalse("Expected non-empty output in " + logFile, output.isEmpty());
    assertTrue("Expected class name in output, got: " + output, output.contains("does.not.Exist"));

    Files.deleteIfExists(commandFile);
    Files.deleteIfExists(logFile);
  }

  @Test
  public void testListOptionPrintsUsageMessage() throws Exception {
    RunJavaReproductionCommands.main(new String[] {"--list"});

    String stdOut = out.toString(StandardCharsets.UTF_8).trim();
    String stdErr = err.toString(StandardCharsets.UTF_8);
    assertFalse(stdOut.isEmpty());
    assertTrue(stdOut, stdOut.startsWith("["));
    assertTrue(stdOut, stdOut.endsWith("]"));
    assertTrue(stdErr, stdErr.isEmpty());
  }

  @Test
  public void testRunsDirectoryInjectionForPrebuiltIndexCommands() throws Exception {
    Path targetDir = Paths.get("target");
    Files.createDirectories(targetDir);

    Path commandFile = Files.createTempFile(targetDir, "run-reproduction-prebuilt-commands", ".txt");
    Files.writeString(commandFile, "does.not.Exist --config injected-runs-dir-test\n", StandardCharsets.UTF_8);

    String runsDirectory = "custom-runs-directory";
    Method loadCommands = RunJavaReproductionCommands.class.getDeclaredMethod("loadCommands", String.class, String.class, String.class);
    loadCommands.setAccessible(true);
    @SuppressWarnings("unchecked")
    List<String> commands = (List<String>) loadCommands.invoke(null, commandFile.toString(), "logs", runsDirectory);

    assertEquals(1, commands.size());
    assertTrue("Expected runs-directory injection, got: " + commands.get(0), commands.get(0).contains("--runs-directory " + runsDirectory));
    Files.deleteIfExists(commandFile);
  }
}
