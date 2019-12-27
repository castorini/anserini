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

package io.anserini.util;

import org.apache.lucene.util.LuceneTestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

public class FeatureVectorTest extends LuceneTestCase {
  private final FeatureVector createAndAddFeatureWeights() {
    FeatureVector fv = new FeatureVector();
    fv.addFeatureWeight("a", 0.2f);
    fv.addFeatureWeight("a", 0.3f);
    fv.addFeatureWeight("b", 0.3f);
    fv.addFeatureWeight("b", 0.5f);
    fv.addFeatureWeight("c", 0.4f);
    fv.addFeatureWeight("d", 0.1f);
    return fv;
  }
  
  @Test
  public void pruneToSizeTest() {
    FeatureVector fv1 = createAndAddFeatureWeights();
    assertEquals(fv1.pruneToSize(2).getFeatures().size(), 2);
    FeatureVector fv2 = createAndAddFeatureWeights();
    assertEquals(fv2.pruneToSize(1).getFeatures().size(), 1);
    FeatureVector fv3 = createAndAddFeatureWeights();
    assertEquals(fv3.pruneToSize(2).getFeatures(), new HashSet<>(Arrays.asList(new String[]{"a", "b"})));
  }
}
