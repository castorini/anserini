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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;

public class RunRegressionsFromPrebuiltIndexesTest extends StdOutStdErrRedirectableLuceneTestCase {
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
  public void testInvalidOption() throws Exception {
    String[] args = new String[] {"-dry"};
    RunRegressionsFromPrebuiltIndexes.main(args);

    assertTrue(err.toString().startsWith("\"-dry\" is not a valid option"));
  }

  @Test
  public void test1() throws Exception {
    String[] args = new String[] {"-regression", "beir", "-dryRun"};
    RunRegressionsFromPrebuiltIndexes.main(args);

    assertTrue(out.toString().startsWith("# Running condition"));
  }

  @Test
  public void test2() throws Exception {
    String[] args = new String[] {"-regression", "beir", "-dryRun", "-printCommands"};
    RunRegressionsFromPrebuiltIndexes.main(args);

    assertTrue(out.toString().startsWith("# Running condition"));
    assertTrue(out.toString().contains("Retrieval command"));
    assertTrue(out.toString().contains("Eval command"));
  }

  @Test
  public void testComputeIndexSize() throws Exception {
    String[] args = new String[] {"-regression", "beir", "-dryRun", "-computeIndexSize"};
    RunRegressionsFromPrebuiltIndexes.main(args);

    String s = out.toString();
    assertTrue(s.contains("Indexes referenced by this run"));
    assertTrue(s.contains("Total size across"));
  }
}
