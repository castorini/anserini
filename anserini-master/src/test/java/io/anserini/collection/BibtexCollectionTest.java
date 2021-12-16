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

import org.jbibtex.Key;
import org.jbibtex.Value;
import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BibtexCollectionTest extends DocumentCollectionTest<BibtexCollection.Document> {
  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/bib/acl");
    collection = new BibtexCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/bib/acl/segment1.bib");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 3);

    totalSegments = 1;
    totalDocs = 3;

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "article-id");
    doc1.put("type", "article");
    doc1.put("title", "this is the title");
    doc1.put("author", "author_name1  and\nauthor_name2");
    doc1.put("journal", "this is the journal");
    doc1.put("volume", "11");
    doc1.put("number", "2-3");
    doc1.put("year", "1985");
    doc1.put("url", "https://www.aclweb.org/anthology/J85-2005");
    doc1.put("pages", "155--169");
    doc1.put("contents", "this is the title. ");
    expected.put("article-id", doc1);

    HashMap<String, String> doc2 = new HashMap<>();
    doc2.put("id", "inproceedings-id");
    doc2.put("type", "inproceedings");
    doc2.put("title", "this is the title");
    doc2.put("author", "author_name");
    doc2.put("booktitle", "this is the booktitle");
    doc2.put("month", "May");
    doc2.put("year", "2019");
    doc2.put("address", "this is the address");
    doc2.put("publisher", "this is the publisher");
    doc2.put("url", "https://www.aclweb.org/anthology/W19-0506");
    doc2.put("doi", "10.18653/v1/W19-0506");
    doc2.put("pages", "38--43");
    doc2.put("abstract", "this is the abstract");
    doc2.put("contents", "this is the title. this is the abstract");
    expected.put("inproceedings-id", doc2);

    HashMap<String, String> doc3 = new HashMap<>();
    doc3.put("id", "proceedings-id");
    doc3.put("type", "proceedings");
    doc3.put("title", "this is the title");
    doc3.put("editor", "editor_name");
    doc3.put("month", "March");
    doc3.put("year", "1985");
    doc3.put("address", "this is the address");
    doc3.put("publisher", "this is the publisher");
    doc3.put("url", "https://www.aclweb.org/anthology/E85-1000");
    doc3.put("contents", "this is the title. ");
    expected.put("proceedings-id", doc3);
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());

    Map<Key, Value> parsedFields = ((BibtexCollection.Document) doc).bibtexEntry().getFields();
    for (Map.Entry<String, String> entry : expected.entrySet()) {
      String expectedKey = entry.getKey();
      String expectedValue = entry.getValue();
      if (expectedKey.equals("id")) {
        assertEquals(expectedValue, doc.id());
      } else if (expectedKey.equals("type")) {
        assertEquals(expectedValue, ((BibtexCollection.Document) doc).type());
      } else if (expectedKey.equals("contents")) {
        assertEquals(expectedValue, doc.contents());
        assertEquals(expectedValue, doc.raw());
      } else {
        Value parsedValue = parsedFields.get(new Key(expectedKey));
        assertNotNull(parsedValue);
        assertEquals(expectedValue, parsedValue.toUserString());
      }
    }
  }
}
