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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PrebuiltInvertedIndexTest {
  @Test
  public void testInvalidName() {
    PrebuiltInvertedIndex.Entry entry = PrebuiltInvertedIndex.get("fake_index");
    assertNull(entry);
  }

  @Test
  public void testLookupByName() {
    assertEquals(2, PrebuiltInvertedIndex.entries().size());
    PrebuiltInvertedIndex.Entry entry;

    entry = PrebuiltInvertedIndex.get("msmarco-v1-passage");
    assertNotNull(entry);
    assertEquals("lucene-inverted.msmarco-v1-passage.20221004.252b5e.tar.gz", entry.filename);
    assertEquals("678876e8c99a89933d553609a0fd8793", entry.md5);
    assertEquals(8841823, entry.documents);
    assertEquals(2170758745L, entry.compressedSize);

    entry = PrebuiltInvertedIndex.get("msmarco-v1-doc");
    assertNotNull(entry);
    assertEquals("lucene-inverted.msmarco-v1-doc.20221004.252b5e.tar.gz", entry.filename);
    assertEquals("f66020a923df6430007bd5718e53de86", entry.md5);
    assertEquals(3213835, entry.documents);
    assertEquals(13736982339L, entry.compressedSize);
  }
}
