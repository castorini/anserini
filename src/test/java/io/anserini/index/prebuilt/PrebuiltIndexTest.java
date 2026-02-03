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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PrebuiltIndexTest {
  @Test
  public void testInvalidName() {
    assertNull(PrebuiltIndex.get("fake_index"));
  }

  @Test
  public void testLookupByType() {
    PrebuiltIndex.Entry entry;

    entry = PrebuiltIndex.get("bright-biology");
    assertNotNull(entry);
    assertEquals("bright-biology", entry.name);
    assertEquals("inverted", entry.type);

    entry = PrebuiltIndex.get("bright-biology.splade-v3");
    assertNotNull(entry);
    assertEquals("bright-biology.splade-v3", entry.name);
    assertEquals("impact", entry.type);

    entry = PrebuiltIndex.get("bright-biology.bge-large-en-v1.5.flat");
    assertNotNull(entry);
    assertEquals("bright-biology.bge-large-en-v1.5.flat", entry.name);
    assertEquals("flat", entry.type);

    entry = PrebuiltIndex.get("msmarco-v1-passage.cosdpr-distil.hnsw");
    assertNotNull(entry);
    assertEquals("msmarco-v1-passage.cosdpr-distil.hnsw", entry.name);
    assertEquals("hnsw", entry.type);
  }
}
