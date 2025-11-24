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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.anserini.TestUtils;
import io.anserini.search.ScoredDocs;

public class ScoredDocsFuserTest {
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

  @Test
  public void testNormalizeScores() throws Exception {
    Path path = Paths.get("src/test/resources/sample_runs/run1");
    ScoredDocs run = ScoredDocsFuser.readRun(path, false);
    
    // Normalize the scores
    ScoredDocsFuser.normalizeScores(run);
    
    // After normalization, scores should be in range [0, 1] per topic
    // Verify exact normalized scores
    // run1 has: query1 [doc1=7.0, doc2=6.0, doc3=5.0], query2 [doc1=14.0, doc2=13.0, doc3=12.0]
    // Normalization per topic: (score - min) / (max - min)
    // query1: min=5.0, max=7.0 -> doc1=1.0, doc2=0.5, doc3=0.0
    // query2: min=12.0, max=14.0 -> doc1=1.0, doc2=0.5, doc3=0.0
    
    assertEquals("query1 doc1 should be normalized to 1.0", 1.0f, run.scores[0], 0.0001f);
    assertEquals("query1 doc2 should be normalized to 0.5", 0.5f, run.scores[1], 0.0001f);
    assertEquals("query1 doc3 should be normalized to 0.0", 0.0f, run.scores[2], 0.0001f);
    assertEquals("query2 doc1 should be normalized to 1.0", 1.0f, run.scores[3], 0.0001f);
    assertEquals("query2 doc2 should be normalized to 0.5", 0.5f, run.scores[4], 0.0001f);
    assertEquals("query2 doc3 should be normalized to 0.0", 0.0f, run.scores[5], 0.0001f);
  }

  @Test
  public void testRescoreMethods() throws Exception {
    Path path = Paths.get("src/test/resources/sample_runs/run1");
    ScoredDocs run = ScoredDocsFuser.readRun(path, false);
    
    // Test SCALE rescore with factor 2.0
    // Original scores: query1 [7.0, 6.0, 5.0], query2 [14.0, 13.0, 12.0]
    // After scaling: query1 [14.0, 12.0, 10.0], query2 [28.0, 26.0, 24.0]
    ScoredDocsFuser.rescore(ScoredDocsFuser.RescoreMethod.SCALE, 0, 2.0, run);
    assertEquals("query1 doc1 scaled to 14.0", 14.0f, run.scores[0], 0.0001f);
    assertEquals("query1 doc2 scaled to 12.0", 12.0f, run.scores[1], 0.0001f);
    assertEquals("query1 doc3 scaled to 10.0", 10.0f, run.scores[2], 0.0001f);
    assertEquals("query2 doc1 scaled to 28.0", 28.0f, run.scores[3], 0.0001f);
    assertEquals("query2 doc2 scaled to 26.0", 26.0f, run.scores[4], 0.0001f);
    assertEquals("query2 doc3 scaled to 24.0", 24.0f, run.scores[5], 0.0001f);
    
    // Reset and test RRF rescore with k=60
    // RRF formula: 1.0 / (k + rank)
    // query1: ranks [1, 2, 3] -> scores [1/61, 1/62, 1/63]
    // query2: ranks [1, 2, 3] -> scores [1/61, 1/62, 1/63]
    run = ScoredDocsFuser.readRun(path, false);
    ScoredDocsFuser.rescore(ScoredDocsFuser.RescoreMethod.RRF, 60, 0, run);
    assertEquals("query1 doc1 RRF score (rank 1)", 1.0f / 61.0f, run.scores[0], 0.0001f);
    assertEquals("query1 doc2 RRF score (rank 2)", 1.0f / 62.0f, run.scores[1], 0.0001f);
    assertEquals("query1 doc3 RRF score (rank 3)", 1.0f / 63.0f, run.scores[2], 0.0001f);
    assertEquals("query2 doc1 RRF score (rank 1)", 1.0f / 61.0f, run.scores[3], 0.0001f);
    assertEquals("query2 doc2 RRF score (rank 2)", 1.0f / 62.0f, run.scores[4], 0.0001f);
    assertEquals("query2 doc3 RRF score (rank 3)", 1.0f / 63.0f, run.scores[5], 0.0001f);
  }
}
