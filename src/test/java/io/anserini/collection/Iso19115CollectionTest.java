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

import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Iso19115CollectionTest extends DocumentCollectionTest<Iso19115Collection.Document> {
  @Before
  public void setUp() throws Exception {
    super.setUp();
    collectionPath = Paths.get("src/test/resources/sample_docs/iso19115");
    collection = new Iso19115Collection(collectionPath);
    Path segment = Paths.get("src/test/resources/sample_docs/iso19115/output.json");

    segmentPaths.add(segment);
    segmentDocCounts.put(segment, 2);

    totalSegments = 1;
    totalDocs = 2;
    expected.put("12957", Map.of("id", "12957", "title", "Test title", "abstract", "Test abstract", "coordinates", "[[43.862008,-80.7178777],[43.862008,-80.272744],[43.6764444,-80.272744],[43.6764444,-80.7178777]]",
                                    "thesaurusName", "Polar Data Catalogue Thesaurus (Canada) : https://www.polardata.ca/pdcinput/public/keywordlibrary"));
    expected.put("13007", Map.of("id", "13007", "title","Test title 2", "abstract", "Test abstract 2", "coordinates", "[[43.452,-80.634],[43.452,-80.49],[43.313,-80.49],[43.313,-80.634]]",
                                "thesaurusName", "Polar Data Catalogue Thesaurus (Canada) : https://www.polardata.ca/pdcinput/public/keywordlibrary"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    // System.out.println(((Iso19115Collection.Document) doc).getThesaurusName());
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("title"), ((Iso19115Collection.Document) doc).getTitle());
    assertEquals(expected.get("abstract"), ((Iso19115Collection.Document) doc).getAbstract());
    assertEquals(expected.get("coordinates"), ((Iso19115Collection.Document) doc).getCoordinates());
    assertEquals(expected.get("thesaurusName"), ((Iso19115Collection.Document) doc).getThesaurusName());
  }
}
