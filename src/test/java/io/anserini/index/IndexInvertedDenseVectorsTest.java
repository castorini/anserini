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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
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

  @Test
  public void indexFWTest() throws Exception {
    createIndex("target/idx-sample-fw" + System.currentTimeMillis(), "fw", false);
    assertTrue(APPENDER.getLastLog().contains("Total 4 documents indexed"));
  }

  @Test
  public void indexFWStoredTest() throws Exception {
    createIndex("target/idx-sample-fw" + System.currentTimeMillis(), "fw", false);
    assertTrue(APPENDER.getLastLog().contains("Total 4 documents indexed"));
  }

  @Test
  public void indexLLTest() throws Exception {
    createIndex("target/idx-sample-ll" + System.currentTimeMillis(), "lexlsh", false);
    assertTrue(APPENDER.getLastLog().contains("Total 4 documents indexed"));
  }

  @Test
  public void indexLLStoredTest() throws Exception {
    createIndex("target/idx-sample-ll" + System.currentTimeMillis(), "lexlsh", false);
    assertTrue(APPENDER.getLastLog().contains("Total 4 documents indexed"));
  }

  public static void createIndex(String path, String encoding, boolean stored) throws Exception {
    List<String> args = new LinkedList<>();
    args.add("-encoding");
    args.add(encoding);
    args.add("-input");
    args.add("src/test/resources/mini-word-vectors.txt");
    args.add("-index");
    args.add(path);
    if (stored) {
      args.add("-stored");
    }

    IndexInvertedDenseVectors.main(args.toArray(new String[0]));
  }

  @Test
  public void testLLCollection() throws Exception {
    List<String> args = new LinkedList<>();
    args.add("-collection");
    args.add("JsonDenseVectorCollection");
    args.add("-encoding");
    args.add("lexlsh");
    args.add("-input");
    args.add("src/test/resources/sample_docs/json_vector/dense_collection1");
    args.add("-index");
    args.add("target/idx-sample-ll-vector" + System.currentTimeMillis());
    args.add("-stored");

    IndexInvertedDenseVectors.main(args.toArray(new String[0]));

    assertTrue(APPENDER.getLastLog().contains("Total 2 documents indexed"));
  }

  @Test
  public void testFWCollection() throws Exception {
    List<String> args = new LinkedList<>();
    args.add("-collection");
    args.add("JsonDenseVectorCollection");
    args.add("-encoding");
    args.add("fw");
    args.add("-input");
    args.add("src/test/resources/sample_docs/json_vector/dense_collection1");
    args.add("-index");
    args.add("target/idx-sample-fw-vector" + System.currentTimeMillis());
    args.add("-stored");

    IndexInvertedDenseVectors.main(args.toArray(new String[0]));

    assertTrue(APPENDER.getLastLog().contains("Total 2 documents indexed"));
  }

  private static final Logger LOGGER = LogManager.getLogger(IndexInvertedDenseVectors.class);
  private static CustomAppender APPENDER;

  @BeforeClass
  public static void setupClass() {
    APPENDER = new CustomAppender("CustomAppender");
    APPENDER.start();

    ((org.apache.logging.log4j.core.Logger) LOGGER).addAppender(APPENDER);

    Configurator.setLevel(IndexInvertedDenseVectors.class.getName(), Level.INFO);
  }

  @AfterClass
  public static void teardownClass() {
    ((org.apache.logging.log4j.core.Logger) LOGGER).removeAppender(APPENDER);
  }

  private static class CustomAppender extends AbstractAppender {
    private String lastLog = null;

    protected CustomAppender(String name) {
      super(name, null, null, true, null);
    }

    public String getLastLog() {
      return lastLog;
    }

    @Override
    public void append(LogEvent event) {
      lastLog = event.getMessage().getFormattedMessage();
    }
  }
}