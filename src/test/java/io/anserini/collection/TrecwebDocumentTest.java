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

package io.anserini.collection;

import io.anserini.collection.DocumentTest;
import io.anserini.collection.TrecwebCollection;
import org.junit.Before;

import java.util.HashMap;

public class TrecwebDocumentTest extends DocumentTest<TrecwebCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();
    dType = new TrecwebCollection.Document();

    rawDocs.add("<DOC>\n" +
        "<DOCNO> WEB-0001 </DOCNO>\n" +
        "<DOCHDR>DOCHDR will NOT be \n" +
        " included</DOCHDR>\n" +
        "<html>Wh at ever here will be parsed \n" +
        " <br> asdf <div>\n" +
        "</html>\n" +
        "</DOC>\n");

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "WEB-0001");
    // <DOCHDR> Will NOT be included
    doc1.put("content", "<html>Wh at ever here will be parsed\n" +
        "<br> asdf <div>\n" +
        "</html>");
    expected.add(doc1);
  }
}
