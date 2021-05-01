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

import java.util.LinkedList;
import java.util.List;

/**
 * Tests for {@link IndexVectors}
 */
public class IndexVectorsTest {

  @Test
  public void indexFWTest() throws Exception {
    createIndex("target/idx-sample-fw" + System.currentTimeMillis(), "fw", false);
  }

  @Test
  public void indexFWStoredTest() throws Exception {
    createIndex("target/idx-sample-fw" + System.currentTimeMillis(), "fw", false);
  }

  @Test
  public void indexLLTest() throws Exception {
    createIndex("target/idx-sample-ll" + System.currentTimeMillis(), "lexlsh", false);
  }

  @Test
  public void indexLLStoredTest() throws Exception {
    createIndex("target/idx-sample-ll" + System.currentTimeMillis(), "lexlsh", false);
  }

  public static void createIndex(String path, String encoding, boolean stored) throws Exception {
    List<String> args = new LinkedList<>();
    args.add("-encoding");
    args.add(encoding);
    args.add("-input");
    args.add("src/test/resources/mini-word-vectors.txt");
    args.add("-path");
    args.add(path);
    if (stored) {
      args.add("-stored");
    }
    IndexVectors.main(args.toArray(new String[0]));
  }


}