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

import io.anserini.ltr.feature.UnigramFeatureExtractor;
import org.junit.Test;

import java.io.IOException;

/**
 * Tests the unigram count feature
 */
public class UnigramFeaturesTest extends BaseFeatureExtractorTest {

  @Test
  public void testSingleQueryTermCounts() throws IOException {
    String testText = "document document simple test case";
    String testQuery = "document";
    float [] expected = {2};
    assertFeatureValues(expected, testQuery, testText, new UnigramFeatureExtractor());
  }

  @Test
  public void testNonMatchQuery() throws IOException {
    String testText = "document document simple";
    String testQuery = "case";
    float[] expected = {0};

    assertFeatureValues(expected, testQuery, testText, new UnigramFeatureExtractor());
  }

  @Test
  public void testPartialMatches() throws IOException {
    String testText = "simple test case document";
    String testQuery = "simple document unigram";
    float[] expected = {2};

    assertFeatureValues(expected, testQuery, testText, new UnigramFeatureExtractor());
  }

  @Test
  public void testMultipleMatches() throws IOException {
    String testText = "simple simple document test case document";
    String testQuery = "document simple case nonexistent query";
    float[] expected = {5};

    assertFeatureValues(expected, testQuery, testText, new UnigramFeatureExtractor());
  }


}
