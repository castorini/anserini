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

package io.anserini.ltr.feature;

import java.util.List;

public class MaxMinRatioPooler implements Pooler {
  public float pool(List<Float> array) {
    float max = 1;
    float min = 1;
    for (float v : array) {
      if (v > max)
        max = v;
      if (v < max)
        min = v;
    }
    //gamma2  return max / min;
    return max/min;
  }

  public Pooler clone() {
    return new MaxMinRatioPooler();
  }

  public String getName() {
    return "maxminratio";
  }
}
