/**
 * Anserini: A Lucene toolkit for replicable information retrieval research
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.anserini.ann;

import org.junit.Test;

/**
 * Tests for {@link IndexVectors}
 */
public class IndexVectorsTest {

  @Test
  public void indexFWTest() throws Exception {
    createIndex("target/idx-sample-fw", "fw");
  }

  @Test
  public void indexLLTest() throws Exception {
    createIndex("target/idx-sample-ll", "lexlsh");
  }

  static void createIndex(String path, String encoding) throws Exception {
    String[] args = new String[]{"-encoding", encoding, "-input", "src/test/resources/mini-word-vectors.txt", "-path",
        path};
    IndexVectors.main(args);
  }


}