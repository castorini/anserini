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

public class MrTyDiCollectionTest extends DocumentCollectionTest<MrTyDiCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    // arabic
    collectionPath = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/ar");
    collection = new MrTyDiCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/ar/corpus.jsonl.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);

    totalSegments = 1;
    totalDocs = 4;

    expected.put("7#0", Map.of("id", "7#0"));
    expected.put("7#1", Map.of("id", "7#1"));
    expected.put("7#2", Map.of("id", "7#2"));
    expected.put("7#3", Map.of("id", "7#3"));

    //  bengali
    collectionPath = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/bn");
    collection = new MrTyDiCollection(collectionPath);

    segment1 = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/bn/corpus.jsonl.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);

    totalSegments = 1;
    totalDocs = 4;

    expected.put("608#0", Map.of("id", "608#0"));
    expected.put("608#1", Map.of("id", "608#1"));
    expected.put("608#2", Map.of("id", "608#2"));
    expected.put("608#3", Map.of("id", "608#3"));

    // english
    collectionPath = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/en");
    collection = new MrTyDiCollection(collectionPath);

    segment1 = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/en/corpus.jsonl.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);

    totalSegments = 1;
    totalDocs = 4;

    expected.put("12#0", Map.of("id", "12#0"));
    expected.put("12#1", Map.of("id", "12#1"));
    expected.put("12#2", Map.of("id", "12#2"));
    expected.put("12#3", Map.of("id", "12#3"));

    // finnish
    collectionPath = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/fi");
    collection = new MrTyDiCollection(collectionPath);

    segment1 = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/fi/corpus.jsonl.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);

    totalSegments = 1;
    totalDocs = 4;

    expected.put("1#0", Map.of("id", "1#0"));
    expected.put("1#1", Map.of("id", "1#1"));
    expected.put("1#2", Map.of("id", "1#2"));
    expected.put("1#3", Map.of("id", "1#3"));

    //  indonesian
    collectionPath = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/id");
    collection = new MrTyDiCollection(collectionPath);

    segment1 = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/id/corpus.jsonl.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);

    totalSegments = 1;
    totalDocs = 4;

    expected.put("1#0", Map.of("id", "1#0"));
    expected.put("1#1", Map.of("id", "1#1"));
    expected.put("1#2", Map.of("id", "1#2"));
    expected.put("1#3", Map.of("id", "1#3"));

    // japanese
    collectionPath = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/ja");
    collection = new MrTyDiCollection(collectionPath);

    segment1 = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/ja/corpus.jsonl.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);

    totalSegments = 1;
    totalDocs = 4;

    expected.put("5#0", Map.of("id", "5#0"));
    expected.put("5#1", Map.of("id", "5#1"));
    expected.put("5#2", Map.of("id", "5#2"));
    expected.put("5#3", Map.of("id", "5#3"));

    // korean
    collectionPath = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/ko");
    collection = new MrTyDiCollection(collectionPath);

    segment1 = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/ko/corpus.jsonl.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);

    totalSegments = 1;
    totalDocs = 4;

    expected.put("5#0", Map.of("id", "5#0"));
    expected.put("5#1", Map.of("id", "5#1"));
    expected.put("5#2", Map.of("id", "5#2"));
    expected.put("5#3", Map.of("id", "5#3"));

    // russian
    collectionPath = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/ru");
    collection = new MrTyDiCollection(collectionPath);

    segment1 = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/ru/corpus.jsonl.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);

    totalSegments = 1;
    totalDocs = 4;

    expected.put("7#0", Map.of("id", "7#0"));
    expected.put("7#1", Map.of("id", "7#1"));
    expected.put("7#2", Map.of("id", "7#2"));
    expected.put("7#3", Map.of("id", "7#3"));

    // swahili
    collectionPath = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/sw");
    collection = new MrTyDiCollection(collectionPath);

    segment1 = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/sw/corpus.jsonl.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);

    totalSegments = 1;
    totalDocs = 4;

    expected.put("2#0", Map.of("id", "2#0"));
    expected.put("2#1", Map.of("id", "2#1"));
    expected.put("2#2", Map.of("id", "2#2"));
    expected.put("2#3", Map.of("id", "2#3"));

    //  telugu
    collectionPath = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/te");
    collection = new MrTyDiCollection(collectionPath);

    segment1 = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/te/corpus.jsonl.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);

    totalSegments = 1;
    totalDocs = 4;

    expected.put("786#0", Map.of("id", "786#0"));
    expected.put("786#1", Map.of("id", "786#1"));
    expected.put("786#2", Map.of("id", "786#2"));
    expected.put("786#3", Map.of("id", "786#3"));

    //  thai
    collectionPath = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/th");
    collection = new MrTyDiCollection(collectionPath);

    segment1 = Paths.get("src/test/resources/sample_docs/mrtydi-1.1/th/corpus.jsonl.gz");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 4);

    totalSegments = 1;
    totalDocs = 4;

    expected.put("1#0", Map.of("id", "1#0"));
    expected.put("545#0", Map.of("id", "545#0"));
    expected.put("545#1", Map.of("id", "545#1"));
    expected.put("545#2", Map.of("id", "545#2"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
  }
}
