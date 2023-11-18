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

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link IndexHnswDenseVectors}
 */
public class IndexHnswDenseVectorsTest {
  private static final Logger LOGGER = LogManager.getLogger(IndexHnswDenseVectors.class);
  private static CustomAppender APPENDER;

  @BeforeClass
  public static void setupClass() {
    APPENDER = new CustomAppender("CustomAppender");
    APPENDER.start();

    ((org.apache.logging.log4j.core.Logger) LOGGER).addAppender(APPENDER);

    Configurator.setLevel(IndexHnswDenseVectors.class.getName(), Level.INFO);
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

    System.out.println(APPENDER.getLastLog());
    assertTrue(APPENDER.getLastLog().contains("Total 100 documents indexed"));

    IndexReader reader = IndexReaderUtils.getReader(indexPath);
    assertNotNull(reader);

    Map<String, Object> results = IndexReaderUtils.getIndexStats(reader, Constants.VECTOR);
    assertEquals(100, results.get("documents"));
  }

  @AfterClass
  public static void teardownClass() {
    ((org.apache.logging.log4j.core.Logger) LOGGER).removeAppender(APPENDER);
  }
}