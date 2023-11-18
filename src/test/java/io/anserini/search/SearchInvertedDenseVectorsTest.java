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
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", runfile,
        "-topicreader", "JsonIntVector",
        "-topicfield", "vector",
        "-hits", "5",
        "-encoding", "fw"};
    SearchInvertedDenseVectors.main(searchArgs);

    check(runfile, new String[] {
        "160885 Q0 40 1 32.355999 Anserini",
        "160885 Q0 44 2 31.581369 Anserini",
        "160885 Q0 48 3 30.734432 Anserini",
        "160885 Q0 43 4 30.215816 Anserini",
        "160885 Q0 41 5 30.153873 Anserini",
        "867490 Q0 97 1 33.122585 Anserini",
        "867490 Q0 95 2 32.564468 Anserini",
        "867490 Q0 43 3 31.937614 Anserini",
        "867490 Q0 10 4 31.408100 Anserini",
        "867490 Q0 45 5 30.429819 Anserini",
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
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", runfile,
        "-topicreader", "JsonIntVector",
        "-topicfield", "vector",
        "-hits", "5",
        "-encoding", "lexlsh"};
    SearchInvertedDenseVectors.main(searchArgs);

    check(runfile, new String[] {
        "160885 Q0 97 1 82.128540 Anserini",
        "160885 Q0 4 2 79.793037 Anserini",
        "160885 Q0 118 3 77.931618 Anserini",
        "160885 Q0 43 4 75.614052 Anserini",
        "160885 Q0 65 5 74.778358 Anserini",
        "867490 Q0 45 1 84.916107 Anserini",
        "867490 Q0 13 2 82.500229 Anserini",
        "867490 Q0 10 3 82.364830 Anserini",
        "867490 Q0 44 4 79.369530 Anserini",
        "867490 Q0 67 5 78.378647 Anserini",
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