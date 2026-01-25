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

public class InvertedIndexTest {

  @Test
  public void testLookupByName() {
    InvertedIndex index = InvertedIndex.load();
    assertEquals(2, index.entries().size());

    InvertedIndex.Entry passage = index.get("msmarco-v1-passage");
    assertNotNull(passage);
    assertEquals("lucene-inverted.msmarco-v1-passage.20221004.252b5e.tar.gz", passage.filename);
    assertEquals("678876e8c99a89933d553609a0fd8793", passage.md5);
    assertEquals(8841823, passage.documents);
    assertEquals(2170758745L, passage.sizeCompressedBytes);

    InvertedIndex.Entry doc = index.get("msmarco-v1-doc");
    assertNotNull(doc);
    assertEquals("lucene-inverted.msmarco-v1-doc.20221004.252b5e.tar.gz", doc.filename);
    assertEquals("f66020a923df6430007bd5718e53de86", doc.md5);
    assertEquals(3213835, doc.documents);
    assertEquals(13736982339L, doc.sizeCompressedBytes);
  }
}
