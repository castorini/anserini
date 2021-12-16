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

package io.anserini.collection;

import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class TrecCollectionTest extends DocumentCollectionTest<TrecCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/trec/collection1");
    collection = new TrecCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/trec/collection1/segment1.txt");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 2);

    totalSegments = 1;
    totalDocs = 2;

    expected.put("AP-0001", Map.of("id", "AP-0001","raw",
        // ONLY "<TEXT>", "<HEADLINE>", "<TITLE>", "<HL>", "<HEAD>",
        // "<TTL>", "<DD>", "<DATE>", "<LP>", "<LEADPARA>" will be included
        "<HEAD>This is head and should be included</HEAD>\n" +
            "<HEADLINE>This is headline and should be included</HEADLINE>\n" +
            "<TEXT>\n" +
            "Hopefully we\n" +
            "get this\n" +
            "right\n" +
            "</TEXT>"));

    expected.put("doc2",
        Map.of("id", "doc2","raw", "<TEXT>\nhere is some text.\n</TEXT>"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(JsoupStringTransform.SINGLETON.apply(expected.get("raw")), doc.contents());
    assertEquals(expected.get("raw"), doc.raw());
  }
}
