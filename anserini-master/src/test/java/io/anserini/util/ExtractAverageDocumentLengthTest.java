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

package io.anserini.util;

import io.anserini.IndexerWithEmptyDocumentTestBase;
import org.junit.Test;

import java.util.Locale;

public class ExtractAverageDocumentLengthTest extends IndexerWithEmptyDocumentTestBase {

  @Test
  public void testEmptyArgs() throws Exception {
    redirectStderr();
    ExtractAverageDocumentLength.main(new String[] {});
    restoreStderr();

    assertTrue(redirectedStderr.toString().startsWith("Option \"-index\" is required"));
  }

  @Test
  public void test() throws Exception {
    // See: https://github.com/castorini/anserini/issues/903
    Locale.setDefault(Locale.US);
    redirectStdout();
    ExtractAverageDocumentLength.main(new String[] {"-index", tempDir1.toString()});
    restoreStdout();

    assertEquals("# Exact avg doclength\n" +
            "SumTotalTermFreq: 12\n" +
            "DocCount:         3\n" +
            "avg doclength:    4.0\n" +
            "\n" +
            "# Lossy avg doclength, based on sum of norms (lossy doclength) of each doc\n" +
            "SumTotalTermFreq: 12\n" +
            "DocCount:         3\n" +
            "avg doclength:    4.0\n",
        redirectedStdout.toString());
  }
}
