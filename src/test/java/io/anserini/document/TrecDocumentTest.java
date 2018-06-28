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
import java.util.HashMap;
import org.junit.Before;


public class TrecDocumentTest extends DocumentTest<TrecDocument> {

  @Before
  public void setUP() throws Exception {
    super.setUp();
    dType = new TrecDocument();

    rawDocs.add("<DOC>\n" +
        "<DOCNO> AP-0001 </DOCNO>\n" +
        "<FILEID>field id test and should NOT be included</FILEID>\n" +
        "<FIRST>first test and should NOT be included</FIRST>\n" +
        "<SECOND>second test and should NOT be included</SECOND>\n" +
        "<HEAD>This is head and should NOT be included</HEAD>\n" +
        "<HEADLINE>This is headline and should be included</HEADLINE>\n" +
        "<DATELINE>AP</DATELINE>\n" +
        "<TEXT>\n" +
        "   Hopefully we \n" +
        "get this\n" +
        " right\n" +
        "</TEXT>\n" +
        "</DOC>\n");

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "AP-0001");
    // ONLY "<TEXT>", "<HEADLINE>", "<TITLE>", "<HL>", "<HEAD>",
    // "<TTL>", "<DD>", "<DATE>", "<LP>", "<LEADPARA>" will be included
    doc1.put("content", "<HEAD>This is head and should NOT be included</HEAD>\n" +
        "<HEADLINE>This is headline and should be included</HEADLINE>\n" +
        "<TEXT>\n" +
        "Hopefully we\n" +
        "get this\n" +
        "right\n" +
        "</TEXT>");
    expected.add(doc1);
  }
}
