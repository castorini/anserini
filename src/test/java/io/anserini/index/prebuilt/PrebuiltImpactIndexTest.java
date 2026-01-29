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

package io.anserini.index.prebuilt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PrebuiltImpactIndexTest {
  @Test
  public void testInvalidName() {
    PrebuiltImpactIndex.Entry entry = PrebuiltImpactIndex.get("fake_index");
    assertNull(entry);
  }

  @Test
  public void testTotalCount() {
    assertEquals(12, PrebuiltImpactIndex.entries().size());
  }

  @Test
  public void testTotalCountForBright() {
    int brightCount = 0;
    for (PrebuiltImpactIndex.Entry entry : PrebuiltImpactIndex.entries()) {
      if (entry != null && entry.name != null && entry.name.toUpperCase().startsWith("BRIGHT")) {
        brightCount++;
      }
    }
    assertEquals(12, brightCount);
  }
}
