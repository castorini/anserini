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
import io.anserini.ltr.feature.base.TFIDFFeatureExtractor;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Make sure that TFIDF feature extractor gives the scores as caculated by the formula
 * in the feature extractor, does not have all components of the lucene formula
 */
public class TFIDFFeatureExtractorTest extends BaseFeatureExtractorTest<Integer> {

  private FeatureExtractor EXTRACTOR = new TFIDFFeatureExtractor();

  @Test
  public void testTFIDFOnSingleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
    float[] expected = {1f};
    assertFeatureValues(expected, "document", "single document test case",  EXTRACTOR);
  }

  @Test
  public void testTFIDFOnSingleDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
    float[] expected = {2f};
    assertFeatureValues(expected, "document test", "single document test case", EXTRACTOR);
  }

  @Test
  public void testTFIDFOnMultiDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
    String queryText = "document";

    float[] expected = {1f};
    assertFeatureValues(expected, queryText, Arrays.asList("single document test case",
            "another document test"), EXTRACTOR, 0 );
  }

  @Test
  public void testTFIDFOnMultiDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
    String queryText = "document test";

    float[] expected = {2f};
    assertFeatureValues(expected, queryText, Arrays.asList("single document test case",
            "another document test"), EXTRACTOR, 0 );
  }

  @Test
  public void testTFIDFOnMultiDocMultiQuery2() throws IOException, ExecutionException, InterruptedException {
    String queryText = "document test";

    float[] expected = {2.9753323f};
    assertFeatureValues(expected, queryText, Arrays.asList("single document test case",
            "another document"), EXTRACTOR, 0 );

  }

  @Test
  public void testTFIDFOnMultiDocMultiQuery3() throws IOException, ExecutionException, InterruptedException {
    String queryText = "document test";

    float[] expected = {3.8667474f};
    assertFeatureValues(expected, queryText, Arrays.asList("single document test case",
            "new document", "another document"), EXTRACTOR, 0 );
  }
}
