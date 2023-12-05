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
 * Tests for {@link SearchHnswDenseVectors}
 */
public class SearchHnswDenseVectorsTest {
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
    Configurator.setLevel(IndexHnswDenseVectors.class.getName(), Level.ERROR);
    Configurator.setLevel(SearchHnswDenseVectors.class.getName(), Level.ERROR);
  }

  @Test
  public void testEmptyInvocation() throws Exception {
    redirectStderr();

    SearchHnswDenseVectors.main(new String[] {});
    assertTrue(err.toString().contains("Error"));
    assertTrue(err.toString().contains("is required"));

    restoreStderr();
  }

  @Test
  public void testAskForHelp() throws Exception {
    redirectStderr();

    SearchHnswDenseVectors.main(new String[] {"-options"});
    assertTrue(err.toString().contains("Options for"));

    restoreStderr();
  }

  @Test
  public void testInvalidIndex1() throws Exception {
    redirectStderr();

    // Fake path that doesn't exist.
    String[] searchArgs = new String[] {
        "-index", "/fake/path",
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", "target/run-" + System.currentTimeMillis(),
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-efSearch", "1000",
        "-hits", "5"};
    SearchHnswDenseVectors.main(searchArgs);

    assertEquals("Error: \"/fake/path\" does not appear to be a valid index.\n", err.toString());
    restoreStderr();
  }

  @Test
  public void testInvalidIndex2() throws Exception {
    redirectStderr();

    // Path that does exist, but isn't an index.
    String[] searchArgs = new String[] {
        "-index", "/fake/path",
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", "target/run-" + System.currentTimeMillis(),
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-efSearch", "1000",
        "-hits", "5"};
    SearchHnswDenseVectors.main(searchArgs);

    assertEquals("Error: \"/fake/path\" does not appear to be a valid index.\n", err.toString());
    restoreStderr();
  }

  @Test
  public void searchInvalidTopics() throws Exception {
    String indexPath = "target/idx-sample-hnsw" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "HnswDenseVectorDocumentGenerator",
        "-threads", "1",
        "-M", "16", "-efC", "100"
    };

    IndexHnswDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "fake/topics/here",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-efSearch", "1000",
        "-hits", "5"};

    redirectStderr();
    SearchHnswDenseVectors.main(searchArgs);

    assertEquals("Error: \"fake/topics/here\" does not appear to be a valid topics file.\n", err.toString());
    restoreStderr();
  }

  @Test
  public void searchInvalidReader() throws Exception {
    String indexPath = "target/idx-sample-hnsw" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "HnswDenseVectorDocumentGenerator",
        "-threads", "1",
        "-M", "16", "-efC", "100"
    };

    IndexHnswDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-topicReader", "FakeJsonIntVector",
        "-topicField", "vector",
        "-efSearch", "1000",
        "-hits", "5"};

    redirectStderr();
    SearchHnswDenseVectors.main(searchArgs);

    assertEquals("Error: Unable to load topic reader \"FakeJsonIntVector\".\n", err.toString());
    restoreStderr();
  }

  @Test
  public void searchInvalidTopicField() throws Exception {
    String indexPath = "target/idx-sample-hnsw" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "HnswDenseVectorDocumentGenerator",
        "-threads", "1",
        "-M", "16", "-efC", "100"
    };

    IndexHnswDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "fake_field",
        "-efSearch", "1000",
        "-hits", "5"};

    redirectStderr();
    SearchHnswDenseVectors.main(searchArgs);

    assertEquals("Error: Unable to read topic field \"fake_field\".\n", err.toString());
    restoreStderr();
  }

  @Test
  public void searchInvalidGenerator() throws Exception {
    String indexPath = "target/idx-sample-hnsw" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "HnswDenseVectorDocumentGenerator",
        "-threads", "1",
        "-M", "16", "-efC", "100"
    };

    IndexHnswDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", runfile,
        "-generator", "FakeVectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-efSearch", "1000",
        "-hits", "5"};

    redirectStderr();
    SearchHnswDenseVectors.main(searchArgs);

    assertEquals("Error: Unable to load QueryGenerator \"FakeVectorQueryGenerator\".\n", err.toString());
    restoreStderr();
  }

  @Test
  public void searchInvalidEncoder() throws Exception {
    String indexPath = "target/idx-sample-hnsw" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "HnswDenseVectorDocumentGenerator",
        "-threads", "1",
        "-M", "16", "-efC", "100"
    };

    IndexHnswDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-encoder", "FakeEncoder",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-efSearch", "1000",
        "-hits", "5"};

    redirectStderr();
    SearchHnswDenseVectors.main(searchArgs);

    assertEquals("Error: Unable to load Encoder \"FakeEncoder\".\n", err.toString());
    restoreStderr();
  }

  @Test
  public void test1() throws Exception {
    String indexPath = "target/idx-sample-hnsw" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "HnswDenseVectorDocumentGenerator",
        "-threads", "1",
        "-M", "16", "-efC", "100"
    };

    IndexHnswDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-efSearch", "1000",
        "-hits", "5"};
    SearchHnswDenseVectors.main(searchArgs);

    TestUtils.checkFile(runfile, new String[] {
        "160885 Q0 45 1 0.863064 Anserini",
        "160885 Q0 44 2 0.861596 Anserini",
        "160885 Q0 40 3 0.858651 Anserini",
        "160885 Q0 48 4 0.858514 Anserini",
        "160885 Q0 41 5 0.856265 Anserini",
        "867490 Q0 10 1 0.850331 Anserini",
        "867490 Q0 45 2 0.846281 Anserini",
        "867490 Q0 44 3 0.845236 Anserini",
        "867490 Q0 95 4 0.845013 Anserini",
        "867490 Q0 97 5 0.844905 Anserini"
    });

    new File(runfile).delete();
  }

}