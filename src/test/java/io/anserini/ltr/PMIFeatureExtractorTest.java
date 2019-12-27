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
import io.anserini.ltr.feature.base.PMIFeatureExtractor;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * Test implementation of PMI
 */
public class PMIFeatureExtractorTest extends BaseFeatureExtractorTest<Integer> {

  private static FeatureExtractors EXTRACTOR = getChain(new PMIFeatureExtractor());

  @Test
  public void testSingleDocSimpleQuery() throws IOException {
    String testText = "test document multiple tokens";
    String testQuery = "test document";
    float[] expected = {0f};

    assertFeatureValues(expected, testQuery, testText, EXTRACTOR);
  }

  @Test
  public void testMultipleDocSimpleQuery() throws IOException {
    float[] expected = {-1.43916f};
    String testQuery = "test document token";
    // 3 query pairs: test document, document token, test token
    // docfreqs: test: 2
    //           document: 5
    //           token: 3
    // intersects:  test document: 1
    //              document token: 2
    //              test token: 0
    // avgPMI = 1/3 * (Math.log(1/(2*5)) + Math.log(2/(5*3))) = -1.43916
    assertFeatureValues(expected, testQuery,
            Arrays.asList("test document",
                    "document token",
                    "document no match",
                    "test",
                    "no match token",
                    "no match document token",
                    "just another document"), EXTRACTOR, 0);
  }

  @Test
  public void testBadQueries() throws IOException {
    float[] expected = {0.0f};
    String testQuery  = "missing tokens";
    assertFeatureValues(expected, testQuery,
            Arrays.asList("document",
            "another document",
            "more documents",
            "just don't have the query pieces",
            "one more"), EXTRACTOR, 0);
  }

  @Test
  public void testNoIntersect() throws IOException {
    float[] expected = {0.0f};
    String testQuery = "test document";
    assertFeatureValues(expected, testQuery,
            Arrays.asList("document", "test entry",
                    "another document"), EXTRACTOR, 0);
  }
}
