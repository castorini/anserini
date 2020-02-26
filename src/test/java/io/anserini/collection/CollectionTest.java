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

public abstract class CollectionTest<T extends SourceDocument> extends LuceneTestCase {
  protected Path collectionPath;
  protected Set<Path> segmentPaths;
  protected Map<Path, Integer> segmentDocCounts;
  protected DocumentCollection<T> collection;
  protected int totalSegments;
  protected int totalDocs;
  protected Map<String, Map<String, String>> expected;

  @Before
  public void setUp() throws Exception {
    super.setUp();

    segmentPaths = new HashSet<>();
    segmentDocCounts = new HashMap<>();
    expected = new HashMap<>();
  }

  @Test
  public void testIterateCollection() {
    if (collection == null)
      return;

    Iterator<FileSegment<T>> segmentIter = collection.iterator();
    for (int i=0; i<segmentPaths.size(); i++) {
      assertTrue(segmentIter.hasNext());
      FileSegment<T> segment = segmentIter.next();

      assertTrue(segmentPaths.contains(segment.getSegmentPath()));
      System.out.println(segment.getSegmentPath());

      int docCount = 0;
      for (T doc : segment) {
        checkDocument(doc, expected.get(doc.id()));
        docCount++;
      }

      System.out.println("Segment " + segment.getSegmentPath() + " has " + docCount + " docs");
      assertEquals((int) segmentDocCounts.get(segment.getSegmentPath()), docCount);

    }
    assertFalse(segmentIter.hasNext());
  }

  @Test
  public void testManualSegmentInitialization() throws IOException {
    if (collection == null)
      return;

    for (Path path : segmentPaths) {
      FileSegment<T> segment = collection.createFileSegment(path);

      assertTrue(segmentPaths.contains(segment.getSegmentPath()));
      System.out.println(segment.getSegmentPath());

      int docCount = 0;
      for (T doc : segment) {
        checkDocument(doc, expected.get(doc.id()));
        docCount++;
      }

      System.out.println("Segment " + segment.getSegmentPath() + " has " + docCount + " docs");
      assertEquals((int) segmentDocCounts.get(segment.getSegmentPath()), docCount);

    }
  }

  @Test
  public void testStreamIteration() {
    if (collection == null)
      return;

    AtomicInteger segmentCnt = new AtomicInteger();
    AtomicInteger docCnt = new AtomicInteger();

    collection.iterator().forEachRemaining(d -> {
      d.iterator().forEachRemaining(doc -> {
        checkDocument(doc, expected.get(doc.id()));
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
