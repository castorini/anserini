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

package io.anserini.index;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.lucene.index.IndexReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link IndexHnswDenseVectors}
 */
public class IndexHnswDenseVectorsTest {
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
  }

  @Test
  public void testEmptyInvocation() throws Exception {
    redirectStderr();
    String[] indexArgs = new String[] {};

    IndexHnswDenseVectors.main(indexArgs);
    assertTrue(err.toString().contains("Error"));
    assertTrue(err.toString().contains("is required"));

    restoreStderr();
  }

  @Test
  public void testAskForHelp() throws Exception {
    redirectStderr();

    IndexHnswDenseVectors.main(new String[] {"-options"});
    assertTrue(err.toString().contains("Options for"));

    restoreStderr();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCollection() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "FakeJsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", "target/idx-sample-hnsw" + System.currentTimeMillis(),
        "-generator", "HnswDenseVectorDocumentGenerator",
        "-threads", "1",
        "-M", "16", "-efC", "100"
    };

    IndexHnswDenseVectors.main(indexArgs);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCollectionPath() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector_fake_path",
        "-index", "target/idx-sample-hnsw" + System.currentTimeMillis(),
        "-generator", "HnswDenseVectorDocumentGenerator",
        "-threads", "1",
        "-M", "16", "-efC", "100"
    };

    IndexHnswDenseVectors.main(indexArgs);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidGenerator() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", "target/idx-sample-hnsw" + System.currentTimeMillis(),
        "-generator", "FakeDenseVectorDocumentGenerator",
        "-threads", "1",
        "-M", "16", "-efC", "100"
    };

    IndexHnswDenseVectors.main(indexArgs);
  }

  @Test
  public void testDefaultGenerator() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", "target/idx-sample-hnsw" + System.currentTimeMillis(),
        "-threads", "1",
        "-M", "16", "-efC", "100"
    };

    IndexHnswDenseVectors.main(indexArgs);
    // If this succeeded, then the default -generator of DenseVectorDocumentGenerator must have worked.
  }

  @Test
  public void test1() throws Exception {
    String indexPath = "target/idx-sample-hnsw" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1",
        "-M", "16", "-efC", "100"
    };

    IndexHnswDenseVectors.main(indexArgs);

    IndexReader reader = IndexReaderUtils.getReader(indexPath);
    assertNotNull(reader);

    Map<String, Object> results = IndexReaderUtils.getIndexStats(reader, Constants.VECTOR);
    assertNotNull(results);
    assertEquals(100, results.get("documents"));
  }

  @Test
  public void testQuantizedInt8() throws Exception {
    String indexPath = "target/idx-sample-hnsw" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1",
        "-M", "16", "-efC", "100", "-quantize.int8"
    };

    IndexHnswDenseVectors.main(indexArgs);

    IndexReader reader = IndexReaderUtils.getReader(indexPath);
    assertNotNull(reader);

    Map<String, Object> results = IndexReaderUtils.getIndexStats(reader, Constants.VECTOR);
    assertNotNull(results);
    assertEquals(100, results.get("documents"));
  }
}