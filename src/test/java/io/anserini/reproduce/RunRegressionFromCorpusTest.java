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
import java.util.Map;
import java.util.SortedMap;

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

public class RunRegressionFromCorpusTest extends StdOutStdErrRedirectableLuceneTestCase {
  @BeforeClass
  public static void setupClass() {
    suppressJvmLogging();

    Configurator.setLevel(RunRegressionFromCorpus.class.getName(), Level.ERROR);
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
  public void testCacmRegressionFromCorpus() throws Exception {
    SortedMap<Integer, Map<String, String>> topics = TopicReader.getTopics(Topics.CACM);
    assertNotNull(topics);

    RunRegressionFromCorpus.main(new String[] {
        "--regression", "cacm",
        "--index",
        "--search",
    });

    String[] expectedRuns = {
        "runs/run.inverted.cacm.cacm.bm25",
        "runs/run.inverted.cacm.cacm.bm25+rm3",
        "runs/run.inverted.cacm.cacm.bm25+ax",
        "runs/run.inverted.cacm.cacm.ql",
        "runs/run.inverted.cacm.cacm.ql+rm3",
        "runs/run.inverted.cacm.cacm.ql+ax"
    };

    for (String run : expectedRuns) {
      Path path = Paths.get(run);
      assertTrue("Missing run file: " + run, Files.exists(path));
      assertTrue("Empty run file: " + run, Files.size(path) > 0);
    }

    TrecEval trecEval = new TrecEval();
    String[] args = new String[] {
        "-m", "P.30",
        "src/test/resources/sample_qrels/cacm/qrels.cacm.txt",
        "runs/run.inverted.cacm.cacm.bm25"
    };
    String[][] output = trecEval.runAndGetOutput(args);

    assertNotNull(output);
    assertEquals(1, output.length);
    assertEquals("P_30", output[0][0]);
    assertEquals("all", output[0][1]);
    assertEquals("0.1942", output[0][2]);
    assertEquals(0, trecEval.getLastExitCode());

    for (String run : expectedRuns) {
      Path path = Paths.get(run);
      Files.deleteIfExists(path);
    }

    Path indexPath = Paths.get("indexes/lucene-inverted.cacm/");
    if (Files.exists(indexPath)) {
      FileUtils.deleteDirectory(new File(indexPath.toString()));
    }
  }

  @Test
  public void testCacmRegressionFromCorpusDownload() throws Exception {
    SortedMap<Integer, Map<String, String>> topics = TopicReader.getTopics(Topics.CACM);
    assertNotNull(topics);

    RunRegressionFromCorpus.main(new String[] {
        "--regression", "cacm-download",
        "--index",
        "--search",
        "--download"
    });

    String[] expectedRuns = {
        "runs/run.inverted.cacm.download.cacm.bm25",
        "runs/run.inverted.cacm.download.cacm.bm25+rm3",
        "runs/run.inverted.cacm.download.cacm.bm25+ax",
        "runs/run.inverted.cacm.download.cacm.ql",
        "runs/run.inverted.cacm.download.cacm.ql+rm3",
        "runs/run.inverted.cacm.download.cacm.ql+ax"
    };

    for (String run : expectedRuns) {
      Path path = Paths.get(run);
      assertTrue("Missing run file: " + run, Files.exists(path));
      assertTrue("Empty run file: " + run, Files.size(path) > 0);
    }

    TrecEval trecEval = new TrecEval();
    String[] args = new String[] {
        "-m", "P.30",
        "src/test/resources/sample_qrels/cacm/qrels.cacm.txt",
        "runs/run.inverted.cacm.download.cacm.bm25"
    };
    String[][] output = trecEval.runAndGetOutput(args);

    assertNotNull(output);
    assertEquals(1, output.length);
    assertEquals("P_30", output[0][0]);
    assertEquals("all", output[0][1]);
    assertEquals("0.1942", output[0][2]);
    assertEquals(0, trecEval.getLastExitCode());

    for (String run : expectedRuns) {
      Path path = Paths.get(run);
      Files.deleteIfExists(path);
    }

    Path indexPath = Paths.get("indexes/lucene-inverted.cacm.download/");
    if (Files.exists(indexPath)) {
      FileUtils.deleteDirectory(new File(indexPath.toString()));
    }
  }
}
