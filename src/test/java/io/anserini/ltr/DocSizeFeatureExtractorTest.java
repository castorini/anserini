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

import io.anserini.ltr.feature.base.DocSizeFeatureExtractor;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * Test we get the doc size correctly
 */
public class DocSizeFeatureExtractorTest extends BaseFeatureExtractorTest<Integer> {

  @Test
  public void testSingleDoc() throws IOException {
    float[] expected = {5};
    assertFeatureValues(expected, "query text can't be empty", "document size independent of query document",
            new DocSizeFeatureExtractor());
  }

  @Test
  public void testMultipleDocs() throws IOException {
    float[] expected = {5};
    assertFeatureValues(expected, "query text", Arrays.asList("first document",
                                      "second document", "test document document document test"),
            getChain(new DocSizeFeatureExtractor()), 2);
  }

}
