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

package io.anserini.fusion;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;
import io.anserini.TestUtils;
import io.anserini.search.ScoredDocs;

public class ScoredDocsFuserTest extends StdOutStdErrRedirectableLuceneTestCase {
  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(FuseRuns.class.getName(), Level.ERROR);
  }

  @Before
  public void setUp() throws Exception {
    // Explictly set locale to US so that decimal points use '.' instead of ','
    Locale.setDefault(Locale.US);

    redirectStdErr();
    super.setUp();
  }

  @After
  public void tearDown() throws Exception {
    restoreStdErr();
    super.tearDown();
  }

  @Test
  public void testInvalidNumRuns() {
    ScoredDocs run = new ScoredDocs();
    Path newPath = Paths.get("runs/new_run");
    IllegalStateException thrown1 = assertThrows(IllegalStateException.class, () -> {
      ScoredDocsFuser.saveToTxt(newPath, "Anserini", run);
    });
    assertEquals("Nothing to save. ScoredDocs is empty".trim(), thrown1.getMessage().trim());

    List<ScoredDocs> runs = new ArrayList<>();
    runs.add(run);
    IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> {
      ScoredDocsFuser.merge(runs, 1000, 1000);
    });
    assertEquals("Merge requires at least 2 runs.".trim(), thrown2.getMessage().trim());
  }

  @Test
  public void testResort() {
    try {
      Path path = Paths.get("src/test/resources/sample_runs/run3");
      ScoredDocs run = ScoredDocsFuser.readRun(path, true);
      Path newPath = Paths.get("runs/sorted_run");
      ScoredDocsFuser.saveToTxt(newPath, "Anserini", run);
      TestUtils.checkFile("runs/sorted_run", new String[]{
        "query1 Q0 doc1 1 7.000000 Anserini",
        "query1 Q0 doc2 2 6.000000 Anserini",
        "query1 Q0 doc3 3 5.000000 Anserini",
        "query2 Q0 doc1 1 14.000000 Anserini",
        "query2 Q0 doc2 2 13.000000 Anserini",
        "query2 Q0 doc3 3 12.000000 Anserini"
      });
      assertTrue(new File("runs/sorted_run").delete());
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
