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

import org.apache.lucene.util.LuceneTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class DocumentCollectionTest<T extends SourceDocument> extends LuceneTestCase {
  Path collectionPath;
  DocumentCollection<T> collection;

  Set<Path> segmentPaths; // Set of segment paths; set because there's no guarantee on iteration order.
  Map<Path, Integer> segmentDocCounts; // Map holding the number of expected documents in each segment.

  int totalSegments; // Total number of expected segments.
  int totalDocs; // Total number of expected documents.

  // Holds the ground truth. Outer key is the docid, map is custom data based on subclass.
  Map<String, Map<String, String>> expected;

  @Before
  public void setUp() throws Exception {
    super.setUp();

    segmentPaths = new HashSet<>();
    segmentDocCounts = new HashMap<>();
    expected = new HashMap<>();
  }

  @Test
  // Iterate through collection using built-in iterators.
  public void testIterateCollection() {
    if (collection == null)
      return;

    Iterator<FileSegment<T>> segmentIter = collection.iterator();
    assertNotNull(segmentIter);
    for (int i=0; i<segmentPaths.size(); i++) {
      assertTrue(segmentIter.hasNext());
      FileSegment<T> segment = segmentIter.next();

      assertTrue(segmentPaths.contains(segment.getSegmentPath()));

      int docCount = 0;
      for (T doc : segment) {
        // This is a special case for Warc collections, where the id can be null.
        if (doc.id() == null) {
          checkDocument(doc, expected.get("null"));
        } else {
          assertTrue(expected.containsKey(doc.id()));
          checkDocument(doc, expected.get(doc.id()));
        }
        docCount++;
      }

      assertEquals((int) segmentDocCounts.get(segment.getSegmentPath()), docCount);
      segment.close();
    }
    assertFalse(segmentIter.hasNext());
  }

  @Test
  // Iterate through collection, but we're going to explicitly create segments.
  public void testManualSegmentInitialization() throws IOException {
    if (collection == null)
      return;

    assertEquals(segmentPaths.size(), collection.getSegmentPaths().size());
    for (Path path : segmentPaths) {
      FileSegment<T> segment = collection.createFileSegment(path);
      assertTrue(segmentPaths.contains(segment.getSegmentPath()));

      int docCount = 0;
      for (T doc : segment) {
        // This is a special case for Warc collections, where the id can be null.
        if (doc.id() == null) {
          checkDocument(doc, expected.get("null"));
        } else {
          assertTrue(expected.containsKey(doc.id()));
          checkDocument(doc, expected.get(doc.id()));
        }
        docCount++;
      }

      assertEquals((int) segmentDocCounts.get(segment.getSegmentPath()), docCount);
      segment.close();
    }
  }

  @Test
  // Iterate through the entire collection using Java's stream processing capabilities.
  public void testStreamIteration() {
    if (collection == null)
      return;

    AtomicInteger segmentCnt = new AtomicInteger();
    AtomicInteger docCnt = new AtomicInteger();

    collection.iterator().forEachRemaining(d -> {
      d.iterator().forEachRemaining(doc -> {

        // This is a special case for Warc collections, where the id can be null.
        if (doc.id() == null) {
          checkDocument(doc, expected.get("null"));
        } else {
          assertTrue(expected.containsKey(doc.id()));
          checkDocument(doc, expected.get(doc.id()));
        }
        docCnt.incrementAndGet();
      });
      segmentCnt.incrementAndGet();
    });

    assertEquals(totalSegments, segmentCnt.get());
    assertEquals(totalDocs, docCnt.get());
  }

  abstract void checkDocument(SourceDocument doc, Map<String, String> expected);

  @After
  public void tearDown() throws Exception {
    super.tearDown();
  }
}
