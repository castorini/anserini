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
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;
import io.anserini.eval.TrecEval;
import io.anserini.index.AbstractIndexer;
import io.anserini.index.IndexCollection;
import io.anserini.search.SearchCollection;
import io.anserini.search.topicreader.TopicReader;
import io.anserini.search.topicreader.Topics;
import io.anserini.util.CacheDirectoryResolver;

public class ReproduceFromDocumentCollectionTest extends StdOutStdErrRedirectableLuceneTestCase {
  private static final Path CACM_FATJAR_LOG = Paths.get("target", "run-cacm-fatjar.log");

  private static final String[] CACM_COLLECTION_IN_REPO_EXPECTED_RUNS = {
    "runs/run.inverted.cacm.cacm.bm25.txt",
    "runs/run.inverted.cacm.cacm.bm25+rm3.txt",
    "runs/run.inverted.cacm.cacm.bm25+ax.txt",
    "runs/run.inverted.cacm.cacm.ql.txt",
    "runs/run.inverted.cacm.cacm.ql+rm3.txt",
    "runs/run.inverted.cacm.cacm.ql+ax.txt"
  };

  private static final String[] CACM_COLLECTION_DOWNLOAD_EXPECTED_RUNS = {
    "runs/run.inverted.cacm.download.cacm.bm25.txt",
    "runs/run.inverted.cacm.download.cacm.bm25+rm3.txt",
    "runs/run.inverted.cacm.download.cacm.bm25+ax.txt",
    "runs/run.inverted.cacm.download.cacm.ql.txt",
    "runs/run.inverted.cacm.download.cacm.ql+rm3.txt",
    "runs/run.inverted.cacm.download.cacm.ql+ax.txt"
  };

  private static final String CACM_QRELS_PATH = "src/test/resources/sample_qrels/cacm/qrels.cacm.txt";

  @BeforeClass
  public static void setupClass() {
    suppressJvmLogging();

    Configurator.setLevel(ReproduceFromDocumentCollection.class.getName(), Level.ERROR);
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
    Files.deleteIfExists(CACM_FATJAR_LOG);
    super.tearDown();
  }

  @Test
  public void testInvalidOption() throws Exception {
    ReproduceFromDocumentCollection.main(new String[] {"--invalid"});

    assertTrue(err.toString().startsWith("Error: \"--invalid\" is not a valid option"));
    assertTrue(err.toString().contains("Options for ReproduceFromDocumentCollection:"));
  }

  @Test
  public void testConfigRequiredUnlessListSpecified() throws Exception {
    ReproduceFromDocumentCollection.main(new String[0]);

    assertTrue(err.toString().contains("Error: Option \"--config\" is required unless \"--list\" is specified."));
    assertTrue(err.toString().contains("Options for ReproduceFromDocumentCollection:"));
  }

  @Test
  public void testHelp() throws Exception {
    ReproduceFromDocumentCollection.main(new String[] {"--help"});

    String usage = err.toString();
    assertTrue(usage.contains("Options for ReproduceFromDocumentCollection:"));
    assertTrue(usage.indexOf("--list") < usage.indexOf("--config"));
    assertTrue(usage.indexOf("--config") < usage.indexOf("--show"));
    assertTrue(usage.contains("[Workflow Stage] Download corpus."));
    assertTrue(usage.contains("[Workflow Stage] Build index."));
    assertTrue(usage.contains("[Workflow Stage] Verify index statistics."));
    assertTrue(usage.contains("[Workflow Stage] Search and verify results."));
    assertTrue(usage.contains("--help"));
  }

  @Test
  public void testListConfigs() throws Exception {
    ReproduceFromDocumentCollection.main(new String[] {"--list"});

    List<?> outputConfigs = new ObjectMapper().readValue(out.toString(), List.class);
    List<String> expectedConfigs = ReproductionUtils.listYamlConfigs(
        ReproduceFromDocumentCollection.class, "reproduce/from-document-collection/configs");
    assertEquals(expectedConfigs.size(), outputConfigs.size());
  }

  @Test
  public void testShowConfig() throws Exception {
    ReproduceFromDocumentCollection.main(new String[] {"--config", "cacm", "--show"});

    assertTrue(out.toString().startsWith("---"));
    assertTrue(out.toString().contains("corpus: cacm"));
    assertTrue(out.toString().contains("collection_class: HtmlCollection"));
    assertTrue(out.toString().contains("models:"));
  }

  @Test
  public void testWorkflowStageRequired() throws Exception {
    ReproduceFromDocumentCollection.main(new String[] {"--config", "cacm", "--dry-run"});

    assertTrue(err.toString().contains(
        "Error: Select at least one workflow stage: --download, --index, --verify, --search."));
    assertTrue(err.toString().contains("Options for ReproduceFromDocumentCollection:"));
  }

  @Test
  public void testCacmRegressionDryRun() throws Exception {
    SortedMap<Integer, Map<String, String>> topics = TopicReader.getTopics(Topics.CACM);
    assertNotNull(topics);

    ReproduceFromDocumentCollection.main(new String[] {"--config", "cacm", "--index", "--search", "--dry-run"});
  }

  @Test
  public void testCacmRegressionInRepo() throws Exception {
    SortedMap<Integer, Map<String, String>> topics = TopicReader.getTopics(Topics.CACM);
    assertNotNull(topics);

    ReproduceFromDocumentCollection.main(new String[] {"--config", "cacm", "--index", "--search"});

    assertRunsExistAndNonEmpty(CACM_COLLECTION_IN_REPO_EXPECTED_RUNS);
    assertTrecEvalP30(CACM_QRELS_PATH, "runs/run.inverted.cacm.cacm.bm25.txt", "0.1942");

    deleteRunsIfExists(CACM_COLLECTION_IN_REPO_EXPECTED_RUNS);
    deleteDirectoryIfExists(Paths.get("indexes/lucene-inverted.cacm/"));
  }

  @Test
  public void testCacmRegressionFromDownload() throws Exception {
    assumeGithubReachable();

    SortedMap<Integer, Map<String, String>> topics = TopicReader.getTopics(Topics.CACM);
    assertNotNull(topics);

    ReproduceFromDocumentCollection.main(new String[] {"--config", "cacm-download", "--download", "--index", "--search"});

    assertRunsExistAndNonEmpty(CACM_COLLECTION_DOWNLOAD_EXPECTED_RUNS);
    assertTrecEvalP30(CACM_QRELS_PATH, "runs/run.inverted.cacm.download.cacm.bm25.txt", "0.1942");

    deleteRunsIfExists(CACM_COLLECTION_DOWNLOAD_EXPECTED_RUNS);
    deleteDirectoryIfExists(Paths.get("indexes/lucene-inverted.cacm.download/"));
    deleteDirectoryIfExists(Paths.get("collections/cacm/"));

    Files.deleteIfExists(Paths.get("collections/cacm-in-folder.tar.gz"));
  }

  @Test
  public void testResolveCorpusPathPrefersCollectionCache() throws Exception {
    Path cache = createTempDir("collection-cache");
    String cacheProperty = System.getProperty(CacheDirectoryResolver.CACHE_PROPERTY);
    System.setProperty(CacheDirectoryResolver.CACHE_PROPERTY, cache.toString());

    try {
      Path cachedCollection = CacheDirectoryResolver.getCollectionCachePath().resolve("newswire/disk45");
      Files.createDirectories(cachedCollection);

      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      ReproduceFromDocumentCollection.Args args = new ReproduceFromDocumentCollection.Args();
      Method resolveCorpusPath = ReproduceFromDocumentCollection.class.getDeclaredMethod(
          "resolveCorpusPath", com.fasterxml.jackson.databind.JsonNode.class, ReproduceFromDocumentCollection.Args.class);
      resolveCorpusPath.setAccessible(true);

      String resolved = (String) resolveCorpusPath.invoke(null, mapper.readTree("""
          corpus: disk45
          corpus_path: collections/newswire/disk45/
          """), args);

      assertEquals(cachedCollection.toString(), resolved);
    } finally {
      if (cacheProperty == null) {
        System.clearProperty(CacheDirectoryResolver.CACHE_PROPERTY);
      } else {
        System.setProperty(CacheDirectoryResolver.CACHE_PROPERTY, cacheProperty);
      }
    }
  }

  private void assumeGithubReachable() {
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress("github.com", 443), 2000);
    } catch (Exception e) {
      Assume.assumeNoException("Skipping download test because github.com:443 is unreachable.", e);
    }
  }

  @Test
  public void testCacmRegressionFromFatjar() throws Exception {
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
    command.add("io.anserini.reproduce.ReproduceFromDocumentCollection");
    command.add("--index");
    command.add("--verify");
    command.add("--search");
    command.add("--config");
    command.add("cacm");

    ProcessBuilder builder = new ProcessBuilder(command);
    builder.redirectErrorStream(true);
    Files.createDirectories(CACM_FATJAR_LOG.getParent());
    builder.redirectOutput(CACM_FATJAR_LOG.toFile());

    Process process = builder.start();
    assertTrue("Reproduction command timed out", process.waitFor(10, TimeUnit.MINUTES));

    int exitCode = process.exitValue();
    assertEquals(0, exitCode);

    assertRunsExistAndNonEmpty(CACM_COLLECTION_IN_REPO_EXPECTED_RUNS);
    deleteRunsIfExists(CACM_COLLECTION_IN_REPO_EXPECTED_RUNS);
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
