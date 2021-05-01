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

package io.anserini.ann;

import org.junit.Test;

/**
 * Tests for {@link ApproximateNearestNeighborSearch}
 */
public class ApproximateNearestNeighborSearchTest {

  @Test
  public void searchFWTest() throws Exception {
    String path = "target/idx-sample-fw" + System.currentTimeMillis();
    String encoding = "fw";
    IndexVectorsTest.createIndex(path, encoding, false);
    String[] args = new String[]{"-encoding", encoding, "-input", "src/test/resources/mini-word-vectors.txt", "-path",
        path, "-word", "foo"};
    ApproximateNearestNeighborSearch.main(args);
  }

  @Test
  public void searchFWStoredTest() throws Exception {
    String path = "target/idx-sample-fw-stored" + System.currentTimeMillis();
    String encoding = "fw";
    IndexVectorsTest.createIndex(path, encoding, true);
    String[] args = new String[]{"-encoding", encoding, "-stored", "-path", path, "-word", "foo"};
    ApproximateNearestNeighborSearch.main(args);
  }

  @Test
  public void searchLLTest() throws Exception {
    String path = "target/idx-sample-ll" + System.currentTimeMillis();
    String encoding = "lexlsh";
    IndexVectorsTest.createIndex(path, encoding, false);
    String[] args = new String[]{"-encoding", encoding, "-input", "src/test/resources/mini-word-vectors.txt", "-path",
        path, "-word", "foo"};
    ApproximateNearestNeighborSearch.main(args);
  }

  @Test
  public void searchLLStoredTest() throws Exception {
    String path = "target/idx-sample-ll" + System.currentTimeMillis();
    String encoding = "lexlsh";
    IndexVectorsTest.createIndex(path, encoding, true);
    String[] args = new String[]{"-encoding", encoding, "-stored", "-path", path, "-word", "foo"};
    ApproximateNearestNeighborSearch.main(args);
  }

}