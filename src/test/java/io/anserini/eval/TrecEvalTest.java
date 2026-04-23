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
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

public class TrecEvalTest {
  @Test
  public void testRunWithMainCacmBm25() {
    assertNotNull(TrecEval.getExecutableName());

    String[] args = new String[] {
        "-m", "P.30",
        "src/test/resources/sample_qrels/cacm/qrels.cacm.txt",
        "src/test/resources/sample_runs/cacm/cacm-bm25.txt"
    };

    TrecEval.main(args);
  }

  @Test
  public void testRunAndGetOutputCacm() {
    assertNotNull(TrecEval.getExecutableName());

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
    assertEquals(0, trecEval.getLastExitCode());
  }

  @Test
  public void testRunAndGetOutputCacmSymbol() {
    assertNotNull(TrecEval.getExecutableName());

    TrecEval trecEval = new TrecEval();
    String[] args = new String[] {
        "-m", "P.30",
        "cacm", // This is a symbol, should resolve to actual path auto-magically.
        "src/test/resources/sample_runs/cacm/cacm-bm25.txt"
    };
    String[][] output = trecEval.runAndGetOutput(args);

    assertNotNull(output);
    assertEquals(1, output.length);
    assertEquals("P_30", output[0][0]);
    assertEquals("all", output[0][1]);
    assertEquals("0.1942", output[0][2]);
  }

  @Test(expected = RuntimeException.class)
  public void testRunAndGetOutputFakeSymbol() {
    // Fake symbol, should throw RuntimeException
    TrecEval trecEval = new TrecEval();
    String[] args = new String[] {
        "-m", "P.30",
        "fake", // This is a fake symbol
        "src/test/resources/sample_runs/cacm/cacm-bm25.txt"
    };

    trecEval.runAndGetOutput(args);
  }

  @Test
  public void testGetOsShortThrowsExceptionOnUnsupportedOs() throws Exception {
    String originalOsName = System.getProperty("os.name");
    try {
      System.setProperty("os.name", "UnsupportedOS");
      Method method = TrecEval.class.getDeclaredMethod("getOsShort");
      method.setAccessible(true);

      try {
        method.invoke(null);
        fail("Expected UnsupportedOperationException to be thrown.");
      } catch (InvocationTargetException e) {
        assertEquals(UnsupportedOperationException.class, e.getCause().getClass());
        assertEquals("Unsupported os: UnsupportedOS", e.getCause().getMessage());
      }
    } finally {
      if (originalOsName == null) {
        System.clearProperty("os.name");
      } else {
        System.setProperty("os.name", originalOsName);
      }
    }
  }
}
