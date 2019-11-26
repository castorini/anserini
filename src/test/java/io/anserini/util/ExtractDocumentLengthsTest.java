/**
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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class ExtractDocumentLengthsTest extends IndexerTestBase {

  @Test
  public void test() throws Exception {
    Random rand = new Random();
    int r = rand.nextInt();
    ExtractDocumentLengths.main(new String[] {"-index", tempDir1.toString(), "-output", "doclengths" + r});

    List<String> lines = Files.readAllLines(Paths.get("doclengths" + r));
    assertEquals(4, lines.size());
    assertEquals("0\t7\t4\t7\t4", lines.get(1));
    assertEquals("1\t2\t2\t2\t2", lines.get(2));
    assertEquals("2\t2\t2\t2\t2", lines.get(3));

    Files.delete(Paths.get("doclengths" + r));
  }
}
