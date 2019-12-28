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
import io.anserini.ltr.feature.base.AvgIDFFeatureExtractor;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class AvgIDFFeatureExtractorTest extends BaseFeatureExtractorTest<Integer> {

  private static FeatureExtractors EXTRACTOR = getChain(new AvgIDFFeatureExtractor());

  @Test
  public void testSingleDoc() throws IOException {
    float[] expected = {0.2876f};
    assertFeatureValues(expected, "document", "test document", EXTRACTOR);
  }

  @Test
  public void testSingleDocMissingToken() throws IOException {
    float[] expected = {0.836985f};
    assertFeatureValues(expected, "document test", "document missing token", EXTRACTOR);
  }

  @Test
  public void testMultipleDocMultipleTokens() throws IOException {
    // N = 7
    // N_document = 4   0.57536
    // N_token = 0      2.77258
    // N_test = 3       0.82667
    float[] expected = {1.391537f};
    assertFeatureValues(expected, "document token test",
            Arrays.asList("first document test",
                    "second document test",
                    "third document test ",
                    "unrelated entry",
                    "another entry",
                    "another document",
                    "one more"), EXTRACTOR, 0);
  }
}
