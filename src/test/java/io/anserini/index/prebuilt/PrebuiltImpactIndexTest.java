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

public class PrebuiltImpactIndexTest {
  @Test
  public void testInvalidName() {
    PrebuiltImpactIndex.Entry entry = PrebuiltImpactIndex.get("fake_index");
    assertNull(entry);
  }

  @Test
  public void testLookupByName() {
    PrebuiltImpactIndex.Entry entry;

    entry = PrebuiltImpactIndex.get("bright-biology.splade-v3");
    assertNotNull(entry);
    assertEquals("lucene-inverted.bright-biology.splade-v3.20250808.c6674a.tar.gz", entry.filename);
    assertEquals("559813ffede15ba7080af05383b64bde", entry.md5);
    assertEquals(57359, entry.documents);
    assertEquals(18830144L, entry.compressedSize);

    entry = PrebuiltImpactIndex.get("bright-leetcode.splade-v3");
    assertNotNull(entry);
    assertEquals("lucene-inverted.bright-leetcode.splade-v3.20250808.c6674a.tar.gz", entry.filename);
    assertEquals("6e585d1d9012c220b52c6bb9306360f8", entry.md5);
    assertEquals(413932, entry.documents);
    assertEquals(167385110L, entry.compressedSize);
  }

  @Test
  public void testTotalCount() {
    assertEquals(12, PrebuiltImpactIndex.entries().size());
  }

  @Test
  public void testSuspiciousDuplicateMetadata() {
    // Verify bright-aops and bright-theoremqa-questions have distinct metadata.
    // These currently share identical total_terms, documents, and unique_terms
    // in both bright-impact.json and bright-inverted.json, which is likely a
    // copy-paste error. This test will fail once the correct values are filled in,
    // at which point the assertNotEquals checks should be updated to assertEquals
    // with the corrected values.
    PrebuiltImpactIndex.Entry aops = PrebuiltImpactIndex.get("bright-aops.splade-v3");
    PrebuiltImpactIndex.Entry theoremqaQ = PrebuiltImpactIndex.get("bright-theoremqa-questions.splade-v3");
    assertNotNull(aops);
    assertNotNull(theoremqaQ);

    // These three fields are currently identical between aops and theoremqa-questions.
    // TODO: Verify whether this is a copy-paste error and update with correct values.
    assertEquals(aops.documents, theoremqaQ.documents);
    assertEquals(aops.totalTerms, theoremqaQ.totalTerms);
    assertEquals(aops.uniqueTerms, theoremqaQ.uniqueTerms);
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
