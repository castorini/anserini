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
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class IndexCollectionInovcationsTest {
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
    Configurator.setLevel(IndexCollection.class.getName(), Level.ERROR);
  }

  @Test
  public void testEmptyInvocation() throws Exception {
    redirectStderr();
    String[] indexArgs = new String[] {};

    IndexCollection.main(indexArgs);
    assertTrue(err.toString().contains("Error"));
    assertTrue(err.toString().contains("is required"));

    restoreStderr();
  }

  @Test
  public void testAskForHelp() throws Exception {
    redirectStderr();

    IndexCollection.main(new String[] {"-options"});
    assertTrue(err.toString().contains("Options for"));

    restoreStderr();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCollection() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "FakeTrecCollection",
        "-input", "src/test/resources/sample_docs/trec/collection2",
        "-index", "target/idx-sample-trec-index" + System.currentTimeMillis(),
        "-generator", "DefaultLuceneDocumentGenerator",
    };

    IndexCollection.main(indexArgs);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCollectionPath() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "TrecCollection",
        "-input", "src/test/resources/sample_docs/trec/collection2_fake_path",
        "-index", "target/idx-sample-trec-index" + System.currentTimeMillis(),
        "-generator", "DefaultLuceneDocumentGenerator",
    };

    IndexCollection.main(indexArgs);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidGenerator() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "TrecCollection",
        "-input", "src/test/resources/sample_docs/trec/collection2",
        "-index", "target/idx-sample-trec-index" + System.currentTimeMillis(),
        "-generator", "FakeDefaultLuceneDocumentGenerator",
    };

    IndexCollection.main(indexArgs);
  }

  @Test
  public void testDefaultGenerator() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "TrecCollection",
        "-input", "src/test/resources/sample_docs/trec/collection2",
        "-index", "target/idx-sample-trec-index" + System.currentTimeMillis()
    };

    IndexCollection.main(indexArgs);
    // If this succeeded, then the default -generator of DefaultLuceneDocumentGenerator must have worked.
  }
}