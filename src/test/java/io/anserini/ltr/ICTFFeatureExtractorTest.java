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
import io.anserini.ltr.feature.base.AvgICTFFeatureExtractor;
import org.junit.Test;

import java.io.IOException;

/**
 * Test ICTF feature extractor is implemented according to
 * the  Carmel, Yom-Tov synthesis series book
 */
public class ICTFFeatureExtractorTest extends BaseFeatureExtractorTest {

  private static FeatureExtractors EXTRACTOR = getChain(new AvgICTFFeatureExtractor());
  @Test
  public void testSingleQueryPhrase() throws IOException {
    float[] expected = {0};
    assertFeatureValues(expected, "document", "document", EXTRACTOR);
  }

  @Test
  public void testSingleQuery2() throws IOException {
    float[] expected = {1.38629f};
    assertFeatureValues(expected, "document", "document multiple tokens more", EXTRACTOR);
  }

  @Test
  public void testSingleQuery3() throws IOException {
    float[] expected = {0.693147f};
    assertFeatureValues(expected, "document", "document document test more tokens document", EXTRACTOR);
  }

  @Test
  public void testMultiQuery() throws IOException {
    float[] expected = {0.20273f};

    assertFeatureValues(expected, "document test", "document document missing", EXTRACTOR);
  }

  @Test
  public void testMultiQuery2() throws IOException {
    // log(8/3)*0.5 + log(8/2) * 0.5
    float[] expected = {1.18356f};
    assertFeatureValues(expected, "document test", "document document test test more tokens document tokens", EXTRACTOR);
  }
}
