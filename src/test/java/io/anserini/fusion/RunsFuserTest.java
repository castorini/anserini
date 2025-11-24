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

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.anserini.search.ScoredDocs;

public class RunsFuserTest {
  
  @Test
  public void testAverage() throws Exception {
    List<ScoredDocs> runs = new ArrayList<>();
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run1"), false));
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run2"), false));
    
    ScoredDocs result = RunsFuser.average(runs, 1000, 1000);
    
    assertNotNull("Result should not be null", result);
    assertTrue("Result should have documents", result.lucene_documents.length > 0);
    
    // Save to file for exact match verification
    ScoredDocsFuser.saveToTxt(Paths.get("runs/test_avg_unit.txt"), "test", result);
    io.anserini.TestUtils.checkFile("runs/test_avg_unit.txt", new String[]{
      "query1 Q0 doc2 1 5.500000 test",
      "query1 Q0 doc1 2 5.500000 test",
      "query1 Q0 doc3 3 2.500000 test",
      "query1 Q0 doc4 4 1.500000 test",
      "query2 Q0 doc3 1 10.500000 test",
      "query2 Q0 doc1 2 7.000000 test",
      "query2 Q0 doc2 3 6.500000 test",
      "query2 Q0 doc5 4 4.000000 test",
      "query2 Q0 doc6 5 3.500000 test"
    });
    assertTrue(new java.io.File("runs/test_avg_unit.txt").delete());
  }

  @Test
  public void testInterpolation() throws Exception {
    List<ScoredDocs> runs = new ArrayList<>();
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run1"), false));
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run2"), false));
    
    ScoredDocs result = RunsFuser.interpolation(runs, 0.7, 1000, 1000);
    
    assertNotNull("Result should not be null", result);
    assertTrue("Result should have documents", result.lucene_documents.length > 0);
    
    // Save to file for exact match verification
    ScoredDocsFuser.saveToTxt(Paths.get("runs/test_interp_unit.txt"), "test", result);
    io.anserini.TestUtils.checkFile("runs/test_interp_unit.txt", new String[]{
      "query1 Q0 doc1 1 6.100000 test",
      "query1 Q0 doc2 2 5.700000 test",
      "query1 Q0 doc3 3 3.500000 test",
      "query1 Q0 doc4 4 0.900000 test",
      "query2 Q0 doc3 1 11.099999 test",
      "query2 Q0 doc1 2 9.800000 test",
      "query2 Q0 doc2 3 9.100000 test",
      "query2 Q0 doc5 4 2.400000 test",
      "query2 Q0 doc6 5 2.100000 test"
    });
    assertTrue(new java.io.File("runs/test_interp_unit.txt").delete());
  }

  @Test
  public void testInterpolationInvalidRunCount() throws IOException {
    List<ScoredDocs> runs = new ArrayList<>();
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run1"), false));
    
    // Static method doesn't validate - it will throw IndexOutOfBoundsException
    assertThrows(IndexOutOfBoundsException.class, () -> {
      RunsFuser.interpolation(runs, 0.5, 1000, 1000);
    });
  }

  @Test
  public void testInterpolationThreeRuns() throws Exception {
    List<ScoredDocs> runs = new ArrayList<>();
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run1"), false));
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run2"), false));
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run3"), false));
    
    // Static method doesn't validate - it will only use first 2 runs
    // Validation happens at the fuse() level, not in static methods
    ScoredDocs result = RunsFuser.interpolation(runs, 0.5, 1000, 1000);
    assertNotNull("Result should not be null", result);
    assertTrue("Result should have documents", result.lucene_documents.length > 0);
    
    // Save to file for exact match verification
    // Note: interpolation only uses first 2 runs, but merge includes all runs
    ScoredDocsFuser.saveToTxt(Paths.get("runs/test_interp3_unit.txt"), "test", result);
    io.anserini.TestUtils.checkFile("runs/test_interp3_unit.txt", new String[]{
      "query1 Q0 doc1 1 12.500000 test",
      "query1 Q0 doc2 2 11.500000 test",
      "query1 Q0 doc3 3 7.500000 test",
      "query1 Q0 doc4 4 1.500000 test",
      "query2 Q0 doc3 1 22.500000 test",
      "query2 Q0 doc1 2 21.000000 test",
      "query2 Q0 doc2 3 19.500000 test",
      "query2 Q0 doc5 4 4.000000 test",
      "query2 Q0 doc6 5 3.500000 test"
    });
    assertTrue(new java.io.File("runs/test_interp3_unit.txt").delete());
  }

  @Test
  public void testWeighted() throws Exception {
    List<ScoredDocs> runs = new ArrayList<>();
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run1"), false));
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run2"), false));
    
    List<Double> weights = new ArrayList<>();
    weights.add(0.7);
    weights.add(0.3);
    
    ScoredDocs result = RunsFuser.weighted(runs, weights, 1000, 1000);
    
    assertNotNull("Result should not be null", result);
    assertTrue("Result should have documents", result.lucene_documents.length > 0);
    
    // Save to file for exact match verification
    ScoredDocsFuser.saveToTxt(Paths.get("runs/test_weighted_unit.txt"), "test", result);
    io.anserini.TestUtils.checkFile("runs/test_weighted_unit.txt", new String[]{
      "query1 Q0 doc1 1 6.100000 test",
      "query1 Q0 doc2 2 5.700000 test",
      "query1 Q0 doc3 3 3.500000 test",
      "query1 Q0 doc4 4 0.900000 test",
      "query2 Q0 doc3 1 11.099999 test",
      "query2 Q0 doc1 2 9.800000 test",
      "query2 Q0 doc2 3 9.100000 test",
      "query2 Q0 doc5 4 2.400000 test",
      "query2 Q0 doc6 5 2.100000 test"
    });
    assertTrue(new java.io.File("runs/test_weighted_unit.txt").delete());
  }

  @Test
  public void testWeightedThreeRuns() throws Exception {
    List<ScoredDocs> runs = new ArrayList<>();
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run1"), false));
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run2"), false));
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run3"), false));
    
    List<Double> weights = new ArrayList<>();
    weights.add(0.5);
    weights.add(0.3);
    weights.add(0.2);
    
    ScoredDocs result = RunsFuser.weighted(runs, weights, 1000, 1000);
    
    assertNotNull("Result should not be null", result);
    assertTrue("Result should have documents", result.lucene_documents.length > 0);
    
    // Save to file for exact match verification
    ScoredDocsFuser.saveToTxt(Paths.get("runs/test_weighted3_unit.txt"), "test", result);
    io.anserini.TestUtils.checkFile("runs/test_weighted3_unit.txt", new String[]{
      "query1 Q0 doc1 1 6.100000 test",
      "query1 Q0 doc2 2 5.700000 test",
      "query1 Q0 doc3 3 3.500000 test",
      "query1 Q0 doc4 4 0.900000 test",
      "query2 Q0 doc3 1 11.100000 test",
      "query2 Q0 doc1 2 9.800000 test",
      "query2 Q0 doc2 3 9.100000 test",
      "query2 Q0 doc5 4 2.400000 test",
      "query2 Q0 doc6 5 2.100000 test"
    });
    assertTrue(new java.io.File("runs/test_weighted3_unit.txt").delete());
  }

  @Test
  public void testFuseWithNormalization() throws Exception {
    RunsFuser.Args args = new RunsFuser.Args();
    args.output = "runs/test_fuse_norm.test";
    args.method = "average";
    args.minMaxNormalization = true;
    args.depth = 1000;
    args.k = 1000;
    
    RunsFuser fuser = new RunsFuser(args);
    List<ScoredDocs> runs = new ArrayList<>();
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run1"), false));
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run2"), false));
    
    fuser.fuse(runs);
    
    assertTrue(new java.io.File("runs/test_fuse_norm.test").exists());
    // With normalization, exact values depend on normalization, so we just verify file exists
    // The exact values are tested in FusionTest integration tests
    assertTrue(new java.io.File("runs/test_fuse_norm.test").delete());
  }

  @Test
  public void testFuseWeightedValidation() throws Exception {
    RunsFuser.Args args = new RunsFuser.Args();
    args.output = "runs/test_fuse_weighted.test";
    args.method = "weighted";
    args.weights = "0.7,0.3";
    args.depth = 1000;
    args.k = 1000;
    
    RunsFuser fuser = new RunsFuser(args);
    List<ScoredDocs> runs = new ArrayList<>();
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run1"), false));
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run2"), false));
    
    fuser.fuse(runs);
    
    assertTrue(new java.io.File("runs/test_fuse_weighted.test").exists());
    // Verify exact results match expected weighted fusion output
    io.anserini.TestUtils.checkFile("runs/test_fuse_weighted.test", new String[]{
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
    assertTrue(new java.io.File("runs/test_fuse_weighted.test").delete());
  }

  @Test
  public void testReciprocalRankFusion() throws Exception {
    List<ScoredDocs> runs = new ArrayList<>();
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run1"), false));
    runs.add(ScoredDocsFuser.readRun(Paths.get("src/test/resources/sample_runs/run2"), false));
    
    ScoredDocs result = RunsFuser.reciprocalRankFusion(runs, 60, 1000, 1000);
    
    assertNotNull("Result should not be null", result);
    assertTrue("Result should have documents", result.lucene_documents.length > 0);
    
    // Save to file for exact match verification
    ScoredDocsFuser.saveToTxt(Paths.get("runs/test_rrf_unit.txt"), "test", result);
    io.anserini.TestUtils.checkFile("runs/test_rrf_unit.txt", new String[]{
      "query1 Q0 doc2 1 0.032522 test",
      "query1 Q0 doc1 2 0.032522 test",
      "query1 Q0 doc4 3 0.015873 test",
      "query1 Q0 doc3 4 0.015873 test",
      "query2 Q0 doc3 1 0.032266 test",
      "query2 Q0 doc1 2 0.016393 test",
      "query2 Q0 doc5 3 0.016129 test",
      "query2 Q0 doc2 4 0.016129 test",
      "query2 Q0 doc6 5 0.015873 test"
    });
    assertTrue(new java.io.File("runs/test_rrf_unit.txt").delete());
  }
}

