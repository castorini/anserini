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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    // Explicitly set locale to US so that decimal points use '.' instead of ','
    Locale.setDefault(Locale.US);
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

  // ========== Error Handling Tests ==========

  @Test
  public void testInvalidDepth() throws Exception {
    err.reset();

    String[] fuseArgs = new String[] {
      "-runs", "/fake/path",
      "-output", "runs/fused_run.test",
      "-method", "average",
      "-k", "1000",
      "-depth", "0",
      "-rrf_k", "60",
      "-alpha", "0.5"};
    FuseRuns.main(fuseArgs);

    assertEquals("Error: Option depth must be greater than 0. Please check the provided arguments. Use the \"-options\" flag to print out detailed information about available options and their usage.\n".trim(), err.toString().trim());
  }

  @Test
  public void testInvalidK() throws Exception {
    err.reset();

    String[] fuseArgs = new String[] {
      "-runs", "/fake/path",
      "-output", "runs/fused_run.test",
      "-method", "average",
      "-k", "0",
      "-depth", "1000",
      "-rrf_k", "60",
      "-alpha", "0.5"};
    FuseRuns.main(fuseArgs);

    assertEquals("Error: Option k must be greater than 0. Please check the provided arguments. Use the \"-options\" flag to print out detailed information about available options and their usage.\n".trim(), err.toString().trim());
  }

  @Test
  public void testInvalidRuns() throws Exception {
    err.reset();

    String[] fuseArgs = new String[] {
      "-runs", "/fake/path /fake/path2",
      "-output", "runs/fused_run.test",
      "-method", "average",
      "-k", "1000",
      "-depth", "1000",
      "-rrf_k", "60",
      "-alpha", "0.5"};
    FuseRuns.main(fuseArgs);

    assertEquals("Error: /fake/path (No such file or directory). Please check the provided arguments. Use the \"-options\" flag to print out detailed information about available options and their usage.\n".trim(), err.toString().trim());
  }

  @Test
  public void testInvalidMethod() throws Exception {
    err.reset();

    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run.test",
      "-method", "add",
      "-k", "1000",
      "-depth", "1000",
      "-rrf_k", "60",
      "-alpha", "0.5"};
    FuseRuns.main(fuseArgs);

    assertEquals("Unknown fusion method: add. Supported methods are: average, rrf, interpolation, weighted.".trim(), err.toString().trim());
  }

  @Test
  public void testWeightedMissingWeights() throws Exception {
    err.reset();

    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run.test",
      "-method", "weighted",
      "-k", "1000",
      "-depth", "1000"};
    FuseRuns.main(fuseArgs);

    assertEquals("Weights must be provided for weighted fusion method".trim(), err.toString().trim());
  }

  @Test
  public void testWeightedEmptyWeights() throws Exception {
    err.reset();

    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run.test",
      "-method", "weighted",
      "-k", "1000",
      "-depth", "1000",
      "-weights", ""};
    FuseRuns.main(fuseArgs);

    assertEquals("Weights must be provided for weighted fusion method".trim(), err.toString().trim());
  }

  @Test
  public void testWeightedCountMismatch() throws Exception {
    err.reset();

    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run.test",
      "-method", "weighted",
      "-k", "1000",
      "-depth", "1000",
      "-weights", "0.7,0.3,0.1"};
    FuseRuns.main(fuseArgs);

    assertEquals("Number of runs must match number of weights".trim(), err.toString().trim());
  }

  @Test
  public void testWeightedInvalidWeightValue() throws Exception {
    err.reset();

    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run.test",
      "-method", "weighted",
      "-k", "1000",
      "-depth", "1000",
      "-weights", "0.7,invalid"};
    FuseRuns.main(fuseArgs);

    assertTrue(err.toString().contains("Invalid weight value: invalid"));
  }

  @Test
  public void testInterpolationInvalidRunCount() throws Exception {
    err.reset();

    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2 src/test/resources/sample_runs/run3",
      "-output", "runs/fused_run.test",
      "-method", "interpolation",
      "-k", "1000",
      "-depth", "1000",
      "-alpha", "0.5"};
    FuseRuns.main(fuseArgs);

    assertEquals("Interpolation requires exactly 2 runs".trim(), err.toString().trim());
  }

  @Test
  public void testOptions() throws Exception {
    err.reset();

    String[] fuseArgs = new String[] {
      "-runs", "/fake/path /fake/path2",
      "-options"
    };
    FuseRuns.main(fuseArgs);

    assertTrue(err.toString().contains("Options for FuseRuns:"));
  }

  // ========== Success Tests ==========
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
  public void testFuseRRFWithNormalization() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run_rrf_norm.test",
      "-method", "rrf",
      "-k", "1000",
      "-depth", "1000",
      "-rrf_k", "60",
      "-min_max_normalization"};
    FuseRuns.main(fuseArgs);
    
    // Normalization is a no-op for RRF: RRF uses ranks (lucene_docids[i]), not scores
    // Normalizing scores doesn't change the ranks, so RRF produces identical results
    // This should match testFuseRRF exactly
    TestUtils.checkFile("runs/fused_run_rrf_norm.test", new String[]{
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
    assertTrue(new File("runs/fused_run_rrf_norm.test").delete());
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
  public void testFuseInterpolationWithNormalization() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run_interp_norm.test",
      "-method", "interpolation",
      "-k", "1000",
      "-depth", "1000",
      "-alpha", "0.5",
      "-min_max_normalization"};
    FuseRuns.main(fuseArgs);
    
    // With normalization: scores normalized per topic to [0,1], then interpolation (alpha=0.5)
    // run1 query1: [7.0,6.0,5.0] -> [1.0,0.5,0.0], run2 query1: [5.0,4.0,3.0] -> [1.0,0.5,0.0]
    // Interpolation (0.5): run1*0.5 + run2*0.5
    // doc1=1.0*0.5+0.5*0.5=0.75, doc2=0.5*0.5+1.0*0.5=0.75, doc3=0.0, doc4=0.0
    // run1 query2: [14.0,13.0,12.0] -> [1.0,0.5,0.0], run2 query2: [9.0,8.0,7.0] -> [1.0,0.5,0.0]
    // doc1=1.0*0.5+0.0*0.5=0.5, doc2=0.5*0.5+0.0*0.5=0.25, doc3=0.0*0.5+1.0*0.5=0.5, doc5=0.0*0.5+0.5*0.5=0.25, doc6=0.0
    TestUtils.checkFile("runs/fused_run_interp_norm.test", new String[]{
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
    assertTrue(new File("runs/fused_run_interp_norm.test").delete());
  }

  @Test
  public void testFuseInterpolationAlpha07() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run_interp_alpha07.test",
      "-method", "interpolation",
      "-k", "1000",
      "-depth", "1000",
      "-alpha", "0.7"};
    FuseRuns.main(fuseArgs);
    
    // Interpolation with alpha=0.7: run1*0.7 + run2*0.3
    // run1 query1: [7.0,6.0,5.0] * 0.7, run2 query1: [5.0,4.0,3.0] * 0.3
    // doc1=7.0*0.7+4.0*0.3=6.1, doc2=6.0*0.7+5.0*0.3=5.7, doc3=5.0*0.7+0.0*0.3=3.5, doc4=0.0*0.7+3.0*0.3=0.9
    // run1 query2: [14.0,13.0,12.0] * 0.7, run2 query2: [9.0,8.0,7.0] * 0.3
    // doc1=14.0*0.7+0.0*0.3=9.8, doc2=13.0*0.7+0.0*0.3=9.1, doc3=12.0*0.7+9.0*0.3=11.1, doc5=0.0*0.7+8.0*0.3=2.4, doc6=0.0*0.7+7.0*0.3=2.1
    TestUtils.checkFile("runs/fused_run_interp_alpha07.test", new String[]{
      "query1 Q0 doc1 1 6.100000 anserini.fusion",
      "query1 Q0 doc2 2 5.700000 anserini.fusion",
      "query1 Q0 doc3 3 3.500000 anserini.fusion",
      "query1 Q0 doc4 4 0.900000 anserini.fusion",
      "query2 Q0 doc3 1 11.099999 anserini.fusion",
      "query2 Q0 doc1 2 9.800000 anserini.fusion",
      "query2 Q0 doc2 3 9.100000 anserini.fusion",
      "query2 Q0 doc5 4 2.400000 anserini.fusion",
      "query2 Q0 doc6 5 2.100000 anserini.fusion"
    });
    assertTrue(new File("runs/fused_run_interp_alpha07.test").delete());
  }

  @Test
  public void testFuseInterpolationAlpha07WithNormalization() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run_interp_alpha07_norm.test",
      "-method", "interpolation",
      "-k", "1000",
      "-depth", "1000",
      "-alpha", "0.7",
      "-min_max_normalization"};
    FuseRuns.main(fuseArgs);
    
    // With normalization: scores normalized per topic to [0,1], then interpolation with alpha=0.7
    // run1 query1: [7.0,6.0,5.0] -> [1.0,0.5,0.0], run2 query1: [5.0,4.0,3.0] -> [1.0,0.5,0.0]
    // Interpolation (0.7): run1*0.7 + run2*0.3
    // doc1=1.0*0.7+0.5*0.3=0.85, doc2=0.5*0.7+1.0*0.3=0.65, doc3=0.0*0.7+0.0*0.3=0.0, doc4=0.0*0.7+0.0*0.3=0.0
    // run1 query2: [14.0,13.0,12.0] -> [1.0,0.5,0.0], run2 query2: [9.0,8.0,7.0] -> [1.0,0.5,0.0]
    // doc1=1.0*0.7+0.0*0.3=0.7, doc2=0.5*0.7+0.0*0.3=0.35, doc3=0.0*0.7+1.0*0.3=0.3, doc5=0.0*0.7+0.5*0.3=0.15, doc6=0.0*0.7+0.0*0.3=0.0
    // Note: Documents with equal scores may appear in different orders due to HashMap iteration
    TestUtils.checkFile("runs/fused_run_interp_alpha07_norm.test", new String[]{
      "query1 Q0 doc1 1 0.850000 anserini.fusion",
      "query1 Q0 doc2 2 0.650000 anserini.fusion",
      "query1 Q0 doc4 3 0.000000 anserini.fusion",
      "query1 Q0 doc3 4 0.000000 anserini.fusion",
      "query2 Q0 doc1 1 0.700000 anserini.fusion",
      "query2 Q0 doc2 2 0.350000 anserini.fusion",
      "query2 Q0 doc3 3 0.300000 anserini.fusion",
      "query2 Q0 doc5 4 0.150000 anserini.fusion",
      "query2 Q0 doc6 5 0.000000 anserini.fusion"
    });
    assertTrue(new File("runs/fused_run_interp_alpha07_norm.test").delete());
  }

  @Test
  public void testFuseAverage() throws Exception {
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
  public void testFuseAverageWithNormalization() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run_avg_norm.test",
      "-method", "average",
      "-k", "1000",
      "-depth", "1000",
      "-min_max_normalization"};
    FuseRuns.main(fuseArgs);
    
    // With normalization: scores normalized per topic to [0,1], then averaged (each run scaled by 0.5)
    // run1 query1: [7.0,6.0,5.0] -> [1.0,0.5,0.0], run2 query1: [5.0,4.0,3.0] -> [1.0,0.5,0.0]
    // Average: doc1=1.0*0.5+0.5*0.5=0.75, doc2=0.5*0.5+1.0*0.5=0.75, doc3=0.0, doc4=0.0
    // run1 query2: [14.0,13.0,12.0] -> [1.0,0.5,0.0], run2 query2: [9.0,8.0,7.0] -> [1.0,0.5,0.0]
    // Average: doc1=1.0*0.5+0.0*0.5=0.5, doc2=0.5*0.5+0.0*0.5=0.25, doc3=0.0*0.5+1.0*0.5=0.5, doc5=0.0*0.5+0.5*0.5=0.25, doc6=0.0
    TestUtils.checkFile("runs/fused_run_avg_norm.test", new String[]{
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
    assertTrue(new File("runs/fused_run_avg_norm.test").delete());
  }

  @Test
  public void testFuseWeighted() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run_weighted.test",
      "-method", "weighted",
      "-k", "1000",
      "-depth", "1000",
      "-weights", "0.7,0.3"};
    FuseRuns.main(fuseArgs);
    
    TestUtils.checkFile("runs/fused_run_weighted.test", new String[]{
      "query1 Q0 doc1 1 6.100000 anserini.fusion",
      "query1 Q0 doc2 2 5.700000 anserini.fusion",
      "query1 Q0 doc3 3 3.500000 anserini.fusion",
      "query1 Q0 doc4 4 0.900000 anserini.fusion",
      "query2 Q0 doc3 1 11.099999 anserini.fusion",
      "query2 Q0 doc1 2 9.800000 anserini.fusion",
      "query2 Q0 doc2 3 9.100000 anserini.fusion",
      "query2 Q0 doc5 4 2.400000 anserini.fusion",
      "query2 Q0 doc6 5 2.100000 anserini.fusion"
    });
    assertTrue(new File("runs/fused_run_weighted.test").delete());
  }

  @Test
  public void testWeightedWithNormalizationFlag() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run_weighted_norm_flag.test",
      "-method", "weighted",
      "-k", "1000",
      "-depth", "1000",
      "-weights", "0.7,0.3",
      "-min_max_normalization"};
    FuseRuns.main(fuseArgs);

    // With normalization: scores are normalized per topic to [0,1] before weighted fusion
    // run1 query1: [7.0,6.0,5.0] -> [1.0,0.5,0.0], run2 query1: [5.0,4.0,3.0] -> [1.0,0.5,0.0]
    // Weighted (0.7,0.3): doc1=1.0*0.7+0.5*0.3=0.85, doc2=0.5*0.7+1.0*0.3=0.65
    // run1 query2: [14.0,13.0,12.0] -> [1.0,0.5,0.0], run2 query2: [9.0,8.0,7.0] -> [1.0,0.5,0.0]
    // Weighted: doc1=1.0*0.7+0.0*0.3=0.7, doc2=0.5*0.7+0.0*0.3=0.35, doc3=0.0*0.7+1.0*0.3=0.3, doc5=0.0*0.7+0.5*0.3=0.15
    // Note: Documents with equal scores (0.0) may appear in different orders due to HashMap iteration
    TestUtils.checkFile("runs/fused_run_weighted_norm_flag.test", new String[]{
      "query1 Q0 doc1 1 0.850000 anserini.fusion",
      "query1 Q0 doc2 2 0.650000 anserini.fusion",
      "query1 Q0 doc4 3 0.000000 anserini.fusion",
      "query1 Q0 doc3 4 0.000000 anserini.fusion",
      "query2 Q0 doc1 1 0.700000 anserini.fusion",
      "query2 Q0 doc2 2 0.350000 anserini.fusion",
      "query2 Q0 doc3 3 0.300000 anserini.fusion",
      "query2 Q0 doc5 4 0.150000 anserini.fusion",
      "query2 Q0 doc6 5 0.000000 anserini.fusion"
    });
    assertTrue(new File("runs/fused_run_weighted_norm_flag.test").delete());
  }

  @Test
  public void testFuseWeightedThreeRuns() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2 src/test/resources/sample_runs/run3",
      "-output", "runs/fused_run_weighted_3.test",
      "-method", "weighted",
      "-k", "1000",
      "-depth", "1000",
      "-weights", "0.5,0.3,0.2"};
    FuseRuns.main(fuseArgs);
    
    // Weighted fusion with 3 runs (weights: 0.5, 0.3, 0.2)
    // run1: [7.0,6.0,5.0] * 0.5, run2: [5.0,4.0,3.0] * 0.3, run3: [7.0,6.0,5.0] * 0.2
    // query1: doc1=7.0*0.5+4.0*0.3+7.0*0.2=6.1, doc2=6.0*0.5+5.0*0.3+6.0*0.2=5.7, doc3=5.0*0.5+3.0*0.3+5.0*0.2=3.4, doc4=0.0*0.5+3.0*0.3+0.0*0.2=0.9
    // query2: doc1=14.0*0.5+0.0*0.3+14.0*0.2=9.8, doc2=13.0*0.5+0.0*0.3+13.0*0.2=9.1, doc3=12.0*0.5+9.0*0.3+12.0*0.2=11.1, doc5=0.0*0.5+8.0*0.3+0.0*0.2=2.4, doc6=0.0*0.5+7.0*0.3+0.0*0.2=2.1
    TestUtils.checkFile("runs/fused_run_weighted_3.test", new String[]{
      "query1 Q0 doc1 1 6.100000 anserini.fusion",
      "query1 Q0 doc2 2 5.700000 anserini.fusion",
      "query1 Q0 doc3 3 3.500000 anserini.fusion",
      "query1 Q0 doc4 4 0.900000 anserini.fusion",
      "query2 Q0 doc3 1 11.100000 anserini.fusion",
      "query2 Q0 doc1 2 9.800000 anserini.fusion",
      "query2 Q0 doc2 3 9.100000 anserini.fusion",
      "query2 Q0 doc5 4 2.400000 anserini.fusion",
      "query2 Q0 doc6 5 2.100000 anserini.fusion"
    });
    assertTrue(new File("runs/fused_run_weighted_3.test").delete());
  }

  @Test
  public void testFuseWeightedThreeRunsWithNormalization() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2 src/test/resources/sample_runs/run3",
      "-output", "runs/fused_run_weighted_3_norm.test",
      "-method", "weighted",
      "-k", "1000",
      "-depth", "1000",
      "-weights", "0.5,0.3,0.2",
      "-min_max_normalization"};
    FuseRuns.main(fuseArgs);
    
    // With normalization: scores normalized per topic to [0,1], then weighted fusion (weights: 0.5, 0.3, 0.2)
    // run1 query1: [7.0,6.0,5.0] -> [1.0,0.5,0.0], run2 query1: [5.0,4.0,3.0] -> [1.0,0.5,0.0], run3 query1: [7.0,6.0,5.0] -> [1.0,0.5,0.0]
    // Weighted: doc1=1.0*0.5+0.5*0.3+1.0*0.2=0.85, doc2=0.5*0.5+1.0*0.3+0.5*0.2=0.65, doc3=0.0*0.5+0.0*0.3+0.0*0.2=0.0, doc4=0.0*0.5+0.0*0.3+0.0*0.2=0.0
    // run1 query2: [14.0,13.0,12.0] -> [1.0,0.5,0.0], run2 query2: [9.0,8.0,7.0] -> [1.0,0.5,0.0], run3 query2: [14.0,13.0,12.0] -> [1.0,0.5,0.0]
    // Weighted: doc1=1.0*0.5+0.0*0.3+1.0*0.2=0.7, doc2=0.5*0.5+0.0*0.3+0.5*0.2=0.35, doc3=0.0*0.5+1.0*0.3+0.0*0.2=0.3, doc5=0.0*0.5+0.5*0.3+0.0*0.2=0.15, doc6=0.0*0.5+0.0*0.3+0.0*0.2=0.0
    // Note: Documents with equal scores (0.0) may appear in different orders due to HashMap iteration
    TestUtils.checkFile("runs/fused_run_weighted_3_norm.test", new String[]{
      "query1 Q0 doc1 1 0.850000 anserini.fusion",
      "query1 Q0 doc2 2 0.650000 anserini.fusion",
      "query1 Q0 doc4 3 0.000000 anserini.fusion",
      "query1 Q0 doc3 4 0.000000 anserini.fusion",
      "query2 Q0 doc1 1 0.700000 anserini.fusion",
      "query2 Q0 doc2 2 0.350000 anserini.fusion",
      "query2 Q0 doc3 3 0.300000 anserini.fusion",
      "query2 Q0 doc5 4 0.150000 anserini.fusion",
      "query2 Q0 doc6 5 0.000000 anserini.fusion"
    });
    assertTrue(new File("runs/fused_run_weighted_3_norm.test").delete());
  }

  @Test
  public void testFuseWeightedLargeWeights() throws Exception {
    String[] fuseArgs = new String[] {
      "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
      "-output", "runs/fused_run_weighted_large.test",
      "-method", "weighted",
      "-k", "1000",
      "-depth", "1000",
      "-weights", "20,25"};
    FuseRuns.main(fuseArgs);
    
    // Weighted fusion with large weights (20, 25): scores are scaled by weights before merging
    // run1 query1: [7.0,6.0,5.0] * 20, run2 query1: [5.0,4.0,3.0] * 25
    // doc1=7.0*20+4.0*25=240, doc2=6.0*20+5.0*25=245, doc3=5.0*20+0.0*25=100, doc4=0.0*20+3.0*25=75
    // run1 query2: [14.0,13.0,12.0] * 20, run2 query2: [9.0,8.0,7.0] * 25
    // doc1=14.0*20+0.0*25=280, doc2=13.0*20+0.0*25=260, doc3=12.0*20+9.0*25=465, doc5=0.0*20+8.0*25=200, doc6=0.0*20+7.0*25=175
    TestUtils.checkFile("runs/fused_run_weighted_large.test", new String[]{
      "query1 Q0 doc2 1 245.000000 anserini.fusion",
      "query1 Q0 doc1 2 240.000000 anserini.fusion",
      "query1 Q0 doc3 3 100.000000 anserini.fusion",
      "query1 Q0 doc4 4 75.000000 anserini.fusion",
      "query2 Q0 doc3 1 465.000000 anserini.fusion",
      "query2 Q0 doc1 2 280.000000 anserini.fusion",
      "query2 Q0 doc2 3 260.000000 anserini.fusion",
      "query2 Q0 doc5 4 200.000000 anserini.fusion",
      "query2 Q0 doc6 5 175.000000 anserini.fusion"
    });
    assertTrue(new File("runs/fused_run_weighted_large.test").delete());
  }
}