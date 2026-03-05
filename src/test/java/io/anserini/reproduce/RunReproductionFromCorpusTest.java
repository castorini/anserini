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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.SortedMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;
import io.anserini.eval.TrecEval;
import io.anserini.index.AbstractIndexer;
import io.anserini.index.IndexCollection;
import io.anserini.search.SearchCollection;
import io.anserini.search.topicreader.TopicReader;
import io.anserini.search.topicreader.Topics;

public class RunReproductionFromCorpusTest extends StdOutStdErrRedirectableLuceneTestCase {
  private static final String[] CACM_IN_REPO_CORPUS_EXPECTED_RUNS = {
    "runs/run.inverted.cacm.cacm.bm25",
    "runs/run.inverted.cacm.cacm.bm25+rm3",
    "runs/run.inverted.cacm.cacm.bm25+ax",
    "runs/run.inverted.cacm.cacm.ql",
    "runs/run.inverted.cacm.cacm.ql+rm3",
    "runs/run.inverted.cacm.cacm.ql+ax"
  };

  private static final String[] CACM_CORPUS_DOWNLOAD_EXPECTED_RUNS = {
    "runs/run.inverted.cacm.download.cacm.bm25",
    "runs/run.inverted.cacm.download.cacm.bm25+rm3",
    "runs/run.inverted.cacm.download.cacm.bm25+ax",
    "runs/run.inverted.cacm.download.cacm.ql",
    "runs/run.inverted.cacm.download.cacm.ql+rm3",
    "runs/run.inverted.cacm.download.cacm.ql+ax"
  };

  private static final String CACM_QRELS_PATH = "src/test/resources/sample_qrels/cacm/qrels.cacm.txt";

  @BeforeClass
  public static void setupClass() {
    suppressJvmLogging();

    Configurator.setLevel(RunReproductionFromCorpus.class.getName(), Level.ERROR);
    Configurator.setLevel(AbstractIndexer.class.getName(), Level.ERROR);
    Configurator.setLevel(IndexCollection.class.getName(), Level.ERROR);
    Configurator.setLevel(SearchCollection.class.getName(), Level.ERROR);
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
  public void testCacmRegressionDryRun() throws Exception {
    SortedMap<Integer, Map<String, String>> topics = TopicReader.getTopics(Topics.CACM);
    assertNotNull(topics);

    RunReproductionFromCorpus.main(new String[] {
        "--config", "cacm",
        "--index",
        "--search",
        "--dry-run"
    });
  }

  @Test
  public void testCacmRegressionFromCorpus() throws Exception {
    SortedMap<Integer, Map<String, String>> topics = TopicReader.getTopics(Topics.CACM);
    assertNotNull(topics);

    RunReproductionFromCorpus.main(new String[] {
        "--config", "cacm",
        "--index",
        "--search",
    });

    assertRunsExistAndNonEmpty(CACM_IN_REPO_CORPUS_EXPECTED_RUNS);
    assertTrecEvalP30(CACM_QRELS_PATH, "runs/run.inverted.cacm.cacm.bm25", "0.1942");

    deleteRunsIfExists(CACM_IN_REPO_CORPUS_EXPECTED_RUNS);
    deleteDirectoryIfExists(Paths.get("indexes/lucene-inverted.cacm/"));
  }

  @Test
  public void testCacmRegressionFromCorpusDownload() throws Exception {
    SortedMap<Integer, Map<String, String>> topics = TopicReader.getTopics(Topics.CACM);
    assertNotNull(topics);

    RunReproductionFromCorpus.main(new String[] {
        "--config", "cacm-download",
        "--index",
        "--search",
        "--download"
    });

    assertRunsExistAndNonEmpty(CACM_CORPUS_DOWNLOAD_EXPECTED_RUNS);
    assertTrecEvalP30(CACM_QRELS_PATH, "runs/run.inverted.cacm.download.cacm.bm25", "0.1942");

    deleteRunsIfExists(CACM_CORPUS_DOWNLOAD_EXPECTED_RUNS);
    deleteDirectoryIfExists(Paths.get("indexes/lucene-inverted.cacm.download/"));
    deleteDirectoryIfExists(Paths.get("collections/cacm/"));

    Files.deleteIfExists(Paths.get("collections/cacm-in-folder.tar.gz"));
  }

  @Test
  public void testCacmRegressionFromCorpusFatjar() throws Exception {
    SortedMap<Integer, Map<String, String>> topics = TopicReader.getTopics(Topics.CACM);
    assertNotNull(topics);

    Path classesDir = Paths.get("target/classes");
    Path testClassesDir = Paths.get("target/test-classes");
    Set<String> classPathEntries = new LinkedHashSet<>();
    classPathEntries.add(classesDir.toString());
    classPathEntries.add(testClassesDir.toString());

    String existingClassPath = System.getProperty("java.class.path");
    if (existingClassPath != null && !existingClassPath.isBlank()) {
      for (String element : existingClassPath.split(java.util.regex.Pattern.quote(System.getProperty("path.separator")))) {
        if (!element.isBlank()) {
          classPathEntries.add(element);
        }
      }
    }
    String classPath = String.join(System.getProperty("path.separator"), classPathEntries);
    assertFalse(classPath.isBlank());

    ArrayList<String> command = new ArrayList<>();
    command.add(Paths.get(System.getProperty("java.home"), "bin", "java").toString());
    command.add("-cp");
    command.add(classPath);
    command.add("io.anserini.reproduce.RunReproductionFromCorpus");
    command.add("--index");
    command.add("--verify");
    command.add("--search");
    command.add("--config");
    command.add("cacm");

    ProcessBuilder builder = new ProcessBuilder(command);
    builder.redirectErrorStream(true);
    Path processLog = Paths.get("target", "run-cacm-fatjar.log");
    Files.createDirectories(processLog.getParent());
    builder.redirectOutput(processLog.toFile());

    Process process = builder.start();
    assertTrue("Reproduction command timed out", process.waitFor(10, TimeUnit.MINUTES));

    int exitCode = process.exitValue();
    assertEquals(0, exitCode);

    assertRunsExistAndNonEmpty(CACM_IN_REPO_CORPUS_EXPECTED_RUNS);
    deleteRunsIfExists(CACM_IN_REPO_CORPUS_EXPECTED_RUNS);
    deleteDirectoryIfExists(Paths.get("indexes/lucene-inverted.cacm/"));
  }

  private void assertRunsExistAndNonEmpty(String[] runs) throws Exception {
    for (String run : runs) {
      Path path = Paths.get(run);
      assertTrue("Missing run file: " + run, Files.exists(path));
      assertTrue("Empty run file: " + run, Files.size(path) > 0);
    }
  }

  private void assertTrecEvalP30(String qrelsPath, String runFile, String expectedP30) throws Exception {
    TrecEval trecEval = new TrecEval();
    String[] args = new String[] {
        "-m", "P.30",
        qrelsPath,
        runFile
    };
    String[][] output = trecEval.runAndGetOutput(args);

    assertNotNull(output);
    assertEquals(1, output.length);
    assertEquals("P_30", output[0][0]);
    assertEquals("all", output[0][1]);
    assertEquals(expectedP30, output[0][2]);
    assertEquals(0, trecEval.getLastExitCode());
  }

  private void deleteRunsIfExists(String[] runs) throws Exception {
    for (String run : runs) {
      Path path = Paths.get(run);
      Files.deleteIfExists(path);
    }
  }

  private void deleteDirectoryIfExists(Path directory) throws Exception {
    if (Files.exists(directory)) {
      FileUtils.deleteDirectory(new File(directory.toString()));
    }
  }
}
