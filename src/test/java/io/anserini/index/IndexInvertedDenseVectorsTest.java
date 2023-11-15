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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link IndexInvertedDenseVectors}
 */
public class IndexInvertedDenseVectorsTest {
  private static final Logger LOGGER = LogManager.getLogger(IndexInvertedDenseVectors.class);
  private static CustomAppender APPENDER;

  @BeforeClass
  public static void setupClass() {
    APPENDER = new CustomAppender("CustomAppender");
    APPENDER.start();

    ((org.apache.logging.log4j.core.Logger) LOGGER).addAppender(APPENDER);

    Configurator.setLevel(IndexInvertedDenseVectors.class.getName(), Level.INFO);
  }

  @Test(expected = ClassNotFoundException.class)
  public void testInvalidCollection() throws Exception {
    List<String> args = new LinkedList<>();
    args.add("-collection");
    args.add("FakeCollection");
    args.add("-input");
    args.add("src/test/resources/sample_docs/openai_ada2/json_vector");
    args.add("-generator");
    args.add("InvertedDenseVectorDocumentGenerator");
    args.add("-index");
    args.add("target/idx-sample-ll-vector" + System.currentTimeMillis());
    args.add("-encoding");
    args.add("lexlsh");

    IndexInvertedDenseVectors.main(args.toArray(new String[0]));
  }

  @Test(expected = RuntimeException.class)
  public void testCollectionPath() throws Exception {
    List<String> args = new LinkedList<>();
    args.add("-collection");
    args.add("JsonDenseVectorCollection");
    args.add("-input");
    args.add("invalid/path");
    args.add("-generator");
    args.add("InvertedDenseVectorDocumentGenerator");
    args.add("-index");
    args.add("target/idx-sample-ll-vector" + System.currentTimeMillis());
    args.add("-encoding");
    args.add("lexlsh");

    IndexInvertedDenseVectors.main(args.toArray(new String[0]));
  }

  @Test(expected = ClassNotFoundException.class)
  public void testInvalidGenerator() throws Exception {
    List<String> args = new LinkedList<>();
    args.add("-collection");
    args.add("JsonDenseVectorCollection");
    args.add("-input");
    args.add("src/test/resources/sample_docs/openai_ada2/json_vector");
    args.add("-generator");
    args.add("FakeGenerator");
    args.add("-index");
    args.add("target/idx-sample-ll-vector" + System.currentTimeMillis());
    args.add("-encoding");
    args.add("lexlsh");

    IndexInvertedDenseVectors.main(args.toArray(new String[0]));
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidEncoding() throws Exception {
    List<String> args = new LinkedList<>();
    args.add("-collection");
    args.add("JsonDenseVectorCollection");
    args.add("-input");
    args.add("src/test/resources/sample_docs/openai_ada2/json_vector");
    args.add("-generator");
    args.add("InvertedDenseVectorDocumentGenerator");
    args.add("-index");
    args.add("target/idx-sample-ll-vector" + System.currentTimeMillis());
    args.add("-encoding");
    args.add("xxx");

    IndexInvertedDenseVectors.main(args.toArray(new String[0]));
  }

  @Test
  public void testLLCollection() throws Exception {
    List<String> args = new LinkedList<>();
    args.add("-collection");
    args.add("JsonDenseVectorCollection");
    args.add("-input");
    args.add("src/test/resources/sample_docs/openai_ada2/json_vector");
    args.add("-generator");
    args.add("InvertedDenseVectorDocumentGenerator");
    args.add("-index");
    args.add("target/idx-sample-ll-vector" + System.currentTimeMillis());
    args.add("-encoding");
    args.add("lexlsh");

    IndexInvertedDenseVectors.main(args.toArray(new String[0]));

    assertTrue(APPENDER.getLastLog().contains("Total 100 documents indexed"));
  }

  @Test
  public void testFWCollection() throws Exception {
    List<String> args = new LinkedList<>();
    args.add("-collection");
    args.add("JsonDenseVectorCollection");
    args.add("-input");
    args.add("src/test/resources/sample_docs/openai_ada2/json_vector");
    args.add("-generator");
    args.add("InvertedDenseVectorDocumentGenerator");
    args.add("-index");
    args.add("target/idx-sample-fw-vector" + System.currentTimeMillis());
    args.add("-encoding");
    args.add("fw");

    IndexInvertedDenseVectors.main(args.toArray(new String[0]));

    assertTrue(APPENDER.getLastLog().contains("Total 100 documents indexed"));
  }

  @AfterClass
  public static void teardownClass() {
    ((org.apache.logging.log4j.core.Logger) LOGGER).removeAppender(APPENDER);
  }
}