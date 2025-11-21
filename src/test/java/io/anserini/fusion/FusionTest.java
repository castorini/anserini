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
import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;
import io.anserini.TestUtils;

public class FusionTest extends StdOutStdErrRedirectableLuceneTestCase {
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
  public void cleanUp() throws Exception {
    restoreStdOut();
    restoreStdErr();
    super.tearDown();
  }

  @Test
  public void testFuseInterpolation() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run_interp.test",
      "-method", "interpolation",
      "-k", "1000",
      "-depth", "1000",
      "-rrf_k", "60",
      "-alpha", "0.5"};
    FuseRuns.main(fuseArgs);
    
    TestUtils.checkFile("runs/fused_run_interp.test", new String[]{
      "query1 Q0 doc2 1 5.500000 anserini.fusion",
      "query1 Q0 doc1 2 5.500000 anserini.fusion",
      "query1 Q0 doc3 3 2.500000 anserini.fusion",
      "query1 Q0 doc4 4 1.500000 anserini.fusion",
      "query2 Q0 doc3 1 10.500000 anserini.fusion",
      "query2 Q0 doc1 2 7.000000 anserini.fusion",
      "query2 Q0 doc2 3 6.500000 anserini.fusion",
      "query2 Q0 doc5 4 4.000000 anserini.fusion",
      "query2 Q0 doc6 5 3.500000 anserini.fusion"
    });
    assertTrue(new File("runs/fused_run_interp.test").delete());
  }

  @Test
  public void testFuseAvg() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run_avg.test",
      "-method", "average",
      "-k", "1000",
      "-depth", "1000",
      "-rrf_k", "60",
      "-alpha", "0.5"};
    FuseRuns.main(fuseArgs);
    
    TestUtils.checkFile("runs/fused_run_avg.test", new String[]{
      "query1 Q0 doc2 1 5.500000 anserini.fusion",
      "query1 Q0 doc1 2 5.500000 anserini.fusion",
      "query1 Q0 doc3 3 2.500000 anserini.fusion",
      "query1 Q0 doc4 4 1.500000 anserini.fusion",
      "query2 Q0 doc3 1 10.500000 anserini.fusion",
      "query2 Q0 doc1 2 7.000000 anserini.fusion",
      "query2 Q0 doc2 3 6.500000 anserini.fusion",
      "query2 Q0 doc5 4 4.000000 anserini.fusion",
      "query2 Q0 doc6 5 3.500000 anserini.fusion"
    });
    assertTrue(new File("runs/fused_run_avg.test").delete());
  }

  @Test
  public void testFuseRRF() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run_rrf.test",
      "-method", "rrf",
      "-k", "1000",
      "-depth", "1000",
      "-rrf_k", "60",
      "-alpha", "0.5"};
    FuseRuns.main(fuseArgs);
    
    TestUtils.checkFile("runs/fused_run_rrf.test", new String[]{
      "query1 Q0 doc2 1 0.032522 anserini.fusion",
      "query1 Q0 doc1 2 0.032522 anserini.fusion",
      "query1 Q0 doc4 3 0.015873 anserini.fusion",
      "query1 Q0 doc3 4 0.015873 anserini.fusion",
      "query2 Q0 doc3 1 0.032266 anserini.fusion",
      "query2 Q0 doc1 2 0.016393 anserini.fusion",
      "query2 Q0 doc5 3 0.016129 anserini.fusion",
      "query2 Q0 doc2 4 0.016129 anserini.fusion",
      "query2 Q0 doc6 5 0.015873 anserini.fusion"
    });
    assertTrue(new File("runs/fused_run_rrf.test").delete());
  }

  @Test
  public void testFuseNormalize() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run_norm.test",
      "-method", "normalize",
      "-k", "1000",
      "-depth", "1000",
      "-rrf_k", "60",
      "-alpha", "0.5"};
    FuseRuns.main(fuseArgs);
    
    TestUtils.checkFile("runs/fused_run_norm.test", new String[]{
      "query1 Q0 doc2 1 0.750000 anserini.fusion",
      "query1 Q0 doc1 2 0.750000 anserini.fusion",
      "query1 Q0 doc4 3 0.000000 anserini.fusion",
      "query1 Q0 doc3 4 0.000000 anserini.fusion",
      "query2 Q0 doc3 1 0.500000 anserini.fusion",
      "query2 Q0 doc1 2 0.500000 anserini.fusion",
      "query2 Q0 doc5 3 0.250000 anserini.fusion",
      "query2 Q0 doc2 4 0.250000 anserini.fusion",
      "query2 Q0 doc6 5 0.000000 anserini.fusion"
    });
    assertTrue(new File("runs/fused_run_norm.test").delete());
  }
}