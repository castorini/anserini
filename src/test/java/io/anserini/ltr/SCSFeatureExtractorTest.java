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

import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.base.SimplifiedClarityFeatureExtractor;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * Tests the simplified clarity feature
 */
public class SCSFeatureExtractorTest extends BaseFeatureExtractorTest<Integer> {
  private FeatureExtractors EXTRACTOR = getChain(new SimplifiedClarityFeatureExtractor());

  @Test
  public void testBadQuery() throws IOException {
    String testQuery = "test";
    // P[t|q] = 1
    // P[t|D] = 0
    float[] expected = {0f};
    assertFeatureValues(expected, testQuery,
            Arrays.asList("document",
                    "another document"), EXTRACTOR, 0);
  }

  @Test
  public void testSimpleQuery() throws IOException {
    String testQuery = "test";

    // P[t|q] = 1
    // P[t|D] = 5 / 10 = 0.5
    // 1 * log(2)
    float[] expected = {0.6931f};

    assertFeatureValues(expected, testQuery,
            Arrays.asList("test document",
                    "test test",
                    "another document",
                    "more test document",
                    "test"), EXTRACTOR, 0);

  }

  @Test
  public void testMultipleTokensQuery() throws IOException {
    String testQuery = "test document";

    // P[t|q] = 1/2
    // P[t|D] = 5 / 10 = 0.5
    // 1/2 * log(0.5/0.5)
    // 1/2 * log(0.5 / 3/10)
    float[] expected = {0.25541f};

    assertFeatureValues(expected, testQuery,
            Arrays.asList("test document",
                    "test test",
                    "another document",
                    "more test document",
                    "test"), EXTRACTOR, 0);

  }
}
