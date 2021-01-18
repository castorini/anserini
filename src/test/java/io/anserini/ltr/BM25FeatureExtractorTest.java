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

package io.anserini.ltr;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.base.BM25;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Tests that BM25 score is computed according to our formula
 */
public class BM25FeatureExtractorTest extends BaseFeatureExtractorTest<Integer> {
  // Test the BM25 extractor with 2 settings of k and b
  private static final FeatureExtractor EXTRACTOR = new BM25(0.9,0.4);
  private static final FeatureExtractor EXTRACTOR2 = new BM25(1.25, 0.75);
  private static List<FeatureExtractor> EXTRACTORS = getChain(EXTRACTOR, EXTRACTOR2);



  @Test
  public void testSingleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
    String docText = "single document test case";
    String queryText = "test";
    //df, tf =1, avgFL = 4, numDocs = 1
    //idf = log(1 + (0.5 / 1 + 0.5)) = 0.287682

    // 0.287682* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (4/4))) = 1 * 0.287682
    // 0.287682 * 2.25 / (1 + 1.25 *(0.25 + 0.75)) = 0.287682
    float[] expected = {0.287682f,0.287682f};

    assertFeatureValues(expected, queryText, docText, EXTRACTORS);

  }

  @Test
  public void testSingleDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
    String docText = "single document test case";
    String queryText = "test document";
    //df, tf =1, avgFL = 4, numDocs = 1
    //idf = log(1 + (0.5 / 1 + 0.5)) = 0.287682

    // 0.287682* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (4/4))) = 1 * 0.287682
    // 0.287682 * 2.25 / (1 + 1.25 *(0.25 + 0.75)) = 0.287682
    float[] expected = {0.575364f,0.575364f};

    assertFeatureValues(expected, queryText, docText, EXTRACTORS);

  }

  @Test
  public void testMultiDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
    String queryText = "test";
    //df , tf =1, avgFL = 3, numDocs = 3
    //idf = log(1 + (3- 1 + 0.5 / 1 + 0.5)) = 0.98082

    // 0.98082* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (4/3))) = 0.92255
    // 0.98082* 2.25 / (1 + 1.25 *(0.25 + 0.75* 4/3)) = 0.8612
    float[] expected = {0.92255f,0.8612f};

    assertFeatureValues(expected, queryText, Arrays.asList("single document test case",
            "another document", "yet another document"), EXTRACTORS,0);

  }

  @Test
  public void testMultiDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
    String queryText = "test document";
    //df , tf =1, avgFL = 3, numDocs = 3
    //idf = log(1 + (3- 1 + 0.5 / 1 + 0.5)) = 0.98082
    //idf = log(1 + (3 - 3 + 0.5 / (3 + 0.5)) = 0.13353

    // 0.98082* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (4/3))) = 0.92255
    // 0.98082* 2.25 / (1 + 1.25 *(0.25 + 0.75* 4/3)) = 0.8612

    // 0.13353* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (4/3))) = 0.12559
    // 0.13353* 2.25 / (1 + 1.25 *(0.25 + 0.75* 4/3)) = 0.1172
    float[] expected = {1.04814f,0.97844f};

    assertFeatureValues(expected, queryText, Arrays.asList("single document test case",
            "another document", "yet another document"), EXTRACTORS,0);

  }
  @Test
  public void testMultiDocMultiQuery2() throws IOException, ExecutionException, InterruptedException {
    String queryText = "test document";
    //df , tf =1, avgFL = 3, numDocs = 3
    //idf = log(1 + (3- 1 + 0.5 / 1 + 0.5)) = 0.98082
    //idf = log(1 + (3 - 3 + 0.5 / (3 + 0.5)) = 0.13353

    // 0.98082* (2*1.9) / (2 + 0.9 * (0.6 + 0.4 * (5/3))) = 1.1870
    // 0.98082* (2*2.25) / (2 + 1.25 *(0.25 + 0.75* 5/3)) = 1.1390

    // 0.13353* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (5/3))) = 0.11855
    // 0.13353* 2.25 / (1 + 1.25 *(0.25 + 0.75* 5/3)) = 0.1045
    float[] expected = {1.30555f,1.2435f};

    assertFeatureValues(expected, queryText, Arrays.asList("single document test case test",
            "another document", "more document"), EXTRACTORS,0);

  }

}
