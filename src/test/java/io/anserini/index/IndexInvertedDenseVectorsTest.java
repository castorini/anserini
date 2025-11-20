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

import org.apache.lucene.index.IndexReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.anserini.StdOutStdErrRedirectableTestCase;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link IndexInvertedDenseVectors}
 */
public class IndexInvertedDenseVectorsTest extends StdOutStdErrRedirectableTestCase {
  @Before
  public void setUp() throws Exception {
    redirectStdOut();
    redirectStdErr();
  }

  @After
  public void cleanUp() throws Exception {
    restoreStdOut();
    restoreStdErr();
  }

  @Test
  public void testEmptyInvocation() throws Exception {
    String[] indexArgs = new String[] {};

    IndexInvertedDenseVectors.main(indexArgs);
    assertTrue(err.toString().contains("Error"));
    assertTrue(err.toString().contains("is required"));
  }

  @Test
  public void testAskForHelp() throws Exception {
    IndexInvertedDenseVectors.main(new String[] {"-options"});
    assertTrue(err.toString().contains("Options for"));
  }

  @Test(expected = IllegalArgumentException.class)
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

  @Test(expected = IllegalArgumentException.class)
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

  @Test(expected = IllegalArgumentException.class)
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

  @Test
  public void testDefaultGenerator() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", "target/idx-sample-ll-vector" + System.currentTimeMillis(),
        "-encoding", "lexlsh"
    };

    IndexInvertedDenseVectors.main(indexArgs);
    // If this succeeded, then the default -generator of InvertedDenseVectorDocumentGenerator must have worked.
  }

  @Test(expected = IllegalArgumentException.class)
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

    IndexReader reader = IndexReaderUtils.getReader(indexPath);
    assertNotNull(reader);

    Map<String, Object> results = IndexReaderUtils.getIndexStats(reader, Constants.VECTOR);
    assertNotNull(results);
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

    IndexReader reader = IndexReaderUtils.getReader(indexPath);
    assertNotNull(reader);

    Map<String, Object> results = IndexReaderUtils.getIndexStats(reader, Constants.VECTOR);
    assertNotNull(results);
    assertEquals(100, results.get("documents"));
    assertEquals(100, results.get("non_empty_documents"));
    assertEquals(1460, (int) ((Long) results.get("unique_terms")).longValue());
    assertEquals(53817, (int) ((Long) results.get("total_terms")).longValue());
  }
}