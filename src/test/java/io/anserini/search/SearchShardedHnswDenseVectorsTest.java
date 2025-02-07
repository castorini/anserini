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

package io.anserini.search;

import io.anserini.TestUtils;
import io.anserini.index.AbstractIndexer;
import io.anserini.index.IndexHnswDenseVectors;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link SearchShardedHnswDenseVectors}
 */
public class SearchShardedHnswDenseVectorsTest {
  private final ByteArrayOutputStream err = new ByteArrayOutputStream();
  private PrintStream save;

  private void redirectStderr() {
    save = System.err;
    err.reset();
    System.setErr(new PrintStream(err));
  }

  private void restoreStderr() {
    System.setErr(save);
  }

  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(AbstractIndexer.class.getName(), Level.ERROR);
    Configurator.setLevel(IndexHnswDenseVectors.class.getName(), Level.ERROR);
    Configurator.setLevel(SearchShardedHnswDenseVectors.class.getName(), Level.ERROR);
    Configurator.setLevel(HnswDenseSearcher.class.getName(), Level.ERROR);
  }

  @Test
  public void testEmptyInvocation() throws Exception {
    redirectStderr();

    SearchShardedHnswDenseVectors.main(new String[] {});
    assertEquals("Error: option \"-index\" is required\n", err.toString());

    restoreStderr();
  }

  @Test
  public void testAskForHelp() throws Exception {
    redirectStderr();

    SearchShardedHnswDenseVectors.main(new String[] {"-options"});
    assertTrue(err.toString().contains("Options for SearchShardedHnswDenseVectors"));

    restoreStderr();
  }

  @Test
  public void testInvalidIndex() throws Exception {
    redirectStderr();

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", "msmarco-v2.1-doc-segmented.nonexistent.hnsw-int8",
        "-efSearch", "1000",
        "-topics", "rag24.test",
        "-topicReader", "TsvString",
        "-topicField", "title",
        "-encoder", "ArcticEmbedL",
        "-output", runfile,
        "-hits", "250",
        "-threads", "32"
    };
    SearchShardedHnswDenseVectors.main(searchArgs);

    assertEquals("Error: No collection found for identifier: msmarco-v2.1-doc-segmented.nonexistent.hnsw-int8\n",
                err.toString());
    restoreStderr();
  }

  @Test
  public void testInvalidTopics() throws Exception {
    redirectStderr();

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", "msmarco-v2.1-doc-segmented.arctic-embed-l.hnsw-int8",
        "-efSearch", "1000",
        "-topics", "nonexistent.test",
        "-topicReader", "TsvString",
        "-topicField", "title",
        "-encoder", "ArcticEmbedL",
        "-output", runfile,
        "-hits", "250",
        "-threads", "32"
    };
    SearchShardedHnswDenseVectors.main(searchArgs);

    assertEquals("Error: \"nonexistent.test\" does not refer to valid topics.\n", err.toString());
    restoreStderr();
  }

  @Test
  public void testInvalidTopicReader() throws Exception {
    redirectStderr();

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", "msmarco-v2.1-doc-segmented.arctic-embed-l.hnsw-int8",
        "-efSearch", "1000",
        "-topics", "rag24.test",
        "-topicReader", "NonexistentReader",
        "-topicField", "title",
        "-encoder", "ArcticEmbedL",
        "-output", runfile,
        "-hits", "250",
        "-threads", "32"
    };
    SearchShardedHnswDenseVectors.main(searchArgs);

    assertEquals("Error: Unable to load topic reader \"NonexistentReader\".\n", err.toString());
    restoreStderr();
  }

  @Test
  public void testInvalidEncoder() throws Exception {
    redirectStderr();

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", "msmarco-v2.1-doc-segmented.arctic-embed-l.hnsw-int8",
        "-efSearch", "1000",
        "-topics", "rag24.test",
        "-topicReader", "TsvString",
        "-topicField", "title",
        "-encoder", "NonexistentEncoder",
        "-output", runfile,
        "-hits", "250",
        "-threads", "32"
    };
    SearchShardedHnswDenseVectors.main(searchArgs);

    assertEquals("Error: Unable to load Encoder \"NonexistentEncoder\".\n", err.toString());
    restoreStderr();
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void testBasicShardedSearch() throws Exception {
    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", "msmarco-v2.1-doc-segmented.arctic-embed-l.hnsw-int8",
        "-efSearch", "1000",
        "-topics", "rag24.test",
        "-topicReader", "TsvString",
        "-topicField", "title",
        "-encoder", "ArcticEmbedL",
        "-output", runfile,
        "-hits", "250",
        "-threads", "32"
    };

    SearchShardedHnswDenseVectors.main(searchArgs);

    // TODO: Update with actual expected results
    TestUtils.checkRunFileApproximate(runfile, new String[] {
        // TODO: Some run data:
    });

    new File(runfile).delete();
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void testShardedSearchWithRemoveQuery() throws Exception {
    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", "msmarco-v2.1-doc-segmented.arctic-embed-l.hnsw-int8",
        "-efSearch", "1000",
        "-topics", "rag24.test",
        "-topicReader", "TsvString",
        "-topicField", "title",
        "-encoder", "ArcticEmbedL",
        "-output", runfile,
        "-hits", "250",
        "-threads", "32",
        "-removeQuery"
    };

    SearchShardedHnswDenseVectors.main(searchArgs);

    // TODO: Update with actual expected results
    TestUtils.checkRunFileApproximate(runfile, new String[] {
        // TODO: Some run data:
    });

    new File(runfile).delete();
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void testShardedSearchWithMaxPassage() throws Exception {
    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", "msmarco-v2.1-doc-segmented.arctic-embed-l.hnsw-int8",
        "-efSearch", "1000",
        "-topics", "rag24.test",
        "-topicReader", "TsvString",
        "-topicField", "title",
        "-encoder", "ArcticEmbedL",
        "-output", runfile,
        "-hits", "250",
        "-threads", "32",
        "-selectMaxPassage",
        "-selectMaxPassage.hits", "5"
    };

    SearchShardedHnswDenseVectors.main(searchArgs);

    // TODO: Update with actual expected results
    TestUtils.checkRunFileApproximate(runfile, new String[] {
        // TODO: Some run data:
    });

    new File(runfile).delete();
  }
}