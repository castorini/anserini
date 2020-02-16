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
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TrecDocumentTest {
  private List<Map<String, String>> expected = new ArrayList<>();

  @Before
  public void setUp() throws Exception {
    expected.add(Map.of("id", "AP-0001",
        // ONLY "<TEXT>", "<HEADLINE>", "<TITLE>", "<HL>", "<HEAD>",
        // "<TTL>", "<DD>", "<DATE>", "<LP>", "<LEADPARA>" will be included
        "content", "<HEAD>This is head and should be included</HEAD>\n" +
            "<HEADLINE>This is headline and should be included</HEADLINE>\n" +
            "<TEXT>\n" +
            "Hopefully we\n" +
            "get this\n" +
            "right\n" +
            "</TEXT>"));

    expected.add(Map.of("id", "doc2",
        "content", "<TEXT>\nhere is some text.\n</TEXT>"));
  }

  @Test
  public void testEntireCollection() {
    TrecCollection collection = new TrecCollection(Paths.get("src/test/resources/sample_docs/trec/collection1"));

    // Iterator over FileSegments:
    Iterator<FileSegment<TrecCollection.Document>> segmentIter = collection.iterator();
    // Iterator over documents in the first FileSegment:
    Iterator<TrecCollection.Document> docIter = segmentIter.next().iterator();

    TrecCollection.Document parsed = docIter.next();
    assertEquals(expected.get(0).get("id"), parsed.id());
    assertEquals(expected.get(0).get("content"), parsed.content());

    parsed = docIter.next();
    assertEquals(expected.get(1).get("id"), parsed.id());
    assertEquals(expected.get(1).get("content"), parsed.content());

    // No more documents in this segment.
    assertFalse(docIter.hasNext());
    // No more FileSegments in this collection.
    assertFalse(segmentIter.hasNext());
  }

  // Test iteration over a single segment.
  @Test @SuppressWarnings("unchecked")
  public void testSingleSegment() throws Exception {
    TrecCollection.Segment segment =
        new TrecCollection.Segment<>(Paths.get("src/test/resources/sample_docs/trec/collection1/segment1.txt"));
    Iterator<TrecCollection.Document> iter = segment.iterator();

    TrecCollection.Document parsed = iter.next();
    assertEquals(expected.get(0).get("id"), parsed.id());
    assertEquals(expected.get(0).get("content"), parsed.content());

    parsed = iter.next();
    assertEquals(expected.get(1).get("id"), parsed.id());
    assertEquals(expected.get(1).get("content"), parsed.content());

    assertEquals(false, iter.hasNext());
  }

  @Test
  public void testFileSegmentStreamIteration() throws Exception {
    TrecCollection collection = new TrecCollection(Paths.get("src/test/resources/sample_docs/trec/collection1"));

    Iterator<FileSegment<TrecCollection.Document>> iter = collection.iterator();
    AtomicInteger cnt = new AtomicInteger();
    iter.forEachRemaining(d -> cnt.incrementAndGet());
    assertEquals(1, cnt.get());
  }

  @Test @SuppressWarnings("unchecked")
  public void testSingleSegmentStreamIteration() throws Exception {
    TrecCollection.Segment segment =
        new TrecCollection.Segment<>(Paths.get("src/test/resources/sample_docs/trec/collection1/segment1.txt"));

    Iterator<TrecCollection.Document> iter = segment.iterator();
    AtomicInteger cnt = new AtomicInteger();
    iter.forEachRemaining(d -> cnt.incrementAndGet());
    assertEquals(2, cnt.get());
  }
}
