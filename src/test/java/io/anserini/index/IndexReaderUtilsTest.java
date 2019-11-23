/**
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

package io.anserini.index;

import io.anserini.IndexerTestBase;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

public class IndexReaderUtilsTest extends IndexerTestBase {

  @Test
  public void testDocidConversion() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    assertEquals("doc1", IndexReaderUtils.convertLuceneDocidToDocid(reader, 0));
    assertEquals("doc2", IndexReaderUtils.convertLuceneDocidToDocid(reader, 1));
    assertEquals("doc3", IndexReaderUtils.convertLuceneDocidToDocid(reader, 2));

    assertEquals(0, IndexReaderUtils.convertDocidToLuceneDocid(reader, "doc1"));
    assertEquals(1, IndexReaderUtils.convertDocidToLuceneDocid(reader, "doc2"));
    assertEquals(2, IndexReaderUtils.convertDocidToLuceneDocid(reader, "doc3"));
  }
}
