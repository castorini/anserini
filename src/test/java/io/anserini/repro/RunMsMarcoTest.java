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

package io.anserini.repro;

import io.anserini.reproduce.RunMsMarco;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class RunMsMarcoTest {
  private final ByteArrayOutputStream out = new ByteArrayOutputStream();
  private final ByteArrayOutputStream err = new ByteArrayOutputStream();
  private PrintStream save;

  private void redirectStderr() {
    save = System.err;
    err.reset();
    System.setErr(new PrintStream(err));
  }

  private void restoreStderr() {
    System.setErr(save);
  }

  private void redirectStdout() {
    save = System.out;
    out.reset();
    System.setOut(new PrintStream(out));
  }

  private void restoreStdout() {
    System.setOut(save);
  }

  @Test
  public void testInvalidOption() throws Exception {
    redirectStderr();

    String[] args = new String[] {"-dry"};
    RunMsMarco.main(args);

    assertTrue(err.toString().startsWith("Error: \"-dry\" is not a valid option."));
    restoreStderr();
  }

  @Test
  public void test1() throws Exception {
    redirectStdout();

    String[] args = new String[] {"-dryRun"};
    RunMsMarco.main(args);

    assertTrue(out.toString().startsWith("# Running condition"));
    restoreStdout();
  }
}
