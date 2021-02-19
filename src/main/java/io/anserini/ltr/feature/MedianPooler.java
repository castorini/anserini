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

import java.util.Collections;
import java.util.List;

public class MedianPooler implements Pooler {
  public float pool(List<Float> array) {
    Collections.sort(array);
    int mid = array.size() / 2;
    if(array.size()==0) return Float.MAX_VALUE;
    if (array.size() % 2 == 0) {
      return (array.get(mid - 1) + array.get(mid)) / 2;
    } else {
      return array.get(mid) / 2;
    }
  }

  public Pooler clone() {
    return new MedianPooler();
  }

  public String getName() {
    return "median";
  }
}
