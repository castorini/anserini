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

import io.anserini.CustomAppender;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.lucene.index.IndexReader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link IndexInvertedDenseVectors}
 */
public class IndexInvertedDenseVectorsTest {
  private static final Logger LOGGER = LogManager.getLogger(IndexInvertedDenseVectors.class);
  private static CustomAppender APPENDER;

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
    APPENDER = new CustomAppender("CustomAppender");
    APPENDER.start();

    ((org.apache.logging.log4j.core.Logger) LOGGER).addAppender(APPENDER);

    Configurator.setLevel(IndexInvertedDenseVectors.class.getName(), Level.INFO);
  }

  @Test
  public void testEmptyInvocation() throws Exception {
    redirectStderr();
    String[] indexArgs = new String[] {};

    err.reset();
    IndexInvertedDenseVectors.main(indexArgs);
    assertTrue(err.toString().contains("Example: IndexInvertedDenseVectors"));

    restoreStderr();
  }

  @Test(expected = ClassNotFoundException.class)
  public void testInvalidCollection() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "FakeCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-generator", "InvertedDenseVectorDocumentGenerator",
        "-index", "target/idx-sample-ll-vector" + System.currentTimeMillis(),
        "-encoding", "lexlsh"
    };

    IndexInvertedDenseVectors.main(indexArgs);
  }

  @Test(expected = RuntimeException.class)
  public void testCollectionPath() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "invalid/path",
        "-generator", "InvertedDenseVectorDocumentGenerator",
        "-index", "target/idx-sample-ll-vector" + System.currentTimeMillis(),
        "-encoding", "lexlsh"
    };

    IndexInvertedDenseVectors.main(indexArgs);
  }

  @Test(expected = ClassNotFoundException.class)
  public void testInvalidGenerator() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-generator", "FakeGenerator",
        "-index", "target/idx-sample-ll-vector" + System.currentTimeMillis(),
        "-encoding", "lexlsh"
    };

    IndexInvertedDenseVectors.main(indexArgs);
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidEncoding() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-generator", "InvertedDenseVectorDocumentGenerator",
        "-index", "target/idx-sample-ll-vector" + System.currentTimeMillis(),
        "-encoding", "xxx"
    };

    IndexInvertedDenseVectors.main(indexArgs);
  }

  @Test
  public void testLLCollection() throws Exception {
    String indexPath = "target/idx-sample-ll-vector" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-generator", "InvertedDenseVectorDocumentGenerator",
        "-index", indexPath,
        "-encoding", "lexlsh"
    };

    IndexInvertedDenseVectors.main(indexArgs);
    assertTrue(APPENDER.getLastLog().contains("Total 100 documents indexed"));

    IndexReader reader = IndexReaderUtils.getReader(indexPath);
    Map<String, Object> results = IndexReaderUtils.getIndexStats(reader, IndexInvertedDenseVectors.FIELD_VECTOR);

    assertEquals(100, results.get("documents"));
    assertEquals(100, results.get("non_empty_documents"));
    assertEquals(4081, (int) ((Long) results.get("unique_terms")).longValue());
    assertEquals(30000, (int) ((Long) results.get("total_terms")).longValue());
  }

  @Test
  public void testFWCollection() throws Exception {
    String indexPath = "target/idx-sample-fw-vector" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-generator", "InvertedDenseVectorDocumentGenerator",
        "-index", indexPath,
        "-encoding", "fw"
    };

    IndexInvertedDenseVectors.main(indexArgs);
    assertTrue(APPENDER.getLastLog().contains("Total 100 documents indexed"));

    IndexReader reader = IndexReaderUtils.getReader(indexPath);
    Map<String, Object> results = IndexReaderUtils.getIndexStats(reader, IndexInvertedDenseVectors.FIELD_VECTOR);

    assertEquals(100, results.get("documents"));
    assertEquals(100, results.get("non_empty_documents"));
    assertEquals(1460, (int) ((Long) results.get("unique_terms")).longValue());
    assertEquals(53817, (int) ((Long) results.get("total_terms")).longValue());
  }

  @AfterClass
  public static void teardownClass() {
    ((org.apache.logging.log4j.core.Logger) LOGGER).removeAppender(APPENDER);
  }
}