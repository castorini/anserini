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

package io.anserini.index;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.anserini.util.PrebuiltIndexHandler;

public class PrebuiltIndexHandlerTest {
  // Note, cannot extend StdOutStdErrRedirectableLuceneTestCase due to concurrency issues.
  // So, we have to duplicate code to save/restore stderr/stdout.

  protected final ByteArrayOutputStream out = new ByteArrayOutputStream();
  protected final ByteArrayOutputStream err = new ByteArrayOutputStream();
  protected PrintStream saveOut;
  protected PrintStream saveErr;

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

  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(PrebuiltIndexHandler.class.getName(), Level.ERROR);
  }

  @Before
  public void setUp() throws Exception {
    redirectStdOut();
    redirectStdErr();
  }

  @After
  public void tearDown() throws Exception {
    restoreStdOut();
    restoreStdErr();
  }

  @Test
  public void testDownload() throws Exception {
    PrebuiltIndexHandler handler = new PrebuiltIndexHandler("cacm");
    handler.fetch();

    assertTrue(handler.getIndexFolderPath().toString().contains("lucene-index.cacm"));
  }

  @Test
  public void testCustomCacheDirectory() throws Exception {
    Path tempDir = Files.createTempDirectory("anserini-test-cache");

    PrebuiltIndexHandler handler = new PrebuiltIndexHandler("cacm", tempDir.toString());
    handler.fetch();

    assertTrue(handler.getIndexFolderPath().toString().contains("lucene-index.cacm"));

    FileUtils.deleteDirectory(tempDir.toFile());
  }
}
