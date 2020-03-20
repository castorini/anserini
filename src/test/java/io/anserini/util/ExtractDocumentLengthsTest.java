/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

package io.anserini.util;

import io.anserini.IndexerTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ExtractDocumentLengthsTest extends IndexerTestBase {
  private static final Random rand = new Random();
  private String randomFileName;

  private final ByteArrayOutputStream out = new ByteArrayOutputStream();
  private PrintStream save;

  private void redirectStdout() {
    save = System.out;
    out.reset();
    System.setOut(new PrintStream(out));
  }

  private void restoreStdout() {
    System.setOut(save);
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    randomFileName = "doclengths" + rand.nextInt();
  }

  @After
  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    Files.delete(Paths.get(randomFileName));
  }

  @Test
  public void test() throws Exception {
    // See: https://github.com/castorini/anserini/issues/903
    Locale.setDefault(Locale.US);
    redirectStdout();
    ExtractDocumentLengths.main(new String[] {"-index", tempDir1.toString(), "-output", randomFileName});
    restoreStdout();

    assertEquals("Total number of terms in collection (sum of doclengths):\nLossy: 12\nExact: 12\n",
        out.toString());

    List<String> lines = Files.readAllLines(Paths.get(randomFileName));
    assertEquals(4, lines.size());
    assertEquals("0\t8\t5\t8\t5", lines.get(1));
    assertEquals("1\t2\t2\t2\t2", lines.get(2));
    assertEquals("2\t2\t2\t2\t2", lines.get(3));
  }
}
