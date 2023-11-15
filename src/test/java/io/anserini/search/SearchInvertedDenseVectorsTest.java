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

package io.anserini.search;

import io.anserini.index.IndexInvertedDenseVectors;
import io.anserini.index.IndexInvertedDenseVectorsTest;
import io.anserini.search.SearchInvertedDenseVectors;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link SearchInvertedDenseVectors}
 */
public class SearchInvertedDenseVectorsTest {

  @Test
  public void searchFWTest() throws Exception {
    String indexPath = "target/idx-sample-fw-vector-" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-generator", "InvertedDenseVectorDocumentGenerator",
        "-index", indexPath,
        "-encoding", "fw"
    };
    IndexInvertedDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-cos-dpr-distil.jsonl",
        "-output", runfile,
        "-topicreader", "JsonIntVector",
        "-topicfield", "vector",
        "-hits", "5",
        "-encoding", "fw"};
    SearchInvertedDenseVectors.main(searchArgs);

    check(runfile, new String[] {
        "2 Q0 26 1 21.478451 Anserini",
        "2 Q0 122 2 19.947021 Anserini",
        "2 Q0 71 3 19.537197 Anserini",
        "2 Q0 80 4 19.263186 Anserini",
        "2 Q0 74 5 19.188883 Anserini",
        "1048585 Q0 30 1 21.119457 Anserini",
        "1048585 Q0 114 2 20.725464 Anserini",
        "1048585 Q0 36 3 20.413668 Anserini",
        "1048585 Q0 4 4 20.092403 Anserini",
        "1048585 Q0 13 5 20.087444 Anserini"
    });

    new File(runfile).delete();
  }

  @Test
  public void searchLLTest() throws Exception {
    String indexPath = "target/idx-sample-fw-vector-" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-generator", "InvertedDenseVectorDocumentGenerator",
        "-index", indexPath,
        "-encoding", "lexlsh"
    };
    IndexInvertedDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-cos-dpr-distil.jsonl",
        "-output", runfile,
        "-topicreader", "JsonIntVector",
        "-topicfield", "vector",
        "-hits", "5",
        "-encoding", "lexlsh"};
    SearchInvertedDenseVectors.main(searchArgs);

    check(runfile, new String[] {
        "2 Q0 14 1 43.783421 Anserini",
        "2 Q0 17 2 42.912968 Anserini",
        "2 Q0 5 3 42.801838 Anserini",
        "2 Q0 6 4 41.686707 Anserini",
        "2 Q0 65 5 41.679508 Anserini",
        "1048585 Q0 99 1 44.071457 Anserini",
        "1048585 Q0 50 2 40.613106 Anserini",
        "1048585 Q0 4 3 39.676960 Anserini",
        "1048585 Q0 10 4 39.406578 Anserini",
        "1048585 Q0 6 5 38.794933 Anserini"
    });

    new File(runfile).delete();
  }

  protected void check(String output, String[] ref) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(output));

    int cnt = 0;
    String s;
    while ((s = br.readLine()) != null) {
      assertEquals(ref[cnt], s);
      cnt++;
    }

    assertEquals(cnt, ref.length);
  }

}