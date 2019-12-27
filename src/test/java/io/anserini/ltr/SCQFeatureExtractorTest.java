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
import io.anserini.ltr.feature.base.SCQFeatureExtractor;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class SCQFeatureExtractorTest extends BaseFeatureExtractorTest<Integer> {

  private static FeatureExtractors EXTRACTOR = getChain(new SCQFeatureExtractor());

  @Test
  public void testSimpleSingleDocument() throws IOException {
    String testText = "test document";
    String testQuery = "document";
    //idf = 0.28768
    //tf =1
    float [] expected = {-0.24590f};
    assertFeatureValues(expected, testQuery, testText, EXTRACTOR);
  }

  @Test
  public void testSingleDocumentMultipleQueryToken() throws IOException {
    String testText = "test document more tokens than just two document ";
    String testQuery = "document missing";

    float[] expected = {0.22362f};
    assertFeatureValues(expected, testQuery, testText, EXTRACTOR);
  }

  @Test
  public void testSimpleMultiDocument() throws IOException {
    String testQuery = "test document";
    // idf = 0.47
    // tf = 3
    // document: 1.34359
    // test : 1 : 0.98064
    float[] expected = {1.162115f};

    assertFeatureValues(expected, testQuery,
            Arrays.asList("test document multiple tokens document",
                    "another document for doc freq count",
                    "yet another"), EXTRACTOR, 0);
  }
}
