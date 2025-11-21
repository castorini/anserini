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

package io.anserini;

import org.apache.lucene.tests.util.LuceneTestCase;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public abstract class StdOutStdErrRedirectableLuceneTestCase extends LuceneTestCase {
  protected final ByteArrayOutputStream out = new ByteArrayOutputStream();
  protected final ByteArrayOutputStream err = new ByteArrayOutputStream();
  protected PrintStream saveOut;
  protected PrintStream saveErr;

  protected static void suppressJvmLogging() {
    // Suppresses warnings like the following, which is not from Anserini code, but from deeper in the JVM.
    // WARNING: Java vector incubator module is not readable. For optimal vector performance, pass '--add-modules jdk.incubator.vector' to enable Vector API.
    java.util.logging.Logger root = java.util.logging.Logger.getLogger("");
    root.setLevel(java.util.logging.Level.OFF); // suppress INFO and below
    for (var handler : root.getHandlers()) {
      handler.setLevel(java.util.logging.Level.OFF);
    }
  }

  protected void redirectStdErr() {
    saveErr = System.err;
    err.reset();
    System.setErr(new PrintStream(err));
  }

  protected void restoreStdErr() {
    System.setErr(saveErr);
  }

  protected void redirectStdOut() {
    saveOut = System.out;
    out.reset();
    System.setOut(new PrintStream(out));
  }

  protected void restoreStdOut() {
    System.setOut(saveOut);
  }
}