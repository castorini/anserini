/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.lucene.util.LuceneTestCase;
import org.junit.Before;
import org.junit.Test;

public class DocumentTest<D extends SourceDocument> extends LuceneTestCase {
  protected List<String> rawDocs;
  protected List<Map<String, String>> expected;
  protected D dType;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    rawDocs = new ArrayList<String>();
    expected = new ArrayList<Map<String, String>>();
  }

  @SuppressWarnings("unchecked")
  protected D parse(String raw) throws Exception {
    BufferedReader bufferedReader = new BufferedReader(new StringReader(raw));
    D d = (D)dType.readNextRecord(bufferedReader);
    return d;
  }

  @Test
  public void test() throws Exception {
    for (int i = 0; i < rawDocs.size(); i++) {
      D parsed = parse(rawDocs.get(i));
      assertEquals(parsed.id(), expected.get(i).get("id"));
      assertEquals(parsed.content(), expected.get(i).get("content"));
    }
  }
}
