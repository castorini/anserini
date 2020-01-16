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
import org.junit.Test;

/**
 * Test loading feature extractors from files
 */
public class LoadFeatureExtractorFromFileTest extends BaseFeatureExtractorTest{

  @Test
  public void testMultipleExtractorNoParam() throws Exception {
    String jsonFile = "src/test/resources/MixedFeatureExtractor.txt";
    String docText = "document missing token";
    String queryText = "document test";
    float[] expected = {0.836985f, 1f};

    FeatureExtractors chain = FeatureExtractors.loadExtractor(jsonFile);

    assertFeatureValues(expected, queryText, docText, chain);
  }
}
