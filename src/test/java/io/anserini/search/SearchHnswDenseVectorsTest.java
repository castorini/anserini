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

import io.anserini.index.IndexHnswDenseVectors;
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
 * Tests for {@link SearchHnswDenseVectors}
 */
public class SearchHnswDenseVectorsTest {

  @Test
  public void searchFWTest() throws Exception {
    String indexPath = "target/idx-sample-hnsw" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "HnswDenseVectorDocumentGenerator",
        "-threads", "1",
        "-M", "16", "-efC", "100"
    };

    IndexHnswDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", runfile,
        "-querygenerator", "VectorQueryGenerator",
        "-topicreader", "JsonIntVector",
        "-topicfield", "vector",
        "-efSearch", "1000",
        "-hits", "5"};
    SearchHnswDenseVectors.main(searchArgs);

    check(runfile, new String[] {
        "160885 Q0 45 1 0.863064 Anserini",
        "160885 Q0 44 2 0.861596 Anserini",
        "160885 Q0 40 3 0.858651 Anserini",
        "160885 Q0 48 4 0.858514 Anserini",
        "160885 Q0 41 5 0.856264 Anserini",
        "867490 Q0 10 1 0.850332 Anserini",
        "867490 Q0 45 2 0.846281 Anserini",
        "867490 Q0 44 3 0.845236 Anserini",
        "867490 Q0 95 4 0.845013 Anserini",
        "867490 Q0 97 5 0.844905 Anserini"
    });

    //new File(runfile).delete();
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