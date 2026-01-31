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

package io.anserini.reproduce;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;

public class RunRegressionFromCorpusTest extends StdOutStdErrRedirectableLuceneTestCase {
  @BeforeClass
  public static void setupClass() {
    suppressJvmLogging();
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
    super.tearDown();
  }

  @Test
  public void testCacmRegressionFromCorpus() throws Exception {
    // We have a catch-22 here: regression calls bin/run.sh, which requires the fatjar to run.
    // But the fatjar isn't built until the tests are run.

    // RunRegressionFromCorpus.main(new String[] {
    //     "--regression", "cacm",
    //     "--index",
    //     "--search",
    // });

    // String[] expectedRuns = {
    //     "runs/run.index.cacm.cacm.bm25",
    //     "runs/run.index.cacm.cacm.bm25+rm3",
    //     "runs/run.index.cacm.cacm.bm25+ax",
    //     "runs/run.index.cacm.cacm.ql",
    //     "runs/run.index.cacm.cacm.ql+rm3",
    //     "runs/run.index.cacm.cacm.ql+ax"
    // };

    // for (String run : expectedRuns) {
    //   Path path = Paths.get(run);
    //   assertTrue("Missing run file: " + run, Files.exists(path));
    //   assertTrue("Empty run file: " + run, Files.size(path) > 0);
    //   Files.deleteIfExists(path);
    // }

    // String stdout = out.toString();
    // int okCount = 0;
    // int index = 0;
    // while ((index = stdout.indexOf("[OK]", index)) != -1) {
    //   okCount++;
    //   index += 4;
    // }
    // assertEquals("Expected 12 instances of [OK] in stdout.", 12, okCount);

    // String[] lines = stdout.split("\\R");
    // String lastLine = lines.length == 0 ? "" : lines[lines.length - 1];
    // if (lastLine.isEmpty() && lines.length > 1) {
    //   lastLine = lines[lines.length - 2];
    // }
    // assertTrue("Final line should contain \"All Tests Passed!\"", lastLine.contains("All Tests Passed!"));
  }
}
