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

package io.anserini.cli;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;

public class ExtractQueriesAndDocumentsFromTrecRunTest extends StdOutStdErrRedirectableLuceneTestCase {

  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(ExtractQueriesAndDocumentsFromTrecRun.class.getName(), Level.ERROR);
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
  public void testHelpOption() throws Exception {
    ExtractQueriesAndDocumentsFromTrecRun.main(new String[] {"--help"});
    assertTrue(err.toString().contains("Options for ExtractQueriesAndDocumentsFromTrecRun:"));
    assertTrue(err.toString().contains("--help"));
    assertTrue(!err.toString().contains("Option \"--index\" is required"));
  }

  @Test
  public void testPrebuiltIndex() throws Exception {
    String[] args = new String[] {
        "--index", "cacm",
        "--run", "src/test/resources/sample_runs/cacm/cacm-bm25.txt",
        "--topics", "cacm",
        "--output", "test_reranker_requests.jsonl"
    };

    ExtractQueriesAndDocumentsFromTrecRun.main(args);
    assertTrue(!err.toString().contains("Error: "));
    assertTrue(new File("test_reranker_requests.jsonl").isFile());
    assertTrue(new File("test_reranker_requests.jsonl").delete());
  }

  @Test
  public void testLocalIndex() throws Exception {
    String[] args = new String[] {
        "--index", "src/test/resources/prebuilt_indexes/raw-beir-collection1-index",
        "--run", "src/test/resources/sample_runs/run5",
        "--topics", "src/test/resources/sample_topics/acl_topics.tsv",
        "--output", "test_reranker_requests.jsonl"
    };

    ExtractQueriesAndDocumentsFromTrecRun.main(args);
    assertTrue(!err.toString().contains("Error: "));
    assertTrue(new File("test_reranker_requests.jsonl").isFile());
    assertTrue(new File("test_reranker_requests.jsonl").delete());
  }

  @Test
  public void testLocalTopics() throws Exception {
    String[] args = new String[] {
        "--index", "src/test/resources/prebuilt_indexes/raw-beir-collection1-index",
        "--run", "src/test/resources/sample_runs/run5",
        "--topics", "src/test/resources/sample_topics/acl_topics.tsv",
        "--output", "test_reranker_requests.jsonl"
    };

    ExtractQueriesAndDocumentsFromTrecRun.main(args);
    assertTrue(!err.toString().contains("Error: "));
    assertTrue(new File("test_reranker_requests.jsonl").isFile());
    assertTrue(new File("test_reranker_requests.jsonl").delete());
  }

  @Test
  public void testBadIndex() throws Exception {
    String[] args = new String[] {
        "--index", "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_trec_collection2/",
        "--run", "src/test/resources/sample_runs/run4",
        "--topics", "src/test/resources/sample_topics/acl_topics.tsv",
        "--output", "test_reranker_requests.jsonl"
    };

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ExtractQueriesAndDocumentsFromTrecRun.main(args));
    assertTrue(exception.getMessage().contains("Raw document with docid "));
    assertTrue(exception.getMessage().contains("not found in index."));
    assertTrue(new File("test_reranker_requests.jsonl").delete());
  }

  @Test
  public void testBadTopics() throws Exception {
    String[] args = new String[] {
        "--index", "src/test/resources/prebuilt_indexes/raw-beir-collection1-index",
        "--run", "src/test/resources/sample_runs/run1",
        "--topics", "src/test/resources/sample_topics/acl_topics.tsv",
        "--output", "test_reranker_requests.jsonl"
    };

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ExtractQueriesAndDocumentsFromTrecRun.main(args));
    assertTrue(exception.getMessage().contains("Unable to find query for query1"));
    assertTrue(new File("test_reranker_requests.jsonl").delete());
  }

  @Test
  public void testGenerate() throws Exception {
    String[] args = new String[] {
        "--index", "src/test/resources/prebuilt_indexes/raw-beir-collection1-index",
        "--run", "src/test/resources/sample_runs/run5",
        "--topics", "src/test/resources/sample_topics/acl_topics.tsv",
        "--output", "test_reranker_requests.jsonl"
    };

    ExtractQueriesAndDocumentsFromTrecRun.main(args);
    assertTrue(!err.toString().contains("Error: "));

    String output = Files.readString(Paths.get("test_reranker_requests.jsonl"));
    assertTrue(output.contains("\"qid\":\"1\""));
    assertTrue(output.contains("\"text\":\"model\""));
    assertTrue(output.contains("\"docid\":\"doc1\""));
    assertTrue(output.contains("\"title\":\"doc1 title\""));
    assertTrue(output.contains("\"text\":\"doc1 text\""));
    assertTrue(output.contains("\"docid\":\"doc3\""));
    assertTrue(output.contains("\"title\":\"doc3 title\""));
    assertTrue(output.contains("\"text\":\"doc3 text\""));
    assertTrue(new File("test_reranker_requests.jsonl").delete());
  }

  @Test
  public void testGenerateWithNonJsonRawDocuments() throws Exception {
    String[] args = new String[] {
        "--index", "cacm",
        "--run", "src/test/resources/sample_runs/cacm/cacm-bm25.txt",
        "--topics", "cacm",
        "--output", "test_reranker_requests.jsonl"
    };

    ExtractQueriesAndDocumentsFromTrecRun.main(args);
    assertTrue(!err.toString().contains("Error: "));

    String output = Files.readString(Paths.get("test_reranker_requests.jsonl"));
    assertTrue(output.contains("\"docid\":\"CACM-1938\""));
    assertTrue(output.contains("\"doc\":\"<html>"));
    assertTrue(output.contains("Time-Sharing System Performance"));

    assertTrue(new File("test_reranker_requests.jsonl").delete());
  }

  @Test
  public void testAmbiguousIndexLabelAndLocalPath() throws Exception {
    // Create a local directory that matches a known prebuilt label to force ambiguity.
    final String prebuiltLabel = "msmarco-v1-passage";
    File localDir = new File(prebuiltLabel);
    if (localDir.exists()) {
      // If it already exists, fail to avoid interfering with other tests.
      throw new IllegalStateException("Unexpected pre-existing directory: " + localDir.getAbsolutePath());
    }

    try {
      if (!localDir.mkdir()) {
        throw new IllegalStateException("Failed to create test directory: " + localDir.getAbsolutePath());
      }

      String[] args = new String[] {
          "--index", prebuiltLabel,
          "--run", "src/test/resources/sample_runs/run4",
          "--topics", "cacm",
          "--output", "test_reranker_requests.jsonl"
      };

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> ExtractQueriesAndDocumentsFromTrecRun.main(args));
      assertTrue(exception.getMessage().contains("Ambiguous index reference"));
    } finally {
      if (localDir.exists()) {
        assertTrue(localDir.delete());
      }
      // Ensure we don't leave the output file around if it was accidentally created
      new File("test_reranker_requests.jsonl").delete();
    }
  }

  @Test
  public void testClassCannotBeInstantiated() throws Exception {
    Constructor<ExtractQueriesAndDocumentsFromTrecRun> constructor =
        ExtractQueriesAndDocumentsFromTrecRun.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
    assertTrue(exception.getCause() instanceof UnsupportedOperationException);
  }
}
