/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

import org.apache.lucene.tests.util.LuceneTestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

public class FeatureVectorTest extends LuceneTestCase {
  private final FeatureVector createAndAddFeatureWeights1() {
    FeatureVector fv = new FeatureVector();
    fv.addFeatureValue("a", 0.2f);
    fv.addFeatureValue("a", 0.3f);
    fv.addFeatureValue("b", 0.3f);
    fv.addFeatureValue("b", 0.5f);
    fv.addFeatureValue("c", 0.4f);
    fv.addFeatureValue("d", 0.1f);
    return fv;
  }

  // To test tie-breaking
  private final FeatureVector createAndAddFeatureWeights2() {
    FeatureVector fv = new FeatureVector();
    fv.addFeatureValue("ds", 0.2f);
    fv.addFeatureValue("z", 0.5f);
    fv.addFeatureValue("zz", 0.01f);
    fv.addFeatureValue("c", 0.4f);
    fv.addFeatureValue("a", 0.2f);
    fv.addFeatureValue("x", 0.2f);
    fv.addFeatureValue("1a", 0.2f);
    fv.addFeatureValue("d", 0.6f);
    return fv;
  }

  @Test
  public void pruneToSizeTest() {
    FeatureVector fv1 = createAndAddFeatureWeights1();
    assertEquals(fv1.pruneToSize(2).getFeatures().size(), 2);
    FeatureVector fv2 = createAndAddFeatureWeights1();
    assertEquals(fv2.pruneToSize(1).getFeatures().size(), 1);
    FeatureVector fv3 = createAndAddFeatureWeights1();
    assertEquals(fv3.pruneToSize(2).getFeatures(), new HashSet<>(Arrays.asList(new String[]{"a", "b"})));
  }

  @Test
  public void toStringTest1() {
    FeatureVector fv = createAndAddFeatureWeights2();
    assertEquals("[d=0.6, z=0.5, c=0.4, 1a=0.2, a=0.2, ds=0.2, x=0.2, zz=0.01]", fv.toString());
    // Make sure that feature value ties are broken lexicographically

    assertEquals("[d=0.6, z=0.5, c=0.4]", fv.toString(3));
    assertEquals("[d=0.6, z=0.5, c=0.4, 1a=0.2, a=0.2]", fv.toString(5));
  }

  @Test
  public void toStringTest2() {
    FeatureVector fv = createAndAddFeatureWeights2();
    assertEquals("[d=0.6, z=0.5, c=0.4, 1a=0.2, a=0.2, ds=0.2, x=0.2, zz=0.01]",
        fv.toString(FeatureVector.Order.VALUE_DESCENDING));
    assertEquals("[d=0.6, z=0.5, c=0.4]", fv.toString(FeatureVector.Order.VALUE_DESCENDING, 3));

    assertEquals("[zz=0.01, 1a=0.2, a=0.2, ds=0.2, x=0.2, c=0.4, z=0.5, d=0.6]",
        fv.toString(FeatureVector.Order.VALUE_ASCENDING));
    assertEquals("[zz=0.01, 1a=0.2, a=0.2]", fv.toString(FeatureVector.Order.VALUE_ASCENDING, 3));

    assertEquals("[1a=0.2, a=0.2, c=0.4, d=0.6, ds=0.2, x=0.2, z=0.5, zz=0.01]",
        fv.toString(FeatureVector.Order.FEATURE_ASCENDING));
    assertEquals("[1a=0.2, a=0.2, c=0.4]", fv.toString(FeatureVector.Order.FEATURE_ASCENDING, 3));

    assertEquals("[zz=0.01, z=0.5, x=0.2, ds=0.2, d=0.6, c=0.4, a=0.2, 1a=0.2]",
        fv.toString(FeatureVector.Order.FEATURE_DESCENDING));
    assertEquals("[zz=0.01, z=0.5, x=0.2]", fv.toString(FeatureVector.Order.FEATURE_DESCENDING, 3));
  }
}
