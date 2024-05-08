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

import io.anserini.TestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;


public class FuseRunsTest {
  private final ByteArrayOutputStream err = new ByteArrayOutputStream();
  private PrintStream save;

  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(FuseRuns.class.getName(), Level.ERROR);
  }

  private void redirectStderr() {
    save = System.err;
    err.reset();
    System.setErr(new PrintStream(err));
  }

  private void restoreStderr() {
    System.setErr(save);
  }

  @Test
  public void testReciprocalRankFusionSimple() throws Exception {
    redirectStderr();
    FuseRuns.main(new String[] {
      "-method", "rrf" , 
      "-runs", "src/test/resources/simple_trec_run_fusion_1.txt", "src/test/resources/simple_trec_run_fusion_2.txt", 
      "-output", "fuse.test",
      "-runtag", "test"
    });

    TestUtils.checkFile("fuse.test", new String[]{
    "1 Q0 pyeb86on 1 0.032787 test",
    "1 Q0 2054tkb7 2 0.032002 test",
    "2 Q0 hanxiao2 1 0.016393 test",
    "3 Q0 hanxiao2 1 0.016393 test"});
    assertTrue(new File("fuse.test").delete());
    restoreStderr();
  }


  @Test
  public void testAverageFusionSimple() throws Exception {
    redirectStderr();
    FuseRuns.main(new String[] {
      "-method", "average" , 
      "-runs", "src/test/resources/simple_trec_run_fusion_1.txt", "src/test/resources/simple_trec_run_fusion_2.txt", 
      "-output", "fuse.test",
      "-runtag", "test"
    });

    TestUtils.checkFile("fuse.test", new String[]{
    "1 Q0 pyeb86on 1 13.200000 test",
    "1 Q0 2054tkb7 2 7.150000 test",
    "2 Q0 hanxiao2 1 49.500000 test",
    "3 Q0 hanxiao2 1 1.650000 test"});
    assertTrue(new File("fuse.test").delete());
    restoreStderr();
  }

  @Test
  public void testInterpolationFusionSimple() throws Exception {
    redirectStderr();
    FuseRuns.main(new String[] {
      "-method", "interpolation" , 
      "-runs", "src/test/resources/simple_trec_run_fusion_1.txt", "src/test/resources/simple_trec_run_fusion_2.txt", 
      "-output", "fuse.test",
      "-alpha", "0.4",
      "-runtag", "test"
    });

    TestUtils.checkFile("fuse.test", new String[]{
    "1 Q0 pyeb86on 1 11.040000 test",
    "1 Q0 2054tkb7 2 5.980000 test",
    "2 Q0 hanxiao2 1 39.600000 test",
    "3 Q0 hanxiao2 1 1.980000 test"});
    assertTrue(new File("fuse.test").delete());
    restoreStderr();
  }

  @Test
  public void testDepthAndKVariance() throws Exception {
    redirectStderr();
    FuseRuns.main(new String[] {
      "-method", "rrf",
      "-runs", "src/test/resources/simple_trec_run_fusion_3.txt", "src/test/resources/simple_trec_run_fusion_4.txt",
      "-output", "fuse.test",
      "-runtag", "test",
      "-k", "1",
      "-depth", "2"
    });

    TestUtils.checkFile("fuse.test", new String[] {
      "1 Q0 hanxiao2 1 0.032787 test"
    });

    assertTrue(new File("fuse.test").delete());
    restoreStderr();
  }

  @Test
  public void testInvalidArguments() throws Exception {
    redirectStderr();

    FuseRuns.main(new String[] {
      "-method", "nonexistentmethod",
      "-runs", "src/test/resources/simple_trec_run_fusion_3.txt", "src/test/resources/simple_trec_run_fusion_4.txt",
      "-output", "fuse.test",
      "-runtag", "test",
    });
    assertTrue(err.toString().contains("This method has not yet been implemented"));
    err.reset();

    FuseRuns.main(new String[] {
      "-method", "rrf",
      "-runs", "src/test/resources/nonexistentfilethatwillneverexist.txt", "src/test/resources/simple_trec_run_fusion_4.txt",
      "-output", "fuse.test",
      "-runtag", "test",
    });
    assertTrue(err.toString().contains("Error occured: src/test/resources/nonexistentfilethatwillneverexist.txt (No such file or directory)"));
    err.reset();

    FuseRuns.main(new String[] {
      "-method", "rrf",
      "-runs", "src/test/resources/simple_trec_run_fusion_3.txt", "src/test/resources/simple_trec_run_fusion_4.txt",
      "-output", "fuse.test",
      "-runtag", "test",
      "-k", "0",
    });
    assertTrue(err.toString().contains("Option k must be greater than 0"));
    err.reset();

    FuseRuns.main(new String[] {
      "-method", "rrf",
      "-runs", "src/test/resources/simple_trec_run_fusion_3.txt", "src/test/resources/simple_trec_run_fusion_4.txt",
      "-output", "fuse.test",
      "-runtag", "test",
      "-depth", "0",
    });
    assertTrue(err.toString().contains("Option depth must be greater than 0"));
    err.reset();

    FuseRuns.main(new String[] {
      "-method", "rrf",
      "-runs", "src/test/resources/simple_trec_run_fusion_3.txt", 
      "-output", "fuse.test",
      "-runtag", "test",
    });
    assertTrue(err.toString().contains("Option run expects exactly 2 files"));
    err.reset();

    restoreStderr();
  }

}

