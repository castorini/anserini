/*
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

package io.anserini.collection;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jbibtex.Key;
import org.jbibtex.Value;
import org.junit.Before;
import org.junit.Test;

public class BibtexDocumentTest extends DocumentTest {
  Path bibtexFilePath;

  @Before
  public void setUp() throws Exception {
    super.setUp();

    String root = System.getProperty("user.dir");
    bibtexFilePath = Paths.get(root + "/src/test/resources/sample-acl.bib");
    
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
    expected.add(doc1);

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
    expected.add(doc2);

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
    expected.add(doc3);

  }

  @Test
  public void test() throws IOException {
    BibtexCollection collection = new BibtexCollection(bibtexFilePath);

    Iterator<BibtexCollection.Document> iter = collection.createFileSegment(bibtexFilePath).iterator();
    int j = 0;
    while (iter.hasNext()) {
        BibtexCollection.Document parsed = iter.next();
        Map<Key, Value> parsedFields = parsed.bibtexEntry().getFields(); 
        for (Map.Entry<String, String> entry: expected.get(j).entrySet()) {
            String expected_key = entry.getKey();
            String expected_value = entry.getValue();
            if (expected_key.equals("id")) {
                assertEquals(expected_value, parsed.id());
            } else if (expected_key.equals("type")) {
                assertEquals(expected_value, parsed.type());
            } else if (expected_key.equals("contents")) {
                assertEquals(expected_value, parsed.content());
            } else {
                Value parsedValue = parsedFields.get(new Key(expected_key));
                assertNotNull(parsedValue);
                assertEquals(expected_value, parsedValue.toUserString());
            }
        }
        j++;
    }
  }

  // Tests if the iterator is behaving properly. If it is, we shouldn't have any issues running into
  // NoSuchElementExceptions.
  @Test
  public void testStreamIteration() {
    BibtexCollection collection = new BibtexCollection(bibtexFilePath);
    try {
      Iterator<BibtexCollection.Document> iter =
              collection.createFileSegment(bibtexFilePath).iterator();
      AtomicInteger cnt = new AtomicInteger();
      iter.forEachRemaining(d -> cnt.incrementAndGet());
      assertEquals(3, cnt.get());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}