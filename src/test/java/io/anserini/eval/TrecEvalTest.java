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

package io.anserini.eval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TrecEvalTest {
  @Test
  public void testRunAndGetOutputCacmBm25() {
    TrecEval trecEval = new TrecEval();
    String[] args = new String[] {
        "-m", "P.30",
        "src/test/resources/sample_qrels/cacm/qrels.cacm.txt",
        "src/test/resources/sample_runs/cacm/cacm-bm25.txt"
    };
    String[][] output = trecEval.runAndGetOutput(args);

    assertNotNull(output);
    assertEquals(1, output.length);
    assertEquals("P_30", output[0][0]);
    assertEquals("all", output[0][1]);
    assertEquals("0.1942", output[0][2]);
  }
}
