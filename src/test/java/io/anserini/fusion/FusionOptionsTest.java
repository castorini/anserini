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

import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;

public class FusionOptionsTest extends StdOutStdErrRedirectableLuceneTestCase {
  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(FuseRuns.class.getName(), Level.ERROR);
  }

  @Before
  public void setUp() throws Exception {
    // Explictly set locale to US so that decimal points use '.' instead of ','
    Locale.setDefault(Locale.US);

    redirectStdOut();
    redirectStdErr();
    super.setUp();
  }

  @After
  public void tearDown() throws Exception {
    restoreStdOut();
    restoreStdErr();
  }

  @Test
  public void testInvalidDepth() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "/fake/path",
      "-output", "runs/fused_run.test",
      "-method", "normalize",
      "-k", "1000",
      "-depth", "0",
      "-rrf_k", "60",
      "-alpha", "0.5"};
    FuseRuns.main(fuseArgs);

    assertEquals("Error: Option depth must be greater than 0. Please check the provided arguments. Use the \"-options\" flag to print out detailed information about available options and their usage.\n".trim(), err.toString().trim());
  }

  @Test
  public void testInvalidK() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "/fake/path",
      "-output", "runs/fused_run.test",
      "-method", "normalize",
      "-k", "0",
      "-depth", "1000",
      "-rrf_k", "60",
      "-alpha", "0.5"};
    FuseRuns.main(fuseArgs);

    assertEquals("Error: Option k must be greater than 0. Please check the provided arguments. Use the \"-options\" flag to print out detailed information about available options and their usage.\n".trim(), err.toString().trim());
  }

  @Test
  public void testInvalidRuns() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "/fake/path /fake/path2",
      "-output", "runs/fused_run.test",
      "-method", "normalize",
      "-k", "1000",
      "-depth", "1000",
      "-rrf_k", "60",
      "-alpha", "0.5"};
    FuseRuns.main(fuseArgs);

    assertEquals("Error: /fake/path (No such file or directory). Please check the provided arguments. Use the \"-options\" flag to print out detailed information about available options and their usage.\n".trim(), err.toString().trim());
  }

  @Test
  public void testInvalidMethod() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run.test",
      "-method", "add",
      "-k", "1000",
      "-depth", "1000",
      "-rrf_k", "60",
      "-alpha", "0.5"};
    FuseRuns.main(fuseArgs);

    assertEquals("Unknown fusion method: add. Supported methods are: average, rrf, interpolation.".trim(), err.toString().trim());
  }

  @Test
  public void testOptions() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "/fake/path /fake/path2",
      "-options"
    };
    FuseRuns.main(fuseArgs);

    assertTrue(err.toString().contains("Options for FuseRuns:"));
  }
}
