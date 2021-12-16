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

package io.anserini.ltr;

import io.anserini.ltr.feature.TpScore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Tests that TP score is computed according to our formula
 */
public class TpScoreTest extends BaseFeatureExtractorTest<Integer> {
  // Test the BM25 extractor with 2 settings of k and b
  private static final FeatureExtractor EXTRACTOR = new TpScore();
  private static List<FeatureExtractor> EXTRACTORS = getChain(EXTRACTOR);



  @Test
  public void testSingleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
    String docText = "single document test case";
    String queryText = "test";
    /*As there is only one querytext in doc, the score is just the bm25 score which is 0.2876821*/
    float[] expected = {0.287682f};

    assertFeatureValues(expected, queryText, docText, EXTRACTORS);

  }

  @Test
  public void testSingleDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
    String docText = "single document test case";
    String queryText = "test document";
    /*As both of the query text only appear one time in doc, the score is just the bm25 score which is 0.575364*/
    float[] expected = {0.575364f};

    assertFeatureValues(expected, queryText, docText, EXTRACTORS);

  }

  @Test
  public void testMultiDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
    String queryText = "test";
    //numDocs =1, docSize =6, avgFL = 6
    //BM25 score 0.4204584
    //weights = log(1/3)=-1.0986
    //K = 0.9 * (0.6+ (0.4 * (6 / 6))) =0.9
    // as there's only one doc, accumulator is 0;
    // x = accumulator * (1 + 0.9) = 0
    // y = accumulator + K =0.9
    // score += weight * (x / y);
    float[] expected = {0.4204584f};

    assertFeatureValues(expected, queryText, Arrays.asList("test single document test test case"), EXTRACTORS,0);

  }

  @Test
  public void testMultiDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
    String queryText = "test document";
    //numDocs =2, docSize =6, avgFL = 5.5
    //BM25 score 1.1838651
    //For "test single document test test case":
    //weights = log(2/3)=-0.405
    //K = 0.9 * (0.6+ (0.4 * (6 / 5.5))) =0.932
    //accumulator = weights *((pos-prev_pos)^-2);
    //accumulator for each query term is -0.5068
    // x = accumulator * (1 + 0.9) = -0.9629
    // y = accumulator + K = 0.42589
    // score += weight * (x / y) = -0.405*(-0.9629/0.42589)*4 = 3.67;
    // then we add the bm25 and the accumulator score together 1.18+3.67
    float[] expected = {4.851002f};

    assertFeatureValues(expected, queryText, Arrays.asList("test single document test test case",
            "another document yet another document"), EXTRACTORS,0);

  }

}
