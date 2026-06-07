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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.lucene.tests.util.TestRuleLimitSysouts;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;

@TestRuleLimitSysouts.Limit(bytes = 64 * 1024L)
public class IndexCollectionInovcationsTest extends StdOutStdErrRedirectableLuceneTestCase {
  private final List<String> indexPaths = new ArrayList<>();

  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(IndexCollection.class.getName(), Level.ERROR);
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
    for (String indexPath : indexPaths) {
      FileUtils.deleteDirectory(new File(indexPath));
    }
    indexPaths.clear();
    super.tearDown();
  }

  private String newIndexPath(String prefix) {
    String indexPath = prefix + System.currentTimeMillis();
    indexPaths.add(indexPath);
    return indexPath;
  }

  @Test
  public void testEmptyInvocation() throws Exception {
    String[] indexArgs = new String[] {};

    IndexCollection.main(indexArgs);
    assertTrue(err.toString().contains("Error"));
    assertTrue(err.toString().contains("is required"));
  }

  @Test
  public void testAskForHelp() throws Exception {
    IndexCollection.main(new String[] {"-options"});
    assertTrue(err.toString().contains("Options for"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCollection() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "FakeTrecCollection",
        "-input", "src/test/resources/sample_docs/trec/collection2",
        "-index", newIndexPath("target/idx-sample-trec-index"),
        "-generator", "DefaultLuceneDocumentGenerator",
    };

    IndexCollection.main(indexArgs);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCollectionPath() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "TrecCollection",
        "-input", "src/test/resources/sample_docs/trec/collection2_fake_path",
        "-index", newIndexPath("target/idx-sample-trec-index"),
        "-generator", "DefaultLuceneDocumentGenerator",
    };

    IndexCollection.main(indexArgs);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidGenerator() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "TrecCollection",
        "-input", "src/test/resources/sample_docs/trec/collection2",
        "-index", newIndexPath("target/idx-sample-trec-index"),
        "-generator", "FakeDefaultLuceneDocumentGenerator",
    };

    IndexCollection.main(indexArgs);
  }

  @Test
  public void testDefaultGenerator() throws Exception {
    String[] indexArgs = new String[] {
        "-collection", "TrecCollection",
        "-input", "src/test/resources/sample_docs/trec/collection2",
        "-index", newIndexPath("target/idx-sample-trec-index")
    };

    IndexCollection.main(indexArgs);
    // If this succeeded, then the default -generator of DefaultLuceneDocumentGenerator must have worked.
  }
}
